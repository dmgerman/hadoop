begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.collector
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
name|timelineservice
operator|.
name|collector
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
name|util
operator|.
name|LinkedHashSet
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledThreadPoolExecutor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|security
operator|.
name|SecurityUtil
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
name|util
operator|.
name|Time
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
name|ipc
operator|.
name|YarnRPC
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
name|server
operator|.
name|api
operator|.
name|CollectorNodemanagerProtocol
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetTimelineCollectorContextRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetTimelineCollectorContextResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|ReportNewCollectorInfoRequest
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
name|timelineservice
operator|.
name|security
operator|.
name|TimelineV2DelegationTokenSecretManagerService
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
name|util
operator|.
name|timeline
operator|.
name|TimelineServerUtils
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
name|GenericExceptionHandler
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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

begin_comment
comment|/**  * Class on the NodeManager side that manages adding and removing collectors and  * their lifecycle. Also instantiates the per-node collector webapp.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|NodeTimelineCollectorManager
specifier|public
class|class
name|NodeTimelineCollectorManager
extends|extends
name|TimelineCollectorManager
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
name|NodeTimelineCollectorManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// REST server for this collector manager.
DECL|field|timelineRestServer
specifier|private
name|HttpServer2
name|timelineRestServer
decl_stmt|;
DECL|field|timelineRestServerBindAddress
specifier|private
name|String
name|timelineRestServerBindAddress
decl_stmt|;
DECL|field|nmCollectorService
specifier|private
specifier|volatile
name|CollectorNodemanagerProtocol
name|nmCollectorService
decl_stmt|;
DECL|field|tokenMgrService
specifier|private
name|TimelineV2DelegationTokenSecretManagerService
name|tokenMgrService
decl_stmt|;
DECL|field|runningAsAuxService
specifier|private
specifier|final
name|boolean
name|runningAsAuxService
decl_stmt|;
DECL|field|loginUGI
specifier|private
name|UserGroupInformation
name|loginUGI
decl_stmt|;
DECL|field|tokenRenewalExecutor
specifier|private
name|ScheduledThreadPoolExecutor
name|tokenRenewalExecutor
decl_stmt|;
DECL|field|tokenRenewInterval
specifier|private
name|long
name|tokenRenewInterval
decl_stmt|;
DECL|field|TIME_BEFORE_RENEW_DATE
specifier|private
specifier|static
specifier|final
name|long
name|TIME_BEFORE_RENEW_DATE
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
comment|// 10 seconds.
DECL|field|COLLECTOR_MANAGER_ATTR_KEY
specifier|static
specifier|final
name|String
name|COLLECTOR_MANAGER_ATTR_KEY
init|=
literal|"collector.manager"
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|NodeTimelineCollectorManager ()
specifier|protected
name|NodeTimelineCollectorManager
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|NodeTimelineCollectorManager (boolean asAuxService)
specifier|protected
name|NodeTimelineCollectorManager
parameter_list|(
name|boolean
name|asAuxService
parameter_list|)
block|{
name|super
argument_list|(
name|NodeTimelineCollectorManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|runningAsAuxService
operator|=
name|asAuxService
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
name|tokenMgrService
operator|=
name|createTokenManagerService
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|tokenMgrService
argument_list|)
expr_stmt|;
name|this
operator|.
name|loginUGI
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|tokenRenewInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_DELEGATION_TOKEN_RENEW_INTERVAL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_DELEGATION_TOKEN_RENEW_INTERVAL
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
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
comment|// Do security login for cases where collector is running outside NM.
if|if
condition|(
operator|!
name|runningAsAuxService
condition|)
block|{
try|try
block|{
name|doSecureLogin
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Failed to login"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|loginUGI
operator|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
expr_stmt|;
block|}
name|tokenRenewalExecutor
operator|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"App Collector Token Renewal thread"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
name|startWebApp
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|TimelineV2DelegationTokenSecretManagerService
DECL|method|createTokenManagerService ()
name|createTokenManagerService
parameter_list|()
block|{
return|return
operator|new
name|TimelineV2DelegationTokenSecretManagerService
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
specifier|public
name|TimelineV2DelegationTokenSecretManagerService
DECL|method|getTokenManagerService ()
name|getTokenManagerService
parameter_list|()
block|{
return|return
name|tokenMgrService
return|;
block|}
DECL|method|doSecureLogin ()
specifier|private
name|void
name|doSecureLogin
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|conf
operator|.
name|getTrimmed
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_BIND_HOST
argument_list|)
argument_list|,
literal|0
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_BIND_HOST
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_KEYTAB
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_PRINCIPAL
argument_list|,
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|)
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
name|timelineRestServer
operator|!=
literal|null
condition|)
block|{
name|timelineRestServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|tokenRenewalExecutor
operator|!=
literal|null
condition|)
block|{
name|tokenRenewalExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|generateTokenForAppCollector ( String user)
specifier|public
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|generateTokenForAppCollector
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|token
init|=
name|tokenMgrService
operator|.
name|generateToken
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
argument_list|,
name|loginUGI
operator|.
name|getShortUserName
argument_list|()
argument_list|)
decl_stmt|;
name|token
operator|.
name|setService
argument_list|(
operator|new
name|Text
argument_list|(
name|timelineRestServerBindAddress
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|token
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|renewTokenForAppCollector ( AppLevelTimelineCollector appCollector)
specifier|public
name|long
name|renewTokenForAppCollector
parameter_list|(
name|AppLevelTimelineCollector
name|appCollector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|appCollector
operator|.
name|getDelegationTokenForApp
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|TimelineDelegationTokenIdentifier
name|identifier
init|=
name|appCollector
operator|.
name|getDelegationTokenForApp
argument_list|()
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
return|return
name|tokenMgrService
operator|.
name|renewToken
argument_list|(
name|appCollector
operator|.
name|getDelegationTokenForApp
argument_list|()
argument_list|,
name|identifier
operator|.
name|getRenewer
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Delegation token not available for renewal for app "
operator|+
name|appCollector
operator|.
name|getTimelineEntityContext
argument_list|()
operator|.
name|getAppId
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|cancelTokenForAppCollector ( AppLevelTimelineCollector appCollector)
specifier|public
name|void
name|cancelTokenForAppCollector
parameter_list|(
name|AppLevelTimelineCollector
name|appCollector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|appCollector
operator|.
name|getDelegationTokenForApp
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tokenMgrService
operator|.
name|cancelToken
argument_list|(
name|appCollector
operator|.
name|getDelegationTokenForApp
argument_list|()
argument_list|,
name|appCollector
operator|.
name|getAppUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doPostPut (ApplicationId appId, TimelineCollector collector)
specifier|protected
name|void
name|doPostPut
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|TimelineCollector
name|collector
parameter_list|)
block|{
try|try
block|{
comment|// Get context info from NM
name|updateTimelineCollectorContext
argument_list|(
name|appId
argument_list|,
name|collector
argument_list|)
expr_stmt|;
comment|// Generate token for app collector.
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
name|Token
name|token
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
operator|&&
name|collector
operator|instanceof
name|AppLevelTimelineCollector
condition|)
block|{
name|AppLevelTimelineCollector
name|appCollector
init|=
operator|(
name|AppLevelTimelineCollector
operator|)
name|collector
decl_stmt|;
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|timelineToken
init|=
name|generateTokenForAppCollector
argument_list|(
name|appCollector
operator|.
name|getAppUser
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|renewalDelay
init|=
operator|(
name|tokenRenewInterval
operator|>
name|TIME_BEFORE_RENEW_DATE
operator|)
condition|?
name|tokenRenewInterval
operator|-
name|TIME_BEFORE_RENEW_DATE
else|:
name|tokenRenewInterval
decl_stmt|;
name|Future
argument_list|<
name|?
argument_list|>
name|renewalFuture
init|=
name|tokenRenewalExecutor
operator|.
name|schedule
argument_list|(
operator|new
name|CollectorTokenRenewer
argument_list|(
name|appId
argument_list|)
argument_list|,
name|renewalDelay
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|appCollector
operator|.
name|setDelegationTokenAndFutureForApp
argument_list|(
name|timelineToken
argument_list|,
name|renewalFuture
argument_list|)
expr_stmt|;
name|token
operator|=
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
name|Token
operator|.
name|newInstance
argument_list|(
name|timelineToken
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|timelineToken
operator|.
name|getKind
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|timelineToken
operator|.
name|getPassword
argument_list|()
argument_list|,
name|timelineToken
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Report to NM if a new collector is added.
name|reportNewCollectorInfoToNM
argument_list|(
name|appId
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
decl||
name|IOException
name|e
parameter_list|)
block|{
comment|// throw exception here as it cannot be used if failed communicate with NM
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to communicate with NM Collector Service for "
operator|+
name|appId
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|postRemove (ApplicationId appId, TimelineCollector collector)
specifier|protected
name|void
name|postRemove
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|TimelineCollector
name|collector
parameter_list|)
block|{
if|if
condition|(
name|collector
operator|instanceof
name|AppLevelTimelineCollector
condition|)
block|{
try|try
block|{
name|cancelTokenForAppCollector
argument_list|(
operator|(
name|AppLevelTimelineCollector
operator|)
name|collector
argument_list|)
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
name|warn
argument_list|(
literal|"Failed to cancel token for app collector with appId "
operator|+
name|appId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Launch the REST web server for this collector manager.    */
DECL|method|startWebApp ()
specifier|private
name|void
name|startWebApp
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|String
name|initializers
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"hadoop.http.filter.initializers"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|defaultInitializers
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|TimelineServerUtils
operator|.
name|addTimelineAuthFilter
argument_list|(
name|initializers
argument_list|,
name|defaultInitializers
argument_list|,
name|tokenMgrService
argument_list|)
expr_stmt|;
name|TimelineServerUtils
operator|.
name|setTimelineFilters
argument_list|(
name|conf
argument_list|,
name|initializers
argument_list|,
name|defaultInitializers
argument_list|)
expr_stmt|;
name|String
name|bindAddress
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_BIND_HOST
argument_list|)
operator|+
literal|":0"
decl_stmt|;
try|try
block|{
name|HttpServer2
operator|.
name|Builder
name|builder
init|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"timeline"
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|addEndpoint
argument_list|(
name|URI
operator|.
name|create
argument_list|(
operator|(
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|?
literal|"https://"
else|:
literal|"http://"
operator|)
operator|+
name|bindAddress
argument_list|)
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
name|builder
operator|=
name|WebAppUtils
operator|.
name|loadSslConfiguration
argument_list|(
name|builder
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|timelineRestServer
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|timelineRestServer
operator|.
name|addJerseyResourcePackage
argument_list|(
name|TimelineCollectorWebService
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|";"
operator|+
name|GenericExceptionHandler
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|";"
operator|+
name|YarnJacksonJaxbJsonProvider
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"/*"
argument_list|)
expr_stmt|;
name|timelineRestServer
operator|.
name|setAttribute
argument_list|(
name|COLLECTOR_MANAGER_ATTR_KEY
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|timelineRestServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"The per-node collector webapp failed to start."
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
comment|//TODO: We need to think of the case of multiple interfaces
name|this
operator|.
name|timelineRestServerBindAddress
operator|=
name|WebAppUtils
operator|.
name|getResolvedAddress
argument_list|(
name|timelineRestServer
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Instantiated the per-node collector webapp at "
operator|+
name|timelineRestServerBindAddress
argument_list|)
expr_stmt|;
block|}
DECL|method|reportNewCollectorInfoToNM (ApplicationId appId, org.apache.hadoop.yarn.api.records.Token token)
specifier|private
name|void
name|reportNewCollectorInfoToNM
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
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
name|Token
name|token
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|ReportNewCollectorInfoRequest
name|request
init|=
name|ReportNewCollectorInfoRequest
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|this
operator|.
name|timelineRestServerBindAddress
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Report a new collector for application: "
operator|+
name|appId
operator|+
literal|" to the NM Collector Service."
argument_list|)
expr_stmt|;
name|getNMCollectorService
argument_list|()
operator|.
name|reportNewCollectorInfo
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|updateTimelineCollectorContext ( ApplicationId appId, TimelineCollector collector)
specifier|private
name|void
name|updateTimelineCollectorContext
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|TimelineCollector
name|collector
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetTimelineCollectorContextRequest
name|request
init|=
name|GetTimelineCollectorContextRequest
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Get timeline collector context for "
operator|+
name|appId
argument_list|)
expr_stmt|;
name|GetTimelineCollectorContextResponse
name|response
init|=
name|getNMCollectorService
argument_list|()
operator|.
name|getTimelineCollectorContext
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
name|userId
init|=
name|response
operator|.
name|getUserId
argument_list|()
decl_stmt|;
if|if
condition|(
name|userId
operator|!=
literal|null
operator|&&
operator|!
name|userId
operator|.
name|isEmpty
argument_list|()
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
literal|"Setting the user in the context: "
operator|+
name|userId
argument_list|)
expr_stmt|;
block|}
name|collector
operator|.
name|getTimelineEntityContext
argument_list|()
operator|.
name|setUserId
argument_list|(
name|userId
argument_list|)
expr_stmt|;
block|}
name|String
name|flowName
init|=
name|response
operator|.
name|getFlowName
argument_list|()
decl_stmt|;
if|if
condition|(
name|flowName
operator|!=
literal|null
operator|&&
operator|!
name|flowName
operator|.
name|isEmpty
argument_list|()
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
literal|"Setting the flow name: "
operator|+
name|flowName
argument_list|)
expr_stmt|;
block|}
name|collector
operator|.
name|getTimelineEntityContext
argument_list|()
operator|.
name|setFlowName
argument_list|(
name|flowName
argument_list|)
expr_stmt|;
block|}
name|String
name|flowVersion
init|=
name|response
operator|.
name|getFlowVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|flowVersion
operator|!=
literal|null
operator|&&
operator|!
name|flowVersion
operator|.
name|isEmpty
argument_list|()
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
literal|"Setting the flow version: "
operator|+
name|flowVersion
argument_list|)
expr_stmt|;
block|}
name|collector
operator|.
name|getTimelineEntityContext
argument_list|()
operator|.
name|setFlowVersion
argument_list|(
name|flowVersion
argument_list|)
expr_stmt|;
block|}
name|long
name|flowRunId
init|=
name|response
operator|.
name|getFlowRunId
argument_list|()
decl_stmt|;
if|if
condition|(
name|flowRunId
operator|!=
literal|0L
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
literal|"Setting the flow run id: "
operator|+
name|flowRunId
argument_list|)
expr_stmt|;
block|}
name|collector
operator|.
name|getTimelineEntityContext
argument_list|()
operator|.
name|setFlowRunId
argument_list|(
name|flowRunId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNMCollectorService ()
specifier|protected
name|CollectorNodemanagerProtocol
name|getNMCollectorService
parameter_list|()
block|{
if|if
condition|(
name|nmCollectorService
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|nmCollectorService
operator|==
literal|null
condition|)
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|nmCollectorServiceAddress
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|NM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|NM_COLLECTOR_SERVICE_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_COLLECTOR_SERVICE_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_COLLECTOR_SERVICE_PORT
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"nmCollectorServiceAddress: "
operator|+
name|nmCollectorServiceAddress
argument_list|)
expr_stmt|;
specifier|final
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// TODO Security settings.
name|nmCollectorService
operator|=
operator|(
name|CollectorNodemanagerProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|CollectorNodemanagerProtocol
operator|.
name|class
argument_list|,
name|nmCollectorServiceAddress
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|nmCollectorService
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRestServerBindAddress ()
specifier|public
name|String
name|getRestServerBindAddress
parameter_list|()
block|{
return|return
name|timelineRestServerBindAddress
return|;
block|}
DECL|class|CollectorTokenRenewer
specifier|private
specifier|final
class|class
name|CollectorTokenRenewer
implements|implements
name|Runnable
block|{
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
decl_stmt|;
DECL|method|CollectorTokenRenewer (ApplicationId applicationId)
specifier|private
name|CollectorTokenRenewer
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|appId
operator|=
name|applicationId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TimelineCollector
name|collector
init|=
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|collector
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cannot find active collector while renewing token for "
operator|+
name|appId
argument_list|)
expr_stmt|;
return|return;
block|}
name|AppLevelTimelineCollector
name|appCollector
init|=
operator|(
name|AppLevelTimelineCollector
operator|)
name|collector
decl_stmt|;
synchronized|synchronized
init|(
name|collector
init|)
block|{
if|if
condition|(
operator|!
name|collector
operator|.
name|isStopped
argument_list|()
condition|)
block|{
try|try
block|{
name|long
name|newExpirationTime
init|=
name|renewTokenForAppCollector
argument_list|(
name|appCollector
argument_list|)
decl_stmt|;
if|if
condition|(
name|newExpirationTime
operator|>
literal|0
condition|)
block|{
name|long
name|renewInterval
init|=
name|newExpirationTime
operator|-
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|renewalDelay
init|=
operator|(
name|renewInterval
operator|>
name|TIME_BEFORE_RENEW_DATE
operator|)
condition|?
name|renewInterval
operator|-
name|TIME_BEFORE_RENEW_DATE
else|:
name|renewInterval
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Renewed token for "
operator|+
name|appId
operator|+
literal|" with new expiration "
operator|+
literal|"timestamp = "
operator|+
name|newExpirationTime
argument_list|)
expr_stmt|;
name|Future
argument_list|<
name|?
argument_list|>
name|renewalFuture
init|=
name|tokenRenewalExecutor
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
name|renewalDelay
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|appCollector
operator|.
name|setRenewalFutureForApp
argument_list|(
name|renewalFuture
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to renew token for "
operator|+
name|appId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

