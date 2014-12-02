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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|KMS
operator|.
name|KMSOp
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|KeyAuthorizationKeyProvider
operator|.
name|KeyACLs
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|KeyAuthorizationKeyProvider
operator|.
name|KeyOpType
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
name|authorize
operator|.
name|AccessControlList
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
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
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
name|ScheduledExecutorService
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Provides access to the<code>AccessControlList</code>s used by KMS,  * hot-reloading them if the<code>kms-acls.xml</code> file where the ACLs  * are defined has been updated.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSACLs
specifier|public
class|class
name|KMSACLs
implements|implements
name|Runnable
implements|,
name|KeyACLs
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
name|KMSACLs
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|UNAUTHORIZED_MSG_WITH_KEY
specifier|private
specifier|static
specifier|final
name|String
name|UNAUTHORIZED_MSG_WITH_KEY
init|=
literal|"User:%s not allowed to do '%s' on '%s'"
decl_stmt|;
DECL|field|UNAUTHORIZED_MSG_WITHOUT_KEY
specifier|private
specifier|static
specifier|final
name|String
name|UNAUTHORIZED_MSG_WITHOUT_KEY
init|=
literal|"User:%s not allowed to do '%s'"
decl_stmt|;
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
DECL|enumConstant|CREATE
DECL|enumConstant|DELETE
DECL|enumConstant|ROLLOVER
DECL|enumConstant|GET
DECL|enumConstant|GET_KEYS
DECL|enumConstant|GET_METADATA
name|CREATE
block|,
name|DELETE
block|,
name|ROLLOVER
block|,
name|GET
block|,
name|GET_KEYS
block|,
name|GET_METADATA
block|,
DECL|enumConstant|SET_KEY_MATERIAL
DECL|enumConstant|GENERATE_EEK
DECL|enumConstant|DECRYPT_EEK
name|SET_KEY_MATERIAL
block|,
name|GENERATE_EEK
block|,
name|DECRYPT_EEK
block|;
DECL|method|getAclConfigKey ()
specifier|public
name|String
name|getAclConfigKey
parameter_list|()
block|{
return|return
name|KMSConfiguration
operator|.
name|CONFIG_PREFIX
operator|+
literal|"acl."
operator|+
name|this
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getBlacklistConfigKey ()
specifier|public
name|String
name|getBlacklistConfigKey
parameter_list|()
block|{
return|return
name|KMSConfiguration
operator|.
name|CONFIG_PREFIX
operator|+
literal|"blacklist."
operator|+
name|this
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|field|ACL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|ACL_DEFAULT
init|=
name|AccessControlList
operator|.
name|WILDCARD_ACL_VALUE
decl_stmt|;
DECL|field|RELOADER_SLEEP_MILLIS
specifier|public
specifier|static
specifier|final
name|int
name|RELOADER_SLEEP_MILLIS
init|=
literal|1000
decl_stmt|;
DECL|field|acls
specifier|private
specifier|volatile
name|Map
argument_list|<
name|Type
argument_list|,
name|AccessControlList
argument_list|>
name|acls
decl_stmt|;
DECL|field|blacklistedAcls
specifier|private
specifier|volatile
name|Map
argument_list|<
name|Type
argument_list|,
name|AccessControlList
argument_list|>
name|blacklistedAcls
decl_stmt|;
DECL|field|keyAcls
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|HashMap
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|>
name|keyAcls
decl_stmt|;
DECL|field|defaultKeyAcls
specifier|private
specifier|final
name|Map
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
name|defaultKeyAcls
init|=
operator|new
name|HashMap
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|whitelistKeyAcls
specifier|private
specifier|final
name|Map
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
name|whitelistKeyAcls
init|=
operator|new
name|HashMap
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|executorService
specifier|private
name|ScheduledExecutorService
name|executorService
decl_stmt|;
DECL|field|lastReload
specifier|private
name|long
name|lastReload
decl_stmt|;
DECL|method|KMSACLs (Configuration conf)
name|KMSACLs
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|conf
operator|=
name|loadACLs
argument_list|()
expr_stmt|;
block|}
name|setKMSACLs
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|setKeyACLs
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|KMSACLs ()
specifier|public
name|KMSACLs
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|setKMSACLs (Configuration conf)
specifier|private
name|void
name|setKMSACLs
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Map
argument_list|<
name|Type
argument_list|,
name|AccessControlList
argument_list|>
name|tempAcls
init|=
operator|new
name|HashMap
argument_list|<
name|Type
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Type
argument_list|,
name|AccessControlList
argument_list|>
name|tempBlacklist
init|=
operator|new
name|HashMap
argument_list|<
name|Type
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Type
name|aclType
range|:
name|Type
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|aclStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|aclType
operator|.
name|getAclConfigKey
argument_list|()
argument_list|,
name|ACL_DEFAULT
argument_list|)
decl_stmt|;
name|tempAcls
operator|.
name|put
argument_list|(
name|aclType
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|aclStr
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|blacklistStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|aclType
operator|.
name|getBlacklistConfigKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|blacklistStr
operator|!=
literal|null
condition|)
block|{
comment|// Only add if blacklist is present
name|tempBlacklist
operator|.
name|put
argument_list|(
name|aclType
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|blacklistStr
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"'{}' Blacklist '{}'"
argument_list|,
name|aclType
argument_list|,
name|blacklistStr
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"'{}' ACL '{}'"
argument_list|,
name|aclType
argument_list|,
name|aclStr
argument_list|)
expr_stmt|;
block|}
name|acls
operator|=
name|tempAcls
expr_stmt|;
name|blacklistedAcls
operator|=
name|tempBlacklist
expr_stmt|;
block|}
DECL|method|setKeyACLs (Configuration conf)
specifier|private
name|void
name|setKeyACLs
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|HashMap
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|>
name|tempKeyAcls
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|HashMap
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|allKeyACLS
init|=
name|conf
operator|.
name|getValByRegex
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|KMSConfiguration
operator|.
name|KEY_ACL_PREFIX
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|keyAcl
range|:
name|allKeyACLS
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|k
init|=
name|keyAcl
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|// this should be of type "key.acl.<KEY_NAME>.<OP_TYPE>"
name|int
name|keyNameStarts
init|=
name|KMSConfiguration
operator|.
name|KEY_ACL_PREFIX
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|keyNameEnds
init|=
name|k
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyNameStarts
operator|>=
name|keyNameEnds
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid key name '{}'"
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|aclStr
init|=
name|keyAcl
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|k
operator|.
name|substring
argument_list|(
name|keyNameStarts
argument_list|,
name|keyNameEnds
argument_list|)
decl_stmt|;
name|String
name|keyOp
init|=
name|k
operator|.
name|substring
argument_list|(
name|keyNameEnds
operator|+
literal|1
argument_list|)
decl_stmt|;
name|KeyOpType
name|aclType
init|=
literal|null
decl_stmt|;
try|try
block|{
name|aclType
operator|=
name|KeyOpType
operator|.
name|valueOf
argument_list|(
name|keyOp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid key Operation '{}'"
argument_list|,
name|keyOp
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aclType
operator|!=
literal|null
condition|)
block|{
comment|// On the assumption this will be single threaded.. else we need to
comment|// ConcurrentHashMap
name|HashMap
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
name|aclMap
init|=
name|tempKeyAcls
operator|.
name|get
argument_list|(
name|keyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|aclMap
operator|==
literal|null
condition|)
block|{
name|aclMap
operator|=
operator|new
name|HashMap
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
expr_stmt|;
name|tempKeyAcls
operator|.
name|put
argument_list|(
name|keyName
argument_list|,
name|aclMap
argument_list|)
expr_stmt|;
block|}
name|aclMap
operator|.
name|put
argument_list|(
name|aclType
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|aclStr
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"KEY_NAME '{}' KEY_OP '{}' ACL '{}'"
argument_list|,
name|keyName
argument_list|,
name|aclType
argument_list|,
name|aclStr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|keyAcls
operator|=
name|tempKeyAcls
expr_stmt|;
for|for
control|(
name|KeyOpType
name|keyOp
range|:
name|KeyOpType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|defaultKeyAcls
operator|.
name|containsKey
argument_list|(
name|keyOp
argument_list|)
condition|)
block|{
name|String
name|confKey
init|=
name|KMSConfiguration
operator|.
name|DEFAULT_KEY_ACL_PREFIX
operator|+
name|keyOp
decl_stmt|;
name|String
name|aclStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|confKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|aclStr
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|aclStr
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Default Key ACL for KEY_OP '{}' is set to '*'"
argument_list|,
name|keyOp
argument_list|)
expr_stmt|;
block|}
name|defaultKeyAcls
operator|.
name|put
argument_list|(
name|keyOp
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|aclStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|whitelistKeyAcls
operator|.
name|containsKey
argument_list|(
name|keyOp
argument_list|)
condition|)
block|{
name|String
name|confKey
init|=
name|KMSConfiguration
operator|.
name|WHITELIST_KEY_ACL_PREFIX
operator|+
name|keyOp
decl_stmt|;
name|String
name|aclStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|confKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|aclStr
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|aclStr
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Whitelist Key ACL for KEY_OP '{}' is set to '*'"
argument_list|,
name|keyOp
argument_list|)
expr_stmt|;
block|}
name|whitelistKeyAcls
operator|.
name|put
argument_list|(
name|keyOp
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|aclStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|KMSConfiguration
operator|.
name|isACLsFileNewer
argument_list|(
name|lastReload
argument_list|)
condition|)
block|{
name|setKMSACLs
argument_list|(
name|loadACLs
argument_list|()
argument_list|)
expr_stmt|;
name|setKeyACLs
argument_list|(
name|loadACLs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Could not reload ACLs file: '%s'"
argument_list|,
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|startReloader ()
specifier|public
specifier|synchronized
name|void
name|startReloader
parameter_list|()
block|{
if|if
condition|(
name|executorService
operator|==
literal|null
condition|)
block|{
name|executorService
operator|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|scheduleAtFixedRate
argument_list|(
name|this
argument_list|,
name|RELOADER_SLEEP_MILLIS
argument_list|,
name|RELOADER_SLEEP_MILLIS
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stopReloader ()
specifier|public
specifier|synchronized
name|void
name|stopReloader
parameter_list|()
block|{
if|if
condition|(
name|executorService
operator|!=
literal|null
condition|)
block|{
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|executorService
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|loadACLs ()
specifier|private
name|Configuration
name|loadACLs
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading ACLs file"
argument_list|)
expr_stmt|;
name|lastReload
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|KMSConfiguration
operator|.
name|getACLsConf
argument_list|()
decl_stmt|;
comment|// triggering the resource loading.
name|conf
operator|.
name|get
argument_list|(
name|Type
operator|.
name|CREATE
operator|.
name|getAclConfigKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * First Check if user is in ACL for the KMS operation, if yes, then    * return true if user is not present in any configured blacklist for    * the operation    * @param type KMS Operation    * @param ugi UserGroupInformation of user    * @return true is user has access    */
DECL|method|hasAccess (Type type, UserGroupInformation ugi)
specifier|public
name|boolean
name|hasAccess
parameter_list|(
name|Type
name|type
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
name|boolean
name|access
init|=
name|acls
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|isUserAllowed
argument_list|(
name|ugi
argument_list|)
decl_stmt|;
if|if
condition|(
name|access
condition|)
block|{
name|AccessControlList
name|blacklist
init|=
name|blacklistedAcls
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|access
operator|=
operator|(
name|blacklist
operator|==
literal|null
operator|)
operator|||
operator|!
name|blacklist
operator|.
name|isUserInList
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
block|}
return|return
name|access
return|;
block|}
DECL|method|assertAccess (KMSACLs.Type aclType, UserGroupInformation ugi, KMSOp operation, String key)
specifier|public
name|void
name|assertAccess
parameter_list|(
name|KMSACLs
operator|.
name|Type
name|aclType
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|KMSOp
name|operation
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
operator|!
name|KMSWebApp
operator|.
name|getACLs
argument_list|()
operator|.
name|hasAccess
argument_list|(
name|aclType
argument_list|,
name|ugi
argument_list|)
condition|)
block|{
name|KMSWebApp
operator|.
name|getUnauthorizedCallsMeter
argument_list|()
operator|.
name|mark
argument_list|()
expr_stmt|;
name|KMSWebApp
operator|.
name|getKMSAudit
argument_list|()
operator|.
name|unauthorized
argument_list|(
name|ugi
argument_list|,
name|operation
argument_list|,
name|key
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AuthorizationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
operator|(
name|key
operator|!=
literal|null
operator|)
condition|?
name|UNAUTHORIZED_MSG_WITH_KEY
else|:
name|UNAUTHORIZED_MSG_WITHOUT_KEY
argument_list|,
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|operation
argument_list|,
name|key
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|hasAccessToKey (String keyName, UserGroupInformation ugi, KeyOpType opType)
specifier|public
name|boolean
name|hasAccessToKey
parameter_list|(
name|String
name|keyName
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|KeyOpType
name|opType
parameter_list|)
block|{
return|return
name|checkKeyAccess
argument_list|(
name|keyName
argument_list|,
name|ugi
argument_list|,
name|opType
argument_list|)
operator|||
name|checkKeyAccess
argument_list|(
name|whitelistKeyAcls
argument_list|,
name|ugi
argument_list|,
name|opType
argument_list|)
return|;
block|}
DECL|method|checkKeyAccess (String keyName, UserGroupInformation ugi, KeyOpType opType)
specifier|private
name|boolean
name|checkKeyAccess
parameter_list|(
name|String
name|keyName
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|KeyOpType
name|opType
parameter_list|)
block|{
name|Map
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
name|keyAcl
init|=
name|keyAcls
operator|.
name|get
argument_list|(
name|keyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyAcl
operator|==
literal|null
condition|)
block|{
comment|// If No key acl defined for this key, check to see if
comment|// there are key defaults configured for this operation
name|keyAcl
operator|=
name|defaultKeyAcls
expr_stmt|;
block|}
return|return
name|checkKeyAccess
argument_list|(
name|keyAcl
argument_list|,
name|ugi
argument_list|,
name|opType
argument_list|)
return|;
block|}
DECL|method|checkKeyAccess (Map<KeyOpType, AccessControlList> keyAcl, UserGroupInformation ugi, KeyOpType opType)
specifier|private
name|boolean
name|checkKeyAccess
parameter_list|(
name|Map
argument_list|<
name|KeyOpType
argument_list|,
name|AccessControlList
argument_list|>
name|keyAcl
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|KeyOpType
name|opType
parameter_list|)
block|{
name|AccessControlList
name|acl
init|=
name|keyAcl
operator|.
name|get
argument_list|(
name|opType
argument_list|)
decl_stmt|;
if|if
condition|(
name|acl
operator|==
literal|null
condition|)
block|{
comment|// If no acl is specified for this operation,
comment|// deny access
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|acl
operator|.
name|isUserAllowed
argument_list|(
name|ugi
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|isACLPresent (String keyName, KeyOpType opType)
specifier|public
name|boolean
name|isACLPresent
parameter_list|(
name|String
name|keyName
parameter_list|,
name|KeyOpType
name|opType
parameter_list|)
block|{
return|return
operator|(
name|keyAcls
operator|.
name|containsKey
argument_list|(
name|keyName
argument_list|)
operator|||
name|defaultKeyAcls
operator|.
name|containsKey
argument_list|(
name|opType
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

