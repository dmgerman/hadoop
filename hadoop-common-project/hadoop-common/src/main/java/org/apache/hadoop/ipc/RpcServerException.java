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
operator|.
name|RpcErrorCodeProto
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
operator|.
name|RpcStatusProto
import|;
end_import

begin_comment
comment|/**  * Indicates an exception on the RPC server   */
end_comment

begin_class
DECL|class|RpcServerException
specifier|public
class|class
name|RpcServerException
extends|extends
name|RpcException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/**    * Constructs exception with the specified detail message.    * @param message detailed message.    */
DECL|method|RpcServerException (final String message)
specifier|public
name|RpcServerException
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs exception with the specified detail message and cause.    *     * @param message message.    * @param cause the cause (can be retried by the {@link #getCause()} method).    *          (A<tt>null</tt> value is permitted, and indicates that the cause    *          is nonexistent or unknown.)    */
DECL|method|RpcServerException (final String message, final Throwable cause)
specifier|public
name|RpcServerException
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**    * get the rpc status corresponding to this exception    */
DECL|method|getRpcStatusProto ()
specifier|public
name|RpcStatusProto
name|getRpcStatusProto
parameter_list|()
block|{
return|return
name|RpcStatusProto
operator|.
name|ERROR
return|;
block|}
comment|/**    * get the detailed rpc status corresponding to this exception    */
DECL|method|getRpcErrorCodeProto ()
specifier|public
name|RpcErrorCodeProto
name|getRpcErrorCodeProto
parameter_list|()
block|{
return|return
name|RpcErrorCodeProto
operator|.
name|ERROR_RPC_SERVER
return|;
block|}
block|}
end_class

end_unit

