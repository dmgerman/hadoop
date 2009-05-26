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
name|*
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
name|Configurable
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ReflectionUtils
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
comment|/** Unit tests for Writable. */
end_comment

begin_class
DECL|class|TestWritable
specifier|public
class|class
name|TestWritable
extends|extends
name|TestCase
block|{
DECL|method|TestWritable (String name)
specifier|public
name|TestWritable
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
comment|/** Test 1: Check that SimpleWritable. */
DECL|method|testSimpleWritable ()
specifier|public
name|void
name|testSimpleWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|testWritable
argument_list|(
operator|new
name|SimpleWritable
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testByteWritable ()
specifier|public
name|void
name|testByteWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|testWritable
argument_list|(
operator|new
name|ByteWritable
argument_list|(
operator|(
name|byte
operator|)
literal|128
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleWritable ()
specifier|public
name|void
name|testDoubleWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|testWritable
argument_list|(
operator|new
name|DoubleWritable
argument_list|(
literal|1.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Utility method for testing writables. */
DECL|method|testWritable (Writable before)
specifier|public
specifier|static
name|Writable
name|testWritable
parameter_list|(
name|Writable
name|before
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|testWritable
argument_list|(
name|before
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Utility method for testing writables. */
DECL|method|testWritable (Writable before , Configuration conf)
specifier|public
specifier|static
name|Writable
name|testWritable
parameter_list|(
name|Writable
name|before
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|before
operator|.
name|write
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|dib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|dib
operator|.
name|reset
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|Writable
name|after
init|=
operator|(
name|Writable
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|before
operator|.
name|getClass
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|after
operator|.
name|readFields
argument_list|(
name|dib
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return
name|after
return|;
block|}
block|}
end_class

end_unit

