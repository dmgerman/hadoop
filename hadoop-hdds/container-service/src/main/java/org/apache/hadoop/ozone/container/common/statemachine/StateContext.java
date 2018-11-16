begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.statemachine
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|GeneratedMessage
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
operator|.
name|Type
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|PipelineAction
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerAction
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|CommandStatus
operator|.
name|Status
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|DatanodeState
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|datanode
operator|.
name|InitDatanodeState
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|datanode
operator|.
name|RunningDatanodeState
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|CommandStatus
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|CommandStatus
operator|.
name|CommandStatusBuilder
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|DeleteBlockCommandStatus
operator|.
name|DeleteBlockCommandStatusBuilder
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|SCMCommand
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|min
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
name|hdds
operator|.
name|scm
operator|.
name|HddsServerUtil
operator|.
name|getScmHeartbeatInterval
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Queue
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
name|concurrent
operator|.
name|ExecutionException
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
name|ExecutorService
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
name|TimeUnit
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
name|TimeoutException
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
name|atomic
operator|.
name|AtomicLong
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
name|Lock
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
name|ReentrantLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  * Current Context of State Machine.  */
end_comment

begin_class
DECL|class|StateContext
specifier|public
class|class
name|StateContext
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|commandQueue
specifier|private
specifier|final
name|Queue
argument_list|<
name|SCMCommand
argument_list|>
name|commandQueue
decl_stmt|;
DECL|field|cmdStatusMap
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|CommandStatus
argument_list|>
name|cmdStatusMap
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
DECL|field|parent
specifier|private
specifier|final
name|DatanodeStateMachine
name|parent
decl_stmt|;
DECL|field|stateExecutionCount
specifier|private
specifier|final
name|AtomicLong
name|stateExecutionCount
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|reports
specifier|private
specifier|final
name|List
argument_list|<
name|GeneratedMessage
argument_list|>
name|reports
decl_stmt|;
DECL|field|containerActions
specifier|private
specifier|final
name|Queue
argument_list|<
name|ContainerAction
argument_list|>
name|containerActions
decl_stmt|;
DECL|field|pipelineActions
specifier|private
specifier|final
name|Queue
argument_list|<
name|PipelineAction
argument_list|>
name|pipelineActions
decl_stmt|;
DECL|field|state
specifier|private
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|state
decl_stmt|;
comment|/**    * Starting with a 2 sec heartbeat frequency which will be updated to the    * real HB frequency after scm registration. With this method the    * initial registration could be significant faster.    */
DECL|field|heartbeatFrequency
specifier|private
name|AtomicLong
name|heartbeatFrequency
init|=
operator|new
name|AtomicLong
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
comment|/**    * Constructs a StateContext.    *    * @param conf   - Configration    * @param state  - State    * @param parent Parent State Machine    */
DECL|method|StateContext (Configuration conf, DatanodeStateMachine.DatanodeStates state, DatanodeStateMachine parent)
specifier|public
name|StateContext
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|state
parameter_list|,
name|DatanodeStateMachine
name|parent
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|commandQueue
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|cmdStatusMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|reports
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|containerActions
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|pipelineActions
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|lock
operator|=
operator|new
name|ReentrantLock
argument_list|()
expr_stmt|;
name|stateExecutionCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the ContainerStateMachine class that holds this state.    *    * @return ContainerStateMachine.    */
DECL|method|getParent ()
specifier|public
name|DatanodeStateMachine
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/**    * Returns true if we are entering a new state.    *    * @return boolean    */
DECL|method|isEntering ()
name|boolean
name|isEntering
parameter_list|()
block|{
return|return
name|stateExecutionCount
operator|.
name|get
argument_list|()
operator|==
literal|0
return|;
block|}
comment|/**    * Returns true if we are exiting from the current state.    *    * @param newState - newState.    * @return boolean    */
DECL|method|isExiting (DatanodeStateMachine.DatanodeStates newState)
name|boolean
name|isExiting
parameter_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|newState
parameter_list|)
block|{
name|boolean
name|isExiting
init|=
name|state
operator|!=
name|newState
operator|&&
name|stateExecutionCount
operator|.
name|get
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|isExiting
condition|)
block|{
name|stateExecutionCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|isExiting
return|;
block|}
comment|/**    * Returns the current state the machine is in.    *    * @return state.    */
DECL|method|getState ()
specifier|public
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**    * Sets the current state of the machine.    *    * @param state state.    */
DECL|method|setState (DatanodeStateMachine.DatanodeStates state)
specifier|public
name|void
name|setState
parameter_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
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
comment|/**    * Adds the report to report queue.    *    * @param report report to be added    */
DECL|method|addReport (GeneratedMessage report)
specifier|public
name|void
name|addReport
parameter_list|(
name|GeneratedMessage
name|report
parameter_list|)
block|{
if|if
condition|(
name|report
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|reports
init|)
block|{
name|reports
operator|.
name|add
argument_list|(
name|report
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Adds the reports which could not be sent by heartbeat back to the    * reports list.    *    * @param reportsToPutBack list of reports which failed to be sent by    *                         heartbeat.    */
DECL|method|putBackReports (List<GeneratedMessage> reportsToPutBack)
specifier|public
name|void
name|putBackReports
parameter_list|(
name|List
argument_list|<
name|GeneratedMessage
argument_list|>
name|reportsToPutBack
parameter_list|)
block|{
synchronized|synchronized
init|(
name|reports
init|)
block|{
name|reports
operator|.
name|addAll
argument_list|(
literal|0
argument_list|,
name|reportsToPutBack
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns all the available reports from the report queue, or empty list if    * the queue is empty.    *    * @return List<reports>    */
DECL|method|getAllAvailableReports ()
specifier|public
name|List
argument_list|<
name|GeneratedMessage
argument_list|>
name|getAllAvailableReports
parameter_list|()
block|{
return|return
name|getReports
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|/**    * Returns available reports from the report queue with a max limit on    * list size, or empty list if the queue is empty.    *    * @return List<reports>    */
DECL|method|getReports (int maxLimit)
specifier|public
name|List
argument_list|<
name|GeneratedMessage
argument_list|>
name|getReports
parameter_list|(
name|int
name|maxLimit
parameter_list|)
block|{
name|List
argument_list|<
name|GeneratedMessage
argument_list|>
name|reportsToReturn
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|reports
init|)
block|{
name|List
argument_list|<
name|GeneratedMessage
argument_list|>
name|tempList
init|=
name|reports
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|min
argument_list|(
name|reports
operator|.
name|size
argument_list|()
argument_list|,
name|maxLimit
argument_list|)
argument_list|)
decl_stmt|;
name|reportsToReturn
operator|.
name|addAll
argument_list|(
name|tempList
argument_list|)
expr_stmt|;
name|tempList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|reportsToReturn
return|;
block|}
comment|/**    * Adds the ContainerAction to ContainerAction queue.    *    * @param containerAction ContainerAction to be added    */
DECL|method|addContainerAction (ContainerAction containerAction)
specifier|public
name|void
name|addContainerAction
parameter_list|(
name|ContainerAction
name|containerAction
parameter_list|)
block|{
synchronized|synchronized
init|(
name|containerActions
init|)
block|{
name|containerActions
operator|.
name|add
argument_list|(
name|containerAction
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add ContainerAction to ContainerAction queue if it's not present.    *    * @param containerAction ContainerAction to be added    */
DECL|method|addContainerActionIfAbsent (ContainerAction containerAction)
specifier|public
name|void
name|addContainerActionIfAbsent
parameter_list|(
name|ContainerAction
name|containerAction
parameter_list|)
block|{
synchronized|synchronized
init|(
name|containerActions
init|)
block|{
if|if
condition|(
operator|!
name|containerActions
operator|.
name|contains
argument_list|(
name|containerAction
argument_list|)
condition|)
block|{
name|containerActions
operator|.
name|add
argument_list|(
name|containerAction
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns all the pending ContainerActions from the ContainerAction queue,    * or empty list if the queue is empty.    *    * @return List<ContainerAction>    */
DECL|method|getAllPendingContainerActions ()
specifier|public
name|List
argument_list|<
name|ContainerAction
argument_list|>
name|getAllPendingContainerActions
parameter_list|()
block|{
return|return
name|getPendingContainerAction
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|/**    * Returns pending ContainerActions from the ContainerAction queue with a    * max limit on list size, or empty list if the queue is empty.    *    * @return List<ContainerAction>    */
DECL|method|getPendingContainerAction (int maxLimit)
specifier|public
name|List
argument_list|<
name|ContainerAction
argument_list|>
name|getPendingContainerAction
parameter_list|(
name|int
name|maxLimit
parameter_list|)
block|{
name|List
argument_list|<
name|ContainerAction
argument_list|>
name|containerActionList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|containerActions
init|)
block|{
if|if
condition|(
operator|!
name|containerActions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|size
init|=
name|containerActions
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
name|size
operator|>
name|maxLimit
condition|?
name|maxLimit
else|:
name|size
decl_stmt|;
for|for
control|(
name|int
name|count
init|=
literal|0
init|;
name|count
operator|<
name|limit
condition|;
name|count
operator|++
control|)
block|{
comment|// we need to remove the action from the containerAction queue
comment|// as well
name|ContainerAction
name|action
init|=
name|containerActions
operator|.
name|poll
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|action
argument_list|)
expr_stmt|;
name|containerActionList
operator|.
name|add
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|containerActionList
return|;
block|}
block|}
comment|/**    * Add PipelineAction to PipelineAction queue if it's not present.    *    * @param pipelineAction PipelineAction to be added    */
DECL|method|addPipelineActionIfAbsent (PipelineAction pipelineAction)
specifier|public
name|void
name|addPipelineActionIfAbsent
parameter_list|(
name|PipelineAction
name|pipelineAction
parameter_list|)
block|{
synchronized|synchronized
init|(
name|pipelineActions
init|)
block|{
comment|/**        * If pipelineAction queue already contains entry for the pipeline id        * with same action, we should just return.        * Note: We should not use pipelineActions.contains(pipelineAction) here        * as, pipelineAction has a msg string. So even if two msgs differ though        * action remains same on the given pipeline, it will end up adding it        * multiple times here.        */
for|for
control|(
name|PipelineAction
name|pipelineActionIter
range|:
name|pipelineActions
control|)
block|{
if|if
condition|(
name|pipelineActionIter
operator|.
name|getAction
argument_list|()
operator|==
name|pipelineAction
operator|.
name|getAction
argument_list|()
operator|&&
name|pipelineActionIter
operator|.
name|hasClosePipeline
argument_list|()
operator|&&
name|pipelineAction
operator|.
name|hasClosePipeline
argument_list|()
operator|&&
name|pipelineActionIter
operator|.
name|getClosePipeline
argument_list|()
operator|.
name|getPipelineID
argument_list|()
operator|.
name|equals
argument_list|(
name|pipelineAction
operator|.
name|getClosePipeline
argument_list|()
operator|.
name|getPipelineID
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|pipelineActions
operator|.
name|add
argument_list|(
name|pipelineAction
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns pending PipelineActions from the PipelineAction queue with a    * max limit on list size, or empty list if the queue is empty.    *    * @return List<ContainerAction>    */
DECL|method|getPendingPipelineAction (int maxLimit)
specifier|public
name|List
argument_list|<
name|PipelineAction
argument_list|>
name|getPendingPipelineAction
parameter_list|(
name|int
name|maxLimit
parameter_list|)
block|{
name|List
argument_list|<
name|PipelineAction
argument_list|>
name|pipelineActionList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|pipelineActions
init|)
block|{
if|if
condition|(
operator|!
name|pipelineActions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|size
init|=
name|pipelineActions
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
name|size
operator|>
name|maxLimit
condition|?
name|maxLimit
else|:
name|size
decl_stmt|;
for|for
control|(
name|int
name|count
init|=
literal|0
init|;
name|count
operator|<
name|limit
condition|;
name|count
operator|++
control|)
block|{
name|pipelineActionList
operator|.
name|add
argument_list|(
name|pipelineActions
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pipelineActionList
return|;
block|}
block|}
comment|/**    * Returns the next task to get executed by the datanode state machine.    * @return A callable that will be executed by the    * {@link DatanodeStateMachine}    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getTask ()
specifier|public
name|DatanodeState
argument_list|<
name|DatanodeStateMachine
operator|.
name|DatanodeStates
argument_list|>
name|getTask
parameter_list|()
block|{
switch|switch
condition|(
name|this
operator|.
name|state
condition|)
block|{
case|case
name|INIT
case|:
return|return
operator|new
name|InitDatanodeState
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|parent
operator|.
name|getConnectionManager
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
case|case
name|RUNNING
case|:
return|return
operator|new
name|RunningDatanodeState
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|parent
operator|.
name|getConnectionManager
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
case|case
name|SHUTDOWN
case|:
return|return
literal|null
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not Implemented yet."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Executes the required state function.    *    * @param service - Executor Service    * @param time    - seconds to wait    * @param unit    - Seconds.    * @throws InterruptedException    * @throws ExecutionException    * @throws TimeoutException    */
DECL|method|execute (ExecutorService service, long time, TimeUnit unit)
specifier|public
name|void
name|execute
parameter_list|(
name|ExecutorService
name|service
parameter_list|,
name|long
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
name|stateExecutionCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|DatanodeState
argument_list|<
name|DatanodeStateMachine
operator|.
name|DatanodeStates
argument_list|>
name|task
init|=
name|getTask
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|isEntering
argument_list|()
condition|)
block|{
name|task
operator|.
name|onEnter
argument_list|()
expr_stmt|;
block|}
name|task
operator|.
name|execute
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|newState
init|=
name|task
operator|.
name|await
argument_list|(
name|time
argument_list|,
name|unit
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|state
operator|!=
name|newState
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Task {} executed, state transited from {} to {}"
argument_list|,
name|task
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|this
operator|.
name|state
argument_list|,
name|newState
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isExiting
argument_list|(
name|newState
argument_list|)
condition|)
block|{
name|task
operator|.
name|onExit
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|setState
argument_list|(
name|newState
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the next command or null if it is empty.    *    * @return SCMCommand or Null.    */
DECL|method|getNextCommand ()
specifier|public
name|SCMCommand
name|getNextCommand
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|commandQueue
operator|.
name|poll
argument_list|()
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Adds a command to the State Machine queue.    *    * @param command - SCMCommand.    */
DECL|method|addCommand (SCMCommand command)
specifier|public
name|void
name|addCommand
parameter_list|(
name|SCMCommand
name|command
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|commandQueue
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|addCmdStatus
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the count of the Execution.    * @return long    */
DECL|method|getExecutionCount ()
specifier|public
name|long
name|getExecutionCount
parameter_list|()
block|{
return|return
name|stateExecutionCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns the next {@link CommandStatus} or null if it is empty.    *    * @return {@link CommandStatus} or Null.    */
DECL|method|getCmdStatus (Long key)
specifier|public
name|CommandStatus
name|getCmdStatus
parameter_list|(
name|Long
name|key
parameter_list|)
block|{
return|return
name|cmdStatusMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Adds a {@link CommandStatus} to the State Machine.    *    * @param status - {@link CommandStatus}.    */
DECL|method|addCmdStatus (Long key, CommandStatus status)
specifier|public
name|void
name|addCmdStatus
parameter_list|(
name|Long
name|key
parameter_list|,
name|CommandStatus
name|status
parameter_list|)
block|{
name|cmdStatusMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a {@link CommandStatus} to the State Machine for given SCMCommand.    *    * @param cmd - {@link SCMCommand}.    */
DECL|method|addCmdStatus (SCMCommand cmd)
specifier|public
name|void
name|addCmdStatus
parameter_list|(
name|SCMCommand
name|cmd
parameter_list|)
block|{
if|if
condition|(
name|cmd
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|Type
operator|.
name|closeContainerCommand
argument_list|)
condition|)
block|{
comment|// We will be removing CommandStatus completely.
comment|// As a first step, removed it for CloseContainerCommand.
return|return;
block|}
name|CommandStatusBuilder
name|statusBuilder
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|deleteBlocksCommand
condition|)
block|{
name|statusBuilder
operator|=
operator|new
name|DeleteBlockCommandStatusBuilder
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|statusBuilder
operator|=
name|CommandStatusBuilder
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|addCmdStatus
argument_list|(
name|cmd
operator|.
name|getId
argument_list|()
argument_list|,
name|statusBuilder
operator|.
name|setCmdId
argument_list|(
name|cmd
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|PENDING
argument_list|)
operator|.
name|setType
argument_list|(
name|cmd
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get map holding all {@link CommandStatus} objects.    *    */
DECL|method|getCommandStatusMap ()
specifier|public
name|Map
argument_list|<
name|Long
argument_list|,
name|CommandStatus
argument_list|>
name|getCommandStatusMap
parameter_list|()
block|{
return|return
name|cmdStatusMap
return|;
block|}
comment|/**    * Updates status of a pending status command.    * @param cmdId       command id    * @param cmdStatusUpdater Consumer to update command status.    * @return true if command status updated successfully else false.    */
DECL|method|updateCommandStatus (Long cmdId, Consumer<CommandStatus> cmdStatusUpdater)
specifier|public
name|boolean
name|updateCommandStatus
parameter_list|(
name|Long
name|cmdId
parameter_list|,
name|Consumer
argument_list|<
name|CommandStatus
argument_list|>
name|cmdStatusUpdater
parameter_list|)
block|{
if|if
condition|(
name|cmdStatusMap
operator|.
name|containsKey
argument_list|(
name|cmdId
argument_list|)
condition|)
block|{
name|cmdStatusUpdater
operator|.
name|accept
argument_list|(
name|cmdStatusMap
operator|.
name|get
argument_list|(
name|cmdId
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|configureHeartbeatFrequency ()
specifier|public
name|void
name|configureHeartbeatFrequency
parameter_list|()
block|{
name|heartbeatFrequency
operator|.
name|set
argument_list|(
name|getScmHeartbeatInterval
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return current heartbeat frequency in ms.    */
DECL|method|getHeartbeatFrequency ()
specifier|public
name|long
name|getHeartbeatFrequency
parameter_list|()
block|{
return|return
name|heartbeatFrequency
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

