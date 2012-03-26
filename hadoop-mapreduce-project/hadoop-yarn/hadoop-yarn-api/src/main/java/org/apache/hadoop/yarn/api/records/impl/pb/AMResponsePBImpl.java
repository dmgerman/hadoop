begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.impl.pb
package|package
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
name|AMResponse
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
name|ContainerStatus
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
name|NodeReport
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
name|Resource
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
name|AMResponseProto
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
name|AMResponseProtoOrBuilder
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
name|ContainerStatusProto
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
name|NodeReportProto
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
name|ResourceProto
import|;
end_import

begin_class
DECL|class|AMResponsePBImpl
specifier|public
class|class
name|AMResponsePBImpl
extends|extends
name|ProtoBase
argument_list|<
name|AMResponseProto
argument_list|>
implements|implements
name|AMResponse
block|{
DECL|field|proto
name|AMResponseProto
name|proto
init|=
name|AMResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|AMResponseProto
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
DECL|field|limit
name|Resource
name|limit
decl_stmt|;
DECL|field|allocatedContainers
specifier|private
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
init|=
literal|null
decl_stmt|;
DECL|field|completedContainersStatuses
specifier|private
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completedContainersStatuses
init|=
literal|null
decl_stmt|;
comment|//  private boolean hasLocalContainerList = false;
DECL|field|updatedNodes
specifier|private
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
init|=
literal|null
decl_stmt|;
DECL|method|AMResponsePBImpl ()
specifier|public
name|AMResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|AMResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|AMResponsePBImpl (AMResponseProto proto)
specifier|public
name|AMResponsePBImpl
parameter_list|(
name|AMResponseProto
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
specifier|synchronized
name|AMResponseProto
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
specifier|synchronized
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|allocatedContainers
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAllocatedContainers
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|ContainerProto
argument_list|>
name|iterable
init|=
name|getProtoIterable
argument_list|(
name|this
operator|.
name|allocatedContainers
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAllAllocatedContainers
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|completedContainersStatuses
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearCompletedContainerStatuses
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|ContainerStatusProto
argument_list|>
name|iterable
init|=
name|getContainerStatusProtoIterable
argument_list|(
name|this
operator|.
name|completedContainersStatuses
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAllCompletedContainerStatuses
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|updatedNodes
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearUpdatedNodes
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|NodeReportProto
argument_list|>
name|iterable
init|=
name|getNodeReportProtoIterable
argument_list|(
name|this
operator|.
name|updatedNodes
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAllUpdatedNodes
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|limit
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setLimit
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|limit
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|mergeLocalToProto ()
specifier|private
specifier|synchronized
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
specifier|synchronized
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
name|AMResponseProto
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
DECL|method|getReboot ()
specifier|public
specifier|synchronized
name|boolean
name|getReboot
parameter_list|()
block|{
name|AMResponseProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
operator|(
name|p
operator|.
name|getReboot
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setReboot (boolean reboot)
specifier|public
specifier|synchronized
name|void
name|setReboot
parameter_list|(
name|boolean
name|reboot
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setReboot
argument_list|(
operator|(
name|reboot
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResponseId ()
specifier|public
specifier|synchronized
name|int
name|getResponseId
parameter_list|()
block|{
name|AMResponseProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
operator|(
name|p
operator|.
name|getResponseId
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setResponseId (int responseId)
specifier|public
specifier|synchronized
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
operator|(
name|responseId
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAvailableResources ()
specifier|public
specifier|synchronized
name|Resource
name|getAvailableResources
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|limit
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|limit
return|;
block|}
name|AMResponseProtoOrBuilder
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
operator|!
name|p
operator|.
name|hasLimit
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|limit
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|limit
return|;
block|}
annotation|@
name|Override
DECL|method|setAvailableResources (Resource limit)
specifier|public
specifier|synchronized
name|void
name|setAvailableResources
parameter_list|(
name|Resource
name|limit
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|limit
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearLimit
argument_list|()
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUpdatedNodes ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getUpdatedNodes
parameter_list|()
block|{
name|initLocalNewNodeReportList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|updatedNodes
return|;
block|}
comment|//Once this is called. updatedNodes will never be null - until a getProto is called.
DECL|method|initLocalNewNodeReportList ()
specifier|private
specifier|synchronized
name|void
name|initLocalNewNodeReportList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|updatedNodes
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|AMResponseProtoOrBuilder
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
name|NodeReportProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getUpdatedNodesList
argument_list|()
decl_stmt|;
name|updatedNodes
operator|=
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeReportProto
name|n
range|:
name|list
control|)
block|{
name|updatedNodes
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setUpdatedNodes (final List<NodeReport> updatedNodes)
specifier|public
specifier|synchronized
name|void
name|setUpdatedNodes
parameter_list|(
specifier|final
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|)
block|{
if|if
condition|(
name|updatedNodes
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|updatedNodes
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|updatedNodes
operator|=
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|(
name|updatedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|updatedNodes
operator|.
name|addAll
argument_list|(
name|updatedNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAllocatedContainers ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|Container
argument_list|>
name|getAllocatedContainers
parameter_list|()
block|{
name|initLocalNewContainerList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|allocatedContainers
return|;
block|}
comment|//Once this is called. containerList will never be null - until a getProto is called.
DECL|method|initLocalNewContainerList ()
specifier|private
specifier|synchronized
name|void
name|initLocalNewContainerList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|allocatedContainers
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|AMResponseProtoOrBuilder
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
name|ContainerProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getAllocatedContainersList
argument_list|()
decl_stmt|;
name|allocatedContainers
operator|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerProto
name|c
range|:
name|list
control|)
block|{
name|allocatedContainers
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
block|}
annotation|@
name|Override
DECL|method|setAllocatedContainers (final List<Container> containers)
specifier|public
specifier|synchronized
name|void
name|setAllocatedContainers
parameter_list|(
specifier|final
name|List
argument_list|<
name|Container
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
comment|// this looks like a bug because it results in append and not set
name|initLocalNewContainerList
argument_list|()
expr_stmt|;
name|allocatedContainers
operator|.
name|addAll
argument_list|(
name|containers
argument_list|)
expr_stmt|;
block|}
DECL|method|getProtoIterable ( final List<Container> newContainersList)
specifier|private
specifier|synchronized
name|Iterable
argument_list|<
name|ContainerProto
argument_list|>
name|getProtoIterable
parameter_list|(
specifier|final
name|List
argument_list|<
name|Container
argument_list|>
name|newContainersList
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|ContainerProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterator
argument_list|<
name|ContainerProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ContainerProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|Container
argument_list|>
name|iter
init|=
name|newContainersList
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|ContainerProto
name|next
parameter_list|()
block|{
return|return
name|convertToProtoFormat
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
return|;
block|}
specifier|private
specifier|synchronized
name|Iterable
argument_list|<
name|ContainerStatusProto
argument_list|>
DECL|method|getContainerStatusProtoIterable ( final List<ContainerStatus> newContainersList)
name|getContainerStatusProtoIterable
parameter_list|(
specifier|final
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|newContainersList
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|ContainerStatusProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterator
argument_list|<
name|ContainerStatusProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ContainerStatusProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ContainerStatus
argument_list|>
name|iter
init|=
name|newContainersList
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|ContainerStatusProto
name|next
parameter_list|()
block|{
return|return
name|convertToProtoFormat
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
return|;
block|}
specifier|private
specifier|synchronized
name|Iterable
argument_list|<
name|NodeReportProto
argument_list|>
DECL|method|getNodeReportProtoIterable ( final List<NodeReport> newNodeReportsList)
name|getNodeReportProtoIterable
parameter_list|(
specifier|final
name|List
argument_list|<
name|NodeReport
argument_list|>
name|newNodeReportsList
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|NodeReportProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Iterator
argument_list|<
name|NodeReportProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|NodeReportProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|NodeReport
argument_list|>
name|iter
init|=
name|newNodeReportsList
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|NodeReportProto
name|next
parameter_list|()
block|{
return|return
name|convertToProtoFormat
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
return|;
block|}
comment|//// Finished containers
annotation|@
name|Override
DECL|method|getCompletedContainersStatuses ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getCompletedContainersStatuses
parameter_list|()
block|{
name|initLocalFinishedContainerList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|completedContainersStatuses
return|;
block|}
comment|//Once this is called. containerList will never be null - untill a getProto is called.
DECL|method|initLocalFinishedContainerList ()
specifier|private
specifier|synchronized
name|void
name|initLocalFinishedContainerList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|completedContainersStatuses
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|AMResponseProtoOrBuilder
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
name|ContainerStatusProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getCompletedContainerStatusesList
argument_list|()
decl_stmt|;
name|completedContainersStatuses
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerStatusProto
name|c
range|:
name|list
control|)
block|{
name|completedContainersStatuses
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
block|}
annotation|@
name|Override
DECL|method|setCompletedContainersStatuses ( final List<ContainerStatus> containers)
specifier|public
specifier|synchronized
name|void
name|setCompletedContainersStatuses
parameter_list|(
specifier|final
name|List
argument_list|<
name|ContainerStatus
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
name|initLocalFinishedContainerList
argument_list|()
expr_stmt|;
name|completedContainersStatuses
operator|.
name|addAll
argument_list|(
name|containers
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat ( NodeReportProto p)
specifier|private
specifier|synchronized
name|NodeReportPBImpl
name|convertFromProtoFormat
parameter_list|(
name|NodeReportProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|NodeReportPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (NodeReport t)
specifier|private
specifier|synchronized
name|NodeReportProto
name|convertToProtoFormat
parameter_list|(
name|NodeReport
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeReportPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat ( ContainerProto p)
specifier|private
specifier|synchronized
name|ContainerPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ContainerProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ContainerPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Container t)
specifier|private
specifier|synchronized
name|ContainerProto
name|convertToProtoFormat
parameter_list|(
name|Container
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat ( ContainerStatusProto p)
specifier|private
specifier|synchronized
name|ContainerStatusPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ContainerStatusProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ContainerStatusPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ContainerStatus t)
specifier|private
specifier|synchronized
name|ContainerStatusProto
name|convertToProtoFormat
parameter_list|(
name|ContainerStatus
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerStatusPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (ResourceProto p)
specifier|private
specifier|synchronized
name|ResourcePBImpl
name|convertFromProtoFormat
parameter_list|(
name|ResourceProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ResourcePBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Resource r)
specifier|private
specifier|synchronized
name|ResourceProto
name|convertToProtoFormat
parameter_list|(
name|Resource
name|r
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|r
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
block|}
end_class

end_unit

