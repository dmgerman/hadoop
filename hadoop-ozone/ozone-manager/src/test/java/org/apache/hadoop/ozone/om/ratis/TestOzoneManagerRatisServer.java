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
name|Test
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

begin_comment
comment|/**  * Test OM Ratis server.  */
end_comment

begin_class
DECL|class|TestOzoneManagerRatisServer
specifier|public
class|class
name|TestOzoneManagerRatisServer
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
name|omRatisServer
operator|=
name|OzoneManagerRatisServer
operator|.
name|newOMRatisServer
argument_list|(
literal|null
argument_list|,
name|omID
argument_list|,
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
argument_list|,
name|conf
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
comment|/**    * Submit any request to OM Ratis server and check that the dummy response    * message is received.    * TODO: Once state machine is implemented, submitting a request to Ratis    * server should result in a valid response.    */
annotation|@
name|Test
DECL|method|testSubmitRatisRequest ()
specifier|public
name|void
name|testSubmitRatisRequest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Wait for leader election
name|Thread
operator|.
name|sleep
argument_list|(
name|LEADER_ELECTION_TIMEOUT
operator|*
literal|2
argument_list|)
expr_stmt|;
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
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|CreateVolume
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
name|OMResponse
name|response
init|=
name|omRatisClient
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|CreateVolume
argument_list|,
name|response
operator|.
name|getCmdType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|response
operator|.
name|getSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|response
operator|.
name|hasCreateVolumeResponse
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
literal|"is not categorized in OmUtils#isReadyOnly"
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
block|}
end_class

end_unit

