begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.statemachine.commandhandler
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
name|common
operator|.
name|statemachine
operator|.
name|commandhandler
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
name|client
operator|.
name|rest
operator|.
name|OzoneException
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
name|ContainerData
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
name|KsmKeyArgs
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|CloseContainerCommand
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
name|OZONE_SCM_CONTAINER_SIZE_GB
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
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_comment
comment|/**  * Test to behaviour of the datanode when recieve close container command.  */
end_comment

begin_class
DECL|class|TestCloseContainerHandler
specifier|public
class|class
name|TestCloseContainerHandler
block|{
annotation|@
name|Test
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
throws|,
name|OzoneException
block|{
comment|//setup a cluster (1G free space is enough for a unit test)
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_SCM_CONTAINER_SIZE_GB
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|MiniOzoneCluster
name|cluster
init|=
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
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
comment|//the easiest way to create an open container is creating a key
name|OzoneClient
name|client
init|=
name|OzoneClientFactory
operator|.
name|getClient
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|ObjectStore
name|objectStore
init|=
name|client
operator|.
name|getObjectStore
argument_list|()
decl_stmt|;
name|objectStore
operator|.
name|createVolume
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|objectStore
operator|.
name|getVolume
argument_list|(
literal|"test"
argument_list|)
operator|.
name|createBucket
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|OzoneOutputStream
name|key
init|=
name|objectStore
operator|.
name|getVolume
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getBucket
argument_list|(
literal|"test"
argument_list|)
operator|.
name|createKey
argument_list|(
literal|"test"
argument_list|,
literal|1024
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
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
literal|"test"
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
comment|//get the name of a valid container
name|KsmKeyArgs
name|keyArgs
init|=
operator|new
name|KsmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
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
name|setDataSize
argument_list|(
literal|1024
argument_list|)
operator|.
name|setKeyName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|KsmKeyLocationInfo
name|ksmKeyLocationInfo
init|=
name|cluster
operator|.
name|getKeySpaceManager
argument_list|()
operator|.
name|lookupKey
argument_list|(
name|keyArgs
argument_list|)
operator|.
name|getKeyLocationVersions
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlocksLatestVersionOnly
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|containerID
init|=
name|ksmKeyLocationInfo
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|isContainerClosed
argument_list|(
name|cluster
argument_list|,
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|datanodeDetails
init|=
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
name|getDatanodeDetails
argument_list|()
decl_stmt|;
comment|//send the order to close the container
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|addDatanodeCommand
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|,
operator|new
name|CloseContainerCommand
argument_list|(
name|containerID
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|isContainerClosed
argument_list|(
name|cluster
argument_list|,
name|containerID
argument_list|)
argument_list|,
literal|500
argument_list|,
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|//double check if it's really closed (waitFor also throws an exception)
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isContainerClosed
argument_list|(
name|cluster
argument_list|,
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isContainerClosed (MiniOzoneCluster cluster, long containerID)
specifier|private
name|Boolean
name|isContainerClosed
parameter_list|(
name|MiniOzoneCluster
name|cluster
parameter_list|,
name|long
name|containerID
parameter_list|)
block|{
name|ContainerData
name|containerData
decl_stmt|;
try|try
block|{
name|containerData
operator|=
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
name|getContainerManager
argument_list|()
operator|.
name|readContainer
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
return|return
operator|!
name|containerData
operator|.
name|isOpen
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

