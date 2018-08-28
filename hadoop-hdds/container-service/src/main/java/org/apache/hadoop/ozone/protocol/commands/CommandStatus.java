begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|CommandStatus
operator|.
name|Status
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * A class that is used to communicate status of datanode commands.  */
end_comment

begin_class
DECL|class|CommandStatus
specifier|public
class|class
name|CommandStatus
block|{
DECL|field|type
specifier|private
name|SCMCommandProto
operator|.
name|Type
name|type
decl_stmt|;
DECL|field|cmdId
specifier|private
name|Long
name|cmdId
decl_stmt|;
DECL|field|status
specifier|private
name|Status
name|status
decl_stmt|;
DECL|field|msg
specifier|private
name|String
name|msg
decl_stmt|;
DECL|method|getType ()
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getCmdId ()
specifier|public
name|Long
name|getCmdId
parameter_list|()
block|{
return|return
name|cmdId
return|;
block|}
DECL|method|getStatus ()
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
DECL|method|getMsg ()
specifier|public
name|String
name|getMsg
parameter_list|()
block|{
return|return
name|msg
return|;
block|}
comment|/**    * To allow change of status once commandStatus is initialized.    *    * @param status    */
DECL|method|setStatus (Status status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|Status
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
comment|/**    * Returns a CommandStatus from the protocol buffers.    *    * @param cmdStatusProto - protoBuf Message    * @return CommandStatus    */
DECL|method|getFromProtoBuf ( StorageContainerDatanodeProtocolProtos.CommandStatus cmdStatusProto)
specifier|public
name|CommandStatus
name|getFromProtoBuf
parameter_list|(
name|StorageContainerDatanodeProtocolProtos
operator|.
name|CommandStatus
name|cmdStatusProto
parameter_list|)
block|{
return|return
name|CommandStatusBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdId
argument_list|(
name|cmdStatusProto
operator|.
name|getCmdId
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|cmdStatusProto
operator|.
name|getStatus
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|cmdStatusProto
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|setMsg
argument_list|(
name|cmdStatusProto
operator|.
name|getMsg
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns a CommandStatus from the protocol buffers.    *    * @return StorageContainerDatanodeProtocolProtos.CommandStatus    */
specifier|public
name|StorageContainerDatanodeProtocolProtos
operator|.
name|CommandStatus
DECL|method|getProtoBufMessage ()
name|getProtoBufMessage
parameter_list|()
block|{
name|StorageContainerDatanodeProtocolProtos
operator|.
name|CommandStatus
operator|.
name|Builder
name|builder
init|=
name|StorageContainerDatanodeProtocolProtos
operator|.
name|CommandStatus
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdId
argument_list|(
name|this
operator|.
name|getCmdId
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|this
operator|.
name|getStatus
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|this
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getMsg
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setMsg
argument_list|(
name|this
operator|.
name|getMsg
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Builder class for CommandStatus.    */
DECL|class|CommandStatusBuilder
specifier|public
specifier|static
specifier|final
class|class
name|CommandStatusBuilder
block|{
DECL|field|type
specifier|private
name|SCMCommandProto
operator|.
name|Type
name|type
decl_stmt|;
DECL|field|cmdId
specifier|private
name|Long
name|cmdId
decl_stmt|;
DECL|field|status
specifier|private
name|StorageContainerDatanodeProtocolProtos
operator|.
name|CommandStatus
operator|.
name|Status
name|status
decl_stmt|;
DECL|field|msg
specifier|private
name|String
name|msg
decl_stmt|;
DECL|method|CommandStatusBuilder ()
specifier|private
name|CommandStatusBuilder
parameter_list|()
block|{     }
DECL|method|newBuilder ()
specifier|public
specifier|static
name|CommandStatusBuilder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|CommandStatusBuilder
argument_list|()
return|;
block|}
DECL|method|setType (Type commandType)
specifier|public
name|CommandStatusBuilder
name|setType
parameter_list|(
name|Type
name|commandType
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|commandType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCmdId (Long commandId)
specifier|public
name|CommandStatusBuilder
name|setCmdId
parameter_list|(
name|Long
name|commandId
parameter_list|)
block|{
name|this
operator|.
name|cmdId
operator|=
name|commandId
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setStatus (Status commandStatus)
specifier|public
name|CommandStatusBuilder
name|setStatus
parameter_list|(
name|Status
name|commandStatus
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|commandStatus
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMsg (String message)
specifier|public
name|CommandStatusBuilder
name|setMsg
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|msg
operator|=
name|message
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|CommandStatus
name|build
parameter_list|()
block|{
name|CommandStatus
name|commandStatus
init|=
operator|new
name|CommandStatus
argument_list|()
decl_stmt|;
name|commandStatus
operator|.
name|type
operator|=
name|this
operator|.
name|type
expr_stmt|;
name|commandStatus
operator|.
name|msg
operator|=
name|this
operator|.
name|msg
expr_stmt|;
name|commandStatus
operator|.
name|status
operator|=
name|this
operator|.
name|status
expr_stmt|;
name|commandStatus
operator|.
name|cmdId
operator|=
name|this
operator|.
name|cmdId
expr_stmt|;
return|return
name|commandStatus
return|;
block|}
block|}
block|}
end_class

end_unit

