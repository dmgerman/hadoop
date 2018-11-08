begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|container
operator|.
name|ContainerID
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
comment|/**  * Test for PipelineStateManager.  */
end_comment

begin_class
DECL|class|TestPipelineStateManager
specifier|public
class|class
name|TestPipelineStateManager
block|{
DECL|field|stateManager
specifier|private
name|PipelineStateManager
name|stateManager
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
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|stateManager
operator|=
operator|new
name|PipelineStateManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|createDummyPipeline (int numNodes)
specifier|private
name|Pipeline
name|createDummyPipeline
parameter_list|(
name|int
name|numNodes
parameter_list|)
block|{
return|return
name|createDummyPipeline
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
argument_list|,
name|numNodes
argument_list|)
return|;
block|}
DECL|method|createDummyPipeline (HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor, int numNodes)
specifier|private
name|Pipeline
name|createDummyPipeline
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|,
name|int
name|numNodes
parameter_list|)
block|{
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
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
name|numNodes
condition|;
name|i
operator|++
control|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Pipeline
operator|.
name|newBuilder
argument_list|()
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setFactor
argument_list|(
name|factor
argument_list|)
operator|.
name|setNodes
argument_list|(
name|nodes
argument_list|)
operator|.
name|setState
argument_list|(
name|Pipeline
operator|.
name|PipelineState
operator|.
name|ALLOCATED
argument_list|)
operator|.
name|setId
argument_list|(
name|PipelineID
operator|.
name|randomId
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testAddAndGetPipeline ()
specifier|public
name|void
name|testAddAndGetPipeline
parameter_list|()
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
name|createDummyPipeline
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Pipeline should not have been added"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// replication factor and number of nodes in the pipeline do not match
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
literal|"do not match"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// add a pipeline
name|pipeline
operator|=
name|createDummyPipeline
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
try|try
block|{
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Pipeline should not have been added"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Can not add a pipeline twice
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
literal|"Duplicate pipeline ID"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// verify pipeline returned is same
name|Pipeline
name|pipeline1
init|=
name|stateManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|pipeline
operator|==
name|pipeline1
argument_list|)
expr_stmt|;
comment|// clean up
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetPipelines ()
specifier|public
name|void
name|testGetPipelines
parameter_list|()
throws|throws
name|IOException
block|{
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
name|Pipeline
name|pipeline
init|=
name|createDummyPipeline
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|pipeline
operator|=
name|createDummyPipeline
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Pipeline
argument_list|>
name|pipelines1
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|stateManager
operator|.
name|getPipelines
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipelines1
operator|.
name|size
argument_list|()
argument_list|,
name|pipelines
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// clean up
for|for
control|(
name|Pipeline
name|pipeline1
range|:
name|pipelines
control|)
block|{
name|removePipeline
argument_list|(
name|pipeline1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetPipelinesByTypeAndFactor ()
specifier|public
name|void
name|testGetPipelinesByTypeAndFactor
parameter_list|()
throws|throws
name|IOException
block|{
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
name|HddsProtos
operator|.
name|ReplicationType
name|type
range|:
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
range|:
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|values
argument_list|()
control|)
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
comment|// 5 pipelines in allocated state for each type and factor
name|Pipeline
name|pipeline
init|=
name|createDummyPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
comment|// 5 pipelines in open state for each type and factor
name|pipeline
operator|=
name|createDummyPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
comment|// 5 pipelines in closed state for each type and factor
name|pipeline
operator|=
name|createDummyPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|HddsProtos
operator|.
name|ReplicationType
name|type
range|:
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
range|:
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|values
argument_list|()
control|)
block|{
comment|// verify pipelines received
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelines1
init|=
name|stateManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|pipelines1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines1
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|p
lambda|->
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|type
argument_list|,
name|p
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|factor
argument_list|,
name|p
operator|.
name|getFactor
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|//clean up
for|for
control|(
name|Pipeline
name|pipeline
range|:
name|pipelines
control|)
block|{
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetPipelinesByTypeAndState ()
specifier|public
name|void
name|testGetPipelinesByTypeAndState
parameter_list|()
throws|throws
name|IOException
block|{
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
name|HddsProtos
operator|.
name|ReplicationType
name|type
range|:
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|values
argument_list|()
control|)
block|{
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
init|=
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
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
comment|// 5 pipelines in allocated state for each type and factor
name|Pipeline
name|pipeline
init|=
name|createDummyPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
comment|// 5 pipelines in open state for each type and factor
name|pipeline
operator|=
name|createDummyPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
comment|// 5 pipelines in closed state for each type and factor
name|pipeline
operator|=
name|createDummyPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|HddsProtos
operator|.
name|ReplicationType
name|type
range|:
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|values
argument_list|()
control|)
block|{
comment|// verify pipelines received
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelines1
init|=
name|stateManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|pipelines1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines1
operator|.
name|forEach
argument_list|(
name|p
lambda|->
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|type
argument_list|,
name|p
operator|.
name|getType
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
name|OPEN
argument_list|,
name|p
operator|.
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|pipelines1
operator|=
name|stateManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|CLOSED
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|pipelines1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//clean up
for|for
control|(
name|Pipeline
name|pipeline
range|:
name|pipelines
control|)
block|{
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetPipelinesByTypeFactorAndState ()
specifier|public
name|void
name|testGetPipelinesByTypeFactorAndState
parameter_list|()
throws|throws
name|IOException
block|{
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
name|HddsProtos
operator|.
name|ReplicationType
name|type
range|:
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
range|:
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|values
argument_list|()
control|)
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
comment|// 5 pipelines in allocated state for each type and factor
name|Pipeline
name|pipeline
init|=
name|createDummyPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
comment|// 5 pipelines in open state for each type and factor
name|pipeline
operator|=
name|createDummyPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
comment|// 5 pipelines in closed state for each type and factor
name|pipeline
operator|=
name|createDummyPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|HddsProtos
operator|.
name|ReplicationType
name|type
range|:
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
range|:
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Pipeline
operator|.
name|PipelineState
name|state
range|:
name|Pipeline
operator|.
name|PipelineState
operator|.
name|values
argument_list|()
control|)
block|{
comment|// verify pipelines received
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelines1
init|=
name|stateManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|pipelines1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|pipelines1
operator|.
name|forEach
argument_list|(
name|p
lambda|->
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|type
argument_list|,
name|p
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|factor
argument_list|,
name|p
operator|.
name|getFactor
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|state
argument_list|,
name|p
operator|.
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//clean up
for|for
control|(
name|Pipeline
name|pipeline
range|:
name|pipelines
control|)
block|{
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAddAndGetContainer ()
specifier|public
name|void
name|testAddAndGetContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|containerID
init|=
literal|0
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|createDummyPipeline
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|pipeline
operator|=
name|stateManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
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
operator|++
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
comment|// move pipeline to open state
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
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
operator|++
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|stateManager
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
operator|++
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
comment|//verify the number of containers returned
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|containerIDs
init|=
name|stateManager
operator|.
name|getContainers
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerIDs
operator|.
name|size
argument_list|()
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
try|try
block|{
name|stateManager
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
operator|++
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Container should not have been added"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Can not add a container to removed pipeline
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
name|Pipeline
name|pipeline
init|=
name|createDummyPipeline
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
comment|// close the pipeline
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
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
try|try
block|{
name|stateManager
operator|.
name|removePipeline
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
literal|"Pipeline should not have been removed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// can not remove a pipeline which already has containers
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
literal|"not yet closed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// close the pipeline
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|stateManager
operator|.
name|removePipeline
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
literal|"Pipeline should not have been removed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// can not remove a pipeline which already has containers
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
literal|"not empty"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// remove containers and then remove the pipeline
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveContainer ()
specifier|public
name|void
name|testRemoveContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|containerID
init|=
literal|1
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|createDummyPipeline
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// create an open pipeline in stateMap
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
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
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stateManager
operator|.
name|getContainers
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
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
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stateManager
operator|.
name|getContainers
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// add two containers in the pipeline
name|stateManager
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
operator|++
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|stateManager
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
operator|++
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stateManager
operator|.
name|getContainers
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// move pipeline to closing state
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
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
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|stateManager
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
operator|--
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stateManager
operator|.
name|getContainers
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// clean up
name|stateManager
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
annotation|@
name|Test
DECL|method|testFinalizePipeline ()
specifier|public
name|void
name|testFinalizePipeline
parameter_list|()
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
name|createDummyPipeline
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
comment|// finalize on ALLOCATED pipeline
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
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
name|stateManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
comment|// clean up
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|pipeline
operator|=
name|createDummyPipeline
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// finalize on OPEN pipeline
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
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
name|stateManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
comment|// clean up
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|pipeline
operator|=
name|createDummyPipeline
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// finalize should work on already closed pipeline
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
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
name|stateManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
comment|// clean up
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenPipeline ()
specifier|public
name|void
name|testOpenPipeline
parameter_list|()
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
name|createDummyPipeline
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
comment|// open on ALLOCATED pipeline
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
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
name|OPEN
argument_list|,
name|stateManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// open should work on already open pipeline
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|,
name|stateManager
operator|.
name|getPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
comment|// clean up
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
DECL|method|removePipeline (Pipeline pipeline)
specifier|private
name|void
name|removePipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|containerIDs
init|=
name|stateManager
operator|.
name|getContainers
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ContainerID
name|containerID
range|:
name|containerIDs
control|)
block|{
name|stateManager
operator|.
name|removeContainerFromPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
name|stateManager
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
block|}
end_class

end_unit

