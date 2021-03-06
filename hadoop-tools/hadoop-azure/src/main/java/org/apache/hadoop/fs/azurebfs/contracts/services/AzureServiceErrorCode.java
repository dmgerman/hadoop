begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contracts.services
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
name|services
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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

begin_comment
comment|/**  * Azure service error codes.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|enum|AzureServiceErrorCode
specifier|public
enum|enum
name|AzureServiceErrorCode
block|{
DECL|enumConstant|FILE_SYSTEM_ALREADY_EXISTS
name|FILE_SYSTEM_ALREADY_EXISTS
argument_list|(
literal|"FilesystemAlreadyExists"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_CONFLICT
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|PATH_ALREADY_EXISTS
name|PATH_ALREADY_EXISTS
argument_list|(
literal|"PathAlreadyExists"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_CONFLICT
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|INTERNAL_OPERATION_ABORT
name|INTERNAL_OPERATION_ABORT
argument_list|(
literal|"InternalOperationAbortError"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_CONFLICT
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|PATH_CONFLICT
name|PATH_CONFLICT
argument_list|(
literal|"PathConflict"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_CONFLICT
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|FILE_SYSTEM_NOT_FOUND
name|FILE_SYSTEM_NOT_FOUND
argument_list|(
literal|"FilesystemNotFound"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|PATH_NOT_FOUND
name|PATH_NOT_FOUND
argument_list|(
literal|"PathNotFound"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|PRE_CONDITION_FAILED
name|PRE_CONDITION_FAILED
argument_list|(
literal|"PreconditionFailed"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_PRECON_FAILED
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|SOURCE_PATH_NOT_FOUND
name|SOURCE_PATH_NOT_FOUND
argument_list|(
literal|"SourcePathNotFound"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|INVALID_SOURCE_OR_DESTINATION_RESOURCE_TYPE
name|INVALID_SOURCE_OR_DESTINATION_RESOURCE_TYPE
argument_list|(
literal|"InvalidSourceOrDestinationResourceType"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_CONFLICT
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|RENAME_DESTINATION_PARENT_PATH_NOT_FOUND
name|RENAME_DESTINATION_PARENT_PATH_NOT_FOUND
argument_list|(
literal|"RenameDestinationParentPathNotFound"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|INVALID_RENAME_SOURCE_PATH
name|INVALID_RENAME_SOURCE_PATH
argument_list|(
literal|"InvalidRenameSourcePath"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_CONFLICT
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|INGRESS_OVER_ACCOUNT_LIMIT
name|INGRESS_OVER_ACCOUNT_LIMIT
argument_list|(
literal|null
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_UNAVAILABLE
argument_list|,
literal|"Ingress is over the account limit."
argument_list|)
block|,
DECL|enumConstant|EGRESS_OVER_ACCOUNT_LIMIT
name|EGRESS_OVER_ACCOUNT_LIMIT
argument_list|(
literal|null
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_UNAVAILABLE
argument_list|,
literal|"Egress is over the account limit."
argument_list|)
block|,
DECL|enumConstant|INVALID_QUERY_PARAMETER_VALUE
name|INVALID_QUERY_PARAMETER_VALUE
argument_list|(
literal|"InvalidQueryParameterValue"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_BAD_REQUEST
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|AUTHORIZATION_PERMISSION_MISS_MATCH
name|AUTHORIZATION_PERMISSION_MISS_MATCH
argument_list|(
literal|"AuthorizationPermissionMismatch"
argument_list|,
name|HttpURLConnection
operator|.
name|HTTP_FORBIDDEN
argument_list|,
literal|null
argument_list|)
block|,
DECL|enumConstant|UNKNOWN
name|UNKNOWN
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
block|;
DECL|field|errorCode
specifier|private
specifier|final
name|String
name|errorCode
decl_stmt|;
DECL|field|httpStatusCode
specifier|private
specifier|final
name|int
name|httpStatusCode
decl_stmt|;
DECL|field|errorMessage
specifier|private
specifier|final
name|String
name|errorMessage
decl_stmt|;
DECL|method|AzureServiceErrorCode (String errorCode, int httpStatusCodes, String errorMessage)
name|AzureServiceErrorCode
parameter_list|(
name|String
name|errorCode
parameter_list|,
name|int
name|httpStatusCodes
parameter_list|,
name|String
name|errorMessage
parameter_list|)
block|{
name|this
operator|.
name|errorCode
operator|=
name|errorCode
expr_stmt|;
name|this
operator|.
name|httpStatusCode
operator|=
name|httpStatusCodes
expr_stmt|;
name|this
operator|.
name|errorMessage
operator|=
name|errorMessage
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
name|httpStatusCode
return|;
block|}
DECL|method|getErrorCode ()
specifier|public
name|String
name|getErrorCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|errorCode
return|;
block|}
DECL|method|getAzureServiceCode (int httpStatusCode)
specifier|public
specifier|static
name|List
argument_list|<
name|AzureServiceErrorCode
argument_list|>
name|getAzureServiceCode
parameter_list|(
name|int
name|httpStatusCode
parameter_list|)
block|{
name|List
argument_list|<
name|AzureServiceErrorCode
argument_list|>
name|errorCodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|httpStatusCode
operator|==
name|UNKNOWN
operator|.
name|httpStatusCode
condition|)
block|{
name|errorCodes
operator|.
name|add
argument_list|(
name|UNKNOWN
argument_list|)
expr_stmt|;
return|return
name|errorCodes
return|;
block|}
for|for
control|(
name|AzureServiceErrorCode
name|azureServiceErrorCode
range|:
name|AzureServiceErrorCode
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|azureServiceErrorCode
operator|.
name|httpStatusCode
operator|==
name|httpStatusCode
condition|)
block|{
name|errorCodes
operator|.
name|add
argument_list|(
name|azureServiceErrorCode
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|errorCodes
return|;
block|}
DECL|method|getAzureServiceCode (int httpStatusCode, String errorCode)
specifier|public
specifier|static
name|AzureServiceErrorCode
name|getAzureServiceCode
parameter_list|(
name|int
name|httpStatusCode
parameter_list|,
name|String
name|errorCode
parameter_list|)
block|{
if|if
condition|(
name|errorCode
operator|==
literal|null
operator|||
name|errorCode
operator|.
name|isEmpty
argument_list|()
operator|||
name|httpStatusCode
operator|==
name|UNKNOWN
operator|.
name|httpStatusCode
condition|)
block|{
return|return
name|UNKNOWN
return|;
block|}
for|for
control|(
name|AzureServiceErrorCode
name|azureServiceErrorCode
range|:
name|AzureServiceErrorCode
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|errorCode
operator|.
name|equalsIgnoreCase
argument_list|(
name|azureServiceErrorCode
operator|.
name|errorCode
argument_list|)
operator|&&
name|azureServiceErrorCode
operator|.
name|httpStatusCode
operator|==
name|httpStatusCode
condition|)
block|{
return|return
name|azureServiceErrorCode
return|;
block|}
block|}
return|return
name|UNKNOWN
return|;
block|}
DECL|method|getAzureServiceCode (int httpStatusCode, String errorCode, final String errorMessage)
specifier|public
specifier|static
name|AzureServiceErrorCode
name|getAzureServiceCode
parameter_list|(
name|int
name|httpStatusCode
parameter_list|,
name|String
name|errorCode
parameter_list|,
specifier|final
name|String
name|errorMessage
parameter_list|)
block|{
if|if
condition|(
name|errorCode
operator|==
literal|null
operator|||
name|errorCode
operator|.
name|isEmpty
argument_list|()
operator|||
name|httpStatusCode
operator|==
name|UNKNOWN
operator|.
name|httpStatusCode
operator|||
name|errorMessage
operator|==
literal|null
operator|||
name|errorMessage
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|UNKNOWN
return|;
block|}
for|for
control|(
name|AzureServiceErrorCode
name|azureServiceErrorCode
range|:
name|AzureServiceErrorCode
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|azureServiceErrorCode
operator|.
name|httpStatusCode
operator|==
name|httpStatusCode
operator|&&
name|errorCode
operator|.
name|equalsIgnoreCase
argument_list|(
name|azureServiceErrorCode
operator|.
name|errorCode
argument_list|)
operator|&&
name|errorMessage
operator|.
name|equalsIgnoreCase
argument_list|(
name|azureServiceErrorCode
operator|.
name|errorMessage
argument_list|)
condition|)
block|{
return|return
name|azureServiceErrorCode
return|;
block|}
block|}
return|return
name|UNKNOWN
return|;
block|}
block|}
end_enum

end_unit

