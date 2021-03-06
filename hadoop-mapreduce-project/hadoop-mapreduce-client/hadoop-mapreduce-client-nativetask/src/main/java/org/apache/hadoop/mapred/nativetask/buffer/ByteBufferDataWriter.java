begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.buffer
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
name|buffer
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
name|mapred
operator|.
name|nativetask
operator|.
name|NativeDataTarget
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
comment|/**  * DataOutputStream implementation which buffers data in a fixed-size  * ByteBuffer.  * When the byte buffer has filled up, synchronously passes the buffer  * to a downstream NativeDataTarget.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ByteBufferDataWriter
specifier|public
class|class
name|ByteBufferDataWriter
extends|extends
name|DataOutputStream
block|{
DECL|field|buffer
specifier|private
specifier|final
name|ByteBuffer
name|buffer
decl_stmt|;
DECL|field|target
specifier|private
specifier|final
name|NativeDataTarget
name|target
decl_stmt|;
DECL|field|TRUE
specifier|private
specifier|final
specifier|static
name|byte
name|TRUE
init|=
operator|(
name|byte
operator|)
literal|1
decl_stmt|;
DECL|field|FALSE
specifier|private
specifier|final
specifier|static
name|byte
name|FALSE
init|=
operator|(
name|byte
operator|)
literal|0
decl_stmt|;
DECL|field|javaWriter
specifier|private
specifier|final
name|java
operator|.
name|io
operator|.
name|DataOutputStream
name|javaWriter
decl_stmt|;
DECL|method|checkSizeAndFlushIfNecessary (int length)
specifier|private
name|void
name|checkSizeAndFlushIfNecessary
parameter_list|(
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|buffer
operator|.
name|position
argument_list|()
operator|>
literal|0
operator|&&
name|buffer
operator|.
name|remaining
argument_list|()
operator|<
name|length
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|ByteBufferDataWriter (NativeDataTarget handler)
specifier|public
name|ByteBufferDataWriter
parameter_list|(
name|NativeDataTarget
name|handler
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|handler
operator|.
name|getOutputBuffer
argument_list|()
operator|.
name|getByteBuffer
argument_list|()
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|javaWriter
operator|=
operator|new
name|java
operator|.
name|io
operator|.
name|DataOutputStream
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (int v)
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSizeAndFlushIfNecessary
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shortOfSpace (int dataLength)
specifier|public
name|boolean
name|shortOfSpace
parameter_list|(
name|int
name|dataLength
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|buffer
operator|.
name|remaining
argument_list|()
operator|<
name|dataLength
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|write (byte b[], int off, int len)
specifier|public
specifier|synchronized
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
name|int
name|remain
init|=
name|len
decl_stmt|;
name|int
name|offset
init|=
name|off
decl_stmt|;
while|while
condition|(
name|remain
operator|>
literal|0
condition|)
block|{
name|int
name|currentFlush
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|buffer
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|currentFlush
operator|=
name|Math
operator|.
name|min
argument_list|(
name|buffer
operator|.
name|remaining
argument_list|()
argument_list|,
name|remain
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|currentFlush
argument_list|)
expr_stmt|;
name|remain
operator|-=
name|currentFlush
expr_stmt|;
name|offset
operator|+=
name|currentFlush
expr_stmt|;
block|}
else|else
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
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
name|target
operator|.
name|sendData
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|position
argument_list|(
literal|0
argument_list|)
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
name|hasUnFlushedData
argument_list|()
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|target
operator|.
name|finishSendData
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBoolean (boolean v)
specifier|public
specifier|final
name|void
name|writeBoolean
parameter_list|(
name|boolean
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSizeAndFlushIfNecessary
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|put
argument_list|(
name|v
condition|?
name|TRUE
else|:
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeByte (int v)
specifier|public
specifier|final
name|void
name|writeByte
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSizeAndFlushIfNecessary
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeShort (int v)
specifier|public
specifier|final
name|void
name|writeShort
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSizeAndFlushIfNecessary
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putShort
argument_list|(
operator|(
name|short
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeChar (int v)
specifier|public
specifier|final
name|void
name|writeChar
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSizeAndFlushIfNecessary
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|put
argument_list|(
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
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|put
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeInt (int v)
specifier|public
specifier|final
name|void
name|writeInt
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSizeAndFlushIfNecessary
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeLong (long v)
specifier|public
specifier|final
name|void
name|writeLong
parameter_list|(
name|long
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSizeAndFlushIfNecessary
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putLong
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFloat (float v)
specifier|public
specifier|final
name|void
name|writeFloat
parameter_list|(
name|float
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSizeAndFlushIfNecessary
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDouble (double v)
specifier|public
specifier|final
name|void
name|writeDouble
parameter_list|(
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSizeAndFlushIfNecessary
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|writeLong
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes (String s)
specifier|public
specifier|final
name|void
name|writeBytes
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|javaWriter
operator|.
name|writeBytes
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeChars (String s)
specifier|public
specifier|final
name|void
name|writeChars
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|javaWriter
operator|.
name|writeChars
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeUTF (String str)
specifier|public
specifier|final
name|void
name|writeUTF
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
name|javaWriter
operator|.
name|writeUTF
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasUnFlushedData ()
specifier|public
name|boolean
name|hasUnFlushedData
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|position
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
end_class

end_unit

