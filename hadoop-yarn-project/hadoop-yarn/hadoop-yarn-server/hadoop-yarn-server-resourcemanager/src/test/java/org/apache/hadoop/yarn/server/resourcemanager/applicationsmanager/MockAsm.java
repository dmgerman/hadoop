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
name|Collection
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
DECL|method|createAndGetApplicationReport (boolean allowAccess)
specifier|public
name|ApplicationReport
name|createAndGetApplicationReport
parameter_list|(
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
DECL|method|pullRMNodeUpdates (Collection<RMNode> updatedNodes)
specifier|public
name|int
name|pullRMNodeUpdates
parameter_list|(
name|Collection
argument_list|<
name|RMNode
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
name|newAppAttemptID
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
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerId
operator|.
name|setApplicationAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
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
name|RMAppState
index|[]
name|allStates
init|=
name|RMAppState
operator|.
name|values
argument_list|()
decl_stmt|;
specifier|final
name|RMAppState
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
name|RMAppState
name|getState
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

