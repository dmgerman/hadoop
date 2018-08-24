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
name|ArrayList
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
name|protocol
operator|.
name|commands
operator|.
name|ReplicateContainerCommand
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
name|OZONE_SCM_CONTAINER_SIZE
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

begin_comment
comment|/**  * Tests the behavior of the datanode, when replicate container command is  * received.  */
end_comment

begin_class
DECL|class|TestReplicateContainerHandler
specifier|public
class|class
name|TestReplicateContainerHandler
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
name|ReplicateContainerCommandHandler
operator|.
name|LOG
argument_list|)
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
name|set
argument_list|(
name|OZONE_SCM_CONTAINER_SIZE
argument_list|,
literal|"1GB"
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
comment|//send the order to replicate the container
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
name|ReplicateContainerCommand
argument_list|(
literal|1L
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO: here we test only the serialization/unserialization as
comment|// the implementation is not yet done
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"not yet handled"
argument_list|)
argument_list|,
literal|500
argument_list|,
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

