begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|math
operator|.
name|BigInteger
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

begin_comment
comment|/**  * This interface allows the DefaultCA to be portable and use different DB  * interfaces later. It also allows us define this interface in the SCM layer  * by which we don't have to take a circular dependency between hdds-common  * and the SCM.  *  * With this interface, DefaultCA server read and write DB or persistence  * layer and we can write to SCM's Metadata DB.  */
end_comment

begin_interface
DECL|interface|CertificateStore
specifier|public
interface|interface
name|CertificateStore
block|{
comment|/**    * Writes a new certificate that was issued to the persistent store.    * @param serialID - Certificate Serial Number.    * @param certificate - Certificate to persist.    * @throws IOException - on Failure.    */
DECL|method|storeValidCertificate (BigInteger serialID, X509Certificate certificate)
name|void
name|storeValidCertificate
parameter_list|(
name|BigInteger
name|serialID
parameter_list|,
name|X509Certificate
name|certificate
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Moves a certificate in a transactional manner from valid certificate to    * revoked certificate state.    * @param serialID - Serial ID of the certificate.    * @throws IOException    */
DECL|method|revokeCertificate (BigInteger serialID)
name|void
name|revokeCertificate
parameter_list|(
name|BigInteger
name|serialID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an expired certificate from the store. Please note: We don't    * remove revoked certificates, we need that information to generate the    * CRLs.    * @param serialID - Certificate ID.    */
DECL|method|removeExpiredCertificate (BigInteger serialID)
name|void
name|removeExpiredCertificate
parameter_list|(
name|BigInteger
name|serialID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Retrieves a Certificate based on the Serial number of that certificate.    * @param serialID - ID of the certificate.    * @param certType    * @return X509Certificate    * @throws IOException    */
DECL|method|getCertificateByID (BigInteger serialID, CertType certType)
name|X509Certificate
name|getCertificateByID
parameter_list|(
name|BigInteger
name|serialID
parameter_list|,
name|CertType
name|certType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Different kind of Certificate stores.    */
DECL|enum|CertType
enum|enum
name|CertType
block|{
DECL|enumConstant|VALID_CERTS
name|VALID_CERTS
block|,
DECL|enumConstant|REVOKED_CERTS
name|REVOKED_CERTS
block|}
block|}
end_interface

end_unit

