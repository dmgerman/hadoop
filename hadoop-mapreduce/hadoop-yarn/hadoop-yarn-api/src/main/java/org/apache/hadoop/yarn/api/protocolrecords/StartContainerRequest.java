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
name|ContainerLaunchContext
import|;
end_import

begin_interface
DECL|interface|StartContainerRequest
specifier|public
interface|interface
name|StartContainerRequest
block|{
DECL|method|getContainerLaunchContext ()
specifier|public
specifier|abstract
name|ContainerLaunchContext
name|getContainerLaunchContext
parameter_list|()
function_decl|;
DECL|method|setContainerLaunchContext (ContainerLaunchContext context)
specifier|public
specifier|abstract
name|void
name|setContainerLaunchContext
parameter_list|(
name|ContainerLaunchContext
name|context
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

