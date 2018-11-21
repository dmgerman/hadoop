begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.certificate.client
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
name|client
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
name|CertificateException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|security
operator|.
name|PublicKey
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
name|CertStore
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
name|List
import|;
end_import

begin_comment
comment|/**  * Certificate client provides and interface to certificate operations that  * needs to be performed by all clients in the Ozone eco-system.  */
end_comment

begin_interface
DECL|interface|CertificateClient
specifier|public
interface|interface
name|CertificateClient
block|{
comment|/**    * Returns the private key of the specified component if it exists on the    * local system.    *    * @param component - String name like DN, OM, SCM etc.    * @return private key or Null if there is no data.    */
DECL|method|getPrivateKey (String component)
name|PrivateKey
name|getPrivateKey
parameter_list|(
name|String
name|component
parameter_list|)
function_decl|;
comment|/**    * Returns the public key of the specified component if it exists on the local    * system.    *    * @param component - String name like DN, OM, SCM etc.    * @return public key or Null if there is no data.    */
DECL|method|getPublicKey (String component)
name|PublicKey
name|getPublicKey
parameter_list|(
name|String
name|component
parameter_list|)
function_decl|;
comment|/**    * Returns the certificate  of the specified component if it exists on the    * local system.    *    * @param component - String name like DN, OM, SCM etc.    * @return certificate or Null if there is no data.    */
DECL|method|getCertificate (String component)
name|X509Certificate
name|getCertificate
parameter_list|(
name|String
name|component
parameter_list|)
function_decl|;
comment|/**    * Verifies if this certificate is part of a trusted chain.    *    * @return true if it trusted, false otherwise.    */
DECL|method|verifyCertificate (X509Certificate certificate)
name|boolean
name|verifyCertificate
parameter_list|(
name|X509Certificate
name|certificate
parameter_list|)
function_decl|;
comment|/**    * Creates digital signature over the data stream using the components private    * key.    *    * @param stream - Data stream to sign.    * @return byte array - containing the signature.    */
DECL|method|signDataStream (InputStream stream, String component)
name|byte
index|[]
name|signDataStream
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|String
name|component
parameter_list|)
throws|throws
name|CertificateException
function_decl|;
comment|/**    * Verifies a digital Signature, given the signature and the certificate of    * the signer.    * @param stream - Data Stream.    * @param signature - Byte Array containing the signature.    * @param cert - Certificate of the Signer.    * @return true if verified, false if not.    */
DECL|method|verifySignature (InputStream stream, byte[] signature, X509Certificate cert)
name|boolean
name|verifySignature
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|byte
index|[]
name|signature
parameter_list|,
name|X509Certificate
name|cert
parameter_list|)
function_decl|;
comment|/**    * Verifies a digital Signature, given the signature and the certificate of    * the signer.    * @param data - Data in byte array.    * @param signature - Byte Array containing the signature.    * @param cert - Certificate of the Signer.    * @return true if verified, false if not.    */
DECL|method|verifySignature (byte[] data, byte[] signature, X509Certificate cert)
name|boolean
name|verifySignature
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|byte
index|[]
name|signature
parameter_list|,
name|X509Certificate
name|cert
parameter_list|)
function_decl|;
comment|/**    * Returns a CSR builder that can be used to creates a Certificate sigining    * request.    *    * @return CertificateSignRequest.Builder    */
DECL|method|getCSRBuilder ()
name|CertificateSignRequest
operator|.
name|Builder
name|getCSRBuilder
parameter_list|()
function_decl|;
comment|/**    * Get the certificate of well-known entity from SCM.    *    * @param query - String Query, please see the implementation for the    * discussion on the query formats.    * @return X509Certificate or null if not found.    */
DECL|method|queryCertificate (String query)
name|X509Certificate
name|queryCertificate
parameter_list|(
name|String
name|query
parameter_list|)
function_decl|;
comment|/**    * Stores the private key of a specified component.    *    * @param key - private key    * @param component - name of the component.    * @throws CertificateException    */
DECL|method|storePrivateKey (PrivateKey key, String component)
name|void
name|storePrivateKey
parameter_list|(
name|PrivateKey
name|key
parameter_list|,
name|String
name|component
parameter_list|)
throws|throws
name|CertificateException
function_decl|;
comment|/**    * Stores the public key of a specified component.    *    * @param key - public key    * @throws CertificateException    */
DECL|method|storePublicKey (PublicKey key, String component)
name|void
name|storePublicKey
parameter_list|(
name|PublicKey
name|key
parameter_list|,
name|String
name|component
parameter_list|)
throws|throws
name|CertificateException
function_decl|;
comment|/**    * Stores the Certificate of a specific component.    *    * @param certificate - X509 Certificate    * @param component - Name of the component.    * @throws CertificateException    */
DECL|method|storeCertificate (X509Certificate certificate, String component)
name|void
name|storeCertificate
parameter_list|(
name|X509Certificate
name|certificate
parameter_list|,
name|String
name|component
parameter_list|)
throws|throws
name|CertificateException
function_decl|;
comment|/**    * Stores the trusted chain of certificates for a specific component.    *    * @param certStore - Cert Store.    * @param component - Trust Chain.    * @throws CertificateException    */
DECL|method|storeTrustChain (CertStore certStore, String component)
name|void
name|storeTrustChain
parameter_list|(
name|CertStore
name|certStore
parameter_list|,
name|String
name|component
parameter_list|)
throws|throws
name|CertificateException
function_decl|;
comment|/**    * Stores the trusted chain of certificates for a specific component.    *    * @param certificates - List of Certificates.    * @param component - String component.    * @throws CertificateException    */
DECL|method|storeTrustChain (List<X509Certificate> certificates, String component)
name|void
name|storeTrustChain
parameter_list|(
name|List
argument_list|<
name|X509Certificate
argument_list|>
name|certificates
parameter_list|,
name|String
name|component
parameter_list|)
throws|throws
name|CertificateException
function_decl|;
block|}
end_interface

end_unit

