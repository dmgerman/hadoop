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
name|FileSystem
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
name|Test
import|;
end_import

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
name|List
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
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
name|Executors
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
name|Future
import|;
end_import

begin_comment
comment|/***  * Test class to hold all Live Azure storage concurrency tests.  */
end_comment

begin_class
DECL|class|TestNativeAzureFileSystemConcurrencyLive
specifier|public
class|class
name|TestNativeAzureFileSystemConcurrencyLive
extends|extends
name|AbstractWasbTestBase
block|{
DECL|field|THREAD_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|THREAD_COUNT
init|=
literal|102
decl_stmt|;
DECL|field|TEST_EXECUTION_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|TEST_EXECUTION_TIMEOUT
init|=
literal|5000
decl_stmt|;
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
comment|/**    * Validate contract for FileSystem.create when overwrite is true and there    * are concurrent callers of FileSystem.delete.  An existing file should be    * overwritten, even if the original destination exists but is deleted by an    * external agent during the create operation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|TEST_EXECUTION_TIMEOUT
argument_list|)
DECL|method|testConcurrentCreateDeleteFile ()
specifier|public
name|void
name|testConcurrentCreateDeleteFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"test.dat"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CreateFileTask
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|THREAD_COUNT
argument_list|)
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
name|THREAD_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|tasks
operator|.
name|add
argument_list|(
operator|new
name|CreateFileTask
argument_list|(
name|fs
argument_list|,
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ExecutorService
name|es
init|=
literal|null
decl_stmt|;
try|try
block|{
name|es
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|THREAD_COUNT
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|futures
init|=
name|es
operator|.
name|invokeAll
argument_list|(
name|tasks
argument_list|)
decl_stmt|;
for|for
control|(
name|Future
argument_list|<
name|Void
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|future
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
comment|// we are using Callable<V>, so if an exception
comment|// occurred during the operation, it will be thrown
comment|// when we call get
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|future
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|es
operator|!=
literal|null
condition|)
block|{
name|es
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Validate contract for FileSystem.delete when invoked concurrently.    * One of the threads should successfully delete the file and return true;    * all other threads should return false.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|TEST_EXECUTION_TIMEOUT
argument_list|)
DECL|method|testConcurrentDeleteFile ()
specifier|public
name|void
name|testConcurrentDeleteFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"test.dat"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|testFile
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|DeleteFileTask
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|THREAD_COUNT
argument_list|)
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
name|THREAD_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|tasks
operator|.
name|add
argument_list|(
operator|new
name|DeleteFileTask
argument_list|(
name|fs
argument_list|,
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ExecutorService
name|es
init|=
literal|null
decl_stmt|;
try|try
block|{
name|es
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|THREAD_COUNT
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|futures
init|=
name|es
operator|.
name|invokeAll
argument_list|(
name|tasks
argument_list|)
decl_stmt|;
name|int
name|successCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Future
argument_list|<
name|Boolean
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|future
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
comment|// we are using Callable<V>, so if an exception
comment|// occurred during the operation, it will be thrown
comment|// when we call get
name|Boolean
name|success
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|successCount
operator|++
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Exactly one delete operation should return true."
argument_list|,
literal|1
argument_list|,
name|successCount
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|es
operator|!=
literal|null
condition|)
block|{
name|es
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_class
DECL|class|FileSystemTask
specifier|abstract
class|class
name|FileSystemTask
parameter_list|<
name|V
parameter_list|>
implements|implements
name|Callable
argument_list|<
name|V
argument_list|>
block|{
DECL|field|fileSystem
specifier|private
specifier|final
name|FileSystem
name|fileSystem
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|method|getFileSystem ()
specifier|protected
name|FileSystem
name|getFileSystem
parameter_list|()
block|{
return|return
name|this
operator|.
name|fileSystem
return|;
block|}
DECL|method|getFilePath ()
specifier|protected
name|Path
name|getFilePath
parameter_list|()
block|{
return|return
name|this
operator|.
name|path
return|;
block|}
DECL|method|FileSystemTask (FileSystem fs, Path p)
name|FileSystemTask
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
block|{
name|this
operator|.
name|fileSystem
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|p
expr_stmt|;
block|}
DECL|method|call ()
specifier|public
specifier|abstract
name|V
name|call
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_class

begin_class
DECL|class|DeleteFileTask
class|class
name|DeleteFileTask
extends|extends
name|FileSystemTask
argument_list|<
name|Boolean
argument_list|>
block|{
DECL|method|DeleteFileTask (FileSystem fs, Path p)
name|DeleteFileTask
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
block|{
name|super
argument_list|(
name|fs
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|this
operator|.
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|this
operator|.
name|getFilePath
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|CreateFileTask
class|class
name|CreateFileTask
extends|extends
name|FileSystemTask
argument_list|<
name|Void
argument_list|>
block|{
DECL|method|CreateFileTask (FileSystem fs, Path p)
name|CreateFileTask
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
block|{
name|super
argument_list|(
name|fs
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|p
init|=
name|getFilePath
argument_list|()
decl_stmt|;
comment|// Create an empty file and close the stream.
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Delete the file.  We don't care if delete returns true or false.
comment|// We just want to ensure the file does not exist.
name|this
operator|.
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|this
operator|.
name|getFilePath
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

