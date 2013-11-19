begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress.snappy
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
operator|.
name|snappy
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
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|lang
operator|.
name|reflect
operator|.
name|Array
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|DataInputBuffer
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
name|DataOutputBuffer
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
name|compress
operator|.
name|BlockCompressorStream
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
name|compress
operator|.
name|BlockDecompressorStream
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
name|compress
operator|.
name|CompressionInputStream
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
name|compress
operator|.
name|CompressionOutputStream
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
name|compress
operator|.
name|SnappyCodec
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
name|compress
operator|.
name|snappy
operator|.
name|SnappyDecompressor
operator|.
name|SnappyDirectDecompressor
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestSnappyCompressorDecompressor
specifier|public
class|class
name|TestSnappyCompressorDecompressor
block|{
annotation|@
name|Before
DECL|method|before ()
specifier|public
name|void
name|before
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|SnappyCodec
operator|.
name|isNativeCodeLoaded
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSnappyCompressorSetInputNullPointerException ()
specifier|public
name|void
name|testSnappyCompressorSetInputNullPointerException
parameter_list|()
block|{
try|try
block|{
name|SnappyCompressor
name|compressor
init|=
operator|new
name|SnappyCompressor
argument_list|()
decl_stmt|;
name|compressor
operator|.
name|setInput
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testSnappyCompressorSetInputNullPointerException error !!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// excepted
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyCompressorSetInputNullPointerException ex error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSnappyDecompressorSetInputNullPointerException ()
specifier|public
name|void
name|testSnappyDecompressorSetInputNullPointerException
parameter_list|()
block|{
try|try
block|{
name|SnappyDecompressor
name|decompressor
init|=
operator|new
name|SnappyDecompressor
argument_list|()
decl_stmt|;
name|decompressor
operator|.
name|setInput
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testSnappyDecompressorSetInputNullPointerException error !!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyDecompressorSetInputNullPointerException ex error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSnappyCompressorSetInputAIOBException ()
specifier|public
name|void
name|testSnappyCompressorSetInputAIOBException
parameter_list|()
block|{
try|try
block|{
name|SnappyCompressor
name|compressor
init|=
operator|new
name|SnappyCompressor
argument_list|()
decl_stmt|;
name|compressor
operator|.
name|setInput
argument_list|(
operator|new
name|byte
index|[]
block|{}
argument_list|,
operator|-
literal|5
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testSnappyCompressorSetInputAIOBException error !!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyCompressorSetInputAIOBException ex error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSnappyDecompressorSetInputAIOUBException ()
specifier|public
name|void
name|testSnappyDecompressorSetInputAIOUBException
parameter_list|()
block|{
try|try
block|{
name|SnappyDecompressor
name|decompressor
init|=
operator|new
name|SnappyDecompressor
argument_list|()
decl_stmt|;
name|decompressor
operator|.
name|setInput
argument_list|(
operator|new
name|byte
index|[]
block|{}
argument_list|,
operator|-
literal|5
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testSnappyDecompressorSetInputAIOUBException error !!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyDecompressorSetInputAIOUBException ex error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSnappyCompressorCompressNullPointerException ()
specifier|public
name|void
name|testSnappyCompressorCompressNullPointerException
parameter_list|()
block|{
try|try
block|{
name|SnappyCompressor
name|compressor
init|=
operator|new
name|SnappyCompressor
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|BytesGenerator
operator|.
name|get
argument_list|(
literal|1024
operator|*
literal|6
argument_list|)
decl_stmt|;
name|compressor
operator|.
name|setInput
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|compressor
operator|.
name|compress
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testSnappyCompressorCompressNullPointerException error !!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyCompressorCompressNullPointerException ex error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSnappyDecompressorCompressNullPointerException ()
specifier|public
name|void
name|testSnappyDecompressorCompressNullPointerException
parameter_list|()
block|{
try|try
block|{
name|SnappyDecompressor
name|decompressor
init|=
operator|new
name|SnappyDecompressor
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|BytesGenerator
operator|.
name|get
argument_list|(
literal|1024
operator|*
literal|6
argument_list|)
decl_stmt|;
name|decompressor
operator|.
name|setInput
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|decompressor
operator|.
name|decompress
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testSnappyDecompressorCompressNullPointerException error !!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyDecompressorCompressNullPointerException ex error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSnappyCompressorCompressAIOBException ()
specifier|public
name|void
name|testSnappyCompressorCompressAIOBException
parameter_list|()
block|{
try|try
block|{
name|SnappyCompressor
name|compressor
init|=
operator|new
name|SnappyCompressor
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|BytesGenerator
operator|.
name|get
argument_list|(
literal|1024
operator|*
literal|6
argument_list|)
decl_stmt|;
name|compressor
operator|.
name|setInput
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|compressor
operator|.
name|compress
argument_list|(
operator|new
name|byte
index|[]
block|{}
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testSnappyCompressorCompressAIOBException error !!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyCompressorCompressAIOBException ex error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSnappyDecompressorCompressAIOBException ()
specifier|public
name|void
name|testSnappyDecompressorCompressAIOBException
parameter_list|()
block|{
try|try
block|{
name|SnappyDecompressor
name|decompressor
init|=
operator|new
name|SnappyDecompressor
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|BytesGenerator
operator|.
name|get
argument_list|(
literal|1024
operator|*
literal|6
argument_list|)
decl_stmt|;
name|decompressor
operator|.
name|setInput
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|decompressor
operator|.
name|decompress
argument_list|(
operator|new
name|byte
index|[]
block|{}
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testSnappyDecompressorCompressAIOBException error !!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyDecompressorCompressAIOBException ex error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSnappyCompressDecompress ()
specifier|public
name|void
name|testSnappyCompressDecompress
parameter_list|()
block|{
name|int
name|BYTE_SIZE
init|=
literal|1024
operator|*
literal|54
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|BytesGenerator
operator|.
name|get
argument_list|(
name|BYTE_SIZE
argument_list|)
decl_stmt|;
name|SnappyCompressor
name|compressor
init|=
operator|new
name|SnappyCompressor
argument_list|()
decl_stmt|;
try|try
block|{
name|compressor
operator|.
name|setInput
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"SnappyCompressDecompress getBytesRead error !!!"
argument_list|,
name|compressor
operator|.
name|getBytesRead
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"SnappyCompressDecompress getBytesWritten before compress error !!!"
argument_list|,
name|compressor
operator|.
name|getBytesWritten
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|byte
index|[]
name|compressed
init|=
operator|new
name|byte
index|[
name|BYTE_SIZE
index|]
decl_stmt|;
name|int
name|cSize
init|=
name|compressor
operator|.
name|compress
argument_list|(
name|compressed
argument_list|,
literal|0
argument_list|,
name|compressed
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"SnappyCompressDecompress getBytesWritten after compress error !!!"
argument_list|,
name|compressor
operator|.
name|getBytesWritten
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|SnappyDecompressor
name|decompressor
init|=
operator|new
name|SnappyDecompressor
argument_list|(
name|BYTE_SIZE
argument_list|)
decl_stmt|;
comment|// set as input for decompressor only compressed data indicated with cSize
name|decompressor
operator|.
name|setInput
argument_list|(
name|compressed
argument_list|,
literal|0
argument_list|,
name|cSize
argument_list|)
expr_stmt|;
name|byte
index|[]
name|decompressed
init|=
operator|new
name|byte
index|[
name|BYTE_SIZE
index|]
decl_stmt|;
name|decompressor
operator|.
name|decompress
argument_list|(
name|decompressed
argument_list|,
literal|0
argument_list|,
name|decompressed
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"testSnappyCompressDecompress finished error !!!"
argument_list|,
name|decompressor
operator|.
name|finished
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|bytes
argument_list|,
name|decompressed
argument_list|)
expr_stmt|;
name|compressor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|decompressor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"decompressor getRemaining error !!!"
argument_list|,
name|decompressor
operator|.
name|getRemaining
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyCompressDecompress ex error!!!"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCompressorDecompressorEmptyStreamLogic ()
specifier|public
name|void
name|testCompressorDecompressorEmptyStreamLogic
parameter_list|()
block|{
name|ByteArrayInputStream
name|bytesIn
init|=
literal|null
decl_stmt|;
name|ByteArrayOutputStream
name|bytesOut
init|=
literal|null
decl_stmt|;
name|byte
index|[]
name|buf
init|=
literal|null
decl_stmt|;
name|BlockDecompressorStream
name|blockDecompressorStream
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// compress empty stream
name|bytesOut
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|BlockCompressorStream
name|blockCompressorStream
init|=
operator|new
name|BlockCompressorStream
argument_list|(
name|bytesOut
argument_list|,
operator|new
name|SnappyCompressor
argument_list|()
argument_list|,
literal|1024
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// close without write
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
literal|"empty stream compressed output size != 4"
argument_list|,
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
comment|// create decompression stream
name|blockDecompressorStream
operator|=
operator|new
name|BlockDecompressorStream
argument_list|(
name|bytesIn
argument_list|,
operator|new
name|SnappyDecompressor
argument_list|()
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
comment|// no byte is available because stream was closed
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
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testCompressorDecompressorEmptyStreamLogic ex error !!!"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|blockDecompressorStream
operator|!=
literal|null
condition|)
try|try
block|{
name|bytesIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|bytesOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|blockDecompressorStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
block|}
block|}
annotation|@
name|Test
DECL|method|testSnappyBlockCompression ()
specifier|public
name|void
name|testSnappyBlockCompression
parameter_list|()
block|{
name|int
name|BYTE_SIZE
init|=
literal|1024
operator|*
literal|50
decl_stmt|;
name|int
name|BLOCK_SIZE
init|=
literal|512
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|block
init|=
operator|new
name|byte
index|[
name|BLOCK_SIZE
index|]
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|BytesGenerator
operator|.
name|get
argument_list|(
name|BYTE_SIZE
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Use default of 512 as bufferSize and compressionOverhead of
comment|// (1% of bufferSize + 12 bytes) = 18 bytes (zlib algorithm).
name|SnappyCompressor
name|compressor
init|=
operator|new
name|SnappyCompressor
argument_list|()
decl_stmt|;
name|int
name|off
init|=
literal|0
decl_stmt|;
name|int
name|len
init|=
name|BYTE_SIZE
decl_stmt|;
name|int
name|maxSize
init|=
name|BLOCK_SIZE
operator|-
literal|18
decl_stmt|;
if|if
condition|(
name|BYTE_SIZE
operator|>
name|maxSize
condition|)
block|{
do|do
block|{
name|int
name|bufLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|maxSize
argument_list|)
decl_stmt|;
name|compressor
operator|.
name|setInput
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|bufLen
argument_list|)
expr_stmt|;
name|compressor
operator|.
name|finish
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|compressor
operator|.
name|finished
argument_list|()
condition|)
block|{
name|compressor
operator|.
name|compress
argument_list|(
name|block
argument_list|,
literal|0
argument_list|,
name|block
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
name|compressor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|off
operator|+=
name|bufLen
expr_stmt|;
name|len
operator|-=
name|bufLen
expr_stmt|;
block|}
do|while
condition|(
name|len
operator|>
literal|0
condition|)
do|;
block|}
name|assertTrue
argument_list|(
literal|"testSnappyBlockCompression error !!!"
argument_list|,
name|out
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyBlockCompression ex error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|compressDecompressLoop (int rawDataSize)
specifier|private
name|void
name|compressDecompressLoop
parameter_list|(
name|int
name|rawDataSize
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|rawData
init|=
name|BytesGenerator
operator|.
name|get
argument_list|(
name|rawDataSize
argument_list|)
decl_stmt|;
name|byte
index|[]
name|compressedResult
init|=
operator|new
name|byte
index|[
name|rawDataSize
operator|+
literal|20
index|]
decl_stmt|;
name|int
name|directBufferSize
init|=
name|Math
operator|.
name|max
argument_list|(
name|rawDataSize
operator|*
literal|2
argument_list|,
literal|64
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|SnappyCompressor
name|compressor
init|=
operator|new
name|SnappyCompressor
argument_list|(
name|directBufferSize
argument_list|)
decl_stmt|;
name|compressor
operator|.
name|setInput
argument_list|(
name|rawData
argument_list|,
literal|0
argument_list|,
name|rawDataSize
argument_list|)
expr_stmt|;
name|int
name|compressedSize
init|=
name|compressor
operator|.
name|compress
argument_list|(
name|compressedResult
argument_list|,
literal|0
argument_list|,
name|compressedResult
operator|.
name|length
argument_list|)
decl_stmt|;
name|SnappyDirectDecompressor
name|decompressor
init|=
operator|new
name|SnappyDirectDecompressor
argument_list|()
decl_stmt|;
name|ByteBuffer
name|inBuf
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|compressedSize
argument_list|)
decl_stmt|;
name|ByteBuffer
name|outBuf
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|rawDataSize
argument_list|)
decl_stmt|;
name|inBuf
operator|.
name|put
argument_list|(
name|compressedResult
argument_list|,
literal|0
argument_list|,
name|compressedSize
argument_list|)
expr_stmt|;
name|inBuf
operator|.
name|flip
argument_list|()
expr_stmt|;
name|ByteBuffer
name|expected
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|rawData
argument_list|)
decl_stmt|;
name|outBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|decompressor
operator|.
name|finished
argument_list|()
condition|)
block|{
name|decompressor
operator|.
name|decompress
argument_list|(
name|inBuf
argument_list|,
name|outBuf
argument_list|)
expr_stmt|;
if|if
condition|(
name|outBuf
operator|.
name|remaining
argument_list|()
operator|==
literal|0
condition|)
block|{
name|outBuf
operator|.
name|flip
argument_list|()
expr_stmt|;
while|while
condition|(
name|outBuf
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|get
argument_list|()
argument_list|,
name|outBuf
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|outBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
name|outBuf
operator|.
name|flip
argument_list|()
expr_stmt|;
while|while
condition|(
name|outBuf
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|get
argument_list|()
argument_list|,
name|outBuf
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|outBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|expected
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSnappyDirectBlockCompression ()
specifier|public
name|void
name|testSnappyDirectBlockCompression
parameter_list|()
block|{
name|int
index|[]
name|size
init|=
block|{
literal|4
operator|*
literal|1024
block|,
literal|64
operator|*
literal|1024
block|,
literal|128
operator|*
literal|1024
block|,
literal|1024
operator|*
literal|1024
block|}
decl_stmt|;
name|assumeTrue
argument_list|(
name|SnappyCodec
operator|.
name|isNativeCodeLoaded
argument_list|()
argument_list|)
expr_stmt|;
try|try
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
name|size
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|compressDecompressLoop
argument_list|(
name|size
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testSnappyDirectBlockCompression ex !!!"
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSnappyCompressorDecopressorLogicWithCompressionStreams ()
specifier|public
name|void
name|testSnappyCompressorDecopressorLogicWithCompressionStreams
parameter_list|()
block|{
name|int
name|BYTE_SIZE
init|=
literal|1024
operator|*
literal|100
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|BytesGenerator
operator|.
name|get
argument_list|(
name|BYTE_SIZE
argument_list|)
decl_stmt|;
name|int
name|bufferSize
init|=
literal|262144
decl_stmt|;
name|int
name|compressionOverhead
init|=
operator|(
name|bufferSize
operator|/
literal|6
operator|)
operator|+
literal|32
decl_stmt|;
name|DataOutputStream
name|deflateOut
init|=
literal|null
decl_stmt|;
name|DataInputStream
name|inflateIn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|DataOutputBuffer
name|compressedDataBuffer
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|CompressionOutputStream
name|deflateFilter
init|=
operator|new
name|BlockCompressorStream
argument_list|(
name|compressedDataBuffer
argument_list|,
operator|new
name|SnappyCompressor
argument_list|(
name|bufferSize
argument_list|)
argument_list|,
name|bufferSize
argument_list|,
name|compressionOverhead
argument_list|)
decl_stmt|;
name|deflateOut
operator|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|deflateFilter
argument_list|)
argument_list|)
expr_stmt|;
name|deflateOut
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|deflateOut
operator|.
name|flush
argument_list|()
expr_stmt|;
name|deflateFilter
operator|.
name|finish
argument_list|()
expr_stmt|;
name|DataInputBuffer
name|deCompressedDataBuffer
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|deCompressedDataBuffer
operator|.
name|reset
argument_list|(
name|compressedDataBuffer
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|compressedDataBuffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|CompressionInputStream
name|inflateFilter
init|=
operator|new
name|BlockDecompressorStream
argument_list|(
name|deCompressedDataBuffer
argument_list|,
operator|new
name|SnappyDecompressor
argument_list|(
name|bufferSize
argument_list|)
argument_list|,
name|bufferSize
argument_list|)
decl_stmt|;
name|inflateIn
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|inflateFilter
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|BYTE_SIZE
index|]
decl_stmt|;
name|inflateIn
operator|.
name|read
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
literal|"original array not equals compress/decompressed array"
argument_list|,
name|result
argument_list|,
name|bytes
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
literal|"testSnappyCompressorDecopressorLogicWithCompressionStreams ex error !!!"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|deflateOut
operator|!=
literal|null
condition|)
name|deflateOut
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|inflateIn
operator|!=
literal|null
condition|)
name|inflateIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{       }
block|}
block|}
DECL|class|BytesGenerator
specifier|static
specifier|final
class|class
name|BytesGenerator
block|{
DECL|method|BytesGenerator ()
specifier|private
name|BytesGenerator
parameter_list|()
block|{     }
DECL|field|CACHE
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|CACHE
init|=
operator|new
name|byte
index|[]
block|{
literal|0x0
block|,
literal|0x1
block|,
literal|0x2
block|,
literal|0x3
block|,
literal|0x4
block|,
literal|0x5
block|,
literal|0x6
block|,
literal|0x7
block|,
literal|0x8
block|,
literal|0x9
block|,
literal|0xA
block|,
literal|0xB
block|,
literal|0xC
block|,
literal|0xD
block|,
literal|0xE
block|,
literal|0xF
block|}
decl_stmt|;
DECL|field|rnd
specifier|private
specifier|static
specifier|final
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|(
literal|12345l
argument_list|)
decl_stmt|;
DECL|method|get (int size)
specifier|public
specifier|static
name|byte
index|[]
name|get
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|byte
index|[]
name|array
init|=
operator|(
name|byte
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|byte
operator|.
name|class
argument_list|,
name|size
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
name|size
condition|;
name|i
operator|++
control|)
name|array
index|[
name|i
index|]
operator|=
name|CACHE
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
name|CACHE
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
expr_stmt|;
return|return
name|array
return|;
block|}
block|}
block|}
end_class

end_unit

