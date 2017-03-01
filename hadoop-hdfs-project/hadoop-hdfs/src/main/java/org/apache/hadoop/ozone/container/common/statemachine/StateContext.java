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
name|SCMCommand
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMNodeReport
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
name|Queue
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

begin_comment
comment|/**  * Current Context of State Machine.  */
end_comment

begin_class
DECL|class|StateContext
specifier|public
class|class
name|StateContext
block|{
DECL|field|commandQueue
specifier|private
specifier|final
name|Queue
argument_list|<
name|SCMCommand
argument_list|>
name|commandQueue
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
DECL|field|state
specifier|private
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|state
decl_stmt|;
DECL|field|nrState
specifier|private
name|SCMNodeReport
name|nrState
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
name|nrState
operator|=
name|SCMNodeReport
operator|.
name|getDefaultInstance
argument_list|()
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
comment|/**    * Returns the node report of the datanode state context.    * @return the node report.    */
DECL|method|getNodeReport ()
specifier|public
name|SCMNodeReport
name|getNodeReport
parameter_list|()
block|{
return|return
name|nrState
return|;
block|}
comment|/**    * Sets the storage location report of the datanode state context.    * @param nrReport - node report    */
DECL|method|setReportState (SCMNodeReport nrReport)
specifier|public
name|void
name|setReportState
parameter_list|(
name|SCMNodeReport
name|nrReport
parameter_list|)
block|{
name|this
operator|.
name|nrState
operator|=
name|nrReport
expr_stmt|;
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
block|}
end_class

end_unit

