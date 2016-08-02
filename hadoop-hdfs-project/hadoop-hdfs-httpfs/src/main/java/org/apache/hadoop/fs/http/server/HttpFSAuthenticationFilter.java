begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|http
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
name|hdfs
operator|.
name|web
operator|.
name|WebHdfsConstants
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
name|security
operator|.
name|authentication
operator|.
name|server
operator|.
name|AuthenticationFilter
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenAuthenticationFilter
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|KerberosDelegationTokenAuthenticationHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Subclass of hadoop-auth<code>AuthenticationFilter</code> that obtains its configuration  * from HttpFSServer's server configuration.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HttpFSAuthenticationFilter
specifier|public
class|class
name|HttpFSAuthenticationFilter
extends|extends
name|DelegationTokenAuthenticationFilter
block|{
DECL|field|CONF_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|CONF_PREFIX
init|=
literal|"httpfs.authentication."
decl_stmt|;
DECL|field|SIGNATURE_SECRET_FILE
specifier|private
specifier|static
specifier|final
name|String
name|SIGNATURE_SECRET_FILE
init|=
name|SIGNATURE_SECRET
operator|+
literal|".file"
decl_stmt|;
comment|/**    * Returns the hadoop-auth configuration from HttpFSServer's configuration.    *<p>    * It returns all HttpFSServer's configuration properties prefixed with    *<code>httpfs.authentication</code>. The<code>httpfs.authentication</code>    * prefix is removed from the returned property names.    *    * @param configPrefix parameter not used.    * @param filterConfig parameter not used.    *    * @return hadoop-auth configuration read from HttpFSServer's configuration.    */
annotation|@
name|Override
DECL|method|getConfiguration (String configPrefix, FilterConfig filterConfig)
specifier|protected
name|Properties
name|getConfiguration
parameter_list|(
name|String
name|configPrefix
parameter_list|,
name|FilterConfig
name|filterConfig
parameter_list|)
throws|throws
name|ServletException
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|AuthenticationFilter
operator|.
name|COOKIE_PATH
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|conf
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|CONF_PREFIX
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|conf
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|CONF_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|signatureSecretFile
init|=
name|props
operator|.
name|getProperty
argument_list|(
name|SIGNATURE_SECRET_FILE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|signatureSecretFile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Undefined property: "
operator|+
name|SIGNATURE_SECRET_FILE
argument_list|)
throw|;
block|}
try|try
block|{
name|StringBuilder
name|secret
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|signatureSecretFile
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|int
name|c
init|=
name|reader
operator|.
name|read
argument_list|()
decl_stmt|;
while|while
condition|(
name|c
operator|>
operator|-
literal|1
condition|)
block|{
name|secret
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
name|c
operator|=
name|reader
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|AuthenticationFilter
operator|.
name|SIGNATURE_SECRET
argument_list|,
name|secret
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not read HttpFS signature secret file: "
operator|+
name|signatureSecretFile
argument_list|)
throw|;
block|}
name|setAuthHandlerClass
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|KerberosDelegationTokenAuthenticationHandler
operator|.
name|TOKEN_KIND
argument_list|,
name|WebHdfsConstants
operator|.
name|WEBHDFS_TOKEN_KIND
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|props
return|;
block|}
DECL|method|getProxyuserConfiguration (FilterConfig filterConfig)
specifier|protected
name|Configuration
name|getProxyuserConfiguration
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|proxyuserConf
init|=
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|getValByRegex
argument_list|(
literal|"httpfs\\.proxyuser\\."
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|proxyuserConf
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|substring
argument_list|(
literal|"httpfs."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|conf
return|;
block|}
block|}
end_class

end_unit

