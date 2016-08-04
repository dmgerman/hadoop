begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
package|;
end_package

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
name|aliyun
operator|.
name|oss
operator|.
name|Constants
operator|.
name|*
import|;
end_import

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
name|fs
operator|.
name|FSInputStream
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
name|FileSystem
operator|.
name|Statistics
import|;
end_import

begin_import
import|import
name|com
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|OSSClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|aliyun
operator|.
name|oss
operator|.
name|model
operator|.
name|GetObjectRequest
import|;
end_import

begin_comment
comment|/**  * The input stream for OSS blob system.  * The class uses multi-part downloading to read data from the object content  * stream.  */
end_comment

begin_class
DECL|class|AliyunOSSInputStream
specifier|public
class|class
name|AliyunOSSInputStream
extends|extends
name|FSInputStream
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AliyunOSSInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MAX_RETRIES
specifier|private
specifier|static
specifier|final
name|int
name|MAX_RETRIES
init|=
literal|10
decl_stmt|;
DECL|field|downloadPartSize
specifier|private
specifier|final
name|long
name|downloadPartSize
decl_stmt|;
DECL|field|bucketName
specifier|private
name|String
name|bucketName
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|ossClient
specifier|private
name|OSSClient
name|ossClient
decl_stmt|;
DECL|field|statistics
specifier|private
name|Statistics
name|statistics
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|wrappedStream
specifier|private
name|InputStream
name|wrappedStream
init|=
literal|null
decl_stmt|;
DECL|field|dataLen
specifier|private
name|long
name|dataLen
decl_stmt|;
DECL|field|position
specifier|private
name|long
name|position
decl_stmt|;
DECL|field|partRemaining
specifier|private
name|long
name|partRemaining
decl_stmt|;
DECL|method|AliyunOSSInputStream (Configuration conf, OSSClient client, String bucketName, String key, Long dataLen, Statistics statistics)
specifier|public
name|AliyunOSSInputStream
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|OSSClient
name|client
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|key
parameter_list|,
name|Long
name|dataLen
parameter_list|,
name|Statistics
name|statistics
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|bucketName
operator|=
name|bucketName
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|ossClient
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|statistics
operator|=
name|statistics
expr_stmt|;
name|this
operator|.
name|dataLen
operator|=
name|dataLen
expr_stmt|;
name|downloadPartSize
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|MULTIPART_DOWNLOAD_SIZE_KEY
argument_list|,
name|MULTIPART_DOWNLOAD_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|reopen
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Reopen the wrapped stream at give position, by seeking for    * data of a part length from object content stream.    *    * @param pos position from start of a file    * @throws IOException if failed to reopen    */
DECL|method|reopen (long pos)
specifier|private
specifier|synchronized
name|void
name|reopen
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|partLen
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Cannot seek at negtive position:"
operator|+
name|pos
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|>
name|dataLen
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Cannot seek after EOF, fileLen:"
operator|+
name|dataLen
operator|+
literal|" position:"
operator|+
name|pos
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|+
name|downloadPartSize
operator|>
name|dataLen
condition|)
block|{
name|partLen
operator|=
name|dataLen
operator|-
name|pos
expr_stmt|;
block|}
else|else
block|{
name|partLen
operator|=
name|downloadPartSize
expr_stmt|;
block|}
if|if
condition|(
name|wrappedStream
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Aborting old stream to open at pos "
operator|+
name|pos
argument_list|)
expr_stmt|;
block|}
name|wrappedStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|GetObjectRequest
name|request
init|=
operator|new
name|GetObjectRequest
argument_list|(
name|bucketName
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|request
operator|.
name|setRange
argument_list|(
name|pos
argument_list|,
name|pos
operator|+
name|partLen
operator|-
literal|1
argument_list|)
expr_stmt|;
name|wrappedStream
operator|=
name|ossClient
operator|.
name|getObject
argument_list|(
name|request
argument_list|)
operator|.
name|getObjectContent
argument_list|()
expr_stmt|;
if|if
condition|(
name|wrappedStream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Null IO stream"
argument_list|)
throw|;
block|}
name|position
operator|=
name|pos
expr_stmt|;
name|partRemaining
operator|=
name|partLen
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|partRemaining
operator|<=
literal|0
operator|&&
name|position
operator|<
name|dataLen
condition|)
block|{
name|reopen
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
name|int
name|tries
init|=
name|MAX_RETRIES
decl_stmt|;
name|boolean
name|retry
decl_stmt|;
name|int
name|byteRead
init|=
operator|-
literal|1
decl_stmt|;
do|do
block|{
name|retry
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|byteRead
operator|=
name|wrappedStream
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|handleReadException
argument_list|(
name|e
argument_list|,
operator|--
name|tries
argument_list|)
expr_stmt|;
name|retry
operator|=
literal|true
expr_stmt|;
block|}
block|}
do|while
condition|(
name|retry
condition|)
do|;
if|if
condition|(
name|byteRead
operator|>=
literal|0
condition|)
block|{
name|position
operator|++
expr_stmt|;
name|partRemaining
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|statistics
operator|!=
literal|null
operator|&&
name|byteRead
operator|>=
literal|0
condition|)
block|{
name|statistics
operator|.
name|incrementBytesRead
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|byteRead
return|;
block|}
comment|/**    * Check whether the input stream is closed.    *    * @throws IOException if stream is closed    */
DECL|method|checkNotClosed ()
specifier|private
name|void
name|checkNotClosed
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
DECL|method|read (byte[] buf, int off, int len)
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|(
name|byte
index|[]
name|buf
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
name|checkNotClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|buf
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
name|buf
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
name|int
name|bytesRead
init|=
literal|0
decl_stmt|;
comment|// Not EOF, and read not done
while|while
condition|(
name|position
operator|<
name|dataLen
operator|&&
name|bytesRead
operator|<
name|len
condition|)
block|{
if|if
condition|(
name|partRemaining
operator|==
literal|0
condition|)
block|{
name|reopen
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
name|int
name|tries
init|=
name|MAX_RETRIES
decl_stmt|;
name|boolean
name|retry
decl_stmt|;
name|int
name|bytes
init|=
operator|-
literal|1
decl_stmt|;
do|do
block|{
name|retry
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|bytes
operator|=
name|wrappedStream
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
operator|+
name|bytesRead
argument_list|,
name|len
operator|-
name|bytesRead
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|handleReadException
argument_list|(
name|e
argument_list|,
operator|--
name|tries
argument_list|)
expr_stmt|;
name|retry
operator|=
literal|true
expr_stmt|;
block|}
block|}
do|while
condition|(
name|retry
condition|)
do|;
if|if
condition|(
name|bytes
operator|>
literal|0
condition|)
block|{
name|bytesRead
operator|+=
name|bytes
expr_stmt|;
name|position
operator|+=
name|bytes
expr_stmt|;
name|partRemaining
operator|-=
name|bytes
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|partRemaining
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to read from stream. Remaining:"
operator|+
name|partRemaining
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|statistics
operator|!=
literal|null
operator|&&
name|bytesRead
operator|>
literal|0
condition|)
block|{
name|statistics
operator|.
name|incrementBytesRead
argument_list|(
name|bytesRead
argument_list|)
expr_stmt|;
block|}
comment|// Read nothing, but attempt to read something
if|if
condition|(
name|bytesRead
operator|==
literal|0
operator|&&
name|len
operator|>
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|bytesRead
return|;
block|}
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
name|closed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|wrappedStream
operator|!=
literal|null
condition|)
block|{
name|wrappedStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|available ()
specifier|public
specifier|synchronized
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
name|long
name|remaining
init|=
name|dataLen
operator|-
name|position
decl_stmt|;
if|if
condition|(
name|remaining
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
return|return
operator|(
name|int
operator|)
name|remaining
return|;
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
name|checkNotClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|position
operator|==
name|pos
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|pos
operator|>
name|position
operator|&&
name|pos
operator|<
name|position
operator|+
name|partRemaining
condition|)
block|{
name|wrappedStream
operator|.
name|skip
argument_list|(
name|pos
operator|-
name|position
argument_list|)
expr_stmt|;
name|position
operator|=
name|pos
expr_stmt|;
block|}
else|else
block|{
name|reopen
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
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
name|checkNotClosed
argument_list|()
expr_stmt|;
return|return
name|position
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
name|checkNotClosed
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|handleReadException (Exception e, int tries)
specifier|private
name|void
name|handleReadException
parameter_list|(
name|Exception
name|e
parameter_list|,
name|int
name|tries
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tries
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"Some exceptions occurred in oss connection, try to reopen oss"
operator|+
literal|" connection at position '"
operator|+
name|position
operator|+
literal|"', "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e2
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e2
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reopen
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

