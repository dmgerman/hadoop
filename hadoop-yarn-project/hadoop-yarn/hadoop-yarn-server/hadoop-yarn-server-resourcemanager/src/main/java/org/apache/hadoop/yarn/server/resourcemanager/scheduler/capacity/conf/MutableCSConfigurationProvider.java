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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|conf
operator|.
name|YarnConfiguration
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
name|ConfigurationMutationACLPolicyFactory
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
name|MutableConfigurationProvider
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
name|capacity
operator|.
name|CapacitySchedulerConfiguration
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
name|capacity
operator|.
name|conf
operator|.
name|YarnConfigurationStore
operator|.
name|LogMutation
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
comment|/**  * CS configuration provider which implements  * {@link MutableConfigurationProvider} for modifying capacity scheduler  * configuration.  */
end_comment

begin_class
DECL|class|MutableCSConfigurationProvider
specifier|public
class|class
name|MutableCSConfigurationProvider
implements|implements
name|CSConfigurationProvider
implements|,
name|MutableConfigurationProvider
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MutableCSConfigurationProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|schedConf
specifier|private
name|Configuration
name|schedConf
decl_stmt|;
DECL|field|oldConf
specifier|private
name|Configuration
name|oldConf
decl_stmt|;
DECL|field|confStore
specifier|private
name|YarnConfigurationStore
name|confStore
decl_stmt|;
DECL|field|aclMutationPolicy
specifier|private
name|ConfigurationMutationACLPolicy
name|aclMutationPolicy
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|MutableCSConfigurationProvider (RMContext rmContext)
specifier|public
name|MutableCSConfigurationProvider
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration config)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|store
init|=
name|config
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|SCHEDULER_CONFIGURATION_STORE_CLASS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_CONFIGURATION_STORE
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|store
condition|)
block|{
case|case
name|YarnConfiguration
operator|.
name|MEMORY_CONFIGURATION_STORE
case|:
name|this
operator|.
name|confStore
operator|=
operator|new
name|InMemoryConfigurationStore
argument_list|()
expr_stmt|;
break|break;
case|case
name|YarnConfiguration
operator|.
name|LEVELDB_CONFIGURATION_STORE
case|:
name|this
operator|.
name|confStore
operator|=
operator|new
name|LeveldbConfigurationStore
argument_list|()
expr_stmt|;
break|break;
case|case
name|YarnConfiguration
operator|.
name|ZK_CONFIGURATION_STORE
case|:
name|this
operator|.
name|confStore
operator|=
operator|new
name|ZKConfigurationStore
argument_list|()
expr_stmt|;
break|break;
default|default:
name|this
operator|.
name|confStore
operator|=
name|YarnConfigurationStoreFactory
operator|.
name|getStore
argument_list|(
name|config
argument_list|)
expr_stmt|;
break|break;
block|}
name|Configuration
name|initialSchedConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|initialSchedConf
operator|.
name|addResource
argument_list|(
name|YarnConfiguration
operator|.
name|CS_CONFIGURATION_FILE
argument_list|)
expr_stmt|;
name|this
operator|.
name|schedConf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// We need to explicitly set the key-values in schedConf, otherwise
comment|// these configuration keys cannot be deleted when
comment|// configuration is reloaded.
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
name|initialSchedConf
control|)
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
try|try
block|{
name|confStore
operator|.
name|initialize
argument_list|(
name|config
argument_list|,
name|schedConf
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// After initializing confStore, the store may already have an existing
comment|// configuration. Use this one.
name|schedConf
operator|=
name|confStore
operator|.
name|retrieve
argument_list|()
expr_stmt|;
name|this
operator|.
name|aclMutationPolicy
operator|=
name|ConfigurationMutationACLPolicyFactory
operator|.
name|getPolicy
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|aclMutationPolicy
operator|.
name|init
argument_list|(
name|config
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getConfStore ()
specifier|public
name|YarnConfigurationStore
name|getConfStore
parameter_list|()
block|{
return|return
name|confStore
return|;
block|}
annotation|@
name|Override
DECL|method|loadConfiguration (Configuration configuration)
specifier|public
name|CapacitySchedulerConfiguration
name|loadConfiguration
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|loadedConf
init|=
operator|new
name|Configuration
argument_list|(
name|schedConf
argument_list|)
decl_stmt|;
name|loadedConf
operator|.
name|addResource
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
return|return
operator|new
name|CapacitySchedulerConfiguration
argument_list|(
name|loadedConf
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAclMutationPolicy ()
specifier|public
name|ConfigurationMutationACLPolicy
name|getAclMutationPolicy
parameter_list|()
block|{
return|return
name|aclMutationPolicy
return|;
block|}
annotation|@
name|Override
DECL|method|logAndApplyMutation (UserGroupInformation user, SchedConfUpdateInfo confUpdate)
specifier|public
name|void
name|logAndApplyMutation
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|SchedConfUpdateInfo
name|confUpdate
parameter_list|)
throws|throws
name|Exception
block|{
name|oldConf
operator|=
operator|new
name|Configuration
argument_list|(
name|schedConf
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|kvUpdate
init|=
name|constructKeyValueConfUpdate
argument_list|(
name|confUpdate
argument_list|)
decl_stmt|;
name|LogMutation
name|log
init|=
operator|new
name|LogMutation
argument_list|(
name|kvUpdate
argument_list|,
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|)
decl_stmt|;
name|confStore
operator|.
name|logMutation
argument_list|(
name|log
argument_list|)
expr_stmt|;
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
name|kvUpdate
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
annotation|@
name|Override
DECL|method|confirmPendingMutation (boolean isValid)
specifier|public
name|void
name|confirmPendingMutation
parameter_list|(
name|boolean
name|isValid
parameter_list|)
throws|throws
name|Exception
block|{
name|confStore
operator|.
name|confirmMutation
argument_list|(
name|isValid
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isValid
condition|)
block|{
name|schedConf
operator|=
name|oldConf
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|reloadConfigurationFromStore ()
specifier|public
name|void
name|reloadConfigurationFromStore
parameter_list|()
throws|throws
name|Exception
block|{
name|schedConf
operator|=
name|confStore
operator|.
name|retrieve
argument_list|()
expr_stmt|;
block|}
DECL|method|getSiblingQueues (String queuePath, Configuration conf)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getSiblingQueues
parameter_list|(
name|String
name|queuePath
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|parentQueue
init|=
name|queuePath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|queuePath
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|childQueuesKey
init|=
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
operator|+
name|parentQueue
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|DOT
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|QUEUES
decl_stmt|;
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|conf
operator|.
name|getStringCollection
argument_list|(
name|childQueuesKey
argument_list|)
argument_list|)
return|;
block|}
DECL|method|constructKeyValueConfUpdate ( SchedConfUpdateInfo mutationInfo)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|constructKeyValueConfUpdate
parameter_list|(
name|SchedConfUpdateInfo
name|mutationInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|CapacitySchedulerConfiguration
name|proposedConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|(
name|schedConf
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|confUpdate
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|queueToRemove
range|:
name|mutationInfo
operator|.
name|getRemoveQueueInfo
argument_list|()
control|)
block|{
name|removeQueue
argument_list|(
name|queueToRemove
argument_list|,
name|proposedConf
argument_list|,
name|confUpdate
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|QueueConfigInfo
name|addQueueInfo
range|:
name|mutationInfo
operator|.
name|getAddQueueInfo
argument_list|()
control|)
block|{
name|addQueue
argument_list|(
name|addQueueInfo
argument_list|,
name|proposedConf
argument_list|,
name|confUpdate
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|QueueConfigInfo
name|updateQueueInfo
range|:
name|mutationInfo
operator|.
name|getUpdateQueueInfo
argument_list|()
control|)
block|{
name|updateQueue
argument_list|(
name|updateQueueInfo
argument_list|,
name|proposedConf
argument_list|,
name|confUpdate
argument_list|)
expr_stmt|;
block|}
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
name|global
range|:
name|mutationInfo
operator|.
name|getGlobalParams
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|confUpdate
operator|.
name|put
argument_list|(
name|global
operator|.
name|getKey
argument_list|()
argument_list|,
name|global
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|confUpdate
return|;
block|}
DECL|method|removeQueue ( String queueToRemove, CapacitySchedulerConfiguration proposedConf, Map<String, String> confUpdate)
specifier|private
name|void
name|removeQueue
parameter_list|(
name|String
name|queueToRemove
parameter_list|,
name|CapacitySchedulerConfiguration
name|proposedConf
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|confUpdate
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|queueToRemove
operator|==
literal|null
condition|)
block|{
return|return;
block|}
else|else
block|{
name|String
name|queueName
init|=
name|queueToRemove
operator|.
name|substring
argument_list|(
name|queueToRemove
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|queueToRemove
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't remove queue "
operator|+
name|queueToRemove
argument_list|)
throw|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|siblingQueues
init|=
name|getSiblingQueues
argument_list|(
name|queueToRemove
argument_list|,
name|proposedConf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|siblingQueues
operator|.
name|contains
argument_list|(
name|queueName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Queue "
operator|+
name|queueToRemove
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|siblingQueues
operator|.
name|remove
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
name|String
name|parentQueuePath
init|=
name|queueToRemove
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|queueToRemove
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
decl_stmt|;
name|proposedConf
operator|.
name|setQueues
argument_list|(
name|parentQueuePath
argument_list|,
name|siblingQueues
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|queuesConfig
init|=
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
operator|+
name|parentQueuePath
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|DOT
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|QUEUES
decl_stmt|;
if|if
condition|(
name|siblingQueues
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|confUpdate
operator|.
name|put
argument_list|(
name|queuesConfig
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|confUpdate
operator|.
name|put
argument_list|(
name|queuesConfig
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|join
argument_list|(
name|siblingQueues
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|confRemove
range|:
name|proposedConf
operator|.
name|getValByRegex
argument_list|(
literal|".*"
operator|+
name|queueToRemove
operator|.
name|replaceAll
argument_list|(
literal|"\\."
argument_list|,
literal|"\\."
argument_list|)
operator|+
literal|"\\..*"
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|proposedConf
operator|.
name|unset
argument_list|(
name|confRemove
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|confUpdate
operator|.
name|put
argument_list|(
name|confRemove
operator|.
name|getKey
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|addQueue ( QueueConfigInfo addInfo, CapacitySchedulerConfiguration proposedConf, Map<String, String> confUpdate)
specifier|private
name|void
name|addQueue
parameter_list|(
name|QueueConfigInfo
name|addInfo
parameter_list|,
name|CapacitySchedulerConfiguration
name|proposedConf
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|confUpdate
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|addInfo
operator|==
literal|null
condition|)
block|{
return|return;
block|}
else|else
block|{
name|String
name|queuePath
init|=
name|addInfo
operator|.
name|getQueue
argument_list|()
decl_stmt|;
name|String
name|queueName
init|=
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
decl_stmt|;
if|if
condition|(
name|queuePath
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't add invalid queue "
operator|+
name|queuePath
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|getSiblingQueues
argument_list|(
name|queuePath
argument_list|,
name|proposedConf
argument_list|)
operator|.
name|contains
argument_list|(
name|queueName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't add existing queue "
operator|+
name|queuePath
argument_list|)
throw|;
block|}
name|String
name|parentQueue
init|=
name|queuePath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|queuePath
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|siblings
init|=
name|proposedConf
operator|.
name|getQueues
argument_list|(
name|parentQueue
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|siblingQueues
init|=
name|siblings
operator|==
literal|null
condition|?
operator|new
name|ArrayList
argument_list|<>
argument_list|()
else|:
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
expr|<
name|String
operator|>
name|asList
argument_list|(
name|siblings
argument_list|)
argument_list|)
decl_stmt|;
name|siblingQueues
operator|.
name|add
argument_list|(
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
argument_list|)
expr_stmt|;
name|proposedConf
operator|.
name|setQueues
argument_list|(
name|parentQueue
argument_list|,
name|siblingQueues
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|confUpdate
operator|.
name|put
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
operator|+
name|parentQueue
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|DOT
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|QUEUES
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|join
argument_list|(
name|siblingQueues
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|keyPrefix
init|=
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
operator|+
name|queuePath
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|DOT
decl_stmt|;
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
name|addInfo
operator|.
name|getParams
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
name|proposedConf
operator|.
name|unset
argument_list|(
name|keyPrefix
operator|+
name|kv
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|proposedConf
operator|.
name|set
argument_list|(
name|keyPrefix
operator|+
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
name|confUpdate
operator|.
name|put
argument_list|(
name|keyPrefix
operator|+
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
DECL|method|updateQueue (QueueConfigInfo updateInfo, CapacitySchedulerConfiguration proposedConf, Map<String, String> confUpdate)
specifier|private
name|void
name|updateQueue
parameter_list|(
name|QueueConfigInfo
name|updateInfo
parameter_list|,
name|CapacitySchedulerConfiguration
name|proposedConf
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|confUpdate
parameter_list|)
block|{
if|if
condition|(
name|updateInfo
operator|==
literal|null
condition|)
block|{
return|return;
block|}
else|else
block|{
name|String
name|queuePath
init|=
name|updateInfo
operator|.
name|getQueue
argument_list|()
decl_stmt|;
name|String
name|keyPrefix
init|=
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
operator|+
name|queuePath
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|DOT
decl_stmt|;
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
name|updateInfo
operator|.
name|getParams
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
name|proposedConf
operator|.
name|unset
argument_list|(
name|keyPrefix
operator|+
name|kv
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|proposedConf
operator|.
name|set
argument_list|(
name|keyPrefix
operator|+
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
name|confUpdate
operator|.
name|put
argument_list|(
name|keyPrefix
operator|+
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
block|}
end_class

end_unit

