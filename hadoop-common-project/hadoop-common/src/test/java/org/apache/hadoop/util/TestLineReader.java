begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
name|ByteArrayInputStream
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
name|io
operator|.
name|Text
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
name|LineReader
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
name|junit
operator|.
name|Assert
import|;
end_import

begin_class
DECL|class|TestLineReader
specifier|public
class|class
name|TestLineReader
block|{
DECL|field|lineReader
specifier|private
name|LineReader
name|lineReader
decl_stmt|;
DECL|field|TestData
specifier|private
name|String
name|TestData
decl_stmt|;
DECL|field|Delimiter
specifier|private
name|String
name|Delimiter
decl_stmt|;
DECL|field|line
specifier|private
name|Text
name|line
decl_stmt|;
annotation|@
name|Test
DECL|method|testCustomDelimiter ()
specifier|public
name|void
name|testCustomDelimiter
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* TEST_1      * The test scenario is the tail of the buffer      * equals the starting character/s of delimiter      *       * The Test Data is such that,      *         * 1) we will have "</entity>" as delimiter        *        * 2) The tail of the current buffer would be "</"      *    which matches with the starting character sequence of delimiter.      *          * 3) The Head of the next buffer would be   "id>"       *    which does NOT match with the remaining characters of delimiter.      *         * 4) Input data would be prefixed by char 'a'       *    about numberOfCharToFillTheBuffer times.      *    So that, one iteration to buffer the input data,      *    would end at '</' ie equals starting 2 char of delimiter        *           * 5) For this we would take BufferSize as 64 * 1024;      *       * Check Condition      *  In the second key value pair, the value should contain       *  "</"  from currentToken and      *  "id>" from next token      */
name|Delimiter
operator|=
literal|"</entity>"
expr_stmt|;
name|String
name|CurrentBufferTailToken
init|=
literal|"</entity><entity><id>Gelesh</"
decl_stmt|;
comment|// Ending part of Input Data Buffer
comment|// It contains '</' ie delimiter character
name|String
name|NextBufferHeadToken
init|=
literal|"id><name>Omathil</name></entity>"
decl_stmt|;
comment|// Supposing the start of next buffer is this
name|String
name|Expected
init|=
operator|(
name|CurrentBufferTailToken
operator|+
name|NextBufferHeadToken
operator|)
operator|.
name|replace
argument_list|(
name|Delimiter
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|// Expected ,must capture from both the buffer, excluding Delimiter
name|String
name|TestPartOfInput
init|=
name|CurrentBufferTailToken
operator|+
name|NextBufferHeadToken
decl_stmt|;
name|int
name|BufferSize
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
name|int
name|numberOfCharToFillTheBuffer
init|=
name|BufferSize
operator|-
name|CurrentBufferTailToken
operator|.
name|length
argument_list|()
decl_stmt|;
name|StringBuilder
name|fillerString
init|=
operator|new
name|StringBuilder
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
name|numberOfCharToFillTheBuffer
condition|;
name|i
operator|++
control|)
block|{
name|fillerString
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
comment|// char 'a' as a filler for the test string
block|}
name|TestData
operator|=
name|fillerString
operator|+
name|TestPartOfInput
expr_stmt|;
name|lineReader
operator|=
operator|new
name|LineReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|TestData
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|Delimiter
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|line
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fillerString
operator|.
name|toString
argument_list|()
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Expected
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|/*TEST_2      * The test scenario is such that,      * the character/s preceding the delimiter,      * equals the starting character/s of delimiter      */
name|Delimiter
operator|=
literal|"record"
expr_stmt|;
name|StringBuilder
name|TestStringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|TestStringBuilder
operator|.
name|append
argument_list|(
name|Delimiter
operator|+
literal|"Kerala "
argument_list|)
expr_stmt|;
name|TestStringBuilder
operator|.
name|append
argument_list|(
name|Delimiter
operator|+
literal|"Bangalore"
argument_list|)
expr_stmt|;
name|TestStringBuilder
operator|.
name|append
argument_list|(
name|Delimiter
operator|+
literal|" North Korea"
argument_list|)
expr_stmt|;
name|TestStringBuilder
operator|.
name|append
argument_list|(
name|Delimiter
operator|+
name|Delimiter
operator|+
literal|"Guantanamo"
argument_list|)
expr_stmt|;
name|TestStringBuilder
operator|.
name|append
argument_list|(
name|Delimiter
operator|+
literal|"ecord"
operator|+
literal|"recor"
operator|+
literal|"core"
argument_list|)
expr_stmt|;
comment|//~EOF with 're'
name|TestData
operator|=
name|TestStringBuilder
operator|.
name|toString
argument_list|()
expr_stmt|;
name|lineReader
operator|=
operator|new
name|LineReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|TestData
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|Delimiter
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Kerala "
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Bangalore"
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|" North Korea"
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Guantanamo"
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|(
literal|"ecord"
operator|+
literal|"recor"
operator|+
literal|"core"
operator|)
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test 3
comment|// The test scenario is such that,
comment|// aaaabccc split by aaab
name|TestData
operator|=
literal|"aaaabccc"
expr_stmt|;
name|Delimiter
operator|=
literal|"aaab"
expr_stmt|;
name|lineReader
operator|=
operator|new
name|LineReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|TestData
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|Delimiter
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|lineReader
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ccc"
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

