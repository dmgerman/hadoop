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
name|QueueInfo
import|;
end_import

begin_interface
DECL|interface|GetQueueInfoResponse
specifier|public
interface|interface
name|GetQueueInfoResponse
block|{
DECL|method|getQueueInfo ()
name|QueueInfo
name|getQueueInfo
parameter_list|()
function_decl|;
DECL|method|setQueueInfo (QueueInfo queueInfo)
name|void
name|setQueueInfo
parameter_list|(
name|QueueInfo
name|queueInfo
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

