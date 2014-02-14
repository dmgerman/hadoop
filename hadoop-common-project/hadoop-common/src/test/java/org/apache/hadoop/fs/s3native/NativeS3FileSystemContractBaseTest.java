begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3native
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3native
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
name|io
operator|.
name|InputStream
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
name|FileSystemContractBaseTest
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
name|s3native
operator|.
name|NativeS3FileSystem
operator|.
name|NativeS3FsInputStream
import|;
end_import

begin_class
DECL|class|NativeS3FileSystemContractBaseTest
specifier|public
specifier|abstract
class|class
name|NativeS3FileSystemContractBaseTest
extends|extends
name|FileSystemContractBaseTest
block|{
DECL|field|store
specifier|private
name|NativeFileSystemStore
name|store
decl_stmt|;
DECL|method|getNativeFileSystemStore ()
specifier|abstract
name|NativeFileSystemStore
name|getNativeFileSystemStore
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|store
operator|=
name|getNativeFileSystemStore
argument_list|()
expr_stmt|;
name|fs
operator|=
operator|new
name|NativeS3FileSystem
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|URI
operator|.
name|create
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"test.fs.s3n.name"
argument_list|)
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|purge
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testCanonicalName ()
specifier|public
name|void
name|testCanonicalName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
literal|"s3n doesn't support security token and shouldn't have canonical name"
argument_list|,
name|fs
operator|.
name|getCanonicalServiceName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testListStatusForRoot ()
specifier|public
name|void
name|testListStatusForRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStatus
index|[]
name|paths
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Root directory is not empty; "
argument_list|,
literal|0
argument_list|,
name|paths
operator|.
name|length
argument_list|)
expr_stmt|;
name|Path
name|testDir
init|=
name|path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
name|paths
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|paths
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|path
argument_list|(
literal|"/test"
argument_list|)
argument_list|,
name|paths
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoTrailingBackslashOnBucket ()
specifier|public
name|void
name|testNoTrailingBackslashOnBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createTestFiles (String base)
specifier|private
name|void
name|createTestFiles
parameter_list|(
name|String
name|base
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"/file1"
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"/dir/file2"
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"/dir/file3"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDirWithDifferentMarkersWorks ()
specifier|public
name|void
name|testDirWithDifferentMarkersWorks
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|String
name|base
init|=
literal|"test/hadoop"
operator|+
name|i
decl_stmt|;
name|Path
name|path
init|=
name|path
argument_list|(
literal|"/"
operator|+
name|base
argument_list|)
decl_stmt|;
name|createTestFiles
argument_list|(
name|base
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
comment|//do nothing, we are testing correctness with no markers
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|1
condition|)
block|{
comment|// test for _$folder$ marker
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"_$folder$"
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"/dir_$folder$"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|2
condition|)
block|{
comment|// test the end slash file marker
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"/dir/"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|3
condition|)
block|{
comment|// test both markers
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"_$folder$"
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"/dir_$folder$"
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeEmptyFile
argument_list|(
name|base
operator|+
literal|"/dir/"
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDeleteWithNoMarker ()
specifier|public
name|void
name|testDeleteWithNoMarker
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|base
init|=
literal|"test/hadoop"
decl_stmt|;
name|Path
name|path
init|=
name|path
argument_list|(
literal|"/"
operator|+
name|base
argument_list|)
decl_stmt|;
name|createTestFiles
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|path
operator|=
name|path
argument_list|(
literal|"/test"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testRenameWithNoMarker ()
specifier|public
name|void
name|testRenameWithNoMarker
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|base
init|=
literal|"test/hadoop"
decl_stmt|;
name|Path
name|dest
init|=
name|path
argument_list|(
literal|"/test/hadoop2"
argument_list|)
decl_stmt|;
name|createTestFiles
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|path
argument_list|(
literal|"/"
operator|+
name|base
argument_list|)
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|dest
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fs
operator|.
name|listStatus
argument_list|(
name|dest
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyFile ()
specifier|public
name|void
name|testEmptyFile
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|storeEmptyFile
argument_list|(
literal|"test/hadoop/file1"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|(
literal|"/test/hadoop/file1"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testBlockSize ()
specifier|public
name|void
name|testBlockSize
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
name|path
argument_list|(
literal|"/test/hadoop/file"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Default block size"
argument_list|,
name|fs
operator|.
name|getDefaultBlockSize
argument_list|(
name|file
argument_list|)
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// Block size is determined at read time
name|long
name|newBlockSize
init|=
name|fs
operator|.
name|getDefaultBlockSize
argument_list|(
name|file
argument_list|)
operator|*
literal|2
decl_stmt|;
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|setLong
argument_list|(
literal|"fs.s3n.block.size"
argument_list|,
name|newBlockSize
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Double default block size"
argument_list|,
name|newBlockSize
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRetryOnIoException ()
specifier|public
name|void
name|testRetryOnIoException
parameter_list|()
throws|throws
name|Exception
block|{
class|class
name|TestInputStream
extends|extends
name|InputStream
block|{
name|boolean
name|shouldThrow
init|=
literal|false
decl_stmt|;
name|int
name|throwCount
init|=
literal|0
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|bytes
decl_stmt|;
specifier|public
name|TestInputStream
parameter_list|()
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
literal|256
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|shouldThrow
operator|=
operator|!
name|shouldThrow
expr_stmt|;
if|if
condition|(
name|shouldThrow
condition|)
block|{
name|throwCount
operator|++
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|()
throw|;
block|}
return|return
name|pos
operator|++
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|shouldThrow
operator|=
operator|!
name|shouldThrow
expr_stmt|;
if|if
condition|(
name|shouldThrow
condition|)
block|{
name|throwCount
operator|++
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|()
throw|;
block|}
name|int
name|sizeToRead
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
literal|256
operator|-
name|pos
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
name|sizeToRead
condition|;
name|i
operator|++
control|)
block|{
name|b
index|[
name|i
index|]
operator|=
name|bytes
index|[
name|pos
operator|+
name|i
index|]
expr_stmt|;
block|}
name|pos
operator|+=
name|sizeToRead
expr_stmt|;
return|return
name|sizeToRead
return|;
block|}
block|}
specifier|final
name|InputStream
name|is
init|=
operator|new
name|TestInputStream
argument_list|()
decl_stmt|;
class|class
name|MockNativeFileSystemStore
extends|extends
name|Jets3tNativeFileSystemStore
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|retrieve
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|byteRangeStart
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|is
return|;
block|}
block|}
name|NativeS3FsInputStream
name|stream
init|=
operator|new
name|NativeS3FsInputStream
argument_list|(
operator|new
name|MockNativeFileSystemStore
argument_list|()
argument_list|,
literal|null
argument_list|,
name|is
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|// Test reading methods.
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
literal|256
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
literal|128
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|stream
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|128
init|;
name|i
operator|<
literal|256
condition|;
name|i
operator|+=
literal|8
control|)
block|{
name|byte
index|[]
name|temp
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|int
name|read
init|=
name|stream
operator|.
name|read
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
name|i
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
comment|// Assert correct
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
name|result
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Test to make sure the throw path was exercised.
comment|// 144 = 128 + (128 / 8)
name|assertEquals
argument_list|(
literal|144
argument_list|,
operator|(
operator|(
name|TestInputStream
operator|)
name|is
operator|)
operator|.
name|throwCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

