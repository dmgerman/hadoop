begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.factories
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|factories
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
name|YarnException
import|;
end_import

begin_interface
DECL|interface|RecordFactory
specifier|public
interface|interface
name|RecordFactory
block|{
DECL|method|newRecordInstance (Class<T> clazz)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|newRecordInstance
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|YarnException
function_decl|;
block|}
end_interface

end_unit

