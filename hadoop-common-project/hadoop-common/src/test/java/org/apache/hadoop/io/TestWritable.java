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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|assertNotNull
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

begin_comment
comment|/** Unit tests for Writable. */
end_comment

begin_class
DECL|class|TestWritable
specifier|public
class|class
name|TestWritable
block|{
DECL|field|TEST_CONFIG_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|TEST_CONFIG_PARAM
init|=
literal|"frob.test"
decl_stmt|;
DECL|field|TEST_CONFIG_VALUE
specifier|private
specifier|static
specifier|final
name|String
name|TEST_CONFIG_VALUE
init|=
literal|"test"
decl_stmt|;
DECL|field|TEST_WRITABLE_CONFIG_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|TEST_WRITABLE_CONFIG_PARAM
init|=
literal|"test.writable"
decl_stmt|;
DECL|field|TEST_WRITABLE_CONFIG_VALUE
specifier|private
specifier|static
specifier|final
name|String
name|TEST_WRITABLE_CONFIG_VALUE
init|=
name|TEST_CONFIG_VALUE
decl_stmt|;
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
DECL|class|SimpleWritableComparable
specifier|public
specifier|static
class|class
name|SimpleWritableComparable
extends|extends
name|SimpleWritable
implements|implements
name|WritableComparable
argument_list|<
name|SimpleWritableComparable
argument_list|>
implements|,
name|Configurable
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|SimpleWritableComparable ()
specifier|public
name|SimpleWritableComparable
parameter_list|()
block|{}
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|this
operator|.
name|conf
return|;
block|}
DECL|method|compareTo (SimpleWritableComparable o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|SimpleWritableComparable
name|o
parameter_list|)
block|{
return|return
name|this
operator|.
name|state
operator|-
name|o
operator|.
name|state
return|;
block|}
block|}
comment|/** Test 1: Check that SimpleWritable. */
annotation|@
name|Test
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
annotation|@
name|Test
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
annotation|@
name|Test
DECL|method|testShortWritable ()
specifier|public
name|void
name|testShortWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|testWritable
argument_list|(
operator|new
name|ShortWritable
argument_list|(
operator|(
name|byte
operator|)
literal|256
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
DECL|class|FrobComparator
specifier|private
specifier|static
class|class
name|FrobComparator
extends|extends
name|WritableComparator
block|{
DECL|method|FrobComparator ()
specifier|public
name|FrobComparator
parameter_list|()
block|{
name|super
argument_list|(
name|Frob
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|compare (byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|b1
parameter_list|,
name|int
name|s1
parameter_list|,
name|int
name|l1
parameter_list|,
name|byte
index|[]
name|b2
parameter_list|,
name|int
name|s2
parameter_list|,
name|int
name|l2
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|class|Frob
specifier|private
specifier|static
class|class
name|Frob
implements|implements
name|WritableComparable
argument_list|<
name|Frob
argument_list|>
block|{
static|static
block|{
comment|// register default comparator
name|WritableComparator
operator|.
name|define
argument_list|(
name|Frob
operator|.
name|class
argument_list|,
operator|new
name|FrobComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|write (DataOutput out)
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{}
DECL|method|readFields (DataInput in)
annotation|@
name|Override
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{}
DECL|method|compareTo (Frob o)
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|Frob
name|o
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/** Test that comparator is defined and configured. */
DECL|method|testGetComparator ()
specifier|public
specifier|static
name|void
name|testGetComparator
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
comment|// Without conf.
name|WritableComparator
name|frobComparator
init|=
name|WritableComparator
operator|.
name|get
argument_list|(
name|Frob
operator|.
name|class
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|frobComparator
operator|instanceof
name|FrobComparator
operator|)
assert|;
name|assertNotNull
argument_list|(
name|frobComparator
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|frobComparator
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|TEST_CONFIG_PARAM
argument_list|)
argument_list|)
expr_stmt|;
comment|// With conf.
name|conf
operator|.
name|set
argument_list|(
name|TEST_CONFIG_PARAM
argument_list|,
name|TEST_CONFIG_VALUE
argument_list|)
expr_stmt|;
name|frobComparator
operator|=
name|WritableComparator
operator|.
name|get
argument_list|(
name|Frob
operator|.
name|class
argument_list|,
name|conf
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|frobComparator
operator|instanceof
name|FrobComparator
operator|)
assert|;
name|assertNotNull
argument_list|(
name|frobComparator
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|TEST_CONFIG_PARAM
argument_list|)
argument_list|,
name|TEST_CONFIG_VALUE
argument_list|)
expr_stmt|;
comment|// Without conf. should reuse configuration.
name|frobComparator
operator|=
name|WritableComparator
operator|.
name|get
argument_list|(
name|Frob
operator|.
name|class
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|frobComparator
operator|instanceof
name|FrobComparator
operator|)
assert|;
name|assertNotNull
argument_list|(
name|frobComparator
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|TEST_CONFIG_PARAM
argument_list|)
argument_list|,
name|TEST_CONFIG_VALUE
argument_list|)
expr_stmt|;
comment|// New conf. should use new configuration.
name|frobComparator
operator|=
name|WritableComparator
operator|.
name|get
argument_list|(
name|Frob
operator|.
name|class
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|frobComparator
operator|instanceof
name|FrobComparator
operator|)
assert|;
name|assertNotNull
argument_list|(
name|frobComparator
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|frobComparator
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|TEST_CONFIG_PARAM
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test a user comparator that relies on deserializing both arguments for each    * compare.    */
annotation|@
name|Test
DECL|method|testShortWritableComparator ()
specifier|public
name|void
name|testShortWritableComparator
parameter_list|()
throws|throws
name|Exception
block|{
name|ShortWritable
name|writable1
init|=
operator|new
name|ShortWritable
argument_list|(
operator|(
name|short
operator|)
literal|256
argument_list|)
decl_stmt|;
name|ShortWritable
name|writable2
init|=
operator|new
name|ShortWritable
argument_list|(
operator|(
name|short
operator|)
literal|128
argument_list|)
decl_stmt|;
name|ShortWritable
name|writable3
init|=
operator|new
name|ShortWritable
argument_list|(
operator|(
name|short
operator|)
literal|256
argument_list|)
decl_stmt|;
specifier|final
name|String
name|SHOULD_NOT_MATCH_WITH_RESULT_ONE
init|=
literal|"Result should be 1, should not match the writables"
decl_stmt|;
name|assertTrue
argument_list|(
name|SHOULD_NOT_MATCH_WITH_RESULT_ONE
argument_list|,
name|writable1
operator|.
name|compareTo
argument_list|(
name|writable2
argument_list|)
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SHOULD_NOT_MATCH_WITH_RESULT_ONE
argument_list|,
name|WritableComparator
operator|.
name|get
argument_list|(
name|ShortWritable
operator|.
name|class
argument_list|)
operator|.
name|compare
argument_list|(
name|writable1
argument_list|,
name|writable2
argument_list|)
operator|==
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|SHOULD_NOT_MATCH_WITH_RESULT_MINUS_ONE
init|=
literal|"Result should be -1, should not match the writables"
decl_stmt|;
name|assertTrue
argument_list|(
name|SHOULD_NOT_MATCH_WITH_RESULT_MINUS_ONE
argument_list|,
name|writable2
operator|.
name|compareTo
argument_list|(
name|writable1
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SHOULD_NOT_MATCH_WITH_RESULT_MINUS_ONE
argument_list|,
name|WritableComparator
operator|.
name|get
argument_list|(
name|ShortWritable
operator|.
name|class
argument_list|)
operator|.
name|compare
argument_list|(
name|writable2
argument_list|,
name|writable1
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|SHOULD_MATCH
init|=
literal|"Result should be 0, should match the writables"
decl_stmt|;
name|assertTrue
argument_list|(
name|SHOULD_MATCH
argument_list|,
name|writable1
operator|.
name|compareTo
argument_list|(
name|writable1
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SHOULD_MATCH
argument_list|,
name|WritableComparator
operator|.
name|get
argument_list|(
name|ShortWritable
operator|.
name|class
argument_list|)
operator|.
name|compare
argument_list|(
name|writable1
argument_list|,
name|writable3
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that Writable's are configured by Comparator.    */
annotation|@
name|Test
DECL|method|testConfigurableWritableComparator ()
specifier|public
name|void
name|testConfigurableWritableComparator
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
name|conf
operator|.
name|set
argument_list|(
name|TEST_WRITABLE_CONFIG_PARAM
argument_list|,
name|TEST_WRITABLE_CONFIG_VALUE
argument_list|)
expr_stmt|;
name|WritableComparator
name|wc
init|=
name|WritableComparator
operator|.
name|get
argument_list|(
name|SimpleWritableComparable
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|SimpleWritableComparable
name|key
init|=
operator|(
operator|(
name|SimpleWritableComparable
operator|)
name|wc
operator|.
name|newKey
argument_list|()
operator|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|wc
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|key
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|key
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|TEST_WRITABLE_CONFIG_PARAM
argument_list|)
argument_list|,
name|TEST_WRITABLE_CONFIG_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

