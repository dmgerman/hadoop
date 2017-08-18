begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.pipelines
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|pipelines
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|proto
operator|.
name|OzoneProtos
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

begin_comment
comment|/**  * Manage Ozone pipelines.  */
end_comment

begin_interface
DECL|interface|PipelineManager
specifier|public
interface|interface
name|PipelineManager
block|{
comment|/**    * This function is called by the Container Manager while allocating a new    * container. The client specifies what kind of replication pipeline is    * needed and based on the replication type in the request appropriate    * Interface is invoked.    *    * @param containerName Name of the container    * @param replicationFactor - Replication Factor    * @return a Pipeline.    */
DECL|method|getPipeline (String containerName, OzoneProtos.ReplicationFactor replicationFactor)
name|Pipeline
name|getPipeline
parameter_list|(
name|String
name|containerName
parameter_list|,
name|OzoneProtos
operator|.
name|ReplicationFactor
name|replicationFactor
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a pipeline from a specified set of Nodes.    * @param pipelineID - Name of the pipeline    * @param datanodes - The list of datanodes that make this pipeline.    */
DECL|method|createPipeline (String pipelineID, List<DatanodeID> datanodes)
name|void
name|createPipeline
parameter_list|(
name|String
name|pipelineID
parameter_list|,
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|datanodes
parameter_list|)
throws|throws
name|IOException
function_decl|;
empty_stmt|;
comment|/**    * Close the  pipeline with the given clusterId.    */
DECL|method|closePipeline (String pipelineID)
name|void
name|closePipeline
parameter_list|(
name|String
name|pipelineID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * list members in the pipeline .    * @return the datanode    */
DECL|method|getMembers (String pipelineID)
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getMembers
parameter_list|(
name|String
name|pipelineID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Update the datanode list of the pipeline.    */
DECL|method|updatePipeline (String pipelineID, List<DatanodeID> newDatanodes)
name|void
name|updatePipeline
parameter_list|(
name|String
name|pipelineID
parameter_list|,
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|newDatanodes
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

