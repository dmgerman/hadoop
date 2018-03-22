/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
*/

#ifndef TOOLS_COMMON_H_
#define TOOLS_COMMON_H_

#include "hdfspp/hdfspp.h"
#include "common/hdfs_configuration.h"
#include "common/configuration_loader.h"

#include <mutex>

namespace hdfs {

  //Pull configurations and get the Options object
  std::shared_ptr<hdfs::Options> getOptions();

  //Build all necessary objects and perform the connection
  std::shared_ptr<hdfs::FileSystem> doConnect(hdfs::URI & uri, hdfs::Options & options);

}

#endif
