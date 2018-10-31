begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client.rpc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
operator|.
name|rpc
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
name|fs
operator|.
name|FileUtil
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
name|ReplicationFactor
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
name|ReplicationType
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
name|datanode
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
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
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerAction
operator|.
name|Action
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
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerAction
operator|.
name|Reason
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
name|StorageContainerException
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
name|client
operator|.
name|ObjectStore
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
name|client
operator|.
name|OzoneClient
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
name|client
operator|.
name|OzoneClientFactory
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
name|client
operator|.
name|io
operator|.
name|ChunkGroupOutputStream
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
name|client
operator|.
name|io
operator|.
name|OzoneOutputStream
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
name|OmKeyArgs
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
name|test
operator|.
name|GenericTestUtils
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
name|IOException
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
name|concurrent
operator|.
name|TimeUnit
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
name|HddsConfigKeys
operator|.
name|HDDS_COMMAND_STATUS_REPORT_INTERVAL
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
name|HddsConfigKeys
operator|.
name|HDDS_CONTAINER_REPORT_INTERVAL
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|HDDS_SCM_WATCHER_TIMEOUT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_STALENODE_INTERVAL
import|;
end_import

begin_comment
comment|/**  * Tests the containerStateMachine failure handling.  */
end_comment

begin_class
DECL|class|TestContainerStateMachineFailures
specifier|public
class|class
name|TestContainerStateMachineFailures
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
DECL|field|client
specifier|private
specifier|static
name|OzoneClient
name|client
decl_stmt|;
DECL|field|objectStore
specifier|private
specifier|static
name|ObjectStore
name|objectStore
decl_stmt|;
DECL|field|volumeName
specifier|private
specifier|static
name|String
name|volumeName
decl_stmt|;
DECL|field|bucketName
specifier|private
specifier|static
name|String
name|bucketName
decl_stmt|;
DECL|field|path
specifier|private
specifier|static
name|String
name|path
decl_stmt|;
DECL|field|chunkSize
specifier|private
specifier|static
name|int
name|chunkSize
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *    * @throws IOException    */
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
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
name|path
operator|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestContainerStateMachineFailures
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|baseDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|chunkSize
operator|=
operator|(
name|int
operator|)
name|OzoneConsts
operator|.
name|MB
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|HDDS_CONTAINER_REPORT_INTERVAL
argument_list|,
literal|200
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|HDDS_COMMAND_STATUS_REPORT_INTERVAL
argument_list|,
literal|200
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|HDDS_SCM_WATCHER_TIMEOUT
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|OZONE_SCM_STALENODE_INTERVAL
argument_list|,
literal|3
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setQuietMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
literal|1
argument_list|)
operator|.
name|setHbInterval
argument_list|(
literal|200
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
comment|//the easiest way to create an open container is creating a key
name|client
operator|=
name|OzoneClientFactory
operator|.
name|getClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|objectStore
operator|=
name|client
operator|.
name|getObjectStore
argument_list|()
expr_stmt|;
name|volumeName
operator|=
literal|"testcontainerstatemachinefailures"
expr_stmt|;
name|bucketName
operator|=
name|volumeName
expr_stmt|;
name|objectStore
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|objectStore
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
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
DECL|method|testContainerStateMachineFailures ()
specifier|public
name|void
name|testContainerStateMachineFailures
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneOutputStream
name|key
init|=
name|objectStore
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
operator|.
name|createKey
argument_list|(
literal|"ratis"
argument_list|,
literal|1024
argument_list|,
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
decl_stmt|;
name|key
operator|.
name|write
argument_list|(
literal|"ratis"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|//get the name of a valid container
name|OmKeyArgs
name|keyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
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
name|ONE
argument_list|)
operator|.
name|setKeyName
argument_list|(
literal|"ratis"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ChunkGroupOutputStream
name|groupOutputStream
init|=
operator|(
name|ChunkGroupOutputStream
operator|)
name|key
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locationInfoList
init|=
name|groupOutputStream
operator|.
name|getLocationInfoList
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|locationInfoList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|OmKeyLocationInfo
name|omKeyLocationInfo
init|=
name|locationInfoList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|containerID
init|=
name|omKeyLocationInfo
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
comment|// delete the container dir
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|omKeyLocationInfo
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
comment|// flush will throw an exception
name|key
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected exception not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|StorageContainerException
argument_list|)
expr_stmt|;
block|}
comment|// Make sure the container is marked unhealthy
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|omKeyLocationInfo
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|getContainerState
argument_list|()
operator|==
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|UNHEALTHY
argument_list|)
expr_stmt|;
try|try
block|{
comment|// subsequent requests will fail with unhealthy container exception
name|key
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected exception not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|StorageContainerException
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
operator|(
name|StorageContainerException
operator|)
name|ioe
operator|.
name|getCause
argument_list|()
operator|)
operator|.
name|getResult
argument_list|()
operator|==
name|ContainerProtos
operator|.
name|Result
operator|.
name|CONTAINER_UNHEALTHY
argument_list|)
expr_stmt|;
block|}
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerAction
name|action
init|=
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerAction
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
operator|.
name|setAction
argument_list|(
name|Action
operator|.
name|CLOSE
argument_list|)
operator|.
name|setReason
argument_list|(
name|Reason
operator|.
name|CONTAINER_UNHEALTHY
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Make sure the container close action is initiated to SCM.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContext
argument_list|()
operator|.
name|getAllPendingContainerActions
argument_list|()
operator|.
name|contains
argument_list|(
name|action
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

