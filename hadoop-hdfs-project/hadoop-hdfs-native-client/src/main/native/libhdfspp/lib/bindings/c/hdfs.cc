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

#include "hdfspp/hdfspp.h"

#include "fs/filesystem.h"
#include "common/hdfs_configuration.h"
#include "common/configuration_loader.h"

#include <hdfs/hdfs.h>
#include <hdfspp/hdfs_ext.h>

#include <string>
#include <cstring>
#include <iostream>
#include <algorithm>

using namespace hdfs;
using std::experimental::nullopt;

static constexpr tPort kDefaultPort = 8020;

/* Separate the handles used by the C api from the C++ API*/
struct hdfs_internal {
  hdfs_internal(FileSystem *p) : filesystem_(p) {}
  hdfs_internal(std::unique_ptr<FileSystem> p)
      : filesystem_(std::move(p)) {}
  virtual ~hdfs_internal(){};
  FileSystem *get_impl() { return filesystem_.get(); }
  const FileSystem *get_impl() const { return filesystem_.get(); }

 private:
  std::unique_ptr<FileSystem> filesystem_;
};

struct hdfsFile_internal {
  hdfsFile_internal(FileHandle *p) : file_(p) {}
  hdfsFile_internal(std::unique_ptr<FileHandle> p) : file_(std::move(p)) {}
  virtual ~hdfsFile_internal(){};
  FileHandle *get_impl() { return file_.get(); }
  const FileHandle *get_impl() const { return file_.get(); }

 private:
  std::unique_ptr<FileHandle> file_;
};

/* Keep thread local copy of last error string */
thread_local std::string errstr;

/* Fetch last error that happened in this thread */
void hdfsGetLastError(char *buf, int len) {
  if(nullptr == buf || len < 1) {
    return;
  }

  /* leave space for a trailing null */
  size_t copylen = std::min((size_t)errstr.size(), (size_t)len);
  if(copylen == (size_t)len) {
    copylen--;
  }

  strncpy(buf, errstr.c_str(), copylen);

  /* stick in null */
  buf[copylen] = 0;
}

struct hdfsBuilder {
  hdfsBuilder();
  hdfsBuilder(const char * directory);
  virtual ~hdfsBuilder() {}
  ConfigurationLoader loader;
  HdfsConfiguration config;

  optional<std::string> overrideHost;
  optional<tPort>       overridePort;
  optional<std::string> user;

  static constexpr tPort kUseDefaultPort = 0;
};

/* Error handling with optional debug to stderr */
static void ReportError(int errnum, const std::string & msg) {
  errno = errnum;
  errstr = msg;
#ifdef LIBHDFSPP_C_API_ENABLE_DEBUG
  std::cerr << "Error: errno=" << strerror(errnum) << " message=\"" << msg
            << "\"" << std::endl;
#else
  (void)msg;
#endif
}

/* Convert Status wrapped error into appropriate errno and return code */
static int Error(const Status &stat) {
  const char * default_message;
  int errnum;

  int code = stat.code();
  switch (code) {
    case Status::Code::kOk:
      return 0;
    case Status::Code::kInvalidArgument:
      errnum = EINVAL;
      default_message = "Invalid argument";
      break;
    case Status::Code::kResourceUnavailable:
      errnum = EAGAIN;
      default_message = "Resource temporarily unavailable";
      break;
    case Status::Code::kUnimplemented:
      errnum = ENOSYS;
      default_message = "Function not implemented";
      break;
    case Status::Code::kException:
      errnum = EINTR;
      default_message = "Exception raised";
      break;
    case Status::Code::kOperationCanceled:
      errnum = EINTR;
      default_message = "Operation canceled";
      break;
    case Status::Code::kPermissionDenied:
      errnum = EACCES;
      default_message = "Permission denied";
      break;
    default:
      errnum = ENOSYS;
      default_message = "Error: unrecognised code";
  }
  if (stat.ToString().empty())
    ReportError(errnum, default_message);
  else
    ReportError(errnum, stat.ToString());
  return -1;
}

static int ReportException(const std::exception & e)
{
  return Error(Status::Exception("Uncaught exception", e.what()));
}

static int ReportCaughtNonException()
{
  return Error(Status::Exception("Uncaught value not derived from std::exception", ""));
}

/* return false on failure */
bool CheckSystemAndHandle(hdfsFS fs, hdfsFile file) {
  if (!fs) {
    ReportError(ENODEV, "Cannot perform FS operations with null FS handle.");
    return false;
  }
  if (!file) {
    ReportError(EBADF, "Cannot perform FS operations with null File handle.");
    return false;
  }
  return true;
}

