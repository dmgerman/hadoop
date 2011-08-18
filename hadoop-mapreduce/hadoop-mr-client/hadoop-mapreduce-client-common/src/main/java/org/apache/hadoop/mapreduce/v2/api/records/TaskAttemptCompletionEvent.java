begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.records
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
name|records
package|;
end_package

begin_interface
DECL|interface|TaskAttemptCompletionEvent
specifier|public
interface|interface
name|TaskAttemptCompletionEvent
block|{
DECL|method|getAttemptId ()
specifier|public
specifier|abstract
name|TaskAttemptId
name|getAttemptId
parameter_list|()
function_decl|;
DECL|method|getStatus ()
specifier|public
specifier|abstract
name|TaskAttemptCompletionEventStatus
name|getStatus
parameter_list|()
function_decl|;
DECL|method|getMapOutputServerAddress ()
specifier|public
specifier|abstract
name|String
name|getMapOutputServerAddress
parameter_list|()
function_decl|;
DECL|method|getAttemptRunTime ()
specifier|public
specifier|abstract
name|int
name|getAttemptRunTime
parameter_list|()
function_decl|;
DECL|method|getEventId ()
specifier|public
specifier|abstract
name|int
name|getEventId
parameter_list|()
function_decl|;
DECL|method|setAttemptId (TaskAttemptId taskAttemptId)
specifier|public
specifier|abstract
name|void
name|setAttemptId
parameter_list|(
name|TaskAttemptId
name|taskAttemptId
parameter_list|)
function_decl|;
DECL|method|setStatus (TaskAttemptCompletionEventStatus status)
specifier|public
specifier|abstract
name|void
name|setStatus
parameter_list|(
name|TaskAttemptCompletionEventStatus
name|status
parameter_list|)
function_decl|;
DECL|method|setMapOutputServerAddress (String address)
specifier|public
specifier|abstract
name|void
name|setMapOutputServerAddress
parameter_list|(
name|String
name|address
parameter_list|)
function_decl|;
DECL|method|setAttemptRunTime (int runTime)
specifier|public
specifier|abstract
name|void
name|setAttemptRunTime
parameter_list|(
name|int
name|runTime
parameter_list|)
function_decl|;
DECL|method|setEventId (int eventId)
specifier|public
specifier|abstract
name|void
name|setEventId
parameter_list|(
name|int
name|eventId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

