begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
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
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|Response
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
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
comment|/**  * HTTP utility class to help propagate server side exception to the client  * over HTTP as a JSON payload.  *<p/>  * It creates HTTP Servlet and JAX-RPC error responses including details of the  * exception that allows a client to recreate the remote exception.  *<p/>  * It parses HTTP client connections and recreates the exception.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|HttpExceptionUtils
specifier|public
class|class
name|HttpExceptionUtils
block|{
DECL|field|ERROR_JSON
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_JSON
init|=
literal|"RemoteException"
decl_stmt|;
DECL|field|ERROR_EXCEPTION_JSON
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_EXCEPTION_JSON
init|=
literal|"exception"
decl_stmt|;
DECL|field|ERROR_CLASSNAME_JSON
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_CLASSNAME_JSON
init|=
literal|"javaClassName"
decl_stmt|;
DECL|field|ERROR_MESSAGE_JSON
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_MESSAGE_JSON
init|=
literal|"message"
decl_stmt|;
DECL|field|APPLICATION_JSON_MIME
specifier|private
specifier|static
specifier|final
name|String
name|APPLICATION_JSON_MIME
init|=
literal|"application/json"
decl_stmt|;
DECL|field|ENTER
specifier|private
specifier|static
specifier|final
name|String
name|ENTER
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|/**    * Creates a HTTP servlet response serializing the exception in it as JSON.    *    * @param response the servlet response    * @param status the error code to set in the response    * @param ex the exception to serialize in the response    * @throws IOException thrown if there was an error while creating the    * response    */
DECL|method|createServletExceptionResponse ( HttpServletResponse response, int status, Throwable ex)
specifier|public
specifier|static
name|void
name|createServletExceptionResponse
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|,
name|int
name|status
parameter_list|,
name|Throwable
name|ex
parameter_list|)
throws|throws
name|IOException
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|APPLICATION_JSON_MIME
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|json
operator|.
name|put
argument_list|(
name|ERROR_MESSAGE_JSON
argument_list|,
name|getOneLineMessage
argument_list|(
name|ex
argument_list|)
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|ERROR_EXCEPTION_JSON
argument_list|,
name|ex
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|ERROR_CLASSNAME_JSON
argument_list|,
name|ex
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonResponse
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|jsonResponse
operator|.
name|put
argument_list|(
name|ERROR_JSON
argument_list|,
name|json
argument_list|)
expr_stmt|;
name|ObjectMapper
name|jsonMapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|Writer
name|writer
init|=
name|response
operator|.
name|getWriter
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
name|jsonResponse
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a HTTP JAX-RPC response serializing the exception in it as JSON.    *    * @param status the error code to set in the response    * @param ex the exception to serialize in the response    * @return the JAX-RPC response with the set error and JSON encoded exception    */
DECL|method|createJerseyExceptionResponse (Response.Status status, Throwable ex)
specifier|public
specifier|static
name|Response
name|createJerseyExceptionResponse
parameter_list|(
name|Response
operator|.
name|Status
name|status
parameter_list|,
name|Throwable
name|ex
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|json
operator|.
name|put
argument_list|(
name|ERROR_MESSAGE_JSON
argument_list|,
name|getOneLineMessage
argument_list|(
name|ex
argument_list|)
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|ERROR_EXCEPTION_JSON
argument_list|,
name|ex
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|ERROR_CLASSNAME_JSON
argument_list|,
name|ex
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
name|ERROR_JSON
argument_list|,
name|json
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|status
argument_list|(
name|status
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
name|response
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getOneLineMessage (Throwable exception)
specifier|private
specifier|static
name|String
name|getOneLineMessage
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|String
name|message
init|=
name|exception
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|int
name|i
init|=
name|message
operator|.
name|indexOf
argument_list|(
name|ENTER
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
operator|-
literal|1
condition|)
block|{
name|message
operator|=
name|message
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|message
return|;
block|}
comment|// trick, riding on generics to throw an undeclared exception
DECL|method|throwEx (Throwable ex)
specifier|private
specifier|static
name|void
name|throwEx
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|HttpExceptionUtils
operator|.
expr|<
name|RuntimeException
operator|>
name|throwException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|throwException (Throwable ex)
specifier|private
specifier|static
parameter_list|<
name|E
extends|extends
name|Throwable
parameter_list|>
name|void
name|throwException
parameter_list|(
name|Throwable
name|ex
parameter_list|)
throws|throws
name|E
block|{
throw|throw
operator|(
name|E
operator|)
name|ex
throw|;
block|}
comment|/**    * Validates the status of an<code>HttpURLConnection</code> against an    * expected HTTP status code. If the current status code is not the expected    * one it throws an exception with a detail message using Server side error    * messages if available.    *<p/>    *<b>NOTE:</b> this method will throw the deserialized exception even if not    * declared in the<code>throws</code> of the method signature.    *    * @param conn the<code>HttpURLConnection</code>.    * @param expectedStatus the expected HTTP status code.    * @throws IOException thrown if the current status code does not match the    * expected one.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|validateResponse (HttpURLConnection conn, int expectedStatus)
specifier|public
specifier|static
name|void
name|validateResponse
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|,
name|int
name|expectedStatus
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|conn
operator|.
name|getResponseCode
argument_list|()
operator|!=
name|expectedStatus
condition|)
block|{
name|Exception
name|toThrow
decl_stmt|;
name|InputStream
name|es
init|=
literal|null
decl_stmt|;
try|try
block|{
name|es
operator|=
name|conn
operator|.
name|getErrorStream
argument_list|()
expr_stmt|;
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|Map
name|json
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|es
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
name|json
operator|=
operator|(
name|Map
operator|)
name|json
operator|.
name|get
argument_list|(
name|ERROR_JSON
argument_list|)
expr_stmt|;
name|String
name|exClass
init|=
operator|(
name|String
operator|)
name|json
operator|.
name|get
argument_list|(
name|ERROR_CLASSNAME_JSON
argument_list|)
decl_stmt|;
name|String
name|exMsg
init|=
operator|(
name|String
operator|)
name|json
operator|.
name|get
argument_list|(
name|ERROR_MESSAGE_JSON
argument_list|)
decl_stmt|;
if|if
condition|(
name|exClass
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ClassLoader
name|cl
init|=
name|HttpExceptionUtils
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|Class
name|klass
init|=
name|cl
operator|.
name|loadClass
argument_list|(
name|exClass
argument_list|)
decl_stmt|;
name|Constructor
name|constr
init|=
name|klass
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|toThrow
operator|=
operator|(
name|Exception
operator|)
name|constr
operator|.
name|newInstance
argument_list|(
name|exMsg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|toThrow
operator|=
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"HTTP status [%d], exception [%s], message [%s] "
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|exClass
argument_list|,
name|exMsg
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|msg
init|=
operator|(
name|exMsg
operator|!=
literal|null
operator|)
condition|?
name|exMsg
else|:
name|conn
operator|.
name|getResponseMessage
argument_list|()
decl_stmt|;
name|toThrow
operator|=
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"HTTP status [%d], message [%s]"
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|toThrow
operator|=
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"HTTP status [%d], message [%s]"
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|conn
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|es
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|es
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|//ignore
block|}
block|}
block|}
name|throwEx
argument_list|(
name|toThrow
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

