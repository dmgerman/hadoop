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
name|Service
operator|.
name|STATE
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
name|ApplicationClientProtocol
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
name|event
operator|.
name|Dispatcher
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
name|event
operator|.
name|DrainDispatcher
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
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|ACLsTestBase
specifier|public
specifier|abstract
class|class
name|ACLsTestBase
block|{
DECL|field|COMMON_USER
specifier|protected
specifier|static
specifier|final
name|String
name|COMMON_USER
init|=
literal|"common_user"
decl_stmt|;
DECL|field|QUEUE_A_USER
specifier|protected
specifier|static
specifier|final
name|String
name|QUEUE_A_USER
init|=
literal|"queueA_user"
decl_stmt|;
DECL|field|QUEUE_B_USER
specifier|protected
specifier|static
specifier|final
name|String
name|QUEUE_B_USER
init|=
literal|"queueB_user"
decl_stmt|;
DECL|field|QUEUE_A_GROUP
specifier|protected
specifier|static
specifier|final
name|String
name|QUEUE_A_GROUP
init|=
literal|"queueA_group"
decl_stmt|;
DECL|field|QUEUE_B_GROUP
specifier|protected
specifier|static
specifier|final
name|String
name|QUEUE_B_GROUP
init|=
literal|"queueB_group"
decl_stmt|;
DECL|field|ROOT_ADMIN
specifier|protected
specifier|static
specifier|final
name|String
name|ROOT_ADMIN
init|=
literal|"root_admin"
decl_stmt|;
DECL|field|QUEUE_A_ADMIN
specifier|protected
specifier|static
specifier|final
name|String
name|QUEUE_A_ADMIN
init|=
literal|"queueA_admin"
decl_stmt|;
DECL|field|QUEUE_B_ADMIN
specifier|protected
specifier|static
specifier|final
name|String
name|QUEUE_B_ADMIN
init|=
literal|"queueB_admin"
decl_stmt|;
DECL|field|QUEUEA
specifier|protected
specifier|static
specifier|final
name|String
name|QUEUEA
init|=
literal|"queueA"
decl_stmt|;
DECL|field|QUEUEB
specifier|protected
specifier|static
specifier|final
name|String
name|QUEUEB
init|=
literal|"queueB"
decl_stmt|;
DECL|field|QUEUEC
specifier|protected
specifier|static
specifier|final
name|String
name|QUEUEC
init|=
literal|"queueC"
decl_stmt|;
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestApplicationACLs
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|resourceManager
specifier|protected
name|MockRM
name|resourceManager
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|rpc
name|YarnRPC
name|rpc
decl_stmt|;
DECL|field|rmAddress
name|InetSocketAddress
name|rmAddress
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|conf
operator|=
name|createConfiguration
argument_list|()
expr_stmt|;
name|rpc
operator|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rmAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_PORT
argument_list|)
expr_stmt|;
name|AccessControlList
name|adminACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ADMIN_ACL
argument_list|,
name|adminACL
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|resourceManager
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
block|{
specifier|protected
name|ClientRMService
name|createClientRMService
parameter_list|()
block|{
return|return
operator|new
name|ClientRMService
argument_list|(
name|getRMContext
argument_list|()
argument_list|,
name|this
operator|.
name|scheduler
argument_list|,
name|this
operator|.
name|rmAppManager
argument_list|,
name|this
operator|.
name|applicationACLsManager
argument_list|,
name|this
operator|.
name|queueACLsManager
argument_list|,
name|getRMContext
argument_list|()
operator|.
name|getRMDelegationTokenSecretManager
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Dispatcher
name|createDispatcher
parameter_list|()
block|{
return|return
operator|new
name|DrainDispatcher
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doSecureLogin
parameter_list|()
throws|throws
name|IOException
block|{       }
block|}
expr_stmt|;
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|resourceManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|waitCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|resourceManager
operator|.
name|getServiceState
argument_list|()
operator|==
name|STATE
operator|.
name|INITED
operator|&&
name|waitCount
operator|++
operator|<
literal|60
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for RM to start..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resourceManager
operator|.
name|getServiceState
argument_list|()
operator|!=
name|STATE
operator|.
name|STARTED
condition|)
block|{
comment|// RM could have failed.
throw|throw
operator|new
name|IOException
argument_list|(
literal|"ResourceManager failed to start. Final state is "
operator|+
name|resourceManager
operator|.
name|getServiceState
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|getRMClientForUser (String user)
specifier|protected
name|ApplicationClientProtocol
name|getRMClientForUser
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|UserGroupInformation
name|userUGI
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|ApplicationClientProtocol
name|userClient
init|=
name|userUGI
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|ApplicationClientProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ApplicationClientProtocol
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|ApplicationClientProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ApplicationClientProtocol
operator|.
name|class
argument_list|,
name|rmAddress
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|userClient
return|;
block|}
DECL|method|createConfiguration ()
specifier|protected
specifier|abstract
name|Configuration
name|createConfiguration
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

