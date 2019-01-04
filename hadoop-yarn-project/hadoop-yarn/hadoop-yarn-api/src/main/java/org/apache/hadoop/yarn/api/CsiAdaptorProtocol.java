begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetPluginInfoRequest
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
name|GetPluginInfoResponse
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
name|NodePublishVolumeRequest
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
name|NodePublishVolumeResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeUnpublishVolumeResponse
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
name|ValidateVolumeCapabilitiesResponse
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * CSI adaptor delegates all the calls from YARN to a CSI driver.  */
end_comment

begin_interface
DECL|interface|CsiAdaptorProtocol
specifier|public
interface|interface
name|CsiAdaptorProtocol
block|{
comment|/**    * Get plugin info from the CSI driver. The driver usually returns    * the name of the driver and its version.    * @param request get plugin info request.    * @return response that contains driver name and its version.    * @throws YarnException    * @throws IOException    */
DECL|method|getPluginInfo (GetPluginInfoRequest request)
name|GetPluginInfoResponse
name|getPluginInfo
parameter_list|(
name|GetPluginInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Validate if the volume capacity can be satisfied on the underneath    * storage system. This method responses if the capacity can be satisfied    * or not, with a detailed message.    * @param request validate volume capability request.    * @return validation response.    * @throws YarnException    * @throws IOException    */
DECL|method|validateVolumeCapacity ( ValidateVolumeCapabilitiesRequest request)
name|ValidateVolumeCapabilitiesResponse
name|validateVolumeCapacity
parameter_list|(
name|ValidateVolumeCapabilitiesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Publish the volume on a node manager, the volume will be mounted    * to the local file system and become visible for clients.    * @param request publish volume request.    * @return publish volume response.    * @throws YarnException    * @throws IOException    */
DECL|method|nodePublishVolume ( NodePublishVolumeRequest request)
name|NodePublishVolumeResponse
name|nodePublishVolume
parameter_list|(
name|NodePublishVolumeRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * This is a reverse operation of    * {@link #nodePublishVolume(NodePublishVolumeRequest)}, it un-mounts the    * volume from given node.    * @param request un-publish volume request.    * @return un-publish volume response.    * @throws YarnException    * @throws IOException    */
DECL|method|nodeUnpublishVolume ( NodeUnpublishVolumeRequest request)
name|NodeUnpublishVolumeResponse
name|nodeUnpublishVolume
parameter_list|(
name|NodeUnpublishVolumeRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

