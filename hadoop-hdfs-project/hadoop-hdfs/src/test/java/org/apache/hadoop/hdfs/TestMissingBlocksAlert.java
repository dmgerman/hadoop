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
name|fs
operator|.
name|ChecksumException
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
name|blockmanagement
operator|.
name|AvailableSpaceBlockPlacementPolicy
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
name|blockmanagement
operator|.
name|BlockManager
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
name|javax
operator|.
name|management
operator|.
name|*
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
name|management
operator|.
name|ManagementFactory
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

begin_comment
comment|/**  * The test makes sure that NameNode detects presense blocks that do not have  * any valid replicas. In addition, it verifies that HDFS front page displays  * a warning in such a case.  */
end_comment

begin_class
DECL|class|TestMissingBlocksAlert
specifier|public
class|class
name|TestMissingBlocksAlert
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
name|TestMissingBlocksAlert
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testMissingBlocksAlert ()
specifier|public
name|void
name|testMissingBlocksAlert
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|MalformedObjectNameException
throws|,
name|AttributeNotFoundException
throws|,
name|MBeanException
throws|,
name|ReflectionException
throws|,
name|InstanceNotFoundException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|//minimize test delay
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|WINDOW_BASE_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|int
name|fileLen
init|=
literal|10
operator|*
literal|1024
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|fileLen
operator|/
literal|2
argument_list|)
expr_stmt|;
comment|//start a cluster with single datanode
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
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|BlockManager
name|bm
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// create a normal file
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/testMissingBlocksAlert/file1"
argument_list|)
argument_list|,
name|fileLen
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Path
name|corruptFile
init|=
operator|new
name|Path
argument_list|(
literal|"/testMissingBlocks/corruptFile"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|corruptFile
argument_list|,
name|fileLen
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Corrupt the block
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|dfs
argument_list|,
name|corruptFile
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|corruptReplica
argument_list|(
literal|0
argument_list|,
name|block
argument_list|)
expr_stmt|;
comment|// read the file so that the corrupt block is reported to NN
name|FSDataInputStream
name|in
init|=
name|dfs
operator|.
name|open
argument_list|(
name|corruptFile
argument_list|)
decl_stmt|;
try|try
block|{
name|in
operator|.
name|readFully
argument_list|(
operator|new
name|byte
index|[
name|fileLen
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|ignored
parameter_list|)
block|{
comment|// checksum error is expected.
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for missing blocks count to increase..."
argument_list|)
expr_stmt|;
while|while
condition|(
name|dfs
operator|.
name|getMissingBlocksCount
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|dfs
operator|.
name|getMissingBlocksCount
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|dfs
operator|.
name|getLowRedundancyBlocksCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|bm
operator|.
name|getUnderReplicatedNotMissingBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|mxbeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=NameNode,name=NameNodeInfo"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
call|(
name|long
call|)
argument_list|(
name|Long
argument_list|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"NumberOfMissingBlocks"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now do the reverse : remove the file expect the number of missing
comment|// blocks to go to zero
name|dfs
operator|.
name|delete
argument_list|(
name|corruptFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for missing blocks count to be zero..."
argument_list|)
expr_stmt|;
while|while
condition|(
name|dfs
operator|.
name|getMissingBlocksCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dfs
operator|.
name|getLowRedundancyBlocksCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bm
operator|.
name|getUnderReplicatedNotMissingBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
call|(
name|long
call|)
argument_list|(
name|Long
argument_list|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"NumberOfMissingBlocks"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|replOneFile
init|=
operator|new
name|Path
argument_list|(
literal|"/testMissingBlocks/replOneFile"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|replOneFile
argument_list|,
name|fileLen
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|replOneBlock
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|dfs
argument_list|,
name|replOneFile
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|corruptReplica
argument_list|(
literal|0
argument_list|,
name|replOneBlock
argument_list|)
expr_stmt|;
comment|// read the file so that the corrupt block is reported to NN
name|in
operator|=
name|dfs
operator|.
name|open
argument_list|(
name|replOneFile
argument_list|)
expr_stmt|;
try|try
block|{
name|in
operator|.
name|readFully
argument_list|(
operator|new
name|byte
index|[
name|fileLen
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|ignored
parameter_list|)
block|{
comment|// checksum error is expected.
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dfs
operator|.
name|getMissingReplOneBlocksCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
call|(
name|long
call|)
argument_list|(
name|Long
argument_list|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"NumberOfMissingBlocksWithReplicationFactorOne"
argument_list|)
argument_list|)
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
annotation|@
name|Test
DECL|method|testMissReplicatedBlockwithTwoRack ()
specifier|public
name|void
name|testMissReplicatedBlockwithTwoRack
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
comment|//Start cluster with rack /default/rack1
name|String
index|[]
name|hosts
init|=
operator|new
name|String
index|[]
block|{
literal|"host0"
block|,
literal|"host1"
block|,
literal|"host2"
block|,
literal|"host3"
block|}
decl_stmt|;
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[]
block|{
literal|"/default/rack1"
block|,
literal|"/default/rack1"
block|,
literal|"/default/rack1"
block|,
literal|"/default/rack1"
block|}
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_KEY
argument_list|,
name|AvailableSpaceBlockPlacementPolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY
argument_list|,
literal|1
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
name|numDataNodes
argument_list|(
literal|4
argument_list|)
operator|.
name|hosts
argument_list|(
name|hosts
argument_list|)
operator|.
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/file2"
argument_list|)
decl_stmt|;
try|try
block|{
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|file
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|//Add one more rack /default/rack2
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/default/rack2"
block|,
literal|"/default/rack2"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"host4"
block|,
literal|"host5"
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setReplication
argument_list|(
name|file
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
comment|// wait for block replication
name|DFSTestUtil
operator|.
name|waitForReplication
argument_list|(
name|dfs
argument_list|,
name|file
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|60000
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

