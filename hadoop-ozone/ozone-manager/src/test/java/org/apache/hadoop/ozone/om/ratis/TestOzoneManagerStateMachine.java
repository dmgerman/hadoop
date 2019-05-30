begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.ratis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|ratis
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomUtils
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|hdds
operator|.
name|client
operator|.
name|BlockID
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
name|hdds
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
operator|.
name|PipelineID
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
name|om
operator|.
name|OMConfigKeys
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
name|om
operator|.
name|OMMetadataManager
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
name|om
operator|.
name|OMNodeDetails
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
name|om
operator|.
name|OmMetadataManagerImpl
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
name|om
operator|.
name|OzoneManager
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
name|om
operator|.
name|exceptions
operator|.
name|OMException
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
name|om
operator|.
name|helpers
operator|.
name|OMRatisHelper
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
name|om
operator|.
name|helpers
operator|.
name|OmKeyLocationInfo
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
name|OzoneManagerProtocolProtos
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
name|OzoneManagerProtocolProtos
operator|.
name|AllocateBlockRequest
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
name|OzoneManagerProtocolProtos
operator|.
name|KeyArgs
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
name|OzoneManagerProtocolProtos
operator|.
name|OMRequest
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
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
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
name|protocolPB
operator|.
name|OzoneManagerHARequestHandler
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
name|protocolPB
operator|.
name|OzoneManagerHARequestHandlerImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|proto
operator|.
name|RaftProtos
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|ClientId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftClientRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroupId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftPeerId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|statemachine
operator|.
name|TransactionContext
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
name|TemporaryFolder
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
name|InetAddress
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
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * This class tests OzoneManagerStateMachine.  */
end_comment

