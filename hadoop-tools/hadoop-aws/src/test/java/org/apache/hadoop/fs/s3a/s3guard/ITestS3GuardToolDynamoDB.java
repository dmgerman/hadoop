begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FileNotFoundException
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|DynamoDB
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
name|Table
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
name|ListTagsOfResourceRequest
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
name|ResourceInUseException
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
name|ResourceNotFoundException
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
name|Tag
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
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AssumptionViolatedException
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
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|S3AFileSystem
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
name|s3guard
operator|.
name|S3GuardTool
operator|.
name|Destroy
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
name|s3guard
operator|.
name|S3GuardTool
operator|.
name|Init
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
name|util
operator|.
name|ExitUtil
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
name|Constants
operator|.
name|S3GUARD_DDB_REGION_KEY
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
name|Constants
operator|.
name|S3GUARD_DDB_TABLE_NAME_KEY
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
name|Constants
operator|.
name|S3GUARD_DDB_TABLE_TAG
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
name|S3ATestUtils
operator|.
name|removeBucketOverrides
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
name|S3AUtils
operator|.
name|setBucketOption
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
name|S3GuardTool
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
name|S3GuardToolTestHelper
operator|.
name|exec
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
import|;
end_import

begin_comment
comment|/**  * Test S3Guard related CLI commands against DynamoDB.  */
end_comment

