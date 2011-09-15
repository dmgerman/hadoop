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
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|LinkedHashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
operator|.
name|ReadLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
operator|.
name|WriteLock
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
name|YarnException
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
name|ApplicationState
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
name|event
operator|.
name|Dispatcher
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
name|ApplicationMasterService
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
name|RMContext
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
name|RMAppManagerEvent
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
name|RMAppManagerEventType
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
name|AMLivelinessMonitor
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
name|RMAppAttemptEvent
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
name|RMAppAttemptEventType
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
name|RMAppAttemptImpl
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
name|RMNodeCleanAppEvent
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
name|YarnScheduler
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
name|state
operator|.
name|InvalidStateTransitonException
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
name|state
operator|.
name|MultipleArcTransition
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
name|state
operator|.
name|SingleArcTransition
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
name|state
operator|.
name|StateMachine
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
name|state
operator|.
name|StateMachineFactory
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
name|BuilderUtils
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

begin_class
DECL|class|RMAppImpl
specifier|public
class|class
name|RMAppImpl
implements|implements
name|RMApp
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
name|RMAppImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Immutable fields
DECL|field|applicationId
specifier|private
specifier|final
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|String
name|queue
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|submissionContext
specifier|private
specifier|final
name|ApplicationSubmissionContext
name|submissionContext
decl_stmt|;
DECL|field|clientTokenStr
specifier|private
specifier|final
name|String
name|clientTokenStr
decl_stmt|;
DECL|field|appStore
specifier|private
specifier|final
name|ApplicationStore
name|appStore
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|YarnScheduler
name|scheduler
decl_stmt|;
DECL|field|masterService
specifier|private
specifier|final
name|ApplicationMasterService
name|masterService
decl_stmt|;
DECL|field|diagnostics
specifier|private
specifier|final
name|StringBuilder
name|diagnostics
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|maxRetries
specifier|private
specifier|final
name|int
name|maxRetries
decl_stmt|;
DECL|field|readLock
specifier|private
specifier|final
name|ReadLock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|private
specifier|final
name|WriteLock
name|writeLock
decl_stmt|;
DECL|field|attempts
specifier|private
specifier|final
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|RMAppAttempt
argument_list|>
name|attempts
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|RMAppAttempt
argument_list|>
argument_list|()
decl_stmt|;
comment|// Mutable fields
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|finishTime
specifier|private
name|long
name|finishTime
decl_stmt|;
DECL|field|currentAttempt
specifier|private
name|RMAppAttempt
name|currentAttempt
decl_stmt|;
DECL|field|FINAL_TRANSITION
specifier|private
specifier|static
specifier|final
name|FinalTransition
name|FINAL_TRANSITION
init|=
operator|new
name|FinalTransition
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|StateMachineFactory
argument_list|<
name|RMAppImpl
argument_list|,
name|RMAppState
argument_list|,
name|RMAppEventType
argument_list|,
DECL|field|stateMachineFactory
name|RMAppEvent
argument_list|>
name|stateMachineFactory
init|=
operator|new
name|StateMachineFactory
argument_list|<
name|RMAppImpl
argument_list|,
name|RMAppState
argument_list|,
name|RMAppEventType
argument_list|,
name|RMAppEvent
argument_list|>
argument_list|(
name|RMAppState
operator|.
name|NEW
argument_list|)
comment|// TODO - ATTEMPT_KILLED not sent right now but should handle if
comment|// attempt starts sending
comment|// Transitions from NEW state
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|NEW
argument_list|,
name|RMAppState
operator|.
name|SUBMITTED
argument_list|,
name|RMAppEventType
operator|.
name|START
argument_list|,
operator|new
name|StartAppAttemptTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|NEW
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|,
operator|new
name|AppKilledTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|NEW
argument_list|,
name|RMAppState
operator|.
name|FAILED
argument_list|,
name|RMAppEventType
operator|.
name|APP_REJECTED
argument_list|,
operator|new
name|AppRejectedTransition
argument_list|()
argument_list|)
comment|// Transitions from SUBMITTED state
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|SUBMITTED
argument_list|,
name|RMAppState
operator|.
name|FAILED
argument_list|,
name|RMAppEventType
operator|.
name|APP_REJECTED
argument_list|,
operator|new
name|AppRejectedTransition
argument_list|()
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|SUBMITTED
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|,
name|RMAppEventType
operator|.
name|APP_ACCEPTED
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|SUBMITTED
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|,
operator|new
name|AppKilledTransition
argument_list|()
argument_list|)
comment|// Transitions from ACCEPTED state
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|ACCEPTED
argument_list|,
name|RMAppState
operator|.
name|RUNNING
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_REGISTERED
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|ACCEPTED
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|RMAppState
operator|.
name|SUBMITTED
argument_list|,
name|RMAppState
operator|.
name|FAILED
argument_list|)
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FAILED
argument_list|,
operator|new
name|AttemptFailedTransition
argument_list|(
name|RMAppState
operator|.
name|SUBMITTED
argument_list|)
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|ACCEPTED
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|,
operator|new
name|AppKilledTransition
argument_list|()
argument_list|)
comment|// Transitions from RUNNING state
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|RUNNING
argument_list|,
name|RMAppState
operator|.
name|FINISHED
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FINISHED
argument_list|,
name|FINAL_TRANSITION
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|RUNNING
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|RMAppState
operator|.
name|SUBMITTED
argument_list|,
name|RMAppState
operator|.
name|FAILED
argument_list|)
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FAILED
argument_list|,
operator|new
name|AttemptFailedTransition
argument_list|(
name|RMAppState
operator|.
name|SUBMITTED
argument_list|)
argument_list|)
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|RUNNING
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|,
operator|new
name|AppKilledTransition
argument_list|()
argument_list|)
comment|// Transitions from FINISHED state
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|FINISHED
argument_list|,
name|RMAppState
operator|.
name|FINISHED
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
comment|// Transitions from FAILED state
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|FAILED
argument_list|,
name|RMAppState
operator|.
name|FAILED
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
comment|// Transitions from KILLED state
operator|.
name|addTransition
argument_list|(
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|RMAppEventType
operator|.
name|KILL
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FINISHED
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FAILED
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_KILLED
argument_list|)
argument_list|)
operator|.
name|installTopology
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|StateMachine
argument_list|<
name|RMAppState
argument_list|,
name|RMAppEventType
argument_list|,
name|RMAppEvent
argument_list|>
DECL|field|stateMachine
name|stateMachine
decl_stmt|;
DECL|method|RMAppImpl (ApplicationId applicationId, RMContext rmContext, Configuration config, String name, String user, String queue, ApplicationSubmissionContext submissionContext, String clientTokenStr, ApplicationStore appStore, YarnScheduler scheduler, ApplicationMasterService masterService)
specifier|public
name|RMAppImpl
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|RMContext
name|rmContext
parameter_list|,
name|Configuration
name|config
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|,
name|ApplicationSubmissionContext
name|submissionContext
parameter_list|,
name|String
name|clientTokenStr
parameter_list|,
name|ApplicationStore
name|appStore
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|,
name|ApplicationMasterService
name|masterService
parameter_list|)
block|{
name|this
operator|.
name|applicationId
operator|=
name|applicationId
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
name|rmContext
operator|.
name|getDispatcher
argument_list|()
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|submissionContext
operator|=
name|submissionContext
expr_stmt|;
name|this
operator|.
name|clientTokenStr
operator|=
name|clientTokenStr
expr_stmt|;
name|this
operator|.
name|appStore
operator|=
name|appStore
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|masterService
operator|=
name|masterService
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxRetries
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_RETRIES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_RETRIES
argument_list|)
expr_stmt|;
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|this
operator|.
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|stateMachine
operator|=
name|stateMachineFactory
operator|.
name|make
argument_list|(
name|this
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
name|this
operator|.
name|applicationId
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
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|stateMachine
operator|.
name|getCurrentState
argument_list|()
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
name|this
operator|.
name|user
return|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|this
operator|.
name|currentAttempt
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|currentAttempt
operator|.
name|getProgress
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|attempts
operator|.
name|get
argument_list|(
name|appAttemptId
argument_list|)
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
name|this
operator|.
name|queue
return|;
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
name|this
operator|.
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|getCurrentAppAttempt ()
specifier|public
name|RMAppAttempt
name|getCurrentAppAttempt
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|currentAttempt
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getApplicationStore ()
specifier|public
name|ApplicationStore
name|getApplicationStore
parameter_list|()
block|{
return|return
name|this
operator|.
name|appStore
return|;
block|}
DECL|method|createApplicationState (RMAppState rmAppState)
specifier|private
name|ApplicationState
name|createApplicationState
parameter_list|(
name|RMAppState
name|rmAppState
parameter_list|)
block|{
switch|switch
condition|(
name|rmAppState
condition|)
block|{
case|case
name|NEW
case|:
return|return
name|ApplicationState
operator|.
name|NEW
return|;
case|case
name|SUBMITTED
case|:
case|case
name|ACCEPTED
case|:
return|return
name|ApplicationState
operator|.
name|SUBMITTED
return|;
case|case
name|RUNNING
case|:
return|return
name|ApplicationState
operator|.
name|RUNNING
return|;
case|case
name|FINISHED
case|:
return|return
name|ApplicationState
operator|.
name|SUCCEEDED
return|;
case|case
name|KILLED
case|:
return|return
name|ApplicationState
operator|.
name|KILLED
return|;
case|case
name|FAILED
case|:
return|return
name|ApplicationState
operator|.
name|FAILED
return|;
block|}
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Unknown state passed!"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|createAndGetApplicationReport ()
specifier|public
name|ApplicationReport
name|createAndGetApplicationReport
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|String
name|clientToken
init|=
literal|"N/A"
decl_stmt|;
name|String
name|trackingUrl
init|=
literal|"N/A"
decl_stmt|;
name|String
name|host
init|=
literal|"N/A"
decl_stmt|;
name|int
name|rpcPort
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|currentAttempt
operator|!=
literal|null
condition|)
block|{
name|trackingUrl
operator|=
name|this
operator|.
name|currentAttempt
operator|.
name|getTrackingUrl
argument_list|()
expr_stmt|;
name|clientToken
operator|=
name|this
operator|.
name|currentAttempt
operator|.
name|getClientToken
argument_list|()
expr_stmt|;
name|host
operator|=
name|this
operator|.
name|currentAttempt
operator|.
name|getHost
argument_list|()
expr_stmt|;
name|rpcPort
operator|=
name|this
operator|.
name|currentAttempt
operator|.
name|getRpcPort
argument_list|()
expr_stmt|;
block|}
return|return
name|BuilderUtils
operator|.
name|newApplicationReport
argument_list|(
name|this
operator|.
name|applicationId
argument_list|,
name|this
operator|.
name|user
argument_list|,
name|this
operator|.
name|queue
argument_list|,
name|this
operator|.
name|name
argument_list|,
name|host
argument_list|,
name|rpcPort
argument_list|,
name|clientToken
argument_list|,
name|createApplicationState
argument_list|(
name|this
operator|.
name|stateMachine
operator|.
name|getCurrentState
argument_list|()
argument_list|)
argument_list|,
name|this
operator|.
name|diagnostics
operator|.
name|toString
argument_list|()
argument_list|,
name|trackingUrl
argument_list|,
name|this
operator|.
name|startTime
argument_list|)
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|finishTime
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTrackingUrl ()
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|this
operator|.
name|currentAttempt
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|currentAttempt
operator|.
name|getTrackingUrl
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
name|StringBuilder
name|getDiagnostics
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|diagnostics
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
name|this
operator|.
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ApplicationId
name|appID
init|=
name|event
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Processing event for "
operator|+
name|appID
operator|+
literal|" of type "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|RMAppState
name|oldState
init|=
name|getState
argument_list|()
decl_stmt|;
try|try
block|{
comment|/* keep the master in sync with the state machine */
name|this
operator|.
name|stateMachine
operator|.
name|doTransition
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidStateTransitonException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't handle this event at current state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|/* TODO fail the application on the failed transition */
block|}
if|if
condition|(
name|oldState
operator|!=
name|getState
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|appID
operator|+
literal|" State change from "
operator|+
name|oldState
operator|+
literal|" to "
operator|+
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createNewAttempt ()
specifier|private
name|void
name|createNewAttempt
parameter_list|()
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appAttemptId
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|appAttemptId
operator|.
name|setAttemptId
argument_list|(
name|attempts
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|RMAppAttempt
name|attempt
init|=
operator|new
name|RMAppAttemptImpl
argument_list|(
name|appAttemptId
argument_list|,
name|clientTokenStr
argument_list|,
name|rmContext
argument_list|,
name|scheduler
argument_list|,
name|masterService
argument_list|,
name|submissionContext
argument_list|)
decl_stmt|;
name|attempts
operator|.
name|put
argument_list|(
name|appAttemptId
argument_list|,
name|attempt
argument_list|)
expr_stmt|;
name|currentAttempt
operator|=
name|attempt
expr_stmt|;
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptEvent
argument_list|(
name|appAttemptId
argument_list|,
name|RMAppAttemptEventType
operator|.
name|START
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|RMAppTransition
specifier|private
specifier|static
class|class
name|RMAppTransition
implements|implements
name|SingleArcTransition
argument_list|<
name|RMAppImpl
argument_list|,
name|RMAppEvent
argument_list|>
block|{
DECL|method|transition (RMAppImpl app, RMAppEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMAppImpl
name|app
parameter_list|,
name|RMAppEvent
name|event
parameter_list|)
block|{     }
empty_stmt|;
block|}
DECL|class|StartAppAttemptTransition
specifier|private
specifier|static
specifier|final
class|class
name|StartAppAttemptTransition
extends|extends
name|RMAppTransition
block|{
DECL|method|transition (RMAppImpl app, RMAppEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMAppImpl
name|app
parameter_list|,
name|RMAppEvent
name|event
parameter_list|)
block|{
name|app
operator|.
name|createNewAttempt
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
block|}
DECL|class|AppKilledTransition
specifier|private
specifier|static
specifier|final
class|class
name|AppKilledTransition
extends|extends
name|FinalTransition
block|{
DECL|method|transition (RMAppImpl app, RMAppEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMAppImpl
name|app
parameter_list|,
name|RMAppEvent
name|event
parameter_list|)
block|{
name|app
operator|.
name|diagnostics
operator|.
name|append
argument_list|(
literal|"Application killed by user."
argument_list|)
expr_stmt|;
name|super
operator|.
name|transition
argument_list|(
name|app
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
DECL|class|AppRejectedTransition
specifier|private
specifier|static
specifier|final
class|class
name|AppRejectedTransition
extends|extends
name|FinalTransition
block|{
DECL|method|transition (RMAppImpl app, RMAppEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMAppImpl
name|app
parameter_list|,
name|RMAppEvent
name|event
parameter_list|)
block|{
name|RMAppRejectedEvent
name|rejectedEvent
init|=
operator|(
name|RMAppRejectedEvent
operator|)
name|event
decl_stmt|;
name|app
operator|.
name|diagnostics
operator|.
name|append
argument_list|(
name|rejectedEvent
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|transition
argument_list|(
name|app
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
DECL|class|FinalTransition
specifier|private
specifier|static
class|class
name|FinalTransition
extends|extends
name|RMAppTransition
block|{
DECL|method|getNodesOnWhichAttemptRan (RMAppImpl app)
specifier|private
name|Set
argument_list|<
name|NodeId
argument_list|>
name|getNodesOnWhichAttemptRan
parameter_list|(
name|RMAppImpl
name|app
parameter_list|)
block|{
name|Set
argument_list|<
name|NodeId
argument_list|>
name|nodes
init|=
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RMAppAttempt
name|attempt
range|:
name|app
operator|.
name|attempts
operator|.
name|values
argument_list|()
control|)
block|{
name|nodes
operator|.
name|addAll
argument_list|(
name|attempt
operator|.
name|getRanNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|transition (RMAppImpl app, RMAppEvent event)
specifier|public
name|void
name|transition
parameter_list|(
name|RMAppImpl
name|app
parameter_list|,
name|RMAppEvent
name|event
parameter_list|)
block|{
name|Set
argument_list|<
name|NodeId
argument_list|>
name|nodes
init|=
name|getNodesOnWhichAttemptRan
argument_list|(
name|app
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeId
name|nodeId
range|:
name|nodes
control|)
block|{
name|app
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeCleanAppEvent
argument_list|(
name|nodeId
argument_list|,
name|app
operator|.
name|applicationId
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|app
operator|.
name|finishTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|app
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppManagerEvent
argument_list|(
name|app
operator|.
name|applicationId
argument_list|,
name|RMAppManagerEventType
operator|.
name|APP_COMPLETED
argument_list|)
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
DECL|class|AttemptFailedTransition
specifier|private
specifier|static
specifier|final
class|class
name|AttemptFailedTransition
implements|implements
name|MultipleArcTransition
argument_list|<
name|RMAppImpl
argument_list|,
name|RMAppEvent
argument_list|,
name|RMAppState
argument_list|>
block|{
DECL|field|initialState
specifier|private
specifier|final
name|RMAppState
name|initialState
decl_stmt|;
DECL|method|AttemptFailedTransition (RMAppState initialState)
specifier|public
name|AttemptFailedTransition
parameter_list|(
name|RMAppState
name|initialState
parameter_list|)
block|{
name|this
operator|.
name|initialState
operator|=
name|initialState
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|transition (RMAppImpl app, RMAppEvent event)
specifier|public
name|RMAppState
name|transition
parameter_list|(
name|RMAppImpl
name|app
parameter_list|,
name|RMAppEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|app
operator|.
name|attempts
operator|.
name|size
argument_list|()
operator|==
name|app
operator|.
name|maxRetries
condition|)
block|{
name|String
name|msg
init|=
literal|"Application "
operator|+
name|app
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" failed "
operator|+
name|app
operator|.
name|maxRetries
operator|+
literal|" times. Failing the application."
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|app
operator|.
name|diagnostics
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|// Inform the node for app-finish
name|FINAL_TRANSITION
operator|.
name|transition
argument_list|(
name|app
argument_list|,
name|event
argument_list|)
expr_stmt|;
return|return
name|RMAppState
operator|.
name|FAILED
return|;
block|}
name|app
operator|.
name|createNewAttempt
argument_list|()
expr_stmt|;
return|return
name|initialState
return|;
block|}
block|}
block|}
end_class

end_unit

