begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|UTFDataFormatException
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|test
operator|.
name|GenericTestUtils
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
name|util
operator|.
name|StringUtils
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/** Unit tests for UTF8. */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|TestUTF8
specifier|public
class|class
name|TestUTF8
block|{
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|getTestString ()
specifier|public
specifier|static
name|String
name|getTestString
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|Character
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testWritable ()
specifier|public
name|void
name|testWritable
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|TestWritable
operator|.
name|testWritable
argument_list|(
operator|new
name|UTF8
argument_list|(
name|getTestString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetBytes ()
specifier|public
name|void
name|testGetBytes
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
comment|// generate a random string
name|String
name|before
init|=
name|getTestString
argument_list|()
decl_stmt|;
comment|// Check that the bytes are stored correctly in Modified-UTF8 format.
comment|// Note that the DataInput and DataOutput interfaces convert between
comment|// bytes and Strings using the Modified-UTF8 format.
name|assertEquals
argument_list|(
name|before
argument_list|,
name|readModifiedUTF
argument_list|(
name|UTF8
operator|.
name|getBytes
argument_list|(
name|before
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readModifiedUTF (byte[] bytes)
specifier|private
name|String
name|readModifiedUTF
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|short
name|lengthBytes
init|=
operator|(
name|short
operator|)
literal|2
decl_stmt|;
name|ByteBuffer
name|bb
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|bytes
operator|.
name|length
operator|+
name|lengthBytes
argument_list|)
decl_stmt|;
name|bb
operator|.
name|putShort
argument_list|(
operator|(
name|short
operator|)
name|bytes
operator|.
name|length
argument_list|)
operator|.
name|put
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|bis
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bb
operator|.
name|array
argument_list|()
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|bis
argument_list|)
decl_stmt|;
return|return
name|dis
operator|.
name|readUTF
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testIO ()
specifier|public
name|void
name|testIO
parameter_list|()
throws|throws
name|Exception
block|{
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
argument_list|()
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
comment|// generate a random string
name|String
name|before
init|=
name|getTestString
argument_list|()
decl_stmt|;
comment|// write it
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
name|UTF8
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|before
argument_list|)
expr_stmt|;
comment|// test that it reads correctly
name|in
operator|.
name|reset
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|after
init|=
name|UTF8
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
comment|// test that it reads correctly with DataInput
name|in
operator|.
name|reset
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|after2
init|=
name|in
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after2
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNullEncoding ()
specifier|public
name|void
name|testNullEncoding
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|s
init|=
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
literal|0
block|}
argument_list|)
decl_stmt|;
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
operator|new
name|UTF8
argument_list|(
name|s
argument_list|)
operator|.
name|write
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
operator|new
name|String
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|2
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
operator|-
literal|2
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test encoding and decoding of UTF8 outside the basic multilingual plane.    *    * This is a regression test for HADOOP-9103.    */
annotation|@
name|Test
DECL|method|testNonBasicMultilingualPlane ()
specifier|public
name|void
name|testNonBasicMultilingualPlane
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test using the "CAT FACE" character (U+1F431)
comment|// See http://www.fileformat.info/info/unicode/char/1f431/index.htm
name|String
name|catFace
init|=
literal|"\uD83D\uDC31"
decl_stmt|;
comment|// This encodes to 4 bytes in UTF-8:
name|byte
index|[]
name|encoded
init|=
name|catFace
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|encoded
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f09f90b1"
argument_list|,
name|StringUtils
operator|.
name|byteToHexString
argument_list|(
name|encoded
argument_list|)
argument_list|)
expr_stmt|;
comment|// Decode back to String using our own decoder
name|String
name|roundTrip
init|=
name|UTF8
operator|.
name|fromBytes
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|catFace
argument_list|,
name|roundTrip
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that decoding invalid UTF8 throws an appropriate error message.    */
annotation|@
name|Test
DECL|method|testInvalidUTF8 ()
specifier|public
name|void
name|testInvalidUTF8
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|invalid
init|=
operator|new
name|byte
index|[]
block|{
literal|0x01
block|,
literal|0x02
block|,
operator|(
name|byte
operator|)
literal|0xff
block|,
operator|(
name|byte
operator|)
literal|0xff
block|,
literal|0x01
block|,
literal|0x02
block|,
literal|0x03
block|,
literal|0x04
block|,
literal|0x05
block|}
decl_stmt|;
try|try
block|{
name|UTF8
operator|.
name|fromBytes
argument_list|(
name|invalid
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not throw an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UTFDataFormatException
name|utfde
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Invalid UTF8 at ffff01020304"
argument_list|,
name|utfde
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test for a 5-byte UTF8 sequence, which is now considered illegal.    */
annotation|@
name|Test
DECL|method|test5ByteUtf8Sequence ()
specifier|public
name|void
name|test5ByteUtf8Sequence
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|invalid
init|=
operator|new
name|byte
index|[]
block|{
literal|0x01
block|,
literal|0x02
block|,
operator|(
name|byte
operator|)
literal|0xf8
block|,
operator|(
name|byte
operator|)
literal|0x88
block|,
operator|(
name|byte
operator|)
literal|0x80
block|,
operator|(
name|byte
operator|)
literal|0x80
block|,
operator|(
name|byte
operator|)
literal|0x80
block|,
literal|0x04
block|,
literal|0x05
block|}
decl_stmt|;
try|try
block|{
name|UTF8
operator|.
name|fromBytes
argument_list|(
name|invalid
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not throw an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UTFDataFormatException
name|utfde
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Invalid UTF8 at f88880808004"
argument_list|,
name|utfde
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that decoding invalid UTF8 due to truncation yields the correct    * exception type.    */
annotation|@
name|Test
DECL|method|testInvalidUTF8Truncated ()
specifier|public
name|void
name|testInvalidUTF8Truncated
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Truncated CAT FACE character -- this is a 4-byte sequence, but we
comment|// only have the first three bytes.
name|byte
index|[]
name|truncated
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xF0
block|,
operator|(
name|byte
operator|)
literal|0x9F
block|,
operator|(
name|byte
operator|)
literal|0x90
block|}
decl_stmt|;
try|try
block|{
name|UTF8
operator|.
name|fromBytes
argument_list|(
name|truncated
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not throw an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UTFDataFormatException
name|utfde
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Truncated UTF8 at f09f90"
argument_list|,
name|utfde
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

