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
name|NodeState
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
name|NodeReportProtoOrBuilder
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
name|util
operator|.
name|ProtoUtils
import|;
end_import

begin_class
DECL|class|NodeReportPBImpl
specifier|public
class|class
name|NodeReportPBImpl
extends|extends
name|NodeReport
block|{
DECL|field|proto
specifier|private
name|NodeReportProto
name|proto
init|=
name|NodeReportProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|NodeReportProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
specifier|private
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|used
specifier|private
name|Resource
name|used
decl_stmt|;
DECL|field|capability
specifier|private
name|Resource
name|capability
decl_stmt|;
DECL|method|NodeReportPBImpl ()
specifier|public
name|NodeReportPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|NodeReportProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|NodeReportPBImpl (NodeReportProto proto)
specifier|public
name|NodeReportPBImpl
parameter_list|(
name|NodeReportProto
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
DECL|method|getCapability ()
specifier|public
name|Resource
name|getCapability
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|capability
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|capability
return|;
block|}
name|NodeReportProtoOrBuilder
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
name|hasCapability
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|capability
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|capability
return|;
block|}
annotation|@
name|Override
DECL|method|getHealthReport ()
specifier|public
name|String
name|getHealthReport
parameter_list|()
block|{
name|NodeReportProtoOrBuilder
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
name|getHealthReport
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setHealthReport (String healthReport)
specifier|public
name|void
name|setHealthReport
parameter_list|(
name|String
name|healthReport
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|healthReport
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearHealthReport
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setHealthReport
argument_list|(
name|healthReport
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLastHealthReportTime ()
specifier|public
name|long
name|getLastHealthReportTime
parameter_list|()
block|{
name|NodeReportProtoOrBuilder
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
name|getLastHealthReportTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setLastHealthReportTime (long lastHealthReportTime)
specifier|public
name|void
name|setLastHealthReportTime
parameter_list|(
name|long
name|lastHealthReportTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setLastHealthReportTime
argument_list|(
name|lastHealthReportTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getHttpAddress ()
specifier|public
name|String
name|getHttpAddress
parameter_list|()
block|{
name|NodeReportProtoOrBuilder
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
name|hasHttpAddress
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getHttpAddress
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getNumContainers ()
specifier|public
name|int
name|getNumContainers
parameter_list|()
block|{
name|NodeReportProtoOrBuilder
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
name|hasNumContainers
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getNumContainers
argument_list|()
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getRackName ()
specifier|public
name|String
name|getRackName
parameter_list|()
block|{
name|NodeReportProtoOrBuilder
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
name|hasRackName
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getRackName
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getUsed ()
specifier|public
name|Resource
name|getUsed
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|used
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|used
return|;
block|}
name|NodeReportProtoOrBuilder
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
name|hasUsed
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|used
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getUsed
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|used
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeId ()
specifier|public
name|NodeId
name|getNodeId
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
return|return
name|this
operator|.
name|nodeId
return|;
block|}
name|NodeReportProtoOrBuilder
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
DECL|method|getNodeState ()
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
name|NodeReportProtoOrBuilder
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
name|hasNodeState
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeState (NodeState nodeState)
specifier|public
name|void
name|setNodeState
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeState
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeState
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setNodeState
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|nodeState
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCapability (Resource capability)
specifier|public
name|void
name|setCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|capability
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearCapability
argument_list|()
expr_stmt|;
name|this
operator|.
name|capability
operator|=
name|capability
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setHttpAddress (String httpAddress)
specifier|public
name|void
name|setHttpAddress
parameter_list|(
name|String
name|httpAddress
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|httpAddress
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearHttpAddress
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setHttpAddress
argument_list|(
name|httpAddress
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNumContainers (int numContainers)
specifier|public
name|void
name|setNumContainers
parameter_list|(
name|int
name|numContainers
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|numContainers
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|clearNumContainers
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setNumContainers
argument_list|(
name|numContainers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setRackName (String rackName)
specifier|public
name|void
name|setRackName
parameter_list|(
name|String
name|rackName
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|rackName
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearRackName
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setRackName
argument_list|(
name|rackName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUsed (Resource used)
specifier|public
name|void
name|setUsed
parameter_list|(
name|Resource
name|used
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|used
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearUsed
argument_list|()
expr_stmt|;
name|this
operator|.
name|used
operator|=
name|used
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|NodeReportProto
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
name|nodeId
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|NodeIdPBImpl
operator|)
name|this
operator|.
name|nodeId
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getNodeId
argument_list|()
argument_list|)
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
name|used
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
name|used
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getUsed
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setUsed
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|used
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|capability
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
name|capability
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getCapability
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setCapability
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|capability
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
name|NodeReportProto
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
DECL|method|convertFromProtoFormat (NodeIdProto p)
specifier|private
name|NodeIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|NodeIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|NodeIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
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
DECL|method|convertToProtoFormat (Resource r)
specifier|private
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

