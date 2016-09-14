begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|server
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|ipc
operator|.
name|ProtobufRpcEngine
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
name|ipc
operator|.
name|RPC
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
name|ipc
operator|.
name|WritableRpcEngine
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
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|AccessControlException
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
name|Groups
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
name|security
operator|.
name|authorize
operator|.
name|ProxyUsers
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
name|logaggregation
operator|.
name|AggregatedLogDeletionService
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
name|proto
operator|.
name|RefreshUserMappingsProtocolProtos
operator|.
name|RefreshUserMappingsProtocolService
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
name|protocolPB
operator|.
name|RefreshUserMappingsProtocolPB
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
name|protocolPB
operator|.
name|RefreshUserMappingsProtocolServerSideTranslatorPB
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
name|tools
operator|.
name|proto
operator|.
name|GetUserMappingsProtocolProtos
operator|.
name|GetUserMappingsProtocolService
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
name|tools
operator|.
name|protocolPB
operator|.
name|GetUserMappingsProtocolPB
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
name|tools
operator|.
name|protocolPB
operator|.
name|GetUserMappingsProtocolServerSideTranslatorPB
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|HSAdminProtocol
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|HSAdminRefreshProtocolPB
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|security
operator|.
name|authorize
operator|.
name|ClientHSPolicyProvider
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|HSAuditLogger
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|HSAuditLogger
operator|.
name|AuditConstants
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|JobHistory
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|proto
operator|.
name|HSAdminRefreshProtocolProtos
operator|.
name|HSAdminRefreshProtocolService
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|protocolPB
operator|.
name|HSAdminRefreshProtocolServerSideTranslatorPB
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
name|protobuf
operator|.
name|BlockingService
import|;
end_import

