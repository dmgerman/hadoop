begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ksm.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ksm
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmBucketArgs
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmBucketInfo
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyArgs
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyInfo
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmVolumeArgs
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
name|KeySpaceManagerProtocolProtos
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
comment|/**  * Protocol to talk to KSM.  */
end_comment

begin_interface
DECL|interface|KeySpaceManagerProtocol
specifier|public
interface|interface
name|KeySpaceManagerProtocol
block|{
comment|/**    * Creates a volume.    * @param args - Arguments to create Volume.    * @throws IOException    */
DECL|method|createVolume (KsmVolumeArgs args)
name|void
name|createVolume
parameter_list|(
name|KsmVolumeArgs
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
name|KsmVolumeArgs
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
DECL|method|listVolumeByUser (String userName, String prefix, String prevKey, long maxKeys)
name|List
argument_list|<
name|KsmVolumeArgs
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
name|long
name|maxKeys
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Lists volume all volumes in the cluster.    * @param prefix  - Filter prefix -- Return only entries that match this.    * @param prevKey - Previous key -- List starts from the next from the prevkey    * @param maxKeys - Max number of keys to return.    * @return List of Volumes.    * @throws IOException    */
DECL|method|listAllVolumes (String prefix, String prevKey, long maxKeys)
name|List
argument_list|<
name|KsmVolumeArgs
argument_list|>
name|listAllVolumes
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|prevKey
parameter_list|,
name|long
name|maxKeys
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a bucket.    * @param bucketInfo - BucketInfo to create Bucket.    * @throws IOException    */
DECL|method|createBucket (KsmBucketInfo bucketInfo)
name|void
name|createBucket
parameter_list|(
name|KsmBucketInfo
name|bucketInfo
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the bucket information.    * @param volumeName - Volume name.    * @param bucketName - Bucket name.    * @return KsmBucketInfo or exception is thrown.    * @throws IOException    */
DECL|method|getBucketInfo (String volumeName, String bucketName)
name|KsmBucketInfo
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
DECL|method|setBucketProperty (KsmBucketArgs args)
name|void
name|setBucketProperty
parameter_list|(
name|KsmBucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Allocate a block to a container, the block is returned to the client.    *    * @param args the args of the key.    * @return KsmKeyInfo isntacne that client uses to talk to container.    * @throws IOException    */
DECL|method|allocateKey (KsmKeyArgs args)
name|KsmKeyInfo
name|allocateKey
parameter_list|(
name|KsmKeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Look up for the container of an existing key.    *    * @param args the args of the key.    * @return KsmKeyInfo isntacne that client uses to talk to container.    * @throws IOException    */
DECL|method|lookupKey (KsmKeyArgs args)
name|KsmKeyInfo
name|lookupKey
parameter_list|(
name|KsmKeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an existing key.    *    * @param args the args of the key.    * @throws IOException    */
DECL|method|deleteKey (KsmKeyArgs args)
name|void
name|deleteKey
parameter_list|(
name|KsmKeyArgs
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
comment|/**    * Returns a list of buckets represented by {@link KsmBucketInfo}    * in the given volume. Argument volumeName is required, others    * are optional.    *    * @param volumeName    *   the name of the volume.    * @param startBucketName    *   the start bucket name, only the buckets whose name is    *   after this value will be included in the result.    * @param bucketPrefix    *   bucket name prefix, only the buckets whose name has    *   this prefix will be included in the result.    * @param maxNumOfBuckets    *   the maximum number of buckets to return. It ensures    *   the size of the result will not exceed this limit.    * @return a list of buckets.    * @throws IOException    */
DECL|method|listBuckets (String volumeName, String startBucketName, String bucketPrefix, int maxNumOfBuckets)
name|List
argument_list|<
name|KsmBucketInfo
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
comment|/**    * Returns a list of keys represented by {@link KsmKeyInfo}    * in the given bucket. Argument volumeName, bucketName is required,    * others are optional.    *    * @param volumeName    *   the name of the volume.    * @param bucketName    *   the name of the bucket.    * @param startKeyName    *   the start key name, only the keys whose name is    *   after this value will be included in the result.    * @param keyPrefix    *   key name prefix, only the keys whose name has    *   this prefix will be included in the result.    * @param maxKeys    *   the maximum number of keys to return. It ensures    *   the size of the result will not exceed this limit.    * @return a list of keys.    * @throws IOException    */
DECL|method|listKeys (String volumeName, String bucketName, String startKeyName, String keyPrefix, int maxKeys)
name|List
argument_list|<
name|KsmKeyInfo
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
block|}
end_interface

end_unit

