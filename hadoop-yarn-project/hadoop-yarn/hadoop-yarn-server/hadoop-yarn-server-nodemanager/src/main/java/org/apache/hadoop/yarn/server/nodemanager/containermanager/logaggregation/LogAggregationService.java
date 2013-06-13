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
name|FileNotFoundException
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
name|ConcurrentMap
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
name|ExecutorService
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
name|TimeUnit
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|Credentials
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
name|yarn
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
name|NodeId
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|loghandler
operator|.
name|LogHandler
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
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerAppFinishedEvent
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
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerAppStartedEvent
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
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerContainerFinishedEvent
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
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerEvent
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
name|service
operator|.
name|AbstractService
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

begin_class
DECL|class|LogAggregationService
specifier|public
class|class
name|LogAggregationService
extends|extends
name|AbstractService
implements|implements
name|LogHandler
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
name|LogAggregationService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/*    * Expected deployment TLD will be 1777, owner=<NMOwner>, group=<NMGroup -    * Group to which NMOwner belongs> App dirs will be created as 770,    * owner=<AppOwner>, group=<NMGroup>: so that the owner and<NMOwner> can    * access / modify the files.    *<NMGroup> should obviously be a limited access group.    */
comment|/**    * Permissions for the top level directory under which app directories will be    * created.    */
DECL|field|TLDIR_PERMISSIONS
specifier|private
specifier|static
specifier|final
name|FsPermission
name|TLDIR_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|01777
argument_list|)
decl_stmt|;
comment|/**    * Permissions for the Application directory.    */
DECL|field|APP_DIR_PERMISSIONS
specifier|private
specifier|static
specifier|final
name|FsPermission
name|APP_DIR_PERMISSIONS
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0770
argument_list|)
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|deletionService
specifier|private
specifier|final
name|DeletionService
name|deletionService
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|dirsHandler
specifier|private
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|field|remoteRootLogDir
name|Path
name|remoteRootLogDir
decl_stmt|;
DECL|field|remoteRootLogDirSuffix
name|String
name|remoteRootLogDirSuffix
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|appLogAggregators
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|AppLogAggregator
argument_list|>
name|appLogAggregators
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ExecutorService
name|threadPool
decl_stmt|;
DECL|method|LogAggregationService (Dispatcher dispatcher, Context context, DeletionService deletionService, LocalDirsHandlerService dirsHandler)
specifier|public
name|LogAggregationService
parameter_list|(
name|Dispatcher
name|dispatcher
parameter_list|,
name|Context
name|context
parameter_list|,
name|DeletionService
name|deletionService
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
block|{
name|super
argument_list|(
name|LogAggregationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|deletionService
operator|=
name|deletionService
expr_stmt|;
name|this
operator|.
name|dirsHandler
operator|=
name|dirsHandler
expr_stmt|;
name|this
operator|.
name|appLogAggregators
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationId
argument_list|,
name|AppLogAggregator
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"LogAggregationService #%d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
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
name|this
operator|.
name|remoteRootLogDirSuffix
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR_SUFFIX
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_REMOTE_APP_LOG_DIR_SUFFIX
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
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
comment|// NodeId is only available during start, the following cannot be moved
comment|// anywhere else.
name|this
operator|.
name|nodeId
operator|=
name|this
operator|.
name|context
operator|.
name|getNodeId
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
name|LOG
operator|.
name|info
argument_list|(
name|this
operator|.
name|getName
argument_list|()
operator|+
literal|" waiting for pending aggregation during exit"
argument_list|)
expr_stmt|;
name|stopAggregators
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|stopAggregators ()
specifier|private
name|void
name|stopAggregators
parameter_list|()
block|{
name|threadPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// politely ask to finish
for|for
control|(
name|AppLogAggregator
name|aggregator
range|:
name|appLogAggregators
operator|.
name|values
argument_list|()
control|)
block|{
name|aggregator
operator|.
name|finishLogAggregation
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|threadPool
operator|.
name|isTerminated
argument_list|()
condition|)
block|{
comment|// wait for all threads to finish
for|for
control|(
name|ApplicationId
name|appId
range|:
name|appLogAggregators
operator|.
name|keySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for aggregation to complete for "
operator|+
name|appId
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
operator|!
name|threadPool
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|threadPool
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
comment|// send interrupt to hurry them along
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
literal|"Aggregation stop interrupted!"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
for|for
control|(
name|ApplicationId
name|appId
range|:
name|appLogAggregators
operator|.
name|keySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Some logs may not have been aggregated for "
operator|+
name|appId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFileSystem (Configuration conf)
specifier|protected
name|FileSystem
name|getFileSystem
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|verifyAndCreateRemoteLogDir (Configuration conf)
name|void
name|verifyAndCreateRemoteLogDir
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// Checking the existence of the TLD
name|FileSystem
name|remoteFS
init|=
literal|null
decl_stmt|;
try|try
block|{
name|remoteFS
operator|=
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Unable to get Remote FileSystem instance"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|boolean
name|remoteExists
init|=
literal|true
decl_stmt|;
try|try
block|{
name|FsPermission
name|perms
init|=
name|remoteFS
operator|.
name|getFileStatus
argument_list|(
name|this
operator|.
name|remoteRootLogDir
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|perms
operator|.
name|equals
argument_list|(
name|TLDIR_PERMISSIONS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Remote Root Log Dir ["
operator|+
name|this
operator|.
name|remoteRootLogDir
operator|+
literal|"] already exist, but with incorrect permissions. "
operator|+
literal|"Expected: ["
operator|+
name|TLDIR_PERMISSIONS
operator|+
literal|"], Found: ["
operator|+
name|perms
operator|+
literal|"]."
operator|+
literal|" The cluster may have problems with multiple users."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|remoteExists
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Failed to check permissions for dir ["
operator|+
name|this
operator|.
name|remoteRootLogDir
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|remoteExists
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Remote Root Log Dir ["
operator|+
name|this
operator|.
name|remoteRootLogDir
operator|+
literal|"] does not exist. Attempting to create it."
argument_list|)
expr_stmt|;
try|try
block|{
name|Path
name|qualified
init|=
name|this
operator|.
name|remoteRootLogDir
operator|.
name|makeQualified
argument_list|(
name|remoteFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|remoteFS
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|remoteFS
operator|.
name|mkdirs
argument_list|(
name|qualified
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|TLDIR_PERMISSIONS
argument_list|)
argument_list|)
expr_stmt|;
name|remoteFS
operator|.
name|setPermission
argument_list|(
name|qualified
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|TLDIR_PERMISSIONS
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Failed to create remoteLogDir ["
operator|+
name|this
operator|.
name|remoteRootLogDir
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getRemoteNodeLogFileForApp (ApplicationId appId, String user)
name|Path
name|getRemoteNodeLogFileForApp
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|user
parameter_list|)
block|{
return|return
name|LogAggregationUtils
operator|.
name|getRemoteNodeLogFileForApp
argument_list|(
name|this
operator|.
name|remoteRootLogDir
argument_list|,
name|appId
argument_list|,
name|user
argument_list|,
name|this
operator|.
name|nodeId
argument_list|,
name|this
operator|.
name|remoteRootLogDirSuffix
argument_list|)
return|;
block|}
DECL|method|createDir (FileSystem fs, Path path, FsPermission fsPerm)
specifier|private
name|void
name|createDir
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|FsPermission
name|fsPerm
parameter_list|)
throws|throws
name|IOException
block|{
name|FsPermission
name|dirPerm
init|=
operator|new
name|FsPermission
argument_list|(
name|fsPerm
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|,
name|dirPerm
argument_list|)
expr_stmt|;
name|FsPermission
name|umask
init|=
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dirPerm
operator|.
name|equals
argument_list|(
name|dirPerm
operator|.
name|applyUMask
argument_list|(
name|umask
argument_list|)
argument_list|)
condition|)
block|{
name|fs
operator|.
name|setPermission
argument_list|(
name|path
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|fsPerm
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkExists (FileSystem fs, Path path, FsPermission fsPerm)
specifier|private
name|boolean
name|checkExists
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|FsPermission
name|fsPerm
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|exists
init|=
literal|true
decl_stmt|;
try|try
block|{
name|FileStatus
name|appDirStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|APP_DIR_PERMISSIONS
operator|.
name|equals
argument_list|(
name|appDirStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
condition|)
block|{
name|fs
operator|.
name|setPermission
argument_list|(
name|path
argument_list|,
name|APP_DIR_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
name|exists
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|exists
return|;
block|}
DECL|method|createAppDir (final String user, final ApplicationId appId, UserGroupInformation userUgi)
specifier|protected
name|void
name|createAppDir
parameter_list|(
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|ApplicationId
name|appId
parameter_list|,
name|UserGroupInformation
name|userUgi
parameter_list|)
block|{
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
try|try
block|{
comment|// TODO: Reuse FS for user?
name|FileSystem
name|remoteFS
init|=
name|getFileSystem
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
comment|// Only creating directories if they are missing to avoid
comment|// unnecessary load on the filesystem from all of the nodes
name|Path
name|appDir
init|=
name|LogAggregationUtils
operator|.
name|getRemoteAppLogDir
argument_list|(
name|LogAggregationService
operator|.
name|this
operator|.
name|remoteRootLogDir
argument_list|,
name|appId
argument_list|,
name|user
argument_list|,
name|LogAggregationService
operator|.
name|this
operator|.
name|remoteRootLogDirSuffix
argument_list|)
decl_stmt|;
name|appDir
operator|=
name|appDir
operator|.
name|makeQualified
argument_list|(
name|remoteFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|remoteFS
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|checkExists
argument_list|(
name|remoteFS
argument_list|,
name|appDir
argument_list|,
name|APP_DIR_PERMISSIONS
argument_list|)
condition|)
block|{
name|Path
name|suffixDir
init|=
name|LogAggregationUtils
operator|.
name|getRemoteLogSuffixedDir
argument_list|(
name|LogAggregationService
operator|.
name|this
operator|.
name|remoteRootLogDir
argument_list|,
name|user
argument_list|,
name|LogAggregationService
operator|.
name|this
operator|.
name|remoteRootLogDirSuffix
argument_list|)
decl_stmt|;
name|suffixDir
operator|=
name|suffixDir
operator|.
name|makeQualified
argument_list|(
name|remoteFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|remoteFS
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|checkExists
argument_list|(
name|remoteFS
argument_list|,
name|suffixDir
argument_list|,
name|APP_DIR_PERMISSIONS
argument_list|)
condition|)
block|{
name|Path
name|userDir
init|=
name|LogAggregationUtils
operator|.
name|getRemoteLogUserDir
argument_list|(
name|LogAggregationService
operator|.
name|this
operator|.
name|remoteRootLogDir
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|userDir
operator|=
name|userDir
operator|.
name|makeQualified
argument_list|(
name|remoteFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|remoteFS
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|checkExists
argument_list|(
name|remoteFS
argument_list|,
name|userDir
argument_list|,
name|APP_DIR_PERMISSIONS
argument_list|)
condition|)
block|{
name|createDir
argument_list|(
name|remoteFS
argument_list|,
name|userDir
argument_list|,
name|APP_DIR_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
name|createDir
argument_list|(
name|remoteFS
argument_list|,
name|suffixDir
argument_list|,
name|APP_DIR_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
name|createDir
argument_list|(
name|remoteFS
argument_list|,
name|appDir
argument_list|,
name|APP_DIR_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
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
literal|"Failed to setup application log directory for "
operator|+
name|appId
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
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
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|initApp (final ApplicationId appId, String user, Credentials credentials, ContainerLogsRetentionPolicy logRetentionPolicy, Map<ApplicationAccessType, String> appAcls)
specifier|private
name|void
name|initApp
parameter_list|(
specifier|final
name|ApplicationId
name|appId
parameter_list|,
name|String
name|user
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|ContainerLogsRetentionPolicy
name|logRetentionPolicy
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
name|ApplicationEvent
name|eventResponse
decl_stmt|;
try|try
block|{
name|verifyAndCreateRemoteLogDir
argument_list|(
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|initAppAggregator
argument_list|(
name|appId
argument_list|,
name|user
argument_list|,
name|credentials
argument_list|,
name|logRetentionPolicy
argument_list|,
name|appAcls
argument_list|)
expr_stmt|;
name|eventResponse
operator|=
operator|new
name|ApplicationEvent
argument_list|(
name|appId
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_INITED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Application failed to init aggregation"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|eventResponse
operator|=
operator|new
name|ApplicationEvent
argument_list|(
name|appId
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_FAILED
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
name|eventResponse
argument_list|)
expr_stmt|;
block|}
DECL|method|initAppAggregator (final ApplicationId appId, String user, Credentials credentials, ContainerLogsRetentionPolicy logRetentionPolicy, Map<ApplicationAccessType, String> appAcls)
specifier|protected
name|void
name|initAppAggregator
parameter_list|(
specifier|final
name|ApplicationId
name|appId
parameter_list|,
name|String
name|user
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|ContainerLogsRetentionPolicy
name|logRetentionPolicy
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
comment|// Get user's FileSystem credentials
specifier|final
name|UserGroupInformation
name|userUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|credentials
operator|!=
literal|null
condition|)
block|{
name|userUgi
operator|.
name|addCredentials
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
block|}
comment|// New application
specifier|final
name|AppLogAggregator
name|appLogAggregator
init|=
operator|new
name|AppLogAggregatorImpl
argument_list|(
name|this
operator|.
name|dispatcher
argument_list|,
name|this
operator|.
name|deletionService
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|appId
argument_list|,
name|userUgi
argument_list|,
name|dirsHandler
argument_list|,
name|getRemoteNodeLogFileForApp
argument_list|(
name|appId
argument_list|,
name|user
argument_list|)
argument_list|,
name|logRetentionPolicy
argument_list|,
name|appAcls
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|appLogAggregators
operator|.
name|putIfAbsent
argument_list|(
name|appId
argument_list|,
name|appLogAggregator
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Duplicate initApp for "
operator|+
name|appId
argument_list|)
throw|;
block|}
comment|// wait until check for existing aggregator to create dirs
try|try
block|{
comment|// Create the app dir
name|createAppDir
argument_list|(
name|user
argument_list|,
name|appId
argument_list|,
name|userUgi
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|appLogAggregators
operator|.
name|remove
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|closeFileSystems
argument_list|(
name|userUgi
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|e
operator|instanceof
name|YarnRuntimeException
operator|)
condition|)
block|{
name|e
operator|=
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|(
name|YarnRuntimeException
operator|)
name|e
throw|;
block|}
comment|// TODO Get the user configuration for the list of containers that need log
comment|// aggregation.
comment|// Schedule the aggregator.
name|Runnable
name|aggregatorWrapper
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|appLogAggregator
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|appLogAggregators
operator|.
name|remove
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|closeFileSystems
argument_list|(
name|userUgi
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|this
operator|.
name|threadPool
operator|.
name|execute
argument_list|(
name|aggregatorWrapper
argument_list|)
expr_stmt|;
block|}
DECL|method|closeFileSystems (final UserGroupInformation userUgi)
specifier|protected
name|void
name|closeFileSystems
parameter_list|(
specifier|final
name|UserGroupInformation
name|userUgi
parameter_list|)
block|{
try|try
block|{
name|FileSystem
operator|.
name|closeAllForUGI
argument_list|(
name|userUgi
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
name|warn
argument_list|(
literal|"Failed to close filesystems: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// for testing only
annotation|@
name|Private
DECL|method|getNumAggregators ()
name|int
name|getNumAggregators
parameter_list|()
block|{
return|return
name|this
operator|.
name|appLogAggregators
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|stopContainer (ContainerId containerId, int exitCode)
specifier|private
name|void
name|stopContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|int
name|exitCode
parameter_list|)
block|{
comment|// A container is complete. Put this containers' logs up for aggregation if
comment|// this containers' logs are needed.
name|AppLogAggregator
name|aggregator
init|=
name|this
operator|.
name|appLogAggregators
operator|.
name|get
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregator
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Log aggregation is not initialized for "
operator|+
name|containerId
operator|+
literal|", did it fail to start?"
argument_list|)
expr_stmt|;
return|return;
block|}
name|aggregator
operator|.
name|startContainerLogAggregation
argument_list|(
name|containerId
argument_list|,
name|exitCode
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|stopApp (ApplicationId appId)
specifier|private
name|void
name|stopApp
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
comment|// App is complete. Finish up any containers' pending log aggregation and
comment|// close the application specific logFile.
name|AppLogAggregator
name|aggregator
init|=
name|this
operator|.
name|appLogAggregators
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregator
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Log aggregation is not initialized for "
operator|+
name|appId
operator|+
literal|", did it fail to start?"
argument_list|)
expr_stmt|;
return|return;
block|}
name|aggregator
operator|.
name|finishLogAggregation
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (LogHandlerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|LogHandlerEvent
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|APPLICATION_STARTED
case|:
name|LogHandlerAppStartedEvent
name|appStartEvent
init|=
operator|(
name|LogHandlerAppStartedEvent
operator|)
name|event
decl_stmt|;
name|initApp
argument_list|(
name|appStartEvent
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|appStartEvent
operator|.
name|getUser
argument_list|()
argument_list|,
name|appStartEvent
operator|.
name|getCredentials
argument_list|()
argument_list|,
name|appStartEvent
operator|.
name|getLogRetentionPolicy
argument_list|()
argument_list|,
name|appStartEvent
operator|.
name|getApplicationAcls
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|CONTAINER_FINISHED
case|:
name|LogHandlerContainerFinishedEvent
name|containerFinishEvent
init|=
operator|(
name|LogHandlerContainerFinishedEvent
operator|)
name|event
decl_stmt|;
name|stopContainer
argument_list|(
name|containerFinishEvent
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|containerFinishEvent
operator|.
name|getExitCode
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|APPLICATION_FINISHED
case|:
name|LogHandlerAppFinishedEvent
name|appFinishedEvent
init|=
operator|(
name|LogHandlerAppFinishedEvent
operator|)
name|event
decl_stmt|;
name|stopApp
argument_list|(
name|appFinishedEvent
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
empty_stmt|;
comment|// Ignore
block|}
block|}
block|}
end_class

end_unit

