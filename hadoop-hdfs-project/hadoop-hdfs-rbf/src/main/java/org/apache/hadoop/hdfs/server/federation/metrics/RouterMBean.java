begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.metrics
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
name|federation
operator|.
name|metrics
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
comment|/**  * JMX interface for the router specific metrics.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|RouterMBean
specifier|public
interface|interface
name|RouterMBean
block|{
comment|/**    * When the router started.    * @return Date as a string the router started.    */
DECL|method|getRouterStarted ()
name|String
name|getRouterStarted
parameter_list|()
function_decl|;
comment|/**    * Get the version of the router.    * @return Version of the router.    */
DECL|method|getVersion ()
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Get the compilation date of the router.    * @return Compilation date of the router.    */
DECL|method|getCompiledDate ()
name|String
name|getCompiledDate
parameter_list|()
function_decl|;
comment|/**    * Get the compilation info of the router.    * @return Compilation info of the router.    */
DECL|method|getCompileInfo ()
name|String
name|getCompileInfo
parameter_list|()
function_decl|;
comment|/**    * Get the host and port of the router.    * @return Host and port of the router.    */
DECL|method|getHostAndPort ()
name|String
name|getHostAndPort
parameter_list|()
function_decl|;
comment|/**    * Get the identifier of the router.    * @return Identifier of the router.    */
DECL|method|getRouterId ()
name|String
name|getRouterId
parameter_list|()
function_decl|;
comment|/**    * Get the current state of the router.    *    * @return String label for the current router state.    */
DECL|method|getRouterStatus ()
name|String
name|getRouterStatus
parameter_list|()
function_decl|;
comment|/**    * Gets the cluster ids of the namenodes.    * @return the cluster ids of the namenodes.    */
DECL|method|getClusterId ()
name|String
name|getClusterId
parameter_list|()
function_decl|;
comment|/**    * Gets the block pool ids of the namenodes.    * @return the block pool ids of the namenodes.    */
DECL|method|getBlockPoolId ()
name|String
name|getBlockPoolId
parameter_list|()
function_decl|;
comment|/**    * Get the current number of delegation tokens in memory.    * @return number of DTs    */
DECL|method|getCurrentTokensCount ()
name|long
name|getCurrentTokensCount
parameter_list|()
function_decl|;
comment|/**    * Gets the safemode status.    *    * @return the safemode status.    */
DECL|method|getSafemode ()
name|String
name|getSafemode
parameter_list|()
function_decl|;
comment|/**    * Gets if security is enabled.    *    * @return true, if security is enabled.    */
DECL|method|isSecurityEnabled ()
name|boolean
name|isSecurityEnabled
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

