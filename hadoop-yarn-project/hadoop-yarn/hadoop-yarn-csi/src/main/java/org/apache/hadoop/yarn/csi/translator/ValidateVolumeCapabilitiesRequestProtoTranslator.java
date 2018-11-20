begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.csi.translator
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|csi
operator|.
name|translator
package|;
end_package

begin_import
import|import
name|csi
operator|.
name|v0
operator|.
name|Csi
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
name|ValidateVolumeCapabilitiesRequest
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
name|ValidateVolumeCapabilitiesRequest
operator|.
name|VolumeCapability
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
name|ValidateVolumeCapabilitiesRequest
operator|.
name|VolumeType
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
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Proto message translator for ValidateVolumeCapabilitiesRequest.  * @param<A> ValidateVolumeCapabilitiesRequest  * @param<B> Csi.ValidateVolumeCapabilitiesRequest  */
end_comment

begin_class
DECL|class|ValidateVolumeCapabilitiesRequestProtoTranslator
specifier|public
class|class
name|ValidateVolumeCapabilitiesRequestProtoTranslator
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
implements|implements
name|ProtoTranslator
argument_list|<
name|ValidateVolumeCapabilitiesRequest
argument_list|,
name|Csi
operator|.
name|ValidateVolumeCapabilitiesRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|convertTo ( ValidateVolumeCapabilitiesRequest request)
specifier|public
name|Csi
operator|.
name|ValidateVolumeCapabilitiesRequest
name|convertTo
parameter_list|(
name|ValidateVolumeCapabilitiesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
block|{
name|Csi
operator|.
name|ValidateVolumeCapabilitiesRequest
operator|.
name|Builder
name|buidler
init|=
name|Csi
operator|.
name|ValidateVolumeCapabilitiesRequest
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|buidler
operator|.
name|setVolumeId
argument_list|(
name|request
operator|.
name|getVolumeId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buidler
operator|.
name|putAllVolumeAttributes
argument_list|(
name|request
operator|.
name|getVolumeAttributes
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|VolumeCapability
name|cap
range|:
name|request
operator|.
name|getVolumeCapabilities
argument_list|()
control|)
block|{
name|Csi
operator|.
name|VolumeCapability
operator|.
name|AccessMode
name|accessMode
init|=
name|Csi
operator|.
name|VolumeCapability
operator|.
name|AccessMode
operator|.
name|newBuilder
argument_list|()
operator|.
name|setModeValue
argument_list|(
name|cap
operator|.
name|getAccessMode
argument_list|()
operator|.
name|ordinal
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Csi
operator|.
name|VolumeCapability
operator|.
name|MountVolume
name|mountVolume
init|=
name|Csi
operator|.
name|VolumeCapability
operator|.
name|MountVolume
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllMountFlags
argument_list|(
name|cap
operator|.
name|getMountFlags
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Csi
operator|.
name|VolumeCapability
name|capability
init|=
name|Csi
operator|.
name|VolumeCapability
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAccessMode
argument_list|(
name|accessMode
argument_list|)
operator|.
name|setMount
argument_list|(
name|mountVolume
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|buidler
operator|.
name|addVolumeCapabilities
argument_list|(
name|capability
argument_list|)
expr_stmt|;
block|}
return|return
name|buidler
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|convertFrom ( Csi.ValidateVolumeCapabilitiesRequest request)
specifier|public
name|ValidateVolumeCapabilitiesRequest
name|convertFrom
parameter_list|(
name|Csi
operator|.
name|ValidateVolumeCapabilitiesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
block|{
name|ValidateVolumeCapabilitiesRequest
name|result
init|=
name|ValidateVolumeCapabilitiesRequest
operator|.
name|newInstance
argument_list|(
name|request
operator|.
name|getVolumeId
argument_list|()
argument_list|,
name|request
operator|.
name|getVolumeAttributesMap
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Csi
operator|.
name|VolumeCapability
name|csiCap
range|:
name|request
operator|.
name|getVolumeCapabilitiesList
argument_list|()
control|)
block|{
name|ValidateVolumeCapabilitiesRequest
operator|.
name|AccessMode
name|mode
init|=
name|ValidateVolumeCapabilitiesRequest
operator|.
name|AccessMode
operator|.
name|valueOf
argument_list|(
name|csiCap
operator|.
name|getAccessMode
argument_list|()
operator|.
name|getMode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|csiCap
operator|.
name|hasMount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Invalid request,"
operator|+
literal|" mount is not found in the request."
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|mountFlags
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|csiCap
operator|.
name|getMount
argument_list|()
operator|.
name|getMountFlagsCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|mountFlags
operator|.
name|add
argument_list|(
name|csiCap
operator|.
name|getMount
argument_list|()
operator|.
name|getMountFlags
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|VolumeCapability
name|capability
init|=
operator|new
name|VolumeCapability
argument_list|(
name|mode
argument_list|,
name|VolumeType
operator|.
name|FILE_SYSTEM
argument_list|,
name|mountFlags
argument_list|)
decl_stmt|;
name|result
operator|.
name|addVolumeCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

