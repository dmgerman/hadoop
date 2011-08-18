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
name|YarnClusterMetrics
import|;
end_import

begin_interface
DECL|interface|GetClusterMetricsResponse
specifier|public
interface|interface
name|GetClusterMetricsResponse
block|{
DECL|method|getClusterMetrics ()
specifier|public
specifier|abstract
name|YarnClusterMetrics
name|getClusterMetrics
parameter_list|()
function_decl|;
DECL|method|setClusterMetrics (YarnClusterMetrics metrics)
specifier|public
specifier|abstract
name|void
name|setClusterMetrics
parameter_list|(
name|YarnClusterMetrics
name|metrics
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

