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
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * A byte array backed output stream with a limit. The limit should be smaller  * than the buffer capacity. The object can be reused through<code>reset</code>  * API and choose different limits in each round.  */
end_comment

begin_class
DECL|class|BoundedByteArrayOutputStream
class|class
name|BoundedByteArrayOutputStream
extends|extends
name|OutputStream
block|{
DECL|field|buffer
specifier|private
specifier|final
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|method|BoundedByteArrayOutputStream (int capacity)
specifier|public
name|BoundedByteArrayOutputStream
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|this
argument_list|(
name|capacity
argument_list|,
name|capacity
argument_list|)
expr_stmt|;
block|}
DECL|method|BoundedByteArrayOutputStream (int capacity, int limit)
specifier|public
name|BoundedByteArrayOutputStream
parameter_list|(
name|int
name|capacity
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
if|if
condition|(
operator|(
name|capacity
operator|<
name|limit
operator|)
operator|||
operator|(
name|capacity
operator||
name|limit
operator|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid capacity/limit"
argument_list|)
throw|;
block|}
name|this
operator|.
name|buffer
operator|=
operator|new
name|byte
index|[
name|capacity
index|]
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
name|this
operator|.
name|count
operator|=
literal|0
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
if|if
condition|(
name|count
operator|>=
name|limit
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Reaching the limit of the buffer."
argument_list|)
throw|;
block|}
name|buffer
index|[
name|count
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|b
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
operator|(
name|off
operator|<
literal|0
operator|)
operator|||
operator|(
name|off
operator|>
name|b
operator|.
name|length
operator|)
operator|||
operator|(
name|len
operator|<
literal|0
operator|)
operator|||
operator|(
operator|(
name|off
operator|+
name|len
operator|)
operator|>
name|b
operator|.
name|length
operator|)
operator|||
operator|(
operator|(
name|off
operator|+
name|len
operator|)
operator|<
literal|0
operator|)
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
name|count
operator|+
name|len
operator|>
name|limit
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Reach the limit of the buffer"
argument_list|)
throw|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|buffer
argument_list|,
name|count
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|count
operator|+=
name|len
expr_stmt|;
block|}
DECL|method|reset (int newlim)
specifier|public
name|void
name|reset
parameter_list|(
name|int
name|newlim
parameter_list|)
block|{
if|if
condition|(
name|newlim
operator|>
name|buffer
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"Limit exceeds buffer size"
argument_list|)
throw|;
block|}
name|this
operator|.
name|limit
operator|=
name|newlim
expr_stmt|;
name|this
operator|.
name|count
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|this
operator|.
name|limit
operator|=
name|buffer
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|count
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|getLimit ()
specifier|public
name|int
name|getLimit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
DECL|method|getBuffer ()
specifier|public
name|byte
index|[]
name|getBuffer
parameter_list|()
block|{
return|return
name|buffer
return|;
block|}
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

