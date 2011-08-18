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

begin_interface
DECL|interface|GetTaskAttemptReportResponse
specifier|public
interface|interface
name|GetTaskAttemptReportResponse
block|{
DECL|method|getTaskAttemptReport ()
specifier|public
specifier|abstract
name|TaskAttemptReport
name|getTaskAttemptReport
parameter_list|()
function_decl|;
DECL|method|setTaskAttemptReport (TaskAttemptReport taskAttemptReport)
specifier|public
specifier|abstract
name|void
name|setTaskAttemptReport
parameter_list|(
name|TaskAttemptReport
name|taskAttemptReport
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

