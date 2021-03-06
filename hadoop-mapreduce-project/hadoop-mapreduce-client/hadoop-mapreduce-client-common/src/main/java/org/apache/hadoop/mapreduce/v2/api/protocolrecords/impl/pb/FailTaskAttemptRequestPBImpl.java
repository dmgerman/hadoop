begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.protocolrecords.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|FailTaskAttemptRequest
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|TaskAttemptIdPBImpl
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
name|mapreduce
operator|.
name|v2
operator|.
name|proto
operator|.
name|MRProtos
operator|.
name|TaskAttemptIdProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|proto
operator|.
name|MRServiceProtos
operator|.
name|FailTaskAttemptRequestProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|proto
operator|.
name|MRServiceProtos
operator|.
name|FailTaskAttemptRequestProtoOrBuilder
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
name|ProtoBase
import|;
end_import

begin_class
DECL|class|FailTaskAttemptRequestPBImpl
specifier|public
class|class
name|FailTaskAttemptRequestPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|FailTaskAttemptRequestProto
argument_list|>
implements|implements
name|FailTaskAttemptRequest
block|{
DECL|field|proto
name|FailTaskAttemptRequestProto
name|proto
init|=
name|FailTaskAttemptRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|FailTaskAttemptRequestProto
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
DECL|field|taskAttemptId
specifier|private
name|TaskAttemptId
name|taskAttemptId
init|=
literal|null
decl_stmt|;
DECL|method|FailTaskAttemptRequestPBImpl ()
specifier|public
name|FailTaskAttemptRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|FailTaskAttemptRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|FailTaskAttemptRequestPBImpl (FailTaskAttemptRequestProto proto)
specifier|public
name|FailTaskAttemptRequestPBImpl
parameter_list|(
name|FailTaskAttemptRequestProto
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
name|FailTaskAttemptRequestProto
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
name|taskAttemptId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setTaskAttemptId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|taskAttemptId
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
name|FailTaskAttemptRequestProto
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
DECL|method|getTaskAttemptId ()
specifier|public
name|TaskAttemptId
name|getTaskAttemptId
parameter_list|()
block|{
name|FailTaskAttemptRequestProtoOrBuilder
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
name|taskAttemptId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|taskAttemptId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasTaskAttemptId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|taskAttemptId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getTaskAttemptId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|taskAttemptId
return|;
block|}
annotation|@
name|Override
DECL|method|setTaskAttemptId (TaskAttemptId taskAttemptId)
specifier|public
name|void
name|setTaskAttemptId
parameter_list|(
name|TaskAttemptId
name|taskAttemptId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|taskAttemptId
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearTaskAttemptId
argument_list|()
expr_stmt|;
name|this
operator|.
name|taskAttemptId
operator|=
name|taskAttemptId
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (TaskAttemptIdProto p)
specifier|private
name|TaskAttemptIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|TaskAttemptIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|TaskAttemptIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (TaskAttemptId t)
specifier|private
name|TaskAttemptIdProto
name|convertToProtoFormat
parameter_list|(
name|TaskAttemptId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TaskAttemptIdPBImpl
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

