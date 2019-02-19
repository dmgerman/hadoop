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
comment|/**  *  */
end_comment

begin_class
DECL|class|MockCAStore
specifier|public
class|class
name|MockCAStore
implements|implements
name|CertificateStore
block|{
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
block|{    }
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
block|{    }
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
block|{    }
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
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

