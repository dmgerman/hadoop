begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements. See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership. The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License. You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources
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
name|linux
operator|.
name|resources
package|;
end_package

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
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperation
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
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperationException
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
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperationExecutor
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|eq
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
name|doReturn
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
name|reset
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
name|spy
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
name|verify
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
name|verifyNoMoreInteractions
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

begin_class
DECL|class|TestTrafficControlBandwidthHandlerImpl
specifier|public
class|class
name|TestTrafficControlBandwidthHandlerImpl
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
name|TestTrafficControlBandwidthHandlerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ROOT_BANDWIDTH_MBIT
specifier|private
specifier|static
specifier|final
name|int
name|ROOT_BANDWIDTH_MBIT
init|=
literal|100
decl_stmt|;
DECL|field|YARN_BANDWIDTH_MBIT
specifier|private
specifier|static
specifier|final
name|int
name|YARN_BANDWIDTH_MBIT
init|=
literal|70
decl_stmt|;
DECL|field|TEST_CLASSID
specifier|private
specifier|static
specifier|final
name|int
name|TEST_CLASSID
init|=
literal|100
decl_stmt|;
DECL|field|TEST_CLASSID_STR
specifier|private
specifier|static
specifier|final
name|String
name|TEST_CLASSID_STR
init|=
literal|"42:100"
decl_stmt|;
DECL|field|TEST_CONTAINER_ID_STR
specifier|private
specifier|static
specifier|final
name|String
name|TEST_CONTAINER_ID_STR
init|=
literal|"container_01"
decl_stmt|;
DECL|field|TEST_TASKS_FILE
specifier|private
specifier|static
specifier|final
name|String
name|TEST_TASKS_FILE
init|=
literal|"testTasksFile"
decl_stmt|;
DECL|field|privilegedOperationExecutorMock
specifier|private
name|PrivilegedOperationExecutor
name|privilegedOperationExecutorMock
decl_stmt|;
DECL|field|cGroupsHandlerMock
specifier|private
name|CGroupsHandler
name|cGroupsHandlerMock
decl_stmt|;
DECL|field|trafficControllerMock
specifier|private
name|TrafficController
name|trafficControllerMock
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|tmpPath
specifier|private
name|String
name|tmpPath
decl_stmt|;
DECL|field|device
specifier|private
name|String
name|device
decl_stmt|;
DECL|field|containerIdMock
name|ContainerId
name|containerIdMock
decl_stmt|;
DECL|field|containerMock
name|Container
name|containerMock
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|privilegedOperationExecutorMock
operator|=
name|mock
argument_list|(
name|PrivilegedOperationExecutor
operator|.
name|class
argument_list|)
expr_stmt|;
name|cGroupsHandlerMock
operator|=
name|mock
argument_list|(
name|CGroupsHandler
operator|.
name|class
argument_list|)
expr_stmt|;
name|trafficControllerMock
operator|=
name|mock
argument_list|(
name|TrafficController
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|tmpPath
operator|=
operator|new
name|StringBuffer
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|device
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_NM_NETWORK_RESOURCE_INTERFACE
expr_stmt|;
name|containerIdMock
operator|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
expr_stmt|;
name|containerMock
operator|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|containerIdMock
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|TEST_CONTAINER_ID_STR
argument_list|)
expr_stmt|;
comment|//mock returning a mock - an angel died somewhere.
name|when
argument_list|(
name|containerMock
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerIdMock
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_RESOURCE_OUTBOUND_BANDWIDTH_MBIT
argument_list|,
name|ROOT_BANDWIDTH_MBIT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_RESOURCE_OUTBOUND_BANDWIDTH_YARN_MBIT
argument_list|,
name|YARN_BANDWIDTH_MBIT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|,
name|tmpPath
argument_list|)
expr_stmt|;
comment|//In these tests, we'll only use TrafficController with recovery disabled
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RECOVERY_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBootstrap ()
specifier|public
name|void
name|testBootstrap
parameter_list|()
block|{
name|TrafficControlBandwidthHandlerImpl
name|handlerImpl
init|=
operator|new
name|TrafficControlBandwidthHandlerImpl
argument_list|(
name|privilegedOperationExecutorMock
argument_list|,
name|cGroupsHandlerMock
argument_list|,
name|trafficControllerMock
argument_list|)
decl_stmt|;
try|try
block|{
name|handlerImpl
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|cGroupsHandlerMock
argument_list|)
operator|.
name|initializeCGroupController
argument_list|(
name|eq
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|NET_CLS
argument_list|)
argument_list|)
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|cGroupsHandlerMock
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|trafficControllerMock
argument_list|)
operator|.
name|bootstrap
argument_list|(
name|eq
argument_list|(
name|device
argument_list|)
argument_list|,
name|eq
argument_list|(
name|ROOT_BANDWIDTH_MBIT
argument_list|)
argument_list|,
name|eq
argument_list|(
name|YARN_BANDWIDTH_MBIT
argument_list|)
argument_list|)
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|trafficControllerMock
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Caught unexpected ResourceHandlerException!"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLifeCycle ()
specifier|public
name|void
name|testLifeCycle
parameter_list|()
block|{
name|TrafficController
name|trafficControllerSpy
init|=
name|spy
argument_list|(
operator|new
name|TrafficController
argument_list|(
name|conf
argument_list|,
name|privilegedOperationExecutorMock
argument_list|)
argument_list|)
decl_stmt|;
name|TrafficControlBandwidthHandlerImpl
name|handlerImpl
init|=
operator|new
name|TrafficControlBandwidthHandlerImpl
argument_list|(
name|privilegedOperationExecutorMock
argument_list|,
name|cGroupsHandlerMock
argument_list|,
name|trafficControllerSpy
argument_list|)
decl_stmt|;
try|try
block|{
name|handlerImpl
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testPreStart
argument_list|(
name|trafficControllerSpy
argument_list|,
name|handlerImpl
argument_list|)
expr_stmt|;
name|testPostComplete
argument_list|(
name|trafficControllerSpy
argument_list|,
name|handlerImpl
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Caught unexpected ResourceHandlerException!"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPreStart (TrafficController trafficControllerSpy, TrafficControlBandwidthHandlerImpl handlerImpl)
specifier|private
name|void
name|testPreStart
parameter_list|(
name|TrafficController
name|trafficControllerSpy
parameter_list|,
name|TrafficControlBandwidthHandlerImpl
name|handlerImpl
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
comment|//This is not the cleanest of solutions - but since we are testing the
comment|//preStart/postComplete lifecycle, we don't have a different way of
comment|//handling this - we don't keep track of the number of invocations by
comment|//a class we are not testing here (TrafficController)
comment|//So, we'll reset this mock. This is not a problem with other mocks.
name|reset
argument_list|(
name|privilegedOperationExecutorMock
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|TEST_CLASSID
argument_list|)
operator|.
name|when
argument_list|(
name|trafficControllerSpy
argument_list|)
operator|.
name|getNextClassId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|TEST_CLASSID_STR
argument_list|)
operator|.
name|when
argument_list|(
name|trafficControllerSpy
argument_list|)
operator|.
name|getStringForNetClsClassId
argument_list|(
name|TEST_CLASSID
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|cGroupsHandlerMock
operator|.
name|getPathForCGroupTasks
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|NET_CLS
argument_list|,
name|TEST_CONTAINER_ID_STR
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|TEST_TASKS_FILE
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|ops
init|=
name|handlerImpl
operator|.
name|preStart
argument_list|(
name|containerMock
argument_list|)
decl_stmt|;
comment|//Ensure that cgroups is created and updated as expected
name|verify
argument_list|(
name|cGroupsHandlerMock
argument_list|)
operator|.
name|createCGroup
argument_list|(
name|eq
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|NET_CLS
argument_list|)
argument_list|,
name|eq
argument_list|(
name|TEST_CONTAINER_ID_STR
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|cGroupsHandlerMock
argument_list|)
operator|.
name|updateCGroupParam
argument_list|(
name|eq
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|NET_CLS
argument_list|)
argument_list|,
name|eq
argument_list|(
name|TEST_CONTAINER_ID_STR
argument_list|)
argument_list|,
name|eq
argument_list|(
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_CLASSID
argument_list|)
argument_list|,
name|eq
argument_list|(
name|TEST_CLASSID_STR
argument_list|)
argument_list|)
expr_stmt|;
comment|//Now check the privileged operations being returned
comment|//We expect two operations - one for adding pid to tasks file and another
comment|//for a tc modify operation
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ops
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//Verify that the add pid op is correct
name|PrivilegedOperation
name|addPidOp
init|=
name|ops
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|expectedAddPidOpArg
init|=
name|PrivilegedOperation
operator|.
name|CGROUP_ARG_PREFIX
operator|+
name|TEST_TASKS_FILE
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|addPidOpArgs
init|=
name|addPidOp
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|ADD_PID_TO_CGROUP
argument_list|,
name|addPidOp
operator|.
name|getOperationType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|addPidOpArgs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedAddPidOpArg
argument_list|,
name|addPidOpArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|//Verify that that tc modify op is correct
name|PrivilegedOperation
name|tcModifyOp
init|=
name|ops
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tcModifyOpArgs
init|=
name|tcModifyOp
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|,
name|tcModifyOp
operator|.
name|getOperationType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tcModifyOpArgs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//verify that the tc command file exists
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|tcModifyOpArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPostComplete (TrafficController trafficControllerSpy, TrafficControlBandwidthHandlerImpl handlerImpl)
specifier|private
name|void
name|testPostComplete
parameter_list|(
name|TrafficController
name|trafficControllerSpy
parameter_list|,
name|TrafficControlBandwidthHandlerImpl
name|handlerImpl
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
comment|//This is not the cleanest of solutions - but since we are testing the
comment|//preStart/postComplete lifecycle, we don't have a different way of
comment|//handling this - we don't keep track of the number of invocations by
comment|//a class we are not testing here (TrafficController)
comment|//So, we'll reset this mock. This is not a problem with other mocks.
name|reset
argument_list|(
name|privilegedOperationExecutorMock
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|ops
init|=
name|handlerImpl
operator|.
name|postComplete
argument_list|(
name|containerIdMock
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|cGroupsHandlerMock
argument_list|)
operator|.
name|deleteCGroup
argument_list|(
name|eq
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|NET_CLS
argument_list|)
argument_list|,
name|eq
argument_list|(
name|TEST_CONTAINER_ID_STR
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
comment|//capture privileged op argument and ensure it is correct
name|ArgumentCaptor
argument_list|<
name|PrivilegedOperation
argument_list|>
name|opCaptor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|PrivilegedOperation
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|privilegedOperationExecutorMock
argument_list|)
operator|.
name|executePrivilegedOperation
argument_list|(
name|opCaptor
operator|.
name|capture
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
name|opCaptor
operator|.
name|getValue
argument_list|()
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|,
name|opCaptor
operator|.
name|getValue
argument_list|()
operator|.
name|getOperationType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|args
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//ensure that tc command file exists
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|trafficControllerSpy
argument_list|)
operator|.
name|releaseClassId
argument_list|(
name|TEST_CLASSID
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PrivilegedOperationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Unexpected PrivilegedOperationException from mock!"
argument_list|)
expr_stmt|;
block|}
comment|//We don't expect any operations to be returned here
name|Assert
operator|.
name|assertNull
argument_list|(
name|ops
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|tmpPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

