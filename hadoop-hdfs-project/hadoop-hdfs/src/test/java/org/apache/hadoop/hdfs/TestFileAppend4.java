begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|assertNotNull
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
name|Matchers
operator|.
name|anyLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyObject
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
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
name|doAnswer
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
name|OutputStream
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
name|atomic
operator|.
name|AtomicReference
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|FSDataOutputStream
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
name|protocol
operator|.
name|DatanodeInfo
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
name|protocol
operator|.
name|ExtendedBlock
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
name|protocol
operator|.
name|HdfsConstants
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
name|protocol
operator|.
name|LocatedBlocks
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
name|datanode
operator|.
name|DataNode
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
name|datanode
operator|.
name|SimulatedFSDataset
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
name|FSDirectory
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|INodeFile
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
name|LeaseManager
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
name|protocol
operator|.
name|NamenodeProtocols
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

begin_comment
comment|/* File Append tests for HDFS-200& HDFS-142, specifically focused on:  *  using append()/sync() to recover block information  */
end_comment

begin_class
DECL|class|TestFileAppend4
specifier|public
class|class
name|TestFileAppend4
block|{
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
name|TestFileAppend4
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|long
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|BBW_SIZE
specifier|static
specifier|final
name|long
name|BBW_SIZE
init|=
literal|500
decl_stmt|;
comment|// don't align on bytes/checksum
DECL|field|NO_ARGS
specifier|static
specifier|final
name|Object
index|[]
name|NO_ARGS
init|=
operator|new
name|Object
index|[]
block|{}
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|file1
name|Path
name|file1
decl_stmt|;
DECL|field|stm
name|FSDataOutputStream
name|stm
decl_stmt|;
DECL|field|simulatedStorage
specifier|final
name|boolean
name|simulatedStorage
init|=
literal|false
decl_stmt|;
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|NameNode
operator|.
name|stateChangeLog
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|LeaseManager
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|DataNode
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|DFSClient
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
if|if
condition|(
name|simulatedStorage
condition|)
block|{
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|// lower heartbeat interval for fast recognition of DN death
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
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
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
comment|// handle under-replicated blocks quickly (for replication asserts)
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
literal|5
argument_list|)
expr_stmt|;
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
comment|// handle failures in the DFSClient pipeline quickly
comment|// (for cluster.shutdown(); fs.close() idiom)
name|conf
operator|.
name|setInt
argument_list|(
literal|"ipc.client.connect.max.retries"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/*    * Recover file.    * Try and open file in append mode.    * Doing this, we get a hold of the file that crashed writer    * was writing to.  Once we have it, close it.  This will    * allow subsequent reader to see up to last sync.    * NOTE: This is the same algorithm that HBase uses for file recovery    * @param fs    * @throws Exception    */
DECL|method|recoverFile (final FileSystem fs)
specifier|private
name|void
name|recoverFile
parameter_list|(
specifier|final
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovering File Lease"
argument_list|)
expr_stmt|;
comment|// set the soft limit to be 1 second so that the
comment|// namenode triggers lease recovery upon append request
name|cluster
operator|.
name|setLeasePeriod
argument_list|(
literal|1000
argument_list|,
name|HdfsConstants
operator|.
name|LEASE_HARDLIMIT_PERIOD
argument_list|)
expr_stmt|;
comment|// Trying recovery
name|int
name|tries
init|=
literal|60
decl_stmt|;
name|boolean
name|recovered
init|=
literal|false
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|recovered
operator|&&
name|tries
operator|--
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|out
operator|=
name|fs
operator|.
name|append
argument_list|(
name|file1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully opened for appends"
argument_list|)
expr_stmt|;
name|recovered
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed open for append, waiting on lease recovery"
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|// ignore it and try again
block|}
block|}
block|}
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|recovered
condition|)
block|{
name|fail
argument_list|(
literal|"Recovery should take< 1 min"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Past out lease recovery"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test case that stops a writer after finalizing a block but    * before calling completeFile, and then tries to recover    * the lease from another thread.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testRecoverFinalizedBlock ()
specifier|public
name|void
name|testRecoverFinalizedBlock
parameter_list|()
throws|throws
name|Throwable
block|{
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
name|numDataNodes
argument_list|(
literal|5
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|NamenodeProtocols
name|preSpyNN
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
name|NamenodeProtocols
name|spyNN
init|=
name|spy
argument_list|(
name|preSpyNN
argument_list|)
decl_stmt|;
comment|// Delay completeFile
name|GenericTestUtils
operator|.
name|DelayAnswer
name|delayer
init|=
operator|new
name|GenericTestUtils
operator|.
name|DelayAnswer
argument_list|(
name|LOG
argument_list|)
decl_stmt|;
name|doAnswer
argument_list|(
name|delayer
argument_list|)
operator|.
name|when
argument_list|(
name|spyNN
argument_list|)
operator|.
name|complete
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
operator|(
name|ExtendedBlock
operator|)
name|anyObject
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
literal|null
argument_list|,
name|spyNN
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|file1
operator|=
operator|new
name|Path
argument_list|(
literal|"/testRecoverFinalized"
argument_list|)
expr_stmt|;
specifier|final
name|OutputStream
name|stm
init|=
name|client
operator|.
name|create
argument_list|(
literal|"/testRecoverFinalized"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// write 1/2 block
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|stm
argument_list|,
literal|0
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|err
init|=
operator|new
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|err
operator|.
name|set
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for close to get to latch..."
argument_list|)
expr_stmt|;
name|delayer
operator|.
name|waitForCall
argument_list|()
expr_stmt|;
comment|// At this point, the block is finalized on the DNs, but the file
comment|// has not been completed in the NN.
comment|// Lose the leases
name|LOG
operator|.
name|info
argument_list|(
literal|"Killing lease checker"
argument_list|)
expr_stmt|;
name|client
operator|.
name|getLeaseRenewer
argument_list|()
operator|.
name|interruptAndJoin
argument_list|()
expr_stmt|;
name|FileSystem
name|fs1
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FileSystem
name|fs2
init|=
name|AppendTestUtil
operator|.
name|createHdfsWithDifferentUsername
argument_list|(
name|fs1
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovering file"
argument_list|)
expr_stmt|;
name|recoverFile
argument_list|(
name|fs2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Telling close to proceed."
argument_list|)
expr_stmt|;
name|delayer
operator|.
name|proceed
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for close to finish."
argument_list|)
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Close finished."
argument_list|)
expr_stmt|;
comment|// We expect that close will get a "File is not open"
comment|// error.
name|Throwable
name|thrownByClose
init|=
name|err
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|thrownByClose
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|thrownByClose
operator|instanceof
name|IOException
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|thrownByClose
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"No lease on /testRecoverFinalized"
argument_list|)
condition|)
throw|throw
name|thrownByClose
throw|;
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
comment|/**    * Test case that stops a writer after finalizing a block but    * before calling completeFile, recovers a file from another writer,    * starts writing from that writer, and then has the old lease holder    * call completeFile    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testCompleteOtherLeaseHoldersFile ()
specifier|public
name|void
name|testCompleteOtherLeaseHoldersFile
parameter_list|()
throws|throws
name|Throwable
block|{
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
name|numDataNodes
argument_list|(
literal|5
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|NamenodeProtocols
name|preSpyNN
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
name|NamenodeProtocols
name|spyNN
init|=
name|spy
argument_list|(
name|preSpyNN
argument_list|)
decl_stmt|;
comment|// Delay completeFile
name|GenericTestUtils
operator|.
name|DelayAnswer
name|delayer
init|=
operator|new
name|GenericTestUtils
operator|.
name|DelayAnswer
argument_list|(
name|LOG
argument_list|)
decl_stmt|;
name|doAnswer
argument_list|(
name|delayer
argument_list|)
operator|.
name|when
argument_list|(
name|spyNN
argument_list|)
operator|.
name|complete
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
operator|(
name|ExtendedBlock
operator|)
name|anyObject
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
literal|null
argument_list|,
name|spyNN
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|file1
operator|=
operator|new
name|Path
argument_list|(
literal|"/testCompleteOtherLease"
argument_list|)
expr_stmt|;
specifier|final
name|OutputStream
name|stm
init|=
name|client
operator|.
name|create
argument_list|(
literal|"/testCompleteOtherLease"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// write 1/2 block
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|stm
argument_list|,
literal|0
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|err
init|=
operator|new
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|err
operator|.
name|set
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for close to get to latch..."
argument_list|)
expr_stmt|;
name|delayer
operator|.
name|waitForCall
argument_list|()
expr_stmt|;
comment|// At this point, the block is finalized on the DNs, but the file
comment|// has not been completed in the NN.
comment|// Lose the leases
name|LOG
operator|.
name|info
argument_list|(
literal|"Killing lease checker"
argument_list|)
expr_stmt|;
name|client
operator|.
name|getLeaseRenewer
argument_list|()
operator|.
name|interruptAndJoin
argument_list|()
expr_stmt|;
name|FileSystem
name|fs1
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FileSystem
name|fs2
init|=
name|AppendTestUtil
operator|.
name|createHdfsWithDifferentUsername
argument_list|(
name|fs1
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovering file"
argument_list|)
expr_stmt|;
name|recoverFile
argument_list|(
name|fs2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Opening file for append from new fs"
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|appenderStream
init|=
name|fs2
operator|.
name|append
argument_list|(
name|file1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Writing some data from new appender"
argument_list|)
expr_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|appenderStream
argument_list|,
literal|0
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Telling old close to proceed."
argument_list|)
expr_stmt|;
name|delayer
operator|.
name|proceed
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for close to finish."
argument_list|)
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Close finished."
argument_list|)
expr_stmt|;
comment|// We expect that close will get a "Lease mismatch"
comment|// error.
name|Throwable
name|thrownByClose
init|=
name|err
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|thrownByClose
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|thrownByClose
operator|instanceof
name|IOException
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|thrownByClose
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Lease mismatch"
argument_list|)
condition|)
throw|throw
name|thrownByClose
throw|;
comment|// The appender should be able to close properly
name|appenderStream
operator|.
name|close
argument_list|()
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
comment|/**    * Test the updation of NeededReplications for the Appended Block    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testUpdateNeededReplicationsForAppendedFile ()
specifier|public
name|void
name|testUpdateNeededReplicationsForAppendedFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
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
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DistributedFileSystem
name|fileSystem
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// create a file.
name|fileSystem
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
literal|"/testAppend"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|create
init|=
name|fileSystem
operator|.
name|create
argument_list|(
name|f
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
decl_stmt|;
name|create
operator|.
name|write
argument_list|(
literal|"/testAppend"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|create
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Append to the file.
name|FSDataOutputStream
name|append
init|=
name|fileSystem
operator|.
name|append
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|append
operator|.
name|write
argument_list|(
literal|"/testAppend"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|append
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Start a new datanode
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Check for replications
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fileSystem
argument_list|,
name|f
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|fileSystem
condition|)
block|{
name|fileSystem
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that an append with no locations fails with an exception    * showing insufficient locations.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAppendInsufficientLocations ()
specifier|public
name|void
name|testAppendInsufficientLocations
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// lower heartbeat interval for fast recognition of DN
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
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
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
literal|3000
argument_list|)
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
name|numDataNodes
argument_list|(
literal|4
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|fileSystem
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// create a file with replication 3
name|fileSystem
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
literal|"/testAppend"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|create
init|=
name|fileSystem
operator|.
name|create
argument_list|(
name|f
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
decl_stmt|;
name|create
operator|.
name|write
argument_list|(
literal|"/testAppend"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|create
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Check for replications
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fileSystem
argument_list|,
name|f
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
comment|// Shut down all DNs that have the last block location for the file
name|LocatedBlocks
name|lbs
init|=
name|fileSystem
operator|.
name|dfs
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
literal|"/testAppend"
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DataNode
argument_list|>
name|dnsOfCluster
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
name|DatanodeInfo
index|[]
name|dnsWithLocations
init|=
name|lbs
operator|.
name|getLastLocatedBlock
argument_list|()
operator|.
name|getLocations
argument_list|()
decl_stmt|;
for|for
control|(
name|DataNode
name|dn
range|:
name|dnsOfCluster
control|)
block|{
for|for
control|(
name|DatanodeInfo
name|loc
range|:
name|dnsWithLocations
control|)
block|{
if|if
condition|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|loc
argument_list|)
condition|)
block|{
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForDatanodeDeath
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Wait till 0 replication is recognized
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fileSystem
argument_list|,
name|f
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
comment|// Append to the file, at this state there are 3 live DNs but none of them
comment|// have the block.
try|try
block|{
name|fileSystem
operator|.
name|append
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Append should fail because insufficient locations"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected exception: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|FSDirectory
name|dir
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
specifier|final
name|INodeFile
name|inode
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|dir
operator|.
name|getINode
argument_list|(
literal|"/testAppend"
argument_list|)
argument_list|,
literal|"/testAppend"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"File should remain closed"
argument_list|,
operator|!
name|inode
operator|.
name|isUnderConstruction
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|fileSystem
condition|)
block|{
name|fileSystem
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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

