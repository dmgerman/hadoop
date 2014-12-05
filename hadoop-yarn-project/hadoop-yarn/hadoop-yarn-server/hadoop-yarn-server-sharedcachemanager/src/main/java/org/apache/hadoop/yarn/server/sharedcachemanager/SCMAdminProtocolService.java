begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager
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
name|sharedcachemanager
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
name|ipc
operator|.
name|Server
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
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|SCMAdminProtocol
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
name|RunSharedCacheCleanerTaskRequest
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
name|RunSharedCacheCleanerTaskResponse
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|RPCUtil
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

begin_comment
comment|/**  * This service handles all SCMAdminProtocol rpc calls from administrators  * to the shared cache manager.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SCMAdminProtocolService
specifier|public
class|class
name|SCMAdminProtocolService
extends|extends
name|AbstractService
implements|implements
name|SCMAdminProtocol
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
name|SCMAdminProtocolService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|clientBindAddress
name|InetSocketAddress
name|clientBindAddress
decl_stmt|;
DECL|field|cleanerService
specifier|private
specifier|final
name|CleanerService
name|cleanerService
decl_stmt|;
DECL|field|adminAcl
specifier|private
name|AccessControlList
name|adminAcl
decl_stmt|;
DECL|method|SCMAdminProtocolService (CleanerService cleanerService)
specifier|public
name|SCMAdminProtocolService
parameter_list|(
name|CleanerService
name|cleanerService
parameter_list|)
block|{
name|super
argument_list|(
name|SCMAdminProtocolService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|cleanerService
operator|=
name|cleanerService
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
name|this
operator|.
name|clientBindAddress
operator|=
name|getBindAddress
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|adminAcl
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
DECL|method|getBindAddress (Configuration conf)
name|InetSocketAddress
name|getBindAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|SCM_ADMIN_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_ADMIN_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_ADMIN_PORT
argument_list|)
return|;
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
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
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
name|this
operator|.
name|server
operator|=
name|rpc
operator|.
name|getServer
argument_list|(
name|SCMAdminProtocol
operator|.
name|class
argument_list|,
name|this
argument_list|,
name|clientBindAddress
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
comment|// Secret manager null for now (security not supported)
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|SCM_ADMIN_CLIENT_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_ADMIN_CLIENT_THREAD_COUNT
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: Enable service authorization (see YARN-2774)
name|this
operator|.
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|clientBindAddress
operator|=
name|conf
operator|.
name|updateConnectAddr
argument_list|(
name|YarnConfiguration
operator|.
name|SCM_ADMIN_ADDRESS
argument_list|,
name|server
operator|.
name|getListenerAddress
argument_list|()
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|server
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|server
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
DECL|method|checkAcls (String method)
specifier|private
name|void
name|checkAcls
parameter_list|(
name|String
name|method
parameter_list|)
throws|throws
name|YarnException
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
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|ioe
argument_list|)
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
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
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
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"SCM Admin: "
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
block|}
annotation|@
name|Override
DECL|method|runCleanerTask ( RunSharedCacheCleanerTaskRequest request)
specifier|public
name|RunSharedCacheCleanerTaskResponse
name|runCleanerTask
parameter_list|(
name|RunSharedCacheCleanerTaskRequest
name|request
parameter_list|)
throws|throws
name|YarnException
block|{
name|checkAcls
argument_list|(
literal|"runCleanerTask"
argument_list|)
expr_stmt|;
name|RunSharedCacheCleanerTaskResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RunSharedCacheCleanerTaskResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|cleanerService
operator|.
name|runCleanerTask
argument_list|()
expr_stmt|;
comment|// if we are here, then we have submitted the request to the cleaner
comment|// service, ack the request to the admin client
name|response
operator|.
name|setAccepted
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
block|}
end_class

end_unit

