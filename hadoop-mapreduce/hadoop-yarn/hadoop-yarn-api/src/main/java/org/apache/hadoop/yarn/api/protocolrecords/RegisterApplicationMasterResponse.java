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
name|Resource
import|;
end_import

begin_interface
DECL|interface|RegisterApplicationMasterResponse
specifier|public
interface|interface
name|RegisterApplicationMasterResponse
block|{
DECL|method|getMinimumResourceCapability ()
specifier|public
name|Resource
name|getMinimumResourceCapability
parameter_list|()
function_decl|;
DECL|method|setMinimumResourceCapability (Resource capability)
specifier|public
name|void
name|setMinimumResourceCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
function_decl|;
DECL|method|getMaximumResourceCapability ()
specifier|public
name|Resource
name|getMaximumResourceCapability
parameter_list|()
function_decl|;
DECL|method|setMaximumResourceCapability (Resource capability)
specifier|public
name|void
name|setMaximumResourceCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

