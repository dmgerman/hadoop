begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.nodelabels
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
name|nodelabels
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|Resource
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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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
name|nodelabels
operator|.
name|NodeLabel
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
name|event
operator|.
name|NodeLabelsUpdateSchedulerEvent
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
name|util
operator|.
name|resource
operator|.
name|Resources
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
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_class
DECL|class|RMNodeLabelsManager
specifier|public
class|class
name|RMNodeLabelsManager
extends|extends
name|CommonNodeLabelsManager
block|{
DECL|class|Queue
specifier|protected
specifier|static
class|class
name|Queue
block|{
DECL|field|acccessibleNodeLabels
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|acccessibleNodeLabels
decl_stmt|;
DECL|field|resource
specifier|protected
name|Resource
name|resource
decl_stmt|;
DECL|method|Queue ()
specifier|protected
name|Queue
parameter_list|()
block|{
name|acccessibleNodeLabels
operator|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|resource
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|queueCollections
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|Queue
argument_list|>
name|queueCollections
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Queue
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|authorizer
specifier|private
name|YarnAuthorizationProvider
name|authorizer
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
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
DECL|method|addLabelsToNode (Map<NodeId, Set<String>> addedLabelsToNode)
specifier|public
name|void
name|addLabelsToNode
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|addedLabelsToNode
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|// get nodesCollection before edition
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|before
init|=
name|cloneNodeMap
argument_list|(
name|addedLabelsToNode
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|super
operator|.
name|addLabelsToNode
argument_list|(
name|addedLabelsToNode
argument_list|)
expr_stmt|;
comment|// get nodesCollection after edition
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|after
init|=
name|cloneNodeMap
argument_list|(
name|addedLabelsToNode
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
comment|// update running nodes resources
name|updateResourceMappings
argument_list|(
name|before
argument_list|,
name|after
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
DECL|method|checkRemoveFromClusterNodeLabelsOfQueue ( Collection<String> labelsToRemove)
specifier|protected
name|void
name|checkRemoveFromClusterNodeLabelsOfQueue
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|labelsToRemove
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Check if label to remove doesn't existed or null/empty, will throw
comment|// exception if any of labels to remove doesn't meet requirement
for|for
control|(
name|String
name|label
range|:
name|labelsToRemove
control|)
block|{
name|label
operator|=
name|normalizeLabel
argument_list|(
name|label
argument_list|)
expr_stmt|;
comment|// check if any queue contains this label
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Queue
argument_list|>
name|entry
range|:
name|queueCollections
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|queueName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|queueLabels
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|acccessibleNodeLabels
decl_stmt|;
if|if
condition|(
name|queueLabels
operator|.
name|contains
argument_list|(
name|label
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot remove label="
operator|+
name|label
operator|+
literal|", because queue="
operator|+
name|queueName
operator|+
literal|" is using this label. "
operator|+
literal|"Please remove label on queue before remove the label"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|removeFromClusterNodeLabels (Collection<String> labelsToRemove)
specifier|public
name|void
name|removeFromClusterNodeLabels
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|labelsToRemove
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|checkRemoveFromClusterNodeLabelsOfQueue
argument_list|(
name|labelsToRemove
argument_list|)
expr_stmt|;
comment|// copy before NMs
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|before
init|=
name|cloneNodeMap
argument_list|()
decl_stmt|;
name|super
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|labelsToRemove
argument_list|)
expr_stmt|;
name|updateResourceMappings
argument_list|(
name|before
argument_list|,
name|nodeCollections
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
annotation|@
name|Override
specifier|public
name|void
DECL|method|removeLabelsFromNode (Map<NodeId, Set<String>> removeLabelsFromNode)
name|removeLabelsFromNode
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|removeLabelsFromNode
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|// get nodesCollection before edition
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|before
init|=
name|cloneNodeMap
argument_list|(
name|removeLabelsFromNode
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|super
operator|.
name|removeLabelsFromNode
argument_list|(
name|removeLabelsFromNode
argument_list|)
expr_stmt|;
comment|// get nodesCollection before edition
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|after
init|=
name|cloneNodeMap
argument_list|(
name|removeLabelsFromNode
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
comment|// update running nodes resources
name|updateResourceMappings
argument_list|(
name|before
argument_list|,
name|after
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
annotation|@
name|Override
DECL|method|replaceLabelsOnNode (Map<NodeId, Set<String>> replaceLabelsToNode)
specifier|public
name|void
name|replaceLabelsOnNode
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|replaceLabelsToNode
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|// get nodesCollection before edition
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|before
init|=
name|cloneNodeMap
argument_list|(
name|replaceLabelsToNode
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|super
operator|.
name|replaceLabelsOnNode
argument_list|(
name|replaceLabelsToNode
argument_list|)
expr_stmt|;
comment|// get nodesCollection after edition
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|after
init|=
name|cloneNodeMap
argument_list|(
name|replaceLabelsToNode
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
comment|// update running nodes resources
name|updateResourceMappings
argument_list|(
name|before
argument_list|,
name|after
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
comment|/*    * Following methods are used for setting if a node is up and running, and it    * will update running nodes resource    */
DECL|method|activateNode (NodeId nodeId, Resource resource)
specifier|public
name|void
name|activateNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|// save if we have a node before
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|before
init|=
name|cloneNodeMap
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|nodeId
argument_list|)
argument_list|)
decl_stmt|;
name|createHostIfNonExisted
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|createNodeIfNonExisted
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"This shouldn't happen, cannot get host in nodeCollection"
operator|+
literal|" associated to the node being activated"
argument_list|)
expr_stmt|;
return|return;
block|}
name|Node
name|nm
init|=
name|getNMInNodeSet
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
name|nm
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
name|nm
operator|.
name|running
operator|=
literal|true
expr_stmt|;
comment|// Add node in labelsCollection
name|Set
argument_list|<
name|String
argument_list|>
name|labelsForNode
init|=
name|getLabelsByNode
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|labelsForNode
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|label
range|:
name|labelsForNode
control|)
block|{
name|NodeLabel
name|labelInfo
init|=
name|labelCollections
operator|.
name|get
argument_list|(
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|labelInfo
operator|!=
literal|null
condition|)
block|{
name|labelInfo
operator|.
name|addNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// get the node after edition
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|after
init|=
name|cloneNodeMap
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|nodeId
argument_list|)
argument_list|)
decl_stmt|;
name|updateResourceMappings
argument_list|(
name|before
argument_list|,
name|after
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
comment|/*    * Following methods are used for setting if a node unregistered to RM    */
DECL|method|deactivateNode (NodeId nodeId)
specifier|public
name|void
name|deactivateNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|// save if we have a node before
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|before
init|=
name|cloneNodeMap
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|nodeId
argument_list|)
argument_list|)
decl_stmt|;
name|Node
name|nm
init|=
name|getNMInNodeSet
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|nm
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|nm
operator|.
name|labels
condition|)
block|{
comment|// When node deactivated, remove the nm from node collection if no
comment|// labels explicitly set for this particular nm
comment|// Save labels first, we need to remove label->nodes relation later
name|Set
argument_list|<
name|String
argument_list|>
name|savedNodeLabels
init|=
name|getLabelsOnNode
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
comment|// Remove this node in nodes collection
name|nodeCollections
operator|.
name|get
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|)
operator|.
name|nms
operator|.
name|remove
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
comment|// Remove this node in labels->node
name|removeNodeFromLabels
argument_list|(
name|nodeId
argument_list|,
name|savedNodeLabels
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// set nm is not running, and its resource = 0
name|nm
operator|.
name|running
operator|=
literal|false
expr_stmt|;
name|nm
operator|.
name|resource
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|// get the node after edition
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|after
init|=
name|cloneNodeMap
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|nodeId
argument_list|)
argument_list|)
decl_stmt|;
name|updateResourceMappings
argument_list|(
name|before
argument_list|,
name|after
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
DECL|method|updateNodeResource (NodeId node, Resource newResource)
specifier|public
name|void
name|updateNodeResource
parameter_list|(
name|NodeId
name|node
parameter_list|,
name|Resource
name|newResource
parameter_list|)
throws|throws
name|IOException
block|{
name|deactivateNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|activateNode
argument_list|(
name|node
argument_list|,
name|newResource
argument_list|)
expr_stmt|;
block|}
DECL|method|reinitializeQueueLabels (Map<String, Set<String>> queueToLabels)
specifier|public
name|void
name|reinitializeQueueLabels
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|queueToLabels
parameter_list|)
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|// clear before set
name|this
operator|.
name|queueCollections
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|queueToLabels
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|queue
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Queue
name|q
init|=
operator|new
name|Queue
argument_list|()
decl_stmt|;
name|this
operator|.
name|queueCollections
operator|.
name|put
argument_list|(
name|queue
argument_list|,
name|q
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|labels
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|labels
operator|.
name|contains
argument_list|(
name|ANY
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|q
operator|.
name|acccessibleNodeLabels
operator|.
name|addAll
argument_list|(
name|labels
argument_list|)
expr_stmt|;
for|for
control|(
name|Host
name|host
range|:
name|nodeCollections
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|Node
argument_list|>
name|nentry
range|:
name|host
operator|.
name|nms
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NodeId
name|nodeId
init|=
name|nentry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Node
name|nm
init|=
name|nentry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|nm
operator|.
name|running
operator|&&
name|isNodeUsableByQueue
argument_list|(
name|getLabelsByNode
argument_list|(
name|nodeId
argument_list|)
argument_list|,
name|q
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|q
operator|.
name|resource
argument_list|,
name|nm
operator|.
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
DECL|method|getQueueResource (String queueName, Set<String> queueLabels, Resource clusterResource)
specifier|public
name|Resource
name|getQueueResource
parameter_list|(
name|String
name|queueName
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|queueLabels
parameter_list|,
name|Resource
name|clusterResource
parameter_list|)
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
name|queueLabels
operator|.
name|contains
argument_list|(
name|ANY
argument_list|)
condition|)
block|{
return|return
name|clusterResource
return|;
block|}
name|Queue
name|q
init|=
name|queueCollections
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|q
condition|)
block|{
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
return|return
name|q
operator|.
name|resource
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
DECL|method|getLabelsOnNode (NodeId nodeId)
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getLabelsOnNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodeLabels
init|=
name|getLabelsByNode
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|nodeLabels
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
DECL|method|containsNodeLabel (String label)
specifier|public
name|boolean
name|containsNodeLabel
parameter_list|(
name|String
name|label
parameter_list|)
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|label
operator|!=
literal|null
operator|&&
operator|(
name|label
operator|.
name|isEmpty
argument_list|()
operator|||
name|labelCollections
operator|.
name|containsKey
argument_list|(
name|label
argument_list|)
operator|)
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
DECL|method|cloneNodeMap (Set<NodeId> nodesToCopy)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|cloneNodeMap
parameter_list|(
name|Set
argument_list|<
name|NodeId
argument_list|>
name|nodesToCopy
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeId
name|nodeId
range|:
name|nodesToCopy
control|)
block|{
if|if
condition|(
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|)
condition|)
block|{
name|Host
name|originalN
init|=
name|nodeCollections
operator|.
name|get
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|originalN
condition|)
block|{
continue|continue;
block|}
name|Host
name|n
init|=
name|originalN
operator|.
name|copy
argument_list|()
decl_stmt|;
name|n
operator|.
name|nms
operator|.
name|clear
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|Host
name|n
init|=
name|map
operator|.
name|get
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|WILDCARD_PORT
operator|==
name|nodeId
operator|.
name|getPort
argument_list|()
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|Node
argument_list|>
name|entry
range|:
name|nodeCollections
operator|.
name|get
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|)
operator|.
name|nms
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|n
operator|.
name|nms
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|copy
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Node
name|nm
init|=
name|getNMInNodeSet
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|nm
condition|)
block|{
name|n
operator|.
name|nms
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
name|nm
operator|.
name|copy
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|map
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|updateResourceMappings (Map<String, Host> before, Map<String, Host> after)
specifier|private
name|void
name|updateResourceMappings
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|before
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|after
parameter_list|)
block|{
comment|// Get NMs in before only
name|Set
argument_list|<
name|NodeId
argument_list|>
name|allNMs
init|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|entry
range|:
name|before
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|allNMs
operator|.
name|addAll
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|nms
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|entry
range|:
name|after
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|allNMs
operator|.
name|addAll
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|nms
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Map used to notify RM
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|newNodeToLabelsMap
init|=
operator|new
name|HashMap
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|// traverse all nms
for|for
control|(
name|NodeId
name|nodeId
range|:
name|allNMs
control|)
block|{
name|Node
name|oldNM
decl_stmt|;
if|if
condition|(
operator|(
name|oldNM
operator|=
name|getNMInNodeSet
argument_list|(
name|nodeId
argument_list|,
name|before
argument_list|,
literal|true
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|oldLabels
init|=
name|getLabelsByNode
argument_list|(
name|nodeId
argument_list|,
name|before
argument_list|)
decl_stmt|;
comment|// no label in the past
if|if
condition|(
name|oldLabels
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// update labels
name|NodeLabel
name|label
init|=
name|labelCollections
operator|.
name|get
argument_list|(
name|NO_LABEL
argument_list|)
decl_stmt|;
name|label
operator|.
name|removeNode
argument_list|(
name|oldNM
operator|.
name|resource
argument_list|)
expr_stmt|;
comment|// update queues, all queue can access this node
for|for
control|(
name|Queue
name|q
range|:
name|queueCollections
operator|.
name|values
argument_list|()
control|)
block|{
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|q
operator|.
name|resource
argument_list|,
name|oldNM
operator|.
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// update labels
for|for
control|(
name|String
name|labelName
range|:
name|oldLabels
control|)
block|{
name|NodeLabel
name|label
init|=
name|labelCollections
operator|.
name|get
argument_list|(
name|labelName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|label
condition|)
block|{
continue|continue;
block|}
name|label
operator|.
name|removeNode
argument_list|(
name|oldNM
operator|.
name|resource
argument_list|)
expr_stmt|;
block|}
comment|// update queues, only queue can access this node will be subtract
for|for
control|(
name|Queue
name|q
range|:
name|queueCollections
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|isNodeUsableByQueue
argument_list|(
name|oldLabels
argument_list|,
name|q
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|q
operator|.
name|resource
argument_list|,
name|oldNM
operator|.
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|Node
name|newNM
decl_stmt|;
if|if
condition|(
operator|(
name|newNM
operator|=
name|getNMInNodeSet
argument_list|(
name|nodeId
argument_list|,
name|after
argument_list|,
literal|true
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|newLabels
init|=
name|getLabelsByNode
argument_list|(
name|nodeId
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|newNodeToLabelsMap
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|newLabels
argument_list|)
argument_list|)
expr_stmt|;
comment|// no label in the past
if|if
condition|(
name|newLabels
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// update labels
name|NodeLabel
name|label
init|=
name|labelCollections
operator|.
name|get
argument_list|(
name|NO_LABEL
argument_list|)
decl_stmt|;
name|label
operator|.
name|addNode
argument_list|(
name|newNM
operator|.
name|resource
argument_list|)
expr_stmt|;
comment|// update queues, all queue can access this node
for|for
control|(
name|Queue
name|q
range|:
name|queueCollections
operator|.
name|values
argument_list|()
control|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|q
operator|.
name|resource
argument_list|,
name|newNM
operator|.
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// update labels
for|for
control|(
name|String
name|labelName
range|:
name|newLabels
control|)
block|{
name|NodeLabel
name|label
init|=
name|labelCollections
operator|.
name|get
argument_list|(
name|labelName
argument_list|)
decl_stmt|;
name|label
operator|.
name|addNode
argument_list|(
name|newNM
operator|.
name|resource
argument_list|)
expr_stmt|;
block|}
comment|// update queues, only queue can access this node will be subtract
for|for
control|(
name|Queue
name|q
range|:
name|queueCollections
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|isNodeUsableByQueue
argument_list|(
name|newLabels
argument_list|,
name|q
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|q
operator|.
name|resource
argument_list|,
name|newNM
operator|.
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// Notify RM
if|if
condition|(
name|rmContext
operator|!=
literal|null
operator|&&
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|NodeLabelsUpdateSchedulerEvent
argument_list|(
name|newNodeToLabelsMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getResourceByLabel (String label, Resource clusterResource)
specifier|public
name|Resource
name|getResourceByLabel
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|clusterResource
parameter_list|)
block|{
name|label
operator|=
name|normalizeLabel
argument_list|(
name|label
argument_list|)
expr_stmt|;
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|labelCollections
operator|.
name|get
argument_list|(
name|label
argument_list|)
condition|)
block|{
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
return|return
name|labelCollections
operator|.
name|get
argument_list|(
name|label
argument_list|)
operator|.
name|getResource
argument_list|()
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
DECL|method|isNodeUsableByQueue (Set<String> nodeLabels, Queue q)
specifier|private
name|boolean
name|isNodeUsableByQueue
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodeLabels
parameter_list|,
name|Queue
name|q
parameter_list|)
block|{
comment|// node without any labels can be accessed by any queue
if|if
condition|(
name|nodeLabels
operator|==
literal|null
operator|||
name|nodeLabels
operator|.
name|isEmpty
argument_list|()
operator|||
operator|(
name|nodeLabels
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|nodeLabels
operator|.
name|contains
argument_list|(
name|NO_LABEL
argument_list|)
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|String
name|label
range|:
name|nodeLabels
control|)
block|{
if|if
condition|(
name|q
operator|.
name|acccessibleNodeLabels
operator|.
name|contains
argument_list|(
name|label
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|cloneNodeMap ()
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Host
argument_list|>
name|cloneNodeMap
parameter_list|()
block|{
name|Set
argument_list|<
name|NodeId
argument_list|>
name|nodesToCopy
init|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|nodeName
range|:
name|nodeCollections
operator|.
name|keySet
argument_list|()
control|)
block|{
name|nodesToCopy
operator|.
name|add
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
name|nodeName
argument_list|,
name|WILDCARD_PORT
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|cloneNodeMap
argument_list|(
name|nodesToCopy
argument_list|)
return|;
block|}
DECL|method|checkAccess (UserGroupInformation user)
specifier|public
name|boolean
name|checkAccess
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|)
block|{
comment|// make sure only admin can invoke
comment|// this method
if|if
condition|(
name|authorizer
operator|.
name|isAdmin
argument_list|(
name|user
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|setRMContext (RMContext rmContext)
specifier|public
name|void
name|setRMContext
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
DECL|method|pullRMNodeLabelsInfo ()
specifier|public
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|pullRMNodeLabelsInfo
parameter_list|()
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|infos
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeLabel
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeLabel
argument_list|>
name|entry
range|:
name|labelCollections
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NodeLabel
name|label
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|infos
operator|.
name|add
argument_list|(
name|label
operator|.
name|getCopy
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|infos
argument_list|)
expr_stmt|;
return|return
name|infos
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
block|}
end_class

end_unit

