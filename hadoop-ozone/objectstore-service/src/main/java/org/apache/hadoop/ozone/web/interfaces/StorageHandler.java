begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.interfaces
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
name|interfaces
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
name|ozone
operator|.
name|client
operator|.
name|io
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
name|client
operator|.
name|rest
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
name|Closeable
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
comment|/**  * Storage handler Interface is the Interface between  * REST protocol and file system.  *  * We will have two default implementations of this interface.  * One for the local file system that is handy while testing  * and another which will point to the HDFS backend.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|StorageHandler
specifier|public
interface|interface
name|StorageHandler
extends|extends
name|Closeable
block|{
comment|/**    * Creates a Storage Volume.    *    * @param args - Volume Name    *    * @throws IOException    * @throws OzoneException    */
DECL|method|createVolume (VolumeArgs args)
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
function_decl|;
comment|/**    * setVolumeOwner - sets the owner of the volume.    *    * @param args owner info is present in the args    *    * @throws IOException    * @throws OzoneException    */
DECL|method|setVolumeOwner (VolumeArgs args)
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
function_decl|;
comment|/**    * Set Volume Quota.    *    * @param args - Has Quota info    * @param remove - true if the request is to remove the quota    *    * @throws IOException    * @throws OzoneException    */
DECL|method|setVolumeQuota (VolumeArgs args, boolean remove)
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
function_decl|;
comment|/**    * Checks if a Volume exists and the user with a role specified has access    * to the Volume.    *    * @param volume - Volume Name whose access permissions needs to be checked    * @param acl - requested acls which needs to be checked for access    *    * @return - Boolean - True if the user with a role can access the volume.    * This is possible for owners of the volume and admin users    *    * @throws IOException    * @throws OzoneException    */
DECL|method|checkVolumeAccess (String volume, OzoneAcl acl)
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
function_decl|;
comment|/**    * Returns the List of Volumes owned by the specific user.    *    * @param args - ListArgs    *    * @return - List of Volumes    *    * @throws IOException    * @throws OzoneException    */
DECL|method|listVolumes (ListArgs args)
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
function_decl|;
comment|/**    * Deletes an Empty Volume.    *    * @param args - Volume Args    *    * @throws IOException    * @throws OzoneException    */
DECL|method|deleteVolume (VolumeArgs args)
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
function_decl|;
comment|/**    * Returns Info about the specified Volume.    *    * @param args - Volume Args    *    * @return VolumeInfo    *    * @throws IOException    * @throws OzoneException    */
DECL|method|getVolumeInfo (VolumeArgs args)
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
function_decl|;
comment|/**    * Creates a Bucket in specified Volume.    *    * @param args BucketArgs- BucketName, UserName and Acls    *    * @throws IOException    */
DECL|method|createBucket (BucketArgs args)
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
function_decl|;
comment|/**    * Adds or Removes ACLs from a Bucket.    *    * @param args - BucketArgs    *    * @throws IOException    */
DECL|method|setBucketAcls (BucketArgs args)
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
function_decl|;
comment|/**    * Enables or disables Bucket Versioning.    *    * @param args - BucketArgs    *    * @throws IOException    */
DECL|method|setBucketVersioning (BucketArgs args)
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
function_decl|;
comment|/**    * Sets the Storage Class of a Bucket.    *    * @param args - BucketArgs    *    * @throws IOException    */
DECL|method|setBucketStorageClass (BucketArgs args)
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
function_decl|;
comment|/**    * Deletes a bucket if it is empty.    *    * @param args Bucket args structure    *    * @throws IOException    */
DECL|method|deleteBucket (BucketArgs args)
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
function_decl|;
comment|/**    * true if the bucket exists and user has read access    * to the bucket else throws Exception.    *    * @param args Bucket args structure    *    * @throws IOException    */
DECL|method|checkBucketAccess (BucketArgs args)
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
function_decl|;
comment|/**    * Returns all Buckets of a specified Volume.    *    * @param listArgs -- List Args.    *    * @return ListAllBuckets    *    * @throws OzoneException    */
DECL|method|listBuckets (ListArgs listArgs)
name|ListBuckets
name|listBuckets
parameter_list|(
name|ListArgs
name|listArgs
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
function_decl|;
comment|/**    * Returns Bucket's Metadata as a String.    *    * @param args Bucket args structure    *    * @return Info about the bucket    *    * @throws IOException    */
DECL|method|getBucketInfo (BucketArgs args)
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
function_decl|;
comment|/**    * Writes a key in an existing bucket.    *    * @param args KeyArgs    *    * @return InputStream    *    * @throws OzoneException    */
DECL|method|newKeyWriter (KeyArgs args)
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
function_decl|;
comment|/**    * Tells the file system that the object has been written out    * completely and it can do any house keeping operation that needs    * to be done.    *    * @param args Key Args    *    * @param stream    * @throws IOException    */
DECL|method|commitKey (KeyArgs args, OutputStream stream)
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
function_decl|;
comment|/**    * Reads a key from an existing bucket.    *    * @param args KeyArgs    *    * @return LengthInputStream    *    * @throws IOException    */
DECL|method|newKeyReader (KeyArgs args)
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
function_decl|;
comment|/**    * Deletes an existing key.    *    * @param args KeyArgs    *    * @throws OzoneException    */
DECL|method|deleteKey (KeyArgs args)
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
function_decl|;
comment|/**    * Renames an existing key within a bucket.    *    * @param args KeyArgs    * @param toKeyName New name to be used for the key    * @throws OzoneException    */
DECL|method|renameKey (KeyArgs args, String toKeyName)
name|void
name|renameKey
parameter_list|(
name|KeyArgs
name|args
parameter_list|,
name|String
name|toKeyName
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
function_decl|;
comment|/**    * Returns a list of Key.    *    * @param args KeyArgs    *    * @return BucketList    *    * @throws IOException    */
DECL|method|listKeys (ListArgs args)
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
function_decl|;
comment|/**    * Get information of the specified Key.    *    * @param args Key Args    *    * @return KeyInfo    *    * @throws IOException    * @throws OzoneException    */
DECL|method|getKeyInfo (KeyArgs args)
name|KeyInfo
name|getKeyInfo
parameter_list|(
name|KeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
function_decl|;
comment|/**    * Get detail information of the specified Key.    *    * @param args Key Args    *    * @return KeyInfo    *    * @throws IOException    * @throws OzoneException    */
DECL|method|getKeyInfoDetails (KeyArgs args)
name|KeyInfo
name|getKeyInfoDetails
parameter_list|(
name|KeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
function_decl|;
comment|/**    * Closes all the opened resources.    */
DECL|method|close ()
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

