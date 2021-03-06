begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
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
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|container
operator|.
name|ContainerException
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
name|AccessControlException
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
name|authentication
operator|.
name|client
operator|.
name|AuthenticationException
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
name|authorize
operator|.
name|AuthorizationException
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
name|util
operator|.
name|HttpExceptionUtils
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
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
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
name|ext
operator|.
name|ExceptionMapper
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
name|ext
operator|.
name|Provider
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
comment|/**  * Jersey provider that converts KMS exceptions into detailed HTTP errors.  */
end_comment

begin_class
annotation|@
name|Provider
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSExceptionsProvider
specifier|public
class|class
name|KMSExceptionsProvider
implements|implements
name|ExceptionMapper
argument_list|<
name|Exception
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KMSExceptionsProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EXCEPTION_LOG
specifier|private
specifier|final
specifier|static
name|Logger
name|EXCEPTION_LOG
init|=
name|KMS
operator|.
name|LOG
decl_stmt|;
DECL|field|ENTER
specifier|private
specifier|static
specifier|final
name|String
name|ENTER
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|method|createResponse (Response.Status status, Throwable ex)
specifier|protected
name|Response
name|createResponse
parameter_list|(
name|Response
operator|.
name|Status
name|status
parameter_list|,
name|Throwable
name|ex
parameter_list|)
block|{
return|return
name|HttpExceptionUtils
operator|.
name|createJerseyExceptionResponse
argument_list|(
name|status
argument_list|,
name|ex
argument_list|)
return|;
block|}
DECL|method|getOneLineMessage (Throwable exception)
specifier|protected
name|String
name|getOneLineMessage
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|String
name|message
init|=
name|exception
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|int
name|i
init|=
name|message
operator|.
name|indexOf
argument_list|(
name|ENTER
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
operator|-
literal|1
condition|)
block|{
name|message
operator|=
name|message
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|message
return|;
block|}
comment|/**    * Maps different exceptions thrown by KMS to HTTP status codes.    */
annotation|@
name|Override
DECL|method|toResponse (Exception exception)
specifier|public
name|Response
name|toResponse
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
name|Response
operator|.
name|Status
name|status
decl_stmt|;
name|boolean
name|doAudit
init|=
literal|true
decl_stmt|;
name|Throwable
name|throwable
init|=
name|exception
decl_stmt|;
if|if
condition|(
name|exception
operator|instanceof
name|ContainerException
condition|)
block|{
name|throwable
operator|=
name|exception
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|throwable
operator|instanceof
name|SecurityException
condition|)
block|{
name|status
operator|=
name|Response
operator|.
name|Status
operator|.
name|FORBIDDEN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|throwable
operator|instanceof
name|AuthenticationException
condition|)
block|{
name|status
operator|=
name|Response
operator|.
name|Status
operator|.
name|FORBIDDEN
expr_stmt|;
comment|// we don't audit here because we did it already when checking access
name|doAudit
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|throwable
operator|instanceof
name|AuthorizationException
condition|)
block|{
name|status
operator|=
name|Response
operator|.
name|Status
operator|.
name|FORBIDDEN
expr_stmt|;
comment|// we don't audit here because we did it already when checking access
name|doAudit
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|throwable
operator|instanceof
name|AccessControlException
condition|)
block|{
name|status
operator|=
name|Response
operator|.
name|Status
operator|.
name|FORBIDDEN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|exception
operator|instanceof
name|IOException
condition|)
block|{
name|status
operator|=
name|Response
operator|.
name|Status
operator|.
name|INTERNAL_SERVER_ERROR
expr_stmt|;
name|log
argument_list|(
name|status
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|exception
operator|instanceof
name|UnsupportedOperationException
condition|)
block|{
name|status
operator|=
name|Response
operator|.
name|Status
operator|.
name|BAD_REQUEST
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|exception
operator|instanceof
name|IllegalArgumentException
condition|)
block|{
name|status
operator|=
name|Response
operator|.
name|Status
operator|.
name|BAD_REQUEST
expr_stmt|;
block|}
else|else
block|{
name|status
operator|=
name|Response
operator|.
name|Status
operator|.
name|INTERNAL_SERVER_ERROR
expr_stmt|;
name|log
argument_list|(
name|status
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doAudit
condition|)
block|{
name|KMSWebApp
operator|.
name|getKMSAudit
argument_list|()
operator|.
name|error
argument_list|(
name|KMSMDCFilter
operator|.
name|getUgi
argument_list|()
argument_list|,
name|KMSMDCFilter
operator|.
name|getMethod
argument_list|()
argument_list|,
name|KMSMDCFilter
operator|.
name|getURL
argument_list|()
argument_list|,
name|getOneLineMessage
argument_list|(
name|exception
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|EXCEPTION_LOG
operator|.
name|warn
argument_list|(
literal|"User {} request {} {} caused exception."
argument_list|,
name|KMSMDCFilter
operator|.
name|getUgi
argument_list|()
argument_list|,
name|KMSMDCFilter
operator|.
name|getMethod
argument_list|()
argument_list|,
name|KMSMDCFilter
operator|.
name|getURL
argument_list|()
argument_list|,
name|exception
argument_list|)
expr_stmt|;
return|return
name|createResponse
argument_list|(
name|status
argument_list|,
name|throwable
argument_list|)
return|;
block|}
DECL|method|log (Response.Status status, Throwable ex)
specifier|protected
name|void
name|log
parameter_list|(
name|Response
operator|.
name|Status
name|status
parameter_list|,
name|Throwable
name|ex
parameter_list|)
block|{
name|UserGroupInformation
name|ugi
init|=
name|KMSMDCFilter
operator|.
name|getUgi
argument_list|()
decl_stmt|;
name|String
name|method
init|=
name|KMSMDCFilter
operator|.
name|getMethod
argument_list|()
decl_stmt|;
name|String
name|url
init|=
name|KMSMDCFilter
operator|.
name|getURL
argument_list|()
decl_stmt|;
name|String
name|remoteClientAddress
init|=
name|KMSMDCFilter
operator|.
name|getRemoteClientAddress
argument_list|()
decl_stmt|;
name|String
name|msg
init|=
name|getOneLineMessage
argument_list|(
name|ex
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"User:'{}' Method:{} URL:{} From:{} Response:{}-{}"
argument_list|,
name|ugi
argument_list|,
name|method
argument_list|,
name|url
argument_list|,
name|remoteClientAddress
argument_list|,
name|status
argument_list|,
name|msg
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

