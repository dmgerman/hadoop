begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.placement
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
name|placement
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
name|AutoCreatedLeafQueue
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
name|CSQueue
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
name|CapacitySchedulerQueueManager
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
name|LeafQueue
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
name|ManagedParentQueue
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
import|import static
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
operator|.
name|DOT
import|;
end_import

begin_comment
comment|/**  * Utility class for Capacity Scheduler queue PlacementRules.  */
end_comment

begin_class
DECL|class|QueuePlacementRuleUtils
specifier|public
specifier|final
class|class
name|QueuePlacementRuleUtils
block|{
DECL|field|CURRENT_USER_MAPPING
specifier|public
specifier|static
specifier|final
name|String
name|CURRENT_USER_MAPPING
init|=
literal|"%user"
decl_stmt|;
DECL|field|PRIMARY_GROUP_MAPPING
specifier|public
specifier|static
specifier|final
name|String
name|PRIMARY_GROUP_MAPPING
init|=
literal|"%primary_group"
decl_stmt|;
DECL|field|SECONDARY_GROUP_MAPPING
specifier|public
specifier|static
specifier|final
name|String
name|SECONDARY_GROUP_MAPPING
init|=
literal|"%secondary_group"
decl_stmt|;
DECL|method|QueuePlacementRuleUtils ()
specifier|private
name|QueuePlacementRuleUtils
parameter_list|()
block|{   }
DECL|method|validateQueueMappingUnderParentQueue (CSQueue parentQueue, String parentQueueName, String leafQueueName)
specifier|private
specifier|static
name|void
name|validateQueueMappingUnderParentQueue
parameter_list|(
name|CSQueue
name|parentQueue
parameter_list|,
name|String
name|parentQueueName
parameter_list|,
name|String
name|leafQueueName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parentQueue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"mapping contains invalid or non-leaf queue ["
operator|+
name|leafQueueName
operator|+
literal|"] and invalid parent queue ["
operator|+
name|parentQueueName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|parentQueue
operator|instanceof
name|ManagedParentQueue
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"mapping contains leaf queue ["
operator|+
name|leafQueueName
operator|+
literal|"] and invalid parent queue which "
operator|+
literal|"does not have auto creation of leaf queues enabled ["
operator|+
name|parentQueueName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|parentQueue
operator|.
name|getQueueName
argument_list|()
operator|.
name|equals
argument_list|(
name|parentQueueName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"mapping contains invalid or non-leaf queue ["
operator|+
name|leafQueueName
operator|+
literal|"] and invalid parent queue "
operator|+
literal|"which does not match existing leaf queue's parent : ["
operator|+
name|parentQueueName
operator|+
literal|"] does not match [ "
operator|+
name|parentQueue
operator|.
name|getQueueName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|validateAndGetAutoCreatedQueueMapping ( CapacitySchedulerQueueManager queueManager, QueueMappingEntity mapping, QueuePath queuePath)
specifier|public
specifier|static
name|QueueMappingEntity
name|validateAndGetAutoCreatedQueueMapping
parameter_list|(
name|CapacitySchedulerQueueManager
name|queueManager
parameter_list|,
name|QueueMappingEntity
name|mapping
parameter_list|,
name|QueuePath
name|queuePath
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|queuePath
operator|.
name|hasParentQueue
argument_list|()
condition|)
block|{
comment|//if parent queue is specified,
comment|// then it should exist and be an instance of ManagedParentQueue
name|validateQueueMappingUnderParentQueue
argument_list|(
name|queueManager
operator|.
name|getQueue
argument_list|(
name|queuePath
operator|.
name|getParentQueue
argument_list|()
argument_list|)
argument_list|,
name|queuePath
operator|.
name|getParentQueue
argument_list|()
argument_list|,
name|queuePath
operator|.
name|getLeafQueue
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|QueueMappingEntity
argument_list|(
name|mapping
operator|.
name|getSource
argument_list|()
argument_list|,
name|queuePath
operator|.
name|getLeafQueue
argument_list|()
argument_list|,
name|queuePath
operator|.
name|getParentQueue
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|validateAndGetQueueMapping ( CapacitySchedulerQueueManager queueManager, CSQueue queue, QueueMappingEntity mapping, QueuePath queuePath)
specifier|public
specifier|static
name|QueueMappingEntity
name|validateAndGetQueueMapping
parameter_list|(
name|CapacitySchedulerQueueManager
name|queueManager
parameter_list|,
name|CSQueue
name|queue
parameter_list|,
name|QueueMappingEntity
name|mapping
parameter_list|,
name|QueuePath
name|queuePath
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|queue
operator|instanceof
name|LeafQueue
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"mapping contains invalid or non-leaf queue : "
operator|+
name|mapping
operator|.
name|getQueue
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|queue
operator|instanceof
name|AutoCreatedLeafQueue
operator|&&
name|queue
operator|.
name|getParent
argument_list|()
operator|instanceof
name|ManagedParentQueue
condition|)
block|{
name|QueueMappingEntity
name|newMapping
init|=
name|validateAndGetAutoCreatedQueueMapping
argument_list|(
name|queueManager
argument_list|,
name|mapping
argument_list|,
name|queuePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|newMapping
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"mapping contains invalid or non-leaf queue "
operator|+
name|mapping
operator|.
name|getQueue
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|newMapping
return|;
block|}
return|return
name|mapping
return|;
block|}
DECL|method|isStaticQueueMapping (QueueMappingEntity mapping)
specifier|public
specifier|static
name|boolean
name|isStaticQueueMapping
parameter_list|(
name|QueueMappingEntity
name|mapping
parameter_list|)
block|{
return|return
operator|!
name|mapping
operator|.
name|getQueue
argument_list|()
operator|.
name|contains
argument_list|(
name|CURRENT_USER_MAPPING
argument_list|)
operator|&&
operator|!
name|mapping
operator|.
name|getQueue
argument_list|()
operator|.
name|contains
argument_list|(
name|PRIMARY_GROUP_MAPPING
argument_list|)
operator|&&
operator|!
name|mapping
operator|.
name|getQueue
argument_list|()
operator|.
name|contains
argument_list|(
name|SECONDARY_GROUP_MAPPING
argument_list|)
return|;
block|}
DECL|method|extractQueuePath (String queueName)
specifier|public
specifier|static
name|QueuePath
name|extractQueuePath
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|parentQueueNameEndIndex
init|=
name|queueName
operator|.
name|lastIndexOf
argument_list|(
name|DOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentQueueNameEndIndex
operator|>
operator|-
literal|1
condition|)
block|{
specifier|final
name|String
name|parentQueue
init|=
name|queueName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|parentQueueNameEndIndex
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
specifier|final
name|String
name|leafQueue
init|=
name|queueName
operator|.
name|substring
argument_list|(
name|parentQueueNameEndIndex
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
return|return
operator|new
name|QueuePath
argument_list|(
name|parentQueue
argument_list|,
name|leafQueue
argument_list|)
return|;
block|}
return|return
operator|new
name|QueuePath
argument_list|(
name|queueName
argument_list|)
return|;
block|}
DECL|method|getPlacementContext ( QueueMappingEntity mapping)
specifier|public
specifier|static
name|ApplicationPlacementContext
name|getPlacementContext
parameter_list|(
name|QueueMappingEntity
name|mapping
parameter_list|)
block|{
return|return
name|getPlacementContext
argument_list|(
name|mapping
argument_list|,
name|mapping
operator|.
name|getQueue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getPlacementContext ( QueueMappingEntity mapping, String leafQueueName)
specifier|public
specifier|static
name|ApplicationPlacementContext
name|getPlacementContext
parameter_list|(
name|QueueMappingEntity
name|mapping
parameter_list|,
name|String
name|leafQueueName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|mapping
operator|.
name|getParentQueue
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|ApplicationPlacementContext
argument_list|(
name|leafQueueName
argument_list|,
name|mapping
operator|.
name|getParentQueue
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ApplicationPlacementContext
argument_list|(
name|leafQueueName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

