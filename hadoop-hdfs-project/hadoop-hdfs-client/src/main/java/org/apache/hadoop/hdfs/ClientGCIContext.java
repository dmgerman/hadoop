begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|ipc
operator|.
name|AlignmentContext
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
name|ipc
operator|.
name|protobuf
operator|.
name|RpcHeaderProtos
operator|.
name|RpcRequestHeaderProto
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
name|ipc
operator|.
name|protobuf
operator|.
name|RpcHeaderProtos
operator|.
name|RpcResponseHeaderProto
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
name|LongAccumulator
import|;
end_import

begin_comment
comment|/**  * This is the client side implementation responsible for receiving  * state alignment info from server(s).  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ClientGCIContext
class|class
name|ClientGCIContext
implements|implements
name|AlignmentContext
block|{
DECL|field|lastSeenStateId
specifier|private
specifier|final
name|LongAccumulator
name|lastSeenStateId
init|=
operator|new
name|LongAccumulator
argument_list|(
name|Math
operator|::
name|max
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
decl_stmt|;
DECL|method|getLastSeenStateId ()
name|long
name|getLastSeenStateId
parameter_list|()
block|{
return|return
name|lastSeenStateId
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Client side implementation only receives state alignment info.    * It does not provide state alignment info therefore this does nothing.    */
annotation|@
name|Override
DECL|method|updateResponseState (RpcResponseHeaderProto.Builder header)
specifier|public
name|void
name|updateResponseState
parameter_list|(
name|RpcResponseHeaderProto
operator|.
name|Builder
name|header
parameter_list|)
block|{
comment|// Do nothing.
block|}
comment|/**    * Client side implementation for receiving state alignment info in responses.    */
annotation|@
name|Override
DECL|method|receiveResponseState (RpcResponseHeaderProto header)
specifier|public
name|void
name|receiveResponseState
parameter_list|(
name|RpcResponseHeaderProto
name|header
parameter_list|)
block|{
name|lastSeenStateId
operator|.
name|accumulate
argument_list|(
name|header
operator|.
name|getStateId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Client side implementation for providing state alignment info in requests.    */
annotation|@
name|Override
DECL|method|updateRequestState (RpcRequestHeaderProto.Builder header)
specifier|public
name|void
name|updateRequestState
parameter_list|(
name|RpcRequestHeaderProto
operator|.
name|Builder
name|header
parameter_list|)
block|{
name|header
operator|.
name|setStateId
argument_list|(
name|lastSeenStateId
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Client side implementation only provides state alignment info in requests.    * Client does not receive RPC requests therefore this does nothing.    */
annotation|@
name|Override
DECL|method|receiveRequestState (RpcRequestHeaderProto header)
specifier|public
name|void
name|receiveRequestState
parameter_list|(
name|RpcRequestHeaderProto
name|header
parameter_list|)
block|{
comment|// Do nothing.
block|}
block|}
end_class

end_unit

