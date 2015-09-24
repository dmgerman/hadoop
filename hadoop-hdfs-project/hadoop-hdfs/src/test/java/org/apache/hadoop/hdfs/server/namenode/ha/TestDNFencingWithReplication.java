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
name|concurrent
operator|.
name|TimeoutException
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
name|BlockLocation
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
name|FileSystem
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
name|DFSTestUtil
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
name|server
operator|.
name|namenode
operator|.
name|FSNamesystem
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
name|io
operator|.
name|retry
operator|.
name|RetryInvocationHandler
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
name|test
operator|.
name|MultithreadedTestUtil
operator|.
name|RepeatingTestThread
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
name|MultithreadedTestUtil
operator|.
name|TestContext
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
name|Assert
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
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Stress-test for potential bugs when replication is changing  * on blocks during a failover.  */
end_comment

begin_class
DECL|class|TestDNFencingWithReplication
specifier|public
class|class
name|TestDNFencingWithReplication
block|{
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|FSNamesystem
operator|.
name|auditLog
argument_list|,
name|Level
operator|.
name|WARN
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|Server
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|FATAL
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|RetryInvocationHandler
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|FATAL
argument_list|)
expr_stmt|;
block|}
DECL|field|NUM_THREADS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_THREADS
init|=
literal|20
decl_stmt|;
comment|// How long should the test try to run for. In practice
comment|// it runs for ~20-30s longer than this constant due to startup/
comment|// shutdown time.
DECL|field|RUNTIME
specifier|private
specifier|static
specifier|final
name|long
name|RUNTIME
init|=
literal|35000
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|class|ReplicationToggler
specifier|private
specifier|static
class|class
name|ReplicationToggler
extends|extends
name|RepeatingTestThread
block|{
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|method|ReplicationToggler (TestContext ctx, FileSystem fs, Path p)
specifier|public
name|ReplicationToggler
parameter_list|(
name|TestContext
name|ctx
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|p
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doAnAction ()
specifier|public
name|void
name|doAnAction
parameter_list|()
throws|throws
name|Exception
block|{
name|fs
operator|.
name|setReplication
argument_list|(
name|path
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|waitForReplicas
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setReplication
argument_list|(
name|path
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|waitForReplicas
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForReplicas (final int replicas)
specifier|private
name|void
name|waitForReplicas
parameter_list|(
specifier|final
name|int
name|replicas
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
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
try|try
block|{
name|BlockLocation
index|[]
name|blocks
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blocks
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|blocks
index|[
literal|0
index|]
operator|.
name|getHosts
argument_list|()
operator|.
name|length
operator|==
name|replicas
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|te
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Timed out waiting for "
operator|+
name|replicas
operator|+
literal|" replicas "
operator|+
literal|"on path "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Toggler for "
operator|+
name|path
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFencingStress ()
specifier|public
name|void
name|testFencingStress
parameter_list|()
throws|throws
name|Exception
block|{
name|HAStressTestHarness
name|harness
init|=
operator|new
name|HAStressTestHarness
argument_list|()
decl_stmt|;
name|harness
operator|.
name|setNumberOfNameNodes
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|harness
operator|.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|harness
operator|.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|harness
operator|.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|MiniDFSCluster
name|cluster
init|=
name|harness
operator|.
name|startCluster
argument_list|()
decl_stmt|;
try|try
block|{
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
name|FileSystem
name|fs
init|=
name|harness
operator|.
name|getFailoverFs
argument_list|()
decl_stmt|;
name|TestContext
name|togglers
init|=
operator|new
name|TestContext
argument_list|()
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|BLOCK_SIZE
operator|*
literal|10
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
operator|(
name|long
operator|)
name|i
argument_list|)
expr_stmt|;
name|togglers
operator|.
name|addThread
argument_list|(
operator|new
name|ReplicationToggler
argument_list|(
name|togglers
argument_list|,
name|fs
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Start a separate thread which will make sure that replication
comment|// happens quickly by triggering deletion reports and replication
comment|// work calculation frequently.
name|harness
operator|.
name|addReplicationTriggerThread
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|harness
operator|.
name|addFailoverThread
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|harness
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|togglers
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|togglers
operator|.
name|waitFor
argument_list|(
name|RUNTIME
argument_list|)
expr_stmt|;
name|togglers
operator|.
name|stop
argument_list|()
expr_stmt|;
name|harness
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
comment|// CHeck that the files can be read without throwing
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"===========================\n\n\n\n"
argument_list|)
expr_stmt|;
name|harness
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

