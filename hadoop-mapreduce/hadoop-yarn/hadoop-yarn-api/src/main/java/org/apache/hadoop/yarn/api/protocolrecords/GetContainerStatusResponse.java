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
name|ContainerStatus
import|;
end_import

begin_interface
DECL|interface|GetContainerStatusResponse
specifier|public
interface|interface
name|GetContainerStatusResponse
block|{
DECL|method|getStatus ()
specifier|public
specifier|abstract
name|ContainerStatus
name|getStatus
parameter_list|()
function_decl|;
DECL|method|setStatus (ContainerStatus containerStatus)
specifier|public
specifier|abstract
name|void
name|setStatus
parameter_list|(
name|ContainerStatus
name|containerStatus
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

