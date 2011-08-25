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
DECL|interface|YarnClusterMetrics
specifier|public
interface|interface
name|YarnClusterMetrics
block|{
DECL|method|getNumNodeManagers ()
specifier|public
specifier|abstract
name|int
name|getNumNodeManagers
parameter_list|()
function_decl|;
DECL|method|setNumNodeManagers (int numNodeManagers)
specifier|public
specifier|abstract
name|void
name|setNumNodeManagers
parameter_list|(
name|int
name|numNodeManagers
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

