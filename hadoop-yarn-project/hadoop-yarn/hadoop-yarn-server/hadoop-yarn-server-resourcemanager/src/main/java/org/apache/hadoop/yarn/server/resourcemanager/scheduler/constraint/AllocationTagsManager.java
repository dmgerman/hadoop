begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint
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
name|constraint
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|ContainerId
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
name|NodeId
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
name|SchedulingRequest
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
name|log4j
operator|.
name|Logger
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|LongBinaryOperator
import|;
end_import

begin_comment
comment|/**  * In-memory mapping between applications/container-tags and nodes/racks.  * Required by constrained affinity/anti-affinity and cardinality placement.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|AllocationTagsManager
specifier|public
class|class
name|AllocationTagsManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AllocationTagsManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|readLock
specifier|private
name|ReentrantReadWriteLock
operator|.
name|ReadLock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|private
name|ReentrantReadWriteLock
operator|.
name|WriteLock
name|writeLock
decl_stmt|;
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
comment|// Application's tags to Node
DECL|field|perAppNodeMappings
specifier|private
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|TypeToCountedTags
argument_list|>
name|perAppNodeMappings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Application's tags to Rack
DECL|field|perAppRackMappings
specifier|private
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|TypeToCountedTags
argument_list|>
name|perAppRackMappings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Global tags to node mapping (used to fast return aggregated tags
comment|// cardinality across apps)
DECL|field|globalNodeMapping
specifier|private
name|TypeToCountedTags
argument_list|<
name|NodeId
argument_list|>
name|globalNodeMapping
init|=
operator|new
name|TypeToCountedTags
argument_list|()
decl_stmt|;
comment|// Global tags to Rack mapping
DECL|field|globalRackMapping
specifier|private
name|TypeToCountedTags
argument_list|<
name|String
argument_list|>
name|globalRackMapping
init|=
operator|new
name|TypeToCountedTags
argument_list|()
decl_stmt|;
comment|/**    * Generic store mapping type T to counted tags.    * Currently used both for NodeId to Tag, Count and Rack to Tag, Count    */
annotation|@
name|VisibleForTesting
DECL|class|TypeToCountedTags
specifier|public
specifier|static
class|class
name|TypeToCountedTags
parameter_list|<
name|T
parameter_list|>
block|{
comment|// Map<Type, Map<Tag, Count>>
DECL|field|typeToTagsWithCount
specifier|private
name|Map
argument_list|<
name|T
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|typeToTagsWithCount
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// protected by external locks
DECL|method|addTags (T type, Set<String> tags)
specifier|private
name|void
name|addTags
parameter_list|(
name|T
name|type
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|tags
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|innerMap
init|=
name|typeToTagsWithCount
operator|.
name|computeIfAbsent
argument_list|(
name|type
argument_list|,
name|k
lambda|->
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|tags
control|)
block|{
name|Long
name|count
init|=
name|innerMap
operator|.
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
block|{
name|innerMap
operator|.
name|put
argument_list|(
name|tag
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|innerMap
operator|.
name|put
argument_list|(
name|tag
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// protected by external locks
DECL|method|addTag (T type, String tag)
specifier|private
name|void
name|addTag
parameter_list|(
name|T
name|type
parameter_list|,
name|String
name|tag
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|innerMap
init|=
name|typeToTagsWithCount
operator|.
name|computeIfAbsent
argument_list|(
name|type
argument_list|,
name|k
lambda|->
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|Long
name|count
init|=
name|innerMap
operator|.
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
block|{
name|innerMap
operator|.
name|put
argument_list|(
name|tag
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|innerMap
operator|.
name|put
argument_list|(
name|tag
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeTagFromInnerMap (Map<String, Long> innerMap, String tag)
specifier|private
name|void
name|removeTagFromInnerMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|innerMap
parameter_list|,
name|String
name|tag
parameter_list|)
block|{
name|Long
name|count
init|=
name|innerMap
operator|.
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|1
condition|)
block|{
name|innerMap
operator|.
name|put
argument_list|(
name|tag
argument_list|,
name|count
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|count
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trying to remove tags from node/rack, however the count already"
operator|+
literal|" becomes 0 or less, it could be a potential bug."
argument_list|)
expr_stmt|;
block|}
name|innerMap
operator|.
name|remove
argument_list|(
name|tag
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeTags (T type, Set<String> tags)
specifier|private
name|void
name|removeTags
parameter_list|(
name|T
name|type
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|tags
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|innerMap
init|=
name|typeToTagsWithCount
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerMap
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to find node/rack="
operator|+
name|type
operator|+
literal|" while trying to remove tags, please double check."
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|String
name|tag
range|:
name|tags
control|)
block|{
name|removeTagFromInnerMap
argument_list|(
name|innerMap
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|innerMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|typeToTagsWithCount
operator|.
name|remove
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeTag (T type, String tag)
specifier|private
name|void
name|removeTag
parameter_list|(
name|T
name|type
parameter_list|,
name|String
name|tag
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|innerMap
init|=
name|typeToTagsWithCount
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerMap
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to find node/rack="
operator|+
name|type
operator|+
literal|" while trying to remove tags, please double check."
argument_list|)
expr_stmt|;
return|return;
block|}
name|removeTagFromInnerMap
argument_list|(
name|innerMap
argument_list|,
name|tag
argument_list|)
expr_stmt|;
if|if
condition|(
name|innerMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|typeToTagsWithCount
operator|.
name|remove
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCardinality (T type, String tag)
specifier|private
name|long
name|getCardinality
parameter_list|(
name|T
name|type
parameter_list|,
name|String
name|tag
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|innerMap
init|=
name|typeToTagsWithCount
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerMap
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
name|Long
name|value
init|=
name|innerMap
operator|.
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
return|return
name|value
operator|==
literal|null
condition|?
literal|0
else|:
name|value
return|;
block|}
DECL|method|getCardinality (T type, Set<String> tags, LongBinaryOperator op)
specifier|private
name|long
name|getCardinality
parameter_list|(
name|T
name|type
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|tags
parameter_list|,
name|LongBinaryOperator
name|op
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|innerMap
init|=
name|typeToTagsWithCount
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerMap
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
name|long
name|returnValue
init|=
literal|0
decl_stmt|;
name|boolean
name|firstTag
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|tags
operator|!=
literal|null
operator|&&
operator|!
name|tags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|tag
range|:
name|tags
control|)
block|{
name|Long
name|value
init|=
name|innerMap
operator|.
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
literal|0L
expr_stmt|;
block|}
if|if
condition|(
name|firstTag
condition|)
block|{
name|returnValue
operator|=
name|value
expr_stmt|;
name|firstTag
operator|=
literal|false
expr_stmt|;
continue|continue;
block|}
name|returnValue
operator|=
name|op
operator|.
name|applyAsLong
argument_list|(
name|returnValue
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Similar to above if, but only iterate values for better performance
for|for
control|(
name|long
name|value
range|:
name|innerMap
operator|.
name|values
argument_list|()
control|)
block|{
comment|// For the first value, we will not apply op
if|if
condition|(
name|firstTag
condition|)
block|{
name|returnValue
operator|=
name|value
expr_stmt|;
name|firstTag
operator|=
literal|false
expr_stmt|;
continue|continue;
block|}
name|returnValue
operator|=
name|op
operator|.
name|applyAsLong
argument_list|(
name|returnValue
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|returnValue
return|;
block|}
DECL|method|isEmpty ()
specifier|private
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|typeToTagsWithCount
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getTypeToTagsWithCount ()
specifier|public
name|Map
argument_list|<
name|T
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|getTypeToTagsWithCount
parameter_list|()
block|{
return|return
name|typeToTagsWithCount
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getPerAppNodeMappings ()
specifier|public
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|TypeToCountedTags
argument_list|>
name|getPerAppNodeMappings
parameter_list|()
block|{
return|return
name|perAppNodeMappings
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getPerAppRackMappings ()
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|TypeToCountedTags
argument_list|>
name|getPerAppRackMappings
parameter_list|()
block|{
return|return
name|perAppRackMappings
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getGlobalNodeMapping ()
name|TypeToCountedTags
name|getGlobalNodeMapping
parameter_list|()
block|{
return|return
name|globalNodeMapping
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getGlobalRackMapping ()
name|TypeToCountedTags
name|getGlobalRackMapping
parameter_list|()
block|{
return|return
name|globalRackMapping
return|;
block|}
DECL|method|AllocationTagsManager (RMContext context)
specifier|public
name|AllocationTagsManager
parameter_list|(
name|RMContext
name|context
parameter_list|)
block|{
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|rmContext
operator|=
name|context
expr_stmt|;
block|}
comment|/**    * Notify container allocated on a node.    *    * @param nodeId         allocated node.    * @param containerId    container id.    * @param allocationTags allocation tags, see    *                       {@link SchedulingRequest#getAllocationTags()}    *                       application_id will be added to allocationTags.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|addContainer (NodeId nodeId, ContainerId containerId, Set<String> allocationTags)
specifier|public
name|void
name|addContainer
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
parameter_list|)
block|{
comment|// Do nothing for empty allocation tags.
if|if
condition|(
name|allocationTags
operator|==
literal|null
operator|||
name|allocationTags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|ApplicationId
name|applicationId
init|=
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|addTags
argument_list|(
name|nodeId
argument_list|,
name|applicationId
argument_list|,
name|allocationTags
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Added container="
operator|+
name|containerId
operator|+
literal|" with tags=["
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
name|allocationTags
argument_list|,
literal|","
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addTags (NodeId nodeId, ApplicationId applicationId, Set<String> allocationTags)
specifier|public
name|void
name|addTags
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|TypeToCountedTags
name|perAppTagsMapping
init|=
name|perAppNodeMappings
operator|.
name|computeIfAbsent
argument_list|(
name|applicationId
argument_list|,
name|k
lambda|->
operator|new
name|TypeToCountedTags
argument_list|()
argument_list|)
decl_stmt|;
name|TypeToCountedTags
name|perAppRackTagsMapping
init|=
name|perAppRackMappings
operator|.
name|computeIfAbsent
argument_list|(
name|applicationId
argument_list|,
name|k
lambda|->
operator|new
name|TypeToCountedTags
argument_list|()
argument_list|)
decl_stmt|;
comment|// Covering test-cases where context is mocked
name|String
name|nodeRack
init|=
operator|(
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|!=
literal|null
operator|&&
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|!=
literal|null
operator|)
condition|?
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|.
name|getRackName
argument_list|()
else|:
literal|"default-rack"
decl_stmt|;
name|perAppTagsMapping
operator|.
name|addTags
argument_list|(
name|nodeId
argument_list|,
name|allocationTags
argument_list|)
expr_stmt|;
name|perAppRackTagsMapping
operator|.
name|addTags
argument_list|(
name|nodeRack
argument_list|,
name|allocationTags
argument_list|)
expr_stmt|;
name|globalNodeMapping
operator|.
name|addTags
argument_list|(
name|nodeId
argument_list|,
name|allocationTags
argument_list|)
expr_stmt|;
name|globalRackMapping
operator|.
name|addTags
argument_list|(
name|nodeRack
argument_list|,
name|allocationTags
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Notify container removed.    *    * @param nodeId         nodeId    * @param containerId    containerId.    * @param allocationTags allocation tags for given container    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|removeContainer (NodeId nodeId, ContainerId containerId, Set<String> allocationTags)
specifier|public
name|void
name|removeContainer
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
parameter_list|)
block|{
comment|// Do nothing for empty allocation tags.
if|if
condition|(
name|allocationTags
operator|==
literal|null
operator|||
name|allocationTags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|ApplicationId
name|applicationId
init|=
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|removeTags
argument_list|(
name|nodeId
argument_list|,
name|applicationId
argument_list|,
name|allocationTags
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Removed container="
operator|+
name|containerId
operator|+
literal|" with tags=["
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
name|allocationTags
argument_list|,
literal|","
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Helper method to just remove the tags associated with a container.    * @param nodeId    * @param applicationId    * @param allocationTags    */
DECL|method|removeTags (NodeId nodeId, ApplicationId applicationId, Set<String> allocationTags)
specifier|public
name|void
name|removeTags
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|TypeToCountedTags
name|perAppTagsMapping
init|=
name|perAppNodeMappings
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
name|TypeToCountedTags
name|perAppRackTagsMapping
init|=
name|perAppRackMappings
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
if|if
condition|(
name|perAppTagsMapping
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// Covering test-cases where context is mocked
name|String
name|nodeRack
init|=
operator|(
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|!=
literal|null
operator|&&
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|!=
literal|null
operator|)
condition|?
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|.
name|getRackName
argument_list|()
else|:
literal|"default-rack"
decl_stmt|;
name|perAppTagsMapping
operator|.
name|removeTags
argument_list|(
name|nodeId
argument_list|,
name|allocationTags
argument_list|)
expr_stmt|;
name|perAppRackTagsMapping
operator|.
name|removeTags
argument_list|(
name|nodeRack
argument_list|,
name|allocationTags
argument_list|)
expr_stmt|;
name|globalNodeMapping
operator|.
name|removeTags
argument_list|(
name|nodeId
argument_list|,
name|allocationTags
argument_list|)
expr_stmt|;
name|globalRackMapping
operator|.
name|removeTags
argument_list|(
name|nodeRack
argument_list|,
name|allocationTags
argument_list|)
expr_stmt|;
if|if
condition|(
name|perAppTagsMapping
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|perAppNodeMappings
operator|.
name|remove
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|perAppRackTagsMapping
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|perAppRackMappings
operator|.
name|remove
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get Node cardinality for a specific tag.    * When applicationId is null, method returns aggregated cardinality    *    * @param nodeId        nodeId, required.    * @param applicationId applicationId. When null is specified, return    *                      aggregated cardinality among all nodes.    * @param tag           allocation tag, see    *                      {@link SchedulingRequest#getAllocationTags()},    *                      If a specified tag doesn't exist,    *                      method returns 0.    * @return cardinality of specified query on the node.    * @throws InvalidAllocationTagsQueryException when illegal query    *                                            parameter specified    */
DECL|method|getNodeCardinality (NodeId nodeId, ApplicationId applicationId, String tag)
specifier|public
name|long
name|getNodeCardinality
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidAllocationTagsQueryException
argument_list|(
literal|"Must specify nodeId/tag to query cardinality"
argument_list|)
throw|;
block|}
name|TypeToCountedTags
name|mapping
decl_stmt|;
if|if
condition|(
name|applicationId
operator|!=
literal|null
condition|)
block|{
name|mapping
operator|=
name|perAppNodeMappings
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapping
operator|=
name|globalNodeMapping
expr_stmt|;
block|}
if|if
condition|(
name|mapping
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|mapping
operator|.
name|getCardinality
argument_list|(
name|nodeId
argument_list|,
name|tag
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get Rack cardinality for a specific tag.    *    * @param rack          rack, required.    * @param applicationId applicationId. When null is specified, return    *                      aggregated cardinality among all nodes.    * @param tag           allocation tag, see    *                      {@link SchedulingRequest#getAllocationTags()},    *                      If a specified tag doesn't exist,    *                      method returns 0.    * @return cardinality of specified query on the rack.    * @throws InvalidAllocationTagsQueryException when illegal query    *                                            parameter specified    */
DECL|method|getRackCardinality (String rack, ApplicationId applicationId, String tag)
specifier|public
name|long
name|getRackCardinality
parameter_list|(
name|String
name|rack
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|rack
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidAllocationTagsQueryException
argument_list|(
literal|"Must specify rack/tag to query cardinality"
argument_list|)
throw|;
block|}
name|TypeToCountedTags
name|mapping
decl_stmt|;
if|if
condition|(
name|applicationId
operator|!=
literal|null
condition|)
block|{
name|mapping
operator|=
name|perAppRackMappings
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapping
operator|=
name|globalRackMapping
expr_stmt|;
block|}
if|if
condition|(
name|mapping
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|mapping
operator|.
name|getCardinality
argument_list|(
name|rack
argument_list|,
name|tag
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Check if given tag exists on node.    *    * @param nodeId        nodeId, required.    * @param applicationId applicationId. When null is specified, return    *                      aggregation among all applications.    * @param tag           allocation tag, see    *                      {@link SchedulingRequest#getAllocationTags()},    *                      When multiple tags specified. Returns cardinality    *                      depends on op. If a specified tag doesn't exist,    *                      0 will be its cardinality.    *                      When null/empty tags specified, all tags    *                      (of the node/app) will be considered.    * @return cardinality of specified query on the node.    * @throws InvalidAllocationTagsQueryException when illegal query    *                                            parameter specified    */
DECL|method|allocationTagExistsOnNode (NodeId nodeId, ApplicationId applicationId, String tag)
specifier|public
name|boolean
name|allocationTagExistsOnNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
return|return
name|getNodeCardinality
argument_list|(
name|nodeId
argument_list|,
name|applicationId
argument_list|,
name|tag
argument_list|)
operator|>
literal|0
return|;
block|}
comment|/**    * Get cardinality for following conditions. External can pass-in a binary op    * to implement customized logic.    *    * @param nodeId        nodeId, required.    * @param applicationId applicationId. When null is specified, return    *                      aggregated cardinality among all applications.    * @param tags          allocation tags, see    *                      {@link SchedulingRequest#getAllocationTags()},    *                      When multiple tags specified. Returns cardinality    *                      depends on op. If a specified tag doesn't exist, 0    *                      will be its cardinality. When null/empty tags    *                      specified, all tags (of the node/app) will be    *                      considered.    * @param op            operator. Such as Long::max, Long::sum, etc. Required.    *                      This parameter only take effect when #values greater    *                      than 2.    * @return cardinality of specified query on the node.    * @throws InvalidAllocationTagsQueryException when illegal query    *                                            parameter specified    */
DECL|method|getNodeCardinalityByOp (NodeId nodeId, ApplicationId applicationId, Set<String> tags, LongBinaryOperator op)
specifier|public
name|long
name|getNodeCardinalityByOp
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|tags
parameter_list|,
name|LongBinaryOperator
name|op
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|nodeId
operator|==
literal|null
operator|||
name|op
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidAllocationTagsQueryException
argument_list|(
literal|"Must specify nodeId/tags/op to query cardinality"
argument_list|)
throw|;
block|}
name|TypeToCountedTags
name|mapping
decl_stmt|;
if|if
condition|(
name|applicationId
operator|!=
literal|null
condition|)
block|{
name|mapping
operator|=
name|perAppNodeMappings
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapping
operator|=
name|globalNodeMapping
expr_stmt|;
block|}
if|if
condition|(
name|mapping
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|mapping
operator|.
name|getCardinality
argument_list|(
name|nodeId
argument_list|,
name|tags
argument_list|,
name|op
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get cardinality for following conditions. External can pass-in a binary op    * to implement customized logic.    *    * @param rack          rack, required.    * @param applicationId applicationId. When null is specified, return    *                      aggregated cardinality among all applications.    * @param tags          allocation tags, see    *                      {@link SchedulingRequest#getAllocationTags()},    *                      When multiple tags specified. Returns cardinality    *                      depends on op. If a specified tag doesn't exist, 0    *                      will be its cardinality. When null/empty tags    *                      specified, all tags (of the rack/app) will be    *                      considered.    * @param op            operator. Such as Long::max, Long::sum, etc. Required.    *                      This parameter only take effect when #values    *                      greater than 2.    * @return cardinality of specified query on the rack.    * @throws InvalidAllocationTagsQueryException when illegal query    *                                            parameter specified    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getRackCardinalityByOp (String rack, ApplicationId applicationId, Set<String> tags, LongBinaryOperator op)
specifier|public
name|long
name|getRackCardinalityByOp
parameter_list|(
name|String
name|rack
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|tags
parameter_list|,
name|LongBinaryOperator
name|op
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|rack
operator|==
literal|null
operator|||
name|op
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidAllocationTagsQueryException
argument_list|(
literal|"Must specify rack/tags/op to query cardinality"
argument_list|)
throw|;
block|}
name|TypeToCountedTags
name|mapping
decl_stmt|;
if|if
condition|(
name|applicationId
operator|!=
literal|null
condition|)
block|{
name|mapping
operator|=
name|perAppRackMappings
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapping
operator|=
name|globalRackMapping
expr_stmt|;
block|}
if|if
condition|(
name|mapping
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|mapping
operator|.
name|getCardinality
argument_list|(
name|rack
argument_list|,
name|tags
argument_list|,
name|op
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns a map whose key is the allocation tag and value is the    * count of allocations with this tag.    *    * @param nodeId    * @return allocation tag to count mapping    */
DECL|method|getAllocationTagsWithCount (NodeId nodeId)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getAllocationTagsWithCount
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
return|return
name|globalNodeMapping
operator|.
name|getTypeToTagsWithCount
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

