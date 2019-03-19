begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.transport.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|transport
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|token
operator|.
name|BlockTokenVerifier
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
name|token
operator|.
name|TokenVerifier
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
operator|.
name|CertificateClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import static
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
operator|.
name|ErrorCode
operator|.
name|MISSING_BLOCK_TOKEN
import|;
end_import

begin_comment
comment|/**  * A server endpoint that acts as the communication layer for Ozone containers.  */
end_comment

begin_class
DECL|class|XceiverServer
specifier|public
specifier|abstract
class|class
name|XceiverServer
implements|implements
name|XceiverServerSpi
block|{
DECL|field|secConfig
specifier|private
specifier|final
name|SecurityConfig
name|secConfig
decl_stmt|;
DECL|field|tokenVerifier
specifier|private
specifier|final
name|TokenVerifier
name|tokenVerifier
decl_stmt|;
DECL|field|caClient
specifier|private
specifier|final
name|CertificateClient
name|caClient
decl_stmt|;
DECL|method|XceiverServer (Configuration conf, CertificateClient client)
specifier|public
name|XceiverServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|CertificateClient
name|client
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|secConfig
operator|=
operator|new
name|SecurityConfig
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|caClient
operator|=
name|client
expr_stmt|;
name|tokenVerifier
operator|=
operator|new
name|BlockTokenVerifier
argument_list|(
name|secConfig
argument_list|,
name|getCaClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Default implementation which just validates security token if security is    * enabled.    *    * @param request ContainerCommandRequest    */
annotation|@
name|Override
DECL|method|submitRequest (ContainerCommandRequestProto request, HddsProtos.PipelineID pipelineID)
specifier|public
name|void
name|submitRequest
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|HddsProtos
operator|.
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|secConfig
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|String
name|encodedToken
init|=
name|request
operator|.
name|getEncodedToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|encodedToken
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SCMSecurityException
argument_list|(
literal|"Security is enabled but client "
operator|+
literal|"request is missing block token."
argument_list|,
name|MISSING_BLOCK_TOKEN
argument_list|)
throw|;
block|}
name|tokenVerifier
operator|.
name|verify
argument_list|(
name|encodedToken
argument_list|,
name|encodedToken
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCaClient ()
specifier|protected
name|CertificateClient
name|getCaClient
parameter_list|()
block|{
return|return
name|caClient
return|;
block|}
DECL|method|getSecurityConfig ()
specifier|protected
name|SecurityConfig
name|getSecurityConfig
parameter_list|()
block|{
return|return
name|secConfig
return|;
block|}
DECL|method|getBlockTokenVerifier ()
specifier|protected
name|TokenVerifier
name|getBlockTokenVerifier
parameter_list|()
block|{
return|return
name|tokenVerifier
return|;
block|}
DECL|method|getSecConfig ()
specifier|public
name|SecurityConfig
name|getSecConfig
parameter_list|()
block|{
return|return
name|secConfig
return|;
block|}
block|}
end_class

end_unit

