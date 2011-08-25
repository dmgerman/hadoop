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
name|TaskReport
import|;
end_import

begin_interface
DECL|interface|GetTaskReportsResponse
specifier|public
interface|interface
name|GetTaskReportsResponse
block|{
DECL|method|getTaskReportList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|TaskReport
argument_list|>
name|getTaskReportList
parameter_list|()
function_decl|;
DECL|method|getTaskReport (int index)
specifier|public
specifier|abstract
name|TaskReport
name|getTaskReport
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getTaskReportCount ()
specifier|public
specifier|abstract
name|int
name|getTaskReportCount
parameter_list|()
function_decl|;
DECL|method|addAllTaskReports (List<TaskReport> taskReports)
specifier|public
specifier|abstract
name|void
name|addAllTaskReports
parameter_list|(
name|List
argument_list|<
name|TaskReport
argument_list|>
name|taskReports
parameter_list|)
function_decl|;
DECL|method|addTaskReport (TaskReport taskReport)
specifier|public
specifier|abstract
name|void
name|addTaskReport
parameter_list|(
name|TaskReport
name|taskReport
parameter_list|)
function_decl|;
DECL|method|removeTaskReport (int index)
specifier|public
specifier|abstract
name|void
name|removeTaskReport
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearTaskReports ()
specifier|public
specifier|abstract
name|void
name|clearTaskReports
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

