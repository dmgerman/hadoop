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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|protocolrecords
operator|.
name|ResourceTypes
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
name|api
operator|.
name|records
operator|.
name|ResourceInformation
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
name|exceptions
operator|.
name|ResourceNotFoundException
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
name|ResourceProtoOrBuilder
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
name|ResourceInformationProto
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
name|UnitsConversionUtil
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
name|ResourceUtils
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ResourcePBImpl
specifier|public
class|class
name|ResourcePBImpl
extends|extends
name|Resource
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ResourcePBImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|proto
name|ResourceProto
name|proto
init|=
name|ResourceProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ResourceProto
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
comment|// call via ProtoUtils.convertToProtoFormat(Resource)
DECL|method|getProto (Resource r)
specifier|static
name|ResourceProto
name|getProto
parameter_list|(
name|Resource
name|r
parameter_list|)
block|{
specifier|final
name|ResourcePBImpl
name|pb
decl_stmt|;
if|if
condition|(
name|r
operator|instanceof
name|ResourcePBImpl
condition|)
block|{
name|pb
operator|=
operator|(
name|ResourcePBImpl
operator|)
name|r
expr_stmt|;
block|}
else|else
block|{
name|pb
operator|=
operator|new
name|ResourcePBImpl
argument_list|()
expr_stmt|;
name|pb
operator|.
name|setMemorySize
argument_list|(
name|r
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|pb
operator|.
name|setVirtualCores
argument_list|(
name|r
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ResourceInformation
name|res
range|:
name|r
operator|.
name|getResources
argument_list|()
control|)
block|{
name|pb
operator|.
name|setResourceInformation
argument_list|(
name|res
operator|.
name|getName
argument_list|()
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pb
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|ResourcePBImpl ()
specifier|public
name|ResourcePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ResourceProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
name|initResources
argument_list|()
expr_stmt|;
block|}
DECL|method|ResourcePBImpl (ResourceProto proto)
specifier|public
name|ResourcePBImpl
parameter_list|(
name|ResourceProto
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
name|initResources
argument_list|()
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|ResourceProto
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
name|ResourceProto
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|getMemory ()
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
return|return
name|castToIntSafely
argument_list|(
name|this
operator|.
name|getMemorySize
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMemorySize ()
specifier|public
name|long
name|getMemorySize
parameter_list|()
block|{
comment|// memory should always be present
name|ResourceInformation
name|ri
init|=
name|resources
index|[
name|MEMORY_INDEX
index|]
decl_stmt|;
if|if
condition|(
name|ri
operator|.
name|getUnits
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ri
operator|.
name|getValue
argument_list|()
return|;
block|}
return|return
name|UnitsConversionUtil
operator|.
name|convert
argument_list|(
name|ri
operator|.
name|getUnits
argument_list|()
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getUnits
argument_list|()
argument_list|,
name|ri
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|setMemory (int memory)
specifier|public
name|void
name|setMemory
parameter_list|(
name|int
name|memory
parameter_list|)
block|{
name|setMemorySize
argument_list|(
name|memory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setMemorySize (long memory)
specifier|public
name|void
name|setMemorySize
parameter_list|(
name|long
name|memory
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|resources
index|[
name|MEMORY_INDEX
index|]
operator|.
name|setValue
argument_list|(
name|memory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getVirtualCores ()
specifier|public
name|int
name|getVirtualCores
parameter_list|()
block|{
comment|// vcores should always be present
return|return
name|castToIntSafely
argument_list|(
name|resources
index|[
name|VCORES_INDEX
index|]
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setVirtualCores (int vCores)
specifier|public
name|void
name|setVirtualCores
parameter_list|(
name|int
name|vCores
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|resources
index|[
name|VCORES_INDEX
index|]
operator|.
name|setValue
argument_list|(
name|vCores
argument_list|)
expr_stmt|;
block|}
DECL|method|initResources ()
specifier|private
name|void
name|initResources
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|resources
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|ResourceProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|ResourceInformation
index|[]
name|types
init|=
name|ResourceUtils
operator|.
name|getResourceTypesArray
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|indexMap
init|=
name|ResourceUtils
operator|.
name|getResourceTypeIndex
argument_list|()
decl_stmt|;
name|resources
operator|=
operator|new
name|ResourceInformation
index|[
name|types
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|ResourceInformationProto
name|entry
range|:
name|p
operator|.
name|getResourceValueMapList
argument_list|()
control|)
block|{
name|Integer
name|index
init|=
name|indexMap
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Got unknown resource type: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"; skipping"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resources
index|[
name|index
index|]
operator|=
name|newDefaultInformation
argument_list|(
name|types
index|[
name|index
index|]
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
name|resources
index|[
name|MEMORY_INDEX
index|]
operator|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
argument_list|)
expr_stmt|;
name|resources
index|[
name|VCORES_INDEX
index|]
operator|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
argument_list|)
expr_stmt|;
name|this
operator|.
name|setMemorySize
argument_list|(
name|p
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setVirtualCores
argument_list|(
name|p
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|newDefaultInformation ( ResourceInformation resourceInformation, ResourceInformationProto entry)
specifier|private
specifier|static
name|ResourceInformation
name|newDefaultInformation
parameter_list|(
name|ResourceInformation
name|resourceInformation
parameter_list|,
name|ResourceInformationProto
name|entry
parameter_list|)
block|{
name|ResourceInformation
name|ri
init|=
operator|new
name|ResourceInformation
argument_list|()
decl_stmt|;
name|ri
operator|.
name|setName
argument_list|(
name|resourceInformation
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ri
operator|.
name|setMinimumAllocation
argument_list|(
name|resourceInformation
operator|.
name|getMinimumAllocation
argument_list|()
argument_list|)
expr_stmt|;
name|ri
operator|.
name|setMaximumAllocation
argument_list|(
name|resourceInformation
operator|.
name|getMaximumAllocation
argument_list|()
argument_list|)
expr_stmt|;
name|ri
operator|.
name|setResourceType
argument_list|(
name|entry
operator|.
name|hasType
argument_list|()
condition|?
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|entry
operator|.
name|getType
argument_list|()
argument_list|)
else|:
name|ResourceTypes
operator|.
name|COUNTABLE
argument_list|)
expr_stmt|;
name|ri
operator|.
name|setUnits
argument_list|(
name|entry
operator|.
name|hasUnits
argument_list|()
condition|?
name|entry
operator|.
name|getUnits
argument_list|()
else|:
name|resourceInformation
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
name|ri
operator|.
name|setValue
argument_list|(
name|entry
operator|.
name|hasValue
argument_list|()
condition|?
name|entry
operator|.
name|getValue
argument_list|()
else|:
literal|0L
argument_list|)
expr_stmt|;
return|return
name|ri
return|;
block|}
annotation|@
name|Override
DECL|method|setResourceInformation (String resource, ResourceInformation resourceInformation)
specifier|public
name|void
name|setResourceInformation
parameter_list|(
name|String
name|resource
parameter_list|,
name|ResourceInformation
name|resourceInformation
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
operator|||
name|resourceInformation
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"resource and/or resourceInformation cannot be null"
argument_list|)
throw|;
block|}
name|ResourceInformation
name|storedResourceInfo
init|=
name|super
operator|.
name|getResourceInformation
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|ResourceInformation
operator|.
name|copy
argument_list|(
name|resourceInformation
argument_list|,
name|storedResourceInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setResourceValue (String resource, long value)
specifier|public
name|void
name|setResourceValue
parameter_list|(
name|String
name|resource
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|ResourceNotFoundException
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"resource type object cannot be null"
argument_list|)
throw|;
block|}
name|getResourceInformation
argument_list|(
name|resource
argument_list|)
operator|.
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResourceInformation (String resource)
specifier|public
name|ResourceInformation
name|getResourceInformation
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|ResourceNotFoundException
block|{
return|return
name|super
operator|.
name|getResourceInformation
argument_list|(
name|resource
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getResourceValue (String resource)
specifier|public
name|long
name|getResourceValue
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|ResourceNotFoundException
block|{
return|return
name|super
operator|.
name|getResourceValue
argument_list|(
name|resource
argument_list|)
return|;
block|}
DECL|method|mergeLocalToBuilder ()
specifier|synchronized
specifier|private
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
name|builder
operator|.
name|clearResourceValueMap
argument_list|()
expr_stmt|;
if|if
condition|(
name|resources
operator|!=
literal|null
operator|&&
name|resources
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|ResourceInformation
name|resInfo
range|:
name|resources
control|)
block|{
name|ResourceInformationProto
operator|.
name|Builder
name|e
init|=
name|ResourceInformationProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|e
operator|.
name|setKey
argument_list|(
name|resInfo
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|setUnits
argument_list|(
name|resInfo
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|setType
argument_list|(
name|ProtoUtils
operator|.
name|converToProtoFormat
argument_list|(
name|resInfo
operator|.
name|getResourceType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|setValue
argument_list|(
name|resInfo
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addResourceValueMap
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|setMemory
argument_list|(
name|this
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setVirtualCores
argument_list|(
name|this
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
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
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
block|}
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
block|}
end_class

end_unit

