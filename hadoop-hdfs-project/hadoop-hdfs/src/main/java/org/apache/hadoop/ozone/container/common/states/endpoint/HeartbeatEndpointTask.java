begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.states.endpoint
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
name|endpoint
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
name|DeletedContainerBlocksSummary
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
name|EndpointStateMachine
operator|.
name|EndPointStates
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
name|protocol
operator|.
name|commands
operator|.
name|CloseContainerCommand
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
name|DeleteBlocksCommand
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
name|ContainerNodeIDProto
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
name|SendContainerCommand
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
name|SCMCommandResponseProto
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
name|SCMHeartbeatResponseProto
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
name|time
operator|.
name|ZonedDateTime
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

begin_comment
comment|/**  * Heartbeat class for SCMs.  */
end_comment

begin_class
DECL|class|HeartbeatEndpointTask
specifier|public
class|class
name|HeartbeatEndpointTask
implements|implements
name|Callable
argument_list|<
name|EndpointStateMachine
operator|.
name|EndPointStates
argument_list|>
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
name|HeartbeatEndpointTask
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rpcEndpoint
specifier|private
specifier|final
name|EndpointStateMachine
name|rpcEndpoint
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|containerNodeIDProto
specifier|private
name|ContainerNodeIDProto
name|containerNodeIDProto
decl_stmt|;
DECL|field|context
specifier|private
name|StateContext
name|context
decl_stmt|;
comment|/**    * Constructs a SCM heart beat.    *    * @param conf Config.    */
DECL|method|HeartbeatEndpointTask (EndpointStateMachine rpcEndpoint, Configuration conf, StateContext context)
specifier|public
name|HeartbeatEndpointTask
parameter_list|(
name|EndpointStateMachine
name|rpcEndpoint
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|StateContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|rpcEndpoint
operator|=
name|rpcEndpoint
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
comment|/**    * Get the container Node ID proto.    *    * @return ContainerNodeIDProto    */
DECL|method|getContainerNodeIDProto ()
specifier|public
name|ContainerNodeIDProto
name|getContainerNodeIDProto
parameter_list|()
block|{
return|return
name|containerNodeIDProto
return|;
block|}
comment|/**    * Set container node ID proto.    *    * @param containerNodeIDProto - the node id.    */
DECL|method|setContainerNodeIDProto (ContainerNodeIDProto containerNodeIDProto)
specifier|public
name|void
name|setContainerNodeIDProto
parameter_list|(
name|ContainerNodeIDProto
name|containerNodeIDProto
parameter_list|)
block|{
name|this
operator|.
name|containerNodeIDProto
operator|=
name|containerNodeIDProto
expr_stmt|;
block|}
comment|/**    * Computes a result, or throws an exception if unable to do so.    *    * @return computed result    * @throws Exception if unable to compute a result    */
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|EndpointStateMachine
operator|.
name|EndPointStates
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|rpcEndpoint
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|this
operator|.
name|containerNodeIDProto
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|DatanodeID
name|datanodeID
init|=
name|DatanodeID
operator|.
name|getFromProtoBuf
argument_list|(
name|this
operator|.
name|containerNodeIDProto
operator|.
name|getDatanodeID
argument_list|()
argument_list|)
decl_stmt|;
name|SCMHeartbeatResponseProto
name|reponse
init|=
name|rpcEndpoint
operator|.
name|getEndPoint
argument_list|()
operator|.
name|sendHeartbeat
argument_list|(
name|datanodeID
argument_list|,
name|this
operator|.
name|context
operator|.
name|getNodeReport
argument_list|()
argument_list|,
name|this
operator|.
name|context
operator|.
name|getContainerReportState
argument_list|()
argument_list|)
decl_stmt|;
name|processResponse
argument_list|(
name|reponse
argument_list|,
name|datanodeID
argument_list|)
expr_stmt|;
name|rpcEndpoint
operator|.
name|setLastSuccessfulHeartbeat
argument_list|(
name|ZonedDateTime
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
name|rpcEndpoint
operator|.
name|zeroMissedCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|rpcEndpoint
operator|.
name|logIfNeeded
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rpcEndpoint
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|rpcEndpoint
operator|.
name|getState
argument_list|()
return|;
block|}
comment|/**    * Returns a builder class for HeartbeatEndpointTask task.    * @return   Builder.    */
DECL|method|newBuilder ()
specifier|public
specifier|static
name|Builder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**    * Add this command to command processing Queue.    *    * @param response - SCMHeartbeat response.    */
DECL|method|processResponse (SCMHeartbeatResponseProto response, final DatanodeID datanodeID)
specifier|private
name|void
name|processResponse
parameter_list|(
name|SCMHeartbeatResponseProto
name|response
parameter_list|,
specifier|final
name|DatanodeID
name|datanodeID
parameter_list|)
block|{
for|for
control|(
name|SCMCommandResponseProto
name|commandResponseProto
range|:
name|response
operator|.
name|getCommandsList
argument_list|()
control|)
block|{
comment|// Verify the response is indeed for this datanode.
name|Preconditions
operator|.
name|checkState
argument_list|(
name|commandResponseProto
operator|.
name|getDatanodeUUID
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|datanodeID
operator|.
name|getDatanodeUuid
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
literal|"Unexpected datanode ID in the response."
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|commandResponseProto
operator|.
name|getCmdType
argument_list|()
condition|)
block|{
case|case
name|sendContainerReport
case|:
name|this
operator|.
name|context
operator|.
name|addCommand
argument_list|(
name|SendContainerCommand
operator|.
name|getFromProtobuf
argument_list|(
name|commandResponseProto
operator|.
name|getSendReport
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|reregisterCommand
case|:
if|if
condition|(
name|rpcEndpoint
operator|.
name|getState
argument_list|()
operator|==
name|EndPointStates
operator|.
name|HEARTBEAT
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
literal|"Received SCM notification to register."
operator|+
literal|" Interrupt HEARTBEAT and transit to REGISTER state."
argument_list|)
expr_stmt|;
block|}
name|rpcEndpoint
operator|.
name|setState
argument_list|(
name|EndPointStates
operator|.
name|REGISTER
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"Illegal state {} found, expecting {}."
argument_list|,
name|rpcEndpoint
operator|.
name|getState
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|EndPointStates
operator|.
name|HEARTBEAT
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|deleteBlocksCommand
case|:
name|DeleteBlocksCommand
name|db
init|=
name|DeleteBlocksCommand
operator|.
name|getFromProtobuf
argument_list|(
name|commandResponseProto
operator|.
name|getDeleteBlocksProto
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|db
operator|.
name|blocksTobeDeleted
argument_list|()
operator|.
name|isEmpty
argument_list|()
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
name|DeletedContainerBlocksSummary
operator|.
name|getFrom
argument_list|(
name|db
operator|.
name|blocksTobeDeleted
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|context
operator|.
name|addCommand
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|closeContainerCommand
case|:
name|CloseContainerCommand
name|closeContainer
init|=
name|CloseContainerCommand
operator|.
name|getFromProtobuf
argument_list|(
name|commandResponseProto
operator|.
name|getCloseContainerProto
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"Received SCM container close request for container {}"
argument_list|,
name|closeContainer
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|context
operator|.
name|addCommand
argument_list|(
name|closeContainer
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown response : "
operator|+
name|commandResponseProto
operator|.
name|getCmdType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Builder class for HeartbeatEndpointTask.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|endPointStateMachine
specifier|private
name|EndpointStateMachine
name|endPointStateMachine
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|containerNodeIDProto
specifier|private
name|ContainerNodeIDProto
name|containerNodeIDProto
decl_stmt|;
DECL|field|context
specifier|private
name|StateContext
name|context
decl_stmt|;
comment|/**      * Constructs the builder class.      */
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
comment|/**      * Sets the endpoint state machine.      *      * @param rpcEndPoint - Endpoint state machine.      * @return Builder      */
DECL|method|setEndpointStateMachine (EndpointStateMachine rpcEndPoint)
specifier|public
name|Builder
name|setEndpointStateMachine
parameter_list|(
name|EndpointStateMachine
name|rpcEndPoint
parameter_list|)
block|{
name|this
operator|.
name|endPointStateMachine
operator|=
name|rpcEndPoint
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the Config.      *      * @param config - config      * @return Builder      */
DECL|method|setConfig (Configuration config)
specifier|public
name|Builder
name|setConfig
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|config
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the NodeID.      *      * @param nodeID - NodeID proto      * @return Builder      */
DECL|method|setNodeID (ContainerNodeIDProto nodeID)
specifier|public
name|Builder
name|setNodeID
parameter_list|(
name|ContainerNodeIDProto
name|nodeID
parameter_list|)
block|{
name|this
operator|.
name|containerNodeIDProto
operator|=
name|nodeID
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the context.      * @param stateContext - State context.      * @return this.      */
DECL|method|setContext (StateContext stateContext)
specifier|public
name|Builder
name|setContext
parameter_list|(
name|StateContext
name|stateContext
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|stateContext
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|HeartbeatEndpointTask
name|build
parameter_list|()
block|{
if|if
condition|(
name|endPointStateMachine
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No endpoint specified."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A valid endpoint state machine is"
operator|+
literal|" needed to construct HeartbeatEndpointTask task"
argument_list|)
throw|;
block|}
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No config specified."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A valid configration is needed to"
operator|+
literal|" construct HeartbeatEndpointTask task"
argument_list|)
throw|;
block|}
if|if
condition|(
name|containerNodeIDProto
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No nodeID specified."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A vaild Node ID is needed to "
operator|+
literal|"construct HeartbeatEndpointTask task"
argument_list|)
throw|;
block|}
name|HeartbeatEndpointTask
name|task
init|=
operator|new
name|HeartbeatEndpointTask
argument_list|(
name|this
operator|.
name|endPointStateMachine
argument_list|,
name|this
operator|.
name|conf
argument_list|,
name|this
operator|.
name|context
argument_list|)
decl_stmt|;
name|task
operator|.
name|setContainerNodeIDProto
argument_list|(
name|containerNodeIDProto
argument_list|)
expr_stmt|;
return|return
name|task
return|;
block|}
block|}
block|}
end_class

end_unit

