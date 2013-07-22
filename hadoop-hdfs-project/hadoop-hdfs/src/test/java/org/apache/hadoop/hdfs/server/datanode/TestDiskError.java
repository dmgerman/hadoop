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
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|datatransfer
operator|.
name|BlockConstructionStage
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
name|datatransfer
operator|.
name|Sender
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenSecretManager
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
name|util
operator|.
name|DataChecksum
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

begin_comment
comment|/**  * Test that datanodes can correctly handle errors during block read/write.  */
end_comment

begin_class
DECL|class|TestDiskError
specifier|public
class|class
name|TestDiskError
block|{
DECL|field|fs
specifier|private
name|FileSystem
name|fs
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
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|512L
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
literal|1
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
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
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
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test to check that a DN goes down when all its volumes have failed.    */
annotation|@
name|Test
DECL|method|testShutdown ()
specifier|public
name|void
name|testShutdown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
condition|)
block|{
comment|/**        * This test depends on OS not allowing file creations on a directory        * that does not have write permissions for the user. Apparently it is         * not the case on Windows (at least under Cygwin), and possibly AIX.        * This is disabled on Windows.        */
return|return;
block|}
comment|// Bring up two more datanodes
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
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|int
name|dnIndex
init|=
literal|0
decl_stmt|;
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
name|File
name|storageDir
init|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
name|dnIndex
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|File
name|dir1
init|=
name|MiniDFSCluster
operator|.
name|getRbwDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|storageDir
operator|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
name|dnIndex
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|File
name|dir2
init|=
name|MiniDFSCluster
operator|.
name|getRbwDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
try|try
block|{
comment|// make the data directory of the first datanode to be readonly
name|assertTrue
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
name|dir1
operator|.
name|setReadOnly
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Couldn't chmod local vol"
argument_list|,
name|dir2
operator|.
name|setReadOnly
argument_list|()
argument_list|)
expr_stmt|;
comment|// create files and make sure that first datanode will be down
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
name|dnIndex
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|dn
operator|.
name|isDatanodeUp
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/test.txt"
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
name|fileName
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|2
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
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
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
finally|finally
block|{
comment|// restore its old permission
name|FileUtil
operator|.
name|setWritable
argument_list|(
name|dir1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|setWritable
argument_list|(
name|dir2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that when there is a failure replicating a block the temporary    * and meta files are cleaned up and subsequent replication succeeds.    */
annotation|@
name|Test
DECL|method|testReplicationError ()
specifier|public
name|void
name|testReplicationError
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a file of replication factor of 1
specifier|final
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/test.txt"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|fileLen
init|=
literal|1
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
literal|1
argument_list|,
operator|(
name|short
operator|)
literal|1
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
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|// get the block belonged to the created file
name|LocatedBlocks
name|blocks
init|=
name|NameNodeAdapter
operator|.
name|getBlockLocations
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
operator|(
name|long
operator|)
name|fileLen
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should only find 1 block"
argument_list|,
name|blocks
operator|.
name|locatedBlockCount
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|LocatedBlock
name|block
init|=
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// bring up a second datanode
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
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|int
name|sndNode
init|=
literal|1
decl_stmt|;
name|DataNode
name|datanode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|sndNode
argument_list|)
decl_stmt|;
comment|// replicate the block to the second datanode
name|InetSocketAddress
name|target
init|=
name|datanode
operator|.
name|getXferAddress
argument_list|()
decl_stmt|;
name|Socket
name|s
init|=
operator|new
name|Socket
argument_list|(
name|target
operator|.
name|getAddress
argument_list|()
argument_list|,
name|target
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
comment|// write the header.
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|s
operator|.
name|getOutputStream
argument_list|()
argument_list|)
decl_stmt|;
name|DataChecksum
name|checksum
init|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
argument_list|,
literal|512
argument_list|)
decl_stmt|;
operator|new
name|Sender
argument_list|(
name|out
argument_list|)
operator|.
name|writeBlock
argument_list|(
name|block
operator|.
name|getBlock
argument_list|()
argument_list|,
name|BlockTokenSecretManager
operator|.
name|DUMMY_TOKEN
argument_list|,
literal|""
argument_list|,
operator|new
name|DatanodeInfo
index|[
literal|0
index|]
argument_list|,
literal|null
argument_list|,
name|BlockConstructionStage
operator|.
name|PIPELINE_SETUP_CREATE
argument_list|,
literal|1
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|checksum
argument_list|,
name|CachingStrategy
operator|.
name|newDefaultStrategy
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// close the connection before sending the content of the block
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// the temporary block& meta files should be deleted
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
name|File
name|storageDir
init|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
name|sndNode
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|File
name|dir1
init|=
name|MiniDFSCluster
operator|.
name|getRbwDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|storageDir
operator|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
name|sndNode
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|File
name|dir2
init|=
name|MiniDFSCluster
operator|.
name|getRbwDir
argument_list|(
name|storageDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
while|while
condition|(
name|dir1
operator|.
name|listFiles
argument_list|()
operator|.
name|length
operator|!=
literal|0
operator|||
name|dir2
operator|.
name|listFiles
argument_list|()
operator|.
name|length
operator|!=
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
comment|// then increase the file's replication factor
name|fs
operator|.
name|setReplication
argument_list|(
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
comment|// replication should succeed
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|// clean up the file
name|fs
operator|.
name|delete
argument_list|(
name|fileName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that the permissions of the local DN directories are as expected.    */
annotation|@
name|Test
DECL|method|testLocalDirs ()
specifier|public
name|void
name|testLocalDirs
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
specifier|final
name|String
name|permStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_PERMISSION_KEY
argument_list|)
decl_stmt|;
name|FsPermission
name|expected
init|=
operator|new
name|FsPermission
argument_list|(
name|permStr
argument_list|)
decl_stmt|;
comment|// Check permissions on directories in 'dfs.datanode.data.dir'
name|FileSystem
name|localFS
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|DataNode
name|dn
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
name|String
index|[]
name|dataDirs
init|=
name|dn
operator|.
name|getConf
argument_list|()
operator|.
name|getStrings
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|dir
range|:
name|dataDirs
control|)
block|{
name|Path
name|dataDir
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|FsPermission
name|actual
init|=
name|localFS
operator|.
name|getFileStatus
argument_list|(
name|dataDir
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Permission for dir: "
operator|+
name|dataDir
operator|+
literal|", is "
operator|+
name|actual
operator|+
literal|", while expected is "
operator|+
name|expected
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

