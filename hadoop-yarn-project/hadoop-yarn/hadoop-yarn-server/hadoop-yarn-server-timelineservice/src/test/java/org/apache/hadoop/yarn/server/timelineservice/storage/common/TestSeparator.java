begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
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
name|assertNull
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
name|assertTrue
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
name|Arrays
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
name|hbase
operator|.
name|util
operator|.
name|Bytes
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_class
DECL|class|TestSeparator
specifier|public
class|class
name|TestSeparator
block|{
DECL|field|villain
specifier|private
specifier|static
name|String
name|villain
init|=
literal|"Dr. Heinz Doofenshmirtz"
decl_stmt|;
DECL|field|special
specifier|private
specifier|static
name|String
name|special
init|=
literal|".   *   |   ?   +   \t   (   )   [   ]   {   }   ^   $  \\ \""
decl_stmt|;
comment|/**    *    */
annotation|@
name|Test
DECL|method|testEncodeDecodeString ()
specifier|public
name|void
name|testEncodeDecodeString
parameter_list|()
block|{
for|for
control|(
name|Separator
name|separator
range|:
name|Separator
operator|.
name|values
argument_list|()
control|)
block|{
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
literal|"!"
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
literal|"?"
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
literal|"&"
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
literal|"+"
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
literal|"\t"
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
literal|"Dr."
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
literal|"Heinz"
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
literal|"Doofenshmirtz"
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
name|villain
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|separator
argument_list|,
name|special
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|separator
operator|.
name|encode
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEncodeDecode (Separator separator, String token)
specifier|private
name|void
name|testEncodeDecode
parameter_list|(
name|Separator
name|separator
parameter_list|,
name|String
name|token
parameter_list|)
block|{
name|String
name|encoded
init|=
name|separator
operator|.
name|encode
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|String
name|decoded
init|=
name|separator
operator|.
name|decode
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
literal|"token:"
operator|+
name|token
operator|+
literal|" separator:"
operator|+
name|separator
operator|+
literal|"."
decl_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|token
argument_list|,
name|decoded
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEncodeDecode ()
specifier|public
name|void
name|testEncodeDecode
parameter_list|()
block|{
name|testEncodeDecode
argument_list|(
literal|"Dr."
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
literal|"Heinz"
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
literal|"Doofenshmirtz"
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
argument_list|,
literal|null
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
literal|"&Perry"
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
argument_list|,
name|Separator
operator|.
name|VALUES
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
literal|"the "
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
literal|"Platypus..."
argument_list|,
operator|(
name|Separator
operator|)
literal|null
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
literal|"The what now ?!?"
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
argument_list|,
name|Separator
operator|.
name|VALUES
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSplits ()
specifier|public
name|void
name|testSplits
parameter_list|()
block|{
name|byte
index|[]
name|maxLongBytes
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|byte
index|[]
name|maxIntBytes
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
for|for
control|(
name|Separator
name|separator
range|:
name|Separator
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|str1
init|=
literal|"cl"
operator|+
name|separator
operator|.
name|getValue
argument_list|()
operator|+
literal|"us"
decl_stmt|;
name|String
name|str2
init|=
name|separator
operator|.
name|getValue
argument_list|()
operator|+
literal|"rst"
decl_stmt|;
name|byte
index|[]
name|sepByteArr
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|separator
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|longVal1Arr
init|=
name|Bytes
operator|.
name|add
argument_list|(
name|sepByteArr
argument_list|,
name|Bytes
operator|.
name|copy
argument_list|(
name|maxLongBytes
argument_list|,
name|sepByteArr
operator|.
name|length
argument_list|,
name|Bytes
operator|.
name|SIZEOF_LONG
operator|-
name|sepByteArr
operator|.
name|length
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|intVal1Arr
init|=
name|Bytes
operator|.
name|add
argument_list|(
name|sepByteArr
argument_list|,
name|Bytes
operator|.
name|copy
argument_list|(
name|maxIntBytes
argument_list|,
name|sepByteArr
operator|.
name|length
argument_list|,
name|Bytes
operator|.
name|SIZEOF_INT
operator|-
name|sepByteArr
operator|.
name|length
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|arr
init|=
name|separator
operator|.
name|join
argument_list|(
name|Bytes
operator|.
name|toBytes
argument_list|(
name|separator
operator|.
name|encode
argument_list|(
name|str1
argument_list|)
argument_list|)
argument_list|,
name|longVal1Arr
argument_list|,
name|Bytes
operator|.
name|toBytes
argument_list|(
name|separator
operator|.
name|encode
argument_list|(
name|str2
argument_list|)
argument_list|)
argument_list|,
name|intVal1Arr
argument_list|)
decl_stmt|;
name|int
index|[]
name|sizes
init|=
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_LONG
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_INT
block|}
decl_stmt|;
name|byte
index|[]
index|[]
name|splits
init|=
name|separator
operator|.
name|split
argument_list|(
name|arr
argument_list|,
name|sizes
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str1
argument_list|,
name|separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Bytes
operator|.
name|toLong
argument_list|(
name|longVal1Arr
argument_list|)
argument_list|,
name|Bytes
operator|.
name|toLong
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str2
argument_list|,
name|separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|2
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Bytes
operator|.
name|toInt
argument_list|(
name|intVal1Arr
argument_list|)
argument_list|,
name|Bytes
operator|.
name|toInt
argument_list|(
name|splits
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|longVal1Arr
operator|=
name|Bytes
operator|.
name|add
argument_list|(
name|Bytes
operator|.
name|copy
argument_list|(
name|maxLongBytes
argument_list|,
literal|0
argument_list|,
name|Bytes
operator|.
name|SIZEOF_LONG
operator|-
name|sepByteArr
operator|.
name|length
argument_list|)
argument_list|,
name|sepByteArr
argument_list|)
expr_stmt|;
name|intVal1Arr
operator|=
name|Bytes
operator|.
name|add
argument_list|(
name|Bytes
operator|.
name|copy
argument_list|(
name|maxIntBytes
argument_list|,
literal|0
argument_list|,
name|Bytes
operator|.
name|SIZEOF_INT
operator|-
name|sepByteArr
operator|.
name|length
argument_list|)
argument_list|,
name|sepByteArr
argument_list|)
expr_stmt|;
name|arr
operator|=
name|separator
operator|.
name|join
argument_list|(
name|Bytes
operator|.
name|toBytes
argument_list|(
name|separator
operator|.
name|encode
argument_list|(
name|str1
argument_list|)
argument_list|)
argument_list|,
name|longVal1Arr
argument_list|,
name|Bytes
operator|.
name|toBytes
argument_list|(
name|separator
operator|.
name|encode
argument_list|(
name|str2
argument_list|)
argument_list|)
argument_list|,
name|intVal1Arr
argument_list|)
expr_stmt|;
name|splits
operator|=
name|separator
operator|.
name|split
argument_list|(
name|arr
argument_list|,
name|sizes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str1
argument_list|,
name|separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Bytes
operator|.
name|toLong
argument_list|(
name|longVal1Arr
argument_list|)
argument_list|,
name|Bytes
operator|.
name|toLong
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str2
argument_list|,
name|separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|2
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Bytes
operator|.
name|toInt
argument_list|(
name|intVal1Arr
argument_list|)
argument_list|,
name|Bytes
operator|.
name|toInt
argument_list|(
name|splits
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|longVal1Arr
operator|=
name|Bytes
operator|.
name|add
argument_list|(
name|sepByteArr
argument_list|,
name|Bytes
operator|.
name|copy
argument_list|(
name|maxLongBytes
argument_list|,
name|sepByteArr
operator|.
name|length
argument_list|,
literal|4
operator|-
name|sepByteArr
operator|.
name|length
argument_list|)
argument_list|,
name|sepByteArr
argument_list|)
expr_stmt|;
name|longVal1Arr
operator|=
name|Bytes
operator|.
name|add
argument_list|(
name|longVal1Arr
argument_list|,
name|Bytes
operator|.
name|copy
argument_list|(
name|maxLongBytes
argument_list|,
literal|4
argument_list|,
literal|3
operator|-
name|sepByteArr
operator|.
name|length
argument_list|)
argument_list|,
name|sepByteArr
argument_list|)
expr_stmt|;
name|arr
operator|=
name|separator
operator|.
name|join
argument_list|(
name|Bytes
operator|.
name|toBytes
argument_list|(
name|separator
operator|.
name|encode
argument_list|(
name|str1
argument_list|)
argument_list|)
argument_list|,
name|longVal1Arr
argument_list|,
name|Bytes
operator|.
name|toBytes
argument_list|(
name|separator
operator|.
name|encode
argument_list|(
name|str2
argument_list|)
argument_list|)
argument_list|,
name|intVal1Arr
argument_list|)
expr_stmt|;
name|splits
operator|=
name|separator
operator|.
name|split
argument_list|(
name|arr
argument_list|,
name|sizes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str1
argument_list|,
name|separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Bytes
operator|.
name|toLong
argument_list|(
name|longVal1Arr
argument_list|)
argument_list|,
name|Bytes
operator|.
name|toLong
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str2
argument_list|,
name|separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|2
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Bytes
operator|.
name|toInt
argument_list|(
name|intVal1Arr
argument_list|)
argument_list|,
name|Bytes
operator|.
name|toInt
argument_list|(
name|splits
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|arr
operator|=
name|separator
operator|.
name|join
argument_list|(
name|Bytes
operator|.
name|toBytes
argument_list|(
name|separator
operator|.
name|encode
argument_list|(
name|str1
argument_list|)
argument_list|)
argument_list|,
name|Bytes
operator|.
name|toBytes
argument_list|(
name|separator
operator|.
name|encode
argument_list|(
name|str2
argument_list|)
argument_list|)
argument_list|,
name|intVal1Arr
argument_list|,
name|longVal1Arr
argument_list|)
expr_stmt|;
name|int
index|[]
name|sizes1
init|=
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_INT
block|,
name|Bytes
operator|.
name|SIZEOF_LONG
block|}
decl_stmt|;
name|splits
operator|=
name|separator
operator|.
name|split
argument_list|(
name|arr
argument_list|,
name|sizes1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str1
argument_list|,
name|separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str2
argument_list|,
name|separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Bytes
operator|.
name|toInt
argument_list|(
name|intVal1Arr
argument_list|)
argument_list|,
name|Bytes
operator|.
name|toInt
argument_list|(
name|splits
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Bytes
operator|.
name|toLong
argument_list|(
name|longVal1Arr
argument_list|)
argument_list|,
name|Bytes
operator|.
name|toLong
argument_list|(
name|splits
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|int
index|[]
name|sizes2
init|=
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_INT
block|,
literal|7
block|}
decl_stmt|;
name|splits
operator|=
name|separator
operator|.
name|split
argument_list|(
name|arr
argument_list|,
name|sizes2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception should have been thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{}
try|try
block|{
name|int
index|[]
name|sizes2
init|=
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
literal|2
block|,
name|Bytes
operator|.
name|SIZEOF_LONG
block|}
decl_stmt|;
name|splits
operator|=
name|separator
operator|.
name|split
argument_list|(
name|arr
argument_list|,
name|sizes2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception should have been thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{}
block|}
block|}
comment|/**    * Simple test to encode and decode using the same separators and confirm that    * we end up with the same as what we started with.    *    * @param token    * @param separators    */
DECL|method|testEncodeDecode (String token, Separator... separators)
specifier|private
specifier|static
name|void
name|testEncodeDecode
parameter_list|(
name|String
name|token
parameter_list|,
name|Separator
modifier|...
name|separators
parameter_list|)
block|{
name|byte
index|[]
name|encoded
init|=
name|Separator
operator|.
name|encode
argument_list|(
name|token
argument_list|,
name|separators
argument_list|)
decl_stmt|;
name|String
name|decoded
init|=
name|Separator
operator|.
name|decode
argument_list|(
name|encoded
argument_list|,
name|separators
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|token
argument_list|,
name|decoded
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJoinStripped ()
specifier|public
name|void
name|testJoinStripped
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|stringList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|stringList
operator|.
name|add
argument_list|(
literal|"nothing"
argument_list|)
expr_stmt|;
name|String
name|joined
init|=
name|Separator
operator|.
name|VALUES
operator|.
name|joinEncoded
argument_list|(
name|stringList
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|split
init|=
name|Separator
operator|.
name|VALUES
operator|.
name|splitEncoded
argument_list|(
name|joined
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|stringList
argument_list|,
name|split
argument_list|)
argument_list|)
expr_stmt|;
name|stringList
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|stringList
operator|.
name|add
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|stringList
operator|.
name|add
argument_list|(
literal|"b?"
argument_list|)
expr_stmt|;
name|stringList
operator|.
name|add
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|joined
operator|=
name|Separator
operator|.
name|VALUES
operator|.
name|joinEncoded
argument_list|(
name|stringList
argument_list|)
expr_stmt|;
name|split
operator|=
name|Separator
operator|.
name|VALUES
operator|.
name|splitEncoded
argument_list|(
name|joined
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|stringList
argument_list|,
name|split
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|stringArray1
init|=
block|{
literal|"else"
block|}
decl_stmt|;
name|joined
operator|=
name|Separator
operator|.
name|VALUES
operator|.
name|joinEncoded
argument_list|(
name|stringArray1
argument_list|)
expr_stmt|;
name|split
operator|=
name|Separator
operator|.
name|VALUES
operator|.
name|splitEncoded
argument_list|(
name|joined
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|stringArray1
argument_list|)
argument_list|,
name|split
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|stringArray2
init|=
block|{
literal|"d"
block|,
literal|"e?"
block|,
literal|"f"
block|}
decl_stmt|;
name|joined
operator|=
name|Separator
operator|.
name|VALUES
operator|.
name|joinEncoded
argument_list|(
name|stringArray2
argument_list|)
expr_stmt|;
name|split
operator|=
name|Separator
operator|.
name|VALUES
operator|.
name|splitEncoded
argument_list|(
name|joined
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|stringArray2
argument_list|)
argument_list|,
name|split
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|empty
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|split
operator|=
name|Separator
operator|.
name|VALUES
operator|.
name|splitEncoded
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|empty
argument_list|,
name|split
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

