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
name|TaskId
import|;
end_import

begin_interface
DECL|interface|GetTaskReportRequest
specifier|public
interface|interface
name|GetTaskReportRequest
block|{
DECL|method|getTaskId ()
specifier|public
specifier|abstract
name|TaskId
name|getTaskId
parameter_list|()
function_decl|;
DECL|method|setTaskId (TaskId taskId)
specifier|public
specifier|abstract
name|void
name|setTaskId
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

