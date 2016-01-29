begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
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
name|pjoin
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
name|PrintWriter
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
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Singleton
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|server
operator|.
name|webproxy
operator|.
name|ProxyUriUtils
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
name|Apps
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
name|webapp
operator|.
name|YarnWebParams
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
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
name|guice
operator|.
name|spi
operator|.
name|container
operator|.
name|servlet
operator|.
name|GuiceContainer
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|RMWebAppFilter
specifier|public
class|class
name|RMWebAppFilter
extends|extends
name|GuiceContainer
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
name|RMWebAppFilter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
comment|/**    *     */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|// define a set of URIs which do not need to do redirection
DECL|field|NON_REDIRECTED_URIS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|NON_REDIRECTED_URIS
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/conf"
argument_list|,
literal|"/stacks"
argument_list|,
literal|"/logLevel"
argument_list|,
literal|"/logs"
argument_list|)
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|ahsEnabled
specifier|private
name|boolean
name|ahsEnabled
decl_stmt|;
DECL|field|ahsPageURLPrefix
specifier|private
name|String
name|ahsPageURLPrefix
decl_stmt|;
DECL|field|BASIC_SLEEP_TIME
specifier|private
specifier|static
specifier|final
name|int
name|BASIC_SLEEP_TIME
init|=
literal|5
decl_stmt|;
DECL|field|MAX_SLEEP_TIME
specifier|private
specifier|static
specifier|final
name|int
name|MAX_SLEEP_TIME
init|=
literal|5
operator|*
literal|60
decl_stmt|;
DECL|field|randnum
specifier|private
specifier|static
specifier|final
name|Random
name|randnum
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|RMWebAppFilter (Injector injector, Configuration conf)
specifier|public
name|RMWebAppFilter
parameter_list|(
name|Injector
name|injector
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
name|InetSocketAddress
name|sock
init|=
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|?
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
else|:
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
decl_stmt|;
name|path
operator|=
name|sock
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|sock
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|path
operator|=
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|?
literal|"https://"
operator|+
name|path
else|:
literal|"http://"
operator|+
name|path
expr_stmt|;
name|ahsEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_APPLICATION_HISTORY_ENABLED
argument_list|)
expr_stmt|;
name|ahsPageURLPrefix
operator|=
name|pjoin
argument_list|(
name|WebAppUtils
operator|.
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
operator|+
name|WebAppUtils
operator|.
name|getAHSWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|"applicationhistory"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doFilter (HttpServletRequest request, HttpServletResponse response, FilterChain chain)
specifier|public
name|void
name|doFilter
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|response
operator|.
name|setCharacterEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|String
name|uri
init|=
name|HtmlQuoting
operator|.
name|quoteHtmlChars
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
literal|"/"
expr_stmt|;
block|}
name|RMWebApp
name|rmWebApp
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|RMWebApp
operator|.
name|class
argument_list|)
decl_stmt|;
name|rmWebApp
operator|.
name|checkIfStandbyRM
argument_list|()
expr_stmt|;
if|if
condition|(
name|rmWebApp
operator|.
name|isStandby
argument_list|()
operator|&&
name|shouldRedirect
argument_list|(
name|rmWebApp
argument_list|,
name|uri
argument_list|)
condition|)
block|{
name|String
name|redirectPath
init|=
name|rmWebApp
operator|.
name|getRedirectPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|redirectPath
operator|!=
literal|null
operator|&&
operator|!
name|redirectPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|redirectPath
operator|+=
name|uri
expr_stmt|;
name|String
name|redirectMsg
init|=
literal|"This is standby RM. The redirect url is: "
operator|+
name|redirectPath
decl_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|redirectMsg
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
literal|"Location"
argument_list|,
name|redirectPath
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_TEMPORARY_REDIRECT
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|boolean
name|doRetry
init|=
literal|true
decl_stmt|;
name|String
name|retryIntervalStr
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|YarnWebParams
operator|.
name|NEXT_REFRESH_INTERVAL
argument_list|)
decl_stmt|;
name|int
name|retryInterval
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|retryIntervalStr
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|retryInterval
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|retryIntervalStr
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
name|doRetry
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|int
name|next
init|=
name|calculateExponentialTime
argument_list|(
name|retryInterval
argument_list|)
decl_stmt|;
name|String
name|redirectUrl
init|=
name|appendOrReplaceParamter
argument_list|(
name|path
operator|+
name|uri
argument_list|,
name|YarnWebParams
operator|.
name|NEXT_REFRESH_INTERVAL
operator|+
literal|"="
operator|+
operator|(
name|retryInterval
operator|+
literal|1
operator|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|redirectUrl
operator|==
literal|null
operator|||
name|next
operator|>
name|MAX_SLEEP_TIME
condition|)
block|{
name|doRetry
operator|=
literal|false
expr_stmt|;
block|}
name|String
name|redirectMsg
init|=
name|doRetry
condition|?
literal|"Can not find any active RM. Will retry in next "
operator|+
name|next
operator|+
literal|" seconds."
else|:
literal|"There is no active RM right now."
decl_stmt|;
name|redirectMsg
operator|+=
literal|"\nHA Zookeeper Connection State: "
operator|+
name|rmWebApp
operator|.
name|getHAZookeeperConnectionState
argument_list|()
expr_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|redirectMsg
argument_list|)
expr_stmt|;
if|if
condition|(
name|doRetry
condition|)
block|{
name|response
operator|.
name|setHeader
argument_list|(
literal|"Refresh"
argument_list|,
name|next
operator|+
literal|";url="
operator|+
name|redirectUrl
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_TEMPORARY_REDIRECT
argument_list|)
expr_stmt|;
block|}
block|}
return|return;
block|}
elseif|else
if|if
condition|(
name|ahsEnabled
condition|)
block|{
name|String
name|ahsRedirectUrl
init|=
name|ahsRedirectPath
argument_list|(
name|uri
argument_list|,
name|rmWebApp
argument_list|)
decl_stmt|;
if|if
condition|(
name|ahsRedirectUrl
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|setHeader
argument_list|(
literal|"Location"
argument_list|,
name|ahsRedirectUrl
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_TEMPORARY_REDIRECT
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|super
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|chain
argument_list|)
expr_stmt|;
block|}
DECL|method|ahsRedirectPath (String uri, RMWebApp rmWebApp)
specifier|private
name|String
name|ahsRedirectPath
parameter_list|(
name|String
name|uri
parameter_list|,
name|RMWebApp
name|rmWebApp
parameter_list|)
block|{
comment|// TODO: Commonize URL parsing code. Will be done in YARN-4642.
name|String
name|redirectPath
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|contains
argument_list|(
literal|"/cluster/"
argument_list|)
condition|)
block|{
name|String
index|[]
name|parts
init|=
name|uri
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|>
literal|3
condition|)
block|{
name|RMContext
name|context
init|=
name|rmWebApp
operator|.
name|getRMContext
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|parts
index|[
literal|2
index|]
decl_stmt|;
name|ApplicationId
name|appId
init|=
literal|null
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
literal|null
decl_stmt|;
name|ContainerId
name|containerId
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
literal|"app"
case|:
try|try
block|{
name|appId
operator|=
name|Apps
operator|.
name|toAppID
argument_list|(
name|parts
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
decl||
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error parsing {} as an ApplicationId"
argument_list|,
name|parts
index|[
literal|3
index|]
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|redirectPath
return|;
block|}
if|if
condition|(
operator|!
name|context
operator|.
name|getRMApps
argument_list|()
operator|.
name|containsKey
argument_list|(
name|appId
argument_list|)
condition|)
block|{
name|redirectPath
operator|=
name|pjoin
argument_list|(
name|ahsPageURLPrefix
argument_list|,
literal|"app"
argument_list|,
name|appId
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|"appattempt"
case|:
try|try
block|{
name|appAttemptId
operator|=
name|ConverterUtils
operator|.
name|toApplicationAttemptId
argument_list|(
name|parts
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error parsing {} as an ApplicationAttemptId"
argument_list|,
name|parts
index|[
literal|3
index|]
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|redirectPath
return|;
block|}
if|if
condition|(
operator|!
name|context
operator|.
name|getRMApps
argument_list|()
operator|.
name|containsKey
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
name|redirectPath
operator|=
name|pjoin
argument_list|(
name|ahsPageURLPrefix
argument_list|,
literal|"appattempt"
argument_list|,
name|appAttemptId
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|"container"
case|:
try|try
block|{
name|containerId
operator|=
name|ContainerId
operator|.
name|fromString
argument_list|(
name|parts
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error parsing {} as an ContainerId"
argument_list|,
name|parts
index|[
literal|3
index|]
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|redirectPath
return|;
block|}
if|if
condition|(
operator|!
name|context
operator|.
name|getRMApps
argument_list|()
operator|.
name|containsKey
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
name|redirectPath
operator|=
name|pjoin
argument_list|(
name|ahsPageURLPrefix
argument_list|,
literal|"container"
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
break|break;
block|}
block|}
block|}
return|return
name|redirectPath
return|;
block|}
DECL|method|shouldRedirect (RMWebApp rmWebApp, String uri)
specifier|private
name|boolean
name|shouldRedirect
parameter_list|(
name|RMWebApp
name|rmWebApp
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
return|return
operator|!
name|uri
operator|.
name|equals
argument_list|(
literal|"/"
operator|+
name|rmWebApp
operator|.
name|wsName
argument_list|()
operator|+
literal|"/v1/cluster/info"
argument_list|)
operator|&&
operator|!
name|uri
operator|.
name|equals
argument_list|(
literal|"/"
operator|+
name|rmWebApp
operator|.
name|name
argument_list|()
operator|+
literal|"/cluster"
argument_list|)
operator|&&
operator|!
name|uri
operator|.
name|startsWith
argument_list|(
name|ProxyUriUtils
operator|.
name|PROXY_BASE
argument_list|)
operator|&&
operator|!
name|NON_REDIRECTED_URIS
operator|.
name|contains
argument_list|(
name|uri
argument_list|)
return|;
block|}
DECL|method|appendOrReplaceParamter (String uri, String newQuery)
specifier|private
name|String
name|appendOrReplaceParamter
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|newQuery
parameter_list|)
block|{
if|if
condition|(
name|uri
operator|.
name|contains
argument_list|(
name|YarnWebParams
operator|.
name|NEXT_REFRESH_INTERVAL
operator|+
literal|"="
argument_list|)
condition|)
block|{
return|return
name|uri
operator|.
name|replaceAll
argument_list|(
name|YarnWebParams
operator|.
name|NEXT_REFRESH_INTERVAL
operator|+
literal|"=[^&]+"
argument_list|,
name|newQuery
argument_list|)
return|;
block|}
try|try
block|{
name|URI
name|oldUri
init|=
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|String
name|appendQuery
init|=
name|oldUri
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|appendQuery
operator|==
literal|null
condition|)
block|{
name|appendQuery
operator|=
name|newQuery
expr_stmt|;
block|}
else|else
block|{
name|appendQuery
operator|+=
literal|"&"
operator|+
name|newQuery
expr_stmt|;
block|}
name|URI
name|newUri
init|=
operator|new
name|URI
argument_list|(
name|oldUri
operator|.
name|getScheme
argument_list|()
argument_list|,
name|oldUri
operator|.
name|getAuthority
argument_list|()
argument_list|,
name|oldUri
operator|.
name|getPath
argument_list|()
argument_list|,
name|appendQuery
argument_list|,
name|oldUri
operator|.
name|getFragment
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|newUri
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|calculateExponentialTime (int retries)
specifier|private
specifier|static
name|int
name|calculateExponentialTime
parameter_list|(
name|int
name|retries
parameter_list|)
block|{
name|long
name|baseTime
init|=
name|BASIC_SLEEP_TIME
operator|*
operator|(
literal|1L
operator|<<
name|retries
operator|)
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|baseTime
operator|*
operator|(
name|randnum
operator|.
name|nextDouble
argument_list|()
operator|+
literal|0.5
operator|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

