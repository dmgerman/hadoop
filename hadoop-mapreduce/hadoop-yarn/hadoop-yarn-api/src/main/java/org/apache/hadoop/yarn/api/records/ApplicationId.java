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
DECL|interface|ApplicationId
specifier|public
interface|interface
name|ApplicationId
extends|extends
name|Comparable
argument_list|<
name|ApplicationId
argument_list|>
block|{
DECL|method|getId ()
specifier|public
specifier|abstract
name|int
name|getId
parameter_list|()
function_decl|;
DECL|method|getClusterTimestamp ()
specifier|public
specifier|abstract
name|long
name|getClusterTimestamp
parameter_list|()
function_decl|;
DECL|method|setId (int id)
specifier|public
specifier|abstract
name|void
name|setId
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
DECL|method|setClusterTimestamp (long clusterTimestamp)
specifier|public
specifier|abstract
name|void
name|setClusterTimestamp
parameter_list|(
name|long
name|clusterTimestamp
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

