begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.exceptions
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
name|exceptions
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Exception thrown by Ozone Manager.  */
end_comment

begin_class
DECL|class|OMException
specifier|public
class|class
name|OMException
extends|extends
name|IOException
block|{
DECL|field|STATUS_CODE
specifier|public
specifier|static
specifier|final
name|String
name|STATUS_CODE
init|=
literal|"STATUS_CODE="
decl_stmt|;
DECL|field|result
specifier|private
specifier|final
name|OMException
operator|.
name|ResultCodes
name|result
decl_stmt|;
comment|/**    * Constructs an {@code IOException} with {@code null}    * as its error detail message.    */
DECL|method|OMException (OMException.ResultCodes result)
specifier|public
name|OMException
parameter_list|(
name|OMException
operator|.
name|ResultCodes
name|result
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
comment|/**    * Constructs an {@code IOException} with the specified detail message.    *    * @param message The detail message (which is saved for later retrieval by    * the    * {@link #getMessage()} method)    */
DECL|method|OMException (String message, OMException.ResultCodes result)
specifier|public
name|OMException
parameter_list|(
name|String
name|message
parameter_list|,
name|OMException
operator|.
name|ResultCodes
name|result
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
comment|/**    * Constructs an {@code IOException} with the specified detail message    * and cause.    *<p>    *<p> Note that the detail message associated with {@code cause} is    *<i>not</i> automatically incorporated into this exception's detail    * message.    *    * @param message The detail message (which is saved for later retrieval by    * the    * {@link #getMessage()} method)    * @param cause The cause (which is saved for later retrieval by the {@link    * #getCause()} method).  (A null value is permitted, and indicates that the    * cause is nonexistent or unknown.)    * @since 1.6    */
DECL|method|OMException (String message, Throwable cause, OMException.ResultCodes result)
specifier|public
name|OMException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|,
name|OMException
operator|.
name|ResultCodes
name|result
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
comment|/**    * Constructs an {@code IOException} with the specified cause and a    * detail message of {@code (cause==null ? null : cause.toString())}    * (which typically contains the class and detail message of {@code cause}).    * This constructor is useful for IO exceptions that are little more    * than wrappers for other throwables.    *    * @param cause The cause (which is saved for later retrieval by the {@link    * #getCause()} method).  (A null value is permitted, and indicates that the    * cause is nonexistent or unknown.)    * @since 1.6    */
DECL|method|OMException (Throwable cause, OMException.ResultCodes result)
specifier|public
name|OMException
parameter_list|(
name|Throwable
name|cause
parameter_list|,
name|OMException
operator|.
name|ResultCodes
name|result
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
comment|/**    * Returns resultCode.    * @return ResultCode    */
DECL|method|getResult ()
specifier|public
name|OMException
operator|.
name|ResultCodes
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|result
operator|+
literal|" "
operator|+
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Error codes to make it easy to decode these exceptions.    */
DECL|enum|ResultCodes
specifier|public
enum|enum
name|ResultCodes
block|{
DECL|enumConstant|OK
name|OK
block|,
DECL|enumConstant|VOLUME_NOT_UNIQUE
name|VOLUME_NOT_UNIQUE
block|,
DECL|enumConstant|VOLUME_NOT_FOUND
name|VOLUME_NOT_FOUND
block|,
DECL|enumConstant|VOLUME_NOT_EMPTY
name|VOLUME_NOT_EMPTY
block|,
DECL|enumConstant|VOLUME_ALREADY_EXISTS
name|VOLUME_ALREADY_EXISTS
block|,
DECL|enumConstant|USER_NOT_FOUND
name|USER_NOT_FOUND
block|,
DECL|enumConstant|USER_TOO_MANY_VOLUMES
name|USER_TOO_MANY_VOLUMES
block|,
DECL|enumConstant|BUCKET_NOT_FOUND
name|BUCKET_NOT_FOUND
block|,
DECL|enumConstant|BUCKET_NOT_EMPTY
name|BUCKET_NOT_EMPTY
block|,
DECL|enumConstant|BUCKET_ALREADY_EXISTS
name|BUCKET_ALREADY_EXISTS
block|,
DECL|enumConstant|KEY_ALREADY_EXISTS
name|KEY_ALREADY_EXISTS
block|,
DECL|enumConstant|KEY_NOT_FOUND
name|KEY_NOT_FOUND
block|,
DECL|enumConstant|INVALID_KEY_NAME
name|INVALID_KEY_NAME
block|,
DECL|enumConstant|ACCESS_DENIED
name|ACCESS_DENIED
block|,
DECL|enumConstant|INTERNAL_ERROR
name|INTERNAL_ERROR
block|,
DECL|enumConstant|KEY_ALLOCATION_ERROR
name|KEY_ALLOCATION_ERROR
block|,
DECL|enumConstant|KEY_DELETION_ERROR
name|KEY_DELETION_ERROR
block|,
DECL|enumConstant|KEY_RENAME_ERROR
name|KEY_RENAME_ERROR
block|,
DECL|enumConstant|METADATA_ERROR
name|METADATA_ERROR
block|,
DECL|enumConstant|OM_NOT_INITIALIZED
name|OM_NOT_INITIALIZED
block|,
DECL|enumConstant|SCM_VERSION_MISMATCH_ERROR
name|SCM_VERSION_MISMATCH_ERROR
block|,
DECL|enumConstant|S3_BUCKET_NOT_FOUND
name|S3_BUCKET_NOT_FOUND
block|,
DECL|enumConstant|S3_BUCKET_ALREADY_EXISTS
name|S3_BUCKET_ALREADY_EXISTS
block|,
DECL|enumConstant|INITIATE_MULTIPART_UPLOAD_ERROR
name|INITIATE_MULTIPART_UPLOAD_ERROR
block|,
DECL|enumConstant|MULTIPART_UPLOAD_PARTFILE_ERROR
name|MULTIPART_UPLOAD_PARTFILE_ERROR
block|,
DECL|enumConstant|NO_SUCH_MULTIPART_UPLOAD_ERROR
name|NO_SUCH_MULTIPART_UPLOAD_ERROR
block|,
DECL|enumConstant|MISMATCH_MULTIPART_LIST
name|MISMATCH_MULTIPART_LIST
block|,
DECL|enumConstant|MISSING_UPLOAD_PARTS
name|MISSING_UPLOAD_PARTS
block|,
DECL|enumConstant|COMPLETE_MULTIPART_UPLOAD_ERROR
name|COMPLETE_MULTIPART_UPLOAD_ERROR
block|,
DECL|enumConstant|ENTITY_TOO_SMALL
name|ENTITY_TOO_SMALL
block|,
DECL|enumConstant|ABORT_MULTIPART_UPLOAD_FAILED
name|ABORT_MULTIPART_UPLOAD_FAILED
block|,
DECL|enumConstant|S3_SECRET_NOT_FOUND
name|S3_SECRET_NOT_FOUND
block|,
DECL|enumConstant|INVALID_AUTH_METHOD
name|INVALID_AUTH_METHOD
block|,
DECL|enumConstant|INVALID_TOKEN
name|INVALID_TOKEN
block|,
DECL|enumConstant|TOKEN_EXPIRED
name|TOKEN_EXPIRED
block|,
DECL|enumConstant|TOKEN_ERROR_OTHER
name|TOKEN_ERROR_OTHER
block|,
DECL|enumConstant|LIST_MULTIPART_UPLOAD_PARTS_FAILED
name|LIST_MULTIPART_UPLOAD_PARTS_FAILED
block|,
DECL|enumConstant|SCM_IN_CHILL_MODE
name|SCM_IN_CHILL_MODE
block|,
DECL|enumConstant|INVALID_REQUEST
name|INVALID_REQUEST
block|,
DECL|enumConstant|BUCKET_ENCRYPTION_KEY_NOT_FOUND
name|BUCKET_ENCRYPTION_KEY_NOT_FOUND
block|,
DECL|enumConstant|UNKNOWN_CIPHER_SUITE
name|UNKNOWN_CIPHER_SUITE
block|,
DECL|enumConstant|INVALID_KMS_PROVIDER
name|INVALID_KMS_PROVIDER
block|,
DECL|enumConstant|TOKEN_CREATION_ERROR
name|TOKEN_CREATION_ERROR
block|,
DECL|enumConstant|FILE_NOT_FOUND
name|FILE_NOT_FOUND
block|}
block|}
end_class

end_unit

