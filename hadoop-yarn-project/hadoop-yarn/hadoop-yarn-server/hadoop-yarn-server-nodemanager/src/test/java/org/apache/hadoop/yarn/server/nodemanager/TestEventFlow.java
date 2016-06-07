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
name|net
operator|.
name|ServerSocketUtil
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
name|protocolrecords
operator|.
name|StopContainersRequest
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
name|ApplicationAttemptId
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
name|containermanager
operator|.
name|BaseContainerManagerTest
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
name|TestContainerManager
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
name|recovery
operator|.
name|NMNullStateStoreService
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
name|nodemanager
operator|.
name|security
operator|.
name|NMTokenSecretManagerInNM
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
DECL|class|TestEventFlow
specifier|public
class|class
name|TestEventFlow
block|{
DECL|field|recordFactory
specifier|private
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
DECL|field|localDir
specifier|private
specifier|static
name|File
name|localDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestEventFlow
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-localDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|localLogDir
specifier|private
specifier|static
name|File
name|localLogDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestEventFlow
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-localLogDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|remoteLogDir
specifier|private
specifier|static
name|File
name|remoteLogDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestEventFlow
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-remoteLogDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|SIMULATED_RM_IDENTIFIER
specifier|private
specifier|static
specifier|final
name|long
name|SIMULATED_RM_IDENTIFIER
init|=
literal|1234
decl_stmt|;
annotation|@
name|Test
DECL|method|testSuccessfulContainerLaunch ()
specifier|public
name|void
name|testSuccessfulContainerLaunch
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|YarnException
block|{
name|FileContext
name|localFS
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
decl_stmt|;
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
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
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
argument_list|,
operator|new
name|NMTokenSecretManagerInNM
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|NMNullStateStoreService
argument_list|()
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|getHttpPort
parameter_list|()
block|{
return|return
literal|1234
return|;
block|}
block|}
decl_stmt|;
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCALIZER_ADDRESS
argument_list|,
literal|"0.0.0.0:"
operator|+
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|8040
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|ContainerExecutor
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
name|DeletionService
name|del
init|=
operator|new
name|DeletionService
argument_list|(
name|exec
argument_list|)
decl_stmt|;
name|Dispatcher
name|dispatcher
init|=
operator|new
name|AsyncDispatcher
argument_list|()
decl_stmt|;
name|LocalDirsHandlerService
name|dirsHandler
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
name|NodeHealthCheckerService
name|healthChecker
init|=
operator|new
name|NodeHealthCheckerService
argument_list|(
name|NodeManager
operator|.
name|getNodeHealthScriptRunner
argument_list|(
name|conf
argument_list|)
argument_list|,
name|dirsHandler
argument_list|)
decl_stmt|;
name|healthChecker
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|NodeManagerMetrics
name|metrics
init|=
name|NodeManagerMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
name|NodeStatusUpdater
name|nodeStatusUpdater
init|=
operator|new
name|NodeStatusUpdaterImpl
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
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
name|stopRMProxy
parameter_list|()
block|{
return|return;
block|}
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
annotation|@
name|Override
specifier|public
name|long
name|getRMIdentifier
parameter_list|()
block|{
return|return
name|SIMULATED_RM_IDENTIFIER
return|;
block|}
block|}
decl_stmt|;
name|DummyContainerManager
name|containerManager
init|=
operator|new
name|DummyContainerManager
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
name|dirsHandler
argument_list|)
decl_stmt|;
name|nodeStatusUpdater
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
operator|(
operator|(
name|NMContext
operator|)
name|context
operator|)
operator|.
name|setContainerManager
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
name|nodeStatusUpdater
operator|.
name|start
argument_list|()
expr_stmt|;
operator|(
operator|(
name|NMContext
operator|)
name|context
operator|)
operator|.
name|setNodeStatusUpdater
argument_list|(
name|nodeStatusUpdater
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|ContainerLaunchContext
name|launchContext
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
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ContainerId
name|cID
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|applicationAttemptId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|user
init|=
literal|"testing"
decl_stmt|;
name|StartContainerRequest
name|scRequest
init|=
name|StartContainerRequest
operator|.
name|newInstance
argument_list|(
name|launchContext
argument_list|,
name|TestContainerManager
operator|.
name|createContainerToken
argument_list|(
name|cID
argument_list|,
name|SIMULATED_RM_IDENTIFIER
argument_list|,
name|context
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|user
argument_list|,
name|context
operator|.
name|getContainerTokenSecretManager
argument_list|()
argument_list|)
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
name|containerManager
operator|.
name|startContainers
argument_list|(
name|allRequests
argument_list|)
expr_stmt|;
name|BaseContainerManagerTest
operator|.
name|waitForContainerState
argument_list|(
name|containerManager
argument_list|,
name|cID
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|containerIds
operator|.
name|add
argument_list|(
name|cID
argument_list|)
expr_stmt|;
name|StopContainersRequest
name|stopRequest
init|=
name|StopContainersRequest
operator|.
name|newInstance
argument_list|(
name|containerIds
argument_list|)
decl_stmt|;
name|containerManager
operator|.
name|stopContainers
argument_list|(
name|stopRequest
argument_list|)
expr_stmt|;
name|BaseContainerManagerTest
operator|.
name|waitForContainerState
argument_list|(
name|containerManager
argument_list|,
name|cID
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

