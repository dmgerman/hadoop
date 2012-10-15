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
name|*
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
block|}
end_class

end_unit

