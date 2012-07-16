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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|UnitTestcaseTimeLimit
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

begin_class
DECL|class|TestStringUtils
specifier|public
class|class
name|TestStringUtils
extends|extends
name|UnitTestcaseTimeLimit
block|{
DECL|field|NULL_STR
specifier|final
specifier|private
specifier|static
name|String
name|NULL_STR
init|=
literal|null
decl_stmt|;
DECL|field|EMPTY_STR
specifier|final
specifier|private
specifier|static
name|String
name|EMPTY_STR
init|=
literal|""
decl_stmt|;
DECL|field|STR_WO_SPECIAL_CHARS
specifier|final
specifier|private
specifier|static
name|String
name|STR_WO_SPECIAL_CHARS
init|=
literal|"AB"
decl_stmt|;
DECL|field|STR_WITH_COMMA
specifier|final
specifier|private
specifier|static
name|String
name|STR_WITH_COMMA
init|=
literal|"A,B"
decl_stmt|;
DECL|field|ESCAPED_STR_WITH_COMMA
specifier|final
specifier|private
specifier|static
name|String
name|ESCAPED_STR_WITH_COMMA
init|=
literal|"A\\,B"
decl_stmt|;
DECL|field|STR_WITH_ESCAPE
specifier|final
specifier|private
specifier|static
name|String
name|STR_WITH_ESCAPE
init|=
literal|"AB\\"
decl_stmt|;
DECL|field|ESCAPED_STR_WITH_ESCAPE
specifier|final
specifier|private
specifier|static
name|String
name|ESCAPED_STR_WITH_ESCAPE
init|=
literal|"AB\\\\"
decl_stmt|;
DECL|field|STR_WITH_BOTH2
specifier|final
specifier|private
specifier|static
name|String
name|STR_WITH_BOTH2
init|=
literal|",A\\,,B\\\\,"
decl_stmt|;
DECL|field|ESCAPED_STR_WITH_BOTH2
specifier|final
specifier|private
specifier|static
name|String
name|ESCAPED_STR_WITH_BOTH2
init|=
literal|"\\,A\\\\\\,\\,B\\\\\\\\\\,"
decl_stmt|;
annotation|@
name|Test
DECL|method|testEscapeString ()
specifier|public
name|void
name|testEscapeString
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|NULL_STR
argument_list|,
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|NULL_STR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EMPTY_STR
argument_list|,
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|EMPTY_STR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STR_WO_SPECIAL_CHARS
argument_list|,
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|STR_WO_SPECIAL_CHARS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ESCAPED_STR_WITH_COMMA
argument_list|,
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|STR_WITH_COMMA
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ESCAPED_STR_WITH_ESCAPE
argument_list|,
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|STR_WITH_ESCAPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ESCAPED_STR_WITH_BOTH2
argument_list|,
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|STR_WITH_BOTH2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSplit ()
specifier|public
name|void
name|testSplit
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|NULL_STR
argument_list|,
name|StringUtils
operator|.
name|split
argument_list|(
name|NULL_STR
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|splits
init|=
name|StringUtils
operator|.
name|split
argument_list|(
name|EMPTY_STR
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|splits
operator|=
name|StringUtils
operator|.
name|split
argument_list|(
literal|",,"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|splits
operator|=
name|StringUtils
operator|.
name|split
argument_list|(
name|STR_WO_SPECIAL_CHARS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STR_WO_SPECIAL_CHARS
argument_list|,
name|splits
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|splits
operator|=
name|StringUtils
operator|.
name|split
argument_list|(
name|STR_WITH_COMMA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A"
argument_list|,
name|splits
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"B"
argument_list|,
name|splits
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|splits
operator|=
name|StringUtils
operator|.
name|split
argument_list|(
name|ESCAPED_STR_WITH_COMMA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ESCAPED_STR_WITH_COMMA
argument_list|,
name|splits
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|splits
operator|=
name|StringUtils
operator|.
name|split
argument_list|(
name|STR_WITH_ESCAPE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STR_WITH_ESCAPE
argument_list|,
name|splits
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|splits
operator|=
name|StringUtils
operator|.
name|split
argument_list|(
name|STR_WITH_BOTH2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EMPTY_STR
argument_list|,
name|splits
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A\\,"
argument_list|,
name|splits
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"B\\\\"
argument_list|,
name|splits
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|splits
operator|=
name|StringUtils
operator|.
name|split
argument_list|(
name|ESCAPED_STR_WITH_BOTH2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ESCAPED_STR_WITH_BOTH2
argument_list|,
name|splits
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleSplit ()
specifier|public
name|void
name|testSimpleSplit
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
index|[]
name|TO_TEST
init|=
block|{
literal|"a/b/c"
block|,
literal|"a/b/c////"
block|,
literal|"///a/b/c"
block|,
literal|""
block|,
literal|"/"
block|,
literal|"////"
block|}
decl_stmt|;
for|for
control|(
name|String
name|testSubject
range|:
name|TO_TEST
control|)
block|{
name|assertArrayEquals
argument_list|(
literal|"Testing '"
operator|+
name|testSubject
operator|+
literal|"'"
argument_list|,
name|testSubject
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|split
argument_list|(
name|testSubject
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUnescapeString ()
specifier|public
name|void
name|testUnescapeString
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|NULL_STR
argument_list|,
name|StringUtils
operator|.
name|unEscapeString
argument_list|(
name|NULL_STR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EMPTY_STR
argument_list|,
name|StringUtils
operator|.
name|unEscapeString
argument_list|(
name|EMPTY_STR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STR_WO_SPECIAL_CHARS
argument_list|,
name|StringUtils
operator|.
name|unEscapeString
argument_list|(
name|STR_WO_SPECIAL_CHARS
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|StringUtils
operator|.
name|unEscapeString
argument_list|(
name|STR_WITH_COMMA
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
name|STR_WITH_COMMA
argument_list|,
name|StringUtils
operator|.
name|unEscapeString
argument_list|(
name|ESCAPED_STR_WITH_COMMA
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|StringUtils
operator|.
name|unEscapeString
argument_list|(
name|STR_WITH_ESCAPE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
name|STR_WITH_ESCAPE
argument_list|,
name|StringUtils
operator|.
name|unEscapeString
argument_list|(
name|ESCAPED_STR_WITH_ESCAPE
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|StringUtils
operator|.
name|unEscapeString
argument_list|(
name|STR_WITH_BOTH2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
name|STR_WITH_BOTH2
argument_list|,
name|StringUtils
operator|.
name|unEscapeString
argument_list|(
name|ESCAPED_STR_WITH_BOTH2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTraditionalBinaryPrefix ()
specifier|public
name|void
name|testTraditionalBinaryPrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|symbol
init|=
block|{
literal|"k"
block|,
literal|"m"
block|,
literal|"g"
block|,
literal|"t"
block|,
literal|"p"
block|,
literal|"e"
block|}
decl_stmt|;
name|long
name|m
init|=
literal|1024
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|symbol
control|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|0
operator|+
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|1
operator|+
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|*=
literal|1024
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1024L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"1k"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1024L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"-1k"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1259520L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"1230K"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1259520L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"-1230K"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|104857600L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"100m"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|104857600L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"-100M"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|956703965184L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"891g"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|956703965184L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"-891G"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|501377302265856L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"456t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|501377302265856L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"-456T"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11258999068426240L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"10p"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|11258999068426240L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"-10P"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1152921504606846976L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"1e"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1152921504606846976L
argument_list|,
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
literal|"-1E"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|tooLargeNumStr
init|=
literal|"10e"
decl_stmt|;
try|try
block|{
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
name|tooLargeNumStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Test passed for a number "
operator|+
name|tooLargeNumStr
operator|+
literal|" too large"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|tooLargeNumStr
operator|+
literal|" does not fit in a Long"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|tooSmallNumStr
init|=
literal|"-10e"
decl_stmt|;
try|try
block|{
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
name|tooSmallNumStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Test passed for a number "
operator|+
name|tooSmallNumStr
operator|+
literal|" too small"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|tooSmallNumStr
operator|+
literal|" does not fit in a Long"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|invalidFormatNumStr
init|=
literal|"10kb"
decl_stmt|;
name|char
name|invalidPrefix
init|=
literal|'b'
decl_stmt|;
try|try
block|{
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|string2long
argument_list|(
name|invalidFormatNumStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Test passed for a number "
operator|+
name|invalidFormatNumStr
operator|+
literal|" has invalid format"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Invalid size prefix '"
operator|+
name|invalidPrefix
operator|+
literal|"' in '"
operator|+
name|invalidFormatNumStr
operator|+
literal|"'. Allowed prefixes are k, m, g, t, p, e(case insensitive)"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testJoin ()
specifier|public
name|void
name|testJoin
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|s
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|s
operator|.
name|add
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|":"
argument_list|,
name|s
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|":"
argument_list|,
name|s
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a:b"
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|":"
argument_list|,
name|s
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a:b:c"
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|":"
argument_list|,
name|s
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTrimmedStrings ()
specifier|public
name|void
name|testGetTrimmedStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|compactDirList
init|=
literal|"/spindle1/hdfs,/spindle2/hdfs,/spindle3/hdfs"
decl_stmt|;
name|String
name|spacedDirList
init|=
literal|"/spindle1/hdfs, /spindle2/hdfs, /spindle3/hdfs"
decl_stmt|;
name|String
name|pathologicalDirList1
init|=
literal|" /spindle1/hdfs  ,  /spindle2/hdfs ,/spindle3/hdfs "
decl_stmt|;
name|String
name|pathologicalDirList2
init|=
literal|" /spindle1/hdfs  ,  /spindle2/hdfs ,/spindle3/hdfs , "
decl_stmt|;
name|String
name|emptyList1
init|=
literal|""
decl_stmt|;
name|String
name|emptyList2
init|=
literal|"   "
decl_stmt|;
name|String
index|[]
name|expectedArray
init|=
block|{
literal|"/spindle1/hdfs"
block|,
literal|"/spindle2/hdfs"
block|,
literal|"/spindle3/hdfs"
block|}
decl_stmt|;
name|String
index|[]
name|emptyArray
init|=
block|{}
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedArray
argument_list|,
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|compactDirList
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedArray
argument_list|,
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|spacedDirList
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedArray
argument_list|,
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|pathologicalDirList1
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedArray
argument_list|,
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|pathologicalDirList2
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|emptyArray
argument_list|,
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|emptyList1
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|emptyArray
argument_list|,
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|emptyList2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCamelize ()
specifier|public
name|void
name|testCamelize
parameter_list|()
block|{
comment|// common use cases
name|assertEquals
argument_list|(
literal|"Map"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"MAP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"JobSetup"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"JOB_SETUP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"SomeStuff"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"some_stuff"
argument_list|)
argument_list|)
expr_stmt|;
comment|// sanity checks for ascii alphabet against unexpected locale issues.
name|assertEquals
argument_list|(
literal|"Aa"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"aA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bb"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"bB"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Cc"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"cC"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Dd"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"dD"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ee"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"eE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ff"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"fF"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Gg"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"gG"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hh"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"hH"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ii"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"iI"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Jj"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"jJ"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Kk"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"kK"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ll"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"lL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Mm"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"mM"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nn"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"nN"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Oo"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"oO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Pp"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"pP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Qq"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"qQ"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Rr"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"rR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ss"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"sS"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Tt"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"tT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Uu"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"uU"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Vv"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"vV"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ww"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"wW"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Xx"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"xX"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Yy"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"yY"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Zz"
argument_list|,
name|StringUtils
operator|.
name|camelize
argument_list|(
literal|"zZ"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStringToURI ()
specifier|public
name|void
name|testStringToURI
parameter_list|()
block|{
name|String
index|[]
name|str
init|=
operator|new
name|String
index|[]
block|{
literal|"file://"
block|}
decl_stmt|;
try|try
block|{
name|StringUtils
operator|.
name|stringToURI
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Ignoring URISyntaxException while creating URI from string file://"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Failed to create uri for file://"
argument_list|,
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Benchmark for StringUtils split
DECL|method|main (String []args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
specifier|final
name|String
name|TO_SPLIT
init|=
literal|"foo,bar,baz,blah,blah"
decl_stmt|;
for|for
control|(
name|boolean
name|useOurs
range|:
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
control|)
block|{
for|for
control|(
name|int
name|outer
init|=
literal|0
init|;
name|outer
operator|<
literal|10
condition|;
name|outer
operator|++
control|)
block|{
name|long
name|st
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|int
name|components
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|inner
init|=
literal|0
init|;
name|inner
operator|<
literal|1000000
condition|;
name|inner
operator|++
control|)
block|{
name|String
index|[]
name|res
decl_stmt|;
if|if
condition|(
name|useOurs
condition|)
block|{
name|res
operator|=
name|StringUtils
operator|.
name|split
argument_list|(
name|TO_SPLIT
argument_list|,
literal|','
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|=
name|TO_SPLIT
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
comment|// be sure to use res, otherwise might be optimized out
name|components
operator|+=
name|res
operator|.
name|length
expr_stmt|;
block|}
name|long
name|et
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|outer
operator|>
literal|3
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
name|useOurs
condition|?
literal|"StringUtils impl"
else|:
literal|"Java impl"
operator|)
operator|+
literal|" #"
operator|+
name|outer
operator|+
literal|":"
operator|+
operator|(
name|et
operator|-
name|st
operator|)
operator|/
literal|1000000
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

