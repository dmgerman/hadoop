begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
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
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_comment
comment|/**  * This class represents queue capacities for a given partition  */
end_comment

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|PartitionQueueCapacitiesInfo
specifier|public
class|class
name|PartitionQueueCapacitiesInfo
block|{
DECL|field|partitionName
specifier|private
name|String
name|partitionName
decl_stmt|;
DECL|field|capacity
specifier|private
name|float
name|capacity
decl_stmt|;
DECL|field|usedCapacity
specifier|private
name|float
name|usedCapacity
decl_stmt|;
DECL|field|maxCapacity
specifier|private
name|float
name|maxCapacity
init|=
literal|100
decl_stmt|;
DECL|field|absoluteCapacity
specifier|private
name|float
name|absoluteCapacity
decl_stmt|;
DECL|field|absoluteUsedCapacity
specifier|private
name|float
name|absoluteUsedCapacity
decl_stmt|;
DECL|field|absoluteMaxCapacity
specifier|private
name|float
name|absoluteMaxCapacity
init|=
literal|100
decl_stmt|;
DECL|field|maxAMLimitPercentage
specifier|private
name|float
name|maxAMLimitPercentage
decl_stmt|;
DECL|field|configuredMinResource
specifier|private
name|ResourceInfo
name|configuredMinResource
decl_stmt|;
DECL|field|configuredMaxResource
specifier|private
name|ResourceInfo
name|configuredMaxResource
decl_stmt|;
DECL|field|effectiveMinResource
specifier|private
name|ResourceInfo
name|effectiveMinResource
decl_stmt|;
DECL|field|effectiveMaxResource
specifier|private
name|ResourceInfo
name|effectiveMaxResource
decl_stmt|;
DECL|method|PartitionQueueCapacitiesInfo ()
specifier|public
name|PartitionQueueCapacitiesInfo
parameter_list|()
block|{   }
DECL|method|PartitionQueueCapacitiesInfo (String partitionName, float capacity, float usedCapacity, float maxCapacity, float absCapacity, float absUsedCapacity, float absMaxCapacity, float maxAMLimitPercentage, Resource confMinRes, Resource confMaxRes, Resource effMinRes, Resource effMaxRes)
specifier|public
name|PartitionQueueCapacitiesInfo
parameter_list|(
name|String
name|partitionName
parameter_list|,
name|float
name|capacity
parameter_list|,
name|float
name|usedCapacity
parameter_list|,
name|float
name|maxCapacity
parameter_list|,
name|float
name|absCapacity
parameter_list|,
name|float
name|absUsedCapacity
parameter_list|,
name|float
name|absMaxCapacity
parameter_list|,
name|float
name|maxAMLimitPercentage
parameter_list|,
name|Resource
name|confMinRes
parameter_list|,
name|Resource
name|confMaxRes
parameter_list|,
name|Resource
name|effMinRes
parameter_list|,
name|Resource
name|effMaxRes
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|partitionName
operator|=
name|partitionName
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|usedCapacity
operator|=
name|usedCapacity
expr_stmt|;
name|this
operator|.
name|maxCapacity
operator|=
name|maxCapacity
expr_stmt|;
name|this
operator|.
name|absoluteCapacity
operator|=
name|absCapacity
expr_stmt|;
name|this
operator|.
name|absoluteUsedCapacity
operator|=
name|absUsedCapacity
expr_stmt|;
name|this
operator|.
name|absoluteMaxCapacity
operator|=
name|absMaxCapacity
expr_stmt|;
name|this
operator|.
name|maxAMLimitPercentage
operator|=
name|maxAMLimitPercentage
expr_stmt|;
name|this
operator|.
name|configuredMinResource
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|confMinRes
argument_list|)
expr_stmt|;
name|this
operator|.
name|configuredMaxResource
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|confMaxRes
argument_list|)
expr_stmt|;
name|this
operator|.
name|effectiveMinResource
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|effMinRes
argument_list|)
expr_stmt|;
name|this
operator|.
name|effectiveMaxResource
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|effMaxRes
argument_list|)
expr_stmt|;
block|}
DECL|method|getCapacity ()
specifier|public
name|float
name|getCapacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
DECL|method|setCapacity (float capacity)
specifier|public
name|void
name|setCapacity
parameter_list|(
name|float
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
block|}
DECL|method|getUsedCapacity ()
specifier|public
name|float
name|getUsedCapacity
parameter_list|()
block|{
return|return
name|usedCapacity
return|;
block|}
DECL|method|setUsedCapacity (float usedCapacity)
specifier|public
name|void
name|setUsedCapacity
parameter_list|(
name|float
name|usedCapacity
parameter_list|)
block|{
name|this
operator|.
name|usedCapacity
operator|=
name|usedCapacity
expr_stmt|;
block|}
DECL|method|getMaxCapacity ()
specifier|public
name|float
name|getMaxCapacity
parameter_list|()
block|{
return|return
name|maxCapacity
return|;
block|}
DECL|method|setMaxCapacity (float maxCapacity)
specifier|public
name|void
name|setMaxCapacity
parameter_list|(
name|float
name|maxCapacity
parameter_list|)
block|{
name|this
operator|.
name|maxCapacity
operator|=
name|maxCapacity
expr_stmt|;
block|}
DECL|method|getPartitionName ()
specifier|public
name|String
name|getPartitionName
parameter_list|()
block|{
return|return
name|partitionName
return|;
block|}
DECL|method|setPartitionName (String partitionName)
specifier|public
name|void
name|setPartitionName
parameter_list|(
name|String
name|partitionName
parameter_list|)
block|{
name|this
operator|.
name|partitionName
operator|=
name|partitionName
expr_stmt|;
block|}
DECL|method|getAbsoluteCapacity ()
specifier|public
name|float
name|getAbsoluteCapacity
parameter_list|()
block|{
return|return
name|absoluteCapacity
return|;
block|}
DECL|method|setAbsoluteCapacity (float absoluteCapacity)
specifier|public
name|void
name|setAbsoluteCapacity
parameter_list|(
name|float
name|absoluteCapacity
parameter_list|)
block|{
name|this
operator|.
name|absoluteCapacity
operator|=
name|absoluteCapacity
expr_stmt|;
block|}
DECL|method|getAbsoluteUsedCapacity ()
specifier|public
name|float
name|getAbsoluteUsedCapacity
parameter_list|()
block|{
return|return
name|absoluteUsedCapacity
return|;
block|}
DECL|method|setAbsoluteUsedCapacity (float absoluteUsedCapacity)
specifier|public
name|void
name|setAbsoluteUsedCapacity
parameter_list|(
name|float
name|absoluteUsedCapacity
parameter_list|)
block|{
name|this
operator|.
name|absoluteUsedCapacity
operator|=
name|absoluteUsedCapacity
expr_stmt|;
block|}
DECL|method|getAbsoluteMaxCapacity ()
specifier|public
name|float
name|getAbsoluteMaxCapacity
parameter_list|()
block|{
return|return
name|absoluteMaxCapacity
return|;
block|}
DECL|method|setAbsoluteMaxCapacity (float absoluteMaxCapacity)
specifier|public
name|void
name|setAbsoluteMaxCapacity
parameter_list|(
name|float
name|absoluteMaxCapacity
parameter_list|)
block|{
name|this
operator|.
name|absoluteMaxCapacity
operator|=
name|absoluteMaxCapacity
expr_stmt|;
block|}
DECL|method|getMaxAMLimitPercentage ()
specifier|public
name|float
name|getMaxAMLimitPercentage
parameter_list|()
block|{
return|return
name|maxAMLimitPercentage
return|;
block|}
DECL|method|setMaxAMLimitPercentage (float maxAMLimitPercentage)
specifier|public
name|void
name|setMaxAMLimitPercentage
parameter_list|(
name|float
name|maxAMLimitPercentage
parameter_list|)
block|{
name|this
operator|.
name|maxAMLimitPercentage
operator|=
name|maxAMLimitPercentage
expr_stmt|;
block|}
DECL|method|getConfiguredMinResource ()
specifier|public
name|ResourceInfo
name|getConfiguredMinResource
parameter_list|()
block|{
return|return
name|configuredMinResource
return|;
block|}
DECL|method|getConfiguredMaxResource ()
specifier|public
name|ResourceInfo
name|getConfiguredMaxResource
parameter_list|()
block|{
if|if
condition|(
name|configuredMaxResource
operator|==
literal|null
operator|||
name|configuredMaxResource
operator|.
name|getResource
argument_list|()
operator|.
name|equals
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|configuredMaxResource
return|;
block|}
DECL|method|getEffectiveMinResource ()
specifier|public
name|ResourceInfo
name|getEffectiveMinResource
parameter_list|()
block|{
return|return
name|effectiveMinResource
return|;
block|}
DECL|method|getEffectiveMaxResource ()
specifier|public
name|ResourceInfo
name|getEffectiveMaxResource
parameter_list|()
block|{
return|return
name|effectiveMaxResource
return|;
block|}
block|}
end_class

end_unit

