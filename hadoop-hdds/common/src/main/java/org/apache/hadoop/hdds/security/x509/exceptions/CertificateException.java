begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|security
operator|.
name|x509
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
name|hdds
operator|.
name|security
operator|.
name|exception
operator|.
name|SCMSecurityException
import|;
end_import

begin_comment
comment|/**  * Certificate Exceptions from the SCM Security layer.  */
end_comment

begin_class
DECL|class|CertificateException
specifier|public
class|class
name|CertificateException
extends|extends
name|SCMSecurityException
block|{
DECL|field|errorCode
specifier|private
name|ErrorCode
name|errorCode
decl_stmt|;
comment|/**    * Ctor.    * @param message - Error Message.    */
DECL|method|CertificateException (String message)
specifier|public
name|CertificateException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ctor.    * @param message - Message.    * @param cause  - Actual cause.    */
DECL|method|CertificateException (String message, Throwable cause)
specifier|public
name|CertificateException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ctor.    * @param message - Message.    * @param cause  - Actual cause.    * @param errorCode    */
DECL|method|CertificateException (String message, Throwable cause, ErrorCode errorCode)
specifier|public
name|CertificateException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|,
name|ErrorCode
name|errorCode
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
name|errorCode
operator|=
name|errorCode
expr_stmt|;
block|}
comment|/**    * Ctor.    * @param message - Message.    * @param errorCode    */
DECL|method|CertificateException (String message, ErrorCode errorCode)
specifier|public
name|CertificateException
parameter_list|(
name|String
name|message
parameter_list|,
name|ErrorCode
name|errorCode
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|errorCode
operator|=
name|errorCode
expr_stmt|;
block|}
comment|/**    * Ctor.    * @param cause - Base Exception.    */
DECL|method|CertificateException (Throwable cause)
specifier|public
name|CertificateException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**    * Error codes to make it easy to decode these exceptions.    */
DECL|enum|ErrorCode
specifier|public
enum|enum
name|ErrorCode
block|{
DECL|enumConstant|KEYSTORE_ERROR
name|KEYSTORE_ERROR
block|,
DECL|enumConstant|CRYPTO_SIGN_ERROR
name|CRYPTO_SIGN_ERROR
block|,
DECL|enumConstant|CERTIFICATE_ERROR
name|CERTIFICATE_ERROR
block|,
DECL|enumConstant|BOOTSTRAP_ERROR
name|BOOTSTRAP_ERROR
block|,
DECL|enumConstant|CSR_ERROR
name|CSR_ERROR
block|,
DECL|enumConstant|CRYPTO_SIGNATURE_VERIFICATION_ERROR
name|CRYPTO_SIGNATURE_VERIFICATION_ERROR
block|}
block|}
end_class

end_unit

