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
name|FileStatus
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
name|contract
operator|.
name|AbstractFSContract
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
name|contract
operator|.
name|ContractTestUtils
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
name|contract
operator|.
name|s3a
operator|.
name|S3AContract
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
name|Test
import|;
end_import

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
name|InputStream
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
name|fs
operator|.
name|contract
operator|.
name|ContractTestUtils
operator|.
name|writeTextFile
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
name|FailureInjectionPolicy
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|eventually
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
comment|/**  * Tests S3A behavior under forced inconsistency via {@link  * InconsistentAmazonS3Client}.  *  * These tests are for validating expected behavior *without* S3Guard, but  * may also run with S3Guard enabled.  For tests that validate S3Guard's  * consistency features, see {@link ITestS3GuardListConsistency}.  */
end_comment

begin_class
DECL|class|ITestS3AInconsistency
specifier|public
class|class
name|ITestS3AInconsistency
extends|extends
name|AbstractS3ATestBase
block|{
DECL|field|OPEN_READ_ITERATIONS
specifier|private
specifier|static
specifier|final
name|int
name|OPEN_READ_ITERATIONS
init|=
literal|20
decl_stmt|;
annotation|@
name|Override
DECL|method|createContract (Configuration conf)
specifier|protected
name|AbstractFSContract
name|createContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setClass
argument_list|(
name|S3_CLIENT_FACTORY_IMPL
argument_list|,
name|InconsistentS3ClientFactory
operator|.
name|class
argument_list|,
name|S3ClientFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FAIL_INJECT_INCONSISTENCY_KEY
argument_list|,
name|DEFAULT_DELAY_KEY_SUBSTRING
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|FAIL_INJECT_INCONSISTENCY_PROBABILITY
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|FAIL_INJECT_INCONSISTENCY_MSEC
argument_list|,
name|DEFAULT_DELAY_KEY_MSEC
argument_list|)
expr_stmt|;
return|return
operator|new
name|S3AContract
argument_list|(
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testGetFileStatus ()
specifier|public
name|void
name|testGetFileStatus
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
comment|// 1. Make sure no ancestor dirs exist
name|Path
name|dir
init|=
name|path
argument_list|(
literal|"ancestor"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|waitUntilDeleted
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// 2. Create a descendant file, which implicitly creates ancestors
comment|// This file has delayed visibility.
name|touch
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|(
literal|"ancestor/file-"
operator|+
name|DEFAULT_DELAY_KEY_SUBSTRING
argument_list|)
argument_list|)
expr_stmt|;
comment|// 3. Assert expected behavior.  If S3Guard is enabled, we should be able
comment|// to get status for ancestor.  If S3Guard is *not* enabled, S3A will
comment|// fail to infer the existence of the ancestor since visibility of the
comment|// child file is delayed, and its key prefix search will return nothing.
try|try
block|{
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|hasMetadataStore
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Ancestor is dir"
argument_list|,
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"getFileStatus should fail due to delayed visibility."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|fs
operator|.
name|hasMetadataStore
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"S3Guard failed to list parent of inconsistent child."
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"File not found, as expected."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Ensure that deleting a file with an open read stream does eventually cause    * readers to get a FNFE, even with S3Guard and its retries enabled.    * In real usage, S3Guard should be enabled for all clients that modify the    * file, so the delete would be immediately recorded in the MetadataStore.    * Here, however, we test deletion from under S3Guard to make sure it still    * eventually propagates the FNFE after any retry policies are exhausted.    */
annotation|@
name|Test
DECL|method|testOpenDeleteRead ()
specifier|public
name|void
name|testOpenDeleteRead
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
name|Path
name|p
init|=
name|path
argument_list|(
literal|"testOpenDeleteRead.txt"
argument_list|)
decl_stmt|;
name|writeTextFile
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
literal|"1337c0d3z"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|s
init|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|)
init|)
block|{
comment|// Disable s3guard, delete file underneath it, re-enable s3guard
name|MetadataStore
name|metadataStore
init|=
name|fs
operator|.
name|getMetadataStore
argument_list|()
decl_stmt|;
name|fs
operator|.
name|setMetadataStore
argument_list|(
operator|new
name|NullMetadataStore
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|p
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setMetadataStore
argument_list|(
name|metadataStore
argument_list|)
expr_stmt|;
name|eventually
argument_list|(
literal|1000
argument_list|,
literal|200
argument_list|,
parameter_list|()
lambda|->
block|{
name|intercept
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|s
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test read() path behavior when getFileStatus() succeeds but subsequent    * read() on the input stream fails due to eventual consistency.    * There are many points in the InputStream codepaths that can fail. We set    * a probability of failure and repeat the test multiple times to achieve    * decent coverage.    */
annotation|@
name|Test
DECL|method|testOpenFailOnRead ()
specifier|public
name|void
name|testOpenFailOnRead
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
comment|// 1. Patch in a different failure injection policy with<1.0 probability
name|Configuration
name|conf
init|=
name|fs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|FAIL_INJECT_INCONSISTENCY_PROBABILITY
argument_list|,
literal|0.5f
argument_list|)
expr_stmt|;
name|InconsistentAmazonS3Client
operator|.
name|setFailureInjectionPolicy
argument_list|(
name|fs
argument_list|,
operator|new
name|FailureInjectionPolicy
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2. Make sure no ancestor dirs exist
name|Path
name|dir
init|=
name|path
argument_list|(
literal|"ancestor"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|waitUntilDeleted
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// 3. Create a descendant file, which implicitly creates ancestors
comment|// This file has delayed visibility.
name|describe
argument_list|(
literal|"creating test file"
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|path
argument_list|(
literal|"ancestor/file-to-read-"
operator|+
name|DEFAULT_DELAY_KEY_SUBSTRING
argument_list|)
decl_stmt|;
name|writeTextFile
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|,
literal|"Reading is fun"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 4. Clear inconsistency so the first getFileStatus() can succeed, if we
comment|// are not using S3Guard. If we are using S3Guard, it should tolerate the
comment|// delayed visibility.
if|if
condition|(
operator|!
name|fs
operator|.
name|hasMetadataStore
argument_list|()
condition|)
block|{
name|InconsistentAmazonS3Client
operator|.
name|clearInconsistency
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
comment|// ? Do we need multiple iterations when S3Guard is disabled?  For now,
comment|// leaving it in
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|OPEN_READ_ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
name|doOpenFailOnReadTest
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doOpenFailOnReadTest (S3AFileSystem fs, Path path, int iteration)
specifier|private
name|void
name|doOpenFailOnReadTest
parameter_list|(
name|S3AFileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|iteration
parameter_list|)
throws|throws
name|Exception
block|{
comment|// 4. Open the file
name|describe
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"i=%d: opening test file"
argument_list|,
name|iteration
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
init|)
block|{
comment|// 5. Assert expected behavior on read() failure.
name|int
name|l
init|=
literal|4
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|l
index|]
decl_stmt|;
name|describe
argument_list|(
literal|"reading test file"
argument_list|)
expr_stmt|;
comment|// Use both read() variants
if|if
condition|(
operator|(
name|iteration
operator|%
literal|2
operator|)
operator|==
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|l
argument_list|,
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|fs
operator|.
name|hasMetadataStore
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|fail
argument_list|(
literal|"S3Guard failed to handle fail-on-read"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"File not found on read(), as expected."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|waitUntilDeleted (final Path p)
specifier|private
name|void
name|waitUntilDeleted
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|)
throws|throws
name|Exception
block|{
name|LambdaTestUtils
operator|.
name|eventually
argument_list|(
literal|30
operator|*
literal|1000
argument_list|,
literal|1000
argument_list|,
parameter_list|()
lambda|->
name|assertPathDoesNotExist
argument_list|(
literal|"Dir should be deleted"
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

