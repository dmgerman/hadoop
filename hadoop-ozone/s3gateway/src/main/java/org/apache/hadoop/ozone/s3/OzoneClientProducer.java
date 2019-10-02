begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|io
operator|.
name|Text
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
name|ozone
operator|.
name|OzoneSecurityUtil
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
name|ozone
operator|.
name|client
operator|.
name|OzoneClient
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
name|ozone
operator|.
name|client
operator|.
name|OzoneClientFactory
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
name|ozone
operator|.
name|s3
operator|.
name|exception
operator|.
name|OS3Exception
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
name|ozone
operator|.
name|security
operator|.
name|OzoneTokenIdentifier
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|Token
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

begin_import
import|import
name|javax
operator|.
name|enterprise
operator|.
name|context
operator|.
name|RequestScoped
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|enterprise
operator|.
name|inject
operator|.
name|Produces
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|container
operator|.
name|ContainerRequestContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
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
name|net
operator|.
name|URISyntaxException
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMTokenProto
operator|.
name|Type
operator|.
name|S3TOKEN
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
name|ozone
operator|.
name|s3
operator|.
name|AWSAuthParser
operator|.
name|AUTHORIZATION_HEADER
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
name|ozone
operator|.
name|s3
operator|.
name|AWSAuthParser
operator|.
name|UTF_8
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
name|ozone
operator|.
name|s3
operator|.
name|exception
operator|.
name|S3ErrorTable
operator|.
name|AUTH_PROTOCOL_NOT_SUPPORTED
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
name|ozone
operator|.
name|s3
operator|.
name|exception
operator|.
name|S3ErrorTable
operator|.
name|S3_TOKEN_CREATION_ERROR
import|;
end_import

begin_comment
comment|/**  * This class creates the OzoneClient for the Rest endpoints.  */
end_comment

begin_class
annotation|@
name|RequestScoped
DECL|class|OzoneClientProducer
specifier|public
class|class
name|OzoneClientProducer
block|{
DECL|field|LOG
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OzoneClientProducer
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Context
DECL|field|context
specifier|private
name|ContainerRequestContext
name|context
decl_stmt|;
annotation|@
name|Inject
DECL|field|ozoneConfiguration
specifier|private
name|OzoneConfiguration
name|ozoneConfiguration
decl_stmt|;
annotation|@
name|Inject
DECL|field|omService
specifier|private
name|Text
name|omService
decl_stmt|;
annotation|@
name|Inject
DECL|field|omServiceID
specifier|private
name|String
name|omServiceID
decl_stmt|;
annotation|@
name|Produces
DECL|method|createClient ()
specifier|public
name|OzoneClient
name|createClient
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getClient
argument_list|(
name|ozoneConfiguration
argument_list|)
return|;
block|}
DECL|method|getClient (OzoneConfiguration config)
specifier|private
name|OzoneClient
name|getClient
parameter_list|(
name|OzoneConfiguration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|OzoneSecurityUtil
operator|.
name|isSecurityEnabled
argument_list|(
name|config
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating s3 token for client."
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getHeaderString
argument_list|(
name|AUTHORIZATION_HEADER
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"AWS4"
argument_list|)
condition|)
block|{
try|try
block|{
name|AWSV4AuthParser
name|v4RequestParser
init|=
operator|new
name|AWSV4AuthParser
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|v4RequestParser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|OzoneTokenIdentifier
name|identifier
init|=
operator|new
name|OzoneTokenIdentifier
argument_list|()
decl_stmt|;
name|identifier
operator|.
name|setTokenType
argument_list|(
name|S3TOKEN
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setStrToSign
argument_list|(
name|v4RequestParser
operator|.
name|getStringToSign
argument_list|()
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setSignature
argument_list|(
name|v4RequestParser
operator|.
name|getSignature
argument_list|()
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setAwsAccessId
argument_list|(
name|v4RequestParser
operator|.
name|getAwsAccessId
argument_list|()
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setOwner
argument_list|(
operator|new
name|Text
argument_list|(
name|v4RequestParser
operator|.
name|getAwsAccessId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Adding token for service:{}"
argument_list|,
name|omService
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|identifier
operator|.
name|getSignature
argument_list|()
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
name|identifier
operator|.
name|getKind
argument_list|()
argument_list|,
name|omService
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|remoteUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|v4RequestParser
operator|.
name|getAwsAccessId
argument_list|()
argument_list|)
decl_stmt|;
name|remoteUser
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|remoteUser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OS3Exception
decl||
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"S3 token creation failed."
argument_list|)
expr_stmt|;
throw|throw
name|S3_TOKEN_CREATION_ERROR
throw|;
block|}
block|}
else|else
block|{
throw|throw
name|AUTH_PROTOCOL_NOT_SUPPORTED
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|omServiceID
operator|==
literal|null
condition|)
block|{
return|return
name|OzoneClientFactory
operator|.
name|getClient
argument_list|(
name|ozoneConfiguration
argument_list|)
return|;
block|}
else|else
block|{
comment|// As in HA case, we need to pass om service ID.
return|return
name|OzoneClientFactory
operator|.
name|getRpcClient
argument_list|(
name|omServiceID
argument_list|,
name|ozoneConfiguration
argument_list|)
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|setContext (ContainerRequestContext context)
specifier|public
name|void
name|setContext
parameter_list|(
name|ContainerRequestContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setOzoneConfiguration (OzoneConfiguration config)
specifier|public
name|void
name|setOzoneConfiguration
parameter_list|(
name|OzoneConfiguration
name|config
parameter_list|)
block|{
name|this
operator|.
name|ozoneConfiguration
operator|=
name|config
expr_stmt|;
block|}
block|}
end_class

end_unit

