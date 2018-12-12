begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
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
name|hdds
operator|.
name|protocol
operator|.
name|StorageType
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
name|OzoneAcl
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
name|client
operator|.
name|*
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
name|client
operator|.
name|OzoneQuota
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
name|client
operator|.
name|ReplicationFactor
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
name|client
operator|.
name|ReplicationType
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
name|client
operator|.
name|io
operator|.
name|OzoneInputStream
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
name|client
operator|.
name|io
operator|.
name|OzoneOutputStream
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
comment|/**  * An implementer of this interface is capable of connecting to Ozone Cluster  * and perform client operations. The protocol used for communication is  * determined by the implementation class specified by  * property<code>ozone.client.protocol</code>. The build-in implementation  * includes: {@link org.apache.hadoop.ozone.client.rpc.RpcClient} for RPC and  * {@link  org.apache.hadoop.ozone.client.rest.RestClient} for REST.  */
end_comment

begin_interface
DECL|interface|ClientProtocol
specifier|public
interface|interface
name|ClientProtocol
block|{
comment|/**    * Creates a new Volume.    * @param volumeName Name of the Volume    * @throws IOException    */
DECL|method|createVolume (String volumeName)
name|void
name|createVolume
parameter_list|(
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a new Volume with properties set in VolumeArgs.    * @param volumeName Name of the Volume    * @param args Properties to be set for the Volume    * @throws IOException    */
DECL|method|createVolume (String volumeName, VolumeArgs args)
name|void
name|createVolume
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Sets the owner of volume.    * @param volumeName Name of the Volume    * @param owner to be set for the Volume    * @throws IOException    */
DECL|method|setVolumeOwner (String volumeName, String owner)
name|void
name|setVolumeOwner
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Set Volume Quota.    * @param volumeName Name of the Volume    * @param quota Quota to be set for the Volume    * @throws IOException    */
DECL|method|setVolumeQuota (String volumeName, OzoneQuota quota)
name|void
name|setVolumeQuota
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|OzoneQuota
name|quota
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns {@link OzoneVolume}.    * @param volumeName Name of the Volume    * @return {@link OzoneVolume}    * @throws IOException    * */
DECL|method|getVolumeDetails (String volumeName)
name|OzoneVolume
name|getVolumeDetails
parameter_list|(
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks if a Volume exists and the user with a role specified has access    * to the Volume.    * @param volumeName Name of the Volume    * @param acl requested acls which needs to be checked for access    * @return Boolean - True if the user with a role can access the volume.    * This is possible for owners of the volume and admin users    * @throws IOException    */
DECL|method|checkVolumeAccess (String volumeName, OzoneAcl acl)
name|boolean
name|checkVolumeAccess
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|OzoneAcl
name|acl
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an empty Volume.    * @param volumeName Name of the Volume    * @throws IOException    */
DECL|method|deleteVolume (String volumeName)
name|void
name|deleteVolume
parameter_list|(
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Lists all volumes in the cluster that matches the volumePrefix,    * size of the returned list depends on maxListResult. If volume prefix    * is null, returns all the volumes. The caller has to make multiple calls    * to read all volumes.    *    * @param volumePrefix Volume prefix to match    * @param prevVolume Starting point of the list, this volume is excluded    * @param maxListResult Max number of volumes to return.    * @return {@code List<OzoneVolume>}    * @throws IOException    */
DECL|method|listVolumes (String volumePrefix, String prevVolume, int maxListResult)
name|List
argument_list|<
name|OzoneVolume
argument_list|>
name|listVolumes
parameter_list|(
name|String
name|volumePrefix
parameter_list|,
name|String
name|prevVolume
parameter_list|,
name|int
name|maxListResult
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Lists all volumes in the cluster that are owned by the specified    * user and matches the volumePrefix, size of the returned list depends on    * maxListResult. If the user is null, return volumes owned by current user.    * If volume prefix is null, returns all the volumes. The caller has to make    * multiple calls to read all volumes.    *    * @param user User Name    * @param volumePrefix Volume prefix to match    * @param prevVolume Starting point of the list, this volume is excluded    * @param maxListResult Max number of volumes to return.    * @return {@code List<OzoneVolume>}    * @throws IOException    */
DECL|method|listVolumes (String user, String volumePrefix, String prevVolume, int maxListResult)
name|List
argument_list|<
name|OzoneVolume
argument_list|>
name|listVolumes
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|volumePrefix
parameter_list|,
name|String
name|prevVolume
parameter_list|,
name|int
name|maxListResult
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a new Bucket in the Volume.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @throws IOException    */
DECL|method|createBucket (String volumeName, String bucketName)
name|void
name|createBucket
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
comment|/**    * Creates a new Bucket in the Volume, with properties set in BucketArgs.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param bucketArgs Bucket Arguments    * @throws IOException    */
DECL|method|createBucket (String volumeName, String bucketName, BucketArgs bucketArgs)
name|void
name|createBucket
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|BucketArgs
name|bucketArgs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Adds ACLs to the Bucket.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param addAcls ACLs to be added    * @throws IOException    */
DECL|method|addBucketAcls (String volumeName, String bucketName, List<OzoneAcl> addAcls)
name|void
name|addBucketAcls
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|addAcls
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Removes ACLs from a Bucket.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param removeAcls ACLs to be removed    * @throws IOException    */
DECL|method|removeBucketAcls (String volumeName, String bucketName, List<OzoneAcl> removeAcls)
name|void
name|removeBucketAcls
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|removeAcls
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Enables or disables Bucket Versioning.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param versioning True to enable Versioning, False to disable.    * @throws IOException    */
DECL|method|setBucketVersioning (String volumeName, String bucketName, Boolean versioning)
name|void
name|setBucketVersioning
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|Boolean
name|versioning
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Sets the Storage Class of a Bucket.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param storageType StorageType to be set    * @throws IOException    */
DECL|method|setBucketStorageType (String volumeName, String bucketName, StorageType storageType)
name|void
name|setBucketStorageType
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|StorageType
name|storageType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes a bucket if it is empty.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @throws IOException    */
DECL|method|deleteBucket (String volumeName, String bucketName)
name|void
name|deleteBucket
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
comment|/**    * True if the bucket exists and user has read access    * to the bucket else throws Exception.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @throws IOException    */
DECL|method|checkBucketAccess (String volumeName, String bucketName)
name|void
name|checkBucketAccess
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
comment|/**    * Returns {@link OzoneBucket}.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @return {@link OzoneBucket}    * @throws IOException    */
DECL|method|getBucketDetails (String volumeName, String bucketName)
name|OzoneBucket
name|getBucketDetails
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
comment|/**    * Returns the List of Buckets in the Volume that matches the bucketPrefix,    * size of the returned list depends on maxListResult. The caller has to make    * multiple calls to read all volumes.    * @param volumeName Name of the Volume    * @param bucketPrefix Bucket prefix to match    * @param prevBucket Starting point of the list, this bucket is excluded    * @param maxListResult Max number of buckets to return.    * @return {@code List<OzoneBucket>}    * @throws IOException    */
DECL|method|listBuckets (String volumeName, String bucketPrefix, String prevBucket, int maxListResult)
name|List
argument_list|<
name|OzoneBucket
argument_list|>
name|listBuckets
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketPrefix
parameter_list|,
name|String
name|prevBucket
parameter_list|,
name|int
name|maxListResult
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Writes a key in an existing bucket.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param keyName Name of the Key    * @param size Size of the data    * @return {@link OzoneOutputStream}    *    */
DECL|method|createKey (String volumeName, String bucketName, String keyName, long size, ReplicationType type, ReplicationFactor factor)
name|OzoneOutputStream
name|createKey
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|,
name|long
name|size
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Reads a key from an existing bucket.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param keyName Name of the Key    * @return {@link OzoneInputStream}    * @throws IOException    */
DECL|method|getKey (String volumeName, String bucketName, String keyName)
name|OzoneInputStream
name|getKey
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an existing key.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param keyName Name of the Key    * @throws IOException    */
DECL|method|deleteKey (String volumeName, String bucketName, String keyName)
name|void
name|deleteKey
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Renames an existing key within a bucket.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param fromKeyName Name of the Key to be renamed    * @param toKeyName New name to be used for the Key    * @throws IOException    */
DECL|method|renameKey (String volumeName, String bucketName, String fromKeyName, String toKeyName)
name|void
name|renameKey
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|fromKeyName
parameter_list|,
name|String
name|toKeyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns list of Keys in {Volume/Bucket} that matches the keyPrefix,    * size of the returned list depends on maxListResult. The caller has    * to make multiple calls to read all keys.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param keyPrefix Bucket prefix to match    * @param prevKey Starting point of the list, this key is excluded    * @param maxListResult Max number of buckets to return.    * @return {@code List<OzoneKey>}    * @throws IOException    */
DECL|method|listKeys (String volumeName, String bucketName, String keyPrefix, String prevKey, int maxListResult)
name|List
argument_list|<
name|OzoneKey
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
name|keyPrefix
parameter_list|,
name|String
name|prevKey
parameter_list|,
name|int
name|maxListResult
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get OzoneKey.    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param keyName Key name    * @return {@link OzoneKey}    * @throws IOException    */
DECL|method|getKeyDetails (String volumeName, String bucketName, String keyName)
name|OzoneKeyDetails
name|getKeyDetails
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
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
comment|/**    * Deletes an s3 bucket and removes mapping of Ozone volume/bucket.    * @param bucketName - S3 Bucket Name.    * @throws  IOException in case the bucket cannot be deleted.    */
DECL|method|deleteS3Bucket (String bucketName)
name|void
name|deleteS3Bucket
parameter_list|(
name|String
name|bucketName
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
comment|/**    * Returns the corresponding Ozone volume given an S3 Bucket.    * @param s3BucketName - S3Bucket Name.    * @return String - Ozone Volume name.    * @throws IOException - Throws if the s3Bucket does not exist.    */
DECL|method|getOzoneVolumeName (String s3BucketName)
name|String
name|getOzoneVolumeName
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the corresponding Ozone bucket name for the given S3 bucket.    * @param s3BucketName - S3Bucket Name.    * @return String - Ozone bucket Name.    * @throws IOException - Throws if the s3bucket does not exist.    */
DECL|method|getOzoneBucketName (String s3BucketName)
name|String
name|getOzoneBucketName
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns Iterator to iterate over all buckets after prevBucket for a    * specific user. If prevBucket is null it returns an iterator to iterate over    * all the buckets of a user. The result can be restricted using bucket    * prefix, will return all buckets if bucket prefix is null.    *    * @param userName user name    * @param bucketPrefix Bucket prefix to match    * @param prevBucket Buckets are listed after this bucket    * @return {@code Iterator<OzoneBucket>}    * @throws IOException    */
DECL|method|listS3Buckets (String userName, String bucketPrefix, String prevBucket, int maxListResult)
name|List
argument_list|<
name|OzoneBucket
argument_list|>
name|listS3Buckets
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|bucketPrefix
parameter_list|,
name|String
name|prevBucket
parameter_list|,
name|int
name|maxListResult
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close and release the resources.    */
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Initiate Multipart upload.    * @param volumeName    * @param bucketName    * @param keyName    * @param type    * @param factor    * @return {@link OmMultipartInfo}    * @throws IOException    */
DECL|method|initiateMultipartUpload (String volumeName, String bucketName, String keyName, ReplicationType type, ReplicationFactor factor)
name|OmMultipartInfo
name|initiateMultipartUpload
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|createMultipartKey (String volumeName, String bucketName, String keyName, long size, int partNumber, String uploadID)
name|OzoneOutputStream
name|createMultipartKey
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|,
name|long
name|size
parameter_list|,
name|int
name|partNumber
parameter_list|,
name|String
name|uploadID
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

