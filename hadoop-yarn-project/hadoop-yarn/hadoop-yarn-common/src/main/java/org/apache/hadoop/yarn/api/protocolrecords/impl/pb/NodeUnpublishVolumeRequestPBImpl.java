begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|NodeUnpublishVolumeRequest
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
name|yarn
operator|.
name|proto
operator|.
name|CsiAdaptorProtos
import|;
end_import

begin_comment
comment|/**  * The protobuf record class for request to un-publish volume on node manager.  */
end_comment

begin_class
DECL|class|NodeUnpublishVolumeRequestPBImpl
specifier|public
class|class
name|NodeUnpublishVolumeRequestPBImpl
extends|extends
name|NodeUnpublishVolumeRequest
block|{
DECL|field|builder
specifier|private
name|CsiAdaptorProtos
operator|.
name|NodeUnpublishVolumeRequest
operator|.
name|Builder
name|builder
decl_stmt|;
DECL|method|NodeUnpublishVolumeRequestPBImpl ()
specifier|public
name|NodeUnpublishVolumeRequestPBImpl
parameter_list|()
block|{
name|this
operator|.
name|builder
operator|=
name|CsiAdaptorProtos
operator|.
name|NodeUnpublishVolumeRequest
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|NodeUnpublishVolumeRequestPBImpl ( CsiAdaptorProtos.NodeUnpublishVolumeRequest request)
specifier|public
name|NodeUnpublishVolumeRequestPBImpl
parameter_list|(
name|CsiAdaptorProtos
operator|.
name|NodeUnpublishVolumeRequest
name|request
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|request
operator|.
name|toBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|CsiAdaptorProtos
operator|.
name|NodeUnpublishVolumeRequest
name|getProto
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setVolumeId (String volumeId)
specifier|public
name|void
name|setVolumeId
parameter_list|(
name|String
name|volumeId
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|.
name|setVolumeId
argument_list|(
name|volumeId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTargetPath (String targetPath)
specifier|public
name|void
name|setTargetPath
parameter_list|(
name|String
name|targetPath
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|.
name|setTargetPath
argument_list|(
name|targetPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getVolumeId ()
specifier|public
name|String
name|getVolumeId
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getVolumeId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTargetPath ()
specifier|public
name|String
name|getTargetPath
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getTargetPath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|getProto
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
operator|.
name|getProto
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

