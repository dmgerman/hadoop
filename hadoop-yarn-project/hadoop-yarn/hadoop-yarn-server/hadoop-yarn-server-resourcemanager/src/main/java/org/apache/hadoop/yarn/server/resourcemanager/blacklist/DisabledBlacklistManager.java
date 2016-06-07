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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * A {@link BlacklistManager} that returns no blacklists.  */
end_comment

begin_class
DECL|class|DisabledBlacklistManager
specifier|public
class|class
name|DisabledBlacklistManager
implements|implements
name|BlacklistManager
block|{
DECL|field|EMPTY_LIST
specifier|private
specifier|static
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|EMPTY_LIST
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|noBlacklist
specifier|private
name|ResourceBlacklistRequest
name|noBlacklist
init|=
name|ResourceBlacklistRequest
operator|.
name|newInstance
argument_list|(
name|EMPTY_LIST
argument_list|,
name|EMPTY_LIST
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|addNode (String node)
specifier|public
name|void
name|addNode
parameter_list|(
name|String
name|node
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getBlacklistUpdates ()
specifier|public
name|ResourceBlacklistRequest
name|getBlacklistUpdates
parameter_list|()
block|{
return|return
name|noBlacklist
return|;
block|}
annotation|@
name|Override
DECL|method|refreshNodeHostCount (int nodeHostCount)
specifier|public
name|void
name|refreshNodeHostCount
parameter_list|(
name|int
name|nodeHostCount
parameter_list|)
block|{
comment|// Do nothing
block|}
block|}
end_class

end_unit

