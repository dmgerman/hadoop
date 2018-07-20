begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.protocol.commands
package|package
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
name|GeneratedMessage
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
name|hdds
operator|.
name|HddsIdFactory
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
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
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|IdentifiableEventPayload
import|;
end_import

begin_comment
comment|/**  * A class that acts as the base class to convert between Java and SCM  * commands in protobuf format.  * @param<T>  */
end_comment

begin_class
DECL|class|SCMCommand
specifier|public
specifier|abstract
class|class
name|SCMCommand
parameter_list|<
name|T
extends|extends
name|GeneratedMessage
parameter_list|>
implements|implements
name|IdentifiableEventPayload
block|{
DECL|field|id
specifier|private
name|long
name|id
decl_stmt|;
DECL|method|SCMCommand ()
name|SCMCommand
parameter_list|()
block|{
name|this
operator|.
name|id
operator|=
name|HddsIdFactory
operator|.
name|getLongId
argument_list|()
expr_stmt|;
block|}
DECL|method|SCMCommand (long id)
name|SCMCommand
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**    * Returns the type of this command.    * @return Type    */
DECL|method|getType ()
specifier|public
specifier|abstract
name|SCMCommandProto
operator|.
name|Type
name|getType
parameter_list|()
function_decl|;
comment|/**    * Gets the protobuf message of this object.    * @return A protobuf message.    */
DECL|method|getProtoBufMessage ()
specifier|public
specifier|abstract
name|byte
index|[]
name|getProtoBufMessage
parameter_list|()
function_decl|;
comment|/**    * Gets the commandId of this object.    * @return uuid.    */
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
block|}
end_class

end_unit

