begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress.bzip2
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
name|bzip2
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A {@link Decompressor} based on the popular   * bzip2 compression algorithm.  * http://www.bzip2.org/  *   */
end_comment

begin_class
DECL|class|Bzip2Decompressor
specifier|public
class|class
name|Bzip2Decompressor
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
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Bzip2Decompressor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|stream
specifier|private
name|long
name|stream
decl_stmt|;
DECL|field|conserveMemory
specifier|private
name|boolean
name|conserveMemory
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
comment|/**    * Creates a new decompressor.    */
DECL|method|Bzip2Decompressor (boolean conserveMemory, int directBufferSize)
specifier|public
name|Bzip2Decompressor
parameter_list|(
name|boolean
name|conserveMemory
parameter_list|,
name|int
name|directBufferSize
parameter_list|)
block|{
name|this
operator|.
name|conserveMemory
operator|=
name|conserveMemory
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
name|conserveMemory
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|Bzip2Decompressor ()
specifier|public
name|Bzip2Decompressor
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|,
name|DEFAULT_DIRECT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setInput (byte[] b, int off, int len)
specifier|public
specifier|synchronized
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
comment|// Reinitialize bzip2's output direct buffer.
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
specifier|synchronized
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
comment|// Reinitialize bzip2's input direct buffer.
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
comment|// Note how much data is being fed to bzip2.
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
specifier|synchronized
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
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|needsInput ()
specifier|public
specifier|synchronized
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
comment|// Check if bzip2 has consumed all input.
if|if
condition|(
name|compressedDirectBufLen
operator|<=
literal|0
condition|)
block|{
comment|// Check if we have consumed all user-input.
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
specifier|synchronized
name|boolean
name|needsDictionary
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|finished ()
specifier|public
specifier|synchronized
name|boolean
name|finished
parameter_list|()
block|{
comment|// Check if bzip2 says it has finished and
comment|// all compressed data has been consumed.
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
specifier|synchronized
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
comment|// Check if there is uncompressed data.
name|int
name|n
init|=
name|uncompressedDirectBuf
operator|.
name|remaining
argument_list|()
decl_stmt|;
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
comment|// Re-initialize bzip2's output direct buffer.
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
comment|// Decompress the data.
name|n
operator|=
name|finished
condition|?
literal|0
else|:
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
comment|// Get at most 'len' bytes.
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
specifier|synchronized
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
comment|/**    * Returns the total number of compressed bytes input so far.</p>    *    * @return the total (non-negative) number of compressed bytes input so far    */
DECL|method|getBytesRead ()
specifier|public
specifier|synchronized
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
comment|/**    * Returns the number of bytes remaining in the input buffers; normally    * called when finished() is true to determine amount of post-gzip-stream    * data.</p>    *    * @return the total (non-negative) number of unprocessed bytes in input    */
annotation|@
name|Override
DECL|method|getRemaining ()
specifier|public
specifier|synchronized
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
comment|/**    * Resets everything including the input buffers (user and direct).</p>    */
annotation|@
name|Override
DECL|method|reset ()
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|checkStream
argument_list|()
expr_stmt|;
name|end
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
name|init
argument_list|(
name|conserveMemory
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
name|finished
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
specifier|synchronized
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
DECL|method|initSymbols (String libname)
specifier|static
name|void
name|initSymbols
parameter_list|(
name|String
name|libname
parameter_list|)
block|{
name|initIDs
argument_list|(
name|libname
argument_list|)
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
DECL|method|initIDs (String libname)
specifier|private
specifier|native
specifier|static
name|void
name|initIDs
parameter_list|(
name|String
name|libname
parameter_list|)
function_decl|;
DECL|method|init (int conserveMemory)
specifier|private
specifier|native
specifier|static
name|long
name|init
parameter_list|(
name|int
name|conserveMemory
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
block|}
end_class

end_unit

