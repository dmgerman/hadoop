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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|CommonConfigurationKeys
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
name|FileStatus
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
name|ClientProtocol
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
name|LocatedBlock
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
name|protocol
operator|.
name|HdfsConstants
operator|.
name|DatanodeReportType
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
name|util
operator|.
name|Time
import|;
end_import

begin_comment
comment|/**  * This class tests the replication of a DFS file.  */
end_comment

begin_class
DECL|class|TestReplication
specifier|public
class|class
name|TestReplication
extends|extends
name|TestCase
block|{
DECL|field|seed
specifier|private
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|8192
decl_stmt|;
DECL|field|fileSize
specifier|private
specifier|static
specifier|final
name|int
name|fileSize
init|=
literal|16384
decl_stmt|;
DECL|field|racks
specifier|private
specifier|static
specifier|final
name|String
name|racks
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"/d1/r1"
block|,
literal|"/d1/r1"
block|,
literal|"/d1/r2"
block|,
literal|"/d1/r2"
block|,
literal|"/d1/r2"
block|,
literal|"/d2/r3"
block|,
literal|"/d2/r3"
block|}
decl_stmt|;
DECL|field|numDatanodes
specifier|private
specifier|static
specifier|final
name|int
name|numDatanodes
init|=
name|racks
operator|.
name|length
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.hadoop.hdfs.TestReplication"
argument_list|)
decl_stmt|;
DECL|method|writeFile (FileSystem fileSys, Path name, int repl)
specifier|private
name|void
name|writeFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|repl
parameter_list|)
throws|throws
name|IOException
block|{
comment|// create and write a file that contains three blocks of data
name|FSDataOutputStream
name|stm
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|fileSys
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
argument_list|,
operator|(
name|short
operator|)
name|repl
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/* check if there are at least two nodes are on the same rack */
DECL|method|checkFile (FileSystem fileSys, Path name, int repl)
specifier|private
name|void
name|checkFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|repl
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|fileSys
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|ClientProtocol
name|namenode
init|=
name|NameNodeProxies
operator|.
name|createProxy
argument_list|(
name|conf
argument_list|,
name|fileSys
operator|.
name|getUri
argument_list|()
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|)
operator|.
name|getProxy
argument_list|()
decl_stmt|;
name|waitForBlockReplication
argument_list|(
name|name
operator|.
name|toString
argument_list|()
argument_list|,
name|namenode
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|numDatanodes
argument_list|,
name|repl
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|locations
init|=
name|namenode
operator|.
name|getBlockLocations
argument_list|(
name|name
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|FileStatus
name|stat
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|BlockLocation
index|[]
name|blockLocations
init|=
name|fileSys
operator|.
name|getFileBlockLocations
argument_list|(
name|stat
argument_list|,
literal|0L
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
comment|// verify that rack locations match
name|assertTrue
argument_list|(
name|blockLocations
operator|.
name|length
operator|==
name|locations
operator|.
name|locatedBlockCount
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
name|blockLocations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LocatedBlock
name|blk
init|=
name|locations
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|datanodes
init|=
name|blk
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|String
index|[]
name|topologyPaths
init|=
name|blockLocations
index|[
name|i
index|]
operator|.
name|getTopologyPaths
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|topologyPaths
operator|.
name|length
operator|==
name|datanodes
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|topologyPaths
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|racks
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|topologyPaths
index|[
name|j
index|]
operator|.
name|startsWith
argument_list|(
name|racks
index|[
name|k
index|]
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|found
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|isOnSameRack
init|=
literal|true
decl_stmt|,
name|isNotOnSameRack
init|=
literal|true
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|blk
range|:
name|locations
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
name|DatanodeInfo
index|[]
name|datanodes
init|=
name|blk
operator|.
name|getLocations
argument_list|()
decl_stmt|;
if|if
condition|(
name|datanodes
operator|.
name|length
operator|<=
literal|1
condition|)
break|break;
if|if
condition|(
name|datanodes
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|isNotOnSameRack
operator|=
operator|!
operator|(
name|datanodes
index|[
literal|0
index|]
operator|.
name|getNetworkLocation
argument_list|()
operator|.
name|equals
argument_list|(
name|datanodes
index|[
literal|1
index|]
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
operator|)
expr_stmt|;
break|break;
block|}
name|isOnSameRack
operator|=
literal|false
expr_stmt|;
name|isNotOnSameRack
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
name|datanodes
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"datanode "
operator|+
name|i
operator|+
literal|": "
operator|+
name|datanodes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|boolean
name|onRack
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|datanodes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|datanodes
index|[
name|i
index|]
operator|.
name|getNetworkLocation
argument_list|()
operator|.
name|equals
argument_list|(
name|datanodes
index|[
name|j
index|]
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
condition|)
block|{
name|onRack
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|onRack
condition|)
block|{
name|isOnSameRack
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|onRack
condition|)
block|{
name|isNotOnSameRack
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|isOnSameRack
operator|&&
name|isNotOnSameRack
condition|)
break|break;
block|}
if|if
condition|(
operator|!
name|isOnSameRack
operator|||
operator|!
name|isNotOnSameRack
condition|)
break|break;
block|}
name|assertTrue
argument_list|(
name|isOnSameRack
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isNotOnSameRack
argument_list|)
expr_stmt|;
block|}
DECL|method|cleanupFile (FileSystem fileSys, Path name)
specifier|private
name|void
name|cleanupFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*     * Test if Datanode reports bad blocks during replication request    */
DECL|method|testBadBlockReportOnTransfer ()
specifier|public
name|void
name|testBadBlockReportOnTransfer
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
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
name|DFSClient
name|dfsClient
init|=
literal|null
decl_stmt|;
name|LocatedBlocks
name|blocks
init|=
literal|null
decl_stmt|;
name|int
name|replicaCount
init|=
literal|0
decl_stmt|;
name|short
name|replFactor
init|=
literal|1
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
literal|2
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
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dfsClient
operator|=
operator|new
name|DFSClient
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Create file with replication factor of 1
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/testBadBlockReportOnTransfer/file1"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
literal|1024
argument_list|,
name|replFactor
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
name|replFactor
argument_list|)
expr_stmt|;
comment|// Corrupt the block belonging to the created file
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|)
decl_stmt|;
name|int
name|blockFilesCorrupted
init|=
name|cluster
operator|.
name|corruptBlockOnDataNodes
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Corrupted too few blocks"
argument_list|,
name|replFactor
argument_list|,
name|blockFilesCorrupted
argument_list|)
expr_stmt|;
comment|// Increase replication factor, this should invoke transfer request
comment|// Receiving datanode fails on checksum and reports it to namenode
name|replFactor
operator|=
literal|2
expr_stmt|;
name|fs
operator|.
name|setReplication
argument_list|(
name|file1
argument_list|,
name|replFactor
argument_list|)
expr_stmt|;
comment|// Now get block details and check if the block is corrupt
name|blocks
operator|=
name|dfsClient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|file1
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
while|while
condition|(
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isCorrupt
argument_list|()
operator|!=
literal|true
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting until block is marked as corrupt..."
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
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{       }
name|blocks
operator|=
name|dfsClient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|file1
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
name|replicaCount
operator|=
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocations
argument_list|()
operator|.
name|length
expr_stmt|;
name|assertTrue
argument_list|(
name|replicaCount
operator|==
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests replication in DFS.    */
DECL|method|runReplication (boolean simulated)
specifier|public
name|void
name|runReplication
parameter_list|(
name|boolean
name|simulated
parameter_list|)
throws|throws
name|IOException
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
name|DFS_NAMENODE_REPLICATION_CONSIDERLOAD_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|simulated
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
name|numDatanodes
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
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|info
init|=
name|client
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of Datanodes "
argument_list|,
name|numDatanodes
argument_list|,
name|info
operator|.
name|length
argument_list|)
expr_stmt|;
name|FileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/smallblocktest.dat"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|checkFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|checkFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|checkFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|checkFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileSys
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testReplicationSimulatedStorag ()
specifier|public
name|void
name|testReplicationSimulatedStorag
parameter_list|()
throws|throws
name|IOException
block|{
name|runReplication
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplication ()
specifier|public
name|void
name|testReplication
parameter_list|()
throws|throws
name|IOException
block|{
name|runReplication
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Waits for all of the blocks to have expected replication
DECL|method|waitForBlockReplication (String filename, ClientProtocol namenode, int expected, long maxWaitSec)
specifier|private
name|void
name|waitForBlockReplication
parameter_list|(
name|String
name|filename
parameter_list|,
name|ClientProtocol
name|namenode
parameter_list|,
name|int
name|expected
parameter_list|,
name|long
name|maxWaitSec
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
comment|//wait for all the blocks to be replicated;
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking for block replication for "
operator|+
name|filename
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|boolean
name|replOk
init|=
literal|true
decl_stmt|;
name|LocatedBlocks
name|blocks
init|=
name|namenode
operator|.
name|getBlockLocations
argument_list|(
name|filename
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|LocatedBlock
argument_list|>
name|iter
init|=
name|blocks
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|LocatedBlock
name|block
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|actual
init|=
name|block
operator|.
name|getLocations
argument_list|()
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|actual
operator|<
name|expected
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Not enough replicas for "
operator|+
name|block
operator|.
name|getBlock
argument_list|()
operator|+
literal|" yet. Expecting "
operator|+
name|expected
operator|+
literal|", got "
operator|+
name|actual
operator|+
literal|"."
argument_list|)
expr_stmt|;
name|replOk
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|replOk
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|maxWaitSec
operator|>
literal|0
operator|&&
operator|(
name|Time
operator|.
name|now
argument_list|()
operator|-
name|start
operator|)
operator|>
operator|(
name|maxWaitSec
operator|*
literal|1000
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Timedout while waiting for all blocks to "
operator|+
literal|" be replicated for "
operator|+
name|filename
argument_list|)
throw|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{}
block|}
block|}
comment|/* This test makes sure that NameNode retries all the available blocks     * for under replicated blocks.     *     * It creates a file with one block and replication of 4. It corrupts     * two of the blocks and removes one of the replicas. Expected behavior is    * that missing replica will be copied from one valid source.    */
DECL|method|testPendingReplicationRetry ()
specifier|public
name|void
name|testPendingReplicationRetry
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|int
name|numDataNodes
init|=
literal|4
decl_stmt|;
name|String
name|testFile
init|=
literal|"/replication-test-file"
decl_stmt|;
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
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
name|buffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
literal|'1'
expr_stmt|;
block|}
try|try
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
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numDataNodes
argument_list|)
argument_list|)
expr_stmt|;
comment|//first time format
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
name|DFSClient
name|dfsClient
init|=
operator|new
name|DFSClient
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|OutputStream
name|out
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|create
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|waitForBlockReplication
argument_list|(
name|testFile
argument_list|,
name|dfsClient
operator|.
name|getNamenode
argument_list|()
argument_list|,
name|numDataNodes
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// get first block of the file.
name|ExtendedBlock
name|block
init|=
name|dfsClient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|testFile
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
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
literal|25
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
literal|'0'
expr_stmt|;
block|}
name|int
name|fileCount
init|=
literal|0
decl_stmt|;
comment|// Choose 3 copies of block file - delete 1 and corrupt the remaining 2
for|for
control|(
name|int
name|dnIndex
init|=
literal|0
init|;
name|dnIndex
operator|<
literal|3
condition|;
name|dnIndex
operator|++
control|)
block|{
name|File
name|blockFile
init|=
name|MiniDFSCluster
operator|.
name|getBlockFile
argument_list|(
name|dnIndex
argument_list|,
name|block
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking for file "
operator|+
name|blockFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|blockFile
operator|!=
literal|null
operator|&&
name|blockFile
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|fileCount
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting file "
operator|+
name|blockFile
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|blockFile
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// corrupt it.
name|LOG
operator|.
name|info
argument_list|(
literal|"Corrupting file "
operator|+
name|blockFile
argument_list|)
expr_stmt|;
name|long
name|len
init|=
name|blockFile
operator|.
name|length
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|len
operator|>
literal|50
argument_list|)
expr_stmt|;
name|RandomAccessFile
name|blockOut
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|blockFile
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
try|try
block|{
name|blockOut
operator|.
name|seek
argument_list|(
name|len
operator|/
literal|3
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|25
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|blockOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|fileCount
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fileCount
argument_list|)
expr_stmt|;
comment|/* Start the MiniDFSCluster with more datanodes since once a writeBlock        * to a datanode node fails, same block can not be written to it        * immediately. In our case some replication attempts will fail.        */
name|LOG
operator|.
name|info
argument_list|(
literal|"Restarting minicluster after deleting a replica and corrupting 2 crcs"
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numDataNodes
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.datanode.block.write.timeout.sec"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY
argument_list|,
literal|"0.75f"
argument_list|)
expr_stmt|;
comment|// only 3 copies exist
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
operator|*
literal|2
argument_list|)
operator|.
name|format
argument_list|(
literal|false
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
name|dfsClient
operator|=
operator|new
name|DFSClient
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|waitForBlockReplication
argument_list|(
name|testFile
argument_list|,
name|dfsClient
operator|.
name|getNamenode
argument_list|()
argument_list|,
name|numDataNodes
argument_list|,
operator|-
literal|1
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
comment|/**    * Test if replication can detect mismatched length on-disk blocks    * @throws Exception    */
DECL|method|testReplicateLenMismatchedBlock ()
specifier|public
name|void
name|testReplicateLenMismatchedBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
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
comment|// test truncated block
name|changeBlockLen
argument_list|(
name|cluster
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// test extended block
name|changeBlockLen
argument_list|(
name|cluster
argument_list|,
literal|1
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
DECL|method|changeBlockLen (MiniDFSCluster cluster, int lenDelta)
specifier|private
name|void
name|changeBlockLen
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|int
name|lenDelta
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/file1"
argument_list|)
decl_stmt|;
specifier|final
name|short
name|REPLICATION_FACTOR
init|=
operator|(
name|short
operator|)
literal|1
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fileLen
init|=
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
literal|512
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
name|fileLen
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
name|REPLICATION_FACTOR
argument_list|)
expr_stmt|;
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
comment|// Change the length of a replica
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|TestDatanodeBlockScanner
operator|.
name|changeReplicaLength
argument_list|(
name|block
argument_list|,
name|i
argument_list|,
name|lenDelta
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
comment|// increase the file's replication factor
name|fs
operator|.
name|setReplication
argument_list|(
name|fileName
argument_list|,
call|(
name|short
call|)
argument_list|(
name|REPLICATION_FACTOR
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// block replication triggers corrupt block detection
name|DFSClient
name|dfsClient
init|=
operator|new
name|DFSClient
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
argument_list|,
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|LocatedBlocks
name|blocks
init|=
name|dfsClient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|fileLen
argument_list|)
decl_stmt|;
if|if
condition|(
name|lenDelta
operator|<
literal|0
condition|)
block|{
comment|// replica truncated
while|while
condition|(
operator|!
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isCorrupt
argument_list|()
operator|||
name|REPLICATION_FACTOR
operator|!=
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocations
argument_list|()
operator|.
name|length
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|dfsClient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|fileLen
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// no corruption detected; block replicated
while|while
condition|(
name|REPLICATION_FACTOR
operator|+
literal|1
operator|!=
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocations
argument_list|()
operator|.
name|length
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|dfsClient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|fileLen
argument_list|)
expr_stmt|;
block|}
block|}
name|fs
operator|.
name|delete
argument_list|(
name|fileName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

