begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords.impl.pb
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
name|protocolrecords
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
name|GetContainersResponse
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
name|ContainerReport
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
name|ContainerReportPBImpl
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
name|ContainerReportProto
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
name|YarnServiceProtos
operator|.
name|GetContainersResponseProto
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
name|YarnServiceProtos
operator|.
name|GetContainersResponseProtoOrBuilder
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
DECL|class|GetContainersResponsePBImpl
specifier|public
class|class
name|GetContainersResponsePBImpl
extends|extends
name|GetContainersResponse
block|{
DECL|field|proto
name|GetContainersResponseProto
name|proto
init|=
name|GetContainersResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetContainersResponseProto
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
DECL|field|containerList
name|List
argument_list|<
name|ContainerReport
argument_list|>
name|containerList
decl_stmt|;
DECL|method|GetContainersResponsePBImpl ()
specifier|public
name|GetContainersResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetContainersResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetContainersResponsePBImpl (GetContainersResponseProto proto)
specifier|public
name|GetContainersResponsePBImpl
parameter_list|(
name|GetContainersResponseProto
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
DECL|method|getContainerList ()
specifier|public
name|List
argument_list|<
name|ContainerReport
argument_list|>
name|getContainerList
parameter_list|()
block|{
name|initLocalContainerList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|containerList
return|;
block|}
annotation|@
name|Override
DECL|method|setContainerList (List<ContainerReport> containers)
specifier|public
name|void
name|setContainerList
parameter_list|(
name|List
argument_list|<
name|ContainerReport
argument_list|>
name|containers
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|containers
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearContainers
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|containerList
operator|=
name|containers
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|GetContainersResponseProto
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
block|{
return|return
literal|false
return|;
block|}
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
name|containerList
operator|!=
literal|null
condition|)
block|{
name|addLocalContainersToProto
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
name|GetContainersResponseProto
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
comment|// Once this is called. containerList will never be null - until a getProto
comment|// is called.
DECL|method|initLocalContainerList ()
specifier|private
name|void
name|initLocalContainerList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|containerList
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetContainersResponseProtoOrBuilder
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
name|ContainerReportProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getContainersList
argument_list|()
decl_stmt|;
name|containerList
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContainerReport
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ContainerReportProto
name|c
range|:
name|list
control|)
block|{
name|containerList
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
DECL|method|addLocalContainersToProto ()
specifier|private
name|void
name|addLocalContainersToProto
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
if|if
condition|(
name|containerList
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|ContainerReportProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|ContainerReportProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ContainerReportProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ContainerReportProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ContainerReport
argument_list|>
name|iter
init|=
name|containerList
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
name|ContainerReportProto
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
name|addAllContainers
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ContainerReportProto p)
specifier|private
name|ContainerReportPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ContainerReportProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ContainerReportPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ContainerReport t)
specifier|private
name|ContainerReportProto
name|convertToProtoFormat
parameter_list|(
name|ContainerReport
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ContainerReportPBImpl
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

