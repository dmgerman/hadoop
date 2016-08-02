begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.management
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|management
package|;
end_package

begin_interface
DECL|interface|MetricsKeys
specifier|public
interface|interface
name|MetricsKeys
block|{
comment|/**    * Prefix for metrics configuration options: {@value}    */
DECL|field|METRICS_PREFIX
name|String
name|METRICS_PREFIX
init|=
literal|"slider.metrics."
decl_stmt|;
comment|/**    * Boolean to enable Ganglia metrics reporting    * {@value}    */
DECL|field|METRICS_GANGLIA_ENABLED
name|String
name|METRICS_GANGLIA_ENABLED
init|=
name|METRICS_PREFIX
operator|+
literal|"ganglia.enabled"
decl_stmt|;
comment|/**    * {@value}    */
DECL|field|METRICS_GANGLIA_HOST
name|String
name|METRICS_GANGLIA_HOST
init|=
name|METRICS_PREFIX
operator|+
literal|"ganglia.host"
decl_stmt|;
comment|/**    * {@value}    */
DECL|field|METRICS_GANGLIA_PORT
name|String
name|METRICS_GANGLIA_PORT
init|=
name|METRICS_PREFIX
operator|+
literal|"ganglia.port"
decl_stmt|;
comment|/**    * {@value}    */
DECL|field|METRICS_GANGLIA_VERSION_31
name|String
name|METRICS_GANGLIA_VERSION_31
init|=
name|METRICS_PREFIX
operator|+
literal|"ganglia.version-31"
decl_stmt|;
comment|/**    * {@value}    */
DECL|field|METRICS_GANGLIA_REPORT_INTERVAL
name|String
name|METRICS_GANGLIA_REPORT_INTERVAL
init|=
name|METRICS_PREFIX
operator|+
literal|"ganglia.report.interval"
decl_stmt|;
comment|/**    * {@value}    */
DECL|field|DEFAULT_GANGLIA_PORT
name|int
name|DEFAULT_GANGLIA_PORT
init|=
literal|8649
decl_stmt|;
comment|/**    * Boolean to enable Logging metrics reporting    * {@value}    */
DECL|field|METRICS_LOGGING_ENABLED
name|String
name|METRICS_LOGGING_ENABLED
init|=
name|METRICS_PREFIX
operator|+
literal|"logging.enabled"
decl_stmt|;
comment|/**    * String name of log to log to    * {@value}    */
DECL|field|METRICS_LOGGING_LOG
name|String
name|METRICS_LOGGING_LOG
init|=
name|METRICS_PREFIX
operator|+
literal|"logging.log.name"
decl_stmt|;
comment|/**    * Default log name: {@value}    */
DECL|field|METRICS_DEFAULT_LOG
name|String
name|METRICS_DEFAULT_LOG
init|=
literal|"org.apache.slider.metrics.log"
decl_stmt|;
comment|/**    * Int log interval in seconds    * {@value}    */
DECL|field|METRICS_LOGGING_LOG_INTERVAL
name|String
name|METRICS_LOGGING_LOG_INTERVAL
init|=
name|METRICS_PREFIX
operator|+
literal|"logging.interval.minutes"
decl_stmt|;
comment|/**    * Default log interval: {@value}.    * This is a big interval as in a long lived service, log overflows are easy    * to create.     */
DECL|field|METRICS_DEFAULT_LOG_INTERVAL
name|int
name|METRICS_DEFAULT_LOG_INTERVAL
init|=
literal|60
decl_stmt|;
block|}
end_interface

end_unit

