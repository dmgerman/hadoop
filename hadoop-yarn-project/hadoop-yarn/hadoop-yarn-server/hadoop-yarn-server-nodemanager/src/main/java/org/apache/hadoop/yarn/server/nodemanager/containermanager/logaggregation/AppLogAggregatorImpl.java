begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.logaggregation
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
name|containermanager
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|concurrent
operator|.
name|BlockingQueue
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
name|LinkedBlockingQueue
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
name|AtomicBoolean
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
name|StringUtils
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
name|ApplicationAccessType
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
name|ContainerId
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
name|event
operator|.
name|Dispatcher
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
name|logaggregation
operator|.
name|ContainerLogsRetentionPolicy
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
name|logaggregation
operator|.
name|AggregatedLogFormat
operator|.
name|LogKey
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
name|logaggregation
operator|.
name|AggregatedLogFormat
operator|.
name|LogValue
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
name|logaggregation
operator|.
name|AggregatedLogFormat
operator|.
name|LogWriter
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
name|DeletionService
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
name|LocalDirsHandlerService
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
name|ApplicationEvent
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
name|ApplicationEventType
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
name|util
operator|.
name|ConverterUtils
import|;
end_import

begin_class
DECL|class|AppLogAggregatorImpl
specifier|public
class|class
name|AppLogAggregatorImpl
implements|implements
name|AppLogAggregator
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
name|AppLogAggregatorImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|THREAD_SLEEP_TIME
specifier|private
specifier|static
specifier|final
name|int
name|THREAD_SLEEP_TIME
init|=
literal|1000
decl_stmt|;
DECL|field|TMP_FILE_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|TMP_FILE_SUFFIX
init|=
literal|".tmp"
decl_stmt|;
DECL|field|dirsHandler
specifier|private
specifier|final
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|appId
specifier|private
specifier|final
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|applicationId
specifier|private
specifier|final
name|String
name|applicationId
decl_stmt|;
DECL|field|logAggregationDisabled
specifier|private
name|boolean
name|logAggregationDisabled
init|=
literal|false
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|delService
specifier|private
specifier|final
name|DeletionService
name|delService
decl_stmt|;
DECL|field|userUgi
specifier|private
specifier|final
name|UserGroupInformation
name|userUgi
decl_stmt|;
DECL|field|remoteNodeLogFileForApp
specifier|private
specifier|final
name|Path
name|remoteNodeLogFileForApp
decl_stmt|;
DECL|field|remoteNodeTmpLogFileForApp
specifier|private
specifier|final
name|Path
name|remoteNodeTmpLogFileForApp
decl_stmt|;
DECL|field|retentionPolicy
specifier|private
specifier|final
name|ContainerLogsRetentionPolicy
name|retentionPolicy
decl_stmt|;
DECL|field|pendingContainers
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|ContainerId
argument_list|>
name|pendingContainers
decl_stmt|;
DECL|field|appFinishing
specifier|private
specifier|final
name|AtomicBoolean
name|appFinishing
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|appAggregationFinished
specifier|private
specifier|final
name|AtomicBoolean
name|appAggregationFinished
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|appAcls
specifier|private
specifier|final
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|appAcls
decl_stmt|;
DECL|field|writer
specifier|private
name|LogWriter
name|writer
init|=
literal|null
decl_stmt|;
DECL|method|AppLogAggregatorImpl (Dispatcher dispatcher, DeletionService deletionService, Configuration conf, ApplicationId appId, UserGroupInformation userUgi, LocalDirsHandlerService dirsHandler, Path remoteNodeLogFileForApp, ContainerLogsRetentionPolicy retentionPolicy, Map<ApplicationAccessType, String> appAcls)
specifier|public
name|AppLogAggregatorImpl
parameter_list|(
name|Dispatcher
name|dispatcher
parameter_list|,
name|DeletionService
name|deletionService
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|UserGroupInformation
name|userUgi
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|,
name|Path
name|remoteNodeLogFileForApp
parameter_list|,
name|ContainerLogsRetentionPolicy
name|retentionPolicy
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|appAcls
parameter_list|)
block|{
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|delService
operator|=
name|deletionService
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|this
operator|.
name|userUgi
operator|=
name|userUgi
expr_stmt|;
name|this
operator|.
name|dirsHandler
operator|=
name|dirsHandler
expr_stmt|;
name|this
operator|.
name|remoteNodeLogFileForApp
operator|=
name|remoteNodeLogFileForApp
expr_stmt|;
name|this
operator|.
name|remoteNodeTmpLogFileForApp
operator|=
name|getRemoteNodeTmpLogFileForApp
argument_list|()
expr_stmt|;
name|this
operator|.
name|retentionPolicy
operator|=
name|retentionPolicy
expr_stmt|;
name|this
operator|.
name|pendingContainers
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|appAcls
operator|=
name|appAcls
expr_stmt|;
block|}
DECL|method|uploadLogsForContainer (ContainerId containerId)
specifier|private
name|void
name|uploadLogsForContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|logAggregationDisabled
condition|)
block|{
return|return;
block|}
comment|// Lazy creation of the writer
if|if
condition|(
name|this
operator|.
name|writer
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting aggregate log-file for app "
operator|+
name|this
operator|.
name|applicationId
operator|+
literal|" at "
operator|+
name|this
operator|.
name|remoteNodeTmpLogFileForApp
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|writer
operator|=
operator|new
name|LogWriter
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|this
operator|.
name|remoteNodeTmpLogFileForApp
argument_list|,
name|this
operator|.
name|userUgi
argument_list|)
expr_stmt|;
comment|//Write ACLs once when and if the writer is created.
name|this
operator|.
name|writer
operator|.
name|writeApplicationACLs
argument_list|(
name|appAcls
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|.
name|writeApplicationOwner
argument_list|(
name|this
operator|.
name|userUgi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot create writer for app "
operator|+
name|this
operator|.
name|applicationId
operator|+
literal|". Disabling log-aggregation for this app."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|this
operator|.
name|logAggregationDisabled
operator|=
literal|true
expr_stmt|;
return|return;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Uploading logs for container "
operator|+
name|containerId
operator|+
literal|". Current good log dirs are "
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|dirsHandler
operator|.
name|getLogDirs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LogKey
name|logKey
init|=
operator|new
name|LogKey
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|LogValue
name|logValue
init|=
operator|new
name|LogValue
argument_list|(
name|dirsHandler
operator|.
name|getLogDirs
argument_list|()
argument_list|,
name|containerId
argument_list|,
name|userUgi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|writer
operator|.
name|append
argument_list|(
name|logKey
argument_list|,
name|logValue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Couldn't upload logs for "
operator|+
name|containerId
operator|+
literal|". Skipping this container."
argument_list|)
expr_stmt|;
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
name|doAppLogAggregation
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|appAggregationFinished
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Aggregation did not complete for application "
operator|+
name|appId
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|appAggregationFinished
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|doAppLogAggregation ()
specifier|private
name|void
name|doAppLogAggregation
parameter_list|()
block|{
name|ContainerId
name|containerId
decl_stmt|;
while|while
condition|(
operator|!
name|this
operator|.
name|appFinishing
operator|.
name|get
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|wait
argument_list|(
name|THREAD_SLEEP_TIME
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"PendingContainers queue is interrupted"
argument_list|)
expr_stmt|;
name|this
operator|.
name|appFinishing
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Application is finished. Finish pending-containers
while|while
condition|(
operator|(
name|containerId
operator|=
name|this
operator|.
name|pendingContainers
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|uploadLogsForContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
comment|// Remove the local app-log-dirs
name|List
argument_list|<
name|String
argument_list|>
name|rootLogDirs
init|=
name|dirsHandler
operator|.
name|getLogDirs
argument_list|()
decl_stmt|;
name|Path
index|[]
name|localAppLogDirs
init|=
operator|new
name|Path
index|[
name|rootLogDirs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|rootLogDir
range|:
name|rootLogDirs
control|)
block|{
name|localAppLogDirs
index|[
name|index
index|]
operator|=
operator|new
name|Path
argument_list|(
name|rootLogDir
argument_list|,
name|this
operator|.
name|applicationId
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
name|this
operator|.
name|delService
operator|.
name|delete
argument_list|(
name|this
operator|.
name|userUgi
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
name|localAppLogDirs
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|writer
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished aggregate log-file for app "
operator|+
name|this
operator|.
name|applicationId
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|userUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|remoteFS
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|remoteFS
operator|.
name|rename
argument_list|(
name|remoteNodeTmpLogFileForApp
argument_list|,
name|remoteNodeLogFileForApp
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to move temporary log file to final location: ["
operator|+
name|remoteNodeTmpLogFileForApp
operator|+
literal|"] to ["
operator|+
name|remoteNodeLogFileForApp
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ApplicationEvent
argument_list|(
name|this
operator|.
name|appId
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_FINISHED
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|appAggregationFinished
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getRemoteNodeTmpLogFileForApp ()
specifier|private
name|Path
name|getRemoteNodeTmpLogFileForApp
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
name|remoteNodeLogFileForApp
operator|.
name|getParent
argument_list|()
argument_list|,
operator|(
name|remoteNodeLogFileForApp
operator|.
name|getName
argument_list|()
operator|+
name|TMP_FILE_SUFFIX
operator|)
argument_list|)
return|;
block|}
DECL|method|shouldUploadLogs (ContainerId containerId, boolean wasContainerSuccessful)
specifier|private
name|boolean
name|shouldUploadLogs
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|boolean
name|wasContainerSuccessful
parameter_list|)
block|{
comment|// All containers
if|if
condition|(
name|this
operator|.
name|retentionPolicy
operator|.
name|equals
argument_list|(
name|ContainerLogsRetentionPolicy
operator|.
name|ALL_CONTAINERS
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// AM Container only
if|if
condition|(
name|this
operator|.
name|retentionPolicy
operator|.
name|equals
argument_list|(
name|ContainerLogsRetentionPolicy
operator|.
name|APPLICATION_MASTER_ONLY
argument_list|)
condition|)
block|{
if|if
condition|(
name|containerId
operator|.
name|getId
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|// AM + Failing containers
if|if
condition|(
name|this
operator|.
name|retentionPolicy
operator|.
name|equals
argument_list|(
name|ContainerLogsRetentionPolicy
operator|.
name|AM_AND_FAILED_CONTAINERS_ONLY
argument_list|)
condition|)
block|{
if|if
condition|(
name|containerId
operator|.
name|getId
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|wasContainerSuccessful
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|startContainerLogAggregation (ContainerId containerId, boolean wasContainerSuccessful)
specifier|public
name|void
name|startContainerLogAggregation
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|boolean
name|wasContainerSuccessful
parameter_list|)
block|{
if|if
condition|(
name|shouldUploadLogs
argument_list|(
name|containerId
argument_list|,
name|wasContainerSuccessful
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Considering container "
operator|+
name|containerId
operator|+
literal|" for log-aggregation"
argument_list|)
expr_stmt|;
name|this
operator|.
name|pendingContainers
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finishLogAggregation ()
specifier|public
specifier|synchronized
name|void
name|finishLogAggregation
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Application just finished : "
operator|+
name|this
operator|.
name|applicationId
argument_list|)
expr_stmt|;
name|this
operator|.
name|appFinishing
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

