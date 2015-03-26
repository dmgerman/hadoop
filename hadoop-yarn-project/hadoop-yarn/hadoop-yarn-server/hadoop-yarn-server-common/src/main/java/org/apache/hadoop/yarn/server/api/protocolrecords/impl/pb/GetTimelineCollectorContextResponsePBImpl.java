begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
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
name|YarnServerCommonServiceProtos
operator|.
name|GetTimelineCollectorContextResponseProtoOrBuilder
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
name|YarnServerCommonServiceProtos
operator|.
name|GetTimelineCollectorContextResponseProto
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
name|GetTimelineCollectorContextResponse
import|;
end_import

begin_class
DECL|class|GetTimelineCollectorContextResponsePBImpl
specifier|public
class|class
name|GetTimelineCollectorContextResponsePBImpl
extends|extends
name|GetTimelineCollectorContextResponse
block|{
DECL|field|proto
name|GetTimelineCollectorContextResponseProto
name|proto
init|=
name|GetTimelineCollectorContextResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetTimelineCollectorContextResponseProto
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
DECL|method|GetTimelineCollectorContextResponsePBImpl ()
specifier|public
name|GetTimelineCollectorContextResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetTimelineCollectorContextResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetTimelineCollectorContextResponsePBImpl ( GetTimelineCollectorContextResponseProto proto)
specifier|public
name|GetTimelineCollectorContextResponsePBImpl
parameter_list|(
name|GetTimelineCollectorContextResponseProto
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
name|GetTimelineCollectorContextResponseProto
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
name|GetTimelineCollectorContextResponseProto
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
DECL|method|getUserId ()
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
name|GetTimelineCollectorContextResponseProtoOrBuilder
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
name|hasUserId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|p
operator|.
name|getUserId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setUserId (String userId)
specifier|public
name|void
name|setUserId
parameter_list|(
name|String
name|userId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|userId
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearUserId
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setUserId
argument_list|(
name|userId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFlowId ()
specifier|public
name|String
name|getFlowId
parameter_list|()
block|{
name|GetTimelineCollectorContextResponseProtoOrBuilder
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
name|hasFlowId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|p
operator|.
name|getFlowId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setFlowId (String flowId)
specifier|public
name|void
name|setFlowId
parameter_list|(
name|String
name|flowId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|flowId
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearFlowId
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setFlowId
argument_list|(
name|flowId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFlowRunId ()
specifier|public
name|String
name|getFlowRunId
parameter_list|()
block|{
name|GetTimelineCollectorContextResponseProtoOrBuilder
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
name|hasFlowRunId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|p
operator|.
name|getFlowRunId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setFlowRunId (String flowRunId)
specifier|public
name|void
name|setFlowRunId
parameter_list|(
name|String
name|flowRunId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|flowRunId
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearFlowRunId
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setFlowRunId
argument_list|(
name|flowRunId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

