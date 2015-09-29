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
name|FSDataInputStream
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|namenode
operator|.
name|LeaseExpiredException
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
name|hdfs
operator|.
name|tools
operator|.
name|DFSAdmin
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
name|IOUtils
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_comment
comment|/**  * This tests pipeline recovery related client protocol works correct or not.  */
end_comment

begin_class
DECL|class|TestClientProtocolForPipelineRecovery
specifier|public
class|class
name|TestClientProtocolForPipelineRecovery
block|{
DECL|method|testGetNewStamp ()
annotation|@
name|Test
specifier|public
name|void
name|testGetNewStamp
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numDataNodes
init|=
literal|1
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
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
name|numDataNodes
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|NamenodeProtocols
name|namenode
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
comment|/* Test writing to finalized replicas */
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"dataprotocol.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|,
literal|1L
argument_list|,
operator|(
name|short
operator|)
name|numDataNodes
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
comment|// get the first blockid for the file
name|ExtendedBlock
name|firstBlock
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|)
decl_stmt|;
comment|// test getNewStampAndToken on a finalized block
try|try
block|{
name|namenode
operator|.
name|updateBlockForPipeline
argument_list|(
name|firstBlock
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Can not get a new GS from a finalized block"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is not under Construction"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// test getNewStampAndToken on a non-existent block
try|try
block|{
name|long
name|newBlockId
init|=
name|firstBlock
operator|.
name|getBlockId
argument_list|()
operator|+
literal|1
decl_stmt|;
name|ExtendedBlock
name|newBlock
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|firstBlock
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|newBlockId
argument_list|,
literal|0
argument_list|,
name|firstBlock
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
decl_stmt|;
name|namenode
operator|.
name|updateBlockForPipeline
argument_list|(
name|newBlock
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Cannot get a new GS from a non-existent block"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"does not exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* Test RBW replicas */
comment|// change first block to a RBW
name|DFSOutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
call|(
name|DFSOutputStream
call|)
argument_list|(
name|fileSys
operator|.
name|append
argument_list|(
name|file
argument_list|)
operator|.
name|getWrappedStream
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|FSDataInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
name|fileSys
operator|.
name|open
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|firstBlock
operator|=
name|DFSTestUtil
operator|.
name|getAllBlocks
argument_list|(
name|in
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|// test non-lease holder
name|DFSClient
name|dfs
init|=
operator|(
operator|(
name|DistributedFileSystem
operator|)
name|fileSys
operator|)
operator|.
name|dfs
decl_stmt|;
try|try
block|{
name|namenode
operator|.
name|updateBlockForPipeline
argument_list|(
name|firstBlock
argument_list|,
literal|"test"
operator|+
name|dfs
operator|.
name|clientName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Cannot get a new GS for a non lease holder"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LeaseExpiredException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Lease mismatch"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// test null lease holder
try|try
block|{
name|namenode
operator|.
name|updateBlockForPipeline
argument_list|(
name|firstBlock
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Cannot get a new GS for a null lease holder"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LeaseExpiredException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Lease mismatch"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// test getNewStampAndToken on a rbw block
name|namenode
operator|.
name|updateBlockForPipeline
argument_list|(
name|firstBlock
argument_list|,
name|dfs
operator|.
name|clientName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
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
comment|/** Test whether corrupt replicas are detected correctly during pipeline    * recoveries.    */
annotation|@
name|Test
DECL|method|testPipelineRecoveryForLastBlock ()
specifier|public
name|void
name|testPipelineRecoveryForLastBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|DFSClientFaultInjector
name|faultInjector
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DFSClientFaultInjector
operator|.
name|class
argument_list|)
decl_stmt|;
name|DFSClientFaultInjector
name|oldInjector
init|=
name|DFSClientFaultInjector
operator|.
name|get
argument_list|()
decl_stmt|;
name|DFSClientFaultInjector
operator|.
name|set
argument_list|(
name|faultInjector
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|BlockWrite
operator|.
name|LOCATEFOLLOWINGBLOCK_RETRIES_KEY
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|numDataNodes
init|=
literal|3
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
name|numDataNodes
argument_list|(
name|numDataNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"dataprotocol1.dat"
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|faultInjector
operator|.
name|failPacket
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|,
literal|68000000L
argument_list|,
operator|(
name|short
operator|)
name|numDataNodes
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
comment|// At this point, NN should have accepted only valid replicas.
comment|// Read should succeed.
name|FSDataInputStream
name|in
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
comment|// Test will fail with BlockMissingException if NN does not update the
comment|// replica state based on the latest report.
block|}
catch|catch
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|BlockMissingException
name|bme
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Block is missing because the file was closed with"
operator|+
literal|" corrupt replicas."
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|DFSClientFaultInjector
operator|.
name|set
argument_list|(
name|oldInjector
argument_list|)
expr_stmt|;
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
block|}
block|}
block|}
comment|/**    * Test recovery on restart OOB message. It also tests the delivery of     * OOB ack originating from the primary datanode. Since there is only    * one node in the cluster, failure of restart-recovery will fail the    * test.    */
annotation|@
name|Test
DECL|method|testPipelineRecoveryOnOOB ()
specifier|public
name|void
name|testPipelineRecoveryOnOOB
parameter_list|()
throws|throws
name|Exception
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
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_DATANODE_RESTART_TIMEOUT_KEY
argument_list|,
literal|"15"
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|numDataNodes
init|=
literal|1
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
name|numDataNodes
argument_list|(
name|numDataNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"dataprotocol2.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|,
literal|10240L
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|DFSOutputStream
name|out
init|=
call|(
name|DFSOutputStream
call|)
argument_list|(
name|fileSys
operator|.
name|append
argument_list|(
name|file
argument_list|)
operator|.
name|getWrappedStream
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|DFSAdmin
name|dfsadmin
init|=
operator|new
name|DFSAdmin
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|String
name|dnAddr
init|=
name|dn
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getIpcAddr
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// issue shutdown to the datanode.
specifier|final
name|String
index|[]
name|args1
init|=
block|{
literal|"-shutdownDatanode"
block|,
name|dnAddr
block|,
literal|"upgrade"
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsadmin
operator|.
name|run
argument_list|(
name|args1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Wait long enough to receive an OOB ack before closing the file.
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
comment|// Retart the datanode
name|cluster
operator|.
name|restartDataNode
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// The following forces a data packet and end of block packets to be sent.
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
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
block|}
block|}
block|}
comment|/** Test restart timeout */
annotation|@
name|Test
DECL|method|testPipelineRecoveryOnRestartFailure ()
specifier|public
name|void
name|testPipelineRecoveryOnRestartFailure
parameter_list|()
throws|throws
name|Exception
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
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_DATANODE_RESTART_TIMEOUT_KEY
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|numDataNodes
init|=
literal|2
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
name|numDataNodes
argument_list|(
name|numDataNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"dataprotocol3.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|,
literal|10240L
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|DFSOutputStream
name|out
init|=
call|(
name|DFSOutputStream
call|)
argument_list|(
name|fileSys
operator|.
name|append
argument_list|(
name|file
argument_list|)
operator|.
name|getWrappedStream
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|DFSAdmin
name|dfsadmin
init|=
operator|new
name|DFSAdmin
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|String
name|dnAddr1
init|=
name|dn
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getIpcAddr
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// issue shutdown to the datanode.
specifier|final
name|String
index|[]
name|args1
init|=
block|{
literal|"-shutdownDatanode"
block|,
name|dnAddr1
block|,
literal|"upgrade"
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsadmin
operator|.
name|run
argument_list|(
name|args1
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
comment|// This should succeed without restarting the node. The restart will
comment|// expire and regular pipeline recovery will kick in.
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// At this point there is only one node in the cluster.
name|out
operator|=
call|(
name|DFSOutputStream
call|)
argument_list|(
name|fileSys
operator|.
name|append
argument_list|(
name|file
argument_list|)
operator|.
name|getWrappedStream
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|dn
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|dnAddr2
init|=
name|dn
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getIpcAddr
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// issue shutdown to the datanode.
specifier|final
name|String
index|[]
name|args2
init|=
block|{
literal|"-shutdownDatanode"
block|,
name|dnAddr2
block|,
literal|"upgrade"
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsadmin
operator|.
name|run
argument_list|(
name|args2
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
try|try
block|{
comment|// close should fail
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
assert|assert
literal|false
assert|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{ }
block|}
finally|finally
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
block|}
block|}
block|}
block|}
end_class

end_unit

