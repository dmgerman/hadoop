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
name|concurrent
operator|.
name|Callable
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|security
operator|.
name|UserGroupInformation
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
name|test
operator|.
name|LambdaTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import static
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
operator|.
name|HASH
import|;
end_import

begin_import
import|import static
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
operator|.
name|RANGE
import|;
end_import

begin_import
import|import static
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
operator|.
name|S
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|anyOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
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
operator|.
name|PathMetadataDynamoDBTranslation
operator|.
name|*
import|;
end_import

begin_import
import|import static
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
operator|.
name|DynamoDBMetadataStore
operator|.
name|VERSION_MARKER_ITEM_NAME
import|;
end_import

begin_import
import|import static
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
operator|.
name|DynamoDBMetadataStore
operator|.
name|VERSION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|never
import|;
end_import

begin_comment
comment|/**  * Test the PathMetadataDynamoDBTranslation is able to translate between domain  * model objects and DynamoDB items.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestPathMetadataDynamoDBTranslation
specifier|public
class|class
name|TestPathMetadataDynamoDBTranslation
extends|extends
name|Assert
block|{
DECL|field|TEST_DIR_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_DIR_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"s3a://test-bucket/myDir"
argument_list|)
decl_stmt|;
DECL|field|TEST_FILE_LENGTH
specifier|private
specifier|static
specifier|final
name|long
name|TEST_FILE_LENGTH
init|=
literal|100
decl_stmt|;
DECL|field|TEST_MOD_TIME
specifier|private
specifier|static
specifier|final
name|long
name|TEST_MOD_TIME
init|=
literal|9999
decl_stmt|;
DECL|field|TEST_BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|long
name|TEST_BLOCK_SIZE
init|=
literal|128
decl_stmt|;
DECL|field|TEST_ETAG
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ETAG
init|=
literal|"abc"
decl_stmt|;
DECL|field|TEST_VERSION_ID
specifier|private
specifier|static
specifier|final
name|String
name|TEST_VERSION_ID
init|=
literal|"def"
decl_stmt|;
DECL|field|TEST_FILE_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_FILE_PATH
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR_PATH
argument_list|,
literal|"myFile"
argument_list|)
decl_stmt|;
DECL|field|testFileItem
specifier|private
specifier|final
name|Item
name|testFileItem
decl_stmt|;
DECL|field|testFilePathMetadata
specifier|private
specifier|final
name|DDBPathMetadata
name|testFilePathMetadata
decl_stmt|;
DECL|field|testDirItem
specifier|private
specifier|final
name|Item
name|testDirItem
decl_stmt|;
DECL|field|testDirPathMetadata
specifier|private
specifier|final
name|DDBPathMetadata
name|testDirPathMetadata
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|params ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|params
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|username
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
comment|// with etag and versionId
block|{
operator|new
name|Item
argument_list|()
operator|.
name|withPrimaryKey
argument_list|(
name|PARENT
argument_list|,
name|pathToParentKey
argument_list|(
name|TEST_FILE_PATH
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|,
name|CHILD
argument_list|,
name|TEST_FILE_PATH
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withBoolean
argument_list|(
name|IS_DIR
argument_list|,
literal|false
argument_list|)
operator|.
name|withLong
argument_list|(
name|FILE_LENGTH
argument_list|,
name|TEST_FILE_LENGTH
argument_list|)
operator|.
name|withLong
argument_list|(
name|MOD_TIME
argument_list|,
name|TEST_MOD_TIME
argument_list|)
operator|.
name|withLong
argument_list|(
name|BLOCK_SIZE
argument_list|,
name|TEST_BLOCK_SIZE
argument_list|)
operator|.
name|withString
argument_list|(
name|ETAG
argument_list|,
name|TEST_ETAG
argument_list|)
operator|.
name|withString
argument_list|(
name|VERSION_ID
argument_list|,
name|TEST_VERSION_ID
argument_list|)
block|,
operator|new
name|DDBPathMetadata
argument_list|(
operator|new
name|S3AFileStatus
argument_list|(
name|TEST_FILE_LENGTH
argument_list|,
name|TEST_MOD_TIME
argument_list|,
name|TEST_FILE_PATH
argument_list|,
name|TEST_BLOCK_SIZE
argument_list|,
name|username
argument_list|,
name|TEST_ETAG
argument_list|,
name|TEST_VERSION_ID
argument_list|)
argument_list|)
block|}
block|,
comment|// without etag or versionId
block|{
operator|new
name|Item
argument_list|()
operator|.
name|withPrimaryKey
argument_list|(
name|PARENT
argument_list|,
name|pathToParentKey
argument_list|(
name|TEST_FILE_PATH
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|,
name|CHILD
argument_list|,
name|TEST_FILE_PATH
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withBoolean
argument_list|(
name|IS_DIR
argument_list|,
literal|false
argument_list|)
operator|.
name|withLong
argument_list|(
name|FILE_LENGTH
argument_list|,
name|TEST_FILE_LENGTH
argument_list|)
operator|.
name|withLong
argument_list|(
name|MOD_TIME
argument_list|,
name|TEST_MOD_TIME
argument_list|)
operator|.
name|withLong
argument_list|(
name|BLOCK_SIZE
argument_list|,
name|TEST_BLOCK_SIZE
argument_list|)
block|,
operator|new
name|DDBPathMetadata
argument_list|(
operator|new
name|S3AFileStatus
argument_list|(
name|TEST_FILE_LENGTH
argument_list|,
name|TEST_MOD_TIME
argument_list|,
name|TEST_FILE_PATH
argument_list|,
name|TEST_BLOCK_SIZE
argument_list|,
name|username
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|TestPathMetadataDynamoDBTranslation (Item item, DDBPathMetadata metadata)
specifier|public
name|TestPathMetadataDynamoDBTranslation
parameter_list|(
name|Item
name|item
parameter_list|,
name|DDBPathMetadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|testFileItem
operator|=
name|item
expr_stmt|;
name|testFilePathMetadata
operator|=
name|metadata
expr_stmt|;
name|String
name|username
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|testDirPathMetadata
operator|=
operator|new
name|DDBPathMetadata
argument_list|(
operator|new
name|S3AFileStatus
argument_list|(
literal|false
argument_list|,
name|TEST_DIR_PATH
argument_list|,
name|username
argument_list|)
argument_list|)
expr_stmt|;
name|testDirItem
operator|=
operator|new
name|Item
argument_list|()
expr_stmt|;
name|testDirItem
operator|.
name|withPrimaryKey
argument_list|(
name|PARENT
argument_list|,
literal|"/test-bucket"
argument_list|,
name|CHILD
argument_list|,
name|TEST_DIR_PATH
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withBoolean
argument_list|(
name|IS_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * It should not take long time as it doesn't involve remote server operation.    */
annotation|@
name|Rule
DECL|field|timeout
specifier|public
specifier|final
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|30
operator|*
literal|1000
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testKeySchema ()
specifier|public
name|void
name|testKeySchema
parameter_list|()
block|{
specifier|final
name|Collection
argument_list|<
name|KeySchemaElement
argument_list|>
name|keySchema
init|=
name|PathMetadataDynamoDBTranslation
operator|.
name|keySchema
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|keySchema
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"There should be HASH and RANGE key in key schema"
argument_list|,
literal|2
argument_list|,
name|keySchema
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|KeySchemaElement
name|element
range|:
name|keySchema
control|)
block|{
name|assertThat
argument_list|(
name|element
operator|.
name|getAttributeName
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|is
argument_list|(
name|PARENT
argument_list|)
argument_list|,
name|is
argument_list|(
name|CHILD
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|element
operator|.
name|getKeyType
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|is
argument_list|(
name|HASH
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|is
argument_list|(
name|RANGE
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|PathMetadataDynamoDBTranslation
operator|.
name|IGNORED_FIELDS
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAttributeDefinitions ()
specifier|public
name|void
name|testAttributeDefinitions
parameter_list|()
block|{
specifier|final
name|Collection
argument_list|<
name|AttributeDefinition
argument_list|>
name|attrs
init|=
name|PathMetadataDynamoDBTranslation
operator|.
name|attributeDefinitions
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|attrs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"There should be HASH and RANGE attributes"
argument_list|,
literal|2
argument_list|,
name|attrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AttributeDefinition
name|definition
range|:
name|attrs
control|)
block|{
name|assertThat
argument_list|(
name|definition
operator|.
name|getAttributeName
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|is
argument_list|(
name|PARENT
argument_list|)
argument_list|,
name|is
argument_list|(
name|CHILD
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|S
operator|.
name|toString
argument_list|()
argument_list|,
name|definition
operator|.
name|getAttributeType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testItemToPathMetadata ()
specifier|public
name|void
name|testItemToPathMetadata
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|itemToPathMetadata
argument_list|(
literal|null
argument_list|,
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|testDirItem
argument_list|,
name|itemToPathMetadata
argument_list|(
name|testDirItem
argument_list|,
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|testFileItem
argument_list|,
name|itemToPathMetadata
argument_list|(
name|testFileItem
argument_list|,
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the Item and PathMetadata objects hold the same information.    */
DECL|method|verify (Item item, PathMetadata meta)
specifier|private
specifier|static
name|void
name|verify
parameter_list|(
name|Item
name|item
parameter_list|,
name|PathMetadata
name|meta
parameter_list|)
block|{
name|assertNotNull
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
name|Path
name|path
init|=
name|status
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|item
operator|.
name|get
argument_list|(
name|PARENT
argument_list|)
argument_list|,
name|pathToParentKey
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|item
operator|.
name|get
argument_list|(
name|CHILD
argument_list|)
argument_list|,
name|path
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
name|isDir
argument_list|,
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
name|len
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|bSize
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
name|assertEquals
argument_list|(
name|bSize
argument_list|,
name|status
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
name|eTag
argument_list|,
name|status
operator|.
name|getETag
argument_list|()
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
name|versionId
argument_list|,
name|status
operator|.
name|getVersionId
argument_list|()
argument_list|)
expr_stmt|;
comment|/*      * S3AFileStatue#getModificationTime() reports the current time, so the      * following assertion is failing.      *      * long modTime = item.hasAttribute(MOD_TIME) ? item.getLong(MOD_TIME) : 0;      * assertEquals(modTime, status.getModificationTime());      */
block|}
annotation|@
name|Test
DECL|method|testPathMetadataToItem ()
specifier|public
name|void
name|testPathMetadataToItem
parameter_list|()
block|{
name|verify
argument_list|(
name|pathMetadataToItem
argument_list|(
name|testDirPathMetadata
argument_list|)
argument_list|,
name|testDirPathMetadata
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|pathMetadataToItem
argument_list|(
name|testFilePathMetadata
argument_list|)
argument_list|,
name|testFilePathMetadata
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPathToParentKeyAttribute ()
specifier|public
name|void
name|testPathToParentKeyAttribute
parameter_list|()
block|{
name|doTestPathToParentKeyAttribute
argument_list|(
name|TEST_DIR_PATH
argument_list|)
expr_stmt|;
name|doTestPathToParentKeyAttribute
argument_list|(
name|TEST_FILE_PATH
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestPathToParentKeyAttribute (Path path)
specifier|private
specifier|static
name|void
name|doTestPathToParentKeyAttribute
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
specifier|final
name|KeyAttribute
name|attr
init|=
name|pathToParentKeyAttribute
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|attr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PARENT
argument_list|,
name|attr
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// this path is expected as parent filed
name|assertEquals
argument_list|(
name|pathToParentKey
argument_list|(
name|path
argument_list|)
argument_list|,
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|pathToParentKey (Path p)
specifier|private
specifier|static
name|String
name|pathToParentKey
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|p
operator|.
name|isUriPathAbsolute
argument_list|()
argument_list|)
expr_stmt|;
name|URI
name|parentUri
init|=
name|p
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|String
name|bucket
init|=
name|parentUri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|String
name|s
init|=
literal|"/"
operator|+
name|bucket
operator|+
name|parentUri
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// strip trailing slash
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
annotation|@
name|Test
DECL|method|testPathToKey ()
specifier|public
name|void
name|testPathToKey
parameter_list|()
throws|throws
name|Exception
block|{
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
operator|new
name|Callable
argument_list|<
name|PrimaryKey
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PrimaryKey
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|pathToKey
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|doTestPathToKey
argument_list|(
name|TEST_DIR_PATH
argument_list|)
expr_stmt|;
name|doTestPathToKey
argument_list|(
name|TEST_FILE_PATH
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestPathToKey (Path path)
specifier|private
specifier|static
name|void
name|doTestPathToKey
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
specifier|final
name|PrimaryKey
name|key
init|=
name|pathToKey
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"There should be both HASH and RANGE keys"
argument_list|,
literal|2
argument_list|,
name|key
operator|.
name|getComponents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|KeyAttribute
name|keyAttribute
range|:
name|key
operator|.
name|getComponents
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|keyAttribute
operator|.
name|getName
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|is
argument_list|(
name|PARENT
argument_list|)
argument_list|,
name|is
argument_list|(
name|CHILD
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|PARENT
operator|.
name|equals
argument_list|(
name|keyAttribute
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|pathToParentKey
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|,
name|keyAttribute
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|path
operator|.
name|getName
argument_list|()
argument_list|,
name|keyAttribute
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testVersionRoundTrip ()
specifier|public
name|void
name|testVersionRoundTrip
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Item
name|marker
init|=
name|createVersionMarker
argument_list|(
name|VERSION_MARKER_ITEM_NAME
argument_list|,
name|VERSION
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Extracted version from "
operator|+
name|marker
argument_list|,
name|VERSION
argument_list|,
name|extractVersionFromMarker
argument_list|(
name|marker
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVersionMarkerNotStatusIllegalPath ()
specifier|public
name|void
name|testVersionMarkerNotStatusIllegalPath
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Item
name|marker
init|=
name|createVersionMarker
argument_list|(
name|VERSION_MARKER_ITEM_NAME
argument_list|,
name|VERSION
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Path metadata fromfrom "
operator|+
name|marker
argument_list|,
name|itemToPathMetadata
argument_list|(
name|marker
argument_list|,
literal|"alice"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test when translating an {@link Item} to {@link DDBPathMetadata} works    * if {@code IS_AUTHORITATIVE} flag is ignored.    */
annotation|@
name|Test
DECL|method|testIsAuthoritativeCompatibilityItemToPathMetadata ()
specifier|public
name|void
name|testIsAuthoritativeCompatibilityItemToPathMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|Item
name|item
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|testDirItem
argument_list|)
decl_stmt|;
name|item
operator|.
name|withBoolean
argument_list|(
name|IS_AUTHORITATIVE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|PathMetadataDynamoDBTranslation
operator|.
name|IGNORED_FIELDS
operator|.
name|add
argument_list|(
name|IS_AUTHORITATIVE
argument_list|)
expr_stmt|;
specifier|final
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|DDBPathMetadata
name|meta
init|=
name|itemToPathMetadata
argument_list|(
name|item
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|item
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|getBoolean
argument_list|(
name|IS_AUTHORITATIVE
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta
operator|.
name|isAuthoritativeDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test when translating an {@link DDBPathMetadata} to {@link Item} works    * if {@code IS_AUTHORITATIVE} flag is ignored.    */
annotation|@
name|Test
DECL|method|testIsAuthoritativeCompatibilityPathMetadataToItem ()
specifier|public
name|void
name|testIsAuthoritativeCompatibilityPathMetadataToItem
parameter_list|()
block|{
name|DDBPathMetadata
name|meta
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|testFilePathMetadata
argument_list|)
decl_stmt|;
name|meta
operator|.
name|setAuthoritativeDir
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PathMetadataDynamoDBTranslation
operator|.
name|IGNORED_FIELDS
operator|.
name|add
argument_list|(
name|IS_AUTHORITATIVE
argument_list|)
expr_stmt|;
name|Item
name|item
init|=
name|pathMetadataToItem
argument_list|(
name|meta
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|meta
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|isAuthoritativeDir
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|item
operator|.
name|hasAttribute
argument_list|(
name|IS_AUTHORITATIVE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test when translating an {@link Item} to {@link DDBPathMetadata} works    * if {@code LAST_UPDATED} flag is ignored.    */
annotation|@
name|Test
DECL|method|testIsLastUpdatedCompatibilityItemToPathMetadata ()
specifier|public
name|void
name|testIsLastUpdatedCompatibilityItemToPathMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|Item
name|item
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|testDirItem
argument_list|)
decl_stmt|;
name|item
operator|.
name|withLong
argument_list|(
name|LAST_UPDATED
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|PathMetadataDynamoDBTranslation
operator|.
name|IGNORED_FIELDS
operator|.
name|add
argument_list|(
name|LAST_UPDATED
argument_list|)
expr_stmt|;
specifier|final
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|DDBPathMetadata
name|meta
init|=
name|itemToPathMetadata
argument_list|(
name|item
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|item
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|getLong
argument_list|(
name|LAST_UPDATED
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta
operator|.
name|isAuthoritativeDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test when translating an {@link DDBPathMetadata} to {@link Item} works    * if {@code LAST_UPDATED} flag is ignored.    */
annotation|@
name|Test
DECL|method|testIsLastUpdatedCompatibilityPathMetadataToItem ()
specifier|public
name|void
name|testIsLastUpdatedCompatibilityPathMetadataToItem
parameter_list|()
block|{
name|DDBPathMetadata
name|meta
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|testFilePathMetadata
argument_list|)
decl_stmt|;
name|meta
operator|.
name|setLastUpdated
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|PathMetadataDynamoDBTranslation
operator|.
name|IGNORED_FIELDS
operator|.
name|add
argument_list|(
name|LAST_UPDATED
argument_list|)
expr_stmt|;
name|Item
name|item
init|=
name|pathMetadataToItem
argument_list|(
name|meta
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|meta
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getLastUpdated
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|item
operator|.
name|hasAttribute
argument_list|(
name|LAST_UPDATED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

