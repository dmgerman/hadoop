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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|ApplicationReport
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
name|QueueConfigurations
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
name|QueueInfo
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
name|QueueState
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
name|QueueStatistics
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
name|ApplicationReportProto
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
name|QueueConfigurationsMapProto
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
name|QueueConfigurationsProto
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
name|QueueInfoProto
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
name|QueueInfoProtoOrBuilder
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
name|QueueStateProto
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
name|QueueStatisticsProto
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|QueueInfoPBImpl
specifier|public
class|class
name|QueueInfoPBImpl
extends|extends
name|QueueInfo
block|{
DECL|field|proto
name|QueueInfoProto
name|proto
init|=
name|QueueInfoProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|QueueInfoProto
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
DECL|field|applicationsList
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|applicationsList
decl_stmt|;
DECL|field|childQueuesList
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|childQueuesList
decl_stmt|;
DECL|field|accessibleNodeLabels
name|Set
argument_list|<
name|String
argument_list|>
name|accessibleNodeLabels
decl_stmt|;
DECL|field|queueConfigurations
name|Map
argument_list|<
name|String
argument_list|,
name|QueueConfigurations
argument_list|>
name|queueConfigurations
decl_stmt|;
DECL|method|QueueInfoPBImpl ()
specifier|public
name|QueueInfoPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|QueueInfoProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|QueueInfoPBImpl (QueueInfoProto proto)
specifier|public
name|QueueInfoPBImpl
parameter_list|(
name|QueueInfoProto
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
annotation|@
name|Override
DECL|method|getApplications ()
specifier|public
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplications
parameter_list|()
block|{
name|initLocalApplicationsList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|applicationsList
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacity ()
specifier|public
name|float
name|getCapacity
parameter_list|()
block|{
name|QueueInfoProtoOrBuilder
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
name|hasCapacity
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getCapacity
argument_list|()
else|:
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getChildQueues ()
specifier|public
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|getChildQueues
parameter_list|()
block|{
name|initLocalChildQueuesList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|childQueuesList
return|;
block|}
annotation|@
name|Override
DECL|method|getCurrentCapacity ()
specifier|public
name|float
name|getCurrentCapacity
parameter_list|()
block|{
name|QueueInfoProtoOrBuilder
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
name|hasCurrentCapacity
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getCurrentCapacity
argument_list|()
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getMaximumCapacity ()
specifier|public
name|float
name|getMaximumCapacity
parameter_list|()
block|{
name|QueueInfoProtoOrBuilder
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
name|hasMaximumCapacity
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getMaximumCapacity
argument_list|()
else|:
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
name|QueueInfoProtoOrBuilder
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
name|hasQueueName
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getQueueName
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueState ()
specifier|public
name|QueueState
name|getQueueState
parameter_list|()
block|{
name|QueueInfoProtoOrBuilder
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
name|hasState
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setApplications (List<ApplicationReport> applications)
specifier|public
name|void
name|setApplications
parameter_list|(
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|applications
parameter_list|)
block|{
if|if
condition|(
name|applications
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearApplications
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|applicationsList
operator|=
name|applications
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCapacity (float capacity)
specifier|public
name|void
name|setCapacity
parameter_list|(
name|float
name|capacity
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setCapacity
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setChildQueues (List<QueueInfo> childQueues)
specifier|public
name|void
name|setChildQueues
parameter_list|(
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|childQueues
parameter_list|)
block|{
if|if
condition|(
name|childQueues
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearChildQueues
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|childQueuesList
operator|=
name|childQueues
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCurrentCapacity (float currentCapacity)
specifier|public
name|void
name|setCurrentCapacity
parameter_list|(
name|float
name|currentCapacity
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setCurrentCapacity
argument_list|(
name|currentCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setMaximumCapacity (float maximumCapacity)
specifier|public
name|void
name|setMaximumCapacity
parameter_list|(
name|float
name|maximumCapacity
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setMaximumCapacity
argument_list|(
name|maximumCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setQueueName (String queueName)
specifier|public
name|void
name|setQueueName
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|queueName
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearQueueName
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setQueueName
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setQueueState (QueueState queueState)
specifier|public
name|void
name|setQueueState
parameter_list|(
name|QueueState
name|queueState
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|queueState
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearState
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setState
argument_list|(
name|convertToProtoFormat
argument_list|(
name|queueState
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|QueueInfoProto
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
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
operator|.
name|getProto
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
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
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|getProto
argument_list|()
argument_list|)
return|;
block|}
DECL|method|initLocalApplicationsList ()
specifier|private
name|void
name|initLocalApplicationsList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationsList
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|QueueInfoProtoOrBuilder
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
name|ApplicationReportProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getApplicationsList
argument_list|()
decl_stmt|;
name|applicationsList
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationReport
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ApplicationReportProto
name|a
range|:
name|list
control|)
block|{
name|applicationsList
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addApplicationsToProto ()
specifier|private
name|void
name|addApplicationsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearApplications
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationsList
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|ApplicationReportProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|ApplicationReportProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ApplicationReportProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ApplicationReportProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ApplicationReport
argument_list|>
name|iter
init|=
name|applicationsList
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
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ApplicationReportProto
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
name|addAllApplications
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
DECL|method|initLocalChildQueuesList ()
specifier|private
name|void
name|initLocalChildQueuesList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|childQueuesList
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|QueueInfoProtoOrBuilder
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
name|QueueInfoProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getChildQueuesList
argument_list|()
decl_stmt|;
name|childQueuesList
operator|=
operator|new
name|ArrayList
argument_list|<
name|QueueInfo
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|QueueInfoProto
name|a
range|:
name|list
control|)
block|{
name|childQueuesList
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addChildQueuesInfoToProto ()
specifier|private
name|void
name|addChildQueuesInfoToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearChildQueues
argument_list|()
expr_stmt|;
if|if
condition|(
name|childQueuesList
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|QueueInfoProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|QueueInfoProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|QueueInfoProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|QueueInfoProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|QueueInfo
argument_list|>
name|iter
init|=
name|childQueuesList
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
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueueInfoProto
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
name|addAllChildQueues
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
DECL|method|addQueueConfigurations ()
specifier|private
name|void
name|addQueueConfigurations
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearQueueConfigurationsMap
argument_list|()
expr_stmt|;
if|if
condition|(
name|queueConfigurations
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|?
extends|extends
name|QueueConfigurationsMapProto
argument_list|>
name|values
init|=
operator|new
name|Iterable
argument_list|<
name|QueueConfigurationsMapProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|QueueConfigurationsMapProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|QueueConfigurationsMapProto
argument_list|>
argument_list|()
block|{
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|queueConfigurations
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
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueueConfigurationsMapProto
name|next
parameter_list|()
block|{
name|String
name|key
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|QueueConfigurationsMapProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPartitionName
argument_list|(
name|key
argument_list|)
operator|.
name|setQueueConfigurations
argument_list|(
name|convertToProtoFormat
argument_list|(
name|queueConfigurations
operator|.
name|get
argument_list|(
name|key
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
name|this
operator|.
name|builder
operator|.
name|addAllQueueConfigurationsMap
argument_list|(
name|values
argument_list|)
expr_stmt|;
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
name|childQueuesList
operator|!=
literal|null
condition|)
block|{
name|addChildQueuesInfoToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|applicationsList
operator|!=
literal|null
condition|)
block|{
name|addApplicationsToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|accessibleNodeLabels
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAccessibleNodeLabels
argument_list|()
expr_stmt|;
name|builder
operator|.
name|addAllAccessibleNodeLabels
argument_list|(
name|this
operator|.
name|accessibleNodeLabels
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|queueConfigurations
operator|!=
literal|null
condition|)
block|{
name|addQueueConfigurations
argument_list|()
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
name|QueueInfoProto
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
DECL|method|convertFromProtoFormat (ApplicationReportProto a)
specifier|private
name|ApplicationReportPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ApplicationReportProto
name|a
parameter_list|)
block|{
return|return
operator|new
name|ApplicationReportPBImpl
argument_list|(
name|a
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ApplicationReport t)
specifier|private
name|ApplicationReportProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationReport
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ApplicationReportPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (QueueInfoProto a)
specifier|private
name|QueueInfoPBImpl
name|convertFromProtoFormat
parameter_list|(
name|QueueInfoProto
name|a
parameter_list|)
block|{
return|return
operator|new
name|QueueInfoPBImpl
argument_list|(
name|a
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (QueueInfo q)
specifier|private
name|QueueInfoProto
name|convertToProtoFormat
parameter_list|(
name|QueueInfo
name|q
parameter_list|)
block|{
return|return
operator|(
operator|(
name|QueueInfoPBImpl
operator|)
name|q
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (QueueStateProto q)
specifier|private
name|QueueState
name|convertFromProtoFormat
parameter_list|(
name|QueueStateProto
name|q
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|q
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (QueueState queueState)
specifier|private
name|QueueStateProto
name|convertToProtoFormat
parameter_list|(
name|QueueState
name|queueState
parameter_list|)
block|{
return|return
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|queueState
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat ( QueueConfigurationsProto q)
specifier|private
name|QueueConfigurationsPBImpl
name|convertFromProtoFormat
parameter_list|(
name|QueueConfigurationsProto
name|q
parameter_list|)
block|{
return|return
operator|new
name|QueueConfigurationsPBImpl
argument_list|(
name|q
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat ( QueueConfigurations q)
specifier|private
name|QueueConfigurationsProto
name|convertToProtoFormat
parameter_list|(
name|QueueConfigurations
name|q
parameter_list|)
block|{
return|return
operator|(
operator|(
name|QueueConfigurationsPBImpl
operator|)
name|q
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setAccessibleNodeLabels (Set<String> nodeLabels)
specifier|public
name|void
name|setAccessibleNodeLabels
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodeLabels
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearAccessibleNodeLabels
argument_list|()
expr_stmt|;
name|this
operator|.
name|accessibleNodeLabels
operator|=
name|nodeLabels
expr_stmt|;
block|}
DECL|method|initNodeLabels ()
specifier|private
name|void
name|initNodeLabels
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|accessibleNodeLabels
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|QueueInfoProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|this
operator|.
name|accessibleNodeLabels
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|accessibleNodeLabels
operator|.
name|addAll
argument_list|(
name|p
operator|.
name|getAccessibleNodeLabelsList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAccessibleNodeLabels ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAccessibleNodeLabels
parameter_list|()
block|{
name|initNodeLabels
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|accessibleNodeLabels
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultNodeLabelExpression ()
specifier|public
name|String
name|getDefaultNodeLabelExpression
parameter_list|()
block|{
name|QueueInfoProtoOrBuilder
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
name|hasDefaultNodeLabelExpression
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getDefaultNodeLabelExpression
argument_list|()
operator|.
name|trim
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setDefaultNodeLabelExpression (String defaultNodeLabelExpression)
specifier|public
name|void
name|setDefaultNodeLabelExpression
parameter_list|(
name|String
name|defaultNodeLabelExpression
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|defaultNodeLabelExpression
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearDefaultNodeLabelExpression
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setDefaultNodeLabelExpression
argument_list|(
name|defaultNodeLabelExpression
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (QueueStatisticsProto q)
specifier|private
name|QueueStatistics
name|convertFromProtoFormat
parameter_list|(
name|QueueStatisticsProto
name|q
parameter_list|)
block|{
return|return
operator|new
name|QueueStatisticsPBImpl
argument_list|(
name|q
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (QueueStatistics q)
specifier|private
name|QueueStatisticsProto
name|convertToProtoFormat
parameter_list|(
name|QueueStatistics
name|q
parameter_list|)
block|{
return|return
operator|(
operator|(
name|QueueStatisticsPBImpl
operator|)
name|q
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueStatistics ()
specifier|public
name|QueueStatistics
name|getQueueStatistics
parameter_list|()
block|{
name|QueueInfoProtoOrBuilder
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
name|hasQueueStatistics
argument_list|()
operator|)
condition|?
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getQueueStatistics
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setQueueStatistics (QueueStatistics queueStatistics)
specifier|public
name|void
name|setQueueStatistics
parameter_list|(
name|QueueStatistics
name|queueStatistics
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|queueStatistics
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearQueueStatistics
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setQueueStatistics
argument_list|(
name|convertToProtoFormat
argument_list|(
name|queueStatistics
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPreemptionDisabled ()
specifier|public
name|Boolean
name|getPreemptionDisabled
parameter_list|()
block|{
name|QueueInfoProtoOrBuilder
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
name|hasPreemptionDisabled
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getPreemptionDisabled
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setPreemptionDisabled (boolean preemptionDisabled)
specifier|public
name|void
name|setPreemptionDisabled
parameter_list|(
name|boolean
name|preemptionDisabled
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setPreemptionDisabled
argument_list|(
name|preemptionDisabled
argument_list|)
expr_stmt|;
block|}
DECL|method|initQueueConfigurations ()
specifier|private
name|void
name|initQueueConfigurations
parameter_list|()
block|{
if|if
condition|(
name|queueConfigurations
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|QueueInfoProtoOrBuilder
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
name|QueueConfigurationsMapProto
argument_list|>
name|lists
init|=
name|p
operator|.
name|getQueueConfigurationsMapList
argument_list|()
decl_stmt|;
name|queueConfigurations
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|QueueConfigurations
argument_list|>
argument_list|(
name|lists
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|QueueConfigurationsMapProto
name|queueConfigurationsProto
range|:
name|lists
control|)
block|{
name|queueConfigurations
operator|.
name|put
argument_list|(
name|queueConfigurationsProto
operator|.
name|getPartitionName
argument_list|()
argument_list|,
name|convertFromProtoFormat
argument_list|(
name|queueConfigurationsProto
operator|.
name|getQueueConfigurations
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getQueueConfigurations ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|QueueConfigurations
argument_list|>
name|getQueueConfigurations
parameter_list|()
block|{
name|initQueueConfigurations
argument_list|()
expr_stmt|;
return|return
name|queueConfigurations
return|;
block|}
annotation|@
name|Override
DECL|method|setQueueConfigurations ( Map<String, QueueConfigurations> queueConfigurations)
specifier|public
name|void
name|setQueueConfigurations
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|QueueConfigurations
argument_list|>
name|queueConfigurations
parameter_list|)
block|{
if|if
condition|(
name|queueConfigurations
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|initQueueConfigurations
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueConfigurations
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueConfigurations
operator|.
name|putAll
argument_list|(
name|queueConfigurations
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getIntraQueuePreemptionDisabled ()
specifier|public
name|Boolean
name|getIntraQueuePreemptionDisabled
parameter_list|()
block|{
name|QueueInfoProtoOrBuilder
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
name|hasIntraQueuePreemptionDisabled
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getIntraQueuePreemptionDisabled
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setIntraQueuePreemptionDisabled ( boolean intraQueuePreemptionDisabled)
specifier|public
name|void
name|setIntraQueuePreemptionDisabled
parameter_list|(
name|boolean
name|intraQueuePreemptionDisabled
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setIntraQueuePreemptionDisabled
argument_list|(
name|intraQueuePreemptionDisabled
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