begin_class
annotation|@
name|Private
DECL|class|HSAdminServer
specifier|public
class|class
name|HSAdminServer
extends|extends
name|AbstractService
implements|implements
name|HSAdminProtocol
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
name|HSAdminServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|adminAcl
specifier|private
name|AccessControlList
name|adminAcl
decl_stmt|;
DECL|field|aggLogDelService
specifier|private
name|AggregatedLogDeletionService
name|aggLogDelService
init|=
literal|null
decl_stmt|;
comment|/** The RPC server that listens to requests from clients */
DECL|field|clientRpcServer
specifier|protected
name|RPC
operator|.
name|Server
name|clientRpcServer
decl_stmt|;
DECL|field|clientRpcAddress
specifier|protected
name|InetSocketAddress
name|clientRpcAddress
decl_stmt|;
DECL|field|HISTORY_ADMIN_SERVER
specifier|private
specifier|static
specifier|final
name|String
name|HISTORY_ADMIN_SERVER
init|=
literal|"HSAdminServer"
decl_stmt|;
DECL|field|jobHistoryService
specifier|private
name|JobHistory
name|jobHistoryService
init|=
literal|null
decl_stmt|;
DECL|field|loginUGI
specifier|private
name|UserGroupInformation
name|loginUGI
decl_stmt|;
DECL|method|HSAdminServer (AggregatedLogDeletionService aggLogDelService, JobHistory jobHistoryService)
specifier|public
name|HSAdminServer
parameter_list|(
name|AggregatedLogDeletionService
name|aggLogDelService
parameter_list|,
name|JobHistory
name|jobHistoryService
parameter_list|)
block|{
name|super
argument_list|(
name|HSAdminServer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|aggLogDelService
operator|=
name|aggLogDelService
expr_stmt|;
name|this
operator|.
name|jobHistoryService
operator|=
name|jobHistoryService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|RefreshUserMappingsProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|RefreshUserMappingsProtocolServerSideTranslatorPB
name|refreshUserMappingXlator
init|=
operator|new
name|RefreshUserMappingsProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|BlockingService
name|refreshUserMappingService
init|=
name|RefreshUserMappingsProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
name|refreshUserMappingXlator
argument_list|)
decl_stmt|;
name|GetUserMappingsProtocolServerSideTranslatorPB
name|getUserMappingXlator
init|=
operator|new
name|GetUserMappingsProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|BlockingService
name|getUserMappingService
init|=
name|GetUserMappingsProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
name|getUserMappingXlator
argument_list|)
decl_stmt|;
name|HSAdminRefreshProtocolServerSideTranslatorPB
name|refreshHSAdminProtocolXlator
init|=
operator|new
name|HSAdminRefreshProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|BlockingService
name|refreshHSAdminProtocolService
init|=
name|HSAdminRefreshProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
name|refreshHSAdminProtocolXlator
argument_list|)
decl_stmt|;
name|WritableRpcEngine
operator|.
name|ensureInitialized
argument_list|()
expr_stmt|;
name|clientRpcAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_BIND_HOST
argument_list|,
name|JHAdminConfig
operator|.
name|JHS_ADMIN_ADDRESS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_JHS_ADMIN_ADDRESS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_JHS_ADMIN_PORT
argument_list|)
expr_stmt|;
name|clientRpcServer
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|RefreshUserMappingsProtocolPB
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|refreshUserMappingService
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|clientRpcAddress
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|setPort
argument_list|(
name|clientRpcAddress
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|addProtocol
argument_list|(
name|conf
argument_list|,
name|GetUserMappingsProtocolPB
operator|.
name|class
argument_list|,
name|getUserMappingService
argument_list|)
expr_stmt|;
name|addProtocol
argument_list|(
name|conf
argument_list|,
name|HSAdminRefreshProtocolPB
operator|.
name|class
argument_list|,
name|refreshHSAdminProtocolService
argument_list|)
expr_stmt|;
comment|// Enable service authorization?
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHORIZATION
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|clientRpcServer
operator|.
name|refreshServiceAcl
argument_list|(
name|conf
argument_list|,
operator|new
name|ClientHSPolicyProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|adminAcl
operator|=
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|JHAdminConfig
operator|.
name|JHS_ADMIN_ACL
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_JHS_ADMIN_ACL
argument_list|)
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
name|loginUGI
operator|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|loginUGI
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
block|}
name|clientRpcServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getLoginUGI ()
name|UserGroupInformation
name|getLoginUGI
parameter_list|()
block|{
return|return
name|loginUGI
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setLoginUGI (UserGroupInformation ugi)
name|void
name|setLoginUGI
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
name|loginUGI
operator|=
name|ugi
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
name|clientRpcServer
operator|!=
literal|null
condition|)
block|{
name|clientRpcServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addProtocol (Configuration conf, Class<?> protocol, BlockingService blockingService)
specifier|private
name|void
name|addProtocol
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|BlockingService
name|blockingService
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|protocol
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|clientRpcServer
operator|.
name|addProtocol
argument_list|(
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|protocol
argument_list|,
name|blockingService
argument_list|)
expr_stmt|;
block|}
DECL|method|checkAcls (String method)
specifier|private
name|UserGroupInformation
name|checkAcls
parameter_list|(
name|String
name|method
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|user
decl_stmt|;
try|try
block|{
name|user
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
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't get current user"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|HSAuditLogger
operator|.
name|logFailure
argument_list|(
literal|"UNKNOWN"
argument_list|,
name|method
argument_list|,
name|adminAcl
operator|.
name|toString
argument_list|()
argument_list|,
name|HISTORY_ADMIN_SERVER
argument_list|,
literal|"Couldn't get current user"
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
if|if
condition|(
operator|!
name|adminAcl
operator|.
name|isUserAllowed
argument_list|(
name|user
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"User "
operator|+
name|user
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|" doesn't have permission"
operator|+
literal|" to call '"
operator|+
name|method
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|HSAuditLogger
operator|.
name|logFailure
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|method
argument_list|,
name|adminAcl
operator|.
name|toString
argument_list|()
argument_list|,
name|HISTORY_ADMIN_SERVER
argument_list|,
name|AuditConstants
operator|.
name|UNAUTHORIZED_USER
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"User "
operator|+
name|user
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|" doesn't have permission"
operator|+
literal|" to call '"
operator|+
name|method
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"HS Admin: "
operator|+
name|method
operator|+
literal|" invoked by user "
operator|+
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|user
return|;
block|}
annotation|@
name|Override
DECL|method|getGroupsForUser (String user)
specifier|public
name|String
index|[]
name|getGroupsForUser
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
operator|.
name|getGroupNames
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|refreshUserToGroupsMappings ()
specifier|public
name|void
name|refreshUserToGroupsMappings
parameter_list|()
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|user
init|=
name|checkAcls
argument_list|(
literal|"refreshUserToGroupsMappings"
argument_list|)
decl_stmt|;
name|Groups
operator|.
name|getUserToGroupsMappingService
argument_list|()
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|HSAuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshUserToGroupsMappings"
argument_list|,
name|HISTORY_ADMIN_SERVER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|refreshSuperUserGroupsConfiguration ()
specifier|public
name|void
name|refreshSuperUserGroupsConfiguration
parameter_list|()
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|user
init|=
name|checkAcls
argument_list|(
literal|"refreshSuperUserGroupsConfiguration"
argument_list|)
decl_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|createConf
argument_list|()
argument_list|)
expr_stmt|;
name|HSAuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshSuperUserGroupsConfiguration"
argument_list|,
name|HISTORY_ADMIN_SERVER
argument_list|)
expr_stmt|;
block|}
DECL|method|createConf ()
specifier|protected
name|Configuration
name|createConf
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|refreshAdminAcls ()
specifier|public
name|void
name|refreshAdminAcls
parameter_list|()
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|user
init|=
name|checkAcls
argument_list|(
literal|"refreshAdminAcls"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
name|createConf
argument_list|()
decl_stmt|;
name|adminAcl
operator|=
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|JHAdminConfig
operator|.
name|JHS_ADMIN_ACL
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_JHS_ADMIN_ACL
argument_list|)
argument_list|)
expr_stmt|;
name|HSAuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshAdminAcls"
argument_list|,
name|HISTORY_ADMIN_SERVER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|refreshLoadedJobCache ()
specifier|public
name|void
name|refreshLoadedJobCache
parameter_list|()
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|user
init|=
name|checkAcls
argument_list|(
literal|"refreshLoadedJobCache"
argument_list|)
decl_stmt|;
try|try
block|{
name|jobHistoryService
operator|.
name|refreshLoadedJobCache
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|HSAuditLogger
operator|.
name|logFailure
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshLoadedJobCache"
argument_list|,
name|adminAcl
operator|.
name|toString
argument_list|()
argument_list|,
name|HISTORY_ADMIN_SERVER
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|HSAuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshLoadedJobCache"
argument_list|,
name|HISTORY_ADMIN_SERVER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|refreshLogRetentionSettings ()
specifier|public
name|void
name|refreshLogRetentionSettings
parameter_list|()
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|user
init|=
name|checkAcls
argument_list|(
literal|"refreshLogRetentionSettings"
argument_list|)
decl_stmt|;
try|try
block|{
name|loginUGI
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|aggLogDelService
operator|.
name|refreshLogRetentionSettings
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|HSAuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshLogRetentionSettings"
argument_list|,
literal|"HSAdminServer"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|refreshJobRetentionSettings ()
specifier|public
name|void
name|refreshJobRetentionSettings
parameter_list|()
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|user
init|=
name|checkAcls
argument_list|(
literal|"refreshJobRetentionSettings"
argument_list|)
decl_stmt|;
try|try
block|{
name|loginUGI
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|jobHistoryService
operator|.
name|refreshJobRetentionSettings
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|HSAuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshJobRetentionSettings"
argument_list|,
name|HISTORY_ADMIN_SERVER
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

