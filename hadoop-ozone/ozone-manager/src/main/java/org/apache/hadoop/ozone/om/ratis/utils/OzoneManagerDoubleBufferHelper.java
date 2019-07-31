begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.ozone.om.ratis.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|ratis
operator|.
name|utils
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
name|ozone
operator|.
name|om
operator|.
name|response
operator|.
name|OMClientResponse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CompletableFuture
import|;
end_import

begin_comment
comment|/**  * Helper interface for OzoneManagerDoubleBuffer.  *  */
end_comment

begin_interface
DECL|interface|OzoneManagerDoubleBufferHelper
specifier|public
interface|interface
name|OzoneManagerDoubleBufferHelper
block|{
DECL|method|add (OMClientResponse response, long transactionIndex)
name|CompletableFuture
argument_list|<
name|Void
argument_list|>
name|add
parameter_list|(
name|OMClientResponse
name|response
parameter_list|,
name|long
name|transactionIndex
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

