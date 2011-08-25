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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_interface
DECL|interface|QueueUserACLInfo
specifier|public
interface|interface
name|QueueUserACLInfo
block|{
DECL|method|getQueueName ()
name|String
name|getQueueName
parameter_list|()
function_decl|;
DECL|method|setQueueName (String queueName)
name|void
name|setQueueName
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
DECL|method|getUserAcls ()
name|List
argument_list|<
name|QueueACL
argument_list|>
name|getUserAcls
parameter_list|()
function_decl|;
DECL|method|setUserAcls (List<QueueACL> acls)
name|void
name|setUserAcls
parameter_list|(
name|List
argument_list|<
name|QueueACL
argument_list|>
name|acls
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

