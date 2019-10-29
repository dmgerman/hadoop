begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
package|;
end_package

begin_comment
comment|/**  * Interface for RMInfo class.  */
end_comment

begin_interface
DECL|interface|RMInfoMXBean
specifier|public
interface|interface
name|RMInfoMXBean
block|{
comment|/**    * Gets the ResourceManager state.    * @return the ResourceManager state.    */
DECL|method|getState ()
name|String
name|getState
parameter_list|()
function_decl|;
comment|/**    * Gets the host and port colon separated.    * @return host and port colon separated.    */
DECL|method|getHostAndPort ()
name|String
name|getHostAndPort
parameter_list|()
function_decl|;
comment|/**    * Gets if security is enabled.    * @return true, if security is enabled.    */
DECL|method|isSecurityEnabled ()
name|boolean
name|isSecurityEnabled
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

