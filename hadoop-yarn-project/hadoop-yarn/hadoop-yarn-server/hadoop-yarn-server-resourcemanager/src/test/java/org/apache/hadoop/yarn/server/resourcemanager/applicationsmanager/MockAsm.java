begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.applicationsmanager
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
name|applicationsmanager
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Set
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
name|ipc
operator|.
name|CallerContext
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
name|MockApps
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
name|ApplicationReport
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
name|ApplicationResourceUsageReport
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
name|api
operator|.
name|records
operator|.
name|ApplicationTimeoutType
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
name|CollectorInfo
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
name|FinalApplicationStatus
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
name|LogAggregationStatus
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
name|NodeUpdateType
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
name|ReservationId
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
name|YarnApplicationState
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
name|api
operator|.
name|protocolrecords
operator|.
name|LogAggregationReport
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
name|api
operator|.
name|records
operator|.
name|AppCollectorData
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
name|ApplicationPlacementContext
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
name|RMAppEvent
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
name|RMAppMetrics
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
name|RMAppState
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
name|Records
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
name|Lists
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|MockAsm
specifier|public
specifier|abstract
class|class
name|MockAsm
extends|extends
name|MockApps
block|{
DECL|class|ApplicationBase
specifier|public
specifier|static
class|class
name|ApplicationBase
implements|implements
name|RMApp
block|{
DECL|field|amReqs
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|amReqs
decl_stmt|;
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getApplicationSubmissionContext ()
specifier|public
name|ApplicationSubmissionContext
name|getApplicationSubmissionContext
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getSubmitTime ()
specifier|public
name|long
name|getSubmitTime
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getLaunchTime ()
specifier|public
name|long
name|getLaunchTime
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
name|StringBuilder
name|getDiagnostics
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getCollectorData ()
specifier|public
name|AppCollectorData
name|getCollectorData
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getCurrentAppAttempt ()
specifier|public
name|RMAppAttempt
name|getCurrentAppAttempt
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAppAttempts ()
specifier|public
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|RMAppAttempt
argument_list|>
name|getAppAttempts
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getRMAppAttempt (ApplicationAttemptId appAttemptId)
specifier|public
name|RMAppAttempt
name|getRMAppAttempt
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|RMAppState
name|getState
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getTrackingUrl ()
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getOriginalTrackingUrl ()
specifier|public
name|String
name|getOriginalTrackingUrl
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getMaxAppAttempts ()
specifier|public
name|int
name|getMaxAppAttempts
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|createAndGetApplicationReport ( String clientUserName,boolean allowAccess)
specifier|public
name|ApplicationReport
name|createAndGetApplicationReport
parameter_list|(
name|String
name|clientUserName
parameter_list|,
name|boolean
name|allowAccess
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|handle (RMAppEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|RMAppEvent
name|event
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getFinalApplicationStatus ()
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|pullRMNodeUpdates (Map<RMNode, NodeUpdateType> updatedNodes)
specifier|public
name|int
name|pullRMNodeUpdates
parameter_list|(
name|Map
argument_list|<
name|RMNode
argument_list|,
name|NodeUpdateType
argument_list|>
name|updatedNodes
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getApplicationType ()
specifier|public
name|String
name|getApplicationType
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getApplicationTags ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getApplicationTags
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setQueue (String name)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|isAppFinalStateStored ()
specifier|public
name|boolean
name|isAppFinalStateStored
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|createApplicationState ()
specifier|public
name|YarnApplicationState
name|createApplicationState
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getRanNodes ()
specifier|public
name|Set
argument_list|<
name|NodeId
argument_list|>
name|getRanNodes
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getRMAppMetrics ()
specifier|public
name|RMAppMetrics
name|getRMAppMetrics
parameter_list|()
block|{
return|return
operator|new
name|RMAppMetrics
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getReservationId ()
specifier|public
name|ReservationId
name|getReservationId
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAMResourceRequests ()
specifier|public
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|getAMResourceRequests
parameter_list|()
block|{
return|return
name|this
operator|.
name|amReqs
return|;
block|}
annotation|@
name|Override
DECL|method|getLogAggregationReportsForApp ()
specifier|public
name|Map
argument_list|<
name|NodeId
argument_list|,
name|LogAggregationReport
argument_list|>
name|getLogAggregationReportsForApp
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getLogAggregationStatusForAppReport ()
specifier|public
name|LogAggregationStatus
name|getLogAggregationStatusForAppReport
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAmNodeLabelExpression ()
specifier|public
name|String
name|getAmNodeLabelExpression
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAppNodeLabelExpression ()
specifier|public
name|String
name|getAppNodeLabelExpression
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
DECL|method|getCallerContext ()
specifier|public
name|CallerContext
name|getCallerContext
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getApplicationTimeouts ()
specifier|public
name|Map
argument_list|<
name|ApplicationTimeoutType
argument_list|,
name|Long
argument_list|>
name|getApplicationTimeouts
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getApplicationPriority ()
specifier|public
name|Priority
name|getApplicationPriority
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|isAppInCompletedStates ()
specifier|public
name|boolean
name|isAppInCompletedStates
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getApplicationPlacementContext ()
specifier|public
name|ApplicationPlacementContext
name|getApplicationPlacementContext
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getCollectorInfo ()
specifier|public
name|CollectorInfo
name|getCollectorInfo
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getApplicationSchedulingEnvs ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getApplicationSchedulingEnvs
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
block|}
DECL|method|newApplication (int i)
specifier|public
specifier|static
name|RMApp
name|newApplication
parameter_list|(
name|int
name|i
parameter_list|)
block|{
specifier|final
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|newAppID
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Container
name|masterContainer
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|masterContainer
operator|.
name|setId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|masterContainer
operator|.
name|setNodeHttpAddress
argument_list|(
literal|"node:port"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|user
init|=
name|newUserName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|newAppName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|queue
init|=
name|newQueue
argument_list|()
decl_stmt|;
specifier|final
name|long
name|start
init|=
literal|123456
operator|+
name|i
operator|*
literal|1000
decl_stmt|;
specifier|final
name|long
name|launch
init|=
name|start
operator|+
name|i
operator|*
literal|100
decl_stmt|;
specifier|final
name|long
name|finish
init|=
literal|234567
operator|+
name|i
operator|*
literal|1000
decl_stmt|;
specifier|final
name|String
name|type
init|=
name|YarnConfiguration
operator|.
name|DEFAULT_APPLICATION_TYPE
decl_stmt|;
name|YarnApplicationState
index|[]
name|allStates
init|=
name|YarnApplicationState
operator|.
name|values
argument_list|()
decl_stmt|;
specifier|final
name|YarnApplicationState
name|state
init|=
name|allStates
index|[
name|i
operator|%
name|allStates
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|int
name|maxAppAttempts
init|=
name|i
operator|%
literal|1000
decl_stmt|;
return|return
operator|new
name|ApplicationBase
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getApplicationType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|start
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLaunchTime
parameter_list|()
block|{
return|return
name|launch
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
return|return
name|finish
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|YarnApplicationState
name|createApplicationState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|StringBuilder
name|getDiagnostics
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
operator|(
name|float
operator|)
name|Math
operator|.
name|random
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
block|{
return|return
name|FinalApplicationStatus
operator|.
name|UNDEFINED
return|;
block|}
annotation|@
name|Override
specifier|public
name|RMAppAttempt
name|getCurrentAppAttempt
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxAppAttempts
parameter_list|()
block|{
return|return
name|maxAppAttempts
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getApplicationTags
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ApplicationReport
name|createAndGetApplicationReport
parameter_list|(
name|String
name|clientUserName
parameter_list|,
name|boolean
name|allowAccess
parameter_list|)
block|{
name|ApplicationResourceUsageReport
name|usageReport
init|=
name|ApplicationResourceUsageReport
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|ApplicationReport
name|report
init|=
name|ApplicationReport
operator|.
name|newInstance
argument_list|(
name|getApplicationId
argument_list|()
argument_list|,
name|appAttemptId
argument_list|,
name|getUser
argument_list|()
argument_list|,
name|getQueue
argument_list|()
argument_list|,
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|getDiagnostics
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|getTrackingUrl
argument_list|()
argument_list|,
name|getLaunchTime
argument_list|()
argument_list|,
name|getStartTime
argument_list|()
argument_list|,
name|getFinishTime
argument_list|()
argument_list|,
name|getFinalApplicationStatus
argument_list|()
argument_list|,
name|usageReport
argument_list|,
literal|null
argument_list|,
name|getProgress
argument_list|()
argument_list|,
name|type
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|report
return|;
block|}
block|}
return|;
block|}
DECL|method|newApplications (int n)
specifier|public
specifier|static
name|List
argument_list|<
name|RMApp
argument_list|>
name|newApplications
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|List
argument_list|<
name|RMApp
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
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
name|n
condition|;
operator|++
name|i
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|newApplication
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

