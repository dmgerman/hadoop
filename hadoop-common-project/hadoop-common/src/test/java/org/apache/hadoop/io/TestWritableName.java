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
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|conf
operator|.
name|Configuration
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
comment|/** Unit tests for WritableName. */
end_comment

begin_class
DECL|class|TestWritableName
specifier|public
class|class
name|TestWritableName
extends|extends
name|TestCase
block|{
DECL|method|TestWritableName (String name)
specifier|public
name|TestWritableName
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
comment|/** Example class used in test cases below. */
DECL|class|SimpleWritable
specifier|public
specifier|static
class|class
name|SimpleWritable
implements|implements
name|Writable
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
DECL|field|state
name|int
name|state
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|()
decl_stmt|;
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|state
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
DECL|method|read (DataInput in)
specifier|public
specifier|static
name|SimpleWritable
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleWritable
name|result
init|=
operator|new
name|SimpleWritable
argument_list|()
decl_stmt|;
name|result
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Required by test code, below. */
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SimpleWritable
operator|)
condition|)
return|return
literal|false
return|;
name|SimpleWritable
name|other
init|=
operator|(
name|SimpleWritable
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|state
operator|==
name|other
operator|.
name|state
return|;
block|}
block|}
DECL|field|testName
specifier|private
specifier|static
specifier|final
name|String
name|testName
init|=
literal|"mystring"
decl_stmt|;
DECL|method|testGoodName ()
specifier|public
name|void
name|testGoodName
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|test
init|=
name|WritableName
operator|.
name|getClass
argument_list|(
literal|"long"
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|test
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetName ()
specifier|public
name|void
name|testSetName
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|WritableName
operator|.
name|setName
argument_list|(
name|SimpleWritable
operator|.
name|class
argument_list|,
name|testName
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|test
init|=
name|WritableName
operator|.
name|getClass
argument_list|(
name|testName
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|equals
argument_list|(
name|SimpleWritable
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddName ()
specifier|public
name|void
name|testAddName
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|String
name|altName
init|=
name|testName
operator|+
literal|".alt"
decl_stmt|;
name|WritableName
operator|.
name|addName
argument_list|(
name|SimpleWritable
operator|.
name|class
argument_list|,
name|altName
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|test
init|=
name|WritableName
operator|.
name|getClass
argument_list|(
name|altName
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|equals
argument_list|(
name|SimpleWritable
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// check original name still works
name|test
operator|=
name|WritableName
operator|.
name|getClass
argument_list|(
name|testName
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|equals
argument_list|(
name|SimpleWritable
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBadName ()
specifier|public
name|void
name|testBadName
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
try|try
block|{
name|WritableName
operator|.
name|getClass
argument_list|(
literal|"unknown_junk"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|matches
argument_list|(
literal|".*unknown_junk.*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

