begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm.container.common.helpers
package|package
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonAutoDetect
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonFilter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonIgnore
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|PropertyAccessor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectWriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ser
operator|.
name|FilterProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ser
operator|.
name|impl
operator|.
name|SimpleBeanPropertyFilter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ser
operator|.
name|impl
operator|.
name|SimpleFilterProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|hdsl
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdslProtos
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
name|Arrays
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
comment|/**  * A pipeline represents the group of machines over which a container lives.  */
end_comment

begin_class
DECL|class|Pipeline
specifier|public
class|class
name|Pipeline
block|{
DECL|field|PIPELINE_INFO
specifier|static
specifier|final
name|String
name|PIPELINE_INFO
init|=
literal|"PIPELINE_INFO_FILTER"
decl_stmt|;
DECL|field|WRITER
specifier|private
specifier|static
specifier|final
name|ObjectWriter
name|WRITER
decl_stmt|;
static|static
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|String
index|[]
name|ignorableFieldNames
init|=
block|{
literal|"data"
block|}
decl_stmt|;
name|FilterProvider
name|filters
init|=
operator|new
name|SimpleFilterProvider
argument_list|()
operator|.
name|addFilter
argument_list|(
name|PIPELINE_INFO
argument_list|,
name|SimpleBeanPropertyFilter
operator|.
name|serializeAllExcept
argument_list|(
name|ignorableFieldNames
argument_list|)
argument_list|)
decl_stmt|;
name|mapper
operator|.
name|setVisibility
argument_list|(
name|PropertyAccessor
operator|.
name|FIELD
argument_list|,
name|JsonAutoDetect
operator|.
name|Visibility
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|addMixIn
argument_list|(
name|Object
operator|.
name|class
argument_list|,
name|MixIn
operator|.
name|class
argument_list|)
expr_stmt|;
name|WRITER
operator|=
name|mapper
operator|.
name|writer
argument_list|(
name|filters
argument_list|)
expr_stmt|;
block|}
DECL|field|containerName
specifier|private
name|String
name|containerName
decl_stmt|;
DECL|field|pipelineChannel
specifier|private
name|PipelineChannel
name|pipelineChannel
decl_stmt|;
comment|/**    * Allows you to maintain private data on pipelines. This is not serialized    * via protobuf, just allows us to maintain some private data.    */
annotation|@
name|JsonIgnore
DECL|field|data
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
comment|/**    * Constructs a new pipeline data structure.    *    * @param containerName - Container    * @param pipelineChannel - transport information for this container    */
DECL|method|Pipeline (String containerName, PipelineChannel pipelineChannel)
specifier|public
name|Pipeline
parameter_list|(
name|String
name|containerName
parameter_list|,
name|PipelineChannel
name|pipelineChannel
parameter_list|)
block|{
name|this
operator|.
name|containerName
operator|=
name|containerName
expr_stmt|;
name|this
operator|.
name|pipelineChannel
operator|=
name|pipelineChannel
expr_stmt|;
name|data
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Gets pipeline object from protobuf.    *    * @param pipeline - ProtoBuf definition for the pipeline.    * @return Pipeline Object    */
DECL|method|getFromProtoBuf (HdslProtos.Pipeline pipeline)
specifier|public
specifier|static
name|Pipeline
name|getFromProtoBuf
parameter_list|(
name|HdslProtos
operator|.
name|Pipeline
name|pipeline
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|PipelineChannel
name|pipelineChannel
init|=
name|PipelineChannel
operator|.
name|getFromProtoBuf
argument_list|(
name|pipeline
operator|.
name|getPipelineChannel
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|Pipeline
argument_list|(
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|pipelineChannel
argument_list|)
return|;
block|}
DECL|method|getFactor ()
specifier|public
name|HdslProtos
operator|.
name|ReplicationFactor
name|getFactor
parameter_list|()
block|{
return|return
name|pipelineChannel
operator|.
name|getFactor
argument_list|()
return|;
block|}
comment|/**    * Returns the first machine in the set of datanodes.    *    * @return First Machine.    */
annotation|@
name|JsonIgnore
DECL|method|getLeader ()
specifier|public
name|DatanodeDetails
name|getLeader
parameter_list|()
block|{
return|return
name|pipelineChannel
operator|.
name|getDatanodes
argument_list|()
operator|.
name|get
argument_list|(
name|pipelineChannel
operator|.
name|getLeaderID
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the leader host.    *    * @return First Machine.    */
DECL|method|getLeaderHost ()
specifier|public
name|String
name|getLeaderHost
parameter_list|()
block|{
return|return
name|pipelineChannel
operator|.
name|getDatanodes
argument_list|()
operator|.
name|get
argument_list|(
name|pipelineChannel
operator|.
name|getLeaderID
argument_list|()
argument_list|)
operator|.
name|getHostName
argument_list|()
return|;
block|}
comment|/**    * Returns all machines that make up this pipeline.    *    * @return List of Machines.    */
annotation|@
name|JsonIgnore
DECL|method|getMachines ()
specifier|public
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|getMachines
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|pipelineChannel
operator|.
name|getDatanodes
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns all machines that make up this pipeline.    *    * @return List of Machines.    */
DECL|method|getDatanodeHosts ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDatanodeHosts
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|dataHosts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeDetails
name|id
range|:
name|pipelineChannel
operator|.
name|getDatanodes
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|dataHosts
operator|.
name|add
argument_list|(
name|id
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|dataHosts
return|;
block|}
comment|/**    * Return a Protobuf Pipeline message from pipeline.    *    * @return Protobuf message    */
annotation|@
name|JsonIgnore
DECL|method|getProtobufMessage ()
specifier|public
name|HdslProtos
operator|.
name|Pipeline
name|getProtobufMessage
parameter_list|()
block|{
name|HdslProtos
operator|.
name|Pipeline
operator|.
name|Builder
name|builder
init|=
name|HdslProtos
operator|.
name|Pipeline
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setContainerName
argument_list|(
name|this
operator|.
name|containerName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setPipelineChannel
argument_list|(
name|this
operator|.
name|pipelineChannel
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns containerName if available.    *    * @return String.    */
DECL|method|getContainerName ()
specifier|public
name|String
name|getContainerName
parameter_list|()
block|{
return|return
name|containerName
return|;
block|}
comment|/**    * Returns private data that is set on this pipeline.    *    * @return blob, the user can interpret it any way they like.    */
DECL|method|getData ()
specifier|public
name|byte
index|[]
name|getData
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|data
operator|!=
literal|null
condition|)
block|{
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|this
operator|.
name|data
argument_list|,
name|this
operator|.
name|data
operator|.
name|length
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getPipelineChannel ()
specifier|public
name|PipelineChannel
name|getPipelineChannel
parameter_list|()
block|{
return|return
name|pipelineChannel
return|;
block|}
comment|/**    * Set private data on pipeline.    *    * @param data -- private data.    */
DECL|method|setData (byte[] data)
specifier|public
name|void
name|setData
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|data
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|data
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Gets the State of the pipeline.    *    * @return - LifeCycleStates.    */
DECL|method|getLifeCycleState ()
specifier|public
name|HdslProtos
operator|.
name|LifeCycleState
name|getLifeCycleState
parameter_list|()
block|{
return|return
name|pipelineChannel
operator|.
name|getLifeCycleState
argument_list|()
return|;
block|}
comment|/**    * Gets the pipeline Name.    *    * @return - Name of the pipeline    */
DECL|method|getPipelineName ()
specifier|public
name|String
name|getPipelineName
parameter_list|()
block|{
return|return
name|pipelineChannel
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**    * Returns the type.    *    * @return type - Standalone, Ratis, Chained.    */
DECL|method|getType ()
specifier|public
name|HdslProtos
operator|.
name|ReplicationType
name|getType
parameter_list|()
block|{
return|return
name|pipelineChannel
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
decl_stmt|;
name|pipelineChannel
operator|.
name|getDatanodes
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|id
lambda|->
name|b
operator|.
name|append
argument_list|(
name|id
operator|.
name|endsWith
argument_list|(
name|pipelineChannel
operator|.
name|getLeaderID
argument_list|()
argument_list|)
condition|?
literal|"*"
operator|+
name|id
else|:
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"] container:"
argument_list|)
operator|.
name|append
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" name:"
argument_list|)
operator|.
name|append
argument_list|(
name|getPipelineName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" type:"
argument_list|)
operator|.
name|append
argument_list|(
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getFactor
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" factor:"
argument_list|)
operator|.
name|append
argument_list|(
name|getFactor
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getLifeCycleState
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" State:"
argument_list|)
operator|.
name|append
argument_list|(
name|getLifeCycleState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns a JSON string of this object.    *    * @return String - json string    * @throws IOException    */
DECL|method|toJsonString ()
specifier|public
name|String
name|toJsonString
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|WRITER
operator|.
name|writeValueAsString
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|JsonFilter
argument_list|(
name|PIPELINE_INFO
argument_list|)
DECL|class|MixIn
class|class
name|MixIn
block|{   }
block|}
end_class

end_unit

