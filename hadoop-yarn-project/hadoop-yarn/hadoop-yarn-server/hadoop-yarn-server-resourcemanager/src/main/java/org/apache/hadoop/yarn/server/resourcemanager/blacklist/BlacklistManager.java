begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.blacklist
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
operator|.
name|blacklist
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
operator|.
name|Private
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ResourceBlacklistRequest
import|;
end_import

begin_comment
comment|/**  * Tracks blacklists based on failures reported on nodes.  */
end_comment

begin_interface
annotation|@
name|Private
DECL|interface|BlacklistManager
specifier|public
interface|interface
name|BlacklistManager
block|{
comment|/**    * Report failure of a container on node.    * @param node that has a container failure    */
DECL|method|addNode (String node)
name|void
name|addNode
parameter_list|(
name|String
name|node
parameter_list|)
function_decl|;
comment|/**    * Get {@link ResourceBlacklistRequest} that indicate which nodes should be    * added or to removed from the blacklist.    * @return {@link ResourceBlacklistRequest}    */
DECL|method|getBlacklistUpdates ()
name|ResourceBlacklistRequest
name|getBlacklistUpdates
parameter_list|()
function_decl|;
comment|/**    * Refresh the number of NodeManagers available for scheduling.    * @param nodeHostCount is the number of node hosts.    */
DECL|method|refreshNodeHostCount (int nodeHostCount)
name|void
name|refreshNodeHostCount
parameter_list|(
name|int
name|nodeHostCount
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

