begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.states.datanode
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
name|states
operator|.
name|datanode
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerUtils
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
name|DatanodeStateMachine
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
name|EndpointStateMachine
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
name|SCMConnectionManager
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
name|StateContext
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
name|endpoint
operator|.
name|HeartbeatEndpointTask
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
name|endpoint
operator|.
name|RegisterEndpointTask
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
name|endpoint
operator|.
name|VersionEndpointTask
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
name|scm
operator|.
name|ScmConfigKeys
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|concurrent
operator|.
name|Callable
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
name|CompletionService
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
name|ExecutorCompletionService
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
name|Future
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

begin_comment
comment|/**  * Class that implements handshake with SCM.  */
end_comment

begin_class
DECL|class|RunningDatanodeState
specifier|public
class|class
name|RunningDatanodeState
implements|implements
name|DatanodeState
block|{
specifier|static
specifier|final
name|Logger
DECL|field|LOG
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RunningDatanodeState
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|connectionManager
specifier|private
specifier|final
name|SCMConnectionManager
name|connectionManager
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|StateContext
name|context
decl_stmt|;
DECL|field|ecs
specifier|private
name|CompletionService
argument_list|<
name|EndpointStateMachine
operator|.
name|EndPointStates
argument_list|>
name|ecs
decl_stmt|;
DECL|method|RunningDatanodeState (Configuration conf, SCMConnectionManager connectionManager, StateContext context)
specifier|public
name|RunningDatanodeState
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|SCMConnectionManager
name|connectionManager
parameter_list|,
name|StateContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|connectionManager
operator|=
name|connectionManager
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/**    * Reads a datanode ID from the persisted information.    *    * @param idPath - Path to the ID File.    * @return DatanodeID    * @throws IOException    */
specifier|private
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerNodeIDProto
DECL|method|readPersistedDatanodeID (Path idPath)
name|readPersistedDatanodeID
parameter_list|(
name|Path
name|idPath
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|idPath
argument_list|)
expr_stmt|;
name|DatanodeID
name|datanodeID
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|datanodeIDs
init|=
name|ContainerUtils
operator|.
name|readDatanodeIDsFrom
argument_list|(
name|idPath
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|containerPort
init|=
name|this
operator|.
name|context
operator|.
name|getContainerPort
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeID
name|dnId
range|:
name|datanodeIDs
control|)
block|{
if|if
condition|(
name|dnId
operator|.
name|getContainerPort
argument_list|()
operator|==
name|containerPort
condition|)
block|{
name|datanodeID
operator|=
name|dnId
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|datanodeID
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No valid datanode ID found from "
operator|+
name|idPath
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" that matches container port "
operator|+
name|containerPort
argument_list|)
throw|;
block|}
else|else
block|{
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerNodeIDProto
name|containerIDProto
init|=
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerNodeIDProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDatanodeID
argument_list|(
name|datanodeID
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|containerIDProto
return|;
block|}
block|}
comment|/**    * Returns ContainerNodeIDProto or null in case of Error.    *    * @return ContainerNodeIDProto    */
specifier|private
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerNodeIDProto
DECL|method|getContainerNodeID ()
name|getContainerNodeID
parameter_list|()
block|{
name|String
name|dataNodeIDPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataNodeIDPath
operator|==
literal|null
operator|||
name|dataNodeIDPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"A valid file path is needed for config setting {}"
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ID
argument_list|)
expr_stmt|;
comment|// This is an unrecoverable error.
name|this
operator|.
name|context
operator|.
name|setState
argument_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|SHUTDOWN
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerNodeIDProto
name|nodeID
decl_stmt|;
comment|// try to read an existing ContainerNode ID.
try|try
block|{
name|nodeID
operator|=
name|readPersistedDatanodeID
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dataNodeIDPath
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeID
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Read Node ID :"
argument_list|,
name|nodeID
operator|.
name|getDatanodeID
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|nodeID
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Not able to find container Node ID, creating it."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|context
operator|.
name|setState
argument_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|SHUTDOWN
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/**    * Called before entering this state.    */
annotation|@
name|Override
DECL|method|onEnter ()
specifier|public
name|void
name|onEnter
parameter_list|()
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Entering handshake task."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called After exiting this state.    */
annotation|@
name|Override
DECL|method|onExit ()
specifier|public
name|void
name|onExit
parameter_list|()
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Exiting handshake task."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Executes one or more tasks that is needed by this state.    *    * @param executor -  ExecutorService    */
annotation|@
name|Override
DECL|method|execute (ExecutorService executor)
specifier|public
name|void
name|execute
parameter_list|(
name|ExecutorService
name|executor
parameter_list|)
block|{
name|ecs
operator|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|executor
argument_list|)
expr_stmt|;
for|for
control|(
name|EndpointStateMachine
name|endpoint
range|:
name|connectionManager
operator|.
name|getValues
argument_list|()
control|)
block|{
name|Callable
argument_list|<
name|EndpointStateMachine
operator|.
name|EndPointStates
argument_list|>
name|endpointTask
init|=
name|getEndPointTask
argument_list|(
name|endpoint
argument_list|)
decl_stmt|;
name|ecs
operator|.
name|submit
argument_list|(
name|endpointTask
argument_list|)
expr_stmt|;
block|}
block|}
comment|//TODO : Cache some of these tasks instead of creating them
comment|//all the time.
specifier|private
name|Callable
argument_list|<
name|EndpointStateMachine
operator|.
name|EndPointStates
argument_list|>
DECL|method|getEndPointTask (EndpointStateMachine endpoint)
name|getEndPointTask
parameter_list|(
name|EndpointStateMachine
name|endpoint
parameter_list|)
block|{
switch|switch
condition|(
name|endpoint
operator|.
name|getState
argument_list|()
condition|)
block|{
case|case
name|GETVERSION
case|:
return|return
operator|new
name|VersionEndpointTask
argument_list|(
name|endpoint
argument_list|,
name|conf
argument_list|)
return|;
case|case
name|REGISTER
case|:
return|return
name|RegisterEndpointTask
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConfig
argument_list|(
name|conf
argument_list|)
operator|.
name|setEndpointStateMachine
argument_list|(
name|endpoint
argument_list|)
operator|.
name|setNodeID
argument_list|(
name|getContainerNodeID
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|HEARTBEAT
case|:
return|return
name|HeartbeatEndpointTask
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConfig
argument_list|(
name|conf
argument_list|)
operator|.
name|setEndpointStateMachine
argument_list|(
name|endpoint
argument_list|)
operator|.
name|setNodeID
argument_list|(
name|getContainerNodeID
argument_list|()
argument_list|)
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|SHUTDOWN
case|:
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal Argument."
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Computes the next state the container state machine must move to by looking    * at all the state of endpoints.    *<p>    * if any endpoint state has moved to Shutdown, either we have an    * unrecoverable error or we have been told to shutdown. Either case the    * datanode state machine should move to Shutdown state, otherwise we    * remain in the Running state.    *    * @return next container state.    */
specifier|private
name|DatanodeStateMachine
operator|.
name|DatanodeStates
DECL|method|computeNextContainerState ( List<Future<EndpointStateMachine.EndPointStates>> results)
name|computeNextContainerState
parameter_list|(
name|List
argument_list|<
name|Future
argument_list|<
name|EndpointStateMachine
operator|.
name|EndPointStates
argument_list|>
argument_list|>
name|results
parameter_list|)
block|{
for|for
control|(
name|Future
argument_list|<
name|EndpointStateMachine
operator|.
name|EndPointStates
argument_list|>
name|state
range|:
name|results
control|)
block|{
try|try
block|{
if|if
condition|(
name|state
operator|.
name|get
argument_list|()
operator|==
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|SHUTDOWN
condition|)
block|{
comment|// if any endpoint tells us to shutdown we move to shutdown state.
return|return
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|SHUTDOWN
return|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in executing end point task."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|RUNNING
return|;
block|}
comment|/**    * Wait for execute to finish.    *    * @param duration - Time    * @param timeUnit - Unit of duration.    */
annotation|@
name|Override
specifier|public
name|DatanodeStateMachine
operator|.
name|DatanodeStates
DECL|method|await (long duration, TimeUnit timeUnit)
name|await
parameter_list|(
name|long
name|duration
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
name|int
name|count
init|=
name|connectionManager
operator|.
name|getValues
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|returned
init|=
literal|0
decl_stmt|;
name|long
name|timeLeft
init|=
name|timeUnit
operator|.
name|toMillis
argument_list|(
name|duration
argument_list|)
decl_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|EndpointStateMachine
operator|.
name|EndPointStates
argument_list|>
argument_list|>
name|results
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|returned
argument_list|<
name|count
operator|&&
name|timeLeft
argument_list|>
literal|0
condition|)
block|{
name|Future
argument_list|<
name|EndpointStateMachine
operator|.
name|EndPointStates
argument_list|>
name|result
init|=
name|ecs
operator|.
name|poll
argument_list|(
name|timeLeft
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|returned
operator|++
expr_stmt|;
block|}
name|timeLeft
operator|=
name|timeLeft
operator|-
operator|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
operator|)
expr_stmt|;
block|}
return|return
name|computeNextContainerState
argument_list|(
name|results
argument_list|)
return|;
block|}
block|}
end_class

end_unit

