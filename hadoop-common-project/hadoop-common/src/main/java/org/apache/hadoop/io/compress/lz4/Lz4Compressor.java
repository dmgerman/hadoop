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
name|io
operator|.
name|compress
operator|.
name|Compressor
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
comment|/**  * A {@link Compressor} based on the lz4 compression algorithm.  * http://code.google.com/p/lz4/  */
end_comment

begin_class
DECL|class|Lz4Compressor
specifier|public
class|class
name|Lz4Compressor
implements|implements
name|Compressor
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
name|Lz4Compressor
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
DECL|field|uncompressedDirectBufLen
specifier|private
name|int
name|uncompressedDirectBufLen
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
DECL|field|finish
DECL|field|finished
specifier|private
name|boolean
name|finish
decl_stmt|,
name|finished
decl_stmt|;
DECL|field|bytesRead
specifier|private
name|long
name|bytesRead
init|=
literal|0L
decl_stmt|;
DECL|field|bytesWritten
specifier|private
name|long
name|bytesWritten
init|=
literal|0L
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
DECL|method|Lz4Compressor (int directBufferSize)
specifier|public
name|Lz4Compressor
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
name|uncompressedDirectBuf
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|directBufferSize
argument_list|)
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
name|compressedDirectBuf
operator|.
name|position
argument_list|(
name|directBufferSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new compressor with the default buffer size.    */
DECL|method|Lz4Compressor ()
specifier|public
name|Lz4Compressor
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_DIRECT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets input data for compression.    * This should be called whenever #needsInput() returns    *<code>true</code> indicating that more input data is required.    *    * @param b   Input data    * @param off Start offset    * @param len Length    */
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
name|finished
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|len
operator|>
name|uncompressedDirectBuf
operator|.
name|remaining
argument_list|()
condition|)
block|{
comment|// save data; now !needsInput
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
block|}
else|else
block|{
operator|(
operator|(
name|ByteBuffer
operator|)
name|uncompressedDirectBuf
operator|)
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|uncompressedDirectBufLen
operator|=
name|uncompressedDirectBuf
operator|.
name|position
argument_list|()
expr_stmt|;
block|}
name|bytesRead
operator|+=
name|len
expr_stmt|;
block|}
comment|/**    * If a write would exceed the capacity of the direct buffers, it is set    * aside to be loaded by this function while the compressed data are    * consumed.    */
DECL|method|setInputFromSavedData ()
specifier|synchronized
name|void
name|setInputFromSavedData
parameter_list|()
block|{
if|if
condition|(
literal|0
operator|>=
name|userBufLen
condition|)
block|{
return|return;
block|}
name|finished
operator|=
literal|false
expr_stmt|;
name|uncompressedDirectBufLen
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
operator|(
operator|(
name|ByteBuffer
operator|)
name|uncompressedDirectBuf
operator|)
operator|.
name|put
argument_list|(
name|userBuf
argument_list|,
name|userBufOff
argument_list|,
name|uncompressedDirectBufLen
argument_list|)
expr_stmt|;
comment|// Note how much data is being fed to lz4
name|userBufOff
operator|+=
name|uncompressedDirectBufLen
expr_stmt|;
name|userBufLen
operator|-=
name|uncompressedDirectBufLen
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
comment|/**    * Returns true if the input data buffer is empty and    * #setInput() should be called to provide more input.    *    * @return<code>true</code> if the input data buffer is empty and    *         #setInput() should be called in order to provide more input.    */
annotation|@
name|Override
DECL|method|needsInput ()
specifier|public
specifier|synchronized
name|boolean
name|needsInput
parameter_list|()
block|{
return|return
operator|!
operator|(
name|compressedDirectBuf
operator|.
name|remaining
argument_list|()
operator|>
literal|0
operator|||
name|uncompressedDirectBuf
operator|.
name|remaining
argument_list|()
operator|==
literal|0
operator|||
name|userBufLen
operator|>
literal|0
operator|)
return|;
block|}
comment|/**    * When called, indicates that compression should end    * with the current contents of the input buffer.    */
annotation|@
name|Override
DECL|method|finish ()
specifier|public
specifier|synchronized
name|void
name|finish
parameter_list|()
block|{
name|finish
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Returns true if the end of the compressed    * data output stream has been reached.    *    * @return<code>true</code> if the end of the compressed    *         data output stream has been reached.    */
annotation|@
name|Override
DECL|method|finished ()
specifier|public
specifier|synchronized
name|boolean
name|finished
parameter_list|()
block|{
comment|// Check if all uncompressed data has been consumed
return|return
operator|(
name|finish
operator|&&
name|finished
operator|&&
name|compressedDirectBuf
operator|.
name|remaining
argument_list|()
operator|==
literal|0
operator|)
return|;
block|}
comment|/**    * Fills specified buffer with compressed data. Returns actual number    * of bytes of compressed data. A return value of 0 indicates that    * needsInput() should be called in order to determine if more input    * data is required.    *    * @param b   Buffer for the compressed data    * @param off Start offset of the data    * @param len Size of the buffer    * @return The actual number of bytes of compressed data.    */
annotation|@
name|Override
DECL|method|compress (byte[] b, int off, int len)
specifier|public
specifier|synchronized
name|int
name|compress
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
comment|// Check if there is compressed data
name|int
name|n
init|=
name|compressedDirectBuf
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
name|compressedDirectBuf
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
name|bytesWritten
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
comment|// Re-initialize the lz4's output direct-buffer
name|compressedDirectBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|compressedDirectBuf
operator|.
name|limit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|uncompressedDirectBuf
operator|.
name|position
argument_list|()
condition|)
block|{
comment|// No compressed data, so we should have !needsInput or !finished
name|setInputFromSavedData
argument_list|()
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|uncompressedDirectBuf
operator|.
name|position
argument_list|()
condition|)
block|{
comment|// Called without data; write nothing
name|finished
operator|=
literal|true
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
comment|// Compress data
name|n
operator|=
name|compressBytesDirect
argument_list|()
expr_stmt|;
name|compressedDirectBuf
operator|.
name|limit
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|uncompressedDirectBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// lz4 consumes all buffer input
comment|// Set 'finished' if snapy has consumed all user-data
if|if
condition|(
literal|0
operator|==
name|userBufLen
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
name|bytesWritten
operator|+=
name|n
expr_stmt|;
operator|(
operator|(
name|ByteBuffer
operator|)
name|compressedDirectBuf
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
comment|/**    * Resets compressor so that a new set of input data can be processed.    */
annotation|@
name|Override
DECL|method|reset ()
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|finish
operator|=
literal|false
expr_stmt|;
name|finished
operator|=
literal|false
expr_stmt|;
name|uncompressedDirectBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uncompressedDirectBufLen
operator|=
literal|0
expr_stmt|;
name|compressedDirectBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|compressedDirectBuf
operator|.
name|limit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|userBufOff
operator|=
name|userBufLen
operator|=
literal|0
expr_stmt|;
name|bytesRead
operator|=
name|bytesWritten
operator|=
literal|0L
expr_stmt|;
block|}
comment|/**    * Prepare the compressor to be used in a new stream with settings defined in    * the given Configuration    *    * @param conf Configuration from which new setting are fetched    */
annotation|@
name|Override
DECL|method|reinit (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|reinit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**    * Return number of bytes given to this compressor since last reset.    */
annotation|@
name|Override
DECL|method|getBytesRead ()
specifier|public
specifier|synchronized
name|long
name|getBytesRead
parameter_list|()
block|{
return|return
name|bytesRead
return|;
block|}
comment|/**    * Return number of bytes consumed by callers of compress since last reset.    */
annotation|@
name|Override
DECL|method|getBytesWritten ()
specifier|public
specifier|synchronized
name|long
name|getBytesWritten
parameter_list|()
block|{
return|return
name|bytesWritten
return|;
block|}
comment|/**    * Closes the compressor and discards any unprocessed input.    */
annotation|@
name|Override
DECL|method|end ()
specifier|public
specifier|synchronized
name|void
name|end
parameter_list|()
block|{   }
DECL|method|initIDs ()
specifier|private
specifier|native
specifier|static
name|void
name|initIDs
parameter_list|()
function_decl|;
DECL|method|compressBytesDirect ()
specifier|private
specifier|native
name|int
name|compressBytesDirect
parameter_list|()
function_decl|;
block|}
end_class

end_unit

