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
name|util
operator|.
name|ArrayList
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
name|List
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
name|s3a
operator|.
name|s3guard
operator|.
name|DirListingMetadata
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
name|ITtlTimeProvider
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
name|S3Guard
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
name|Assume
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
name|s3a
operator|.
name|Constants
operator|.
name|METADATASTORE_AUTHORITATIVE
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
name|isMetadataStoreAuthoritative
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
name|metadataStorePersistsAuthoritativeBit
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
name|mock
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
name|when
import|;
end_import

begin_comment
comment|/**  * These tests are testing the S3Guard TTL (time to live) features.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|ITestS3GuardTtl
specifier|public
class|class
name|ITestS3GuardTtl
extends|extends
name|AbstractS3ATestBase
block|{
DECL|field|authoritative
specifier|private
specifier|final
name|boolean
name|authoritative
decl_stmt|;
comment|/**    * Test array for parameterized test runs.    * @return a list of parameter tuples.    */
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
block|{
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
block|{
literal|true
block|}
block|,
block|{
literal|false
block|}
block|}
argument_list|)
return|;
block|}
comment|/**    * By changing the method name, the thread name is changed and    * so you can see in the logs which mode is being tested.    * @return a string to use for the thread namer.    */
annotation|@
name|Override
DECL|method|getMethodName ()
specifier|protected
name|String
name|getMethodName
parameter_list|()
block|{
return|return
name|super
operator|.
name|getMethodName
argument_list|()
operator|+
operator|(
name|authoritative
condition|?
literal|"-auth"
else|:
literal|"-nonauth"
operator|)
return|;
block|}
DECL|method|ITestS3GuardTtl (boolean authoritative)
specifier|public
name|ITestS3GuardTtl
parameter_list|(
name|boolean
name|authoritative
parameter_list|)
block|{
name|this
operator|.
name|authoritative
operator|=
name|authoritative
expr_stmt|;
block|}
comment|/**    * Patch the configuration - this test needs disabled filesystem caching.    * These tests modify the fs instance that would cause flaky tests.    * @return a configuration    */
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|configuration
init|=
name|super
operator|.
name|createConfiguration
argument_list|()
decl_stmt|;
name|S3ATestUtils
operator|.
name|disableFilesystemCaching
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|configuration
operator|=
name|S3ATestUtils
operator|.
name|prepareTestConfiguration
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|setBoolean
argument_list|(
name|METADATASTORE_AUTHORITATIVE
argument_list|,
name|authoritative
argument_list|)
expr_stmt|;
return|return
name|configuration
return|;
block|}
annotation|@
name|Test
DECL|method|testDirectoryListingAuthoritativeTtl ()
specifier|public
name|void
name|testDirectoryListingAuthoritativeTtl
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Authoritative mode: {}"
argument_list|,
name|authoritative
argument_list|)
expr_stmt|;
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|fs
operator|.
name|hasMetadataStore
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|MetadataStore
name|ms
init|=
name|fs
operator|.
name|getMetadataStore
argument_list|()
decl_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
literal|"MetadataStore should be capable for authoritative "
operator|+
literal|"storage of directories to run this test."
argument_list|,
name|metadataStorePersistsAuthoritativeBit
argument_list|(
name|ms
argument_list|)
argument_list|)
expr_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
literal|"MetadataStore should be authoritative for this test"
argument_list|,
name|isMetadataStoreAuthoritative
argument_list|(
name|getFileSystem
argument_list|()
operator|.
name|getConf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ITtlTimeProvider
name|mockTimeProvider
init|=
name|mock
argument_list|(
name|ITtlTimeProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|ITtlTimeProvider
name|restoreTimeProvider
init|=
name|fs
operator|.
name|getTtlTimeProvider
argument_list|()
decl_stmt|;
name|fs
operator|.
name|setTtlTimeProvider
argument_list|(
name|mockTimeProvider
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getMetadataTtl
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|Path
name|dir
init|=
name|path
argument_list|(
literal|"ttl/"
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
name|path
argument_list|(
literal|"ttl/afile"
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|touch
argument_list|(
name|fs
argument_list|,
name|file
argument_list|)
expr_stmt|;
comment|// get an authoritative listing in ms
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// check if authoritative
name|DirListingMetadata
name|dirListing
init|=
name|S3Guard
operator|.
name|listChildrenWithTtl
argument_list|(
name|ms
argument_list|,
name|dir
argument_list|,
name|mockTimeProvider
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Listing should be authoritative."
argument_list|,
name|dirListing
operator|.
name|isAuthoritative
argument_list|()
argument_list|)
expr_stmt|;
comment|// change the time, and assume it's not authoritative anymore
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|102L
argument_list|)
expr_stmt|;
name|dirListing
operator|=
name|S3Guard
operator|.
name|listChildrenWithTtl
argument_list|(
name|ms
argument_list|,
name|dir
argument_list|,
name|mockTimeProvider
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Listing should not be authoritative."
argument_list|,
name|dirListing
operator|.
name|isAuthoritative
argument_list|()
argument_list|)
expr_stmt|;
comment|// get an authoritative listing in ms again - retain test
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// check if authoritative
name|dirListing
operator|=
name|S3Guard
operator|.
name|listChildrenWithTtl
argument_list|(
name|ms
argument_list|,
name|dir
argument_list|,
name|mockTimeProvider
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Listing shoud be authoritative after listStatus."
argument_list|,
name|dirListing
operator|.
name|isAuthoritative
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|delete
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setTtlTimeProvider
argument_list|(
name|restoreTimeProvider
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFileMetadataExpiresTtl ()
specifier|public
name|void
name|testFileMetadataExpiresTtl
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Authoritative mode: {}"
argument_list|,
name|authoritative
argument_list|)
expr_stmt|;
name|Path
name|fileExpire1
init|=
name|path
argument_list|(
literal|"expirettl-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|fileExpire2
init|=
name|path
argument_list|(
literal|"expirettl-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|fileRetain
init|=
name|path
argument_list|(
literal|"expirettl-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|fs
operator|.
name|hasMetadataStore
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|MetadataStore
name|ms
init|=
name|fs
operator|.
name|getMetadataStore
argument_list|()
decl_stmt|;
name|ITtlTimeProvider
name|mockTimeProvider
init|=
name|mock
argument_list|(
name|ITtlTimeProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|ITtlTimeProvider
name|originalTimeProvider
init|=
name|fs
operator|.
name|getTtlTimeProvider
argument_list|()
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|setTtlTimeProvider
argument_list|(
name|mockTimeProvider
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getMetadataTtl
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|5L
argument_list|)
expr_stmt|;
comment|// set the time, so the fileExpire1 will expire
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
name|touch
argument_list|(
name|fs
argument_list|,
name|fileExpire1
argument_list|)
expr_stmt|;
comment|// set the time, so fileExpire2 will expire
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|101L
argument_list|)
expr_stmt|;
name|touch
argument_list|(
name|fs
argument_list|,
name|fileExpire2
argument_list|)
expr_stmt|;
comment|// set the time, so fileRetain won't expire
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|109L
argument_list|)
expr_stmt|;
name|touch
argument_list|(
name|fs
argument_list|,
name|fileRetain
argument_list|)
expr_stmt|;
specifier|final
name|FileStatus
name|origFileRetainStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|fileRetain
argument_list|)
decl_stmt|;
comment|// change time, so the first two file metadata is expired
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|110L
argument_list|)
expr_stmt|;
comment|// metadata is expired so this should refresh the metadata with
comment|// last_updated to the getNow()
specifier|final
name|FileStatus
name|fileExpire1Status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|fileExpire1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|fileExpire1Status
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|110L
argument_list|,
name|ms
operator|.
name|get
argument_list|(
name|fileExpire1
argument_list|)
operator|.
name|getLastUpdated
argument_list|()
argument_list|)
expr_stmt|;
comment|// metadata is expired so this should refresh the metadata with
comment|// last_updated to the getNow()
specifier|final
name|FileStatus
name|fileExpire2Status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|fileExpire2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|fileExpire2Status
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|110L
argument_list|,
name|ms
operator|.
name|get
argument_list|(
name|fileExpire2
argument_list|)
operator|.
name|getLastUpdated
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FileStatus
name|fileRetainStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|fileRetain
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Modification time of these files should be equal."
argument_list|,
name|origFileRetainStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|fileRetainStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|fileRetainStatus
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|109L
argument_list|,
name|ms
operator|.
name|get
argument_list|(
name|fileRetain
argument_list|)
operator|.
name|getLastUpdated
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|delete
argument_list|(
name|fileExpire1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|fileExpire2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|fileRetain
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setTtlTimeProvider
argument_list|(
name|originalTimeProvider
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * create(tombstone file) must succeed irrespective of overwrite flag.    */
annotation|@
name|Test
DECL|method|testCreateOnTombstonedFileSucceeds ()
specifier|public
name|void
name|testCreateOnTombstonedFileSucceeds
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Authoritative mode: {}"
argument_list|,
name|authoritative
argument_list|)
expr_stmt|;
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|String
name|fileToTry
init|=
name|methodName
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|filePath
init|=
name|path
argument_list|(
name|fileToTry
argument_list|)
decl_stmt|;
comment|// Create a directory with
name|ITtlTimeProvider
name|mockTimeProvider
init|=
name|mock
argument_list|(
name|ITtlTimeProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|ITtlTimeProvider
name|originalTimeProvider
init|=
name|fs
operator|.
name|getTtlTimeProvider
argument_list|()
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|setTtlTimeProvider
argument_list|(
name|mockTimeProvider
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getMetadataTtl
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|5L
argument_list|)
expr_stmt|;
comment|// CREATE A FILE
name|touch
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
comment|// DELETE THE FILE - TOMBSTONE
name|fs
operator|.
name|delete
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// CREATE THE SAME FILE WITHOUT ERROR DESPITE THE TOMBSTONE
name|touch
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|delete
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setTtlTimeProvider
argument_list|(
name|originalTimeProvider
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * create("parent has tombstone") must always succeed (We dont check the    * parent), but after the file has been written, all entries up the tree    * must be valid. That is: the putAncestor code will correct everything    */
annotation|@
name|Test
DECL|method|testCreateParentHasTombstone ()
specifier|public
name|void
name|testCreateParentHasTombstone
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Authoritative mode: {}"
argument_list|,
name|authoritative
argument_list|)
expr_stmt|;
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|String
name|dirToDelete
init|=
name|methodName
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|fileToTry
init|=
name|dirToDelete
operator|+
literal|"/theFileToTry"
decl_stmt|;
specifier|final
name|Path
name|dirPath
init|=
name|path
argument_list|(
name|dirToDelete
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|filePath
init|=
name|path
argument_list|(
name|fileToTry
argument_list|)
decl_stmt|;
comment|// Create a directory with
name|ITtlTimeProvider
name|mockTimeProvider
init|=
name|mock
argument_list|(
name|ITtlTimeProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|ITtlTimeProvider
name|originalTimeProvider
init|=
name|fs
operator|.
name|getTtlTimeProvider
argument_list|()
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|setTtlTimeProvider
argument_list|(
name|mockTimeProvider
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getMetadataTtl
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|5L
argument_list|)
expr_stmt|;
comment|// CREATE DIRECTORY
name|fs
operator|.
name|mkdirs
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
comment|// DELETE DIRECTORY
name|fs
operator|.
name|delete
argument_list|(
name|dirPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// WRITE TO DELETED DIRECTORY - SUCCESS
name|touch
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
comment|// SET TIME SO METADATA EXPIRES
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|110L
argument_list|)
expr_stmt|;
comment|// WRITE TO DELETED DIRECTORY - SUCCESS
name|touch
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|delete
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dirPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setTtlTimeProvider
argument_list|(
name|originalTimeProvider
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that listing of metadatas is filtered from expired items.    */
annotation|@
name|Test
DECL|method|testListingFilteredExpiredItems ()
specifier|public
name|void
name|testListingFilteredExpiredItems
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Authoritative mode: {}"
argument_list|,
name|authoritative
argument_list|)
expr_stmt|;
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|long
name|oldTime
init|=
literal|100L
decl_stmt|;
name|long
name|newTime
init|=
literal|110L
decl_stmt|;
name|long
name|ttl
init|=
literal|9L
decl_stmt|;
specifier|final
name|String
name|basedir
init|=
literal|"testListingFilteredExpiredItems"
decl_stmt|;
specifier|final
name|Path
name|tombstonedPath
init|=
name|path
argument_list|(
name|basedir
operator|+
literal|"/tombstonedPath"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|baseDirPath
init|=
name|path
argument_list|(
name|basedir
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|filesToCreate
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|MetadataStore
name|ms
init|=
name|fs
operator|.
name|getMetadataStore
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|filesToCreate
operator|.
name|add
argument_list|(
name|path
argument_list|(
name|basedir
operator|+
literal|"/file"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ITtlTimeProvider
name|mockTimeProvider
init|=
name|mock
argument_list|(
name|ITtlTimeProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|ITtlTimeProvider
name|originalTimeProvider
init|=
name|fs
operator|.
name|getTtlTimeProvider
argument_list|()
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|setTtlTimeProvider
argument_list|(
name|mockTimeProvider
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getMetadataTtl
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ttl
argument_list|)
expr_stmt|;
comment|// add and delete entry with the oldtime
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|oldTime
argument_list|)
expr_stmt|;
name|touch
argument_list|(
name|fs
argument_list|,
name|tombstonedPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|tombstonedPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// create items with newTime
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|newTime
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|filesToCreate
control|)
block|{
name|touch
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|// listing will contain the tombstone with oldtime
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|oldTime
argument_list|)
expr_stmt|;
specifier|final
name|DirListingMetadata
name|fullDLM
init|=
name|ms
operator|.
name|listChildren
argument_list|(
name|baseDirPath
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|containedPaths
init|=
name|fullDLM
operator|.
name|getListing
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|pm
lambda|->
name|pm
operator|.
name|getFileStatus
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|Assertions
operator|.
name|assertThat
argument_list|(
name|containedPaths
argument_list|)
operator|.
name|describedAs
argument_list|(
literal|"Full listing of path %s"
argument_list|,
name|baseDirPath
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|11
argument_list|)
operator|.
name|contains
argument_list|(
name|tombstonedPath
argument_list|)
expr_stmt|;
comment|// listing will be filtered, and won't contain the tombstone with oldtime
name|when
argument_list|(
name|mockTimeProvider
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|newTime
argument_list|)
expr_stmt|;
specifier|final
name|DirListingMetadata
name|filteredDLM
init|=
name|ms
operator|.
name|listChildren
argument_list|(
name|baseDirPath
argument_list|)
decl_stmt|;
name|containedPaths
operator|=
name|filteredDLM
operator|.
name|getListing
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|pm
lambda|->
name|pm
operator|.
name|getFileStatus
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
name|Assertions
operator|.
name|assertThat
argument_list|(
name|containedPaths
argument_list|)
operator|.
name|describedAs
argument_list|(
literal|"Full listing of path %s"
argument_list|,
name|baseDirPath
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|10
argument_list|)
operator|.
name|doesNotContain
argument_list|(
name|tombstonedPath
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|delete
argument_list|(
name|baseDirPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setTtlTimeProvider
argument_list|(
name|originalTimeProvider
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

