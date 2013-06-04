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
name|GetTaskAttemptReportResponse
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
name|TaskAttemptReport
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
name|TaskAttemptReportPBImpl
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
name|TaskAttemptReportProto
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
name|GetTaskAttemptReportResponseProto
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
name|GetTaskAttemptReportResponseProtoOrBuilder
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
DECL|class|GetTaskAttemptReportResponsePBImpl
specifier|public
class|class
name|GetTaskAttemptReportResponsePBImpl
extends|extends
name|ProtoBase
argument_list|<
name|GetTaskAttemptReportResponseProto
argument_list|>
implements|implements
name|GetTaskAttemptReportResponse
block|{
DECL|field|proto
name|GetTaskAttemptReportResponseProto
name|proto
init|=
name|GetTaskAttemptReportResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetTaskAttemptReportResponseProto
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
DECL|field|taskAttemptReport
specifier|private
name|TaskAttemptReport
name|taskAttemptReport
init|=
literal|null
decl_stmt|;
DECL|method|GetTaskAttemptReportResponsePBImpl ()
specifier|public
name|GetTaskAttemptReportResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetTaskAttemptReportResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetTaskAttemptReportResponsePBImpl (GetTaskAttemptReportResponseProto proto)
specifier|public
name|GetTaskAttemptReportResponsePBImpl
parameter_list|(
name|GetTaskAttemptReportResponseProto
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
name|GetTaskAttemptReportResponseProto
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
name|taskAttemptReport
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setTaskAttemptReport
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|taskAttemptReport
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
name|GetTaskAttemptReportResponseProto
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
DECL|method|getTaskAttemptReport ()
specifier|public
name|TaskAttemptReport
name|getTaskAttemptReport
parameter_list|()
block|{
name|GetTaskAttemptReportResponseProtoOrBuilder
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
name|taskAttemptReport
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|taskAttemptReport
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasTaskAttemptReport
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|taskAttemptReport
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getTaskAttemptReport
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|taskAttemptReport
return|;
block|}
annotation|@
name|Override
DECL|method|setTaskAttemptReport (TaskAttemptReport taskAttemptReport)
specifier|public
name|void
name|setTaskAttemptReport
parameter_list|(
name|TaskAttemptReport
name|taskAttemptReport
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|taskAttemptReport
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearTaskAttemptReport
argument_list|()
expr_stmt|;
name|this
operator|.
name|taskAttemptReport
operator|=
name|taskAttemptReport
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (TaskAttemptReportProto p)
specifier|private
name|TaskAttemptReportPBImpl
name|convertFromProtoFormat
parameter_list|(
name|TaskAttemptReportProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|TaskAttemptReportPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (TaskAttemptReport t)
specifier|private
name|TaskAttemptReportProto
name|convertToProtoFormat
parameter_list|(
name|TaskAttemptReport
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TaskAttemptReportPBImpl
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

