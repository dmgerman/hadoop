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
name|ApplicationReport
import|;
end_import

begin_interface
DECL|interface|GetApplicationReportResponse
specifier|public
interface|interface
name|GetApplicationReportResponse
block|{
DECL|method|getApplicationReport ()
specifier|public
specifier|abstract
name|ApplicationReport
name|getApplicationReport
parameter_list|()
function_decl|;
DECL|method|setApplicationReport (ApplicationReport ApplicationReport)
specifier|public
specifier|abstract
name|void
name|setApplicationReport
parameter_list|(
name|ApplicationReport
name|ApplicationReport
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

