begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
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
name|ha
operator|.
name|HAAdmin
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|hdfs
operator|.
name|MiniDFSNNTopology
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNodeAdapter
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
name|test
operator|.
name|GenericTestUtils
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
name|util
operator|.
name|Shell
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
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
name|Charsets
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
name|Joiner
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
name|io
operator|.
name|Files
import|;
end_import

begin_comment
comment|/**  * Tests for HAAdmin command with {@link MiniDFSCluster} set up in HA mode.  */
end_comment

begin_class
DECL|class|TestDFSHAAdminMiniCluster
specifier|public
class|class
name|TestDFSHAAdminMiniCluster
block|{
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HAAdmin
operator|.
name|class
argument_list|)
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
block|}
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
name|TestDFSHAAdminMiniCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|tool
specifier|private
name|DFSHAAdmin
name|tool
decl_stmt|;
DECL|field|errOutBytes
specifier|private
specifier|final
name|ByteArrayOutputStream
name|errOutBytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|errOutput
specifier|private
name|String
name|errOutput
decl_stmt|;
DECL|field|nn1Port
specifier|private
name|int
name|nn1Port
decl_stmt|;
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
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|tool
operator|=
operator|new
name|DFSHAAdmin
argument_list|()
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setErrOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|errOutBytes
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|nn1Port
operator|=
name|cluster
operator|.
name|getNameNodePort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetServiceState ()
specifier|public
name|void
name|testGetServiceState
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-getServiceState"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-getServiceState"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-getServiceState"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-getServiceState"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStateTransition ()
specifier|public
name|void
name|testStateTransition
parameter_list|()
throws|throws
name|Exception
block|{
name|NameNode
name|nnode1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nnode1
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToStandby"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|NameNode
name|nnode2
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nnode2
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nnode2
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToStandby"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nnode2
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToObserver"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nnode2
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nnode2
operator|.
name|isObserverState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testObserverTransition ()
specifier|public
name|void
name|testObserverTransition
parameter_list|()
throws|throws
name|Exception
block|{
name|NameNode
name|nnode1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should be able to transition from STANDBY to OBSERVER
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToObserver"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nnode1
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isObserverState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Transition from Observer to Observer should be no-op
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToObserver"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isObserverState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should also be able to transition back from OBSERVER to STANDBY
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToStandby"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nnode1
operator|.
name|isObserverState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testObserverIllegalTransition ()
specifier|public
name|void
name|testObserverIllegalTransition
parameter_list|()
throws|throws
name|Exception
block|{
name|NameNode
name|nnode1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nnode1
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isActiveState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should NOT be able to transition from ACTIVE to OBSERVER
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToObserver"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isActiveState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should NOT be able to transition from OBSERVER to ACTIVE
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToStandby"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToObserver"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nnode1
operator|.
name|isObserverState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nnode1
operator|.
name|isActiveState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTryFailoverToSafeMode ()
specifier|public
name|void
name|testTryFailoverToSafeMode
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
name|TestDFSHAAdmin
operator|.
name|getFencerTrueCommand
argument_list|()
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn2"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Bad output: "
operator|+
name|errOutput
argument_list|,
name|errOutput
operator|.
name|contains
argument_list|(
literal|"is not ready to become active: "
operator|+
literal|"The NameNode is in safemode"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test failover with various options    */
annotation|@
name|Test
DECL|method|testFencer ()
specifier|public
name|void
name|testFencer
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test failover with no fencer
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set up fencer to write info about the fencing target into a
comment|// tmp file, so we can verify that the args were substituted right
name|File
name|tmpFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"testFencer"
argument_list|,
literal|".txt"
argument_list|)
decl_stmt|;
name|tmpFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"shell(echo %target_nameserviceid%.%target_namenodeid% "
operator|+
literal|"%target_port% %dfs_ha_namenode_id%> "
operator|+
name|tmpFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"shell(echo -n $target_nameserviceid.$target_namenodeid "
operator|+
literal|"$target_port $dfs_ha_namenode_id> "
operator|+
name|tmpFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
comment|// Test failover with fencer
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test failover with fencer and nameservice
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-ns"
argument_list|,
literal|"minidfs-ns"
argument_list|,
literal|"-failover"
argument_list|,
literal|"nn2"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fencer has not run yet, since none of the above required fencing
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|Files
operator|.
name|asCharSource
argument_list|(
name|tmpFile
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test failover with fencer and forcefence option
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forcefence"
argument_list|)
argument_list|)
expr_stmt|;
comment|// The fence script should run with the configuration from the target
comment|// node, rather than the configuration from the fencing node. Strip
comment|// out any trailing spaces and CR/LFs which may be present on Windows.
name|String
name|fenceCommandOutput
init|=
name|Files
operator|.
name|asCharSource
argument_list|(
name|tmpFile
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|read
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|" *[\r\n]+"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"minidfs-ns.nn1 "
operator|+
name|nn1Port
operator|+
literal|" nn1"
argument_list|,
name|fenceCommandOutput
argument_list|)
expr_stmt|;
name|tmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
comment|// Test failover with forceactive option
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn2"
argument_list|,
literal|"nn1"
argument_list|,
literal|"--forceactive"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fencing should not occur, since it was graceful
name|assertFalse
argument_list|(
name|tmpFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test failover with not fencer and forcefence option
name|conf
operator|.
name|unset
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forcefence"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tmpFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test failover with bad fencer and forcefence option
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"foobar!"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forcefence"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tmpFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test failover with force fence listed before the other arguments
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
name|TestDFSHAAdmin
operator|.
name|getFencerTrueCommand
argument_list|()
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"--forcefence"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCheckHealth ()
specifier|public
name|void
name|testCheckHealth
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-checkHealth"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-checkHealth"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test case to check whether both the name node is active or not    * @throws Exception    */
annotation|@
name|Test
DECL|method|testTransitionToActiveWhenOtherNamenodeisActive ()
specifier|public
name|void
name|testTransitionToActiveWhenOtherNamenodeisActive
parameter_list|()
throws|throws
name|Exception
block|{
name|NameNode
name|nn1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NameNode
name|nn2
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|nn1
operator|.
name|getState
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|nn1
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nn2
operator|.
name|getState
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|nn2
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|//Making sure both the namenode are in standby state
name|assertTrue
argument_list|(
name|nn1
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nn2
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Triggering the transition for both namenode to Active
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"nn2"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Both namenodes cannot be active"
argument_list|,
name|nn1
operator|.
name|isActiveState
argument_list|()
operator|&&
name|nn2
operator|.
name|isActiveState
argument_list|()
argument_list|)
expr_stmt|;
comment|/*  In this test case, we have deliberately shut down nn1 and this will         cause HAAAdmin#isOtherTargetNodeActive to throw an Exception          and transitionToActive for nn2 with  forceActive switch will succeed          even with Exception  */
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|nn2
operator|.
name|getState
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|nn2
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|//Making sure both the namenode (nn2) is in standby state
name|assertTrue
argument_list|(
name|nn2
operator|.
name|isStandbyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cluster
operator|.
name|isNameNodeUp
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forceactive"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Namenode nn2 should be active"
argument_list|,
name|nn2
operator|.
name|isActiveState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|runTool (String .... args)
specifier|private
name|int
name|runTool
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|errOutBytes
operator|.
name|reset
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running: DFSHAAdmin "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|" "
argument_list|)
operator|.
name|join
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|ret
init|=
name|tool
operator|.
name|run
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|errOutput
operator|=
operator|new
name|String
argument_list|(
name|errOutBytes
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Output:\n"
operator|+
name|errOutput
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

