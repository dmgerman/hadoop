begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BrokenBarrierException
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
name|CyclicBarrier
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
name|protocolrecords
operator|.
name|StartContainerRequest
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
name|StartContainersRequest
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
name|ContainerLaunchContext
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
name|exceptions
operator|.
name|NMNotYetReadyException
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|ContainerManagerImpl
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
name|security
operator|.
name|ApplicationACLsManager
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestNodeManagerResync
specifier|public
class|class
name|TestNodeManagerResync
block|{
DECL|field|basedir
specifier|static
specifier|final
name|File
name|basedir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestNodeManagerResync
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|tmpDir
specifier|static
specifier|final
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"tmpDir"
argument_list|)
decl_stmt|;
DECL|field|logsDir
specifier|static
specifier|final
name|File
name|logsDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"logs"
argument_list|)
decl_stmt|;
DECL|field|remoteLogsDir
specifier|static
specifier|final
name|File
name|remoteLogsDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"remotelogs"
argument_list|)
decl_stmt|;
DECL|field|nmLocalDir
specifier|static
specifier|final
name|File
name|nmLocalDir
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
literal|"nm0"
argument_list|)
decl_stmt|;
DECL|field|processStartFile
specifier|static
specifier|final
name|File
name|processStartFile
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"start_file.txt"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|recordFactory
specifier|static
specifier|final
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
DECL|field|user
specifier|static
specifier|final
name|String
name|user
init|=
literal|"nobody"
decl_stmt|;
DECL|field|localFS
specifier|private
name|FileContext
name|localFS
decl_stmt|;
DECL|field|syncBarrier
specifier|private
name|CyclicBarrier
name|syncBarrier
decl_stmt|;
DECL|field|assertionFailedInThread
specifier|private
name|AtomicBoolean
name|assertionFailedInThread
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
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
name|tmpDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|logsDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|remoteLogsDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|nmLocalDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|syncBarrier
operator|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
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
name|localFS
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|basedir
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertionFailedInThread
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testKillContainersOnResync ()
specifier|public
name|void
name|testKillContainersOnResync
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
name|NodeManager
name|nm
init|=
operator|new
name|TestNodeManager1
argument_list|()
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
name|createNMConfig
argument_list|()
decl_stmt|;
name|nm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nm
operator|.
name|start
argument_list|()
expr_stmt|;
name|ContainerId
name|cId
init|=
name|TestNodeManagerShutdown
operator|.
name|createContainerId
argument_list|()
decl_stmt|;
name|TestNodeManagerShutdown
operator|.
name|startContainer
argument_list|(
name|nm
argument_list|,
name|cId
argument_list|,
name|localFS
argument_list|,
name|tmpDir
argument_list|,
name|processStartFile
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|TestNodeManager1
operator|)
name|nm
operator|)
operator|.
name|getNMRegistrationCount
argument_list|()
argument_list|)
expr_stmt|;
name|nm
operator|.
name|getNMDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|NodeManagerEvent
argument_list|(
name|NodeManagerEventType
operator|.
name|RESYNC
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|syncBarrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BrokenBarrierException
name|e
parameter_list|)
block|{     }
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|TestNodeManager1
operator|)
name|nm
operator|)
operator|.
name|getNMRegistrationCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|assertionFailedInThread
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|nm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// This test tests new container requests are blocked when NM starts from
comment|// scratch until it register with RM AND while NM is resyncing with RM
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testBlockNewContainerRequestsOnStartAndResync ()
specifier|public
name|void
name|testBlockNewContainerRequestsOnStartAndResync
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
name|NodeManager
name|nm
init|=
operator|new
name|TestNodeManager2
argument_list|()
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
name|createNMConfig
argument_list|()
decl_stmt|;
name|nm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Start the container in running state
name|ContainerId
name|cId
init|=
name|TestNodeManagerShutdown
operator|.
name|createContainerId
argument_list|()
decl_stmt|;
name|TestNodeManagerShutdown
operator|.
name|startContainer
argument_list|(
name|nm
argument_list|,
name|cId
argument_list|,
name|localFS
argument_list|,
name|tmpDir
argument_list|,
name|processStartFile
argument_list|)
expr_stmt|;
name|nm
operator|.
name|getNMDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|NodeManagerEvent
argument_list|(
name|NodeManagerEventType
operator|.
name|RESYNC
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|syncBarrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BrokenBarrierException
name|e
parameter_list|)
block|{     }
name|Assert
operator|.
name|assertFalse
argument_list|(
name|assertionFailedInThread
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|nm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|createNMConfig ()
specifier|private
name|YarnConfiguration
name|createNMConfig
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_MB
argument_list|,
literal|5
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// 5GB
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ADDRESS
argument_list|,
literal|"127.0.0.1:12345"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCALIZER_ADDRESS
argument_list|,
literal|"127.0.0.1:12346"
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
name|logsDir
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
name|remoteLogsDir
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
name|NM_LOCAL_DIRS
argument_list|,
name|nmLocalDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|class|TestNodeManager1
class|class
name|TestNodeManager1
extends|extends
name|NodeManager
block|{
DECL|field|registrationCount
specifier|private
name|int
name|registrationCount
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|createNodeStatusUpdater (Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker)
specifier|protected
name|NodeStatusUpdater
name|createNodeStatusUpdater
parameter_list|(
name|Context
name|context
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|NodeHealthCheckerService
name|healthChecker
parameter_list|)
block|{
return|return
operator|new
name|TestNodeStatusUpdaterImpl1
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|,
name|metrics
argument_list|)
return|;
block|}
DECL|method|getNMRegistrationCount ()
specifier|public
name|int
name|getNMRegistrationCount
parameter_list|()
block|{
return|return
name|registrationCount
return|;
block|}
DECL|class|TestNodeStatusUpdaterImpl1
class|class
name|TestNodeStatusUpdaterImpl1
extends|extends
name|MockNodeStatusUpdater
block|{
DECL|method|TestNodeStatusUpdaterImpl1 (Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker, NodeManagerMetrics metrics)
specifier|public
name|TestNodeStatusUpdaterImpl1
parameter_list|(
name|Context
name|context
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|NodeHealthCheckerService
name|healthChecker
parameter_list|,
name|NodeManagerMetrics
name|metrics
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|registerWithRM ()
specifier|protected
name|void
name|registerWithRM
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|super
operator|.
name|registerWithRM
argument_list|()
expr_stmt|;
name|registrationCount
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rebootNodeStatusUpdater ()
specifier|protected
name|void
name|rebootNodeStatusUpdater
parameter_list|()
block|{
name|ConcurrentMap
argument_list|<
name|ContainerId
argument_list|,
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
argument_list|>
name|containers
init|=
name|getNMContext
argument_list|()
operator|.
name|getContainers
argument_list|()
decl_stmt|;
try|try
block|{
comment|// ensure that containers are empty before restart nodeStatusUpdater
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|rebootNodeStatusUpdater
argument_list|()
expr_stmt|;
name|syncBarrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
catch|catch
parameter_list|(
name|BrokenBarrierException
name|e
parameter_list|)
block|{         }
catch|catch
parameter_list|(
name|AssertionError
name|ae
parameter_list|)
block|{
name|ae
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertionFailedInThread
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|TestNodeManager2
class|class
name|TestNodeManager2
extends|extends
name|NodeManager
block|{
DECL|field|launchContainersThread
name|Thread
name|launchContainersThread
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|createNodeStatusUpdater (Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker)
specifier|protected
name|NodeStatusUpdater
name|createNodeStatusUpdater
parameter_list|(
name|Context
name|context
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|NodeHealthCheckerService
name|healthChecker
parameter_list|)
block|{
return|return
operator|new
name|TestNodeStatusUpdaterImpl2
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|,
name|metrics
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createContainerManager (Context context, ContainerExecutor exec, DeletionService del, NodeStatusUpdater nodeStatusUpdater, ApplicationACLsManager aclsManager, LocalDirsHandlerService dirsHandler)
specifier|protected
name|ContainerManagerImpl
name|createContainerManager
parameter_list|(
name|Context
name|context
parameter_list|,
name|ContainerExecutor
name|exec
parameter_list|,
name|DeletionService
name|del
parameter_list|,
name|NodeStatusUpdater
name|nodeStatusUpdater
parameter_list|,
name|ApplicationACLsManager
name|aclsManager
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
block|{
return|return
operator|new
name|ContainerManagerImpl
argument_list|(
name|context
argument_list|,
name|exec
argument_list|,
name|del
argument_list|,
name|nodeStatusUpdater
argument_list|,
name|metrics
argument_list|,
name|aclsManager
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
if|if
condition|(
name|blockNewContainerRequests
condition|)
block|{
comment|// start test thread right after blockNewContainerRequests is set
comment|// true
name|super
operator|.
name|setBlockNewContainerRequests
argument_list|(
name|blockNewContainerRequests
argument_list|)
expr_stmt|;
name|launchContainersThread
operator|=
operator|new
name|RejectedContainersLauncherThread
argument_list|()
expr_stmt|;
name|launchContainersThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// join the test thread right before blockNewContainerRequests is
comment|// reset
try|try
block|{
comment|// stop the test thread
operator|(
operator|(
name|RejectedContainersLauncherThread
operator|)
name|launchContainersThread
operator|)
operator|.
name|setStopThreadFlag
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|launchContainersThread
operator|.
name|join
argument_list|()
expr_stmt|;
operator|(
operator|(
name|RejectedContainersLauncherThread
operator|)
name|launchContainersThread
operator|)
operator|.
name|setStopThreadFlag
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|super
operator|.
name|setBlockNewContainerRequests
argument_list|(
name|blockNewContainerRequests
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|;
block|}
DECL|class|TestNodeStatusUpdaterImpl2
class|class
name|TestNodeStatusUpdaterImpl2
extends|extends
name|MockNodeStatusUpdater
block|{
DECL|method|TestNodeStatusUpdaterImpl2 (Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker, NodeManagerMetrics metrics)
specifier|public
name|TestNodeStatusUpdaterImpl2
parameter_list|(
name|Context
name|context
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|NodeHealthCheckerService
name|healthChecker
parameter_list|,
name|NodeManagerMetrics
name|metrics
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rebootNodeStatusUpdater ()
specifier|protected
name|void
name|rebootNodeStatusUpdater
parameter_list|()
block|{
name|ConcurrentMap
argument_list|<
name|ContainerId
argument_list|,
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
argument_list|>
name|containers
init|=
name|getNMContext
argument_list|()
operator|.
name|getContainers
argument_list|()
decl_stmt|;
try|try
block|{
comment|// ensure that containers are empty before restart nodeStatusUpdater
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|rebootNodeStatusUpdater
argument_list|()
expr_stmt|;
comment|// After this point new containers are free to be launched, except
comment|// containers from previous RM
comment|// Wait here so as to sync with the main test thread.
name|syncBarrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
catch|catch
parameter_list|(
name|BrokenBarrierException
name|e
parameter_list|)
block|{         }
catch|catch
parameter_list|(
name|AssertionError
name|ae
parameter_list|)
block|{
name|ae
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertionFailedInThread
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|RejectedContainersLauncherThread
class|class
name|RejectedContainersLauncherThread
extends|extends
name|Thread
block|{
DECL|field|isStopped
name|boolean
name|isStopped
init|=
literal|false
decl_stmt|;
DECL|method|setStopThreadFlag (boolean isStopped)
specifier|public
name|void
name|setStopThreadFlag
parameter_list|(
name|boolean
name|isStopped
parameter_list|)
block|{
name|this
operator|.
name|isStopped
operator|=
name|isStopped
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
name|int
name|numContainers
init|=
literal|0
decl_stmt|;
name|int
name|numContainersRejected
init|=
literal|0
decl_stmt|;
name|ContainerLaunchContext
name|containerLaunchContext
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|!
name|isStopped
operator|&&
name|numContainers
operator|<
literal|10
condition|)
block|{
name|ContainerId
name|cId
init|=
name|TestNodeManagerShutdown
operator|.
name|createContainerId
argument_list|()
decl_stmt|;
name|StartContainerRequest
name|scRequest
init|=
name|StartContainerRequest
operator|.
name|newInstance
argument_list|(
name|containerLaunchContext
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StartContainerRequest
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|StartContainerRequest
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|scRequest
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|allRequests
init|=
name|StartContainersRequest
operator|.
name|newInstance
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"no. of containers to be launched: "
operator|+
name|numContainers
argument_list|)
expr_stmt|;
name|numContainers
operator|++
expr_stmt|;
try|try
block|{
name|getContainerManager
argument_list|()
operator|.
name|startContainers
argument_list|(
name|allRequests
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|numContainersRejected
operator|++
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Rejecting new containers as NodeManager has not"
operator|+
literal|" yet connected with ResourceManager"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NMNotYetReadyException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertionFailedInThread
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|// no. of containers to be launched should equal to no. of
comment|// containers rejected
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numContainers
argument_list|,
name|numContainersRejected
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|ae
parameter_list|)
block|{
name|assertionFailedInThread
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

