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
DECL|interface|Resource
specifier|public
interface|interface
name|Resource
extends|extends
name|Comparable
argument_list|<
name|Resource
argument_list|>
block|{
DECL|method|getMemory ()
specifier|public
specifier|abstract
name|int
name|getMemory
parameter_list|()
function_decl|;
DECL|method|setMemory (int memory)
specifier|public
specifier|abstract
name|void
name|setMemory
parameter_list|(
name|int
name|memory
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

