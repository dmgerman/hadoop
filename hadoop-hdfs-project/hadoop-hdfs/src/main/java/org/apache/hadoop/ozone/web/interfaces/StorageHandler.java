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
name|UserArgs
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

begin_comment
comment|/**  * Storage handler Interface is the Interface between  * REST protocol and file system.  *  * We will have two default implementations of this interface.  * One for the local file system that is handy while testing  * and another which will point to the HDFS backend.  */
end_comment

begin_interface
DECL|interface|StorageHandler
specifier|public
interface|interface
name|StorageHandler
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
comment|/**    * Checks if a Volume exists and the user specified has access to the    * Volume.    *    * @param args - Volume Args    *    * @return - Boolean - True if the user can modify the volume.    * This is possible for owners of the volume and admin users    *    * @throws IOException    * @throws OzoneException    */
DECL|method|checkVolumeAccess (VolumeArgs args)
name|boolean
name|checkVolumeAccess
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
function_decl|;
comment|/**    * Returns the List of Volumes owned by the specific user.    *    * @param args - UserArgs    *    * @return - List of Volumes    *    * @throws IOException    * @throws OzoneException    */
DECL|method|listVolumes (UserArgs args)
name|ListVolumes
name|listVolumes
parameter_list|(
name|UserArgs
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
block|}
end_interface

end_unit

