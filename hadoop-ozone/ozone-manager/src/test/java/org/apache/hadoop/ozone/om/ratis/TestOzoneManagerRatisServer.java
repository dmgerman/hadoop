begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|OmUtils
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
name|util
operator|.
name|LifeCycle
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
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Test OM Ratis server.  */
end_comment

begin_class
DECL|class|TestOzoneManagerRatisServer
specifier|public
class|class
name|TestOzoneManagerRatisServer
block|{
annotation|@
name|Rule
DECL|field|folder
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
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
DECL|field|omRatisClient
specifier|private
name|OzoneManagerRatisClient
name|omRatisClient
decl_stmt|;
DECL|field|omID
specifier|private
name|String
name|omID
decl_stmt|;
DECL|field|clientId
specifier|private
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
DECL|field|LEADER_ELECTION_TIMEOUT
specifier|private
specifier|static
specifier|final
name|long
name|LEADER_ELECTION_TIMEOUT
init|=
literal|500L
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
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
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
specifier|final
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|omID
argument_list|)
decl_stmt|;
name|Path
name|metaDirPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|,
literal|"om-meta"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|metaDirPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
argument_list|,
name|LEADER_ELECTION_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
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
comment|// Starts a single node Ratis server
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
name|folder
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
name|omRatisServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|omRatisClient
operator|=
name|OzoneManagerRatisClient
operator|.
name|newOzoneManagerRatisClient
argument_list|(
name|omID
argument_list|,
name|omRatisServer
operator|.
name|getRaftGroup
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|omRatisClient
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|omRatisServer
operator|!=
literal|null
condition|)
block|{
name|omRatisServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|omRatisClient
operator|!=
literal|null
condition|)
block|{
name|omRatisClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Start a OM Ratis Server and checks its state.    */
annotation|@
name|Test
DECL|method|testStartOMRatisServer ()
specifier|public
name|void
name|testStartOMRatisServer
parameter_list|()
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Ratis Server should be in running state"
argument_list|,
name|LifeCycle
operator|.
name|State
operator|.
name|RUNNING
argument_list|,
name|omRatisServer
operator|.
name|getServerState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that all of {@link OzoneManagerProtocolProtos.Type} enum values are    * categorized in {@link OmUtils#isReadOnly(OMRequest)}.    */
annotation|@
name|Test
DECL|method|testIsReadOnlyCapturesAllCmdTypeEnums ()
specifier|public
name|void
name|testIsReadOnlyCapturesAllCmdTypeEnums
parameter_list|()
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|LogCapturer
name|logCapturer
init|=
name|GenericTestUtils
operator|.
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OmUtils
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|Type
index|[]
name|cmdTypes
init|=
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
name|cmdtype
range|:
name|cmdTypes
control|)
block|{
name|OMRequest
name|request
init|=
name|OMRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|cmdtype
argument_list|)
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmUtils
operator|.
name|isReadOnly
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cmdtype
operator|+
literal|" is not categorized in "
operator|+
literal|"OmUtils#isReadyOnly"
argument_list|,
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"CmdType "
operator|+
name|cmdtype
operator|+
literal|" is not "
operator|+
literal|"categorized as readOnly or not."
argument_list|)
argument_list|)
expr_stmt|;
name|logCapturer
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|verifyRaftGroupIdGenerationWithDefaultOmServiceId ()
specifier|public
name|void
name|verifyRaftGroupIdGenerationWithDefaultOmServiceId
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|nameUUIDFromBytes
argument_list|(
name|OzoneConsts
operator|.
name|OM_SERVICE_ID_DEFAULT
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|RaftGroupId
name|raftGroupId
init|=
name|omRatisServer
operator|.
name|getRaftGroup
argument_list|()
operator|.
name|getGroupId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|uuid
argument_list|,
name|raftGroupId
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|raftGroupId
operator|.
name|toByteString
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|verifyRaftGroupIdGenerationWithCustomOmServiceId ()
specifier|public
name|void
name|verifyRaftGroupIdGenerationWithCustomOmServiceId
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|customOmServiceId
init|=
literal|"omSIdCustom123"
decl_stmt|;
name|OzoneConfiguration
name|newConf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|String
name|newOmId
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
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|newOmId
argument_list|)
decl_stmt|;
name|Path
name|metaDirPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|,
literal|"om-meta"
argument_list|)
decl_stmt|;
name|newConf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|metaDirPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|newConf
operator|.
name|setTimeDuration
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
argument_list|,
name|LEADER_ELECTION_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|int
name|ratisPort
init|=
literal|9873
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
name|newOmId
argument_list|)
operator|.
name|setOMServiceId
argument_list|(
name|customOmServiceId
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Starts a single node Ratis server
name|OzoneManagerRatisServer
name|newOmRatisServer
init|=
name|OzoneManagerRatisServer
operator|.
name|newOMRatisServer
argument_list|(
name|newConf
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
decl_stmt|;
name|newOmRatisServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|OzoneManagerRatisClient
name|newOmRatisClient
init|=
name|OzoneManagerRatisClient
operator|.
name|newOzoneManagerRatisClient
argument_list|(
name|newOmId
argument_list|,
name|newOmRatisServer
operator|.
name|getRaftGroup
argument_list|()
argument_list|,
name|newConf
argument_list|)
decl_stmt|;
name|newOmRatisClient
operator|.
name|connect
argument_list|()
expr_stmt|;
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|nameUUIDFromBytes
argument_list|(
name|customOmServiceId
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|RaftGroupId
name|raftGroupId
init|=
name|newOmRatisServer
operator|.
name|getRaftGroup
argument_list|()
operator|.
name|getGroupId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|uuid
argument_list|,
name|raftGroupId
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|raftGroupId
operator|.
name|toByteString
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

