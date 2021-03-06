begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.wsrs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|wsrs
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
DECL|class|TestParam
specifier|public
class|class
name|TestParam
block|{
DECL|method|test (Param<T> param, String name, String domain, T defaultValue, T validValue, String invalidStrValue, String outOfRangeValue)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|void
name|test
parameter_list|(
name|Param
argument_list|<
name|T
argument_list|>
name|param
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|domain
parameter_list|,
name|T
name|defaultValue
parameter_list|,
name|T
name|validValue
parameter_list|,
name|String
name|invalidStrValue
parameter_list|,
name|String
name|outOfRangeValue
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|name
argument_list|,
name|param
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|domain
argument_list|,
name|param
operator|.
name|getDomain
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|defaultValue
argument_list|,
name|param
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|defaultValue
argument_list|,
name|param
operator|.
name|parseParam
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|defaultValue
argument_list|,
name|param
operator|.
name|parseParam
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|validValue
argument_list|,
name|param
operator|.
name|parseParam
argument_list|(
name|validValue
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|invalidStrValue
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|param
operator|.
name|parseParam
argument_list|(
name|invalidStrValue
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|//NOP
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|outOfRangeValue
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|param
operator|.
name|parseParam
argument_list|(
name|outOfRangeValue
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|//NOP
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testBoolean ()
specifier|public
name|void
name|testBoolean
parameter_list|()
throws|throws
name|Exception
block|{
name|Param
argument_list|<
name|Boolean
argument_list|>
name|param
init|=
operator|new
name|BooleanParam
argument_list|(
literal|"b"
argument_list|,
literal|false
argument_list|)
block|{     }
decl_stmt|;
name|test
argument_list|(
name|param
argument_list|,
literal|"b"
argument_list|,
literal|"a boolean"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|"x"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testByte ()
specifier|public
name|void
name|testByte
parameter_list|()
throws|throws
name|Exception
block|{
name|Param
argument_list|<
name|Byte
argument_list|>
name|param
init|=
operator|new
name|ByteParam
argument_list|(
literal|"B"
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|{     }
decl_stmt|;
name|test
argument_list|(
name|param
argument_list|,
literal|"B"
argument_list|,
literal|"a byte"
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|,
operator|(
name|byte
operator|)
literal|2
argument_list|,
literal|"x"
argument_list|,
literal|"256"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShort ()
specifier|public
name|void
name|testShort
parameter_list|()
throws|throws
name|Exception
block|{
name|Param
argument_list|<
name|Short
argument_list|>
name|param
init|=
operator|new
name|ShortParam
argument_list|(
literal|"S"
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
block|{     }
decl_stmt|;
name|test
argument_list|(
name|param
argument_list|,
literal|"S"
argument_list|,
literal|"a short"
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|"x"
argument_list|,
literal|""
operator|+
operator|(
operator|(
name|int
operator|)
name|Short
operator|.
name|MAX_VALUE
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|param
operator|=
operator|new
name|ShortParam
argument_list|(
literal|"S"
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|8
argument_list|)
block|{     }
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Short
argument_list|(
operator|(
name|short
operator|)
literal|01777
argument_list|)
argument_list|,
name|param
operator|.
name|parse
argument_list|(
literal|"01777"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInteger ()
specifier|public
name|void
name|testInteger
parameter_list|()
throws|throws
name|Exception
block|{
name|Param
argument_list|<
name|Integer
argument_list|>
name|param
init|=
operator|new
name|IntegerParam
argument_list|(
literal|"I"
argument_list|,
literal|1
argument_list|)
block|{     }
decl_stmt|;
name|test
argument_list|(
name|param
argument_list|,
literal|"I"
argument_list|,
literal|"an integer"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|"x"
argument_list|,
literal|""
operator|+
operator|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLong ()
specifier|public
name|void
name|testLong
parameter_list|()
throws|throws
name|Exception
block|{
name|Param
argument_list|<
name|Long
argument_list|>
name|param
init|=
operator|new
name|LongParam
argument_list|(
literal|"L"
argument_list|,
literal|1L
argument_list|)
block|{     }
decl_stmt|;
name|test
argument_list|(
name|param
argument_list|,
literal|"L"
argument_list|,
literal|"a long"
argument_list|,
literal|1L
argument_list|,
literal|2L
argument_list|,
literal|"x"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|enum|ENUM
specifier|public
enum|enum
name|ENUM
block|{
DECL|enumConstant|FOO
DECL|enumConstant|BAR
name|FOO
block|,
name|BAR
block|}
annotation|@
name|Test
DECL|method|testEnum ()
specifier|public
name|void
name|testEnum
parameter_list|()
throws|throws
name|Exception
block|{
name|EnumParam
argument_list|<
name|ENUM
argument_list|>
name|param
init|=
operator|new
name|EnumParam
argument_list|<
name|ENUM
argument_list|>
argument_list|(
literal|"e"
argument_list|,
name|ENUM
operator|.
name|class
argument_list|,
name|ENUM
operator|.
name|FOO
argument_list|)
block|{     }
decl_stmt|;
name|test
argument_list|(
name|param
argument_list|,
literal|"e"
argument_list|,
literal|"FOO,BAR"
argument_list|,
name|ENUM
operator|.
name|FOO
argument_list|,
name|ENUM
operator|.
name|BAR
argument_list|,
literal|"x"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testString ()
specifier|public
name|void
name|testString
parameter_list|()
throws|throws
name|Exception
block|{
name|Param
argument_list|<
name|String
argument_list|>
name|param
init|=
operator|new
name|StringParam
argument_list|(
literal|"s"
argument_list|,
literal|"foo"
argument_list|)
block|{     }
decl_stmt|;
name|test
argument_list|(
name|param
argument_list|,
literal|"s"
argument_list|,
literal|"a string"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegEx ()
specifier|public
name|void
name|testRegEx
parameter_list|()
throws|throws
name|Exception
block|{
name|Param
argument_list|<
name|String
argument_list|>
name|param
init|=
operator|new
name|StringParam
argument_list|(
literal|"r"
argument_list|,
literal|"aa"
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|".."
argument_list|)
argument_list|)
block|{     }
decl_stmt|;
name|test
argument_list|(
name|param
argument_list|,
literal|"r"
argument_list|,
literal|".."
argument_list|,
literal|"aa"
argument_list|,
literal|"bb"
argument_list|,
literal|"c"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

