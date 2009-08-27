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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_class
DECL|class|TestGenericsUtil
specifier|public
class|class
name|TestGenericsUtil
extends|extends
name|TestCase
block|{
DECL|method|testToArray ()
specifier|public
name|void
name|testToArray
parameter_list|()
block|{
comment|//test a list of size 10
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|Integer
index|[]
name|arr
init|=
name|GenericsUtil
operator|.
name|toArray
argument_list|(
name|list
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
name|arr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|arr
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testWithEmptyList ()
specifier|public
name|void
name|testWithEmptyList
parameter_list|()
block|{
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|arr
init|=
name|GenericsUtil
operator|.
name|toArray
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Empty array should throw exception"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|arr
argument_list|)
expr_stmt|;
comment|//use arr so that compiler will not complain
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|ex
parameter_list|)
block|{
comment|//test case is successful
block|}
block|}
DECL|method|testWithEmptyList2 ()
specifier|public
name|void
name|testWithEmptyList2
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|//this method should not throw IndexOutOfBoundsException
name|String
index|[]
name|arr
init|=
name|GenericsUtil
operator|.
expr|<
name|String
operator|>
name|toArray
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|list
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|arr
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** This class uses generics */
DECL|class|GenericClass
specifier|private
class|class
name|GenericClass
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|dummy
name|T
name|dummy
decl_stmt|;
DECL|field|list
name|List
argument_list|<
name|T
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|add (T item)
name|void
name|add
parameter_list|(
name|T
name|item
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
DECL|method|funcThatUsesToArray ()
name|T
index|[]
name|funcThatUsesToArray
parameter_list|()
block|{
name|T
index|[]
name|arr
init|=
name|GenericsUtil
operator|.
name|toArray
argument_list|(
name|list
argument_list|)
decl_stmt|;
return|return
name|arr
return|;
block|}
block|}
DECL|method|testWithGenericClass ()
specifier|public
name|void
name|testWithGenericClass
parameter_list|()
block|{
name|GenericClass
argument_list|<
name|String
argument_list|>
name|testSubject
init|=
operator|new
name|GenericClass
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|testSubject
operator|.
name|add
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|testSubject
operator|.
name|add
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
try|try
block|{
comment|//this cast would fail, if we had not used GenericsUtil.toArray, since the
comment|//rmethod would return Object[] rather than String[]
name|String
index|[]
name|arr
init|=
name|testSubject
operator|.
name|funcThatUsesToArray
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test1"
argument_list|,
name|arr
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test2"
argument_list|,
name|arr
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"GenericsUtil#toArray() is not working for generic classes"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGenericOptionsParser ()
specifier|public
name|void
name|testGenericOptionsParser
parameter_list|()
throws|throws
name|Exception
block|{
name|GenericOptionsParser
name|parser
init|=
operator|new
name|GenericOptionsParser
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-jt"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|parser
operator|.
name|getRemainingArgs
argument_list|()
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//  test if -D accepts -Dx=y=z
name|parser
operator|=
operator|new
name|GenericOptionsParser
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-Dx=y=z"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parser
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
argument_list|,
literal|"y=z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetClass ()
specifier|public
name|void
name|testGetClass
parameter_list|()
block|{
comment|//test with Integer
name|Integer
name|x
init|=
operator|new
name|Integer
argument_list|(
literal|42
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|Integer
argument_list|>
name|c
init|=
name|GenericsUtil
operator|.
name|getClass
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
name|c
argument_list|)
expr_stmt|;
comment|//test with GenericClass<Integer>
name|GenericClass
argument_list|<
name|Integer
argument_list|>
name|testSubject
init|=
operator|new
name|GenericClass
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|GenericClass
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|c2
init|=
name|GenericsUtil
operator|.
name|getClass
argument_list|(
name|testSubject
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|GenericClass
operator|.
name|class
argument_list|,
name|c2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

