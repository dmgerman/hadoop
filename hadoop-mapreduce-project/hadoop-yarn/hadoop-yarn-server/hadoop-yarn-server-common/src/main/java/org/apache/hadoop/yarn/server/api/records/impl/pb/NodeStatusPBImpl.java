begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.records.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
package|;
end_package

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
operator|.
name|Entry
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Container
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeHealthStatus
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ProtoBase
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|ApplicationIdPBImpl
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|ContainerPBImpl
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|NodeHealthStatusPBImpl
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|NodeIdPBImpl
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|ApplicationIdProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|ContainerProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|NodeHealthStatusProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|NodeIdProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServerCommonProtos
operator|.
name|ApplicationIdContainerListMapProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServerCommonProtos
operator|.
name|ContainerListProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServerCommonProtos
operator|.
name|NodeStatusProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServerCommonProtos
operator|.
name|NodeStatusProtoOrBuilder
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
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|records
operator|.
name|NodeStatus
import|;
end_import

begin_class
DECL|class|NodeStatusPBImpl
specifier|public
class|class
name|NodeStatusPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|NodeStatusProto
argument_list|>
implements|implements
name|NodeStatus
block|{
DECL|field|proto
name|NodeStatusProto
name|proto
init|=
name|NodeStatusProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|NodeStatusProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
init|=
literal|null
decl_stmt|;
DECL|field|containers
specifier|private
name|Map
argument_list|<
name|ApplicationIdProto
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|containers
init|=
literal|null
decl_stmt|;
DECL|field|nodeHealthStatus
specifier|private
name|NodeHealthStatus
name|nodeHealthStatus
init|=
literal|null
decl_stmt|;
DECL|method|NodeStatusPBImpl ()
specifier|public
name|NodeStatusPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|NodeStatusProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|NodeStatusPBImpl (NodeStatusProto proto)
specifier|public
name|NodeStatusPBImpl
parameter_list|(
name|NodeStatusProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|NodeStatusProto
name|getProto
parameter_list|()
block|{
name|mergeLocalToProto
argument_list|()
expr_stmt|;
name|proto
operator|=
name|viaProto
condition|?
name|proto
else|:
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
return|return
name|proto
return|;
block|}
DECL|method|mergeLocalToBuilder ()
specifier|private
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|nodeId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setNodeId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|containers
operator|!=
literal|null
condition|)
block|{
name|addContainersToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|nodeHealthStatus
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setNodeHealthStatus
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|nodeHealthStatus
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|mergeLocalToProto ()
specifier|private
name|void
name|mergeLocalToProto
parameter_list|()
block|{
if|if
condition|(
name|viaProto
condition|)
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|mergeLocalToBuilder
argument_list|()
expr_stmt|;
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|maybeInitBuilder ()
specifier|private
name|void
name|maybeInitBuilder
parameter_list|()
block|{
if|if
condition|(
name|viaProto
operator|||
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
name|NodeStatusProto
operator|.
name|newBuilder
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
name|viaProto
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResponseId ()
specifier|public
name|int
name|getResponseId
parameter_list|()
block|{
name|NodeStatusProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getResponseId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setResponseId (int responseId)
specifier|public
name|void
name|setResponseId
parameter_list|(
name|int
name|responseId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setResponseId
argument_list|(
name|responseId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeId ()
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
name|NodeStatusProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|nodeId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|nodeId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasNodeId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|nodeId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|nodeId
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeId (NodeId nodeId)
specifier|public
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearNodeId
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAllContainers ()
specifier|public
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|getAllContainers
parameter_list|()
block|{
name|initContainers
argument_list|()
expr_stmt|;
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|returnMap
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
argument_list|(
name|this
operator|.
name|containers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ApplicationIdProto
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|entry
range|:
name|this
operator|.
name|containers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|returnMap
operator|.
name|put
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|returnMap
return|;
block|}
annotation|@
name|Override
DECL|method|getContainers (ApplicationId applicationId)
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|getContainers
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|initContainers
argument_list|()
expr_stmt|;
name|ApplicationIdProto
name|applicationIdProto
init|=
name|convertToProtoFormat
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|containers
operator|.
name|get
argument_list|(
name|applicationIdProto
argument_list|)
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|containers
operator|.
name|put
argument_list|(
name|applicationIdProto
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|containers
operator|.
name|get
argument_list|(
name|applicationIdProto
argument_list|)
return|;
block|}
DECL|method|initContainers ()
specifier|private
name|void
name|initContainers
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|containers
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|NodeStatusProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|List
argument_list|<
name|ApplicationIdContainerListMapProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getContainersList
argument_list|()
decl_stmt|;
name|this
operator|.
name|containers
operator|=
operator|new
name|HashMap
argument_list|<
name|ApplicationIdProto
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ApplicationIdContainerListMapProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|containers
operator|.
name|put
argument_list|(
name|c
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|convertFromProtoFormat
argument_list|(
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addAllContainers (final Map<ApplicationId, List<Container>> containers)
specifier|public
name|void
name|addAllContainers
parameter_list|(
specifier|final
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|containers
parameter_list|)
block|{
if|if
condition|(
name|containers
operator|==
literal|null
condition|)
return|return;
name|initContainers
argument_list|()
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|entry
range|:
name|containers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|this
operator|.
name|containers
operator|.
name|put
argument_list|(
name|convertToProtoFormat
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addContainersToProto ()
specifier|private
name|void
name|addContainersToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearContainers
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|false
expr_stmt|;
name|Iterable
argument_list|<
name|ApplicationIdContainerListMapProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|ApplicationIdContainerListMapProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ApplicationIdContainerListMapProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ApplicationIdContainerListMapProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ApplicationIdProto
argument_list|>
name|keyIter
init|=
name|containers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|keyIter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ApplicationIdContainerListMapProto
name|next
parameter_list|()
block|{
name|ApplicationIdProto
name|applicationIdProto
init|=
name|keyIter
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|ApplicationIdContainerListMapProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setApplicationId
argument_list|(
name|applicationIdProto
argument_list|)
operator|.
name|setValue
argument_list|(
name|convertToProtoFormat
argument_list|(
name|containers
operator|.
name|get
argument_list|(
name|applicationIdProto
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|builder
operator|.
name|addAllContainers
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeHealthStatus ()
specifier|public
name|NodeHealthStatus
name|getNodeHealthStatus
parameter_list|()
block|{
name|NodeStatusProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
name|nodeHealthStatus
operator|!=
literal|null
condition|)
block|{
return|return
name|nodeHealthStatus
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasNodeHealthStatus
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|nodeHealthStatus
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getNodeHealthStatus
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|nodeHealthStatus
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeHealthStatus (NodeHealthStatus healthStatus)
specifier|public
name|void
name|setNodeHealthStatus
parameter_list|(
name|NodeHealthStatus
name|healthStatus
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|healthStatus
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeHealthStatus
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|nodeHealthStatus
operator|=
name|healthStatus
expr_stmt|;
block|}
comment|/*    *     * @Override   public String getApplicationName() {     ApplicationSubmissionContextProtoOrBuilder p = viaProto ? proto : builder;     if (!p.hasApplicationName()) {       return null;     }     return (p.getApplicationName());   }    @Override   public void setApplicationName(String applicationName) {     maybeInitBuilder();     if (applicationName == null) {       builder.clearApplicationName();       return;     }     builder.setApplicationName((applicationName));   }   */
DECL|method|convertToProtoFormat (List<Container> src)
specifier|private
name|ContainerListProto
name|convertToProtoFormat
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|src
parameter_list|)
block|{
name|ContainerListProto
operator|.
name|Builder
name|ret
init|=
name|ContainerListProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Container
name|c
range|:
name|src
control|)
block|{
name|ret
operator|.
name|addContainer
argument_list|(
operator|(
operator|(
name|ContainerPBImpl
operator|)
name|c
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (ContainerListProto src)
specifier|private
name|List
argument_list|<
name|Container
argument_list|>
name|convertFromProtoFormat
parameter_list|(
name|ContainerListProto
name|src
parameter_list|)
block|{
name|List
argument_list|<
name|Container
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerProto
name|c
range|:
name|src
operator|.
name|getContainerList
argument_list|()
control|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|convertFromProtoFormat (ContainerProto src)
specifier|private
name|Container
name|convertFromProtoFormat
parameter_list|(
name|ContainerProto
name|src
parameter_list|)
block|{
return|return
operator|new
name|ContainerPBImpl
argument_list|(
name|src
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setContainers (ApplicationId applicationId, List<Container> containers)
specifier|public
name|void
name|setContainers
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
block|{
name|initContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|containers
operator|.
name|put
argument_list|(
name|convertToProtoFormat
argument_list|(
name|applicationId
argument_list|)
argument_list|,
name|containers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeContainers (ApplicationId applicationId)
specifier|public
name|void
name|removeContainers
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|initContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|containers
operator|.
name|remove
argument_list|(
name|convertToProtoFormat
argument_list|(
name|applicationId
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearContainers ()
specifier|public
name|void
name|clearContainers
parameter_list|()
block|{
name|initContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|containers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|convertToProtoFormat (NodeId nodeId)
specifier|private
name|NodeIdProto
name|convertToProtoFormat
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeIdPBImpl
operator|)
name|nodeId
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (NodeIdProto proto)
specifier|private
name|NodeId
name|convertFromProtoFormat
parameter_list|(
name|NodeIdProto
name|proto
parameter_list|)
block|{
return|return
operator|new
name|NodeIdPBImpl
argument_list|(
name|proto
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ApplicationId applicationId)
specifier|private
name|ApplicationIdProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ApplicationIdPBImpl
operator|)
name|applicationId
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (ApplicationIdProto proto)
specifier|private
name|ApplicationId
name|convertFromProtoFormat
parameter_list|(
name|ApplicationIdProto
name|proto
parameter_list|)
block|{
return|return
operator|new
name|ApplicationIdPBImpl
argument_list|(
name|proto
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat ( NodeHealthStatus healthStatus)
specifier|private
name|NodeHealthStatusProto
name|convertToProtoFormat
parameter_list|(
name|NodeHealthStatus
name|healthStatus
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeHealthStatusPBImpl
operator|)
name|healthStatus
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (NodeHealthStatusProto proto)
specifier|private
name|NodeHealthStatus
name|convertFromProtoFormat
parameter_list|(
name|NodeHealthStatusProto
name|proto
parameter_list|)
block|{
return|return
operator|new
name|NodeHealthStatusPBImpl
argument_list|(
name|proto
argument_list|)
return|;
block|}
block|}
end_class

end_unit

