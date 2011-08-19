begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
package|;
end_package

begin_interface
DECL|interface|ContainerId
specifier|public
interface|interface
name|ContainerId
extends|extends
name|Comparable
argument_list|<
name|ContainerId
argument_list|>
block|{
DECL|method|getAppAttemptId ()
specifier|public
specifier|abstract
name|ApplicationAttemptId
name|getAppAttemptId
parameter_list|()
function_decl|;
DECL|method|getAppId ()
specifier|public
specifier|abstract
name|ApplicationId
name|getAppId
parameter_list|()
function_decl|;
DECL|method|getId ()
specifier|public
specifier|abstract
name|int
name|getId
parameter_list|()
function_decl|;
DECL|method|setAppAttemptId (ApplicationAttemptId atId)
specifier|public
specifier|abstract
name|void
name|setAppAttemptId
parameter_list|(
name|ApplicationAttemptId
name|atId
parameter_list|)
function_decl|;
DECL|method|setAppId (ApplicationId appID)
specifier|public
specifier|abstract
name|void
name|setAppId
parameter_list|(
name|ApplicationId
name|appID
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

