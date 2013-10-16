begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/** Unit tests for ArrayWritable */
end_comment

begin_class
DECL|class|TestArrayWritable
specifier|public
class|class
name|TestArrayWritable
extends|extends
name|TestCase
block|{
DECL|class|TextArrayWritable
specifier|static
class|class
name|TextArrayWritable
extends|extends
name|ArrayWritable
block|{
DECL|method|TextArrayWritable ()
specifier|public
name|TextArrayWritable
parameter_list|()
block|{
name|super
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|TestArrayWritable (String name)
specifier|public
name|TestArrayWritable
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
comment|/**    * If valueClass is undefined, readFields should throw an exception indicating    * that the field is null. Otherwise, readFields should succeed.	    */
DECL|method|testThrowUndefinedValueException ()
specifier|public
name|void
name|testThrowUndefinedValueException
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Get a buffer containing a simple text array
name|Text
index|[]
name|elements
init|=
block|{
operator|new
name|Text
argument_list|(
literal|"zero"
argument_list|)
block|,
operator|new
name|Text
argument_list|(
literal|"one"
argument_list|)
block|,
operator|new
name|Text
argument_list|(
literal|"two"
argument_list|)
block|}
decl_stmt|;
name|TextArrayWritable
name|sourceArray
init|=
operator|new
name|TextArrayWritable
argument_list|()
decl_stmt|;
name|sourceArray
operator|.
name|set
argument_list|(
name|elements
argument_list|)
expr_stmt|;
comment|// Write it to a normal output buffer
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
name|sourceArray
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// Read the output buffer with TextReadable. Since the valueClass is defined,
comment|// this should succeed
name|TextArrayWritable
name|destArray
init|=
operator|new
name|TextArrayWritable
argument_list|()
decl_stmt|;
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
name|destArray
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|Writable
index|[]
name|destElements
init|=
name|destArray
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|destElements
operator|.
name|length
operator|==
name|elements
operator|.
name|length
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
name|elements
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|destElements
index|[
name|i
index|]
argument_list|,
name|elements
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**   * test {@link ArrayWritable} toArray() method    */
DECL|method|testArrayWritableToArray ()
specifier|public
name|void
name|testArrayWritableToArray
parameter_list|()
block|{
name|Text
index|[]
name|elements
init|=
block|{
operator|new
name|Text
argument_list|(
literal|"zero"
argument_list|)
block|,
operator|new
name|Text
argument_list|(
literal|"one"
argument_list|)
block|,
operator|new
name|Text
argument_list|(
literal|"two"
argument_list|)
block|}
decl_stmt|;
name|TextArrayWritable
name|arrayWritable
init|=
operator|new
name|TextArrayWritable
argument_list|()
decl_stmt|;
name|arrayWritable
operator|.
name|set
argument_list|(
name|elements
argument_list|)
expr_stmt|;
name|Object
name|array
init|=
name|arrayWritable
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"TestArrayWritable testArrayWritableToArray error!!! "
argument_list|,
name|array
operator|instanceof
name|Text
index|[]
argument_list|)
expr_stmt|;
name|Text
index|[]
name|destElements
init|=
operator|(
name|Text
index|[]
operator|)
name|array
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
name|elements
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|destElements
index|[
name|i
index|]
argument_list|,
name|elements
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * test {@link ArrayWritable} constructor with null    */
DECL|method|testNullArgument ()
specifier|public
name|void
name|testNullArgument
parameter_list|()
block|{
try|try
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Writable
argument_list|>
name|valueClass
init|=
literal|null
decl_stmt|;
operator|new
name|ArrayWritable
argument_list|(
name|valueClass
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testNullArgument error !!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|exp
parameter_list|)
block|{
comment|//should be for test pass
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testNullArgument error !!!"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * test {@link ArrayWritable} constructor with {@code String[]} as a parameter    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testArrayWritableStringConstructor ()
specifier|public
name|void
name|testArrayWritableStringConstructor
parameter_list|()
block|{
name|String
index|[]
name|original
init|=
block|{
literal|"test1"
block|,
literal|"test2"
block|,
literal|"test3"
block|}
decl_stmt|;
name|ArrayWritable
name|arrayWritable
init|=
operator|new
name|ArrayWritable
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"testArrayWritableStringConstructor class error!!!"
argument_list|,
name|UTF8
operator|.
name|class
argument_list|,
name|arrayWritable
operator|.
name|getValueClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
literal|"testArrayWritableStringConstructor toString error!!!"
argument_list|,
name|original
argument_list|,
name|arrayWritable
operator|.
name|toStrings
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

