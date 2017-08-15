begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
package|package
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
name|ha
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
name|assertTrue
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
name|fail
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|BindException
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
name|Collection
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
name|Callable
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
name|TimeoutException
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
name|permission
operator|.
name|FsPermission
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
name|permission
operator|.
name|PermissionStatus
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
name|ServiceFailedException
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
name|HAUtil
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
name|HdfsConfiguration
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
name|FSEditLog
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
name|FSImage
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
name|NNStorage
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
name|log4j
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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
name|Supplier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestEditLogTailer
specifier|public
class|class
name|TestEditLogTailer
block|{
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|FSEditLog
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Parameters
DECL|method|data ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|}
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
DECL|field|useAsyncEditLog
specifier|private
specifier|static
name|boolean
name|useAsyncEditLog
decl_stmt|;
DECL|method|TestEditLogTailer (Boolean async)
specifier|public
name|TestEditLogTailer
parameter_list|(
name|Boolean
name|async
parameter_list|)
block|{
name|useAsyncEditLog
operator|=
name|async
expr_stmt|;
block|}
DECL|field|DIR_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|DIR_PREFIX
init|=
literal|"/dir"
decl_stmt|;
DECL|field|DIRS_TO_MAKE
specifier|private
specifier|static
specifier|final
name|int
name|DIRS_TO_MAKE
init|=
literal|20
decl_stmt|;
DECL|field|SLEEP_TIME
specifier|static
specifier|final
name|long
name|SLEEP_TIME
init|=
literal|1000
decl_stmt|;
DECL|field|NN_LAG_TIMEOUT
specifier|static
specifier|final
name|long
name|NN_LAG_TIMEOUT
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|FSImage
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|FSEditLog
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|EditLogTailer
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|private
specifier|static
name|Configuration
name|getConf
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_ASYNC_LOGGING
argument_list|,
name|useAsyncEditLog
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|testTailer ()
specifier|public
name|void
name|testTailer
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ServiceFailedException
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_ALL_NAMESNODES_RETRY_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|HAUtil
operator|.
name|setAllowStandbyReads
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
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
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|DIRS_TO_MAKE
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|NameNodeAdapter
operator|.
name|mkdirs
argument_list|(
name|nn1
argument_list|,
name|getDirPath
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|00755
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn1
argument_list|,
name|nn2
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
name|DIRS_TO_MAKE
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|nn2
argument_list|,
name|getDirPath
argument_list|(
name|i
argument_list|)
argument_list|,
literal|false
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|DIRS_TO_MAKE
operator|/
literal|2
init|;
name|i
operator|<
name|DIRS_TO_MAKE
condition|;
name|i
operator|++
control|)
block|{
name|NameNodeAdapter
operator|.
name|mkdirs
argument_list|(
name|nn1
argument_list|,
name|getDirPath
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|00755
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn1
argument_list|,
name|nn2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|DIRS_TO_MAKE
operator|/
literal|2
init|;
name|i
operator|<
name|DIRS_TO_MAKE
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|nn2
argument_list|,
name|getDirPath
argument_list|(
name|i
argument_list|)
argument_list|,
literal|false
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNN0TriggersLogRolls ()
specifier|public
name|void
name|testNN0TriggersLogRolls
parameter_list|()
throws|throws
name|Exception
block|{
name|testStandbyTriggersLogRolls
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNN1TriggersLogRolls ()
specifier|public
name|void
name|testNN1TriggersLogRolls
parameter_list|()
throws|throws
name|Exception
block|{
name|testStandbyTriggersLogRolls
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNN2TriggersLogRolls ()
specifier|public
name|void
name|testNN2TriggersLogRolls
parameter_list|()
throws|throws
name|Exception
block|{
name|testStandbyTriggersLogRolls
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testStandbyTriggersLogRolls (int activeIndex)
specifier|private
specifier|static
name|void
name|testStandbyTriggersLogRolls
parameter_list|(
name|int
name|activeIndex
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
comment|// Roll every 1s
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_LOGROLL_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_ALL_NAMESNODES_RETRY_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
comment|// Have to specify IPC ports so the NNs can talk to each other.
name|int
index|[]
name|ports
init|=
name|ServerSocketUtil
operator|.
name|getPorts
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"ns1"
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn1"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|ports
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn2"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|ports
index|[
literal|1
index|]
argument_list|)
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn3"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|ports
index|[
literal|2
index|]
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
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
name|topology
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
break|break;
block|}
catch|catch
parameter_list|(
name|BindException
name|e
parameter_list|)
block|{
comment|// retry if race on ports given by ServerSocketUtil#getPorts
continue|continue;
block|}
block|}
if|if
condition|(
name|cluster
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"failed to start mini cluster."
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cluster
operator|.
name|transitionToActive
argument_list|(
name|activeIndex
argument_list|)
expr_stmt|;
name|waitForLogRollInSharedDir
argument_list|(
name|cluster
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*     1. when all NN become standby nn, standby NN execute to roll log,     it will be failed.     2. when one NN become active, standby NN roll log success.    */
annotation|@
name|Test
DECL|method|testTriggersLogRollsForAllStandbyNN ()
specifier|public
name|void
name|testTriggersLogRollsForAllStandbyNN
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
comment|// Roll every 1s
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_LOGROLL_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_ALL_NAMESNODES_RETRY_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// Have to specify IPC ports so the NNs can talk to each other.
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"ns1"
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn1"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn2"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn3"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
name|topology
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|2
argument_list|)
expr_stmt|;
try|try
block|{
name|waitForLogRollInSharedDir
argument_list|(
name|cluster
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"After all NN become Standby state, Standby NN should roll log, "
operator|+
literal|"but it will be failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|ignore
parameter_list|)
block|{       }
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|waitForLogRollInSharedDir
argument_list|(
name|cluster
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getDirPath (int suffix)
specifier|private
specifier|static
name|String
name|getDirPath
parameter_list|(
name|int
name|suffix
parameter_list|)
block|{
return|return
name|DIR_PREFIX
operator|+
name|suffix
return|;
block|}
DECL|method|waitForLogRollInSharedDir (MiniDFSCluster cluster, long startTxId)
specifier|private
specifier|static
name|void
name|waitForLogRollInSharedDir
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|long
name|startTxId
parameter_list|)
throws|throws
name|Exception
block|{
name|URI
name|sharedUri
init|=
name|cluster
operator|.
name|getSharedEditsDir
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|File
name|sharedDir
init|=
operator|new
name|File
argument_list|(
name|sharedUri
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
specifier|final
name|File
name|expectedInProgressLog
init|=
operator|new
name|File
argument_list|(
name|sharedDir
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
name|startTxId
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|File
name|expectedFinalizedLog
init|=
operator|new
name|File
argument_list|(
name|sharedDir
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
name|startTxId
argument_list|,
name|startTxId
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// There is a chance that multiple rolling happens by multiple NameNodes
comment|// And expected inprogress file would have also finalized. So look for the
comment|// finalized edits file as well
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|expectedInProgressLog
operator|.
name|exists
argument_list|()
operator|||
name|expectedFinalizedLog
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testRollEditTimeoutForActiveNN ()
specifier|public
name|void
name|testRollEditTimeoutForActiveNN
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_ROLLEDITS_TIMEOUT_KEY
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// 5s
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_ALL_NAMESNODES_RETRY_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|HAUtil
operator|.
name|setAllowStandbyReads
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|EditLogTailer
name|tailer
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|1
argument_list|)
operator|.
name|getEditLogTailer
argument_list|()
argument_list|)
decl_stmt|;
name|AtomicInteger
name|flag
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Return a slow roll edit process.
name|when
argument_list|(
name|tailer
operator|.
name|getNameNodeProxy
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
comment|// sleep for 30 seconds.
name|assertTrue
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
argument_list|)
expr_stmt|;
name|flag
operator|.
name|addAndGet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|tailer
operator|.
name|triggerActiveLogRoll
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|flag
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

