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
name|StopContainerRequest
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
name|api
operator|.
name|records
operator|.
name|Resource
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
argument_list|)
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
name|NodeHealthCheckerService
name|healthChecker
init|=
operator|new
name|NodeHealthCheckerService
argument_list|()
decl_stmt|;
name|healthChecker
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirsHandler
init|=
name|healthChecker
operator|.
name|getDiskHandler
argument_list|()
decl_stmt|;
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
name|startStatusUpdater
parameter_list|()
block|{
return|return;
comment|// Don't start any updating thread.
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
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|,
name|dirsHandler
argument_list|)
decl_stmt|;
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
name|ContainerId
name|cID
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|applicationId
operator|.
name|setClusterTimestamp
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|applicationId
operator|.
name|setId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|applicationAttemptId
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|applicationAttemptId
operator|.
name|setAttemptId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cID
operator|.
name|setApplicationAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|launchContext
operator|.
name|setContainerId
argument_list|(
name|cID
argument_list|)
expr_stmt|;
name|launchContext
operator|.
name|setUser
argument_list|(
literal|"testing"
argument_list|)
expr_stmt|;
name|launchContext
operator|.
name|setResource
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|StartContainerRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setContainerLaunchContext
argument_list|(
name|launchContext
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|startContainer
argument_list|(
name|request
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
name|StopContainerRequest
name|stopRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StopContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|stopRequest
operator|.
name|setContainerId
argument_list|(
name|cID
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|stopContainer
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

