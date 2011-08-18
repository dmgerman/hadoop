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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

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
name|QueueUserACLInfo
import|;
end_import

begin_interface
DECL|interface|GetQueueUserAclsInfoResponse
specifier|public
interface|interface
name|GetQueueUserAclsInfoResponse
block|{
DECL|method|getUserAclsInfoList ()
specifier|public
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|getUserAclsInfoList
parameter_list|()
function_decl|;
DECL|method|setUserAclsInfoList (List<QueueUserACLInfo> queueUserAclsList)
specifier|public
name|void
name|setUserAclsInfoList
parameter_list|(
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|queueUserAclsList
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

