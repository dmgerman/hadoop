begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.logaggregation.tracker
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|logaggregation
operator|.
name|tracker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|ConcurrentHashMap
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
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
operator|.
name|ReadLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
operator|.
name|WriteLock
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
name|service
operator|.
name|CompositeService
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LogAggregationStatus
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRuntimeException
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
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|LogAggregationReport
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|Context
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|Application
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

begin_comment
comment|/**  * {@link NMLogAggregationStatusTracker} is used to cache log aggregation  * status for finished applications. It will also delete the old cached  * log aggregation status periodically.  *  */
end_comment

begin_class
DECL|class|NMLogAggregationStatusTracker
specifier|public
class|class
name|NMLogAggregationStatusTracker
extends|extends
name|CompositeService
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
name|NMLogAggregationStatusTracker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|readLocker
specifier|private
specifier|final
name|ReadLock
name|readLocker
decl_stmt|;
DECL|field|writeLocker
specifier|private
specifier|final
name|WriteLock
name|writeLocker
decl_stmt|;
DECL|field|nmContext
specifier|private
specifier|final
name|Context
name|nmContext
decl_stmt|;
DECL|field|rollingInterval
specifier|private
specifier|final
name|long
name|rollingInterval
decl_stmt|;
DECL|field|timer
specifier|private
specifier|final
name|Timer
name|timer
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|AppLogAggregationStatusForRMRecovery
argument_list|>
DECL|field|recoveryStatuses
name|recoveryStatuses
decl_stmt|;
DECL|field|disabled
specifier|private
name|boolean
name|disabled
init|=
literal|false
decl_stmt|;
DECL|method|NMLogAggregationStatusTracker (Context context)
specifier|public
name|NMLogAggregationStatusTracker
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|NMLogAggregationStatusTracker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|nmContext
operator|=
name|context
expr_stmt|;
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConf
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_LOG_AGGREGATION_ENABLED
argument_list|)
condition|)
block|{
name|disabled
operator|=
literal|true
expr_stmt|;
block|}
name|this
operator|.
name|recoveryStatuses
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|this
operator|.
name|readLocker
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|writeLocker
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|timer
operator|=
operator|new
name|Timer
argument_list|()
expr_stmt|;
name|long
name|configuredRollingInterval
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_STATUS_TIME_OUT_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_LOG_AGGREGATION_STATUS_TIME_OUT_MS
argument_list|)
decl_stmt|;
if|if
condition|(
name|configuredRollingInterval
operator|<=
literal|0
condition|)
block|{
name|this
operator|.
name|rollingInterval
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_LOG_AGGREGATION_STATUS_TIME_OUT_MS
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"The configured log-aggregation-status.time-out.ms is "
operator|+
name|configuredRollingInterval
operator|+
literal|" which should be larger than 0. "
operator|+
literal|"Using the default value:"
operator|+
name|this
operator|.
name|rollingInterval
operator|+
literal|" instead."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|rollingInterval
operator|=
name|configuredRollingInterval
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"the rolling interval seconds for the NodeManager Cached Log "
operator|+
literal|"aggregation status is "
operator|+
operator|(
name|rollingInterval
operator|/
literal|1000
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|disabled
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Log Aggregation is disabled."
operator|+
literal|"So is the LogAggregationStatusTracker."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|timer
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|LogAggregationStatusRoller
argument_list|()
argument_list|,
name|rollingInterval
argument_list|,
name|rollingInterval
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
DECL|method|updateLogAggregationStatus (ApplicationId appId, LogAggregationStatus logAggregationStatus, long updateTime, String diagnosis, boolean finalized)
specifier|public
name|void
name|updateLogAggregationStatus
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|LogAggregationStatus
name|logAggregationStatus
parameter_list|,
name|long
name|updateTime
parameter_list|,
name|String
name|diagnosis
parameter_list|,
name|boolean
name|finalized
parameter_list|)
block|{
if|if
condition|(
name|disabled
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The log aggregation is diabled. No need to update "
operator|+
literal|"the log aggregation status"
argument_list|)
expr_stmt|;
block|}
comment|// In NM, each application has exactly one appLogAggregator thread
comment|// to handle the log aggregation. So, it is fine which multiple
comment|// appLogAggregator thread to update log aggregation status for its
comment|// own application. This is why we are using readLocker here.
name|this
operator|.
name|readLocker
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|AppLogAggregationStatusForRMRecovery
name|tracker
init|=
name|recoveryStatuses
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|tracker
operator|==
literal|null
condition|)
block|{
name|Application
name|application
init|=
name|this
operator|.
name|nmContext
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|application
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The application:"
operator|+
name|appId
operator|+
literal|" has already finished,"
operator|+
literal|" and has been removed from NodeManager, we should not "
operator|+
literal|"receive the log aggregation status update for "
operator|+
literal|"this application."
argument_list|)
expr_stmt|;
return|return;
block|}
name|AppLogAggregationStatusForRMRecovery
name|newTracker
init|=
operator|new
name|AppLogAggregationStatusForRMRecovery
argument_list|(
name|logAggregationStatus
argument_list|,
name|diagnosis
argument_list|)
decl_stmt|;
name|newTracker
operator|.
name|setLastModifiedTime
argument_list|(
name|updateTime
argument_list|)
expr_stmt|;
name|newTracker
operator|.
name|setFinalized
argument_list|(
name|finalized
argument_list|)
expr_stmt|;
name|recoveryStatuses
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|newTracker
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|tracker
operator|.
name|isFinalized
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignore the log aggregation status update request "
operator|+
literal|"for the application:"
operator|+
name|appId
operator|+
literal|". The cached log aggregation "
operator|+
literal|"status is "
operator|+
name|tracker
operator|.
name|getLogAggregationStatus
argument_list|()
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|tracker
operator|.
name|getLastModifiedTime
argument_list|()
operator|>
name|updateTime
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignore the log aggregation status update request "
operator|+
literal|"for the application:"
operator|+
name|appId
operator|+
literal|". The request log "
operator|+
literal|"aggregation status update is older than the cached "
operator|+
literal|"log aggregation status."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tracker
operator|.
name|setLogAggregationStatus
argument_list|(
name|logAggregationStatus
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|setDiagnosis
argument_list|(
name|diagnosis
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|setLastModifiedTime
argument_list|(
name|updateTime
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|setFinalized
argument_list|(
name|finalized
argument_list|)
expr_stmt|;
name|recoveryStatuses
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|tracker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|readLocker
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|pullCachedLogAggregationReports ()
specifier|public
name|List
argument_list|<
name|LogAggregationReport
argument_list|>
name|pullCachedLogAggregationReports
parameter_list|()
block|{
name|List
argument_list|<
name|LogAggregationReport
argument_list|>
name|reports
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|disabled
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The log aggregation is diabled."
operator|+
literal|"There is no cached log aggregation status."
argument_list|)
expr_stmt|;
return|return
name|reports
return|;
block|}
comment|// When we pull cached Log aggregation reports for all application in
comment|// this NM, we should make sure that we need to block all of the
comment|// updateLogAggregationStatus calls. So, the writeLocker is used here.
name|this
operator|.
name|writeLocker
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|AppLogAggregationStatusForRMRecovery
argument_list|>
name|tracker
range|:
name|recoveryStatuses
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|AppLogAggregationStatusForRMRecovery
name|current
init|=
name|tracker
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|LogAggregationReport
name|report
init|=
name|LogAggregationReport
operator|.
name|newInstance
argument_list|(
name|tracker
operator|.
name|getKey
argument_list|()
argument_list|,
name|current
operator|.
name|getLogAggregationStatus
argument_list|()
argument_list|,
name|current
operator|.
name|getDiagnosis
argument_list|()
argument_list|)
decl_stmt|;
name|reports
operator|.
name|add
argument_list|(
name|report
argument_list|)
expr_stmt|;
block|}
return|return
name|reports
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|writeLocker
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|LogAggregationStatusRoller
specifier|private
class|class
name|LogAggregationStatusRoller
extends|extends
name|TimerTask
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|rollLogAggregationStatus
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|rollLogAggregationStatus ()
specifier|private
name|void
name|rollLogAggregationStatus
parameter_list|()
block|{
comment|// When we call rollLogAggregationStatus, basically fetch all
comment|// cached log aggregation status and delete the out-of-timeout period
comment|// log aggregation status, we should block the rollLogAggregationStatus
comment|// calls as well as pullCachedLogAggregationReports call. So, the
comment|// writeLocker is used here.
name|this
operator|.
name|writeLocker
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|long
name|currentTimeStamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Rolling over the cached log aggregation status."
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|AppLogAggregationStatusForRMRecovery
argument_list|>
argument_list|>
name|it
init|=
name|recoveryStatuses
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|AppLogAggregationStatusForRMRecovery
argument_list|>
name|tracker
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// the application has finished.
if|if
condition|(
name|nmContext
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|tracker
operator|.
name|getKey
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|currentTimeStamp
operator|-
name|tracker
operator|.
name|getValue
argument_list|()
operator|.
name|getLastModifiedTime
argument_list|()
operator|>
name|rollingInterval
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|writeLocker
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|AppLogAggregationStatusForRMRecovery
specifier|private
specifier|static
class|class
name|AppLogAggregationStatusForRMRecovery
block|{
DECL|field|logAggregationStatus
specifier|private
name|LogAggregationStatus
name|logAggregationStatus
decl_stmt|;
DECL|field|lastModifiedTime
specifier|private
name|long
name|lastModifiedTime
decl_stmt|;
DECL|field|finalized
specifier|private
name|boolean
name|finalized
decl_stmt|;
DECL|field|diagnosis
specifier|private
name|String
name|diagnosis
decl_stmt|;
DECL|method|AppLogAggregationStatusForRMRecovery ( LogAggregationStatus logAggregationStatus, String diagnosis)
name|AppLogAggregationStatusForRMRecovery
parameter_list|(
name|LogAggregationStatus
name|logAggregationStatus
parameter_list|,
name|String
name|diagnosis
parameter_list|)
block|{
name|this
operator|.
name|setLogAggregationStatus
argument_list|(
name|logAggregationStatus
argument_list|)
expr_stmt|;
name|this
operator|.
name|setDiagnosis
argument_list|(
name|diagnosis
argument_list|)
expr_stmt|;
block|}
DECL|method|getLogAggregationStatus ()
specifier|public
name|LogAggregationStatus
name|getLogAggregationStatus
parameter_list|()
block|{
return|return
name|logAggregationStatus
return|;
block|}
DECL|method|setLogAggregationStatus ( LogAggregationStatus logAggregationStatus)
specifier|public
name|void
name|setLogAggregationStatus
parameter_list|(
name|LogAggregationStatus
name|logAggregationStatus
parameter_list|)
block|{
name|this
operator|.
name|logAggregationStatus
operator|=
name|logAggregationStatus
expr_stmt|;
block|}
DECL|method|getLastModifiedTime ()
specifier|public
name|long
name|getLastModifiedTime
parameter_list|()
block|{
return|return
name|lastModifiedTime
return|;
block|}
DECL|method|setLastModifiedTime (long lastModifiedTime)
specifier|public
name|void
name|setLastModifiedTime
parameter_list|(
name|long
name|lastModifiedTime
parameter_list|)
block|{
name|this
operator|.
name|lastModifiedTime
operator|=
name|lastModifiedTime
expr_stmt|;
block|}
DECL|method|isFinalized ()
specifier|public
name|boolean
name|isFinalized
parameter_list|()
block|{
return|return
name|finalized
return|;
block|}
DECL|method|setFinalized (boolean finalized)
specifier|public
name|void
name|setFinalized
parameter_list|(
name|boolean
name|finalized
parameter_list|)
block|{
name|this
operator|.
name|finalized
operator|=
name|finalized
expr_stmt|;
block|}
DECL|method|getDiagnosis ()
specifier|public
name|String
name|getDiagnosis
parameter_list|()
block|{
return|return
name|diagnosis
return|;
block|}
DECL|method|setDiagnosis (String diagnosis)
specifier|public
name|void
name|setDiagnosis
parameter_list|(
name|String
name|diagnosis
parameter_list|)
block|{
name|this
operator|.
name|diagnosis
operator|=
name|diagnosis
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

