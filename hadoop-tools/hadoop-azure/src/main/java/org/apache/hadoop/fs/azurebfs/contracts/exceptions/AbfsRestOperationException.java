begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contracts.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|services
operator|.
name|AzureServiceErrorCode
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
name|fs
operator|.
name|azurebfs
operator|.
name|oauth2
operator|.
name|AzureADAuthenticator
operator|.
name|HttpException
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
name|fs
operator|.
name|azurebfs
operator|.
name|services
operator|.
name|AbfsHttpOperation
import|;
end_import

begin_comment
comment|/**  * Exception to wrap Azure service error responses.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AbfsRestOperationException
specifier|public
class|class
name|AbfsRestOperationException
extends|extends
name|AzureBlobFileSystemException
block|{
DECL|field|statusCode
specifier|private
specifier|final
name|int
name|statusCode
decl_stmt|;
DECL|field|errorCode
specifier|private
specifier|final
name|AzureServiceErrorCode
name|errorCode
decl_stmt|;
DECL|field|errorMessage
specifier|private
specifier|final
name|String
name|errorMessage
decl_stmt|;
DECL|method|AbfsRestOperationException ( final int statusCode, final String errorCode, final String errorMessage, final Exception innerException)
specifier|public
name|AbfsRestOperationException
parameter_list|(
specifier|final
name|int
name|statusCode
parameter_list|,
specifier|final
name|String
name|errorCode
parameter_list|,
specifier|final
name|String
name|errorMessage
parameter_list|,
specifier|final
name|Exception
name|innerException
parameter_list|)
block|{
name|super
argument_list|(
literal|"Status code: "
operator|+
name|statusCode
operator|+
literal|" error code: "
operator|+
name|errorCode
operator|+
literal|" error message: "
operator|+
name|errorMessage
argument_list|,
name|innerException
argument_list|)
expr_stmt|;
name|this
operator|.
name|statusCode
operator|=
name|statusCode
expr_stmt|;
name|this
operator|.
name|errorCode
operator|=
name|AzureServiceErrorCode
operator|.
name|getAzureServiceCode
argument_list|(
name|this
operator|.
name|statusCode
argument_list|,
name|errorCode
argument_list|)
expr_stmt|;
name|this
operator|.
name|errorMessage
operator|=
name|errorMessage
expr_stmt|;
block|}
DECL|method|AbfsRestOperationException ( final int statusCode, final String errorCode, final String errorMessage, final Exception innerException, final AbfsHttpOperation abfsHttpOperation)
specifier|public
name|AbfsRestOperationException
parameter_list|(
specifier|final
name|int
name|statusCode
parameter_list|,
specifier|final
name|String
name|errorCode
parameter_list|,
specifier|final
name|String
name|errorMessage
parameter_list|,
specifier|final
name|Exception
name|innerException
parameter_list|,
specifier|final
name|AbfsHttpOperation
name|abfsHttpOperation
parameter_list|)
block|{
name|super
argument_list|(
name|formatMessage
argument_list|(
name|abfsHttpOperation
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|statusCode
operator|=
name|statusCode
expr_stmt|;
name|this
operator|.
name|errorCode
operator|=
name|AzureServiceErrorCode
operator|.
name|getAzureServiceCode
argument_list|(
name|this
operator|.
name|statusCode
argument_list|,
name|errorCode
argument_list|)
expr_stmt|;
name|this
operator|.
name|errorMessage
operator|=
name|errorMessage
expr_stmt|;
block|}
DECL|method|AbfsRestOperationException (final HttpException innerException)
specifier|public
name|AbfsRestOperationException
parameter_list|(
specifier|final
name|HttpException
name|innerException
parameter_list|)
block|{
name|super
argument_list|(
name|innerException
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|statusCode
operator|=
name|innerException
operator|.
name|getHttpErrorCode
argument_list|()
expr_stmt|;
name|this
operator|.
name|errorCode
operator|=
name|AzureServiceErrorCode
operator|.
name|UNKNOWN
expr_stmt|;
name|this
operator|.
name|errorMessage
operator|=
name|innerException
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
DECL|method|getStatusCode ()
specifier|public
name|int
name|getStatusCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|statusCode
return|;
block|}
DECL|method|getErrorCode ()
specifier|public
name|AzureServiceErrorCode
name|getErrorCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|errorCode
return|;
block|}
DECL|method|getErrorMessage ()
specifier|public
name|String
name|getErrorMessage
parameter_list|()
block|{
return|return
name|this
operator|.
name|errorMessage
return|;
block|}
DECL|method|formatMessage (final AbfsHttpOperation abfsHttpOperation)
specifier|private
specifier|static
name|String
name|formatMessage
parameter_list|(
specifier|final
name|AbfsHttpOperation
name|abfsHttpOperation
parameter_list|)
block|{
comment|// HEAD request response doesn't have StorageErrorCode, StorageErrorMessage.
if|if
condition|(
name|abfsHttpOperation
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
literal|"HEAD"
argument_list|)
condition|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Operation failed: \"%1$s\", %2$s, HEAD, %3$s"
argument_list|,
name|abfsHttpOperation
operator|.
name|getStatusDescription
argument_list|()
argument_list|,
name|abfsHttpOperation
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|abfsHttpOperation
operator|.
name|getUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Operation failed: \"%1$s\", %2$s, %3$s, %4$s, %5$s, \"%6$s\""
argument_list|,
name|abfsHttpOperation
operator|.
name|getStatusDescription
argument_list|()
argument_list|,
name|abfsHttpOperation
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|abfsHttpOperation
operator|.
name|getMethod
argument_list|()
argument_list|,
name|abfsHttpOperation
operator|.
name|getUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|abfsHttpOperation
operator|.
name|getStorageErrorCode
argument_list|()
argument_list|,
comment|// Remove break line to ensure the request id and timestamp can be shown in console.
name|abfsHttpOperation
operator|.
name|getStorageErrorMessage
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|" "
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

