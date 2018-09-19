begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.common.helpers
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
name|container
operator|.
name|common
operator|.
name|helpers
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
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroupId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * ID for the pipeline, the ID is based on UUID so that it can be used  * in Ratis as RaftGroupId, GroupID is used by the datanodes to initialize  * the ratis group they are part of.  */
end_comment

begin_class
DECL|class|PipelineID
specifier|public
specifier|final
class|class
name|PipelineID
implements|implements
name|Comparable
argument_list|<
name|PipelineID
argument_list|>
block|{
DECL|field|id
specifier|private
name|UUID
name|id
decl_stmt|;
DECL|field|groupId
specifier|private
name|RaftGroupId
name|groupId
decl_stmt|;
DECL|method|PipelineID (UUID id)
specifier|private
name|PipelineID
parameter_list|(
name|UUID
name|id
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
name|groupId
operator|=
name|RaftGroupId
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
DECL|method|randomId ()
specifier|public
specifier|static
name|PipelineID
name|randomId
parameter_list|()
block|{
return|return
operator|new
name|PipelineID
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
return|;
block|}
DECL|method|valueOf (UUID id)
specifier|public
specifier|static
name|PipelineID
name|valueOf
parameter_list|(
name|UUID
name|id
parameter_list|)
block|{
return|return
operator|new
name|PipelineID
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|valueOf (RaftGroupId groupId)
specifier|public
specifier|static
name|PipelineID
name|valueOf
parameter_list|(
name|RaftGroupId
name|groupId
parameter_list|)
block|{
return|return
name|valueOf
argument_list|(
name|groupId
operator|.
name|getUuid
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getRaftGroupID ()
specifier|public
name|RaftGroupId
name|getRaftGroupID
parameter_list|()
block|{
return|return
name|groupId
return|;
block|}
DECL|method|getId ()
specifier|public
name|UUID
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getProtobuf ()
specifier|public
name|HddsProtos
operator|.
name|PipelineID
name|getProtobuf
parameter_list|()
block|{
return|return
name|HddsProtos
operator|.
name|PipelineID
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getFromProtobuf (HddsProtos.PipelineID protos)
specifier|public
specifier|static
name|PipelineID
name|getFromProtobuf
parameter_list|(
name|HddsProtos
operator|.
name|PipelineID
name|protos
parameter_list|)
block|{
return|return
operator|new
name|PipelineID
argument_list|(
name|UUID
operator|.
name|fromString
argument_list|(
name|protos
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
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
return|return
literal|"pipelineId="
operator|+
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (PipelineID o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|PipelineID
name|o
parameter_list|)
block|{
return|return
name|this
operator|.
name|id
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|id
argument_list|)
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
name|PipelineID
name|that
init|=
operator|(
name|PipelineID
operator|)
name|o
decl_stmt|;
return|return
name|id
operator|.
name|equals
argument_list|(
name|that
operator|.
name|id
argument_list|)
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
name|id
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

