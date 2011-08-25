begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.protocolrecords
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
package|;
end_package

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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptCompletionEvent
import|;
end_import

begin_interface
DECL|interface|GetTaskAttemptCompletionEventsResponse
specifier|public
interface|interface
name|GetTaskAttemptCompletionEventsResponse
block|{
DECL|method|getCompletionEventList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|TaskAttemptCompletionEvent
argument_list|>
name|getCompletionEventList
parameter_list|()
function_decl|;
DECL|method|getCompletionEvent (int index)
specifier|public
specifier|abstract
name|TaskAttemptCompletionEvent
name|getCompletionEvent
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getCompletionEventCount ()
specifier|public
specifier|abstract
name|int
name|getCompletionEventCount
parameter_list|()
function_decl|;
DECL|method|addAllCompletionEvents (List<TaskAttemptCompletionEvent> eventList)
specifier|public
specifier|abstract
name|void
name|addAllCompletionEvents
parameter_list|(
name|List
argument_list|<
name|TaskAttemptCompletionEvent
argument_list|>
name|eventList
parameter_list|)
function_decl|;
DECL|method|addCompletionEvent (TaskAttemptCompletionEvent event)
specifier|public
specifier|abstract
name|void
name|addCompletionEvent
parameter_list|(
name|TaskAttemptCompletionEvent
name|event
parameter_list|)
function_decl|;
DECL|method|removeCompletionEvent (int index)
specifier|public
specifier|abstract
name|void
name|removeCompletionEvent
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearCompletionEvents ()
specifier|public
specifier|abstract
name|void
name|clearCompletionEvents
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

