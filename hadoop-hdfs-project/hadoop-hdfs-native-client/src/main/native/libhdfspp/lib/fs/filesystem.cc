/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "filesystem.h"
#include "common/continuation/asio.h"
#include "common/util.h"
#include "common/logging.h"

#include <asio/ip/tcp.hpp>

#include <functional>
#include <limits>
#include <future>
#include <tuple>
#include <iostream>
#include <pwd.h>

#define FMT_THIS_ADDR "this=" << (void*)this

namespace hdfs {

static const char kNamenodeProtocol[] =
    "org.apache.hadoop.hdfs.protocol.ClientProtocol";
static const int kNamenodeProtocolVersion = 1;

using ::asio::ip::tcp;

static constexpr uint16_t kDefaultPort = 8020;


/*****************************************************************************
 *                    NAMENODE OPERATIONS
 ****************************************************************************/

void NameNodeOperations::Connect(const std::string &cluster_name,
                                 const std::string &server,
                             const std::string &service,
                             std::function<void(const Status &)> &&handler) {
  using namespace asio_continuation;
  typedef std::vector<tcp::endpoint> State;
  auto m = Pipeline<State>::Create();
  m->Push(Resolve(io_service_, server, service,
                  std::back_inserter(m->state())))
      .Push(Bind([this, m, cluster_name](const Continuation::Next &next) {
        engine_.Connect(cluster_name, m->state(), next);
      }));
  m->Run([this, handler](const Status &status, const State &) {
    handler(status);
  });
}

void NameNodeOperations::GetBlockLocations(const std::string & path,
  std::function<void(const Status &, std::shared_ptr<const struct FileInfo>)> handler)
{
  using ::hadoop::hdfs::GetBlockLocationsRequestProto;
  using ::hadoop::hdfs::GetBlockLocationsResponseProto;

  LOG_TRACE(kFileSystem, << "NameNodeOperations::GetBlockLocations("
                           << FMT_THIS_ADDR << ", path=" << path << ", ...) called");

  GetBlockLocationsRequestProto req;
  req.set_src(path);
  req.set_offset(0);
  req.set_length(std::numeric_limits<long long>::max());

  auto resp = std::make_shared<GetBlockLocationsResponseProto>();

  namenode_.GetBlockLocations(&req, resp, [resp, handler](const Status &stat) {
    if (stat.ok()) {
      auto file_info = std::make_shared<struct FileInfo>();
      auto locations = resp->locations();

      file_info->file_length_ = locations.filelength();
      file_info->last_block_complete_ = locations.islastblockcomplete();
      file_info->under_construction_ = locations.underconstruction();

      for (const auto &block : locations.blocks()) {
        file_info->blocks_.push_back(block);
      }

      if (!locations.islastblockcomplete() &&
          locations.has_lastblock() && locations.lastblock().b().numbytes()) {
        file_info->blocks_.push_back(locations.lastblock());
        file_info->file_length_ += locations.lastblock().b().numbytes();
      }

      handler(stat, file_info);
    } else {
      handler(stat, nullptr);
    }
  });
}

void NameNodeOperations::GetFileInfo(const std::string & path,
  std::function<void(const Status &, const StatInfo &)> handler)
{
  using ::hadoop::hdfs::GetFileInfoRequestProto;
  using ::hadoop::hdfs::GetFileInfoResponseProto;

  LOG_TRACE(kFileSystem, << "NameNodeOperations::GetFileInfo("
                           << FMT_THIS_ADDR << ", path=" << path << ") called");

  GetFileInfoRequestProto req;
  req.set_src(path);

  auto resp = std::make_shared<GetFileInfoResponseProto>();

  namenode_.GetFileInfo(&req, resp, [resp, handler, path](const Status &stat) {
    if (stat.ok()) {
      // For non-existant files, the server will respond with an OK message but
      //   no fs in the protobuf.
      if(resp -> has_fs()){
          struct StatInfo stat_info;
          stat_info.path=path;
          HdfsFileStatusProtoToStatInfo(stat_info, resp->fs());
          handler(stat, stat_info);
        } else {
          std::string errormsg = "No such file or directory: " + path;
          Status statNew = Status::PathNotFound(errormsg.c_str());
          handler(statNew, StatInfo());
        }
    } else {
      handler(stat, StatInfo());
    }
  });
}

void NameNodeOperations::GetListing(
    const std::string & path,
    std::function<void(const Status &, std::shared_ptr<std::vector<StatInfo>> &, bool)> handler,
    const std::string & start_after) {
  using ::hadoop::hdfs::GetListingRequestProto;
  using ::hadoop::hdfs::GetListingResponseProto;

  LOG_TRACE(
      kFileSystem,
      << "NameNodeOperations::GetListing(" << FMT_THIS_ADDR << ", path=" << path << ") called");

  GetListingRequestProto req;
  req.set_src(path);
  req.set_startafter(start_after.c_str());
  req.set_needlocation(false);

  auto resp = std::make_shared<GetListingResponseProto>();

  namenode_.GetListing(
      &req,
      resp,
      [resp, handler, path](const Status &stat) {
        if (stat.ok()) {
          if(resp -> has_dirlist()){
            std::shared_ptr<std::vector<StatInfo>> stat_infos(new std::vector<StatInfo>);
            for (::hadoop::hdfs::HdfsFileStatusProto const& fs : resp->dirlist().partiallisting()) {
              StatInfo si;
              si.path=fs.path();
              HdfsFileStatusProtoToStatInfo(si, fs);
              stat_infos->push_back(si);
            }
            handler(stat, stat_infos, resp->dirlist().remainingentries() > 0);
          } else {
            std::string errormsg = "No such file or directory: " + path;
            Status statNew = Status::PathNotFound(errormsg.c_str());
            std::shared_ptr<std::vector<StatInfo>> stat_infos;
            handler(statNew, stat_infos, false);
          }
        } else {
          std::shared_ptr<std::vector<StatInfo>> stat_infos;
          handler(stat, stat_infos, false);
        }
      });
}


void NameNodeOperations::SetFsEventCallback(fs_event_callback callback) {
  engine_.SetFsEventCallback(callback);
}

void NameNodeOperations::HdfsFileStatusProtoToStatInfo(
    hdfs::StatInfo & stat_info,
    const ::hadoop::hdfs::HdfsFileStatusProto & fs) {
  stat_info.file_type = fs.filetype();
  stat_info.length = fs.length();
  stat_info.permissions = fs.permission().perm();
  stat_info.owner = fs.owner();
  stat_info.group = fs.group();
  stat_info.modification_time = fs.modification_time();
  stat_info.access_time = fs.access_time();
  stat_info.symlink = fs.symlink();
  stat_info.block_replication = fs.block_replication();
  stat_info.blocksize = fs.blocksize();
  stat_info.fileid = fs.fileid();
  stat_info.children_num = fs.childrennum();
}

/*****************************************************************************
 *                    FILESYSTEM BASE CLASS
 ****************************************************************************/

FileSystem * FileSystem::New(
    IoService *&io_service, const std::string &user_name, const Options &options) {
  return new FileSystemImpl(io_service, user_name, options);
}

/*****************************************************************************
 *                    FILESYSTEM IMPLEMENTATION
 ****************************************************************************/

const std::string get_effective_user_name(const std::string &user_name) {
  if (!user_name.empty())
    return user_name;

  // If no user name was provided, try the HADOOP_USER_NAME and USER environment
  //    variables
  const char * env = getenv("HADOOP_USER_NAME");
  if (env) {
    return env;
  }

  env = getenv("USER");
  if (env) {
    return env;
  }

  // If running on POSIX, use the currently logged in user
#if defined(_POSIX_VERSION)
  uid_t uid = geteuid();
  struct passwd *pw = getpwuid(uid);
  if (pw && pw->pw_name)
  {
    return pw->pw_name;
  }
#endif

  return "unknown_user";
}

FileSystemImpl::FileSystemImpl(IoService *&io_service, const std::string &user_name,
                               const Options &options)
  :   options_(options),
      io_service_(static_cast<IoServiceImpl *>(io_service)),
      nn_(&io_service_->io_service(), options,
      GetRandomClientName(), get_effective_user_name(user_name), kNamenodeProtocol,
      kNamenodeProtocolVersion), client_name_(GetRandomClientName()),
      bad_node_tracker_(std::make_shared<BadDataNodeTracker>()),
      event_handlers_(std::make_shared<LibhdfsEvents>())
{
  LOG_TRACE(kFileSystem, << "FileSystemImpl::FileSystemImpl("
                         << FMT_THIS_ADDR << ") called");

  // Poor man's move
  io_service = nullptr;

  /* spawn background threads for asio delegation */
  unsigned int threads = 1 /* options.io_threads_, pending HDFS-9117 */;
  for (unsigned int i = 0; i < threads; i++) {
    AddWorkerThread();
  }
}

FileSystemImpl::~FileSystemImpl() {
  LOG_TRACE(kFileSystem, << "FileSystemImpl::~FileSystemImpl("
                         << FMT_THIS_ADDR << ") called");

  /**
   * Note: IoService must be stopped before getting rid of worker threads.
   * Once worker threads are joined and deleted the service can be deleted.
   **/
  io_service_->Stop();
  worker_threads_.clear();
}

void FileSystemImpl::Connect(const std::string &server,
                             const std::string &service,
                             const std::function<void(const Status &, FileSystem * fs)> &handler) {
  LOG_INFO(kFileSystem, << "FileSystemImpl::Connect(" << FMT_THIS_ADDR
                        << ", server=" << server << ", service="
                        << service << ") called");

  /* IoService::New can return nullptr */
  if (!io_service_) {
    handler (Status::Error("Null IoService"), this);
  }

  cluster_name_ = server + ":" + service;

  nn_.Connect(cluster_name_, server, service, [this, handler](const Status & s) {
    handler(s, this);
  });
}

Status FileSystemImpl::Connect(const std::string &server, const std::string &service) {
  LOG_INFO(kFileSystem, << "FileSystemImpl::[sync]Connect(" << FMT_THIS_ADDR
                        << ", server=" << server << ", service=" << service << ") called");

  /* synchronized */
  auto stat = std::make_shared<std::promise<Status>>();
  std::future<Status> future = stat->get_future();

  auto callback = [stat](const Status &s, FileSystem *fs) {
    (void)fs;
    stat->set_value(s);
  };

  Connect(server, service, callback);

  /* block until promise is set */
  auto s = future.get();

  return s;
}

void FileSystemImpl::ConnectToDefaultFs(const std::function<void(const Status &, FileSystem *)> &handler) {
  std::string scheme = options_.defaultFS.get_scheme();
  if (strcasecmp(scheme.c_str(), "hdfs") != 0) {
    std::string error_message;
    error_message += "defaultFS of [" + options_.defaultFS.str() + "] is not supported";
    handler(Status::InvalidArgument(error_message.c_str()), nullptr);
    return;
  }

  std::string host = options_.defaultFS.get_host();
  if (host.empty()) {
    handler(Status::InvalidArgument("defaultFS must specify a hostname"), nullptr);
    return;
  }

  optional<uint16_t>  port = options_.defaultFS.get_port();
  if (!port) {
    port = kDefaultPort;
  }
  std::string port_as_string = std::to_string(*port);

  Connect(host, port_as_string, handler);
}

Status FileSystemImpl::ConnectToDefaultFs() {
  auto stat = std::make_shared<std::promise<Status>>();
  std::future<Status> future = stat->get_future();

  auto callback = [stat](const Status &s, FileSystem *fs) {
    (void)fs;
    stat->set_value(s);
  };

  ConnectToDefaultFs(callback);

  /* block until promise is set */
  auto s = future.get();

  return s;
}



int FileSystemImpl::AddWorkerThread() {
  LOG_DEBUG(kFileSystem, << "FileSystemImpl::AddWorkerThread("
                                  << FMT_THIS_ADDR << ") called."
                                  << " Existing thread count = " << worker_threads_.size());

  auto service_task = [](IoService *service) { service->Run(); };
  worker_threads_.push_back(
      WorkerPtr(new std::thread(service_task, io_service_.get())));
  return worker_threads_.size();
}

void FileSystemImpl::Open(
    const std::string &path,
    const std::function<void(const Status &, FileHandle *)> &handler) {
  LOG_INFO(kFileSystem, << "FileSystemImpl::Open("
                                 << FMT_THIS_ADDR << ", path="
                                 << path << ") called");

  nn_.GetBlockLocations(path, [this, path, handler](const Status &stat, std::shared_ptr<const struct FileInfo> file_info) {
    handler(stat, stat.ok() ? new FileHandleImpl(cluster_name_, path, &io_service_->io_service(), client_name_, file_info, bad_node_tracker_, event_handlers_)
                            : nullptr);
  });
}

Status FileSystemImpl::Open(const std::string &path,
                                         FileHandle **handle) {
  LOG_INFO(kFileSystem, << "FileSystemImpl::[sync]Open("
                                 << FMT_THIS_ADDR << ", path="
                                 << path << ") called");

  auto callstate = std::make_shared<std::promise<std::tuple<Status, FileHandle*>>>();
  std::future<std::tuple<Status, FileHandle*>> future(callstate->get_future());

  /* wrap async FileSystem::Open with promise to make it a blocking call */
  auto h = [callstate](const Status &s, FileHandle *is) {
    callstate->set_value(std::make_tuple(s, is));
  };

  Open(path, h);

  /* block until promise is set */
  auto returnstate = future.get();
  Status stat = std::get<0>(returnstate);
  FileHandle *file_handle = std::get<1>(returnstate);

  if (!stat.ok()) {
    delete file_handle;
    return stat;
  }
  if (!file_handle) {
    return stat;
  }

  *handle = file_handle;
  return stat;
}

BlockLocation LocatedBlockToBlockLocation(const hadoop::hdfs::LocatedBlockProto & locatedBlock)
{
  BlockLocation result;

  result.setCorrupt(locatedBlock.corrupt());
  result.setOffset(locatedBlock.offset());

  std::vector<DNInfo> dn_info;
  dn_info.reserve(locatedBlock.locs_size());
  for (const hadoop::hdfs::DatanodeInfoProto & datanode_info: locatedBlock.locs()) {
    const hadoop::hdfs::DatanodeIDProto &id = datanode_info.id();
    DNInfo newInfo;
    if (id.has_ipaddr())
        newInfo.setIPAddr(id.ipaddr());
    if (id.has_hostname())
        newInfo.setHostname(id.hostname());
    if (id.has_xferport())
        newInfo.setXferPort(id.xferport());
    if (id.has_infoport())
        newInfo.setInfoPort(id.infoport());
    if (id.has_ipcport())
        newInfo.setIPCPort(id.ipcport());
    if (id.has_infosecureport())
      newInfo.setInfoSecurePort(id.infosecureport());
    dn_info.push_back(newInfo);
  }
  result.setDataNodes(dn_info);

  if (locatedBlock.has_b()) {
    const hadoop::hdfs::ExtendedBlockProto & b=locatedBlock.b();
    result.setLength(b.numbytes());
  }


  return result;
}

void FileSystemImpl::GetBlockLocations(const std::string & path,
  const std::function<void(const Status &, std::shared_ptr<FileBlockLocation> locations)> handler)
{
  LOG_DEBUG(kFileSystem, << "FileSystemImpl::GetBlockLocations("
                                 << FMT_THIS_ADDR << ", path="
                                 << path << ") called");

  auto conversion = [handler](const Status & status, std::shared_ptr<const struct FileInfo> fileInfo) {
    if (status.ok()) {
      auto result = std::make_shared<FileBlockLocation>();

      result->setFileLength(fileInfo->file_length_);
      result->setLastBlockComplete(fileInfo->last_block_complete_);
      result->setUnderConstruction(fileInfo->under_construction_);

      std::vector<BlockLocation> blocks;
      for (const hadoop::hdfs::LocatedBlockProto & locatedBlock: fileInfo->blocks_) {
          auto newLocation = LocatedBlockToBlockLocation(locatedBlock);
          blocks.push_back(newLocation);
      }
      result->setBlockLocations(blocks);

      handler(status, result);
    } else {
      handler(status, std::shared_ptr<FileBlockLocation>());
    }
  };

  nn_.GetBlockLocations(path, conversion);
}

Status FileSystemImpl::GetBlockLocations(const std::string & path,
  std::shared_ptr<FileBlockLocation> * fileBlockLocations)
{
  LOG_DEBUG(kFileSystem, << "FileSystemImpl::[sync]GetBlockLocations("
                                 << FMT_THIS_ADDR << ", path="
                                 << path << ") called");

  if (!fileBlockLocations)
    return Status::InvalidArgument("Null pointer passed to GetBlockLocations");

  auto callstate = std::make_shared<std::promise<std::tuple<Status, std::shared_ptr<FileBlockLocation>>>>();
  std::future<std::tuple<Status, std::shared_ptr<FileBlockLocation>>> future(callstate->get_future());

  /* wrap async call with promise/future to make it blocking */
  auto callback = [callstate](const Status &s, std::shared_ptr<FileBlockLocation> blockInfo) {
    callstate->set_value(std::make_tuple(s,blockInfo));
  };

  GetBlockLocations(path, callback);

  /* wait for async to finish */
  auto returnstate = future.get();
  auto stat = std::get<0>(returnstate);

  if (!stat.ok()) {
    return stat;
  }

  *fileBlockLocations = std::get<1>(returnstate);

  return stat;
}

void FileSystemImpl::GetFileInfo(
    const std::string &path,
    const std::function<void(const Status &, const StatInfo &)> &handler) {
  LOG_DEBUG(kFileSystem, << "FileSystemImpl::GetFileInfo("
                                 << FMT_THIS_ADDR << ", path="
                                 << path << ") called");

  nn_.GetFileInfo(path, [handler](const Status &stat, const StatInfo &stat_info) {
    handler(stat, stat_info);
  });
}

Status FileSystemImpl::GetFileInfo(const std::string &path,
                                         StatInfo & stat_info) {
  LOG_DEBUG(kFileSystem, << "FileSystemImpl::[sync]GetFileInfo("
                                 << FMT_THIS_ADDR << ", path="
                                 << path << ") called");

  auto callstate = std::make_shared<std::promise<std::tuple<Status, StatInfo>>>();
  std::future<std::tuple<Status, StatInfo>> future(callstate->get_future());

  /* wrap async FileSystem::GetFileInfo with promise to make it a blocking call */
  auto h = [callstate](const Status &s, const StatInfo &si) {
    callstate->set_value(std::make_tuple(s, si));
  };

  GetFileInfo(path, h);

  /* block until promise is set */
  auto returnstate = future.get();
  Status stat = std::get<0>(returnstate);
  StatInfo info = std::get<1>(returnstate);

  if (!stat.ok()) {
    return stat;
  }

  stat_info = info;
  return stat;
}

/**
 * Helper function for recursive GetListing calls.
 *
 * Some compilers don't like recursive lambdas, so we make the lambda call a
 * method, which in turn creates a lambda calling itself.
 */
void FileSystemImpl::GetListingShim(const Status &stat, std::shared_ptr<std::vector<StatInfo>> &stat_infos, bool has_more,
                        std::string path,
                        const std::function<bool(const Status &, std::shared_ptr<std::vector<StatInfo>>&, bool)> &handler) {
  bool has_next = stat_infos && stat_infos->size() > 0;
  bool get_more = handler(stat, stat_infos, has_more && has_next);
  if (get_more && has_more && has_next ) {
    auto callback = [this, path, handler](const Status &stat, std::shared_ptr<std::vector<StatInfo>> &stat_infos, bool has_more) {
      GetListingShim(stat, stat_infos, has_more, path, handler);
    };

    std::string last = stat_infos->back().path;
    nn_.GetListing(path, callback, last);
  }
}

void FileSystemImpl::GetListing(
    const std::string &path,
    const std::function<bool(const Status &, std::shared_ptr<std::vector<StatInfo>>&, bool)> &handler) {
  LOG_INFO(kFileSystem, << "FileSystemImpl::GetListing("
                                 << FMT_THIS_ADDR << ", path="
                                 << path << ") called");

  // Caputure the state and push it into the shim
  auto callback = [this, path, handler](const Status &stat, std::shared_ptr<std::vector<StatInfo>> &stat_infos, bool has_more) {
    GetListingShim(stat, stat_infos, has_more, path, handler);
  };

  nn_.GetListing(path, callback);
}

Status FileSystemImpl::GetListing(const std::string &path, std::shared_ptr<std::vector<StatInfo>> &stat_infos) {
  LOG_INFO(kFileSystem, << "FileSystemImpl::[sync]GetListing("
                                 << FMT_THIS_ADDR << ", path="
                                 << path << ") called");

  // In this case, we're going to allocate the result on the heap and have the
  //   async code populate it.
  auto results = std::make_shared<std::vector<StatInfo>>();

  auto callstate = std::make_shared<std::promise<Status>>();
  std::future<Status> future(callstate->get_future());

  /* wrap async FileSystem::GetListing with promise to make it a blocking call.
   *
     Keep requesting more until we get the entire listing, and don't set the promise
   * until we have the entire listing.
   */
  auto h = [callstate, results](const Status &s, std::shared_ptr<std::vector<StatInfo>> si, bool has_more) -> bool {
    if (si) {
      results->insert(results->end(), si->begin(), si->end());
    }

    bool done = !s.ok() || !has_more;
    if (done) {
      callstate->set_value(s);
      return false;
    }
    return true;
  };

  GetListing(path, h);

  /* block until promise is set */
  Status stat = future.get();

  if (!stat.ok()) {
    return stat;
  }

  stat_infos = results;
  return stat;
}


void FileSystemImpl::WorkerDeleter::operator()(std::thread *t) {
  // It is far too easy to destroy the filesystem (and thus the threadpool)
  //     from within one of the worker threads, leading to a deadlock.  Let's
  //     provide some explicit protection.
  if(t->get_id() == std::this_thread::get_id()) {
    LOG_ERROR(kFileSystem, << "FileSystemImpl::WorkerDeleter::operator(treadptr="
                           << t << ") : FATAL: Attempted to destroy a thread pool"
                           "from within a callback of the thread pool!");
  }
  t->join();
  delete t;
}


void FileSystemImpl::SetFsEventCallback(fs_event_callback callback) {
  if (event_handlers_) {
    event_handlers_->set_fs_callback(callback);
    nn_.SetFsEventCallback(callback);
  }
}



std::shared_ptr<LibhdfsEvents> FileSystemImpl::get_event_handlers() {
  return event_handlers_;
}

}
