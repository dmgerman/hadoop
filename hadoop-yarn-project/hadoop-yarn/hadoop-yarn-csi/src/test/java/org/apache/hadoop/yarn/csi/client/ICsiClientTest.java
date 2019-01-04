begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.csi.client
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
name|client
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This interface is used only in testing. It gives default implementation  * of all methods.  */
end_comment

begin_interface
DECL|interface|ICsiClientTest
specifier|public
interface|interface
name|ICsiClientTest
extends|extends
name|CsiClient
block|{
annotation|@
name|Override
DECL|method|getPluginInfo ()
specifier|default
name|Csi
operator|.
name|GetPluginInfoResponse
name|getPluginInfo
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|validateVolumeCapabilities ( Csi.ValidateVolumeCapabilitiesRequest request)
specifier|default
name|Csi
operator|.
name|ValidateVolumeCapabilitiesResponse
name|validateVolumeCapabilities
parameter_list|(
name|Csi
operator|.
name|ValidateVolumeCapabilitiesRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|nodePublishVolume ( Csi.NodePublishVolumeRequest request)
specifier|default
name|Csi
operator|.
name|NodePublishVolumeResponse
name|nodePublishVolume
parameter_list|(
name|Csi
operator|.
name|NodePublishVolumeRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|nodeUnpublishVolume ( Csi.NodeUnpublishVolumeRequest request)
specifier|default
name|Csi
operator|.
name|NodeUnpublishVolumeResponse
name|nodeUnpublishVolume
parameter_list|(
name|Csi
operator|.
name|NodeUnpublishVolumeRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
end_interface

end_unit

