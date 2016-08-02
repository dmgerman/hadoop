begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.client.rest
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|client
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

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
name|Client
import|;
end_import

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
name|ClientHandlerException
import|;
end_import

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
name|GenericType
import|;
end_import

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
name|UniformInterfaceException
import|;
end_import

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
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|ExceptionConverter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|restclient
operator|.
name|HttpVerb
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|restclient
operator|.
name|UgiJerseyBinding
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
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * This is a base class for Jersey REST clients in Slider.  * It supports the execution of operations âwith  * exceptions uprated to IOExceptions when needed.  *<p>  * Subclasses can use these operations to provide an API-like view  * of the REST model  */
end_comment

begin_class
DECL|class|BaseRestClient
specifier|public
class|class
name|BaseRestClient
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BaseRestClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|method|BaseRestClient ( Client client)
specifier|public
name|BaseRestClient
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|client
argument_list|,
literal|"null jersey client"
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
comment|/**    * Get the jersey client    * @return jersey client    */
DECL|method|getClient ()
specifier|public
name|Client
name|getClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
comment|/**    * Execute the operation. Failures are raised as IOException subclasses    * @param method method to execute    * @param resource resource to work against    * @param c class to build    * @param<T> type expected    * @return an instance of the type T    * @throws IOException on any failure    */
DECL|method|exec (HttpVerb method, WebResource resource, Class<T> c)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|exec
parameter_list|(
name|HttpVerb
name|method
parameter_list|,
name|WebResource
name|resource
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|c
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"{}} {}"
argument_list|,
name|method
argument_list|,
name|resource
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|resource
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|)
operator|.
name|method
argument_list|(
name|method
operator|.
name|getVerb
argument_list|()
argument_list|,
name|c
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClientHandlerException
name|ex
parameter_list|)
block|{
throw|throw
name|ExceptionConverter
operator|.
name|convertJerseyException
argument_list|(
name|method
operator|.
name|getVerb
argument_list|()
argument_list|,
name|resource
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
name|ex
parameter_list|)
block|{
throw|throw
name|UgiJerseyBinding
operator|.
name|uprateFaults
argument_list|(
name|method
argument_list|,
name|resource
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Execute the operation. Failures are raised as IOException subclasses    * @param method method to execute    * @param resource resource to work against    * @param t type to work with    * @param<T> type expected    * @return an instance of the type T    * @throws IOException on any failure    */
DECL|method|exec (HttpVerb method, WebResource resource, GenericType<T> t)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|exec
parameter_list|(
name|HttpVerb
name|method
parameter_list|,
name|WebResource
name|resource
parameter_list|,
name|GenericType
argument_list|<
name|T
argument_list|>
name|t
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|t
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"{}} {}"
argument_list|,
name|method
argument_list|,
name|resource
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|resource
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|)
expr_stmt|;
return|return
name|resource
operator|.
name|method
argument_list|(
name|method
operator|.
name|getVerb
argument_list|()
argument_list|,
name|t
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClientHandlerException
name|ex
parameter_list|)
block|{
throw|throw
name|ExceptionConverter
operator|.
name|convertJerseyException
argument_list|(
name|method
operator|.
name|getVerb
argument_list|()
argument_list|,
name|resource
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
name|ex
parameter_list|)
block|{
throw|throw
name|UgiJerseyBinding
operator|.
name|uprateFaults
argument_list|(
name|method
argument_list|,
name|resource
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Execute the  GET operation. Failures are raised as IOException subclasses    * @param resource resource to work against    * @param c class to build    * @param<T> type expected    * @return an instance of the type T    * @throws IOException on any failure    */
DECL|method|get (WebResource resource, Class<T> c)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|WebResource
name|resource
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|exec
argument_list|(
name|HttpVerb
operator|.
name|GET
argument_list|,
name|resource
argument_list|,
name|c
argument_list|)
return|;
block|}
comment|/**    * Create a Web resource from the client.    *    * @param u the URI of the resource.    * @return the Web resource.    */
DECL|method|resource (URI u)
specifier|public
name|WebResource
name|resource
parameter_list|(
name|URI
name|u
parameter_list|)
block|{
return|return
name|client
operator|.
name|resource
argument_list|(
name|u
argument_list|)
return|;
block|}
comment|/**    * Create a Web resource from the client.    *    * @param u the URI of the resource.    * @return the Web resource.    */
DECL|method|resource (String url)
specifier|public
name|WebResource
name|resource
parameter_list|(
name|String
name|url
parameter_list|)
block|{
return|return
name|client
operator|.
name|resource
argument_list|(
name|url
argument_list|)
return|;
block|}
block|}
end_class

end_unit

