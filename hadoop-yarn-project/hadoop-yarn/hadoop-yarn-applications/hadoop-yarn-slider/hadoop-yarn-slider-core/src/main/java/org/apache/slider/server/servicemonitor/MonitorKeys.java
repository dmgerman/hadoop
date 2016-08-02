begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.servicemonitor
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|servicemonitor
package|;
end_package

begin_comment
comment|/**  * Config keys for monitoring  */
end_comment

begin_interface
DECL|interface|MonitorKeys
specifier|public
interface|interface
name|MonitorKeys
block|{
comment|/**    * Prefix of all other configuration options: {@value}    */
DECL|field|MONITOR_KEY_PREFIX
name|String
name|MONITOR_KEY_PREFIX
init|=
literal|"service.monitor."
decl_stmt|;
comment|/**    * Classname of the reporter Key: {@value}    */
DECL|field|MONITOR_REPORTER
name|String
name|MONITOR_REPORTER
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"report.classname"
decl_stmt|;
comment|/**    * Interval in milliseconds between reporting health status to the reporter    * Key: {@value}    */
DECL|field|MONITOR_REPORT_INTERVAL
name|String
name|MONITOR_REPORT_INTERVAL
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"report.interval"
decl_stmt|;
comment|/**    * Time in millis between the last probing cycle ending and the new one    * beginning. Key: {@value}    */
DECL|field|MONITOR_PROBE_INTERVAL
name|String
name|MONITOR_PROBE_INTERVAL
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"probe.interval"
decl_stmt|;
comment|/**    * How long in milliseconds does the probing loop have to be blocked before    * that is considered a liveness failure Key: {@value}    */
DECL|field|MONITOR_PROBE_TIMEOUT
name|String
name|MONITOR_PROBE_TIMEOUT
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"probe.timeout"
decl_stmt|;
comment|/**    * How long in milliseconds does the probing loop have to be blocked before    * that is considered a liveness failure Key: {@value}    */
DECL|field|MONITOR_BOOTSTRAP_TIMEOUT
name|String
name|MONITOR_BOOTSTRAP_TIMEOUT
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"bootstrap.timeout"
decl_stmt|;
comment|/**    * does the monitor depend on DFS being live    */
DECL|field|MONITOR_DEPENDENCY_DFSLIVE
name|String
name|MONITOR_DEPENDENCY_DFSLIVE
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"dependency.dfslive"
decl_stmt|;
comment|/**    * default timeout for the entire bootstrap phase {@value}    */
DECL|field|BOOTSTRAP_TIMEOUT_DEFAULT
name|int
name|BOOTSTRAP_TIMEOUT_DEFAULT
init|=
literal|60000
decl_stmt|;
comment|/**    * Default value if the key is not in the config file: {@value}    */
DECL|field|REPORT_INTERVAL_DEFAULT
name|int
name|REPORT_INTERVAL_DEFAULT
init|=
literal|10000
decl_stmt|;
comment|/**    * Default value if the key is not in the config file: {@value}    */
DECL|field|PROBE_INTERVAL_DEFAULT
name|int
name|PROBE_INTERVAL_DEFAULT
init|=
literal|10000
decl_stmt|;
comment|/**    * Default value if the key is not in the config file: {@value}    */
DECL|field|PROBE_TIMEOUT_DEFAULT
name|int
name|PROBE_TIMEOUT_DEFAULT
init|=
literal|60000
decl_stmt|;
comment|/**    * Port probe enabled/disabled flag Key: {@value}    */
DECL|field|PORT_PROBE_ENABLED
name|String
name|PORT_PROBE_ENABLED
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"portprobe.enabled"
decl_stmt|;
comment|/**    * Port probing key : port to attempt to create a TCP connection to {@value}    */
DECL|field|PORT_PROBE_PORT
name|String
name|PORT_PROBE_PORT
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"portprobe.port"
decl_stmt|;
comment|/**    * Port probing key : port to attempt to create a TCP connection to {@value}    */
DECL|field|PORT_PROBE_HOST
name|String
name|PORT_PROBE_HOST
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"portprobe.host"
decl_stmt|;
comment|/**    * Port probing key : timeout of the connection attempt {@value}    */
DECL|field|PORT_PROBE_CONNECT_TIMEOUT
name|String
name|PORT_PROBE_CONNECT_TIMEOUT
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"portprobe.connect.timeout"
decl_stmt|;
comment|/**    * Port probing key : bootstrap timeout -how long in milliseconds should the    * port probing take to connect before the failure to connect is considered a    * liveness failure. That is: how long should the IPC port take to come up?    * {@value}    */
DECL|field|PORT_PROBE_BOOTSTRAP_TIMEOUT
name|String
name|PORT_PROBE_BOOTSTRAP_TIMEOUT
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"portprobe.bootstrap.timeout"
decl_stmt|;
comment|/**    * default timeout for port probes {@value}    */
DECL|field|PORT_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
name|int
name|PORT_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
init|=
literal|60000
decl_stmt|;
comment|/**    * default value for port probe connection attempts {@value}    */
DECL|field|PORT_PROBE_CONNECT_TIMEOUT_DEFAULT
name|int
name|PORT_PROBE_CONNECT_TIMEOUT_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/**    * default port for probes {@value}    */
DECL|field|DEFAULT_PROBE_PORT
name|int
name|DEFAULT_PROBE_PORT
init|=
literal|8020
decl_stmt|;
comment|/**    * default host for probes {@value}    */
DECL|field|DEFAULT_PROBE_HOST
name|String
name|DEFAULT_PROBE_HOST
init|=
literal|"localhost"
decl_stmt|;
comment|/**    * Probe enabled/disabled flag Key: {@value}    */
DECL|field|LS_PROBE_ENABLED
name|String
name|LS_PROBE_ENABLED
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"lsprobe.enabled"
decl_stmt|;
comment|/**    * Probe path for LS operation Key: {@value}    */
DECL|field|LS_PROBE_PATH
name|String
name|LS_PROBE_PATH
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"lsprobe.path"
decl_stmt|;
comment|/**    * Default path for LS operation Key: {@value}    */
DECL|field|LS_PROBE_DEFAULT
name|String
name|LS_PROBE_DEFAULT
init|=
literal|"/"
decl_stmt|;
comment|/**    * Port probing key : bootstrap timeout -how long in milliseconds should the    * port probing take to connect before the failure to connect is considered a    * liveness failure. That is: how long should the IPC port take to come up?    * {@value}    */
DECL|field|LS_PROBE_BOOTSTRAP_TIMEOUT
name|String
name|LS_PROBE_BOOTSTRAP_TIMEOUT
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"lsprobe.bootstrap.timeout"
decl_stmt|;
comment|/**    * default timeout for port probes {@value}    */
DECL|field|LS_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
name|int
name|LS_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
init|=
name|PORT_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
decl_stmt|;
comment|/**    * Probe enabled/disabled flag Key: {@value}    */
DECL|field|WEB_PROBE_ENABLED
name|String
name|WEB_PROBE_ENABLED
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"webprobe.enabled"
decl_stmt|;
comment|/**    * Probe URL Key: {@value}    */
DECL|field|WEB_PROBE_URL
name|String
name|WEB_PROBE_URL
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"webprobe.url"
decl_stmt|;
comment|/**    * Default path for web probe Key: {@value}    */
DECL|field|WEB_PROBE_DEFAULT_URL
name|String
name|WEB_PROBE_DEFAULT_URL
init|=
literal|"http://localhost:50070/"
decl_stmt|;
comment|/**    * min error code Key: {@value}    */
DECL|field|WEB_PROBE_MIN
name|String
name|WEB_PROBE_MIN
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"webprobe.min"
decl_stmt|;
comment|/**    * min error code Key: {@value}    */
DECL|field|WEB_PROBE_MAX
name|String
name|WEB_PROBE_MAX
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"webprobe.max"
decl_stmt|;
comment|/**    * Port probing key : timeout of the connection attempt {@value}    */
DECL|field|WEB_PROBE_CONNECT_TIMEOUT
name|String
name|WEB_PROBE_CONNECT_TIMEOUT
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"webprobe.connect.timeout"
decl_stmt|;
comment|/**    * Default HTTP response code expected from the far end for    * the endpoint to be considered live.    */
DECL|field|WEB_PROBE_DEFAULT_CODE
name|int
name|WEB_PROBE_DEFAULT_CODE
init|=
literal|200
decl_stmt|;
comment|/**    * Port probing key : bootstrap timeout -how long in milliseconds should the    * port probing take to connect before the failure to connect is considered a    * liveness failure. That is: how long should the IPC port take to come up?    * {@value}    */
DECL|field|WEB_PROBE_BOOTSTRAP_TIMEOUT
name|String
name|WEB_PROBE_BOOTSTRAP_TIMEOUT
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"webprobe.bootstrap.timeout"
decl_stmt|;
comment|/**    * default timeout for port probes {@value}    */
DECL|field|WEB_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
name|int
name|WEB_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
init|=
name|PORT_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
decl_stmt|;
comment|/**    * Probe enabled/disabled flag Key: {@value}    */
DECL|field|JT_PROBE_ENABLED
name|String
name|JT_PROBE_ENABLED
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"jtprobe.enabled"
decl_stmt|;
comment|/**    * Port probing key : bootstrap timeout -how long in milliseconds should the    * port probing take to connect before the failure to connect is considered a    * liveness failure. That is: how long should the IPC port take to come up?    * {@value}    */
DECL|field|JT_PROBE_BOOTSTRAP_TIMEOUT
name|String
name|JT_PROBE_BOOTSTRAP_TIMEOUT
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"jtprobe.bootstrap.timeout"
decl_stmt|;
comment|/**    * default timeout for port probes {@value}    */
DECL|field|JT_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
name|int
name|JT_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
init|=
name|PORT_PROBE_BOOTSTRAP_TIMEOUT_DEFAULT
decl_stmt|;
comment|/**    * Probe enabled/disabled flag Key: {@value}    */
DECL|field|PID_PROBE_ENABLED
name|String
name|PID_PROBE_ENABLED
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"pidprobe.enabled"
decl_stmt|;
comment|/**    * PID probing key : pid to attempt to create a TCP connection to {@value}    */
DECL|field|PID_PROBE_PIDFILE
name|String
name|PID_PROBE_PIDFILE
init|=
name|MONITOR_KEY_PREFIX
operator|+
literal|"pidprobe.pidfile"
decl_stmt|;
block|}
end_interface

end_unit

