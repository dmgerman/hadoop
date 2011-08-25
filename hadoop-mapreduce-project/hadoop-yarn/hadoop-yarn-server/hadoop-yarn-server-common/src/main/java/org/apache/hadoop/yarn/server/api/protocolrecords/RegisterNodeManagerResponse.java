begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
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
name|server
operator|.
name|api
operator|.
name|records
operator|.
name|RegistrationResponse
import|;
end_import

begin_interface
DECL|interface|RegisterNodeManagerResponse
specifier|public
interface|interface
name|RegisterNodeManagerResponse
block|{
DECL|method|getRegistrationResponse ()
specifier|public
specifier|abstract
name|RegistrationResponse
name|getRegistrationResponse
parameter_list|()
function_decl|;
DECL|method|setRegistrationResponse (RegistrationResponse registrationResponse)
specifier|public
specifier|abstract
name|void
name|setRegistrationResponse
parameter_list|(
name|RegistrationResponse
name|registrationResponse
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

