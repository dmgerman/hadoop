begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|Cache
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
name|io
operator|.
name|IOUtils
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
name|MiniOzoneClassicCluster
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
name|MiniOzoneCluster
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
name|OzoneConsts
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
name|scm
operator|.
name|XceiverClientSpi
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
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|storage
operator|.
name|ContainerProtocolCalls
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
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|SCM_CONTAINER_CLIENT_MAX_SIZE_KEY
import|;
end_import

begin_comment
comment|/**  * Test for XceiverClientManager caching and eviction.  */
end_comment

begin_class
DECL|class|TestXceiverClientManager
specifier|public
class|class
name|TestXceiverClientManager
block|{
DECL|field|config
specifier|private
specifier|static
name|OzoneConfiguration
name|config
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
specifier|private
specifier|static
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
DECL|field|containerOwner
specifier|private
specifier|static
name|String
name|containerOwner
init|=
literal|"OZONE"
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
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|config
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|3
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
expr_stmt|;
name|storageContainerLocationClient
operator|=
name|cluster
operator|.
name|createStorageContainerLocationClient
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|cluster
argument_list|,
name|storageContainerLocationClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCaching ()
specifier|public
name|void
name|testCaching
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
name|XceiverClientManager
name|clientManager
init|=
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|containerName1
init|=
literal|"container"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline1
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|clientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|clientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerName1
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|XceiverClientSpi
name|client1
init|=
name|clientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client1
operator|.
name|getRefcount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerName1
argument_list|,
name|client1
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|containerName2
init|=
literal|"container"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline2
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|clientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|clientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerName2
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|XceiverClientSpi
name|client2
init|=
name|clientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client2
operator|.
name|getRefcount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerName2
argument_list|,
name|client2
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|XceiverClientSpi
name|client3
init|=
name|clientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client3
operator|.
name|getRefcount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|client1
operator|.
name|getRefcount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerName1
argument_list|,
name|client3
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|client1
argument_list|,
name|client3
argument_list|)
expr_stmt|;
name|clientManager
operator|.
name|releaseClient
argument_list|(
name|client1
argument_list|)
expr_stmt|;
name|clientManager
operator|.
name|releaseClient
argument_list|(
name|client2
argument_list|)
expr_stmt|;
name|clientManager
operator|.
name|releaseClient
argument_list|(
name|client3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFreeByReference ()
specifier|public
name|void
name|testFreeByReference
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
name|conf
operator|.
name|setInt
argument_list|(
name|SCM_CONTAINER_CLIENT_MAX_SIZE_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|XceiverClientManager
name|clientManager
init|=
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Cache
argument_list|<
name|String
argument_list|,
name|XceiverClientSpi
argument_list|>
name|cache
init|=
name|clientManager
operator|.
name|getClientCache
argument_list|()
decl_stmt|;
name|String
name|containerName1
init|=
literal|"container"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline1
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|clientManager
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
name|containerName1
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|XceiverClientSpi
name|client1
init|=
name|clientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client1
operator|.
name|getRefcount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerName1
argument_list|,
name|client1
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|containerName2
init|=
literal|"container"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline2
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|clientManager
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
name|containerName2
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|XceiverClientSpi
name|client2
init|=
name|clientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client2
operator|.
name|getRefcount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerName2
argument_list|,
name|client2
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
name|client1
argument_list|,
name|client2
argument_list|)
expr_stmt|;
comment|// least recent container (i.e containerName1) is evicted
name|XceiverClientSpi
name|nonExistent1
init|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|containerName1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|nonExistent1
argument_list|)
expr_stmt|;
comment|// However container call should succeed because of refcount on the client.
name|String
name|traceID1
init|=
literal|"trace"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|client1
argument_list|,
name|traceID1
argument_list|)
expr_stmt|;
comment|// After releasing the client, this connection should be closed
comment|// and any container operations should fail
name|clientManager
operator|.
name|releaseClient
argument_list|(
name|client1
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"This channel is not connected."
argument_list|)
expr_stmt|;
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|client1
argument_list|,
name|traceID1
argument_list|)
expr_stmt|;
name|clientManager
operator|.
name|releaseClient
argument_list|(
name|client2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFreeByEviction ()
specifier|public
name|void
name|testFreeByEviction
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
name|conf
operator|.
name|setInt
argument_list|(
name|SCM_CONTAINER_CLIENT_MAX_SIZE_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|XceiverClientManager
name|clientManager
init|=
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Cache
argument_list|<
name|String
argument_list|,
name|XceiverClientSpi
argument_list|>
name|cache
init|=
name|clientManager
operator|.
name|getClientCache
argument_list|()
decl_stmt|;
name|String
name|containerName1
init|=
literal|"container"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline1
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|clientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|clientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerName1
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|XceiverClientSpi
name|client1
init|=
name|clientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client1
operator|.
name|getRefcount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerName1
argument_list|,
name|client1
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|clientManager
operator|.
name|releaseClient
argument_list|(
name|client1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|client1
operator|.
name|getRefcount
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|containerName2
init|=
literal|"container"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline2
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|clientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|clientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerName2
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|XceiverClientSpi
name|client2
init|=
name|clientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client2
operator|.
name|getRefcount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerName2
argument_list|,
name|client2
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
name|client1
argument_list|,
name|client2
argument_list|)
expr_stmt|;
comment|// now client 1 should be evicted
name|XceiverClientSpi
name|nonExistent
init|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|containerName1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|nonExistent
argument_list|)
expr_stmt|;
comment|// Any container operation should now fail
name|String
name|traceID2
init|=
literal|"trace"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"This channel is not connected."
argument_list|)
expr_stmt|;
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|client1
argument_list|,
name|traceID2
argument_list|)
expr_stmt|;
name|clientManager
operator|.
name|releaseClient
argument_list|(
name|client2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

