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
name|AMResponse
import|;
end_import

begin_interface
DECL|interface|AllocateResponse
specifier|public
interface|interface
name|AllocateResponse
block|{
DECL|method|getAMResponse ()
specifier|public
specifier|abstract
name|AMResponse
name|getAMResponse
parameter_list|()
function_decl|;
DECL|method|setAMResponse (AMResponse amResponse)
specifier|public
specifier|abstract
name|void
name|setAMResponse
parameter_list|(
name|AMResponse
name|amResponse
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

