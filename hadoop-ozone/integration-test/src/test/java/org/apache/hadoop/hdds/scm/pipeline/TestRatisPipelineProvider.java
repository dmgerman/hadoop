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
name|commons
operator|.
name|collections
operator|.
name|CollectionUtils
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
name|node
operator|.
name|NodeManager
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
name|List
import|;
end_import

begin_comment
comment|/**  * Test for RatisPipelineProvider.  */
end_comment

begin_class
DECL|class|TestRatisPipelineProvider
specifier|public
class|class
name|TestRatisPipelineProvider
block|{
DECL|field|nodeManager
specifier|private
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|provider
specifier|private
name|PipelineProvider
name|provider
decl_stmt|;
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
name|nodeManager
operator|=
operator|new
name|MockNodeManager
argument_list|(
literal|true
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|stateManager
operator|=
operator|new
name|PipelineStateManager
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|=
operator|new
name|RatisPipelineProvider
argument_list|(
name|nodeManager
argument_list|,
name|stateManager
argument_list|,
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createPipelineAndAssertions ( HddsProtos.ReplicationFactor factor)
specifier|private
name|void
name|createPipelineAndAssertions
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|)
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
name|provider
operator|.
name|create
argument_list|(
name|factor
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|,
name|factor
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getPipelineState
argument_list|()
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline1
init|=
name|provider
operator|.
name|create
argument_list|(
name|factor
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline1
argument_list|)
expr_stmt|;
comment|// New pipeline should not overlap with the previous created pipeline
name|Assert
operator|.
name|assertTrue
argument_list|(
name|CollectionUtils
operator|.
name|intersection
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
argument_list|,
name|pipeline1
operator|.
name|getNodes
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline1
operator|.
name|getType
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline1
operator|.
name|getFactor
argument_list|()
argument_list|,
name|factor
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline1
operator|.
name|getPipelineState
argument_list|()
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline1
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreatePipelineWithFactor ()
specifier|public
name|void
name|testCreatePipelineWithFactor
parameter_list|()
throws|throws
name|IOException
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
name|Pipeline
name|pipeline
init|=
name|provider
operator|.
name|create
argument_list|(
name|factor
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|,
name|factor
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getPipelineState
argument_list|()
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|factor
operator|=
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
expr_stmt|;
name|Pipeline
name|pipeline1
init|=
name|provider
operator|.
name|create
argument_list|(
name|factor
argument_list|)
decl_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline1
argument_list|)
expr_stmt|;
comment|// New pipeline should overlap with the previous created pipeline,
comment|// and one datanode should overlap between the two types.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|CollectionUtils
operator|.
name|intersection
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
argument_list|,
name|pipeline1
operator|.
name|getNodes
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline1
operator|.
name|getType
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline1
operator|.
name|getFactor
argument_list|()
argument_list|,
name|factor
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline1
operator|.
name|getPipelineState
argument_list|()
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline1
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreatePipelineWithFactorThree ()
specifier|public
name|void
name|testCreatePipelineWithFactorThree
parameter_list|()
throws|throws
name|IOException
block|{
name|createPipelineAndAssertions
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreatePipelineWithFactorOne ()
specifier|public
name|void
name|testCreatePipelineWithFactorOne
parameter_list|()
throws|throws
name|IOException
block|{
name|createPipelineAndAssertions
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
expr_stmt|;
block|}
DECL|method|createListOfNodes (int nodeCount)
specifier|private
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|createListOfNodes
parameter_list|(
name|int
name|nodeCount
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
name|nodeCount
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
name|nodes
return|;
block|}
annotation|@
name|Test
DECL|method|testCreatePipelineWithNodes ()
specifier|public
name|void
name|testCreatePipelineWithNodes
parameter_list|()
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
name|Pipeline
name|pipeline
init|=
name|provider
operator|.
name|create
argument_list|(
name|factor
argument_list|,
name|createListOfNodes
argument_list|(
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|,
name|factor
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getPipelineState
argument_list|()
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|factor
operator|=
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
expr_stmt|;
name|pipeline
operator|=
name|provider
operator|.
name|create
argument_list|(
name|factor
argument_list|,
name|createListOfNodes
argument_list|(
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|,
name|factor
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getPipelineState
argument_list|()
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|factor
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

