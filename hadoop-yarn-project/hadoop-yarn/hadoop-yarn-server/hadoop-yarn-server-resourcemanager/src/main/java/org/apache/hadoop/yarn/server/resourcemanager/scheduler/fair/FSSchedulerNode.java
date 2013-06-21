begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|fair
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
name|ApplicationAttemptId
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|SchedulerNode
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|FSSchedulerNode
specifier|public
class|class
name|FSSchedulerNode
extends|extends
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
name|FSSchedulerNode
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|availableResource
specifier|private
name|Resource
name|availableResource
decl_stmt|;
DECL|field|usedResource
specifier|private
name|Resource
name|usedResource
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|numContainers
specifier|private
specifier|volatile
name|int
name|numContainers
decl_stmt|;
DECL|field|reservedContainer
specifier|private
name|RMContainer
name|reservedContainer
decl_stmt|;
DECL|field|reservedAppSchedulable
specifier|private
name|AppSchedulable
name|reservedAppSchedulable
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
DECL|method|FSSchedulerNode (RMNode node)
specifier|public
name|FSSchedulerNode
parameter_list|(
name|RMNode
name|node
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
block|}
DECL|method|getRMNode ()
specifier|public
name|RMNode
name|getRMNode
parameter_list|()
block|{
return|return
name|rmNode
return|;
block|}
DECL|method|getNodeID ()
specifier|public
name|NodeId
name|getNodeID
parameter_list|()
block|{
return|return
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
name|rmNode
operator|.
name|getHttpAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|rmNode
operator|.
name|getHostName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRackName ()
specifier|public
name|String
name|getRackName
parameter_list|()
block|{
return|return
name|rmNode
operator|.
name|getRackName
argument_list|()
return|;
block|}
comment|/**    * The Scheduler has allocated containers on this node to the     * given application.    *     * @param applicationId application    * @param rmContainer allocated container    */
DECL|method|allocateContainer (ApplicationId applicationId, RMContainer rmContainer)
specifier|public
specifier|synchronized
name|void
name|allocateContainer
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAvailableResource ()
specifier|public
specifier|synchronized
name|Resource
name|getAvailableResource
parameter_list|()
block|{
return|return
name|availableResource
return|;
block|}
annotation|@
name|Override
DECL|method|getUsedResource ()
specifier|public
specifier|synchronized
name|Resource
name|getUsedResource
parameter_list|()
block|{
return|return
name|usedResource
return|;
block|}
DECL|method|isValidContainer (Container c)
specifier|private
specifier|synchronized
name|boolean
name|isValidContainer
parameter_list|(
name|Container
name|c
parameter_list|)
block|{
if|if
condition|(
name|launchedContainers
operator|.
name|containsKey
argument_list|(
name|c
operator|.
name|getId
argument_list|()
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
comment|/**    * Release an allocated container on this node.    * @param container container to be released    */
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
name|launchedContainers
operator|.
name|remove
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|updateResource
argument_list|(
name|container
argument_list|)
expr_stmt|;
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
operator|+
literal|" used="
operator|+
name|getUsedResource
argument_list|()
return|;
block|}
annotation|@
name|Override
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
DECL|method|reserveResource ( FSSchedulerApp application, Priority priority, RMContainer reservedContainer)
specifier|public
specifier|synchronized
name|void
name|reserveResource
parameter_list|(
name|FSSchedulerApp
name|application
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|RMContainer
name|reservedContainer
parameter_list|)
block|{
comment|// Check if it's already reserved
if|if
condition|(
name|this
operator|.
name|reservedContainer
operator|!=
literal|null
condition|)
block|{
comment|// Sanity check
if|if
condition|(
operator|!
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to reserve"
operator|+
literal|" container "
operator|+
name|reservedContainer
operator|+
literal|" on node "
operator|+
name|reservedContainer
operator|.
name|getReservedNode
argument_list|()
operator|+
literal|" when currently"
operator|+
literal|" reserved resource "
operator|+
name|this
operator|.
name|reservedContainer
operator|+
literal|" on node "
operator|+
name|this
operator|.
name|reservedContainer
operator|.
name|getReservedNode
argument_list|()
argument_list|)
throw|;
block|}
comment|// Cannot reserve more than one application on a given node!
if|if
condition|(
operator|!
name|this
operator|.
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|equals
argument_list|(
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to reserve"
operator|+
literal|" container "
operator|+
name|reservedContainer
operator|+
literal|" for application "
operator|+
name|application
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" when currently"
operator|+
literal|" reserved container "
operator|+
name|this
operator|.
name|reservedContainer
operator|+
literal|" on node "
operator|+
name|this
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Updated reserved container "
operator|+
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" on node "
operator|+
name|this
operator|+
literal|" for application "
operator|+
name|application
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Reserved container "
operator|+
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" on node "
operator|+
name|this
operator|+
literal|" for application "
operator|+
name|application
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|reservedContainer
operator|=
name|reservedContainer
expr_stmt|;
name|this
operator|.
name|reservedAppSchedulable
operator|=
name|application
operator|.
name|getAppSchedulable
argument_list|()
expr_stmt|;
block|}
DECL|method|unreserveResource ( FSSchedulerApp application)
specifier|public
specifier|synchronized
name|void
name|unreserveResource
parameter_list|(
name|FSSchedulerApp
name|application
parameter_list|)
block|{
comment|// Cannot unreserve for wrong application...
name|ApplicationAttemptId
name|reservedApplication
init|=
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|reservedApplication
operator|.
name|equals
argument_list|(
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to unreserve "
operator|+
literal|" for application "
operator|+
name|application
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" when currently reserved "
operator|+
literal|" for application "
operator|+
name|reservedApplication
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" on node "
operator|+
name|this
argument_list|)
throw|;
block|}
name|this
operator|.
name|reservedContainer
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|reservedAppSchedulable
operator|=
literal|null
expr_stmt|;
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
DECL|method|getReservedAppSchedulable ()
specifier|public
specifier|synchronized
name|AppSchedulable
name|getReservedAppSchedulable
parameter_list|()
block|{
return|return
name|reservedAppSchedulable
return|;
block|}
block|}
end_class

end_unit

