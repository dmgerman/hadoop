begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmOzoneAclMap
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

begin_comment
comment|/**  * A class that encapsulates OzoneVolume.  */
end_comment

begin_class
DECL|class|OzoneVolume
specifier|public
class|class
name|OzoneVolume
block|{
comment|/**    * Admin Name of the Volume.    */
DECL|field|adminName
specifier|private
specifier|final
name|String
name|adminName
decl_stmt|;
comment|/**    * Owner of the Volume.    */
DECL|field|ownerName
specifier|private
specifier|final
name|String
name|ownerName
decl_stmt|;
comment|/**    * Name of the Volume.    */
DECL|field|volumeName
specifier|private
specifier|final
name|String
name|volumeName
decl_stmt|;
comment|/**    * Quota allocated for the Volume.    */
DECL|field|quotaInBytes
specifier|private
specifier|final
name|long
name|quotaInBytes
decl_stmt|;
comment|/**    * Volume ACLs.    */
DECL|field|aclMap
specifier|private
specifier|final
name|KsmOzoneAclMap
name|aclMap
decl_stmt|;
comment|/**    * Constructs OzoneVolume from KsmVolumeArgs.    *    * @param ksmVolumeArgs    */
DECL|method|OzoneVolume (KsmVolumeArgs ksmVolumeArgs)
specifier|public
name|OzoneVolume
parameter_list|(
name|KsmVolumeArgs
name|ksmVolumeArgs
parameter_list|)
block|{
name|this
operator|.
name|adminName
operator|=
name|ksmVolumeArgs
operator|.
name|getAdminName
argument_list|()
expr_stmt|;
name|this
operator|.
name|ownerName
operator|=
name|ksmVolumeArgs
operator|.
name|getOwnerName
argument_list|()
expr_stmt|;
name|this
operator|.
name|volumeName
operator|=
name|ksmVolumeArgs
operator|.
name|getVolume
argument_list|()
expr_stmt|;
name|this
operator|.
name|quotaInBytes
operator|=
name|ksmVolumeArgs
operator|.
name|getQuotaInBytes
argument_list|()
expr_stmt|;
name|this
operator|.
name|aclMap
operator|=
name|ksmVolumeArgs
operator|.
name|getAclMap
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns Volume's admin name.    *    * @return adminName    */
DECL|method|getAdminName ()
specifier|public
name|String
name|getAdminName
parameter_list|()
block|{
return|return
name|adminName
return|;
block|}
comment|/**    * Returns Volume's owner name.    *    * @return ownerName    */
DECL|method|getOwnerName ()
specifier|public
name|String
name|getOwnerName
parameter_list|()
block|{
return|return
name|ownerName
return|;
block|}
comment|/**    * Returns Volume name.    *    * @return volumeName    */
DECL|method|getVolumeName ()
specifier|public
name|String
name|getVolumeName
parameter_list|()
block|{
return|return
name|volumeName
return|;
block|}
comment|/**    * Returns Quota allocated for the Volume in bytes.    *    * @return quotaInBytes    */
DECL|method|getQuota ()
specifier|public
name|long
name|getQuota
parameter_list|()
block|{
return|return
name|quotaInBytes
return|;
block|}
comment|/**    * Returns OzoneAcl list associated with the Volume.    *    * @return aclMap    */
DECL|method|getAclMap ()
specifier|public
name|KsmOzoneAclMap
name|getAclMap
parameter_list|()
block|{
return|return
name|aclMap
return|;
block|}
block|}
end_class

end_unit

