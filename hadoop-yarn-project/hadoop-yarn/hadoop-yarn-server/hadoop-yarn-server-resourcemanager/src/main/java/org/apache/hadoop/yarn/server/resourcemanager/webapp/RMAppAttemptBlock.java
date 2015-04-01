begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
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
name|webapp
package|;
end_package

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
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|_EVEN
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
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|_INFO_WRAP
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
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|_ODD
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
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|_TH
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
name|ApplicationAttemptReport
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
name|ContainerReport
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
name|api
operator|.
name|records
operator|.
name|ResourceRequest
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
name|YarnApplicationAttemptState
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
name|ResourceManager
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
name|rmapp
operator|.
name|RMApp
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttempt
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptMetrics
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
name|AbstractYarnScheduler
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
name|SchedulerApplicationAttempt
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
name|AppInfo
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
name|webapp
operator|.
name|AppAttemptBlock
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
name|webapp
operator|.
name|dao
operator|.
name|AppAttemptInfo
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
name|hamlet
operator|.
name|Hamlet
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
name|hamlet
operator|.
name|Hamlet
operator|.
name|DIV
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
name|hamlet
operator|.
name|Hamlet
operator|.
name|TABLE
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
name|hamlet
operator|.
name|Hamlet
operator|.
name|TBODY
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
name|util
operator|.
name|WebAppUtils
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
name|view
operator|.
name|InfoBlock
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
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
name|Set
import|;
end_import

