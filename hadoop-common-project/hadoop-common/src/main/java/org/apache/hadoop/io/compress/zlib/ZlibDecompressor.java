begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress.zlib
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
name|zlib
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
name|nio
operator|.
name|Buffer
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
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|compress
operator|.
name|Decompressor
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
name|DirectDecompressor
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
name|NativeCodeLoader
import|;
end_import

begin_comment
comment|/**  * A {@link Decompressor} based on the popular   * zlib compression algorithm.  * http://www.zlib.net/  *   */
end_comment

begin_class
DECL|class|ZlibDecompressor
specifier|public
class|class
name|ZlibDecompressor
implements|implements
name|Decompressor
block|{
DECL|field|DEFAULT_DIRECT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_DIRECT_BUFFER_SIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|stream
specifier|private
name|long
name|stream
decl_stmt|;
DECL|field|header
specifier|private
name|CompressionHeader
name|header
decl_stmt|;
DECL|field|directBufferSize
specifier|private
name|int
name|directBufferSize
decl_stmt|;
DECL|field|compressedDirectBuf
specifier|private
name|Buffer
name|compressedDirectBuf
init|=
literal|null
decl_stmt|;
DECL|field|compressedDirectBufOff
DECL|field|compressedDirectBufLen
specifier|private
name|int
name|compressedDirectBufOff
decl_stmt|,
name|compressedDirectBufLen
decl_stmt|;
DECL|field|uncompressedDirectBuf
specifier|private
name|Buffer
name|uncompressedDirectBuf
init|=
literal|null
decl_stmt|;
DECL|field|userBuf
specifier|private
name|byte
index|[]
name|userBuf
init|=
literal|null
decl_stmt|;
DECL|field|userBufOff
DECL|field|userBufLen
specifier|private
name|int
name|userBufOff
init|=
literal|0
decl_stmt|,
name|userBufLen
init|=
literal|0
decl_stmt|;
DECL|field|finished
specifier|private
name|boolean
name|finished
decl_stmt|;
DECL|field|needDict
specifier|private
name|boolean
name|needDict
decl_stmt|;
comment|/**    * The headers to detect from compressed data.    */
DECL|enum|CompressionHeader
specifier|public
enum|enum
name|CompressionHeader
block|{
comment|/**      * No headers/trailers/checksums.      */
DECL|enumConstant|NO_HEADER
name|NO_HEADER
argument_list|(
operator|-
literal|15
argument_list|)
block|,
comment|/**      * Default headers/trailers/checksums.      */
DECL|enumConstant|DEFAULT_HEADER
name|DEFAULT_HEADER
argument_list|(
literal|15
argument_list|)
block|,
comment|/**      * Simple gzip headers/trailers.      */
DECL|enumConstant|GZIP_FORMAT
name|GZIP_FORMAT
argument_list|(
literal|31
argument_list|)
block|,
comment|/**      * Autodetect gzip/zlib headers/trailers.      */
DECL|enumConstant|AUTODETECT_GZIP_ZLIB
name|AUTODETECT_GZIP_ZLIB
argument_list|(
literal|47
argument_list|)
block|;
DECL|field|windowBits
specifier|private
specifier|final
name|int
name|windowBits
decl_stmt|;
DECL|method|CompressionHeader (int windowBits)
name|CompressionHeader
parameter_list|(
name|int
name|windowBits
parameter_list|)
block|{
name|this
operator|.
name|windowBits
operator|=
name|windowBits
expr_stmt|;
block|}
DECL|method|windowBits ()
specifier|public
name|int
name|windowBits
parameter_list|()
block|{
return|return
name|windowBits
return|;
block|}
block|}
DECL|field|nativeZlibLoaded
specifier|private
specifier|static
name|boolean
name|nativeZlibLoaded
init|=
literal|false
decl_stmt|;
static|static
block|{
if|if
condition|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
try|try
block|{
comment|// Initialize the native library
name|initIDs
argument_list|()
expr_stmt|;
name|nativeZlibLoaded
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Ignore failure to load/initialize native-zlib
block|}
block|}
block|}
DECL|method|isNativeZlibLoaded ()
specifier|static
name|boolean
name|isNativeZlibLoaded
parameter_list|()
block|{
return|return
name|nativeZlibLoaded
return|;
block|}
comment|/**    * Creates a new decompressor.    */
DECL|method|ZlibDecompressor (CompressionHeader header, int directBufferSize)
specifier|public
name|ZlibDecompressor
parameter_list|(
name|CompressionHeader
name|header
parameter_list|,
name|int
name|directBufferSize
parameter_list|)
block|{
name|this
operator|.
name|header
operator|=
name|header
expr_stmt|;
name|this
operator|.
name|directBufferSize
operator|=
name|directBufferSize
expr_stmt|;
name|compressedDirectBuf
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|directBufferSize
argument_list|)
expr_stmt|;
name|uncompressedDirectBuf
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|directBufferSize
argument_list|)
expr_stmt|;
name|uncompressedDirectBuf
operator|.
name|position
argument_list|(
name|directBufferSize
argument_list|)
expr_stmt|;
name|stream
operator|=
name|init
argument_list|(
name|this
operator|.
name|header
operator|.
name|windowBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ZlibDecompressor ()
specifier|public
name|ZlibDecompressor
parameter_list|()
block|{
name|this
argument_list|(
name|CompressionHeader
operator|.
name|DEFAULT_HEADER
argument_list|,
name|DEFAULT_DIRECT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setInput (byte[] b, int off, int len)
specifier|public
name|void
name|setInput
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
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
if|if
condition|(
name|off
operator|<
literal|0
operator|||
name|len
argument_list|<
literal|0
operator|||
name|off
argument_list|>
name|b
operator|.
name|length
operator|-
name|len
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|()
throw|;
block|}
name|this
operator|.
name|userBuf
operator|=
name|b
expr_stmt|;
name|this
operator|.
name|userBufOff
operator|=
name|off
expr_stmt|;
name|this
operator|.
name|userBufLen
operator|=
name|len
expr_stmt|;
name|setInputFromSavedData
argument_list|()
expr_stmt|;
comment|// Reinitialize zlib's output direct buffer
name|uncompressedDirectBuf
operator|.
name|limit
argument_list|(
name|directBufferSize
argument_list|)
expr_stmt|;
name|uncompressedDirectBuf
operator|.
name|position
argument_list|(
name|directBufferSize
argument_list|)
expr_stmt|;
block|}
DECL|method|setInputFromSavedData ()
name|void
name|setInputFromSavedData
parameter_list|()
block|{
name|compressedDirectBufOff
operator|=
literal|0
expr_stmt|;
name|compressedDirectBufLen
operator|=
name|userBufLen
expr_stmt|;
if|if
condition|(
name|compressedDirectBufLen
operator|>
name|directBufferSize
condition|)
block|{
name|compressedDirectBufLen
operator|=
name|directBufferSize
expr_stmt|;
block|}
comment|// Reinitialize zlib's input direct buffer
name|compressedDirectBuf
operator|.
name|rewind
argument_list|()
expr_stmt|;
operator|(
operator|(
name|ByteBuffer
operator|)
name|compressedDirectBuf
operator|)
operator|.
name|put
argument_list|(
name|userBuf
argument_list|,
name|userBufOff
argument_list|,
name|compressedDirectBufLen
argument_list|)
expr_stmt|;
comment|// Note how much data is being fed to zlib
name|userBufOff
operator|+=
name|compressedDirectBufLen
expr_stmt|;
name|userBufLen
operator|-=
name|compressedDirectBufLen
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDictionary (byte[] b, int off, int len)
specifier|public
name|void
name|setDictionary
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
block|{
if|if
condition|(
name|stream
operator|==
literal|0
operator|||
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
if|if
condition|(
name|off
operator|<
literal|0
operator|||
name|len
argument_list|<
literal|0
operator|||
name|off
argument_list|>
name|b
operator|.
name|length
operator|-
name|len
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|()
throw|;
block|}
name|setDictionary
argument_list|(
name|stream
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|needDict
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsInput ()
specifier|public
name|boolean
name|needsInput
parameter_list|()
block|{
comment|// Consume remaining compressed data?
if|if
condition|(
name|uncompressedDirectBuf
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Check if zlib has consumed all input
if|if
condition|(
name|compressedDirectBufLen
operator|<=
literal|0
condition|)
block|{
comment|// Check if we have consumed all user-input
if|if
condition|(
name|userBufLen
operator|<=
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|setInputFromSavedData
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|needsDictionary ()
specifier|public
name|boolean
name|needsDictionary
parameter_list|()
block|{
return|return
name|needDict
return|;
block|}
annotation|@
name|Override
DECL|method|finished ()
specifier|public
name|boolean
name|finished
parameter_list|()
block|{
comment|// Check if 'zlib' says it's 'finished' and
comment|// all compressed data has been consumed
return|return
operator|(
name|finished
operator|&&
name|uncompressedDirectBuf
operator|.
name|remaining
argument_list|()
operator|==
literal|0
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|decompress (byte[] b, int off, int len)
specifier|public
name|int
name|decompress
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
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
if|if
condition|(
name|off
operator|<
literal|0
operator|||
name|len
argument_list|<
literal|0
operator|||
name|off
argument_list|>
name|b
operator|.
name|length
operator|-
name|len
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|()
throw|;
block|}
name|int
name|n
init|=
literal|0
decl_stmt|;
comment|// Check if there is uncompressed data
name|n
operator|=
name|uncompressedDirectBuf
operator|.
name|remaining
argument_list|()
expr_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|n
operator|=
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|len
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ByteBuffer
operator|)
name|uncompressedDirectBuf
operator|)
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|n
argument_list|)
expr_stmt|;
return|return
name|n
return|;
block|}
comment|// Re-initialize the zlib's output direct buffer
name|uncompressedDirectBuf
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|uncompressedDirectBuf
operator|.
name|limit
argument_list|(
name|directBufferSize
argument_list|)
expr_stmt|;
comment|// Decompress data
name|n
operator|=
name|inflateBytesDirect
argument_list|()
expr_stmt|;
name|uncompressedDirectBuf
operator|.
name|limit
argument_list|(
name|n
argument_list|)
expr_stmt|;
comment|// Get at most 'len' bytes
name|n
operator|=
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|len
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ByteBuffer
operator|)
name|uncompressedDirectBuf
operator|)
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|n
argument_list|)
expr_stmt|;
return|return
name|n
return|;
block|}
comment|/**    * Returns the total number of uncompressed bytes output so far.    *    * @return the total (non-negative) number of uncompressed bytes output so far    */
DECL|method|getBytesWritten ()
specifier|public
name|long
name|getBytesWritten
parameter_list|()
block|{
name|checkStream
argument_list|()
expr_stmt|;
return|return
name|getBytesWritten
argument_list|(
name|stream
argument_list|)
return|;
block|}
comment|/**    * Returns the total number of compressed bytes input so far.    *    * @return the total (non-negative) number of compressed bytes input so far    */
DECL|method|getBytesRead ()
specifier|public
name|long
name|getBytesRead
parameter_list|()
block|{
name|checkStream
argument_list|()
expr_stmt|;
return|return
name|getBytesRead
argument_list|(
name|stream
argument_list|)
return|;
block|}
comment|/**    * Returns the number of bytes remaining in the input buffers; normally    * called when finished() is true to determine amount of post-gzip-stream    * data.    *    * @return the total (non-negative) number of unprocessed bytes in input    */
annotation|@
name|Override
DECL|method|getRemaining ()
specifier|public
name|int
name|getRemaining
parameter_list|()
block|{
name|checkStream
argument_list|()
expr_stmt|;
return|return
name|userBufLen
operator|+
name|getRemaining
argument_list|(
name|stream
argument_list|)
return|;
comment|// userBuf + compressedDirectBuf
block|}
comment|/**    * Resets everything including the input buffers (user and direct).    */
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|checkStream
argument_list|()
expr_stmt|;
name|reset
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|finished
operator|=
literal|false
expr_stmt|;
name|needDict
operator|=
literal|false
expr_stmt|;
name|compressedDirectBufOff
operator|=
name|compressedDirectBufLen
operator|=
literal|0
expr_stmt|;
name|uncompressedDirectBuf
operator|.
name|limit
argument_list|(
name|directBufferSize
argument_list|)
expr_stmt|;
name|uncompressedDirectBuf
operator|.
name|position
argument_list|(
name|directBufferSize
argument_list|)
expr_stmt|;
name|userBufOff
operator|=
name|userBufLen
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|end ()
specifier|public
name|void
name|end
parameter_list|()
block|{
if|if
condition|(
name|stream
operator|!=
literal|0
condition|)
block|{
name|end
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
literal|0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finalize ()
specifier|protected
name|void
name|finalize
parameter_list|()
block|{
name|end
argument_list|()
expr_stmt|;
block|}
DECL|method|checkStream ()
specifier|private
name|void
name|checkStream
parameter_list|()
block|{
if|if
condition|(
name|stream
operator|==
literal|0
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
DECL|method|initIDs ()
specifier|private
specifier|native
specifier|static
name|void
name|initIDs
parameter_list|()
function_decl|;
DECL|method|init (int windowBits)
specifier|private
specifier|native
specifier|static
name|long
name|init
parameter_list|(
name|int
name|windowBits
parameter_list|)
function_decl|;
DECL|method|setDictionary (long strm, byte[] b, int off, int len)
specifier|private
specifier|native
specifier|static
name|void
name|setDictionary
parameter_list|(
name|long
name|strm
parameter_list|,
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
function_decl|;
DECL|method|inflateBytesDirect ()
specifier|private
specifier|native
name|int
name|inflateBytesDirect
parameter_list|()
function_decl|;
DECL|method|getBytesRead (long strm)
specifier|private
specifier|native
specifier|static
name|long
name|getBytesRead
parameter_list|(
name|long
name|strm
parameter_list|)
function_decl|;
DECL|method|getBytesWritten (long strm)
specifier|private
specifier|native
specifier|static
name|long
name|getBytesWritten
parameter_list|(
name|long
name|strm
parameter_list|)
function_decl|;
DECL|method|getRemaining (long strm)
specifier|private
specifier|native
specifier|static
name|int
name|getRemaining
parameter_list|(
name|long
name|strm
parameter_list|)
function_decl|;
DECL|method|reset (long strm)
specifier|private
specifier|native
specifier|static
name|void
name|reset
parameter_list|(
name|long
name|strm
parameter_list|)
function_decl|;
DECL|method|end (long strm)
specifier|private
specifier|native
specifier|static
name|void
name|end
parameter_list|(
name|long
name|strm
parameter_list|)
function_decl|;
DECL|method|inflateDirect (ByteBuffer src, ByteBuffer dst)
name|int
name|inflateDirect
parameter_list|(
name|ByteBuffer
name|src
parameter_list|,
name|ByteBuffer
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|(
name|this
operator|instanceof
name|ZlibDirectDecompressor
operator|)
assert|;
name|ByteBuffer
name|presliced
init|=
name|dst
decl_stmt|;
if|if
condition|(
name|dst
operator|.
name|position
argument_list|()
operator|>
literal|0
condition|)
block|{
name|presliced
operator|=
name|dst
expr_stmt|;
name|dst
operator|=
name|dst
operator|.
name|slice
argument_list|()
expr_stmt|;
block|}
name|Buffer
name|originalCompressed
init|=
name|compressedDirectBuf
decl_stmt|;
name|Buffer
name|originalUncompressed
init|=
name|uncompressedDirectBuf
decl_stmt|;
name|int
name|originalBufferSize
init|=
name|directBufferSize
decl_stmt|;
name|compressedDirectBuf
operator|=
name|src
expr_stmt|;
name|compressedDirectBufOff
operator|=
name|src
operator|.
name|position
argument_list|()
expr_stmt|;
name|compressedDirectBufLen
operator|=
name|src
operator|.
name|remaining
argument_list|()
expr_stmt|;
name|uncompressedDirectBuf
operator|=
name|dst
expr_stmt|;
name|directBufferSize
operator|=
name|dst
operator|.
name|remaining
argument_list|()
expr_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
try|try
block|{
name|n
operator|=
name|inflateBytesDirect
argument_list|()
expr_stmt|;
name|presliced
operator|.
name|position
argument_list|(
name|presliced
operator|.
name|position
argument_list|()
operator|+
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|compressedDirectBufLen
operator|>
literal|0
condition|)
block|{
name|src
operator|.
name|position
argument_list|(
name|compressedDirectBufOff
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|src
operator|.
name|position
argument_list|(
name|src
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|compressedDirectBuf
operator|=
name|originalCompressed
expr_stmt|;
name|uncompressedDirectBuf
operator|=
name|originalUncompressed
expr_stmt|;
name|compressedDirectBufOff
operator|=
literal|0
expr_stmt|;
name|compressedDirectBufLen
operator|=
literal|0
expr_stmt|;
name|directBufferSize
operator|=
name|originalBufferSize
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
DECL|class|ZlibDirectDecompressor
specifier|public
specifier|static
class|class
name|ZlibDirectDecompressor
extends|extends
name|ZlibDecompressor
implements|implements
name|DirectDecompressor
block|{
DECL|method|ZlibDirectDecompressor ()
specifier|public
name|ZlibDirectDecompressor
parameter_list|()
block|{
name|super
argument_list|(
name|CompressionHeader
operator|.
name|DEFAULT_HEADER
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|ZlibDirectDecompressor (CompressionHeader header, int directBufferSize)
specifier|public
name|ZlibDirectDecompressor
parameter_list|(
name|CompressionHeader
name|header
parameter_list|,
name|int
name|directBufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|header
argument_list|,
name|directBufferSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finished ()
specifier|public
name|boolean
name|finished
parameter_list|()
block|{
return|return
operator|(
name|endOfInput
operator|&&
name|super
operator|.
name|finished
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|endOfInput
operator|=
literal|true
expr_stmt|;
block|}
DECL|field|endOfInput
specifier|private
name|boolean
name|endOfInput
decl_stmt|;
annotation|@
name|Override
DECL|method|decompress (ByteBuffer src, ByteBuffer dst)
specifier|public
name|void
name|decompress
parameter_list|(
name|ByteBuffer
name|src
parameter_list|,
name|ByteBuffer
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|dst
operator|.
name|isDirect
argument_list|()
operator|:
literal|"dst.isDirect()"
assert|;
assert|assert
name|src
operator|.
name|isDirect
argument_list|()
operator|:
literal|"src.isDirect()"
assert|;
assert|assert
name|dst
operator|.
name|remaining
argument_list|()
operator|>
literal|0
operator|:
literal|"dst.remaining()> 0"
assert|;
name|this
operator|.
name|inflateDirect
argument_list|(
name|src
argument_list|,
name|dst
argument_list|)
expr_stmt|;
name|endOfInput
operator|=
operator|!
name|src
operator|.
name|hasRemaining
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDictionary (byte[] b, int off, int len)
specifier|public
name|void
name|setDictionary
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"byte[] arrays are not supported for DirectDecompressor"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|decompress (byte[] b, int off, int len)
specifier|public
name|int
name|decompress
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"byte[] arrays are not supported for DirectDecompressor"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

