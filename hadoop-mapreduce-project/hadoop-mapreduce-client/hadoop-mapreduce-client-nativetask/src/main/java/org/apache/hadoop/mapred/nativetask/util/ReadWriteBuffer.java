begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|util
package|;
end_package

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
name|Charsets
import|;
end_import

begin_class
DECL|class|ReadWriteBuffer
specifier|public
class|class
name|ReadWriteBuffer
block|{
DECL|field|_buff
specifier|private
name|byte
index|[]
name|_buff
decl_stmt|;
DECL|field|_writePoint
specifier|private
name|int
name|_writePoint
decl_stmt|;
DECL|field|_readPoint
specifier|private
name|int
name|_readPoint
decl_stmt|;
DECL|field|CACHE_LINE_SIZE
specifier|final
name|int
name|CACHE_LINE_SIZE
init|=
literal|16
decl_stmt|;
DECL|method|ReadWriteBuffer (int length)
specifier|public
name|ReadWriteBuffer
parameter_list|(
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|_buff
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
block|}
block|}
DECL|method|ReadWriteBuffer ()
specifier|public
name|ReadWriteBuffer
parameter_list|()
block|{
name|_buff
operator|=
operator|new
name|byte
index|[
name|CACHE_LINE_SIZE
index|]
expr_stmt|;
block|}
DECL|method|ReadWriteBuffer (byte[] bytes)
specifier|public
name|ReadWriteBuffer
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|_buff
operator|=
name|bytes
expr_stmt|;
name|_writePoint
operator|=
literal|0
expr_stmt|;
name|_readPoint
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|reset (byte[] newBuff)
specifier|public
name|void
name|reset
parameter_list|(
name|byte
index|[]
name|newBuff
parameter_list|)
block|{
name|_buff
operator|=
name|newBuff
expr_stmt|;
name|_writePoint
operator|=
literal|0
expr_stmt|;
name|_readPoint
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|setReadPoint (int pos)
specifier|public
name|void
name|setReadPoint
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|_readPoint
operator|=
name|pos
expr_stmt|;
block|}
DECL|method|setWritePoint (int pos)
specifier|public
name|void
name|setWritePoint
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|_writePoint
operator|=
name|pos
expr_stmt|;
block|}
DECL|method|getBuff ()
specifier|public
name|byte
index|[]
name|getBuff
parameter_list|()
block|{
return|return
name|_buff
return|;
block|}
DECL|method|getWritePoint ()
specifier|public
name|int
name|getWritePoint
parameter_list|()
block|{
return|return
name|_writePoint
return|;
block|}
DECL|method|getReadPoint ()
specifier|public
name|int
name|getReadPoint
parameter_list|()
block|{
return|return
name|_readPoint
return|;
block|}
DECL|method|writeInt (int v)
specifier|public
name|void
name|writeInt
parameter_list|(
name|int
name|v
parameter_list|)
block|{
name|checkWriteSpaceAndResizeIfNecessary
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|v
operator|>>>
literal|16
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|v
operator|>>>
literal|24
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|_writePoint
operator|+=
literal|4
expr_stmt|;
block|}
DECL|method|writeLong (long v)
specifier|public
name|void
name|writeLong
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|checkWriteSpaceAndResizeIfNecessary
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|0
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|8
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|16
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|24
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|4
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|32
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|5
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|40
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|6
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|48
argument_list|)
expr_stmt|;
name|_buff
index|[
name|_writePoint
operator|+
literal|7
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|56
argument_list|)
expr_stmt|;
name|_writePoint
operator|+=
literal|8
expr_stmt|;
block|}
DECL|method|writeBytes (byte b[], int off, int len)
specifier|public
name|void
name|writeBytes
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
block|{
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|checkWriteSpaceAndResizeIfNecessary
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|_buff
argument_list|,
name|_writePoint
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|_writePoint
operator|+=
name|len
expr_stmt|;
block|}
DECL|method|readInt ()
specifier|public
name|int
name|readInt
parameter_list|()
block|{
specifier|final
name|int
name|ch4
init|=
literal|0xff
operator|&
operator|(
name|_buff
index|[
name|_readPoint
operator|+
literal|0
index|]
operator|)
decl_stmt|;
specifier|final
name|int
name|ch3
init|=
literal|0xff
operator|&
operator|(
name|_buff
index|[
name|_readPoint
operator|+
literal|1
index|]
operator|)
decl_stmt|;
specifier|final
name|int
name|ch2
init|=
literal|0xff
operator|&
operator|(
name|_buff
index|[
name|_readPoint
operator|+
literal|2
index|]
operator|)
decl_stmt|;
specifier|final
name|int
name|ch1
init|=
literal|0xff
operator|&
operator|(
name|_buff
index|[
name|_readPoint
operator|+
literal|3
index|]
operator|)
decl_stmt|;
name|_readPoint
operator|+=
literal|4
expr_stmt|;
return|return
operator|(
operator|(
name|ch1
operator|<<
literal|24
operator|)
operator|+
operator|(
name|ch2
operator|<<
literal|16
operator|)
operator|+
operator|(
name|ch3
operator|<<
literal|8
operator|)
operator|+
operator|(
name|ch4
operator|<<
literal|0
operator|)
operator|)
return|;
block|}
DECL|method|readLong ()
specifier|public
name|long
name|readLong
parameter_list|()
block|{
specifier|final
name|long
name|result
init|=
operator|(
operator|(
name|_buff
index|[
name|_readPoint
operator|+
literal|0
index|]
operator|&
literal|255
operator|)
operator|<<
literal|0
operator|)
operator|+
operator|(
operator|(
name|_buff
index|[
name|_readPoint
operator|+
literal|1
index|]
operator|&
literal|255
operator|)
operator|<<
literal|8
operator|)
operator|+
operator|(
operator|(
name|_buff
index|[
name|_readPoint
operator|+
literal|2
index|]
operator|&
literal|255
operator|)
operator|<<
literal|16
operator|)
operator|+
operator|(
call|(
name|long
call|)
argument_list|(
name|_buff
index|[
name|_readPoint
operator|+
literal|3
index|]
operator|&
literal|255
argument_list|)
operator|<<
literal|24
operator|)
operator|+
operator|(
call|(
name|long
call|)
argument_list|(
name|_buff
index|[
name|_readPoint
operator|+
literal|4
index|]
operator|&
literal|255
argument_list|)
operator|<<
literal|32
operator|)
operator|+
operator|(
call|(
name|long
call|)
argument_list|(
name|_buff
index|[
name|_readPoint
operator|+
literal|5
index|]
operator|&
literal|255
argument_list|)
operator|<<
literal|40
operator|)
operator|+
operator|(
call|(
name|long
call|)
argument_list|(
name|_buff
index|[
name|_readPoint
operator|+
literal|6
index|]
operator|&
literal|255
argument_list|)
operator|<<
literal|48
operator|)
operator|+
operator|(
operator|(
operator|(
name|long
operator|)
name|_buff
index|[
name|_readPoint
operator|+
literal|7
index|]
operator|<<
literal|56
operator|)
operator|)
decl_stmt|;
name|_readPoint
operator|+=
literal|8
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|readBytes ()
specifier|public
name|byte
index|[]
name|readBytes
parameter_list|()
block|{
specifier|final
name|int
name|length
init|=
name|readInt
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|_buff
argument_list|,
name|_readPoint
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|_readPoint
operator|+=
name|length
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|writeString (String str)
specifier|public
name|void
name|writeString
parameter_list|(
name|String
name|str
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|bytes
init|=
name|str
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|writeBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|readString ()
specifier|public
name|String
name|readString
parameter_list|()
block|{
specifier|final
name|byte
index|[]
name|bytes
init|=
name|readBytes
argument_list|()
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
DECL|method|checkWriteSpaceAndResizeIfNecessary (int toBeWritten)
specifier|private
name|void
name|checkWriteSpaceAndResizeIfNecessary
parameter_list|(
name|int
name|toBeWritten
parameter_list|)
block|{
if|if
condition|(
name|_buff
operator|.
name|length
operator|-
name|_writePoint
operator|>=
name|toBeWritten
condition|)
block|{
return|return;
block|}
specifier|final
name|int
name|newLength
init|=
operator|(
name|toBeWritten
operator|+
name|_writePoint
operator|>
name|CACHE_LINE_SIZE
operator|)
condition|?
operator|(
name|toBeWritten
operator|+
name|_writePoint
operator|)
else|:
name|CACHE_LINE_SIZE
decl_stmt|;
specifier|final
name|byte
index|[]
name|newBuff
init|=
operator|new
name|byte
index|[
name|newLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|_buff
argument_list|,
literal|0
argument_list|,
name|newBuff
argument_list|,
literal|0
argument_list|,
name|_writePoint
argument_list|)
expr_stmt|;
name|_buff
operator|=
name|newBuff
expr_stmt|;
block|}
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

end_unit

