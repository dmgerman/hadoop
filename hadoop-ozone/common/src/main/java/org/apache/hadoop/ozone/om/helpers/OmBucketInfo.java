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
name|HashMap
import|;
end_import

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
name|Objects
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
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
name|protocolPB
operator|.
name|OMPBHelper
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

begin_comment
comment|/**  * A class that encapsulates Bucket Info.  */
end_comment

begin_class
DECL|class|OmBucketInfo
specifier|public
specifier|final
class|class
name|OmBucketInfo
extends|extends
name|WithMetadata
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
comment|/**    * Creation time of bucket.    */
DECL|field|creationTime
specifier|private
specifier|final
name|long
name|creationTime
decl_stmt|;
comment|/**    * Bucket encryption key info if encryption is enabled.    */
DECL|field|bekInfo
specifier|private
name|BucketEncryptionKeyInfo
name|bekInfo
decl_stmt|;
comment|/**    * Private constructor, constructed via builder.    * @param volumeName - Volume name.    * @param bucketName - Bucket name.    * @param acls - list of ACLs.    * @param isVersionEnabled - Bucket version flag.    * @param storageType - Storage type to be used.    * @param creationTime - Bucket creation time.    * @param metadata - metadata.    * @param bekInfo - bucket encryption key info.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:ParameterNumber"
argument_list|)
DECL|method|OmBucketInfo (String volumeName, String bucketName, List<OzoneAcl> acls, boolean isVersionEnabled, StorageType storageType, long creationTime, Map<String, String> metadata, BucketEncryptionKeyInfo bekInfo)
specifier|private
name|OmBucketInfo
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
name|acls
parameter_list|,
name|boolean
name|isVersionEnabled
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|,
name|BucketEncryptionKeyInfo
name|bekInfo
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
name|this
operator|.
name|creationTime
operator|=
name|creationTime
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|bekInfo
operator|=
name|bekInfo
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
comment|/**    * Returns the ACL's associated with this bucket.    * @return {@literal List<OzoneAcl>}    */
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
comment|/**    * Add an ozoneAcl to list of existing Acl set.    * @param ozoneAcl    * @return true - if successfully added, false if not added or acl is    * already existing in the acl list.    */
DECL|method|addAcl (OzoneAcl ozoneAcl)
specifier|public
name|boolean
name|addAcl
parameter_list|(
name|OzoneAcl
name|ozoneAcl
parameter_list|)
block|{
return|return
name|OzoneAclUtil
operator|.
name|addAcl
argument_list|(
name|acls
argument_list|,
name|ozoneAcl
argument_list|)
return|;
block|}
comment|/**    * Remove acl from existing acl list.    * @param ozoneAcl    * @return true - if successfully removed, false if not able to remove due    * to that acl is not in the existing acl list.    */
DECL|method|removeAcl (OzoneAcl ozoneAcl)
specifier|public
name|boolean
name|removeAcl
parameter_list|(
name|OzoneAcl
name|ozoneAcl
parameter_list|)
block|{
return|return
name|OzoneAclUtil
operator|.
name|removeAcl
argument_list|(
name|acls
argument_list|,
name|ozoneAcl
argument_list|)
return|;
block|}
comment|/**    * Reset the existing acl list.    * @param ozoneAcls    * @return true - if successfully able to reset.    */
DECL|method|setAcls (List<OzoneAcl> ozoneAcls)
specifier|public
name|boolean
name|setAcls
parameter_list|(
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|ozoneAcls
parameter_list|)
block|{
return|return
name|OzoneAclUtil
operator|.
name|setAcl
argument_list|(
name|acls
argument_list|,
name|ozoneAcls
argument_list|)
return|;
block|}
comment|/**    * Returns true if bucket version is enabled, else false.    * @return isVersionEnabled    */
DECL|method|getIsVersionEnabled ()
specifier|public
name|boolean
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
comment|/**    * Returns creation time.    *    * @return long    */
DECL|method|getCreationTime ()
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|creationTime
return|;
block|}
comment|/**    * Returns bucket encryption key info.    * @return bucket encryption key info    */
DECL|method|getEncryptionKeyInfo ()
specifier|public
name|BucketEncryptionKeyInfo
name|getEncryptionKeyInfo
parameter_list|()
block|{
return|return
name|bekInfo
return|;
block|}
comment|/**    * Returns new builder class that builds a OmBucketInfo.    *    * @return Builder    */
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
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|ACLS
argument_list|,
operator|(
name|this
operator|.
name|acls
operator|!=
literal|null
operator|)
condition|?
name|this
operator|.
name|acls
operator|.
name|toString
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
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
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|STORAGE_TYPE
argument_list|,
operator|(
name|this
operator|.
name|storageType
operator|!=
literal|null
operator|)
condition|?
name|this
operator|.
name|storageType
operator|.
name|name
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|CREATION_TIME
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|creationTime
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|auditMap
return|;
block|}
comment|/**    * Builder for OmBucketInfo.    */
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
DECL|field|acls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
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
DECL|field|creationTime
specifier|private
name|long
name|creationTime
decl_stmt|;
DECL|field|metadata
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
decl_stmt|;
DECL|field|bekInfo
specifier|private
name|BucketEncryptionKeyInfo
name|bekInfo
decl_stmt|;
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{
comment|//Default values
name|this
operator|.
name|acls
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|isVersionEnabled
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|StorageType
operator|.
name|DISK
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
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
DECL|method|setAcls (List<OzoneAcl> listOfAcls)
specifier|public
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
if|if
condition|(
name|listOfAcls
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|acls
operator|.
name|addAll
argument_list|(
name|listOfAcls
argument_list|)
expr_stmt|;
block|}
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
DECL|method|setCreationTime (long createdOn)
specifier|public
name|Builder
name|setCreationTime
parameter_list|(
name|long
name|createdOn
parameter_list|)
block|{
name|this
operator|.
name|creationTime
operator|=
name|createdOn
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addMetadata (String key, String value)
specifier|public
name|Builder
name|addMetadata
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|metadata
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addAllMetadata (Map<String, String> additionalMetadata)
specifier|public
name|Builder
name|addAllMetadata
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|additionalMetadata
parameter_list|)
block|{
if|if
condition|(
name|additionalMetadata
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|putAll
argument_list|(
name|additionalMetadata
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|setBucketEncryptionKey ( BucketEncryptionKeyInfo info)
specifier|public
name|Builder
name|setBucketEncryptionKey
parameter_list|(
name|BucketEncryptionKeyInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|bekInfo
operator|=
name|info
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs the OmBucketInfo.      * @return instance of OmBucketInfo.      */
DECL|method|build ()
specifier|public
name|OmBucketInfo
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|acls
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|isVersionEnabled
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|storageType
argument_list|)
expr_stmt|;
return|return
operator|new
name|OmBucketInfo
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|acls
argument_list|,
name|isVersionEnabled
argument_list|,
name|storageType
argument_list|,
name|creationTime
argument_list|,
name|metadata
argument_list|,
name|bekInfo
argument_list|)
return|;
block|}
block|}
comment|/**    * Creates BucketInfo protobuf from OmBucketInfo.    */
DECL|method|getProtobuf ()
specifier|public
name|BucketInfo
name|getProtobuf
parameter_list|()
block|{
name|BucketInfo
operator|.
name|Builder
name|bib
init|=
name|BucketInfo
operator|.
name|newBuilder
argument_list|()
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
operator|.
name|addAllAcls
argument_list|(
name|OzoneAclUtil
operator|.
name|toProtobuf
argument_list|(
name|acls
argument_list|)
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
name|isVersionEnabled
argument_list|)
operator|.
name|setStorageType
argument_list|(
name|storageType
operator|.
name|toProto
argument_list|()
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|creationTime
argument_list|)
operator|.
name|addAllMetadata
argument_list|(
name|KeyValueUtil
operator|.
name|toProtobuf
argument_list|(
name|metadata
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|bekInfo
operator|!=
literal|null
operator|&&
name|bekInfo
operator|.
name|getKeyName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|bib
operator|.
name|setBeinfo
argument_list|(
name|OMPBHelper
operator|.
name|convert
argument_list|(
name|bekInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|bib
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Parses BucketInfo protobuf and creates OmBucketInfo.    * @param bucketInfo    * @return instance of OmBucketInfo    */
DECL|method|getFromProtobuf (BucketInfo bucketInfo)
specifier|public
specifier|static
name|OmBucketInfo
name|getFromProtobuf
parameter_list|(
name|BucketInfo
name|bucketInfo
parameter_list|)
block|{
name|OmBucketInfo
operator|.
name|Builder
name|obib
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|bucketInfo
operator|.
name|getVolumeName
argument_list|()
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketInfo
operator|.
name|getBucketName
argument_list|()
argument_list|)
operator|.
name|setAcls
argument_list|(
name|bucketInfo
operator|.
name|getAclsList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|OzoneAcl
operator|::
name|fromProtobuf
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
operator|.
name|setIsVersionEnabled
argument_list|(
name|bucketInfo
operator|.
name|getIsVersionEnabled
argument_list|()
argument_list|)
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|valueOf
argument_list|(
name|bucketInfo
operator|.
name|getStorageType
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|bucketInfo
operator|.
name|getCreationTime
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketInfo
operator|.
name|getMetadataList
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|obib
operator|.
name|addAllMetadata
argument_list|(
name|KeyValueUtil
operator|.
name|getFromProtobuf
argument_list|(
name|bucketInfo
operator|.
name|getMetadataList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bucketInfo
operator|.
name|hasBeinfo
argument_list|()
condition|)
block|{
name|obib
operator|.
name|setBucketEncryptionKey
argument_list|(
name|OMPBHelper
operator|.
name|convert
argument_list|(
name|bucketInfo
operator|.
name|getBeinfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|obib
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|OmBucketInfo
name|that
init|=
operator|(
name|OmBucketInfo
operator|)
name|o
decl_stmt|;
return|return
name|creationTime
operator|==
name|that
operator|.
name|creationTime
operator|&&
name|volumeName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|volumeName
argument_list|)
operator|&&
name|bucketName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|bucketName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|acls
argument_list|,
name|that
operator|.
name|acls
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|isVersionEnabled
argument_list|,
name|that
operator|.
name|isVersionEnabled
argument_list|)
operator|&&
name|storageType
operator|==
name|that
operator|.
name|storageType
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|metadata
argument_list|,
name|that
operator|.
name|metadata
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|bekInfo
argument_list|,
name|that
operator|.
name|bekInfo
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

