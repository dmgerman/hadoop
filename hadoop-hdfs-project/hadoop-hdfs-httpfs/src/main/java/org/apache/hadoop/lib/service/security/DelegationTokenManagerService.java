begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.service.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|service
operator|.
name|security
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
name|classification
operator|.
name|InterfaceAudience
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
name|fs
operator|.
name|http
operator|.
name|server
operator|.
name|HttpFSServerWebApp
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
name|hdfs
operator|.
name|web
operator|.
name|SWebHdfsFileSystem
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
name|hdfs
operator|.
name|web
operator|.
name|WebHdfsFileSystem
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
name|lib
operator|.
name|server
operator|.
name|BaseService
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
name|lib
operator|.
name|server
operator|.
name|ServerException
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
name|lib
operator|.
name|server
operator|.
name|ServiceException
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
name|lib
operator|.
name|service
operator|.
name|DelegationTokenIdentifier
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
name|lib
operator|.
name|service
operator|.
name|DelegationTokenManager
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
name|lib
operator|.
name|service
operator|.
name|DelegationTokenManagerException
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
name|SecurityUtil
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
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|AbstractDelegationTokenSecretManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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

begin_comment
comment|/**  * DelegationTokenManager service implementation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DelegationTokenManagerService
specifier|public
class|class
name|DelegationTokenManagerService
extends|extends
name|BaseService
implements|implements
name|DelegationTokenManager
block|{
DECL|field|PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"delegation.token.manager"
decl_stmt|;
DECL|field|UPDATE_INTERVAL
specifier|private
specifier|static
specifier|final
name|String
name|UPDATE_INTERVAL
init|=
literal|"update.interval"
decl_stmt|;
DECL|field|MAX_LIFETIME
specifier|private
specifier|static
specifier|final
name|String
name|MAX_LIFETIME
init|=
literal|"max.lifetime"
decl_stmt|;
DECL|field|RENEW_INTERVAL
specifier|private
specifier|static
specifier|final
name|String
name|RENEW_INTERVAL
init|=
literal|"renew.interval"
decl_stmt|;
DECL|field|HOUR
specifier|private
specifier|static
specifier|final
name|long
name|HOUR
init|=
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|field|DAY
specifier|private
specifier|static
specifier|final
name|long
name|DAY
init|=
literal|24
operator|*
name|HOUR
decl_stmt|;
DECL|field|secretManager
name|DelegationTokenSecretManager
name|secretManager
init|=
literal|null
decl_stmt|;
DECL|field|tokenKind
specifier|private
name|Text
name|tokenKind
decl_stmt|;
DECL|method|DelegationTokenManagerService ()
specifier|public
name|DelegationTokenManagerService
parameter_list|()
block|{
name|super
argument_list|(
name|PREFIX
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes the service.    *    * @throws ServiceException thrown if the service could not be initialized.    */
annotation|@
name|Override
DECL|method|init ()
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|ServiceException
block|{
name|long
name|updateInterval
init|=
name|getServiceConfig
argument_list|()
operator|.
name|getLong
argument_list|(
name|UPDATE_INTERVAL
argument_list|,
name|DAY
argument_list|)
decl_stmt|;
name|long
name|maxLifetime
init|=
name|getServiceConfig
argument_list|()
operator|.
name|getLong
argument_list|(
name|MAX_LIFETIME
argument_list|,
literal|7
operator|*
name|DAY
argument_list|)
decl_stmt|;
name|long
name|renewInterval
init|=
name|getServiceConfig
argument_list|()
operator|.
name|getLong
argument_list|(
name|RENEW_INTERVAL
argument_list|,
name|DAY
argument_list|)
decl_stmt|;
name|tokenKind
operator|=
operator|(
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|isSslEnabled
argument_list|()
operator|)
condition|?
name|SWebHdfsFileSystem
operator|.
name|TOKEN_KIND
else|:
name|WebHdfsFileSystem
operator|.
name|TOKEN_KIND
expr_stmt|;
name|secretManager
operator|=
operator|new
name|DelegationTokenSecretManager
argument_list|(
name|tokenKind
argument_list|,
name|updateInterval
argument_list|,
name|maxLifetime
argument_list|,
name|renewInterval
argument_list|,
name|HOUR
argument_list|)
expr_stmt|;
try|try
block|{
name|secretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|ServiceException
operator|.
name|ERROR
operator|.
name|S12
argument_list|,
name|DelegationTokenManager
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Destroys the service.    */
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|secretManager
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
name|super
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the service interface.    *    * @return the service interface.    */
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
name|getInterface
parameter_list|()
block|{
return|return
name|DelegationTokenManager
operator|.
name|class
return|;
block|}
comment|/**    * Creates a delegation token.    *    * @param ugi UGI creating the token.    * @param renewer token renewer.    * @return new delegation token.    * @throws DelegationTokenManagerException thrown if the token could not be    * created.    */
annotation|@
name|Override
DECL|method|createToken (UserGroupInformation ugi, String renewer)
specifier|public
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|createToken
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
name|String
name|renewer
parameter_list|)
throws|throws
name|DelegationTokenManagerException
block|{
name|renewer
operator|=
operator|(
name|renewer
operator|==
literal|null
operator|)
condition|?
name|ugi
operator|.
name|getShortUserName
argument_list|()
else|:
name|renewer
expr_stmt|;
name|String
name|user
init|=
name|ugi
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|Text
name|owner
init|=
operator|new
name|Text
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|Text
name|realUser
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ugi
operator|.
name|getRealUser
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|realUser
operator|=
operator|new
name|Text
argument_list|(
name|ugi
operator|.
name|getRealUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DelegationTokenIdentifier
name|tokenIdentifier
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|(
name|tokenKind
argument_list|,
name|owner
argument_list|,
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|,
name|realUser
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|(
name|tokenIdentifier
argument_list|,
name|secretManager
argument_list|)
decl_stmt|;
try|try
block|{
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|token
argument_list|,
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServerException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|DelegationTokenManagerException
argument_list|(
name|DelegationTokenManagerException
operator|.
name|ERROR
operator|.
name|DT04
argument_list|,
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
return|return
name|token
return|;
block|}
comment|/**    * Renews a delegation token.    *    * @param token delegation token to renew.    * @param renewer token renewer.    * @return epoc expiration time.    * @throws DelegationTokenManagerException thrown if the token could not be    * renewed.    */
annotation|@
name|Override
DECL|method|renewToken (Token<DelegationTokenIdentifier> token, String renewer)
specifier|public
name|long
name|renewToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|renewer
parameter_list|)
throws|throws
name|DelegationTokenManagerException
block|{
try|try
block|{
return|return
name|secretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
name|renewer
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|DelegationTokenManagerException
argument_list|(
name|DelegationTokenManagerException
operator|.
name|ERROR
operator|.
name|DT02
argument_list|,
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Cancels a delegation token.    *    * @param token delegation token to cancel.    * @param canceler token canceler.    * @throws DelegationTokenManagerException thrown if the token could not be    * canceled.    */
annotation|@
name|Override
DECL|method|cancelToken (Token<DelegationTokenIdentifier> token, String canceler)
specifier|public
name|void
name|cancelToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|canceler
parameter_list|)
throws|throws
name|DelegationTokenManagerException
block|{
try|try
block|{
name|secretManager
operator|.
name|cancelToken
argument_list|(
name|token
argument_list|,
name|canceler
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|DelegationTokenManagerException
argument_list|(
name|DelegationTokenManagerException
operator|.
name|ERROR
operator|.
name|DT03
argument_list|,
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Verifies a delegation token.    *    * @param token delegation token to verify.    * @return the UGI for the token.    * @throws DelegationTokenManagerException thrown if the token could not be    * verified.    */
annotation|@
name|Override
DECL|method|verifyToken (Token<DelegationTokenIdentifier> token)
specifier|public
name|UserGroupInformation
name|verifyToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|DelegationTokenManagerException
block|{
name|ByteArrayInputStream
name|buf
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|DelegationTokenIdentifier
name|id
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|(
name|tokenKind
argument_list|)
decl_stmt|;
try|try
block|{
name|id
operator|.
name|readFields
argument_list|(
name|dis
argument_list|)
expr_stmt|;
name|dis
operator|.
name|close
argument_list|()
expr_stmt|;
name|secretManager
operator|.
name|verifyToken
argument_list|(
name|id
argument_list|,
name|token
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|DelegationTokenManagerException
argument_list|(
name|DelegationTokenManagerException
operator|.
name|ERROR
operator|.
name|DT01
argument_list|,
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
return|return
name|id
operator|.
name|getUser
argument_list|()
return|;
block|}
DECL|class|DelegationTokenSecretManager
specifier|private
specifier|static
class|class
name|DelegationTokenSecretManager
extends|extends
name|AbstractDelegationTokenSecretManager
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
block|{
DECL|field|tokenKind
specifier|private
name|Text
name|tokenKind
decl_stmt|;
comment|/**      * Create a secret manager      *      * @param delegationKeyUpdateInterval the number of seconds for rolling new      * secret keys.      * @param delegationTokenMaxLifetime the maximum lifetime of the delegation      * tokens      * @param delegationTokenRenewInterval how often the tokens must be renewed      * @param delegationTokenRemoverScanInterval how often the tokens are      * scanned      * for expired tokens      */
DECL|method|DelegationTokenSecretManager (Text tokenKind, long delegationKeyUpdateInterval, long delegationTokenMaxLifetime, long delegationTokenRenewInterval, long delegationTokenRemoverScanInterval)
specifier|public
name|DelegationTokenSecretManager
parameter_list|(
name|Text
name|tokenKind
parameter_list|,
name|long
name|delegationKeyUpdateInterval
parameter_list|,
name|long
name|delegationTokenMaxLifetime
parameter_list|,
name|long
name|delegationTokenRenewInterval
parameter_list|,
name|long
name|delegationTokenRemoverScanInterval
parameter_list|)
block|{
name|super
argument_list|(
name|delegationKeyUpdateInterval
argument_list|,
name|delegationTokenMaxLifetime
argument_list|,
name|delegationTokenRenewInterval
argument_list|,
name|delegationTokenRemoverScanInterval
argument_list|)
expr_stmt|;
name|this
operator|.
name|tokenKind
operator|=
name|tokenKind
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|DelegationTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|DelegationTokenIdentifier
argument_list|(
name|tokenKind
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

