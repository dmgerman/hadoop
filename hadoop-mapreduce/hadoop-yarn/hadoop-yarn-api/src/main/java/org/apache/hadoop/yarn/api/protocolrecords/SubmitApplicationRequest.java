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
name|ApplicationSubmissionContext
import|;
end_import

begin_interface
DECL|interface|SubmitApplicationRequest
specifier|public
interface|interface
name|SubmitApplicationRequest
block|{
DECL|method|getApplicationSubmissionContext ()
specifier|public
specifier|abstract
name|ApplicationSubmissionContext
name|getApplicationSubmissionContext
parameter_list|()
function_decl|;
DECL|method|setApplicationSubmissionContext (ApplicationSubmissionContext context)
specifier|public
specifier|abstract
name|void
name|setApplicationSubmissionContext
parameter_list|(
name|ApplicationSubmissionContext
name|context
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

