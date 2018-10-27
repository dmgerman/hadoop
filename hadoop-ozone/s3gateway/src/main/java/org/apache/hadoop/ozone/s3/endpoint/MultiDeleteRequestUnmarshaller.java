begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.endpoint
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|endpoint
package|;
end_package

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
name|MessageBodyReader
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
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|UnmarshallerHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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
name|InputStream
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import

begin_comment
comment|/**  * Custom unmarshaller to read MultiDeleteRequest w/wo namespace.  */
end_comment

begin_class
annotation|@
name|Provider
annotation|@
name|Produces
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
DECL|class|MultiDeleteRequestUnmarshaller
specifier|public
class|class
name|MultiDeleteRequestUnmarshaller
implements|implements
name|MessageBodyReader
argument_list|<
name|MultiDeleteRequest
argument_list|>
block|{
DECL|field|context
specifier|private
specifier|final
name|JAXBContext
name|context
decl_stmt|;
DECL|field|xmlReader
specifier|private
specifier|final
name|XMLReader
name|xmlReader
decl_stmt|;
DECL|method|MultiDeleteRequestUnmarshaller ()
specifier|public
name|MultiDeleteRequestUnmarshaller
parameter_list|()
block|{
try|try
block|{
name|context
operator|=
name|JAXBContext
operator|.
name|newInstance
argument_list|(
name|MultiDeleteRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|SAXParserFactory
name|saxParserFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|xmlReader
operator|=
name|saxParserFactory
operator|.
name|newSAXParser
argument_list|()
operator|.
name|getXMLReader
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Can't instantiate MultiDeleteRequest parser"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|isReadable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
specifier|public
name|boolean
name|isReadable
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
name|Type
name|genericType
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
name|type
operator|.
name|equals
argument_list|(
name|MultiDeleteRequest
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom (Class<MultiDeleteRequest> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
specifier|public
name|MultiDeleteRequest
name|readFrom
parameter_list|(
name|Class
argument_list|<
name|MultiDeleteRequest
argument_list|>
name|type
parameter_list|,
name|Type
name|genericType
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
name|String
argument_list|>
name|httpHeaders
parameter_list|,
name|InputStream
name|entityStream
parameter_list|)
throws|throws
name|IOException
throws|,
name|WebApplicationException
block|{
try|try
block|{
name|UnmarshallerHandler
name|unmarshallerHandler
init|=
name|context
operator|.
name|createUnmarshaller
argument_list|()
operator|.
name|getUnmarshallerHandler
argument_list|()
decl_stmt|;
name|XmlNamespaceFilter
name|filter
init|=
operator|new
name|XmlNamespaceFilter
argument_list|(
literal|"http://s3.amazonaws.com/doc/2006-03-01/"
argument_list|)
decl_stmt|;
name|filter
operator|.
name|setContentHandler
argument_list|(
name|unmarshallerHandler
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setParent
argument_list|(
name|xmlReader
argument_list|)
expr_stmt|;
name|filter
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
name|entityStream
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|MultiDeleteRequest
operator|)
name|unmarshallerHandler
operator|.
name|getResult
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|WebApplicationException
argument_list|(
literal|"Can't parse request body to XML."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

