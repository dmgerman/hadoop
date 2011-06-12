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

#ifndef __FUSE_STAT_STRUCT_H__
#define __FUSE_STAT_STRUCT_H__

#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

#include "hdfs.h"

/**
 * Converts from a hdfs hdfsFileInfo to a POSIX stat struct
 * Should be thread safe.
 */
int fill_stat_structure(hdfsFileInfo *info, struct stat *st) ;

extern const int default_id;
extern const int blksize;
#endif
