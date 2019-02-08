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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|PipelineReport
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
name|ScmConfigKeys
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
name|container
operator|.
name|ContainerManager
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
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|PipelineReportFromDatanode
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
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|PipelineActionsFromDatanode
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
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|EventQueue
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
name|container
operator|.
name|ozoneimpl
operator|.
name|OzoneContainer
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
name|TimeUnit
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

begin_comment
comment|/**  * Tests for Pipeline Closing.  */
end_comment

begin_class
DECL|class|TestPipelineClose
specifier|public
class|class
name|TestPipelineClose
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
DECL|field|scm
specifier|private
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|ratisContainer
specifier|private
name|ContainerWithPipeline
name|ratisContainer
decl_stmt|;
DECL|field|containerManager
specifier|private
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|field|pipelineManager
specifier|private
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|pipelineDestroyTimeoutInMillis
specifier|private
name|long
name|pipelineDestroyTimeoutInMillis
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *    * @throws IOException    */
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_HEARTBEAT_INTERVAL
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|pipelineDestroyTimeoutInMillis
operator|=
literal|5000
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_PIPELINE_DESTROY_TIMEOUT
argument_list|,
name|pipelineDestroyTimeoutInMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
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
name|containerManager
operator|=
name|scm
operator|.
name|getContainerManager
argument_list|()
expr_stmt|;
name|pipelineManager
operator|=
name|scm
operator|.
name|getPipelineManager
argument_list|()
expr_stmt|;
name|ContainerInfo
name|containerInfo
init|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|RATIS
argument_list|,
name|THREE
argument_list|,
literal|"testOwner"
argument_list|)
decl_stmt|;
name|ratisContainer
operator|=
operator|new
name|ContainerWithPipeline
argument_list|(
name|containerInfo
argument_list|,
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|containerInfo
operator|.
name|getPipelineID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|pipelineManager
operator|=
name|scm
operator|.
name|getPipelineManager
argument_list|()
expr_stmt|;
comment|// At this stage, there should be 2 pipeline one with 1 open container each.
comment|// Try closing the both the pipelines, one with a closed container and
comment|// the other with an open container.
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
name|pipelineManager
operator|.
name|getContainersInPipeline
argument_list|(
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerID
name|cId
init|=
name|ratisContainer
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
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
name|cId
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now close the container and it should not show up while fetching
comment|// containers by pipeline
name|containerManager
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
name|containerManager
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
name|pipelineManager
operator|.
name|getContainersInPipeline
argument_list|(
name|ratisContainer
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
name|pipelineManager
operator|.
name|finalizePipeline
argument_list|(
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline1
init|=
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|ratisContainer
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
name|Pipeline
operator|.
name|PipelineState
operator|.
name|CLOSED
argument_list|,
name|pipeline1
operator|.
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
name|pipelineManager
operator|.
name|removePipeline
argument_list|(
name|pipeline1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|DatanodeDetails
name|dn
range|:
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getNodes
argument_list|()
control|)
block|{
comment|// Assert that the pipeline has been removed from Node2PipelineMap as well
name|Assert
operator|.
name|assertFalse
argument_list|(
name|scm
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|getPipelines
argument_list|(
name|dn
argument_list|)
operator|.
name|contains
argument_list|(
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
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
name|pipelineManager
operator|.
name|getContainersInPipeline
argument_list|(
name|ratisContainer
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
name|ContainerID
name|cId2
init|=
name|ratisContainer
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
decl_stmt|;
name|pipelineManager
operator|.
name|finalizePipeline
argument_list|(
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Pipeline
operator|.
name|PipelineState
operator|.
name|CLOSED
argument_list|,
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline2
init|=
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|ratisContainer
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
name|Pipeline
operator|.
name|PipelineState
operator|.
name|CLOSED
argument_list|,
name|pipeline2
operator|.
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPipelineCloseWithPipelineAction ()
specifier|public
name|void
name|testPipelineCloseWithPipelineAction
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|dns
init|=
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|PipelineActionsFromDatanode
name|pipelineActionsFromDatanode
init|=
name|TestUtils
operator|.
name|getPipelineActionFromDatanode
argument_list|(
name|dns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
comment|// send closing action for pipeline
name|PipelineActionHandler
name|pipelineActionHandler
init|=
operator|new
name|PipelineActionHandler
argument_list|(
name|pipelineManager
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|pipelineActionHandler
operator|.
name|onMessage
argument_list|(
name|pipelineActionsFromDatanode
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
call|(
name|int
call|)
argument_list|(
name|pipelineDestroyTimeoutInMillis
operator|*
literal|1.2
argument_list|)
argument_list|)
expr_stmt|;
name|OzoneContainer
name|ozoneContainer
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
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PipelineReport
argument_list|>
name|pipelineReports
init|=
name|ozoneContainer
operator|.
name|getPipelineReport
argument_list|()
operator|.
name|getPipelineReportList
argument_list|()
decl_stmt|;
for|for
control|(
name|PipelineReport
name|pipelineReport
range|:
name|pipelineReports
control|)
block|{
comment|// ensure the pipeline is not reported by any dn
name|Assert
operator|.
name|assertNotEquals
argument_list|(
name|PipelineID
operator|.
name|getFromProtobuf
argument_list|(
name|pipelineReport
operator|.
name|getPipelineID
argument_list|()
argument_list|)
argument_list|,
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Pipeline should not exist in SCM"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PipelineNotFoundException
name|e
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
DECL|method|testPipelineCloseWithPipelineReport ()
specifier|public
name|void
name|testPipelineCloseWithPipelineReport
parameter_list|()
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|pipelineManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove pipeline from SCM
name|pipelineManager
operator|.
name|removePipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|DatanodeDetails
name|dn
range|:
name|pipeline
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|PipelineReportFromDatanode
name|pipelineReport
init|=
name|TestUtils
operator|.
name|getPipelineReportFromDatanode
argument_list|(
name|dn
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|PipelineReportHandler
name|pipelineReportHandler
init|=
operator|new
name|PipelineReportHandler
argument_list|(
name|pipelineManager
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// on receiving pipeline report for the pipeline, pipeline report handler
comment|// should destroy the pipeline for the dn
name|pipelineReportHandler
operator|.
name|onMessage
argument_list|(
name|pipelineReport
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|OzoneContainer
name|ozoneContainer
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
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PipelineReport
argument_list|>
name|pipelineReports
init|=
name|ozoneContainer
operator|.
name|getPipelineReport
argument_list|()
operator|.
name|getPipelineReportList
argument_list|()
decl_stmt|;
for|for
control|(
name|PipelineReport
name|pipelineReport
range|:
name|pipelineReports
control|)
block|{
comment|// pipeline should not be reported by any dn
name|Assert
operator|.
name|assertNotEquals
argument_list|(
name|PipelineID
operator|.
name|getFromProtobuf
argument_list|(
name|pipelineReport
operator|.
name|getPipelineID
argument_list|()
argument_list|)
argument_list|,
name|ratisContainer
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

