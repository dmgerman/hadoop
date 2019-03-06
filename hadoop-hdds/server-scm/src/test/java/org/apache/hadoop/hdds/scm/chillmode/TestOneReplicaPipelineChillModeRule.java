begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.chillmode
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
name|chillmode
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|PipelineReportsProto
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
name|HddsTestUtils
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
name|MockNodeManager
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
name|MockRatisPipelineProvider
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
name|events
operator|.
name|SCMEvents
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
name|PipelineProvider
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
name|SCMPipelineManager
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
name|slf4j
operator|.
name|LoggerFactory
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
name|List
import|;
end_import

begin_comment
comment|/**  * This class tests OneReplicaPipelineChillModeRule.  */
end_comment

begin_class
DECL|class|TestOneReplicaPipelineChillModeRule
specifier|public
class|class
name|TestOneReplicaPipelineChillModeRule
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
DECL|field|rule
specifier|private
name|OneReplicaPipelineChillModeRule
name|rule
decl_stmt|;
DECL|field|pipelineManager
specifier|private
name|SCMPipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|eventQueue
specifier|private
name|EventQueue
name|eventQueue
decl_stmt|;
DECL|method|setup (int nodes, int pipelineFactorThreeCount, int pipelineFactorOneCount)
specifier|private
name|void
name|setup
parameter_list|(
name|int
name|nodes
parameter_list|,
name|int
name|pipelineFactorThreeCount
parameter_list|,
name|int
name|pipelineFactorOneCount
parameter_list|)
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|ozoneConfiguration
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|ozoneConfiguration
operator|.
name|setBoolean
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ozoneConfiguration
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|containers
operator|.
name|addAll
argument_list|(
name|HddsTestUtils
operator|.
name|getContainerInfo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|MockNodeManager
name|mockNodeManager
init|=
operator|new
name|MockNodeManager
argument_list|(
literal|true
argument_list|,
name|nodes
argument_list|)
decl_stmt|;
name|eventQueue
operator|=
operator|new
name|EventQueue
argument_list|()
expr_stmt|;
name|pipelineManager
operator|=
operator|new
name|SCMPipelineManager
argument_list|(
name|ozoneConfiguration
argument_list|,
name|mockNodeManager
argument_list|,
name|eventQueue
argument_list|)
expr_stmt|;
name|PipelineProvider
name|mockRatisProvider
init|=
operator|new
name|MockRatisPipelineProvider
argument_list|(
name|mockNodeManager
argument_list|,
name|pipelineManager
operator|.
name|getStateManager
argument_list|()
argument_list|,
name|ozoneConfiguration
argument_list|)
decl_stmt|;
name|pipelineManager
operator|.
name|setPipelineProvider
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|mockRatisProvider
argument_list|)
expr_stmt|;
name|createPipelines
argument_list|(
name|pipelineFactorThreeCount
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
expr_stmt|;
name|createPipelines
argument_list|(
name|pipelineFactorOneCount
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
expr_stmt|;
name|SCMChillModeManager
name|scmChillModeManager
init|=
operator|new
name|SCMChillModeManager
argument_list|(
name|ozoneConfiguration
argument_list|,
name|containers
argument_list|,
name|pipelineManager
argument_list|,
name|eventQueue
argument_list|)
decl_stmt|;
name|rule
operator|=
name|scmChillModeManager
operator|.
name|getOneReplicaPipelineChillModeRule
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOneReplicaPipelineRule ()
specifier|public
name|void
name|testOneReplicaPipelineRule
parameter_list|()
throws|throws
name|Exception
block|{
comment|// As with 30 nodes, We can create 7 pipelines with replication factor 3.
comment|// (This is because in node manager for every 10 nodes, 7 nodes are
comment|// healthy, 2 are stale one is dead.)
name|int
name|nodes
init|=
literal|30
decl_stmt|;
name|int
name|pipelineFactorThreeCount
init|=
literal|7
decl_stmt|;
name|int
name|pipelineCountOne
init|=
literal|0
decl_stmt|;
name|setup
argument_list|(
name|nodes
argument_list|,
name|pipelineFactorThreeCount
argument_list|,
name|pipelineCountOne
argument_list|)
expr_stmt|;
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
name|SCMChillModeManager
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelines
init|=
name|pipelineManager
operator|.
name|getPipelines
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pipelineFactorThreeCount
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|firePipelineEvent
argument_list|(
name|pipelines
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// As 90% of 7 with ceil is 7, if we send 6 pipeline reports, rule
comment|// validate should be still false.
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
literal|"reported count is 6"
argument_list|)
argument_list|,
literal|1000
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|rule
operator|.
name|validate
argument_list|()
argument_list|)
expr_stmt|;
comment|//Fire last pipeline event from datanode.
name|firePipelineEvent
argument_list|(
name|pipelines
operator|.
name|get
argument_list|(
name|pipelineFactorThreeCount
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|rule
operator|.
name|validate
argument_list|()
argument_list|,
literal|1000
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOneReplicaPipelineRuleMixedPipelines ()
specifier|public
name|void
name|testOneReplicaPipelineRuleMixedPipelines
parameter_list|()
throws|throws
name|Exception
block|{
comment|// As with 30 nodes, We can create 7 pipelines with replication factor 3.
comment|// (This is because in node manager for every 10 nodes, 7 nodes are
comment|// healthy, 2 are stale one is dead.)
name|int
name|nodes
init|=
literal|30
decl_stmt|;
name|int
name|pipelineCountThree
init|=
literal|7
decl_stmt|;
name|int
name|pipelineCountOne
init|=
literal|21
decl_stmt|;
name|setup
argument_list|(
name|nodes
argument_list|,
name|pipelineCountThree
argument_list|,
name|pipelineCountOne
argument_list|)
expr_stmt|;
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
name|SCMChillModeManager
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelines
init|=
name|pipelineManager
operator|.
name|getPipelines
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pipelineCountOne
condition|;
name|i
operator|++
control|)
block|{
name|firePipelineEvent
argument_list|(
name|pipelines
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"reported count is 0"
argument_list|)
argument_list|,
literal|1000
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
comment|// fired events for one node ratis pipeline, so we will be still false.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|rule
operator|.
name|validate
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines
operator|=
name|pipelineManager
operator|.
name|getPipelines
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pipelineCountThree
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|firePipelineEvent
argument_list|(
name|pipelines
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"reported count is 6"
argument_list|)
argument_list|,
literal|1000
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
comment|//Fire last pipeline event from datanode.
name|firePipelineEvent
argument_list|(
name|pipelines
operator|.
name|get
argument_list|(
name|pipelineCountThree
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|rule
operator|.
name|validate
argument_list|()
argument_list|,
literal|1000
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
block|}
DECL|method|createPipelines (int count, HddsProtos.ReplicationFactor factor)
specifier|private
name|void
name|createPipelines
parameter_list|(
name|int
name|count
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|pipelineManager
operator|.
name|createPipeline
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|factor
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|firePipelineEvent (Pipeline pipeline)
specifier|private
name|void
name|firePipelineEvent
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
name|PipelineReportsProto
operator|.
name|Builder
name|reportBuilder
init|=
name|PipelineReportsProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|reportBuilder
operator|.
name|addPipelineReport
argument_list|(
name|PipelineReport
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPipelineID
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
operator|.
name|getProtobuf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|pipeline
operator|.
name|getFactor
argument_list|()
operator|==
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
condition|)
block|{
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|PROCESSED_PIPELINE_REPORT
argument_list|,
operator|new
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|PipelineReportFromDatanode
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|reportBuilder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|PROCESSED_PIPELINE_REPORT
argument_list|,
operator|new
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|PipelineReportFromDatanode
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|reportBuilder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|PROCESSED_PIPELINE_REPORT
argument_list|,
operator|new
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|PipelineReportFromDatanode
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|reportBuilder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|PROCESSED_PIPELINE_REPORT
argument_list|,
operator|new
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|PipelineReportFromDatanode
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|reportBuilder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

