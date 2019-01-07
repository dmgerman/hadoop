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
name|OmBucketArgs
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
name|OmBucketInfo
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|ServiceInfo
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
name|OzoneAclInfo
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
comment|/**  * Protocol to talk to OM.  */
end_comment

begin_interface
DECL|interface|OzoneManagerProtocol
specifier|public
interface|interface
name|OzoneManagerProtocol
block|{
comment|/**    * Creates a volume.    * @param args - Arguments to create Volume.    * @throws IOException    */
DECL|method|createVolume (OmVolumeArgs args)
name|void
name|createVolume
parameter_list|(
name|OmVolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Changes the owner of a volume.    * @param volume  - Name of the volume.    * @param owner - Name of the owner.    * @throws IOException    */
DECL|method|setOwner (String volume, String owner)
name|void
name|setOwner
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
comment|/**    * Changes the Quota on a volume.    * @param volume - Name of the volume.    * @param quota - Quota in bytes.    * @throws IOException    */
DECL|method|setQuota (String volume, long quota)
name|void
name|setQuota
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
comment|/**    * Checks if the specified user can access this volume.    * @param volume - volume    * @param userAcl - user acls which needs to be checked for access    * @return true if the user has required access for the volume,    *         false otherwise    * @throws IOException    */
DECL|method|checkVolumeAccess (String volume, OzoneAclInfo userAcl)
name|boolean
name|checkVolumeAccess
parameter_list|(
name|String
name|volume
parameter_list|,
name|OzoneAclInfo
name|userAcl
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the volume information.    * @param volume - Volume name.    * @return VolumeArgs or exception is thrown.    * @throws IOException    */
DECL|method|getVolumeInfo (String volume)
name|OmVolumeArgs
name|getVolumeInfo
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an existing empty volume.    * @param volume - Name of the volume.    * @throws IOException    */
DECL|method|deleteVolume (String volume)
name|void
name|deleteVolume
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Lists volume owned by a specific user.    * @param userName - user name    * @param prefix  - Filter prefix -- Return only entries that match this.    * @param prevKey - Previous key -- List starts from the next from the prevkey    * @param maxKeys - Max number of keys to return.    * @return List of Volumes.    * @throws IOException    */
DECL|method|listVolumeByUser (String userName, String prefix, String prevKey, int maxKeys)
name|List
argument_list|<
name|OmVolumeArgs
argument_list|>
name|listVolumeByUser
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|prevKey
parameter_list|,
name|int
name|maxKeys
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Lists volume all volumes in the cluster.    * @param prefix  - Filter prefix -- Return only entries that match this.    * @param prevKey - Previous key -- List starts from the next from the prevkey    * @param maxKeys - Max number of keys to return.    * @return List of Volumes.    * @throws IOException    */
DECL|method|listAllVolumes (String prefix, String prevKey, int maxKeys)
name|List
argument_list|<
name|OmVolumeArgs
argument_list|>
name|listAllVolumes
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|prevKey
parameter_list|,
name|int
name|maxKeys
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a bucket.    * @param bucketInfo - BucketInfo to create Bucket.    * @throws IOException    */
DECL|method|createBucket (OmBucketInfo bucketInfo)
name|void
name|createBucket
parameter_list|(
name|OmBucketInfo
name|bucketInfo
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the bucket information.    * @param volumeName - Volume name.    * @param bucketName - Bucket name.    * @return OmBucketInfo or exception is thrown.    * @throws IOException    */
DECL|method|getBucketInfo (String volumeName, String bucketName)
name|OmBucketInfo
name|getBucketInfo
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Sets bucket property from args.    * @param args - BucketArgs.    * @throws IOException    */
DECL|method|setBucketProperty (OmBucketArgs args)
name|void
name|setBucketProperty
parameter_list|(
name|OmBucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Open the given key and return an open key session.    *    * @param args the args of the key.    * @return OpenKeySession instance that client uses to talk to container.    * @throws IOException    */
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
comment|/**    * Commit a key. This will make the change from the client visible. The client    * is identified by the clientID.    *    * @param args the key to commit    * @param clientID the client identification    * @throws IOException    */
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
comment|/**    * Allocate a new block, it is assumed that the client is having an open key    * session going on. This block will be appended to this open key session.    *    * @param args the key to append    * @param clientID the client identification    * @return an allocated block    * @throws IOException    */
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
comment|/**    * Look up for the container of an existing key.    *    * @param args the args of the key.    * @return OmKeyInfo instance that client uses to talk to container.    * @throws IOException    */
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
comment|/**    * Rename an existing key within a bucket.    * @param args the args of the key.    * @param toKeyName New name to be used for the Key    * @throws IOException    */
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
comment|/**    * Deletes an existing key.    *    * @param args the args of the key.    * @throws IOException    */
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
comment|/**    * Deletes an existing empty bucket from volume.    * @param volume - Name of the volume.    * @param bucket - Name of the bucket.    * @throws IOException    */
DECL|method|deleteBucket (String volume, String bucket)
name|void
name|deleteBucket
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a list of buckets represented by {@link OmBucketInfo}    * in the given volume. Argument volumeName is required, others    * are optional.    *    * @param volumeName    *   the name of the volume.    * @param startBucketName    *   the start bucket name, only the buckets whose name is    *   after this value will be included in the result.    * @param bucketPrefix    *   bucket name prefix, only the buckets whose name has    *   this prefix will be included in the result.    * @param maxNumOfBuckets    *   the maximum number of buckets to return. It ensures    *   the size of the result will not exceed this limit.    * @return a list of buckets.    * @throws IOException    */
DECL|method|listBuckets (String volumeName, String startBucketName, String bucketPrefix, int maxNumOfBuckets)
name|List
argument_list|<
name|OmBucketInfo
argument_list|>
name|listBuckets
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|startBucketName
parameter_list|,
name|String
name|bucketPrefix
parameter_list|,
name|int
name|maxNumOfBuckets
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a list of keys represented by {@link OmKeyInfo}    * in the given bucket. Argument volumeName, bucketName is required,    * others are optional.    *    * @param volumeName    *   the name of the volume.    * @param bucketName    *   the name of the bucket.    * @param startKeyName    *   the start key name, only the keys whose name is    *   after this value will be included in the result.    * @param keyPrefix    *   key name prefix, only the keys whose name has    *   this prefix will be included in the result.    * @param maxKeys    *   the maximum number of keys to return. It ensures    *   the size of the result will not exceed this limit.    * @return a list of keys.    * @throws IOException    */
DECL|method|listKeys (String volumeName, String bucketName, String startKeyName, String keyPrefix, int maxKeys)
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
name|startKeyName
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
comment|/**    * Returns list of Ozone services with its configuration details.    *    * @return list of Ozone services    * @throws IOException    */
DECL|method|getServiceList ()
name|List
argument_list|<
name|ServiceInfo
argument_list|>
name|getServiceList
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/*    * S3 Specific functionality that is supported by Ozone Manager.    */
comment|/**    * Creates an S3 bucket inside Ozone manager and creates the mapping needed    * to access via both S3 and Ozone.    * @param userName - S3 user name.    * @param s3BucketName - S3 bucket Name.    * @throws IOException - On failure, throws an exception like Bucket exists.    */
DECL|method|createS3Bucket (String userName, String s3BucketName)
name|void
name|createS3Bucket
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delets an S3 bucket inside Ozone manager and deletes the mapping.    * @param s3BucketName - S3 bucket Name.    * @throws IOException in case the bucket cannot be deleted.    */
DECL|method|deleteS3Bucket (String s3BucketName)
name|void
name|deleteS3Bucket
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the Ozone Namespace for the S3Bucket. It will return the    * OzoneVolume/OzoneBucketName.    * @param s3BucketName  - S3 Bucket Name.    * @return String - The Ozone canonical name for this s3 bucket. This    * string is useful for mounting an OzoneFS.    * @throws IOException - Error is throw if the s3bucket does not exist.    */
DECL|method|getOzoneBucketMapping (String s3BucketName)
name|String
name|getOzoneBucketMapping
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a list of buckets represented by {@link OmBucketInfo}    * for the given user. Argument username is required, others    * are optional.    *    * @param userName    *   user Name.    * @param startBucketName    *   the start bucket name, only the buckets whose name is    *   after this value will be included in the result.    * @param bucketPrefix    *   bucket name prefix, only the buckets whose name has    *   this prefix will be included in the result.    * @param maxNumOfBuckets    *   the maximum number of buckets to return. It ensures    *   the size of the result will not exceed this limit.    * @return a list of buckets.    * @throws IOException    */
DECL|method|listS3Buckets (String userName, String startBucketName, String bucketPrefix, int maxNumOfBuckets)
name|List
argument_list|<
name|OmBucketInfo
argument_list|>
name|listS3Buckets
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|startBucketName
parameter_list|,
name|String
name|bucketPrefix
parameter_list|,
name|int
name|maxNumOfBuckets
parameter_list|)
throws|throws
name|IOException
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
DECL|method|completeMultipartUpload ( OmKeyArgs omKeyArgs, OmMultipartUploadList multipartUploadList)
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

