begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|document
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|document
operator|.
name|KeyAttribute
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|document
operator|.
name|PrimaryKey
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|model
operator|.
name|AttributeDefinition
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|model
operator|.
name|KeySchemaElement
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|model
operator|.
name|KeyType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|dynamodbv2
operator|.
name|model
operator|.
name|ScalarAttributeType
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
name|annotations
operator|.
name|VisibleForTesting
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
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|classification
operator|.
name|InterfaceStability
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
name|Path
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
name|s3a
operator|.
name|Constants
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
name|s3a
operator|.
name|S3AFileStatus
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
name|s3a
operator|.
name|Tristate
import|;
end_import

begin_comment
comment|/**  * Defines methods for translating between domain model objects and their  * representations in the DynamoDB schema.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
annotation|@
name|VisibleForTesting
DECL|class|PathMetadataDynamoDBTranslation
specifier|public
specifier|final
class|class
name|PathMetadataDynamoDBTranslation
block|{
comment|/** The HASH key name of each item. */
annotation|@
name|VisibleForTesting
DECL|field|PARENT
specifier|static
specifier|final
name|String
name|PARENT
init|=
literal|"parent"
decl_stmt|;
comment|/** The RANGE key name of each item. */
annotation|@
name|VisibleForTesting
DECL|field|CHILD
specifier|static
specifier|final
name|String
name|CHILD
init|=
literal|"child"
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|IS_DIR
specifier|static
specifier|final
name|String
name|IS_DIR
init|=
literal|"is_dir"
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|MOD_TIME
specifier|static
specifier|final
name|String
name|MOD_TIME
init|=
literal|"mod_time"
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|FILE_LENGTH
specifier|static
specifier|final
name|String
name|FILE_LENGTH
init|=
literal|"file_length"
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|String
name|BLOCK_SIZE
init|=
literal|"block_size"
decl_stmt|;
DECL|field|IS_DELETED
specifier|static
specifier|final
name|String
name|IS_DELETED
init|=
literal|"is_deleted"
decl_stmt|;
DECL|field|IS_AUTHORITATIVE
specifier|static
specifier|final
name|String
name|IS_AUTHORITATIVE
init|=
literal|"is_authoritative"
decl_stmt|;
DECL|field|LAST_UPDATED
specifier|static
specifier|final
name|String
name|LAST_UPDATED
init|=
literal|"last_updated"
decl_stmt|;
DECL|field|ETAG
specifier|static
specifier|final
name|String
name|ETAG
init|=
literal|"etag"
decl_stmt|;
DECL|field|VERSION_ID
specifier|static
specifier|final
name|String
name|VERSION_ID
init|=
literal|"version_id"
decl_stmt|;
comment|/** Used while testing backward compatibility. */
annotation|@
name|VisibleForTesting
DECL|field|IGNORED_FIELDS
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|IGNORED_FIELDS
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Table version field {@value} in version marker item. */
annotation|@
name|VisibleForTesting
DECL|field|TABLE_VERSION
specifier|static
specifier|final
name|String
name|TABLE_VERSION
init|=
literal|"table_version"
decl_stmt|;
comment|/** Table creation timestampfield {@value} in version marker item. */
annotation|@
name|VisibleForTesting
DECL|field|TABLE_CREATED
specifier|static
specifier|final
name|String
name|TABLE_CREATED
init|=
literal|"table_created"
decl_stmt|;
comment|/** The version marker field is invalid. */
DECL|field|E_NOT_VERSION_MARKER
specifier|static
specifier|final
name|String
name|E_NOT_VERSION_MARKER
init|=
literal|"Not a version marker: "
decl_stmt|;
comment|/**    * Returns the key schema for the DynamoDB table.    *    * @return DynamoDB key schema    */
DECL|method|keySchema ()
specifier|static
name|Collection
argument_list|<
name|KeySchemaElement
argument_list|>
name|keySchema
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|KeySchemaElement
argument_list|(
name|PARENT
argument_list|,
name|KeyType
operator|.
name|HASH
argument_list|)
argument_list|,
operator|new
name|KeySchemaElement
argument_list|(
name|CHILD
argument_list|,
name|KeyType
operator|.
name|RANGE
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the attribute definitions for the DynamoDB table.    *    * @return DynamoDB attribute definitions    */
DECL|method|attributeDefinitions ()
specifier|static
name|Collection
argument_list|<
name|AttributeDefinition
argument_list|>
name|attributeDefinitions
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|AttributeDefinition
argument_list|(
name|PARENT
argument_list|,
name|ScalarAttributeType
operator|.
name|S
argument_list|)
argument_list|,
operator|new
name|AttributeDefinition
argument_list|(
name|CHILD
argument_list|,
name|ScalarAttributeType
operator|.
name|S
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Converts a DynamoDB item to a {@link DDBPathMetadata}.    *    * @param item DynamoDB item to convert    * @return {@code item} converted to a {@link DDBPathMetadata}    */
DECL|method|itemToPathMetadata (Item item, String username)
specifier|static
name|DDBPathMetadata
name|itemToPathMetadata
parameter_list|(
name|Item
name|item
parameter_list|,
name|String
name|username
parameter_list|)
block|{
if|if
condition|(
name|item
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|parentStr
init|=
name|item
operator|.
name|getString
argument_list|(
name|PARENT
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|parentStr
argument_list|,
literal|"No parent entry in item %s"
argument_list|,
name|item
argument_list|)
expr_stmt|;
name|String
name|childStr
init|=
name|item
operator|.
name|getString
argument_list|(
name|CHILD
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|childStr
argument_list|,
literal|"No child entry in item %s"
argument_list|,
name|item
argument_list|)
expr_stmt|;
comment|// Skip table version markers, which are only non-absolute paths stored.
name|Path
name|rawPath
init|=
operator|new
name|Path
argument_list|(
name|parentStr
argument_list|,
name|childStr
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rawPath
operator|.
name|isAbsoluteAndSchemeAuthorityNull
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Path
name|parent
init|=
operator|new
name|Path
argument_list|(
name|Constants
operator|.
name|FS_S3A
operator|+
literal|":/"
operator|+
name|parentStr
operator|+
literal|"/"
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|parent
argument_list|,
name|childStr
argument_list|)
decl_stmt|;
name|boolean
name|isDir
init|=
name|item
operator|.
name|hasAttribute
argument_list|(
name|IS_DIR
argument_list|)
operator|&&
name|item
operator|.
name|getBoolean
argument_list|(
name|IS_DIR
argument_list|)
decl_stmt|;
name|boolean
name|isAuthoritativeDir
init|=
literal|false
decl_stmt|;
specifier|final
name|S3AFileStatus
name|fileStatus
decl_stmt|;
name|long
name|lastUpdated
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|isDir
condition|)
block|{
name|isAuthoritativeDir
operator|=
operator|!
name|IGNORED_FIELDS
operator|.
name|contains
argument_list|(
name|IS_AUTHORITATIVE
argument_list|)
operator|&&
name|item
operator|.
name|hasAttribute
argument_list|(
name|IS_AUTHORITATIVE
argument_list|)
operator|&&
name|item
operator|.
name|getBoolean
argument_list|(
name|IS_AUTHORITATIVE
argument_list|)
expr_stmt|;
name|fileStatus
operator|=
name|DynamoDBMetadataStore
operator|.
name|makeDirStatus
argument_list|(
name|path
argument_list|,
name|username
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|len
init|=
name|item
operator|.
name|hasAttribute
argument_list|(
name|FILE_LENGTH
argument_list|)
condition|?
name|item
operator|.
name|getLong
argument_list|(
name|FILE_LENGTH
argument_list|)
else|:
literal|0
decl_stmt|;
name|long
name|modTime
init|=
name|item
operator|.
name|hasAttribute
argument_list|(
name|MOD_TIME
argument_list|)
condition|?
name|item
operator|.
name|getLong
argument_list|(
name|MOD_TIME
argument_list|)
else|:
literal|0
decl_stmt|;
name|long
name|block
init|=
name|item
operator|.
name|hasAttribute
argument_list|(
name|BLOCK_SIZE
argument_list|)
condition|?
name|item
operator|.
name|getLong
argument_list|(
name|BLOCK_SIZE
argument_list|)
else|:
literal|0
decl_stmt|;
name|String
name|eTag
init|=
name|item
operator|.
name|getString
argument_list|(
name|ETAG
argument_list|)
decl_stmt|;
name|String
name|versionId
init|=
name|item
operator|.
name|getString
argument_list|(
name|VERSION_ID
argument_list|)
decl_stmt|;
name|fileStatus
operator|=
operator|new
name|S3AFileStatus
argument_list|(
name|len
argument_list|,
name|modTime
argument_list|,
name|path
argument_list|,
name|block
argument_list|,
name|username
argument_list|,
name|eTag
argument_list|,
name|versionId
argument_list|)
expr_stmt|;
block|}
name|lastUpdated
operator|=
operator|!
name|IGNORED_FIELDS
operator|.
name|contains
argument_list|(
name|LAST_UPDATED
argument_list|)
operator|&&
name|item
operator|.
name|hasAttribute
argument_list|(
name|LAST_UPDATED
argument_list|)
condition|?
name|item
operator|.
name|getLong
argument_list|(
name|LAST_UPDATED
argument_list|)
else|:
literal|0
expr_stmt|;
name|boolean
name|isDeleted
init|=
name|item
operator|.
name|hasAttribute
argument_list|(
name|IS_DELETED
argument_list|)
operator|&&
name|item
operator|.
name|getBoolean
argument_list|(
name|IS_DELETED
argument_list|)
decl_stmt|;
return|return
operator|new
name|DDBPathMetadata
argument_list|(
name|fileStatus
argument_list|,
name|Tristate
operator|.
name|UNKNOWN
argument_list|,
name|isDeleted
argument_list|,
name|isAuthoritativeDir
argument_list|,
name|lastUpdated
argument_list|)
return|;
block|}
comment|/**    * Converts a {@link DDBPathMetadata} to a DynamoDB item.    *    * Can ignore {@code IS_AUTHORITATIVE} flag if {@code ignoreIsAuthFlag} is    * true.    *    * @param meta {@link DDBPathMetadata} to convert    * @return {@code meta} converted to DynamoDB item    */
DECL|method|pathMetadataToItem (DDBPathMetadata meta)
specifier|static
name|Item
name|pathMetadataToItem
parameter_list|(
name|DDBPathMetadata
name|meta
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|meta
argument_list|)
expr_stmt|;
specifier|final
name|S3AFileStatus
name|status
init|=
name|meta
operator|.
name|getFileStatus
argument_list|()
decl_stmt|;
specifier|final
name|Item
name|item
init|=
operator|new
name|Item
argument_list|()
operator|.
name|withPrimaryKey
argument_list|(
name|pathToKey
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|item
operator|.
name|withBoolean
argument_list|(
name|IS_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|IGNORED_FIELDS
operator|.
name|contains
argument_list|(
name|IS_AUTHORITATIVE
argument_list|)
condition|)
block|{
name|item
operator|.
name|withBoolean
argument_list|(
name|IS_AUTHORITATIVE
argument_list|,
name|meta
operator|.
name|isAuthoritativeDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|item
operator|.
name|withLong
argument_list|(
name|FILE_LENGTH
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
operator|.
name|withLong
argument_list|(
name|MOD_TIME
argument_list|,
name|status
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|withLong
argument_list|(
name|BLOCK_SIZE
argument_list|,
name|status
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|status
operator|.
name|getETag
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|item
operator|.
name|withString
argument_list|(
name|ETAG
argument_list|,
name|status
operator|.
name|getETag
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|status
operator|.
name|getVersionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|item
operator|.
name|withString
argument_list|(
name|VERSION_ID
argument_list|,
name|status
operator|.
name|getVersionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|item
operator|.
name|withBoolean
argument_list|(
name|IS_DELETED
argument_list|,
name|meta
operator|.
name|isDeleted
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|IGNORED_FIELDS
operator|.
name|contains
argument_list|(
name|LAST_UPDATED
argument_list|)
condition|)
block|{
name|item
operator|.
name|withLong
argument_list|(
name|LAST_UPDATED
argument_list|,
name|meta
operator|.
name|getLastUpdated
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|item
return|;
block|}
comment|/**    * The version marker has a primary key whose PARENT is {@code name};    * this MUST NOT be a value which represents an absolute path.    * @param name name of the version marker    * @param version version number    * @param timestamp creation timestamp    * @return an item representing a version marker.    */
DECL|method|createVersionMarker (String name, int version, long timestamp)
specifier|static
name|Item
name|createVersionMarker
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|version
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
return|return
operator|new
name|Item
argument_list|()
operator|.
name|withPrimaryKey
argument_list|(
name|createVersionMarkerPrimaryKey
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|withInt
argument_list|(
name|TABLE_VERSION
argument_list|,
name|version
argument_list|)
operator|.
name|withLong
argument_list|(
name|TABLE_CREATED
argument_list|,
name|timestamp
argument_list|)
return|;
block|}
comment|/**    * Create the primary key of the version marker.    * @param name key name    * @return the key to use when registering or resolving version markers    */
DECL|method|createVersionMarkerPrimaryKey (String name)
specifier|static
name|PrimaryKey
name|createVersionMarkerPrimaryKey
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|PrimaryKey
argument_list|(
name|PARENT
argument_list|,
name|name
argument_list|,
name|CHILD
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/**    * Extract the version from a version marker item.    * @param marker version marker item    * @return the extracted version field    * @throws IOException if the item is not a version marker    */
DECL|method|extractVersionFromMarker (Item marker)
specifier|static
name|int
name|extractVersionFromMarker
parameter_list|(
name|Item
name|marker
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|marker
operator|.
name|hasAttribute
argument_list|(
name|TABLE_VERSION
argument_list|)
condition|)
block|{
return|return
name|marker
operator|.
name|getInt
argument_list|(
name|TABLE_VERSION
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|E_NOT_VERSION_MARKER
operator|+
name|marker
argument_list|)
throw|;
block|}
block|}
comment|/**    * Extract the creation time, if present.    * @param marker version marker item    * @return the creation time, or null    * @throws IOException if the item is not a version marker    */
DECL|method|extractCreationTimeFromMarker (Item marker)
specifier|static
name|Long
name|extractCreationTimeFromMarker
parameter_list|(
name|Item
name|marker
parameter_list|)
block|{
if|if
condition|(
name|marker
operator|.
name|hasAttribute
argument_list|(
name|TABLE_CREATED
argument_list|)
condition|)
block|{
return|return
name|marker
operator|.
name|getLong
argument_list|(
name|TABLE_CREATED
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Converts a collection {@link DDBPathMetadata} to a collection DynamoDB    * items.    *    * @see #pathMetadataToItem(DDBPathMetadata)    */
DECL|method|pathMetadataToItem (Collection<DDBPathMetadata> metas)
specifier|static
name|Item
index|[]
name|pathMetadataToItem
parameter_list|(
name|Collection
argument_list|<
name|DDBPathMetadata
argument_list|>
name|metas
parameter_list|)
block|{
if|if
condition|(
name|metas
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Item
index|[]
name|items
init|=
operator|new
name|Item
index|[
name|metas
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DDBPathMetadata
name|meta
range|:
name|metas
control|)
block|{
name|items
index|[
name|i
operator|++
index|]
operator|=
name|pathMetadataToItem
argument_list|(
name|meta
argument_list|)
expr_stmt|;
block|}
return|return
name|items
return|;
block|}
comment|/**    * Converts a {@link Path} to a DynamoDB equality condition on that path as    * parent, suitable for querying all direct children of the path.    *    * @param path the path; can not be null    * @return DynamoDB equality condition on {@code path} as parent    */
DECL|method|pathToParentKeyAttribute (Path path)
specifier|static
name|KeyAttribute
name|pathToParentKeyAttribute
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
operator|new
name|KeyAttribute
argument_list|(
name|PARENT
argument_list|,
name|pathToParentKey
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * e.g. {@code pathToParentKey(s3a://bucket/path/a) -> /bucket/path/a}    * @param path path to convert    * @return string for parent key    */
annotation|@
name|VisibleForTesting
DECL|method|pathToParentKey (Path path)
specifier|public
specifier|static
name|String
name|pathToParentKey
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|path
operator|.
name|isUriPathAbsolute
argument_list|()
argument_list|,
literal|"Path not absolute: '%s'"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|path
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|String
name|bucket
init|=
name|uri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|bucket
argument_list|)
argument_list|,
literal|"Path missing bucket %s"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|String
name|pKey
init|=
literal|"/"
operator|+
name|bucket
operator|+
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// Strip trailing slash
if|if
condition|(
name|pKey
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|pKey
operator|=
name|pKey
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pKey
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|pKey
return|;
block|}
comment|/**    * Converts a {@link Path} to a DynamoDB key, suitable for getting the item    * matching the path.    *    * @param path the path; can not be null    * @return DynamoDB key for item matching {@code path}    */
DECL|method|pathToKey (Path path)
specifier|static
name|PrimaryKey
name|pathToKey
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|path
operator|.
name|isRoot
argument_list|()
argument_list|,
literal|"Root path is not mapped to any PrimaryKey"
argument_list|)
expr_stmt|;
return|return
operator|new
name|PrimaryKey
argument_list|(
name|PARENT
argument_list|,
name|pathToParentKey
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|,
name|CHILD
argument_list|,
name|path
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Converts a collection of {@link Path} to a collection of DynamoDB keys.    *    * @see #pathToKey(Path)    */
DECL|method|pathToKey (Collection<Path> paths)
specifier|static
name|PrimaryKey
index|[]
name|pathToKey
parameter_list|(
name|Collection
argument_list|<
name|Path
argument_list|>
name|paths
parameter_list|)
block|{
if|if
condition|(
name|paths
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|PrimaryKey
index|[]
name|keys
init|=
operator|new
name|PrimaryKey
index|[
name|paths
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Path
name|p
range|:
name|paths
control|)
block|{
name|keys
index|[
name|i
operator|++
index|]
operator|=
name|pathToKey
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
name|keys
return|;
block|}
comment|/**    * There is no need to instantiate this class.    */
DECL|method|PathMetadataDynamoDBTranslation ()
specifier|private
name|PathMetadataDynamoDBTranslation
parameter_list|()
block|{   }
comment|/**    * Convert a collection of metadata entries to a list    * of DDBPathMetadata entries.    * If the sources are already DDBPathMetadata instances, they    * are copied directly into the new list, otherwise new    * instances are created.    * @param pathMetadatas source data    * @return the converted list.    */
DECL|method|pathMetaToDDBPathMeta ( Collection<? extends PathMetadata> pathMetadatas)
specifier|static
name|List
argument_list|<
name|DDBPathMetadata
argument_list|>
name|pathMetaToDDBPathMeta
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|PathMetadata
argument_list|>
name|pathMetadatas
parameter_list|)
block|{
return|return
name|pathMetadatas
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|p
lambda|->
operator|(
name|p
operator|instanceof
name|DDBPathMetadata
operator|)
condition|?
operator|(
name|DDBPathMetadata
operator|)
name|p
else|:
operator|new
name|DDBPathMetadata
argument_list|(
name|p
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Convert an item's (parent, child) key to a string value    * for logging. There is no validation of the item.    * @param item item.    * @return an s3a:// prefixed string.    */
DECL|method|itemPrimaryKeyToString (Item item)
specifier|static
name|String
name|itemPrimaryKeyToString
parameter_list|(
name|Item
name|item
parameter_list|)
block|{
name|String
name|parent
init|=
name|item
operator|.
name|getString
argument_list|(
name|PARENT
argument_list|)
decl_stmt|;
name|String
name|child
init|=
name|item
operator|.
name|getString
argument_list|(
name|CHILD
argument_list|)
decl_stmt|;
return|return
literal|"s3a://"
operator|+
name|parent
operator|+
literal|"/"
operator|+
name|child
return|;
block|}
comment|/**    * Convert an item's (parent, child) key to a string value    * for logging. There is no validation of the item.    * @param item item.    * @return an s3a:// prefixed string.    */
DECL|method|primaryKeyToString (PrimaryKey item)
specifier|static
name|String
name|primaryKeyToString
parameter_list|(
name|PrimaryKey
name|item
parameter_list|)
block|{
name|Collection
argument_list|<
name|KeyAttribute
argument_list|>
name|c
init|=
name|item
operator|.
name|getComponents
argument_list|()
decl_stmt|;
name|String
name|parent
init|=
literal|""
decl_stmt|;
name|String
name|child
init|=
literal|""
decl_stmt|;
for|for
control|(
name|KeyAttribute
name|attr
range|:
name|c
control|)
block|{
switch|switch
condition|(
name|attr
operator|.
name|getName
argument_list|()
condition|)
block|{
case|case
name|PARENT
case|:
name|parent
operator|=
name|attr
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
case|case
name|CHILD
case|:
name|child
operator|=
name|attr
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
default|default:
block|}
block|}
return|return
literal|"s3a://"
operator|+
name|parent
operator|+
literal|"/"
operator|+
name|child
return|;
block|}
block|}
end_class

end_unit

