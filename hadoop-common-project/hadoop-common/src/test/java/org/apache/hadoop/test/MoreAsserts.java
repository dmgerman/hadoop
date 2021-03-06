begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  * A few more asserts  */
end_comment

begin_class
DECL|class|MoreAsserts
specifier|public
class|class
name|MoreAsserts
block|{
comment|/**    * Assert equivalence for array and iterable    * @param<T> the type of the elements    * @param s the name/message for the collection    * @param expected  the expected array of elements    * @param actual    the actual iterable of elements    */
DECL|method|assertEquals (String s, T[] expected, Iterable<T> actual)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|assertEquals
parameter_list|(
name|String
name|s
parameter_list|,
name|T
index|[]
name|expected
parameter_list|,
name|Iterable
argument_list|<
name|T
argument_list|>
name|actual
parameter_list|)
block|{
name|Iterator
argument_list|<
name|T
argument_list|>
name|it
init|=
name|actual
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|expected
operator|.
name|length
operator|&&
name|it
operator|.
name|hasNext
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Element "
operator|+
name|i
operator|+
literal|" for "
operator|+
name|s
argument_list|,
name|expected
index|[
name|i
index|]
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected more elements"
argument_list|,
name|i
operator|==
name|expected
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected less elements"
argument_list|,
operator|!
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert equality for two iterables    * @param<T> the type of the elements    * @param s    * @param expected    * @param actual    */
DECL|method|assertEquals (String s, Iterable<T> expected, Iterable<T> actual)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|assertEquals
parameter_list|(
name|String
name|s
parameter_list|,
name|Iterable
argument_list|<
name|T
argument_list|>
name|expected
parameter_list|,
name|Iterable
argument_list|<
name|T
argument_list|>
name|actual
parameter_list|)
block|{
name|Iterator
argument_list|<
name|T
argument_list|>
name|ite
init|=
name|expected
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|T
argument_list|>
name|ita
init|=
name|actual
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ite
operator|.
name|hasNext
argument_list|()
operator|&&
name|ita
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Element "
operator|+
name|i
operator|+
literal|" for "
operator|+
name|s
argument_list|,
name|ite
operator|.
name|next
argument_list|()
argument_list|,
name|ita
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected more elements"
argument_list|,
operator|!
name|ite
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected less elements"
argument_list|,
operator|!
name|ita
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

