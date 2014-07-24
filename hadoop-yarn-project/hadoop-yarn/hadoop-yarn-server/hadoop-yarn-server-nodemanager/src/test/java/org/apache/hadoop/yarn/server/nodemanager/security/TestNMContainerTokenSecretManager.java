begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.security
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
name|nodemanager
operator|.
name|security
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
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|token
operator|.
name|SecretManager
operator|.
name|InvalidToken
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
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|Priority
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
name|security
operator|.
name|ContainerTokenIdentifier
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
name|records
operator|.
name|MasterKey
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
name|nodemanager
operator|.
name|recovery
operator|.
name|NMMemoryStateStoreService
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
name|security
operator|.
name|BaseContainerTokenSecretManager
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
name|utils
operator|.
name|BuilderUtils
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

begin_class
DECL|class|TestNMContainerTokenSecretManager
specifier|public
class|class
name|TestNMContainerTokenSecretManager
block|{
annotation|@
name|Test
DECL|method|testRecovery ()
specifier|public
name|void
name|testRecovery
parameter_list|()
throws|throws
name|IOException
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RECOVERY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|NodeId
name|nodeId
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"somehost"
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
specifier|final
name|ContainerId
name|cid1
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|ContainerId
name|cid2
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ContainerTokenKeyGeneratorForTest
name|keygen
init|=
operator|new
name|ContainerTokenKeyGeneratorForTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|NMMemoryStateStoreService
name|stateStore
init|=
operator|new
name|NMMemoryStateStoreService
argument_list|()
decl_stmt|;
name|stateStore
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|stateStore
operator|.
name|start
argument_list|()
expr_stmt|;
name|NMContainerTokenSecretManager
name|secretMgr
init|=
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|,
name|stateStore
argument_list|)
decl_stmt|;
name|secretMgr
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|MasterKey
name|currentKey
init|=
name|keygen
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|secretMgr
operator|.
name|setMasterKey
argument_list|(
name|currentKey
argument_list|)
expr_stmt|;
name|ContainerTokenIdentifier
name|tokenId1
init|=
name|createContainerTokenId
argument_list|(
name|cid1
argument_list|,
name|nodeId
argument_list|,
literal|"user1"
argument_list|,
name|secretMgr
argument_list|)
decl_stmt|;
name|ContainerTokenIdentifier
name|tokenId2
init|=
name|createContainerTokenId
argument_list|(
name|cid2
argument_list|,
name|nodeId
argument_list|,
literal|"user2"
argument_list|,
name|secretMgr
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|secretMgr
operator|.
name|retrievePassword
argument_list|(
name|tokenId1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|secretMgr
operator|.
name|retrievePassword
argument_list|(
name|tokenId2
argument_list|)
argument_list|)
expr_stmt|;
comment|// restart and verify tokens still valid
name|secretMgr
operator|=
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|,
name|stateStore
argument_list|)
expr_stmt|;
name|secretMgr
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|secretMgr
operator|.
name|recover
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|currentKey
argument_list|,
name|secretMgr
operator|.
name|getCurrentKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secretMgr
operator|.
name|isValidStartContainerRequest
argument_list|(
name|tokenId1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secretMgr
operator|.
name|isValidStartContainerRequest
argument_list|(
name|tokenId2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|secretMgr
operator|.
name|retrievePassword
argument_list|(
name|tokenId1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|secretMgr
operator|.
name|retrievePassword
argument_list|(
name|tokenId2
argument_list|)
argument_list|)
expr_stmt|;
comment|// roll master key and start a container
name|secretMgr
operator|.
name|startContainerSuccessful
argument_list|(
name|tokenId2
argument_list|)
expr_stmt|;
name|currentKey
operator|=
name|keygen
operator|.
name|generateKey
argument_list|()
expr_stmt|;
name|secretMgr
operator|.
name|setMasterKey
argument_list|(
name|currentKey
argument_list|)
expr_stmt|;
comment|// restart and verify tokens still valid due to prev key persist
name|secretMgr
operator|=
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|,
name|stateStore
argument_list|)
expr_stmt|;
name|secretMgr
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|secretMgr
operator|.
name|recover
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|currentKey
argument_list|,
name|secretMgr
operator|.
name|getCurrentKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secretMgr
operator|.
name|isValidStartContainerRequest
argument_list|(
name|tokenId1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|secretMgr
operator|.
name|isValidStartContainerRequest
argument_list|(
name|tokenId2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|secretMgr
operator|.
name|retrievePassword
argument_list|(
name|tokenId1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|secretMgr
operator|.
name|retrievePassword
argument_list|(
name|tokenId2
argument_list|)
argument_list|)
expr_stmt|;
comment|// roll master key again, restart, and verify keys no longer valid
name|currentKey
operator|=
name|keygen
operator|.
name|generateKey
argument_list|()
expr_stmt|;
name|secretMgr
operator|.
name|setMasterKey
argument_list|(
name|currentKey
argument_list|)
expr_stmt|;
name|secretMgr
operator|=
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|,
name|stateStore
argument_list|)
expr_stmt|;
name|secretMgr
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|secretMgr
operator|.
name|recover
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|currentKey
argument_list|,
name|secretMgr
operator|.
name|getCurrentKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secretMgr
operator|.
name|isValidStartContainerRequest
argument_list|(
name|tokenId1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|secretMgr
operator|.
name|isValidStartContainerRequest
argument_list|(
name|tokenId2
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|secretMgr
operator|.
name|retrievePassword
argument_list|(
name|tokenId1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"token should not be valid"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidToken
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|secretMgr
operator|.
name|retrievePassword
argument_list|(
name|tokenId2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"token should not be valid"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidToken
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|stateStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createContainerTokenId ( ContainerId cid, NodeId nodeId, String user, NMContainerTokenSecretManager secretMgr)
specifier|private
specifier|static
name|ContainerTokenIdentifier
name|createContainerTokenId
parameter_list|(
name|ContainerId
name|cid
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|String
name|user
parameter_list|,
name|NMContainerTokenSecretManager
name|secretMgr
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|rmid
init|=
name|cid
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getClusterTimestamp
argument_list|()
decl_stmt|;
name|ContainerTokenIdentifier
name|ctid
init|=
operator|new
name|ContainerTokenIdentifier
argument_list|(
name|cid
argument_list|,
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|,
name|user
argument_list|,
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|100000L
argument_list|,
name|secretMgr
operator|.
name|getCurrentKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|rmid
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Token
name|token
init|=
name|BuilderUtils
operator|.
name|newContainerToken
argument_list|(
name|nodeId
argument_list|,
name|secretMgr
operator|.
name|createPassword
argument_list|(
name|ctid
argument_list|)
argument_list|,
name|ctid
argument_list|)
decl_stmt|;
return|return
name|BuilderUtils
operator|.
name|newContainerTokenIdentifier
argument_list|(
name|token
argument_list|)
return|;
block|}
DECL|class|ContainerTokenKeyGeneratorForTest
specifier|private
specifier|static
class|class
name|ContainerTokenKeyGeneratorForTest
extends|extends
name|BaseContainerTokenSecretManager
block|{
DECL|method|ContainerTokenKeyGeneratorForTest (Configuration conf)
specifier|public
name|ContainerTokenKeyGeneratorForTest
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|generateKey ()
specifier|public
name|MasterKey
name|generateKey
parameter_list|()
block|{
return|return
name|createNewMasterKey
argument_list|()
operator|.
name|getMasterKey
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

