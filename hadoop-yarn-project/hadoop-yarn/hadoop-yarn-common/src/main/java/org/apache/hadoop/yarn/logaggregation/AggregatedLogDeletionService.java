begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|logaggregation
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
operator|.
name|Private
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
name|FileStatus
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
name|fs
operator|.
name|Path
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
name|service
operator|.
name|AbstractService
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

begin_comment
comment|/**  * A service that periodically deletes aggregated logs.  */
end_comment

begin_class
annotation|@
name|Private
DECL|class|AggregatedLogDeletionService
specifier|public
class|class
name|AggregatedLogDeletionService
extends|extends
name|AbstractService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AggregatedLogDeletionService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|timer
specifier|private
name|Timer
name|timer
init|=
literal|null
decl_stmt|;
DECL|field|checkIntervalMsecs
specifier|private
name|long
name|checkIntervalMsecs
decl_stmt|;
DECL|class|LogDeletionTask
specifier|static
class|class
name|LogDeletionTask
extends|extends
name|TimerTask
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|retentionMillis
specifier|private
name|long
name|retentionMillis
decl_stmt|;
DECL|field|suffix
specifier|private
name|String
name|suffix
init|=
literal|null
decl_stmt|;
DECL|field|remoteRootLogDir
specifier|private
name|Path
name|remoteRootLogDir
init|=
literal|null
decl_stmt|;
DECL|method|LogDeletionTask (Configuration conf, long retentionSecs)
specifier|public
name|LogDeletionTask
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|long
name|retentionSecs
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|retentionMillis
operator|=
name|retentionSecs
operator|*
literal|1000
expr_stmt|;
name|this
operator|.
name|suffix
operator|=
name|LogAggregationUtils
operator|.
name|getRemoteNodeLogDirSuffix
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|remoteRootLogDir
operator|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_REMOTE_APP_LOG_DIR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|cutoffMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|retentionMillis
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"aggregated log deletion started."
argument_list|)
expr_stmt|;
try|try
block|{
name|FileSystem
name|fs
init|=
name|remoteRootLogDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|userDir
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|remoteRootLogDir
argument_list|)
control|)
block|{
if|if
condition|(
name|userDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|Path
name|userDirPath
init|=
operator|new
name|Path
argument_list|(
name|userDir
operator|.
name|getPath
argument_list|()
argument_list|,
name|suffix
argument_list|)
decl_stmt|;
name|deleteOldLogDirsFrom
argument_list|(
name|userDirPath
argument_list|,
name|cutoffMillis
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logIOException
argument_list|(
literal|"Error reading root log dir this deletion "
operator|+
literal|"attempt is being aborted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"aggregated log deletion finished."
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteOldLogDirsFrom (Path dir, long cutoffMillis, FileSystem fs)
specifier|private
specifier|static
name|void
name|deleteOldLogDirsFrom
parameter_list|(
name|Path
name|dir
parameter_list|,
name|long
name|cutoffMillis
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|FileStatus
name|appDir
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
control|)
block|{
if|if
condition|(
name|appDir
operator|.
name|isDirectory
argument_list|()
operator|&&
name|appDir
operator|.
name|getModificationTime
argument_list|()
operator|<
name|cutoffMillis
condition|)
block|{
if|if
condition|(
name|shouldDeleteLogDir
argument_list|(
name|appDir
argument_list|,
name|cutoffMillis
argument_list|,
name|fs
argument_list|)
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting aggregated logs in "
operator|+
name|appDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|appDir
operator|.
name|getPath
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logIOException
argument_list|(
literal|"Could not delete "
operator|+
name|appDir
operator|.
name|getPath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logIOException
argument_list|(
literal|"Could not read the contents of "
operator|+
name|dir
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shouldDeleteLogDir (FileStatus dir, long cutoffMillis, FileSystem fs)
specifier|private
specifier|static
name|boolean
name|shouldDeleteLogDir
parameter_list|(
name|FileStatus
name|dir
parameter_list|,
name|long
name|cutoffMillis
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
block|{
name|boolean
name|shouldDelete
init|=
literal|true
decl_stmt|;
try|try
block|{
for|for
control|(
name|FileStatus
name|node
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|node
operator|.
name|getModificationTime
argument_list|()
operator|>=
name|cutoffMillis
condition|)
block|{
name|shouldDelete
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logIOException
argument_list|(
literal|"Error reading the contents of "
operator|+
name|dir
operator|.
name|getPath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|shouldDelete
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|shouldDelete
return|;
block|}
block|}
DECL|method|logIOException (String comment, IOException e)
specifier|private
specifier|static
name|void
name|logIOException
parameter_list|(
name|String
name|comment
parameter_list|,
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|AccessControlException
condition|)
block|{
name|String
name|message
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
comment|//TODO fix this after HADOOP-8661
name|message
operator|=
name|message
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|comment
operator|+
literal|" "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
name|comment
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|AggregatedLogDeletionService ()
specifier|public
name|AggregatedLogDeletionService
parameter_list|()
block|{
name|super
argument_list|(
name|AggregatedLogDeletionService
operator|.
name|class
operator|.
name|getName
argument_list|()
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
name|scheduleLogDeletionTask
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|stopTimer
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|setLogAggCheckIntervalMsecs (long retentionSecs)
specifier|private
name|void
name|setLogAggCheckIntervalMsecs
parameter_list|(
name|long
name|retentionSecs
parameter_list|)
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|checkIntervalMsecs
operator|=
literal|1000
operator|*
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_RETAIN_CHECK_INTERVAL_SECONDS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_LOG_AGGREGATION_RETAIN_CHECK_INTERVAL_SECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkIntervalMsecs
operator|<=
literal|0
condition|)
block|{
comment|// when unspecified compute check interval as 1/10th of retention
name|checkIntervalMsecs
operator|=
operator|(
name|retentionSecs
operator|*
literal|1000
operator|)
operator|/
literal|10
expr_stmt|;
block|}
block|}
DECL|method|refreshLogRetentionSettings ()
specifier|public
name|void
name|refreshLogRetentionSettings
parameter_list|()
block|{
if|if
condition|(
name|getServiceState
argument_list|()
operator|==
name|STATE
operator|.
name|STARTED
condition|)
block|{
name|Configuration
name|conf
init|=
name|createConf
argument_list|()
decl_stmt|;
name|setConfig
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|stopTimer
argument_list|()
expr_stmt|;
name|scheduleLogDeletionTask
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to execute refreshLogRetentionSettings : Aggregated Log Deletion Service is not started"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|scheduleLogDeletionTask ()
specifier|private
name|void
name|scheduleLogDeletionTask
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|getConfig
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
comment|// Log aggregation is not enabled so don't bother
return|return;
block|}
name|long
name|retentionSecs
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_RETAIN_SECONDS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_LOG_AGGREGATION_RETAIN_SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|retentionSecs
operator|<
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Log Aggregation deletion is disabled because retention is"
operator|+
literal|" too small ("
operator|+
name|retentionSecs
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return;
block|}
name|setLogAggCheckIntervalMsecs
argument_list|(
name|retentionSecs
argument_list|)
expr_stmt|;
name|TimerTask
name|task
init|=
operator|new
name|LogDeletionTask
argument_list|(
name|conf
argument_list|,
name|retentionSecs
argument_list|)
decl_stmt|;
name|timer
operator|=
operator|new
name|Timer
argument_list|()
expr_stmt|;
name|timer
operator|.
name|scheduleAtFixedRate
argument_list|(
name|task
argument_list|,
literal|0
argument_list|,
name|checkIntervalMsecs
argument_list|)
expr_stmt|;
block|}
DECL|method|stopTimer ()
specifier|private
name|void
name|stopTimer
parameter_list|()
block|{
if|if
condition|(
name|timer
operator|!=
literal|null
condition|)
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getCheckIntervalMsecs ()
specifier|public
name|long
name|getCheckIntervalMsecs
parameter_list|()
block|{
return|return
name|checkIntervalMsecs
return|;
block|}
DECL|method|createConf ()
specifier|protected
name|Configuration
name|createConf
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|()
return|;
block|}
block|}
end_class

end_unit

