begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyInt
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSDataInputStream
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
name|FilterFileSystem
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
name|PositionedReadable
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
name|Seekable
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
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Job
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
name|mapreduce
operator|.
name|MRConfig
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|filecache
operator|.
name|DistributedCache
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
name|Before
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
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|TestLocalDistributedCacheManager
specifier|public
class|class
name|TestLocalDistributedCacheManager
block|{
DECL|field|mockfs
specifier|private
specifier|static
name|FileSystem
name|mockfs
decl_stmt|;
DECL|class|MockFileSystem
specifier|public
specifier|static
class|class
name|MockFileSystem
extends|extends
name|FilterFileSystem
block|{
DECL|method|MockFileSystem ()
specifier|public
name|MockFileSystem
parameter_list|()
block|{
name|super
argument_list|(
name|mockfs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|localDir
specifier|private
name|File
name|localDir
decl_stmt|;
DECL|method|delete (File file)
specifier|private
specifier|static
name|void
name|delete
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|length
argument_list|()
operator|<
literal|5
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Path ["
operator|+
name|file
operator|+
literal|"] is too short, not deleting"
argument_list|)
throw|;
block|}
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|children
init|=
name|file
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|child
range|:
name|children
control|)
block|{
name|delete
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not delete path ["
operator|+
name|file
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|mockfs
operator|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|localDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.dir"
argument_list|,
literal|"target/test-dir"
argument_list|)
argument_list|,
name|TestLocalDistributedCacheManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|localDir
argument_list|)
expr_stmt|;
name|localDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|delete
argument_list|(
name|localDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Mock input stream based on a byte array so that it can be used by a    * FSDataInputStream.    */
DECL|class|MockInputStream
specifier|private
specifier|static
class|class
name|MockInputStream
extends|extends
name|ByteArrayInputStream
implements|implements
name|Seekable
implements|,
name|PositionedReadable
block|{
DECL|method|MockInputStream (byte[] buf)
specifier|public
name|MockInputStream
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|)
block|{
name|super
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
comment|// empty implementation for unused methods
DECL|method|read (long position, byte[] buffer, int offset, int length)
specifier|public
name|int
name|read
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|readFully (long position, byte[] buffer, int offset, int length)
specifier|public
name|void
name|readFully
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{}
DECL|method|readFully (long position, byte[] buffer)
specifier|public
name|void
name|readFully
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
block|{}
DECL|method|seek (long position)
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|position
parameter_list|)
block|{}
DECL|method|getPos ()
specifier|public
name|long
name|getPos
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|seekToNewSource (long targetPos)
specifier|public
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDownload ()
specifier|public
name|void
name|testDownload
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
literal|"fs.mock.impl"
argument_list|,
name|MockFileSystem
operator|.
name|class
argument_list|,
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|URI
name|mockBase
init|=
operator|new
name|URI
argument_list|(
literal|"mock://test-nn1/"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getUri
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockBase
argument_list|)
expr_stmt|;
name|Path
name|working
init|=
operator|new
name|Path
argument_list|(
literal|"mock://test-nn1/user/me/"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|working
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|resolvePath
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Path
name|answer
parameter_list|(
name|InvocationOnMock
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
operator|(
name|Path
operator|)
name|args
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|URI
name|file
init|=
operator|new
name|URI
argument_list|(
literal|"mock://test-nn1/user/me/file.txt#link"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|File
name|link
init|=
operator|new
name|File
argument_list|(
literal|"link"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getFileStatus
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileStatus
name|answer
parameter_list|(
name|InvocationOnMock
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|Path
name|p
init|=
operator|(
name|Path
operator|)
name|args
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
literal|"file.txt"
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|FileStatus
argument_list|(
literal|201
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|,
literal|101
argument_list|,
literal|101
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|"me"
argument_list|,
literal|"me"
argument_list|,
name|filePath
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|p
operator|+
literal|" not supported by mocking"
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|FSDataInputStream
name|in
init|=
operator|new
name|FSDataInputStream
argument_list|(
operator|new
name|MockInputStream
argument_list|(
literal|"This is a test file\n"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|open
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|FSDataInputStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FSDataInputStream
name|answer
parameter_list|(
name|InvocationOnMock
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|Path
name|src
init|=
operator|(
name|Path
operator|)
name|args
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
literal|"file.txt"
operator|.
name|equals
argument_list|(
name|src
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|in
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|src
operator|+
literal|" not supported by mocking"
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addCacheFile
argument_list|(
name|file
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|policies
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|policies
operator|.
name|put
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Job
operator|.
name|setFileSharedCacheUploadPolicies
argument_list|(
name|conf
argument_list|,
name|policies
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_TIMESTAMPS
argument_list|,
literal|"101"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES_SIZES
argument_list|,
literal|"201"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
argument_list|,
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|LocalDistributedCacheManager
name|manager
init|=
operator|new
name|LocalDistributedCacheManager
argument_list|()
decl_stmt|;
try|try
block|{
name|manager
operator|.
name|setup
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|link
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|manager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|link
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyDownload ()
specifier|public
name|void
name|testEmptyDownload
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
literal|"fs.mock.impl"
argument_list|,
name|MockFileSystem
operator|.
name|class
argument_list|,
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|URI
name|mockBase
init|=
operator|new
name|URI
argument_list|(
literal|"mock://test-nn1/"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getUri
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockBase
argument_list|)
expr_stmt|;
name|Path
name|working
init|=
operator|new
name|Path
argument_list|(
literal|"mock://test-nn1/user/me/"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|working
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|resolvePath
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Path
name|answer
parameter_list|(
name|InvocationOnMock
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
operator|(
name|Path
operator|)
name|args
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getFileStatus
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileStatus
name|answer
parameter_list|(
name|InvocationOnMock
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|Path
name|p
init|=
operator|(
name|Path
operator|)
name|args
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|p
operator|+
literal|" not supported by mocking"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|open
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|FSDataInputStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FSDataInputStream
name|answer
parameter_list|(
name|InvocationOnMock
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|Path
name|src
init|=
operator|(
name|Path
operator|)
name|args
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|src
operator|+
literal|" not supported by mocking"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
argument_list|,
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|LocalDistributedCacheManager
name|manager
init|=
operator|new
name|LocalDistributedCacheManager
argument_list|()
decl_stmt|;
try|try
block|{
name|manager
operator|.
name|setup
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|manager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDuplicateDownload ()
specifier|public
name|void
name|testDuplicateDownload
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
literal|"fs.mock.impl"
argument_list|,
name|MockFileSystem
operator|.
name|class
argument_list|,
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|URI
name|mockBase
init|=
operator|new
name|URI
argument_list|(
literal|"mock://test-nn1/"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getUri
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockBase
argument_list|)
expr_stmt|;
name|Path
name|working
init|=
operator|new
name|Path
argument_list|(
literal|"mock://test-nn1/user/me/"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|working
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|resolvePath
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Path
name|answer
parameter_list|(
name|InvocationOnMock
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
operator|(
name|Path
operator|)
name|args
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|URI
name|file
init|=
operator|new
name|URI
argument_list|(
literal|"mock://test-nn1/user/me/file.txt#link"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|File
name|link
init|=
operator|new
name|File
argument_list|(
literal|"link"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getFileStatus
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileStatus
name|answer
parameter_list|(
name|InvocationOnMock
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|Path
name|p
init|=
operator|(
name|Path
operator|)
name|args
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
literal|"file.txt"
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|FileStatus
argument_list|(
literal|201
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|,
literal|101
argument_list|,
literal|101
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|"me"
argument_list|,
literal|"me"
argument_list|,
name|filePath
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|p
operator|+
literal|" not supported by mocking"
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|FSDataInputStream
name|in
init|=
operator|new
name|FSDataInputStream
argument_list|(
operator|new
name|MockInputStream
argument_list|(
literal|"This is a test file\n"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockfs
operator|.
name|open
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|FSDataInputStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FSDataInputStream
name|answer
parameter_list|(
name|InvocationOnMock
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|Path
name|src
init|=
operator|(
name|Path
operator|)
name|args
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
literal|"file.txt"
operator|.
name|equals
argument_list|(
name|src
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|in
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|src
operator|+
literal|" not supported by mocking"
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addCacheFile
argument_list|(
name|file
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addCacheFile
argument_list|(
name|file
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|policies
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|policies
operator|.
name|put
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Job
operator|.
name|setFileSharedCacheUploadPolicies
argument_list|(
name|conf
argument_list|,
name|policies
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_TIMESTAMPS
argument_list|,
literal|"101,101"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES_SIZES
argument_list|,
literal|"201,201"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
argument_list|,
literal|"false,false"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
argument_list|,
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|LocalDistributedCacheManager
name|manager
init|=
operator|new
name|LocalDistributedCacheManager
argument_list|()
decl_stmt|;
try|try
block|{
name|manager
operator|.
name|setup
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|link
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|manager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|link
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

