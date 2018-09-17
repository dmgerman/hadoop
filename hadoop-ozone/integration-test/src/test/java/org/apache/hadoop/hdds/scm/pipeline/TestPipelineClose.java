begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipeline
package|package
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
name|ContainerMapping
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
name|ContainerWithPipeline
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|states
operator|.
name|ContainerStateMap
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
name|pipelines
operator|.
name|PipelineSelector
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
name|server
operator|.
name|StorageContainerManager
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
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
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
import|;
end_import

begin_class
DECL|class|TestPipelineClose
specifier|public
class|class
name|TestPipelineClose
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
DECL|field|scm
specifier|private
specifier|static
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|ratisContainer1
specifier|private
specifier|static
name|ContainerWithPipeline
name|ratisContainer1
decl_stmt|;
DECL|field|ratisContainer2
specifier|private
specifier|static
name|ContainerWithPipeline
name|ratisContainer2
decl_stmt|;
DECL|field|stateMap
specifier|private
specifier|static
name|ContainerStateMap
name|stateMap
decl_stmt|;
DECL|field|mapping
specifier|private
specifier|static
name|ContainerMapping
name|mapping
decl_stmt|;
DECL|field|pipelineSelector
specifier|private
specifier|static
name|PipelineSelector
name|pipelineSelector
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
literal|6
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
name|scm
operator|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
expr_stmt|;
name|mapping
operator|=
operator|(
name|ContainerMapping
operator|)
name|scm
operator|.
name|getScmContainerManager
argument_list|()
expr_stmt|;
name|stateMap
operator|=
name|mapping
operator|.
name|getStateManager
argument_list|()
operator|.
name|getContainerStateMap
argument_list|()
expr_stmt|;
name|ratisContainer1
operator|=
name|mapping
operator|.
name|allocateContainer
argument_list|(
name|RATIS
argument_list|,
name|THREE
argument_list|,
literal|"testOwner"
argument_list|)
expr_stmt|;
name|ratisContainer2
operator|=
name|mapping
operator|.
name|allocateContainer
argument_list|(
name|RATIS
argument_list|,
name|THREE
argument_list|,
literal|"testOwner"
argument_list|)
expr_stmt|;
name|pipelineSelector
operator|=
name|mapping
operator|.
name|getPipelineSelector
argument_list|()
expr_stmt|;
comment|// At this stage, there should be 2 pipeline one with 1 open container each.
comment|// Try closing the both the pipelines, one with a closed container and
comment|// the other with an open container.
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
DECL|method|testPipelineCloseWithClosedContainer ()
specifier|public
name|void
name|testPipelineCloseWithClosedContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|set
init|=
name|pipelineSelector
operator|.
name|getOpenContainerIDsByPipeline
argument_list|(
name|ratisContainer1
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|cId
init|=
name|ratisContainer1
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|forEach
argument_list|(
name|containerID
lambda|->
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerID
argument_list|,
name|ContainerID
operator|.
name|valueof
argument_list|(
name|cId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now close the container and it should not show up while fetching
comment|// containers by pipeline
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|cId
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|cId
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATED
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|cId
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|FINALIZE
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|cId
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|setClosed
init|=
name|pipelineSelector
operator|.
name|getOpenContainerIDsByPipeline
argument_list|(
name|ratisContainer1
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|setClosed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|pipelineSelector
operator|.
name|finalizePipeline
argument_list|(
name|ratisContainer1
operator|.
name|getPipeline
argument_list|()
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline1
init|=
name|pipelineSelector
operator|.
name|getPipeline
argument_list|(
name|ratisContainer1
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|pipeline1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ratisContainer1
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLifeCycleState
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
for|for
control|(
name|DatanodeDetails
name|dn
range|:
name|ratisContainer1
operator|.
name|getPipeline
argument_list|()
operator|.
name|getMachines
argument_list|()
control|)
block|{
comment|// Assert that the pipeline has been removed from Node2PipelineMap as well
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipelineSelector
operator|.
name|getPipelineId
argument_list|(
name|dn
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPipelineCloseWithOpenContainer ()
specifier|public
name|void
name|testPipelineCloseWithOpenContainer
parameter_list|()
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|setOpen
init|=
name|pipelineSelector
operator|.
name|getOpenContainerIDsByPipeline
argument_list|(
name|ratisContainer2
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|setOpen
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|cId2
init|=
name|ratisContainer2
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|cId2
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|cId2
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATED
argument_list|)
expr_stmt|;
name|pipelineSelector
operator|.
name|finalizePipeline
argument_list|(
name|ratisContainer2
operator|.
name|getPipeline
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ratisContainer2
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLifeCycleState
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSING
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline2
init|=
name|pipelineSelector
operator|.
name|getPipeline
argument_list|(
name|ratisContainer2
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline2
operator|.
name|getLifeCycleState
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSING
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

