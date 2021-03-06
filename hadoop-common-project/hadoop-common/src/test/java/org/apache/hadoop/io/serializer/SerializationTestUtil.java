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
name|util
operator|.
name|GenericsUtil
import|;
end_import

begin_class
DECL|class|SerializationTestUtil
specifier|public
class|class
name|SerializationTestUtil
block|{
comment|/**    * A utility that tests serialization/deserialization.     * @param conf configuration to use, "io.serializations" is read to     * determine the serialization    * @param<K> the class of the item    * @param before item to (de)serialize    * @return deserialized item    */
DECL|method|testSerialization (Configuration conf, K before)
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|>
name|K
name|testSerialization
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|K
name|before
parameter_list|)
throws|throws
name|Exception
block|{
name|SerializationFactory
name|factory
init|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Serializer
argument_list|<
name|K
argument_list|>
name|serializer
init|=
name|factory
operator|.
name|getSerializer
argument_list|(
name|GenericsUtil
operator|.
name|getClass
argument_list|(
name|before
argument_list|)
argument_list|)
decl_stmt|;
name|Deserializer
argument_list|<
name|K
argument_list|>
name|deserializer
init|=
name|factory
operator|.
name|getDeserializer
argument_list|(
name|GenericsUtil
operator|.
name|getClass
argument_list|(
name|before
argument_list|)
argument_list|)
decl_stmt|;
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|open
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|close
argument_list|()
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
name|deserializer
operator|.
name|open
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|K
name|after
init|=
name|deserializer
operator|.
name|deserialize
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|deserializer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|after
return|;
block|}
block|}
end_class

end_unit

