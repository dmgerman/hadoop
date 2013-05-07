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
comment|/**  * No such Method for an Rpc Call  *  */
end_comment

begin_class
DECL|class|RpcNoSuchMethodException
specifier|public
class|class
name|RpcNoSuchMethodException
extends|extends
name|RpcServerException
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
DECL|method|RpcNoSuchMethodException (final String message)
specifier|public
name|RpcNoSuchMethodException
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
name|ERROR_NO_SUCH_METHOD
return|;
block|}
block|}
end_class

end_unit

