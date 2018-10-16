begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.response
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
name|response
package|;
end_package

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
name|JsonUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonAutoDetect
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonFilter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|PropertyAccessor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectWriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ser
operator|.
name|FilterProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ser
operator|.
name|impl
operator|.
name|SimpleBeanPropertyFilter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ser
operator|.
name|impl
operator|.
name|SimpleFilterProvider
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
comment|/**  * BucketInfo class, this is used as response class to send  * Json info about a bucket back to a client.  */
end_comment

begin_class
DECL|class|BucketInfo
specifier|public
class|class
name|BucketInfo
implements|implements
name|Comparable
argument_list|<
name|BucketInfo
argument_list|>
block|{
DECL|field|BUCKET_INFO
specifier|static
specifier|final
name|String
name|BUCKET_INFO
init|=
literal|"BUCKET_INFO_FILTER"
decl_stmt|;
DECL|field|READER
specifier|private
specifier|static
specifier|final
name|ObjectReader
name|READER
init|=
operator|new
name|ObjectMapper
argument_list|()
operator|.
name|readerFor
argument_list|(
name|BucketInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|WRITER
specifier|private
specifier|static
specifier|final
name|ObjectWriter
name|WRITER
decl_stmt|;
static|static
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|String
index|[]
name|ignorableFieldNames
init|=
block|{
literal|"bytesUsed"
block|,
literal|"keyCount"
block|}
decl_stmt|;
name|FilterProvider
name|filters
init|=
operator|new
name|SimpleFilterProvider
argument_list|()
operator|.
name|addFilter
argument_list|(
name|BUCKET_INFO
argument_list|,
name|SimpleBeanPropertyFilter
operator|.
name|serializeAllExcept
argument_list|(
name|ignorableFieldNames
argument_list|)
argument_list|)
decl_stmt|;
name|mapper
operator|.
name|setVisibility
argument_list|(
name|PropertyAccessor
operator|.
name|FIELD
argument_list|,
name|JsonAutoDetect
operator|.
name|Visibility
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|addMixIn
argument_list|(
name|Object
operator|.
name|class
argument_list|,
name|MixIn
operator|.
name|class
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|setFilterProvider
argument_list|(
name|filters
argument_list|)
expr_stmt|;
name|WRITER
operator|=
name|mapper
operator|.
name|writerWithDefaultPrettyPrinter
argument_list|()
expr_stmt|;
block|}
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
DECL|field|createdOn
specifier|private
name|String
name|createdOn
decl_stmt|;
DECL|field|acls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
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
DECL|field|bytesUsed
specifier|private
name|long
name|bytesUsed
decl_stmt|;
DECL|field|keyCount
specifier|private
name|long
name|keyCount
decl_stmt|;
comment|/**    * Constructor for BucketInfo.    *    * @param volumeName    * @param bucketName    */
DECL|method|BucketInfo (String volumeName, String bucketName)
specifier|public
name|BucketInfo
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
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
block|}
comment|/**    * Default constructor for BucketInfo.    */
DECL|method|BucketInfo ()
specifier|public
name|BucketInfo
parameter_list|()
block|{
name|acls
operator|=
operator|new
name|LinkedList
argument_list|<
name|OzoneAcl
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Parse a JSON string into BucketInfo Object.    *    * @param jsonString - Json String    *    * @return - BucketInfo    *    * @throws IOException    */
DECL|method|parse (String jsonString)
specifier|public
specifier|static
name|BucketInfo
name|parse
parameter_list|(
name|String
name|jsonString
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|READER
operator|.
name|readValue
argument_list|(
name|jsonString
argument_list|)
return|;
block|}
comment|/**    * Returns a List of ACL on the Bucket.    *    * @return List of Acls    */
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
comment|/**    * Sets ACls.    *    * @param acls - Acls list    */
DECL|method|setAcls (List<OzoneAcl> acls)
specifier|public
name|void
name|setAcls
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
name|acls
operator|=
name|acls
expr_stmt|;
block|}
comment|/**    * Returns Storage Type info.    *    * @return Storage Type of the bucket    */
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
comment|/**    * Sets the Storage Type.    *    * @param storageType - Storage Type    */
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
comment|/**    * Returns versioning.    *    * @return versioning Enum    */
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
comment|/**    * Sets Versioning.    *    * @param versioning    */
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
comment|/**    * Gets bucket Name.    *    * @return String    */
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
comment|/**    * Sets bucket Name.    *    * @param bucketName - Name of the bucket    */
DECL|method|setBucketName (String bucketName)
specifier|public
name|void
name|setBucketName
parameter_list|(
name|String
name|bucketName
parameter_list|)
block|{
name|this
operator|.
name|bucketName
operator|=
name|bucketName
expr_stmt|;
block|}
comment|/**    * Sets creation time of the bucket.    *    * @param creationTime - Date String    */
DECL|method|setCreatedOn (String creationTime)
specifier|public
name|void
name|setCreatedOn
parameter_list|(
name|String
name|creationTime
parameter_list|)
block|{
name|this
operator|.
name|createdOn
operator|=
name|creationTime
expr_stmt|;
block|}
comment|/**    * Returns creation time.    *    * @return creation time of bucket.    */
DECL|method|getCreatedOn ()
specifier|public
name|String
name|getCreatedOn
parameter_list|()
block|{
return|return
name|createdOn
return|;
block|}
comment|/**    * Returns a JSON string of this object.    * After stripping out bytesUsed and keyCount    *    * @return String    */
DECL|method|toJsonString ()
specifier|public
name|String
name|toJsonString
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|WRITER
operator|.
name|writeValueAsString
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Returns the Object as a Json String.    *    * The reason why both toJSONString exists and toDBString exists    * is because toJSONString supports an external facing contract with    * REST clients. However server internally would want to add more    * fields to this class. The distinction helps in serializing all    * fields vs. only fields that are part of REST protocol.    */
DECL|method|toDBString ()
specifier|public
name|String
name|toDBString
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|JsonUtils
operator|.
name|toJsonString
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Returns Volume Name.    *    * @return String volume name    */
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
comment|/**    * Sets the Volume Name of the bucket.    *    * @param volumeName - volumeName    */
DECL|method|setVolumeName (String volumeName)
specifier|public
name|void
name|setVolumeName
parameter_list|(
name|String
name|volumeName
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|volumeName
expr_stmt|;
block|}
comment|/**    * Compares this object with the specified object for order.  Returns a    * negative integer, zero, or a positive integer as this object is less    * than, equal to, or greater than the specified object.    *    * Please note : BucketInfo compare functions are used only within the    * context of a volume, hence volume name is purposefully ignored in    * compareTo, equal and hashcode functions of this class.    */
annotation|@
name|Override
DECL|method|compareTo (BucketInfo o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|BucketInfo
name|o
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|o
operator|.
name|getVolumeName
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getVolumeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|bucketName
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getBucketName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Checks if two bucketInfo's are equal.    * @param o Object BucketInfo    * @return  True or False    */
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
operator|!
operator|(
name|o
operator|instanceof
name|BucketInfo
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BucketInfo
name|that
init|=
operator|(
name|BucketInfo
operator|)
name|o
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|that
operator|.
name|getVolumeName
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getVolumeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bucketName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|bucketName
argument_list|)
return|;
block|}
comment|/**    * Hash Code for this object.    * @return int    */
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|bucketName
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Get the number of bytes used by this bucket.    *    * @return long    */
DECL|method|getBytesUsed ()
specifier|public
name|long
name|getBytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
return|;
block|}
comment|/**    * Set bytes Used.    *    * @param bytesUsed - bytesUsed    */
DECL|method|setBytesUsed (long bytesUsed)
specifier|public
name|void
name|setBytesUsed
parameter_list|(
name|long
name|bytesUsed
parameter_list|)
block|{
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
block|}
comment|/**    * Get Key Count  inside this bucket.    *    * @return - KeyCount    */
DECL|method|getKeyCount ()
specifier|public
name|long
name|getKeyCount
parameter_list|()
block|{
return|return
name|keyCount
return|;
block|}
comment|/**    * Set Key Count inside this bucket.    *    * @param keyCount - Sets the Key Count    */
DECL|method|setKeyCount (long keyCount)
specifier|public
name|void
name|setKeyCount
parameter_list|(
name|long
name|keyCount
parameter_list|)
block|{
name|this
operator|.
name|keyCount
operator|=
name|keyCount
expr_stmt|;
block|}
comment|/**    * This class allows us to create custom filters    * for the Json serialization.    */
annotation|@
name|JsonFilter
argument_list|(
name|BUCKET_INFO
argument_list|)
DECL|class|MixIn
class|class
name|MixIn
block|{    }
block|}
end_class

end_unit

