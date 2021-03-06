begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb
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
name|protocolrecords
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
name|DecommissionType
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
name|YarnServerResourceManagerServiceProtos
operator|.
name|DecommissionTypeProto
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
name|YarnServerResourceManagerServiceProtos
operator|.
name|RefreshNodesRequestProto
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
name|YarnServerResourceManagerServiceProtos
operator|.
name|RefreshNodesRequestProtoOrBuilder
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
name|protocolrecords
operator|.
name|RefreshNodesRequest
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
DECL|class|RefreshNodesRequestPBImpl
specifier|public
class|class
name|RefreshNodesRequestPBImpl
extends|extends
name|RefreshNodesRequest
block|{
DECL|field|proto
name|RefreshNodesRequestProto
name|proto
init|=
name|RefreshNodesRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|RefreshNodesRequestProto
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
DECL|field|decommissionType
specifier|private
name|DecommissionType
name|decommissionType
decl_stmt|;
DECL|method|RefreshNodesRequestPBImpl ()
specifier|public
name|RefreshNodesRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|RefreshNodesRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|RefreshNodesRequestPBImpl (RefreshNodesRequestProto proto)
specifier|public
name|RefreshNodesRequestPBImpl
parameter_list|(
name|RefreshNodesRequestProto
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
name|RefreshNodesRequestProto
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
name|decommissionType
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setDecommissionType
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|decommissionType
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|RefreshNodesRequestProto
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
annotation|@
name|Override
DECL|method|setDecommissionType ( DecommissionType decommissionType)
specifier|public
specifier|synchronized
name|void
name|setDecommissionType
parameter_list|(
name|DecommissionType
name|decommissionType
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|this
operator|.
name|decommissionType
operator|=
name|decommissionType
expr_stmt|;
name|mergeLocalToBuilder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDecommissionType ()
specifier|public
specifier|synchronized
name|DecommissionType
name|getDecommissionType
parameter_list|()
block|{
name|RefreshNodesRequestProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getDecommissionType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDecommissionTimeout (Integer timeout)
specifier|public
specifier|synchronized
name|void
name|setDecommissionTimeout
parameter_list|(
name|Integer
name|timeout
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|timeout
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setDecommissionTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|clearDecommissionTimeout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDecommissionTimeout ()
specifier|public
specifier|synchronized
name|Integer
name|getDecommissionTimeout
parameter_list|()
block|{
name|RefreshNodesRequestProtoOrBuilder
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
name|hasDecommissionTimeout
argument_list|()
condition|?
name|p
operator|.
name|getDecommissionTimeout
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|convertFromProtoFormat (DecommissionTypeProto p)
specifier|private
name|DecommissionType
name|convertFromProtoFormat
parameter_list|(
name|DecommissionTypeProto
name|p
parameter_list|)
block|{
return|return
name|DecommissionType
operator|.
name|valueOf
argument_list|(
name|p
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (DecommissionType t)
specifier|private
name|DecommissionTypeProto
name|convertToProtoFormat
parameter_list|(
name|DecommissionType
name|t
parameter_list|)
block|{
return|return
name|DecommissionTypeProto
operator|.
name|valueOf
argument_list|(
name|t
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

