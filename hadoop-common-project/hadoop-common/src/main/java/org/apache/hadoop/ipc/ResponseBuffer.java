begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|util
operator|.
name|Arrays
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ResponseBuffer
class|class
name|ResponseBuffer
extends|extends
name|DataOutputStream
block|{
DECL|method|ResponseBuffer (int capacity)
name|ResponseBuffer
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|FramedBuffer
argument_list|(
name|capacity
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// update framing bytes based on bytes written to stream.
DECL|method|getFramedBuffer ()
specifier|private
name|FramedBuffer
name|getFramedBuffer
parameter_list|()
block|{
name|FramedBuffer
name|buf
init|=
operator|(
name|FramedBuffer
operator|)
name|out
decl_stmt|;
name|buf
operator|.
name|setSize
argument_list|(
name|written
argument_list|)
expr_stmt|;
return|return
name|buf
return|;
block|}
DECL|method|writeTo (OutputStream out)
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|getFramedBuffer
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|toByteArray ()
name|byte
index|[]
name|toByteArray
parameter_list|()
block|{
return|return
name|getFramedBuffer
argument_list|()
operator|.
name|toByteArray
argument_list|()
return|;
block|}
DECL|method|capacity ()
name|int
name|capacity
parameter_list|()
block|{
return|return
operator|(
operator|(
name|FramedBuffer
operator|)
name|out
operator|)
operator|.
name|capacity
argument_list|()
return|;
block|}
DECL|method|setCapacity (int capacity)
name|void
name|setCapacity
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
operator|(
operator|(
name|FramedBuffer
operator|)
name|out
operator|)
operator|.
name|setCapacity
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
block|}
DECL|method|ensureCapacity (int capacity)
name|void
name|ensureCapacity
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
if|if
condition|(
operator|(
operator|(
name|FramedBuffer
operator|)
name|out
operator|)
operator|.
name|capacity
argument_list|()
operator|<
name|capacity
condition|)
block|{
operator|(
operator|(
name|FramedBuffer
operator|)
name|out
operator|)
operator|.
name|setCapacity
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|reset ()
name|ResponseBuffer
name|reset
parameter_list|()
block|{
name|written
operator|=
literal|0
expr_stmt|;
operator|(
operator|(
name|FramedBuffer
operator|)
name|out
operator|)
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|class|FramedBuffer
specifier|private
specifier|static
class|class
name|FramedBuffer
extends|extends
name|ByteArrayOutputStream
block|{
DECL|field|FRAMING_BYTES
specifier|private
specifier|static
specifier|final
name|int
name|FRAMING_BYTES
init|=
literal|4
decl_stmt|;
DECL|method|FramedBuffer (int capacity)
name|FramedBuffer
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|super
argument_list|(
name|capacity
operator|+
name|FRAMING_BYTES
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|count
operator|-
name|FRAMING_BYTES
return|;
block|}
DECL|method|setSize (int size)
name|void
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|buf
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|size
operator|>>>
literal|24
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|buf
index|[
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|size
operator|>>>
literal|16
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|buf
index|[
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|size
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|buf
index|[
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|size
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
DECL|method|capacity ()
name|int
name|capacity
parameter_list|()
block|{
return|return
name|buf
operator|.
name|length
operator|-
name|FRAMING_BYTES
return|;
block|}
DECL|method|setCapacity (int capacity)
name|void
name|setCapacity
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|buf
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|buf
argument_list|,
name|capacity
operator|+
name|FRAMING_BYTES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|count
operator|=
name|FRAMING_BYTES
expr_stmt|;
name|setSize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit

