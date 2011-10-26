begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webproxy
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
name|webproxy
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|StringHelper
operator|.
name|ujoin
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
import|;
end_import

begin_class
DECL|class|ProxyUriUtils
specifier|public
class|class
name|ProxyUriUtils
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ProxyUriUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**Name of the servlet to use when registering the proxy servlet. */
DECL|field|PROXY_SERVLET_NAME
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_SERVLET_NAME
init|=
literal|"proxy"
decl_stmt|;
comment|/**Base path where the proxy servlet will handle requests.*/
DECL|field|PROXY_BASE
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_BASE
init|=
literal|"/proxy/"
decl_stmt|;
comment|/**Path Specification for the proxy servlet.*/
DECL|field|PROXY_PATH_SPEC
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PATH_SPEC
init|=
name|PROXY_BASE
operator|+
literal|"*"
decl_stmt|;
comment|/**Query Parameter indicating that the URI was approved.*/
DECL|field|PROXY_APPROVAL_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_APPROVAL_PARAM
init|=
literal|"proxyapproved"
decl_stmt|;
DECL|method|uriEncode (Object o)
specifier|private
specifier|static
name|String
name|uriEncode
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
try|try
block|{
assert|assert
operator|(
name|o
operator|!=
literal|null
operator|)
operator|:
literal|"o canot be null"
assert|;
return|return
name|URLEncoder
operator|.
name|encode
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|//This should never happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"UTF-8 is not supported by this system?"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Get the proxied path for an application.    * @param id the application id to use.    * @return the base path to that application through the proxy.    */
DECL|method|getPath (ApplicationId id)
specifier|public
specifier|static
name|String
name|getPath
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Application id cannot be null "
argument_list|)
throw|;
block|}
return|return
name|ujoin
argument_list|(
name|PROXY_BASE
argument_list|,
name|uriEncode
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the proxied path for an application.    * @param id the application id to use.    * @param path the rest of the path to the application.    * @return the base path to that application through the proxy.    */
DECL|method|getPath (ApplicationId id, String path)
specifier|public
specifier|static
name|String
name|getPath
parameter_list|(
name|ApplicationId
name|id
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
name|getPath
argument_list|(
name|id
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ujoin
argument_list|(
name|getPath
argument_list|(
name|id
argument_list|)
argument_list|,
name|path
argument_list|)
return|;
block|}
block|}
comment|/**    * Get the proxied path for an application    * @param id the id of the application    * @param path the path of the application.    * @param query the query parameters    * @param approved true if the user has approved accessing this app.    * @return the proxied path for this app.    */
DECL|method|getPathAndQuery (ApplicationId id, String path, String query, boolean approved)
specifier|public
specifier|static
name|String
name|getPathAndQuery
parameter_list|(
name|ApplicationId
name|id
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|query
parameter_list|,
name|boolean
name|approved
parameter_list|)
block|{
name|StringBuilder
name|newp
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|newp
operator|.
name|append
argument_list|(
name|getPath
argument_list|(
name|id
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|first
init|=
name|appendQuery
argument_list|(
name|newp
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|approved
condition|)
block|{
name|first
operator|=
name|appendQuery
argument_list|(
name|newp
argument_list|,
name|PROXY_APPROVAL_PARAM
operator|+
literal|"=true"
argument_list|,
name|first
argument_list|)
expr_stmt|;
block|}
return|return
name|newp
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|appendQuery (StringBuilder builder, String query, boolean first)
specifier|private
specifier|static
name|boolean
name|appendQuery
parameter_list|(
name|StringBuilder
name|builder
parameter_list|,
name|String
name|query
parameter_list|,
name|boolean
name|first
parameter_list|)
block|{
if|if
condition|(
name|query
operator|!=
literal|null
operator|&&
operator|!
name|query
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|first
operator|&&
operator|!
name|query
operator|.
name|startsWith
argument_list|(
literal|"?"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|'?'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|first
operator|&&
operator|!
name|query
operator|.
name|startsWith
argument_list|(
literal|"&"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|first
return|;
block|}
comment|/**    * Get a proxied URI for the original URI.    * @param originalUri the original URI to go through the proxy    * @param proxyUri the URI of the proxy itself, scheme, host and port are used.    * @param id the id of the application    * @return the proxied URI    */
DECL|method|getProxyUri (URI originalUri, URI proxyUri, ApplicationId id)
specifier|public
specifier|static
name|URI
name|getProxyUri
parameter_list|(
name|URI
name|originalUri
parameter_list|,
name|URI
name|proxyUri
parameter_list|,
name|ApplicationId
name|id
parameter_list|)
block|{
try|try
block|{
name|String
name|path
init|=
name|getPath
argument_list|(
name|id
argument_list|,
name|originalUri
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|URI
argument_list|(
name|proxyUri
operator|.
name|getScheme
argument_list|()
argument_list|,
name|proxyUri
operator|.
name|getAuthority
argument_list|()
argument_list|,
name|path
argument_list|,
name|originalUri
operator|.
name|getQuery
argument_list|()
argument_list|,
name|originalUri
operator|.
name|getFragment
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not proxify "
operator|+
name|originalUri
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create a URI form a no scheme Url, such as is returned by the AM.    * @param noSchemeUrl the URL formate returned by an AM    * @return a URI with an http scheme    * @throws URISyntaxException if the url is not formatted correctly.    */
DECL|method|getUriFromAMUrl (String noSchemeUrl)
specifier|public
specifier|static
name|URI
name|getUriFromAMUrl
parameter_list|(
name|String
name|noSchemeUrl
parameter_list|)
throws|throws
name|URISyntaxException
block|{
return|return
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|noSchemeUrl
argument_list|)
return|;
block|}
block|}
end_class

end_unit

