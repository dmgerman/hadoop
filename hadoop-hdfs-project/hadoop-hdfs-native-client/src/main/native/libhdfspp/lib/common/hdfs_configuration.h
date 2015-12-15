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

#ifndef COMMON_HDFS_CONFIGURATION_H_
#define COMMON_HDFS_CONFIGURATION_H_

#include "common/configuration.h"
#include "libhdfspp/options.h"

#include <string>
#include <map>
#include <vector>
#include <set>
#include <istream>
#include <stdint.h>

namespace hdfs {

class HdfsConfiguration : public Configuration {
  public:
    // Interprets the resources to build an Options object
    Options GetOptions();

    // Keys to look for in the configuration file
    static constexpr const char * kDfsClientSocketTimeoutKey = "dfs.client.socket-timeout";
    static constexpr const char * kIpcClientConnectMaxRetriesKey = "ipc.client.connect.max.retries";
    static constexpr const char * kIpcClientConnectRetryIntervalKey = "ipc.client.connect.retry.interval";
private:
    friend class ConfigurationLoader;

    // Constructs a configuration with no search path and no resources loaded
    HdfsConfiguration();

    // Constructs a configuration with some static data
    HdfsConfiguration(ConfigMap &src_map);

    static std::vector<std::string> GetDefaultFilenames();
};

}

#endif
