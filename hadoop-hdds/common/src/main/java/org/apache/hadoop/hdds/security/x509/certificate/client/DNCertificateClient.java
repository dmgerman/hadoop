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

begin_comment
comment|/**  * Certificate client for DataNodes.  */
end_comment

begin_class
DECL|class|DNCertificateClient
specifier|public
class|class
name|DNCertificateClient
extends|extends
name|DefaultCertificateClient
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
name|DNCertificateClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|DNCertificateClient (SecurityConfig securityConfig, String component)
name|DNCertificateClient
parameter_list|(
name|SecurityConfig
name|securityConfig
parameter_list|,
name|String
name|component
parameter_list|)
block|{
name|super
argument_list|(
name|securityConfig
argument_list|,
name|component
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
block|}
DECL|method|getLogger ()
specifier|public
name|Logger
name|getLogger
parameter_list|()
block|{
return|return
name|LOG
return|;
block|}
block|}
end_class

end_unit

