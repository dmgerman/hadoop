begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.rm.preemption
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|preemption
package|;
end_package

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
name|mapreduce
operator|.
name|JobCounter
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
name|mapreduce
operator|.
name|checkpoint
operator|.
name|TaskCheckpointID
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskId
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|JobCounterUpdateEvent
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptEvent
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptEventType
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
name|PreemptionContainer
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
name|PreemptionContract
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
name|PreemptionMessage
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
name|StrictPreemptionContract
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
name|event
operator|.
name|EventHandler
import|;
end_import

begin_comment
comment|/**  * Sample policy that aggressively kills tasks when requested.  */
end_comment

begin_class
DECL|class|KillAMPreemptionPolicy
specifier|public
class|class
name|KillAMPreemptionPolicy
implements|implements
name|AMPreemptionPolicy
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
name|KillAMPreemptionPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|field|dispatcher
specifier|private
name|EventHandler
name|dispatcher
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init (AppContext context)
specifier|public
name|void
name|init
parameter_list|(
name|AppContext
name|context
parameter_list|)
block|{
name|dispatcher
operator|=
name|context
operator|.
name|getEventHandler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|preempt (Context ctxt, PreemptionMessage preemptionRequests)
specifier|public
name|void
name|preempt
parameter_list|(
name|Context
name|ctxt
parameter_list|,
name|PreemptionMessage
name|preemptionRequests
parameter_list|)
block|{
comment|// for both strict and negotiable preemption requests kill the
comment|// container
name|StrictPreemptionContract
name|strictContract
init|=
name|preemptionRequests
operator|.
name|getStrictContract
argument_list|()
decl_stmt|;
if|if
condition|(
name|strictContract
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PreemptionContainer
name|c
range|:
name|strictContract
operator|.
name|getContainers
argument_list|()
control|)
block|{
name|killContainer
argument_list|(
name|ctxt
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|PreemptionContract
name|contract
init|=
name|preemptionRequests
operator|.
name|getContract
argument_list|()
decl_stmt|;
if|if
condition|(
name|contract
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PreemptionContainer
name|c
range|:
name|contract
operator|.
name|getContainers
argument_list|()
control|)
block|{
name|killContainer
argument_list|(
name|ctxt
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|killContainer (Context ctxt, PreemptionContainer c)
specifier|private
name|void
name|killContainer
parameter_list|(
name|Context
name|ctxt
parameter_list|,
name|PreemptionContainer
name|c
parameter_list|)
block|{
name|ContainerId
name|reqCont
init|=
name|c
operator|.
name|getId
argument_list|()
decl_stmt|;
name|TaskAttemptId
name|reqTask
init|=
name|ctxt
operator|.
name|getTaskAttempt
argument_list|(
name|reqCont
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Evicting "
operator|+
name|reqTask
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|reqTask
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_KILL
argument_list|)
argument_list|)
expr_stmt|;
comment|// add preemption to counters
name|JobCounterUpdateEvent
name|jce
init|=
operator|new
name|JobCounterUpdateEvent
argument_list|(
name|reqTask
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
name|jce
operator|.
name|addCounterUpdate
argument_list|(
name|JobCounter
operator|.
name|TASKS_REQ_PREEMPT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|handle
argument_list|(
name|jce
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleFailedContainer (TaskAttemptId attemptID)
specifier|public
name|void
name|handleFailedContainer
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
comment|// ignore
block|}
annotation|@
name|Override
DECL|method|isPreempted (TaskAttemptId yarnAttemptID)
specifier|public
name|boolean
name|isPreempted
parameter_list|(
name|TaskAttemptId
name|yarnAttemptID
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|reportSuccessfulPreemption (TaskAttemptId taskAttemptID)
specifier|public
name|void
name|reportSuccessfulPreemption
parameter_list|(
name|TaskAttemptId
name|taskAttemptID
parameter_list|)
block|{
comment|// ignore
block|}
annotation|@
name|Override
DECL|method|getCheckpointID (TaskId taskId)
specifier|public
name|TaskCheckpointID
name|getCheckpointID
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setCheckpointID (TaskId taskId, TaskCheckpointID cid)
specifier|public
name|void
name|setCheckpointID
parameter_list|(
name|TaskId
name|taskId
parameter_list|,
name|TaskCheckpointID
name|cid
parameter_list|)
block|{
comment|// ignore
block|}
annotation|@
name|Override
DECL|method|handleCompletedContainer (TaskAttemptId attemptID)
specifier|public
name|void
name|handleCompletedContainer
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
comment|// ignore
block|}
block|}
end_class

end_unit

