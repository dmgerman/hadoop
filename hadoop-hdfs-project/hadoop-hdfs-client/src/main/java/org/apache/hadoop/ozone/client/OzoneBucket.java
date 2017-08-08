begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
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
name|OzoneConsts
operator|.
name|Versioning
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
comment|/**  * A class that encapsulates OzoneBucket.  */
end_comment

begin_class
DECL|class|OzoneBucket
specifier|public
class|class
name|OzoneBucket
block|{
comment|/**    * Name of the volume in which the bucket belongs to.    */
DECL|field|volumeName
specifier|private
specifier|final
name|String
name|volumeName
decl_stmt|;
comment|/**    * Name of the bucket.    */
DECL|field|bucketName
specifier|private
specifier|final
name|String
name|bucketName
decl_stmt|;
comment|/**    * Bucket ACLs.    */
DECL|field|acls
specifier|private
specifier|final
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
decl_stmt|;
comment|/**    * Type of storage to be used for this bucket.    * [RAM_DISK, SSD, DISK, ARCHIVE]    */
DECL|field|storageType
specifier|private
specifier|final
name|StorageType
name|storageType
decl_stmt|;
comment|/**    * Bucket Version flag.    */
DECL|field|versioning
specifier|private
specifier|final
name|Versioning
name|versioning
decl_stmt|;
comment|/**    * Constructs OzoneBucket from KsmBucketInfo.    *    * @param ksmBucketInfo    */
DECL|method|OzoneBucket (KsmBucketInfo ksmBucketInfo)
specifier|public
name|OzoneBucket
parameter_list|(
name|KsmBucketInfo
name|ksmBucketInfo
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|ksmBucketInfo
operator|.
name|getVolumeName
argument_list|()
expr_stmt|;
name|this
operator|.
name|bucketName
operator|=
name|ksmBucketInfo
operator|.
name|getBucketName
argument_list|()
expr_stmt|;
name|this
operator|.
name|acls
operator|=
name|ksmBucketInfo
operator|.
name|getAcls
argument_list|()
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|ksmBucketInfo
operator|.
name|getStorageType
argument_list|()
expr_stmt|;
name|this
operator|.
name|versioning
operator|=
name|ksmBucketInfo
operator|.
name|getIsVersionEnabled
argument_list|()
condition|?
name|Versioning
operator|.
name|ENABLED
else|:
name|Versioning
operator|.
name|DISABLED
expr_stmt|;
block|}
comment|/**    * Returns Volume Name.    *    * @return volumeName    */
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
comment|/**    * Returns Bucket Name.    *    * @return bucketName    */
DECL|method|getBucketName ()
specifier|public
name|String
name|getBucketName
parameter_list|()
block|{
return|return
name|bucketName
return|;
block|}
comment|/**    * Returns ACL's associated with the Bucket.    *    * @return acls    */
DECL|method|getAcls ()
specifier|public
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getAcls
parameter_list|()
block|{
return|return
name|acls
return|;
block|}
comment|/**    * Returns StorageType of the Bucket.    *    * @return storageType    */
DECL|method|getStorageType ()
specifier|public
name|StorageType
name|getStorageType
parameter_list|()
block|{
return|return
name|storageType
return|;
block|}
comment|/**    * Returns Versioning associated with the Bucket.    *    * @return versioning    */
DECL|method|getVersioning ()
specifier|public
name|Versioning
name|getVersioning
parameter_list|()
block|{
return|return
name|versioning
return|;
block|}
block|}
end_class

end_unit

