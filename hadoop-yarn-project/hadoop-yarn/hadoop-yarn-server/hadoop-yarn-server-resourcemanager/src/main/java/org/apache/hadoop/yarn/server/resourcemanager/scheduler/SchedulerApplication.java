begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppState
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SchedulerApplication
specifier|public
class|class
name|SchedulerApplication
parameter_list|<
name|T
extends|extends
name|SchedulerApplicationAttempt
parameter_list|>
block|{
DECL|field|queue
specifier|private
name|Queue
name|queue
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|currentAttempt
specifier|private
name|T
name|currentAttempt
decl_stmt|;
DECL|field|priority
specifier|private
specifier|volatile
name|Priority
name|priority
decl_stmt|;
DECL|method|SchedulerApplication (Queue queue, String user)
specifier|public
name|SchedulerApplication
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|priority
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|SchedulerApplication (Queue queue, String user, Priority priority)
specifier|public
name|SchedulerApplication
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|String
name|user
parameter_list|,
name|Priority
name|priority
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
DECL|method|getQueue ()
specifier|public
name|Queue
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
DECL|method|setQueue (Queue queue)
specifier|public
name|void
name|setQueue
parameter_list|(
name|Queue
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
DECL|method|getCurrentAppAttempt ()
specifier|public
name|T
name|getCurrentAppAttempt
parameter_list|()
block|{
return|return
name|currentAttempt
return|;
block|}
DECL|method|setCurrentAppAttempt (T currentAttempt)
specifier|public
name|void
name|setCurrentAppAttempt
parameter_list|(
name|T
name|currentAttempt
parameter_list|)
block|{
name|this
operator|.
name|currentAttempt
operator|=
name|currentAttempt
expr_stmt|;
block|}
DECL|method|stop (RMAppState rmAppFinalState)
specifier|public
name|void
name|stop
parameter_list|(
name|RMAppState
name|rmAppFinalState
parameter_list|)
block|{
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|finishApp
argument_list|(
name|user
argument_list|,
name|rmAppFinalState
argument_list|)
expr_stmt|;
block|}
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
DECL|method|setPriority (Priority priority)
specifier|public
name|void
name|setPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
comment|// Also set priority in current running attempt
if|if
condition|(
literal|null
operator|!=
name|currentAttempt
condition|)
block|{
name|currentAttempt
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

