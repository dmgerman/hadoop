begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|server
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
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
name|scm
operator|.
name|metadata
operator|.
name|SCMMetadataStore
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
name|exception
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
name|certificate
operator|.
name|authority
operator|.
name|CertificateStore
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
name|utils
operator|.
name|db
operator|.
name|BatchOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A Certificate Store class that persists certificates issued by SCM CA.  */
end_comment

begin_class
DECL|class|SCMCertStore
specifier|public
class|class
name|SCMCertStore
implements|implements
name|CertificateStore
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SCMCertStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|scmMetadataStore
specifier|private
specifier|final
name|SCMMetadataStore
name|scmMetadataStore
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
DECL|method|SCMCertStore (SCMMetadataStore dbStore)
specifier|public
name|SCMCertStore
parameter_list|(
name|SCMMetadataStore
name|dbStore
parameter_list|)
block|{
name|this
operator|.
name|scmMetadataStore
operator|=
name|dbStore
expr_stmt|;
name|lock
operator|=
operator|new
name|ReentrantLock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storeValidCertificate (BigInteger serialID, X509Certificate certificate)
specifier|public
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
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// This makes sure that no certificate IDs are reusable.
if|if
condition|(
operator|(
name|getCertificateByID
argument_list|(
name|serialID
argument_list|,
name|CertType
operator|.
name|VALID_CERTS
argument_list|)
operator|==
literal|null
operator|)
operator|&&
operator|(
name|getCertificateByID
argument_list|(
name|serialID
argument_list|,
name|CertType
operator|.
name|REVOKED_CERTS
argument_list|)
operator|==
literal|null
operator|)
condition|)
block|{
name|scmMetadataStore
operator|.
name|getValidCertsTable
argument_list|()
operator|.
name|put
argument_list|(
name|serialID
argument_list|,
name|certificate
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SCMSecurityException
argument_list|(
literal|"Conflicting certificate ID"
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|revokeCertificate (BigInteger serialID)
specifier|public
name|void
name|revokeCertificate
parameter_list|(
name|BigInteger
name|serialID
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|X509Certificate
name|cert
init|=
name|getCertificateByID
argument_list|(
name|serialID
argument_list|,
name|CertType
operator|.
name|VALID_CERTS
argument_list|)
decl_stmt|;
if|if
condition|(
name|cert
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"trying to revoke a certificate that is not valid. Serial: "
operator|+
literal|"{}"
argument_list|,
name|serialID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SCMSecurityException
argument_list|(
literal|"Trying to revoke an invalid "
operator|+
literal|"certificate."
argument_list|)
throw|;
block|}
comment|// TODO : Check if we are trying to revoke an expired certificate.
if|if
condition|(
name|getCertificateByID
argument_list|(
name|serialID
argument_list|,
name|CertType
operator|.
name|REVOKED_CERTS
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Trying to revoke a certificate that is already revoked."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SCMSecurityException
argument_list|(
literal|"Trying to revoke an already revoked "
operator|+
literal|"certificate."
argument_list|)
throw|;
block|}
comment|// let is do this in a transaction.
try|try
init|(
name|BatchOperation
name|batch
init|=
name|scmMetadataStore
operator|.
name|getStore
argument_list|()
operator|.
name|initBatchOperation
argument_list|()
init|;
init|)
block|{
name|scmMetadataStore
operator|.
name|getRevokedCertsTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batch
argument_list|,
name|serialID
argument_list|,
name|cert
argument_list|)
expr_stmt|;
name|scmMetadataStore
operator|.
name|getValidCertsTable
argument_list|()
operator|.
name|deleteWithBatch
argument_list|(
name|batch
argument_list|,
name|serialID
argument_list|)
expr_stmt|;
name|scmMetadataStore
operator|.
name|getStore
argument_list|()
operator|.
name|commitBatchOperation
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeExpiredCertificate (BigInteger serialID)
specifier|public
name|void
name|removeExpiredCertificate
parameter_list|(
name|BigInteger
name|serialID
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: Later this allows removal of expired certificates from the system.
block|}
annotation|@
name|Override
DECL|method|getCertificateByID (BigInteger serialID, CertType certType)
specifier|public
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
block|{
if|if
condition|(
name|certType
operator|==
name|CertType
operator|.
name|VALID_CERTS
condition|)
block|{
return|return
name|scmMetadataStore
operator|.
name|getValidCertsTable
argument_list|()
operator|.
name|get
argument_list|(
name|serialID
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|scmMetadataStore
operator|.
name|getRevokedCertsTable
argument_list|()
operator|.
name|get
argument_list|(
name|serialID
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

