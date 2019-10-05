begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.conf
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
name|scheduler
operator|.
name|capacity
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
name|conf
operator|.
name|Configuration
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
name|server
operator|.
name|records
operator|.
name|Version
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A default implementation of {@link YarnConfigurationStore}. Doesn't offer  * persistent configuration storage, just stores the configuration in memory.  */
end_comment

begin_class
DECL|class|InMemoryConfigurationStore
specifier|public
class|class
name|InMemoryConfigurationStore
extends|extends
name|YarnConfigurationStore
block|{
DECL|field|schedConf
specifier|private
name|Configuration
name|schedConf
decl_stmt|;
DECL|field|pendingMutation
specifier|private
name|LogMutation
name|pendingMutation
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (Configuration conf, Configuration schedConf, RMContext rmContext)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Configuration
name|schedConf
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|schedConf
operator|=
name|schedConf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|logMutation (LogMutation logMutation)
specifier|public
name|void
name|logMutation
parameter_list|(
name|LogMutation
name|logMutation
parameter_list|)
block|{
name|pendingMutation
operator|=
name|logMutation
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|confirmMutation (boolean isValid)
specifier|public
name|void
name|confirmMutation
parameter_list|(
name|boolean
name|isValid
parameter_list|)
block|{
if|if
condition|(
name|isValid
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|kv
range|:
name|pendingMutation
operator|.
name|getUpdates
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|kv
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
name|schedConf
operator|.
name|unset
argument_list|(
name|kv
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|schedConf
operator|.
name|set
argument_list|(
name|kv
operator|.
name|getKey
argument_list|()
argument_list|,
name|kv
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|pendingMutation
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|format ()
specifier|public
name|void
name|format
parameter_list|()
block|{
name|this
operator|.
name|schedConf
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|retrieve ()
specifier|public
specifier|synchronized
name|Configuration
name|retrieve
parameter_list|()
block|{
return|return
name|schedConf
return|;
block|}
annotation|@
name|Override
DECL|method|getConfirmedConfHistory (long fromId)
specifier|public
name|List
argument_list|<
name|LogMutation
argument_list|>
name|getConfirmedConfHistory
parameter_list|(
name|long
name|fromId
parameter_list|)
block|{
comment|// Unimplemented.
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getConfStoreVersion ()
specifier|public
name|Version
name|getConfStoreVersion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Does nothing.
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|storeVersion ()
specifier|public
name|void
name|storeVersion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Does nothing.
block|}
annotation|@
name|Override
DECL|method|getCurrentVersion ()
specifier|public
name|Version
name|getCurrentVersion
parameter_list|()
block|{
comment|// Does nothing.
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|checkVersion ()
specifier|public
name|void
name|checkVersion
parameter_list|()
block|{
comment|// Does nothing. (Version is always compatible since it's in memory)
block|}
block|}
end_class

end_unit

