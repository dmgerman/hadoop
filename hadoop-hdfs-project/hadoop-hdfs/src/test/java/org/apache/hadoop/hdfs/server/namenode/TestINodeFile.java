begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|util
operator|.
name|EnumSet
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
name|CreateFlag
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
name|Options
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
name|PathIsNotDirectoryException
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
name|DistributedFileSystem
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
name|HdfsFileStatus
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
name|BlockInfo
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
name|io
operator|.
name|EnumSetWritable
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

begin_class
DECL|class|TestINodeFile
specifier|public
class|class
name|TestINodeFile
block|{
DECL|field|BLOCKBITS
specifier|static
specifier|final
name|short
name|BLOCKBITS
init|=
literal|48
decl_stmt|;
DECL|field|BLKSIZE_MAXVALUE
specifier|static
specifier|final
name|long
name|BLKSIZE_MAXVALUE
init|=
operator|~
operator|(
literal|0xffffL
operator|<<
name|BLOCKBITS
operator|)
decl_stmt|;
DECL|field|userName
specifier|private
name|String
name|userName
init|=
literal|"Test"
decl_stmt|;
DECL|field|replication
specifier|private
name|short
name|replication
decl_stmt|;
DECL|field|preferredBlockSize
specifier|private
name|long
name|preferredBlockSize
decl_stmt|;
comment|/**    * Test for the Replication value. Sets a value and checks if it was set    * correct.    */
annotation|@
name|Test
DECL|method|testReplication ()
specifier|public
name|void
name|testReplication
parameter_list|()
block|{
name|replication
operator|=
literal|3
expr_stmt|;
name|preferredBlockSize
operator|=
literal|128
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
name|INodeFile
name|inf
init|=
operator|new
name|INodeFile
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
name|userName
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|preferredBlockSize
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"True has to be returned in this case"
argument_list|,
name|replication
argument_list|,
name|inf
operator|.
name|getBlockReplication
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * IllegalArgumentException is expected for setting below lower bound    * for Replication.    * @throws IllegalArgumentException as the result    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testReplicationBelowLowerBound ()
specifier|public
name|void
name|testReplicationBelowLowerBound
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
name|replication
operator|=
operator|-
literal|1
expr_stmt|;
name|preferredBlockSize
operator|=
literal|128
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
operator|new
name|INodeFile
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
name|userName
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|preferredBlockSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for the PreferredBlockSize value. Sets a value and checks if it was    * set correct.    */
annotation|@
name|Test
DECL|method|testPreferredBlockSize ()
specifier|public
name|void
name|testPreferredBlockSize
parameter_list|()
block|{
name|replication
operator|=
literal|3
expr_stmt|;
name|preferredBlockSize
operator|=
literal|128
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
name|INodeFile
name|inf
init|=
operator|new
name|INodeFile
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
name|userName
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|preferredBlockSize
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"True has to be returned in this case"
argument_list|,
name|preferredBlockSize
argument_list|,
name|inf
operator|.
name|getPreferredBlockSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreferredBlockSizeUpperBound ()
specifier|public
name|void
name|testPreferredBlockSizeUpperBound
parameter_list|()
block|{
name|replication
operator|=
literal|3
expr_stmt|;
name|preferredBlockSize
operator|=
name|BLKSIZE_MAXVALUE
expr_stmt|;
name|INodeFile
name|inf
init|=
operator|new
name|INodeFile
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
name|userName
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|preferredBlockSize
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"True has to be returned in this case"
argument_list|,
name|BLKSIZE_MAXVALUE
argument_list|,
name|inf
operator|.
name|getPreferredBlockSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * IllegalArgumentException is expected for setting below lower bound    * for PreferredBlockSize.    * @throws IllegalArgumentException as the result    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testPreferredBlockSizeBelowLowerBound ()
specifier|public
name|void
name|testPreferredBlockSizeBelowLowerBound
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
name|replication
operator|=
literal|3
expr_stmt|;
name|preferredBlockSize
operator|=
operator|-
literal|1
expr_stmt|;
operator|new
name|INodeFile
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
name|userName
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|preferredBlockSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * IllegalArgumentException is expected for setting above upper bound    * for PreferredBlockSize.    * @throws IllegalArgumentException as the result    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testPreferredBlockSizeAboveUpperBound ()
specifier|public
name|void
name|testPreferredBlockSizeAboveUpperBound
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
name|replication
operator|=
literal|3
expr_stmt|;
name|preferredBlockSize
operator|=
name|BLKSIZE_MAXVALUE
operator|+
literal|1
expr_stmt|;
operator|new
name|INodeFile
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
name|userName
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|preferredBlockSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFullPathName ()
specifier|public
name|void
name|testGetFullPathName
parameter_list|()
block|{
name|PermissionStatus
name|perms
init|=
operator|new
name|PermissionStatus
argument_list|(
name|userName
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
name|replication
operator|=
literal|3
expr_stmt|;
name|preferredBlockSize
operator|=
literal|128
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
name|INodeFile
name|inf
init|=
operator|new
name|INodeFile
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
name|perms
argument_list|,
literal|null
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|preferredBlockSize
argument_list|)
decl_stmt|;
name|inf
operator|.
name|setLocalName
argument_list|(
literal|"f"
argument_list|)
expr_stmt|;
name|INodeDirectory
name|root
init|=
operator|new
name|INodeDirectory
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
name|INodeDirectory
operator|.
name|ROOT_NAME
argument_list|,
name|perms
argument_list|)
decl_stmt|;
name|INodeDirectory
name|dir
init|=
operator|new
name|INodeDirectory
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
literal|"d"
argument_list|,
name|perms
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"f"
argument_list|,
name|inf
operator|.
name|getFullPathName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|inf
operator|.
name|getLocalParentDir
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|addChild
argument_list|(
name|inf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d"
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"f"
argument_list|,
name|inf
operator|.
name|getFullPathName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d"
argument_list|,
name|inf
operator|.
name|getLocalParentDir
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|addChild
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"d"
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"f"
argument_list|,
name|inf
operator|.
name|getFullPathName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"d"
argument_list|,
name|dir
operator|.
name|getFullPathName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|,
name|root
operator|.
name|getFullPathName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|,
name|root
operator|.
name|getLocalParentDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * FSDirectory#unprotectedSetQuota creates a new INodeDirectoryWithQuota to    * replace the original INodeDirectory. Before HDFS-4243, the parent field of    * all the children INodes of the target INodeDirectory is not changed to    * point to the new INodeDirectoryWithQuota. This testcase tests this    * scenario.    */
annotation|@
name|Test
DECL|method|testGetFullPathNameAfterSetQuota ()
specifier|public
name|void
name|testGetFullPathNameAfterSetQuota
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|fileLen
init|=
literal|1024
decl_stmt|;
name|replication
operator|=
literal|3
expr_stmt|;
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
literal|null
decl_stmt|;
try|try
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
name|replication
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
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|FSDirectory
name|fsdir
init|=
name|fsn
operator|.
name|getFSDirectory
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
comment|// Create a file for test
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/dir"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|file
argument_list|,
name|fileLen
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
comment|// Check the full path name of the INode associating with the file
name|INode
name|fnode
init|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|,
name|fnode
operator|.
name|getFullPathName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Call FSDirectory#unprotectedSetQuota which calls
comment|// INodeDirectory#replaceChild
name|dfs
operator|.
name|setQuota
argument_list|(
name|dir
argument_list|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|,
name|replication
operator|*
name|fileLen
operator|*
literal|10
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|newDir
init|=
operator|new
name|Path
argument_list|(
literal|"/newdir"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|newFile
init|=
operator|new
name|Path
argument_list|(
name|newDir
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
comment|// Also rename dir
name|dfs
operator|.
name|rename
argument_list|(
name|dir
argument_list|,
name|newDir
argument_list|,
name|Options
operator|.
name|Rename
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
comment|// /dir/file now should be renamed to /newdir/file
name|fnode
operator|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|newFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// getFullPathName can return correct result only if the parent field of
comment|// child node is set correctly
name|assertEquals
argument_list|(
name|newFile
operator|.
name|toString
argument_list|()
argument_list|,
name|fnode
operator|.
name|getFullPathName
argument_list|()
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
DECL|method|testAppendBlocks ()
specifier|public
name|void
name|testAppendBlocks
parameter_list|()
block|{
name|INodeFile
name|origFile
init|=
name|createINodeFiles
argument_list|(
literal|1
argument_list|,
literal|"origfile"
argument_list|)
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of blocks didn't match"
argument_list|,
name|origFile
operator|.
name|numBlocks
argument_list|()
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|INodeFile
index|[]
name|appendFiles
init|=
name|createINodeFiles
argument_list|(
literal|4
argument_list|,
literal|"appendfile"
argument_list|)
decl_stmt|;
name|origFile
operator|.
name|appendBlocks
argument_list|(
name|appendFiles
argument_list|,
name|getTotalBlocks
argument_list|(
name|appendFiles
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of blocks didn't match"
argument_list|,
name|origFile
operator|.
name|numBlocks
argument_list|()
argument_list|,
literal|5L
argument_list|)
expr_stmt|;
block|}
comment|/**     * Gives the count of blocks for a given number of files    * @param files Array of INode files    * @return total count of blocks    */
DECL|method|getTotalBlocks (INodeFile[] files)
specifier|private
name|int
name|getTotalBlocks
parameter_list|(
name|INodeFile
index|[]
name|files
parameter_list|)
block|{
name|int
name|nBlocks
init|=
literal|0
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nBlocks
operator|+=
name|files
index|[
name|i
index|]
operator|.
name|numBlocks
argument_list|()
expr_stmt|;
block|}
return|return
name|nBlocks
return|;
block|}
comment|/**     * Creates the required number of files with one block each    * @param nCount Number of INodes to create    * @return Array of INode files    */
DECL|method|createINodeFiles (int nCount, String fileNamePrefix)
specifier|private
name|INodeFile
index|[]
name|createINodeFiles
parameter_list|(
name|int
name|nCount
parameter_list|,
name|String
name|fileNamePrefix
parameter_list|)
block|{
if|if
condition|(
name|nCount
operator|<=
literal|0
condition|)
return|return
operator|new
name|INodeFile
index|[
literal|1
index|]
return|;
name|replication
operator|=
literal|3
expr_stmt|;
name|preferredBlockSize
operator|=
literal|128
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
name|INodeFile
index|[]
name|iNodes
init|=
operator|new
name|INodeFile
index|[
name|nCount
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
name|nCount
condition|;
name|i
operator|++
control|)
block|{
name|PermissionStatus
name|perms
init|=
operator|new
name|PermissionStatus
argument_list|(
name|userName
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
name|iNodes
index|[
name|i
index|]
operator|=
operator|new
name|INodeFile
argument_list|(
name|i
argument_list|,
name|perms
argument_list|,
literal|null
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|preferredBlockSize
argument_list|)
expr_stmt|;
name|iNodes
index|[
name|i
index|]
operator|.
name|setLocalName
argument_list|(
name|fileNamePrefix
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|BlockInfo
name|newblock
init|=
operator|new
name|BlockInfo
argument_list|(
name|replication
argument_list|)
decl_stmt|;
name|iNodes
index|[
name|i
index|]
operator|.
name|addBlock
argument_list|(
name|newblock
argument_list|)
expr_stmt|;
block|}
return|return
name|iNodes
return|;
block|}
comment|/**    * Test for the static {@link INodeFile#valueOf(INode, String)}    * and {@link INodeFileUnderConstruction#valueOf(INode, String)} methods.    * @throws IOException     */
annotation|@
name|Test
DECL|method|testValueOf ()
specifier|public
name|void
name|testValueOf
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|path
init|=
literal|"/testValueOf"
decl_stmt|;
specifier|final
name|PermissionStatus
name|perm
init|=
operator|new
name|PermissionStatus
argument_list|(
name|userName
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|short
name|replication
init|=
literal|3
decl_stmt|;
block|{
comment|//cast from null
specifier|final
name|INode
name|from
init|=
literal|null
decl_stmt|;
comment|//cast to INodeFile, should fail
try|try
block|{
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|fnfe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"File does not exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//cast to INodeFileUnderConstruction, should fail
try|try
block|{
name|INodeFileUnderConstruction
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|fnfe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"File does not exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//cast to INodeDirectory, should fail
try|try
block|{
name|INodeDirectory
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Directory does not exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|{
comment|//cast from INodeFile
specifier|final
name|INode
name|from
init|=
operator|new
name|INodeFile
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
name|perm
argument_list|,
literal|null
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|preferredBlockSize
argument_list|)
decl_stmt|;
comment|//cast to INodeFile, should success
specifier|final
name|INodeFile
name|f
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|==
name|from
argument_list|)
expr_stmt|;
comment|//cast to INodeFileUnderConstruction, should fail
try|try
block|{
name|INodeFileUnderConstruction
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"File is not under construction"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//cast to INodeDirectory, should fail
try|try
block|{
name|INodeDirectory
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathIsNotDirectoryException
name|e
parameter_list|)
block|{       }
block|}
block|{
comment|//cast from INodeFileUnderConstruction
specifier|final
name|INode
name|from
init|=
operator|new
name|INodeFileUnderConstruction
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
name|perm
argument_list|,
name|replication
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|"client"
argument_list|,
literal|"machine"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|//cast to INodeFile, should success
specifier|final
name|INodeFile
name|f
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|==
name|from
argument_list|)
expr_stmt|;
comment|//cast to INodeFileUnderConstruction, should success
specifier|final
name|INodeFileUnderConstruction
name|u
init|=
name|INodeFileUnderConstruction
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|u
operator|==
name|from
argument_list|)
expr_stmt|;
comment|//cast to INodeDirectory, should fail
try|try
block|{
name|INodeDirectory
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathIsNotDirectoryException
name|e
parameter_list|)
block|{       }
block|}
block|{
comment|//cast from INodeDirectory
specifier|final
name|INode
name|from
init|=
operator|new
name|INodeDirectory
argument_list|(
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
name|perm
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
comment|//cast to INodeFile, should fail
try|try
block|{
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|fnfe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Path is not a file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//cast to INodeFileUnderConstruction, should fail
try|try
block|{
name|INodeFileUnderConstruction
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|fnfe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Path is not a file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//cast to INodeDirectory, should success
specifier|final
name|INodeDirectory
name|d
init|=
name|INodeDirectory
operator|.
name|valueOf
argument_list|(
name|from
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|d
operator|==
name|from
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Verify root always has inode id 1001 and new formated fsimage has last    * allocated inode id 1000. Validate correct lastInodeId is persisted.    * @throws IOException    */
annotation|@
name|Test
DECL|method|testInodeId ()
specifier|public
name|void
name|testInodeId
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_DEFAULT
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
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
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|long
name|lastId
init|=
name|fsn
operator|.
name|getLastInodeId
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|lastId
operator|==
literal|1001
argument_list|)
expr_stmt|;
comment|// Create one directory and the last inode id should increase to 1002
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fsn
operator|.
name|getLastInodeId
argument_list|()
operator|==
literal|1002
argument_list|)
expr_stmt|;
comment|// Use namenode rpc to create a file
name|NamenodeProtocols
name|nnrpc
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
name|HdfsFileStatus
name|fileStatus
init|=
name|nnrpc
operator|.
name|create
argument_list|(
literal|"/test1/file"
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
argument_list|,
literal|"client"
argument_list|,
operator|new
name|EnumSetWritable
argument_list|<
name|CreateFlag
argument_list|>
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|128
operator|*
literal|1024
operator|*
literal|1024L
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fsn
operator|.
name|getLastInodeId
argument_list|()
operator|==
literal|1003
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileStatus
operator|.
name|getFileId
argument_list|()
operator|==
literal|1003
argument_list|)
expr_stmt|;
comment|// Rename doesn't increase inode id
name|Path
name|renamedPath
init|=
operator|new
name|Path
argument_list|(
literal|"/test2"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|path
argument_list|,
name|renamedPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fsn
operator|.
name|getLastInodeId
argument_list|()
operator|==
literal|1003
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Make sure empty editlog can be handled
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fsn
operator|.
name|getLastInodeId
argument_list|()
operator|==
literal|1003
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
DECL|method|testWriteToRenamedFile ()
specifier|public
name|void
name|testWriteToRenamedFile
parameter_list|()
throws|throws
name|IOException
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
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|conf
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
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
comment|// Create one file
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/test1/file"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|fos
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
comment|// Rename /test1 to test2, and recreate /test1/file
name|Path
name|renamedPath
init|=
operator|new
name|Path
argument_list|(
literal|"/test2"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|path
argument_list|,
name|renamedPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|// Add new block should fail since /test1/file has a different fileId
try|try
block|{
name|fos
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// make sure addBlock() request gets to NN immediately
name|fos
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Write should fail after rename"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|/* Ignore */
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

