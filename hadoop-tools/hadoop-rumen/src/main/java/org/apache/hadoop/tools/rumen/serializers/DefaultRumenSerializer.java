begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen.serializers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
operator|.
name|serializers
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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonGenerator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonProcessingException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|JsonSerializer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|SerializerProvider
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
name|tools
operator|.
name|rumen
operator|.
name|datatypes
operator|.
name|DataType
import|;
end_import

begin_comment
comment|/**  * Default Rumen JSON serializer.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|DefaultRumenSerializer
specifier|public
class|class
name|DefaultRumenSerializer
extends|extends
name|JsonSerializer
argument_list|<
name|DataType
argument_list|>
block|{
DECL|method|serialize (DataType object, JsonGenerator jGen, SerializerProvider sProvider)
specifier|public
name|void
name|serialize
parameter_list|(
name|DataType
name|object
parameter_list|,
name|JsonGenerator
name|jGen
parameter_list|,
name|SerializerProvider
name|sProvider
parameter_list|)
throws|throws
name|IOException
throws|,
name|JsonProcessingException
block|{
name|Object
name|data
init|=
name|object
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|data
operator|instanceof
name|String
condition|)
block|{
name|jGen
operator|.
name|writeString
argument_list|(
name|data
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jGen
operator|.
name|writeObject
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit

