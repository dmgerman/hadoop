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
name|ApplicationResourceUsageReport
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
name|ApplicationResourceUsageReportProto
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
name|ApplicationResourceUsageReportProtoOrBuilder
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
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ApplicationResourceUsageReportPBImpl
specifier|public
class|class
name|ApplicationResourceUsageReportPBImpl
extends|extends
name|ApplicationResourceUsageReport
block|{
DECL|field|proto
name|ApplicationResourceUsageReportProto
name|proto
init|=
name|ApplicationResourceUsageReportProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ApplicationResourceUsageReportProto
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
DECL|field|usedResources
name|Resource
name|usedResources
decl_stmt|;
DECL|field|reservedResources
name|Resource
name|reservedResources
decl_stmt|;
DECL|field|neededResources
name|Resource
name|neededResources
decl_stmt|;
DECL|method|ApplicationResourceUsageReportPBImpl ()
specifier|public
name|ApplicationResourceUsageReportPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ApplicationResourceUsageReportProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ApplicationResourceUsageReportPBImpl ( ApplicationResourceUsageReportProto proto)
specifier|public
name|ApplicationResourceUsageReportPBImpl
parameter_list|(
name|ApplicationResourceUsageReportProto
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
name|ApplicationResourceUsageReportProto
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
name|getProto
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|", "
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
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
name|usedResources
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|this
operator|.
name|usedResources
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getUsedResources
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setUsedResources
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|usedResources
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|reservedResources
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|this
operator|.
name|reservedResources
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getReservedResources
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setReservedResources
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|reservedResources
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|neededResources
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|this
operator|.
name|neededResources
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getNeededResources
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setNeededResources
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|neededResources
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
name|ApplicationResourceUsageReportProto
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
DECL|method|getNumUsedContainers ()
specifier|public
specifier|synchronized
name|int
name|getNumUsedContainers
parameter_list|()
block|{
name|ApplicationResourceUsageReportProtoOrBuilder
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
name|getNumUsedContainers
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNumUsedContainers (int num_containers)
specifier|public
specifier|synchronized
name|void
name|setNumUsedContainers
parameter_list|(
name|int
name|num_containers
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setNumUsedContainers
argument_list|(
operator|(
name|num_containers
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumReservedContainers ()
specifier|public
specifier|synchronized
name|int
name|getNumReservedContainers
parameter_list|()
block|{
name|ApplicationResourceUsageReportProtoOrBuilder
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
name|getNumReservedContainers
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNumReservedContainers ( int num_reserved_containers)
specifier|public
specifier|synchronized
name|void
name|setNumReservedContainers
parameter_list|(
name|int
name|num_reserved_containers
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setNumReservedContainers
argument_list|(
operator|(
name|num_reserved_containers
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUsedResources ()
specifier|public
specifier|synchronized
name|Resource
name|getUsedResources
parameter_list|()
block|{
name|ApplicationResourceUsageReportProtoOrBuilder
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
name|usedResources
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|usedResources
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasUsedResources
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|usedResources
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getUsedResources
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|usedResources
return|;
block|}
annotation|@
name|Override
DECL|method|setUsedResources (Resource resources)
specifier|public
specifier|synchronized
name|void
name|setUsedResources
parameter_list|(
name|Resource
name|resources
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|resources
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearUsedResources
argument_list|()
expr_stmt|;
name|this
operator|.
name|usedResources
operator|=
name|resources
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReservedResources ()
specifier|public
specifier|synchronized
name|Resource
name|getReservedResources
parameter_list|()
block|{
name|ApplicationResourceUsageReportProtoOrBuilder
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
name|reservedResources
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|reservedResources
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasReservedResources
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|reservedResources
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getReservedResources
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|reservedResources
return|;
block|}
annotation|@
name|Override
DECL|method|setReservedResources (Resource reserved_resources)
specifier|public
specifier|synchronized
name|void
name|setReservedResources
parameter_list|(
name|Resource
name|reserved_resources
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|reserved_resources
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearReservedResources
argument_list|()
expr_stmt|;
name|this
operator|.
name|reservedResources
operator|=
name|reserved_resources
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNeededResources ()
specifier|public
specifier|synchronized
name|Resource
name|getNeededResources
parameter_list|()
block|{
name|ApplicationResourceUsageReportProtoOrBuilder
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
name|neededResources
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|neededResources
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasNeededResources
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|neededResources
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getNeededResources
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|neededResources
return|;
block|}
annotation|@
name|Override
DECL|method|setNeededResources (Resource reserved_resources)
specifier|public
specifier|synchronized
name|void
name|setNeededResources
parameter_list|(
name|Resource
name|reserved_resources
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|reserved_resources
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearNeededResources
argument_list|()
expr_stmt|;
name|this
operator|.
name|neededResources
operator|=
name|reserved_resources
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ResourceProto p)
specifier|private
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
DECL|method|convertToProtoFormat (Resource t)
specifier|private
name|ResourceProto
name|convertToProtoFormat
parameter_list|(
name|Resource
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ResourcePBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
block|}
end_class

end_unit

