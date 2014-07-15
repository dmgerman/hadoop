begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|HttpURLConnection
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
name|URL
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
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|GnuParser
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
name|cli
operator|.
name|HelpFormatter
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
name|cli
operator|.
name|Options
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
name|Unstable
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
name|io
operator|.
name|Text
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
name|UserGroupInformation
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
name|client
operator|.
name|AuthenticatedURL
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
name|client
operator|.
name|AuthenticationException
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
name|Token
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
name|timeline
operator|.
name|TimelineEntities
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
name|timeline
operator|.
name|TimelineEntity
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
name|timeline
operator|.
name|TimelinePutResponse
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
name|client
operator|.
name|api
operator|.
name|TimelineClient
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
name|YarnException
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
name|security
operator|.
name|client
operator|.
name|TimelineDelegationTokenIdentifier
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
name|security
operator|.
name|client
operator|.
name|TimelineDelegationTokenSelector
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
name|timeline
operator|.
name|TimelineUtils
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
name|YarnJacksonJaxbJsonProvider
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|ClientResponse
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
name|config
operator|.
name|ClientConfig
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
name|config
operator|.
name|DefaultClientConfig
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
name|client
operator|.
name|urlconnection
operator|.
name|HttpURLConnectionFactory
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
name|client
operator|.
name|urlconnection
operator|.
name|URLConnectionClientHandler
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineClientImpl
specifier|public
class|class
name|TimelineClientImpl
extends|extends
name|TimelineClient
block|{
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
name|TimelineClientImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RESOURCE_URI_STR
specifier|private
specifier|static
specifier|final
name|String
name|RESOURCE_URI_STR
init|=
literal|"/ws/v1/timeline/"
decl_stmt|;
DECL|field|URL_PARAM_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|URL_PARAM_USER_NAME
init|=
literal|"user.name"
decl_stmt|;
DECL|field|JOINER
specifier|private
specifier|static
specifier|final
name|Joiner
name|JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|field|opts
specifier|private
specifier|static
name|Options
name|opts
decl_stmt|;
static|static
block|{
name|opts
operator|=
operator|new
name|Options
argument_list|()
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"put"
argument_list|,
literal|true
argument_list|,
literal|"Put the TimelineEntities in a JSON file"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|getOption
argument_list|(
literal|"put"
argument_list|)
operator|.
name|setArgName
argument_list|(
literal|"Path to the JSON file"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
literal|"help"
argument_list|,
literal|false
argument_list|,
literal|"Print usage"
argument_list|)
expr_stmt|;
block|}
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
DECL|field|resURI
specifier|private
name|URI
name|resURI
decl_stmt|;
DECL|field|isEnabled
specifier|private
name|boolean
name|isEnabled
decl_stmt|;
DECL|field|urlFactory
specifier|private
name|KerberosAuthenticatedURLConnectionFactory
name|urlFactory
decl_stmt|;
DECL|method|TimelineClientImpl ()
specifier|public
name|TimelineClientImpl
parameter_list|()
block|{
name|super
argument_list|(
name|TimelineClientImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ClientConfig
name|cc
init|=
operator|new
name|DefaultClientConfig
argument_list|()
decl_stmt|;
name|cc
operator|.
name|getClasses
argument_list|()
operator|.
name|add
argument_list|(
name|YarnJacksonJaxbJsonProvider
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|urlFactory
operator|=
operator|new
name|KerberosAuthenticatedURLConnectionFactory
argument_list|()
expr_stmt|;
name|client
operator|=
operator|new
name|Client
argument_list|(
operator|new
name|URLConnectionClientHandler
argument_list|(
name|urlFactory
argument_list|)
argument_list|,
name|cc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|client
operator|=
operator|new
name|Client
argument_list|(
operator|new
name|URLConnectionClientHandler
argument_list|(
operator|new
name|PseudoAuthenticatedURLConnectionFactory
argument_list|()
argument_list|)
argument_list|,
name|cc
argument_list|)
expr_stmt|;
block|}
block|}
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
name|isEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_ENABLED
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isEnabled
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Timeline service is not enabled"
argument_list|)
expr_stmt|;
block|}
else|else
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
name|resURI
operator|=
name|URI
operator|.
name|create
argument_list|(
name|JOINER
operator|.
name|join
argument_list|(
literal|"https://"
argument_list|,
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
argument_list|,
name|RESOURCE_URI_STR
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resURI
operator|=
name|URI
operator|.
name|create
argument_list|(
name|JOINER
operator|.
name|join
argument_list|(
literal|"http://"
argument_list|,
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
argument_list|,
name|RESOURCE_URI_STR
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|urlFactory
operator|.
name|setService
argument_list|(
name|TimelineUtils
operator|.
name|buildTimelineTokenService
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Timeline service address: "
operator|+
name|resURI
argument_list|)
expr_stmt|;
block|}
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
DECL|method|putEntities ( TimelineEntity... entities)
specifier|public
name|TimelinePutResponse
name|putEntities
parameter_list|(
name|TimelineEntity
modifier|...
name|entities
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
if|if
condition|(
operator|!
name|isEnabled
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Nothing will be put because timeline service is not enabled"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TimelinePutResponse
argument_list|()
return|;
block|}
name|TimelineEntities
name|entitiesContainer
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|entitiesContainer
operator|.
name|addEntities
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|entities
argument_list|)
argument_list|)
expr_stmt|;
name|ClientResponse
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|doPostingEntities
argument_list|(
name|entitiesContainer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
comment|// runtime exception is expected if the client cannot connect the server
name|String
name|msg
init|=
literal|"Failed to get the response from the timeline server."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|re
argument_list|)
expr_stmt|;
throw|throw
name|re
throw|;
block|}
if|if
condition|(
name|resp
operator|==
literal|null
operator|||
name|resp
operator|.
name|getClientResponseStatus
argument_list|()
operator|!=
name|ClientResponse
operator|.
name|Status
operator|.
name|OK
condition|)
block|{
name|String
name|msg
init|=
literal|"Failed to get the response from the timeline server."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
name|resp
operator|!=
literal|null
condition|)
block|{
name|String
name|output
init|=
name|resp
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
name|debug
argument_list|(
literal|"HTTP error code: "
operator|+
name|resp
operator|.
name|getStatus
argument_list|()
operator|+
literal|" Server response : \n"
operator|+
name|output
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|YarnException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
return|return
name|resp
operator|.
name|getEntity
argument_list|(
name|TimelinePutResponse
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDelegationToken ( String renewer)
specifier|public
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
return|return
name|TimelineAuthenticator
operator|.
name|getDelegationToken
argument_list|(
name|resURI
operator|.
name|toURL
argument_list|()
argument_list|,
name|urlFactory
operator|.
name|token
argument_list|,
name|renewer
argument_list|)
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|doPostingEntities (TimelineEntities entities)
specifier|public
name|ClientResponse
name|doPostingEntities
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|)
block|{
name|WebResource
name|webResource
init|=
name|client
operator|.
name|resource
argument_list|(
name|resURI
argument_list|)
decl_stmt|;
return|return
name|webResource
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|,
name|entities
argument_list|)
return|;
block|}
DECL|class|PseudoAuthenticatedURLConnectionFactory
specifier|private
specifier|static
class|class
name|PseudoAuthenticatedURLConnectionFactory
implements|implements
name|HttpURLConnectionFactory
block|{
annotation|@
name|Override
DECL|method|getHttpURLConnection (URL url)
specifier|public
name|HttpURLConnection
name|getHttpURLConnection
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|URL_PARAM_USER_NAME
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|url
operator|=
name|TimelineAuthenticator
operator|.
name|appendParams
argument_list|(
name|url
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"URL with delegation token: "
operator|+
name|url
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
return|;
block|}
block|}
DECL|class|KerberosAuthenticatedURLConnectionFactory
specifier|private
specifier|static
class|class
name|KerberosAuthenticatedURLConnectionFactory
implements|implements
name|HttpURLConnectionFactory
block|{
DECL|field|token
specifier|private
name|AuthenticatedURL
operator|.
name|Token
name|token
decl_stmt|;
DECL|field|authenticator
specifier|private
name|TimelineAuthenticator
name|authenticator
decl_stmt|;
DECL|field|dToken
specifier|private
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|dToken
decl_stmt|;
DECL|field|service
specifier|private
name|Text
name|service
decl_stmt|;
DECL|method|KerberosAuthenticatedURLConnectionFactory ()
specifier|public
name|KerberosAuthenticatedURLConnectionFactory
parameter_list|()
block|{
name|token
operator|=
operator|new
name|AuthenticatedURL
operator|.
name|Token
argument_list|()
expr_stmt|;
name|authenticator
operator|=
operator|new
name|TimelineAuthenticator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getHttpURLConnection (URL url)
specifier|public
name|HttpURLConnection
name|getHttpURLConnection
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|dToken
operator|==
literal|null
condition|)
block|{
comment|//TODO: need to take care of the renew case
name|dToken
operator|=
name|selectToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Timeline delegation token: "
operator|+
name|dToken
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|dToken
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|TimelineAuthenticator
operator|.
name|injectDelegationToken
argument_list|(
name|params
argument_list|,
name|dToken
argument_list|)
expr_stmt|;
name|url
operator|=
name|TimelineAuthenticator
operator|.
name|appendParams
argument_list|(
name|url
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"URL with delegation token: "
operator|+
name|url
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|AuthenticatedURL
argument_list|(
name|authenticator
argument_list|)
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|token
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Authentication failed when openning connection ["
operator|+
name|url
operator|+
literal|"] with token ["
operator|+
name|token
operator|+
literal|"]."
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|selectToken ()
specifier|private
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|selectToken
parameter_list|()
block|{
name|UserGroupInformation
name|ugi
decl_stmt|;
try|try
block|{
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Error when getting the current user"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|TimelineDelegationTokenSelector
name|tokenSelector
init|=
operator|new
name|TimelineDelegationTokenSelector
argument_list|()
decl_stmt|;
return|return
name|tokenSelector
operator|.
name|selectToken
argument_list|(
name|service
argument_list|,
name|ugi
operator|.
name|getCredentials
argument_list|()
operator|.
name|getAllTokens
argument_list|()
argument_list|)
return|;
block|}
DECL|method|setService (Text service)
specifier|public
name|void
name|setService
parameter_list|(
name|Text
name|service
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
block|}
block|}
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|CommandLine
name|cliParser
init|=
operator|new
name|GnuParser
argument_list|()
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|argv
argument_list|)
decl_stmt|;
if|if
condition|(
name|cliParser
operator|.
name|hasOption
argument_list|(
literal|"put"
argument_list|)
condition|)
block|{
name|String
name|path
init|=
name|cliParser
operator|.
name|getOptionValue
argument_list|(
literal|"put"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
name|path
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|putTimelineEntitiesInJSONFile
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|printUsage
argument_list|()
expr_stmt|;
block|}
comment|/**    * Put timeline data in a JSON file via command line.    *     * @param path    *          path to the {@link TimelineEntities} JSON file    */
DECL|method|putTimelineEntitiesInJSONFile (String path)
specifier|private
specifier|static
name|void
name|putTimelineEntitiesInJSONFile
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|File
name|jsonFile
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|jsonFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Error: File ["
operator|+
name|jsonFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"] doesn't exist"
argument_list|)
expr_stmt|;
return|return;
block|}
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|YarnJacksonJaxbJsonProvider
operator|.
name|configObjectMapper
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
name|TimelineEntities
name|entities
init|=
literal|null
decl_stmt|;
try|try
block|{
name|entities
operator|=
name|mapper
operator|.
name|readValue
argument_list|(
name|jsonFile
argument_list|,
name|TimelineEntities
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
return|return;
block|}
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|TimelineClient
name|client
init|=
name|TimelineClient
operator|.
name|createTimelineClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
operator|&&
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|token
init|=
name|client
operator|.
name|getDelegationToken
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
decl_stmt|;
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
name|TimelinePutResponse
name|response
init|=
name|client
operator|.
name|putEntities
argument_list|(
name|entities
operator|.
name|getEntities
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|TimelineEntity
index|[
name|entities
operator|.
name|getEntities
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getErrors
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Timeline data is successfully put"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|TimelinePutResponse
operator|.
name|TimelinePutError
name|error
range|:
name|response
operator|.
name|getErrors
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TimelineEntity ["
operator|+
name|error
operator|.
name|getEntityType
argument_list|()
operator|+
literal|":"
operator|+
name|error
operator|.
name|getEntityId
argument_list|()
operator|+
literal|"] is not successfully put. Error code: "
operator|+
name|error
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Helper function to print out usage    */
DECL|method|printUsage ()
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
operator|new
name|HelpFormatter
argument_list|()
operator|.
name|printHelp
argument_list|(
literal|"TimelineClient"
argument_list|,
name|opts
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

