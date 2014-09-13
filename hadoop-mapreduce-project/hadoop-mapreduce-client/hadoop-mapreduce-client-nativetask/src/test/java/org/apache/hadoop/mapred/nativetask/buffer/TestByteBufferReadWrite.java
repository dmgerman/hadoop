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
name|*
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
name|Charsets
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
name|primitives
operator|.
name|Shorts
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
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestByteBufferReadWrite
specifier|public
class|class
name|TestByteBufferReadWrite
block|{
annotation|@
name|Test
DECL|method|testReadWrite ()
specifier|public
name|void
name|testReadWrite
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buff
init|=
operator|new
name|byte
index|[
literal|10000
index|]
decl_stmt|;
name|InputBuffer
name|input
init|=
operator|new
name|InputBuffer
argument_list|(
name|buff
argument_list|)
decl_stmt|;
name|MockDataTarget
name|target
init|=
operator|new
name|MockDataTarget
argument_list|(
name|buff
argument_list|)
decl_stmt|;
name|ByteBufferDataWriter
name|writer
init|=
operator|new
name|ByteBufferDataWriter
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|2
block|,
literal|2
block|}
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeByte
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeShort
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeChar
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeInt
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeLong
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeFloat
argument_list|(
literal|9
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeDouble
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeBytes
argument_list|(
literal|"goodboy"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeChars
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeUTF
argument_list|(
literal|"native task"
argument_list|)
expr_stmt|;
name|int
name|length
init|=
name|target
operator|.
name|getOutputBuffer
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
name|input
operator|.
name|rewind
argument_list|(
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|ByteBufferDataReader
name|reader
init|=
operator|new
name|ByteBufferDataReader
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|two
init|=
operator|new
name|byte
index|[
literal|2
index|]
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
name|two
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|two
index|[
literal|0
index|]
operator|==
name|two
index|[
literal|1
index|]
operator|&&
name|two
index|[
literal|0
index|]
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|reader
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|reader
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|reader
operator|.
name|readShort
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|reader
operator|.
name|readChar
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|reader
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|reader
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|reader
operator|.
name|readFloat
argument_list|()
operator|-
literal|9
operator|<
literal|0.0001
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|reader
operator|.
name|readDouble
argument_list|()
operator|-
literal|10
operator|<
literal|0.0001
argument_list|)
expr_stmt|;
name|byte
index|[]
name|goodboy
init|=
operator|new
name|byte
index|[
literal|"goodboy"
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
name|goodboy
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"goodboy"
argument_list|,
name|toString
argument_list|(
name|goodboy
argument_list|)
argument_list|)
expr_stmt|;
name|char
index|[]
name|hello
init|=
operator|new
name|char
index|[
literal|"hello"
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hello
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hello
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|readChar
argument_list|()
expr_stmt|;
block|}
name|String
name|helloString
init|=
operator|new
name|String
argument_list|(
name|hello
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|helloString
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"native task"
argument_list|,
name|reader
operator|.
name|readUTF
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|input
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that Unicode characters outside the basic multilingual plane,    * such as this cat face, are properly encoded.    */
annotation|@
name|Test
DECL|method|testCatFace ()
specifier|public
name|void
name|testCatFace
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buff
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
name|MockDataTarget
name|target
init|=
operator|new
name|MockDataTarget
argument_list|(
name|buff
argument_list|)
decl_stmt|;
name|ByteBufferDataWriter
name|writer
init|=
operator|new
name|ByteBufferDataWriter
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|String
name|catFace
init|=
literal|"\uD83D\uDE38"
decl_stmt|;
name|writer
operator|.
name|writeUTF
argument_list|(
name|catFace
argument_list|)
expr_stmt|;
comment|// Check that our own decoder can read it
name|InputBuffer
name|input
init|=
operator|new
name|InputBuffer
argument_list|(
name|buff
argument_list|)
decl_stmt|;
name|input
operator|.
name|rewind
argument_list|(
literal|0
argument_list|,
name|buff
operator|.
name|length
argument_list|)
expr_stmt|;
name|ByteBufferDataReader
name|reader
init|=
operator|new
name|ByteBufferDataReader
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|catFace
argument_list|,
name|reader
operator|.
name|readUTF
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that the standard Java one can read it too
name|String
name|fromJava
init|=
operator|new
name|java
operator|.
name|io
operator|.
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|buff
argument_list|)
argument_list|)
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|catFace
argument_list|,
name|fromJava
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShortOfSpace ()
specifier|public
name|void
name|testShortOfSpace
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buff
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
name|MockDataTarget
name|target
init|=
operator|new
name|MockDataTarget
argument_list|(
name|buff
argument_list|)
decl_stmt|;
name|ByteBufferDataWriter
name|writer
init|=
operator|new
name|ByteBufferDataWriter
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|writer
operator|.
name|hasUnFlushedData
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|2
block|,
literal|2
block|}
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|writer
operator|.
name|hasUnFlushedData
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|writer
operator|.
name|shortOfSpace
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFlush ()
specifier|public
name|void
name|testFlush
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buff
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
name|MockDataTarget
name|target
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|MockDataTarget
argument_list|(
name|buff
argument_list|)
argument_list|)
decl_stmt|;
name|ByteBufferDataWriter
name|writer
init|=
operator|new
name|ByteBufferDataWriter
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|writer
operator|.
name|hasUnFlushedData
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[
literal|100
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|writer
operator|.
name|hasUnFlushedData
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|target
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|11
argument_list|)
argument_list|)
operator|.
name|sendData
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|target
argument_list|)
operator|.
name|finishSendData
argument_list|()
expr_stmt|;
block|}
DECL|method|toString (byte[] str)
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|byte
index|[]
name|str
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
return|return
operator|new
name|String
argument_list|(
name|str
argument_list|,
literal|0
argument_list|,
name|str
operator|.
name|length
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
DECL|class|MockDataTarget
specifier|private
specifier|static
class|class
name|MockDataTarget
implements|implements
name|NativeDataTarget
block|{
DECL|field|out
specifier|private
name|OutputBuffer
name|out
decl_stmt|;
DECL|method|MockDataTarget (byte[] buffer)
name|MockDataTarget
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
operator|new
name|OutputBuffer
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendData ()
specifier|public
name|void
name|sendData
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|finishSendData ()
specifier|public
name|void
name|finishSendData
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|getOutputBuffer ()
specifier|public
name|OutputBuffer
name|getOutputBuffer
parameter_list|()
block|{
return|return
name|out
return|;
block|}
block|}
block|}
end_class

end_unit

