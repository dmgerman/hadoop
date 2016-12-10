begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|security
operator|.
name|token
operator|.
name|JobTokenSecretManager
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
name|JobId
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
name|TaskAttemptFinishingMonitor
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|preemption
operator|.
name|CheckpointAMPreemptionPolicy
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
name|rm
operator|.
name|RMHeartbeatHandler
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
name|util
operator|.
name|MRBuilderUtils
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
name|Event
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
name|SystemClock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|TestTaskAttemptFinishingMonitor
specifier|public
class|class
name|TestTaskAttemptFinishingMonitor
block|{
annotation|@
name|Test
DECL|method|testFinshingAttemptTimeout ()
specifier|public
name|void
name|testFinshingAttemptTimeout
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|SystemClock
name|clock
init|=
name|SystemClock
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|TASK_EXIT_TIMEOUT
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|TASK_EXIT_TIMEOUT_CHECK_INTERVAL_MS
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|AppContext
name|appCtx
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|JobTokenSecretManager
name|secret
init|=
name|mock
argument_list|(
name|JobTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|RMHeartbeatHandler
name|rmHeartbeatHandler
init|=
name|mock
argument_list|(
name|RMHeartbeatHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|MockEventHandler
name|eventHandler
init|=
operator|new
name|MockEventHandler
argument_list|()
decl_stmt|;
name|TaskAttemptFinishingMonitor
name|taskAttemptFinishingMonitor
init|=
operator|new
name|TaskAttemptFinishingMonitor
argument_list|(
name|eventHandler
argument_list|)
decl_stmt|;
name|taskAttemptFinishingMonitor
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|taskAttemptFinishingMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|appCtx
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|eventHandler
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|appCtx
operator|.
name|getNMHostname
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"0.0.0.0"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|appCtx
operator|.
name|getTaskAttemptFinishingMonitor
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|taskAttemptFinishingMonitor
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|appCtx
operator|.
name|getClock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|CheckpointAMPreemptionPolicy
name|policy
init|=
operator|new
name|CheckpointAMPreemptionPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|init
argument_list|(
name|appCtx
argument_list|)
expr_stmt|;
name|TaskAttemptListenerImpl
name|listener
init|=
operator|new
name|TaskAttemptListenerImpl
argument_list|(
name|appCtx
argument_list|,
name|secret
argument_list|,
name|rmHeartbeatHandler
argument_list|,
name|policy
argument_list|)
decl_stmt|;
name|listener
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|listener
operator|.
name|start
argument_list|()
expr_stmt|;
name|JobId
name|jid
init|=
name|MRBuilderUtils
operator|.
name|newJobId
argument_list|(
literal|12345
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TaskId
name|tid
init|=
name|MRBuilderUtils
operator|.
name|newTaskId
argument_list|(
name|jid
argument_list|,
literal|0
argument_list|,
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
name|TaskType
operator|.
name|MAP
argument_list|)
decl_stmt|;
name|TaskAttemptId
name|attemptId
init|=
name|MRBuilderUtils
operator|.
name|newTaskAttemptId
argument_list|(
name|tid
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|appCtx
operator|.
name|getTaskAttemptFinishingMonitor
argument_list|()
operator|.
name|register
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|int
name|check
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|eventHandler
operator|.
name|timedOut
operator|&&
name|check
operator|++
operator|<
literal|10
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|taskAttemptFinishingMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Finishing attempt didn't time out."
argument_list|,
name|eventHandler
operator|.
name|timedOut
argument_list|)
expr_stmt|;
block|}
DECL|class|MockEventHandler
specifier|public
specifier|static
class|class
name|MockEventHandler
implements|implements
name|EventHandler
argument_list|<
name|Event
argument_list|>
block|{
DECL|field|timedOut
specifier|public
name|boolean
name|timedOut
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|handle (Event event)
specifier|public
name|void
name|handle
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|instanceof
name|TaskAttemptEvent
condition|)
block|{
name|TaskAttemptEvent
name|attemptEvent
init|=
operator|(
operator|(
name|TaskAttemptEvent
operator|)
name|event
operator|)
decl_stmt|;
if|if
condition|(
name|TaskAttemptEventType
operator|.
name|TA_TIMED_OUT
operator|==
name|attemptEvent
operator|.
name|getType
argument_list|()
condition|)
block|{
name|timedOut
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit

