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
name|security
operator|.
name|UserGroupInformation
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
name|QueueACL
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
name|QueueInfo
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
name|security
operator|.
name|YarnAuthorizationProvider
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
name|ConfigurationMutationACLPolicy
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
name|scheduler
operator|.
name|MutableConfScheduler
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
name|scheduler
operator|.
name|Queue
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
name|webapp
operator|.
name|dao
operator|.
name|QueueConfigInfo
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
name|webapp
operator|.
name|dao
operator|.
name|SchedConfUpdateInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A configuration mutation ACL policy which checks that user has admin  * privileges on all queues they are changing.  */
end_comment

begin_class
DECL|class|QueueAdminConfigurationMutationACLPolicy
specifier|public
class|class
name|QueueAdminConfigurationMutationACLPolicy
implements|implements
name|ConfigurationMutationACLPolicy
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|authorizer
specifier|private
name|YarnAuthorizationProvider
name|authorizer
decl_stmt|;
annotation|@
name|Override
DECL|method|init (Configuration config, RMContext context)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|RMContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|authorizer
operator|=
name|YarnAuthorizationProvider
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isMutationAllowed (UserGroupInformation user, SchedConfUpdateInfo confUpdate)
specifier|public
name|boolean
name|isMutationAllowed
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|SchedConfUpdateInfo
name|confUpdate
parameter_list|)
block|{
comment|// If there are global config changes, check if user is admin.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|globalParams
init|=
name|confUpdate
operator|.
name|getGlobalParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|globalParams
operator|!=
literal|null
operator|&&
name|globalParams
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|authorizer
operator|.
name|isAdmin
argument_list|(
name|user
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// Check if user is admin of all modified queues.
name|Set
argument_list|<
name|String
argument_list|>
name|queues
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|QueueConfigInfo
name|addQueueInfo
range|:
name|confUpdate
operator|.
name|getAddQueueInfo
argument_list|()
control|)
block|{
name|queues
operator|.
name|add
argument_list|(
name|addQueueInfo
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|removeQueue
range|:
name|confUpdate
operator|.
name|getRemoveQueueInfo
argument_list|()
control|)
block|{
name|queues
operator|.
name|add
argument_list|(
name|removeQueue
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|QueueConfigInfo
name|updateQueueInfo
range|:
name|confUpdate
operator|.
name|getUpdateQueueInfo
argument_list|()
control|)
block|{
name|queues
operator|.
name|add
argument_list|(
name|updateQueueInfo
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|queuePath
range|:
name|queues
control|)
block|{
name|String
name|queueName
init|=
name|queuePath
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|!=
operator|-
literal|1
condition|?
name|queuePath
operator|.
name|substring
argument_list|(
name|queuePath
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
else|:
name|queuePath
decl_stmt|;
name|QueueInfo
name|queueInfo
init|=
literal|null
decl_stmt|;
try|try
block|{
name|queueInfo
operator|=
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getQueueInfo
argument_list|(
name|queueName
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Queue is not found, do nothing.
block|}
name|String
name|parentPath
init|=
name|queuePath
decl_stmt|;
while|while
condition|(
name|queueInfo
operator|==
literal|null
condition|)
block|{
comment|// We are adding a queue (whose parent we are possibly also adding).
comment|// Check ACL of lowest parent queue which already exists.
name|parentPath
operator|=
name|parentPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|parentPath
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|parentName
init|=
name|parentPath
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|!=
operator|-
literal|1
condition|?
name|parentPath
operator|.
name|substring
argument_list|(
name|parentPath
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
else|:
name|parentPath
decl_stmt|;
try|try
block|{
name|queueInfo
operator|=
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getQueueInfo
argument_list|(
name|parentName
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Queue is not found, do nothing.
block|}
block|}
name|Queue
name|queue
init|=
operator|(
operator|(
name|MutableConfScheduler
operator|)
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|)
operator|.
name|getQueue
argument_list|(
name|queueInfo
operator|.
name|getQueueName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|!=
literal|null
operator|&&
operator|!
name|queue
operator|.
name|hasAccess
argument_list|(
name|QueueACL
operator|.
name|ADMINISTER_QUEUE
argument_list|,
name|user
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

