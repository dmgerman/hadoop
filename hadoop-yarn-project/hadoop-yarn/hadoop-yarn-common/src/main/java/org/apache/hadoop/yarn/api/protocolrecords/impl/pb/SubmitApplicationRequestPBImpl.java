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
name|SubmitApplicationRequest
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
name|ApplicationSubmissionContext
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
name|ApplicationSubmissionContextPBImpl
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
name|ApplicationSubmissionContextProto
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
name|SubmitApplicationRequestProto
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
name|SubmitApplicationRequestProtoOrBuilder
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
DECL|class|SubmitApplicationRequestPBImpl
specifier|public
class|class
name|SubmitApplicationRequestPBImpl
extends|extends
name|SubmitApplicationRequest
block|{
DECL|field|proto
name|SubmitApplicationRequestProto
name|proto
init|=
name|SubmitApplicationRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|SubmitApplicationRequestProto
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
DECL|field|applicationSubmissionContext
specifier|private
name|ApplicationSubmissionContext
name|applicationSubmissionContext
init|=
literal|null
decl_stmt|;
DECL|method|SubmitApplicationRequestPBImpl ()
specifier|public
name|SubmitApplicationRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|SubmitApplicationRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|SubmitApplicationRequestPBImpl (SubmitApplicationRequestProto proto)
specifier|public
name|SubmitApplicationRequestPBImpl
parameter_list|(
name|SubmitApplicationRequestProto
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
name|SubmitApplicationRequestProto
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
name|applicationSubmissionContext
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setApplicationSubmissionContext
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|applicationSubmissionContext
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
name|SubmitApplicationRequestProto
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
DECL|method|getApplicationSubmissionContext ()
specifier|public
name|ApplicationSubmissionContext
name|getApplicationSubmissionContext
parameter_list|()
block|{
name|SubmitApplicationRequestProtoOrBuilder
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
name|applicationSubmissionContext
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|applicationSubmissionContext
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasApplicationSubmissionContext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|applicationSubmissionContext
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getApplicationSubmissionContext
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|applicationSubmissionContext
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationSubmissionContext (ApplicationSubmissionContext applicationSubmissionContext)
specifier|public
name|void
name|setApplicationSubmissionContext
parameter_list|(
name|ApplicationSubmissionContext
name|applicationSubmissionContext
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationSubmissionContext
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearApplicationSubmissionContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationSubmissionContext
operator|=
name|applicationSubmissionContext
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ApplicationSubmissionContextProto p)
specifier|private
name|ApplicationSubmissionContextPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ApplicationSubmissionContextProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ApplicationSubmissionContextPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ApplicationSubmissionContext t)
specifier|private
name|ApplicationSubmissionContextProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationSubmissionContext
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ApplicationSubmissionContextPBImpl
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

