begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|OzoneAcl
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
comment|/**  * This class encapsulates the arguments that are  * required for creating a bucket.  */
end_comment

begin_class
DECL|class|BucketArgs
specifier|public
specifier|final
class|class
name|BucketArgs
block|{
comment|/**    * ACL Information.    */
DECL|field|acls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
decl_stmt|;
comment|/**    * Bucket Version flag.    */
DECL|field|isVersionEnabled
specifier|private
name|Boolean
name|isVersionEnabled
decl_stmt|;
comment|/**    * Type of storage to be used for this bucket.    * [RAM_DISK, SSD, DISK, ARCHIVE]    */
DECL|field|storageType
specifier|private
name|StorageType
name|storageType
decl_stmt|;
comment|/**    * Private constructor, constructed via builder.    * @param isVersionEnabled Bucket version flag.    * @param storageType Storage type to be used.    * @param acls list of ACLs.    */
DECL|method|BucketArgs (Boolean isVersionEnabled, StorageType storageType, List<OzoneAcl> acls)
specifier|private
name|BucketArgs
parameter_list|(
name|Boolean
name|isVersionEnabled
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
parameter_list|)
block|{
name|this
operator|.
name|acls
operator|=
name|acls
expr_stmt|;
name|this
operator|.
name|isVersionEnabled
operator|=
name|isVersionEnabled
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
block|}
comment|/**    * Returns true if bucket version is enabled, else false.    * @return isVersionEnabled    */
DECL|method|isVersionEnabled ()
specifier|public
name|Boolean
name|isVersionEnabled
parameter_list|()
block|{
return|return
name|isVersionEnabled
return|;
block|}
comment|/**    * Returns the type of storage to be used.    * @return StorageType    */
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
comment|/**    * Returns the ACL's associated with this bucket.    * @return List<OzoneAcl>    */
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
comment|/**    * Returns new builder class that builds a KsmBucketInfo.    *    * @return Builder    */
DECL|method|newBuilder ()
specifier|public
specifier|static
name|BucketArgs
operator|.
name|Builder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|BucketArgs
operator|.
name|Builder
argument_list|()
return|;
block|}
comment|/**    * Builder for KsmBucketInfo.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|isVersionEnabled
specifier|private
name|Boolean
name|isVersionEnabled
decl_stmt|;
DECL|field|storageType
specifier|private
name|StorageType
name|storageType
decl_stmt|;
DECL|field|acls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
decl_stmt|;
DECL|method|setIsVersionEnabled (Boolean versionFlag)
specifier|public
name|BucketArgs
operator|.
name|Builder
name|setIsVersionEnabled
parameter_list|(
name|Boolean
name|versionFlag
parameter_list|)
block|{
name|this
operator|.
name|isVersionEnabled
operator|=
name|versionFlag
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setStorageType (StorageType storage)
specifier|public
name|BucketArgs
operator|.
name|Builder
name|setStorageType
parameter_list|(
name|StorageType
name|storage
parameter_list|)
block|{
name|this
operator|.
name|storageType
operator|=
name|storage
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setAcls (List<OzoneAcl> listOfAcls)
specifier|public
name|BucketArgs
operator|.
name|Builder
name|setAcls
parameter_list|(
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|listOfAcls
parameter_list|)
block|{
name|this
operator|.
name|acls
operator|=
name|listOfAcls
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs the BucketArgs.      * @return instance of BucketArgs.      */
DECL|method|build ()
specifier|public
name|BucketArgs
name|build
parameter_list|()
block|{
return|return
operator|new
name|BucketArgs
argument_list|(
name|isVersionEnabled
argument_list|,
name|storageType
argument_list|,
name|acls
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

