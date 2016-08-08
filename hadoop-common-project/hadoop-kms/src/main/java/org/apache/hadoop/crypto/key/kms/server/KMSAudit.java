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
name|security
operator|.
name|UserGroupInformation
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|Strings
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|RemovalListener
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
name|cache
operator|.
name|RemovalNotification
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
name|collect
operator|.
name|Sets
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Callable
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
name|ExecutionException
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Provides convenience methods for audit logging consistently the different  * types of events.  */
end_comment

begin_class
DECL|class|KMSAudit
specifier|public
class|class
name|KMSAudit
block|{
DECL|class|AuditEvent
specifier|private
specifier|static
class|class
name|AuditEvent
block|{
DECL|field|accessCount
specifier|private
specifier|final
name|AtomicLong
name|accessCount
init|=
operator|new
name|AtomicLong
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|keyName
specifier|private
specifier|final
name|String
name|keyName
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|op
specifier|private
specifier|final
name|KMS
operator|.
name|KMSOp
name|op
decl_stmt|;
DECL|field|extraMsg
specifier|private
specifier|final
name|String
name|extraMsg
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|method|AuditEvent (String keyName, String user, KMS.KMSOp op, String msg)
specifier|private
name|AuditEvent
parameter_list|(
name|String
name|keyName
parameter_list|,
name|String
name|user
parameter_list|,
name|KMS
operator|.
name|KMSOp
name|op
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|this
operator|.
name|keyName
operator|=
name|keyName
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|extraMsg
operator|=
name|msg
expr_stmt|;
block|}
DECL|method|getExtraMsg ()
specifier|public
name|String
name|getExtraMsg
parameter_list|()
block|{
return|return
name|extraMsg
return|;
block|}
DECL|method|getAccessCount ()
specifier|public
name|AtomicLong
name|getAccessCount
parameter_list|()
block|{
return|return
name|accessCount
return|;
block|}
DECL|method|getKeyName ()
specifier|public
name|String
name|getKeyName
parameter_list|()
block|{
return|return
name|keyName
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|getOp ()
specifier|public
name|KMS
operator|.
name|KMSOp
name|getOp
parameter_list|()
block|{
return|return
name|op
return|;
block|}
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
block|}
DECL|enum|OpStatus
specifier|public
specifier|static
enum|enum
name|OpStatus
block|{
DECL|enumConstant|OK
DECL|enumConstant|UNAUTHORIZED
DECL|enumConstant|UNAUTHENTICATED
DECL|enumConstant|ERROR
name|OK
block|,
name|UNAUTHORIZED
block|,
name|UNAUTHENTICATED
block|,
name|ERROR
block|;   }
DECL|field|AGGREGATE_OPS_WHITELIST
specifier|private
specifier|static
name|Set
argument_list|<
name|KMS
operator|.
name|KMSOp
argument_list|>
name|AGGREGATE_OPS_WHITELIST
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|KMS
operator|.
name|KMSOp
operator|.
name|GET_KEY_VERSION
argument_list|,
name|KMS
operator|.
name|KMSOp
operator|.
name|GET_CURRENT_KEY
argument_list|,
name|KMS
operator|.
name|KMSOp
operator|.
name|DECRYPT_EEK
argument_list|,
name|KMS
operator|.
name|KMSOp
operator|.
name|GENERATE_EEK
argument_list|)
decl_stmt|;
DECL|field|cache
specifier|private
name|Cache
argument_list|<
name|String
argument_list|,
name|AuditEvent
argument_list|>
name|cache
decl_stmt|;
DECL|field|executor
specifier|private
name|ScheduledExecutorService
name|executor
decl_stmt|;
DECL|field|KMS_LOGGER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|KMS_LOGGER_NAME
init|=
literal|"kms-audit"
decl_stmt|;
DECL|field|AUDIT_LOG
specifier|private
specifier|static
name|Logger
name|AUDIT_LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KMS_LOGGER_NAME
argument_list|)
decl_stmt|;
comment|/**    * Create a new KMSAudit.    *    * @param windowMs Duplicate events within the aggregation window are quashed    *                 to reduce log traffic. A single message for aggregated    *                 events is printed at the end of the window, along with a    *                 count of the number of aggregated events.    */
DECL|method|KMSAudit (long windowMs)
name|KMSAudit
parameter_list|(
name|long
name|windowMs
parameter_list|)
block|{
name|cache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterWrite
argument_list|(
name|windowMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|removalListener
argument_list|(
operator|new
name|RemovalListener
argument_list|<
name|String
argument_list|,
name|AuditEvent
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|RemovalNotification
argument_list|<
name|String
argument_list|,
name|AuditEvent
argument_list|>
name|entry
parameter_list|)
block|{
name|AuditEvent
name|event
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|event
operator|.
name|getAccessCount
argument_list|()
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
name|KMSAudit
operator|.
name|this
operator|.
name|logEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|event
operator|.
name|getAccessCount
argument_list|()
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|KMSAudit
operator|.
name|this
operator|.
name|cache
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|executor
operator|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
name|KMS_LOGGER_NAME
operator|+
literal|"_thread"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|executor
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|cache
operator|.
name|cleanUp
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|windowMs
operator|/
literal|10
argument_list|,
name|windowMs
operator|/
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|logEvent (AuditEvent event)
specifier|private
name|void
name|logEvent
parameter_list|(
name|AuditEvent
name|event
parameter_list|)
block|{
name|AUDIT_LOG
operator|.
name|info
argument_list|(
literal|"OK[op={}, key={}, user={}, accessCount={}, interval={}ms] {}"
argument_list|,
name|event
operator|.
name|getOp
argument_list|()
argument_list|,
name|event
operator|.
name|getKeyName
argument_list|()
argument_list|,
name|event
operator|.
name|getUser
argument_list|()
argument_list|,
name|event
operator|.
name|getAccessCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|event
operator|.
name|getStartTime
argument_list|()
operator|)
argument_list|,
name|event
operator|.
name|getExtraMsg
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|op (OpStatus opStatus, final KMS.KMSOp op, final String user, final String key, final String extraMsg)
specifier|private
name|void
name|op
parameter_list|(
name|OpStatus
name|opStatus
parameter_list|,
specifier|final
name|KMS
operator|.
name|KMSOp
name|op
parameter_list|,
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|String
name|extraMsg
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|user
argument_list|)
operator|&&
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|key
argument_list|)
operator|&&
operator|(
name|op
operator|!=
literal|null
operator|)
operator|&&
name|AGGREGATE_OPS_WHITELIST
operator|.
name|contains
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|String
name|cacheKey
init|=
name|createCacheKey
argument_list|(
name|user
argument_list|,
name|key
argument_list|,
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|opStatus
operator|==
name|OpStatus
operator|.
name|UNAUTHORIZED
condition|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
name|cacheKey
argument_list|)
expr_stmt|;
name|AUDIT_LOG
operator|.
name|info
argument_list|(
literal|"UNAUTHORIZED[op={}, key={}, user={}] {}"
argument_list|,
name|op
argument_list|,
name|key
argument_list|,
name|user
argument_list|,
name|extraMsg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|AuditEvent
name|event
init|=
name|cache
operator|.
name|get
argument_list|(
name|cacheKey
argument_list|,
operator|new
name|Callable
argument_list|<
name|AuditEvent
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AuditEvent
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|AuditEvent
argument_list|(
name|key
argument_list|,
name|user
argument_list|,
name|op
argument_list|,
name|extraMsg
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Log first access (initialized as -1 so
comment|// incrementAndGet() == 0 implies first access)
if|if
condition|(
name|event
operator|.
name|getAccessCount
argument_list|()
operator|.
name|incrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|event
operator|.
name|getAccessCount
argument_list|()
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|logEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|kvs
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|!=
literal|null
condition|)
block|{
name|kvs
operator|.
name|add
argument_list|(
literal|"op="
operator|+
name|op
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|kvs
operator|.
name|add
argument_list|(
literal|"key="
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|user
argument_list|)
condition|)
block|{
name|kvs
operator|.
name|add
argument_list|(
literal|"user="
operator|+
name|user
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|kvs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|AUDIT_LOG
operator|.
name|info
argument_list|(
literal|"{} {}"
argument_list|,
name|opStatus
operator|.
name|toString
argument_list|()
argument_list|,
name|extraMsg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|join
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|kvs
argument_list|)
decl_stmt|;
name|AUDIT_LOG
operator|.
name|info
argument_list|(
literal|"{}[{}] {}"
argument_list|,
name|opStatus
operator|.
name|toString
argument_list|()
argument_list|,
name|join
argument_list|,
name|extraMsg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|ok (UserGroupInformation user, KMS.KMSOp op, String key, String extraMsg)
specifier|public
name|void
name|ok
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|KMS
operator|.
name|KMSOp
name|op
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|extraMsg
parameter_list|)
block|{
name|op
argument_list|(
name|OpStatus
operator|.
name|OK
argument_list|,
name|op
argument_list|,
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|key
argument_list|,
name|extraMsg
argument_list|)
expr_stmt|;
block|}
DECL|method|ok (UserGroupInformation user, KMS.KMSOp op, String extraMsg)
specifier|public
name|void
name|ok
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|KMS
operator|.
name|KMSOp
name|op
parameter_list|,
name|String
name|extraMsg
parameter_list|)
block|{
name|op
argument_list|(
name|OpStatus
operator|.
name|OK
argument_list|,
name|op
argument_list|,
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
name|extraMsg
argument_list|)
expr_stmt|;
block|}
DECL|method|unauthorized (UserGroupInformation user, KMS.KMSOp op, String key)
specifier|public
name|void
name|unauthorized
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|KMS
operator|.
name|KMSOp
name|op
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|op
argument_list|(
name|OpStatus
operator|.
name|UNAUTHORIZED
argument_list|,
name|op
argument_list|,
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|key
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|error (UserGroupInformation user, String method, String url, String extraMsg)
specifier|public
name|void
name|error
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|String
name|method
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|extraMsg
parameter_list|)
block|{
name|op
argument_list|(
name|OpStatus
operator|.
name|ERROR
argument_list|,
literal|null
argument_list|,
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"Method:'"
operator|+
name|method
operator|+
literal|"' Exception:'"
operator|+
name|extraMsg
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
DECL|method|unauthenticated (String remoteHost, String method, String url, String extraMsg)
specifier|public
name|void
name|unauthenticated
parameter_list|(
name|String
name|remoteHost
parameter_list|,
name|String
name|method
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|extraMsg
parameter_list|)
block|{
name|op
argument_list|(
name|OpStatus
operator|.
name|UNAUTHENTICATED
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"RemoteHost:"
operator|+
name|remoteHost
operator|+
literal|" Method:"
operator|+
name|method
operator|+
literal|" URL:"
operator|+
name|url
operator|+
literal|" ErrorMsg:'"
operator|+
name|extraMsg
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
DECL|method|createCacheKey (String user, String key, KMS.KMSOp op)
specifier|private
specifier|static
name|String
name|createCacheKey
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|key
parameter_list|,
name|KMS
operator|.
name|KMSOp
name|op
parameter_list|)
block|{
return|return
name|user
operator|+
literal|"#"
operator|+
name|key
operator|+
literal|"#"
operator|+
name|op
return|;
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|evictCacheForTesting ()
name|void
name|evictCacheForTesting
parameter_list|()
block|{
name|cache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

