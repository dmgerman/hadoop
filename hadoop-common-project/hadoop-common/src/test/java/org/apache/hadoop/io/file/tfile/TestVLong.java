begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.file.tfile
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|file
operator|.
name|tfile
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
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_class
DECL|class|TestVLong
specifier|public
class|class
name|TestVLong
extends|extends
name|TestCase
block|{
DECL|field|ROOT
specifier|private
specifier|static
name|String
name|ROOT
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp/tfile-test"
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|path
specifier|private
name|Path
name|path
decl_stmt|;
DECL|field|outputFile
specifier|private
name|String
name|outputFile
init|=
literal|"TestVLong"
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|path
operator|=
operator|new
name|Path
argument_list|(
name|ROOT
argument_list|,
name|outputFile
argument_list|)
expr_stmt|;
name|fs
operator|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testVLongByte ()
specifier|public
name|void
name|testVLongByte
parameter_list|()
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|Byte
operator|.
name|MIN_VALUE
init|;
name|i
operator|<=
name|Byte
operator|.
name|MAX_VALUE
condition|;
operator|++
name|i
control|)
block|{
name|Utils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect encoded size"
argument_list|,
operator|(
literal|1
operator|<<
name|Byte
operator|.
name|SIZE
operator|)
operator|+
literal|96
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|Byte
operator|.
name|MIN_VALUE
init|;
name|i
operator|<=
name|Byte
operator|.
name|MAX_VALUE
condition|;
operator|++
name|i
control|)
block|{
name|long
name|n
init|=
name|Utils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|n
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|writeAndVerify (int shift)
specifier|private
name|long
name|writeAndVerify
parameter_list|(
name|int
name|shift
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|Short
operator|.
name|MIN_VALUE
init|;
name|i
operator|<=
name|Short
operator|.
name|MAX_VALUE
condition|;
operator|++
name|i
control|)
block|{
name|Utils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|long
operator|)
name|i
operator|)
operator|<<
name|shift
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|Short
operator|.
name|MIN_VALUE
init|;
name|i
operator|<=
name|Short
operator|.
name|MAX_VALUE
condition|;
operator|++
name|i
control|)
block|{
name|long
name|n
init|=
name|Utils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|n
argument_list|,
operator|(
operator|(
name|long
operator|)
name|i
operator|)
operator|<<
name|shift
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|ret
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|testVLongShort ()
specifier|public
name|void
name|testVLongShort
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|size
init|=
name|writeAndVerify
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect encoded size"
argument_list|,
operator|(
literal|1
operator|<<
name|Short
operator|.
name|SIZE
operator|)
operator|*
literal|2
operator|+
operator|(
operator|(
literal|1
operator|<<
name|Byte
operator|.
name|SIZE
operator|)
operator|-
literal|40
operator|)
operator|*
operator|(
literal|1
operator|<<
name|Byte
operator|.
name|SIZE
operator|)
operator|-
literal|128
operator|-
literal|32
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|testVLong3Bytes ()
specifier|public
name|void
name|testVLong3Bytes
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|size
init|=
name|writeAndVerify
argument_list|(
name|Byte
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect encoded size"
argument_list|,
operator|(
literal|1
operator|<<
name|Short
operator|.
name|SIZE
operator|)
operator|*
literal|3
operator|+
operator|(
operator|(
literal|1
operator|<<
name|Byte
operator|.
name|SIZE
operator|)
operator|-
literal|32
operator|)
operator|*
operator|(
literal|1
operator|<<
name|Byte
operator|.
name|SIZE
operator|)
operator|-
literal|40
operator|-
literal|1
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|testVLong4Bytes ()
specifier|public
name|void
name|testVLong4Bytes
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|size
init|=
name|writeAndVerify
argument_list|(
name|Byte
operator|.
name|SIZE
operator|*
literal|2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect encoded size"
argument_list|,
operator|(
literal|1
operator|<<
name|Short
operator|.
name|SIZE
operator|)
operator|*
literal|4
operator|+
operator|(
operator|(
literal|1
operator|<<
name|Byte
operator|.
name|SIZE
operator|)
operator|-
literal|16
operator|)
operator|*
operator|(
literal|1
operator|<<
name|Byte
operator|.
name|SIZE
operator|)
operator|-
literal|32
operator|-
literal|2
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|testVLong5Bytes ()
specifier|public
name|void
name|testVLong5Bytes
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|size
init|=
name|writeAndVerify
argument_list|(
name|Byte
operator|.
name|SIZE
operator|*
literal|3
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect encoded size"
argument_list|,
operator|(
literal|1
operator|<<
name|Short
operator|.
name|SIZE
operator|)
operator|*
literal|6
operator|-
literal|256
operator|-
literal|16
operator|-
literal|3
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySixOrMoreBytes (int bytes)
specifier|private
name|void
name|verifySixOrMoreBytes
parameter_list|(
name|int
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|size
init|=
name|writeAndVerify
argument_list|(
name|Byte
operator|.
name|SIZE
operator|*
operator|(
name|bytes
operator|-
literal|2
operator|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect encoded size"
argument_list|,
operator|(
literal|1
operator|<<
name|Short
operator|.
name|SIZE
operator|)
operator|*
operator|(
name|bytes
operator|+
literal|1
operator|)
operator|-
literal|256
operator|-
name|bytes
operator|+
literal|1
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|testVLong6Bytes ()
specifier|public
name|void
name|testVLong6Bytes
parameter_list|()
throws|throws
name|IOException
block|{
name|verifySixOrMoreBytes
argument_list|(
literal|6
argument_list|)
expr_stmt|;
block|}
DECL|method|testVLong7Bytes ()
specifier|public
name|void
name|testVLong7Bytes
parameter_list|()
throws|throws
name|IOException
block|{
name|verifySixOrMoreBytes
argument_list|(
literal|7
argument_list|)
expr_stmt|;
block|}
DECL|method|testVLong8Bytes ()
specifier|public
name|void
name|testVLong8Bytes
parameter_list|()
throws|throws
name|IOException
block|{
name|verifySixOrMoreBytes
argument_list|(
literal|8
argument_list|)
expr_stmt|;
block|}
DECL|method|testVLongRandom ()
specifier|public
name|void
name|testVLongRandom
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
name|long
name|data
index|[]
init|=
operator|new
name|long
index|[
name|count
index|]
decl_stmt|;
name|Random
name|rng
init|=
operator|new
name|Random
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
name|data
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|int
name|shift
init|=
name|rng
operator|.
name|nextInt
argument_list|(
name|Long
operator|.
name|SIZE
argument_list|)
operator|+
literal|1
decl_stmt|;
name|long
name|mask
init|=
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1
decl_stmt|;
name|long
name|a
init|=
operator|(
operator|(
name|long
operator|)
name|rng
operator|.
name|nextInt
argument_list|()
operator|)
operator|<<
literal|32
decl_stmt|;
name|long
name|b
init|=
operator|(
operator|(
name|long
operator|)
name|rng
operator|.
name|nextInt
argument_list|()
operator|)
operator|&
literal|0xffffffffL
decl_stmt|;
name|data
index|[
name|i
index|]
operator|=
operator|(
name|a
operator|+
name|b
operator|)
operator|&
name|mask
expr_stmt|;
block|}
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
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
name|data
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Utils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
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
name|data
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Utils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
argument_list|,
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

