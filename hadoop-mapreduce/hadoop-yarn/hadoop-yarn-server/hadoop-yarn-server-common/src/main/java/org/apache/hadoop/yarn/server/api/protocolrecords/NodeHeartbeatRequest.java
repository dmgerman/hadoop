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
name|NodeStatus
import|;
end_import

begin_interface
DECL|interface|NodeHeartbeatRequest
specifier|public
interface|interface
name|NodeHeartbeatRequest
block|{
DECL|method|getNodeStatus ()
specifier|public
specifier|abstract
name|NodeStatus
name|getNodeStatus
parameter_list|()
function_decl|;
DECL|method|setNodeStatus (NodeStatus status)
specifier|public
specifier|abstract
name|void
name|setNodeStatus
parameter_list|(
name|NodeStatus
name|status
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

