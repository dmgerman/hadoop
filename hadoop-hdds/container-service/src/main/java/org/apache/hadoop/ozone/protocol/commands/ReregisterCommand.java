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
import|import static
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
name|ReregisterCommandProto
import|;
end_import

begin_comment
comment|/**  * Informs a datanode to register itself with SCM again.  */
end_comment

begin_class
DECL|class|ReregisterCommand
specifier|public
class|class
name|ReregisterCommand
extends|extends
name|SCMCommand
argument_list|<
name|ReregisterCommandProto
argument_list|>
block|{
comment|/**    * Returns the type of this command.    *    * @return Type    */
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|SCMCommandProto
operator|.
name|Type
name|getType
parameter_list|()
block|{
return|return
name|SCMCommandProto
operator|.
name|Type
operator|.
name|reregisterCommand
return|;
block|}
comment|/**    * Gets the protobuf message of this object.    *    * @return A protobuf message.    */
annotation|@
name|Override
DECL|method|getProtoBufMessage ()
specifier|public
name|byte
index|[]
name|getProtoBufMessage
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|/**    * Not implemented for ReregisterCommand.    *    * @return cmdId.    */
annotation|@
name|Override
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|getProto ()
specifier|public
name|ReregisterCommandProto
name|getProto
parameter_list|()
block|{
return|return
name|ReregisterCommandProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

