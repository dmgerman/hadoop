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

#include "fuse_dfs.h"
#include "fuse_impls.h"
#include "fuse_trash.h"
#include "fuse_connect.h"

int dfs_mkdir(const char *path, mode_t mode)
{
  TRACE1("mkdir", path)

  // retrieve dfs specific data
  dfs_context *dfs = (dfs_context*)fuse_get_context()->private_data;

  // check params and the context var
  assert(path);
  assert(dfs);
  assert('/' == *path);

  if (is_protected(path)) {
    ERROR("HDFS trying to create directory %s", path);
    return -EACCES;
  }

  if (dfs->read_only) {
    ERROR("HDFS is configured read-only, cannot create directory %s", path);
    return -EACCES;
  }
  
  hdfsFS userFS;
  // if not connected, try to connect and fail out if we can't.
  if ((userFS = doConnectAsUser(dfs->nn_hostname,dfs->nn_port))== NULL) {
    ERROR("Could not connect");
    return -EIO;
  }

  // In theory the create and chmod should be atomic.

  if (hdfsCreateDirectory(userFS, path)) {
    ERROR("HDFS could not create directory %s", path);
    return -EIO;
  }

#if PERMS
  if (hdfsChmod(userFS, path, (short)mode)) {
    ERROR("Could not chmod %s to %d", path, (int)mode);
    return -EIO;
  }
#endif
  return 0;
}
