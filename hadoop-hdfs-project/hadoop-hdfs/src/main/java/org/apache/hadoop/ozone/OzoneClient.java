begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|fs
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
name|OzoneConsts
operator|.
name|Versioning
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
name|io
operator|.
name|OzoneOutputStream
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
name|Iterator
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
comment|/**  * OzoneClient can connect to a Ozone Cluster and  * perform basic operations.  */
end_comment

begin_interface
DECL|interface|OzoneClient
specifier|public
interface|interface
name|OzoneClient
block|{
comment|/**    * Creates a new Volume.    *    * @param volumeName Name of the Volume    *    * @throws IOException    */
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
comment|/**    * Creates a new Volume, with owner set.    *    * @param volumeName Name of the Volume    * @param owner Owner to be set for Volume    *    * @throws IOException    */
DECL|method|createVolume (String volumeName, String owner)
name|void
name|createVolume
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
comment|/**    * Creates a new Volume, with owner and quota set.    *    * @param volumeName Name of the Volume    * @param owner Owner to be set for Volume    * @param acls ACLs to be added to the Volume    *    * @throws IOException    */
DECL|method|createVolume (String volumeName, String owner, OzoneAcl... acls)
name|void
name|createVolume
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|owner
parameter_list|,
name|OzoneAcl
modifier|...
name|acls
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a new Volume, with owner and quota set.    *    * @param volumeName Name of the Volume    * @param owner Owner to be set for Volume    * @param quota Volume Quota    *    * @throws IOException    */
DECL|method|createVolume (String volumeName, String owner, long quota)
name|void
name|createVolume
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|owner
parameter_list|,
name|long
name|quota
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a new Volume, with owner and quota set.    *    * @param volumeName Name of the Volume    * @param owner Owner to be set for Volume    * @param quota Volume Quota    * @param acls ACLs to be added to the Volume    *    * @throws IOException    */
DECL|method|createVolume (String volumeName, String owner, long quota, OzoneAcl... acls)
name|void
name|createVolume
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|owner
parameter_list|,
name|long
name|quota
parameter_list|,
name|OzoneAcl
modifier|...
name|acls
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Sets the owner of the volume.    *    * @param volumeName Name of the Volume    * @param owner to be set for the Volume    *    * @throws IOException    */
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
comment|/**    * Set Volume Quota.    *    * @param volumeName Name of the Volume    * @param quota Quota to be set for the Volume    *    * @throws IOException    */
DECL|method|setVolumeQuota (String volumeName, long quota)
name|void
name|setVolumeQuota
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|long
name|quota
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns {@link OzoneVolume}.    *    * @param volumeName Name of the Volume    *    * @return KsmVolumeArgs    *    * @throws OzoneVolume    * */
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
comment|/**    * Checks if a Volume exists and the user with a role specified has access    * to the Volume.    *    * @param volumeName Name of the Volume    * @param acl requested acls which needs to be checked for access    *    * @return Boolean - True if the user with a role can access the volume.    * This is possible for owners of the volume and admin users    *    * @throws IOException    */
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
comment|/**    * Deletes an Empty Volume.    *    * @param volumeName Name of the Volume    *    * @throws IOException    */
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
comment|/**    * Returns the List of Volumes owned by current user.    *    * @param volumePrefix Volume prefix to match    *    * @return KsmVolumeArgs Iterator    *    * @throws IOException    */
DECL|method|listVolumes (String volumePrefix)
name|Iterator
argument_list|<
name|OzoneVolume
argument_list|>
name|listVolumes
parameter_list|(
name|String
name|volumePrefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the List of Volumes owned by the specific user.    *    * @param volumePrefix Volume prefix to match    * @param user User Name    *    * @return KsmVolumeArgs Iterator    *    * @throws IOException    */
DECL|method|listVolumes (String volumePrefix, String user)
name|Iterator
argument_list|<
name|OzoneVolume
argument_list|>
name|listVolumes
parameter_list|(
name|String
name|volumePrefix
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a new Bucket in the Volume.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    *    * @throws IOException    */
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
comment|/**    * Creates a new Bucket in the Volume, with versioning set.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param versioning Bucket versioning    *    * @throws IOException    */
DECL|method|createBucket (String volumeName, String bucketName, Versioning versioning)
name|void
name|createBucket
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|Versioning
name|versioning
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a new Bucket in the Volume, with storage type set.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param storageType StorageType for the Bucket    *    * @throws IOException    */
DECL|method|createBucket (String volumeName, String bucketName, StorageType storageType)
name|void
name|createBucket
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
comment|/**    * Creates a new Bucket in the Volume, with ACLs set.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param acls OzoneAcls for the Bucket    *    * @throws IOException    */
DECL|method|createBucket (String volumeName, String bucketName, OzoneAcl... acls)
name|void
name|createBucket
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|OzoneAcl
modifier|...
name|acls
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a new Bucket in the Volume, with versioning    * storage type and ACLs set.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param storageType StorageType for the Bucket    *    * @throws IOException    */
DECL|method|createBucket (String volumeName, String bucketName, OzoneConsts.Versioning versioning, StorageType storageType, OzoneAcl... acls)
name|void
name|createBucket
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|OzoneConsts
operator|.
name|Versioning
name|versioning
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|OzoneAcl
modifier|...
name|acls
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Adds or Removes ACLs from a Bucket.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    *    * @throws IOException    */
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
comment|/**    * Adds or Removes ACLs from a Bucket.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    *    * @throws IOException    */
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
comment|/**    * Enables or disables Bucket Versioning.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    *    * @throws IOException    */
DECL|method|setBucketVersioning (String volumeName, String bucketName, Versioning versioning)
name|void
name|setBucketVersioning
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|Versioning
name|versioning
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Sets the Storage Class of a Bucket.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    *    * @throws IOException    */
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
comment|/**    * Deletes a bucket if it is empty.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    *    * @throws IOException    */
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
comment|/**    * true if the bucket exists and user has read access    * to the bucket else throws Exception.    *    * @param volumeName Name of the Volume    *    * @throws IOException    */
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
comment|/**      * Returns {@link OzoneBucket}.      *      * @param volumeName Name of the Volume      * @param bucketName Name of the Bucket      *      * @return OzoneBucket      *      * @throws IOException      */
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
comment|/**    * Returns the List of Buckets in the Volume.    *    * @param volumeName Name of the Volume    * @param bucketPrefix Bucket prefix to match    *    * @return KsmVolumeArgs Iterator    *    * @throws IOException    */
DECL|method|listBuckets (String volumeName, String bucketPrefix)
name|Iterator
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
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Writes a key in an existing bucket.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param size Size of the data    *    * @return OutputStream    *    */
DECL|method|createKey (String volumeName, String bucketName, String keyName, long size)
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
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Reads a key from an existing bucket.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    *    * @return LengthInputStream    *    * @throws IOException    */
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
comment|/**    * Deletes an existing key.    *    * @param volumeName Name of the Volume    *    * @throws IOException    */
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
comment|/**    * Returns list of {@link OzoneKey} in Volume/Bucket.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    *    * @return OzoneKey    *    * @throws IOException    */
DECL|method|listKeys (String volumeName, String bucketName, String keyPrefix)
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
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get OzoneKey.    *    * @param volumeName Name of the Volume    * @param bucketName Name of the Bucket    * @param keyName Key name    *    * @return OzoneKey    *    * @throws IOException    */
DECL|method|getKeyDetails (String volumeName, String bucketName, String keyName)
name|OzoneKey
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
comment|/**    * Close and release the resources.    */
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