begin_class
DECL|class|TestOzoneManagerStateMachine
specifier|public
class|class
name|TestOzoneManagerStateMachine
block|{
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|omRatisServer
specifier|private
name|OzoneManagerRatisServer
name|omRatisServer
decl_stmt|;
DECL|field|omID
specifier|private
name|String
name|omID
decl_stmt|;
DECL|field|requestHandler
specifier|private
name|OzoneManagerHARequestHandler
name|requestHandler
decl_stmt|;
DECL|field|raftGroupId
specifier|private
name|RaftGroupId
name|raftGroupId
decl_stmt|;
DECL|field|ozoneManagerStateMachine
specifier|private
name|OzoneManagerStateMachine
name|ozoneManagerStateMachine
decl_stmt|;
DECL|field|omMetadataManager
specifier|private
name|OMMetadataManager
name|omMetadataManager
decl_stmt|;
DECL|field|ozoneManager
specifier|private
name|OzoneManager
name|ozoneManager
decl_stmt|;
annotation|@
name|Rule
DECL|field|temporaryFolder
specifier|public
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|omID
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|ratisPort
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_RATIS_PORT_KEY
argument_list|,
name|OMConfigKeys
operator|.
name|OZONE_OM_RATIS_PORT_DEFAULT
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|rpcAddress
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|OMNodeDetails
name|omNodeDetails
init|=
operator|new
name|OMNodeDetails
operator|.
name|Builder
argument_list|()
operator|.
name|setRpcAddress
argument_list|(
name|rpcAddress
argument_list|)
operator|.
name|setRatisPort
argument_list|(
name|ratisPort
argument_list|)
operator|.
name|setOMNodeId
argument_list|(
name|omID
argument_list|)
operator|.
name|setOMServiceId
argument_list|(
name|OzoneConsts
operator|.
name|OM_SERVICE_ID_DEFAULT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ozoneManager
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|OzoneManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|OzoneConfiguration
name|ozoneConfiguration
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|ozoneConfiguration
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_DB_DIRS
argument_list|,
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|=
operator|new
name|OmMetadataManagerImpl
argument_list|(
name|ozoneConfiguration
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getMetadataManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|omMetadataManager
argument_list|)
expr_stmt|;
comment|// Starts a single node Ratis server
name|omRatisServer
operator|=
name|OzoneManagerRatisServer
operator|.
name|newOMRatisServer
argument_list|(
name|conf
argument_list|,
name|ozoneManager
argument_list|,
name|omNodeDetails
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|ozoneManagerStateMachine
operator|=
operator|new
name|OzoneManagerStateMachine
argument_list|(
name|omRatisServer
argument_list|)
expr_stmt|;
name|requestHandler
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|OzoneManagerHARequestHandlerImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|raftGroupId
operator|=
name|omRatisServer
operator|.
name|getRaftGroup
argument_list|()
operator|.
name|getGroupId
argument_list|()
expr_stmt|;
name|ozoneManagerStateMachine
operator|.
name|setHandler
argument_list|(
name|requestHandler
argument_list|)
expr_stmt|;
name|ozoneManagerStateMachine
operator|.
name|setRaftGroupId
argument_list|(
name|raftGroupId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllocateBlockRequestWithSuccess ()
specifier|public
name|void
name|testAllocateBlockRequestWithSuccess
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|volumeName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|long
name|allocateBlockClientId
init|=
name|RandomUtils
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|String
name|clientId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|OMRequest
name|omRequest
init|=
name|createOmRequestForAllocateBlock
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|allocateBlockClientId
argument_list|,
name|clientId
argument_list|)
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
name|omResponse
init|=
name|createOmResponseForAllocateBlock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|requestHandler
operator|.
name|handle
argument_list|(
name|omRequest
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|omResponse
argument_list|)
expr_stmt|;
name|RaftClientRequest
name|raftClientRequest
init|=
operator|new
name|RaftClientRequest
argument_list|(
name|ClientId
operator|.
name|randomId
argument_list|()
argument_list|,
name|RaftPeerId
operator|.
name|valueOf
argument_list|(
literal|"random"
argument_list|)
argument_list|,
name|raftGroupId
argument_list|,
literal|1
argument_list|,
name|Message
operator|.
name|valueOf
argument_list|(
name|OMRatisHelper
operator|.
name|convertRequestToByteString
argument_list|(
name|omRequest
argument_list|)
argument_list|)
argument_list|,
name|RaftClientRequest
operator|.
name|Type
operator|.
name|valueOf
argument_list|(
name|RaftProtos
operator|.
name|WriteRequestTypeProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TransactionContext
name|transactionContext
init|=
name|ozoneManagerStateMachine
operator|.
name|startTransaction
argument_list|(
name|raftClientRequest
argument_list|)
decl_stmt|;
name|OMRequest
name|newOmRequest
init|=
name|OMRatisHelper
operator|.
name|convertByteStringToOMRequest
argument_list|(
name|transactionContext
operator|.
name|getStateMachineLogEntry
argument_list|()
operator|.
name|getLogData
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newOmRequest
operator|.
name|hasAllocateBlockRequest
argument_list|()
argument_list|)
expr_stmt|;
name|checkModifiedOmRequest
argument_list|(
name|omRequest
argument_list|,
name|newOmRequest
argument_list|)
expr_stmt|;
comment|// Check this keyLocation, and the keyLocation is same as from OmResponse.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newOmRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|hasKeyLocation
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|omResponse
operator|.
name|getAllocateBlockResponse
argument_list|()
operator|.
name|getKeyLocation
argument_list|()
argument_list|,
name|newOmRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getKeyLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createOmRequestForAllocateBlock (String volumeName, String bucketName, String keyName, long allocateClientId, String clientId)
specifier|private
name|OMRequest
name|createOmRequestForAllocateBlock
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|,
name|long
name|allocateClientId
parameter_list|,
name|String
name|clientId
parameter_list|)
block|{
comment|//Create AllocateBlockRequest
name|AllocateBlockRequest
operator|.
name|Builder
name|req
init|=
name|AllocateBlockRequest
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|KeyArgs
name|keyArgs
init|=
name|KeyArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|100
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|req
operator|.
name|setKeyArgs
argument_list|(
name|keyArgs
argument_list|)
expr_stmt|;
name|req
operator|.
name|setClientID
argument_list|(
name|allocateClientId
argument_list|)
expr_stmt|;
name|req
operator|.
name|setExcludeList
argument_list|(
name|HddsProtos
operator|.
name|ExcludeListProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|OMRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|AllocateBlock
argument_list|)
operator|.
name|setAllocateBlockRequest
argument_list|(
name|req
argument_list|)
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|createOmResponseForAllocateBlock (boolean status)
specifier|private
name|OMResponse
name|createOmResponseForAllocateBlock
parameter_list|(
name|boolean
name|status
parameter_list|)
block|{
name|OmKeyLocationInfo
name|newLocation
init|=
operator|new
name|OmKeyLocationInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setBlockID
argument_list|(
operator|new
name|BlockID
argument_list|(
name|RandomUtils
operator|.
name|nextLong
argument_list|()
argument_list|,
name|RandomUtils
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setLength
argument_list|(
name|RandomUtils
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|Pipeline
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
name|PipelineID
operator|.
name|randomId
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
operator|.
name|setFactor
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
operator|.
name|setState
argument_list|(
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
operator|.
name|setNodes
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|AllocateBlockResponse
operator|.
name|Builder
name|resp
init|=
name|OzoneManagerProtocolProtos
operator|.
name|AllocateBlockResponse
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|resp
operator|.
name|setKeyLocation
argument_list|(
name|newLocation
operator|.
name|getProtobuf
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|status
condition|)
block|{
return|return
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setSuccess
argument_list|(
literal|true
argument_list|)
operator|.
name|setAllocateBlockResponse
argument_list|(
name|resp
argument_list|)
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|AllocateBlock
argument_list|)
operator|.
name|setStatus
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setSuccess
argument_list|(
name|status
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setSuccess
argument_list|(
literal|true
argument_list|)
operator|.
name|setAllocateBlockResponse
argument_list|(
name|resp
argument_list|)
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|AllocateBlock
argument_list|)
operator|.
name|setStatus
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|SCM_IN_SAFE_MODE
argument_list|)
operator|.
name|setMessage
argument_list|(
literal|"Scm in Safe mode"
argument_list|)
operator|.
name|setSuccess
argument_list|(
name|status
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAllocateBlockWithFailure ()
specifier|public
name|void
name|testAllocateBlockWithFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|volumeName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|long
name|allocateBlockClientId
init|=
name|RandomUtils
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|String
name|clientId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|OMRequest
name|omRequest
init|=
name|createOmRequestForAllocateBlock
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|allocateBlockClientId
argument_list|,
name|clientId
argument_list|)
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
name|omResponse
init|=
name|createOmResponseForAllocateBlock
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|requestHandler
operator|.
name|handle
argument_list|(
name|omRequest
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|omResponse
argument_list|)
expr_stmt|;
name|RaftClientRequest
name|raftClientRequest
init|=
operator|new
name|RaftClientRequest
argument_list|(
name|ClientId
operator|.
name|randomId
argument_list|()
argument_list|,
name|RaftPeerId
operator|.
name|valueOf
argument_list|(
literal|"random"
argument_list|)
argument_list|,
name|raftGroupId
argument_list|,
literal|1
argument_list|,
name|Message
operator|.
name|valueOf
argument_list|(
name|OMRatisHelper
operator|.
name|convertRequestToByteString
argument_list|(
name|omRequest
argument_list|)
argument_list|)
argument_list|,
name|RaftClientRequest
operator|.
name|Type
operator|.
name|valueOf
argument_list|(
name|RaftProtos
operator|.
name|WriteRequestTypeProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TransactionContext
name|transactionContext
init|=
name|ozoneManagerStateMachine
operator|.
name|startTransaction
argument_list|(
name|raftClientRequest
argument_list|)
decl_stmt|;
name|OMRequest
name|newOmRequest
init|=
name|OMRatisHelper
operator|.
name|convertByteStringToOMRequest
argument_list|(
name|transactionContext
operator|.
name|getStateMachineLogEntry
argument_list|()
operator|.
name|getLogData
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newOmRequest
operator|.
name|hasAllocateBlockRequest
argument_list|()
argument_list|)
expr_stmt|;
name|checkModifiedOmRequest
argument_list|(
name|omRequest
argument_list|,
name|newOmRequest
argument_list|)
expr_stmt|;
comment|// As the request failed, check for keyLocation and  the transaction
comment|// context error message
name|Assert
operator|.
name|assertFalse
argument_list|(
name|newOmRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|hasKeyLocation
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Scm in Safe mode "
operator|+
name|OMException
operator|.
name|STATUS_CODE
operator|+
name|OMException
operator|.
name|ResultCodes
operator|.
name|SCM_IN_SAFE_MODE
argument_list|,
name|transactionContext
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|transactionContext
operator|.
name|getException
argument_list|()
operator|instanceof
name|IOException
argument_list|)
expr_stmt|;
block|}
DECL|method|checkModifiedOmRequest (OMRequest omRequest, OMRequest newOmRequest)
specifier|private
name|void
name|checkModifiedOmRequest
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|,
name|OMRequest
name|newOmRequest
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newOmRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|getBucketName
argument_list|()
operator|.
name|equals
argument_list|(
name|omRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|getBucketName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|omRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|getVolumeName
argument_list|()
operator|.
name|equals
argument_list|(
name|newOmRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|getVolumeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|omRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|getKeyName
argument_list|()
operator|.
name|equals
argument_list|(
name|newOmRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|getKeyName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|omRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|getDataSize
argument_list|()
argument_list|,
name|newOmRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|getDataSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|omRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getClientID
argument_list|()
argument_list|,
name|newOmRequest
operator|.
name|getAllocateBlockRequest
argument_list|()
operator|.
name|getClientID
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|omRequest
operator|.
name|getClientId
argument_list|()
argument_list|,
name|newOmRequest
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

