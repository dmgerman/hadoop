begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager
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
name|IOException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|FileContext
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
name|UnsupportedFileSystemException
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
name|ContainerManager
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
name|protocolrecords
operator|.
name|GetContainerStatusRequest
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
name|api
operator|.
name|records
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
name|api
operator|.
name|records
operator|.
name|ContainerStatus
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
name|AsyncDispatcher
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
name|YarnRemoteException
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|ResourceTracker
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
name|ContainerExecutor
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
name|DefaultContainerExecutor
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
name|LocalRMInterface
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
name|NodeHealthCheckerService
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
name|NodeManager
operator|.
name|NMContext
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
name|NodeStatusUpdater
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
name|NodeStatusUpdaterImpl
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
name|application
operator|.
name|ApplicationState
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
name|metrics
operator|.
name|NodeManagerMetrics
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
name|security
operator|.
name|NMContainerTokenSecretManager
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
name|security
operator|.
name|ApplicationACLsManager
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
name|Service
operator|.
name|STATE
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_class
DECL|class|BaseContainerManagerTest
specifier|public
specifier|abstract
class|class
name|BaseContainerManagerTest
block|{
DECL|field|recordFactory
specifier|protected
specifier|static
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|localFS
specifier|protected
specifier|static
name|FileContext
name|localFS
decl_stmt|;
DECL|field|localDir
specifier|protected
specifier|static
name|File
name|localDir
decl_stmt|;
DECL|field|localLogDir
specifier|protected
specifier|static
name|File
name|localLogDir
decl_stmt|;
DECL|field|remoteLogDir
specifier|protected
specifier|static
name|File
name|remoteLogDir
decl_stmt|;
DECL|field|tmpDir
specifier|protected
specifier|static
name|File
name|tmpDir
decl_stmt|;
DECL|field|metrics
specifier|protected
specifier|final
name|NodeManagerMetrics
name|metrics
init|=
name|NodeManagerMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
DECL|method|BaseContainerManagerTest ()
specifier|public
name|BaseContainerManagerTest
parameter_list|()
throws|throws
name|UnsupportedFileSystemException
block|{
name|localFS
operator|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
expr_stmt|;
name|localDir
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-localDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|localLogDir
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-localLogDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|remoteLogDir
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-remoteLogDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|tmpDir
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-tmpDir"
argument_list|)
expr_stmt|;
block|}
DECL|field|LOG
specifier|protected
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BaseContainerManagerTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|protected
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
DECL|field|context
specifier|protected
name|Context
name|context
init|=
operator|new
name|NMContext
argument_list|(
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|exec
specifier|protected
name|ContainerExecutor
name|exec
decl_stmt|;
DECL|field|delSrvc
specifier|protected
name|DeletionService
name|delSrvc
decl_stmt|;
DECL|field|user
specifier|protected
name|String
name|user
init|=
literal|"nobody"
decl_stmt|;
DECL|field|nodeHealthChecker
specifier|protected
name|NodeHealthCheckerService
name|nodeHealthChecker
decl_stmt|;
DECL|field|dirsHandler
specifier|protected
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|field|nodeStatusUpdater
specifier|protected
name|NodeStatusUpdater
name|nodeStatusUpdater
init|=
operator|new
name|NodeStatusUpdaterImpl
argument_list|(
name|context
argument_list|,
operator|new
name|AsyncDispatcher
argument_list|()
argument_list|,
literal|null
argument_list|,
name|metrics
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ResourceTracker
name|getRMClient
parameter_list|()
block|{
return|return
operator|new
name|LocalRMInterface
argument_list|()
return|;
block|}
empty_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|startStatusUpdater
parameter_list|()
block|{
return|return;
comment|// Don't start any updating thread.
block|}
block|}
decl_stmt|;
DECL|field|containerManager
specifier|protected
name|ContainerManagerImpl
name|containerManager
init|=
literal|null
decl_stmt|;
DECL|method|createContainerExecutor ()
specifier|protected
name|ContainerExecutor
name|createContainerExecutor
parameter_list|()
block|{
name|DefaultContainerExecutor
name|exec
init|=
operator|new
name|DefaultContainerExecutor
argument_list|()
decl_stmt|;
name|exec
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|exec
return|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|localFS
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localFS
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|tmpDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localFS
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|localLogDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localFS
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|remoteLogDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|tmpDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|localLogDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|remoteLogDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created localDir in "
operator|+
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created tmpDir in "
operator|+
name|tmpDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|bindAddress
init|=
literal|"0.0.0.0:5555"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ADDRESS
argument_list|,
name|bindAddress
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|localLogDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR
argument_list|,
name|remoteLogDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Default delSrvc
name|delSrvc
operator|=
operator|new
name|DeletionService
argument_list|(
name|exec
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|user
parameter_list|,
name|Path
name|subDir
parameter_list|,
name|Path
index|[]
name|baseDirs
parameter_list|)
block|{
comment|// Don't do any deletions.
name|LOG
operator|.
name|info
argument_list|(
literal|"Psuedo delete: user - "
operator|+
name|user
operator|+
literal|", subDir - "
operator|+
name|subDir
operator|+
literal|", baseDirs - "
operator|+
name|baseDirs
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
expr_stmt|;
name|delSrvc
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|exec
operator|=
name|createContainerExecutor
argument_list|()
expr_stmt|;
name|nodeHealthChecker
operator|=
operator|new
name|NodeHealthCheckerService
argument_list|()
expr_stmt|;
name|nodeHealthChecker
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dirsHandler
operator|=
name|nodeHealthChecker
operator|.
name|getDiskHandler
argument_list|()
expr_stmt|;
name|containerManager
operator|=
operator|new
name|ContainerManagerImpl
argument_list|(
name|context
argument_list|,
name|exec
argument_list|,
name|delSrvc
argument_list|,
name|nodeStatusUpdater
argument_list|,
name|metrics
argument_list|,
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|,
name|dirsHandler
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|setBlockNewContainerRequests
parameter_list|(
name|boolean
name|blockNewContainerRequests
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
expr_stmt|;
name|containerManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|containerManager
operator|!=
literal|null
operator|&&
name|containerManager
operator|.
name|getServiceState
argument_list|()
operator|==
name|STATE
operator|.
name|STARTED
condition|)
block|{
name|containerManager
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|createContainerExecutor
argument_list|()
operator|.
name|deleteAsUser
argument_list|(
name|user
argument_list|,
operator|new
name|Path
argument_list|(
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Path
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForContainerState (ContainerManager containerManager, ContainerId containerID, ContainerState finalState)
specifier|public
specifier|static
name|void
name|waitForContainerState
parameter_list|(
name|ContainerManager
name|containerManager
parameter_list|,
name|ContainerId
name|containerID
parameter_list|,
name|ContainerState
name|finalState
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|YarnRemoteException
block|{
name|waitForContainerState
argument_list|(
name|containerManager
argument_list|,
name|containerID
argument_list|,
name|finalState
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForContainerState (ContainerManager containerManager, ContainerId containerID, ContainerState finalState, int timeOutMax)
specifier|public
specifier|static
name|void
name|waitForContainerState
parameter_list|(
name|ContainerManager
name|containerManager
parameter_list|,
name|ContainerId
name|containerID
parameter_list|,
name|ContainerState
name|finalState
parameter_list|,
name|int
name|timeOutMax
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|YarnRemoteException
block|{
name|GetContainerStatusRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetContainerStatusRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setContainerId
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|containerManager
operator|.
name|getContainerStatus
argument_list|(
name|request
argument_list|)
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|int
name|timeoutSecs
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|containerStatus
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|finalState
argument_list|)
operator|&&
name|timeoutSecs
operator|++
operator|<
name|timeOutMax
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for container to get into state "
operator|+
name|finalState
operator|+
literal|". Current state is "
operator|+
name|containerStatus
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|containerStatus
operator|=
name|containerManager
operator|.
name|getContainerStatus
argument_list|(
name|request
argument_list|)
operator|.
name|getStatus
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Container state is "
operator|+
name|containerStatus
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ContainerState is not correct (timedout)"
argument_list|,
name|finalState
argument_list|,
name|containerStatus
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForApplicationState (ContainerManagerImpl containerManager, ApplicationId appID, ApplicationState finalState)
specifier|static
name|void
name|waitForApplicationState
parameter_list|(
name|ContainerManagerImpl
name|containerManager
parameter_list|,
name|ApplicationId
name|appID
parameter_list|,
name|ApplicationState
name|finalState
parameter_list|)
throws|throws
name|InterruptedException
block|{
comment|// Wait for app-finish
name|Application
name|app
init|=
name|containerManager
operator|.
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|appID
argument_list|)
decl_stmt|;
name|int
name|timeout
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
operator|(
name|app
operator|.
name|getApplicationState
argument_list|()
operator|.
name|equals
argument_list|(
name|finalState
argument_list|)
operator|)
operator|&&
name|timeout
operator|++
operator|<
literal|15
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for app to reach "
operator|+
name|finalState
operator|+
literal|".. Current state is "
operator|+
name|app
operator|.
name|getApplicationState
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"App is not in "
operator|+
name|finalState
operator|+
literal|" yet!! Timedout!!"
argument_list|,
name|app
operator|.
name|getApplicationState
argument_list|()
operator|.
name|equals
argument_list|(
name|finalState
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

