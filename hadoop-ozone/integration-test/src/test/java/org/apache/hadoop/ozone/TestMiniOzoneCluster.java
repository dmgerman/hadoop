begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DatanodeDetails
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
name|container
operator|.
name|common
operator|.
name|helpers
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
name|container
operator|.
name|common
operator|.
name|SCMTestUtils
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
name|common
operator|.
name|helpers
operator|.
name|ContainerUtils
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
name|common
operator|.
name|statemachine
operator|.
name|DatanodeStateMachine
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
name|ozoneimpl
operator|.
name|TestOzoneContainer
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
name|TestUtils
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
name|XceiverClientGrpc
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
name|test
operator|.
name|PathUtils
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
name|TestGenericTestUtils
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
name|Assert
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
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|List
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
operator|.
name|Port
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
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
name|*
import|;
end_import

begin_comment
comment|/**  * Test cases for mini ozone cluster.  */
end_comment

begin_class
DECL|class|TestMiniOzoneCluster
specifier|public
class|class
name|TestMiniOzoneCluster
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|TEST_ROOT
specifier|private
specifier|final
specifier|static
name|File
name|TEST_ROOT
init|=
name|TestGenericTestUtils
operator|.
name|getTestDir
argument_list|()
decl_stmt|;
DECL|field|WRITE_TMP
specifier|private
specifier|final
specifier|static
name|File
name|WRITE_TMP
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT
argument_list|,
literal|"write"
argument_list|)
decl_stmt|;
DECL|field|READ_TMP
specifier|private
specifier|final
specifier|static
name|File
name|READ_TMP
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT
argument_list|,
literal|"read"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_METADATA_DIRS
argument_list|,
name|TEST_ROOT
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|WRITE_TMP
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|READ_TMP
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|WRITE_TMP
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|READ_TMP
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testStartMultipleDatanodes ()
specifier|public
name|void
name|testStartMultipleDatanodes
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numberOfNodes
init|=
literal|3
decl_stmt|;
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
name|numberOfNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|HddsDatanodeService
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|numberOfNodes
argument_list|,
name|datanodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|HddsDatanodeService
name|dn
range|:
name|datanodes
control|)
block|{
comment|// Create a single member pipe line
name|DatanodeDetails
name|datanodeDetails
init|=
name|dn
operator|.
name|getDatanodeDetails
argument_list|()
decl_stmt|;
specifier|final
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
name|datanodeDetails
operator|.
name|getUuidString
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
name|PipelineID
operator|.
name|randomId
argument_list|()
argument_list|)
decl_stmt|;
name|pipeline
operator|.
name|addMember
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
comment|// Verify client is able to connect to the container
try|try
init|(
name|XceiverClientGrpc
name|client
init|=
operator|new
name|XceiverClientGrpc
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
init|)
block|{
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|isConnected
argument_list|(
name|pipeline
operator|.
name|getLeader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testDatanodeIDPersistent ()
specifier|public
name|void
name|testDatanodeIDPersistent
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Generate IDs for testing
name|DatanodeDetails
name|id1
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|id2
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|id3
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|id1
operator|.
name|setPort
argument_list|(
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|id2
operator|.
name|setPort
argument_list|(
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|id3
operator|.
name|setPort
argument_list|(
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Write a single ID to the file and read it out
name|File
name|validIdsFile
init|=
operator|new
name|File
argument_list|(
name|WRITE_TMP
argument_list|,
literal|"valid-values.id"
argument_list|)
decl_stmt|;
name|validIdsFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|ContainerUtils
operator|.
name|writeDatanodeDetailsTo
argument_list|(
name|id1
argument_list|,
name|validIdsFile
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|validId
init|=
name|ContainerUtils
operator|.
name|readDatanodeDetailsFrom
argument_list|(
name|validIdsFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|id1
argument_list|,
name|validId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|id1
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|,
name|validId
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// Read should return an empty value if file doesn't exist
name|File
name|nonExistFile
init|=
operator|new
name|File
argument_list|(
name|READ_TMP
argument_list|,
literal|"non_exist.id"
argument_list|)
decl_stmt|;
name|nonExistFile
operator|.
name|delete
argument_list|()
expr_stmt|;
try|try
block|{
name|ContainerUtils
operator|.
name|readDatanodeDetailsFrom
argument_list|(
name|nonExistFile
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|IOException
argument_list|)
expr_stmt|;
block|}
comment|// Read should fail if the file is malformed
name|File
name|malformedFile
init|=
operator|new
name|File
argument_list|(
name|READ_TMP
argument_list|,
literal|"malformed.id"
argument_list|)
decl_stmt|;
name|createMalformedIDFile
argument_list|(
name|malformedFile
argument_list|)
expr_stmt|;
try|try
block|{
name|ContainerUtils
operator|.
name|readDatanodeDetailsFrom
argument_list|(
name|malformedFile
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Read a malformed ID file should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|IOException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testContainerRandomPort ()
specifier|public
name|void
name|testContainerRandomPort
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|ozoneConf
init|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|File
name|testDir
init|=
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|TestOzoneContainer
operator|.
name|class
argument_list|)
decl_stmt|;
name|ozoneConf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|ozoneConf
operator|.
name|set
argument_list|(
name|OZONE_METADATA_DIRS
argument_list|,
name|TEST_ROOT
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Each instance of SM will create an ozone container
comment|// that bounds to a random port.
name|ozoneConf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_RANDOM_PORT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ozoneConf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|DatanodeStateMachine
name|sm1
init|=
operator|new
name|DatanodeStateMachine
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|ozoneConf
argument_list|)
init|;
name|DatanodeStateMachine
name|sm2
operator|=
operator|new
name|DatanodeStateMachine
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|ozoneConf
argument_list|)
init|;
name|DatanodeStateMachine
name|sm3
operator|=
operator|new
name|DatanodeStateMachine
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|ozoneConf
argument_list|)
init|)
block|{
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|ports
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ports
operator|.
name|add
argument_list|(
name|sm1
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerServerPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ports
operator|.
name|add
argument_list|(
name|sm2
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerServerPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ports
operator|.
name|add
argument_list|(
name|sm3
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerServerPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Assert that ratis is also on a different port.
name|assertTrue
argument_list|(
name|ports
operator|.
name|add
argument_list|(
name|sm1
operator|.
name|getContainer
argument_list|()
operator|.
name|getRatisContainerServerPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ports
operator|.
name|add
argument_list|(
name|sm2
operator|.
name|getContainer
argument_list|()
operator|.
name|getRatisContainerServerPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ports
operator|.
name|add
argument_list|(
name|sm3
operator|.
name|getContainer
argument_list|()
operator|.
name|getRatisContainerServerPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Turn off the random port flag and test again
name|ozoneConf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_RANDOM_PORT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|DatanodeStateMachine
name|sm1
init|=
operator|new
name|DatanodeStateMachine
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|ozoneConf
argument_list|)
init|;
name|DatanodeStateMachine
name|sm2
operator|=
operator|new
name|DatanodeStateMachine
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|ozoneConf
argument_list|)
init|;
name|DatanodeStateMachine
name|sm3
operator|=
operator|new
name|DatanodeStateMachine
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|ozoneConf
argument_list|)
init|)
block|{
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|ports
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ports
operator|.
name|add
argument_list|(
name|sm1
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerServerPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ports
operator|.
name|add
argument_list|(
name|sm2
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerServerPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ports
operator|.
name|add
argument_list|(
name|sm3
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerServerPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ports
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createMalformedIDFile (File malformedFile)
specifier|private
name|void
name|createMalformedIDFile
parameter_list|(
name|File
name|malformedFile
parameter_list|)
throws|throws
name|IOException
block|{
name|malformedFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|DatanodeDetails
name|id
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|ContainerUtils
operator|.
name|writeDatanodeDetailsTo
argument_list|(
name|id
argument_list|,
name|malformedFile
argument_list|)
expr_stmt|;
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|malformedFile
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"malformed"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

