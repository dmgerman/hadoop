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
name|org
operator|.
name|bouncycastle
operator|.
name|operator
operator|.
name|OperatorCreationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|pkcs
operator|.
name|PKCS10CertificationRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivateKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
comment|/**  * Certificate Approver interface is used to inspectCSR a certificate.  */
end_comment

begin_interface
DECL|interface|CertificateApprover
interface|interface
name|CertificateApprover
block|{
comment|/**    * Approves a Certificate Request based on the policies of this approver.    *    * @param csr - Certificate Signing Request.    * @return - Future that will be contain the certificate or exception.    */
name|CompletableFuture
argument_list|<
name|X509CertificateHolder
argument_list|>
DECL|method|inspectCSR (PKCS10CertificationRequest csr)
name|inspectCSR
parameter_list|(
name|PKCS10CertificationRequest
name|csr
parameter_list|)
function_decl|;
comment|/**    * Approves a Certificate Request based on the policies of this approver.    *    * @param csr - Certificate Signing Request.    * @return - Future that will be contain the certificate or exception.    * @throws IOException - On Error.    */
name|CompletableFuture
argument_list|<
name|X509CertificateHolder
argument_list|>
DECL|method|inspectCSR (String csr)
name|inspectCSR
parameter_list|(
name|String
name|csr
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Sign function signs a Certificate.    * @param config - Security Config.    * @param caPrivate - CAs private Key.    * @param caCertificate - CA Certificate.    * @param validFrom - Begin Date    * @param validTill - End Date    * @param certificationRequest - Certification Request.    * @return Signed Certificate.    * @throws IOException - On Error    * @throws OperatorCreationException - on Error.    */
DECL|method|sign ( SecurityConfig config, PrivateKey caPrivate, X509CertificateHolder caCertificate, Date validFrom, Date validTill, PKCS10CertificationRequest certificationRequest)
name|X509CertificateHolder
name|sign
parameter_list|(
name|SecurityConfig
name|config
parameter_list|,
name|PrivateKey
name|caPrivate
parameter_list|,
name|X509CertificateHolder
name|caCertificate
parameter_list|,
name|Date
name|validFrom
parameter_list|,
name|Date
name|validTill
parameter_list|,
name|PKCS10CertificationRequest
name|certificationRequest
parameter_list|)
throws|throws
name|IOException
throws|,
name|OperatorCreationException
function_decl|;
comment|/**    * Approval Types for a certificate request.    */
DECL|enum|ApprovalType
enum|enum
name|ApprovalType
block|{
DECL|enumConstant|KERBEROS_TRUSTED
name|KERBEROS_TRUSTED
block|,
comment|/* The Request came from a DN using Kerberos Identity*/
DECL|enumConstant|MANUAL
name|MANUAL
block|,
comment|/* Wait for a Human being to inspect CSR of this certificate */
DECL|enumConstant|TESTING_AUTOMATIC
name|TESTING_AUTOMATIC
comment|/* For testing purpose, Automatic Approval. */
block|}
block|}
end_interface

end_unit

