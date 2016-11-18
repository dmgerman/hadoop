begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.ozoneimpl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|container
operator|.
name|ContainerTestHelper
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
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
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
name|XceiverClient
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
name|Assert
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
name|Timeout
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

begin_comment
comment|/**  * Tests ozone containers.  */
end_comment

begin_class
DECL|class|TestOzoneContainer
specifier|public
class|class
name|TestOzoneContainer
block|{
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
name|Test
DECL|method|testCreateOzoneContainer ()
specifier|public
name|void
name|testCreateOzoneContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|URL
name|p
init|=
name|conf
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|p
operator|.
name|getPath
argument_list|()
operator|.
name|concat
argument_list|(
name|TestOzoneContainer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|path
operator|+=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT_DEFAULT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|path
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
name|setHandlerType
argument_list|(
literal|"local"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// We don't start Ozone Container via data node, we will do it
comment|// independently in our test path.
name|Pipeline
name|pipeline
init|=
name|ContainerTestHelper
operator|.
name|createSingleNodePipeline
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getContainerPort
argument_list|()
argument_list|)
expr_stmt|;
name|OzoneContainer
name|container
init|=
operator|new
name|OzoneContainer
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|container
operator|.
name|start
argument_list|()
expr_stmt|;
name|XceiverClient
name|client
init|=
operator|new
name|XceiverClient
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|container
operator|.
name|stop
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOzoneContainerViaDataNode ()
specifier|public
name|void
name|testOzoneContainerViaDataNode
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|keyName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|URL
name|p
init|=
name|conf
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|p
operator|.
name|getPath
argument_list|()
operator|.
name|concat
argument_list|(
name|TestOzoneContainer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|path
operator|+=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT_DEFAULT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|// Start ozone container Via Datanode create.
name|Pipeline
name|pipeline
init|=
name|ContainerTestHelper
operator|.
name|createSingleNodePipeline
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getContainerPort
argument_list|()
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
name|setHandlerType
argument_list|(
literal|"local"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// This client talks to ozone container via datanode.
name|XceiverClient
name|client
init|=
operator|new
name|XceiverClient
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
comment|// Create container
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Write Chunk
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|writeChunkRequest
init|=
name|ContainerTestHelper
operator|.
name|getWriteChunkRequest
argument_list|(
name|pipeline
argument_list|,
name|containerName
argument_list|,
name|keyName
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|writeChunkRequest
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Read Chunk
name|request
operator|=
name|ContainerTestHelper
operator|.
name|getReadChunkRequest
argument_list|(
name|writeChunkRequest
operator|.
name|getWriteChunk
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Put Key
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|putKeyRequest
init|=
name|ContainerTestHelper
operator|.
name|getPutKeyRequest
argument_list|(
name|writeChunkRequest
operator|.
name|getWriteChunk
argument_list|()
argument_list|)
decl_stmt|;
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|putKeyRequest
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get Key
name|request
operator|=
name|ContainerTestHelper
operator|.
name|getKeyRequest
argument_list|(
name|putKeyRequest
operator|.
name|getPutKey
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|ContainerTestHelper
operator|.
name|verifyGetKey
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
comment|// Delete Key
name|request
operator|=
name|ContainerTestHelper
operator|.
name|getDeleteKeyRequest
argument_list|(
name|putKeyRequest
operator|.
name|getPutKey
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//Delete Chunk
name|request
operator|=
name|ContainerTestHelper
operator|.
name|getDeleteChunkRequest
argument_list|(
name|writeChunkRequest
operator|.
name|getWriteChunk
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBothGetandPutSmallFile ()
specifier|public
name|void
name|testBothGetandPutSmallFile
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|keyName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|URL
name|p
init|=
name|conf
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|p
operator|.
name|getPath
argument_list|()
operator|.
name|concat
argument_list|(
name|TestOzoneContainer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|path
operator|+=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT_DEFAULT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|// Start ozone container Via Datanode create.
name|Pipeline
name|pipeline
init|=
name|ContainerTestHelper
operator|.
name|createSingleNodePipeline
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getContainerPort
argument_list|()
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
name|setHandlerType
argument_list|(
literal|"local"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// This client talks to ozone container via datanode.
name|XceiverClient
name|client
init|=
operator|new
name|XceiverClient
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
comment|// Create container
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|smallFileRequest
init|=
name|ContainerTestHelper
operator|.
name|getWriteSmallFileRequest
argument_list|(
name|pipeline
argument_list|,
name|containerName
argument_list|,
name|keyName
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|smallFileRequest
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|smallFileRequest
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|getSmallFileRequest
init|=
name|ContainerTestHelper
operator|.
name|getReadSmallFileRequest
argument_list|(
name|smallFileRequest
operator|.
name|getPutSmallFile
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|getSmallFileRequest
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|smallFileRequest
operator|.
name|getPutSmallFile
argument_list|()
operator|.
name|getData
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|response
operator|.
name|getGetSmallFile
argument_list|()
operator|.
name|getData
argument_list|()
operator|.
name|getData
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

