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
name|EOFException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileDescriptor
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
name|util
operator|.
name|EnumSet
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
name|ByteBufferReadable
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
name|CanSetReadahead
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
name|CanUnbuffer
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
name|HasEnhancedByteBufferAccess
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
name|HasFileDescriptor
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
name|PositionedReadable
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
name|ReadOption
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
name|Seekable
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|ByteBufferPool
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|Assert
operator|.
name|assertFalse
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

begin_class
DECL|class|TestCryptoStreams
specifier|public
class|class
name|TestCryptoStreams
extends|extends
name|CryptoStreamsTestBase
block|{
comment|/**    * Data storage.    * {@link #getOutputStream(int)} will write to this buf.    * {@link #getInputStream(int)} will read from this buf.    */
DECL|field|buf
specifier|private
name|byte
index|[]
name|buf
decl_stmt|;
DECL|field|bufLen
specifier|private
name|int
name|bufLen
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
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
name|codec
operator|=
name|CryptoCodec
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Override
DECL|method|getOutputStream (int bufferSize, byte[] key, byte[] iv)
specifier|protected
name|OutputStream
name|getOutputStream
parameter_list|(
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
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|buf
operator|=
name|getData
argument_list|()
expr_stmt|;
name|bufLen
operator|=
name|getLength
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|buf
operator|=
name|getData
argument_list|()
expr_stmt|;
name|bufLen
operator|=
name|getLength
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|CryptoOutputStream
argument_list|(
operator|new
name|FakeOutputStream
argument_list|(
name|out
argument_list|)
argument_list|,
name|codec
argument_list|,
name|bufferSize
argument_list|,
name|key
argument_list|,
name|iv
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getInputStream (int bufferSize, byte[] key, byte[] iv)
specifier|protected
name|InputStream
name|getInputStream
parameter_list|(
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
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|bufLen
argument_list|)
expr_stmt|;
return|return
operator|new
name|CryptoInputStream
argument_list|(
operator|new
name|FakeInputStream
argument_list|(
name|in
argument_list|)
argument_list|,
name|codec
argument_list|,
name|bufferSize
argument_list|,
name|key
argument_list|,
name|iv
argument_list|)
return|;
block|}
DECL|class|FakeOutputStream
specifier|private
class|class
name|FakeOutputStream
extends|extends
name|OutputStream
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
DECL|field|out
specifier|private
specifier|final
name|DataOutputBuffer
name|out
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|method|FakeOutputStream (DataOutputBuffer out)
specifier|public
name|FakeOutputStream
parameter_list|(
name|DataOutputBuffer
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (byte b[], int off, int len)
specifier|public
name|void
name|write
parameter_list|(
name|byte
name|b
index|[]
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
elseif|else
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
elseif|else
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|checkStream
argument_list|()
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|checkStream
argument_list|()
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
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
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
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
block|{     }
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
name|checkStream
argument_list|()
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
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
name|checkStream
argument_list|()
expr_stmt|;
name|flush
argument_list|()
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
switch|switch
condition|(
name|capability
operator|.
name|toLowerCase
argument_list|()
condition|)
block|{
case|case
name|StreamCapabilities
operator|.
name|HFLUSH
case|:
case|case
name|StreamCapabilities
operator|.
name|HSYNC
case|:
case|case
name|StreamCapabilities
operator|.
name|DROPBEHIND
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
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
literal|"Stream is closed!"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|FakeInputStream
specifier|static
class|class
name|FakeInputStream
extends|extends
name|InputStream
implements|implements
name|Seekable
implements|,
name|PositionedReadable
implements|,
name|ByteBufferReadable
implements|,
name|HasFileDescriptor
implements|,
name|CanSetDropBehind
implements|,
name|CanSetReadahead
implements|,
name|HasEnhancedByteBufferAccess
implements|,
name|CanUnbuffer
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
DECL|field|pos
specifier|private
name|int
name|pos
init|=
literal|0
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|FakeInputStream (DataInputBuffer in)
name|FakeInputStream
parameter_list|(
name|DataInputBuffer
name|in
parameter_list|)
block|{
name|data
operator|=
name|in
operator|.
name|getData
argument_list|()
expr_stmt|;
name|length
operator|=
name|in
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seek (long pos)
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot seek after EOF."
argument_list|)
throw|;
block|}
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot seek to negative offset."
argument_list|)
throw|;
block|}
name|checkStream
argument_list|()
expr_stmt|;
name|this
operator|.
name|pos
operator|=
operator|(
name|int
operator|)
name|pos
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPos ()
specifier|public
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|available ()
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|length
operator|-
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte b[], int off, int len)
specifier|public
name|int
name|read
parameter_list|(
name|byte
name|b
index|[]
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
elseif|else
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
elseif|else
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|checkStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|<
name|length
condition|)
block|{
name|int
name|n
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|length
operator|-
name|pos
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
return|return
operator|-
literal|1
return|;
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
literal|"Stream is closed!"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|read (ByteBuffer buf)
specifier|public
name|int
name|read
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
name|checkStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|<
name|length
condition|)
block|{
name|int
name|n
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|buf
operator|.
name|remaining
argument_list|()
argument_list|,
name|length
operator|-
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|put
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|pos
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|skip (long n)
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|checkStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|n
operator|+
name|pos
operator|>
name|length
condition|)
block|{
name|n
operator|=
name|length
operator|-
name|pos
expr_stmt|;
block|}
name|pos
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
return|return
name|n
operator|<
literal|0
condition|?
operator|-
literal|1
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read (long position, byte[] b, int off, int len)
specifier|public
name|int
name|read
parameter_list|(
name|long
name|position
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
elseif|else
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
elseif|else
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|position
operator|>
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot read after EOF."
argument_list|)
throw|;
block|}
if|if
condition|(
name|position
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot read to negative offset."
argument_list|)
throw|;
block|}
name|checkStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|position
operator|<
name|length
condition|)
block|{
name|int
name|n
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|length
operator|-
name|position
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
operator|(
name|int
operator|)
name|position
argument_list|,
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
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|readFully (long position, byte[] b, int off, int len)
specifier|public
name|void
name|readFully
parameter_list|(
name|long
name|position
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
elseif|else
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
elseif|else
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|position
operator|>
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot read after EOF."
argument_list|)
throw|;
block|}
if|if
condition|(
name|position
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot read to negative offset."
argument_list|)
throw|;
block|}
name|checkStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|position
operator|+
name|len
operator|>
name|length
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Reach the end of stream."
argument_list|)
throw|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
operator|(
name|int
operator|)
name|position
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFully (long position, byte[] buffer)
specifier|public
name|void
name|readFully
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|readFully
argument_list|(
name|position
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read (ByteBufferPool bufferPool, int maxLength, EnumSet<ReadOption> opts)
specifier|public
name|ByteBuffer
name|read
parameter_list|(
name|ByteBufferPool
name|bufferPool
parameter_list|,
name|int
name|maxLength
parameter_list|,
name|EnumSet
argument_list|<
name|ReadOption
argument_list|>
name|opts
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedOperationException
block|{
if|if
condition|(
name|bufferPool
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Please specify buffer pool."
argument_list|)
throw|;
block|}
name|ByteBuffer
name|buffer
init|=
name|bufferPool
operator|.
name|getBuffer
argument_list|(
literal|true
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
name|buffer
operator|.
name|position
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|>=
literal|0
condition|)
block|{
name|buffer
operator|.
name|position
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|releaseBuffer (ByteBuffer buffer)
specifier|public
name|void
name|releaseBuffer
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{            }
annotation|@
name|Override
DECL|method|setReadahead (Long readahead)
specifier|public
name|void
name|setReadahead
parameter_list|(
name|Long
name|readahead
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedOperationException
block|{     }
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
block|{     }
annotation|@
name|Override
DECL|method|unbuffer ()
specifier|public
name|void
name|unbuffer
parameter_list|()
block|{     }
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
switch|switch
condition|(
name|capability
operator|.
name|toLowerCase
argument_list|()
condition|)
block|{
case|case
name|StreamCapabilities
operator|.
name|READAHEAD
case|:
case|case
name|StreamCapabilities
operator|.
name|DROPBEHIND
case|:
case|case
name|StreamCapabilities
operator|.
name|UNBUFFER
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFileDescriptor ()
specifier|public
name|FileDescriptor
name|getFileDescriptor
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|seekToNewSource (long targetPos)
specifier|public
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|targetPos
operator|>
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attempted to read past end of file."
argument_list|)
throw|;
block|}
if|if
condition|(
name|targetPos
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot seek after EOF."
argument_list|)
throw|;
block|}
name|checkStream
argument_list|()
expr_stmt|;
name|this
operator|.
name|pos
operator|=
operator|(
name|int
operator|)
name|targetPos
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|ret
init|=
name|read
argument_list|(
name|oneByteBuf
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
return|return
operator|(
name|ret
operator|<=
literal|0
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
name|oneByteBuf
index|[
literal|0
index|]
operator|&
literal|0xff
operator|)
return|;
block|}
block|}
comment|/**    * This tests {@link StreamCapabilities#hasCapability(String)} for the    * the underlying streams.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testHasCapability ()
specifier|public
name|void
name|testHasCapability
parameter_list|()
throws|throws
name|Exception
block|{
comment|// verify hasCapability returns what FakeOutputStream is set up for
name|CryptoOutputStream
name|cos
init|=
operator|(
name|CryptoOutputStream
operator|)
name|getOutputStream
argument_list|(
name|defaultBufferSize
argument_list|,
name|key
argument_list|,
name|iv
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cos
operator|instanceof
name|StreamCapabilities
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cos
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|HFLUSH
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cos
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|HSYNC
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cos
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|DROPBEHIND
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cos
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|READAHEAD
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cos
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|UNBUFFER
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify hasCapability for input stream
name|CryptoInputStream
name|cis
init|=
operator|(
name|CryptoInputStream
operator|)
name|getInputStream
argument_list|(
name|defaultBufferSize
argument_list|,
name|key
argument_list|,
name|iv
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cis
operator|instanceof
name|StreamCapabilities
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cis
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|DROPBEHIND
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cis
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|READAHEAD
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cis
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|UNBUFFER
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cis
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|HFLUSH
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cis
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|HSYNC
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

