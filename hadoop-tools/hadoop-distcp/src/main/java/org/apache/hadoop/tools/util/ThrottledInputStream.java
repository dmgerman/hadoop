begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|util
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
name|hadoop
operator|.
name|fs
operator|.
name|FSDataInputStream
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
comment|/**  * The ThrottleInputStream provides bandwidth throttling on a specified  * InputStream. It is implemented as a wrapper on top of another InputStream  * instance.  * The throttling works by examining the number of bytes read from the underlying  * InputStream from the beginning, and sleep()ing for a time interval if  * the byte-transfer is found exceed the specified tolerable maximum.  * (Thus, while the read-rate might exceed the maximum for a given short interval,  * the average tends towards the specified maximum, overall.)  */
end_comment

begin_class
DECL|class|ThrottledInputStream
specifier|public
class|class
name|ThrottledInputStream
extends|extends
name|InputStream
block|{
DECL|field|rawStream
specifier|private
specifier|final
name|InputStream
name|rawStream
decl_stmt|;
DECL|field|maxBytesPerSec
specifier|private
specifier|final
name|long
name|maxBytesPerSec
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|bytesRead
specifier|private
name|long
name|bytesRead
init|=
literal|0
decl_stmt|;
DECL|field|totalSleepTime
specifier|private
name|long
name|totalSleepTime
init|=
literal|0
decl_stmt|;
DECL|field|SLEEP_DURATION_MS
specifier|private
specifier|static
specifier|final
name|long
name|SLEEP_DURATION_MS
init|=
literal|50
decl_stmt|;
DECL|method|ThrottledInputStream (InputStream rawStream)
specifier|public
name|ThrottledInputStream
parameter_list|(
name|InputStream
name|rawStream
parameter_list|)
block|{
name|this
argument_list|(
name|rawStream
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
DECL|method|ThrottledInputStream (InputStream rawStream, long maxBytesPerSec)
specifier|public
name|ThrottledInputStream
parameter_list|(
name|InputStream
name|rawStream
parameter_list|,
name|long
name|maxBytesPerSec
parameter_list|)
block|{
assert|assert
name|maxBytesPerSec
operator|>
literal|0
operator|:
literal|"Bandwidth "
operator|+
name|maxBytesPerSec
operator|+
literal|" is invalid"
assert|;
name|this
operator|.
name|rawStream
operator|=
name|rawStream
expr_stmt|;
name|this
operator|.
name|maxBytesPerSec
operator|=
name|maxBytesPerSec
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
name|rawStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
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
name|throttle
argument_list|()
expr_stmt|;
name|int
name|data
init|=
name|rawStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|data
operator|!=
operator|-
literal|1
condition|)
block|{
name|bytesRead
operator|++
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|read (byte[] b)
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|throttle
argument_list|()
expr_stmt|;
name|int
name|readLen
init|=
name|rawStream
operator|.
name|read
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|readLen
operator|!=
operator|-
literal|1
condition|)
block|{
name|bytesRead
operator|+=
name|readLen
expr_stmt|;
block|}
return|return
name|readLen
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|read (byte[] b, int off, int len)
specifier|public
name|int
name|read
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
name|throttle
argument_list|()
expr_stmt|;
name|int
name|readLen
init|=
name|rawStream
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|readLen
operator|!=
operator|-
literal|1
condition|)
block|{
name|bytesRead
operator|+=
name|readLen
expr_stmt|;
block|}
return|return
name|readLen
return|;
block|}
comment|/**    * Read bytes starting from the specified position. This requires rawStream is    * an instance of {@link PositionedReadable}.    */
DECL|method|read (long position, byte[] buffer, int offset, int length)
specifier|public
name|int
name|read
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|rawStream
operator|instanceof
name|PositionedReadable
operator|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"positioned read is not supported by the internal stream"
argument_list|)
throw|;
block|}
name|throttle
argument_list|()
expr_stmt|;
name|int
name|readLen
init|=
operator|(
operator|(
name|PositionedReadable
operator|)
name|rawStream
operator|)
operator|.
name|read
argument_list|(
name|position
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|readLen
operator|!=
operator|-
literal|1
condition|)
block|{
name|bytesRead
operator|+=
name|readLen
expr_stmt|;
block|}
return|return
name|readLen
return|;
block|}
DECL|method|throttle ()
specifier|private
name|void
name|throttle
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|getBytesPerSec
argument_list|()
operator|>
name|maxBytesPerSec
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|SLEEP_DURATION_MS
argument_list|)
expr_stmt|;
name|totalSleepTime
operator|+=
name|SLEEP_DURATION_MS
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Thread aborted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Getter for the number of bytes read from this stream, since creation.    * @return The number of bytes.    */
DECL|method|getTotalBytesRead ()
specifier|public
name|long
name|getTotalBytesRead
parameter_list|()
block|{
return|return
name|bytesRead
return|;
block|}
comment|/**    * Getter for the read-rate from this stream, since creation.    * Calculated as bytesRead/elapsedTimeSinceStart.    * @return Read rate, in bytes/sec.    */
DECL|method|getBytesPerSec ()
specifier|public
name|long
name|getBytesPerSec
parameter_list|()
block|{
name|long
name|elapsed
init|=
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
operator|/
literal|1000
decl_stmt|;
if|if
condition|(
name|elapsed
operator|==
literal|0
condition|)
block|{
return|return
name|bytesRead
return|;
block|}
else|else
block|{
return|return
name|bytesRead
operator|/
name|elapsed
return|;
block|}
block|}
comment|/**    * Getter the total time spent in sleep.    * @return Number of milliseconds spent in sleep.    */
DECL|method|getTotalSleepTime ()
specifier|public
name|long
name|getTotalSleepTime
parameter_list|()
block|{
return|return
name|totalSleepTime
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ThrottledInputStream{"
operator|+
literal|"bytesRead="
operator|+
name|bytesRead
operator|+
literal|", maxBytesPerSec="
operator|+
name|maxBytesPerSec
operator|+
literal|", bytesPerSec="
operator|+
name|getBytesPerSec
argument_list|()
operator|+
literal|", totalSleepTime="
operator|+
name|totalSleepTime
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

