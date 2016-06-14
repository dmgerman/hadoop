begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
name|webapp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|List
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
name|io
operator|.
name|SecureIOUtils
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
name|Application
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
name|container
operator|.
name|Container
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
name|container
operator|.
name|ContainerState
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
name|launcher
operator|.
name|ContainerLaunch
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
name|NotFoundException
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
comment|/**  * Contains utilities for fetching a user's log file in a secure fashion.  */
end_comment

begin_class
DECL|class|ContainerLogsUtils
specifier|public
class|class
name|ContainerLogsUtils
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerLogsUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Finds the local directories that logs for the given container are stored    * on.    */
DECL|method|getContainerLogDirs (ContainerId containerId, String remoteUser, Context context)
specifier|public
specifier|static
name|List
argument_list|<
name|File
argument_list|>
name|getContainerLogDirs
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|String
name|remoteUser
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|YarnException
block|{
name|Container
name|container
init|=
name|context
operator|.
name|getContainers
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|Application
name|application
init|=
name|getApplicationForContainer
argument_list|(
name|containerId
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|remoteUser
argument_list|,
name|application
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|// It is not required to have null check for container ( container == null )
comment|// and throw back exception.Because when container is completed, NodeManager
comment|// remove container information from its NMContext.Configuring log
comment|// aggregation to false, container log view request is forwarded to NM. NM
comment|// does not have completed container information,but still NM serve request for
comment|// reading container logs.
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|checkState
argument_list|(
name|container
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|getContainerLogDirs
argument_list|(
name|containerId
argument_list|,
name|context
operator|.
name|getLocalDirsHandler
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getContainerLogDirs (ContainerId containerId, LocalDirsHandlerService dirsHandler)
specifier|static
name|List
argument_list|<
name|File
argument_list|>
name|getContainerLogDirs
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
throws|throws
name|YarnException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
init|=
name|dirsHandler
operator|.
name|getLogDirsForRead
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|containerLogDirs
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|(
name|logDirs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|logDir
range|:
name|logDirs
control|)
block|{
name|logDir
operator|=
operator|new
name|File
argument_list|(
name|logDir
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|String
name|appIdStr
init|=
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|File
name|appLogDir
init|=
operator|new
name|File
argument_list|(
name|logDir
argument_list|,
name|appIdStr
argument_list|)
decl_stmt|;
name|containerLogDirs
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|appLogDir
argument_list|,
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|containerLogDirs
return|;
block|}
comment|/**    * Finds the log file with the given filename for the given container.    */
DECL|method|getContainerLogFile (ContainerId containerId, String fileName, String remoteUser, Context context)
specifier|public
specifier|static
name|File
name|getContainerLogFile
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|remoteUser
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|YarnException
block|{
name|Container
name|container
init|=
name|context
operator|.
name|getContainers
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|Application
name|application
init|=
name|getApplicationForContainer
argument_list|(
name|containerId
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
name|remoteUser
argument_list|,
name|application
argument_list|,
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|checkState
argument_list|(
name|container
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|LocalDirsHandlerService
name|dirsHandler
init|=
name|context
operator|.
name|getLocalDirsHandler
argument_list|()
decl_stmt|;
name|String
name|relativeContainerLogDir
init|=
name|ContainerLaunch
operator|.
name|getRelativeContainerLogDir
argument_list|(
name|application
operator|.
name|getAppId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|logPath
init|=
name|dirsHandler
operator|.
name|getLogPathToRead
argument_list|(
name|relativeContainerLogDir
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|fileName
argument_list|)
decl_stmt|;
name|URI
name|logPathURI
init|=
operator|new
name|File
argument_list|(
name|logPath
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|toURI
argument_list|()
decl_stmt|;
name|File
name|logFile
init|=
operator|new
name|File
argument_list|(
name|logPathURI
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|logFile
return|;
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
literal|"Failed to find log file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Cannot find this log on the local disk."
argument_list|)
throw|;
block|}
block|}
DECL|method|getApplicationForContainer (ContainerId containerId, Context context)
specifier|private
specifier|static
name|Application
name|getApplicationForContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|Application
name|application
init|=
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
if|if
condition|(
name|application
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Unknown container. Container either has not started or "
operator|+
literal|"has already completed or "
operator|+
literal|"doesn't belong to this node at all."
argument_list|)
throw|;
block|}
return|return
name|application
return|;
block|}
DECL|method|checkAccess (String remoteUser, Application application, Context context)
specifier|private
specifier|static
name|void
name|checkAccess
parameter_list|(
name|String
name|remoteUser
parameter_list|,
name|Application
name|application
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|YarnException
block|{
name|UserGroupInformation
name|callerUGI
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|remoteUser
operator|!=
literal|null
condition|)
block|{
name|callerUGI
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|remoteUser
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|callerUGI
operator|!=
literal|null
operator|&&
operator|!
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|application
operator|.
name|getUser
argument_list|()
argument_list|,
name|application
operator|.
name|getAppId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"User ["
operator|+
name|remoteUser
operator|+
literal|"] is not authorized to view the logs for application "
operator|+
name|application
operator|.
name|getAppId
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|checkState (ContainerState state)
specifier|private
specifier|static
name|void
name|checkState
parameter_list|(
name|ContainerState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
name|ContainerState
operator|.
name|NEW
operator|||
name|state
operator|==
name|ContainerState
operator|.
name|LOCALIZING
operator|||
name|state
operator|==
name|ContainerState
operator|.
name|LOCALIZED
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Container is not yet running. Current state is "
operator|+
name|state
argument_list|)
throw|;
block|}
if|if
condition|(
name|state
operator|==
name|ContainerState
operator|.
name|LOCALIZATION_FAILED
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Container wasn't started. Localization failed."
argument_list|)
throw|;
block|}
block|}
DECL|method|openLogFileForRead (String containerIdStr, File logFile, Context context)
specifier|public
specifier|static
name|FileInputStream
name|openLogFileForRead
parameter_list|(
name|String
name|containerIdStr
parameter_list|,
name|File
name|logFile
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|fromString
argument_list|(
name|containerIdStr
argument_list|)
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|String
name|user
init|=
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
operator|.
name|getUser
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|SecureIOUtils
operator|.
name|openForRead
argument_list|(
name|logFile
argument_list|,
name|user
argument_list|,
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"did not match expected owner '"
operator|+
name|user
operator|+
literal|"'"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception reading log file "
operator|+
name|logFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception reading log file. Application submitted by '"
operator|+
name|user
operator|+
literal|"' doesn't own requested log file : "
operator|+
name|logFile
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception reading log file. It might be because log "
operator|+
literal|"file was aggregated : "
operator|+
name|logFile
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

