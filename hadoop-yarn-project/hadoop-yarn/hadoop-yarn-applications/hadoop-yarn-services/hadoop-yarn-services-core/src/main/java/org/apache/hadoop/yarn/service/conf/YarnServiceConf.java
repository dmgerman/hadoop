begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|conf
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Configuration
import|;
end_import

begin_class
DECL|class|YarnServiceConf
specifier|public
class|class
name|YarnServiceConf
block|{
comment|// Retry settings for the ServiceClient to talk to Service AppMaster
DECL|field|CLIENT_AM_RETRY_MAX_WAIT_MS
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_AM_RETRY_MAX_WAIT_MS
init|=
literal|"yarn.service.client-am.retry.max-wait-ms"
decl_stmt|;
DECL|field|CLIENT_AM_RETRY_MAX_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_AM_RETRY_MAX_INTERVAL_MS
init|=
literal|"yarn.service.client-am.retry-interval-ms"
decl_stmt|;
comment|// Retry settings for container failures
DECL|field|CONTAINER_RETRY_MAX
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_RETRY_MAX
init|=
literal|"yarn.service.container-failure.retry.max"
decl_stmt|;
DECL|field|CONTAINER_RETRY_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_RETRY_INTERVAL
init|=
literal|"yarn.service.container-failure.retry-interval-ms"
decl_stmt|;
DECL|field|AM_RESTART_MAX
specifier|public
specifier|static
specifier|final
name|String
name|AM_RESTART_MAX
init|=
literal|"yarn.service.am-restart.max-attempts"
decl_stmt|;
DECL|field|AM_RESOURCE_MEM
specifier|public
specifier|static
specifier|final
name|String
name|AM_RESOURCE_MEM
init|=
literal|"yarn.service.am-resource.memory"
decl_stmt|;
DECL|field|DEFAULT_KEY_AM_RESOURCE_MEM
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_KEY_AM_RESOURCE_MEM
init|=
literal|1024
decl_stmt|;
DECL|field|YARN_QUEUE
specifier|public
specifier|static
specifier|final
name|String
name|YARN_QUEUE
init|=
literal|"yarn.service.queue"
decl_stmt|;
DECL|field|API_SERVER_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|API_SERVER_ADDRESS
init|=
literal|"yarn.service.api-server.address"
decl_stmt|;
DECL|field|DEFAULT_API_SERVER_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_API_SERVER_ADDRESS
init|=
literal|"0.0.0.0:"
decl_stmt|;
DECL|field|DEFAULT_API_SERVER_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_API_SERVER_PORT
init|=
literal|9191
decl_stmt|;
DECL|field|FINAL_LOG_INCLUSION_PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|FINAL_LOG_INCLUSION_PATTERN
init|=
literal|"yarn.service.log.include-pattern"
decl_stmt|;
DECL|field|FINAL_LOG_EXCLUSION_PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|FINAL_LOG_EXCLUSION_PATTERN
init|=
literal|"yarn.service.log.exclude-pattern"
decl_stmt|;
DECL|field|ROLLING_LOG_INCLUSION_PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|ROLLING_LOG_INCLUSION_PATTERN
init|=
literal|"yarn.service.rolling-log.include-pattern"
decl_stmt|;
DECL|field|ROLLING_LOG_EXCLUSION_PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|ROLLING_LOG_EXCLUSION_PATTERN
init|=
literal|"yarn.service.rolling-log.exclude-pattern"
decl_stmt|;
comment|/**    * The yarn service base path:    * Defaults to HomeDir/.yarn/    */
DECL|field|YARN_SERVICE_BASE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|YARN_SERVICE_BASE_PATH
init|=
literal|"yarn.service.base.path"
decl_stmt|;
comment|/**    * maximum number of failed containers (in a single component)    * before the app exits    */
DECL|field|CONTAINER_FAILURE_THRESHOLD
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_FAILURE_THRESHOLD
init|=
literal|"yarn.service.container-failure-per-component.threshold"
decl_stmt|;
comment|/**    * Maximum number of container failures on a node before the node is blacklisted    */
DECL|field|NODE_BLACKLIST_THRESHOLD
specifier|public
specifier|static
specifier|final
name|String
name|NODE_BLACKLIST_THRESHOLD
init|=
literal|"yarn.service.node-blacklist.threshold"
decl_stmt|;
comment|/**    * The failure count for CONTAINER_FAILURE_THRESHOLD and NODE_BLACKLIST_THRESHOLD    * gets reset periodically, the unit is seconds.    */
DECL|field|CONTAINER_FAILURE_WINDOW
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_FAILURE_WINDOW
init|=
literal|"yarn.service.failure-count-reset.window"
decl_stmt|;
comment|/**    * interval between readiness checks.    */
DECL|field|READINESS_CHECK_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|READINESS_CHECK_INTERVAL
init|=
literal|"yarn.service.readiness-check-interval.seconds"
decl_stmt|;
DECL|field|DEFAULT_READINESS_CHECK_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_READINESS_CHECK_INTERVAL
init|=
literal|30
decl_stmt|;
comment|// seconds
comment|/**    * JVM opts.    */
DECL|field|JVM_OPTS
specifier|public
specifier|static
specifier|final
name|String
name|JVM_OPTS
init|=
literal|"yarn.service.am.java.opts"
decl_stmt|;
comment|/**    * Get long value for the property. First get from the userConf, if not    * present, get from systemConf.    *    * @param name name of the property    * @param defaultValue default value of the property, if it is not defined in    *                     userConf and systemConf.    * @param userConf Configuration provided by client in the JSON definition    * @param systemConf The YarnConfiguration in the system.    * @return long value for the property    */
DECL|method|getLong (String name, long defaultValue, Configuration userConf, org.apache.hadoop.conf.Configuration systemConf)
specifier|public
specifier|static
name|long
name|getLong
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|defaultValue
parameter_list|,
name|Configuration
name|userConf
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
name|systemConf
parameter_list|)
block|{
return|return
name|userConf
operator|.
name|getPropertyLong
argument_list|(
name|name
argument_list|,
name|systemConf
operator|.
name|getLong
argument_list|(
name|name
argument_list|,
name|defaultValue
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getInt (String name, int defaultValue, Configuration userConf, org.apache.hadoop.conf.Configuration systemConf)
specifier|public
specifier|static
name|int
name|getInt
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|defaultValue
parameter_list|,
name|Configuration
name|userConf
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
name|systemConf
parameter_list|)
block|{
return|return
name|userConf
operator|.
name|getPropertyInt
argument_list|(
name|name
argument_list|,
name|systemConf
operator|.
name|getInt
argument_list|(
name|name
argument_list|,
name|defaultValue
argument_list|)
argument_list|)
return|;
block|}
DECL|method|get (String name, String defaultVal, Configuration userConf, org.apache.hadoop.conf.Configuration systemConf)
specifier|public
specifier|static
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|defaultVal
parameter_list|,
name|Configuration
name|userConf
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
name|systemConf
parameter_list|)
block|{
return|return
name|userConf
operator|.
name|getProperty
argument_list|(
name|name
argument_list|,
name|systemConf
operator|.
name|get
argument_list|(
name|name
argument_list|,
name|defaultVal
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

