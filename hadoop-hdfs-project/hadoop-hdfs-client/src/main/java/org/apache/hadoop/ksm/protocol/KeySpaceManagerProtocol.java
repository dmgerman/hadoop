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
comment|/**    * Checks if the specified user can access this volume.    * @param volume - volume    * @param userName - user name    * @throws IOException    */
DECL|method|checkVolumeAccess (String volume, String userName)
name|void
name|checkVolumeAccess
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|userName
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
comment|/**    * Allocate a block to a container, the block is returned to the client.    */
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
block|}
end_interface

end_unit

