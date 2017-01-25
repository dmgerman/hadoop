begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * This is the JMX management interface for NameNode status information.  * End users shouldn't be implementing these interfaces, and instead  * access this information through the JMX APIs. *  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|NameNodeStatusMXBean
specifier|public
interface|interface
name|NameNodeStatusMXBean
block|{
comment|/**    * Gets the NameNode role.    *    * @return the NameNode role.    */
DECL|method|getNNRole ()
specifier|public
name|String
name|getNNRole
parameter_list|()
function_decl|;
comment|/**    * Gets the NameNode state.    *    * @return the NameNode state.    */
DECL|method|getState ()
specifier|public
name|String
name|getState
parameter_list|()
function_decl|;
comment|/**    * Gets the host and port colon separated.    *    * @return host and port colon separated.    */
DECL|method|getHostAndPort ()
specifier|public
name|String
name|getHostAndPort
parameter_list|()
function_decl|;
comment|/**    * Gets if security is enabled.    *    * @return true, if security is enabled.    */
DECL|method|isSecurityEnabled ()
specifier|public
name|boolean
name|isSecurityEnabled
parameter_list|()
function_decl|;
comment|/**    * Gets the most recent HA transition time in milliseconds from the epoch.    *    * @return the most recent HA transition time in milliseconds from the epoch.    */
DECL|method|getLastHATransitionTime ()
specifier|public
name|long
name|getLastHATransitionTime
parameter_list|()
function_decl|;
comment|/**    * Gets number of bytes in blocks with future generation stamps.    * @return number of bytes that can be deleted if exited from safe mode.    */
DECL|method|getBytesWithFutureGenerationStamps ()
name|long
name|getBytesWithFutureGenerationStamps
parameter_list|()
function_decl|;
comment|/**    * Retrieves information about slow DataNodes, if the feature is    * enabled. The report is in a JSON format.    */
DECL|method|getSlowPeersReport ()
name|String
name|getSlowPeersReport
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

