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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Message
import|;
end_import

begin_interface
DECL|interface|ProtobufRpcEngineCallback
specifier|public
interface|interface
name|ProtobufRpcEngineCallback
block|{
DECL|method|setResponse (Message message)
specifier|public
name|void
name|setResponse
parameter_list|(
name|Message
name|message
parameter_list|)
function_decl|;
DECL|method|error (Throwable t)
specifier|public
name|void
name|error
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

