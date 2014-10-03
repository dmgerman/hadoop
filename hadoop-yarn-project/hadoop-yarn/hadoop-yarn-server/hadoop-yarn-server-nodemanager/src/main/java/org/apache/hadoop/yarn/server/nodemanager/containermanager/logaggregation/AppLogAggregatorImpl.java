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
name|HashMap
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
name|api
operator|.
name|records
operator|.
name|LogAggregationContext
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
name|logaggregation
operator|.
name|LogAggregationUtils
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
name|Predicate
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
name|Iterables
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
DECL|field|aborted
specifier|private
specifier|final
name|AtomicBoolean
name|aborted
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
DECL|field|logAggregationContext
specifier|private
specifier|final
name|LogAggregationContext
name|logAggregationContext
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|containerLogAggregators
specifier|private
specifier|final
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLogAggregator
argument_list|>
name|containerLogAggregators
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLogAggregator
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|AppLogAggregatorImpl (Dispatcher dispatcher, DeletionService deletionService, Configuration conf, ApplicationId appId, UserGroupInformation userUgi, LocalDirsHandlerService dirsHandler, Path remoteNodeLogFileForApp, ContainerLogsRetentionPolicy retentionPolicy, Map<ApplicationAccessType, String> appAcls, LogAggregationContext logAggregationContext, Context context)
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
parameter_list|,
name|LogAggregationContext
name|logAggregationContext
parameter_list|,
name|Context
name|context
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
name|this
operator|.
name|logAggregationContext
operator|=
name|logAggregationContext
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|method|uploadLogsForContainers ()
specifier|private
name|void
name|uploadLogsForContainers
parameter_list|()
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
comment|// Create a set of Containers whose logs will be uploaded in this cycle.
comment|// It includes:
comment|// a) all containers in pendingContainers: those containers are finished
comment|//    and satisfy the retentionPolicy.
comment|// b) some set of running containers: For all the Running containers,
comment|// we have ContainerLogsRetentionPolicy.AM_AND_FAILED_CONTAINERS_ONLY,
comment|// so simply set wasContainerSuccessful as true to
comment|// bypass FAILED_CONTAINERS check and find the running containers
comment|// which satisfy the retentionPolicy.
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|pendingContainerInThisCycle
init|=
operator|new
name|HashSet
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|this
operator|.
name|pendingContainers
operator|.
name|drainTo
argument_list|(
name|pendingContainerInThisCycle
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|finishedContainers
init|=
operator|new
name|HashSet
argument_list|<
name|ContainerId
argument_list|>
argument_list|(
name|pendingContainerInThisCycle
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|this
operator|.
name|appId
argument_list|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ContainerId
name|container
range|:
name|this
operator|.
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|this
operator|.
name|appId
argument_list|)
operator|.
name|getContainers
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|shouldUploadLogs
argument_list|(
name|container
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|pendingContainerInThisCycle
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LogWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
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
comment|// Write ACLs once when the writer is created.
name|writer
operator|.
name|writeApplicationACLs
argument_list|(
name|appAcls
argument_list|)
expr_stmt|;
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
name|e1
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
literal|". Skip log upload this time. "
argument_list|)
expr_stmt|;
return|return;
block|}
name|boolean
name|uploadedLogsInThisCycle
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ContainerId
name|container
range|:
name|pendingContainerInThisCycle
control|)
block|{
name|ContainerLogAggregator
name|aggregator
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|containerLogAggregators
operator|.
name|containsKey
argument_list|(
name|container
argument_list|)
condition|)
block|{
name|aggregator
operator|=
name|containerLogAggregators
operator|.
name|get
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|aggregator
operator|=
operator|new
name|ContainerLogAggregator
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|containerLogAggregators
operator|.
name|put
argument_list|(
name|container
argument_list|,
name|aggregator
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|Path
argument_list|>
name|uploadedFilePathsInThisCycle
init|=
name|aggregator
operator|.
name|doContainerLogAggregation
argument_list|(
name|writer
argument_list|)
decl_stmt|;
if|if
condition|(
name|uploadedFilePathsInThisCycle
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|uploadedLogsInThisCycle
operator|=
literal|true
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
name|uploadedFilePathsInThisCycle
operator|.
name|toArray
argument_list|(
operator|new
name|Path
index|[
name|uploadedFilePathsInThisCycle
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// This container is finished, and all its logs have been uploaded,
comment|// remove it from containerLogAggregators.
if|if
condition|(
name|finishedContainers
operator|.
name|contains
argument_list|(
name|container
argument_list|)
condition|)
block|{
name|containerLogAggregators
operator|.
name|remove
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Path
name|renamedPath
init|=
name|logAggregationContext
operator|==
literal|null
operator|||
name|logAggregationContext
operator|.
name|getRollingIntervalSeconds
argument_list|()
operator|<=
literal|0
condition|?
name|remoteNodeLogFileForApp
else|:
operator|new
name|Path
argument_list|(
name|remoteNodeLogFileForApp
operator|.
name|getParent
argument_list|()
argument_list|,
name|remoteNodeLogFileForApp
operator|.
name|getName
argument_list|()
operator|+
literal|"_"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|rename
init|=
name|uploadedLogsInThisCycle
decl_stmt|;
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
if|if
condition|(
name|remoteFS
operator|.
name|exists
argument_list|(
name|remoteNodeTmpLogFileForApp
argument_list|)
operator|&&
name|rename
condition|)
block|{
name|remoteFS
operator|.
name|rename
argument_list|(
name|remoteNodeTmpLogFileForApp
argument_list|,
name|renamedPath
argument_list|)
expr_stmt|;
block|}
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
name|renamedPath
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
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
while|while
condition|(
operator|!
name|this
operator|.
name|appFinishing
operator|.
name|get
argument_list|()
operator|&&
operator|!
name|this
operator|.
name|aborted
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
if|if
condition|(
name|this
operator|.
name|logAggregationContext
operator|!=
literal|null
operator|&&
name|this
operator|.
name|logAggregationContext
operator|.
name|getRollingIntervalSeconds
argument_list|()
operator|>
literal|0
condition|)
block|{
name|wait
argument_list|(
name|this
operator|.
name|logAggregationContext
operator|.
name|getRollingIntervalSeconds
argument_list|()
operator|*
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|appFinishing
operator|.
name|get
argument_list|()
operator|||
name|this
operator|.
name|aborted
operator|.
name|get
argument_list|()
condition|)
block|{
break|break;
block|}
name|uploadLogsForContainers
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|wait
argument_list|(
name|THREAD_SLEEP_TIME
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|this
operator|.
name|aborted
operator|.
name|get
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// App is finished, upload the container logs.
name|uploadLogsForContainers
argument_list|()
expr_stmt|;
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
name|LogAggregationUtils
operator|.
name|TMP_FILE_SUFFIX
operator|)
argument_list|)
return|;
block|}
comment|// TODO: The condition: containerId.getId() == 1 to determine an AM container
comment|// is not always true.
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
annotation|@
name|Override
DECL|method|abortLogAggregation ()
specifier|public
specifier|synchronized
name|void
name|abortLogAggregation
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Aborting log aggregation for "
operator|+
name|this
operator|.
name|applicationId
argument_list|)
expr_stmt|;
name|this
operator|.
name|aborted
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
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|doLogAggregationOutOfBand ()
specifier|public
specifier|synchronized
name|void
name|doLogAggregationOutOfBand
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Do OutOfBand log aggregation"
argument_list|)
expr_stmt|;
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
DECL|class|ContainerLogAggregator
specifier|private
class|class
name|ContainerLogAggregator
block|{
DECL|field|containerId
specifier|private
specifier|final
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|uploadedFileMeta
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|uploadedFileMeta
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ContainerLogAggregator (ContainerId containerId)
specifier|public
name|ContainerLogAggregator
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
block|}
DECL|method|doContainerLogAggregation (LogWriter writer)
specifier|public
name|Set
argument_list|<
name|Path
argument_list|>
name|doContainerLogAggregation
parameter_list|(
name|LogWriter
name|writer
parameter_list|)
block|{
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
specifier|final
name|LogKey
name|logKey
init|=
operator|new
name|LogKey
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
specifier|final
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
argument_list|,
name|logAggregationContext
argument_list|,
name|this
operator|.
name|uploadedFileMeta
argument_list|)
decl_stmt|;
try|try
block|{
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
name|Exception
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
return|return
operator|new
name|HashSet
argument_list|<
name|Path
argument_list|>
argument_list|()
return|;
block|}
name|this
operator|.
name|uploadedFileMeta
operator|.
name|addAll
argument_list|(
name|logValue
operator|.
name|getCurrentUpLoadedFileMeta
argument_list|()
argument_list|)
expr_stmt|;
comment|// if any of the previous uploaded logs have been deleted,
comment|// we need to remove them from alreadyUploadedLogs
name|Iterable
argument_list|<
name|String
argument_list|>
name|mask
init|=
name|Iterables
operator|.
name|filter
argument_list|(
name|uploadedFileMeta
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|String
name|next
parameter_list|)
block|{
return|return
name|logValue
operator|.
name|getAllExistingFilesMeta
argument_list|()
operator|.
name|contains
argument_list|(
name|next
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|this
operator|.
name|uploadedFileMeta
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|mask
argument_list|)
expr_stmt|;
return|return
name|logValue
operator|.
name|getCurrentUpLoadedFilesPath
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

