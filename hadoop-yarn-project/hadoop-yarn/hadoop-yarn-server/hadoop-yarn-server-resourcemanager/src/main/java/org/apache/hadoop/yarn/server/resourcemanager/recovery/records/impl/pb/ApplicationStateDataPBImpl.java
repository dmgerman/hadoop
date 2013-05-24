begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb
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
name|recovery
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
name|YarnServerResourceManagerServiceProtos
operator|.
name|ApplicationStateDataProto
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
name|ApplicationStateDataProtoOrBuilder
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|ApplicationStateData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|log
operator|.
name|Log
import|;
end_import

begin_class
DECL|class|ApplicationStateDataPBImpl
specifier|public
class|class
name|ApplicationStateDataPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|ApplicationStateDataProto
argument_list|>
implements|implements
name|ApplicationStateData
block|{
DECL|field|proto
name|ApplicationStateDataProto
name|proto
init|=
name|ApplicationStateDataProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ApplicationStateDataProto
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
DECL|method|ApplicationStateDataPBImpl ()
specifier|public
name|ApplicationStateDataPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ApplicationStateDataProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ApplicationStateDataPBImpl ( ApplicationStateDataProto proto)
specifier|public
name|ApplicationStateDataPBImpl
parameter_list|(
name|ApplicationStateDataProto
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
name|ApplicationStateDataProto
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
operator|(
operator|(
name|ApplicationSubmissionContextPBImpl
operator|)
name|applicationSubmissionContext
operator|)
operator|.
name|getProto
argument_list|()
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
name|ApplicationStateDataProto
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
DECL|method|getSubmitTime ()
specifier|public
name|long
name|getSubmitTime
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
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
name|hasSubmitTime
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getSubmitTime
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setSubmitTime (long submitTime)
specifier|public
name|void
name|setSubmitTime
parameter_list|(
name|long
name|submitTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setSubmitTime
argument_list|(
name|submitTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
name|ApplicationStateDataProtoOrBuilder
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
name|hasUser
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|p
operator|.
name|getUser
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setUser (String user)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
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
name|ApplicationStateDataProtoOrBuilder
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
name|applicationSubmissionContext
operator|!=
literal|null
condition|)
block|{
return|return
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
name|applicationSubmissionContext
operator|=
operator|new
name|ApplicationSubmissionContextPBImpl
argument_list|(
name|p
operator|.
name|getApplicationSubmissionContext
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|applicationSubmissionContext
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationSubmissionContext ( ApplicationSubmissionContext context)
specifier|public
name|void
name|setApplicationSubmissionContext
parameter_list|(
name|ApplicationSubmissionContext
name|context
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearApplicationSubmissionContext
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|applicationSubmissionContext
operator|=
name|context
expr_stmt|;
block|}
block|}
end_class

end_unit

