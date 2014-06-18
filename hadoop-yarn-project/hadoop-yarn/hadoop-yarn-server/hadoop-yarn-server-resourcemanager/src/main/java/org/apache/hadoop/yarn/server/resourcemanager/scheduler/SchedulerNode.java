begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|Container
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
name|Priority
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
name|rmcontainer
operator|.
name|RMContainer
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
name|rmcontainer
operator|.
name|RMContainerState
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
name|rmnode
operator|.
name|RMNode
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

begin_comment
comment|/**  * Represents a YARN Cluster Node from the viewpoint of the scheduler.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SchedulerNode
specifier|public
specifier|abstract
class|class
name|SchedulerNode
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SchedulerNode
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|availableResource
specifier|private
name|Resource
name|availableResource
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|field|usedResource
specifier|private
name|Resource
name|usedResource
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|field|totalResourceCapability
specifier|private
name|Resource
name|totalResourceCapability
decl_stmt|;
DECL|field|reservedContainer
specifier|private
name|RMContainer
name|reservedContainer
decl_stmt|;
DECL|field|numContainers
specifier|private
specifier|volatile
name|int
name|numContainers
decl_stmt|;
comment|/* set of containers that are allocated containers */
DECL|field|launchedContainers
specifier|private
specifier|final
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|RMContainer
argument_list|>
name|launchedContainers
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|RMContainer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|rmNode
specifier|private
specifier|final
name|RMNode
name|rmNode
decl_stmt|;
DECL|field|nodeName
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
DECL|method|SchedulerNode (RMNode node, boolean usePortForNodeName)
specifier|public
name|SchedulerNode
parameter_list|(
name|RMNode
name|node
parameter_list|,
name|boolean
name|usePortForNodeName
parameter_list|)
block|{
name|this
operator|.
name|rmNode
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|availableResource
operator|=
name|Resources
operator|.
name|clone
argument_list|(
name|node
operator|.
name|getTotalCapability
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|totalResourceCapability
operator|=
name|Resources
operator|.
name|clone
argument_list|(
name|node
operator|.
name|getTotalCapability
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|usePortForNodeName
condition|)
block|{
name|nodeName
operator|=
name|rmNode
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|node
operator|.
name|getNodeID
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|nodeName
operator|=
name|rmNode
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getRMNode ()
specifier|public
name|RMNode
name|getRMNode
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmNode
return|;
block|}
comment|/**    * Get the ID of the node which contains both its hostname and port.    *     * @return the ID of the node    */
DECL|method|getNodeID ()
specifier|public
name|NodeId
name|getNodeID
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmNode
operator|.
name|getNodeID
argument_list|()
return|;
block|}
DECL|method|getHttpAddress ()
specifier|public
name|String
name|getHttpAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmNode
operator|.
name|getHttpAddress
argument_list|()
return|;
block|}
comment|/**    * Get the name of the node for scheduling matching decisions.    *<p/>    * Typically this is the 'hostname' reported by the node, but it could be    * configured to be 'hostname:port' reported by the node via the    * {@link YarnConfiguration#RM_SCHEDULER_INCLUDE_PORT_IN_NODE_NAME} constant.    * The main usecase of this is Yarn minicluster to be able to differentiate    * node manager instances by their port number.    *     * @return name of the node for scheduling matching decisions.    */
DECL|method|getNodeName ()
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
comment|/**    * Get rackname.    *     * @return rackname    */
DECL|method|getRackName ()
specifier|public
name|String
name|getRackName
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmNode
operator|.
name|getRackName
argument_list|()
return|;
block|}
comment|/**    * The Scheduler has allocated containers on this node to the given    * application.    *     * @param rmContainer    *          allocated container    */
DECL|method|allocateContainer (RMContainer rmContainer)
specifier|public
specifier|synchronized
name|void
name|allocateContainer
parameter_list|(
name|RMContainer
name|rmContainer
parameter_list|)
block|{
name|Container
name|container
init|=
name|rmContainer
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|deductAvailableResource
argument_list|(
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
operator|++
name|numContainers
expr_stmt|;
name|launchedContainers
operator|.
name|put
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|rmContainer
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Assigned container "
operator|+
name|container
operator|.
name|getId
argument_list|()
operator|+
literal|" of capacity "
operator|+
name|container
operator|.
name|getResource
argument_list|()
operator|+
literal|" on host "
operator|+
name|rmNode
operator|.
name|getNodeAddress
argument_list|()
operator|+
literal|", which has "
operator|+
name|numContainers
operator|+
literal|" containers, "
operator|+
name|getUsedResource
argument_list|()
operator|+
literal|" used and "
operator|+
name|getAvailableResource
argument_list|()
operator|+
literal|" available after allocation"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get available resources on the node.    *     * @return available resources on the node    */
DECL|method|getAvailableResource ()
specifier|public
specifier|synchronized
name|Resource
name|getAvailableResource
parameter_list|()
block|{
return|return
name|this
operator|.
name|availableResource
return|;
block|}
comment|/**    * Get used resources on the node.    *     * @return used resources on the node    */
DECL|method|getUsedResource ()
specifier|public
specifier|synchronized
name|Resource
name|getUsedResource
parameter_list|()
block|{
return|return
name|this
operator|.
name|usedResource
return|;
block|}
comment|/**    * Get total resources on the node.    *     * @return total resources on the node.    */
DECL|method|getTotalResource ()
specifier|public
name|Resource
name|getTotalResource
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalResourceCapability
return|;
block|}
DECL|method|isValidContainer (ContainerId containerId)
specifier|public
specifier|synchronized
name|boolean
name|isValidContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
if|if
condition|(
name|launchedContainers
operator|.
name|containsKey
argument_list|(
name|containerId
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
DECL|method|updateResource (Container container)
specifier|private
specifier|synchronized
name|void
name|updateResource
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|addAvailableResource
argument_list|(
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
operator|--
name|numContainers
expr_stmt|;
block|}
comment|/**    * Release an allocated container on this node.    *     * @param container    *          container to be released    */
DECL|method|releaseContainer (Container container)
specifier|public
specifier|synchronized
name|void
name|releaseContainer
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isValidContainer
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid container released "
operator|+
name|container
argument_list|)
expr_stmt|;
return|return;
block|}
comment|/* remove the containers from the nodemanger */
if|if
condition|(
literal|null
operator|!=
name|launchedContainers
operator|.
name|remove
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|updateResource
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Released container "
operator|+
name|container
operator|.
name|getId
argument_list|()
operator|+
literal|" of capacity "
operator|+
name|container
operator|.
name|getResource
argument_list|()
operator|+
literal|" on host "
operator|+
name|rmNode
operator|.
name|getNodeAddress
argument_list|()
operator|+
literal|", which currently has "
operator|+
name|numContainers
operator|+
literal|" containers, "
operator|+
name|getUsedResource
argument_list|()
operator|+
literal|" used and "
operator|+
name|getAvailableResource
argument_list|()
operator|+
literal|" available"
operator|+
literal|", release resources="
operator|+
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|addAvailableResource (Resource resource)
specifier|private
specifier|synchronized
name|void
name|addAvailableResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid resource addition of null resource for "
operator|+
name|rmNode
operator|.
name|getNodeAddress
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|Resources
operator|.
name|addTo
argument_list|(
name|availableResource
argument_list|,
name|resource
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|usedResource
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
DECL|method|deductAvailableResource (Resource resource)
specifier|private
specifier|synchronized
name|void
name|deductAvailableResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid deduction of null resource for "
operator|+
name|rmNode
operator|.
name|getNodeAddress
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|availableResource
argument_list|,
name|resource
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|usedResource
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reserve container for the attempt on this node.    */
DECL|method|reserveResource (SchedulerApplicationAttempt attempt, Priority priority, RMContainer container)
specifier|public
specifier|abstract
name|void
name|reserveResource
parameter_list|(
name|SchedulerApplicationAttempt
name|attempt
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|RMContainer
name|container
parameter_list|)
function_decl|;
comment|/**    * Unreserve resources on this node.    */
DECL|method|unreserveResource (SchedulerApplicationAttempt attempt)
specifier|public
specifier|abstract
name|void
name|unreserveResource
parameter_list|(
name|SchedulerApplicationAttempt
name|attempt
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"host: "
operator|+
name|rmNode
operator|.
name|getNodeAddress
argument_list|()
operator|+
literal|" #containers="
operator|+
name|getNumContainers
argument_list|()
operator|+
literal|" available="
operator|+
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|+
literal|" used="
operator|+
name|getUsedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
return|;
block|}
comment|/**    * Get number of active containers on the node.    *     * @return number of active containers on the node    */
DECL|method|getNumContainers ()
specifier|public
name|int
name|getNumContainers
parameter_list|()
block|{
return|return
name|numContainers
return|;
block|}
DECL|method|getRunningContainers ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|RMContainer
argument_list|>
name|getRunningContainers
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|RMContainer
argument_list|>
argument_list|(
name|launchedContainers
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getReservedContainer ()
specifier|public
specifier|synchronized
name|RMContainer
name|getReservedContainer
parameter_list|()
block|{
return|return
name|reservedContainer
return|;
block|}
specifier|protected
specifier|synchronized
name|void
DECL|method|setReservedContainer (RMContainer reservedContainer)
name|setReservedContainer
parameter_list|(
name|RMContainer
name|reservedContainer
parameter_list|)
block|{
name|this
operator|.
name|reservedContainer
operator|=
name|reservedContainer
expr_stmt|;
block|}
comment|/**    * Apply delta resource on node's available resource.    *     * @param deltaResource    *          the delta of resource need to apply to node    */
specifier|public
specifier|synchronized
name|void
DECL|method|applyDeltaOnAvailableResource (Resource deltaResource)
name|applyDeltaOnAvailableResource
parameter_list|(
name|Resource
name|deltaResource
parameter_list|)
block|{
comment|// we can only adjust available resource if total resource is changed.
name|Resources
operator|.
name|addTo
argument_list|(
name|this
operator|.
name|availableResource
argument_list|,
name|deltaResource
argument_list|)
expr_stmt|;
block|}
DECL|method|recoverContainer (RMContainer rmContainer)
specifier|public
specifier|synchronized
name|void
name|recoverContainer
parameter_list|(
name|RMContainer
name|rmContainer
parameter_list|)
block|{
if|if
condition|(
name|rmContainer
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|RMContainerState
operator|.
name|COMPLETED
argument_list|)
condition|)
block|{
return|return;
block|}
name|allocateContainer
argument_list|(
name|rmContainer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

