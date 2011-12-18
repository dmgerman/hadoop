begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress.lz4
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
name|lz4
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|util
operator|.
name|NativeCodeLoader
import|;
end_import

begin_comment
comment|/**  * A {@link Decompressor} based on the lz4 compression algorithm.  * http://code.google.com/p/lz4/  */
end_comment

begin_class
DECL|class|Lz4Decompressor
specifier|public
class|class
name|Lz4Decompressor
implements|implements
name|Decompressor
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Lz4Compressor
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
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
comment|// HACK - Use this as a global lock in the JNI layer
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"unused"
block|}
argument_list|)
DECL|field|clazz
specifier|private
specifier|static
name|Class
name|clazz
init|=
name|Lz4Decompressor
operator|.
name|class
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
DECL|field|compressedDirectBufLen
specifier|private
name|int
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
comment|// Initialize the native library
try|try
block|{
name|initIDs
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Ignore failure to load/initialize lz4
name|LOG
operator|.
name|warn
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot load "
operator|+
name|Lz4Compressor
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" without native hadoop library!"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a new compressor.    *    * @param directBufferSize size of the direct buffer to be used.    */
DECL|method|Lz4Decompressor (int directBufferSize)
specifier|public
name|Lz4Decompressor
parameter_list|(
name|int
name|directBufferSize
parameter_list|)
block|{
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
block|}
comment|/**    * Creates a new decompressor with the default buffer size.    */
DECL|method|Lz4Decompressor ()
specifier|public
name|Lz4Decompressor
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_DIRECT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets input data for decompression.    * This should be called if and only if {@link #needsInput()} returns    *<code>true</code> indicating that more input data is required.    * (Both native and non-native versions of various Decompressors require    * that the data passed in via<code>b[]</code> remain unmodified until    * the caller is explicitly notified--via {@link #needsInput()}--that the    * buffer may be safely modified.  With this requirement, an extra    * buffer-copy can be avoided.)    *    * @param b   Input data    * @param off Start offset    * @param len Length    */
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
comment|// Reinitialize lz4's output direct-buffer
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
comment|/**    * If a write would exceed the capacity of the direct buffers, it is set    * aside to be loaded by this function while the compressed data are    * consumed.    */
DECL|method|setInputFromSavedData ()
specifier|synchronized
name|void
name|setInputFromSavedData
parameter_list|()
block|{
name|compressedDirectBufLen
operator|=
name|Math
operator|.
name|min
argument_list|(
name|userBufLen
argument_list|,
name|directBufferSize
argument_list|)
expr_stmt|;
comment|// Reinitialize lz4's input direct buffer
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
comment|// Note how much data is being fed to lz4
name|userBufOff
operator|+=
name|compressedDirectBufLen
expr_stmt|;
name|userBufLen
operator|-=
name|compressedDirectBufLen
expr_stmt|;
block|}
comment|/**    * Does nothing.    */
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
comment|// do nothing
block|}
comment|/**    * Returns true if the input data buffer is empty and    * {@link #setInput(byte[], int, int)} should be called to    * provide more input.    *    * @return<code>true</code> if the input data buffer is empty and    *         {@link #setInput(byte[], int, int)} should be called in    *         order to provide more input.    */
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
comment|// Check if lz4 has consumed all input
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
comment|/**    * Returns<code>false</code>.    *    * @return<code>false</code>.    */
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
comment|/**    * Returns true if the end of the decompressed    * data output stream has been reached.    *    * @return<code>true</code> if the end of the decompressed    *         data output stream has been reached.    */
annotation|@
name|Override
DECL|method|finished ()
specifier|public
specifier|synchronized
name|boolean
name|finished
parameter_list|()
block|{
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
comment|/**    * Fills specified buffer with uncompressed data. Returns actual number    * of bytes of uncompressed data. A return value of 0 indicates that    * {@link #needsInput()} should be called in order to determine if more    * input data is required.    *    * @param b   Buffer for the compressed data    * @param off Start offset of the data    * @param len Size of the buffer    * @return The actual number of bytes of compressed data.    * @throws IOException    */
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
if|if
condition|(
name|compressedDirectBufLen
operator|>
literal|0
condition|)
block|{
comment|// Re-initialize the lz4's output direct buffer
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
name|decompressBytesDirect
argument_list|()
expr_stmt|;
name|uncompressedDirectBuf
operator|.
name|limit
argument_list|(
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|userBufLen
operator|<=
literal|0
condition|)
block|{
name|finished
operator|=
literal|true
expr_stmt|;
block|}
comment|// Get atmost 'len' bytes
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
block|}
return|return
name|n
return|;
block|}
comment|/**    * Returns<code>0</code>.    *    * @return<code>0</code>.    */
annotation|@
name|Override
DECL|method|getRemaining ()
specifier|public
specifier|synchronized
name|int
name|getRemaining
parameter_list|()
block|{
comment|// Never use this function in BlockDecompressorStream.
return|return
literal|0
return|;
block|}
DECL|method|reset ()
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|finished
operator|=
literal|false
expr_stmt|;
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
comment|/**    * Resets decompressor and input and output buffers so that a new set of    * input data can be processed.    */
annotation|@
name|Override
DECL|method|end ()
specifier|public
specifier|synchronized
name|void
name|end
parameter_list|()
block|{
comment|// do nothing
block|}
DECL|method|initIDs ()
specifier|private
specifier|native
specifier|static
name|void
name|initIDs
parameter_list|()
function_decl|;
DECL|method|decompressBytesDirect ()
specifier|private
specifier|native
name|int
name|decompressBytesDirect
parameter_list|()
function_decl|;
block|}
end_class

end_unit

