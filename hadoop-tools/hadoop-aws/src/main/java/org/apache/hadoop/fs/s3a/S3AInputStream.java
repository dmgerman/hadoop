begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3Client
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|GetObjectRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|S3Object
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|S3ObjectInputStream
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
name|net
operator|.
name|SocketTimeoutException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
import|;
end_import

begin_class
DECL|class|S3AInputStream
specifier|public
class|class
name|S3AInputStream
extends|extends
name|FSInputStream
block|{
DECL|field|pos
specifier|private
name|long
name|pos
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|wrappedStream
specifier|private
name|S3ObjectInputStream
name|wrappedStream
decl_stmt|;
DECL|field|wrappedObject
specifier|private
name|S3Object
name|wrappedObject
decl_stmt|;
DECL|field|stats
specifier|private
name|FileSystem
operator|.
name|Statistics
name|stats
decl_stmt|;
DECL|field|client
specifier|private
name|AmazonS3Client
name|client
decl_stmt|;
DECL|field|bucket
specifier|private
name|String
name|bucket
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|contentLength
specifier|private
name|long
name|contentLength
decl_stmt|;
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|S3AFileSystem
operator|.
name|LOG
decl_stmt|;
DECL|method|S3AInputStream (String bucket, String key, long contentLength, AmazonS3Client client, FileSystem.Statistics stats)
specifier|public
name|S3AInputStream
parameter_list|(
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|,
name|long
name|contentLength
parameter_list|,
name|AmazonS3Client
name|client
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|stats
parameter_list|)
block|{
name|this
operator|.
name|bucket
operator|=
name|bucket
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|contentLength
operator|=
name|contentLength
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|pos
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|closed
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|wrappedObject
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|wrappedStream
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|openIfNeeded ()
specifier|private
name|void
name|openIfNeeded
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|wrappedObject
operator|==
literal|null
condition|)
block|{
name|reopen
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
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
name|abort
argument_list|()
expr_stmt|;
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
name|EOFException
argument_list|(
literal|"Trying to seek to a negative offset "
operator|+
name|pos
argument_list|)
throw|;
block|}
if|if
condition|(
name|contentLength
operator|>
literal|0
operator|&&
name|pos
operator|>
name|contentLength
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Trying to seek to an offset "
operator|+
name|pos
operator|+
literal|" past the end of the file"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Actually opening file "
operator|+
name|key
operator|+
literal|" at pos "
operator|+
name|pos
argument_list|)
expr_stmt|;
name|GetObjectRequest
name|request
init|=
operator|new
name|GetObjectRequest
argument_list|(
name|bucket
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
name|contentLength
operator|-
literal|1
argument_list|)
expr_stmt|;
name|wrappedObject
operator|=
name|client
operator|.
name|getObject
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|wrappedStream
operator|=
name|wrappedObject
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
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPos ()
specifier|public
specifier|synchronized
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
DECL|method|seek (long pos)
specifier|public
specifier|synchronized
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
name|this
operator|.
name|pos
operator|==
name|pos
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Reopening "
operator|+
name|this
operator|.
name|key
operator|+
literal|" to seek to new offset "
operator|+
operator|(
name|pos
operator|-
name|this
operator|.
name|pos
operator|)
argument_list|)
expr_stmt|;
name|reopen
argument_list|(
name|pos
argument_list|)
expr_stmt|;
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
return|return
literal|false
return|;
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
name|openIfNeeded
argument_list|()
expr_stmt|;
name|int
name|byteRead
decl_stmt|;
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
name|SocketTimeoutException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got timeout while trying to read from stream, trying to recover "
operator|+
name|e
argument_list|)
expr_stmt|;
name|reopen
argument_list|(
name|pos
argument_list|)
expr_stmt|;
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
name|SocketException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got socket exception while trying to read from stream, trying to recover "
operator|+
name|e
argument_list|)
expr_stmt|;
name|reopen
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|byteRead
operator|=
name|wrappedStream
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|byteRead
operator|>=
literal|0
condition|)
block|{
name|pos
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|stats
operator|!=
literal|null
operator|&&
name|byteRead
operator|>=
literal|0
condition|)
block|{
name|stats
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
annotation|@
name|Override
DECL|method|read (byte buf[], int off, int len)
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|(
name|byte
name|buf
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
name|openIfNeeded
argument_list|()
expr_stmt|;
name|int
name|byteRead
decl_stmt|;
try|try
block|{
name|byteRead
operator|=
name|wrappedStream
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got timeout while trying to read from stream, trying to recover "
operator|+
name|e
argument_list|)
expr_stmt|;
name|reopen
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|byteRead
operator|=
name|wrappedStream
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got socket exception while trying to read from stream, trying to recover "
operator|+
name|e
argument_list|)
expr_stmt|;
name|reopen
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|byteRead
operator|=
name|wrappedStream
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|byteRead
operator|>
literal|0
condition|)
block|{
name|pos
operator|+=
name|byteRead
expr_stmt|;
block|}
if|if
condition|(
name|stats
operator|!=
literal|null
operator|&&
name|byteRead
operator|>
literal|0
condition|)
block|{
name|stats
operator|.
name|incrementBytesRead
argument_list|(
name|byteRead
argument_list|)
expr_stmt|;
block|}
return|return
name|byteRead
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
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|wrappedObject
operator|!=
literal|null
condition|)
block|{
name|wrappedObject
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
name|long
name|remaining
init|=
name|this
operator|.
name|contentLength
operator|-
name|this
operator|.
name|pos
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
DECL|method|markSupported ()
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

