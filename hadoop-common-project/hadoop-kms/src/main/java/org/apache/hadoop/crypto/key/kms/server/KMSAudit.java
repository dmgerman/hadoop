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
import|import static
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
name|KMSAuditLogger
operator|.
name|AuditEvent
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|KMSAuditLogger
operator|.
name|OpStatus
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
name|util
operator|.
name|ReflectionUtils
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
name|Time
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
name|HashSet
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
name|Collection
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

begin_comment
comment|/**  * Provides convenience methods for audit logging consisting different  * types of events.  */
end_comment

begin_class
DECL|class|KMSAudit
specifier|public
class|class
name|KMSAudit
block|{
annotation|@
name|VisibleForTesting
DECL|field|AGGREGATE_OPS_WHITELIST
specifier|static
specifier|final
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
argument_list|,
name|KMS
operator|.
name|KMSOp
operator|.
name|REENCRYPT_EEK
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
name|KMSAudit
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|auditLoggers
specifier|private
specifier|final
name|List
argument_list|<
name|KMSAuditLogger
argument_list|>
name|auditLoggers
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Create a new KMSAudit.    *    * @param conf The configuration object.    */
DECL|method|KMSAudit (Configuration conf)
name|KMSAudit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// Duplicate events within the aggregation window are quashed
comment|// to reduce log traffic. A single message for aggregated
comment|// events is printed at the end of the window, along with a
comment|// count of the number of aggregated events.
name|long
name|windowMs
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|KMSConfiguration
operator|.
name|KMS_AUDIT_AGGREGATION_WINDOW
argument_list|,
name|KMSConfiguration
operator|.
name|KMS_AUDIT_AGGREGATION_WINDOW_DEFAULT
argument_list|)
decl_stmt|;
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
name|OpStatus
operator|.
name|OK
argument_list|,
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
name|initializeAuditLoggers
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read the KMSAuditLogger classes from configuration. If any loggers fail to    * load, a RumTimeException will be thrown.    *    * @param conf The configuration.    * @return Collection of KMSAudigLogger classes.    */
DECL|method|getAuditLoggerClasses ( final Configuration conf)
specifier|private
name|Set
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|KMSAuditLogger
argument_list|>
argument_list|>
name|getAuditLoggerClasses
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
name|Set
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|KMSAuditLogger
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// getTrimmedStringCollection will remove duplicates.
name|Collection
argument_list|<
name|String
argument_list|>
name|classes
init|=
name|conf
operator|.
name|getTrimmedStringCollection
argument_list|(
name|KMSConfiguration
operator|.
name|KMS_AUDIT_LOGGER_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|classes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No audit logger configured, using default."
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|SimpleKMSAuditLogger
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
for|for
control|(
name|String
name|c
range|:
name|classes
control|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|cls
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|cls
operator|.
name|asSubclass
argument_list|(
name|KMSAuditLogger
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to load "
operator|+
name|c
operator|+
literal|", please check "
operator|+
literal|"configuration "
operator|+
name|KMSConfiguration
operator|.
name|KMS_AUDIT_LOGGER_KEY
argument_list|,
name|cnfe
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Create a collection of KMSAuditLoggers from configuration, and initialize    * them. If any logger failed to be created or initialized, a RunTimeException    * is thrown.    */
DECL|method|initializeAuditLoggers (Configuration conf)
specifier|private
name|void
name|initializeAuditLoggers
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Set
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|KMSAuditLogger
argument_list|>
argument_list|>
name|classes
init|=
name|getAuditLoggerClasses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|classes
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"Should have at least 1 audit logger."
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|KMSAuditLogger
argument_list|>
name|c
range|:
name|classes
control|)
block|{
specifier|final
name|KMSAuditLogger
name|logger
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|c
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|auditLoggers
operator|.
name|add
argument_list|(
name|logger
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|KMSAuditLogger
name|logger
range|:
name|auditLoggers
control|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing audit logger {}"
argument_list|,
name|logger
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|initialize
argument_list|(
name|conf
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
name|RuntimeException
argument_list|(
literal|"Failed to initialize "
operator|+
name|logger
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|logEvent (final OpStatus status, AuditEvent event)
specifier|private
name|void
name|logEvent
parameter_list|(
specifier|final
name|OpStatus
name|status
parameter_list|,
name|AuditEvent
name|event
parameter_list|)
block|{
name|event
operator|.
name|setEndTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|KMSAuditLogger
name|logger
range|:
name|auditLoggers
control|)
block|{
name|logger
operator|.
name|logAuditEvent
argument_list|(
name|status
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|op (final OpStatus opStatus, final KMS.KMSOp op, final UserGroupInformation ugi, final String key, final String remoteHost, final String extraMsg)
specifier|private
name|void
name|op
parameter_list|(
specifier|final
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
name|UserGroupInformation
name|ugi
parameter_list|,
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|String
name|remoteHost
parameter_list|,
specifier|final
name|String
name|extraMsg
parameter_list|)
block|{
specifier|final
name|String
name|user
init|=
name|ugi
operator|==
literal|null
condition|?
literal|null
else|:
name|ugi
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
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
name|logEvent
argument_list|(
name|opStatus
argument_list|,
operator|new
name|AuditEvent
argument_list|(
name|op
argument_list|,
name|ugi
argument_list|,
name|key
argument_list|,
name|remoteHost
argument_list|,
name|extraMsg
argument_list|)
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
name|op
argument_list|,
name|ugi
argument_list|,
name|key
argument_list|,
name|remoteHost
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
name|opStatus
argument_list|,
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
name|logEvent
argument_list|(
name|opStatus
argument_list|,
operator|new
name|AuditEvent
argument_list|(
name|op
argument_list|,
name|ugi
argument_list|,
name|key
argument_list|,
name|remoteHost
argument_list|,
name|extraMsg
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|,
name|key
argument_list|,
literal|"Unknown"
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
argument_list|,
literal|null
argument_list|,
literal|"Unknown"
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
argument_list|,
name|key
argument_list|,
literal|"Unknown"
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
argument_list|,
literal|null
argument_list|,
literal|"Unknown"
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
name|remoteHost
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
for|for
control|(
name|KMSAuditLogger
name|logger
range|:
name|auditLoggers
control|)
block|{
try|try
block|{
name|logger
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to cleanup logger {}"
argument_list|,
name|logger
operator|.
name|getClass
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
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

