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
name|container
operator|.
name|ContainerID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_comment
comment|/**  * Interface which exposes the api for pipeline management.  */
end_comment

begin_interface
DECL|interface|PipelineManager
specifier|public
interface|interface
name|PipelineManager
extends|extends
name|Closeable
block|{
DECL|method|createPipeline (ReplicationType type, ReplicationFactor factor)
name|Pipeline
name|createPipeline
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|createPipeline (ReplicationType type, ReplicationFactor factor, List<DatanodeDetails> nodes)
name|Pipeline
name|createPipeline
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
function_decl|;
DECL|method|getPipeline (PipelineID pipelineID)
name|Pipeline
name|getPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|PipelineNotFoundException
function_decl|;
DECL|method|getPipelines ()
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|()
function_decl|;
DECL|method|getPipelines (ReplicationType type)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|)
function_decl|;
DECL|method|getPipelines (ReplicationType type, ReplicationFactor factor)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
function_decl|;
DECL|method|getPipelines (ReplicationType type, ReplicationFactor factor, Pipeline.PipelineState state)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|Pipeline
operator|.
name|PipelineState
name|state
parameter_list|)
function_decl|;
DECL|method|addContainerToPipeline (PipelineID pipelineID, ContainerID containerID)
name|void
name|addContainerToPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|,
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|removeContainerFromPipeline (PipelineID pipelineID, ContainerID containerID)
name|void
name|removeContainerFromPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|,
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getContainersInPipeline (PipelineID pipelineID)
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|getContainersInPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getNumberOfContainers (PipelineID pipelineID)
name|int
name|getNumberOfContainers
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|finalizePipeline (PipelineID pipelineID)
name|void
name|finalizePipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|openPipeline (PipelineID pipelineId)
name|void
name|openPipeline
parameter_list|(
name|PipelineID
name|pipelineId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|removePipeline (PipelineID pipelineID)
name|void
name|removePipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

