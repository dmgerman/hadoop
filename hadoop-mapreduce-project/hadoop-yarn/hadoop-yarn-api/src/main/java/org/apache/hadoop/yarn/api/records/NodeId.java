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
DECL|interface|NodeId
specifier|public
interface|interface
name|NodeId
extends|extends
name|Comparable
argument_list|<
name|NodeId
argument_list|>
block|{
DECL|method|getHost ()
name|String
name|getHost
parameter_list|()
function_decl|;
DECL|method|setHost (String host)
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
function_decl|;
DECL|method|getPort ()
name|int
name|getPort
parameter_list|()
function_decl|;
DECL|method|setPort (int port)
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

