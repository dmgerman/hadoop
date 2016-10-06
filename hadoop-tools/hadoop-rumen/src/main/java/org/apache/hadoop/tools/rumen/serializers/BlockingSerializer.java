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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A JSON serializer for Strings.  */
end_comment

begin_class
DECL|class|BlockingSerializer
specifier|public
class|class
name|BlockingSerializer
extends|extends
name|JsonSerializer
argument_list|<
name|String
argument_list|>
block|{
DECL|method|serialize (String object, JsonGenerator jGen, SerializerProvider sProvider)
specifier|public
name|void
name|serialize
parameter_list|(
name|String
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
name|jGen
operator|.
name|writeNull
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
block|}
end_class

end_unit

