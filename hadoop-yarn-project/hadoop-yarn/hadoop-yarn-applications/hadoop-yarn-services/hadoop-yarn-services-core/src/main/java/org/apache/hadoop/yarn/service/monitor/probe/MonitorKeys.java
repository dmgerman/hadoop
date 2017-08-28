begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.monitor.probe
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
name|monitor
operator|.
name|probe
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
comment|/**    * Port probing key : port to attempt to create a TCP connection to {@value}.    */
DECL|field|PORT_PROBE_PORT
name|String
name|PORT_PROBE_PORT
init|=
literal|"port"
decl_stmt|;
comment|/**    * Port probing key : timeout for the the connection attempt {@value}.    */
DECL|field|PORT_PROBE_CONNECT_TIMEOUT
name|String
name|PORT_PROBE_CONNECT_TIMEOUT
init|=
literal|"timeout"
decl_stmt|;
comment|/**    * Port probing default : timeout for the connection attempt {@value}.    */
DECL|field|PORT_PROBE_CONNECT_TIMEOUT_DEFAULT
name|int
name|PORT_PROBE_CONNECT_TIMEOUT_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/**    * Web probing key : URL {@value}.    */
DECL|field|WEB_PROBE_URL
name|String
name|WEB_PROBE_URL
init|=
literal|"url"
decl_stmt|;
comment|/**    * Web probing key : min success code {@value}.    */
DECL|field|WEB_PROBE_MIN_SUCCESS
name|String
name|WEB_PROBE_MIN_SUCCESS
init|=
literal|"min.success"
decl_stmt|;
comment|/**    * Web probing key : max success code {@value}.    */
DECL|field|WEB_PROBE_MAX_SUCCESS
name|String
name|WEB_PROBE_MAX_SUCCESS
init|=
literal|"max.success"
decl_stmt|;
comment|/**    * Web probing default : min successful response code {@value}.    */
DECL|field|WEB_PROBE_MIN_SUCCESS_DEFAULT
name|int
name|WEB_PROBE_MIN_SUCCESS_DEFAULT
init|=
literal|200
decl_stmt|;
comment|/**    * Web probing default : max successful response code {@value}.    */
DECL|field|WEB_PROBE_MAX_SUCCESS_DEFAULT
name|int
name|WEB_PROBE_MAX_SUCCESS_DEFAULT
init|=
literal|299
decl_stmt|;
comment|/**    * Web probing key : timeout for the connection attempt {@value}    */
DECL|field|WEB_PROBE_CONNECT_TIMEOUT
name|String
name|WEB_PROBE_CONNECT_TIMEOUT
init|=
literal|"timeout"
decl_stmt|;
comment|/**    * Port probing default : timeout for the connection attempt {@value}.    */
DECL|field|WEB_PROBE_CONNECT_TIMEOUT_DEFAULT
name|int
name|WEB_PROBE_CONNECT_TIMEOUT_DEFAULT
init|=
literal|1000
decl_stmt|;
block|}
end_interface

end_unit

