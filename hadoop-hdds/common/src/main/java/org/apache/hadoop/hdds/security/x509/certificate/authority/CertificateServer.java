begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.certificate.authority
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
name|certificate
operator|.
name|authority
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
name|x509
operator|.
name|certificates
operator|.
name|CertificateSignRequest
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
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|exceptions
operator|.
name|SCMSecurityException
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
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|SecurityConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|cert
operator|.
name|X509CertificateHolder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
name|Future
import|;
end_import

begin_comment
comment|/**  * Interface for Certificate Authority. This can be extended to talk to external  * CAs later or HSMs later.  */
end_comment

begin_interface
DECL|interface|CertificateServer
specifier|public
interface|interface
name|CertificateServer
block|{
comment|/**    * Initialize the Certificate Authority.    *    * @param securityConfig - Security Configuration.    * @param type - The Type of CertificateServer we are creating, we make this    * explicit so that when we read code it is visible to the users.    * @throws SCMSecurityException - Throws if the init fails.    */
DECL|method|init (SecurityConfig securityConfig, CAType type)
name|void
name|init
parameter_list|(
name|SecurityConfig
name|securityConfig
parameter_list|,
name|CAType
name|type
parameter_list|)
throws|throws
name|SCMSecurityException
function_decl|;
comment|/**    * Returns the CA Certificate for this CA.    *    * @return X509CertificateHolder - Certificate for this CA.    * @throws SCMSecurityException -- usually thrown if this CA is not    *                              initialized.    */
DECL|method|getCACertificate ()
name|X509CertificateHolder
name|getCACertificate
parameter_list|()
throws|throws
name|SCMSecurityException
function_decl|;
comment|/**    * Request a Certificate based on Certificate Signing Request.    *    * @param csr - Certificate Signing Request.    * @return A future that will have this certificate when this request is    * approved.    * @throws SCMSecurityException - on Error.    */
DECL|method|requestCertificate (CertificateSignRequest csr, CertificateApprover approver)
name|Future
argument_list|<
name|X509CertificateHolder
argument_list|>
name|requestCertificate
parameter_list|(
name|CertificateSignRequest
name|csr
parameter_list|,
name|CertificateApprover
name|approver
parameter_list|)
throws|throws
name|SCMSecurityException
function_decl|;
comment|/**    * Revokes a Certificate issued by this CertificateServer.    *    * @param certificate - Certificate to revoke    * @param approver - Approval process to follow.    * @return Future that tells us what happened.    * @throws SCMSecurityException - on Error.    */
DECL|method|revokeCertificate (X509Certificate certificate, CertificateApprover approver)
name|Future
argument_list|<
name|Boolean
argument_list|>
name|revokeCertificate
parameter_list|(
name|X509Certificate
name|certificate
parameter_list|,
name|CertificateApprover
name|approver
parameter_list|)
throws|throws
name|SCMSecurityException
function_decl|;
comment|/**    * TODO : CRL, OCSP etc. Later. This is the start of a CertificateServer    * framework.    */
comment|/**    * Approval Types for a certificate request.    */
DECL|enum|CertificateApprover
enum|enum
name|CertificateApprover
block|{
DECL|enumConstant|KERBEROS_TRUSTED
name|KERBEROS_TRUSTED
block|,
comment|/* The Request came from a DN using Kerberos Identity*/
DECL|enumConstant|MANUAL
name|MANUAL
block|,
comment|/* Wait for a Human being to approve this certificate */
DECL|enumConstant|TESTING_AUTOMATIC
name|TESTING_AUTOMATIC
comment|/* For testing purpose, Automatic Approval. */
block|}
comment|/**    * Make it explicit what type of CertificateServer we are creating here.    */
DECL|enum|CAType
enum|enum
name|CAType
block|{
DECL|enumConstant|SELF_SIGNED_CA
name|SELF_SIGNED_CA
block|,
DECL|enumConstant|INTERMEDIARY_CA
name|INTERMEDIARY_CA
block|}
block|}
end_interface

end_unit

