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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
comment|/** Unit test for EnumSetWritable */
end_comment

begin_class
DECL|class|TestEnumSetWritable
specifier|public
class|class
name|TestEnumSetWritable
extends|extends
name|TestCase
block|{
DECL|enum|TestEnumSet
enum|enum
name|TestEnumSet
block|{
DECL|enumConstant|CREATE
DECL|enumConstant|OVERWRITE
DECL|enumConstant|APPEND
name|CREATE
block|,
name|OVERWRITE
block|,
name|APPEND
block|;   }
DECL|field|nonEmptyFlag
name|EnumSet
argument_list|<
name|TestEnumSet
argument_list|>
name|nonEmptyFlag
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|TestEnumSet
operator|.
name|APPEND
argument_list|)
decl_stmt|;
DECL|field|nonEmptyFlagWritable
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
name|nonEmptyFlagWritable
init|=
operator|new
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
argument_list|(
name|nonEmptyFlag
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSerializeAndDeserializeNonEmpty ()
specifier|public
name|void
name|testSerializeAndDeserializeNonEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|ObjectWritable
operator|.
name|writeObject
argument_list|(
name|out
argument_list|,
name|nonEmptyFlagWritable
argument_list|,
name|nonEmptyFlagWritable
operator|.
name|getClass
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
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
name|EnumSet
argument_list|<
name|TestEnumSet
argument_list|>
name|read
init|=
operator|(
operator|(
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
operator|)
name|ObjectWritable
operator|.
name|readObject
argument_list|(
name|in
argument_list|,
literal|null
argument_list|)
operator|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|read
argument_list|,
name|nonEmptyFlag
argument_list|)
expr_stmt|;
block|}
DECL|field|emptyFlag
name|EnumSet
argument_list|<
name|TestEnumSet
argument_list|>
name|emptyFlag
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|TestEnumSet
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSerializeAndDeserializeEmpty ()
specifier|public
name|void
name|testSerializeAndDeserializeEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
operator|new
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
argument_list|(
name|emptyFlag
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Instantiate empty EnumSetWritable with no element type class providesd should throw exception."
argument_list|,
name|gotException
argument_list|)
expr_stmt|;
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
name|emptyFlagWritable
init|=
operator|new
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
argument_list|(
name|emptyFlag
argument_list|,
name|TestEnumSet
operator|.
name|class
argument_list|)
decl_stmt|;
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|ObjectWritable
operator|.
name|writeObject
argument_list|(
name|out
argument_list|,
name|emptyFlagWritable
argument_list|,
name|emptyFlagWritable
operator|.
name|getClass
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
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
name|EnumSet
argument_list|<
name|TestEnumSet
argument_list|>
name|read
init|=
operator|(
operator|(
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
operator|)
name|ObjectWritable
operator|.
name|readObject
argument_list|(
name|in
argument_list|,
literal|null
argument_list|)
operator|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|read
argument_list|,
name|emptyFlag
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSerializeAndDeserializeNull ()
specifier|public
name|void
name|testSerializeAndDeserializeNull
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
operator|new
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Instantiate empty EnumSetWritable with no element type class providesd should throw exception."
argument_list|,
name|gotException
argument_list|)
expr_stmt|;
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
name|nullFlagWritable
init|=
operator|new
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
argument_list|(
literal|null
argument_list|,
name|TestEnumSet
operator|.
name|class
argument_list|)
decl_stmt|;
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|ObjectWritable
operator|.
name|writeObject
argument_list|(
name|out
argument_list|,
name|nullFlagWritable
argument_list|,
name|nullFlagWritable
operator|.
name|getClass
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
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
name|EnumSet
argument_list|<
name|TestEnumSet
argument_list|>
name|read
init|=
operator|(
operator|(
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
operator|)
name|ObjectWritable
operator|.
name|readObject
argument_list|(
name|in
argument_list|,
literal|null
argument_list|)
operator|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|read
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|field|testField
specifier|public
name|EnumSetWritable
argument_list|<
name|TestEnumSet
argument_list|>
name|testField
decl_stmt|;
DECL|method|testAvroReflect ()
specifier|public
name|void
name|testAvroReflect
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|schema
init|=
literal|"{\"type\":\"array\",\"items\":{\"type\":\"enum\",\"name\":\"TestEnumSet\",\"namespace\":\"org.apache.hadoop.io.TestEnumSetWritable$\",\"symbols\":[\"CREATE\",\"OVERWRITE\",\"APPEND\"]},\"java-class\":\"org.apache.hadoop.io.EnumSetWritable\"}"
decl_stmt|;
name|Type
name|type
init|=
name|TestEnumSetWritable
operator|.
name|class
operator|.
name|getField
argument_list|(
literal|"testField"
argument_list|)
operator|.
name|getGenericType
argument_list|()
decl_stmt|;
name|AvroTestUtil
operator|.
name|testReflect
argument_list|(
name|nonEmptyFlagWritable
argument_list|,
name|type
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

