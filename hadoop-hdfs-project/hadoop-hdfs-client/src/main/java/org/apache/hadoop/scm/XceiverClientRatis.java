begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|RatisHelper
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|client
operator|.
name|RaftClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftClientReply
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|RpcType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|SupportedRpcType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
operator|.
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
operator|.
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ShadedProtoUtil
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
name|util
operator|.
name|Objects
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
name|CompletableFuture
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_comment
comment|/**  * An abstract implementation of {@link XceiverClientSpi} using Ratis.  * The underlying RPC mechanism can be chosen via the constructor.  */
end_comment

begin_class
DECL|class|XceiverClientRatis
specifier|public
specifier|final
class|class
name|XceiverClientRatis
extends|extends
name|XceiverClientSpi
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
name|XceiverClientRatis
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|newXceiverClientRatis ( Pipeline pipeline, Configuration ozoneConf)
specifier|public
specifier|static
name|XceiverClientRatis
name|newXceiverClientRatis
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|Configuration
name|ozoneConf
parameter_list|)
block|{
specifier|final
name|String
name|rpcType
init|=
name|ozoneConf
operator|.
name|get
argument_list|(
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
argument_list|,
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|XceiverClientRatis
argument_list|(
name|pipeline
argument_list|,
name|SupportedRpcType
operator|.
name|valueOfIgnoreCase
argument_list|(
name|rpcType
argument_list|)
argument_list|)
return|;
block|}
DECL|field|pipeline
specifier|private
specifier|final
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|rpcType
specifier|private
specifier|final
name|RpcType
name|rpcType
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|RaftClient
argument_list|>
name|client
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Constructs a client. */
DECL|method|XceiverClientRatis (Pipeline pipeline, RpcType rpcType)
specifier|private
name|XceiverClientRatis
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|RpcType
name|rpcType
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|pipeline
operator|=
name|pipeline
expr_stmt|;
name|this
operator|.
name|rpcType
operator|=
name|rpcType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPipeline ()
specifier|public
name|Pipeline
name|getPipeline
parameter_list|()
block|{
return|return
name|pipeline
return|;
block|}
annotation|@
name|Override
DECL|method|connect ()
specifier|public
name|void
name|connect
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|client
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|RatisHelper
operator|.
name|newRaftClient
argument_list|(
name|rpcType
argument_list|,
name|getPipeline
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Client is already connected."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
specifier|final
name|RaftClient
name|c
init|=
name|client
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getClient ()
specifier|private
name|RaftClient
name|getClient
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|client
operator|.
name|get
argument_list|()
argument_list|,
literal|"client is null"
argument_list|)
return|;
block|}
DECL|method|isReadOnly (ContainerCommandRequestProto proto)
specifier|private
name|boolean
name|isReadOnly
parameter_list|(
name|ContainerCommandRequestProto
name|proto
parameter_list|)
block|{
switch|switch
condition|(
name|proto
operator|.
name|getCmdType
argument_list|()
condition|)
block|{
case|case
name|ReadContainer
case|:
case|case
name|ReadChunk
case|:
case|case
name|ListKey
case|:
case|case
name|GetKey
case|:
case|case
name|GetSmallFile
case|:
case|case
name|ListContainer
case|:
case|case
name|ListChunk
case|:
return|return
literal|true
return|;
case|case
name|CloseContainer
case|:
case|case
name|WriteChunk
case|:
case|case
name|UpdateContainer
case|:
case|case
name|CompactChunk
case|:
case|case
name|CreateContainer
case|:
case|case
name|DeleteChunk
case|:
case|case
name|DeleteContainer
case|:
case|case
name|DeleteKey
case|:
case|case
name|PutKey
case|:
case|case
name|PutSmallFile
case|:
default|default:
return|return
literal|false
return|;
block|}
block|}
DECL|method|sendRequest (ContainerCommandRequestProto request)
specifier|private
name|RaftClientReply
name|sendRequest
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|isReadOnlyRequest
init|=
name|isReadOnly
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|ByteString
name|byteString
init|=
name|ShadedProtoUtil
operator|.
name|asShadedByteString
argument_list|(
name|request
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"sendCommand {} {}"
argument_list|,
name|isReadOnlyRequest
argument_list|,
name|request
argument_list|)
expr_stmt|;
specifier|final
name|RaftClientReply
name|reply
init|=
name|isReadOnlyRequest
condition|?
name|getClient
argument_list|()
operator|.
name|sendReadOnly
argument_list|(
parameter_list|()
lambda|->
name|byteString
argument_list|)
else|:
name|getClient
argument_list|()
operator|.
name|send
argument_list|(
parameter_list|()
lambda|->
name|byteString
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"reply {} {}"
argument_list|,
name|isReadOnlyRequest
argument_list|,
name|reply
argument_list|)
expr_stmt|;
return|return
name|reply
return|;
block|}
annotation|@
name|Override
DECL|method|sendCommand ( ContainerCommandRequestProto request)
specifier|public
name|ContainerCommandResponseProto
name|sendCommand
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|RaftClientReply
name|reply
init|=
name|sendRequest
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|reply
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ContainerCommandResponseProto
operator|.
name|parseFrom
argument_list|(
name|ShadedProtoUtil
operator|.
name|asByteString
argument_list|(
name|reply
operator|.
name|getMessage
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Sends a given command to server gets a waitable future back.    *    * @param request Request    * @return Response to the command    * @throws IOException    */
annotation|@
name|Override
DECL|method|sendCommandAsync ( ContainerCommandRequestProto request)
specifier|public
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
name|sendCommandAsync
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not implemented"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

