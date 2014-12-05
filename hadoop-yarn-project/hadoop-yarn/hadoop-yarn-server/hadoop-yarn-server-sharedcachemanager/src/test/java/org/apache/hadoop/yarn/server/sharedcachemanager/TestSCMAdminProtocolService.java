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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|isA
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
name|spy
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
name|times
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
name|verify
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
name|doNothing
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|RunSharedCacheCleanerTaskResponsePBImpl
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
name|SCMAdmin
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
name|server
operator|.
name|sharedcachemanager
operator|.
name|store
operator|.
name|InMemorySCMStore
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
name|sharedcachemanager
operator|.
name|store
operator|.
name|SCMStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Basic unit tests for the SCM Admin Protocol Service and SCMAdmin.  */
end_comment

begin_class
DECL|class|TestSCMAdminProtocolService
specifier|public
class|class
name|TestSCMAdminProtocolService
block|{
DECL|field|service
specifier|static
name|SCMAdminProtocolService
name|service
decl_stmt|;
DECL|field|SCMAdminProxy
specifier|static
name|SCMAdminProtocol
name|SCMAdminProxy
decl_stmt|;
DECL|field|mockAdmin
specifier|static
name|SCMAdminProtocol
name|mockAdmin
decl_stmt|;
DECL|field|adminCLI
specifier|static
name|SCMAdmin
name|adminCLI
decl_stmt|;
DECL|field|store
specifier|static
name|SCMStore
name|store
decl_stmt|;
DECL|field|cleaner
specifier|static
name|CleanerService
name|cleaner
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
annotation|@
name|Before
DECL|method|startUp ()
specifier|public
name|void
name|startUp
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|SCM_STORE_CLASS
argument_list|,
name|InMemorySCMStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|cleaner
operator|=
name|mock
argument_list|(
name|CleanerService
operator|.
name|class
argument_list|)
expr_stmt|;
name|service
operator|=
name|spy
argument_list|(
operator|new
name|SCMAdminProtocolService
argument_list|(
name|cleaner
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|scmAddress
init|=
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
decl_stmt|;
name|SCMAdminProxy
operator|=
operator|(
name|SCMAdminProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|SCMAdminProtocol
operator|.
name|class
argument_list|,
name|scmAddress
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|mockAdmin
operator|=
name|mock
argument_list|(
name|SCMAdminProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|adminCLI
operator|=
operator|new
name|SCMAdmin
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|SCMAdminProtocol
name|createSCMAdminProtocol
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|mockAdmin
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUpTest ()
specifier|public
name|void
name|cleanUpTest
parameter_list|()
block|{
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|SCMAdminProxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|SCMAdminProxy
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRunCleanerTask ()
specifier|public
name|void
name|testRunCleanerTask
parameter_list|()
throws|throws
name|Exception
block|{
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|cleaner
argument_list|)
operator|.
name|runCleanerTask
argument_list|()
expr_stmt|;
name|RunSharedCacheCleanerTaskRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RunSharedCacheCleanerTaskRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|RunSharedCacheCleanerTaskResponse
name|response
init|=
name|SCMAdminProxy
operator|.
name|runCleanerTask
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"cleaner task request isn't accepted"
argument_list|,
name|response
operator|.
name|getAccepted
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|service
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|runCleanerTask
argument_list|(
name|any
argument_list|(
name|RunSharedCacheCleanerTaskRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRunCleanerTaskCLI ()
specifier|public
name|void
name|testRunCleanerTaskCLI
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-runCleanerTask"
block|}
decl_stmt|;
name|RunSharedCacheCleanerTaskResponse
name|rp
init|=
operator|new
name|RunSharedCacheCleanerTaskResponsePBImpl
argument_list|()
decl_stmt|;
name|rp
operator|.
name|setAccepted
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockAdmin
operator|.
name|runCleanerTask
argument_list|(
name|isA
argument_list|(
name|RunSharedCacheCleanerTaskRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|adminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|rp
operator|.
name|setAccepted
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockAdmin
operator|.
name|runCleanerTask
argument_list|(
name|isA
argument_list|(
name|RunSharedCacheCleanerTaskRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|adminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockAdmin
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|runCleanerTask
argument_list|(
name|any
argument_list|(
name|RunSharedCacheCleanerTaskRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

