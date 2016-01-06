begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|CountDownLatch
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
name|io
operator|.
name|IOUtils
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
name|FSDataOutputStream
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
name|permission
operator|.
name|FsPermission
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
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|StorageException
import|;
end_import

begin_comment
comment|/*  * Tests the Native Azure file system (WASB) against an actual blob store if  * provided in the environment.  */
end_comment

begin_class
DECL|class|TestNativeAzureFileSystemLive
specifier|public
class|class
name|TestNativeAzureFileSystemLive
extends|extends
name|NativeAzureFileSystemBaseTest
block|{
annotation|@
name|Override
DECL|method|createTestAccount ()
specifier|protected
name|AzureBlobStorageTestAccount
name|createTestAccount
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|AzureBlobStorageTestAccount
operator|.
name|create
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testLazyRenamePendingCanOverwriteExistingFile ()
specifier|public
name|void
name|testLazyRenamePendingCanOverwriteExistingFile
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|SRC_FILE_KEY
init|=
literal|"srcFile"
decl_stmt|;
specifier|final
name|String
name|DST_FILE_KEY
init|=
literal|"dstFile"
decl_stmt|;
name|Path
name|srcPath
init|=
operator|new
name|Path
argument_list|(
name|SRC_FILE_KEY
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|srcStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|srcPath
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|dstPath
init|=
operator|new
name|Path
argument_list|(
name|DST_FILE_KEY
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|dstStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|dstPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|dstPath
argument_list|)
argument_list|)
expr_stmt|;
name|NativeAzureFileSystem
name|nfs
init|=
operator|(
name|NativeAzureFileSystem
operator|)
name|fs
decl_stmt|;
specifier|final
name|String
name|fullSrcKey
init|=
name|nfs
operator|.
name|pathToKey
argument_list|(
name|nfs
operator|.
name|makeAbsolute
argument_list|(
name|srcPath
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fullDstKey
init|=
name|nfs
operator|.
name|pathToKey
argument_list|(
name|nfs
operator|.
name|makeAbsolute
argument_list|(
name|dstPath
argument_list|)
argument_list|)
decl_stmt|;
name|nfs
operator|.
name|getStoreInterface
argument_list|()
operator|.
name|rename
argument_list|(
name|fullSrcKey
argument_list|,
name|fullDstKey
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|dstPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|srcPath
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|srcStream
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|dstStream
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests fs.delete() function to delete a blob when another blob is holding a    * lease on it. Delete if called without a lease should fail if another process    * is holding a lease and throw appropriate exception    * This is a scenario that would happen in HMaster startup when it tries to    * clean up the temp dirs while the HMaster process which was killed earlier    * held lease on the blob when doing some DDL operation    */
annotation|@
name|Test
DECL|method|testDeleteThrowsExceptionWithLeaseExistsErrorMessage ()
specifier|public
name|void
name|testDeleteThrowsExceptionWithLeaseExistsErrorMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting test"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|FILE_KEY
init|=
literal|"fileWithLease"
decl_stmt|;
comment|// Create the file
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|FILE_KEY
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|NativeAzureFileSystem
name|nfs
init|=
operator|(
name|NativeAzureFileSystem
operator|)
name|fs
decl_stmt|;
specifier|final
name|String
name|fullKey
init|=
name|nfs
operator|.
name|pathToKey
argument_list|(
name|nfs
operator|.
name|makeAbsolute
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AzureNativeFileSystemStore
name|store
init|=
name|nfs
operator|.
name|getStore
argument_list|()
decl_stmt|;
comment|// Acquire the lease on the file in a background thread
specifier|final
name|CountDownLatch
name|leaseAttemptComplete
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|beginningDeleteAttempt
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// Acquire the lease and then signal the main test thread.
name|SelfRenewingLease
name|lease
init|=
literal|null
decl_stmt|;
try|try
block|{
name|lease
operator|=
name|store
operator|.
name|acquireLease
argument_list|(
name|fullKey
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Lease acquired: "
operator|+
name|lease
operator|.
name|getLeaseID
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AzureException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Lease acqusition thread unable to acquire lease"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|leaseAttemptComplete
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
comment|// Wait for the main test thread to signal it will attempt the delete.
try|try
block|{
name|beginningDeleteAttempt
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
comment|// Keep holding the lease past the lease acquisition retry interval, so
comment|// the test covers the case of delete retrying to acquire the lease.
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|SelfRenewingLease
operator|.
name|LEASE_ACQUIRE_RETRY_INTERVAL
operator|*
literal|3
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|lease
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Freeing lease"
argument_list|)
expr_stmt|;
name|lease
operator|.
name|free
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|StorageException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to free lease."
argument_list|,
name|se
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
comment|// Start the background thread and wait for it to signal the lease is held.
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|leaseAttemptComplete
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
comment|// Try to delete the same file
name|beginningDeleteAttempt
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|store
operator|.
name|delete
argument_list|(
name|fullKey
argument_list|)
expr_stmt|;
comment|// At this point file SHOULD BE DELETED
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that isPageBlobKey works as expected. This assumes that    * in the test configuration, the list of supported page blob directories    * only includes "pageBlobs". That's why this test is made specific    * to this subclass.    */
annotation|@
name|Test
DECL|method|testIsPageBlobKey ()
specifier|public
name|void
name|testIsPageBlobKey
parameter_list|()
block|{
name|AzureNativeFileSystemStore
name|store
init|=
operator|(
operator|(
name|NativeAzureFileSystem
operator|)
name|fs
operator|)
operator|.
name|getStore
argument_list|()
decl_stmt|;
comment|// Use literal strings so it's easier to understand the tests.
comment|// In case the constant changes, we want to know about it so we can update this test.
name|assertEquals
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|DEFAULT_PAGE_BLOB_DIRECTORY
argument_list|,
literal|"pageBlobs"
argument_list|)
expr_stmt|;
comment|// URI prefix for test environment.
name|String
name|uriPrefix
init|=
literal|"file:///"
decl_stmt|;
comment|// negative tests
name|String
index|[]
name|negativeKeys
init|=
block|{
literal|""
block|,
literal|"/"
block|,
literal|"bar"
block|,
literal|"bar/"
block|,
literal|"bar/pageBlobs"
block|,
literal|"bar/pageBlobs/foo"
block|,
literal|"bar/pageBlobs/foo/"
block|,
literal|"/pageBlobs/"
block|,
literal|"/pageBlobs"
block|,
literal|"pageBlobs"
block|,
literal|"pageBlobsxyz/"
block|}
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|negativeKeys
control|)
block|{
name|assertFalse
argument_list|(
name|store
operator|.
name|isPageBlobKey
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|isPageBlobKey
argument_list|(
name|uriPrefix
operator|+
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// positive tests
name|String
index|[]
name|positiveKeys
init|=
block|{
literal|"pageBlobs/"
block|,
literal|"pageBlobs/foo/"
block|,
literal|"pageBlobs/foo/bar/"
block|}
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|positiveKeys
control|)
block|{
name|assertTrue
argument_list|(
name|store
operator|.
name|isPageBlobKey
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|isPageBlobKey
argument_list|(
name|uriPrefix
operator|+
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that isAtomicRenameKey() works as expected.    */
annotation|@
name|Test
DECL|method|testIsAtomicRenameKey ()
specifier|public
name|void
name|testIsAtomicRenameKey
parameter_list|()
block|{
name|AzureNativeFileSystemStore
name|store
init|=
operator|(
operator|(
name|NativeAzureFileSystem
operator|)
name|fs
operator|)
operator|.
name|getStore
argument_list|()
decl_stmt|;
comment|// We want to know if the default configuration changes so we can fix
comment|// this test.
name|assertEquals
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|DEFAULT_ATOMIC_RENAME_DIRECTORIES
argument_list|,
literal|"/atomicRenameDir1,/atomicRenameDir2"
argument_list|)
expr_stmt|;
comment|// URI prefix for test environment.
name|String
name|uriPrefix
init|=
literal|"file:///"
decl_stmt|;
comment|// negative tests
name|String
index|[]
name|negativeKeys
init|=
block|{
literal|""
block|,
literal|"/"
block|,
literal|"bar"
block|,
literal|"bar/"
block|,
literal|"bar/hbase"
block|,
literal|"bar/hbase/foo"
block|,
literal|"bar/hbase/foo/"
block|,
literal|"/hbase/"
block|,
literal|"/hbase"
block|,
literal|"hbase"
block|,
literal|"hbasexyz/"
block|,
literal|"foo/atomicRenameDir1/"
block|}
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|negativeKeys
control|)
block|{
name|assertFalse
argument_list|(
name|store
operator|.
name|isAtomicRenameKey
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|isAtomicRenameKey
argument_list|(
name|uriPrefix
operator|+
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Positive tests. The directories for atomic rename are /hbase
comment|// plus the ones in the configuration (DEFAULT_ATOMIC_RENAME_DIRECTORIES
comment|// for this test).
name|String
index|[]
name|positiveKeys
init|=
block|{
literal|"hbase/"
block|,
literal|"hbase/foo/"
block|,
literal|"hbase/foo/bar/"
block|,
literal|"atomicRenameDir1/foo/"
block|,
literal|"atomicRenameDir2/bar/"
block|}
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|positiveKeys
control|)
block|{
name|assertTrue
argument_list|(
name|store
operator|.
name|isAtomicRenameKey
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|isAtomicRenameKey
argument_list|(
name|uriPrefix
operator|+
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests fs.mkdir() function to create a target blob while another thread    * is holding the lease on the blob. mkdir should not fail since the blob    * already exists.    * This is a scenario that would happen in HBase distributed log splitting.    * Multiple threads will try to create and update "recovered.edits" folder    * under the same path.    */
annotation|@
name|Test
DECL|method|testMkdirOnExistingFolderWithLease ()
specifier|public
name|void
name|testMkdirOnExistingFolderWithLease
parameter_list|()
throws|throws
name|Exception
block|{
name|SelfRenewingLease
name|lease
decl_stmt|;
specifier|final
name|String
name|FILE_KEY
init|=
literal|"folderWithLease"
decl_stmt|;
comment|// Create the folder
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|FILE_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|NativeAzureFileSystem
name|nfs
init|=
operator|(
name|NativeAzureFileSystem
operator|)
name|fs
decl_stmt|;
name|String
name|fullKey
init|=
name|nfs
operator|.
name|pathToKey
argument_list|(
name|nfs
operator|.
name|makeAbsolute
argument_list|(
operator|new
name|Path
argument_list|(
name|FILE_KEY
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|AzureNativeFileSystemStore
name|store
init|=
name|nfs
operator|.
name|getStore
argument_list|()
decl_stmt|;
comment|// Acquire the lease on the folder
name|lease
operator|=
name|store
operator|.
name|acquireLease
argument_list|(
name|fullKey
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|lease
operator|.
name|getLeaseID
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// Try to create the same folder
name|store
operator|.
name|storeEmptyFolder
argument_list|(
name|fullKey
argument_list|,
name|nfs
operator|.
name|createPermissionStatus
argument_list|(
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lease
operator|.
name|free
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

