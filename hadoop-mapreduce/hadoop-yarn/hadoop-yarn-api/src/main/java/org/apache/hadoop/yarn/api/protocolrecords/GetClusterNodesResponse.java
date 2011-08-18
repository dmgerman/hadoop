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
name|NodeReport
import|;
end_import

begin_interface
DECL|interface|GetClusterNodesResponse
specifier|public
interface|interface
name|GetClusterNodesResponse
block|{
DECL|method|getNodeReports ()
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getNodeReports
parameter_list|()
function_decl|;
DECL|method|setNodeReports (List<NodeReport> nodeReports)
name|void
name|setNodeReports
parameter_list|(
name|List
argument_list|<
name|NodeReport
argument_list|>
name|nodeReports
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

