begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.launcher
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|launcher
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
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|ThreadPoolExecutor
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
name|AtomicInteger
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
name|ipc
operator|.
name|Server
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobState
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptState
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskState
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskType
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|MRApp
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Task
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|TaskAttempt
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRBuilderUtils
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
name|NetUtils
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
name|protocolrecords
operator|.
name|GetContainerStatusResponse
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
name|StartContainerResponse
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
name|protocolrecords
operator|.
name|StopContainerResponse
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
name|api
operator|.
name|records
operator|.
name|ContainerToken
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
name|factory
operator|.
name|providers
operator|.
name|YarnRemoteExceptionFactoryProvider
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
name|ipc
operator|.
name|HadoopYarnProtoRPC
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
name|ipc
operator|.
name|YarnRPC
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
name|BuilderUtils
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
DECL|class|TestContainerLauncher
specifier|public
class|class
name|TestContainerLauncher
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
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|server
name|Server
name|server
decl_stmt|;
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestContainerLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testPoolSize ()
specifier|public
name|void
name|testPoolSize
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ApplicationId
name|appId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|12345
argument_list|,
literal|67
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|MRBuilderUtils
operator|.
name|newJobId
argument_list|(
name|appId
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|TaskId
name|taskId
init|=
name|MRBuilderUtils
operator|.
name|newTaskId
argument_list|(
name|jobId
argument_list|,
literal|9
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|)
decl_stmt|;
name|AppContext
name|context
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|CustomContainerLauncher
name|containerLauncher
init|=
operator|new
name|CustomContainerLauncher
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|containerLauncher
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|containerLauncher
operator|.
name|start
argument_list|()
expr_stmt|;
name|ThreadPoolExecutor
name|threadPool
init|=
name|containerLauncher
operator|.
name|getThreadPool
argument_list|()
decl_stmt|;
comment|// No events yet
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|threadPool
operator|.
name|getPoolSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerLauncherImpl
operator|.
name|INITIAL_POOL_SIZE
argument_list|,
name|threadPool
operator|.
name|getCorePoolSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|containerLauncher
operator|.
name|foundErrors
argument_list|)
expr_stmt|;
name|containerLauncher
operator|.
name|expectedCorePoolSize
operator|=
name|ContainerLauncherImpl
operator|.
name|INITIAL_POOL_SIZE
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|ContainerId
name|containerId
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|TaskAttemptId
name|taskAttemptId
init|=
name|MRBuilderUtils
operator|.
name|newTaskAttemptId
argument_list|(
name|taskId
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|containerLauncher
operator|.
name|handle
argument_list|(
operator|new
name|ContainerLauncherEvent
argument_list|(
name|taskAttemptId
argument_list|,
name|containerId
argument_list|,
literal|"host"
operator|+
name|i
operator|+
literal|":1234"
argument_list|,
literal|null
argument_list|,
name|ContainerLauncher
operator|.
name|EventType
operator|.
name|CONTAINER_REMOTE_LAUNCH
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|waitForEvents
argument_list|(
name|containerLauncher
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|threadPool
operator|.
name|getPoolSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|containerLauncher
operator|.
name|foundErrors
argument_list|)
expr_stmt|;
comment|// Same set of hosts, so no change
name|containerLauncher
operator|.
name|finishEventHandling
operator|=
literal|true
expr_stmt|;
name|int
name|timeOut
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|containerLauncher
operator|.
name|numEventsProcessed
operator|.
name|get
argument_list|()
operator|<
literal|10
operator|&&
name|timeOut
operator|++
operator|<
literal|200
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for number of events processed to become "
operator|+
literal|10
operator|+
literal|". It is now "
operator|+
name|containerLauncher
operator|.
name|numEventsProcessed
operator|.
name|get
argument_list|()
operator|+
literal|". Timeout is "
operator|+
name|timeOut
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
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|containerLauncher
operator|.
name|numEventsProcessed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|containerLauncher
operator|.
name|finishEventHandling
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|ContainerId
name|containerId
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
name|i
operator|+
literal|10
argument_list|)
decl_stmt|;
name|TaskAttemptId
name|taskAttemptId
init|=
name|MRBuilderUtils
operator|.
name|newTaskAttemptId
argument_list|(
name|taskId
argument_list|,
name|i
operator|+
literal|10
argument_list|)
decl_stmt|;
name|containerLauncher
operator|.
name|handle
argument_list|(
operator|new
name|ContainerLauncherEvent
argument_list|(
name|taskAttemptId
argument_list|,
name|containerId
argument_list|,
literal|"host"
operator|+
name|i
operator|+
literal|":1234"
argument_list|,
literal|null
argument_list|,
name|ContainerLauncher
operator|.
name|EventType
operator|.
name|CONTAINER_REMOTE_LAUNCH
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|waitForEvents
argument_list|(
name|containerLauncher
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|threadPool
operator|.
name|getPoolSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|containerLauncher
operator|.
name|foundErrors
argument_list|)
expr_stmt|;
comment|// Different hosts, there should be an increase in core-thread-pool size to
comment|// 21(11hosts+10buffer)
comment|// Core pool size should be 21 but the live pool size should be only 11.
name|containerLauncher
operator|.
name|expectedCorePoolSize
operator|=
literal|11
operator|+
name|ContainerLauncherImpl
operator|.
name|INITIAL_POOL_SIZE
expr_stmt|;
name|containerLauncher
operator|.
name|finishEventHandling
operator|=
literal|false
expr_stmt|;
name|ContainerId
name|containerId
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|21
argument_list|)
decl_stmt|;
name|TaskAttemptId
name|taskAttemptId
init|=
name|MRBuilderUtils
operator|.
name|newTaskAttemptId
argument_list|(
name|taskId
argument_list|,
literal|21
argument_list|)
decl_stmt|;
name|containerLauncher
operator|.
name|handle
argument_list|(
operator|new
name|ContainerLauncherEvent
argument_list|(
name|taskAttemptId
argument_list|,
name|containerId
argument_list|,
literal|"host11:1234"
argument_list|,
literal|null
argument_list|,
name|ContainerLauncher
operator|.
name|EventType
operator|.
name|CONTAINER_REMOTE_LAUNCH
argument_list|)
argument_list|)
expr_stmt|;
name|waitForEvents
argument_list|(
name|containerLauncher
argument_list|,
literal|21
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|threadPool
operator|.
name|getPoolSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|containerLauncher
operator|.
name|foundErrors
argument_list|)
expr_stmt|;
name|containerLauncher
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPoolLimits ()
specifier|public
name|void
name|testPoolLimits
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ApplicationId
name|appId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|12345
argument_list|,
literal|67
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|MRBuilderUtils
operator|.
name|newJobId
argument_list|(
name|appId
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|TaskId
name|taskId
init|=
name|MRBuilderUtils
operator|.
name|newTaskId
argument_list|(
name|jobId
argument_list|,
literal|9
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|)
decl_stmt|;
name|TaskAttemptId
name|taskAttemptId
init|=
name|MRBuilderUtils
operator|.
name|newTaskAttemptId
argument_list|(
name|taskId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|AppContext
name|context
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|CustomContainerLauncher
name|containerLauncher
init|=
operator|new
name|CustomContainerLauncher
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_CONTAINERLAUNCHER_THREAD_COUNT_LIMIT
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|containerLauncher
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|containerLauncher
operator|.
name|start
argument_list|()
expr_stmt|;
name|ThreadPoolExecutor
name|threadPool
init|=
name|containerLauncher
operator|.
name|getThreadPool
argument_list|()
decl_stmt|;
comment|// 10 different hosts
name|containerLauncher
operator|.
name|expectedCorePoolSize
operator|=
name|ContainerLauncherImpl
operator|.
name|INITIAL_POOL_SIZE
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|containerLauncher
operator|.
name|handle
argument_list|(
operator|new
name|ContainerLauncherEvent
argument_list|(
name|taskAttemptId
argument_list|,
name|containerId
argument_list|,
literal|"host"
operator|+
name|i
operator|+
literal|":1234"
argument_list|,
literal|null
argument_list|,
name|ContainerLauncher
operator|.
name|EventType
operator|.
name|CONTAINER_REMOTE_LAUNCH
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|waitForEvents
argument_list|(
name|containerLauncher
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|threadPool
operator|.
name|getPoolSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|containerLauncher
operator|.
name|foundErrors
argument_list|)
expr_stmt|;
comment|// 4 more different hosts, but thread pool size should be capped at 12
name|containerLauncher
operator|.
name|expectedCorePoolSize
operator|=
literal|12
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|containerLauncher
operator|.
name|handle
argument_list|(
operator|new
name|ContainerLauncherEvent
argument_list|(
name|taskAttemptId
argument_list|,
name|containerId
argument_list|,
literal|"host1"
operator|+
name|i
operator|+
literal|":1234"
argument_list|,
literal|null
argument_list|,
name|ContainerLauncher
operator|.
name|EventType
operator|.
name|CONTAINER_REMOTE_LAUNCH
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|waitForEvents
argument_list|(
name|containerLauncher
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|threadPool
operator|.
name|getPoolSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|containerLauncher
operator|.
name|foundErrors
argument_list|)
expr_stmt|;
comment|// Make some threads ideal so that remaining events are also done.
name|containerLauncher
operator|.
name|finishEventHandling
operator|=
literal|true
expr_stmt|;
name|waitForEvents
argument_list|(
name|containerLauncher
argument_list|,
literal|14
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|threadPool
operator|.
name|getPoolSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|containerLauncher
operator|.
name|foundErrors
argument_list|)
expr_stmt|;
name|containerLauncher
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|waitForEvents (CustomContainerLauncher containerLauncher, int expectedNumEvents)
specifier|private
name|void
name|waitForEvents
parameter_list|(
name|CustomContainerLauncher
name|containerLauncher
parameter_list|,
name|int
name|expectedNumEvents
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|timeOut
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|containerLauncher
operator|.
name|numEventsProcessing
operator|.
name|get
argument_list|()
operator|<
name|expectedNumEvents
operator|&&
name|timeOut
operator|++
operator|<
literal|20
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for number of events to become "
operator|+
name|expectedNumEvents
operator|+
literal|". It is now "
operator|+
name|containerLauncher
operator|.
name|numEventsProcessing
operator|.
name|get
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
name|assertEquals
argument_list|(
name|expectedNumEvents
argument_list|,
name|containerLauncher
operator|.
name|numEventsProcessing
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSlowNM ()
specifier|public
name|void
name|testSlowNM
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|()
expr_stmt|;
block|}
DECL|method|test ()
specifier|private
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|int
name|maxAttempts
init|=
literal|1
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
name|maxAttempts
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_ENABLE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// set timeout low for the test
name|conf
operator|.
name|setInt
argument_list|(
literal|"yarn.rpc.nm-command-timeout"
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_RPC_IMPL
argument_list|,
name|HadoopYarnProtoRPC
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|bindAddr
init|=
literal|"localhost:0"
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|bindAddr
argument_list|)
decl_stmt|;
name|server
operator|=
name|rpc
operator|.
name|getServer
argument_list|(
name|ContainerManager
operator|.
name|class
argument_list|,
operator|new
name|DummyContainerManager
argument_list|()
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|MRApp
name|app
init|=
operator|new
name|MRAppWithSlowNM
argument_list|()
decl_stmt|;
try|try
block|{
name|Job
name|job
init|=
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|tasks
init|=
name|job
operator|.
name|getTasks
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num tasks is not correct"
argument_list|,
literal|1
argument_list|,
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
name|tasks
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|task
argument_list|,
name|TaskState
operator|.
name|SCHEDULED
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|TaskAttempt
argument_list|>
name|attempts
init|=
name|tasks
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getAttempts
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num attempts is not correct"
argument_list|,
name|maxAttempts
argument_list|,
name|attempts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TaskAttempt
name|attempt
init|=
name|attempts
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|attempt
argument_list|,
name|TaskAttemptState
operator|.
name|ASSIGNED
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|FAILED
argument_list|)
expr_stmt|;
name|String
name|diagnostics
init|=
name|attempt
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"attempt.getDiagnostics: "
operator|+
name|diagnostics
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|diagnostics
operator|.
name|contains
argument_list|(
literal|"Container launch failed for "
operator|+
literal|"container_0_0000_01_000000 : "
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|diagnostics
operator|.
name|contains
argument_list|(
literal|"java.net.SocketTimeoutException: 3000 millis timeout while waiting for channel"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|CustomContainerLauncher
specifier|private
specifier|final
class|class
name|CustomContainerLauncher
extends|extends
name|ContainerLauncherImpl
block|{
DECL|field|expectedCorePoolSize
specifier|private
specifier|volatile
name|int
name|expectedCorePoolSize
init|=
literal|0
decl_stmt|;
DECL|field|numEventsProcessing
specifier|private
name|AtomicInteger
name|numEventsProcessing
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|numEventsProcessed
specifier|private
name|AtomicInteger
name|numEventsProcessed
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|foundErrors
specifier|private
specifier|volatile
name|String
name|foundErrors
init|=
literal|null
decl_stmt|;
DECL|field|finishEventHandling
specifier|private
specifier|volatile
name|boolean
name|finishEventHandling
decl_stmt|;
DECL|method|CustomContainerLauncher (AppContext context)
specifier|private
name|CustomContainerLauncher
parameter_list|(
name|AppContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|getThreadPool ()
specifier|public
name|ThreadPoolExecutor
name|getThreadPool
parameter_list|()
block|{
return|return
name|super
operator|.
name|launcherPool
return|;
block|}
DECL|class|CustomEventProcessor
specifier|private
specifier|final
class|class
name|CustomEventProcessor
extends|extends
name|ContainerLauncherImpl
operator|.
name|EventProcessor
block|{
DECL|field|event
specifier|private
specifier|final
name|ContainerLauncherEvent
name|event
decl_stmt|;
DECL|method|CustomEventProcessor (ContainerLauncherEvent event)
specifier|private
name|CustomEventProcessor
parameter_list|(
name|ContainerLauncherEvent
name|event
parameter_list|)
block|{
name|super
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|this
operator|.
name|event
operator|=
name|event
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
comment|// do nothing substantial
name|LOG
operator|.
name|info
argument_list|(
literal|"Processing the event "
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|numEventsProcessing
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// Stall
while|while
condition|(
operator|!
name|finishEventHandling
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
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
empty_stmt|;
block|}
block|}
block|}
name|numEventsProcessed
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createEventProcessor ( final ContainerLauncherEvent event)
specifier|protected
name|ContainerLauncherImpl
operator|.
name|EventProcessor
name|createEventProcessor
parameter_list|(
specifier|final
name|ContainerLauncherEvent
name|event
parameter_list|)
block|{
comment|// At this point of time, the EventProcessor is being created and so no
comment|// additional threads would have been created.
comment|// Core-pool-size should have increased by now.
if|if
condition|(
name|expectedCorePoolSize
operator|!=
name|launcherPool
operator|.
name|getCorePoolSize
argument_list|()
condition|)
block|{
name|foundErrors
operator|=
literal|"Expected "
operator|+
name|expectedCorePoolSize
operator|+
literal|" but found "
operator|+
name|launcherPool
operator|.
name|getCorePoolSize
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|CustomEventProcessor
argument_list|(
name|event
argument_list|)
return|;
block|}
block|}
DECL|class|MRAppWithSlowNM
specifier|private
class|class
name|MRAppWithSlowNM
extends|extends
name|MRApp
block|{
DECL|method|MRAppWithSlowNM ()
specifier|public
name|MRAppWithSlowNM
parameter_list|()
block|{
name|super
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|"TestContainerLauncher"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createContainerLauncher (AppContext context)
specifier|protected
name|ContainerLauncher
name|createContainerLauncher
parameter_list|(
name|AppContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|ContainerLauncherImpl
argument_list|(
name|context
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ContainerManager
name|getCMProxy
parameter_list|(
name|ContainerId
name|containerID
parameter_list|,
name|String
name|containerManagerBindAddr
parameter_list|,
name|ContainerToken
name|containerToken
parameter_list|)
throws|throws
name|IOException
block|{
comment|// make proxy connect to our local containerManager server
name|ContainerManager
name|proxy
init|=
operator|(
name|ContainerManager
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ContainerManager
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
block|}
return|;
block|}
empty_stmt|;
block|}
DECL|class|DummyContainerManager
specifier|public
class|class
name|DummyContainerManager
implements|implements
name|ContainerManager
block|{
DECL|field|status
specifier|private
name|ContainerStatus
name|status
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|getContainerStatus ( GetContainerStatusRequest request)
specifier|public
name|GetContainerStatusResponse
name|getContainerStatus
parameter_list|(
name|GetContainerStatusRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|GetContainerStatusResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetContainerStatusResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|startContainer (StartContainerRequest request)
specifier|public
name|StartContainerResponse
name|startContainer
parameter_list|(
name|StartContainerRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ContainerLaunchContext
name|container
init|=
name|request
operator|.
name|getContainerLaunchContext
argument_list|()
decl_stmt|;
name|StartContainerResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|status
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
comment|// make the thread sleep to look like its not going to respond
name|Thread
operator|.
name|sleep
argument_list|(
literal|15000
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
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|status
operator|.
name|setState
argument_list|(
name|ContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|status
operator|.
name|setContainerId
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|.
name|setExitStatus
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|stopContainer (StopContainerRequest request)
specifier|public
name|StopContainerResponse
name|stopContainer
parameter_list|(
name|StopContainerRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|Exception
name|e
init|=
operator|new
name|Exception
argument_list|(
literal|"Dummy function"
argument_list|,
operator|new
name|Exception
argument_list|(
literal|"Dummy function cause"
argument_list|)
argument_list|)
decl_stmt|;
throw|throw
name|YarnRemoteExceptionFactoryProvider
operator|.
name|getYarnRemoteExceptionFactory
argument_list|(
literal|null
argument_list|)
operator|.
name|createYarnRemoteException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

