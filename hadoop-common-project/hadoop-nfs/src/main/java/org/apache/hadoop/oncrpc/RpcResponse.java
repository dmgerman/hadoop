begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.oncrpc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|oncrpc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
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

begin_comment
comment|/**  * RpcResponse encapsulates a response to a RPC request. It contains the data  * that is going to cross the wire, as well as the information of the remote  * peer.  */
end_comment

begin_class
DECL|class|RpcResponse
specifier|public
class|class
name|RpcResponse
block|{
DECL|field|data
specifier|private
specifier|final
name|ChannelBuffer
name|data
decl_stmt|;
DECL|field|remoteAddress
specifier|private
specifier|final
name|SocketAddress
name|remoteAddress
decl_stmt|;
DECL|method|RpcResponse (ChannelBuffer data, SocketAddress remoteAddress)
specifier|public
name|RpcResponse
parameter_list|(
name|ChannelBuffer
name|data
parameter_list|,
name|SocketAddress
name|remoteAddress
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|remoteAddress
operator|=
name|remoteAddress
expr_stmt|;
block|}
DECL|method|data ()
specifier|public
name|ChannelBuffer
name|data
parameter_list|()
block|{
return|return
name|data
return|;
block|}
DECL|method|remoteAddress ()
specifier|public
name|SocketAddress
name|remoteAddress
parameter_list|()
block|{
return|return
name|remoteAddress
return|;
block|}
block|}
end_class

end_unit

