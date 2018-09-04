begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|helpers
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|hdfs
operator|.
name|protocolPB
operator|.
name|PBHelperClient
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
name|audit
operator|.
name|Auditable
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
name|OzoneManagerProtocolProtos
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
name|protocolPB
operator|.
name|OMPBHelper
import|;
end_import

begin_comment
comment|/**  * A class that encapsulates Bucket Arguments.  */
end_comment

begin_class
DECL|class|OmBucketArgs
specifier|public
specifier|final
class|class
name|OmBucketArgs
implements|implements
name|Auditable
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
comment|/**    * ACL's that are to be added for the bucket.    */
DECL|field|addAcls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|addAcls
decl_stmt|;
comment|/**    * ACL's that are to be removed from the bucket.    */
DECL|field|removeAcls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|removeAcls
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
comment|/**    * Private constructor, constructed via builder.    * @param volumeName - Volume name.    * @param bucketName - Bucket name.    * @param addAcls - ACL's to be added.    * @param removeAcls - ACL's to be removed.    * @param isVersionEnabled - Bucket version flag.    * @param storageType - Storage type to be used.    */
DECL|method|OmBucketArgs (String volumeName, String bucketName, List<OzoneAcl> addAcls, List<OzoneAcl> removeAcls, Boolean isVersionEnabled, StorageType storageType)
specifier|private
name|OmBucketArgs
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|addAcls
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|removeAcls
parameter_list|,
name|Boolean
name|isVersionEnabled
parameter_list|,
name|StorageType
name|storageType
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|volumeName
expr_stmt|;
name|this
operator|.
name|bucketName
operator|=
name|bucketName
expr_stmt|;
name|this
operator|.
name|addAcls
operator|=
name|addAcls
expr_stmt|;
name|this
operator|.
name|removeAcls
operator|=
name|removeAcls
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
comment|/**    * Returns the Volume Name.    * @return String.    */
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
comment|/**    * Returns the Bucket Name.    * @return String    */
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
comment|/**    * Returns the ACL's that are to be added.    * @return List<OzoneAclInfo>    */
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
comment|/**    * Returns the ACL's that are to be removed.    * @return List<OzoneAclInfo>    */
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
comment|/**    * Returns true if bucket version is enabled, else false.    * @return isVersionEnabled    */
DECL|method|getIsVersionEnabled ()
specifier|public
name|Boolean
name|getIsVersionEnabled
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
comment|/**    * Returns new builder class that builds a OmBucketArgs.    *    * @return Builder    */
DECL|method|newBuilder ()
specifier|public
specifier|static
name|Builder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toAuditMap ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toAuditMap
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|VOLUME
argument_list|,
name|this
operator|.
name|volumeName
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|BUCKET
argument_list|,
name|this
operator|.
name|bucketName
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|addAcls
operator|!=
literal|null
condition|)
block|{
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|ADD_ACLS
argument_list|,
name|this
operator|.
name|addAcls
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|removeAcls
operator|!=
literal|null
condition|)
block|{
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|REMOVE_ACLS
argument_list|,
name|this
operator|.
name|removeAcls
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|IS_VERSION_ENABLED
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|isVersionEnabled
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|storageType
operator|!=
literal|null
condition|)
block|{
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|STORAGE_TYPE
argument_list|,
name|this
operator|.
name|storageType
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|auditMap
return|;
block|}
comment|/**    * Builder for OmBucketArgs.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|volumeName
specifier|private
name|String
name|volumeName
decl_stmt|;
DECL|field|bucketName
specifier|private
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
DECL|method|setVolumeName (String volume)
specifier|public
name|Builder
name|setVolumeName
parameter_list|(
name|String
name|volume
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|volume
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBucketName (String bucket)
specifier|public
name|Builder
name|setBucketName
parameter_list|(
name|String
name|bucket
parameter_list|)
block|{
name|this
operator|.
name|bucketName
operator|=
name|bucket
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setAddAcls (List<OzoneAcl> acls)
specifier|public
name|Builder
name|setAddAcls
parameter_list|(
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
parameter_list|)
block|{
name|this
operator|.
name|addAcls
operator|=
name|acls
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRemoveAcls (List<OzoneAcl> acls)
specifier|public
name|Builder
name|setRemoveAcls
parameter_list|(
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
parameter_list|)
block|{
name|this
operator|.
name|removeAcls
operator|=
name|acls
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setIsVersionEnabled (Boolean versionFlag)
specifier|public
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
comment|/**      * Constructs the OmBucketArgs.      * @return instance of OmBucketArgs.      */
DECL|method|build ()
specifier|public
name|OmBucketArgs
name|build
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
return|return
operator|new
name|OmBucketArgs
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|addAcls
argument_list|,
name|removeAcls
argument_list|,
name|isVersionEnabled
argument_list|,
name|storageType
argument_list|)
return|;
block|}
block|}
comment|/**    * Creates BucketArgs protobuf from OmBucketArgs.    */
DECL|method|getProtobuf ()
specifier|public
name|BucketArgs
name|getProtobuf
parameter_list|()
block|{
name|BucketArgs
operator|.
name|Builder
name|builder
init|=
name|BucketArgs
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
if|if
condition|(
name|addAcls
operator|!=
literal|null
operator|&&
operator|!
name|addAcls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addAllAddAcls
argument_list|(
name|addAcls
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|OMPBHelper
operator|::
name|convertOzoneAcl
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|removeAcls
operator|!=
literal|null
operator|&&
operator|!
name|removeAcls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addAllRemoveAcls
argument_list|(
name|removeAcls
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|OMPBHelper
operator|::
name|convertOzoneAcl
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isVersionEnabled
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setIsVersionEnabled
argument_list|(
name|isVersionEnabled
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storageType
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setStorageType
argument_list|(
name|PBHelperClient
operator|.
name|convertStorageType
argument_list|(
name|storageType
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Parses BucketInfo protobuf and creates OmBucketArgs.    * @param bucketArgs    * @return instance of OmBucketArgs    */
DECL|method|getFromProtobuf (BucketArgs bucketArgs)
specifier|public
specifier|static
name|OmBucketArgs
name|getFromProtobuf
parameter_list|(
name|BucketArgs
name|bucketArgs
parameter_list|)
block|{
return|return
operator|new
name|OmBucketArgs
argument_list|(
name|bucketArgs
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|bucketArgs
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|bucketArgs
operator|.
name|getAddAclsList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|OMPBHelper
operator|::
name|convertOzoneAcl
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|,
name|bucketArgs
operator|.
name|getRemoveAclsList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|OMPBHelper
operator|::
name|convertOzoneAcl
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|,
name|bucketArgs
operator|.
name|hasIsVersionEnabled
argument_list|()
condition|?
name|bucketArgs
operator|.
name|getIsVersionEnabled
argument_list|()
else|:
literal|null
argument_list|,
name|bucketArgs
operator|.
name|hasStorageType
argument_list|()
condition|?
name|PBHelperClient
operator|.
name|convertStorageType
argument_list|(
name|bucketArgs
operator|.
name|getStorageType
argument_list|()
argument_list|)
else|:
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

