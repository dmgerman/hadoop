begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|annotations
operator|.
name|VisibleForTesting
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|OzoneClientUtils
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
name|statemachine
operator|.
name|commandhandler
operator|.
name|CommandDispatcher
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
name|statemachine
operator|.
name|commandhandler
operator|.
name|ContainerReportHandler
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
name|ozoneimpl
operator|.
name|OzoneContainer
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
name|util
operator|.
name|Time
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
name|util
operator|.
name|concurrent
operator|.
name|HadoopExecutors
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
name|io
operator|.
name|Closeable
import|;
end_import

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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * State Machine Class.  */
end_comment

begin_class
DECL|class|DatanodeStateMachine
specifier|public
class|class
name|DatanodeStateMachine
implements|implements
name|Closeable
block|{
annotation|@
name|VisibleForTesting
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
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|executorService
specifier|private
specifier|final
name|ExecutorService
name|executorService
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|connectionManager
specifier|private
specifier|final
name|SCMConnectionManager
name|connectionManager
decl_stmt|;
DECL|field|heartbeatFrequency
specifier|private
specifier|final
name|long
name|heartbeatFrequency
decl_stmt|;
DECL|field|context
specifier|private
name|StateContext
name|context
decl_stmt|;
DECL|field|container
specifier|private
specifier|final
name|OzoneContainer
name|container
decl_stmt|;
DECL|field|datanodeID
specifier|private
name|DatanodeID
name|datanodeID
init|=
literal|null
decl_stmt|;
DECL|field|commandDispatcher
specifier|private
specifier|final
name|CommandDispatcher
name|commandDispatcher
decl_stmt|;
DECL|field|commandsHandled
specifier|private
name|long
name|commandsHandled
decl_stmt|;
DECL|field|nextHB
specifier|private
name|AtomicLong
name|nextHB
decl_stmt|;
DECL|field|stateMachineThread
specifier|private
name|Thread
name|stateMachineThread
init|=
literal|null
decl_stmt|;
DECL|field|cmdProcessThread
specifier|private
name|Thread
name|cmdProcessThread
init|=
literal|null
decl_stmt|;
comment|/**    * Constructs a a datanode state machine.    *    * @param datanodeID - DatanodeID used to identify a datanode    * @param conf - Configuration.    */
DECL|method|DatanodeStateMachine (DatanodeID datanodeID, Configuration conf)
specifier|public
name|DatanodeStateMachine
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|executorService
operator|=
name|HadoopExecutors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Datanode State Machine Thread - %d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|connectionManager
operator|=
operator|new
name|SCMConnectionManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|context
operator|=
operator|new
name|StateContext
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|DatanodeStates
operator|.
name|getInitState
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|heartbeatFrequency
operator|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|OzoneClientUtils
operator|.
name|getScmHeartbeatInterval
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|container
operator|=
operator|new
name|OzoneContainer
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|datanodeID
operator|=
name|datanodeID
expr_stmt|;
name|nextHB
operator|=
operator|new
name|AtomicLong
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
expr_stmt|;
comment|// When we add new handlers just adding a new handler here should do the
comment|// trick.
name|commandDispatcher
operator|=
name|CommandDispatcher
operator|.
name|newBuilder
argument_list|()
operator|.
name|addHandler
argument_list|(
operator|new
name|ContainerReportHandler
argument_list|()
argument_list|)
operator|.
name|setConnectionManager
argument_list|(
name|connectionManager
argument_list|)
operator|.
name|setContainer
argument_list|(
name|container
argument_list|)
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|DatanodeStateMachine (Configuration conf)
specifier|public
name|DatanodeStateMachine
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|setDatanodeID (DatanodeID datanodeID)
specifier|public
name|void
name|setDatanodeID
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|)
block|{
name|this
operator|.
name|datanodeID
operator|=
name|datanodeID
expr_stmt|;
block|}
comment|/**    *    * Return DatanodeID if set, return null otherwise.    *    * @return datanodeID    */
DECL|method|getDatanodeID ()
specifier|public
name|DatanodeID
name|getDatanodeID
parameter_list|()
block|{
return|return
name|this
operator|.
name|datanodeID
return|;
block|}
comment|/**    * Returns the Connection manager for this state machine.    *    * @return - SCMConnectionManager.    */
DECL|method|getConnectionManager ()
specifier|public
name|SCMConnectionManager
name|getConnectionManager
parameter_list|()
block|{
return|return
name|connectionManager
return|;
block|}
DECL|method|getContainer ()
specifier|public
name|OzoneContainer
name|getContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|container
return|;
block|}
comment|/**    * Runs the state machine at a fixed frequency.    */
DECL|method|start ()
specifier|private
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|now
init|=
literal|0
decl_stmt|;
name|container
operator|.
name|start
argument_list|()
expr_stmt|;
name|initCommandHandlerThread
argument_list|(
name|conf
argument_list|)
expr_stmt|;
while|while
condition|(
name|context
operator|.
name|getState
argument_list|()
operator|!=
name|DatanodeStates
operator|.
name|SHUTDOWN
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Executing cycle Number : {}"
argument_list|,
name|context
operator|.
name|getExecutionCount
argument_list|()
argument_list|)
expr_stmt|;
name|nextHB
operator|.
name|set
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|+
name|heartbeatFrequency
argument_list|)
expr_stmt|;
name|context
operator|.
name|setReportState
argument_list|(
name|container
operator|.
name|getNodeReport
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|execute
argument_list|(
name|executorService
argument_list|,
name|heartbeatFrequency
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|now
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
if|if
condition|(
name|now
operator|<
name|nextHB
operator|.
name|get
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|nextHB
operator|.
name|get
argument_list|()
operator|-
name|now
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Ignore this exception.
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to finish the execution."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Gets the current context.    *    * @return StateContext    */
DECL|method|getContext ()
specifier|public
name|StateContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
comment|/**    * Sets the current context.    *    * @param context - Context    */
DECL|method|setContext (StateContext context)
specifier|public
name|void
name|setContext
parameter_list|(
name|StateContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/**    * Closes this stream and releases any system resources associated with it. If    * the stream is already closed then invoking this method has no effect.    *<p>    *<p> As noted in {@link AutoCloseable#close()}, cases where the close may    * fail require careful attention. It is strongly advised to relinquish the    * underlying resources and to internally<em>mark</em> the {@code Closeable}    * as closed, prior to throwing the {@code IOException}.    *    * @throws IOException if an I/O error occurs    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|stateMachineThread
operator|!=
literal|null
condition|)
block|{
name|stateMachineThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cmdProcessThread
operator|!=
literal|null
condition|)
block|{
name|cmdProcessThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|setState
argument_list|(
name|DatanodeStates
operator|.
name|getLastState
argument_list|()
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to shutdown state machine properly."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error attempting to shutdown."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|connectionManager
operator|!=
literal|null
condition|)
block|{
name|connectionManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|container
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * States that a datanode  can be in. GetNextState will move this enum from    * getInitState to getLastState.    */
DECL|enum|DatanodeStates
specifier|public
enum|enum
name|DatanodeStates
block|{
DECL|enumConstant|INIT
name|INIT
argument_list|(
literal|1
argument_list|)
block|,
DECL|enumConstant|RUNNING
name|RUNNING
argument_list|(
literal|2
argument_list|)
block|,
DECL|enumConstant|SHUTDOWN
name|SHUTDOWN
argument_list|(
literal|3
argument_list|)
block|;
DECL|field|value
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
comment|/**      * Constructs ContainerStates.      *      * @param value  Enum Value      */
DECL|method|DatanodeStates (int value)
name|DatanodeStates
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Returns the first State.      *      * @return First State.      */
DECL|method|getInitState ()
specifier|public
specifier|static
name|DatanodeStates
name|getInitState
parameter_list|()
block|{
return|return
name|INIT
return|;
block|}
comment|/**      * The last state of endpoint states.      *      * @return last state.      */
DECL|method|getLastState ()
specifier|public
specifier|static
name|DatanodeStates
name|getLastState
parameter_list|()
block|{
return|return
name|SHUTDOWN
return|;
block|}
comment|/**      * returns the numeric value associated with the endPoint.      *      * @return int.      */
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      * Returns the next logical state that endPoint should move to. This      * function assumes the States are sequentially numbered.      *      * @return NextState.      */
DECL|method|getNextState ()
specifier|public
name|DatanodeStates
name|getNextState
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|value
operator|<
name|getLastState
argument_list|()
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|int
name|stateValue
init|=
name|this
operator|.
name|getValue
argument_list|()
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|DatanodeStates
name|iter
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|stateValue
operator|==
name|iter
operator|.
name|getValue
argument_list|()
condition|)
block|{
return|return
name|iter
return|;
block|}
block|}
block|}
return|return
name|getLastState
argument_list|()
return|;
block|}
block|}
comment|/**    * Start datanode state machine as a single thread daemon.    */
DECL|method|startDaemon ()
specifier|public
name|void
name|startDaemon
parameter_list|()
block|{
name|Runnable
name|startStateMachineTask
init|=
parameter_list|()
lambda|->
block|{
try|try
block|{
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Ozone container server started."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to start the DatanodeState Machine"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|stateMachineThread
operator|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Datanode State Machine Thread - %d"
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|newThread
argument_list|(
name|startStateMachineTask
argument_list|)
expr_stmt|;
name|stateMachineThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stop the daemon thread of the datanode state machine.    */
DECL|method|stopDaemon ()
specifier|public
specifier|synchronized
name|void
name|stopDaemon
parameter_list|()
block|{
try|try
block|{
name|context
operator|.
name|setState
argument_list|(
name|DatanodeStates
operator|.
name|SHUTDOWN
argument_list|)
expr_stmt|;
name|this
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Ozone container server stopped."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Stop ozone container server failed."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *    * Check if the datanode state machine daemon is stopped.    *    * @return True if datanode state machine daemon is stopped    * and false otherwise.    */
annotation|@
name|VisibleForTesting
DECL|method|isDaemonStopped ()
specifier|public
name|boolean
name|isDaemonStopped
parameter_list|()
block|{
return|return
name|this
operator|.
name|executorService
operator|.
name|isShutdown
argument_list|()
operator|&&
name|this
operator|.
name|getContext
argument_list|()
operator|.
name|getExecutionCount
argument_list|()
operator|==
literal|0
operator|&&
name|this
operator|.
name|getContext
argument_list|()
operator|.
name|getState
argument_list|()
operator|==
name|DatanodeStates
operator|.
name|SHUTDOWN
return|;
block|}
comment|/**    * Create a command handler thread.    *    * @param conf    */
DECL|method|initCommandHandlerThread (Configuration conf)
specifier|private
name|void
name|initCommandHandlerThread
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|/**      * Task that periodically checks if we have any outstanding commands.      * It is assumed that commands can be processed slowly and in order.      * This assumption might change in future. Right now due to this assumption      * we have single command  queue process thread.      */
name|Runnable
name|processCommandQueue
init|=
parameter_list|()
lambda|->
block|{
name|long
name|now
decl_stmt|;
while|while
condition|(
name|getContext
argument_list|()
operator|.
name|getState
argument_list|()
operator|!=
name|DatanodeStates
operator|.
name|SHUTDOWN
condition|)
block|{
name|SCMCommand
name|command
init|=
name|getContext
argument_list|()
operator|.
name|getNextCommand
argument_list|()
decl_stmt|;
if|if
condition|(
name|command
operator|!=
literal|null
condition|)
block|{
name|commandDispatcher
operator|.
name|handle
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|commandsHandled
operator|++
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
comment|// Sleep till the next HB + 1 second.
name|now
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextHB
operator|.
name|get
argument_list|()
operator|>
name|now
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
operator|(
name|nextHB
operator|.
name|get
argument_list|()
operator|-
name|now
operator|)
operator|+
literal|1000L
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Ignore this exception.
block|}
block|}
block|}
block|}
decl_stmt|;
comment|// We will have only one thread for command processing in a datanode.
name|cmdProcessThread
operator|=
operator|new
name|Thread
argument_list|(
name|processCommandQueue
argument_list|)
expr_stmt|;
name|cmdProcessThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cmdProcessThread
operator|.
name|setName
argument_list|(
literal|"Command processor thread"
argument_list|)
expr_stmt|;
name|cmdProcessThread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
lambda|->
block|{
comment|// Let us just restart this thread after logging a critical error.
comment|// if this thread is not running we cannot handle commands from SCM.
name|LOG
operator|.
name|error
argument_list|(
literal|"Critical Error : Command processor thread encountered an "
operator|+
literal|"error. Thread: {}"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|cmdProcessThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|cmdProcessThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the number of commands handled  by the datanode.    * @return  count    */
annotation|@
name|VisibleForTesting
DECL|method|getCommandHandled ()
specifier|public
name|long
name|getCommandHandled
parameter_list|()
block|{
return|return
name|commandsHandled
return|;
block|}
block|}
end_class

end_unit

