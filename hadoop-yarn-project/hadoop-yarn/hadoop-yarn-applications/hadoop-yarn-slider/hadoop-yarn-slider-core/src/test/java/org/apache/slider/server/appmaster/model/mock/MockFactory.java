begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.model.mock
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
name|model
operator|.
name|mock
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
name|ContainerState
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
name|ContainerStatus
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
name|NodeReport
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
name|NodeState
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
name|impl
operator|.
name|pb
operator|.
name|NodeReportPBImpl
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
name|client
operator|.
name|api
operator|.
name|AMRMClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ResourceKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Application
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|PlacementPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|ProviderRole
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
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ResourceKeys
operator|.
name|COMPONENT_PLACEMENT_POLICY
import|;
end_import

begin_comment
comment|/**  * Factory for creating things.  */
end_comment

begin_class
DECL|class|MockFactory
specifier|public
class|class
name|MockFactory
implements|implements
name|MockRoles
block|{
DECL|field|NODE_FAILURE_THRESHOLD
specifier|public
specifier|static
specifier|final
name|int
name|NODE_FAILURE_THRESHOLD
init|=
literal|2
decl_stmt|;
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|MockFactory
name|INSTANCE
init|=
operator|new
name|MockFactory
argument_list|()
decl_stmt|;
comment|/**    * Basic role.    */
DECL|field|PROVIDER_ROLE0
specifier|public
specifier|static
specifier|final
name|ProviderRole
name|PROVIDER_ROLE0
init|=
operator|new
name|ProviderRole
argument_list|(
name|MockRoles
operator|.
name|ROLE0
argument_list|,
literal|0
argument_list|,
name|PlacementPolicy
operator|.
name|DEFAULT
argument_list|,
name|NODE_FAILURE_THRESHOLD
argument_list|,
literal|1
argument_list|,
name|ResourceKeys
operator|.
name|DEF_YARN_LABEL_EXPRESSION
argument_list|)
decl_stmt|;
comment|/**    * role 1 is strict. timeout should be irrelevant; same as failures    */
DECL|field|PROVIDER_ROLE1
specifier|public
specifier|static
specifier|final
name|ProviderRole
name|PROVIDER_ROLE1
init|=
operator|new
name|ProviderRole
argument_list|(
name|MockRoles
operator|.
name|ROLE1
argument_list|,
literal|1
argument_list|,
name|PlacementPolicy
operator|.
name|STRICT
argument_list|,
name|NODE_FAILURE_THRESHOLD
argument_list|,
literal|1
argument_list|,
name|ResourceKeys
operator|.
name|DEF_YARN_LABEL_EXPRESSION
argument_list|)
decl_stmt|;
comment|/**    * role 2: longer delay.    */
DECL|field|PROVIDER_ROLE2
specifier|public
specifier|static
specifier|final
name|ProviderRole
name|PROVIDER_ROLE2
init|=
operator|new
name|ProviderRole
argument_list|(
name|MockRoles
operator|.
name|ROLE2
argument_list|,
literal|2
argument_list|,
name|PlacementPolicy
operator|.
name|ANYWHERE
argument_list|,
name|NODE_FAILURE_THRESHOLD
argument_list|,
literal|2
argument_list|,
name|ResourceKeys
operator|.
name|DEF_YARN_LABEL_EXPRESSION
argument_list|)
decl_stmt|;
comment|/**    * Patch up a "role2" role to have anti-affinity set.    */
DECL|field|AAROLE_2
specifier|public
specifier|static
specifier|final
name|ProviderRole
name|AAROLE_2
init|=
operator|new
name|ProviderRole
argument_list|(
name|MockRoles
operator|.
name|ROLE2
argument_list|,
literal|2
argument_list|,
name|PlacementPolicy
operator|.
name|ANTI_AFFINITY_REQUIRED
argument_list|,
name|NODE_FAILURE_THRESHOLD
argument_list|,
literal|2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**    * Patch up a "role1" role to have anti-affinity set and GPI as the label.    */
DECL|field|AAROLE_1_GPU
specifier|public
specifier|static
specifier|final
name|ProviderRole
name|AAROLE_1_GPU
init|=
operator|new
name|ProviderRole
argument_list|(
name|MockRoles
operator|.
name|ROLE1
argument_list|,
literal|1
argument_list|,
name|PlacementPolicy
operator|.
name|ANTI_AFFINITY_REQUIRED
argument_list|,
name|NODE_FAILURE_THRESHOLD
argument_list|,
literal|1
argument_list|,
name|MockRoles
operator|.
name|LABEL_GPU
argument_list|)
decl_stmt|;
DECL|field|appIdCount
specifier|private
name|int
name|appIdCount
decl_stmt|;
DECL|field|attemptIdCount
specifier|private
name|int
name|attemptIdCount
decl_stmt|;
DECL|field|containerIdCount
specifier|private
name|int
name|containerIdCount
decl_stmt|;
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
init|=
name|newAppId
argument_list|()
decl_stmt|;
DECL|field|attemptId
specifier|private
name|ApplicationAttemptId
name|attemptId
init|=
name|newApplicationAttemptId
argument_list|(
name|appId
argument_list|)
decl_stmt|;
comment|/**    * List of roles.    */
DECL|field|ROLES
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|ProviderRole
argument_list|>
name|ROLES
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|PROVIDER_ROLE0
argument_list|,
name|PROVIDER_ROLE1
argument_list|,
name|PROVIDER_ROLE2
argument_list|)
decl_stmt|;
DECL|field|ROLE_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|ROLE_COUNT
init|=
name|ROLES
operator|.
name|size
argument_list|()
decl_stmt|;
DECL|method|newContainerId ()
name|MockContainerId
name|newContainerId
parameter_list|()
block|{
return|return
name|newContainerId
argument_list|(
name|attemptId
argument_list|)
return|;
block|}
DECL|method|newContainerId (ApplicationAttemptId attemptId0)
name|MockContainerId
name|newContainerId
parameter_list|(
name|ApplicationAttemptId
name|attemptId0
parameter_list|)
block|{
name|MockContainerId
name|cid
init|=
operator|new
name|MockContainerId
argument_list|(
name|attemptId0
argument_list|,
name|containerIdCount
operator|++
argument_list|)
decl_stmt|;
return|return
name|cid
return|;
block|}
DECL|method|newApplicationAttemptId (ApplicationId appId0)
name|MockApplicationAttemptId
name|newApplicationAttemptId
parameter_list|(
name|ApplicationId
name|appId0
parameter_list|)
block|{
name|MockApplicationAttemptId
name|id
init|=
operator|new
name|MockApplicationAttemptId
argument_list|(
name|appId0
argument_list|,
name|attemptIdCount
operator|++
argument_list|)
decl_stmt|;
return|return
name|id
return|;
block|}
DECL|method|newAppId ()
name|MockApplicationId
name|newAppId
parameter_list|()
block|{
name|MockApplicationId
name|id
init|=
operator|new
name|MockApplicationId
argument_list|()
decl_stmt|;
name|id
operator|.
name|setId
argument_list|(
name|appIdCount
operator|++
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
DECL|method|newNodeId (String host)
specifier|public
name|MockNodeId
name|newNodeId
parameter_list|(
name|String
name|host
parameter_list|)
block|{
return|return
operator|new
name|MockNodeId
argument_list|(
name|host
argument_list|)
return|;
block|}
DECL|method|newContainer (ContainerId cid)
name|MockContainer
name|newContainer
parameter_list|(
name|ContainerId
name|cid
parameter_list|)
block|{
name|MockContainer
name|c
init|=
operator|new
name|MockContainer
argument_list|()
decl_stmt|;
name|c
operator|.
name|setId
argument_list|(
name|cid
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|newContainer ()
specifier|public
name|MockContainer
name|newContainer
parameter_list|()
block|{
return|return
name|newContainer
argument_list|(
name|newContainerId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|newContainer (NodeId nodeId, Priority priority)
specifier|public
name|MockContainer
name|newContainer
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|Priority
name|priority
parameter_list|)
block|{
name|MockContainer
name|container
init|=
name|newContainer
argument_list|(
name|newContainerId
argument_list|()
argument_list|)
decl_stmt|;
name|container
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|container
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
comment|/**    * Build a new container  using the request to supply priority and resource.    * @param req request    * @param host hostname to assign to    * @return the container    */
DECL|method|newContainer (AMRMClient.ContainerRequest req, String host)
specifier|public
name|MockContainer
name|newContainer
parameter_list|(
name|AMRMClient
operator|.
name|ContainerRequest
name|req
parameter_list|,
name|String
name|host
parameter_list|)
block|{
name|MockContainer
name|container
init|=
name|newContainer
argument_list|(
name|newContainerId
argument_list|()
argument_list|)
decl_stmt|;
name|container
operator|.
name|setResource
argument_list|(
name|req
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setPriority
argument_list|(
name|req
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setNodeId
argument_list|(
operator|new
name|MockNodeId
argument_list|(
name|host
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
comment|/**    * Create a new instance with the given components definined in the    * resources section.    * @param r1    * @param r2    * @param r3    * @return    */
DECL|method|newApplication (long r1, long r2, long r3)
specifier|public
name|Application
name|newApplication
parameter_list|(
name|long
name|r1
parameter_list|,
name|long
name|r2
parameter_list|,
name|long
name|r3
parameter_list|)
block|{
name|Application
name|application
init|=
operator|new
name|Application
argument_list|()
decl_stmt|;
name|application
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|ResourceKeys
operator|.
name|NODE_FAILURE_THRESHOLD
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|NODE_FAILURE_THRESHOLD
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Component
argument_list|>
name|components
init|=
name|application
operator|.
name|getComponents
argument_list|()
decl_stmt|;
name|Component
name|c1
init|=
operator|new
name|Component
argument_list|()
operator|.
name|name
argument_list|(
name|ROLE0
argument_list|)
operator|.
name|numberOfContainers
argument_list|(
name|r1
argument_list|)
decl_stmt|;
name|c1
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|COMPONENT_PLACEMENT_POLICY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|PlacementPolicy
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|Component
name|c2
init|=
operator|new
name|Component
argument_list|()
operator|.
name|name
argument_list|(
name|ROLE1
argument_list|)
operator|.
name|numberOfContainers
argument_list|(
name|r2
argument_list|)
decl_stmt|;
name|c2
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|COMPONENT_PLACEMENT_POLICY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|PlacementPolicy
operator|.
name|STRICT
argument_list|)
argument_list|)
expr_stmt|;
name|Component
name|c3
init|=
operator|new
name|Component
argument_list|()
operator|.
name|name
argument_list|(
name|ROLE2
argument_list|)
operator|.
name|numberOfContainers
argument_list|(
name|r3
argument_list|)
decl_stmt|;
name|c3
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|COMPONENT_PLACEMENT_POLICY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|PlacementPolicy
operator|.
name|ANYWHERE
argument_list|)
argument_list|)
expr_stmt|;
name|components
operator|.
name|add
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|components
operator|.
name|add
argument_list|(
name|c2
argument_list|)
expr_stmt|;
name|components
operator|.
name|add
argument_list|(
name|c3
argument_list|)
expr_stmt|;
return|return
name|application
return|;
block|}
DECL|method|newResource (int memory, int vcores)
specifier|public
name|MockResource
name|newResource
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|vcores
parameter_list|)
block|{
return|return
operator|new
name|MockResource
argument_list|(
name|memory
argument_list|,
name|vcores
argument_list|)
return|;
block|}
DECL|method|newContainerStatus ()
name|ContainerStatus
name|newContainerStatus
parameter_list|()
block|{
return|return
name|newContainerStatus
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|newContainerStatus (ContainerId containerId, ContainerState containerState, String diagnostics, int exitStatus)
name|ContainerStatus
name|newContainerStatus
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|ContainerState
name|containerState
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|int
name|exitStatus
parameter_list|)
block|{
return|return
name|ContainerStatus
operator|.
name|newInstance
argument_list|(
name|containerId
argument_list|,
name|containerState
argument_list|,
name|diagnostics
argument_list|,
name|exitStatus
argument_list|)
return|;
block|}
comment|/**    * Create a single instance.    * @param hostname    * @param nodeState    * @param label    */
DECL|method|newNodeReport (String hostname, NodeState nodeState, String label)
specifier|public
name|NodeReport
name|newNodeReport
parameter_list|(
name|String
name|hostname
parameter_list|,
name|NodeState
name|nodeState
parameter_list|,
name|String
name|label
parameter_list|)
block|{
name|NodeId
name|nodeId
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
name|hostname
argument_list|,
literal|80
argument_list|)
decl_stmt|;
name|Integer
operator|.
name|valueOf
argument_list|(
name|hostname
argument_list|,
literal|16
argument_list|)
expr_stmt|;
return|return
name|newNodeReport
argument_list|(
name|hostname
argument_list|,
name|nodeId
argument_list|,
name|nodeState
argument_list|,
name|label
argument_list|)
return|;
block|}
DECL|method|newNodeReport ( String hostname, NodeId nodeId, NodeState nodeState, String label)
name|NodeReport
name|newNodeReport
parameter_list|(
name|String
name|hostname
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|NodeState
name|nodeState
parameter_list|,
name|String
name|label
parameter_list|)
block|{
name|NodeReport
name|report
init|=
operator|new
name|NodeReportPBImpl
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|nodeLabels
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|nodeLabels
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|report
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|report
operator|.
name|setNodeLabels
argument_list|(
name|nodeLabels
argument_list|)
expr_stmt|;
name|report
operator|.
name|setNodeState
argument_list|(
name|nodeState
argument_list|)
expr_stmt|;
name|report
operator|.
name|setHttpAddress
argument_list|(
literal|"http$hostname:80"
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
comment|/**    * Create a list of instances -one for each hostname.    * @param hostnames hosts    * @return    */
DECL|method|createNodeReports ( List<String> hostnames, NodeState nodeState, String label)
specifier|public
name|List
argument_list|<
name|NodeReport
argument_list|>
name|createNodeReports
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|hostnames
parameter_list|,
name|NodeState
name|nodeState
parameter_list|,
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|nodeState
operator|==
literal|null
condition|)
block|{
name|nodeState
operator|=
name|NodeState
operator|.
name|RUNNING
expr_stmt|;
block|}
name|List
argument_list|<
name|NodeReport
argument_list|>
name|reports
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|hostnames
control|)
block|{
name|reports
operator|.
name|add
argument_list|(
name|newNodeReport
argument_list|(
name|name
argument_list|,
name|nodeState
argument_list|,
name|label
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|reports
return|;
block|}
block|}
end_class

end_unit

