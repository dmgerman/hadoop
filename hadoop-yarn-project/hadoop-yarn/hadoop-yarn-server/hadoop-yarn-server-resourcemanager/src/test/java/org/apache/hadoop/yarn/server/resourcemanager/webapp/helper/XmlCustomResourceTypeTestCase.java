begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.helper
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|helper
package|;
end_package

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|WebResource
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
name|http
operator|.
name|JettyUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
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
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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

begin_comment
comment|/**  * This class hides the implementation details of how to verify the structure of  * XML responses. Tests should only provide the path of the  * {@link WebResource}, the response from the resource and  * the verifier Consumer to  * {@link XmlCustomResourceTypeTestCase#verify(Consumer)}. An instance of  * {@link JSONObject} will be passed to that consumer to be able to  * verify the response.  */
end_comment

begin_class
DECL|class|XmlCustomResourceTypeTestCase
specifier|public
class|class
name|XmlCustomResourceTypeTestCase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XmlCustomResourceTypeTestCase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|path
specifier|private
name|WebResource
name|path
decl_stmt|;
DECL|field|response
specifier|private
name|BufferedClientResponse
name|response
decl_stmt|;
DECL|field|parsedResponse
specifier|private
name|Document
name|parsedResponse
decl_stmt|;
DECL|method|XmlCustomResourceTypeTestCase (WebResource path, BufferedClientResponse response)
specifier|public
name|XmlCustomResourceTypeTestCase
parameter_list|(
name|WebResource
name|path
parameter_list|,
name|BufferedClientResponse
name|response
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|verifyStatus
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
DECL|method|verifyStatus (BufferedClientResponse response)
specifier|private
name|void
name|verifyStatus
parameter_list|(
name|BufferedClientResponse
name|response
parameter_list|)
block|{
name|String
name|responseStr
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"HTTP status should be 200, "
operator|+
literal|"status info: "
operator|+
name|response
operator|.
name|getStatusInfo
argument_list|()
operator|+
literal|" response as string: "
operator|+
name|responseStr
argument_list|,
literal|200
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verify (Consumer<Document> verifier)
specifier|public
name|void
name|verify
parameter_list|(
name|Consumer
argument_list|<
name|Document
argument_list|>
name|verifier
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
argument_list|,
name|response
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|parsedResponse
operator|=
name|parseXml
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|logResponse
argument_list|(
name|parsedResponse
argument_list|)
expr_stmt|;
name|verifier
operator|.
name|accept
argument_list|(
name|parsedResponse
argument_list|)
expr_stmt|;
block|}
DECL|method|parseXml (BufferedClientResponse response)
specifier|private
name|Document
name|parseXml
parameter_list|(
name|BufferedClientResponse
name|response
parameter_list|)
block|{
try|try
block|{
name|String
name|xml
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|DocumentBuilder
name|db
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|()
decl_stmt|;
name|is
operator|.
name|setCharacterStream
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|db
operator|.
name|parse
argument_list|(
name|is
argument_list|)
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|logResponse (Document doc)
specifier|private
name|void
name|logResponse
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|String
name|responseStr
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Raw response from service URL {}: {}"
argument_list|,
name|path
operator|.
name|toString
argument_list|()
argument_list|,
name|responseStr
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Parsed response from service URL {}: {}"
argument_list|,
name|path
operator|.
name|toString
argument_list|()
argument_list|,
name|toXml
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|toXml (Node node)
specifier|public
specifier|static
name|String
name|toXml
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|StringWriter
name|writer
decl_stmt|;
try|try
block|{
name|TransformerFactory
name|tf
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Transformer
name|transformer
init|=
name|tf
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|transformer
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|transformer
operator|.
name|setOutputProperty
argument_list|(
literal|"{http://xml.apache.org/xslt}indent"
operator|+
literal|"-amount"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
operator|new
name|DOMSource
argument_list|(
name|node
argument_list|)
argument_list|,
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|writer
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