begin_class
DECL|class|RMAppAttemptBlock
specifier|public
class|class
name|RMAppAttemptBlock
extends|extends
name|AppAttemptBlock
block|{
DECL|field|rm
specifier|private
specifier|final
name|ResourceManager
name|rm
decl_stmt|;
DECL|field|conf
specifier|protected
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Inject
DECL|method|RMAppAttemptBlock (ViewContext ctx, ResourceManager rm, Configuration conf)
name|RMAppAttemptBlock
parameter_list|(
name|ViewContext
name|ctx
parameter_list|,
name|ResourceManager
name|rm
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|rm
operator|.
name|getClientRMService
argument_list|()
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|rm
operator|=
name|rm
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|super
operator|.
name|render
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|createContainerLocalityTable
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|createResourceRequestsTable
argument_list|(
name|html
argument_list|)
expr_stmt|;
block|}
DECL|method|createResourceRequestsTable (Block html)
specifier|private
name|void
name|createResourceRequestsTable
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|AppInfo
name|app
init|=
operator|new
name|AppInfo
argument_list|(
name|rm
argument_list|,
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|this
operator|.
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|,
name|WebAppUtils
operator|.
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|tbody
init|=
name|html
operator|.
name|table
argument_list|(
literal|"#ResourceRequests"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
literal|".priority"
argument_list|,
literal|"Priority"
argument_list|)
operator|.
name|th
argument_list|(
literal|".resourceName"
argument_list|,
literal|"ResourceName"
argument_list|)
operator|.
name|th
argument_list|(
literal|".totalResource"
argument_list|,
literal|"Capability"
argument_list|)
operator|.
name|th
argument_list|(
literal|".numContainers"
argument_list|,
literal|"NumContainers"
argument_list|)
operator|.
name|th
argument_list|(
literal|".relaxLocality"
argument_list|,
literal|"RelaxLocality"
argument_list|)
operator|.
name|th
argument_list|(
literal|".nodeLabelExpression"
argument_list|,
literal|"NodeLabelExpression"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tbody
argument_list|()
decl_stmt|;
name|Resource
name|totalResource
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
if|if
condition|(
name|app
operator|.
name|getResourceRequests
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ResourceRequest
name|request
range|:
name|app
operator|.
name|getResourceRequests
argument_list|()
control|)
block|{
if|if
condition|(
name|request
operator|.
name|getNumContainers
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|tbody
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|request
operator|.
name|getPriority
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|request
operator|.
name|getResourceName
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|request
operator|.
name|getNumContainers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|request
operator|.
name|getRelaxLocality
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|request
operator|.
name|getNodeLabelExpression
argument_list|()
operator|==
literal|null
condition|?
literal|"N/A"
else|:
name|request
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getResourceName
argument_list|()
operator|.
name|equals
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|totalResource
argument_list|,
name|Resources
operator|.
name|multiply
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|,
name|request
operator|.
name|getNumContainers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|html
operator|.
name|div
argument_list|()
operator|.
name|$class
argument_list|(
literal|"totalResourceRequests"
argument_list|)
operator|.
name|h3
argument_list|(
literal|"Total Outstanding Resource Requests: "
operator|+
name|totalResource
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|tbody
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
DECL|method|createContainerLocalityTable (Block html)
specifier|private
name|void
name|createContainerLocalityTable
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|RMAppAttemptMetrics
name|attemptMetrics
init|=
literal|null
decl_stmt|;
name|RMAppAttempt
name|attempt
init|=
name|getRMAppAttempt
argument_list|()
decl_stmt|;
if|if
condition|(
name|attempt
operator|!=
literal|null
condition|)
block|{
name|attemptMetrics
operator|=
name|attempt
operator|.
name|getRMAppAttemptMetrics
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|attemptMetrics
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|div
init|=
name|html
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
decl_stmt|;
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|table
init|=
name|div
operator|.
name|h3
argument_list|(
literal|"Total Allocated Containers: "
operator|+
name|attemptMetrics
operator|.
name|getTotalAllocatedContainers
argument_list|()
argument_list|)
operator|.
name|h3
argument_list|(
literal|"Each table cell"
operator|+
literal|" represents the number of NodeLocal/RackLocal/OffSwitch containers"
operator|+
literal|" satisfied by NodeLocal/RackLocal/OffSwitch resource requests."
argument_list|)
operator|.
name|table
argument_list|(
literal|"#containerLocality"
argument_list|)
decl_stmt|;
name|table
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|""
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Node Local Request"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Rack Local Request"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Off Switch Request"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|String
index|[]
name|containersType
init|=
block|{
literal|"Num Node Local Containers (satisfied by)"
block|,
literal|"Num Rack Local Containers (satisfied by)"
block|,
literal|"Num Off Switch Containers (satisfied by)"
block|}
decl_stmt|;
name|boolean
name|odd
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attemptMetrics
operator|.
name|getLocalityStatistics
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|table
operator|.
name|tr
argument_list|(
operator|(
name|odd
operator|=
operator|!
name|odd
operator|)
condition|?
name|_ODD
else|:
name|_EVEN
argument_list|)
operator|.
name|td
argument_list|(
name|containersType
index|[
name|i
index|]
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|attemptMetrics
operator|.
name|getLocalityStatistics
argument_list|()
index|[
name|i
index|]
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|i
operator|==
literal|0
condition|?
literal|""
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|attemptMetrics
operator|.
name|getLocalityStatistics
argument_list|()
index|[
name|i
index|]
index|[
literal|1
index|]
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|i
operator|<=
literal|1
condition|?
literal|""
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|attemptMetrics
operator|.
name|getLocalityStatistics
argument_list|()
index|[
name|i
index|]
index|[
literal|2
index|]
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|table
operator|.
name|_
argument_list|()
expr_stmt|;
name|div
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
DECL|method|isApplicationInFinalState (YarnApplicationAttemptState state)
specifier|private
name|boolean
name|isApplicationInFinalState
parameter_list|(
name|YarnApplicationAttemptState
name|state
parameter_list|)
block|{
return|return
name|state
operator|==
name|YarnApplicationAttemptState
operator|.
name|FINISHED
operator|||
name|state
operator|==
name|YarnApplicationAttemptState
operator|.
name|FAILED
operator|||
name|state
operator|==
name|YarnApplicationAttemptState
operator|.
name|KILLED
return|;
block|}
annotation|@
name|Override
DECL|method|createAttemptHeadRoomTable (Block html)
specifier|protected
name|void
name|createAttemptHeadRoomTable
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|RMAppAttempt
name|attempt
init|=
name|getRMAppAttempt
argument_list|()
decl_stmt|;
if|if
condition|(
name|attempt
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|isApplicationInFinalState
argument_list|(
name|YarnApplicationAttemptState
operator|.
name|valueOf
argument_list|(
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|pdiv
init|=
name|html
operator|.
name|_
argument_list|(
name|InfoBlock
operator|.
name|class
argument_list|)
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
decl_stmt|;
name|info
argument_list|(
literal|"Application Attempt Overview"
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
name|info
argument_list|(
literal|"Application Attempt Metrics"
argument_list|)
operator|.
name|_
argument_list|(
literal|"Application Attempt Headroom : "
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|pdiv
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getRMAppAttempt ()
specifier|private
name|RMAppAttempt
name|getRMAppAttempt
parameter_list|()
block|{
name|ApplicationId
name|appId
init|=
name|this
operator|.
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|RMAppAttempt
name|attempt
init|=
literal|null
decl_stmt|;
name|RMApp
name|rmApp
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmApp
operator|!=
literal|null
condition|)
block|{
name|attempt
operator|=
name|rmApp
operator|.
name|getAppAttempts
argument_list|()
operator|.
name|get
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
block|}
return|return
name|attempt
return|;
block|}
DECL|method|generateOverview (ApplicationAttemptReport appAttemptReport, Collection<ContainerReport> containers, AppAttemptInfo appAttempt, String node)
specifier|protected
name|void
name|generateOverview
parameter_list|(
name|ApplicationAttemptReport
name|appAttemptReport
parameter_list|,
name|Collection
argument_list|<
name|ContainerReport
argument_list|>
name|containers
parameter_list|,
name|AppAttemptInfo
name|appAttempt
parameter_list|,
name|String
name|node
parameter_list|)
block|{
name|String
name|blacklistedNodes
init|=
literal|"-"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|getBlacklistedNodes
argument_list|(
name|rm
argument_list|,
name|getRMAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|nodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|blacklistedNodes
operator|=
name|StringUtils
operator|.
name|join
argument_list|(
name|nodes
argument_list|,
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
name|info
argument_list|(
literal|"Application Attempt Overview"
argument_list|)
operator|.
name|_
argument_list|(
literal|"Application Attempt State:"
argument_list|,
name|appAttempt
operator|.
name|getAppAttemptState
argument_list|()
operator|==
literal|null
condition|?
name|UNAVAILABLE
else|:
name|appAttempt
operator|.
name|getAppAttemptState
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"AM Container:"
argument_list|,
name|appAttempt
operator|.
name|getAmContainerId
argument_list|()
operator|==
literal|null
operator|||
name|containers
operator|==
literal|null
operator|||
operator|!
name|hasAMContainer
argument_list|(
name|appAttemptReport
operator|.
name|getAMContainerId
argument_list|()
argument_list|,
name|containers
argument_list|)
condition|?
literal|null
else|:
name|root_url
argument_list|(
literal|"container"
argument_list|,
name|appAttempt
operator|.
name|getAmContainerId
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|appAttempt
operator|.
name|getAmContainerId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"Node:"
argument_list|,
name|node
argument_list|)
operator|.
name|_
argument_list|(
literal|"Tracking URL:"
argument_list|,
name|appAttempt
operator|.
name|getTrackingUrl
argument_list|()
operator|==
literal|null
operator|||
name|appAttempt
operator|.
name|getTrackingUrl
argument_list|()
operator|.
name|equals
argument_list|(
name|UNAVAILABLE
argument_list|)
condition|?
literal|null
else|:
name|root_url
argument_list|(
name|appAttempt
operator|.
name|getTrackingUrl
argument_list|()
argument_list|)
argument_list|,
name|appAttempt
operator|.
name|getTrackingUrl
argument_list|()
operator|==
literal|null
operator|||
name|appAttempt
operator|.
name|getTrackingUrl
argument_list|()
operator|.
name|equals
argument_list|(
name|UNAVAILABLE
argument_list|)
condition|?
literal|"Unassigned"
else|:
name|appAttempt
operator|.
name|getAppAttemptState
argument_list|()
operator|==
name|YarnApplicationAttemptState
operator|.
name|FINISHED
operator|||
name|appAttempt
operator|.
name|getAppAttemptState
argument_list|()
operator|==
name|YarnApplicationAttemptState
operator|.
name|FAILED
operator|||
name|appAttempt
operator|.
name|getAppAttemptState
argument_list|()
operator|==
name|YarnApplicationAttemptState
operator|.
name|KILLED
condition|?
literal|"History"
else|:
literal|"ApplicationMaster"
argument_list|)
operator|.
name|_
argument_list|(
literal|"Diagnostics Info:"
argument_list|,
name|appAttempt
operator|.
name|getDiagnosticsInfo
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|appAttempt
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Blacklisted Nodes:"
argument_list|,
name|blacklistedNodes
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlacklistedNodes (ResourceManager rm, ApplicationAttemptId appid)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getBlacklistedNodes
parameter_list|(
name|ResourceManager
name|rm
parameter_list|,
name|ApplicationAttemptId
name|appid
parameter_list|)
block|{
if|if
condition|(
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|instanceof
name|AbstractYarnScheduler
condition|)
block|{
name|AbstractYarnScheduler
name|ayScheduler
init|=
operator|(
name|AbstractYarnScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|SchedulerApplicationAttempt
name|attempt
init|=
name|ayScheduler
operator|.
name|getApplicationAttempt
argument_list|(
name|appid
argument_list|)
decl_stmt|;
if|if
condition|(
name|attempt
operator|!=
literal|null
condition|)
block|{
return|return
name|attempt
operator|.
name|getBlacklistedNodes
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

