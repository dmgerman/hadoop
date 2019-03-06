begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|chillmode
operator|.
name|SCMChillModeManager
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
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Test cases to verify PipelineManager.  */
end_comment

begin_class
DECL|class|TestSCMPipelineManager
specifier|public
class|class
name|TestSCMPipelineManager
block|{
DECL|field|nodeManager
specifier|private
specifier|static
name|MockNodeManager
name|nodeManager
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|File
name|testDir
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
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
name|testDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestSCMPipelineManager
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|folderExisted
init|=
name|testDir
operator|.
name|exists
argument_list|()
operator|||
name|testDir
operator|.
name|mkdirs
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|folderExisted
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create test directory path"
argument_list|)
throw|;
block|}
name|nodeManager
operator|=
operator|new
name|MockNodeManager
argument_list|(
literal|true
argument_list|,
literal|20
argument_list|)
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
throws|throws
name|IOException
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPipelineReload ()
specifier|public
name|void
name|testPipelineReload
parameter_list|()
throws|throws
name|IOException
block|{
name|SCMPipelineManager
name|pipelineManager
init|=
operator|new
name|SCMPipelineManager
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
decl_stmt|;
name|PipelineProvider
name|mockRatisProvider
init|=
operator|new
name|MockRatisPipelineProvider
argument_list|(
name|nodeManager
argument_list|,
name|pipelineManager
operator|.
name|getStateManager
argument_list|()
argument_list|,
name|conf
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
name|Set
argument_list|<
name|Pipeline
argument_list|>
name|pipelines
init|=
operator|new
name|HashSet
argument_list|<>
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|Pipeline
name|pipeline
init|=
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
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
decl_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
name|pipelineManager
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// new pipeline manager should be able to load the pipelines from the db
name|pipelineManager
operator|=
operator|new
name|SCMPipelineManager
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
expr_stmt|;
name|mockRatisProvider
operator|=
operator|new
name|MockRatisPipelineProvider
argument_list|(
name|nodeManager
argument_list|,
name|pipelineManager
operator|.
name|getStateManager
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
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
for|for
control|(
name|Pipeline
name|p
range|:
name|pipelines
control|)
block|{
name|pipelineManager
operator|.
name|openPipeline
argument_list|(
name|p
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelineList
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
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipelines
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|pipelineList
argument_list|)
argument_list|)
expr_stmt|;
comment|// clean up
for|for
control|(
name|Pipeline
name|pipeline
range|:
name|pipelines
control|)
block|{
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
block|}
name|pipelineManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemovePipeline ()
specifier|public
name|void
name|testRemovePipeline
parameter_list|()
throws|throws
name|IOException
block|{
name|SCMPipelineManager
name|pipelineManager
init|=
operator|new
name|SCMPipelineManager
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
decl_stmt|;
name|PipelineProvider
name|mockRatisProvider
init|=
operator|new
name|MockRatisPipelineProvider
argument_list|(
name|nodeManager
argument_list|,
name|pipelineManager
operator|.
name|getStateManager
argument_list|()
argument_list|,
name|conf
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
name|Pipeline
name|pipeline
init|=
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
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
decl_stmt|;
name|pipelineManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|pipelineManager
operator|.
name|addContainerToPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|ContainerID
operator|.
name|valueof
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
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
name|pipelineManager
operator|.
name|removeContainerFromPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|ContainerID
operator|.
name|valueof
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
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
name|pipelineManager
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// new pipeline manager should not be able to load removed pipelines
name|pipelineManager
operator|=
operator|new
name|SCMPipelineManager
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Pipeline should not have been retrieved"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"not found"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// clean up
name|pipelineManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPipelineReport ()
specifier|public
name|void
name|testPipelineReport
parameter_list|()
throws|throws
name|IOException
block|{
name|EventQueue
name|eventQueue
init|=
operator|new
name|EventQueue
argument_list|()
decl_stmt|;
name|SCMPipelineManager
name|pipelineManager
init|=
operator|new
name|SCMPipelineManager
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
name|eventQueue
argument_list|)
decl_stmt|;
name|PipelineProvider
name|mockRatisProvider
init|=
operator|new
name|MockRatisPipelineProvider
argument_list|(
name|nodeManager
argument_list|,
name|pipelineManager
operator|.
name|getStateManager
argument_list|()
argument_list|,
name|conf
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
name|SCMChillModeManager
name|scmChillModeManager
init|=
operator|new
name|SCMChillModeManager
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
name|pipelineManager
argument_list|,
name|eventQueue
argument_list|)
decl_stmt|;
comment|// create a pipeline in allocated state with no dns yet reported
name|Pipeline
name|pipeline
init|=
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
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isHealthy
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
comment|// get pipeline report from each dn in the pipeline
name|PipelineReportHandler
name|pipelineReportHandler
init|=
operator|new
name|PipelineReportHandler
argument_list|(
name|scmChillModeManager
argument_list|,
name|pipelineManager
argument_list|,
name|conf
argument_list|)
decl_stmt|;
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
name|pipelineReportFromDatanode
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
comment|// pipeline is not healthy until all dns report
name|Assert
operator|.
name|assertFalse
argument_list|(
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isHealthy
argument_list|()
argument_list|)
expr_stmt|;
name|pipelineReportHandler
operator|.
name|onMessage
argument_list|(
name|pipelineReportFromDatanode
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// pipeline is healthy when all dns report
name|Assert
operator|.
name|assertTrue
argument_list|(
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isHealthy
argument_list|()
argument_list|)
expr_stmt|;
comment|// pipeline should now move to open state
name|Assert
operator|.
name|assertTrue
argument_list|(
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
comment|// close the pipeline
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
name|pipelineReportFromDatanode
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
comment|// pipeline report for a closed pipeline should destroy the pipeline
comment|// and remove it from the pipeline manager
name|pipelineReportHandler
operator|.
name|onMessage
argument_list|(
name|pipelineReportFromDatanode
argument_list|,
operator|new
name|EventQueue
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
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Pipeline should not have been retrieved"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"not found"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// clean up
name|pipelineManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