begin_class
DECL|class|ITestS3GuardToolDynamoDB
specifier|public
class|class
name|ITestS3GuardToolDynamoDB
extends|extends
name|AbstractS3GuardToolTestBase
block|{
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
try|try
block|{
name|getMetadataStore
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
literal|"Test only applies when DynamoDB is used for S3Guard Store"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMetadataStore ()
specifier|protected
name|DynamoDBMetadataStore
name|getMetadataStore
parameter_list|()
block|{
return|return
operator|(
name|DynamoDBMetadataStore
operator|)
name|super
operator|.
name|getMetadataStore
argument_list|()
return|;
block|}
comment|// Check the existence of a given DynamoDB table.
DECL|method|exist (DynamoDB dynamoDB, String tableName)
specifier|private
specifier|static
name|boolean
name|exist
parameter_list|(
name|DynamoDB
name|dynamoDB
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|dynamoDB
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|tableName
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"empty table name"
argument_list|,
name|tableName
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Table
name|table
init|=
name|dynamoDB
operator|.
name|getTable
argument_list|(
name|tableName
argument_list|)
decl_stmt|;
name|table
operator|.
name|describe
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Test
DECL|method|testInvalidRegion ()
specifier|public
name|void
name|testInvalidRegion
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|testTableName
init|=
name|getTestTableName
argument_list|(
literal|"testInvalidRegion"
operator|+
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|testRegion
init|=
literal|"invalidRegion"
decl_stmt|;
comment|// Initialize MetadataStore
specifier|final
name|Init
name|initCmd
init|=
operator|new
name|Init
argument_list|(
name|getFileSystem
argument_list|()
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|res
init|=
name|initCmd
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"init"
block|,
literal|"-region"
block|,
name|testRegion
block|,
literal|"-meta"
block|,
literal|"dynamodb://"
operator|+
name|testTableName
block|}
argument_list|)
decl_stmt|;
return|return
literal|"Use of invalid region did not fail, returning "
operator|+
name|res
operator|+
literal|"- table may have been "
operator|+
literal|"created and not cleaned up: "
operator|+
name|testTableName
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDynamoTableTagging ()
specifier|public
name|void
name|testDynamoTableTagging
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
comment|// If the region is not set in conf, skip the test.
name|String
name|ddbRegion
init|=
name|conf
operator|.
name|get
argument_list|(
name|S3GUARD_DDB_REGION_KEY
argument_list|)
decl_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|S3GUARD_DDB_REGION_KEY
operator|+
literal|" should be set to run this test"
argument_list|,
name|ddbRegion
operator|!=
literal|null
operator|&&
operator|!
name|ddbRegion
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// setup
comment|// clear all table tagging config before this test
name|conf
operator|.
name|getPropsWithPrefix
argument_list|(
name|S3GUARD_DDB_TABLE_TAG
argument_list|)
operator|.
name|keySet
argument_list|()
operator|.
name|forEach
argument_list|(
name|propKey
lambda|->
name|conf
operator|.
name|unset
argument_list|(
name|S3GUARD_DDB_TABLE_TAG
operator|+
name|propKey
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|S3GUARD_DDB_TABLE_NAME_KEY
argument_list|,
name|getTestTableName
argument_list|(
literal|"testDynamoTableTagging-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|bucket
init|=
name|getFileSystem
argument_list|()
operator|.
name|getBucket
argument_list|()
decl_stmt|;
name|removeBucketOverrides
argument_list|(
name|bucket
argument_list|,
name|conf
argument_list|,
name|S3GUARD_DDB_TABLE_NAME_KEY
argument_list|,
name|S3GUARD_DDB_REGION_KEY
argument_list|)
expr_stmt|;
name|S3GuardTool
operator|.
name|Init
name|cmdR
init|=
operator|new
name|S3GuardTool
operator|.
name|Init
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tagMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|tagMap
operator|.
name|put
argument_list|(
literal|"hello"
argument_list|,
literal|"dynamo"
argument_list|)
expr_stmt|;
name|tagMap
operator|.
name|put
argument_list|(
literal|"tag"
argument_list|,
literal|"youre it"
argument_list|)
expr_stmt|;
name|String
index|[]
name|argsR
init|=
operator|new
name|String
index|[]
block|{
name|cmdR
operator|.
name|getName
argument_list|()
block|,
literal|"-tag"
block|,
name|tagMapToStringParams
argument_list|(
name|tagMap
argument_list|)
block|,
literal|"s3a://"
operator|+
name|bucket
operator|+
literal|"/"
block|}
decl_stmt|;
comment|// run
name|cmdR
operator|.
name|run
argument_list|(
name|argsR
argument_list|)
expr_stmt|;
comment|// Check. Should create new metadatastore with the table name set.
try|try
init|(
name|DynamoDBMetadataStore
name|ddbms
init|=
operator|new
name|DynamoDBMetadataStore
argument_list|()
init|)
block|{
name|ddbms
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
operator|new
name|S3Guard
operator|.
name|TtlTimeProvider
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|ListTagsOfResourceRequest
name|listTagsOfResourceRequest
init|=
operator|new
name|ListTagsOfResourceRequest
argument_list|()
operator|.
name|withResourceArn
argument_list|(
name|ddbms
operator|.
name|getTable
argument_list|()
operator|.
name|getDescription
argument_list|()
operator|.
name|getTableArn
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Tag
argument_list|>
name|tags
init|=
name|ddbms
operator|.
name|getAmazonDynamoDB
argument_list|()
operator|.
name|listTagsOfResource
argument_list|(
name|listTagsOfResourceRequest
argument_list|)
operator|.
name|getTags
argument_list|()
decl_stmt|;
comment|// assert
name|assertEquals
argument_list|(
name|tagMap
operator|.
name|size
argument_list|()
argument_list|,
name|tags
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Tag
name|tag
range|:
name|tags
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|tagMap
operator|.
name|get
argument_list|(
name|tag
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|tag
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// be sure to clean up - delete table
name|ddbms
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|tagMapToStringParams (Map<String, String> tagMap)
specifier|private
name|String
name|tagMapToStringParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tagMap
parameter_list|)
block|{
name|StringBuilder
name|stringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|kv
range|:
name|tagMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|stringBuilder
operator|.
name|append
argument_list|(
name|kv
operator|.
name|getKey
argument_list|()
operator|+
literal|"="
operator|+
name|kv
operator|.
name|getValue
argument_list|()
operator|+
literal|";"
argument_list|)
expr_stmt|;
block|}
return|return
name|stringBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getCapacities ()
specifier|private
name|DDBCapacities
name|getCapacities
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|DDBCapacities
operator|.
name|extractCapacities
argument_list|(
name|getMetadataStore
argument_list|()
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testDynamoDBInitDestroyCycle ()
specifier|public
name|void
name|testDynamoDBInitDestroyCycle
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|testTableName
init|=
name|getTestTableName
argument_list|(
literal|"testDynamoDBInitDestroy"
operator|+
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|testS3Url
init|=
name|path
argument_list|(
name|testTableName
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|DynamoDB
name|db
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Initialize MetadataStore
name|Init
name|initCmd
init|=
operator|new
name|Init
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|expectSuccess
argument_list|(
literal|"Init command did not exit successfully - see output"
argument_list|,
name|initCmd
argument_list|,
name|Init
operator|.
name|NAME
argument_list|,
literal|"-"
operator|+
name|READ_FLAG
argument_list|,
literal|"0"
argument_list|,
literal|"-"
operator|+
name|WRITE_FLAG
argument_list|,
literal|"0"
argument_list|,
literal|"-"
operator|+
name|META_FLAG
argument_list|,
literal|"dynamodb://"
operator|+
name|testTableName
argument_list|,
name|testS3Url
argument_list|)
expr_stmt|;
comment|// Verify it exists
name|MetadataStore
name|ms
init|=
name|getMetadataStore
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"metadata store should be DynamoDBMetadataStore"
argument_list|,
name|ms
operator|instanceof
name|DynamoDBMetadataStore
argument_list|)
expr_stmt|;
name|DynamoDBMetadataStore
name|dynamoMs
init|=
operator|(
name|DynamoDBMetadataStore
operator|)
name|ms
decl_stmt|;
name|db
operator|=
name|dynamoMs
operator|.
name|getDynamoDB
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s does not exist"
argument_list|,
name|testTableName
argument_list|)
argument_list|,
name|exist
argument_list|(
name|db
argument_list|,
name|testTableName
argument_list|)
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|fs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|String
name|bucket
init|=
name|fs
operator|.
name|getBucket
argument_list|()
decl_stmt|;
comment|// force in a new bucket
name|setBucketOption
argument_list|(
name|conf
argument_list|,
name|bucket
argument_list|,
name|Constants
operator|.
name|S3_METADATA_STORE_IMPL
argument_list|,
name|Constants
operator|.
name|S3GUARD_METASTORE_DYNAMO
argument_list|)
expr_stmt|;
name|initCmd
operator|=
operator|new
name|Init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|initOutput
init|=
name|exec
argument_list|(
name|initCmd
argument_list|,
literal|"init"
argument_list|,
literal|"-meta"
argument_list|,
literal|"dynamodb://"
operator|+
name|testTableName
argument_list|,
name|testS3Url
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No Dynamo diagnostics in output "
operator|+
name|initOutput
argument_list|,
name|initOutput
operator|.
name|contains
argument_list|(
name|DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
comment|// run a bucket info command and look for
comment|// confirmation that it got the output from DDB diags
name|S3GuardTool
operator|.
name|BucketInfo
name|infocmd
init|=
operator|new
name|S3GuardTool
operator|.
name|BucketInfo
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|info
init|=
name|exec
argument_list|(
name|infocmd
argument_list|,
name|S3GuardTool
operator|.
name|BucketInfo
operator|.
name|NAME
argument_list|,
literal|"-"
operator|+
name|S3GuardTool
operator|.
name|BucketInfo
operator|.
name|GUARDED_FLAG
argument_list|,
name|testS3Url
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No Dynamo diagnostics in output "
operator|+
name|info
argument_list|,
name|info
operator|.
name|contains
argument_list|(
name|DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No Dynamo diagnostics in output "
operator|+
name|info
argument_list|,
name|info
operator|.
name|contains
argument_list|(
name|DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
comment|// get the current values to set again
comment|// play with the set-capacity option
name|String
name|fsURI
init|=
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|DDBCapacities
name|original
init|=
name|getCapacities
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong billing mode in "
operator|+
name|info
argument_list|,
name|info
operator|.
name|contains
argument_list|(
name|BILLING_MODE_PER_REQUEST
argument_list|)
argument_list|)
expr_stmt|;
comment|// per-request tables fail here, so expect that
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|E_ON_DEMAND_NO_SET_CAPACITY
argument_list|,
parameter_list|()
lambda|->
name|exec
argument_list|(
name|newSetCapacity
argument_list|()
argument_list|,
name|SetCapacity
operator|.
name|NAME
argument_list|,
name|fsURI
argument_list|)
argument_list|)
expr_stmt|;
comment|// Destroy MetadataStore
name|Destroy
name|destroyCmd
init|=
operator|new
name|Destroy
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|destroyed
init|=
name|exec
argument_list|(
name|destroyCmd
argument_list|,
literal|"destroy"
argument_list|,
literal|"-meta"
argument_list|,
literal|"dynamodb://"
operator|+
name|testTableName
argument_list|,
name|testS3Url
argument_list|)
decl_stmt|;
comment|// Verify it does not exist
name|assertFalse
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s still exists"
argument_list|,
name|testTableName
argument_list|)
argument_list|,
name|exist
argument_list|(
name|db
argument_list|,
name|testTableName
argument_list|)
argument_list|)
expr_stmt|;
comment|// delete again and expect success again
name|expectSuccess
argument_list|(
literal|"Destroy command did not exit successfully - see output"
argument_list|,
name|destroyCmd
argument_list|,
literal|"destroy"
argument_list|,
literal|"-meta"
argument_list|,
literal|"dynamodb://"
operator|+
name|testTableName
argument_list|,
name|testS3Url
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"DynamoDB table %s does not exist"
argument_list|,
name|testTableName
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Table may have not been cleaned up: "
operator|+
name|testTableName
argument_list|)
expr_stmt|;
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|Table
name|table
init|=
name|db
operator|.
name|getTable
argument_list|(
name|testTableName
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|table
operator|.
name|delete
argument_list|()
expr_stmt|;
name|table
operator|.
name|waitForDelete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
decl||
name|ResourceInUseException
name|e
parameter_list|)
block|{
comment|/* Ignore */
block|}
block|}
block|}
block|}
block|}
DECL|method|newSetCapacity ()
specifier|private
name|S3GuardTool
name|newSetCapacity
parameter_list|()
block|{
name|S3GuardTool
name|setCapacity
init|=
operator|new
name|S3GuardTool
operator|.
name|SetCapacity
argument_list|(
name|getFileSystem
argument_list|()
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|setCapacity
operator|.
name|setStore
argument_list|(
name|getMetadataStore
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|setCapacity
return|;
block|}
annotation|@
name|Test
DECL|method|testDestroyUnknownTable ()
specifier|public
name|void
name|testDestroyUnknownTable
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|S3GuardTool
operator|.
name|Destroy
operator|.
name|NAME
argument_list|,
literal|"-region"
argument_list|,
literal|"us-west-2"
argument_list|,
literal|"-meta"
argument_list|,
literal|"dynamodb://"
operator|+
name|getTestTableName
argument_list|(
name|DYNAMODB_TABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCLIFsckWithoutParam ()
specifier|public
name|void
name|testCLIFsckWithoutParam
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|ExitUtil
operator|.
name|ExitException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|run
argument_list|(
name|Fsck
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCLIFsckWithParam ()
specifier|public
name|void
name|testCLIFsckWithParam
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|result
init|=
name|run
argument_list|(
name|S3GuardTool
operator|.
name|Fsck
operator|.
name|NAME
argument_list|,
literal|"-check"
argument_list|,
literal|"s3a://"
operator|+
name|getFileSystem
argument_list|()
operator|.
name|getBucket
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"This test serves the purpose to run fsck with the correct "
operator|+
literal|"parameters, so there will be no exception thrown. "
operator|+
literal|"The return value of the run: {}"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCLIFsckWithParamParentOfRoot ()
specifier|public
name|void
name|testCLIFsckWithParamParentOfRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
literal|"Invalid URI"
argument_list|,
parameter_list|()
lambda|->
name|run
argument_list|(
name|S3GuardTool
operator|.
name|Fsck
operator|.
name|NAME
argument_list|,
literal|"-check"
argument_list|,
literal|"s3a://"
operator|+
name|getFileSystem
argument_list|()
operator|.
name|getBucket
argument_list|()
operator|+
literal|"/.."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCLIFsckFailInitializeFs ()
specifier|public
name|void
name|testCLIFsckFailInitializeFs
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|,
literal|"does not exist"
argument_list|,
parameter_list|()
lambda|->
name|run
argument_list|(
name|S3GuardTool
operator|.
name|Fsck
operator|.
name|NAME
argument_list|,
literal|"-check"
argument_list|,
literal|"s3a://this-bucket-does-not-exist-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

