begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

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

begin_comment
comment|/**  * This interface intends to align the state between client and server  * via RPC communication.  *  * This should be implemented separately on the client side and server side  * and can be used to pass state information on RPC responses from server  * to client.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|AlignmentContext
specifier|public
interface|interface
name|AlignmentContext
block|{
comment|/**    * This is the intended server method call to implement to pass state info    * during RPC response header construction.    *    * @param header The RPC response header builder.    */
DECL|method|updateResponseState (RpcResponseHeaderProto.Builder header)
name|void
name|updateResponseState
parameter_list|(
name|RpcResponseHeaderProto
operator|.
name|Builder
name|header
parameter_list|)
function_decl|;
comment|/**    * This is the intended client method call to implement to recieve state info    * during RPC response processing.    *    * @param header The RPC response header.    */
DECL|method|receiveResponseState (RpcResponseHeaderProto header)
name|void
name|receiveResponseState
parameter_list|(
name|RpcResponseHeaderProto
name|header
parameter_list|)
function_decl|;
comment|/**    * This is the intended client method call to pull last seen state info    * into RPC request processing.    *    * @param header The RPC request header builder.    */
DECL|method|updateRequestState (RpcRequestHeaderProto.Builder header)
name|void
name|updateRequestState
parameter_list|(
name|RpcRequestHeaderProto
operator|.
name|Builder
name|header
parameter_list|)
function_decl|;
comment|/**    * This is the intended server method call to implement to receive    * client state info during RPC response header processing.    *    * @param header The RPC request header.    * @param threshold a parameter to verify a condition when server    *        should reject client request due to its state being too far    *        misaligned with the client state.    *        See implementation for more details.    * @return state id required for the server to execute the call.    * @throws IOException    */
DECL|method|receiveRequestState (RpcRequestHeaderProto header, long threshold)
name|long
name|receiveRequestState
parameter_list|(
name|RpcRequestHeaderProto
name|header
parameter_list|,
name|long
name|threshold
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the last seen state id of the alignment context instance.    *    * @return the value of the last seen state id.    */
DECL|method|getLastSeenStateId ()
name|long
name|getLastSeenStateId
parameter_list|()
function_decl|;
comment|/**    * Return true if this method call does need to be synced, false    * otherwise. sync meaning server state needs to have caught up with    * client state.    *    * @param protocolName the name of the protocol    * @param method the method call to check    * @return true if this method is async, false otherwise.    */
DECL|method|isCoordinatedCall (String protocolName, String method)
name|boolean
name|isCoordinatedCall
parameter_list|(
name|String
name|protocolName
parameter_list|,
name|String
name|method
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

