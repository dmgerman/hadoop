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
name|container
operator|.
name|ContainerID
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
name|ContainerInfo
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
name|BlockOutputStreamEntry
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
name|KeyOutputStream
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
name|OmKeyInfo
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
name|Test
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
comment|/**  * Tests MultiBlock Writes with Dn failures by Ozone Client.  */
end_comment

begin_class
DECL|class|TestMultiBlockWritesWithDnFailures
specifier|public
class|class
name|TestMultiBlockWritesWithDnFailures
block|{
DECL|field|cluster
specifier|private
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|client
specifier|private
name|OzoneClient
name|client
decl_stmt|;
DECL|field|objectStore
specifier|private
name|ObjectStore
name|objectStore
decl_stmt|;
DECL|field|chunkSize
specifier|private
name|int
name|chunkSize
decl_stmt|;
DECL|field|blockSize
specifier|private
name|int
name|blockSize
decl_stmt|;
DECL|field|volumeName
specifier|private
name|String
name|volumeName
decl_stmt|;
DECL|field|bucketName
specifier|private
name|String
name|bucketName
decl_stmt|;
DECL|field|keyString
specifier|private
name|String
name|keyString
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true    *    * @throws IOException    */
DECL|method|startCluster (int datanodes)
specifier|private
name|void
name|startCluster
parameter_list|(
name|int
name|datanodes
parameter_list|)
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
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
name|blockSize
operator|=
literal|4
operator|*
name|chunkSize
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_WATCH_REQUEST_TIMEOUT
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
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
literal|100
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_KEY
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
argument_list|,
literal|1
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
name|datanodes
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
name|keyString
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|volumeName
operator|=
literal|"datanodefailurehandlingtest"
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
name|After
DECL|method|shutdown ()
specifier|public
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
DECL|method|testMultiBlockWritesWithDnFailures ()
specifier|public
name|void
name|testMultiBlockWritesWithDnFailures
parameter_list|()
throws|throws
name|Exception
block|{
name|startCluster
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|String
name|keyName
init|=
literal|"ratis3"
decl_stmt|;
name|OzoneOutputStream
name|key
init|=
name|createKey
argument_list|(
name|keyName
argument_list|,
name|ReplicationType
operator|.
name|RATIS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|data
init|=
name|ContainerTestHelper
operator|.
name|getFixedLengthString
argument_list|(
name|keyString
argument_list|,
name|blockSize
operator|+
name|chunkSize
argument_list|)
decl_stmt|;
name|key
operator|.
name|write
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// get the name of a valid container
name|Assert
operator|.
name|assertTrue
argument_list|(
name|key
operator|.
name|getOutputStream
argument_list|()
operator|instanceof
name|KeyOutputStream
argument_list|)
expr_stmt|;
name|KeyOutputStream
name|groupOutputStream
init|=
operator|(
name|KeyOutputStream
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
name|assertTrue
argument_list|(
name|locationInfoList
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|long
name|containerId
init|=
name|locationInfoList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|ContainerInfo
name|container
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getContainerManager
argument_list|()
operator|.
name|getContainer
argument_list|(
name|ContainerID
operator|.
name|valueof
argument_list|(
name|containerId
argument_list|)
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getPipelineManager
argument_list|()
operator|.
name|getPipeline
argument_list|(
name|container
operator|.
name|getPipelineID
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
init|=
name|pipeline
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|shutdownHddsDatanode
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdownHddsDatanode
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// The write will fail but exception will be handled and length will be
comment|// updated correctly in OzoneManager once the steam is closed
name|key
operator|.
name|write
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|key
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|THREE
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setRefreshPipeline
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmKeyInfo
name|keyInfo
init|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
operator|.
name|lookupKey
argument_list|(
name|keyArgs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
operator|*
name|data
operator|.
name|getBytes
argument_list|()
operator|.
name|length
argument_list|,
name|keyInfo
operator|.
name|getDataSize
argument_list|()
argument_list|)
expr_stmt|;
name|validateData
argument_list|(
name|keyName
argument_list|,
name|data
operator|.
name|concat
argument_list|(
name|data
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiBlockWritesWithIntermittentDnFailures ()
specifier|public
name|void
name|testMultiBlockWritesWithIntermittentDnFailures
parameter_list|()
throws|throws
name|Exception
block|{
name|startCluster
argument_list|(
literal|10
argument_list|)
expr_stmt|;
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
name|OzoneOutputStream
name|key
init|=
name|createKey
argument_list|(
name|keyName
argument_list|,
name|ReplicationType
operator|.
name|RATIS
argument_list|,
literal|6
operator|*
name|blockSize
argument_list|)
decl_stmt|;
name|String
name|data
init|=
name|ContainerTestHelper
operator|.
name|getFixedLengthString
argument_list|(
name|keyString
argument_list|,
name|blockSize
operator|+
name|chunkSize
argument_list|)
decl_stmt|;
name|key
operator|.
name|write
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// get the name of a valid container
name|Assert
operator|.
name|assertTrue
argument_list|(
name|key
operator|.
name|getOutputStream
argument_list|()
operator|instanceof
name|KeyOutputStream
argument_list|)
expr_stmt|;
name|KeyOutputStream
name|keyOutputStream
init|=
operator|(
name|KeyOutputStream
operator|)
name|key
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BlockOutputStreamEntry
argument_list|>
name|streamEntryList
init|=
name|keyOutputStream
operator|.
name|getStreamEntries
argument_list|()
decl_stmt|;
comment|// Assert that 6 block will be preallocated
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|streamEntryList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|key
operator|.
name|write
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|key
operator|.
name|flush
argument_list|()
expr_stmt|;
name|long
name|containerId
init|=
name|streamEntryList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlockID
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|BlockID
name|blockId
init|=
name|streamEntryList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlockID
argument_list|()
decl_stmt|;
name|ContainerInfo
name|container
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getContainerManager
argument_list|()
operator|.
name|getContainer
argument_list|(
name|ContainerID
operator|.
name|valueof
argument_list|(
name|containerId
argument_list|)
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getPipelineManager
argument_list|()
operator|.
name|getPipeline
argument_list|(
name|container
operator|.
name|getPipelineID
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
init|=
name|pipeline
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|shutdownHddsDatanode
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// The write will fail but exception will be handled and length will be
comment|// updated correctly in OzoneManager once the steam is closed
name|key
operator|.
name|write
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// shutdown the second datanode
name|cluster
operator|.
name|shutdownHddsDatanode
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|key
operator|.
name|write
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|key
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|THREE
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setRefreshPipeline
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmKeyInfo
name|keyInfo
init|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
operator|.
name|lookupKey
argument_list|(
name|keyArgs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
operator|*
name|data
operator|.
name|getBytes
argument_list|()
operator|.
name|length
argument_list|,
name|keyInfo
operator|.
name|getDataSize
argument_list|()
argument_list|)
expr_stmt|;
name|validateData
argument_list|(
name|keyName
argument_list|,
name|data
operator|.
name|concat
argument_list|(
name|data
argument_list|)
operator|.
name|concat
argument_list|(
name|data
argument_list|)
operator|.
name|concat
argument_list|(
name|data
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createKey (String keyName, ReplicationType type, long size)
specifier|private
name|OzoneOutputStream
name|createKey
parameter_list|(
name|String
name|keyName
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|ContainerTestHelper
operator|.
name|createKey
argument_list|(
name|keyName
argument_list|,
name|type
argument_list|,
name|size
argument_list|,
name|objectStore
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
return|;
block|}
DECL|method|validateData (String keyName, byte[] data)
specifier|private
name|void
name|validateData
parameter_list|(
name|String
name|keyName
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|ContainerTestHelper
operator|.
name|validateData
argument_list|(
name|keyName
argument_list|,
name|data
argument_list|,
name|objectStore
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

