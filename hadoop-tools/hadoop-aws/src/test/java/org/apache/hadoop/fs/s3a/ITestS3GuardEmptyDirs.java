begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
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
name|io
operator|.
name|InputStream
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
name|Stream
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
name|s3
operator|.
name|AmazonS3
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
name|s3
operator|.
name|model
operator|.
name|ListObjectsV2Request
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
name|s3
operator|.
name|model
operator|.
name|ListObjectsV2Result
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
name|s3
operator|.
name|model
operator|.
name|PutObjectRequest
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
name|s3
operator|.
name|model
operator|.
name|S3ObjectSummary
import|;
end_import

begin_import
import|import
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
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
name|impl
operator|.
name|StatusProbeEnum
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
name|impl
operator|.
name|StoreContext
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
name|DDBPathMetadata
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
name|DynamoDBMetadataStore
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
name|MetadataStore
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
name|NullMetadataStore
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|assertRenameOutcome
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|touch
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
name|assume
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
name|assumeFilesystemHasMetadatastore
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
name|getStatusWithEmptyDirFlag
import|;
end_import

begin_comment
comment|/**  * Test logic around whether or not a directory is empty, with S3Guard enabled.  * The fact that S3AFileStatus has an isEmptyDirectory flag in it makes caching  * S3AFileStatus's really tricky, as the flag can change as a side effect of  * changes to other paths.  * After S3Guard is merged to trunk, we should try to remove the  * isEmptyDirectory flag from S3AFileStatus, or maintain it outside  * of the MetadataStore.  */
end_comment

