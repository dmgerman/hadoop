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
name|commons
operator|.
name|lang3
operator|.
name|builder
operator|.
name|EqualsBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|LinkedHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Represents a group of datanodes which store a container.  */
end_comment

begin_class
DECL|class|Pipeline
specifier|public
specifier|final
class|class
name|Pipeline
block|{
DECL|field|id
specifier|private
specifier|final
name|PipelineID
name|id
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|ReplicationType
name|type
decl_stmt|;
DECL|field|factor
specifier|private
specifier|final
name|ReplicationFactor
name|factor
decl_stmt|;
DECL|field|state
specifier|private
name|PipelineState
name|state
decl_stmt|;
DECL|field|nodeStatus
specifier|private
name|Map
argument_list|<
name|DatanodeDetails
argument_list|,
name|Long
argument_list|>
name|nodeStatus
decl_stmt|;
DECL|method|Pipeline (PipelineID id, ReplicationType type, ReplicationFactor factor, PipelineState state, Map<DatanodeDetails, Long> nodeStatus)
specifier|private
name|Pipeline
parameter_list|(
name|PipelineID
name|id
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|PipelineState
name|state
parameter_list|,
name|Map
argument_list|<
name|DatanodeDetails
argument_list|,
name|Long
argument_list|>
name|nodeStatus
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|factor
operator|=
name|factor
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|nodeStatus
operator|=
name|nodeStatus
expr_stmt|;
block|}
comment|/**    * Returns the ID of this pipeline.    *    * @return PipelineID    */
DECL|method|getId ()
specifier|public
name|PipelineID
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**    * Returns the type.    *    * @return type - Simple or Ratis.    */
DECL|method|getType ()
specifier|public
name|ReplicationType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * Returns the factor.    *    * @return type - Simple or Ratis.    */
DECL|method|getFactor ()
specifier|public
name|ReplicationFactor
name|getFactor
parameter_list|()
block|{
return|return
name|factor
return|;
block|}
comment|/**    * Returns the State of the pipeline.    *    * @return - LifeCycleStates.    */
DECL|method|getPipelineState ()
specifier|public
name|PipelineState
name|getPipelineState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**    * Returns the list of nodes which form this pipeline.    *    * @return List of DatanodeDetails    */
DECL|method|getNodes ()
specifier|public
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|getNodes
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nodeStatus
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getFirstNode ()
specifier|public
name|DatanodeDetails
name|getFirstNode
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|nodeStatus
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Pipeline=%s is empty"
argument_list|,
name|id
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|nodeStatus
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|isClosed ()
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|state
operator|==
name|PipelineState
operator|.
name|CLOSED
return|;
block|}
DECL|method|isOpen ()
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|state
operator|==
name|PipelineState
operator|.
name|OPEN
return|;
block|}
DECL|method|reportDatanode (DatanodeDetails dn)
name|void
name|reportDatanode
parameter_list|(
name|DatanodeDetails
name|dn
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nodeStatus
operator|.
name|get
argument_list|(
name|dn
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Datanode=%s not part of pipeline=%s"
argument_list|,
name|dn
argument_list|,
name|id
argument_list|)
argument_list|)
throw|;
block|}
name|nodeStatus
operator|.
name|put
argument_list|(
name|dn
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|isHealthy ()
name|boolean
name|isHealthy
parameter_list|()
block|{
for|for
control|(
name|Long
name|reportedTime
range|:
name|nodeStatus
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|reportedTime
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|nodeStatus
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|getProtobufMessage ()
specifier|public
name|HddsProtos
operator|.
name|Pipeline
name|getProtobufMessage
parameter_list|()
throws|throws
name|UnknownPipelineStateException
block|{
name|HddsProtos
operator|.
name|Pipeline
operator|.
name|Builder
name|builder
init|=
name|HddsProtos
operator|.
name|Pipeline
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
name|id
operator|.
name|getProtobuf
argument_list|()
argument_list|)
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
name|setState
argument_list|(
name|PipelineState
operator|.
name|getProtobuf
argument_list|(
name|state
argument_list|)
argument_list|)
operator|.
name|setLeaderID
argument_list|(
literal|""
argument_list|)
operator|.
name|addAllMembers
argument_list|(
name|nodeStatus
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|DatanodeDetails
operator|::
name|getProtoBufMessage
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getFromProtobuf (HddsProtos.Pipeline pipeline)
specifier|public
specifier|static
name|Pipeline
name|getFromProtobuf
parameter_list|(
name|HddsProtos
operator|.
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|UnknownPipelineStateException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
argument_list|,
literal|"Pipeline is null"
argument_list|)
expr_stmt|;
return|return
operator|new
name|Builder
argument_list|()
operator|.
name|setId
argument_list|(
name|PipelineID
operator|.
name|getFromProtobuf
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setFactor
argument_list|(
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|setState
argument_list|(
name|PipelineState
operator|.
name|fromProtobuf
argument_list|(
name|pipeline
operator|.
name|getState
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setNodes
argument_list|(
name|pipeline
operator|.
name|getMembersList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|DatanodeDetails
operator|::
name|getFromProtoBuf
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Pipeline
name|that
init|=
operator|(
name|Pipeline
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|id
argument_list|,
name|that
operator|.
name|id
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|,
name|that
operator|.
name|type
argument_list|)
operator|.
name|append
argument_list|(
name|factor
argument_list|,
name|that
operator|.
name|factor
argument_list|)
operator|.
name|append
argument_list|(
name|getNodes
argument_list|()
argument_list|,
name|that
operator|.
name|getNodes
argument_list|()
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|id
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
name|factor
argument_list|)
operator|.
name|append
argument_list|(
name|nodeStatus
argument_list|)
operator|.
name|toHashCode
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
name|b
operator|.
name|append
argument_list|(
literal|" Id: "
argument_list|)
operator|.
name|append
argument_list|(
name|id
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|", Nodes: "
argument_list|)
expr_stmt|;
name|nodeStatus
operator|.
name|keySet
argument_list|()
operator|.
name|forEach
argument_list|(
name|b
operator|::
name|append
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|", Type:"
argument_list|)
operator|.
name|append
argument_list|(
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|", Factor:"
argument_list|)
operator|.
name|append
argument_list|(
name|getFactor
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|", State:"
argument_list|)
operator|.
name|append
argument_list|(
name|getPipelineState
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|newBuilder ()
specifier|public
specifier|static
name|Builder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|method|newBuilder (Pipeline pipeline)
specifier|public
specifier|static
name|Builder
name|newBuilder
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|pipeline
argument_list|)
return|;
block|}
comment|/**    * Builder class for Pipeline.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|id
specifier|private
name|PipelineID
name|id
init|=
literal|null
decl_stmt|;
DECL|field|type
specifier|private
name|ReplicationType
name|type
init|=
literal|null
decl_stmt|;
DECL|field|factor
specifier|private
name|ReplicationFactor
name|factor
init|=
literal|null
decl_stmt|;
DECL|field|state
specifier|private
name|PipelineState
name|state
init|=
literal|null
decl_stmt|;
DECL|field|nodeStatus
specifier|private
name|Map
argument_list|<
name|DatanodeDetails
argument_list|,
name|Long
argument_list|>
name|nodeStatus
init|=
literal|null
decl_stmt|;
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{}
DECL|method|Builder (Pipeline pipeline)
specifier|public
name|Builder
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|pipeline
operator|.
name|id
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|pipeline
operator|.
name|type
expr_stmt|;
name|this
operator|.
name|factor
operator|=
name|pipeline
operator|.
name|factor
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|pipeline
operator|.
name|state
expr_stmt|;
name|this
operator|.
name|nodeStatus
operator|=
name|pipeline
operator|.
name|nodeStatus
expr_stmt|;
block|}
DECL|method|setId (PipelineID id1)
specifier|public
name|Builder
name|setId
parameter_list|(
name|PipelineID
name|id1
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id1
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setType (ReplicationType type1)
specifier|public
name|Builder
name|setType
parameter_list|(
name|ReplicationType
name|type1
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type1
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFactor (ReplicationFactor factor1)
specifier|public
name|Builder
name|setFactor
parameter_list|(
name|ReplicationFactor
name|factor1
parameter_list|)
block|{
name|this
operator|.
name|factor
operator|=
name|factor1
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setState (PipelineState state1)
specifier|public
name|Builder
name|setState
parameter_list|(
name|PipelineState
name|state1
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state1
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNodes (List<DatanodeDetails> nodes)
specifier|public
name|Builder
name|setNodes
parameter_list|(
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|nodeStatus
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|nodes
operator|.
name|forEach
argument_list|(
name|node
lambda|->
name|nodeStatus
operator|.
name|put
argument_list|(
name|node
argument_list|,
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|Pipeline
name|build
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|factor
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nodeStatus
argument_list|)
expr_stmt|;
return|return
operator|new
name|Pipeline
argument_list|(
name|id
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|state
argument_list|,
name|nodeStatus
argument_list|)
return|;
block|}
block|}
comment|/**    * Possible Pipeline states in SCM.    */
DECL|enum|PipelineState
specifier|public
enum|enum
name|PipelineState
block|{
DECL|enumConstant|ALLOCATED
DECL|enumConstant|OPEN
DECL|enumConstant|CLOSED
name|ALLOCATED
block|,
name|OPEN
block|,
name|CLOSED
block|;
DECL|method|fromProtobuf (HddsProtos.PipelineState state)
specifier|public
specifier|static
name|PipelineState
name|fromProtobuf
parameter_list|(
name|HddsProtos
operator|.
name|PipelineState
name|state
parameter_list|)
throws|throws
name|UnknownPipelineStateException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|state
argument_list|,
literal|"Pipeline state is null"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|PIPELINE_ALLOCATED
case|:
return|return
name|ALLOCATED
return|;
case|case
name|PIPELINE_OPEN
case|:
return|return
name|OPEN
return|;
case|case
name|PIPELINE_CLOSED
case|:
return|return
name|CLOSED
return|;
default|default:
throw|throw
operator|new
name|UnknownPipelineStateException
argument_list|(
literal|"Pipeline state: "
operator|+
name|state
operator|+
literal|" is not recognized."
argument_list|)
throw|;
block|}
block|}
DECL|method|getProtobuf (PipelineState state)
specifier|public
specifier|static
name|HddsProtos
operator|.
name|PipelineState
name|getProtobuf
parameter_list|(
name|PipelineState
name|state
parameter_list|)
throws|throws
name|UnknownPipelineStateException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|state
argument_list|,
literal|"Pipeline state is null"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|ALLOCATED
case|:
return|return
name|HddsProtos
operator|.
name|PipelineState
operator|.
name|PIPELINE_ALLOCATED
return|;
case|case
name|OPEN
case|:
return|return
name|HddsProtos
operator|.
name|PipelineState
operator|.
name|PIPELINE_OPEN
return|;
case|case
name|CLOSED
case|:
return|return
name|HddsProtos
operator|.
name|PipelineState
operator|.
name|PIPELINE_CLOSED
return|;
default|default:
throw|throw
operator|new
name|UnknownPipelineStateException
argument_list|(
literal|"Pipeline state: "
operator|+
name|state
operator|+
literal|" is not recognized."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

