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
name|tools
operator|.
name|rumen
operator|.
name|datatypes
operator|.
name|AnonymizableDataType
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
name|state
operator|.
name|StatePool
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
DECL|class|DefaultAnonymizingRumenSerializer
specifier|public
class|class
name|DefaultAnonymizingRumenSerializer
extends|extends
name|JsonSerializer
argument_list|<
name|AnonymizableDataType
argument_list|>
block|{
DECL|field|statePool
specifier|private
name|StatePool
name|statePool
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|DefaultAnonymizingRumenSerializer (StatePool statePool, Configuration conf)
specifier|public
name|DefaultAnonymizingRumenSerializer
parameter_list|(
name|StatePool
name|statePool
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|statePool
operator|=
name|statePool
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|serialize (AnonymizableDataType object, JsonGenerator jGen, SerializerProvider sProvider)
specifier|public
name|void
name|serialize
parameter_list|(
name|AnonymizableDataType
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
name|val
init|=
name|object
operator|.
name|getAnonymizedValue
argument_list|(
name|statePool
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// output the data if its a string
if|if
condition|(
name|val
operator|instanceof
name|String
condition|)
block|{
name|jGen
operator|.
name|writeString
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// let the mapper (JSON generator) handle this anonymized object.
name|jGen
operator|.
name|writeObject
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit

