begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|GetTaskAttemptCompletionEventsRequest
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
name|JobId
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
name|JobIdPBImpl
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
name|JobIdProto
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
name|GetTaskAttemptCompletionEventsRequestProto
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
name|GetTaskAttemptCompletionEventsRequestProtoOrBuilder
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

begin_class
DECL|class|GetTaskAttemptCompletionEventsRequestPBImpl
specifier|public
class|class
name|GetTaskAttemptCompletionEventsRequestPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|GetTaskAttemptCompletionEventsRequestProto
argument_list|>
implements|implements
name|GetTaskAttemptCompletionEventsRequest
block|{
DECL|field|proto
name|GetTaskAttemptCompletionEventsRequestProto
name|proto
init|=
name|GetTaskAttemptCompletionEventsRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetTaskAttemptCompletionEventsRequestProto
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
DECL|field|jobId
specifier|private
name|JobId
name|jobId
init|=
literal|null
decl_stmt|;
DECL|method|GetTaskAttemptCompletionEventsRequestPBImpl ()
specifier|public
name|GetTaskAttemptCompletionEventsRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetTaskAttemptCompletionEventsRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetTaskAttemptCompletionEventsRequestPBImpl (GetTaskAttemptCompletionEventsRequestProto proto)
specifier|public
name|GetTaskAttemptCompletionEventsRequestPBImpl
parameter_list|(
name|GetTaskAttemptCompletionEventsRequestProto
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
name|GetTaskAttemptCompletionEventsRequestProto
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
name|jobId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setJobId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|jobId
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
name|GetTaskAttemptCompletionEventsRequestProto
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
DECL|method|getJobId ()
specifier|public
name|JobId
name|getJobId
parameter_list|()
block|{
name|GetTaskAttemptCompletionEventsRequestProtoOrBuilder
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
name|jobId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|jobId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasJobId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|jobId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|jobId
return|;
block|}
annotation|@
name|Override
DECL|method|setJobId (JobId jobId)
specifier|public
name|void
name|setJobId
parameter_list|(
name|JobId
name|jobId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|jobId
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearJobId
argument_list|()
expr_stmt|;
name|this
operator|.
name|jobId
operator|=
name|jobId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFromEventId ()
specifier|public
name|int
name|getFromEventId
parameter_list|()
block|{
name|GetTaskAttemptCompletionEventsRequestProtoOrBuilder
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
name|getFromEventId
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setFromEventId (int fromEventId)
specifier|public
name|void
name|setFromEventId
parameter_list|(
name|int
name|fromEventId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setFromEventId
argument_list|(
operator|(
name|fromEventId
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxEvents ()
specifier|public
name|int
name|getMaxEvents
parameter_list|()
block|{
name|GetTaskAttemptCompletionEventsRequestProtoOrBuilder
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
name|getMaxEvents
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|setMaxEvents (int maxEvents)
specifier|public
name|void
name|setMaxEvents
parameter_list|(
name|int
name|maxEvents
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setMaxEvents
argument_list|(
operator|(
name|maxEvents
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (JobIdProto p)
specifier|private
name|JobIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|JobIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|JobIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (JobId t)
specifier|private
name|JobIdProto
name|convertToProtoFormat
parameter_list|(
name|JobId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|JobIdPBImpl
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

