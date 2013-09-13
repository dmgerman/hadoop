begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Utils
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
name|nfs
operator|.
name|nfs3
operator|.
name|FileHandle
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Constant
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Constant
operator|.
name|WriteStableHow
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Status
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
name|nfs
operator|.
name|nfs3
operator|.
name|request
operator|.
name|CREATE3Request
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
name|nfs
operator|.
name|nfs3
operator|.
name|request
operator|.
name|SetAttr3
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
name|nfs
operator|.
name|nfs3
operator|.
name|request
operator|.
name|WRITE3Request
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
name|oncrpc
operator|.
name|RegistrationClient
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
name|oncrpc
operator|.
name|RpcCall
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
name|oncrpc
operator|.
name|RpcFrameDecoder
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
name|oncrpc
operator|.
name|RpcReply
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
name|oncrpc
operator|.
name|SimpleTcpClient
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
name|oncrpc
operator|.
name|SimpleTcpClientHandler
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
name|oncrpc
operator|.
name|XDR
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|Channel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelPipeline
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelPipelineFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|Channels
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|MessageEvent
import|;
end_import

begin_class
DECL|class|TestOutOfOrderWrite
specifier|public
class|class
name|TestOutOfOrderWrite
block|{
DECL|field|LOG
specifier|public
specifier|final
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestOutOfOrderWrite
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|handle
specifier|static
name|FileHandle
name|handle
init|=
literal|null
decl_stmt|;
DECL|field|channel
specifier|static
name|Channel
name|channel
decl_stmt|;
DECL|field|data1
specifier|static
name|byte
index|[]
name|data1
init|=
operator|new
name|byte
index|[
literal|1000
index|]
decl_stmt|;
DECL|field|data2
specifier|static
name|byte
index|[]
name|data2
init|=
operator|new
name|byte
index|[
literal|1000
index|]
decl_stmt|;
DECL|field|data3
specifier|static
name|byte
index|[]
name|data3
init|=
operator|new
name|byte
index|[
literal|1000
index|]
decl_stmt|;
DECL|method|create ()
specifier|static
name|XDR
name|create
parameter_list|()
block|{
name|XDR
name|request
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|RpcCall
operator|.
name|write
argument_list|(
name|request
argument_list|,
literal|0x8000004c
argument_list|,
name|Nfs3Constant
operator|.
name|PROGRAM
argument_list|,
name|Nfs3Constant
operator|.
name|VERSION
argument_list|,
name|Nfs3Constant
operator|.
name|NFSPROC3
operator|.
name|CREATE
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// credentials
name|request
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// auth null
name|request
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// length zero
comment|// verifier
name|request
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// auth null
name|request
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// length zero
name|SetAttr3
name|objAttr
init|=
operator|new
name|SetAttr3
argument_list|()
decl_stmt|;
name|CREATE3Request
name|createReq
init|=
operator|new
name|CREATE3Request
argument_list|(
operator|new
name|FileHandle
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"out-of-order-write"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|0
argument_list|,
name|objAttr
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|createReq
operator|.
name|serialize
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|write (FileHandle handle, int xid, long offset, int count, byte[] data)
specifier|static
name|XDR
name|write
parameter_list|(
name|FileHandle
name|handle
parameter_list|,
name|int
name|xid
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|count
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
name|XDR
name|request
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|RpcCall
operator|.
name|write
argument_list|(
name|request
argument_list|,
name|xid
argument_list|,
name|Nfs3Constant
operator|.
name|PROGRAM
argument_list|,
name|Nfs3Constant
operator|.
name|VERSION
argument_list|,
name|Nfs3Constant
operator|.
name|NFSPROC3
operator|.
name|WRITE
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// credentials
name|request
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// auth null
name|request
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// length zero
comment|// verifier
name|request
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// auth null
name|request
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// length zero
name|WRITE3Request
name|write1
init|=
operator|new
name|WRITE3Request
argument_list|(
name|handle
argument_list|,
name|offset
argument_list|,
name|count
argument_list|,
name|WriteStableHow
operator|.
name|UNSTABLE
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|write1
operator|.
name|serialize
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|testRequest (XDR request)
specifier|static
name|void
name|testRequest
parameter_list|(
name|XDR
name|request
parameter_list|)
block|{
name|RegistrationClient
name|registrationClient
init|=
operator|new
name|RegistrationClient
argument_list|(
literal|"localhost"
argument_list|,
name|Nfs3Constant
operator|.
name|SUN_RPCBIND
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|registrationClient
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
DECL|class|WriteHandler
specifier|static
class|class
name|WriteHandler
extends|extends
name|SimpleTcpClientHandler
block|{
DECL|method|WriteHandler (XDR request)
specifier|public
name|WriteHandler
parameter_list|(
name|XDR
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|messageReceived (ChannelHandlerContext ctx, MessageEvent e)
specifier|public
name|void
name|messageReceived
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|MessageEvent
name|e
parameter_list|)
block|{
comment|// Get handle from create response
name|ChannelBuffer
name|buf
init|=
operator|(
name|ChannelBuffer
operator|)
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|XDR
name|rsp
init|=
operator|new
name|XDR
argument_list|(
name|buf
operator|.
name|array
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getBytes
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"rsp length is zero, why?"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"rsp length="
operator|+
name|rsp
operator|.
name|getBytes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|RpcReply
name|reply
init|=
name|RpcReply
operator|.
name|read
argument_list|(
name|rsp
argument_list|)
decl_stmt|;
name|int
name|xid
init|=
name|reply
operator|.
name|getXid
argument_list|()
decl_stmt|;
comment|// Only process the create response
if|if
condition|(
name|xid
operator|!=
literal|0x8000004c
condition|)
block|{
return|return;
block|}
name|int
name|status
init|=
name|rsp
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|!=
name|Nfs3Status
operator|.
name|NFS3_OK
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Create failed, status ="
operator|+
name|status
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Create succeeded"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
comment|// value follow
name|handle
operator|=
operator|new
name|FileHandle
argument_list|()
expr_stmt|;
name|handle
operator|.
name|deserialize
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|channel
operator|=
name|e
operator|.
name|getChannel
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|WriteClient
specifier|static
class|class
name|WriteClient
extends|extends
name|SimpleTcpClient
block|{
DECL|method|WriteClient (String host, int port, XDR request, Boolean oneShot)
specifier|public
name|WriteClient
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|XDR
name|request
parameter_list|,
name|Boolean
name|oneShot
parameter_list|)
block|{
name|super
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|request
argument_list|,
name|oneShot
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setPipelineFactory ()
specifier|protected
name|ChannelPipelineFactory
name|setPipelineFactory
parameter_list|()
block|{
name|this
operator|.
name|pipelineFactory
operator|=
operator|new
name|ChannelPipelineFactory
argument_list|()
block|{
specifier|public
name|ChannelPipeline
name|getPipeline
parameter_list|()
block|{
return|return
name|Channels
operator|.
name|pipeline
argument_list|(
operator|new
name|RpcFrameDecoder
argument_list|()
argument_list|,
operator|new
name|WriteHandler
argument_list|(
name|request
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
return|return
name|this
operator|.
name|pipelineFactory
return|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|data1
argument_list|,
operator|(
name|byte
operator|)
literal|7
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data2
argument_list|,
operator|(
name|byte
operator|)
literal|8
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data3
argument_list|,
operator|(
name|byte
operator|)
literal|9
argument_list|)
expr_stmt|;
comment|// NFS3 Create request
name|WriteClient
name|client
init|=
operator|new
name|WriteClient
argument_list|(
literal|"localhost"
argument_list|,
name|Nfs3Constant
operator|.
name|PORT
argument_list|,
name|create
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|client
operator|.
name|run
argument_list|()
expr_stmt|;
while|while
condition|(
name|handle
operator|==
literal|null
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"handle is still null..."
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Send write1 request"
argument_list|)
expr_stmt|;
name|XDR
name|writeReq
decl_stmt|;
name|writeReq
operator|=
name|write
argument_list|(
name|handle
argument_list|,
literal|0x8000005c
argument_list|,
literal|2000
argument_list|,
literal|1000
argument_list|,
name|data3
argument_list|)
expr_stmt|;
name|Nfs3Utils
operator|.
name|writeChannel
argument_list|(
name|channel
argument_list|,
name|writeReq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|writeReq
operator|=
name|write
argument_list|(
name|handle
argument_list|,
literal|0x8000005d
argument_list|,
literal|1000
argument_list|,
literal|1000
argument_list|,
name|data2
argument_list|)
expr_stmt|;
name|Nfs3Utils
operator|.
name|writeChannel
argument_list|(
name|channel
argument_list|,
name|writeReq
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|writeReq
operator|=
name|write
argument_list|(
name|handle
argument_list|,
literal|0x8000005e
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|,
name|data1
argument_list|)
expr_stmt|;
name|Nfs3Utils
operator|.
name|writeChannel
argument_list|(
name|channel
argument_list|,
name|writeReq
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// TODO: convert to Junit test, and validate result automatically
block|}
block|}
end_class

end_unit

