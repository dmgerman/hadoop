begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
import|;
end_import

begin_interface
DECL|interface|FinishApplicationMasterRequest
specifier|public
interface|interface
name|FinishApplicationMasterRequest
block|{
DECL|method|getApplicationAttemptId ()
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
DECL|method|setAppAttemptId (ApplicationAttemptId applicationAttemptId)
name|void
name|setAppAttemptId
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
function_decl|;
DECL|method|getFinalState ()
name|String
name|getFinalState
parameter_list|()
function_decl|;
DECL|method|setFinalState (String string)
name|void
name|setFinalState
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
DECL|method|getDiagnostics ()
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
DECL|method|setDiagnostics (String string)
name|void
name|setDiagnostics
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
DECL|method|getTrackingUrl ()
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
DECL|method|setTrackingUrl (String historyUrl)
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|historyUrl
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

