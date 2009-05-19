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

begin_comment
comment|/**  *<p>  * A {@link RawComparator} that uses a {@link JavaSerialization}  * {@link Deserializer} to deserialize objects that are then compared via  * their {@link Comparable} interfaces.  *</p>  * @param<T>  * @see JavaSerialization  */
end_comment

begin_class
DECL|class|JavaSerializationComparator
specifier|public
class|class
name|JavaSerializationComparator
parameter_list|<
name|T
extends|extends
name|Serializable
operator|&
name|Comparable
parameter_list|<
name|T
parameter_list|>
parameter_list|>
extends|extends
name|DeserializerComparator
argument_list|<
name|T
argument_list|>
block|{
DECL|method|JavaSerializationComparator ()
specifier|public
name|JavaSerializationComparator
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|JavaSerialization
operator|.
name|JavaSerializationDeserializer
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|compare (T o1, T o2)
specifier|public
name|int
name|compare
parameter_list|(
name|T
name|o1
parameter_list|,
name|T
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|compareTo
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
end_class

end_unit

