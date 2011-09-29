begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp
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
name|rmapp
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
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|ApplicationsStore
operator|.
name|ApplicationStore
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

begin_class
DECL|class|MockRMApp
specifier|public
class|class
name|MockRMApp
implements|implements
name|RMApp
block|{
DECL|field|DT
specifier|static
specifier|final
name|int
name|DT
init|=
literal|1000000
decl_stmt|;
comment|// ms
DECL|field|user
name|String
name|user
init|=
name|MockApps
operator|.
name|newUserName
argument_list|()
decl_stmt|;
DECL|field|name
name|String
name|name
init|=
name|MockApps
operator|.
name|newAppName
argument_list|()
decl_stmt|;
DECL|field|queue
name|String
name|queue
init|=
name|MockApps
operator|.
name|newQueue
argument_list|()
decl_stmt|;
DECL|field|start
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|random
argument_list|()
operator|*
name|DT
argument_list|)
decl_stmt|;
DECL|field|finish
name|long
name|finish
init|=
literal|0
decl_stmt|;
DECL|field|state
name|RMAppState
name|state
init|=
name|RMAppState
operator|.
name|NEW
decl_stmt|;
DECL|field|failCount
name|int
name|failCount
init|=
literal|0
decl_stmt|;
DECL|field|id
name|ApplicationId
name|id
decl_stmt|;
DECL|field|url
name|String
name|url
init|=
literal|null
decl_stmt|;
DECL|field|diagnostics
name|StringBuilder
name|diagnostics
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|attempt
name|RMAppAttempt
name|attempt
decl_stmt|;
DECL|method|MockRMApp (int newid, long time, RMAppState newState)
specifier|public
name|MockRMApp
parameter_list|(
name|int
name|newid
parameter_list|,
name|long
name|time
parameter_list|,
name|RMAppState
name|newState
parameter_list|)
block|{
name|finish
operator|=
name|time
expr_stmt|;
name|id
operator|=
name|MockApps
operator|.
name|newAppID
argument_list|(
name|newid
argument_list|)
expr_stmt|;
name|state
operator|=
name|newState
expr_stmt|;
block|}
DECL|method|MockRMApp (int newid, long time, RMAppState newState, String userName)
specifier|public
name|MockRMApp
parameter_list|(
name|int
name|newid
parameter_list|,
name|long
name|time
parameter_list|,
name|RMAppState
name|newState
parameter_list|,
name|String
name|userName
parameter_list|)
block|{
name|this
argument_list|(
name|newid
argument_list|,
name|time
argument_list|,
name|newState
argument_list|)
expr_stmt|;
name|user
operator|=
name|userName
expr_stmt|;
block|}
DECL|method|MockRMApp (int newid, long time, RMAppState newState, String userName, String diag)
specifier|public
name|MockRMApp
parameter_list|(
name|int
name|newid
parameter_list|,
name|long
name|time
parameter_list|,
name|RMAppState
name|newState
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|diag
parameter_list|)
block|{
name|this
argument_list|(
name|newid
argument_list|,
name|time
argument_list|,
name|newState
argument_list|,
name|userName
argument_list|)
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|=
operator|new
name|StringBuilder
argument_list|(
name|diag
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|RMAppState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|setState (RMAppState state)
specifier|public
name|void
name|setState
parameter_list|(
name|RMAppState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|setUser (String user)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
operator|(
name|float
operator|)
literal|0.0
return|;
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
DECL|method|setQueue (String queue)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCurrentAppAttempt ()
specifier|public
name|RMAppAttempt
name|getCurrentAppAttempt
parameter_list|()
block|{
return|return
name|attempt
return|;
block|}
DECL|method|setCurrentAppAttempt (RMAppAttempt attempt)
specifier|public
name|void
name|setCurrentAppAttempt
parameter_list|(
name|RMAppAttempt
name|attempt
parameter_list|)
block|{
name|this
operator|.
name|attempt
operator|=
name|attempt
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAndGetApplicationReport ()
specifier|public
name|ApplicationReport
name|createAndGetApplicationReport
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
DECL|method|getApplicationStore ()
specifier|public
name|ApplicationStore
name|getApplicationStore
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
return|return
name|finish
return|;
block|}
DECL|method|setFinishTime (long time)
specifier|public
name|void
name|setFinishTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|finish
operator|=
name|time
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|start
return|;
block|}
DECL|method|setStartTime (long time)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|time
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTrackingUrl ()
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
return|return
name|url
return|;
block|}
DECL|method|setTrackingUrl (String url)
specifier|public
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
name|StringBuilder
name|getDiagnostics
parameter_list|()
block|{
return|return
name|diagnostics
return|;
block|}
DECL|method|setDiagnostics (String diag)
specifier|public
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diag
parameter_list|)
block|{
name|this
operator|.
name|diagnostics
operator|=
operator|new
name|StringBuilder
argument_list|(
name|diag
argument_list|)
expr_stmt|;
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
block|{   }
annotation|@
name|Override
DECL|method|getAMFinalState ()
specifier|public
name|String
name|getAMFinalState
parameter_list|()
block|{
return|return
literal|"UNKNOWN"
return|;
block|}
empty_stmt|;
block|}
end_class

end_unit

