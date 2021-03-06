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
name|csi
operator|.
name|v0
operator|.
name|Csi
operator|.
name|GetPluginInfoResponse
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
comment|/**  * General interface for a CSI client. This interface defines all APIs  * that CSI spec supports, including both identity/controller/node service  * APIs.  */
end_comment

begin_interface
DECL|interface|CsiClient
specifier|public
interface|interface
name|CsiClient
block|{
comment|/**    * Gets some basic info about the CSI plugin, including the driver name,    * version and optionally some manifest info.    * @return {@link GetPluginInfoResponse}    * @throws IOException when unable to get plugin info from the driver.    */
DECL|method|getPluginInfo ()
name|GetPluginInfoResponse
name|getPluginInfo
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|validateVolumeCapabilities ( Csi.ValidateVolumeCapabilitiesRequest request)
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
function_decl|;
DECL|method|nodePublishVolume ( Csi.NodePublishVolumeRequest request)
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
function_decl|;
DECL|method|nodeUnpublishVolume ( Csi.NodeUnpublishVolumeRequest request)
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
function_decl|;
block|}
end_interface

end_unit