/**
 * C API implementations
 **/

int hdfsFileIsOpenForRead(hdfsFile file) {
  /* files can only be open for reads at the moment, do a quick check */
  if (file) {
    return 1; // Update implementation when we get file writing
  }
  return 0;
}

hdfsFS doHdfsConnect(optional<std::string> nn, optional<tPort> port, optional<std::string> user, const Options & options) {
  try
  {
    IoService * io_service = IoService::New();

    FileSystem *fs = FileSystem::New(io_service, user.value_or(""), options);
    if (!fs) {
      ReportError(ENODEV, "Could not create FileSystem object");
      return nullptr;
    }

    Status status;
    if (nn || port) {
      if (!port) {
        port = kDefaultPort;
      }
      std::string port_as_string = std::to_string(*port);
      status = fs->Connect(nn.value_or(""), port_as_string);
    } else {
      status = fs->ConnectToDefaultFs();
    }

    if (!status.ok()) {
      Error(status);

      // FileSystem's ctor might take ownership of the io_service; if it does,
      //    it will null out the pointer
      if (io_service)
        delete io_service;

      delete fs;

      return nullptr;
    }
    return new hdfs_internal(fs);
  } catch (const std::exception & e) {
    ReportException(e);
    return nullptr;
  } catch (...) {
    ReportCaughtNonException();
    return nullptr;
  }
}

hdfsFS hdfsConnect(const char *nn, tPort port) {
  return hdfsConnectAsUser(nn, port, "");
}

hdfsFS hdfsConnectAsUser(const char* nn, tPort port, const char *user) {
  return doHdfsConnect(std::string(nn), port, std::string(user), Options());
}

