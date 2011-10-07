begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
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
name|FilenameFilter
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|BlockReader
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
name|BlockReaderFactory
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
name|server
operator|.
name|blockmanagement
operator|.
name|BlockManagerTestUtil
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
name|common
operator|.
name|HdfsServerConstants
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
name|protocol
operator|.
name|DatanodeRegistration
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
name|net
operator|.
name|NetUtils
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Fine-grain testing of block files and locations after volume failure.  */
end_comment

begin_class
DECL|class|TestDataNodeVolumeFailure
specifier|public
class|class
name|TestDataNodeVolumeFailure
block|{
DECL|field|block_size
specifier|final
specifier|private
name|int
name|block_size
init|=
literal|512
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|dn_num
name|int
name|dn_num
init|=
literal|2
decl_stmt|;
DECL|field|blocks_num
name|int
name|blocks_num
init|=
literal|30
decl_stmt|;
DECL|field|repl
name|short
name|repl
init|=
literal|2
decl_stmt|;
DECL|field|dataDir
name|File
name|dataDir
init|=
literal|null
decl_stmt|;
DECL|field|data_fail
name|File
name|data_fail
init|=
literal|null
decl_stmt|;
DECL|field|failedDir
name|File
name|failedDir
init|=
literal|null
decl_stmt|;
comment|// mapping blocks to Meta files(physical files) and locs(NameNode locations)
DECL|class|BlockLocs
specifier|private
class|class
name|BlockLocs
block|{
DECL|field|num_files
specifier|public
name|int
name|num_files
init|=
literal|0
decl_stmt|;
DECL|field|num_locs
specifier|public
name|int
name|num_locs
init|=
literal|0
decl_stmt|;
block|}
comment|// block id to BlockLocs
DECL|field|block_map
name|Map
argument_list|<
name|String
argument_list|,
name|BlockLocs
argument_list|>
name|block_map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|BlockLocs
argument_list|>
argument_list|()
decl_stmt|;
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
comment|// bring up a cluster of 2
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|block_size
argument_list|)
expr_stmt|;
comment|// Allow a single volume failure (there are two volumes)
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
argument_list|,
literal|1
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
name|dn_num
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
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|data_fail
operator|!=
literal|null
condition|)
block|{
name|data_fail
operator|.
name|setWritable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|failedDir
operator|!=
literal|null
condition|)
block|{
name|failedDir
operator|.
name|setWritable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
comment|/*    * Verify the number of blocks and files are correct after volume failure,    * and that we can replicate to both datanodes even after a single volume    * failure if the configuration parameter allows this.    */
annotation|@
name|Test
DECL|method|testVolumeFailure ()
specifier|public
name|void
name|testVolumeFailure
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|cluster
operator|.
name|getDataDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Data dir: is "
operator|+
name|dataDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Data dir structure is dataDir/data[1-4]/[current,tmp...]
comment|// data1,2 is for datanode 1, data2,3 - datanode2
name|String
name|filename
init|=
literal|"/test.txt"
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|filename
argument_list|)
decl_stmt|;
comment|// we use only small number of blocks to avoid creating subdirs in the data dir..
name|int
name|filesize
init|=
name|block_size
operator|*
name|blocks_num
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|filesize
argument_list|,
name|repl
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|repl
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"file "
operator|+
name|filename
operator|+
literal|"(size "
operator|+
name|filesize
operator|+
literal|") is created and replicated"
argument_list|)
expr_stmt|;
comment|// fail the volume
comment|// delete/make non-writable one of the directories (failed volume)
name|data_fail
operator|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"data3"
argument_list|)
expr_stmt|;
name|failedDir
operator|=
name|MiniDFSCluster
operator|.
name|getFinalizedDir
argument_list|(
name|dataDir
argument_list|,
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|failedDir
operator|.
name|exists
argument_list|()
operator|&&
comment|//!FileUtil.fullyDelete(failedDir)
operator|!
name|deteteBlocks
argument_list|(
name|failedDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not delete hdfs directory '"
operator|+
name|failedDir
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|data_fail
operator|.
name|setReadOnly
argument_list|()
expr_stmt|;
name|failedDir
operator|.
name|setReadOnly
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deleteing "
operator|+
name|failedDir
operator|.
name|getPath
argument_list|()
operator|+
literal|"; exist="
operator|+
name|failedDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// access all the blocks on the "failed" DataNode,
comment|// we need to make sure that the "failed" volume is being accessed -
comment|// and that will cause failure, blocks removal, "emergency" block report
name|triggerFailure
argument_list|(
name|filename
argument_list|,
name|filesize
argument_list|)
expr_stmt|;
comment|// make sure a block report is sent
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
literal|1
argument_list|)
decl_stmt|;
comment|//corresponds to dir data3
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|DatanodeRegistration
name|dnR
init|=
name|dn
operator|.
name|getDNRegistrationForBP
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|long
index|[]
name|bReport
init|=
name|dn
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getBlockReport
argument_list|(
name|bpid
argument_list|)
operator|.
name|getBlockListAsLongs
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|blockReport
argument_list|(
name|dnR
argument_list|,
name|bpid
argument_list|,
name|bReport
argument_list|)
expr_stmt|;
comment|// verify number of blocks and files...
name|verify
argument_list|(
name|filename
argument_list|,
name|filesize
argument_list|)
expr_stmt|;
comment|// create another file (with one volume failed).
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"creating file test1.txt"
argument_list|)
expr_stmt|;
name|Path
name|fileName1
init|=
operator|new
name|Path
argument_list|(
literal|"/test1.txt"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|fileName1
argument_list|,
name|filesize
argument_list|,
name|repl
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
comment|// should be able to replicate to both nodes (2 DN, repl=2)
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName1
argument_list|,
name|repl
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"file "
operator|+
name|fileName1
operator|.
name|getName
argument_list|()
operator|+
literal|" is created and replicated"
argument_list|)
expr_stmt|;
block|}
comment|/**    * verifies two things:    *  1. number of locations of each block in the name node    *   matches number of actual files    *  2. block files + pending block equals to total number of blocks that a file has     *     including the replication (HDFS file has 30 blocks, repl=2 - total 60    * @param fn - file name    * @param fs - file size    * @throws IOException    */
DECL|method|verify (String fn, int fs)
specifier|private
name|void
name|verify
parameter_list|(
name|String
name|fn
parameter_list|,
name|int
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// now count how many physical blocks are there
name|int
name|totalReal
init|=
name|countRealBlocks
argument_list|(
name|block_map
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"countRealBlocks counted "
operator|+
name|totalReal
operator|+
literal|" blocks"
argument_list|)
expr_stmt|;
comment|// count how many blocks store in NN structures.
name|int
name|totalNN
init|=
name|countNNBlocks
argument_list|(
name|block_map
argument_list|,
name|fn
argument_list|,
name|fs
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"countNNBlocks counted "
operator|+
name|totalNN
operator|+
literal|" blocks"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|bid
range|:
name|block_map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|BlockLocs
name|bl
init|=
name|block_map
operator|.
name|get
argument_list|(
name|bid
argument_list|)
decl_stmt|;
comment|// System.out.println(bid + "->" + bl.num_files + "vs." + bl.num_locs);
comment|// number of physical files (1 or 2) should be same as number of datanodes
comment|// in the list of the block locations
name|assertEquals
argument_list|(
literal|"Num files should match num locations"
argument_list|,
name|bl
operator|.
name|num_files
argument_list|,
name|bl
operator|.
name|num_locs
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Num physical blocks should match num stored in the NN"
argument_list|,
name|totalReal
argument_list|,
name|totalNN
argument_list|)
expr_stmt|;
comment|// now check the number of under-replicated blocks
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
comment|// force update of all the metric counts by calling computeDatanodeWork
name|BlockManagerTestUtil
operator|.
name|getComputedDatanodeWork
argument_list|(
name|fsn
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
comment|// get all the counts
name|long
name|underRepl
init|=
name|fsn
operator|.
name|getUnderReplicatedBlocks
argument_list|()
decl_stmt|;
name|long
name|pendRepl
init|=
name|fsn
operator|.
name|getPendingReplicationBlocks
argument_list|()
decl_stmt|;
name|long
name|totalRepl
init|=
name|underRepl
operator|+
name|pendRepl
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"underreplicated after = "
operator|+
name|underRepl
operator|+
literal|" and pending repl ="
operator|+
name|pendRepl
operator|+
literal|"; total underRepl = "
operator|+
name|totalRepl
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"total blocks (real and replicating):"
operator|+
operator|(
name|totalReal
operator|+
name|totalRepl
operator|)
operator|+
literal|" vs. all files blocks "
operator|+
name|blocks_num
operator|*
literal|2
argument_list|)
expr_stmt|;
comment|// together all the blocks should be equal to all real + all underreplicated
name|assertEquals
argument_list|(
literal|"Incorrect total block count"
argument_list|,
name|totalReal
operator|+
name|totalRepl
argument_list|,
name|blocks_num
operator|*
name|repl
argument_list|)
expr_stmt|;
block|}
comment|/**    * go to each block on the 2nd DataNode until it fails...    * @param path    * @param size    * @throws IOException    */
DECL|method|triggerFailure (String path, long size)
specifier|private
name|void
name|triggerFailure
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|NamenodeProtocols
name|nn
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|locatedBlocks
init|=
name|nn
operator|.
name|getBlockLocations
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
operator|.
name|getLocatedBlocks
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|lb
range|:
name|locatedBlocks
control|)
block|{
name|DatanodeInfo
name|dinfo
init|=
name|lb
operator|.
name|getLocations
argument_list|()
index|[
literal|1
index|]
decl_stmt|;
name|ExtendedBlock
name|b
init|=
name|lb
operator|.
name|getBlock
argument_list|()
decl_stmt|;
try|try
block|{
name|accessBlock
argument_list|(
name|dinfo
argument_list|,
name|lb
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Failure triggered, on block: "
operator|+
name|b
operator|.
name|getBlockId
argument_list|()
operator|+
literal|"; corresponding volume should be removed by now"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**    * simulate failure delete all the block files    * @param dir    * @throws IOException    */
DECL|method|deteteBlocks (File dir)
specifier|private
name|boolean
name|deteteBlocks
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|File
index|[]
name|fileList
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|fileList
control|)
block|{
if|if
condition|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"blk_"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * try to access a block on a data node. If fails - throws exception    * @param datanode    * @param lblock    * @throws IOException    */
DECL|method|accessBlock (DatanodeInfo datanode, LocatedBlock lblock)
specifier|private
name|void
name|accessBlock
parameter_list|(
name|DatanodeInfo
name|datanode
parameter_list|,
name|LocatedBlock
name|lblock
parameter_list|)
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|targetAddr
init|=
literal|null
decl_stmt|;
name|Socket
name|s
init|=
literal|null
decl_stmt|;
name|ExtendedBlock
name|block
init|=
name|lblock
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|targetAddr
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|datanode
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|Socket
argument_list|()
expr_stmt|;
name|s
operator|.
name|connect
argument_list|(
name|targetAddr
argument_list|,
name|HdfsServerConstants
operator|.
name|READ_TIMEOUT
argument_list|)
expr_stmt|;
name|s
operator|.
name|setSoTimeout
argument_list|(
name|HdfsServerConstants
operator|.
name|READ_TIMEOUT
argument_list|)
expr_stmt|;
name|String
name|file
init|=
name|BlockReaderFactory
operator|.
name|getFileName
argument_list|(
name|targetAddr
argument_list|,
literal|"test-blockpoolid"
argument_list|,
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
decl_stmt|;
name|BlockReader
name|blockReader
init|=
name|BlockReaderFactory
operator|.
name|newBlockReader
argument_list|(
name|s
argument_list|,
name|file
argument_list|,
name|block
argument_list|,
name|lblock
operator|.
name|getBlockToken
argument_list|()
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|4096
argument_list|)
decl_stmt|;
comment|// nothing - if it fails - it will throw and exception
block|}
comment|/**    * Count datanodes that have copies of the blocks for a file    * put it into the map    * @param map    * @param path    * @param size    * @return    * @throws IOException    */
DECL|method|countNNBlocks (Map<String, BlockLocs> map, String path, long size)
specifier|private
name|int
name|countNNBlocks
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|BlockLocs
argument_list|>
name|map
parameter_list|,
name|String
name|path
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|total
init|=
literal|0
decl_stmt|;
name|NamenodeProtocols
name|nn
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|locatedBlocks
init|=
name|nn
operator|.
name|getBlockLocations
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
operator|.
name|getLocatedBlocks
argument_list|()
decl_stmt|;
comment|//System.out.println("Number of blocks: " + locatedBlocks.size());
for|for
control|(
name|LocatedBlock
name|lb
range|:
name|locatedBlocks
control|)
block|{
name|String
name|blockId
init|=
literal|""
operator|+
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
comment|//System.out.print(blockId + ": ");
name|DatanodeInfo
index|[]
name|dn_locs
init|=
name|lb
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|BlockLocs
name|bl
init|=
name|map
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
name|bl
operator|==
literal|null
condition|)
block|{
name|bl
operator|=
operator|new
name|BlockLocs
argument_list|()
expr_stmt|;
block|}
comment|//System.out.print(dn_info.name+",");
name|total
operator|+=
name|dn_locs
operator|.
name|length
expr_stmt|;
name|bl
operator|.
name|num_locs
operator|+=
name|dn_locs
operator|.
name|length
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|blockId
argument_list|,
name|bl
argument_list|)
expr_stmt|;
comment|//System.out.println();
block|}
return|return
name|total
return|;
block|}
comment|/**    *  look for real blocks    *  by counting *.meta files in all the storage dirs     * @param map    * @return    */
DECL|method|countRealBlocks (Map<String, BlockLocs> map)
specifier|private
name|int
name|countRealBlocks
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|BlockLocs
argument_list|>
name|map
parameter_list|)
block|{
name|int
name|total
init|=
literal|0
decl_stmt|;
specifier|final
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
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
name|dn_num
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
literal|1
condition|;
name|j
operator|++
control|)
block|{
name|File
name|storageDir
init|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
name|i
argument_list|,
name|j
argument_list|)
decl_stmt|;
name|File
name|dir
init|=
name|MiniDFSCluster
operator|.
name|getFinalizedDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"dir is null for dn="
operator|+
name|i
operator|+
literal|" and data_dir="
operator|+
name|j
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|String
index|[]
name|res
init|=
name|metaFilesInDir
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"res is null for dir = "
operator|+
name|dir
operator|+
literal|" i="
operator|+
name|i
operator|+
literal|" and j="
operator|+
name|j
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|//System.out.println("for dn" + i + "." + j + ": " + dir + "=" + res.length+ " files");
comment|//int ii = 0;
for|for
control|(
name|String
name|s
range|:
name|res
control|)
block|{
comment|// cut off "blk_-" at the beginning and ".meta" at the end
name|assertNotNull
argument_list|(
literal|"Block file name should not be null"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|String
name|bid
init|=
name|s
operator|.
name|substring
argument_list|(
name|s
operator|.
name|indexOf
argument_list|(
literal|"_"
argument_list|)
operator|+
literal|1
argument_list|,
name|s
operator|.
name|lastIndexOf
argument_list|(
literal|"_"
argument_list|)
argument_list|)
decl_stmt|;
comment|//System.out.println(ii++ + ". block " + s + "; id=" + bid);
name|BlockLocs
name|val
init|=
name|map
operator|.
name|get
argument_list|(
name|bid
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|val
operator|=
operator|new
name|BlockLocs
argument_list|()
expr_stmt|;
block|}
name|val
operator|.
name|num_files
operator|++
expr_stmt|;
comment|// one more file for the block
name|map
operator|.
name|put
argument_list|(
name|bid
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("dir1="+dir.getPath() + "blocks=" + res.length);
comment|//System.out.println("dir2="+dir2.getPath() + "blocks=" + res2.length);
name|total
operator|+=
name|res
operator|.
name|length
expr_stmt|;
block|}
block|}
return|return
name|total
return|;
block|}
comment|/*    * count how many files *.meta are in the dir    */
DECL|method|metaFilesInDir (File dir)
specifier|private
name|String
index|[]
name|metaFilesInDir
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|String
index|[]
name|res
init|=
name|dir
operator|.
name|list
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"blk_"
argument_list|)
operator|&&
name|name
operator|.
name|endsWith
argument_list|(
name|FSDataset
operator|.
name|METADATA_EXTENSION
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

