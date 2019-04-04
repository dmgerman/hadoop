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
name|OmKeyLocationInfo
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|KeyArgs
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
name|KeyInfo
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
name|KeyLocation
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
comment|/**    * Add a allocate block, it is assumed that the client is having an open    * key session going on. This block will be appended to this open key session.    * This will be called only during HA enabled OM, as during HA we get an    * allocated Block information, and add that information to OM DB.    *    * In HA the flow for allocateBlock is in StartTransaction allocateBlock    * will be called which returns block information, and in the    * applyTransaction addAllocateBlock will be called to add the block    * information to DB.    *    * @param args the key to append    * @param clientID the client identification    * @param keyLocation key location given by allocateBlock    * @return an allocated block    * @throws IOException    */
DECL|method|addAllocatedBlock (OmKeyArgs args, long clientID, KeyLocation keyLocation)
name|OmKeyLocationInfo
name|addAllocatedBlock
parameter_list|(
name|OmKeyArgs
name|args
parameter_list|,
name|long
name|clientID
parameter_list|,
name|KeyLocation
name|keyLocation
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Add the openKey entry with given keyInfo and clientID in to openKeyTable.    * This will be called only from applyTransaction, once after calling    * applyKey in startTransaction.    *    * @param omKeyArgs    * @param keyInfo    * @param clientID    * @throws IOException    */
DECL|method|applyOpenKey (KeyArgs omKeyArgs, KeyInfo keyInfo, long clientID)
name|void
name|applyOpenKey
parameter_list|(
name|KeyArgs
name|omKeyArgs
parameter_list|,
name|KeyInfo
name|keyInfo
parameter_list|,
name|long
name|clientID
parameter_list|)
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
block|}
end_interface

end_unit

