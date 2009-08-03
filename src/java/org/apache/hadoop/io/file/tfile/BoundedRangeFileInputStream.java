begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.file.tfile
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|file
operator|.
name|tfile
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

begin_comment
comment|/**  * BoundedRangeFIleInputStream abstracts a contiguous region of a Hadoop  * FSDataInputStream as a regular input stream. One can create multiple  * BoundedRangeFileInputStream on top of the same FSDataInputStream and they  * would not interfere with each other.  */
end_comment

begin_class
DECL|class|BoundedRangeFileInputStream
class|class
name|BoundedRangeFileInputStream
extends|extends
name|InputStream
block|{
DECL|field|in
specifier|private
name|FSDataInputStream
name|in
decl_stmt|;
DECL|field|pos
specifier|private
name|long
name|pos
decl_stmt|;
DECL|field|end
specifier|private
name|long
name|end
decl_stmt|;
DECL|field|mark
specifier|private
name|long
name|mark
decl_stmt|;
DECL|field|oneByte
specifier|private
specifier|final
name|byte
index|[]
name|oneByte
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
comment|/**    * Constructor    *     * @param in    *          The FSDataInputStream we connect to.    * @param offset    *          Begining offset of the region.    * @param length    *          Length of the region.    *     *          The actual length of the region may be smaller if (off_begin +    *          length) goes beyond the end of FS input stream.    */
DECL|method|BoundedRangeFileInputStream (FSDataInputStream in, long offset, long length)
specifier|public
name|BoundedRangeFileInputStream
parameter_list|(
name|FSDataInputStream
name|in
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|<
literal|0
operator|||
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"Invalid offset/length: "
operator|+
name|offset
operator|+
literal|"/"
operator|+
name|length
argument_list|)
throw|;
block|}
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|offset
operator|+
name|length
expr_stmt|;
name|this
operator|.
name|mark
operator|=
operator|-
literal|1
expr_stmt|;
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
name|int
name|avail
init|=
name|in
operator|.
name|available
argument_list|()
decl_stmt|;
if|if
condition|(
name|pos
operator|+
name|avail
operator|>
name|end
condition|)
block|{
name|avail
operator|=
call|(
name|int
call|)
argument_list|(
name|end
operator|-
name|pos
argument_list|)
expr_stmt|;
block|}
return|return
name|avail
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
name|oneByte
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|1
condition|)
return|return
name|oneByte
index|[
literal|0
index|]
operator|&
literal|0xff
return|;
return|return
operator|-
literal|1
return|;
block|}
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
return|return
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
return|;
block|}
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
if|if
condition|(
operator|(
name|off
operator||
name|len
operator||
operator|(
name|off
operator|+
name|len
operator|)
operator||
operator|(
name|b
operator|.
name|length
operator|-
operator|(
name|off
operator|+
name|len
operator|)
operator|)
operator|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
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
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
operator|(
name|end
operator|-
name|pos
operator|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|ret
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|in
init|)
block|{
name|in
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|ret
operator|=
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ret
operator|<
literal|0
condition|)
block|{
name|end
operator|=
name|pos
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|pos
operator|+=
name|ret
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
comment|/*    * We may skip beyond the end of the file.    */
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
name|long
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|end
operator|-
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
return|return
name|len
return|;
block|}
annotation|@
name|Override
DECL|method|mark (int readlimit)
specifier|public
specifier|synchronized
name|void
name|mark
parameter_list|(
name|int
name|readlimit
parameter_list|)
block|{
name|mark
operator|=
name|pos
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset ()
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|mark
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resetting to invalid mark"
argument_list|)
throw|;
name|pos
operator|=
name|mark
expr_stmt|;
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
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// Invalidate the state of the stream.
name|in
operator|=
literal|null
expr_stmt|;
name|pos
operator|=
name|end
expr_stmt|;
name|mark
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
end_class

end_unit

