begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation.filecontroller
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
operator|.
name|filecontroller
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
name|io
operator|.
name|OutputStream
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|io
operator|.
name|IOUtils
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
name|RemoteException
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
name|token
operator|.
name|SecretManager
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
name|exceptions
operator|.
name|YarnException
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
name|webapp
operator|.
name|View
operator|.
name|ViewContext
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
name|webapp
operator|.
name|view
operator|.
name|HtmlBlock
operator|.
name|Block
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
name|ContainerLogMeta
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
name|ContainerLogsRequest
import|;
end_import

begin_comment
comment|/**  * Base class to implement Log Aggregation File Controller.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|LogAggregationFileController
specifier|public
specifier|abstract
class|class
name|LogAggregationFileController
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
name|LogAggregationFileController
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/*    * Expected deployment TLD will be 1777, owner=<NMOwner>, group=<NMGroup -    * Group to which NMOwner belongs> App dirs will be created as 770,    * owner=<AppOwner>, group=<NMGroup>: so that the owner and<NMOwner> can    * access / modify the files.    *<NMGroup> should obviously be a limited access group.    */
comment|/**    * Permissions for the top level directory under which app directories will be    * created.    */
DECL|field|TLDIR_PERMISSIONS
specifier|protected
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
specifier|protected
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
comment|/**    * Umask for the log file.    */
DECL|field|APP_LOG_FILE_UMASK
specifier|protected
specifier|static
specifier|final
name|FsPermission
name|APP_LOG_FILE_UMASK
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
call|(
name|short
call|)
argument_list|(
literal|0640
operator|^
literal|0777
argument_list|)
argument_list|)
decl_stmt|;
comment|// This is temporary solution. The configuration will be deleted once we have
comment|// the FileSystem API to check whether append operation is supported or not.
DECL|field|LOG_AGGREGATION_FS_SUPPORT_APPEND
specifier|public
specifier|static
specifier|final
name|String
name|LOG_AGGREGATION_FS_SUPPORT_APPEND
init|=
name|YarnConfiguration
operator|.
name|YARN_PREFIX
operator|+
literal|"log-aggregation.fs-support-append"
decl_stmt|;
DECL|field|conf
specifier|protected
name|Configuration
name|conf
decl_stmt|;
DECL|field|remoteRootLogDir
specifier|protected
name|Path
name|remoteRootLogDir
decl_stmt|;
DECL|field|remoteRootLogDirSuffix
specifier|protected
name|String
name|remoteRootLogDirSuffix
decl_stmt|;
DECL|field|retentionSize
specifier|protected
name|int
name|retentionSize
decl_stmt|;
DECL|field|fileControllerName
specifier|protected
name|String
name|fileControllerName
decl_stmt|;
DECL|method|LogAggregationFileController ()
specifier|public
name|LogAggregationFileController
parameter_list|()
block|{}
comment|/**    * Initialize the log file controller.    * @param conf the Configuration    * @param controllerName the log controller class name    */
DECL|method|initialize (Configuration conf, String controllerName)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|controllerName
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|int
name|configuredRetentionSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_AGGREGATION_NUM_LOG_FILES_SIZE_PER_APP
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_AGGREGATION_NUM_LOG_FILES_SIZE_PER_APP
argument_list|)
decl_stmt|;
if|if
condition|(
name|configuredRetentionSize
operator|<=
literal|0
condition|)
block|{
name|this
operator|.
name|retentionSize
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_AGGREGATION_NUM_LOG_FILES_SIZE_PER_APP
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|retentionSize
operator|=
name|configuredRetentionSize
expr_stmt|;
block|}
name|this
operator|.
name|fileControllerName
operator|=
name|controllerName
expr_stmt|;
name|initInternal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Derived classes initialize themselves using this method.    * @param conf the Configuration    */
DECL|method|initInternal (Configuration conf)
specifier|protected
specifier|abstract
name|void
name|initInternal
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
comment|/**    * Get the remote root log directory.    * @return the remote root log directory path    */
DECL|method|getRemoteRootLogDir ()
specifier|public
name|Path
name|getRemoteRootLogDir
parameter_list|()
block|{
return|return
name|this
operator|.
name|remoteRootLogDir
return|;
block|}
comment|/**    * Get the log aggregation directory suffix.    * @return the log aggregation directory suffix    */
DECL|method|getRemoteRootLogDirSuffix ()
specifier|public
name|String
name|getRemoteRootLogDirSuffix
parameter_list|()
block|{
return|return
name|this
operator|.
name|remoteRootLogDirSuffix
return|;
block|}
comment|/**    * Initialize the writer.    * @param context the {@link LogAggregationFileControllerContext}    * @throws IOException if fails to initialize the writer    */
DECL|method|initializeWriter ( LogAggregationFileControllerContext context)
specifier|public
specifier|abstract
name|void
name|initializeWriter
parameter_list|(
name|LogAggregationFileControllerContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close the writer.    * @throws LogAggregationDFSException if the closing of the writer fails    *         (for example due to HDFS quota being exceeded)    */
DECL|method|closeWriter ()
specifier|public
specifier|abstract
name|void
name|closeWriter
parameter_list|()
throws|throws
name|LogAggregationDFSException
function_decl|;
comment|/**    * Write the log content.    * @param logKey the log key    * @param logValue the log content    * @throws IOException if fails to write the logs    */
DECL|method|write (LogKey logKey, LogValue logValue)
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|LogKey
name|logKey
parameter_list|,
name|LogValue
name|logValue
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Operations needed after write the log content.    * @param record the {@link LogAggregationFileControllerContext}    * @throws Exception if anything fails    */
DECL|method|postWrite (LogAggregationFileControllerContext record)
specifier|public
specifier|abstract
name|void
name|postWrite
parameter_list|(
name|LogAggregationFileControllerContext
name|record
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|closePrintStream (OutputStream out)
specifier|protected
name|void
name|closePrintStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
if|if
condition|(
name|out
operator|!=
name|System
operator|.
name|out
condition|)
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Output container log.    * @param logRequest {@link ContainerLogsRequest}    * @param os the output stream    * @return true if we can read the aggregated logs successfully    * @throws IOException if we can not access the log file.    */
DECL|method|readAggregatedLogs (ContainerLogsRequest logRequest, OutputStream os)
specifier|public
specifier|abstract
name|boolean
name|readAggregatedLogs
parameter_list|(
name|ContainerLogsRequest
name|logRequest
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return a list of {@link ContainerLogMeta} for an application    * from Remote FileSystem.    *    * @param logRequest {@link ContainerLogsRequest}    * @return a list of {@link ContainerLogMeta}    * @throws IOException if there is no available log file    */
DECL|method|readAggregatedLogsMeta ( ContainerLogsRequest logRequest)
specifier|public
specifier|abstract
name|List
argument_list|<
name|ContainerLogMeta
argument_list|>
name|readAggregatedLogsMeta
parameter_list|(
name|ContainerLogsRequest
name|logRequest
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Render Aggregated Logs block.    * @param html the html    * @param context the ViewContext    */
DECL|method|renderAggregatedLogsBlock (Block html, ViewContext context)
specifier|public
specifier|abstract
name|void
name|renderAggregatedLogsBlock
parameter_list|(
name|Block
name|html
parameter_list|,
name|ViewContext
name|context
parameter_list|)
function_decl|;
comment|/**    * Returns the owner of the application.    *    * @param aggregatedLogPath the aggregatedLog path    * @param appId the ApplicationId    * @return the application owner    * @throws IOException if we can not get the application owner    */
DECL|method|getApplicationOwner (Path aggregatedLogPath, ApplicationId appId)
specifier|public
specifier|abstract
name|String
name|getApplicationOwner
parameter_list|(
name|Path
name|aggregatedLogPath
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns ACLs for the application. An empty map is returned if no ACLs are    * found.    *    * @param aggregatedLogPath the aggregatedLog path.    * @param appId the ApplicationId    * @return a map of the Application ACLs.    * @throws IOException if we can not get the application acls    */
DECL|method|getApplicationAcls ( Path aggregatedLogPath, ApplicationId appId)
specifier|public
specifier|abstract
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|getApplicationAcls
parameter_list|(
name|Path
name|aggregatedLogPath
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Verify and create the remote log directory.    */
DECL|method|verifyAndCreateRemoteLogDir ()
specifier|public
name|void
name|verifyAndCreateRemoteLogDir
parameter_list|()
block|{
name|boolean
name|logPermError
init|=
literal|true
decl_stmt|;
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
name|Path
name|remoteRootLogDir
init|=
name|getRemoteRootLogDir
argument_list|()
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
operator|&&
name|logPermError
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Remote Root Log Dir ["
operator|+
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
name|logPermError
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|logPermError
operator|=
literal|true
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
name|UserGroupInformation
name|loginUser
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
name|String
name|primaryGroupName
init|=
literal|null
decl_stmt|;
try|try
block|{
name|primaryGroupName
operator|=
name|loginUser
operator|.
name|getPrimaryGroupName
argument_list|()
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
literal|"No primary group found. The remote root log directory"
operator|+
literal|" will be created with the HDFS superuser being its group "
operator|+
literal|"owner. JobHistoryServer may be unable to read the directory."
argument_list|)
expr_stmt|;
block|}
comment|// set owner on the remote directory only if the primary group exists
if|if
condition|(
name|primaryGroupName
operator|!=
literal|null
condition|)
block|{
name|remoteFS
operator|.
name|setOwner
argument_list|(
name|qualified
argument_list|,
name|loginUser
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|primaryGroupName
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
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Failed to create remoteLogDir ["
operator|+
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
comment|/**    * Create remote Application directory for log aggregation.    * @param user the user    * @param appId the application ID    * @param userUgi the UGI    */
DECL|method|createAppDir (final String user, final ApplicationId appId, UserGroupInformation userUgi)
specifier|public
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
specifier|final
name|Path
name|remoteRootLogDir
init|=
name|getRemoteRootLogDir
argument_list|()
decl_stmt|;
specifier|final
name|String
name|remoteRootLogDirSuffix
init|=
name|getRemoteRootLogDirSuffix
argument_list|()
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
try|try
block|{
comment|// TODO: Reuse FS for user?
name|FileSystem
name|remoteFS
init|=
name|getFileSystem
argument_list|(
name|conf
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
name|remoteRootLogDir
argument_list|,
name|appId
argument_list|,
name|user
argument_list|,
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
name|remoteRootLogDir
argument_list|,
name|user
argument_list|,
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
if|if
condition|(
name|e
operator|instanceof
name|RemoteException
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
operator|(
operator|(
name|RemoteException
operator|)
name|e
operator|)
operator|.
name|unwrapRemoteException
argument_list|(
name|SecretManager
operator|.
name|InvalidToken
operator|.
name|class
argument_list|)
argument_list|)
throw|;
block|}
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
name|VisibleForTesting
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
name|getRemoteRootLogDir
argument_list|()
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|createDir (FileSystem fs, Path path, FsPermission fsPerm)
specifier|protected
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
specifier|protected
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
comment|/**    * Get the remote aggregated log path.    * @param appId the ApplicationId    * @param user the Application Owner    * @param nodeId the NodeManager Id    * @return the remote aggregated log path    */
DECL|method|getRemoteNodeLogFileForApp (ApplicationId appId, String user, NodeId nodeId)
specifier|public
name|Path
name|getRemoteNodeLogFileForApp
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|user
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
block|{
return|return
name|LogAggregationUtils
operator|.
name|getRemoteNodeLogFileForApp
argument_list|(
name|getRemoteRootLogDir
argument_list|()
argument_list|,
name|appId
argument_list|,
name|user
argument_list|,
name|nodeId
argument_list|,
name|getRemoteRootLogDirSuffix
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the remote application directory for log aggregation.    * @param appId the Application ID    * @param appOwner the Application Owner    * @return the remote application directory    * @throws IOException if can not find the remote application directory    */
DECL|method|getRemoteAppLogDir (ApplicationId appId, String appOwner)
specifier|public
name|Path
name|getRemoteAppLogDir
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|appOwner
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|LogAggregationUtils
operator|.
name|getRemoteAppLogDir
argument_list|(
name|conf
argument_list|,
name|appId
argument_list|,
name|appOwner
argument_list|,
name|this
operator|.
name|remoteRootLogDir
argument_list|,
name|this
operator|.
name|remoteRootLogDirSuffix
argument_list|)
return|;
block|}
DECL|method|cleanOldLogs (Path remoteNodeLogFileForApp, final NodeId nodeId, UserGroupInformation userUgi)
specifier|protected
name|void
name|cleanOldLogs
parameter_list|(
name|Path
name|remoteNodeLogFileForApp
parameter_list|,
specifier|final
name|NodeId
name|nodeId
parameter_list|,
name|UserGroupInformation
name|userUgi
parameter_list|)
block|{
try|try
block|{
specifier|final
name|FileSystem
name|remoteFS
init|=
name|remoteNodeLogFileForApp
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|appDir
init|=
name|remoteNodeLogFileForApp
operator|.
name|getParent
argument_list|()
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
name|Set
argument_list|<
name|FileStatus
argument_list|>
name|status
init|=
operator|new
name|HashSet
argument_list|<
name|FileStatus
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|remoteFS
operator|.
name|listStatus
argument_list|(
name|appDir
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|FileStatus
argument_list|>
name|mask
init|=
name|Iterables
operator|.
name|filter
argument_list|(
name|status
argument_list|,
operator|new
name|Predicate
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|FileStatus
name|next
parameter_list|)
block|{
return|return
name|next
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
name|LogAggregationUtils
operator|.
name|getNodeString
argument_list|(
name|nodeId
argument_list|)
argument_list|)
operator|&&
operator|!
name|next
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|LogAggregationUtils
operator|.
name|TMP_FILE_SUFFIX
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|status
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|mask
argument_list|)
expr_stmt|;
comment|// Normally, we just need to delete one oldest log
comment|// before we upload a new log.
comment|// If we can not delete the older logs in this cycle,
comment|// we will delete them in next cycle.
if|if
condition|(
name|status
operator|.
name|size
argument_list|()
operator|>=
name|this
operator|.
name|retentionSize
condition|)
block|{
comment|// sort by the lastModificationTime ascending
name|List
argument_list|<
name|FileStatus
argument_list|>
name|statusList
init|=
operator|new
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
argument_list|(
name|status
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|statusList
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|FileStatus
name|s1
parameter_list|,
name|FileStatus
name|s2
parameter_list|)
block|{
return|return
name|s1
operator|.
name|getModificationTime
argument_list|()
operator|<
name|s2
operator|.
name|getModificationTime
argument_list|()
condition|?
operator|-
literal|1
else|:
name|s1
operator|.
name|getModificationTime
argument_list|()
operator|>
name|s2
operator|.
name|getModificationTime
argument_list|()
condition|?
literal|1
else|:
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|statusList
operator|.
name|size
argument_list|()
operator|-
name|this
operator|.
name|retentionSize
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FileStatus
name|remove
init|=
name|statusList
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|remoteFS
operator|.
name|delete
argument_list|(
name|remove
operator|.
name|getPath
argument_list|()
argument_list|,
literal|false
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
literal|"Failed to delete "
operator|+
name|remove
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
literal|"Failed to clean old logs"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create the aggregated log suffix. The LogAggregationFileController    * should call this to get the suffix and append the suffix to the end    * of each log. This would keep the aggregated log format consistent.    *    * @param fileName the File Name    * @return the aggregated log suffix String    */
DECL|method|aggregatedLogSuffix (String fileName)
specifier|protected
name|String
name|aggregatedLogSuffix
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|endOfFile
init|=
literal|"End of LogType:"
operator|+
name|fileName
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
operator|+
name|endOfFile
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"*"
argument_list|,
name|endOfFile
operator|.
name|length
argument_list|()
operator|+
literal|50
argument_list|)
operator|+
literal|"\n\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

