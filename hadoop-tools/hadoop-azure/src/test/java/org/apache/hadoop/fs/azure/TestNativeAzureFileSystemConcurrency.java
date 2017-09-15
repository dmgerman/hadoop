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
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLDecoder
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
name|Iterator
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
name|ConcurrentLinkedQueue
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
name|util
operator|.
name|StringUtils
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

begin_class
DECL|class|TestNativeAzureFileSystemConcurrency
specifier|public
class|class
name|TestNativeAzureFileSystemConcurrency
extends|extends
name|AbstractWasbTestBase
block|{
DECL|field|backingStore
specifier|private
name|InMemoryBlockBlobStore
name|backingStore
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|backingStore
operator|=
name|getTestAccount
argument_list|()
operator|.
name|getMockStorage
argument_list|()
operator|.
name|getBackingStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|backingStore
operator|=
literal|null
expr_stmt|;
block|}
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
name|createMock
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testLinkBlobs ()
specifier|public
name|void
name|testLinkBlobs
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/inProgress"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|outputStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
comment|// Since the stream is still open, we should see an empty link
comment|// blob in the backing store linking to the temporary file.
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
name|backingStore
operator|.
name|getMetadata
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|toMockUri
argument_list|(
name|filePath
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|String
name|linkValue
init|=
name|metadata
operator|.
name|get
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|LINK_BACK_TO_UPLOAD_IN_PROGRESS_METADATA_KEY
argument_list|)
decl_stmt|;
name|linkValue
operator|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|linkValue
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|linkValue
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|backingStore
operator|.
name|exists
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|toMockUri
argument_list|(
name|linkValue
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Also, WASB should say the file exists now even before we close the
comment|// stream.
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|filePath
argument_list|)
argument_list|)
expr_stmt|;
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now there should be no link metadata on the final file.
name|metadata
operator|=
name|backingStore
operator|.
name|getMetadata
argument_list|(
name|AzureBlobStorageTestAccount
operator|.
name|toMockUri
argument_list|(
name|filePath
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|AzureNativeFileSystemStore
operator|.
name|LINK_BACK_TO_UPLOAD_IN_PROGRESS_METADATA_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|toString (FileStatus[] list)
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|FileStatus
index|[]
name|list
parameter_list|)
block|{
name|String
index|[]
name|asStrings
init|=
operator|new
name|String
index|[
name|list
operator|.
name|length
index|]
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
name|list
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|asStrings
index|[
name|i
index|]
operator|=
name|list
index|[
name|i
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|asStrings
argument_list|)
return|;
block|}
comment|/**    * Test to make sure that we don't expose the temporary upload folder when    * listing at the root.    */
annotation|@
name|Test
DECL|method|testNoTempBlobsVisible ()
specifier|public
name|void
name|testNoTempBlobsVisible
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/inProgress"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|outputStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
comment|// Make sure I can't see the temporary blob if I ask for a listing
name|FileStatus
index|[]
name|listOfRoot
init|=
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected one file listed, instead got: "
operator|+
name|toString
argument_list|(
name|listOfRoot
argument_list|)
argument_list|,
literal|1
argument_list|,
name|listOfRoot
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|filePath
argument_list|)
argument_list|,
name|listOfRoot
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Converts a collection of exceptions to a collection of strings by getting    * the stack trace on every exception.    */
DECL|method|selectToString ( final Iterable<Throwable> collection)
specifier|private
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|selectToString
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|Throwable
argument_list|>
name|collection
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|Throwable
argument_list|>
name|exceptionIterator
init|=
name|collection
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|exceptionIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|stringWriter
argument_list|)
decl_stmt|;
name|exceptionIterator
operator|.
name|next
argument_list|()
operator|.
name|printStackTrace
argument_list|(
name|printWriter
argument_list|)
expr_stmt|;
name|printWriter
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|stringWriter
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|exceptionIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/**    * Tests running starting multiple threads all doing various File system    * operations against the same FS.    */
annotation|@
name|Test
DECL|method|testMultiThreadedOperation ()
specifier|public
name|void
name|testMultiThreadedOperation
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|numThreads
init|=
literal|20
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|numThreads
index|]
decl_stmt|;
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|Throwable
argument_list|>
name|exceptionsEncountered
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|Throwable
argument_list|>
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Path
name|threadLocalFile
init|=
operator|new
name|Path
argument_list|(
literal|"/myFile"
operator|+
name|i
argument_list|)
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|assertTrue
argument_list|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|threadLocalFile
argument_list|)
argument_list|)
expr_stmt|;
name|OutputStream
name|output
init|=
name|fs
operator|.
name|create
argument_list|(
name|threadLocalFile
argument_list|)
decl_stmt|;
name|output
operator|.
name|write
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|threadLocalFile
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|exceptionsEncountered
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Encountered exceptions: "
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
literal|"\r\n"
argument_list|,
name|selectToString
argument_list|(
name|exceptionsEncountered
argument_list|)
argument_list|)
argument_list|,
name|exceptionsEncountered
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
name|setUp
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

