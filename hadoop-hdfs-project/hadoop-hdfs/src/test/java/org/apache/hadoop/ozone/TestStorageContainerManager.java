begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|commons
operator|.
name|lang
operator|.
name|RandomStringUtils
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
name|OzoneConfiguration
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|DeleteBlocksCommand
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|SCMCommand
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|NodeState
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|DeletedBlocksTransaction
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ReportState
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|Type
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
name|ozone
operator|.
name|scm
operator|.
name|SCMStorage
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
name|ozone
operator|.
name|scm
operator|.
name|StorageContainerManager
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
name|ozone
operator|.
name|scm
operator|.
name|StorageContainerManager
operator|.
name|StartupOption
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
name|ozone
operator|.
name|scm
operator|.
name|block
operator|.
name|DeletedBlockLog
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
name|ozone
operator|.
name|scm
operator|.
name|block
operator|.
name|SCMBlockDeletingService
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
name|ozone
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|scm
operator|.
name|XceiverClientManager
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|Lists
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
name|Maps
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
name|ozone
operator|.
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyInfo
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
name|ozone
operator|.
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyLocationInfo
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
name|scm
operator|.
name|ScmConfigKeys
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
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_comment
comment|/**  * Test class that exercises the StorageContainerManager.  */
end_comment

begin_class
DECL|class|TestStorageContainerManager
specifier|public
class|class
name|TestStorageContainerManager
block|{
DECL|field|xceiverClientManager
specifier|private
specifier|static
name|XceiverClientManager
name|xceiverClientManager
init|=
operator|new
name|XceiverClientManager
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Set the timeout for every test.    */
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testRpcPermission ()
specifier|public
name|void
name|testRpcPermission
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test with default configuration
name|OzoneConfiguration
name|defaultConf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|testRpcPermissionWithConf
argument_list|(
name|defaultConf
argument_list|,
literal|"unknownUser"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Test with ozone.administrators defined in configuration
name|OzoneConfiguration
name|ozoneConf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|ozoneConf
operator|.
name|setStrings
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_ADMINISTRATORS
argument_list|,
literal|"adminUser1, adminUser2"
argument_list|)
expr_stmt|;
comment|// Non-admin user will get permission denied.
name|testRpcPermissionWithConf
argument_list|(
name|ozoneConf
argument_list|,
literal|"unknownUser"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Admin user will pass the permission check.
name|testRpcPermissionWithConf
argument_list|(
name|ozoneConf
argument_list|,
literal|"adminUser2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testRpcPermissionWithConf ( OzoneConfiguration ozoneConf, String fakeRemoteUsername, boolean expectPermissionDenied)
specifier|private
name|void
name|testRpcPermissionWithConf
parameter_list|(
name|OzoneConfiguration
name|ozoneConf
parameter_list|,
name|String
name|fakeRemoteUsername
parameter_list|,
name|boolean
name|expectPermissionDenied
parameter_list|)
throws|throws
name|IOException
block|{
name|MiniOzoneCluster
name|cluster
init|=
operator|new
name|MiniOzoneCluster
operator|.
name|Builder
argument_list|(
name|ozoneConf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|fakeUser
init|=
name|fakeRemoteUsername
decl_stmt|;
name|StorageContainerManager
name|mockScm
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockScm
operator|.
name|getPpcRemoteUsername
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fakeUser
argument_list|)
expr_stmt|;
try|try
block|{
name|mockScm
operator|.
name|deleteContainer
argument_list|(
literal|"container1"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Operation should fail, expecting an IOException here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|expectPermissionDenied
condition|)
block|{
name|verifyPermissionDeniedException
argument_list|(
name|e
argument_list|,
name|fakeUser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If passes permission check, it should fail with
comment|// container not exist exception.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"container doesn't exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|Pipeline
name|pipeLine2
init|=
name|mockScm
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|OzoneProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
literal|"container2"
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectPermissionDenied
condition|)
block|{
name|fail
argument_list|(
literal|"Operation should fail, expecting an IOException here."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container2"
argument_list|,
name|pipeLine2
operator|.
name|getContainerName
argument_list|()
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
name|verifyPermissionDeniedException
argument_list|(
name|e
argument_list|,
name|fakeUser
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Pipeline
name|pipeLine3
init|=
name|mockScm
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|OzoneProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
literal|"container3"
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectPermissionDenied
condition|)
block|{
name|fail
argument_list|(
literal|"Operation should fail, expecting an IOException here."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container3"
argument_list|,
name|pipeLine3
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pipeLine3
operator|.
name|getMachines
argument_list|()
operator|.
name|size
argument_list|()
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
name|verifyPermissionDeniedException
argument_list|(
name|e
argument_list|,
name|fakeUser
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|mockScm
operator|.
name|getContainer
argument_list|(
literal|"container4"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Operation should fail, expecting an IOException here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|expectPermissionDenied
condition|)
block|{
name|verifyPermissionDeniedException
argument_list|(
name|e
argument_list|,
name|fakeUser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If passes permission check, it should fail with
comment|// key not exist exception.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Specified key does not exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyPermissionDeniedException (Exception e, String userName)
specifier|private
name|void
name|verifyPermissionDeniedException
parameter_list|(
name|Exception
name|e
parameter_list|,
name|String
name|userName
parameter_list|)
block|{
name|String
name|expectedErrorMessage
init|=
literal|"Access denied for user "
operator|+
name|userName
operator|+
literal|". "
operator|+
literal|"Superuser privilege is required."
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|IOException
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedErrorMessage
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockDeletionTransactions ()
specifier|public
name|void
name|testBlockDeletionTransactions
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numKeys
init|=
literal|5
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_INTERVAL_SECONDS
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_MS
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_MS
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|// Reset container provision size, otherwise only one container
comment|// is created by default.
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE
argument_list|,
name|numKeys
argument_list|)
expr_stmt|;
name|MiniOzoneCluster
name|cluster
init|=
operator|new
name|MiniOzoneCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|DeletedBlockLog
name|delLog
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmBlockManager
argument_list|()
operator|.
name|getDeletedBlockLog
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|delLog
operator|.
name|getNumOfValidTransactions
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create {numKeys} random names keys.
name|TestStorageContainerManagerHelper
name|helper
init|=
operator|new
name|TestStorageContainerManagerHelper
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|KsmKeyInfo
argument_list|>
name|keyLocations
init|=
name|helper
operator|.
name|createKeys
argument_list|(
name|numKeys
argument_list|,
literal|4096
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|containerBlocks
init|=
name|createDeleteTXLog
argument_list|(
name|delLog
argument_list|,
name|keyLocations
argument_list|,
name|helper
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|containerNames
init|=
name|containerBlocks
operator|.
name|keySet
argument_list|()
decl_stmt|;
comment|// Verify a few TX gets created in the TX log.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|delLog
operator|.
name|getNumOfValidTransactions
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Once TXs are written into the log, SCM starts to fetch TX
comment|// entries from the log and schedule block deletions in HB interval,
comment|// after sometime, all the TX should be proceed and by then
comment|// the number of containerBlocks of all known containers will be
comment|// empty again.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
return|return
name|delLog
operator|.
name|getNumOfValidTransactions
argument_list|()
operator|==
literal|0
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|helper
operator|.
name|getAllBlocks
argument_list|(
name|containerNames
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// Continue the work, add some TXs that with known container names,
comment|// but unknown block IDs.
for|for
control|(
name|String
name|containerName
range|:
name|containerBlocks
operator|.
name|keySet
argument_list|()
control|)
block|{
comment|// Add 2 TXs per container.
name|delLog
operator|.
name|addTransaction
argument_list|(
name|containerName
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|delLog
operator|.
name|addTransaction
argument_list|(
name|containerName
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Verify a few TX gets created in the TX log.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|delLog
operator|.
name|getNumOfValidTransactions
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// These blocks cannot be found in the container, skip deleting them
comment|// eventually these TX will success.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
return|return
name|delLog
operator|.
name|getFailedTransactions
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testBlockDeletingThrottling ()
specifier|public
name|void
name|testBlockDeletingThrottling
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numKeys
init|=
literal|15
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_INTERVAL_SECONDS
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_MS
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_MS
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE
argument_list|,
name|numKeys
argument_list|)
expr_stmt|;
name|MiniOzoneCluster
name|cluster
init|=
operator|new
name|MiniOzoneCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DeletedBlockLog
name|delLog
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmBlockManager
argument_list|()
operator|.
name|getDeletedBlockLog
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|delLog
operator|.
name|getNumOfValidTransactions
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|limitSize
init|=
literal|1
decl_stmt|;
comment|// Reset limit value to 1, so that we only allow one TX is dealt per
comment|// datanode.
name|SCMBlockDeletingService
name|delService
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmBlockManager
argument_list|()
operator|.
name|getSCMBlockDeletingService
argument_list|()
decl_stmt|;
name|delService
operator|.
name|setBlockDeleteTXNum
argument_list|(
name|limitSize
argument_list|)
expr_stmt|;
comment|// Create {numKeys} random names keys.
name|TestStorageContainerManagerHelper
name|helper
init|=
operator|new
name|TestStorageContainerManagerHelper
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|KsmKeyInfo
argument_list|>
name|keyLocations
init|=
name|helper
operator|.
name|createKeys
argument_list|(
name|numKeys
argument_list|,
literal|4096
argument_list|)
decl_stmt|;
name|createDeleteTXLog
argument_list|(
name|delLog
argument_list|,
name|keyLocations
argument_list|,
name|helper
argument_list|)
expr_stmt|;
comment|// Verify a few TX gets created in the TX log.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|delLog
operator|.
name|getNumOfValidTransactions
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Verify the size in delete commands is expected.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
name|NodeManager
name|nodeManager
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
decl_stmt|;
name|ReportState
name|reportState
init|=
name|ReportState
operator|.
name|newBuilder
argument_list|()
operator|.
name|setState
argument_list|(
name|ReportState
operator|.
name|states
operator|.
name|noContainerReports
argument_list|)
operator|.
name|setCount
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|commands
init|=
name|nodeManager
operator|.
name|sendHeartbeat
argument_list|(
name|nodeManager
operator|.
name|getNodes
argument_list|(
name|NodeState
operator|.
name|HEALTHY
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|null
argument_list|,
name|reportState
argument_list|)
decl_stmt|;
if|if
condition|(
name|commands
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SCMCommand
name|cmd
range|:
name|commands
control|)
block|{
if|if
condition|(
name|cmd
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|deleteBlocksCommand
condition|)
block|{
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|deletedTXs
init|=
operator|(
operator|(
name|DeleteBlocksCommand
operator|)
name|cmd
operator|)
operator|.
name|blocksTobeDeleted
argument_list|()
decl_stmt|;
return|return
name|deletedTXs
operator|!=
literal|null
operator|&&
name|deletedTXs
operator|.
name|size
argument_list|()
operator|==
name|limitSize
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
argument_list|,
literal|500
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
DECL|method|createDeleteTXLog (DeletedBlockLog delLog, Map<String, KsmKeyInfo> keyLocations, TestStorageContainerManagerHelper helper)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|createDeleteTXLog
parameter_list|(
name|DeletedBlockLog
name|delLog
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|KsmKeyInfo
argument_list|>
name|keyLocations
parameter_list|,
name|TestStorageContainerManagerHelper
name|helper
parameter_list|)
throws|throws
name|IOException
block|{
comment|// These keys will be written into a bunch of containers,
comment|// gets a set of container names, verify container containerBlocks
comment|// on datanodes.
name|Set
argument_list|<
name|String
argument_list|>
name|containerNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|KsmKeyInfo
argument_list|>
name|entry
range|:
name|keyLocations
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getKeyLocationList
argument_list|()
operator|.
name|forEach
argument_list|(
name|loc
lambda|->
name|containerNames
operator|.
name|add
argument_list|(
name|loc
operator|.
name|getContainerName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Total number of containerBlocks of these containers should be equal to
comment|// total number of containerBlocks via creation call.
name|int
name|totalCreatedBlocks
init|=
literal|0
decl_stmt|;
for|for
control|(
name|KsmKeyInfo
name|info
range|:
name|keyLocations
operator|.
name|values
argument_list|()
control|)
block|{
name|totalCreatedBlocks
operator|+=
name|info
operator|.
name|getKeyLocationList
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|totalCreatedBlocks
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|totalCreatedBlocks
argument_list|,
name|helper
operator|.
name|getAllBlocks
argument_list|(
name|containerNames
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create a deletion TX for each key.
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|containerBlocks
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|KsmKeyInfo
name|info
range|:
name|keyLocations
operator|.
name|values
argument_list|()
control|)
block|{
name|List
argument_list|<
name|KsmKeyLocationInfo
argument_list|>
name|list
init|=
name|info
operator|.
name|getKeyLocationList
argument_list|()
decl_stmt|;
name|list
operator|.
name|forEach
argument_list|(
name|location
lambda|->
block|{
if|if
condition|(
name|containerBlocks
operator|.
name|containsKey
argument_list|(
name|location
operator|.
name|getContainerName
argument_list|()
argument_list|)
condition|)
block|{
name|containerBlocks
operator|.
name|get
argument_list|(
name|location
operator|.
name|getContainerName
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|location
operator|.
name|getBlockID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|blks
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|blks
operator|.
name|add
argument_list|(
name|location
operator|.
name|getBlockID
argument_list|()
argument_list|)
expr_stmt|;
name|containerBlocks
operator|.
name|put
argument_list|(
name|location
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|blks
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|tx
range|:
name|containerBlocks
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|delLog
operator|.
name|addTransaction
argument_list|(
name|tx
operator|.
name|getKey
argument_list|()
argument_list|,
name|tx
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|containerBlocks
return|;
block|}
annotation|@
name|Test
DECL|method|testSCMInitialization ()
specifier|public
name|void
name|testSCMInitialization
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|scmPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|,
literal|"scm-meta"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|scmPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|StartupOption
operator|.
name|INIT
operator|.
name|setClusterId
argument_list|(
literal|"testClusterId"
argument_list|)
expr_stmt|;
comment|// This will initialize SCM
name|StorageContainerManager
operator|.
name|scmInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|SCMStorage
name|scmStore
init|=
operator|new
name|SCMStorage
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneConsts
operator|.
name|NodeType
operator|.
name|SCM
argument_list|,
name|scmStore
operator|.
name|getNodeType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testClusterId"
argument_list|,
name|scmStore
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
name|StartupOption
operator|.
name|INIT
operator|.
name|setClusterId
argument_list|(
literal|"testClusterIdNew"
argument_list|)
expr_stmt|;
name|StorageContainerManager
operator|.
name|scmInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneConsts
operator|.
name|NodeType
operator|.
name|SCM
argument_list|,
name|scmStore
operator|.
name|getNodeType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testClusterId"
argument_list|,
name|scmStore
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSCMReinitialization ()
specifier|public
name|void
name|testSCMReinitialization
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|scmPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|,
literal|"scm-meta"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|scmPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//This will set the cluster id in the version file
name|MiniOzoneCluster
name|cluster
init|=
operator|new
name|MiniOzoneCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|StartupOption
operator|.
name|INIT
operator|.
name|setClusterId
argument_list|(
literal|"testClusterId"
argument_list|)
expr_stmt|;
comment|// This will initialize SCM
name|StorageContainerManager
operator|.
name|scmInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|SCMStorage
name|scmStore
init|=
operator|new
name|SCMStorage
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneConsts
operator|.
name|NodeType
operator|.
name|SCM
argument_list|,
name|scmStore
operator|.
name|getNodeType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"testClusterId"
argument_list|,
name|scmStore
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSCMInitializationFailure ()
specifier|public
name|void
name|testSCMInitializationFailure
parameter_list|()
throws|throws
name|IOException
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|scmPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|,
literal|"scm-meta"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|scmPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|SCMException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"SCM not initialized."
argument_list|)
expr_stmt|;
name|StorageContainerManager
operator|.
name|createSCM
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

