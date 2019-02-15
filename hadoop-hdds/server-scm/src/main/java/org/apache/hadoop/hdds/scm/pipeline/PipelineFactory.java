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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|HashMap
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Creates pipeline based on replication type.  */
end_comment

begin_class
DECL|class|PipelineFactory
specifier|public
specifier|final
class|class
name|PipelineFactory
block|{
DECL|field|providers
specifier|private
name|Map
argument_list|<
name|ReplicationType
argument_list|,
name|PipelineProvider
argument_list|>
name|providers
decl_stmt|;
DECL|method|PipelineFactory (NodeManager nodeManager, PipelineStateManager stateManager, Configuration conf)
name|PipelineFactory
parameter_list|(
name|NodeManager
name|nodeManager
parameter_list|,
name|PipelineStateManager
name|stateManager
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|providers
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|providers
operator|.
name|put
argument_list|(
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
operator|new
name|SimplePipelineProvider
argument_list|(
name|nodeManager
argument_list|)
argument_list|)
expr_stmt|;
name|providers
operator|.
name|put
argument_list|(
name|ReplicationType
operator|.
name|RATIS
argument_list|,
operator|new
name|RatisPipelineProvider
argument_list|(
name|nodeManager
argument_list|,
name|stateManager
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|create (ReplicationType type, ReplicationFactor factor)
specifier|public
name|Pipeline
name|create
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|providers
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|create
argument_list|(
name|factor
argument_list|)
return|;
block|}
DECL|method|create (ReplicationType type, ReplicationFactor factor, List<DatanodeDetails> nodes)
specifier|public
name|Pipeline
name|create
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|nodes
parameter_list|)
block|{
return|return
name|providers
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|create
argument_list|(
name|factor
argument_list|,
name|nodes
argument_list|)
return|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|PipelineProvider
name|p
range|:
name|providers
operator|.
name|values
argument_list|()
control|)
block|{
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

