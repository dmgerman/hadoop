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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

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
name|FileWriter
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
name|Writer
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
name|FileUtil
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
name|Token
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
name|NodeManager
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
name|ResourceView
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
name|ContainerImpl
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
name|health
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
name|recovery
operator|.
name|NMStateStoreService
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
name|server
operator|.
name|utils
operator|.
name|BuilderUtils
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
name|Assert
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
DECL|class|TestNMWebServer
specifier|public
class|class
name|TestNMWebServer
block|{
DECL|field|testRootDir
specifier|private
specifier|static
specifier|final
name|File
name|testRootDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestNMWebServer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|testLogDir
specifier|private
specifier|static
name|File
name|testLogDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestNMWebServer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"LogDir"
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|testRootDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|testLogDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testRootDir
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testLogDir
argument_list|)
expr_stmt|;
block|}
DECL|method|createNodeHealthCheckerService ()
specifier|private
name|NodeHealthCheckerService
name|createNodeHealthCheckerService
parameter_list|()
block|{
name|LocalDirsHandlerService
name|dirsHandler
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
return|return
operator|new
name|NodeHealthCheckerService
argument_list|(
name|dirsHandler
argument_list|)
return|;
block|}
DECL|method|startNMWebAppServer (String webAddr)
specifier|private
name|int
name|startNMWebAppServer
parameter_list|(
name|String
name|webAddr
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Context
name|nmContext
init|=
operator|new
name|NodeManager
operator|.
name|NMContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ResourceView
name|resourceView
init|=
operator|new
name|ResourceView
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|getVmemAllocatedForContainers
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPmemAllocatedForContainers
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getVCoresAllocatedForContainers
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isVmemCheckEnabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPmemCheckEnabled
parameter_list|()
block|{
return|return
literal|true
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
name|testRootDir
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
name|testLogDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|NodeHealthCheckerService
name|healthChecker
init|=
name|createNodeHealthCheckerService
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_ADDRESS
argument_list|,
name|webAddr
argument_list|)
expr_stmt|;
name|WebServer
name|server
init|=
operator|new
name|WebServer
argument_list|(
name|nmContext
argument_list|,
name|resourceView
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
try|try
block|{
name|server
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|server
operator|.
name|getPort
argument_list|()
return|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|healthChecker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNMWebAppWithOutPort ()
specifier|public
name|void
name|testNMWebAppWithOutPort
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|port
init|=
name|startNMWebAppServer
argument_list|(
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|validatePortVal
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
DECL|method|validatePortVal (int portVal)
specifier|private
name|void
name|validatePortVal
parameter_list|(
name|int
name|portVal
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Port is not updated"
argument_list|,
name|portVal
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Port is default "
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PORT
argument_list|,
name|portVal
operator|!=
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PORT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNMWebAppWithEphemeralPort ()
specifier|public
name|void
name|testNMWebAppWithEphemeralPort
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|port
init|=
name|startNMWebAppServer
argument_list|(
literal|"0.0.0.0:0"
argument_list|)
decl_stmt|;
name|validatePortVal
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNMWebApp ()
specifier|public
name|void
name|testNMWebApp
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Context
name|nmContext
init|=
operator|new
name|NodeManager
operator|.
name|NMContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ResourceView
name|resourceView
init|=
operator|new
name|ResourceView
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|getVmemAllocatedForContainers
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPmemAllocatedForContainers
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getVCoresAllocatedForContainers
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isVmemCheckEnabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPmemCheckEnabled
parameter_list|()
block|{
return|return
literal|true
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
name|testRootDir
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
name|testLogDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|NodeHealthCheckerService
name|healthChecker
init|=
name|createNodeHealthCheckerService
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
name|WebServer
name|server
init|=
operator|new
name|WebServer
argument_list|(
name|nmContext
argument_list|,
name|resourceView
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
name|server
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Add an application and the corresponding containers
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Dispatcher
name|dispatcher
init|=
operator|new
name|AsyncDispatcher
argument_list|()
decl_stmt|;
name|String
name|user
init|=
literal|"nobody"
decl_stmt|;
name|long
name|clusterTimeStamp
init|=
literal|1234
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
name|recordFactory
argument_list|,
name|clusterTimeStamp
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Application
name|app
init|=
name|mock
argument_list|(
name|Application
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|app
operator|.
name|getAppId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|nmContext
operator|.
name|getApplications
argument_list|()
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|app
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|container1
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|recordFactory
argument_list|,
name|appId
argument_list|,
name|appAttemptId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ContainerId
name|container2
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|recordFactory
argument_list|,
name|appId
argument_list|,
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|NodeManagerMetrics
name|metrics
init|=
name|mock
argument_list|(
name|NodeManagerMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|NMStateStoreService
name|stateStore
init|=
operator|new
name|NMNullStateStoreService
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerId
name|containerId
range|:
operator|new
name|ContainerId
index|[]
block|{
name|container1
block|,
name|container2
block|}
control|)
block|{
comment|// TODO: Use builder utils
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
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Token
name|containerToken
init|=
name|BuilderUtils
operator|.
name|newContainerToken
argument_list|(
name|containerId
argument_list|,
literal|0
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|1234
argument_list|,
name|user
argument_list|,
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|currentTime
operator|+
literal|10000L
argument_list|,
literal|123
argument_list|,
literal|"password"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|currentTime
argument_list|)
decl_stmt|;
name|Context
name|context
init|=
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
decl_stmt|;
name|Container
name|container
init|=
operator|new
name|ContainerImpl
argument_list|(
name|conf
argument_list|,
name|dispatcher
argument_list|,
name|launchContext
argument_list|,
literal|null
argument_list|,
name|metrics
argument_list|,
name|BuilderUtils
operator|.
name|newContainerTokenIdentifier
argument_list|(
name|containerToken
argument_list|)
argument_list|,
name|context
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|ContainerState
name|getContainerState
parameter_list|()
block|{
return|return
name|ContainerState
operator|.
name|RUNNING
return|;
block|}
empty_stmt|;
block|}
decl_stmt|;
name|nmContext
operator|.
name|getContainers
argument_list|()
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|container
argument_list|)
expr_stmt|;
comment|//TODO: Gross hack. Fix in code.
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
name|nmContext
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
operator|.
name|getContainers
argument_list|()
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|writeContainerLogs
argument_list|(
name|nmContext
argument_list|,
name|containerId
argument_list|,
name|dirsHandler
argument_list|)
expr_stmt|;
block|}
comment|// TODO: Pull logs and test contents.
comment|//    Thread.sleep(1000000);
block|}
DECL|method|writeContainerLogs (Context nmContext, ContainerId containerId, LocalDirsHandlerService dirsHandler)
specifier|private
name|void
name|writeContainerLogs
parameter_list|(
name|Context
name|nmContext
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
comment|// ContainerLogDir should be created
name|File
name|containerLogDir
init|=
name|ContainerLogsUtils
operator|.
name|getContainerLogDirs
argument_list|(
name|containerId
argument_list|,
name|dirsHandler
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|containerLogDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|fileType
range|:
operator|new
name|String
index|[]
block|{
literal|"stdout"
block|,
literal|"stderr"
block|,
literal|"syslog"
block|}
control|)
block|{
name|Writer
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
operator|new
name|File
argument_list|(
name|containerLogDir
argument_list|,
name|fileType
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
operator|+
literal|"\n Hello "
operator|+
name|fileType
operator|+
literal|"!"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

