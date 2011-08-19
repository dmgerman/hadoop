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
DECL|interface|TaskId
specifier|public
interface|interface
name|TaskId
block|{
DECL|method|getJobId ()
specifier|public
specifier|abstract
name|JobId
name|getJobId
parameter_list|()
function_decl|;
DECL|method|getTaskType ()
specifier|public
specifier|abstract
name|TaskType
name|getTaskType
parameter_list|()
function_decl|;
DECL|method|getId ()
specifier|public
specifier|abstract
name|int
name|getId
parameter_list|()
function_decl|;
DECL|method|setJobId (JobId jobId)
specifier|public
specifier|abstract
name|void
name|setJobId
parameter_list|(
name|JobId
name|jobId
parameter_list|)
function_decl|;
DECL|method|setTaskType (TaskType taskType)
specifier|public
specifier|abstract
name|void
name|setTaskType
parameter_list|(
name|TaskType
name|taskType
parameter_list|)
function_decl|;
DECL|method|setId (int id)
specifier|public
specifier|abstract
name|void
name|setId
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

