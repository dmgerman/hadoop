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
DECL|interface|GetAllApplicationsResponse
specifier|public
interface|interface
name|GetAllApplicationsResponse
block|{
DECL|method|getApplicationList ()
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplicationList
parameter_list|()
function_decl|;
DECL|method|setApplicationList (List<ApplicationReport> applications)
name|void
name|setApplicationList
parameter_list|(
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|applications
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

