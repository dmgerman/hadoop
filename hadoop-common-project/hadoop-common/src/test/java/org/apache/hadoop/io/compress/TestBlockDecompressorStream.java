begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|compress
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
name|assertTrue
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
name|fail
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
name|ByteArrayOutputStream
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
name|FileInputStream
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
name|nio
operator|.
name|ByteBuffer
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
DECL|class|TestBlockDecompressorStream
specifier|public
class|class
name|TestBlockDecompressorStream
block|{
DECL|field|buf
specifier|private
name|byte
index|[]
name|buf
decl_stmt|;
DECL|field|bytesIn
specifier|private
name|ByteArrayInputStream
name|bytesIn
decl_stmt|;
DECL|field|bytesOut
specifier|private
name|ByteArrayOutputStream
name|bytesOut
decl_stmt|;
annotation|@
name|Test
DECL|method|testRead1 ()
specifier|public
name|void
name|testRead1
parameter_list|()
throws|throws
name|IOException
block|{
name|testRead
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRead2 ()
specifier|public
name|void
name|testRead2
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test eof after getting non-zero block size info
name|testRead
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
DECL|method|testRead (int bufLen)
specifier|private
name|void
name|testRead
parameter_list|(
name|int
name|bufLen
parameter_list|)
throws|throws
name|IOException
block|{
comment|// compress empty stream
name|bytesOut
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|bufLen
operator|>
literal|0
condition|)
block|{
name|bytesOut
operator|.
name|write
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|bufLen
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1024
argument_list|)
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bufLen
argument_list|)
expr_stmt|;
block|}
name|BlockCompressorStream
name|blockCompressorStream
init|=
operator|new
name|BlockCompressorStream
argument_list|(
name|bytesOut
argument_list|,
operator|new
name|FakeCompressor
argument_list|()
argument_list|,
literal|1024
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// close without any write
name|blockCompressorStream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// check compressed output
name|buf
operator|=
name|bytesOut
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"empty file compressed output size is not "
operator|+
operator|(
name|bufLen
operator|+
literal|4
operator|)
argument_list|,
name|bufLen
operator|+
literal|4
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// use compressed output as input for decompression
name|bytesIn
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|buf
argument_list|)
expr_stmt|;
comment|// get decompression stream
try|try
init|(
name|BlockDecompressorStream
name|blockDecompressorStream
init|=
operator|new
name|BlockDecompressorStream
argument_list|(
name|bytesIn
argument_list|,
operator|new
name|FakeDecompressor
argument_list|()
argument_list|,
literal|1024
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|"return value is not -1"
argument_list|,
operator|-
literal|1
argument_list|,
name|blockDecompressorStream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"unexpected IOException : "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadWhenIoExceptionOccure ()
specifier|public
name|void
name|testReadWhenIoExceptionOccure
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"testReadWhenIOException"
argument_list|)
decl_stmt|;
try|try
block|{
name|file
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|InputStream
name|io
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File blocks missing"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
try|try
init|(
name|BlockDecompressorStream
name|blockDecompressorStream
init|=
operator|new
name|BlockDecompressorStream
argument_list|(
name|io
argument_list|,
operator|new
name|FakeDecompressor
argument_list|()
argument_list|,
literal|1024
argument_list|)
init|)
block|{
name|int
name|byteRead
init|=
name|blockDecompressorStream
operator|.
name|read
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"Should not return -1 in case of IOException. Byte read "
operator|+
name|byteRead
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"File blocks missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

