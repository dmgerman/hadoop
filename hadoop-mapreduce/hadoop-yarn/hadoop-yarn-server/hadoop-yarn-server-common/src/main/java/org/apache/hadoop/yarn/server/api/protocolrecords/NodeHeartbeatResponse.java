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
name|HeartbeatResponse
import|;
end_import

begin_interface
DECL|interface|NodeHeartbeatResponse
specifier|public
interface|interface
name|NodeHeartbeatResponse
block|{
DECL|method|getHeartbeatResponse ()
specifier|public
specifier|abstract
name|HeartbeatResponse
name|getHeartbeatResponse
parameter_list|()
function_decl|;
DECL|method|setHeartbeatResponse (HeartbeatResponse heartbeatResponse)
specifier|public
specifier|abstract
name|void
name|setHeartbeatResponse
parameter_list|(
name|HeartbeatResponse
name|heartbeatResponse
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

