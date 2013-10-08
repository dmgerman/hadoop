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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|nio
operator|.
name|BufferUnderflowException
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
name|nio
operator|.
name|charset
operator|.
name|CharacterCodingException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Bytes
import|;
end_import

begin_comment
comment|/** Unit tests for LargeUTF8. */
end_comment

begin_class
DECL|class|TestText
specifier|public
class|class
name|TestText
extends|extends
name|TestCase
block|{
DECL|field|NUM_ITERATIONS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_ITERATIONS
init|=
literal|100
decl_stmt|;
DECL|method|TestText (String name)
specifier|public
name|TestText
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|RAND_LEN
specifier|private
specifier|static
specifier|final
name|int
name|RAND_LEN
init|=
operator|-
literal|1
decl_stmt|;
comment|// generate a valid java String
DECL|method|getTestString (int len)
specifier|private
specifier|static
name|String
name|getTestString
parameter_list|(
name|int
name|len
parameter_list|)
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
operator|(
name|len
operator|==
name|RAND_LEN
operator|)
condition|?
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
else|:
name|len
decl_stmt|;
while|while
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|<
name|length
condition|)
block|{
name|int
name|codePoint
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|Character
operator|.
name|MAX_CODE_POINT
argument_list|)
decl_stmt|;
name|char
name|tmpStr
index|[]
init|=
operator|new
name|char
index|[
literal|2
index|]
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|isDefined
argument_list|(
name|codePoint
argument_list|)
condition|)
block|{
comment|//unpaired surrogate
if|if
condition|(
name|codePoint
operator|<
name|Character
operator|.
name|MIN_SUPPLEMENTARY_CODE_POINT
operator|&&
operator|!
name|Character
operator|.
name|isHighSurrogate
argument_list|(
operator|(
name|char
operator|)
name|codePoint
argument_list|)
operator|&&
operator|!
name|Character
operator|.
name|isLowSurrogate
argument_list|(
operator|(
name|char
operator|)
name|codePoint
argument_list|)
condition|)
block|{
name|Character
operator|.
name|toChars
argument_list|(
name|codePoint
argument_list|,
name|tmpStr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|tmpStr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getTestString ()
specifier|public
specifier|static
name|String
name|getTestString
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getTestString
argument_list|(
name|RAND_LEN
argument_list|)
return|;
block|}
DECL|method|getLongString ()
specifier|public
specifier|static
name|String
name|getLongString
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
name|getTestString
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|Short
operator|.
name|MAX_VALUE
operator|+
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|<
name|length
condition|)
name|buffer
operator|.
name|append
argument_list|(
name|str
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
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
name|NUM_ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
name|str
operator|=
name|getLongString
argument_list|()
expr_stmt|;
else|else
name|str
operator|=
name|getTestString
argument_list|()
expr_stmt|;
name|TestWritable
operator|.
name|testWritable
argument_list|(
operator|new
name|Text
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCoding ()
specifier|public
name|void
name|testCoding
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|before
init|=
literal|"Bad \t encoding \t testcase"
decl_stmt|;
name|Text
name|text
init|=
operator|new
name|Text
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|String
name|after
init|=
name|text
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|equals
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
comment|// generate a random string
if|if
condition|(
name|i
operator|==
literal|0
condition|)
name|before
operator|=
name|getLongString
argument_list|()
expr_stmt|;
else|else
name|before
operator|=
name|getTestString
argument_list|()
expr_stmt|;
comment|// test string to utf8
name|ByteBuffer
name|bb
init|=
name|Text
operator|.
name|encode
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|byte
index|[]
name|utf8Text
init|=
name|bb
operator|.
name|array
argument_list|()
decl_stmt|;
name|byte
index|[]
name|utf8Java
init|=
name|before
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|WritableComparator
operator|.
name|compareBytes
argument_list|(
name|utf8Text
argument_list|,
literal|0
argument_list|,
name|bb
operator|.
name|limit
argument_list|()
argument_list|,
name|utf8Java
argument_list|,
literal|0
argument_list|,
name|utf8Java
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|// test utf8 to string
name|after
operator|=
name|Text
operator|.
name|decode
argument_list|(
name|utf8Java
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|equals
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|NUM_ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
comment|// generate a random string
name|String
name|before
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
name|before
operator|=
name|getLongString
argument_list|()
expr_stmt|;
else|else
name|before
operator|=
name|getTestString
argument_list|()
expr_stmt|;
comment|// write it
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Text
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
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|equals
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test compatibility with Java's other decoder
name|int
name|strLenSize
init|=
name|WritableUtils
operator|.
name|getVIntSize
argument_list|(
name|Text
operator|.
name|utf8Length
argument_list|(
name|before
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|after2
init|=
operator|new
name|String
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
name|strLenSize
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
operator|-
name|strLenSize
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|before
operator|.
name|equals
argument_list|(
name|after2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestLimitedIO (String str, int len)
specifier|public
name|void
name|doTestLimitedIO
parameter_list|(
name|String
name|str
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
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
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|str
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected writeString to fail when told to write a string "
operator|+
literal|"that was too long!  The string was '"
operator|+
name|str
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|str
argument_list|,
name|len
operator|+
literal|1
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
name|in
operator|.
name|mark
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|String
name|after
decl_stmt|;
try|try
block|{
name|after
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected readString to fail when told to read a string "
operator|+
literal|"that was too long!  The string was '"
operator|+
name|str
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
name|after
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|,
name|len
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|str
operator|.
name|equals
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLimitedIO ()
specifier|public
name|void
name|testLimitedIO
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestLimitedIO
argument_list|(
literal|"abcd"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|doTestLimitedIO
argument_list|(
literal|"foo bar baz"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|doTestLimitedIO
argument_list|(
literal|"1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testCompare ()
specifier|public
name|void
name|testCompare
parameter_list|()
throws|throws
name|Exception
block|{
name|DataOutputBuffer
name|out1
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|DataOutputBuffer
name|out2
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|DataOutputBuffer
name|out3
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|Text
operator|.
name|Comparator
name|comparator
init|=
operator|new
name|Text
operator|.
name|Comparator
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
name|NUM_ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
comment|// reset output buffer
name|out1
operator|.
name|reset
argument_list|()
expr_stmt|;
name|out2
operator|.
name|reset
argument_list|()
expr_stmt|;
name|out3
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// generate two random strings
name|String
name|str1
init|=
name|getTestString
argument_list|()
decl_stmt|;
name|String
name|str2
init|=
name|getTestString
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|str1
operator|=
name|getLongString
argument_list|()
expr_stmt|;
name|str2
operator|=
name|getLongString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|str1
operator|=
name|getTestString
argument_list|()
expr_stmt|;
name|str2
operator|=
name|getTestString
argument_list|()
expr_stmt|;
block|}
comment|// convert to texts
name|Text
name|txt1
init|=
operator|new
name|Text
argument_list|(
name|str1
argument_list|)
decl_stmt|;
name|Text
name|txt2
init|=
operator|new
name|Text
argument_list|(
name|str2
argument_list|)
decl_stmt|;
name|Text
name|txt3
init|=
operator|new
name|Text
argument_list|(
name|str1
argument_list|)
decl_stmt|;
comment|// serialize them
name|txt1
operator|.
name|write
argument_list|(
name|out1
argument_list|)
expr_stmt|;
name|txt2
operator|.
name|write
argument_list|(
name|out2
argument_list|)
expr_stmt|;
name|txt3
operator|.
name|write
argument_list|(
name|out3
argument_list|)
expr_stmt|;
comment|// compare two strings by looking at their binary formats
name|int
name|ret1
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|out1
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|out1
operator|.
name|getLength
argument_list|()
argument_list|,
name|out2
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|out2
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
comment|// compare two strings
name|int
name|ret2
init|=
name|txt1
operator|.
name|compareTo
argument_list|(
name|txt2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ret1
argument_list|,
name|ret2
argument_list|)
expr_stmt|;
comment|// test equal
name|assertEquals
argument_list|(
name|txt1
operator|.
name|compareTo
argument_list|(
name|txt3
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|comparator
operator|.
name|compare
argument_list|(
name|out1
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|out3
operator|.
name|getLength
argument_list|()
argument_list|,
name|out3
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|out3
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFind ()
specifier|public
name|void
name|testFind
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|text
init|=
operator|new
name|Text
argument_list|(
literal|"abcd\u20acbdcd\u20ac"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|text
operator|.
name|find
argument_list|(
literal|"abd"
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|text
operator|.
name|find
argument_list|(
literal|"ac"
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|text
operator|.
name|find
argument_list|(
literal|"\u20ac"
argument_list|)
operator|==
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|text
operator|.
name|find
argument_list|(
literal|"\u20ac"
argument_list|,
literal|5
argument_list|)
operator|==
literal|11
argument_list|)
expr_stmt|;
block|}
DECL|method|testFindAfterUpdatingContents ()
specifier|public
name|void
name|testFindAfterUpdatingContents
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|text
init|=
operator|new
name|Text
argument_list|(
literal|"abcd"
argument_list|)
decl_stmt|;
name|text
operator|.
name|set
argument_list|(
literal|"a"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|text
operator|.
name|getLength
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|text
operator|.
name|find
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|text
operator|.
name|find
argument_list|(
literal|"b"
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testValidate ()
specifier|public
name|void
name|testValidate
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|text
init|=
operator|new
name|Text
argument_list|(
literal|"abcd\u20acbdcd\u20ac"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|utf8
init|=
name|text
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|text
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|Text
operator|.
name|validateUTF8
argument_list|(
name|utf8
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testClear ()
specifier|public
name|void
name|testClear
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test lengths on an empty text object
name|Text
name|text
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Actual string on an empty text object must be an empty string"
argument_list|,
literal|""
argument_list|,
name|text
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Underlying byte array length must be zero"
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|getBytes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"String's length must be zero"
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test if clear works as intended
name|text
operator|=
operator|new
name|Text
argument_list|(
literal|"abcd\u20acbdcd\u20ac"
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|text
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|text
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"String must be empty after clear()"
argument_list|,
literal|""
argument_list|,
name|text
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Length of the byte array must not decrease after clear()"
argument_list|,
name|text
operator|.
name|getBytes
argument_list|()
operator|.
name|length
operator|>=
name|len
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Length of the string must be reset to 0 after clear()"
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTextText ()
specifier|public
name|void
name|testTextText
parameter_list|()
throws|throws
name|CharacterCodingException
block|{
name|Text
name|a
init|=
operator|new
name|Text
argument_list|(
literal|"abc"
argument_list|)
decl_stmt|;
name|Text
name|b
init|=
operator|new
name|Text
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|b
operator|.
name|set
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|append
argument_list|(
literal|"xdefgxxx"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"modified aliased string"
argument_list|,
literal|"abc"
argument_list|,
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"appended string incorrectly"
argument_list|,
literal|"abcdefg"
argument_list|,
name|a
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// add an extra byte so that capacity = 14 and length = 8
name|a
operator|.
name|append
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'d'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|14
argument_list|,
name|a
operator|.
name|getBytes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|a
operator|.
name|copyBytes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|class|ConcurrentEncodeDecodeThread
specifier|private
class|class
name|ConcurrentEncodeDecodeThread
extends|extends
name|Thread
block|{
DECL|method|ConcurrentEncodeDecodeThread (String name)
specifier|public
name|ConcurrentEncodeDecodeThread
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|String
name|name
init|=
name|this
operator|.
name|getName
argument_list|()
decl_stmt|;
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
try|try
block|{
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|name
argument_list|)
expr_stmt|;
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
name|s
init|=
name|WritableUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|name
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|testConcurrentEncodeDecode ()
specifier|public
name|void
name|testConcurrentEncodeDecode
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|thread1
init|=
operator|new
name|ConcurrentEncodeDecodeThread
argument_list|(
literal|"apache"
argument_list|)
decl_stmt|;
name|Thread
name|thread2
init|=
operator|new
name|ConcurrentEncodeDecodeThread
argument_list|(
literal|"hadoop"
argument_list|)
decl_stmt|;
name|thread1
operator|.
name|start
argument_list|()
expr_stmt|;
name|thread2
operator|.
name|start
argument_list|()
expr_stmt|;
name|thread2
operator|.
name|join
argument_list|()
expr_stmt|;
name|thread2
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|testAvroReflect ()
specifier|public
name|void
name|testAvroReflect
parameter_list|()
throws|throws
name|Exception
block|{
name|AvroTestUtil
operator|.
name|testReflect
argument_list|(
operator|new
name|Text
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"{\"type\":\"string\",\"java-class\":\"org.apache.hadoop.io.Text\"}"
argument_list|)
expr_stmt|;
block|}
comment|/**    *     */
DECL|method|testCharAt ()
specifier|public
name|void
name|testCharAt
parameter_list|()
block|{
name|String
name|line
init|=
literal|"adsawseeeeegqewgasddga"
decl_stmt|;
name|Text
name|text
init|=
operator|new
name|Text
argument_list|(
name|line
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
name|line
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"testCharAt error1 !!!"
argument_list|,
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
name|line
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"testCharAt error2 !!!"
argument_list|,
operator|-
literal|1
argument_list|,
name|text
operator|.
name|charAt
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testCharAt error3 !!!"
argument_list|,
operator|-
literal|1
argument_list|,
name|text
operator|.
name|charAt
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * test {@code Text} readFields/write operations    */
DECL|method|testReadWriteOperations ()
specifier|public
name|void
name|testReadWriteOperations
parameter_list|()
block|{
name|String
name|line
init|=
literal|"adsawseeeeegqewgasddga"
decl_stmt|;
name|byte
index|[]
name|inputBytes
init|=
name|line
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|inputBytes
operator|=
name|Bytes
operator|.
name|concat
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|22
block|}
argument_list|,
name|inputBytes
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|Text
name|text
init|=
operator|new
name|Text
argument_list|(
name|line
argument_list|)
decl_stmt|;
try|try
block|{
name|in
operator|.
name|reset
argument_list|(
name|inputBytes
argument_list|,
name|inputBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|text
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testReadFields error !!!"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|text
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{           }
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testReadWriteOperations error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * test {@code Text.bytesToCodePoint(bytes) }     * with {@code BufferUnderflowException}    *     */
DECL|method|testBytesToCodePoint ()
specifier|public
name|void
name|testBytesToCodePoint
parameter_list|()
block|{
try|try
block|{
name|ByteBuffer
name|bytes
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|-
literal|2
block|,
literal|45
block|,
literal|23
block|,
literal|12
block|,
literal|76
block|,
literal|89
block|}
argument_list|)
decl_stmt|;
name|Text
operator|.
name|bytesToCodePoint
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"testBytesToCodePoint error !!!"
argument_list|,
name|bytes
operator|.
name|position
argument_list|()
operator|==
literal|6
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testBytesToCodePoint unexp exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testBytesToCodePoint unexp exception"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testbytesToCodePointWithInvalidUTF ()
specifier|public
name|void
name|testbytesToCodePointWithInvalidUTF
parameter_list|()
block|{
try|try
block|{
name|Text
operator|.
name|bytesToCodePoint
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|-
literal|2
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testbytesToCodePointWithInvalidUTF error unexp exception !!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|ex
parameter_list|)
block|{           }
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testbytesToCodePointWithInvalidUTF error unexp exception !!!"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUtf8Length ()
specifier|public
name|void
name|testUtf8Length
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"testUtf8Length1 error   !!!"
argument_list|,
literal|1
argument_list|,
name|Text
operator|.
name|utf8Length
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
operator|(
name|char
operator|)
literal|1
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testUtf8Length127 error !!!"
argument_list|,
literal|1
argument_list|,
name|Text
operator|.
name|utf8Length
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
operator|(
name|char
operator|)
literal|127
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testUtf8Length128 error !!!"
argument_list|,
literal|2
argument_list|,
name|Text
operator|.
name|utf8Length
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
operator|(
name|char
operator|)
literal|128
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testUtf8Length193 error !!!"
argument_list|,
literal|2
argument_list|,
name|Text
operator|.
name|utf8Length
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
operator|(
name|char
operator|)
literal|193
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testUtf8Length225 error !!!"
argument_list|,
literal|2
argument_list|,
name|Text
operator|.
name|utf8Length
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
operator|(
name|char
operator|)
literal|225
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testUtf8Length254 error !!!"
argument_list|,
literal|2
argument_list|,
name|Text
operator|.
name|utf8Length
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
operator|(
name|char
operator|)
literal|254
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|TestText
name|test
init|=
operator|new
name|TestText
argument_list|(
literal|"main"
argument_list|)
decl_stmt|;
name|test
operator|.
name|testIO
argument_list|()
expr_stmt|;
name|test
operator|.
name|testCompare
argument_list|()
expr_stmt|;
name|test
operator|.
name|testCoding
argument_list|()
expr_stmt|;
name|test
operator|.
name|testWritable
argument_list|()
expr_stmt|;
name|test
operator|.
name|testFind
argument_list|()
expr_stmt|;
name|test
operator|.
name|testValidate
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