begin_class
DECL|class|ITestS3GuardEmptyDirs
specifier|public
class|class
name|ITestS3GuardEmptyDirs
extends|extends
name|AbstractS3ATestBase
block|{
annotation|@
name|Test
DECL|method|testRenameEmptyDir ()
specifier|public
name|void
name|testRenameEmptyDir
parameter_list|()
throws|throws
name|Throwable
block|{
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|basePath
init|=
name|path
argument_list|(
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|sourceDir
init|=
operator|new
name|Path
argument_list|(
name|basePath
argument_list|,
literal|"AAA-source"
argument_list|)
decl_stmt|;
name|String
name|sourceDirMarker
init|=
name|fs
operator|.
name|pathToKey
argument_list|(
name|sourceDir
argument_list|)
operator|+
literal|"/"
decl_stmt|;
name|Path
name|destDir
init|=
operator|new
name|Path
argument_list|(
name|basePath
argument_list|,
literal|"BBB-dest"
argument_list|)
decl_stmt|;
name|String
name|destDirMarker
init|=
name|fs
operator|.
name|pathToKey
argument_list|(
name|destDir
argument_list|)
operator|+
literal|"/"
decl_stmt|;
comment|// set things up.
name|mkdirs
argument_list|(
name|sourceDir
argument_list|)
expr_stmt|;
comment|// there'a source directory marker
name|fs
operator|.
name|getObjectMetadata
argument_list|(
name|sourceDirMarker
argument_list|)
expr_stmt|;
name|S3AFileStatus
name|srcStatus
init|=
name|getEmptyDirStatus
argument_list|(
name|sourceDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Must be an empty dir: "
operator|+
name|srcStatus
argument_list|,
name|Tristate
operator|.
name|TRUE
argument_list|,
name|srcStatus
operator|.
name|isEmptyDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// do the rename
name|assertRenameOutcome
argument_list|(
name|fs
argument_list|,
name|sourceDir
argument_list|,
name|destDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|S3AFileStatus
name|destStatus
init|=
name|getEmptyDirStatus
argument_list|(
name|destDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Must be an empty dir: "
operator|+
name|destStatus
argument_list|,
name|Tristate
operator|.
name|TRUE
argument_list|,
name|destStatus
operator|.
name|isEmptyDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// source does not exist.
name|intercept
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|getEmptyDirStatus
argument_list|(
name|sourceDir
argument_list|)
argument_list|)
expr_stmt|;
comment|// and verify that there's no dir marker hidden under a tombstone
name|intercept
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|Invoker
operator|.
name|once
argument_list|(
literal|"HEAD"
argument_list|,
name|sourceDirMarker
argument_list|,
parameter_list|()
lambda|->
name|fs
operator|.
name|getObjectMetadata
argument_list|(
name|sourceDirMarker
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// the parent dir mustn't be confused
name|S3AFileStatus
name|baseStatus
init|=
name|getEmptyDirStatus
argument_list|(
name|basePath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Must not be an empty dir: "
operator|+
name|baseStatus
argument_list|,
name|Tristate
operator|.
name|FALSE
argument_list|,
name|baseStatus
operator|.
name|isEmptyDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// and verify the dest dir has a marker
name|fs
operator|.
name|getObjectMetadata
argument_list|(
name|destDirMarker
argument_list|)
expr_stmt|;
block|}
DECL|method|getEmptyDirStatus (Path dir)
specifier|private
name|S3AFileStatus
name|getEmptyDirStatus
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFileSystem
argument_list|()
operator|.
name|innerGetFileStatus
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|,
name|StatusProbeEnum
operator|.
name|ALL
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testEmptyDirs ()
specifier|public
name|void
name|testEmptyDirs
parameter_list|()
throws|throws
name|Exception
block|{
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|assumeFilesystemHasMetadatastore
argument_list|(
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
name|MetadataStore
name|configuredMs
init|=
name|fs
operator|.
name|getMetadataStore
argument_list|()
decl_stmt|;
name|Path
name|existingDir
init|=
name|path
argument_list|(
literal|"existing-dir"
argument_list|)
decl_stmt|;
name|Path
name|existingFile
init|=
name|path
argument_list|(
literal|"existing-dir/existing-file"
argument_list|)
decl_stmt|;
try|try
block|{
comment|// 1. Simulate files already existing in the bucket before we started our
comment|// cluster.  Temporarily disable the MetadataStore so it doesn't witness
comment|// us creating these files.
name|fs
operator|.
name|setMetadataStore
argument_list|(
operator|new
name|NullMetadataStore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|existingDir
argument_list|)
argument_list|)
expr_stmt|;
name|touch
argument_list|(
name|fs
argument_list|,
name|existingFile
argument_list|)
expr_stmt|;
comment|// 2. Simulate (from MetadataStore's perspective) starting our cluster and
comment|// creating a file in an existing directory.
name|fs
operator|.
name|setMetadataStore
argument_list|(
name|configuredMs
argument_list|)
expr_stmt|;
comment|// "start cluster"
name|Path
name|newFile
init|=
name|path
argument_list|(
literal|"existing-dir/new-file"
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|fs
argument_list|,
name|newFile
argument_list|)
expr_stmt|;
name|S3AFileStatus
name|status
init|=
name|fs
operator|.
name|innerGetFileStatus
argument_list|(
name|existingDir
argument_list|,
literal|true
argument_list|,
name|StatusProbeEnum
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should not be empty dir"
argument_list|,
name|Tristate
operator|.
name|FALSE
argument_list|,
name|status
operator|.
name|isEmptyDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// 3. Assert that removing the only file the MetadataStore witnessed
comment|// being created doesn't cause it to think the directory is now empty.
name|fs
operator|.
name|delete
argument_list|(
name|newFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|status
operator|=
name|fs
operator|.
name|innerGetFileStatus
argument_list|(
name|existingDir
argument_list|,
literal|true
argument_list|,
name|StatusProbeEnum
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should not be empty dir"
argument_list|,
name|Tristate
operator|.
name|FALSE
argument_list|,
name|status
operator|.
name|isEmptyDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// 4. Assert that removing the final file, that existed "before"
comment|// MetadataStore started, *does* cause the directory to be marked empty.
name|fs
operator|.
name|delete
argument_list|(
name|existingFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|status
operator|=
name|fs
operator|.
name|innerGetFileStatus
argument_list|(
name|existingDir
argument_list|,
literal|true
argument_list|,
name|StatusProbeEnum
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should be empty dir now"
argument_list|,
name|Tristate
operator|.
name|TRUE
argument_list|,
name|status
operator|.
name|isEmptyDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|configuredMs
operator|.
name|forgetMetadata
argument_list|(
name|existingFile
argument_list|)
expr_stmt|;
name|configuredMs
operator|.
name|forgetMetadata
argument_list|(
name|existingDir
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test tombstones don't get in the way of a listing of the    * root dir.    * This test needs to create a path which appears first in the listing,    * and an entry which can come later. To allow the test to proceed    * while other tests are running, the filename "0000" is used for that    * deleted entry.    */
annotation|@
name|Test
DECL|method|testTombstonesAndEmptyDirectories ()
specifier|public
name|void
name|testTombstonesAndEmptyDirectories
parameter_list|()
throws|throws
name|Throwable
block|{
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|assumeFilesystemHasMetadatastore
argument_list|(
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create the first and last files.
name|Path
name|base
init|=
name|path
argument_list|(
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
comment|// use something ahead of all the ASCII alphabet characters so
comment|// even during parallel test runs, this test is expected to work.
name|String
name|first
init|=
literal|"0000"
decl_stmt|;
name|Path
name|firstPath
init|=
operator|new
name|Path
argument_list|(
name|base
argument_list|,
name|first
argument_list|)
decl_stmt|;
comment|// this path is near the bottom of the ASCII string space.
comment|// This isn't so critical.
name|String
name|last
init|=
literal|"zzzz"
decl_stmt|;
name|Path
name|lastPath
init|=
operator|new
name|Path
argument_list|(
name|base
argument_list|,
name|last
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|fs
argument_list|,
name|firstPath
argument_list|)
expr_stmt|;
name|touch
argument_list|(
name|fs
argument_list|,
name|lastPath
argument_list|)
expr_stmt|;
comment|// Delete first entry (+assert tombstone)
name|assertDeleted
argument_list|(
name|firstPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DynamoDBMetadataStore
name|ddbMs
init|=
name|getRequiredDDBMetastore
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|DDBPathMetadata
name|firstMD
init|=
name|ddbMs
operator|.
name|get
argument_list|(
name|firstPath
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No MD for "
operator|+
name|firstPath
argument_list|,
name|firstMD
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not a tombstone "
operator|+
name|firstMD
argument_list|,
name|firstMD
operator|.
name|isDeleted
argument_list|()
argument_list|)
expr_stmt|;
comment|// PUT child to store going past the FS entirely.
comment|// This is not going to show up on S3Guard.
name|Path
name|child
init|=
operator|new
name|Path
argument_list|(
name|firstPath
argument_list|,
literal|"child"
argument_list|)
decl_stmt|;
name|StoreContext
name|ctx
init|=
name|fs
operator|.
name|createStoreContext
argument_list|()
decl_stmt|;
name|String
name|childKey
init|=
name|ctx
operator|.
name|pathToKey
argument_list|(
name|child
argument_list|)
decl_stmt|;
name|String
name|baseKey
init|=
name|ctx
operator|.
name|pathToKey
argument_list|(
name|base
argument_list|)
operator|+
literal|"/"
decl_stmt|;
name|AmazonS3
name|s3
init|=
name|fs
operator|.
name|getAmazonS3ClientForTesting
argument_list|(
literal|"LIST"
argument_list|)
decl_stmt|;
name|String
name|bucket
init|=
name|ctx
operator|.
name|getBucket
argument_list|()
decl_stmt|;
try|try
block|{
name|createEmptyObject
argument_list|(
name|fs
argument_list|,
name|childKey
argument_list|)
expr_stmt|;
comment|// Do a list
name|ListObjectsV2Request
name|listReq
init|=
operator|new
name|ListObjectsV2Request
argument_list|()
operator|.
name|withBucketName
argument_list|(
name|bucket
argument_list|)
operator|.
name|withPrefix
argument_list|(
name|baseKey
argument_list|)
operator|.
name|withMaxKeys
argument_list|(
literal|10
argument_list|)
operator|.
name|withDelimiter
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|ListObjectsV2Result
name|listing
init|=
name|s3
operator|.
name|listObjectsV2
argument_list|(
name|listReq
argument_list|)
decl_stmt|;
comment|// the listing has the first path as a prefix, because of the child
name|Assertions
operator|.
name|assertThat
argument_list|(
name|listing
operator|.
name|getCommonPrefixes
argument_list|()
argument_list|)
operator|.
name|describedAs
argument_list|(
literal|"The prefixes of a LIST of %s"
argument_list|,
name|base
argument_list|)
operator|.
name|contains
argument_list|(
name|baseKey
operator|+
name|first
operator|+
literal|"/"
argument_list|)
expr_stmt|;
comment|// and the last file is one of the files
name|Stream
argument_list|<
name|String
argument_list|>
name|files
init|=
name|listing
operator|.
name|getObjectSummaries
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|S3ObjectSummary
operator|::
name|getKey
argument_list|)
decl_stmt|;
name|Assertions
operator|.
name|assertThat
argument_list|(
name|files
argument_list|)
operator|.
name|describedAs
argument_list|(
literal|"The files of a LIST of %s"
argument_list|,
name|base
argument_list|)
operator|.
name|contains
argument_list|(
name|baseKey
operator|+
name|last
argument_list|)
expr_stmt|;
comment|// verify absolutely that the last file exists
name|assertPathExists
argument_list|(
literal|"last file"
argument_list|,
name|lastPath
argument_list|)
expr_stmt|;
name|boolean
name|isDDB
init|=
name|fs
operator|.
name|getMetadataStore
argument_list|()
operator|instanceof
name|DynamoDBMetadataStore
decl_stmt|;
comment|// if DDB is the metastore, then we expect no FS requests to be made
comment|// at all.
name|S3ATestUtils
operator|.
name|MetricDiff
name|listMetric
init|=
operator|new
name|S3ATestUtils
operator|.
name|MetricDiff
argument_list|(
name|fs
argument_list|,
name|Statistic
operator|.
name|OBJECT_LIST_REQUESTS
argument_list|)
decl_stmt|;
name|S3ATestUtils
operator|.
name|MetricDiff
name|getMetric
init|=
operator|new
name|S3ATestUtils
operator|.
name|MetricDiff
argument_list|(
name|fs
argument_list|,
name|Statistic
operator|.
name|OBJECT_METADATA_REQUESTS
argument_list|)
decl_stmt|;
comment|// do a getFile status with empty dir flag
name|S3AFileStatus
name|status
init|=
name|getStatusWithEmptyDirFlag
argument_list|(
name|fs
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|assertNonEmptyDir
argument_list|(
name|status
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDDB
condition|)
block|{
name|listMetric
operator|.
name|assertDiffEquals
argument_list|(
literal|"FileSystem called S3 LIST rather than use DynamoDB"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|getMetric
operator|.
name|assertDiffEquals
argument_list|(
literal|"FileSystem called S3 GET rather than use DynamoDB"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Verified that DDB directory status was accepted"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// try to recover from the defective state.
name|s3
operator|.
name|deleteObject
argument_list|(
name|bucket
argument_list|,
name|childKey
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|lastPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ddbMs
operator|.
name|forgetMetadata
argument_list|(
name|firstPath
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertNonEmptyDir (final S3AFileStatus status)
specifier|protected
name|void
name|assertNonEmptyDir
parameter_list|(
specifier|final
name|S3AFileStatus
name|status
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Should not be empty dir: "
operator|+
name|status
argument_list|,
name|Tristate
operator|.
name|FALSE
argument_list|,
name|status
operator|.
name|isEmptyDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the DynamoDB metastore; assume false if it is of a different    * type.    * @return extracted and cast metadata store.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ConstantConditions"
argument_list|)
DECL|method|getRequiredDDBMetastore (S3AFileSystem fs)
specifier|private
name|DynamoDBMetadataStore
name|getRequiredDDBMetastore
parameter_list|(
name|S3AFileSystem
name|fs
parameter_list|)
block|{
name|MetadataStore
name|ms
init|=
name|fs
operator|.
name|getMetadataStore
argument_list|()
decl_stmt|;
name|assume
argument_list|(
literal|"Not a DynamoDBMetadataStore: "
operator|+
name|ms
argument_list|,
name|ms
operator|instanceof
name|DynamoDBMetadataStore
argument_list|)
expr_stmt|;
return|return
operator|(
name|DynamoDBMetadataStore
operator|)
name|ms
return|;
block|}
comment|/**    * From {@code S3AFileSystem.createEmptyObject()}.    * @param fs filesystem    * @param key key    */
DECL|method|createEmptyObject (S3AFileSystem fs, String key)
specifier|private
name|void
name|createEmptyObject
parameter_list|(
name|S3AFileSystem
name|fs
parameter_list|,
name|String
name|key
parameter_list|)
block|{
specifier|final
name|InputStream
name|im
init|=
operator|new
name|InputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
decl_stmt|;
name|PutObjectRequest
name|putObjectRequest
init|=
name|fs
operator|.
name|newPutObjectRequest
argument_list|(
name|key
argument_list|,
name|fs
operator|.
name|newObjectMetadata
argument_list|(
literal|0L
argument_list|)
argument_list|,
name|im
argument_list|)
decl_stmt|;
name|AmazonS3
name|s3
init|=
name|fs
operator|.
name|getAmazonS3ClientForTesting
argument_list|(
literal|"PUT"
argument_list|)
decl_stmt|;
name|s3
operator|.
name|putObject
argument_list|(
name|putObjectRequest
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

