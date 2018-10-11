begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterOutputStream
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
name|OutputStream
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
name|security
operator|.
name|GeneralSecurityException
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|CanSetDropBehind
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
name|StreamCapabilities
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
name|Syncable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * CryptoOutputStream encrypts data. It is not thread-safe. AES CTR mode is  * required in order to ensure that the plain text and cipher text have a 1:1  * mapping. The encryption is buffer based. The key points of the encryption are  * (1) calculating counter and (2) padding through stream position.  *<p>  * counter = base + pos/(algorithm blocksize);   * padding = pos%(algorithm blocksize);   *<p>  * The underlying stream offset is maintained as state.  *  * Note that while some of this class' methods are synchronized, this is just to  * match the threadsafety behavior of DFSOutputStream. See HADOOP-11710.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|CryptoOutputStream
specifier|public
class|class
name|CryptoOutputStream
extends|extends
name|FilterOutputStream
implements|implements
name|Syncable
implements|,
name|CanSetDropBehind
implements|,
name|StreamCapabilities
block|{
DECL|field|oneByteBuf
specifier|private
specifier|final
name|byte
index|[]
name|oneByteBuf
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
DECL|field|codec
specifier|private
specifier|final
name|CryptoCodec
name|codec
decl_stmt|;
DECL|field|encryptor
specifier|private
specifier|final
name|Encryptor
name|encryptor
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
comment|/**    * Input data buffer. The data starts at inBuffer.position() and ends at     * inBuffer.limit().    */
DECL|field|inBuffer
specifier|private
name|ByteBuffer
name|inBuffer
decl_stmt|;
comment|/**    * Encrypted data buffer. The data starts at outBuffer.position() and ends at     * outBuffer.limit();    */
DECL|field|outBuffer
specifier|private
name|ByteBuffer
name|outBuffer
decl_stmt|;
DECL|field|streamOffset
specifier|private
name|long
name|streamOffset
init|=
literal|0
decl_stmt|;
comment|// Underlying stream offset.
comment|/**    * Padding = pos%(algorithm blocksize); Padding is put into {@link #inBuffer}     * before any other data goes in. The purpose of padding is to put input data    * at proper position.    */
DECL|field|padding
specifier|private
name|byte
name|padding
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|byte
index|[]
name|key
decl_stmt|;
DECL|field|initIV
specifier|private
specifier|final
name|byte
index|[]
name|initIV
decl_stmt|;
DECL|field|iv
specifier|private
name|byte
index|[]
name|iv
decl_stmt|;
DECL|field|closeOutputStream
specifier|private
name|boolean
name|closeOutputStream
decl_stmt|;
DECL|method|CryptoOutputStream (OutputStream out, CryptoCodec codec, int bufferSize, byte[] key, byte[] iv)
specifier|public
name|CryptoOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|CryptoCodec
name|codec
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|out
argument_list|,
name|codec
argument_list|,
name|bufferSize
argument_list|,
name|key
argument_list|,
name|iv
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|CryptoOutputStream (OutputStream out, CryptoCodec codec, int bufferSize, byte[] key, byte[] iv, long streamOffset)
specifier|public
name|CryptoOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|CryptoCodec
name|codec
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|,
name|long
name|streamOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|out
argument_list|,
name|codec
argument_list|,
name|bufferSize
argument_list|,
name|key
argument_list|,
name|iv
argument_list|,
name|streamOffset
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|CryptoOutputStream (OutputStream out, CryptoCodec codec, int bufferSize, byte[] key, byte[] iv, long streamOffset, boolean closeOutputStream)
specifier|public
name|CryptoOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|CryptoCodec
name|codec
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|,
name|long
name|streamOffset
parameter_list|,
name|boolean
name|closeOutputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|CryptoStreamUtils
operator|.
name|checkCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|CryptoStreamUtils
operator|.
name|checkBufferSize
argument_list|(
name|codec
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|codec
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|initIV
operator|=
name|iv
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|iv
operator|=
name|iv
operator|.
name|clone
argument_list|()
expr_stmt|;
name|inBuffer
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|this
operator|.
name|bufferSize
argument_list|)
expr_stmt|;
name|outBuffer
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|this
operator|.
name|bufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|streamOffset
operator|=
name|streamOffset
expr_stmt|;
name|this
operator|.
name|closeOutputStream
operator|=
name|closeOutputStream
expr_stmt|;
try|try
block|{
name|encryptor
operator|=
name|codec
operator|.
name|createEncryptor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GeneralSecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|updateEncryptor
argument_list|()
expr_stmt|;
block|}
DECL|method|CryptoOutputStream (OutputStream out, CryptoCodec codec, byte[] key, byte[] iv)
specifier|public
name|CryptoOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|CryptoCodec
name|codec
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|out
argument_list|,
name|codec
argument_list|,
name|key
argument_list|,
name|iv
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|CryptoOutputStream (OutputStream out, CryptoCodec codec, byte[] key, byte[] iv, long streamOffset)
specifier|public
name|CryptoOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|CryptoCodec
name|codec
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|,
name|long
name|streamOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|out
argument_list|,
name|codec
argument_list|,
name|key
argument_list|,
name|iv
argument_list|,
name|streamOffset
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|CryptoOutputStream (OutputStream out, CryptoCodec codec, byte[] key, byte[] iv, long streamOffset, boolean closeOutputStream)
specifier|public
name|CryptoOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|CryptoCodec
name|codec
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|,
name|long
name|streamOffset
parameter_list|,
name|boolean
name|closeOutputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|out
argument_list|,
name|codec
argument_list|,
name|CryptoStreamUtils
operator|.
name|getBufferSize
argument_list|(
name|codec
operator|.
name|getConf
argument_list|()
argument_list|)
argument_list|,
name|key
argument_list|,
name|iv
argument_list|,
name|streamOffset
argument_list|,
name|closeOutputStream
argument_list|)
expr_stmt|;
block|}
DECL|method|getWrappedStream ()
specifier|public
name|OutputStream
name|getWrappedStream
parameter_list|()
block|{
return|return
name|out
return|;
block|}
comment|/**    * Encryption is buffer based.    * If there is enough room in {@link #inBuffer}, then write to this buffer.    * If {@link #inBuffer} is full, then do encryption and write data to the    * underlying stream.    * @param b the data.    * @param off the start offset in the data.    * @param len the number of bytes to write.    * @throws IOException    */
annotation|@
name|Override
DECL|method|write (byte[] b, int off, int len)
specifier|public
specifier|synchronized
name|void
name|write
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
name|checkStream
argument_list|()
expr_stmt|;
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
elseif|else
if|if
condition|(
name|off
argument_list|<
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
operator|||
name|len
argument_list|>
name|b
operator|.
name|length
operator|-
name|off
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|remaining
init|=
name|inBuffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|<
name|remaining
condition|)
block|{
name|inBuffer
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
name|len
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|inBuffer
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|remaining
argument_list|)
expr_stmt|;
name|off
operator|+=
name|remaining
expr_stmt|;
name|len
operator|-=
name|remaining
expr_stmt|;
name|encrypt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Do the encryption, input is {@link #inBuffer} and output is     * {@link #outBuffer}.    */
DECL|method|encrypt ()
specifier|private
name|void
name|encrypt
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|inBuffer
operator|.
name|position
argument_list|()
operator|>=
name|padding
argument_list|)
expr_stmt|;
if|if
condition|(
name|inBuffer
operator|.
name|position
argument_list|()
operator|==
name|padding
condition|)
block|{
comment|// There is no real data in the inBuffer.
return|return;
block|}
name|inBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|outBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|encryptor
operator|.
name|encrypt
argument_list|(
name|inBuffer
argument_list|,
name|outBuffer
argument_list|)
expr_stmt|;
name|inBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|outBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
if|if
condition|(
name|padding
operator|>
literal|0
condition|)
block|{
comment|/*        * The plain text and cipher text have a 1:1 mapping, they start at the         * same position.        */
name|outBuffer
operator|.
name|position
argument_list|(
name|padding
argument_list|)
expr_stmt|;
name|padding
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|int
name|len
init|=
name|outBuffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
comment|/*      * If underlying stream supports {@link ByteBuffer} write in future, needs      * refine here.       */
specifier|final
name|byte
index|[]
name|tmp
init|=
name|getTmpBuf
argument_list|()
decl_stmt|;
name|outBuffer
operator|.
name|get
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|streamOffset
operator|+=
name|len
expr_stmt|;
if|if
condition|(
name|encryptor
operator|.
name|isContextReset
argument_list|()
condition|)
block|{
comment|/*        * This code is generally not executed since the encryptor usually        * maintains encryption context (e.g. the counter) internally. However,        * some implementations can't maintain context so a re-init is necessary        * after each encryption call.        */
name|updateEncryptor
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Update the {@link #encryptor}: calculate counter and {@link #padding}. */
DECL|method|updateEncryptor ()
specifier|private
name|void
name|updateEncryptor
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|long
name|counter
init|=
name|streamOffset
operator|/
name|codec
operator|.
name|getCipherSuite
argument_list|()
operator|.
name|getAlgorithmBlockSize
argument_list|()
decl_stmt|;
name|padding
operator|=
call|(
name|byte
call|)
argument_list|(
name|streamOffset
operator|%
name|codec
operator|.
name|getCipherSuite
argument_list|()
operator|.
name|getAlgorithmBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|inBuffer
operator|.
name|position
argument_list|(
name|padding
argument_list|)
expr_stmt|;
comment|// Set proper position for input data.
name|codec
operator|.
name|calculateIV
argument_list|(
name|initIV
argument_list|,
name|counter
argument_list|,
name|iv
argument_list|)
expr_stmt|;
name|encryptor
operator|.
name|init
argument_list|(
name|key
argument_list|,
name|iv
argument_list|)
expr_stmt|;
block|}
DECL|field|tmpBuf
specifier|private
name|byte
index|[]
name|tmpBuf
decl_stmt|;
DECL|method|getTmpBuf ()
specifier|private
name|byte
index|[]
name|getTmpBuf
parameter_list|()
block|{
if|if
condition|(
name|tmpBuf
operator|==
literal|null
condition|)
block|{
name|tmpBuf
operator|=
operator|new
name|byte
index|[
name|bufferSize
index|]
expr_stmt|;
block|}
return|return
name|tmpBuf
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
try|try
block|{
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|closeOutputStream
condition|)
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|codec
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|freeBuffers
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**    * To flush, we need to encrypt the data in the buffer and write to the     * underlying stream, then do the flush.    */
annotation|@
name|Override
DECL|method|flush ()
specifier|public
specifier|synchronized
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
name|encrypt
argument_list|()
expr_stmt|;
name|super
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (int b)
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|oneByteBuf
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|b
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|oneByteBuf
argument_list|,
literal|0
argument_list|,
name|oneByteBuf
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|checkStream ()
specifier|private
name|void
name|checkStream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Stream closed"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|setDropBehind (Boolean dropCache)
specifier|public
name|void
name|setDropBehind
parameter_list|(
name|Boolean
name|dropCache
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedOperationException
block|{
try|try
block|{
operator|(
operator|(
name|CanSetDropBehind
operator|)
name|out
operator|)
operator|.
name|setDropBehind
argument_list|(
name|dropCache
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This stream does not "
operator|+
literal|"support setting the drop-behind caching."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|hflush ()
specifier|public
name|void
name|hflush
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|out
operator|instanceof
name|Syncable
condition|)
block|{
operator|(
operator|(
name|Syncable
operator|)
name|out
operator|)
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|hsync ()
specifier|public
name|void
name|hsync
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|out
operator|instanceof
name|Syncable
condition|)
block|{
operator|(
operator|(
name|Syncable
operator|)
name|out
operator|)
operator|.
name|hsync
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Forcibly free the direct buffers. */
DECL|method|freeBuffers ()
specifier|private
name|void
name|freeBuffers
parameter_list|()
block|{
name|CryptoStreamUtils
operator|.
name|freeDB
argument_list|(
name|inBuffer
argument_list|)
expr_stmt|;
name|CryptoStreamUtils
operator|.
name|freeDB
argument_list|(
name|outBuffer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasCapability (String capability)
specifier|public
name|boolean
name|hasCapability
parameter_list|(
name|String
name|capability
parameter_list|)
block|{
if|if
condition|(
name|out
operator|instanceof
name|StreamCapabilities
condition|)
block|{
return|return
operator|(
operator|(
name|StreamCapabilities
operator|)
name|out
operator|)
operator|.
name|hasCapability
argument_list|(
name|capability
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

