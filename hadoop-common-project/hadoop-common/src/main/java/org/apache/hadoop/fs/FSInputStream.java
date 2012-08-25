begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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

begin_comment
comment|/****************************************************************  * FSInputStream is a generic old InputStream with a little bit  * of RAF-style seek ability.  *  *****************************************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|FSInputStream
specifier|public
specifier|abstract
class|class
name|FSInputStream
extends|extends
name|InputStream
implements|implements
name|Seekable
implements|,
name|PositionedReadable
block|{
comment|/**    * Seek to the given offset from the start of the file.    * The next read() will be from that location.  Can't    * seek past the end of the file.    */
annotation|@
name|Override
DECL|method|seek (long pos)
specifier|public
specifier|abstract
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the current offset from the start of the file    */
annotation|@
name|Override
DECL|method|getPos ()
specifier|public
specifier|abstract
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Seeks a different copy of the data.  Returns true if     * found a new source, false otherwise.    */
annotation|@
name|Override
DECL|method|seekToNewSource (long targetPos)
specifier|public
specifier|abstract
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
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
synchronized|synchronized
init|(
name|this
init|)
block|{
name|long
name|oldPos
init|=
name|getPos
argument_list|()
decl_stmt|;
name|int
name|nread
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|nread
operator|=
name|read
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|seek
argument_list|(
name|oldPos
argument_list|)
expr_stmt|;
block|}
return|return
name|nread
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readFully (long position, byte[] buffer, int offset, int length)
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
name|int
name|nread
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|nread
operator|<
name|length
condition|)
block|{
name|int
name|nbytes
init|=
name|read
argument_list|(
name|position
operator|+
name|nread
argument_list|,
name|buffer
argument_list|,
name|offset
operator|+
name|nread
argument_list|,
name|length
operator|-
name|nread
argument_list|)
decl_stmt|;
if|if
condition|(
name|nbytes
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"End of file reached before reading fully."
argument_list|)
throw|;
block|}
name|nread
operator|+=
name|nbytes
expr_stmt|;
block|}
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
block|}
end_class

end_unit

