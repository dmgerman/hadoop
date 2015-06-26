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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|StorageContainerConfiguration
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
comment|/**  * PLEASE NOTE : This file is a dummy backend for test purposes  * and prototyping effort only. It does not handle any Object semantics  * correctly, neither does it take care of security.  */
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
DECL|field|storageRoot
specifier|private
name|String
name|storageRoot
init|=
literal|null
decl_stmt|;
comment|/**    * Constructs LocalStorageHandler.    */
DECL|method|LocalStorageHandler ()
specifier|public
name|LocalStorageHandler
parameter_list|()
block|{
name|StorageContainerConfiguration
name|conf
init|=
operator|new
name|StorageContainerConfiguration
argument_list|()
decl_stmt|;
name|storageRoot
operator|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_STORAGE_LOCAL_ROOT
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_STORAGE_LOCAL_ROOT_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates Storage Volume.    *    * @param args - volumeArgs    *    * @throws IOException    */
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
block|{   }
comment|/**    * setVolumeOwner - sets the owner of the volume.    *    * @param args volumeArgs    *    * @throws IOException    */
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
block|{   }
comment|/**    * Set Volume Quota Info.    *    * @param args - volumeArgs    * @param remove - true if the request is to remove the quota    *    * @throws IOException    */
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
block|{   }
comment|/**    * Checks if a Volume exists and the user specified has access to the    * volume.    *    * @param args - volumeArgs    *    * @return - Boolean - True if the user can modify the volume.    * This is possible for owners of the volume and admin users    *    * @throws FileSystemException    */
annotation|@
name|Override
DECL|method|checkVolumeAccess (VolumeArgs args)
specifier|public
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
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Returns Info about the specified Volume.    *    * @param args - volumeArgs    *    * @return VolumeInfo    *    * @throws IOException    */
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
return|return
literal|null
return|;
block|}
comment|/**    * Deletes an Empty Volume.    *    * @param args - Volume Args    *    * @throws IOException    */
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
block|{   }
comment|/**    * Returns the List of Volumes owned by the specific user.    *    * @param args - UserArgs    *    * @return - List of Volumes    *    * @throws IOException    */
annotation|@
name|Override
DECL|method|listVolumes (UserArgs args)
specifier|public
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
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

