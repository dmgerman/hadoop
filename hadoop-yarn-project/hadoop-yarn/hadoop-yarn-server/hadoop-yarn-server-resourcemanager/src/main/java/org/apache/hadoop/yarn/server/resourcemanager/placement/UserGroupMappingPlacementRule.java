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
name|List
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
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|security
operator|.
name|Groups
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
name|ApplicationId
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
name|ApplicationSubmissionContext
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
name|exceptions
operator|.
name|YarnException
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
name|placement
operator|.
name|UserGroupMappingPlacementRule
operator|.
name|QueueMapping
operator|.
name|MappingType
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
name|annotations
operator|.
name|VisibleForTesting
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
name|ResourceScheduler
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
name|CapacityScheduler
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
name|CapacitySchedulerContext
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

begin_class
DECL|class|UserGroupMappingPlacementRule
specifier|public
class|class
name|UserGroupMappingPlacementRule
extends|extends
name|PlacementRule
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UserGroupMappingPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|field|overrideWithQueueMappings
specifier|private
name|boolean
name|overrideWithQueueMappings
init|=
literal|false
decl_stmt|;
DECL|field|mappings
specifier|private
name|List
argument_list|<
name|QueueMapping
argument_list|>
name|mappings
init|=
literal|null
decl_stmt|;
DECL|field|groups
specifier|private
name|Groups
name|groups
decl_stmt|;
annotation|@
name|Private
DECL|class|QueueMapping
specifier|public
specifier|static
class|class
name|QueueMapping
block|{
DECL|enum|MappingType
specifier|public
enum|enum
name|MappingType
block|{
DECL|enumConstant|USER
DECL|enumConstant|GROUP
name|USER
argument_list|(
literal|"u"
argument_list|)
block|,
name|GROUP
argument_list|(
literal|"g"
argument_list|)
block|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|method|MappingType (String type)
specifier|private
name|MappingType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
empty_stmt|;
DECL|field|type
name|MappingType
name|type
decl_stmt|;
DECL|field|source
name|String
name|source
decl_stmt|;
DECL|field|queue
name|String
name|queue
decl_stmt|;
DECL|field|parentQueue
name|String
name|parentQueue
decl_stmt|;
DECL|field|DELIMITER
specifier|public
specifier|final
specifier|static
name|String
name|DELIMITER
init|=
literal|":"
decl_stmt|;
DECL|method|QueueMapping (MappingType type, String source, String queue)
specifier|public
name|QueueMapping
parameter_list|(
name|MappingType
name|type
parameter_list|,
name|String
name|source
parameter_list|,
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|parentQueue
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|QueueMapping (MappingType type, String source, String queue, String parentQueue)
specifier|public
name|QueueMapping
parameter_list|(
name|MappingType
name|type
parameter_list|,
name|String
name|source
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|parentQueue
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|parentQueue
operator|=
name|parentQueue
expr_stmt|;
block|}
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
DECL|method|getParentQueue ()
specifier|public
name|String
name|getParentQueue
parameter_list|()
block|{
return|return
name|parentQueue
return|;
block|}
DECL|method|hasParentQueue ()
specifier|public
name|boolean
name|hasParentQueue
parameter_list|()
block|{
return|return
name|parentQueue
operator|!=
literal|null
return|;
block|}
DECL|method|getType ()
specifier|public
name|MappingType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getSource ()
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|QueueMapping
condition|)
block|{
name|QueueMapping
name|other
init|=
operator|(
name|QueueMapping
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|other
operator|.
name|type
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|&&
name|other
operator|.
name|source
operator|.
name|equals
argument_list|(
name|source
argument_list|)
operator|&&
name|other
operator|.
name|queue
operator|.
name|equals
argument_list|(
name|queue
argument_list|)
operator|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|type
operator|.
name|toString
argument_list|()
operator|+
name|DELIMITER
operator|+
name|source
operator|+
name|DELIMITER
operator|+
operator|(
name|parentQueue
operator|!=
literal|null
condition|?
name|parentQueue
operator|+
literal|"."
operator|+
name|queue
else|:
name|queue
operator|)
return|;
block|}
block|}
DECL|method|UserGroupMappingPlacementRule ()
specifier|public
name|UserGroupMappingPlacementRule
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|UserGroupMappingPlacementRule (boolean overrideWithQueueMappings, List<QueueMapping> newMappings, Groups groups)
specifier|public
name|UserGroupMappingPlacementRule
parameter_list|(
name|boolean
name|overrideWithQueueMappings
parameter_list|,
name|List
argument_list|<
name|QueueMapping
argument_list|>
name|newMappings
parameter_list|,
name|Groups
name|groups
parameter_list|)
block|{
name|this
operator|.
name|mappings
operator|=
name|newMappings
expr_stmt|;
name|this
operator|.
name|overrideWithQueueMappings
operator|=
name|overrideWithQueueMappings
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
block|}
DECL|method|getPlacementForUser (String user)
specifier|private
name|ApplicationPlacementContext
name|getPlacementForUser
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|QueueMapping
name|mapping
range|:
name|mappings
control|)
block|{
if|if
condition|(
name|mapping
operator|.
name|type
operator|==
name|MappingType
operator|.
name|USER
condition|)
block|{
if|if
condition|(
name|mapping
operator|.
name|source
operator|.
name|equals
argument_list|(
name|CURRENT_USER_MAPPING
argument_list|)
condition|)
block|{
if|if
condition|(
name|mapping
operator|.
name|queue
operator|.
name|equals
argument_list|(
name|CURRENT_USER_MAPPING
argument_list|)
condition|)
block|{
return|return
name|getPlacementContext
argument_list|(
name|mapping
argument_list|,
name|user
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|mapping
operator|.
name|queue
operator|.
name|equals
argument_list|(
name|PRIMARY_GROUP_MAPPING
argument_list|)
condition|)
block|{
return|return
name|getPlacementContext
argument_list|(
name|mapping
argument_list|,
name|groups
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getPlacementContext
argument_list|(
name|mapping
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|user
operator|.
name|equals
argument_list|(
name|mapping
operator|.
name|source
argument_list|)
condition|)
block|{
return|return
name|getPlacementContext
argument_list|(
name|mapping
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|mapping
operator|.
name|type
operator|==
name|MappingType
operator|.
name|GROUP
condition|)
block|{
for|for
control|(
name|String
name|userGroups
range|:
name|groups
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
control|)
block|{
if|if
condition|(
name|userGroups
operator|.
name|equals
argument_list|(
name|mapping
operator|.
name|source
argument_list|)
condition|)
block|{
if|if
condition|(
name|mapping
operator|.
name|queue
operator|.
name|equals
argument_list|(
name|CURRENT_USER_MAPPING
argument_list|)
condition|)
block|{
return|return
name|getPlacementContext
argument_list|(
name|mapping
argument_list|,
name|user
argument_list|)
return|;
block|}
return|return
name|getPlacementContext
argument_list|(
name|mapping
argument_list|)
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getPlacementForApp ( ApplicationSubmissionContext asc, String user)
specifier|public
name|ApplicationPlacementContext
name|getPlacementForApp
parameter_list|(
name|ApplicationSubmissionContext
name|asc
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|YarnException
block|{
name|String
name|queueName
init|=
name|asc
operator|.
name|getQueue
argument_list|()
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|asc
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
if|if
condition|(
name|mappings
operator|!=
literal|null
operator|&&
name|mappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|ApplicationPlacementContext
name|mappedQueue
init|=
name|getPlacementForUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappedQueue
operator|!=
literal|null
condition|)
block|{
comment|// We have a mapping, should we use it?
if|if
condition|(
name|queueName
operator|.
name|equals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_QUEUE_NAME
argument_list|)
comment|//queueName will be same as mapped queue name in case of recovery
operator|||
name|queueName
operator|.
name|equals
argument_list|(
name|mappedQueue
operator|.
name|getQueue
argument_list|()
argument_list|)
operator|||
name|overrideWithQueueMappings
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Application "
operator|+
name|applicationId
operator|+
literal|" user "
operator|+
name|user
operator|+
literal|" mapping ["
operator|+
name|queueName
operator|+
literal|"] to ["
operator|+
name|mappedQueue
operator|+
literal|"] override "
operator|+
name|overrideWithQueueMappings
argument_list|)
expr_stmt|;
return|return
name|mappedQueue
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioex
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Failed to submit application "
operator|+
name|applicationId
operator|+
literal|" submitted by user "
operator|+
name|user
operator|+
literal|" reason: "
operator|+
name|ioex
operator|.
name|getMessage
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getPlacementContext ( QueueMapping mapping)
specifier|private
name|ApplicationPlacementContext
name|getPlacementContext
parameter_list|(
name|QueueMapping
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
DECL|method|getPlacementContext (QueueMapping mapping, String leafQueueName)
specifier|private
name|ApplicationPlacementContext
name|getPlacementContext
parameter_list|(
name|QueueMapping
name|mapping
parameter_list|,
name|String
name|leafQueueName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|mapping
operator|.
name|parentQueue
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
annotation|@
name|VisibleForTesting
annotation|@
name|Override
DECL|method|initialize (ResourceScheduler scheduler)
specifier|public
name|boolean
name|initialize
parameter_list|(
name|ResourceScheduler
name|scheduler
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|scheduler
operator|instanceof
name|CapacityScheduler
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"UserGroupMappingPlacementRule can be configured only for "
operator|+
literal|"CapacityScheduler"
argument_list|)
throw|;
block|}
name|CapacitySchedulerContext
name|schedulerContext
init|=
operator|(
name|CapacitySchedulerContext
operator|)
name|scheduler
decl_stmt|;
name|CapacitySchedulerConfiguration
name|conf
init|=
name|schedulerContext
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|boolean
name|overrideWithQueueMappings
init|=
name|conf
operator|.
name|getOverrideWithQueueMappings
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialized queue mappings, override: "
operator|+
name|overrideWithQueueMappings
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|QueueMapping
argument_list|>
name|queueMappings
init|=
name|conf
operator|.
name|getQueueMappings
argument_list|()
decl_stmt|;
comment|// Get new user/group mappings
name|List
argument_list|<
name|QueueMapping
argument_list|>
name|newMappings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CapacitySchedulerQueueManager
name|queueManager
init|=
name|schedulerContext
operator|.
name|getCapacitySchedulerQueueManager
argument_list|()
decl_stmt|;
comment|// check if mappings refer to valid queues
for|for
control|(
name|QueueMapping
name|mapping
range|:
name|queueMappings
control|)
block|{
name|QueuePath
name|queuePath
init|=
name|extractQueuePath
argument_list|(
name|mapping
operator|.
name|getQueue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isStaticQueueMapping
argument_list|(
name|mapping
argument_list|)
condition|)
block|{
comment|//Try getting queue by its leaf queue name
comment|// without splitting into parent/leaf queues
name|CSQueue
name|queue
init|=
name|queueManager
operator|.
name|getQueue
argument_list|(
name|mapping
operator|.
name|getQueue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ifQueueDoesNotExist
argument_list|(
name|queue
argument_list|)
condition|)
block|{
comment|//Try getting the queue by extracting leaf and parent queue names
comment|//Assuming its a potential auto created leaf queue
name|queue
operator|=
name|queueManager
operator|.
name|getQueue
argument_list|(
name|queuePath
operator|.
name|getLeafQueue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ifQueueDoesNotExist
argument_list|(
name|queue
argument_list|)
condition|)
block|{
comment|//if leaf queue does not exist,
comment|// this could be a potential auto created leaf queue
comment|//validate if parent queue is specified,
comment|// then it should exist and
comment|// be an instance of AutoCreateEnabledParentQueue
name|QueueMapping
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
name|newMappings
operator|.
name|add
argument_list|(
name|newMapping
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|QueueMapping
name|newMapping
init|=
name|validateAndGetQueueMapping
argument_list|(
name|queueManager
argument_list|,
name|queue
argument_list|,
name|mapping
argument_list|,
name|queuePath
argument_list|)
decl_stmt|;
name|newMappings
operator|.
name|add
argument_list|(
name|newMapping
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// if queue exists, validate
comment|//   if its an instance of leaf queue
comment|//   if its an instance of auto created leaf queue,
comment|// then extract parent queue name and update queue mapping
name|QueueMapping
name|newMapping
init|=
name|validateAndGetQueueMapping
argument_list|(
name|queueManager
argument_list|,
name|queue
argument_list|,
name|mapping
argument_list|,
name|queuePath
argument_list|)
decl_stmt|;
name|newMappings
operator|.
name|add
argument_list|(
name|newMapping
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//If it is a dynamic queue mapping,
comment|// we can safely assume leaf queue name does not have '.' in it
comment|// validate
comment|// if parent queue is specified, then
comment|//  parent queue exists and an instance of AutoCreateEnabledParentQueue
comment|//
name|QueueMapping
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
operator|!=
literal|null
condition|)
block|{
name|newMappings
operator|.
name|add
argument_list|(
name|newMapping
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newMappings
operator|.
name|add
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// initialize groups if mappings are present
if|if
condition|(
name|newMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Groups
name|groups
init|=
operator|new
name|Groups
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|mappings
operator|=
name|newMappings
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
name|this
operator|.
name|overrideWithQueueMappings
operator|=
name|overrideWithQueueMappings
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|validateAndGetQueueMapping ( CapacitySchedulerQueueManager queueManager, CSQueue queue, QueueMapping mapping, QueuePath queuePath)
specifier|private
specifier|static
name|QueueMapping
name|validateAndGetQueueMapping
parameter_list|(
name|CapacitySchedulerQueueManager
name|queueManager
parameter_list|,
name|CSQueue
name|queue
parameter_list|,
name|QueueMapping
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
name|QueueMapping
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
DECL|method|ifQueueDoesNotExist (CSQueue queue)
specifier|private
specifier|static
name|boolean
name|ifQueueDoesNotExist
parameter_list|(
name|CSQueue
name|queue
parameter_list|)
block|{
return|return
name|queue
operator|==
literal|null
return|;
block|}
DECL|method|validateAndGetAutoCreatedQueueMapping ( CapacitySchedulerQueueManager queueManager, QueueMapping mapping, QueuePath queuePath)
specifier|private
specifier|static
name|QueueMapping
name|validateAndGetAutoCreatedQueueMapping
parameter_list|(
name|CapacitySchedulerQueueManager
name|queueManager
parameter_list|,
name|QueueMapping
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
name|validateParentQueue
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
name|QueueMapping
argument_list|(
name|mapping
operator|.
name|getType
argument_list|()
argument_list|,
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
DECL|method|isStaticQueueMapping (QueueMapping mapping)
specifier|private
specifier|static
name|boolean
name|isStaticQueueMapping
parameter_list|(
name|QueueMapping
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
name|UserGroupMappingPlacementRule
operator|.
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
name|UserGroupMappingPlacementRule
operator|.
name|PRIMARY_GROUP_MAPPING
argument_list|)
return|;
block|}
DECL|class|QueuePath
specifier|private
specifier|static
class|class
name|QueuePath
block|{
DECL|field|parentQueue
specifier|public
name|String
name|parentQueue
decl_stmt|;
DECL|field|leafQueue
specifier|public
name|String
name|leafQueue
decl_stmt|;
DECL|method|QueuePath (final String leafQueue)
specifier|public
name|QueuePath
parameter_list|(
specifier|final
name|String
name|leafQueue
parameter_list|)
block|{
name|this
operator|.
name|leafQueue
operator|=
name|leafQueue
expr_stmt|;
block|}
DECL|method|QueuePath (final String parentQueue, final String leafQueue)
specifier|public
name|QueuePath
parameter_list|(
specifier|final
name|String
name|parentQueue
parameter_list|,
specifier|final
name|String
name|leafQueue
parameter_list|)
block|{
name|this
operator|.
name|parentQueue
operator|=
name|parentQueue
expr_stmt|;
name|this
operator|.
name|leafQueue
operator|=
name|leafQueue
expr_stmt|;
block|}
DECL|method|getParentQueue ()
specifier|public
name|String
name|getParentQueue
parameter_list|()
block|{
return|return
name|parentQueue
return|;
block|}
DECL|method|getLeafQueue ()
specifier|public
name|String
name|getLeafQueue
parameter_list|()
block|{
return|return
name|leafQueue
return|;
block|}
DECL|method|hasParentQueue ()
specifier|public
name|boolean
name|hasParentQueue
parameter_list|()
block|{
return|return
name|parentQueue
operator|!=
literal|null
return|;
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
name|parentQueue
operator|+
name|DOT
operator|+
name|leafQueue
return|;
block|}
block|}
DECL|method|extractQueuePath (String queueName)
specifier|private
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
DECL|method|validateParentQueue (CSQueue parentQueue, String parentQueueName, String leafQueueName)
specifier|private
specifier|static
name|void
name|validateParentQueue
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
annotation|@
name|VisibleForTesting
DECL|method|getQueueMappings ()
specifier|public
name|List
argument_list|<
name|QueueMapping
argument_list|>
name|getQueueMappings
parameter_list|()
block|{
return|return
name|mappings
return|;
block|}
block|}
end_class

end_unit

