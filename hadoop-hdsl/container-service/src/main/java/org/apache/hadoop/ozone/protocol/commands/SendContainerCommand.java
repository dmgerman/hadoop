begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SendContainerReportProto
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCmdType
import|;
end_import

begin_comment
comment|/**  * Allows a Datanode to send in the container report.  */
end_comment

begin_class
DECL|class|SendContainerCommand
specifier|public
class|class
name|SendContainerCommand
extends|extends
name|SCMCommand
argument_list|<
name|SendContainerReportProto
argument_list|>
block|{
comment|/**    * Returns a NullCommand class from NullCommandResponse Proto.    * @param unused  - unused    * @return NullCommand    */
DECL|method|getFromProtobuf ( final SendContainerReportProto unused)
specifier|public
specifier|static
name|SendContainerCommand
name|getFromProtobuf
parameter_list|(
specifier|final
name|SendContainerReportProto
name|unused
parameter_list|)
block|{
return|return
operator|new
name|SendContainerCommand
argument_list|()
return|;
block|}
comment|/**    * returns a new builder.    * @return Builder    */
DECL|method|newBuilder ()
specifier|public
specifier|static
name|SendContainerCommand
operator|.
name|Builder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|SendContainerCommand
operator|.
name|Builder
argument_list|()
return|;
block|}
comment|/**    * Returns the type of this command.    *    * @return Type    */
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|SCMCmdType
name|getType
parameter_list|()
block|{
return|return
name|SCMCmdType
operator|.
name|sendContainerReport
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
name|SendContainerReportProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|/**    * A Builder class this is the standard pattern we are using for all commands.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
comment|/**      * Return a null command.      * @return - NullCommand.      */
DECL|method|build ()
specifier|public
name|SendContainerCommand
name|build
parameter_list|()
block|{
return|return
operator|new
name|SendContainerCommand
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

