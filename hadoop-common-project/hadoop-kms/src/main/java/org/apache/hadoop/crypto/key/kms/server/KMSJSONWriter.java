begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Produces
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|WebApplicationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MultivaluedMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|MessageBodyWriter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|Provider
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
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
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * Jersey provider that converts<code>Map</code>s and<code>List</code>s  * to their JSON representation.  */
end_comment

begin_class
annotation|@
name|Provider
annotation|@
name|Produces
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSJSONWriter
specifier|public
class|class
name|KMSJSONWriter
implements|implements
name|MessageBodyWriter
argument_list|<
name|Object
argument_list|>
block|{
annotation|@
name|Override
DECL|method|isWriteable (Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
specifier|public
name|boolean
name|isWriteable
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|aClass
parameter_list|,
name|Type
name|type
parameter_list|,
name|Annotation
index|[]
name|annotations
parameter_list|,
name|MediaType
name|mediaType
parameter_list|)
block|{
return|return
name|Map
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|aClass
argument_list|)
operator|||
name|List
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|aClass
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSize (Object obj, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
specifier|public
name|long
name|getSize
parameter_list|(
name|Object
name|obj
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|aClass
parameter_list|,
name|Type
name|type
parameter_list|,
name|Annotation
index|[]
name|annotations
parameter_list|,
name|MediaType
name|mediaType
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo (Object obj, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream)
specifier|public
name|void
name|writeTo
parameter_list|(
name|Object
name|obj
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|aClass
parameter_list|,
name|Type
name|type
parameter_list|,
name|Annotation
index|[]
name|annotations
parameter_list|,
name|MediaType
name|mediaType
parameter_list|,
name|MultivaluedMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|stringObjectMultivaluedMap
parameter_list|,
name|OutputStream
name|outputStream
parameter_list|)
throws|throws
name|IOException
throws|,
name|WebApplicationException
block|{
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|outputStream
argument_list|)
decl_stmt|;
name|ObjectMapper
name|jsonMapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|jsonMapper
operator|.
name|writerWithDefaultPrettyPrinter
argument_list|()
operator|.
name|writeValue
argument_list|(
name|writer
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

