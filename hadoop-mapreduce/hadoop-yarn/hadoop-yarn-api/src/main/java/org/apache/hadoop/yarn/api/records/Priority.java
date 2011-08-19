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
DECL|interface|Priority
specifier|public
interface|interface
name|Priority
extends|extends
name|Comparable
argument_list|<
name|Priority
argument_list|>
block|{
DECL|method|getPriority ()
specifier|public
specifier|abstract
name|int
name|getPriority
parameter_list|()
function_decl|;
DECL|method|setPriority (int priority)
specifier|public
specifier|abstract
name|void
name|setPriority
parameter_list|(
name|int
name|priority
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

