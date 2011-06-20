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
name|xerial
operator|.
name|snappy
operator|.
name|Snappy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xerial
operator|.
name|snappy
operator|.
name|SnappyException
import|;
end_import

begin_class
DECL|class|SnappyCompressor
specifier|public
class|class
name|SnappyCompressor
implements|implements
name|Compressor
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Log
name|logger
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SnappyCompressor
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|finish
DECL|field|finished
specifier|private
name|boolean
name|finish
decl_stmt|,
name|finished
decl_stmt|;
DECL|field|outBuf
specifier|private
name|ByteBuffer
name|outBuf
decl_stmt|;
DECL|field|compressedBuf
specifier|private
name|ByteBuffer
name|compressedBuf
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
DECL|method|SnappyCompressor (int bufferSize)
specifier|public
name|SnappyCompressor
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
name|outBuf
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|compressedBuf
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|Snappy
operator|.
name|maxCompressedLength
argument_list|(
name|bufferSize
argument_list|)
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
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
name|outBuf
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
name|bytesRead
operator|+=
name|len
expr_stmt|;
block|}
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
DECL|method|needsInput ()
specifier|public
specifier|synchronized
name|boolean
name|needsInput
parameter_list|()
block|{
comment|// needs input if compressed data was consumed
if|if
condition|(
name|compressedBuf
operator|.
name|position
argument_list|()
operator|>
literal|0
operator|&&
name|compressedBuf
operator|.
name|limit
argument_list|()
operator|>
name|compressedBuf
operator|.
name|position
argument_list|()
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
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
DECL|method|finished ()
specifier|public
specifier|synchronized
name|boolean
name|finished
parameter_list|()
block|{
comment|// Check if all compressed data has been consumed
return|return
operator|(
name|finish
operator|&&
name|finished
operator|)
return|;
block|}
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
if|if
condition|(
name|finished
operator|||
name|outBuf
operator|.
name|position
argument_list|()
operator|==
literal|0
condition|)
block|{
name|finished
operator|=
literal|true
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|// Only need todo this once
if|if
condition|(
name|compressedBuf
operator|.
name|position
argument_list|()
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|outBuf
operator|.
name|limit
argument_list|(
name|outBuf
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|outBuf
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|int
name|lim
init|=
name|Snappy
operator|.
name|compress
argument_list|(
name|outBuf
argument_list|,
name|compressedBuf
argument_list|)
decl_stmt|;
name|compressedBuf
operator|.
name|limit
argument_list|(
name|lim
argument_list|)
expr_stmt|;
name|compressedBuf
operator|.
name|rewind
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SnappyException
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
block|}
name|int
name|n
init|=
operator|(
name|compressedBuf
operator|.
name|limit
argument_list|()
operator|-
name|compressedBuf
operator|.
name|position
argument_list|()
operator|)
operator|>
name|len
condition|?
name|len
else|:
operator|(
name|compressedBuf
operator|.
name|limit
argument_list|()
operator|-
name|compressedBuf
operator|.
name|position
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
name|finished
operator|=
literal|true
expr_stmt|;
return|return
literal|0
return|;
block|}
name|compressedBuf
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
comment|// Set 'finished' if snappy has consumed all user-data
if|if
condition|(
name|compressedBuf
operator|.
name|position
argument_list|()
operator|==
name|compressedBuf
operator|.
name|limit
argument_list|()
condition|)
block|{
name|finished
operator|=
literal|true
expr_stmt|;
name|outBuf
operator|.
name|limit
argument_list|(
name|outBuf
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|outBuf
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|compressedBuf
operator|.
name|limit
argument_list|(
name|compressedBuf
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|compressedBuf
operator|.
name|rewind
argument_list|()
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
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
name|outBuf
operator|.
name|limit
argument_list|(
name|outBuf
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|outBuf
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|compressedBuf
operator|.
name|limit
argument_list|(
name|compressedBuf
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|compressedBuf
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|bytesRead
operator|=
name|bytesWritten
operator|=
literal|0L
expr_stmt|;
block|}
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
DECL|method|end ()
specifier|public
specifier|synchronized
name|void
name|end
parameter_list|()
block|{   }
block|}
end_class

end_unit

