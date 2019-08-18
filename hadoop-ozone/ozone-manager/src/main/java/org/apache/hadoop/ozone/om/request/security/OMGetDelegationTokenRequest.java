begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|request
operator|.
name|security
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
name|base
operator|.
name|Optional
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
name|om
operator|.
name|OMMetadataManager
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
name|om
operator|.
name|OzoneManager
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
name|om
operator|.
name|ratis
operator|.
name|utils
operator|.
name|OzoneManagerDoubleBufferHelper
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
name|om
operator|.
name|request
operator|.
name|OMClientRequest
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
name|om
operator|.
name|response
operator|.
name|OMClientResponse
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
name|om
operator|.
name|response
operator|.
name|security
operator|.
name|OMGetDelegationTokenResponse
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|GetDelegationTokenResponseProto
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMRequest
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMResponse
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|UpdateGetDelegationTokenRequest
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
name|protocolPB
operator|.
name|OMPBHelper
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
name|proto
operator|.
name|SecurityProtos
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
name|proto
operator|.
name|SecurityProtos
operator|.
name|GetDelegationTokenRequestProto
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
name|utils
operator|.
name|db
operator|.
name|cache
operator|.
name|CacheKey
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
name|utils
operator|.
name|db
operator|.
name|cache
operator|.
name|CacheValue
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Handle GetDelegationToken Request.  */
end_comment

begin_class
DECL|class|OMGetDelegationTokenRequest
specifier|public
class|class
name|OMGetDelegationTokenRequest
extends|extends
name|OMClientRequest
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
name|OMGetDelegationTokenRequest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|OMGetDelegationTokenRequest (OMRequest omRequest)
specifier|public
name|OMGetDelegationTokenRequest
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|super
argument_list|(
name|omRequest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|preExecute (OzoneManager ozoneManager)
specifier|public
name|OMRequest
name|preExecute
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|)
throws|throws
name|IOException
block|{
name|GetDelegationTokenRequestProto
name|getDelegationTokenRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getGetDelegationTokenRequest
argument_list|()
decl_stmt|;
comment|// Call OM to create token
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|token
init|=
name|ozoneManager
operator|.
name|getDelegationToken
argument_list|(
operator|new
name|Text
argument_list|(
name|getDelegationTokenRequest
operator|.
name|getRenewer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// Client issues GetDelegationToken request, when received by OM leader
comment|// it will generate a token. Original GetDelegationToken request is
comment|// converted to UpdateGetDelegationToken request with the generated token
comment|// information. This updated request will be submitted to Ratis. In this
comment|// way delegation token created by leader, will be replicated across all
comment|// OMs. With this approach, original GetDelegationToken request from
comment|// client does not need any proto changes.
comment|// Create UpdateGetDelegationTokenRequest with token response.
name|OMRequest
operator|.
name|Builder
name|omRequest
init|=
name|OMRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setUserInfo
argument_list|(
name|getUserInfo
argument_list|()
argument_list|)
operator|.
name|setUpdateGetDelegationTokenRequest
argument_list|(
name|UpdateGetDelegationTokenRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setGetDelegationTokenResponse
argument_list|(
name|GetDelegationTokenResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setResponse
argument_list|(
name|SecurityProtos
operator|.
name|GetDelegationTokenResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setToken
argument_list|(
name|OMPBHelper
operator|.
name|convertToTokenProto
argument_list|(
name|token
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setCmdType
argument_list|(
name|getOmRequest
argument_list|()
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setClientId
argument_list|(
name|getOmRequest
argument_list|()
operator|.
name|getClientId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|getOmRequest
argument_list|()
operator|.
name|hasTraceID
argument_list|()
condition|)
block|{
name|omRequest
operator|.
name|setTraceID
argument_list|(
name|getOmRequest
argument_list|()
operator|.
name|getTraceID
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|omRequest
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|validateAndUpdateCache (OzoneManager ozoneManager, long transactionLogIndex, OzoneManagerDoubleBufferHelper ozoneManagerDoubleBufferHelper)
specifier|public
name|OMClientResponse
name|validateAndUpdateCache
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|,
name|long
name|transactionLogIndex
parameter_list|,
name|OzoneManagerDoubleBufferHelper
name|ozoneManagerDoubleBufferHelper
parameter_list|)
block|{
name|UpdateGetDelegationTokenRequest
name|updateGetDelegationTokenRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getUpdateGetDelegationTokenRequest
argument_list|()
decl_stmt|;
name|SecurityProtos
operator|.
name|TokenProto
name|tokenProto
init|=
name|updateGetDelegationTokenRequest
operator|.
name|getGetDelegationTokenResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|getToken
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|ozoneTokenIdentifierToken
init|=
name|OMPBHelper
operator|.
name|convertToDelegationToken
argument_list|(
name|tokenProto
argument_list|)
decl_stmt|;
name|OMMetadataManager
name|omMetadataManager
init|=
name|ozoneManager
operator|.
name|getMetadataManager
argument_list|()
decl_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
literal|null
decl_stmt|;
name|OMResponse
operator|.
name|Builder
name|omResponse
init|=
name|OMResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|GetDelegationToken
argument_list|)
operator|.
name|setStatus
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setSuccess
argument_list|(
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|OzoneTokenIdentifier
name|ozoneTokenIdentifier
init|=
name|ozoneTokenIdentifierToken
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
comment|// Update in memory map of token.
name|long
name|renewTime
init|=
name|ozoneManager
operator|.
name|getDelegationTokenMgr
argument_list|()
operator|.
name|updateToken
argument_list|(
name|ozoneTokenIdentifierToken
argument_list|,
name|ozoneTokenIdentifier
argument_list|)
decl_stmt|;
comment|// Update Cache.
name|omMetadataManager
operator|.
name|getDelegationTokenTable
argument_list|()
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|ozoneTokenIdentifier
argument_list|)
argument_list|,
operator|new
name|CacheValue
argument_list|<>
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|renewTime
argument_list|)
argument_list|,
name|transactionLogIndex
argument_list|)
argument_list|)
expr_stmt|;
name|omClientResponse
operator|=
operator|new
name|OMGetDelegationTokenResponse
argument_list|(
name|ozoneTokenIdentifier
argument_list|,
name|renewTime
argument_list|,
name|omResponse
operator|.
name|setGetDelegationTokenResponse
argument_list|(
name|updateGetDelegationTokenRequest
operator|.
name|getGetDelegationTokenResponse
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in Updating DelegationToken {}"
argument_list|,
name|ozoneTokenIdentifierToken
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|omClientResponse
operator|=
operator|new
name|OMGetDelegationTokenResponse
argument_list|(
literal|null
argument_list|,
operator|-
literal|1L
argument_list|,
name|createErrorOMResponse
argument_list|(
name|omResponse
argument_list|,
name|ex
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|omClientResponse
operator|!=
literal|null
condition|)
block|{
name|omClientResponse
operator|.
name|setFlushFuture
argument_list|(
name|ozoneManagerDoubleBufferHelper
operator|.
name|add
argument_list|(
name|omClientResponse
argument_list|,
name|transactionLogIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Updated delegation token in-memory map: {}"
argument_list|,
name|ozoneTokenIdentifierToken
argument_list|)
expr_stmt|;
block|}
return|return
name|omClientResponse
return|;
block|}
block|}
end_class

end_unit

