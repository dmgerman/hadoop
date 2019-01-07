begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
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
name|conf
operator|.
name|OzoneConfiguration
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
name|common
operator|.
name|BlockGroup
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
name|OmKeyInfo
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
name|OmMultipartCommitUploadPartInfo
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
name|OmMultipartUploadCompleteInfo
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
name|OmMultipartUploadList
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
name|OpenKeySession
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
name|utils
operator|.
name|BackgroundService
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
comment|/**  * Handles key level commands.  */
end_comment

begin_interface
DECL|interface|KeyManager
specifier|public
interface|interface
name|KeyManager
block|{
comment|/**    * Start key manager.    *    * @param configuration    * @throws IOException    */
DECL|method|start (OzoneConfiguration configuration)
name|void
name|start
parameter_list|(
name|OzoneConfiguration
name|configuration
parameter_list|)
function_decl|;
comment|/**    * Stop key manager.    */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * After calling commit, the key will be made visible. There can be multiple    * open key writes in parallel (identified by client id). The most recently    * committed one will be the one visible.    *    * @param args the key to commit.    * @param clientID the client that is committing.    * @throws IOException    */
DECL|method|commitKey (OmKeyArgs args, long clientID)
name|void
name|commitKey
parameter_list|(
name|OmKeyArgs
name|args
parameter_list|,
name|long
name|clientID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * A client calls this on an open key, to request to allocate a new block,    * and appended to the tail of current block list of the open client.    *    * @param args the key to append    * @param clientID the client requesting block.    * @return the reference to the new block.    * @throws IOException    */
DECL|method|allocateBlock (OmKeyArgs args, long clientID)
name|OmKeyLocationInfo
name|allocateBlock
parameter_list|(
name|OmKeyArgs
name|args
parameter_list|,
name|long
name|clientID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Given the args of a key to put, write an open key entry to meta data.    *    * In case that the container creation or key write failed on    * DistributedStorageHandler, this key's metadata will still stay in OM.    * TODO garbage collect the open keys that never get closed    *    * @param args the args of the key provided by client.    * @return a OpenKeySession instance client uses to talk to container.    * @throws IOException    */
DECL|method|openKey (OmKeyArgs args)
name|OpenKeySession
name|openKey
parameter_list|(
name|OmKeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Look up an existing key. Return the info of the key to client side, which    * DistributedStorageHandler will use to access the data on datanode.    *    * @param args the args of the key provided by client.    * @return a OmKeyInfo instance client uses to talk to container.    * @throws IOException    */
DECL|method|lookupKey (OmKeyArgs args)
name|OmKeyInfo
name|lookupKey
parameter_list|(
name|OmKeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Renames an existing key within a bucket.    *    * @param args the args of the key provided by client.    * @param toKeyName New name to be used for the key    * @throws IOException if specified key doesn't exist or    * some other I/O errors while renaming the key.    */
DECL|method|renameKey (OmKeyArgs args, String toKeyName)
name|void
name|renameKey
parameter_list|(
name|OmKeyArgs
name|args
parameter_list|,
name|String
name|toKeyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an object by an object key. The key will be immediately removed    * from OM namespace and become invisible to clients. The object data    * will be removed in async manner that might retain for some time.    *    * @param args the args of the key provided by client.    * @throws IOException if specified key doesn't exist or    * some other I/O errors while deleting an object.    */
DECL|method|deleteKey (OmKeyArgs args)
name|void
name|deleteKey
parameter_list|(
name|OmKeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a list of keys represented by {@link OmKeyInfo}    * in the given bucket.    *    * @param volumeName    *   the name of the volume.    * @param bucketName    *   the name of the bucket.    * @param startKey    *   the start key name, only the keys whose name is    *   after this value will be included in the result.    *   This key is excluded from the result.    * @param keyPrefix    *   key name prefix, only the keys whose name has    *   this prefix will be included in the result.    * @param maxKeys    *   the maximum number of keys to return. It ensures    *   the size of the result will not exceed this limit.    * @return a list of keys.    * @throws IOException    */
DECL|method|listKeys (String volumeName, String bucketName, String startKey, String keyPrefix, int maxKeys)
name|List
argument_list|<
name|OmKeyInfo
argument_list|>
name|listKeys
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|startKey
parameter_list|,
name|String
name|keyPrefix
parameter_list|,
name|int
name|maxKeys
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a list of pending deletion key info that ups to the given count.    * Each entry is a {@link BlockGroup}, which contains the info about the    * key name and all its associated block IDs. A pending deletion key is    * stored with #deleting# prefix in OM DB.    *    * @param count max number of keys to return.    * @return a list of {@link BlockGroup} representing keys and blocks.    * @throws IOException    */
DECL|method|getPendingDeletionKeys (int count)
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|getPendingDeletionKeys
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a list of all still open key info. Which contains the info about    * the key name and all its associated block IDs. A pending open key has    * prefix #open# in OM DB.    *    * @return a list of {@link BlockGroup} representing keys and blocks.    * @throws IOException    */
DECL|method|getExpiredOpenKeys ()
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|getExpiredOpenKeys
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes a expired open key by its name. Called when a hanging key has been    * lingering for too long. Once called, the open key entries gets removed    * from OM mdata data.    *    * @param objectKeyName object key name with #open# prefix.    * @throws IOException if specified key doesn't exist or other I/O errors.    */
DECL|method|deleteExpiredOpenKey (String objectKeyName)
name|void
name|deleteExpiredOpenKey
parameter_list|(
name|String
name|objectKeyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the metadataManager.    * @return OMMetadataManager.    */
DECL|method|getMetadataManager ()
name|OMMetadataManager
name|getMetadataManager
parameter_list|()
function_decl|;
comment|/**    * Returns the instance of Deleting Service.    * @return Background service.    */
DECL|method|getDeletingService ()
name|BackgroundService
name|getDeletingService
parameter_list|()
function_decl|;
comment|/**    * Initiate multipart upload for the specified key.    * @param keyArgs    * @return MultipartInfo    * @throws IOException    */
DECL|method|initiateMultipartUpload (OmKeyArgs keyArgs)
name|OmMultipartInfo
name|initiateMultipartUpload
parameter_list|(
name|OmKeyArgs
name|keyArgs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Commit Multipart upload part file.    * @param omKeyArgs    * @param clientID    * @return OmMultipartCommitUploadPartInfo    * @throws IOException    */
DECL|method|commitMultipartUploadPart ( OmKeyArgs omKeyArgs, long clientID)
name|OmMultipartCommitUploadPartInfo
name|commitMultipartUploadPart
parameter_list|(
name|OmKeyArgs
name|omKeyArgs
parameter_list|,
name|long
name|clientID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Complete Multipart upload Request.    * @param omKeyArgs    * @param multipartUploadList    * @return OmMultipartUploadCompleteInfo    * @throws IOException    */
DECL|method|completeMultipartUpload (OmKeyArgs omKeyArgs, OmMultipartUploadList multipartUploadList)
name|OmMultipartUploadCompleteInfo
name|completeMultipartUpload
parameter_list|(
name|OmKeyArgs
name|omKeyArgs
parameter_list|,
name|OmMultipartUploadList
name|multipartUploadList
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

