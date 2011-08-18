begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.protocolrecords
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|Counters
import|;
end_import

begin_interface
DECL|interface|GetCountersResponse
specifier|public
interface|interface
name|GetCountersResponse
block|{
DECL|method|getCounters ()
specifier|public
specifier|abstract
name|Counters
name|getCounters
parameter_list|()
function_decl|;
DECL|method|setCounters (Counters counters)
specifier|public
specifier|abstract
name|void
name|setCounters
parameter_list|(
name|Counters
name|counters
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

