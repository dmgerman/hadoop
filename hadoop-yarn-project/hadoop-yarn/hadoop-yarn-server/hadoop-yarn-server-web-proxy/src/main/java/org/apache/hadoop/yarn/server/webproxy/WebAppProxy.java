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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|service
operator|.
name|AbstractService
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
name|util
operator|.
name|StringUtils
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
name|webapp
operator|.
name|util
operator|.
name|WebAppUtils
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
name|fs
operator|.
name|CommonConfigurationKeys
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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

begin_class
DECL|class|WebAppProxy
specifier|public
class|class
name|WebAppProxy
extends|extends
name|AbstractService
block|{
DECL|field|FETCHER_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|FETCHER_ATTRIBUTE
init|=
literal|"AppUrlFetcher"
decl_stmt|;
DECL|field|IS_SECURITY_ENABLED_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|IS_SECURITY_ENABLED_ATTRIBUTE
init|=
literal|"IsSecurityEnabled"
decl_stmt|;
DECL|field|PROXY_HOST_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_HOST_ATTRIBUTE
init|=
literal|"proxyHost"
decl_stmt|;
DECL|field|PROXY_CA
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_CA
init|=
literal|"ProxyCA"
decl_stmt|;
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
name|WebAppProxy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|proxyServer
specifier|private
name|HttpServer2
name|proxyServer
init|=
literal|null
decl_stmt|;
DECL|field|bindAddress
specifier|private
name|String
name|bindAddress
init|=
literal|null
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|acl
specifier|private
name|AccessControlList
name|acl
init|=
literal|null
decl_stmt|;
DECL|field|fetcher
specifier|private
name|AppReportFetcher
name|fetcher
init|=
literal|null
decl_stmt|;
DECL|field|isSecurityEnabled
specifier|private
name|boolean
name|isSecurityEnabled
init|=
literal|false
decl_stmt|;
DECL|field|proxyHost
specifier|private
name|String
name|proxyHost
init|=
literal|null
decl_stmt|;
DECL|method|WebAppProxy ()
specifier|public
name|WebAppProxy
parameter_list|()
block|{
name|super
argument_list|(
name|WebAppProxy
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|auth
init|=
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|auth
operator|==
literal|null
operator|||
literal|"simple"
operator|.
name|equals
argument_list|(
name|auth
argument_list|)
condition|)
block|{
name|isSecurityEnabled
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"kerberos"
operator|.
name|equals
argument_list|(
name|auth
argument_list|)
condition|)
block|{
name|isSecurityEnabled
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unrecognized attribute value for "
operator|+
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
operator|+
literal|" of "
operator|+
name|auth
argument_list|)
expr_stmt|;
block|}
name|String
name|proxy
init|=
name|WebAppUtils
operator|.
name|getProxyHostAndPort
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
index|[]
name|proxyParts
init|=
name|proxy
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|proxyHost
operator|=
name|proxyParts
index|[
literal|0
index|]
expr_stmt|;
name|fetcher
operator|=
operator|new
name|AppReportFetcher
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|bindAddress
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|PROXY_ADDRESS
argument_list|)
expr_stmt|;
if|if
condition|(
name|bindAddress
operator|==
literal|null
operator|||
name|bindAddress
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|YarnConfiguration
operator|.
name|PROXY_ADDRESS
operator|+
literal|" is not set so the proxy will not run."
argument_list|)
throw|;
block|}
name|String
index|[]
name|parts
init|=
name|StringUtils
operator|.
name|split
argument_list|(
name|bindAddress
argument_list|,
literal|':'
argument_list|)
decl_stmt|;
name|port
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|bindAddress
operator|=
name|parts
index|[
literal|0
index|]
expr_stmt|;
name|port
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|String
name|bindHost
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|YarnConfiguration
operator|.
name|PROXY_BIND_HOST
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|bindHost
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{} is set, will be used to run proxy."
argument_list|,
name|YarnConfiguration
operator|.
name|PROXY_BIND_HOST
argument_list|)
expr_stmt|;
name|bindAddress
operator|=
name|bindHost
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Instantiating Proxy at {}:{}"
argument_list|,
name|bindAddress
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ADMIN_ACL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_YARN_ADMIN_ACL
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|HttpServer2
operator|.
name|Builder
name|b
init|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"proxy"
argument_list|)
operator|.
name|addEndpoint
argument_list|(
name|URI
operator|.
name|create
argument_list|(
name|WebAppUtils
operator|.
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
operator|+
name|bindAddress
operator|+
literal|":"
operator|+
name|port
argument_list|)
argument_list|)
operator|.
name|setFindPort
argument_list|(
name|port
operator|==
literal|0
argument_list|)
operator|.
name|setConf
argument_list|(
name|getConfig
argument_list|()
argument_list|)
operator|.
name|setACL
argument_list|(
name|acl
argument_list|)
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
name|WebAppUtils
operator|.
name|loadSslConfiguration
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|proxyServer
operator|=
name|b
operator|.
name|build
argument_list|()
expr_stmt|;
name|proxyServer
operator|.
name|addServlet
argument_list|(
name|ProxyUriUtils
operator|.
name|PROXY_SERVLET_NAME
argument_list|,
name|ProxyUriUtils
operator|.
name|PROXY_PATH_SPEC
argument_list|,
name|WebAppProxyServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|proxyServer
operator|.
name|setAttribute
argument_list|(
name|FETCHER_ATTRIBUTE
argument_list|,
name|fetcher
argument_list|)
expr_stmt|;
name|proxyServer
operator|.
name|setAttribute
argument_list|(
name|IS_SECURITY_ENABLED_ATTRIBUTE
argument_list|,
name|isSecurityEnabled
argument_list|)
expr_stmt|;
name|proxyServer
operator|.
name|setAttribute
argument_list|(
name|PROXY_HOST_ATTRIBUTE
argument_list|,
name|proxyHost
argument_list|)
expr_stmt|;
name|proxyServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not start proxy web server"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|proxyServer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|proxyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error stopping proxy web server"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Error stopping proxy web server"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|this
operator|.
name|fetcher
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|fetcher
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|join ()
specifier|public
name|void
name|join
parameter_list|()
block|{
if|if
condition|(
name|proxyServer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|proxyServer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignored
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getBindAddress ()
name|String
name|getBindAddress
parameter_list|()
block|{
return|return
name|bindAddress
operator|+
literal|":"
operator|+
name|port
return|;
block|}
block|}
end_class

end_unit