int hdfsDisconnect(hdfsFS fs) {
  try
  {
    if (!fs) {
      ReportError(ENODEV, "Cannot disconnect null FS handle.");
      return -1;
    }

    delete fs;
    return 0;
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

hdfsFile hdfsOpenFile(hdfsFS fs, const char *path, int flags, int bufferSize,
                      short replication, tSize blocksize) {
  try
  {
    (void)flags;
    (void)bufferSize;
    (void)replication;
    (void)blocksize;
    if (!fs) {
      ReportError(ENODEV, "Cannot perform FS operations with null FS handle.");
      return nullptr;
    }
    FileHandle *f = nullptr;
    Status stat = fs->get_impl()->Open(path, &f);
    if (!stat.ok()) {
      Error(stat);
      return nullptr;
    }
    return new hdfsFile_internal(f);
  } catch (const std::exception & e) {
    ReportException(e);
    return nullptr;
  } catch (...) {
    ReportCaughtNonException();
    return nullptr;
  }
}

int hdfsCloseFile(hdfsFS fs, hdfsFile file) {
  try
  {
    if (!CheckSystemAndHandle(fs, file)) {
      return -1;
    }
    delete file;
    return 0;
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

tSize hdfsPread(hdfsFS fs, hdfsFile file, tOffset position, void *buffer,
                tSize length) {
  try
  {
    if (!CheckSystemAndHandle(fs, file)) {
      return -1;
    }

    size_t len = length;
    Status stat = file->get_impl()->PositionRead(buffer, &len, position);
    if(!stat.ok()) {
      return Error(stat);
    }
    return (tSize)len;
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

tSize hdfsRead(hdfsFS fs, hdfsFile file, void *buffer, tSize length) {
  try
  {
  if (!CheckSystemAndHandle(fs, file)) {
    return -1;
  }

    size_t len = length;
    Status stat = file->get_impl()->Read(buffer, &len);
    if (!stat.ok()) {
      return Error(stat);
    }

    return (tSize)len;
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

/* 0 on success, -1 on error*/
int hdfsSeek(hdfsFS fs, hdfsFile file, tOffset desiredPos) {
  try
  {
    if (!CheckSystemAndHandle(fs, file)) {
      return -1;
    }

    off_t desired = desiredPos;
    Status stat = file->get_impl()->Seek(&desired, std::ios_base::beg);
    if (!stat.ok()) {
      return Error(stat);
    }

    return 0;
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

tOffset hdfsTell(hdfsFS fs, hdfsFile file) {
  try
  {
    if (!CheckSystemAndHandle(fs, file)) {
      return -1;
    }

    ssize_t offset = 0;
    Status stat = file->get_impl()->Seek(&offset, std::ios_base::cur);
    if (!stat.ok()) {
      return Error(stat);
    }

    return offset;
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

/* extended API */
int hdfsCancel(hdfsFS fs, hdfsFile file) {
  try
  {
    if (!CheckSystemAndHandle(fs, file)) {
      return -1;
    }
    static_cast<FileHandleImpl*>(file->get_impl())->CancelOperations();
    return 0;
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

/*******************************************************************
 *                BUILDER INTERFACE
 *******************************************************************/

HdfsConfiguration LoadDefault(ConfigurationLoader & loader)
{
  optional<HdfsConfiguration> result = loader.LoadDefaultResources<HdfsConfiguration>();
  if (result)
  {
    return result.value();
  }
  else
  {
    return loader.New<HdfsConfiguration>();
  }
}

hdfsBuilder::hdfsBuilder() : config(loader.New<HdfsConfiguration>())
{
  loader.SetDefaultSearchPath();
  config = LoadDefault(loader);
}

hdfsBuilder::hdfsBuilder(const char * directory) :
      config(loader.New<HdfsConfiguration>())
{
  loader.SetSearchPath(directory);
  config = LoadDefault(loader);
}

struct hdfsBuilder *hdfsNewBuilder(void)
{
  try
  {
    return new struct hdfsBuilder();
  } catch (const std::exception & e) {
    ReportException(e);
    return nullptr;
  } catch (...) {
    ReportCaughtNonException();
    return nullptr;
  }
}

void hdfsBuilderSetNameNode(struct hdfsBuilder *bld, const char *nn)
{
  bld->overrideHost = std::string(nn);
}

void hdfsBuilderSetNameNodePort(struct hdfsBuilder *bld, tPort port)
{
  bld->overridePort = port;
}

void hdfsBuilderSetUserName(struct hdfsBuilder *bld, const char *userName)
{
  if (userName && *userName) {
    bld->user = std::string(userName);
  }
}


void hdfsFreeBuilder(struct hdfsBuilder *bld)
{
  try
  {
    delete bld;
  } catch (const std::exception & e) {
    ReportException(e);
  } catch (...) {
    ReportCaughtNonException();
  }
}

int hdfsBuilderConfSetStr(struct hdfsBuilder *bld, const char *key,
                          const char *val)
{
  try
  {
    optional<HdfsConfiguration> newConfig = bld->loader.OverlayValue(bld->config, key, val);
    if (newConfig)
    {
      bld->config = newConfig.value();
      return 0;
    }
    else
    {
      ReportError(EINVAL, "Could not change Builder value");
      return 1;
    }
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

void hdfsConfStrFree(char *val)
{
  free(val);
}

hdfsFS hdfsBuilderConnect(struct hdfsBuilder *bld) {
  return doHdfsConnect(bld->overrideHost, bld->overridePort, bld->user, bld->config.GetOptions());
}

int hdfsConfGetStr(const char *key, char **val)
{
  try
  {
    hdfsBuilder builder;
    return hdfsBuilderConfGetStr(&builder, key, val);
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

int hdfsConfGetInt(const char *key, int32_t *val)
{
  try
  {
    hdfsBuilder builder;
    return hdfsBuilderConfGetInt(&builder, key, val);
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

//
//  Extended builder interface
//
struct hdfsBuilder *hdfsNewBuilderFromDirectory(const char * configDirectory)
{
  try
  {
    return new struct hdfsBuilder(configDirectory);
  } catch (const std::exception & e) {
    ReportException(e);
    return nullptr;
  } catch (...) {
    ReportCaughtNonException();
    return nullptr;
  }
}

int hdfsBuilderConfGetStr(struct hdfsBuilder *bld, const char *key,
                          char **val)
{
  try
  {
    optional<std::string> value = bld->config.Get(key);
    if (value)
    {
      size_t len = value->length() + 1;
      *val = static_cast<char *>(malloc(len));
      strncpy(*val, value->c_str(), len);
    }
    else
    {
      *val = nullptr;
    }
    return 0;
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}

// If we're running on a 32-bit platform, we might get 64-bit values that
//    don't fit in an int, and int is specified by the java hdfs.h interface
bool isValidInt(int64_t value)
{
  return (value >= std::numeric_limits<int>::min() &&
          value <= std::numeric_limits<int>::max());
}

int hdfsBuilderConfGetInt(struct hdfsBuilder *bld, const char *key, int32_t *val)
{
  try
  {
    // Pull from default configuration
    optional<int64_t> value = bld->config.GetInt(key);
    if (value)
    {
      if (!isValidInt(*value))
        return 1;

      *val = *value;
    }
    // If not found, don't change val
    ReportError(EINVAL, "Could not get Builder value");
    return 0;
  } catch (const std::exception & e) {
    return ReportException(e);
  } catch (...) {
    return ReportCaughtNonException();
  }
}
