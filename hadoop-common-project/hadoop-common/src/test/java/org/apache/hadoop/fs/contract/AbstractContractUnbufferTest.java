begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
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
name|Path
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
name|IOException
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
name|createFile
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
name|dataset
import|;
end_import

begin_comment
comment|/**  * Contract tests for {@link org.apache.hadoop.fs.CanUnbuffer#unbuffer}.  */
end_comment

begin_class
DECL|class|AbstractContractUnbufferTest
specifier|public
specifier|abstract
class|class
name|AbstractContractUnbufferTest
extends|extends
name|AbstractFSContractTestBase
block|{
DECL|field|file
specifier|private
name|Path
name|file
decl_stmt|;
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
name|skipIfUnsupported
argument_list|(
name|SUPPORTS_UNBUFFER
argument_list|)
expr_stmt|;
name|file
operator|=
name|path
argument_list|(
literal|"unbufferFile"
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|file
argument_list|,
literal|true
argument_list|,
name|dataset
argument_list|(
name|TEST_FILE_LEN
argument_list|,
literal|0
argument_list|,
literal|255
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnbufferAfterRead ()
specifier|public
name|void
name|testUnbufferAfterRead
parameter_list|()
throws|throws
name|IOException
block|{
name|describe
argument_list|(
literal|"unbuffer a file after a single read"
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataInputStream
name|stream
init|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|file
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|128
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUnbufferBeforeRead ()
specifier|public
name|void
name|testUnbufferBeforeRead
parameter_list|()
throws|throws
name|IOException
block|{
name|describe
argument_list|(
literal|"unbuffer a file before a read"
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataInputStream
name|stream
init|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|file
argument_list|)
init|)
block|{
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|128
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUnbufferEmptyFile ()
specifier|public
name|void
name|testUnbufferEmptyFile
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|emptyFile
init|=
name|path
argument_list|(
literal|"emptyUnbufferFile"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|emptyFile
argument_list|,
literal|true
argument_list|,
name|dataset
argument_list|(
name|TEST_FILE_LEN
argument_list|,
literal|0
argument_list|,
literal|255
argument_list|)
argument_list|)
expr_stmt|;
name|describe
argument_list|(
literal|"unbuffer an empty file"
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataInputStream
name|stream
init|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|emptyFile
argument_list|)
init|)
block|{
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUnbufferOnClosedFile ()
specifier|public
name|void
name|testUnbufferOnClosedFile
parameter_list|()
throws|throws
name|IOException
block|{
name|describe
argument_list|(
literal|"unbuffer a file before a read"
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|128
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleUnbuffers ()
specifier|public
name|void
name|testMultipleUnbuffers
parameter_list|()
throws|throws
name|IOException
block|{
name|describe
argument_list|(
literal|"unbuffer a file multiple times"
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataInputStream
name|stream
init|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|file
argument_list|)
init|)
block|{
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|128
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUnbufferMultipleReads ()
specifier|public
name|void
name|testUnbufferMultipleReads
parameter_list|()
throws|throws
name|IOException
block|{
name|describe
argument_list|(
literal|"unbuffer a file multiple times"
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataInputStream
name|stream
init|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|file
argument_list|)
init|)
block|{
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|128
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|128
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|128
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|128
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|128
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|128
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|unbuffer
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|unbuffer (FSDataInputStream stream)
specifier|private
name|void
name|unbuffer
parameter_list|(
name|FSDataInputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|pos
init|=
name|stream
operator|.
name|getPos
argument_list|()
decl_stmt|;
name|stream
operator|.
name|unbuffer
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|pos
argument_list|,
name|stream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

