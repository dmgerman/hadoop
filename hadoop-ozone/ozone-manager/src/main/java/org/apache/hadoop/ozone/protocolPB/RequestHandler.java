begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.ozone.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocolPB
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
name|exceptions
operator|.
name|OMException
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMRequest
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
import|;
end_import

begin_comment
comment|/**  * Handler to handle the OmRequests.  */
end_comment

begin_interface
DECL|interface|RequestHandler
specifier|public
interface|interface
name|RequestHandler
block|{
comment|/**    * Handle the OmRequest, and returns OmResponse.    * @param request    * @return OmResponse    */
DECL|method|handle (OMRequest request)
name|OMResponse
name|handle
parameter_list|(
name|OMRequest
name|request
parameter_list|)
function_decl|;
comment|/**    * Validates that the incoming OM request has required parameters.    * TODO: Add more validation checks before writing the request to Ratis log.    *    * @param omRequest client request to OM    * @throws OMException thrown if required parameters are set to null.    */
DECL|method|validateRequest (OMRequest omRequest)
name|void
name|validateRequest
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|)
throws|throws
name|OMException
function_decl|;
block|}
end_interface

end_unit

