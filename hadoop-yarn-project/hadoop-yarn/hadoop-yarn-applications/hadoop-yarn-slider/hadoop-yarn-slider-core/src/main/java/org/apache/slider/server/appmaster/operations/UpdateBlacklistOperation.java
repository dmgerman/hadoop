begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.operations
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
name|operations
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Update blacklisted resources for the application.  */
end_comment

begin_class
DECL|class|UpdateBlacklistOperation
specifier|public
class|class
name|UpdateBlacklistOperation
extends|extends
name|AbstractRMOperation
block|{
DECL|field|blacklistAdditions
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|blacklistAdditions
decl_stmt|;
DECL|field|blacklistRemovals
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|blacklistRemovals
decl_stmt|;
DECL|method|UpdateBlacklistOperation (List<String> blacklistAdditions, List<String> blacklistRemovals)
specifier|public
name|UpdateBlacklistOperation
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|blacklistAdditions
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|blacklistRemovals
parameter_list|)
block|{
name|this
operator|.
name|blacklistAdditions
operator|=
name|blacklistAdditions
expr_stmt|;
name|this
operator|.
name|blacklistRemovals
operator|=
name|blacklistRemovals
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute (RMOperationHandlerActions handler)
specifier|public
name|void
name|execute
parameter_list|(
name|RMOperationHandlerActions
name|handler
parameter_list|)
block|{
name|handler
operator|.
name|updateBlacklist
argument_list|(
name|blacklistAdditions
argument_list|,
name|blacklistRemovals
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"blacklist additions: "
operator|+
name|blacklistAdditions
operator|+
literal|", blacklist removals: "
operator|+
name|blacklistRemovals
return|;
block|}
block|}
end_class

end_unit

