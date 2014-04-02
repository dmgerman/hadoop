begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
operator|.
name|HA_DT_SERVICE_PREFIX
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|fs
operator|.
name|DelegationTokenRenewer
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
name|DelegationTokenRenewer
operator|.
name|Renewable
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
name|FileSystem
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
name|HAUtil
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
name|security
operator|.
name|token
operator|.
name|delegation
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
name|net
operator|.
name|NetUtils
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
name|TokenRenewer
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
name|AbstractDelegationTokenSelector
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

begin_comment
comment|/**  * This class implements the aspects that relate to delegation tokens for all  * HTTP-based file system.  */
end_comment

begin_class
DECL|class|TokenAspect
specifier|final
class|class
name|TokenAspect
parameter_list|<
name|T
extends|extends
name|FileSystem
operator|&
name|Renewable
parameter_list|>
block|{
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|TokenManager
specifier|public
specifier|static
class|class
name|TokenManager
extends|extends
name|TokenRenewer
block|{
annotation|@
name|Override
DECL|method|cancel (Token<?> token, Configuration conf)
specifier|public
name|void
name|cancel
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|getInstance
argument_list|(
name|token
argument_list|,
name|conf
argument_list|)
operator|.
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleKind (Text kind)
specifier|public
name|boolean
name|handleKind
parameter_list|(
name|Text
name|kind
parameter_list|)
block|{
return|return
name|kind
operator|.
name|equals
argument_list|(
name|WebHdfsFileSystem
operator|.
name|TOKEN_KIND
argument_list|)
operator|||
name|kind
operator|.
name|equals
argument_list|(
name|SWebHdfsFileSystem
operator|.
name|TOKEN_KIND
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isManaged (Token<?> token)
specifier|public
name|boolean
name|isManaged
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|renew (Token<?> token, Configuration conf)
specifier|public
name|long
name|renew
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getInstance
argument_list|(
name|token
argument_list|,
name|conf
argument_list|)
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
return|;
block|}
DECL|method|getInstance (Token<?> token, Configuration conf)
specifier|private
name|TokenManagementDelegator
name|getInstance
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|URI
name|uri
decl_stmt|;
specifier|final
name|String
name|scheme
init|=
name|getSchemeByKind
argument_list|(
name|token
operator|.
name|getKind
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|HAUtil
operator|.
name|isTokenForLogicalUri
argument_list|(
name|token
argument_list|)
condition|)
block|{
name|uri
operator|=
name|HAUtil
operator|.
name|getServiceUriFromToken
argument_list|(
name|scheme
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|InetSocketAddress
name|address
init|=
name|SecurityUtil
operator|.
name|getTokenServiceAddr
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|uri
operator|=
name|URI
operator|.
name|create
argument_list|(
name|scheme
operator|+
literal|"://"
operator|+
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|address
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|TokenManagementDelegator
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
return|;
block|}
DECL|method|getSchemeByKind (Text kind)
specifier|private
specifier|static
name|String
name|getSchemeByKind
parameter_list|(
name|Text
name|kind
parameter_list|)
block|{
if|if
condition|(
name|kind
operator|.
name|equals
argument_list|(
name|WebHdfsFileSystem
operator|.
name|TOKEN_KIND
argument_list|)
condition|)
block|{
return|return
name|WebHdfsFileSystem
operator|.
name|SCHEME
return|;
block|}
elseif|else
if|if
condition|(
name|kind
operator|.
name|equals
argument_list|(
name|SWebHdfsFileSystem
operator|.
name|TOKEN_KIND
argument_list|)
condition|)
block|{
return|return
name|SWebHdfsFileSystem
operator|.
name|SCHEME
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported scheme"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|DTSelecorByKind
specifier|private
specifier|static
class|class
name|DTSelecorByKind
extends|extends
name|AbstractDelegationTokenSelector
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
block|{
DECL|method|DTSelecorByKind (final Text kind)
specifier|public
name|DTSelecorByKind
parameter_list|(
specifier|final
name|Text
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|kind
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Callbacks for token management    */
DECL|interface|TokenManagementDelegator
interface|interface
name|TokenManagementDelegator
block|{
DECL|method|cancelDelegationToken (final Token<?> token)
name|void
name|cancelDelegationToken
parameter_list|(
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|renewDelegationToken (final Token<?> token)
name|long
name|renewDelegationToken
parameter_list|(
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|field|action
specifier|private
name|DelegationTokenRenewer
operator|.
name|RenewAction
argument_list|<
name|?
argument_list|>
name|action
decl_stmt|;
DECL|field|dtRenewer
specifier|private
name|DelegationTokenRenewer
name|dtRenewer
init|=
literal|null
decl_stmt|;
DECL|field|dtSelector
specifier|private
specifier|final
name|DTSelecorByKind
name|dtSelector
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|T
name|fs
decl_stmt|;
DECL|field|hasInitedToken
specifier|private
name|boolean
name|hasInitedToken
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|final
name|Log
name|LOG
decl_stmt|;
DECL|field|serviceName
specifier|private
specifier|final
name|Text
name|serviceName
decl_stmt|;
DECL|method|TokenAspect (T fs, final Text serviceName, final Text kind)
name|TokenAspect
parameter_list|(
name|T
name|fs
parameter_list|,
specifier|final
name|Text
name|serviceName
parameter_list|,
specifier|final
name|Text
name|kind
parameter_list|)
block|{
name|this
operator|.
name|LOG
operator|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|fs
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|dtSelector
operator|=
operator|new
name|DTSelecorByKind
argument_list|(
name|kind
argument_list|)
expr_stmt|;
name|this
operator|.
name|serviceName
operator|=
name|serviceName
expr_stmt|;
block|}
DECL|method|ensureTokenInitialized ()
specifier|synchronized
name|void
name|ensureTokenInitialized
parameter_list|()
throws|throws
name|IOException
block|{
comment|// we haven't inited yet, or we used to have a token but it expired
if|if
condition|(
operator|!
name|hasInitedToken
operator|||
operator|(
name|action
operator|!=
literal|null
operator|&&
operator|!
name|action
operator|.
name|isValid
argument_list|()
operator|)
condition|)
block|{
comment|//since we don't already have a token, go get one
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
name|fs
operator|.
name|getDelegationToken
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|// security might be disabled
if|if
condition|(
name|token
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|setDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|addRenewAction
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Created new DT for "
operator|+
name|token
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|hasInitedToken
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|reset ()
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|hasInitedToken
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|initDelegationToken (UserGroupInformation ugi)
specifier|synchronized
name|void
name|initDelegationToken
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
name|selectDelegationToken
argument_list|(
name|ugi
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found existing DT for "
operator|+
name|token
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|hasInitedToken
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|removeRenewAction ()
specifier|synchronized
name|void
name|removeRenewAction
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|dtRenewer
operator|!=
literal|null
condition|)
block|{
name|dtRenewer
operator|.
name|removeRenewAction
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|selectDelegationToken ( UserGroupInformation ugi)
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|selectDelegationToken
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
return|return
name|dtSelector
operator|.
name|selectToken
argument_list|(
name|serviceName
argument_list|,
name|ugi
operator|.
name|getTokens
argument_list|()
argument_list|)
return|;
block|}
DECL|method|addRenewAction (final T webhdfs)
specifier|private
specifier|synchronized
name|void
name|addRenewAction
parameter_list|(
specifier|final
name|T
name|webhdfs
parameter_list|)
block|{
if|if
condition|(
name|dtRenewer
operator|==
literal|null
condition|)
block|{
name|dtRenewer
operator|=
name|DelegationTokenRenewer
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
name|action
operator|=
name|dtRenewer
operator|.
name|addRenewAction
argument_list|(
name|webhdfs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

