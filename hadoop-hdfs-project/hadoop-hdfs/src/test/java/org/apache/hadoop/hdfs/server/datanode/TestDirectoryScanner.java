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
name|FileOutputStream
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
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Block
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
name|GenerationStamp
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
name|FSDataset
operator|.
name|FSVolume
import|;
end_import

begin_comment
comment|/**  * Tests {@link DirectoryScanner} handling of differences  * between blocks on the disk and block in memory.  */
end_comment

begin_class
DECL|class|TestDirectoryScanner
specifier|public
class|class
name|TestDirectoryScanner
extends|extends
name|TestCase
block|{
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
name|TestDirectoryScanner
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONF
specifier|private
specifier|static
specifier|final
name|Configuration
name|CONF
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_GEN_STAMP
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_GEN_STAMP
init|=
literal|9999
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|bpid
specifier|private
name|String
name|bpid
decl_stmt|;
DECL|field|fds
specifier|private
name|FSDataset
name|fds
init|=
literal|null
decl_stmt|;
DECL|field|scanner
specifier|private
name|DirectoryScanner
name|scanner
init|=
literal|null
decl_stmt|;
DECL|field|rand
specifier|private
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|r
specifier|private
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
static|static
block|{
name|CONF
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
comment|/** create a file with a length of<code>fileLen</code> */
DECL|method|createFile (String fileName, long fileLen)
specifier|private
name|void
name|createFile
parameter_list|(
name|String
name|fileName
parameter_list|,
name|long
name|fileLen
parameter_list|)
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
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|fileLen
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Truncate a block file */
DECL|method|truncateBlockFile ()
specifier|private
name|long
name|truncateBlockFile
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|fds
init|)
block|{
for|for
control|(
name|ReplicaInfo
name|b
range|:
name|fds
operator|.
name|volumeMap
operator|.
name|replicas
argument_list|(
name|bpid
argument_list|)
control|)
block|{
name|File
name|f
init|=
name|b
operator|.
name|getBlockFile
argument_list|()
decl_stmt|;
name|File
name|mf
init|=
name|b
operator|.
name|getMetaFile
argument_list|()
decl_stmt|;
comment|// Truncate a block file that has a corresponding metadata file
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
operator|&&
name|f
operator|.
name|length
argument_list|()
operator|!=
literal|0
operator|&&
name|mf
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileOutputStream
name|s
init|=
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|FileChannel
name|channel
init|=
name|s
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|channel
operator|.
name|truncate
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Truncated block file "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|getBlockId
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|0
return|;
block|}
comment|/** Delete a block file */
DECL|method|deleteBlockFile ()
specifier|private
name|long
name|deleteBlockFile
parameter_list|()
block|{
synchronized|synchronized
init|(
name|fds
init|)
block|{
for|for
control|(
name|ReplicaInfo
name|b
range|:
name|fds
operator|.
name|volumeMap
operator|.
name|replicas
argument_list|(
name|bpid
argument_list|)
control|)
block|{
name|File
name|f
init|=
name|b
operator|.
name|getBlockFile
argument_list|()
decl_stmt|;
name|File
name|mf
init|=
name|b
operator|.
name|getMetaFile
argument_list|()
decl_stmt|;
comment|// Delete a block file that has corresponding metadata file
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
operator|&&
name|mf
operator|.
name|exists
argument_list|()
operator|&&
name|f
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting block file "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|getBlockId
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|0
return|;
block|}
comment|/** Delete block meta file */
DECL|method|deleteMetaFile ()
specifier|private
name|long
name|deleteMetaFile
parameter_list|()
block|{
synchronized|synchronized
init|(
name|fds
init|)
block|{
for|for
control|(
name|ReplicaInfo
name|b
range|:
name|fds
operator|.
name|volumeMap
operator|.
name|replicas
argument_list|(
name|bpid
argument_list|)
control|)
block|{
name|File
name|file
init|=
name|b
operator|.
name|getMetaFile
argument_list|()
decl_stmt|;
comment|// Delete a metadata file
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
operator|&&
name|file
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting metadata file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|getBlockId
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|0
return|;
block|}
comment|/** Get a random blockId that is not used already */
DECL|method|getFreeBlockId ()
specifier|private
name|long
name|getFreeBlockId
parameter_list|()
block|{
name|long
name|id
init|=
name|rand
operator|.
name|nextLong
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|id
operator|=
name|rand
operator|.
name|nextLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|fds
operator|.
name|fetchReplicaInfo
argument_list|(
name|bpid
argument_list|,
name|id
argument_list|)
operator|==
literal|null
condition|)
block|{
break|break;
block|}
block|}
return|return
name|id
return|;
block|}
DECL|method|getBlockFile (long id)
specifier|private
name|String
name|getBlockFile
parameter_list|(
name|long
name|id
parameter_list|)
block|{
return|return
name|Block
operator|.
name|BLOCK_FILE_PREFIX
operator|+
name|id
return|;
block|}
DECL|method|getMetaFile (long id)
specifier|private
name|String
name|getMetaFile
parameter_list|(
name|long
name|id
parameter_list|)
block|{
return|return
name|Block
operator|.
name|BLOCK_FILE_PREFIX
operator|+
name|id
operator|+
literal|"_"
operator|+
name|DEFAULT_GEN_STAMP
operator|+
name|Block
operator|.
name|METADATA_EXTENSION
return|;
block|}
comment|/** Create a block file in a random volume*/
DECL|method|createBlockFile ()
specifier|private
name|long
name|createBlockFile
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FSVolume
argument_list|>
name|volumes
init|=
name|fds
operator|.
name|getVolumes
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|volumes
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|id
init|=
name|getFreeBlockId
argument_list|()
decl_stmt|;
name|File
name|finalizedDir
init|=
name|volumes
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getFinalizedDir
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|finalizedDir
argument_list|,
name|getBlockFile
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Created block file "
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
comment|/** Create a metafile in a random volume*/
DECL|method|createMetaFile ()
specifier|private
name|long
name|createMetaFile
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FSVolume
argument_list|>
name|volumes
init|=
name|fds
operator|.
name|getVolumes
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|volumes
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|id
init|=
name|getFreeBlockId
argument_list|()
decl_stmt|;
name|File
name|finalizedDir
init|=
name|volumes
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getFinalizedDir
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|finalizedDir
argument_list|,
name|getMetaFile
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Created metafile "
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
comment|/** Create block file and corresponding metafile in a rondom volume */
DECL|method|createBlockMetaFile ()
specifier|private
name|long
name|createBlockMetaFile
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FSVolume
argument_list|>
name|volumes
init|=
name|fds
operator|.
name|getVolumes
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|volumes
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|id
init|=
name|getFreeBlockId
argument_list|()
decl_stmt|;
name|File
name|finalizedDir
init|=
name|volumes
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getFinalizedDir
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|finalizedDir
argument_list|,
name|getBlockFile
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Created block file "
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create files with same prefix as block file but extension names
comment|// such that during sorting, these files appear around meta file
comment|// to test how DirectoryScanner handles extraneous files
name|String
name|name1
init|=
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|".l"
decl_stmt|;
name|String
name|name2
init|=
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|".n"
decl_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|name1
argument_list|)
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Created extraneous file "
operator|+
name|name1
argument_list|)
expr_stmt|;
block|}
name|file
operator|=
operator|new
name|File
argument_list|(
name|name2
argument_list|)
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Created extraneous file "
operator|+
name|name2
argument_list|)
expr_stmt|;
block|}
name|file
operator|=
operator|new
name|File
argument_list|(
name|finalizedDir
argument_list|,
name|getMetaFile
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Created metafile "
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|id
return|;
block|}
DECL|method|scan (long totalBlocks, int diffsize, long missingMetaFile, long missingBlockFile, long missingMemoryBlocks, long mismatchBlocks)
specifier|private
name|void
name|scan
parameter_list|(
name|long
name|totalBlocks
parameter_list|,
name|int
name|diffsize
parameter_list|,
name|long
name|missingMetaFile
parameter_list|,
name|long
name|missingBlockFile
parameter_list|,
name|long
name|missingMemoryBlocks
parameter_list|,
name|long
name|mismatchBlocks
parameter_list|)
block|{
name|scanner
operator|.
name|reconcile
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|scanner
operator|.
name|diffs
operator|.
name|containsKey
argument_list|(
name|bpid
argument_list|)
argument_list|)
expr_stmt|;
name|LinkedList
argument_list|<
name|DirectoryScanner
operator|.
name|ScanInfo
argument_list|>
name|diff
init|=
name|scanner
operator|.
name|diffs
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|scanner
operator|.
name|stats
operator|.
name|containsKey
argument_list|(
name|bpid
argument_list|)
argument_list|)
expr_stmt|;
name|DirectoryScanner
operator|.
name|Stats
name|stats
init|=
name|scanner
operator|.
name|stats
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|diffsize
argument_list|,
name|diff
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|totalBlocks
argument_list|,
name|stats
operator|.
name|totalBlocks
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missingMetaFile
argument_list|,
name|stats
operator|.
name|missingMetaFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missingBlockFile
argument_list|,
name|stats
operator|.
name|missingBlockFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missingMemoryBlocks
argument_list|,
name|stats
operator|.
name|missingMemoryBlocks
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|mismatchBlocks
argument_list|,
name|stats
operator|.
name|mismatchBlocks
argument_list|)
expr_stmt|;
block|}
DECL|method|testDirectoryScanner ()
specifier|public
name|void
name|testDirectoryScanner
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Run the test with and without parallel scanning
for|for
control|(
name|int
name|parallelism
init|=
literal|1
init|;
name|parallelism
operator|<
literal|3
condition|;
name|parallelism
operator|++
control|)
block|{
name|runTest
argument_list|(
name|parallelism
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|runTest (int parallelism)
specifier|public
name|void
name|runTest
parameter_list|(
name|int
name|parallelism
parameter_list|)
throws|throws
name|Exception
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|CONF
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
name|bpid
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
expr_stmt|;
name|fds
operator|=
operator|(
name|FSDataset
operator|)
name|DataNodeTestUtils
operator|.
name|getFSDataset
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DIRECTORYSCAN_THREADS_KEY
argument_list|,
name|parallelism
argument_list|)
expr_stmt|;
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
name|scanner
operator|=
operator|new
name|DirectoryScanner
argument_list|(
name|dn
argument_list|,
name|fds
argument_list|,
name|CONF
argument_list|)
expr_stmt|;
name|scanner
operator|.
name|setRetainDiffs
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Add files with 100 blocks
name|createFile
argument_list|(
literal|"/tmp/t1"
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|long
name|totalBlocks
init|=
literal|100
decl_stmt|;
comment|// Test1: No difference between in-memory and disk
name|scan
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test2: block metafile is missing
name|long
name|blockId
init|=
name|deleteMetaFile
argument_list|()
decl_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyGenStamp
argument_list|(
name|blockId
argument_list|,
name|GenerationStamp
operator|.
name|GRANDFATHER_GENERATION_STAMP
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test3: block file is missing
name|blockId
operator|=
name|deleteBlockFile
argument_list|()
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|totalBlocks
operator|--
expr_stmt|;
name|verifyDeletion
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test4: A block file exists for which there is no metafile and
comment|// a block in memory
name|blockId
operator|=
name|createBlockFile
argument_list|()
expr_stmt|;
name|totalBlocks
operator|++
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|verifyAddition
argument_list|(
name|blockId
argument_list|,
name|GenerationStamp
operator|.
name|GRANDFATHER_GENERATION_STAMP
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test5: A metafile exists for which there is no block file and
comment|// a block in memory
name|blockId
operator|=
name|createMetaFile
argument_list|()
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
operator|+
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|File
name|metafile
init|=
operator|new
name|File
argument_list|(
name|getMetaFile
argument_list|(
name|blockId
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|metafile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test6: A block file and metafile exists for which there is no block in
comment|// memory
name|blockId
operator|=
name|createBlockMetaFile
argument_list|()
expr_stmt|;
name|totalBlocks
operator|++
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|verifyAddition
argument_list|(
name|blockId
argument_list|,
name|DEFAULT_GEN_STAMP
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test7: Delete bunch of metafiles
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|blockId
operator|=
name|deleteMetaFile
argument_list|()
expr_stmt|;
block|}
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test8: Delete bunch of block files
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|blockId
operator|=
name|deleteBlockFile
argument_list|()
expr_stmt|;
block|}
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|totalBlocks
operator|-=
literal|10
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test9: create a bunch of blocks files
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|blockId
operator|=
name|createBlockFile
argument_list|()
expr_stmt|;
block|}
name|totalBlocks
operator|+=
literal|10
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test10: create a bunch of metafiles
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|blockId
operator|=
name|createMetaFile
argument_list|()
expr_stmt|;
block|}
name|scan
argument_list|(
name|totalBlocks
operator|+
literal|10
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test11: create a bunch block files and meta files
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|blockId
operator|=
name|createBlockMetaFile
argument_list|()
expr_stmt|;
block|}
name|totalBlocks
operator|+=
literal|10
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test12: truncate block files to test block length mismatch
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|truncateBlockFile
argument_list|()
expr_stmt|;
block|}
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test13: all the conditions combined
name|createMetaFile
argument_list|()
expr_stmt|;
name|createBlockFile
argument_list|()
expr_stmt|;
name|createBlockMetaFile
argument_list|()
expr_stmt|;
name|deleteMetaFile
argument_list|()
expr_stmt|;
name|deleteBlockFile
argument_list|()
expr_stmt|;
name|truncateBlockFile
argument_list|()
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
operator|+
literal|3
argument_list|,
literal|6
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|scan
argument_list|(
name|totalBlocks
operator|+
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test14: validate clean shutdown of DirectoryScanner
comment|////assertTrue(scanner.getRunStatus()); //assumes "real" FSDataset, not sim
name|scanner
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|scanner
operator|.
name|getRunStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|scanner
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|verifyAddition (long blockId, long genStamp, long size)
specifier|private
name|void
name|verifyAddition
parameter_list|(
name|long
name|blockId
parameter_list|,
name|long
name|genStamp
parameter_list|,
name|long
name|size
parameter_list|)
block|{
specifier|final
name|ReplicaInfo
name|replicainfo
decl_stmt|;
name|replicainfo
operator|=
name|fds
operator|.
name|fetchReplicaInfo
argument_list|(
name|bpid
argument_list|,
name|blockId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|replicainfo
argument_list|)
expr_stmt|;
comment|// Added block has the same file as the one created by the test
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getBlockFile
argument_list|(
name|blockId
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|,
name|fds
operator|.
name|getFile
argument_list|(
name|bpid
argument_list|,
name|blockId
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Generation stamp is same as that of created file
name|assertEquals
argument_list|(
name|genStamp
argument_list|,
name|replicainfo
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
comment|// File size matches
name|assertEquals
argument_list|(
name|size
argument_list|,
name|replicainfo
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyDeletion (long blockId)
specifier|private
name|void
name|verifyDeletion
parameter_list|(
name|long
name|blockId
parameter_list|)
block|{
comment|// Ensure block does not exist in memory
name|assertNull
argument_list|(
name|fds
operator|.
name|fetchReplicaInfo
argument_list|(
name|bpid
argument_list|,
name|blockId
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyGenStamp (long blockId, long genStamp)
specifier|private
name|void
name|verifyGenStamp
parameter_list|(
name|long
name|blockId
parameter_list|,
name|long
name|genStamp
parameter_list|)
block|{
specifier|final
name|ReplicaInfo
name|memBlock
decl_stmt|;
name|memBlock
operator|=
name|fds
operator|.
name|fetchReplicaInfo
argument_list|(
name|bpid
argument_list|,
name|blockId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|memBlock
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|genStamp
argument_list|,
name|memBlock
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

