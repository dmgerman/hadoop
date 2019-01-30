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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
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
name|any
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
name|times
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
name|when
import|;
end_import

begin_class
DECL|class|TestTrafficController
specifier|public
class|class
name|TestTrafficController
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
name|TestTrafficController
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
DECL|field|CONTAINER_BANDWIDTH_MBIT
specifier|private
specifier|static
specifier|final
name|int
name|CONTAINER_BANDWIDTH_MBIT
init|=
literal|10
decl_stmt|;
comment|//These constants are closely tied to the implementation of TrafficController
comment|//and will have to be modified in tandem with any related TrafficController
comment|//changes.
DECL|field|DEVICE
specifier|private
specifier|static
specifier|final
name|String
name|DEVICE
init|=
literal|"eth0"
decl_stmt|;
DECL|field|WIPE_STATE_CMD
specifier|private
specifier|static
specifier|final
name|String
name|WIPE_STATE_CMD
init|=
literal|"qdisc del dev eth0 parent root"
decl_stmt|;
DECL|field|ADD_ROOT_QDISC_CMD
specifier|private
specifier|static
specifier|final
name|String
name|ADD_ROOT_QDISC_CMD
init|=
literal|"qdisc add dev eth0 root handle 42: htb default 2"
decl_stmt|;
DECL|field|ADD_CGROUP_FILTER_CMD
specifier|private
specifier|static
specifier|final
name|String
name|ADD_CGROUP_FILTER_CMD
init|=
literal|"filter add dev eth0 parent 42: protocol ip prio 10 handle 1: cgroup"
decl_stmt|;
DECL|field|ADD_ROOT_CLASS_CMD
specifier|private
specifier|static
specifier|final
name|String
name|ADD_ROOT_CLASS_CMD
init|=
literal|"class add dev eth0 parent 42:0 classid 42:1 htb rate 100mbit ceil 100mbit"
decl_stmt|;
DECL|field|ADD_DEFAULT_CLASS_CMD
specifier|private
specifier|static
specifier|final
name|String
name|ADD_DEFAULT_CLASS_CMD
init|=
literal|"class add dev eth0 parent 42:1 classid 42:2 htb rate 30mbit ceil 100mbit"
decl_stmt|;
DECL|field|ADD_YARN_CLASS_CMD
specifier|private
specifier|static
specifier|final
name|String
name|ADD_YARN_CLASS_CMD
init|=
literal|"class add dev eth0 parent 42:1 classid 42:3 htb rate 70mbit ceil 70mbit"
decl_stmt|;
DECL|field|DEFAULT_TC_STATE_EXAMPLE
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_TC_STATE_EXAMPLE
init|=
literal|"qdisc pfifo_fast 0: root refcnt 2 bands 3 priomap  1 2 2 2 1 2 0 0 1 1 1 1 1 1 1 1"
decl_stmt|;
DECL|field|READ_QDISC_CMD
specifier|private
specifier|static
specifier|final
name|String
name|READ_QDISC_CMD
init|=
literal|"qdisc show dev eth0"
decl_stmt|;
DECL|field|READ_FILTER_CMD
specifier|private
specifier|static
specifier|final
name|String
name|READ_FILTER_CMD
init|=
literal|"filter show dev eth0"
decl_stmt|;
DECL|field|READ_CLASS_CMD
specifier|private
specifier|static
specifier|final
name|String
name|READ_CLASS_CMD
init|=
literal|"class show dev eth0"
decl_stmt|;
DECL|field|MIN_CONTAINER_CLASS_ID
specifier|private
specifier|static
specifier|final
name|int
name|MIN_CONTAINER_CLASS_ID
init|=
literal|4
decl_stmt|;
DECL|field|FORMAT_CONTAINER_CLASS_STR
specifier|private
specifier|static
specifier|final
name|String
name|FORMAT_CONTAINER_CLASS_STR
init|=
literal|"0x0042%04d"
decl_stmt|;
DECL|field|FORMAT_ADD_CONTAINER_CLASS_TO_DEVICE
specifier|private
specifier|static
specifier|final
name|String
name|FORMAT_ADD_CONTAINER_CLASS_TO_DEVICE
init|=
literal|"class add dev eth0 parent 42:3 classid 42:%d htb rate 10mbit ceil %dmbit"
decl_stmt|;
DECL|field|FORAMT_DELETE_CONTAINER_CLASS_FROM_DEVICE
specifier|private
specifier|static
specifier|final
name|String
name|FORAMT_DELETE_CONTAINER_CLASS_FROM_DEVICE
init|=
literal|"class del dev eth0 classid 42:%d"
decl_stmt|;
DECL|field|TEST_CLASS_ID
specifier|private
specifier|static
specifier|final
name|int
name|TEST_CLASS_ID
init|=
literal|97
decl_stmt|;
comment|//decimal form of 0x00420097 - when reading a classid file, it is read out
comment|//as decimal
DECL|field|TEST_CLASS_ID_DECIMAL_STR
specifier|private
specifier|static
specifier|final
name|String
name|TEST_CLASS_ID_DECIMAL_STR
init|=
literal|"4325527"
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
DECL|field|privilegedOperationExecutorMock
specifier|private
name|PrivilegedOperationExecutor
name|privilegedOperationExecutorMock
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
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|,
name|tmpPath
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyTrafficControlOperation (PrivilegedOperation op, PrivilegedOperation.OperationType expectedOpType, List<String> expectedTcCmds)
specifier|private
name|void
name|verifyTrafficControlOperation
parameter_list|(
name|PrivilegedOperation
name|op
parameter_list|,
name|PrivilegedOperation
operator|.
name|OperationType
name|expectedOpType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedTcCmds
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Verify that the optype matches
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedOpType
argument_list|,
name|op
operator|.
name|getOperationType
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
name|op
operator|.
name|getArguments
argument_list|()
decl_stmt|;
comment|//Verify that arg count is always 1 (tc command file) for a tc operation
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
name|File
name|tcCmdsFile
init|=
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
decl_stmt|;
comment|//Verify that command file exists
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tcCmdsFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tcCmds
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|tcCmdsFile
operator|.
name|toPath
argument_list|()
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
comment|//Verify that the number of commands is the same as expected and verify
comment|//that each command is the same, in sequence
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedTcCmds
operator|.
name|size
argument_list|()
argument_list|,
name|tcCmds
operator|.
name|size
argument_list|()
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
operator|<
name|tcCmds
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedTcCmds
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|tcCmds
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBootstrapRecoveryDisabled ()
specifier|public
name|void
name|testBootstrapRecoveryDisabled
parameter_list|()
block|{
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
name|TrafficController
name|trafficController
init|=
operator|new
name|TrafficController
argument_list|(
name|conf
argument_list|,
name|privilegedOperationExecutorMock
argument_list|)
decl_stmt|;
try|try
block|{
name|trafficController
operator|.
name|bootstrap
argument_list|(
name|DEVICE
argument_list|,
name|ROOT_BANDWIDTH_MBIT
argument_list|,
name|YARN_BANDWIDTH_MBIT
argument_list|)
expr_stmt|;
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
comment|//NM_RECOVERY_DISABLED - so we expect two privileged operation executions
comment|//one for wiping tc state - a second for initializing state
name|verify
argument_list|(
name|privilegedOperationExecutorMock
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
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
comment|//Now verify that the two operations were correct
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|ops
init|=
name|opCaptor
operator|.
name|getAllValues
argument_list|()
decl_stmt|;
name|verifyTrafficControlOperation
argument_list|(
name|ops
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|WIPE_STATE_CMD
argument_list|)
argument_list|)
expr_stmt|;
name|verifyTrafficControlOperation
argument_list|(
name|ops
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|ADD_ROOT_QDISC_CMD
argument_list|,
name|ADD_CGROUP_FILTER_CMD
argument_list|,
name|ADD_ROOT_CLASS_CMD
argument_list|,
name|ADD_DEFAULT_CLASS_CMD
argument_list|,
name|ADD_YARN_CLASS_CMD
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
decl||
name|PrivilegedOperationException
decl||
name|IOException
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
literal|"Caught unexpected exception: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBootstrapRecoveryEnabled ()
specifier|public
name|void
name|testBootstrapRecoveryEnabled
parameter_list|()
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RECOVERY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TrafficController
name|trafficController
init|=
operator|new
name|TrafficController
argument_list|(
name|conf
argument_list|,
name|privilegedOperationExecutorMock
argument_list|)
decl_stmt|;
try|try
block|{
comment|//Return a default tc state when attempting to read state
name|when
argument_list|(
name|privilegedOperationExecutorMock
operator|.
name|executePrivilegedOperation
argument_list|(
name|any
argument_list|(
name|PrivilegedOperation
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|DEFAULT_TC_STATE_EXAMPLE
argument_list|)
expr_stmt|;
name|trafficController
operator|.
name|bootstrap
argument_list|(
name|DEVICE
argument_list|,
name|ROOT_BANDWIDTH_MBIT
argument_list|,
name|YARN_BANDWIDTH_MBIT
argument_list|)
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|PrivilegedOperation
argument_list|>
name|readOpCaptor
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
comment|//NM_RECOVERY_ENABLED - so we expect three privileged operation executions
comment|//1) read tc state 2) wipe tc state 3) init tc state
comment|//one for wiping tc state - a second for initializing state
comment|//First, verify read op
name|verify
argument_list|(
name|privilegedOperationExecutorMock
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|executePrivilegedOperation
argument_list|(
name|readOpCaptor
operator|.
name|capture
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|readOps
init|=
name|readOpCaptor
operator|.
name|getAllValues
argument_list|()
decl_stmt|;
name|verifyTrafficControlOperation
argument_list|(
name|readOps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_READ_STATE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|READ_QDISC_CMD
argument_list|,
name|READ_FILTER_CMD
argument_list|,
name|READ_CLASS_CMD
argument_list|)
argument_list|)
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|PrivilegedOperation
argument_list|>
name|writeOpCaptor
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
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|executePrivilegedOperation
argument_list|(
name|writeOpCaptor
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
comment|//Now verify that the two write operations were correct
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|writeOps
init|=
name|writeOpCaptor
operator|.
name|getAllValues
argument_list|()
decl_stmt|;
name|verifyTrafficControlOperation
argument_list|(
name|writeOps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|WIPE_STATE_CMD
argument_list|)
argument_list|)
expr_stmt|;
name|verifyTrafficControlOperation
argument_list|(
name|writeOps
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|ADD_ROOT_QDISC_CMD
argument_list|,
name|ADD_CGROUP_FILTER_CMD
argument_list|,
name|ADD_ROOT_CLASS_CMD
argument_list|,
name|ADD_DEFAULT_CLASS_CMD
argument_list|,
name|ADD_YARN_CLASS_CMD
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
decl||
name|PrivilegedOperationException
decl||
name|IOException
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
literal|"Caught unexpected exception: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidBuilder ()
specifier|public
name|void
name|testInvalidBuilder
parameter_list|()
block|{
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
name|TrafficController
name|trafficController
init|=
operator|new
name|TrafficController
argument_list|(
name|conf
argument_list|,
name|privilegedOperationExecutorMock
argument_list|)
decl_stmt|;
try|try
block|{
name|trafficController
operator|.
name|bootstrap
argument_list|(
name|DEVICE
argument_list|,
name|ROOT_BANDWIDTH_MBIT
argument_list|,
name|YARN_BANDWIDTH_MBIT
argument_list|)
expr_stmt|;
try|try
block|{
comment|//Invalid op type for TC batch builder
name|TrafficController
operator|.
name|BatchBuilder
name|invalidBuilder
init|=
name|trafficController
operator|.
expr|new
name|BatchBuilder
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|ADD_PID_TO_CGROUP
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Invalid builder check failed!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|e
parameter_list|)
block|{
comment|//expected
block|}
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
literal|"Caught unexpected exception: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testClassIdFileContentParsing ()
specifier|public
name|void
name|testClassIdFileContentParsing
parameter_list|()
block|{
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
name|TrafficController
name|trafficController
init|=
operator|new
name|TrafficController
argument_list|(
name|conf
argument_list|,
name|privilegedOperationExecutorMock
argument_list|)
decl_stmt|;
comment|//Verify that classid file contents are parsed correctly
comment|//This call strips the QDISC prefix and returns the classid asociated with
comment|//the container
name|int
name|parsedClassId
init|=
name|trafficController
operator|.
name|getClassIdFromFileContents
argument_list|(
name|TEST_CLASS_ID_DECIMAL_STR
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TEST_CLASS_ID
argument_list|,
name|parsedClassId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerOperations ()
specifier|public
name|void
name|testContainerOperations
parameter_list|()
block|{
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
name|TrafficController
name|trafficController
init|=
operator|new
name|TrafficController
argument_list|(
name|conf
argument_list|,
name|privilegedOperationExecutorMock
argument_list|)
decl_stmt|;
try|try
block|{
name|trafficController
operator|.
name|bootstrap
argument_list|(
name|DEVICE
argument_list|,
name|ROOT_BANDWIDTH_MBIT
argument_list|,
name|YARN_BANDWIDTH_MBIT
argument_list|)
expr_stmt|;
name|int
name|classId
init|=
name|trafficController
operator|.
name|getNextClassId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|classId
operator|>=
name|MIN_CONTAINER_CLASS_ID
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|FORMAT_CONTAINER_CLASS_STR
argument_list|,
name|classId
argument_list|)
argument_list|,
name|trafficController
operator|.
name|getStringForNetClsClassId
argument_list|(
name|classId
argument_list|)
argument_list|)
expr_stmt|;
comment|//Verify that the operation is setup correctly with strictMode = false
name|TrafficController
operator|.
name|BatchBuilder
name|builder
init|=
name|trafficController
operator|.
expr|new
name|BatchBuilder
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|)
operator|.
name|addContainerClass
argument_list|(
name|classId
argument_list|,
name|CONTAINER_BANDWIDTH_MBIT
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|PrivilegedOperation
name|addClassOp
init|=
name|builder
operator|.
name|commitBatchToTempFile
argument_list|()
decl_stmt|;
name|String
name|expectedAddClassCmd
init|=
name|String
operator|.
name|format
argument_list|(
name|FORMAT_ADD_CONTAINER_CLASS_TO_DEVICE
argument_list|,
name|classId
argument_list|,
name|YARN_BANDWIDTH_MBIT
argument_list|)
decl_stmt|;
name|verifyTrafficControlOperation
argument_list|(
name|addClassOp
argument_list|,
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|expectedAddClassCmd
argument_list|)
argument_list|)
expr_stmt|;
comment|//Verify that the operation is setup correctly with strictMode = true
name|TrafficController
operator|.
name|BatchBuilder
name|strictModeBuilder
init|=
name|trafficController
operator|.
expr|new
name|BatchBuilder
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|)
operator|.
name|addContainerClass
argument_list|(
name|classId
argument_list|,
name|CONTAINER_BANDWIDTH_MBIT
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrivilegedOperation
name|addClassStrictModeOp
init|=
name|strictModeBuilder
operator|.
name|commitBatchToTempFile
argument_list|()
decl_stmt|;
name|String
name|expectedAddClassStrictModeCmd
init|=
name|String
operator|.
name|format
argument_list|(
name|FORMAT_ADD_CONTAINER_CLASS_TO_DEVICE
argument_list|,
name|classId
argument_list|,
name|CONTAINER_BANDWIDTH_MBIT
argument_list|)
decl_stmt|;
name|verifyTrafficControlOperation
argument_list|(
name|addClassStrictModeOp
argument_list|,
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|expectedAddClassStrictModeCmd
argument_list|)
argument_list|)
expr_stmt|;
name|TrafficController
operator|.
name|BatchBuilder
name|deleteBuilder
init|=
name|trafficController
operator|.
expr|new
name|BatchBuilder
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|)
operator|.
name|deleteContainerClass
argument_list|(
name|classId
argument_list|)
decl_stmt|;
name|PrivilegedOperation
name|deleteClassOp
init|=
name|deleteBuilder
operator|.
name|commitBatchToTempFile
argument_list|()
decl_stmt|;
name|String
name|expectedDeleteClassCmd
init|=
name|String
operator|.
name|format
argument_list|(
name|FORAMT_DELETE_CONTAINER_CLASS_FROM_DEVICE
argument_list|,
name|classId
argument_list|)
decl_stmt|;
name|verifyTrafficControlOperation
argument_list|(
name|deleteClassOp
argument_list|,
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|TC_MODIFY_STATE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|expectedDeleteClassCmd
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
decl||
name|IOException
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
literal|"Caught unexpected exception: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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

