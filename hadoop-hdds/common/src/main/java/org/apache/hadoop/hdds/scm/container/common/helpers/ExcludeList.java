begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|pipeline
operator|.
name|PipelineID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This class contains set of dns and containers which ozone client provides  * to be handed over to SCM when block allocation request comes.  */
end_comment

begin_class
DECL|class|ExcludeList
specifier|public
class|class
name|ExcludeList
block|{
DECL|field|datanodes
specifier|private
specifier|final
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
decl_stmt|;
DECL|field|containerIds
specifier|private
specifier|final
name|List
argument_list|<
name|ContainerID
argument_list|>
name|containerIds
decl_stmt|;
DECL|field|pipelineIds
specifier|private
specifier|final
name|List
argument_list|<
name|PipelineID
argument_list|>
name|pipelineIds
decl_stmt|;
DECL|method|ExcludeList ()
specifier|public
name|ExcludeList
parameter_list|()
block|{
name|datanodes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|containerIds
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|pipelineIds
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|getContainerIds ()
specifier|public
name|List
argument_list|<
name|ContainerID
argument_list|>
name|getContainerIds
parameter_list|()
block|{
return|return
name|containerIds
return|;
block|}
DECL|method|getDatanodes ()
specifier|public
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|getDatanodes
parameter_list|()
block|{
return|return
name|datanodes
return|;
block|}
DECL|method|addDatanodes (Collection<DatanodeDetails> dns)
specifier|public
name|void
name|addDatanodes
parameter_list|(
name|Collection
argument_list|<
name|DatanodeDetails
argument_list|>
name|dns
parameter_list|)
block|{
name|datanodes
operator|.
name|addAll
argument_list|(
name|dns
argument_list|)
expr_stmt|;
block|}
DECL|method|addDatanode (DatanodeDetails dn)
specifier|public
name|void
name|addDatanode
parameter_list|(
name|DatanodeDetails
name|dn
parameter_list|)
block|{
name|datanodes
operator|.
name|add
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
DECL|method|addConatinerId (ContainerID containerId)
specifier|public
name|void
name|addConatinerId
parameter_list|(
name|ContainerID
name|containerId
parameter_list|)
block|{
name|containerIds
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
DECL|method|addPipeline (PipelineID pipelineId)
specifier|public
name|void
name|addPipeline
parameter_list|(
name|PipelineID
name|pipelineId
parameter_list|)
block|{
name|pipelineIds
operator|.
name|add
argument_list|(
name|pipelineId
argument_list|)
expr_stmt|;
block|}
DECL|method|getPipelineIds ()
specifier|public
name|List
argument_list|<
name|PipelineID
argument_list|>
name|getPipelineIds
parameter_list|()
block|{
return|return
name|pipelineIds
return|;
block|}
DECL|method|getProtoBuf ()
specifier|public
name|HddsProtos
operator|.
name|ExcludeListProto
name|getProtoBuf
parameter_list|()
block|{
name|HddsProtos
operator|.
name|ExcludeListProto
operator|.
name|Builder
name|builder
init|=
name|HddsProtos
operator|.
name|ExcludeListProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|containerIds
operator|.
name|parallelStream
argument_list|()
operator|.
name|forEach
argument_list|(
name|id
lambda|->
name|builder
operator|.
name|addContainerIds
argument_list|(
name|id
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|datanodes
operator|.
name|parallelStream
argument_list|()
operator|.
name|forEach
argument_list|(
name|dn
lambda|->
block|{
name|builder
operator|.
name|addDatanodes
argument_list|(
name|dn
operator|.
name|getUuidString
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|pipelineIds
operator|.
name|parallelStream
argument_list|()
operator|.
name|forEach
argument_list|(
name|pipelineID
lambda|->
block|{
name|builder
operator|.
name|addPipelineIds
argument_list|(
name|pipelineID
operator|.
name|getProtobuf
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getFromProtoBuf ( HddsProtos.ExcludeListProto excludeListProto)
specifier|public
specifier|static
name|ExcludeList
name|getFromProtoBuf
parameter_list|(
name|HddsProtos
operator|.
name|ExcludeListProto
name|excludeListProto
parameter_list|)
block|{
name|ExcludeList
name|excludeList
init|=
operator|new
name|ExcludeList
argument_list|()
decl_stmt|;
name|excludeListProto
operator|.
name|getContainerIdsList
argument_list|()
operator|.
name|parallelStream
argument_list|()
operator|.
name|forEach
argument_list|(
name|id
lambda|->
block|{
name|excludeList
operator|.
name|addConatinerId
argument_list|(
name|ContainerID
operator|.
name|valueof
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|DatanodeDetails
operator|.
name|Builder
name|builder
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|excludeListProto
operator|.
name|getDatanodesList
argument_list|()
operator|.
name|forEach
argument_list|(
name|dn
lambda|->
block|{
name|builder
operator|.
name|setUuid
argument_list|(
name|dn
argument_list|)
expr_stmt|;
name|excludeList
operator|.
name|addDatanode
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|excludeListProto
operator|.
name|getPipelineIdsList
argument_list|()
operator|.
name|forEach
argument_list|(
name|pipelineID
lambda|->
block|{
name|excludeList
operator|.
name|addPipeline
argument_list|(
name|PipelineID
operator|.
name|getFromProtobuf
argument_list|(
name|pipelineID
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|excludeList
return|;
block|}
block|}
end_class

end_unit

