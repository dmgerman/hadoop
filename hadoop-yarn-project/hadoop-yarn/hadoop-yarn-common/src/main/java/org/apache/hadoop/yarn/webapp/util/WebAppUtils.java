begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|util
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
name|PATH_JOINER
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
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Evolving
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
name|http
operator|.
name|HtmlQuoting
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
name|HttpConfig
operator|.
name|Policy
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
name|HttpServer2
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
name|net
operator|.
name|NetUtils
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
name|conf
operator|.
name|YarnConfiguration
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
name|conf
operator|.
name|HAUtil
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|factories
operator|.
name|RecordFactory
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
name|util
operator|.
name|ConverterUtils
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
name|util
operator|.
name|RMHAUtils
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
name|webapp
operator|.
name|BadRequestException
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
name|webapp
operator|.
name|NotFoundException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|NameValuePair
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|utils
operator|.
name|URLEncodedUtils
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
name|HttpServletRequest
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|WebAppUtils
specifier|public
class|class
name|WebAppUtils
block|{
DECL|field|WEB_APP_TRUSTSTORE_PASSWORD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|WEB_APP_TRUSTSTORE_PASSWORD_KEY
init|=
literal|"ssl.server.truststore.password"
decl_stmt|;
DECL|field|WEB_APP_KEYSTORE_PASSWORD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|WEB_APP_KEYSTORE_PASSWORD_KEY
init|=
literal|"ssl.server.keystore.password"
decl_stmt|;
DECL|field|WEB_APP_KEY_PASSWORD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|WEB_APP_KEY_PASSWORD_KEY
init|=
literal|"ssl.server.keystore.keypassword"
decl_stmt|;
DECL|field|HTTPS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|HTTPS_PREFIX
init|=
literal|"https://"
decl_stmt|;
DECL|field|HTTP_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_PREFIX
init|=
literal|"http://"
decl_stmt|;
DECL|method|setRMWebAppPort (Configuration conf, int port)
specifier|public
specifier|static
name|void
name|setRMWebAppPort
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|String
name|hostname
init|=
name|getRMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|hostname
operator|=
operator|(
name|hostname
operator|.
name|contains
argument_list|(
literal|":"
argument_list|)
operator|)
condition|?
name|hostname
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|hostname
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
argument_list|)
else|:
name|hostname
expr_stmt|;
name|setRMWebAppHostnameAndPort
argument_list|(
name|conf
argument_list|,
name|hostname
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
DECL|method|setRMWebAppHostnameAndPort (Configuration conf, String hostname, int port)
specifier|public
specifier|static
name|void
name|setRMWebAppHostnameAndPort
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|hostname
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|String
name|resolvedAddress
init|=
name|hostname
operator|+
literal|":"
operator|+
name|port
decl_stmt|;
if|if
condition|(
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|resolvedAddress
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
name|resolvedAddress
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setNMWebAppHostNameAndPort (Configuration conf, String hostName, int port)
specifier|public
specifier|static
name|void
name|setNMWebAppHostNameAndPort
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|hostName
parameter_list|,
name|int
name|port
parameter_list|)
block|{
if|if
condition|(
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|hostName
operator|+
literal|":"
operator|+
name|port
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_ADDRESS
argument_list|,
name|hostName
operator|+
literal|":"
operator|+
name|port
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRMWebAppURLWithScheme (Configuration conf)
specifier|public
specifier|static
name|String
name|getRMWebAppURLWithScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
operator|+
name|getRMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|getRMWebAppURLWithoutScheme (Configuration conf)
specifier|public
specifier|static
name|String
name|getRMWebAppURLWithoutScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|)
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_HTTPS_ADDRESS
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_ADDRESS
argument_list|)
return|;
block|}
block|}
DECL|method|getProxyHostsAndPortsForAmFilter ( Configuration conf)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getProxyHostsAndPortsForAmFilter
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|addrs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|proxyAddr
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|PROXY_ADDRESS
argument_list|)
decl_stmt|;
comment|// If PROXY_ADDRESS isn't set, fallback to RM_WEBAPP(_HTTPS)_ADDRESS
comment|// There could be multiple if using RM HA
if|if
condition|(
name|proxyAddr
operator|==
literal|null
operator|||
name|proxyAddr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// If RM HA is enabled, try getting those addresses
if|if
condition|(
name|HAUtil
operator|.
name|isHAEnabled
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|haAddrs
init|=
name|RMHAUtils
operator|.
name|getRMHAWebappAddresses
argument_list|(
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|addr
range|:
name|haAddrs
control|)
block|{
try|try
block|{
name|InetSocketAddress
name|socketAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|addrs
operator|.
name|add
argument_list|(
name|getResolvedAddress
argument_list|(
name|socketAddr
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// skip if can't resolve
block|}
block|}
block|}
comment|// If couldn't resolve any of the addresses or not using RM HA, fallback
if|if
condition|(
name|addrs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addrs
operator|.
name|add
argument_list|(
name|getResolvedRMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|addrs
operator|.
name|add
argument_list|(
name|proxyAddr
argument_list|)
expr_stmt|;
block|}
return|return
name|addrs
return|;
block|}
DECL|method|getProxyHostAndPort (Configuration conf)
specifier|public
specifier|static
name|String
name|getProxyHostAndPort
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|addr
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|PROXY_ADDRESS
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|==
literal|null
operator|||
name|addr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addr
operator|=
name|getResolvedRMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
name|addr
return|;
block|}
DECL|method|getResolvedRemoteRMWebAppURLWithScheme ( Configuration conf)
specifier|public
specifier|static
name|String
name|getResolvedRemoteRMWebAppURLWithScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
operator|+
name|getResolvedRemoteRMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|getResolvedRMWebAppURLWithScheme (Configuration conf)
specifier|public
specifier|static
name|String
name|getResolvedRMWebAppURLWithScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
operator|+
name|getResolvedRMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|getResolvedRemoteRMWebAppURLWithoutScheme ( Configuration conf)
specifier|public
specifier|static
name|String
name|getResolvedRemoteRMWebAppURLWithoutScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|getResolvedRemoteRMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|?
name|Policy
operator|.
name|HTTPS_ONLY
else|:
name|Policy
operator|.
name|HTTP_ONLY
argument_list|)
return|;
block|}
DECL|method|getResolvedRMWebAppURLWithoutScheme (Configuration conf)
specifier|public
specifier|static
name|String
name|getResolvedRMWebAppURLWithoutScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|getResolvedRMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|?
name|Policy
operator|.
name|HTTPS_ONLY
else|:
name|Policy
operator|.
name|HTTP_ONLY
argument_list|)
return|;
block|}
DECL|method|getResolvedRMWebAppURLWithoutScheme (Configuration conf, Policy httpPolicy)
specifier|public
specifier|static
name|String
name|getResolvedRMWebAppURLWithoutScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Policy
name|httpPolicy
parameter_list|)
block|{
name|InetSocketAddress
name|address
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|httpPolicy
operator|==
name|Policy
operator|.
name|HTTPS_ONLY
condition|)
block|{
name|address
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_HTTPS_PORT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|address
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_PORT
argument_list|)
expr_stmt|;
block|}
return|return
name|getResolvedAddress
argument_list|(
name|address
argument_list|)
return|;
block|}
DECL|method|getResolvedRemoteRMWebAppURLWithoutScheme (Configuration conf, Policy httpPolicy)
specifier|public
specifier|static
name|String
name|getResolvedRemoteRMWebAppURLWithoutScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Policy
name|httpPolicy
parameter_list|)
block|{
name|InetSocketAddress
name|address
init|=
literal|null
decl_stmt|;
name|String
name|rmId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|HAUtil
operator|.
name|isHAEnabled
argument_list|(
name|conf
argument_list|)
condition|)
block|{
comment|// If HA enabled, pick one of the RM-IDs and rely on redirect to go to
comment|// the Active RM
name|rmId
operator|=
operator|(
name|String
operator|)
name|HAUtil
operator|.
name|getRMHAIds
argument_list|(
name|conf
argument_list|)
operator|.
name|toArray
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
block|}
if|if
condition|(
name|httpPolicy
operator|==
name|Policy
operator|.
name|HTTPS_ONLY
condition|)
block|{
name|address
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|rmId
operator|==
literal|null
condition|?
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
else|:
name|HAUtil
operator|.
name|addSuffix
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|rmId
argument_list|)
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_HTTPS_PORT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|address
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|rmId
operator|==
literal|null
condition|?
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
else|:
name|HAUtil
operator|.
name|addSuffix
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
name|rmId
argument_list|)
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_PORT
argument_list|)
expr_stmt|;
block|}
return|return
name|getResolvedAddress
argument_list|(
name|address
argument_list|)
return|;
block|}
DECL|method|getResolvedAddress (InetSocketAddress address)
specifier|public
specifier|static
name|String
name|getResolvedAddress
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|)
block|{
name|address
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|InetAddress
name|resolved
init|=
name|address
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|resolved
operator|==
literal|null
operator|||
name|resolved
operator|.
name|isAnyLocalAddress
argument_list|()
operator|||
name|resolved
operator|.
name|isLoopbackAddress
argument_list|()
condition|)
block|{
name|String
name|lh
init|=
name|address
operator|.
name|getHostName
argument_list|()
decl_stmt|;
try|try
block|{
name|lh
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
comment|//Ignore and fallback.
block|}
name|sb
operator|.
name|append
argument_list|(
name|lh
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|address
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Get the URL to use for binding where bind hostname can be specified    * to override the hostname in the webAppURLWithoutScheme. Port specified in the    * webAppURLWithoutScheme will be used.    *    * @param conf the configuration    * @param hostProperty bind host property name    * @param webAppURLWithoutScheme web app URL without scheme String    * @return String representing bind URL    */
DECL|method|getWebAppBindURL ( Configuration conf, String hostProperty, String webAppURLWithoutScheme)
specifier|public
specifier|static
name|String
name|getWebAppBindURL
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|hostProperty
parameter_list|,
name|String
name|webAppURLWithoutScheme
parameter_list|)
block|{
comment|// If the bind-host setting exists then it overrides the hostname
comment|// portion of the corresponding webAppURLWithoutScheme
name|String
name|host
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|hostProperty
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|!=
literal|null
operator|&&
operator|!
name|host
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|webAppURLWithoutScheme
operator|.
name|contains
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
name|webAppURLWithoutScheme
operator|=
name|host
operator|+
literal|":"
operator|+
name|webAppURLWithoutScheme
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"webAppURLWithoutScheme must include port specification but doesn't: "
operator|+
name|webAppURLWithoutScheme
argument_list|)
throw|;
block|}
block|}
return|return
name|webAppURLWithoutScheme
return|;
block|}
DECL|method|getNMWebAppURLWithoutScheme (Configuration conf)
specifier|public
specifier|static
name|String
name|getNMWebAppURLWithoutScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|)
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_WEBAPP_HTTPS_ADDRESS
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_WEBAPP_ADDRESS
argument_list|)
return|;
block|}
block|}
DECL|method|getAHSWebAppURLWithoutScheme (Configuration conf)
specifier|public
specifier|static
name|String
name|getAHSWebAppURLWithoutScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|getTimelineReaderWebAppURL
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|getTimelineReaderWebAppURL (Configuration conf)
specifier|public
specifier|static
name|String
name|getTimelineReaderWebAppURL
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|)
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_WEBAPP_HTTPS_ADDRESS
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|)
return|;
block|}
block|}
comment|/**    * if url has scheme then it will be returned as it is else it will return    * url with scheme.    * @param schemePrefix eg. http:// or https://    * @param url    * @return url with scheme    */
DECL|method|getURLWithScheme (String schemePrefix, String url)
specifier|public
specifier|static
name|String
name|getURLWithScheme
parameter_list|(
name|String
name|schemePrefix
parameter_list|,
name|String
name|url
parameter_list|)
block|{
comment|// If scheme is provided then it will be returned as it is
if|if
condition|(
name|url
operator|.
name|indexOf
argument_list|(
literal|"://"
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|url
return|;
block|}
else|else
block|{
return|return
name|schemePrefix
operator|+
name|url
return|;
block|}
block|}
DECL|method|getRunningLogURL ( String nodeHttpAddress, String containerId, String user)
specifier|public
specifier|static
name|String
name|getRunningLogURL
parameter_list|(
name|String
name|nodeHttpAddress
parameter_list|,
name|String
name|containerId
parameter_list|,
name|String
name|user
parameter_list|)
block|{
if|if
condition|(
name|nodeHttpAddress
operator|==
literal|null
operator|||
name|nodeHttpAddress
operator|.
name|isEmpty
argument_list|()
operator|||
name|containerId
operator|==
literal|null
operator|||
name|containerId
operator|.
name|isEmpty
argument_list|()
operator|||
name|user
operator|==
literal|null
operator|||
name|user
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|PATH_JOINER
operator|.
name|join
argument_list|(
name|nodeHttpAddress
argument_list|,
literal|"node"
argument_list|,
literal|"containerlogs"
argument_list|,
name|containerId
argument_list|,
name|user
argument_list|)
return|;
block|}
DECL|method|getAggregatedLogURL (String serverHttpAddress, String allocatedNode, String containerId, String entity, String user)
specifier|public
specifier|static
name|String
name|getAggregatedLogURL
parameter_list|(
name|String
name|serverHttpAddress
parameter_list|,
name|String
name|allocatedNode
parameter_list|,
name|String
name|containerId
parameter_list|,
name|String
name|entity
parameter_list|,
name|String
name|user
parameter_list|)
block|{
if|if
condition|(
name|serverHttpAddress
operator|==
literal|null
operator|||
name|serverHttpAddress
operator|.
name|isEmpty
argument_list|()
operator|||
name|allocatedNode
operator|==
literal|null
operator|||
name|allocatedNode
operator|.
name|isEmpty
argument_list|()
operator|||
name|containerId
operator|==
literal|null
operator|||
name|containerId
operator|.
name|isEmpty
argument_list|()
operator|||
name|entity
operator|==
literal|null
operator|||
name|entity
operator|.
name|isEmpty
argument_list|()
operator|||
name|user
operator|==
literal|null
operator|||
name|user
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|PATH_JOINER
operator|.
name|join
argument_list|(
name|serverHttpAddress
argument_list|,
literal|"applicationhistory"
argument_list|,
literal|"logs"
argument_list|,
name|allocatedNode
argument_list|,
name|containerId
argument_list|,
name|entity
argument_list|,
name|user
argument_list|)
return|;
block|}
comment|/**    * Choose which scheme (HTTP or HTTPS) to use when generating a URL based on    * the configuration.    *     * @return the scheme (HTTP / HTTPS)    */
DECL|method|getHttpSchemePrefix (Configuration conf)
specifier|public
specifier|static
name|String
name|getHttpSchemePrefix
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|?
name|HTTPS_PREFIX
else|:
name|HTTP_PREFIX
return|;
block|}
comment|/**    * Load the SSL keystore / truststore into the HttpServer builder.    * @param builder the HttpServer2.Builder to populate with ssl config    */
DECL|method|loadSslConfiguration ( HttpServer2.Builder builder)
specifier|public
specifier|static
name|HttpServer2
operator|.
name|Builder
name|loadSslConfiguration
parameter_list|(
name|HttpServer2
operator|.
name|Builder
name|builder
parameter_list|)
block|{
return|return
name|loadSslConfiguration
argument_list|(
name|builder
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Load the SSL keystore / truststore into the HttpServer builder.    * @param builder the HttpServer2.Builder to populate with ssl config    * @param sslConf the Configuration instance to use during loading of SSL conf    */
DECL|method|loadSslConfiguration ( HttpServer2.Builder builder, Configuration sslConf)
specifier|public
specifier|static
name|HttpServer2
operator|.
name|Builder
name|loadSslConfiguration
parameter_list|(
name|HttpServer2
operator|.
name|Builder
name|builder
parameter_list|,
name|Configuration
name|sslConf
parameter_list|)
block|{
if|if
condition|(
name|sslConf
operator|==
literal|null
condition|)
block|{
name|sslConf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|boolean
name|needsClientAuth
init|=
name|YarnConfiguration
operator|.
name|YARN_SSL_CLIENT_HTTPS_NEED_AUTH_DEFAULT
decl_stmt|;
name|sslConf
operator|.
name|addResource
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SSL_SERVER_RESOURCE_DEFAULT
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|needsClientAuth
argument_list|(
name|needsClientAuth
argument_list|)
operator|.
name|keyPassword
argument_list|(
name|getPassword
argument_list|(
name|sslConf
argument_list|,
name|WEB_APP_KEY_PASSWORD_KEY
argument_list|)
argument_list|)
operator|.
name|keyStore
argument_list|(
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.keystore.location"
argument_list|)
argument_list|,
name|getPassword
argument_list|(
name|sslConf
argument_list|,
name|WEB_APP_KEYSTORE_PASSWORD_KEY
argument_list|)
argument_list|,
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.keystore.type"
argument_list|,
literal|"jks"
argument_list|)
argument_list|)
operator|.
name|trustStore
argument_list|(
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.truststore.location"
argument_list|)
argument_list|,
name|getPassword
argument_list|(
name|sslConf
argument_list|,
name|WEB_APP_TRUSTSTORE_PASSWORD_KEY
argument_list|)
argument_list|,
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.truststore.type"
argument_list|,
literal|"jks"
argument_list|)
argument_list|)
operator|.
name|excludeCiphers
argument_list|(
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.exclude.cipher.list"
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Leverages the Configuration.getPassword method to attempt to get    * passwords from the CredentialProvider API before falling back to    * clear text in config - if falling back is allowed.    * @param conf Configuration instance    * @param alias name of the credential to retreive    * @return String credential value or null    */
DECL|method|getPassword (Configuration conf, String alias)
specifier|static
name|String
name|getPassword
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
name|String
name|password
init|=
literal|null
decl_stmt|;
try|try
block|{
name|char
index|[]
name|passchars
init|=
name|conf
operator|.
name|getPassword
argument_list|(
name|alias
argument_list|)
decl_stmt|;
if|if
condition|(
name|passchars
operator|!=
literal|null
condition|)
block|{
name|password
operator|=
operator|new
name|String
argument_list|(
name|passchars
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|password
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|password
return|;
block|}
DECL|method|parseApplicationId (RecordFactory recordFactory, String appId)
specifier|public
specifier|static
name|ApplicationId
name|parseApplicationId
parameter_list|(
name|RecordFactory
name|recordFactory
parameter_list|,
name|String
name|appId
parameter_list|)
block|{
if|if
condition|(
name|appId
operator|==
literal|null
operator|||
name|appId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"appId, "
operator|+
name|appId
operator|+
literal|", is empty or null"
argument_list|)
throw|;
block|}
name|ApplicationId
name|aid
init|=
literal|null
decl_stmt|;
try|try
block|{
name|aid
operator|=
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|appId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|aid
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"app with id "
operator|+
name|appId
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
return|return
name|aid
return|;
block|}
DECL|method|getSupportedLogContentType (String format)
specifier|public
specifier|static
name|String
name|getSupportedLogContentType
parameter_list|(
name|String
name|format
parameter_list|)
block|{
if|if
condition|(
name|format
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"text"
argument_list|)
condition|)
block|{
return|return
literal|"text/plain"
return|;
block|}
elseif|else
if|if
condition|(
name|format
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"octet-stream"
argument_list|)
condition|)
block|{
return|return
literal|"application/octet-stream"
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getDefaultLogContentType ()
specifier|public
specifier|static
name|String
name|getDefaultLogContentType
parameter_list|()
block|{
return|return
literal|"text/plain"
return|;
block|}
DECL|method|listSupportedLogContentType ()
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|listSupportedLogContentType
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
literal|"text"
argument_list|,
literal|"octet-stream"
argument_list|)
return|;
block|}
DECL|method|getURLEncodedQueryString (HttpServletRequest request)
specifier|private
specifier|static
name|String
name|getURLEncodedQueryString
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|queryString
init|=
name|request
operator|.
name|getQueryString
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryString
operator|!=
literal|null
operator|&&
operator|!
name|queryString
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|reqEncoding
init|=
name|request
operator|.
name|getCharacterEncoding
argument_list|()
decl_stmt|;
if|if
condition|(
name|reqEncoding
operator|==
literal|null
operator|||
name|reqEncoding
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|reqEncoding
operator|=
literal|"ISO-8859-1"
expr_stmt|;
block|}
name|Charset
name|encoding
init|=
name|Charset
operator|.
name|forName
argument_list|(
name|reqEncoding
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NameValuePair
argument_list|>
name|params
init|=
name|URLEncodedUtils
operator|.
name|parse
argument_list|(
name|queryString
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
return|return
name|URLEncodedUtils
operator|.
name|format
argument_list|(
name|params
argument_list|,
name|encoding
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get a HTML escaped uri with the query parameters of the request.    * @param request HttpServletRequest with the request details    * @return HTML escaped uri with the query paramters    */
DECL|method|getHtmlEscapedURIWithQueryString ( HttpServletRequest request)
specifier|public
specifier|static
name|String
name|getHtmlEscapedURIWithQueryString
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|urlEncodedQueryString
init|=
name|getURLEncodedQueryString
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|urlEncodedQueryString
operator|!=
literal|null
condition|)
block|{
return|return
name|HtmlQuoting
operator|.
name|quoteHtmlChars
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
operator|+
literal|"?"
operator|+
name|urlEncodedQueryString
argument_list|)
return|;
block|}
return|return
name|HtmlQuoting
operator|.
name|quoteHtmlChars
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Add the query params from a HttpServletRequest to the target uri passed.    * @param request HttpServletRequest with the request details    * @param targetUri the uri to which the query params must be added    * @return URL encoded string containing the targetUri + "?" + query string    */
DECL|method|appendQueryParams (HttpServletRequest request, String targetUri)
specifier|public
specifier|static
name|String
name|appendQueryParams
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|targetUri
parameter_list|)
block|{
name|String
name|ret
init|=
name|targetUri
decl_stmt|;
name|String
name|urlEncodedQueryString
init|=
name|getURLEncodedQueryString
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|urlEncodedQueryString
operator|!=
literal|null
condition|)
block|{
name|ret
operator|+=
literal|"?"
operator|+
name|urlEncodedQueryString
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

