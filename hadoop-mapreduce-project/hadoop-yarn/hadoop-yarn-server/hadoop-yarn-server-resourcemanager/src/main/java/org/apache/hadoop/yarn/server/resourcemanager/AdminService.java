begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|avro
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
name|SecurityInfo
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
name|YarnRemoteException
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
name|SchedulerSecurityInfo
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
name|api
operator|.
name|RMAdminProtocol
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshAdminAclsRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshAdminAclsResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshNodesRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshNodesResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshQueuesRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshQueuesResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshSuperUserGroupsConfigurationRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshSuperUserGroupsConfigurationResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshUserToGroupsMappingsRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshUserToGroupsMappingsResponse
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
name|scheduler
operator|.
name|ResourceScheduler
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
name|RMAuditLogger
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
name|yarn
operator|.
name|service
operator|.
name|AbstractService
import|;
end_import

begin_class
DECL|class|AdminService
specifier|public
class|class
name|AdminService
extends|extends
name|AbstractService
implements|implements
name|RMAdminProtocol
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
name|AdminService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|ResourceScheduler
name|scheduler
decl_stmt|;
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|nodesListManager
specifier|private
specifier|final
name|NodesListManager
name|nodesListManager
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|masterServiceAddress
specifier|private
name|InetSocketAddress
name|masterServiceAddress
decl_stmt|;
DECL|field|adminAcl
specifier|private
name|AccessControlList
name|adminAcl
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
DECL|method|AdminService (Configuration conf, ResourceScheduler scheduler, RMContext rmContext, NodesListManager nodesListManager)
specifier|public
name|AdminService
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ResourceScheduler
name|scheduler
parameter_list|,
name|RMContext
name|rmContext
parameter_list|,
name|NodesListManager
name|nodesListManager
parameter_list|)
block|{
name|super
argument_list|(
name|AdminService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
name|this
operator|.
name|nodesListManager
operator|=
name|nodesListManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|conf
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
name|RM_ADMIN_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADMIN_ADDRESS
argument_list|)
decl_stmt|;
name|masterServiceAddress
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|bindAddress
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
name|RM_ADMIN_ACL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADMIN_ACL
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
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
name|RMAdminProtocol
operator|.
name|class
argument_list|,
name|this
argument_list|,
name|masterServiceAddress
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADMIN_CLIENT_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADMIN_CLIENT_THREAD_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
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
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|stop
argument_list|()
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
name|YarnRemoteException
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
name|RMAuditLogger
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
literal|"AdminService"
argument_list|,
literal|"Couldn't get current user"
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
name|RMAuditLogger
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
literal|"AdminService"
argument_list|,
name|AuditConstants
operator|.
name|UNAUTHORIZED_USER
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
literal|"RM Admin: "
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
DECL|method|refreshQueues (RefreshQueuesRequest request)
specifier|public
name|RefreshQueuesResponse
name|refreshQueues
parameter_list|(
name|RefreshQueuesRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|UserGroupInformation
name|user
init|=
name|checkAcls
argument_list|(
literal|"refreshQueues"
argument_list|)
decl_stmt|;
try|try
block|{
name|scheduler
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// ContainerTokenSecretManager can't
comment|// be 'refreshed'
name|RMAuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshQueues"
argument_list|,
literal|"AdminService"
argument_list|)
expr_stmt|;
return|return
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RefreshQueuesResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception refreshing queues "
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|RMAuditLogger
operator|.
name|logFailure
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshQueues"
argument_list|,
name|adminAcl
operator|.
name|toString
argument_list|()
argument_list|,
literal|"AdminService"
argument_list|,
literal|"Exception refreshing queues"
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
block|}
annotation|@
name|Override
DECL|method|refreshNodes (RefreshNodesRequest request)
specifier|public
name|RefreshNodesResponse
name|refreshNodes
parameter_list|(
name|RefreshNodesRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|UserGroupInformation
name|user
init|=
name|checkAcls
argument_list|(
literal|"refreshNodes"
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|nodesListManager
operator|.
name|refreshNodes
argument_list|()
expr_stmt|;
name|RMAuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshNodes"
argument_list|,
literal|"AdminService"
argument_list|)
expr_stmt|;
return|return
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RefreshNodesResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception refreshing nodes "
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|RMAuditLogger
operator|.
name|logFailure
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"refreshNodes"
argument_list|,
name|adminAcl
operator|.
name|toString
argument_list|()
argument_list|,
literal|"AdminService"
argument_list|,
literal|"Exception refreshing nodes"
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
block|}
annotation|@
name|Override
DECL|method|refreshSuperUserGroupsConfiguration ( RefreshSuperUserGroupsConfigurationRequest request)
specifier|public
name|RefreshSuperUserGroupsConfigurationResponse
name|refreshSuperUserGroupsConfiguration
parameter_list|(
name|RefreshSuperUserGroupsConfigurationRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
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
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|RMAuditLogger
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
literal|"AdminService"
argument_list|)
expr_stmt|;
return|return
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RefreshSuperUserGroupsConfigurationResponse
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|refreshUserToGroupsMappings ( RefreshUserToGroupsMappingsRequest request)
specifier|public
name|RefreshUserToGroupsMappingsResponse
name|refreshUserToGroupsMappings
parameter_list|(
name|RefreshUserToGroupsMappingsRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
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
name|RMAuditLogger
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
literal|"AdminService"
argument_list|)
expr_stmt|;
return|return
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RefreshUserToGroupsMappingsResponse
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|refreshAdminAcls ( RefreshAdminAclsRequest request)
specifier|public
name|RefreshAdminAclsResponse
name|refreshAdminAcls
parameter_list|(
name|RefreshAdminAclsRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
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
operator|new
name|Configuration
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
name|YarnConfiguration
operator|.
name|RM_ADMIN_ACL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADMIN_ACL
argument_list|)
argument_list|)
expr_stmt|;
name|RMAuditLogger
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
literal|"AdminService"
argument_list|)
expr_stmt|;
return|return
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RefreshAdminAclsResponse
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

