begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.serializer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|serializer
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|TestGenericWritable
operator|.
name|CONF_TEST_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|TestGenericWritable
operator|.
name|CONF_TEST_VALUE
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
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|Schema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|util
operator|.
name|Utf8
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
name|io
operator|.
name|DataInputBuffer
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
name|DataOutputBuffer
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
name|RawComparator
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
name|io
operator|.
name|WritableComparable
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
name|serializer
operator|.
name|avro
operator|.
name|AvroSerialization
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
name|serializer
operator|.
name|avro
operator|.
name|AvroGenericSerialization
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
name|GenericsUtil
import|;
end_import

begin_comment
comment|/**  * Test the getRawComparator API of the various serialization systems.  */
end_comment

begin_class
DECL|class|TestRawComparators
specifier|public
class|class
name|TestRawComparators
extends|extends
name|TestCase
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
block|}
comment|/** A WritableComparable that is guaranteed to use the    * generic WritableComparator.    */
DECL|class|FooWritable
specifier|public
specifier|static
class|class
name|FooWritable
implements|implements
name|WritableComparable
argument_list|<
name|FooWritable
argument_list|>
block|{
DECL|field|val
specifier|public
name|long
name|val
decl_stmt|;
DECL|method|FooWritable ()
specifier|public
name|FooWritable
parameter_list|()
block|{
name|this
operator|.
name|val
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|FooWritable (long v)
specifier|public
name|FooWritable
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|this
operator|.
name|val
operator|=
name|v
expr_stmt|;
block|}
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
name|writeLong
argument_list|(
name|val
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
name|val
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
DECL|method|compareTo (FooWritable other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|FooWritable
name|other
parameter_list|)
block|{
return|return
operator|new
name|Long
argument_list|(
name|val
argument_list|)
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|val
argument_list|)
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|runComparisonTest (Object low, Object high)
specifier|private
name|void
name|runComparisonTest
parameter_list|(
name|Object
name|low
parameter_list|,
name|Object
name|high
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
name|SerializationBase
operator|.
name|getMetadataFromClass
argument_list|(
name|GenericsUtil
operator|.
name|getClass
argument_list|(
name|low
argument_list|)
argument_list|)
decl_stmt|;
name|runComparisonTest
argument_list|(
name|low
argument_list|,
name|high
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|runComparisonTest (Object low, Object high, Map<String, String> metadata)
specifier|private
name|void
name|runComparisonTest
parameter_list|(
name|Object
name|low
parameter_list|,
name|Object
name|high
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
throws|throws
name|Exception
block|{
name|DataOutputBuffer
name|out1
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|DataOutputBuffer
name|out2
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|in1
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|in2
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|SerializationFactory
name|factory
init|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Serialize some data to two byte streams.
name|SerializerBase
name|serializer
init|=
name|factory
operator|.
name|getSerializer
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Serializer is null!"
argument_list|,
name|serializer
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|open
argument_list|(
name|out1
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|low
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|close
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|open
argument_list|(
name|out2
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|high
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Shift that data into an input buffer.
name|in1
operator|.
name|reset
argument_list|(
name|out1
operator|.
name|getData
argument_list|()
argument_list|,
name|out1
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|in2
operator|.
name|reset
argument_list|(
name|out2
operator|.
name|getData
argument_list|()
argument_list|,
name|out2
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get the serialization and then the RawComparator;
comment|// use these to compare the data in the input streams and
comment|// assert that the low stream (1) is less than the high stream (2).
name|SerializationBase
name|serializationBase
init|=
name|factory
operator|.
name|getSerialization
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Null SerializationBase!"
argument_list|,
name|serializationBase
argument_list|)
expr_stmt|;
name|RawComparator
name|rawComparator
init|=
name|serializationBase
operator|.
name|getRawComparator
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Null raw comparator!"
argument_list|,
name|rawComparator
argument_list|)
expr_stmt|;
name|int
name|actual
init|=
name|rawComparator
operator|.
name|compare
argument_list|(
name|in1
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|in1
operator|.
name|getLength
argument_list|()
argument_list|,
name|in2
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|in2
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not compare FooWritable correctly"
argument_list|,
name|actual
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testBasicWritable ()
specifier|public
name|void
name|testBasicWritable
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test that a WritableComparable can be used with this API
comment|// correctly.
name|FooWritable
name|low
init|=
operator|new
name|FooWritable
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|FooWritable
name|high
init|=
operator|new
name|FooWritable
argument_list|(
literal|42
argument_list|)
decl_stmt|;
name|runComparisonTest
argument_list|(
name|low
argument_list|,
name|high
argument_list|)
expr_stmt|;
block|}
DECL|method|testTextWritable ()
specifier|public
name|void
name|testTextWritable
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test that a Text object (which uses Writable serialization, and
comment|// has its own RawComparator implementation) can be used with this
comment|// API correctly.
name|Text
name|low
init|=
operator|new
name|Text
argument_list|(
literal|"aaa"
argument_list|)
decl_stmt|;
name|Text
name|high
init|=
operator|new
name|Text
argument_list|(
literal|"zzz"
argument_list|)
decl_stmt|;
name|runComparisonTest
argument_list|(
name|low
argument_list|,
name|high
argument_list|)
expr_stmt|;
block|}
DECL|method|testAvroComparator ()
specifier|public
name|void
name|testAvroComparator
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test a record created via an Avro schema that doesn't have a fixed
comment|// class associated with it.
name|Schema
name|s1
init|=
name|Schema
operator|.
name|create
argument_list|(
name|Schema
operator|.
name|Type
operator|.
name|INT
argument_list|)
decl_stmt|;
comment|// Create a metadata mapping containing an Avro schema and a request to use
comment|// Avro generic serialization.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|AvroSerialization
operator|.
name|AVRO_SCHEMA_KEY
argument_list|,
name|s1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|SerializationBase
operator|.
name|SERIALIZATION_KEY
argument_list|,
name|AvroGenericSerialization
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|runComparisonTest
argument_list|(
operator|new
name|Integer
argument_list|(
literal|42
argument_list|)
argument_list|,
operator|new
name|Integer
argument_list|(
literal|123
argument_list|)
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
comment|// Now test it with a string record type.
name|Schema
name|s2
init|=
name|Schema
operator|.
name|create
argument_list|(
name|Schema
operator|.
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|AvroSerialization
operator|.
name|AVRO_SCHEMA_KEY
argument_list|,
name|s2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|runComparisonTest
argument_list|(
operator|new
name|Utf8
argument_list|(
literal|"baz"
argument_list|)
argument_list|,
operator|new
name|Utf8
argument_list|(
literal|"meep"
argument_list|)
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

