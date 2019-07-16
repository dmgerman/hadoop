begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|protocol
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmDeleteVolumeResponse
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmKeyArgs
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmMultipartInfo
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmVolumeArgs
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmVolumeOwnerChangeResponse
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|VolumeList
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
comment|/**  * Protocol to talk to OM HA. These methods are needed only called from  * OmRequestHandler.  */
end_comment

begin_interface
DECL|interface|OzoneManagerHAProtocol
specifier|public
interface|interface
name|OzoneManagerHAProtocol
block|{
comment|/**    * Store the snapshot index i.e. the raft log index, corresponding to the    * last transaction applied to the OM RocksDB, in OM metadata dir on disk.    * @return the snapshot index    * @throws IOException    */
DECL|method|saveRatisSnapshot ()
name|long
name|saveRatisSnapshot
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Initiate multipart upload for the specified key.    *    * This will be called only from applyTransaction.    * @param omKeyArgs    * @param multipartUploadID    * @return OmMultipartInfo    * @throws IOException    */
DECL|method|applyInitiateMultipartUpload (OmKeyArgs omKeyArgs, String multipartUploadID)
name|OmMultipartInfo
name|applyInitiateMultipartUpload
parameter_list|(
name|OmKeyArgs
name|omKeyArgs
parameter_list|,
name|String
name|multipartUploadID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Start Create Volume Transaction.    * @param omVolumeArgs    * @return VolumeList    * @throws IOException    */
DECL|method|startCreateVolume (OmVolumeArgs omVolumeArgs)
name|VolumeList
name|startCreateVolume
parameter_list|(
name|OmVolumeArgs
name|omVolumeArgs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Apply Create Volume changes to OM DB.    * @param omVolumeArgs    * @param volumeList    * @throws IOException    */
DECL|method|applyCreateVolume (OmVolumeArgs omVolumeArgs, VolumeList volumeList)
name|void
name|applyCreateVolume
parameter_list|(
name|OmVolumeArgs
name|omVolumeArgs
parameter_list|,
name|VolumeList
name|volumeList
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Start setOwner Transaction.    * @param volume    * @param owner    * @return OmVolumeOwnerChangeResponse    * @throws IOException    */
DECL|method|startSetOwner (String volume, String owner)
name|OmVolumeOwnerChangeResponse
name|startSetOwner
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Apply Set Quota changes to OM DB.    * @param oldOwner    * @param oldOwnerVolumeList    * @param newOwnerVolumeList    * @param newOwnerVolumeArgs    * @throws IOException    */
DECL|method|applySetOwner (String oldOwner, VolumeList oldOwnerVolumeList, VolumeList newOwnerVolumeList, OmVolumeArgs newOwnerVolumeArgs)
name|void
name|applySetOwner
parameter_list|(
name|String
name|oldOwner
parameter_list|,
name|VolumeList
name|oldOwnerVolumeList
parameter_list|,
name|VolumeList
name|newOwnerVolumeList
parameter_list|,
name|OmVolumeArgs
name|newOwnerVolumeArgs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Start Set Quota Transaction.    * @param volume    * @param quota    * @return OmVolumeArgs    * @throws IOException    */
DECL|method|startSetQuota (String volume, long quota)
name|OmVolumeArgs
name|startSetQuota
parameter_list|(
name|String
name|volume
parameter_list|,
name|long
name|quota
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Apply Set Quota Changes to OM DB.    * @param omVolumeArgs    * @throws IOException    */
DECL|method|applySetQuota (OmVolumeArgs omVolumeArgs)
name|void
name|applySetQuota
parameter_list|(
name|OmVolumeArgs
name|omVolumeArgs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Start Delete Volume Transaction.    * @param volume    * @return OmDeleteVolumeResponse    * @throws IOException    */
DECL|method|startDeleteVolume (String volume)
name|OmDeleteVolumeResponse
name|startDeleteVolume
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Apply Delete Volume changes to OM DB.    * @param volume    * @param owner    * @param newVolumeList    * @throws IOException    */
DECL|method|applyDeleteVolume (String volume, String owner, VolumeList newVolumeList)
name|void
name|applyDeleteVolume
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|owner
parameter_list|,
name|VolumeList
name|newVolumeList
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

