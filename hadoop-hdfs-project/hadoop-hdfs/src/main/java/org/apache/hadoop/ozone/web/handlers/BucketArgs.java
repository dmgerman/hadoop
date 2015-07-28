begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.handlers
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
name|handlers
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
name|utils
operator|.
name|OzoneConsts
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
comment|/**  * BucketArgs packages all bucket related arguments to  * file system calls.  */
end_comment

begin_class
DECL|class|BucketArgs
specifier|public
class|class
name|BucketArgs
extends|extends
name|VolumeArgs
block|{
DECL|field|bucketName
specifier|private
specifier|final
name|String
name|bucketName
decl_stmt|;
DECL|field|addAcls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|addAcls
decl_stmt|;
DECL|field|removeAcls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|removeAcls
decl_stmt|;
DECL|field|versioning
specifier|private
name|OzoneConsts
operator|.
name|Versioning
name|versioning
decl_stmt|;
DECL|field|storageType
specifier|private
name|StorageType
name|storageType
decl_stmt|;
comment|/**    * Constructor for BucketArgs.    *    * @param volumeName - volumeName    * @param bucketName - bucket Name    * @param userArgs - userArgs    */
DECL|method|BucketArgs (String volumeName, String bucketName, UserArgs userArgs)
specifier|public
name|BucketArgs
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|UserArgs
name|userArgs
parameter_list|)
block|{
name|super
argument_list|(
name|volumeName
argument_list|,
name|userArgs
argument_list|)
expr_stmt|;
name|this
operator|.
name|bucketName
operator|=
name|bucketName
expr_stmt|;
name|this
operator|.
name|versioning
operator|=
name|OzoneConsts
operator|.
name|Versioning
operator|.
name|NOT_DEFINED
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Constructor for BucketArgs.    *    * @param bucketName - bucket Name    * @param volumeArgs - volume Args    */
DECL|method|BucketArgs (String bucketName, VolumeArgs volumeArgs)
specifier|public
name|BucketArgs
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|VolumeArgs
name|volumeArgs
parameter_list|)
block|{
name|super
argument_list|(
name|volumeArgs
argument_list|)
expr_stmt|;
name|this
operator|.
name|bucketName
operator|=
name|bucketName
expr_stmt|;
name|this
operator|.
name|versioning
operator|=
name|OzoneConsts
operator|.
name|Versioning
operator|.
name|NOT_DEFINED
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Constructor for BucketArgs.    *    * @param args - Bucket Args    */
DECL|method|BucketArgs (BucketArgs args)
specifier|public
name|BucketArgs
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
block|{
name|this
argument_list|(
name|args
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|setAddAcls
argument_list|(
name|args
operator|.
name|getAddAcls
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setRemoveAcls
argument_list|(
name|args
operator|.
name|getRemoveAcls
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the Bucket Name.    *    * @return Bucket Name    */
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
comment|/**    * Returns Additive ACLs for the Bucket if specified.    *    * @return acls    */
DECL|method|getAddAcls ()
specifier|public
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getAddAcls
parameter_list|()
block|{
return|return
name|addAcls
return|;
block|}
comment|/**    * Set Additive ACLs.    *    * @param acl - ACL    */
DECL|method|setAddAcls (List<OzoneAcl> acl)
specifier|public
name|void
name|setAddAcls
parameter_list|(
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acl
parameter_list|)
block|{
name|this
operator|.
name|addAcls
operator|=
name|acl
expr_stmt|;
block|}
comment|/**    * Returns remove ACLs for the Bucket if specified.    *    * @return acls    */
DECL|method|getRemoveAcls ()
specifier|public
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getRemoveAcls
parameter_list|()
block|{
return|return
name|removeAcls
return|;
block|}
comment|/**    * Takes an ACL and sets the ACL object to ACL represented by the String.    *    * @param aclString - aclString    */
DECL|method|addAcls (List<String> aclString)
specifier|public
name|void
name|addAcls
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|aclString
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|aclString
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ACLs cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|this
operator|.
name|addAcls
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|addAcls
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|s
range|:
name|aclString
control|)
block|{
name|this
operator|.
name|addAcls
operator|.
name|add
argument_list|(
name|OzoneAcl
operator|.
name|parseAcl
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Takes an ACL and sets the ACL object to ACL represented by the String.    *    * @param aclString - aclString    */
DECL|method|removeAcls (List<String> aclString)
specifier|public
name|void
name|removeAcls
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|aclString
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|aclString
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ACLs cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|this
operator|.
name|removeAcls
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|removeAcls
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|s
range|:
name|aclString
control|)
block|{
name|this
operator|.
name|removeAcls
operator|.
name|add
argument_list|(
name|OzoneAcl
operator|.
name|parseAcl
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set remove ACLs.    *    * @param acl - ACL    */
DECL|method|setRemoveAcls (List<OzoneAcl> acl)
specifier|public
name|void
name|setRemoveAcls
parameter_list|(
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acl
parameter_list|)
block|{
name|this
operator|.
name|removeAcls
operator|=
name|acl
expr_stmt|;
block|}
comment|/**    * Returns Versioning Info.    *    * @return versioning    */
DECL|method|getVersioning ()
specifier|public
name|OzoneConsts
operator|.
name|Versioning
name|getVersioning
parameter_list|()
block|{
return|return
name|versioning
return|;
block|}
comment|/**    * SetVersioning Info.    *    * @param versioning - Enum value    */
DECL|method|setVersioning (OzoneConsts.Versioning versioning)
specifier|public
name|void
name|setVersioning
parameter_list|(
name|OzoneConsts
operator|.
name|Versioning
name|versioning
parameter_list|)
block|{
name|this
operator|.
name|versioning
operator|=
name|versioning
expr_stmt|;
block|}
comment|/**    * returns the current Storage Class.    *    * @return Storage Class    */
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
comment|/**    * Sets the Storage Class.    *    * @param storageType Set Storage Class    */
DECL|method|setStorageType (StorageType storageType)
specifier|public
name|void
name|setStorageType
parameter_list|(
name|StorageType
name|storageType
parameter_list|)
block|{
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
block|}
comment|/**    * returns - Volume/bucketName.    *    * @return String    */
annotation|@
name|Override
DECL|method|getResourceName ()
specifier|public
name|String
name|getResourceName
parameter_list|()
block|{
return|return
name|getVolumeName
argument_list|()
operator|+
literal|"/"
operator|+
name|getBucketName
argument_list|()
return|;
block|}
comment|/**    * Returns User/Volume name which is the parent of this    * bucket.    *    * @return String    */
DECL|method|getParentName ()
specifier|public
name|String
name|getParentName
parameter_list|()
block|{
return|return
name|getUserName
argument_list|()
operator|+
literal|"/"
operator|+
name|getVolumeName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

