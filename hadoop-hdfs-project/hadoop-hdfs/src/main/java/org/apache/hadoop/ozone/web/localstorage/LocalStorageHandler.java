begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.localstorage
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|localstorage
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
name|classification
operator|.
name|InterfaceAudience
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
name|conf
operator|.
name|Configuration
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|LengthInputStream
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
name|web
operator|.
name|exceptions
operator|.
name|OzoneException
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
name|web
operator|.
name|handlers
operator|.
name|BucketArgs
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
name|web
operator|.
name|handlers
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
name|web
operator|.
name|handlers
operator|.
name|ListArgs
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
name|web
operator|.
name|handlers
operator|.
name|VolumeArgs
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
name|web
operator|.
name|interfaces
operator|.
name|StorageHandler
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
name|web
operator|.
name|request
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
name|web
operator|.
name|request
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
name|ozone
operator|.
name|web
operator|.
name|response
operator|.
name|BucketInfo
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
name|web
operator|.
name|response
operator|.
name|ListBuckets
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
name|web
operator|.
name|response
operator|.
name|ListKeys
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
name|web
operator|.
name|response
operator|.
name|ListVolumes
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
name|web
operator|.
name|response
operator|.
name|VolumeInfo
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * PLEASE NOTE : This file is a dummy backend for test purposes and prototyping  * effort only. It does not handle any Object semantics correctly, neither does  * it take care of security.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|LocalStorageHandler
specifier|public
class|class
name|LocalStorageHandler
implements|implements
name|StorageHandler
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
comment|/**    * Constructs LocalStorageHandler.    *    * @param conf ozone conf.    */
DECL|method|LocalStorageHandler (Configuration conf)
specifier|public
name|LocalStorageHandler
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
comment|/**    * Creates Storage Volume.    *    * @param args - volumeArgs    * @throws IOException    */
annotation|@
name|Override
DECL|method|createVolume (VolumeArgs args)
specifier|public
name|void
name|createVolume
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|oz
operator|.
name|createVolume
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * setVolumeOwner - sets the owner of the volume.    *    * @param args volumeArgs    * @throws IOException    */
annotation|@
name|Override
DECL|method|setVolumeOwner (VolumeArgs args)
specifier|public
name|void
name|setVolumeOwner
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|oz
operator|.
name|setVolumeProperty
argument_list|(
name|args
argument_list|,
name|OzoneMetadataManager
operator|.
name|VolumeProperty
operator|.
name|OWNER
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set Volume Quota Info.    *    * @param args   - volumeArgs    * @param remove - true if the request is to remove the quota    * @throws IOException    */
annotation|@
name|Override
DECL|method|setVolumeQuota (VolumeArgs args, boolean remove)
specifier|public
name|void
name|setVolumeQuota
parameter_list|(
name|VolumeArgs
name|args
parameter_list|,
name|boolean
name|remove
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|remove
condition|)
block|{
name|OzoneQuota
name|quota
init|=
operator|new
name|OzoneQuota
argument_list|()
decl_stmt|;
name|args
operator|.
name|setQuota
argument_list|(
name|quota
argument_list|)
expr_stmt|;
block|}
name|oz
operator|.
name|setVolumeProperty
argument_list|(
name|args
argument_list|,
name|OzoneMetadataManager
operator|.
name|VolumeProperty
operator|.
name|QUOTA
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks if a Volume exists and the user specified has access to the volume.    *    * @param volume - Volume Name    * @param acl - Ozone acl which needs to be compared for access    * @return - Boolean - True if the user can modify the volume. This is    * possible for owners of the volume and admin users    * @throws IOException    */
annotation|@
name|Override
DECL|method|checkVolumeAccess (String volume, OzoneAcl acl)
specifier|public
name|boolean
name|checkVolumeAccess
parameter_list|(
name|String
name|volume
parameter_list|,
name|OzoneAcl
name|acl
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
name|oz
operator|.
name|checkVolumeAccess
argument_list|(
name|volume
argument_list|,
name|acl
argument_list|)
return|;
block|}
comment|/**    * Returns Info about the specified Volume.    *    * @param args - volumeArgs    * @return VolumeInfo    * @throws IOException    */
annotation|@
name|Override
DECL|method|getVolumeInfo (VolumeArgs args)
specifier|public
name|VolumeInfo
name|getVolumeInfo
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
name|oz
operator|.
name|getVolumeInfo
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Deletes an Empty Volume.    *    * @param args - Volume Args    * @throws IOException    */
annotation|@
name|Override
DECL|method|deleteVolume (VolumeArgs args)
specifier|public
name|void
name|deleteVolume
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|oz
operator|.
name|deleteVolume
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the List of Volumes owned by the specific user.    *    * @param args - ListArgs    * @return - List of Volumes    * @throws IOException    */
annotation|@
name|Override
DECL|method|listVolumes (ListArgs args)
specifier|public
name|ListVolumes
name|listVolumes
parameter_list|(
name|ListArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
name|oz
operator|.
name|listVolumes
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * true if the bucket exists and user has read access to the bucket else    * throws Exception.    *    * @param args Bucket args structure    * @throws IOException    */
annotation|@
name|Override
DECL|method|checkBucketAccess (BucketArgs args)
specifier|public
name|void
name|checkBucketAccess
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{    }
comment|/**    * Creates a Bucket in specified Volume.    *    * @param args BucketArgs- BucketName, UserName and Acls    * @throws IOException    */
annotation|@
name|Override
DECL|method|createBucket (BucketArgs args)
specifier|public
name|void
name|createBucket
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|oz
operator|.
name|createBucket
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds or Removes ACLs from a Bucket.    *    * @param args - BucketArgs    * @throws IOException    */
annotation|@
name|Override
DECL|method|setBucketAcls (BucketArgs args)
specifier|public
name|void
name|setBucketAcls
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|oz
operator|.
name|setBucketProperty
argument_list|(
name|args
argument_list|,
name|OzoneMetadataManager
operator|.
name|BucketProperty
operator|.
name|ACLS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Enables or disables Bucket Versioning.    *    * @param args - BucketArgs    * @throws IOException    */
annotation|@
name|Override
DECL|method|setBucketVersioning (BucketArgs args)
specifier|public
name|void
name|setBucketVersioning
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|oz
operator|.
name|setBucketProperty
argument_list|(
name|args
argument_list|,
name|OzoneMetadataManager
operator|.
name|BucketProperty
operator|.
name|VERSIONING
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the Storage Class of a Bucket.    *    * @param args - BucketArgs    * @throws IOException    */
annotation|@
name|Override
DECL|method|setBucketStorageClass (BucketArgs args)
specifier|public
name|void
name|setBucketStorageClass
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|oz
operator|.
name|setBucketProperty
argument_list|(
name|args
argument_list|,
name|OzoneMetadataManager
operator|.
name|BucketProperty
operator|.
name|STORAGETYPE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletes a bucket if it is empty.    *    * @param args Bucket args structure    * @throws IOException    */
annotation|@
name|Override
DECL|method|deleteBucket (BucketArgs args)
specifier|public
name|void
name|deleteBucket
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|oz
operator|.
name|deleteBucket
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns all Buckets of a specified Volume.    *    * @param args --User Args    * @return ListAllBuckets    * @throws OzoneException    */
annotation|@
name|Override
DECL|method|listBuckets (ListArgs args)
specifier|public
name|ListBuckets
name|listBuckets
parameter_list|(
name|ListArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
name|oz
operator|.
name|listBuckets
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Returns Bucket's Metadata as a String.    *    * @param args Bucket args structure    * @return Info about the bucket    * @throws IOException    */
annotation|@
name|Override
DECL|method|getBucketInfo (BucketArgs args)
specifier|public
name|BucketInfo
name|getBucketInfo
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
name|oz
operator|.
name|getBucketInfo
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Writes a key in an existing bucket.    *    * @param args KeyArgs    * @return InputStream    * @throws OzoneException    */
annotation|@
name|Override
DECL|method|newKeyWriter (KeyArgs args)
specifier|public
name|OutputStream
name|newKeyWriter
parameter_list|(
name|KeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
name|oz
operator|.
name|createKey
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Tells the file system that the object has been written out completely and    * it can do any house keeping operation that needs to be done.    *    * @param args   Key Args    * @param stream    * @throws IOException    */
annotation|@
name|Override
DECL|method|commitKey (KeyArgs args, OutputStream stream)
specifier|public
name|void
name|commitKey
parameter_list|(
name|KeyArgs
name|args
parameter_list|,
name|OutputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|oz
operator|.
name|commitKey
argument_list|(
name|args
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads a key from an existing bucket.    *    * @param args KeyArgs    * @return LengthInputStream    * @throws IOException    */
annotation|@
name|Override
DECL|method|newKeyReader (KeyArgs args)
specifier|public
name|LengthInputStream
name|newKeyReader
parameter_list|(
name|KeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
name|oz
operator|.
name|newKeyReader
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Deletes an existing key.    *    * @param args KeyArgs    * @throws OzoneException    */
annotation|@
name|Override
DECL|method|deleteKey (KeyArgs args)
specifier|public
name|void
name|deleteKey
parameter_list|(
name|KeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|oz
operator|.
name|deleteKey
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a list of Key.    *    * @param args KeyArgs    * @return BucketList    * @throws IOException    */
annotation|@
name|Override
DECL|method|listKeys (ListArgs args)
specifier|public
name|ListKeys
name|listKeys
parameter_list|(
name|ListArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|OzoneMetadataManager
name|oz
init|=
name|OzoneMetadataManager
operator|.
name|getOzoneMetadataManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
name|oz
operator|.
name|listKeys
argument_list|(
name|args
argument_list|)
return|;
block|}
block|}
end_class

end_unit

