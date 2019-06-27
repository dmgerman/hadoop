begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request
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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|ProtobufRpcEngine
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
name|OzoneConsts
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
name|audit
operator|.
name|AuditAction
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
name|audit
operator|.
name|AuditEventStatus
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
name|audit
operator|.
name|AuditLogger
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
name|audit
operator|.
name|AuditMessage
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
name|OzoneManagerRatisUtils
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
name|security
operator|.
name|acl
operator|.
name|IAccessAuthorizer
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
name|acl
operator|.
name|OzoneObj
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

begin_comment
comment|/**  * OMClientRequest provides methods which every write OM request should  * implement.  */
end_comment

begin_class
DECL|class|OMClientRequest
specifier|public
specifier|abstract
class|class
name|OMClientRequest
implements|implements
name|RequestAuditor
block|{
DECL|field|omRequest
specifier|private
name|OMRequest
name|omRequest
decl_stmt|;
DECL|method|OMClientRequest (OMRequest omRequest)
specifier|public
name|OMClientRequest
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|omRequest
argument_list|)
expr_stmt|;
name|this
operator|.
name|omRequest
operator|=
name|omRequest
expr_stmt|;
block|}
comment|/**    * Perform pre-execute steps on a OMRequest.    *    * Called from the RPC context, and generates a OMRequest object which has    * all the information that will be either persisted    * in RocksDB or returned to the caller once this operation    * is executed.    *    * @return OMRequest that will be serialized and handed off to Ratis for    *         consensus.    */
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
name|omRequest
operator|=
name|getOmRequest
argument_list|()
operator|.
name|toBuilder
argument_list|()
operator|.
name|setUserInfo
argument_list|(
name|getUserInfo
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|omRequest
return|;
block|}
comment|/**    * Validate the OMRequest and update the cache.    * This step should verify that the request can be executed, perform    * any authorization steps and update the in-memory cache.     * This step does not persist the changes to the database.    *    * @return the response that will be returned to the client.    */
DECL|method|validateAndUpdateCache ( OzoneManager ozoneManager, long transactionLogIndex)
specifier|public
specifier|abstract
name|OMClientResponse
name|validateAndUpdateCache
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|,
name|long
name|transactionLogIndex
parameter_list|)
function_decl|;
annotation|@
name|VisibleForTesting
DECL|method|getOmRequest ()
specifier|public
name|OMRequest
name|getOmRequest
parameter_list|()
block|{
return|return
name|omRequest
return|;
block|}
comment|/**    * Get User information which needs to be set in the OMRequest object.    * @return User Info.    */
DECL|method|getUserInfo ()
specifier|public
name|OzoneManagerProtocolProtos
operator|.
name|UserInfo
name|getUserInfo
parameter_list|()
block|{
name|UserGroupInformation
name|user
init|=
name|ProtobufRpcEngine
operator|.
name|Server
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
name|InetAddress
name|remoteAddress
init|=
name|ProtobufRpcEngine
operator|.
name|Server
operator|.
name|getRemoteIp
argument_list|()
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|UserInfo
operator|.
name|Builder
name|userInfo
init|=
name|OzoneManagerProtocolProtos
operator|.
name|UserInfo
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
comment|// Added not null checks, as in UT's these values might be null.
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|userInfo
operator|.
name|setUserName
argument_list|(
name|user
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|remoteAddress
operator|!=
literal|null
condition|)
block|{
name|userInfo
operator|.
name|setRemoteAddress
argument_list|(
name|remoteAddress
operator|.
name|getHostAddress
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
name|userInfo
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Check Acls of ozone object.    * @param ozoneManager    * @param resType    * @param storeType    * @param aclType    * @param vol    * @param bucket    * @param key    * @throws IOException    */
DECL|method|checkAcls (OzoneManager ozoneManager, OzoneObj.ResourceType resType, OzoneObj.StoreType storeType, IAccessAuthorizer.ACLType aclType, String vol, String bucket, String key)
specifier|public
name|void
name|checkAcls
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|,
name|OzoneObj
operator|.
name|ResourceType
name|resType
parameter_list|,
name|OzoneObj
operator|.
name|StoreType
name|storeType
parameter_list|,
name|IAccessAuthorizer
operator|.
name|ACLType
name|aclType
parameter_list|,
name|String
name|vol
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|ozoneManager
operator|.
name|checkAcls
argument_list|(
name|resType
argument_list|,
name|storeType
argument_list|,
name|aclType
argument_list|,
name|vol
argument_list|,
name|bucket
argument_list|,
name|key
argument_list|,
name|createUGI
argument_list|()
argument_list|,
name|getRemoteAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return UGI object created from OMRequest userInfo. If userInfo is not    * set, returns null.    * @return UserGroupInformation.    */
annotation|@
name|VisibleForTesting
DECL|method|createUGI ()
specifier|public
name|UserGroupInformation
name|createUGI
parameter_list|()
block|{
if|if
condition|(
name|omRequest
operator|.
name|hasUserInfo
argument_list|()
condition|)
block|{
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|omRequest
operator|.
name|getUserInfo
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// This will never happen, as for every OM request preExecute, we
comment|// should add userInfo.
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Return InetAddress created from OMRequest userInfo. If userInfo is not    * set, returns null.    * @return InetAddress    * @throws IOException    */
annotation|@
name|VisibleForTesting
DECL|method|getRemoteAddress ()
specifier|public
name|InetAddress
name|getRemoteAddress
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|omRequest
operator|.
name|hasUserInfo
argument_list|()
condition|)
block|{
return|return
name|InetAddress
operator|.
name|getByName
argument_list|(
name|omRequest
operator|.
name|getUserInfo
argument_list|()
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Set parameters needed for return error response to client.    * @param omResponse    * @param ex - IOException    * @return error response need to be returned to client - OMResponse.    */
DECL|method|createErrorOMResponse (OMResponse.Builder omResponse, IOException ex)
specifier|protected
name|OMResponse
name|createErrorOMResponse
parameter_list|(
name|OMResponse
operator|.
name|Builder
name|omResponse
parameter_list|,
name|IOException
name|ex
parameter_list|)
block|{
name|omResponse
operator|.
name|setSuccess
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|omResponse
operator|.
name|setMessage
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|omResponse
operator|.
name|setStatus
argument_list|(
name|OzoneManagerRatisUtils
operator|.
name|exceptionToResponseStatus
argument_list|(
name|ex
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|omResponse
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Log the auditMessage.    * @param auditLogger    * @param auditMessage    */
DECL|method|auditLog (AuditLogger auditLogger, AuditMessage auditMessage)
specifier|protected
name|void
name|auditLog
parameter_list|(
name|AuditLogger
name|auditLogger
parameter_list|,
name|AuditMessage
name|auditMessage
parameter_list|)
block|{
name|auditLogger
operator|.
name|logWrite
argument_list|(
name|auditMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildAuditMessage (AuditAction op, Map< String, String > auditMap, Throwable throwable, OzoneManagerProtocolProtos.UserInfo userInfo)
specifier|public
name|AuditMessage
name|buildAuditMessage
parameter_list|(
name|AuditAction
name|op
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
parameter_list|,
name|Throwable
name|throwable
parameter_list|,
name|OzoneManagerProtocolProtos
operator|.
name|UserInfo
name|userInfo
parameter_list|)
block|{
return|return
operator|new
name|AuditMessage
operator|.
name|Builder
argument_list|()
operator|.
name|setUser
argument_list|(
name|userInfo
operator|!=
literal|null
condition|?
name|userInfo
operator|.
name|getUserName
argument_list|()
else|:
literal|null
argument_list|)
operator|.
name|atIp
argument_list|(
name|userInfo
operator|!=
literal|null
condition|?
name|userInfo
operator|.
name|getRemoteAddress
argument_list|()
else|:
literal|null
argument_list|)
operator|.
name|forOperation
argument_list|(
name|op
operator|.
name|getAction
argument_list|()
argument_list|)
operator|.
name|withParams
argument_list|(
name|auditMap
argument_list|)
operator|.
name|withResult
argument_list|(
name|throwable
operator|!=
literal|null
condition|?
name|AuditEventStatus
operator|.
name|FAILURE
operator|.
name|toString
argument_list|()
else|:
name|AuditEventStatus
operator|.
name|SUCCESS
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withException
argument_list|(
name|throwable
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|buildVolumeAuditMap (String volume)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|buildVolumeAuditMap
parameter_list|(
name|String
name|volume
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|VOLUME
argument_list|,
name|volume
argument_list|)
expr_stmt|;
return|return
name|auditMap
return|;
block|}
block|}
end_class

end_unit

