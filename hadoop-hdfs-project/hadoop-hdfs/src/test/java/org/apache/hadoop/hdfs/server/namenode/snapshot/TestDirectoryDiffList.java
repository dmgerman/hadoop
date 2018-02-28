begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
name|snapshot
package|;
end_package

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
name|INode
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
name|INodeDirectory
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
name|snapshot
operator|.
name|DirectoryWithSnapshotFeature
operator|.
name|DirectoryDiff
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
name|util
operator|.
name|ReadOnlyList
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
name|Assert
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * This class tests the DirectoryDiffList API's.  */
end_comment

begin_class
DECL|class|TestDirectoryDiffList
specifier|public
class|class
name|TestDirectoryDiffList
block|{
static|static
block|{
name|SnapshotTestHelper
operator|.
name|disableLogs
argument_list|()
expr_stmt|;
block|}
DECL|field|SEED
specifier|private
specifier|static
specifier|final
name|long
name|SEED
init|=
literal|0
decl_stmt|;
DECL|field|REPL
specifier|private
specifier|static
specifier|final
name|short
name|REPL
init|=
literal|3
decl_stmt|;
DECL|field|REPL_2
specifier|private
specifier|static
specifier|final
name|short
name|REPL_2
init|=
literal|1
decl_stmt|;
DECL|field|BLOCKSIZE
specifier|private
specifier|static
specifier|final
name|long
name|BLOCKSIZE
init|=
literal|1024
decl_stmt|;
DECL|field|CONF
specifier|private
specifier|static
specifier|final
name|Configuration
name|CONF
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fsn
specifier|private
specifier|static
name|FSNamesystem
name|fsn
decl_stmt|;
DECL|field|fsdir
specifier|private
specifier|static
name|FSDirectory
name|fsdir
decl_stmt|;
DECL|field|hdfs
specifier|private
specifier|static
name|DistributedFileSystem
name|hdfs
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
name|CONF
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
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
name|numDataNodes
argument_list|(
name|REPL
argument_list|)
operator|.
name|format
argument_list|(
literal|true
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|fsdir
operator|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
expr_stmt|;
name|hdfs
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|compareChildrenList (ReadOnlyList<INode> list1, ReadOnlyList<INode> list2)
specifier|private
name|void
name|compareChildrenList
parameter_list|(
name|ReadOnlyList
argument_list|<
name|INode
argument_list|>
name|list1
parameter_list|,
name|ReadOnlyList
argument_list|<
name|INode
argument_list|>
name|list2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|list1
operator|.
name|size
argument_list|()
argument_list|,
name|list2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|list1
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|list1
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|,
name|list2
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyChildrenListForAllSnapshots (DirectoryDiffList list, INodeDirectory dir)
specifier|private
name|void
name|verifyChildrenListForAllSnapshots
parameter_list|(
name|DirectoryDiffList
name|list
parameter_list|,
name|INodeDirectory
name|dir
parameter_list|)
block|{
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|ReadOnlyList
argument_list|<
name|INode
argument_list|>
name|list1
init|=
name|dir
operator|.
name|getChildrenList
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DirectoryWithSnapshotFeature
operator|.
name|DirectoryDiff
argument_list|>
name|subList
init|=
name|list
operator|.
name|getMinListForRange
argument_list|(
name|index
argument_list|,
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|ReadOnlyList
argument_list|<
name|INode
argument_list|>
name|list2
init|=
name|getChildrenList
argument_list|(
name|dir
argument_list|,
name|subList
argument_list|)
decl_stmt|;
name|compareChildrenList
argument_list|(
name|list1
argument_list|,
name|list2
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getChildrenList (INodeDirectory currentINode, List<DirectoryWithSnapshotFeature.DirectoryDiff> list)
specifier|private
name|ReadOnlyList
argument_list|<
name|INode
argument_list|>
name|getChildrenList
parameter_list|(
name|INodeDirectory
name|currentINode
parameter_list|,
name|List
argument_list|<
name|DirectoryWithSnapshotFeature
operator|.
name|DirectoryDiff
argument_list|>
name|list
parameter_list|)
block|{
name|List
argument_list|<
name|INode
argument_list|>
name|children
init|=
literal|null
decl_stmt|;
specifier|final
name|DirectoryWithSnapshotFeature
operator|.
name|ChildrenDiff
name|combined
init|=
operator|new
name|DirectoryWithSnapshotFeature
operator|.
name|ChildrenDiff
argument_list|()
decl_stmt|;
for|for
control|(
name|DirectoryWithSnapshotFeature
operator|.
name|DirectoryDiff
name|d
range|:
name|list
control|)
block|{
name|combined
operator|.
name|combinePosterior
argument_list|(
name|d
operator|.
name|getChildrenDiff
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|children
operator|=
name|combined
operator|.
name|apply2Current
argument_list|(
name|ReadOnlyList
operator|.
name|Util
operator|.
name|asList
argument_list|(
name|currentINode
operator|.
name|getChildrenList
argument_list|(
name|Snapshot
operator|.
name|CURRENT_STATE_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ReadOnlyList
operator|.
name|Util
operator|.
name|asReadOnlyList
argument_list|(
name|children
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testDirectoryDiffList ()
specifier|public
name|void
name|testDirectoryDiffList
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|sdir1
init|=
operator|new
name|Path
argument_list|(
literal|"/dir1"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|sdir1
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sdir1
argument_list|,
literal|"s0"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|31
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|sdir1
argument_list|,
literal|"file"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPL_2
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sdir1
argument_list|,
literal|"s"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|INodeDirectory
name|sdir1Node
init|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|sdir1
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asDirectory
argument_list|()
decl_stmt|;
name|DiffList
argument_list|<
name|DirectoryDiff
argument_list|>
name|diffs
init|=
name|sdir1Node
operator|.
name|getDiffs
argument_list|()
operator|.
name|asList
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|DirectoryDiff
argument_list|>
name|itr
init|=
name|diffs
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DirectoryDiffList
name|skipList
init|=
name|DirectoryDiffList
operator|.
name|createSkipList
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
decl_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|skipList
operator|.
name|addLast
argument_list|(
name|itr
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// verify that the both the children list obtained from hdfs and
comment|// DirectoryDiffList are same
name|verifyChildrenListForAllSnapshots
argument_list|(
name|skipList
argument_list|,
name|sdir1Node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDirectoryDiffList2 ()
specifier|public
name|void
name|testDirectoryDiffList2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|sdir1
init|=
operator|new
name|Path
argument_list|(
literal|"/dir1"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|sdir1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|31
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|sdir1
argument_list|,
literal|"file"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPL_2
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
block|}
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sdir1
argument_list|,
literal|"s0"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|31
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|sdir1
argument_list|,
literal|"file"
operator|+
operator|(
literal|31
operator|-
name|i
operator|)
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sdir1
argument_list|,
literal|"s"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|INodeDirectory
name|sdir1Node
init|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|sdir1
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asDirectory
argument_list|()
decl_stmt|;
name|DiffList
argument_list|<
name|DirectoryDiff
argument_list|>
name|diffs
init|=
name|sdir1Node
operator|.
name|getDiffs
argument_list|()
operator|.
name|asList
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|diffs
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
name|DirectoryDiffList
name|skipList
init|=
name|DirectoryDiffList
operator|.
name|createSkipList
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
decl_stmt|;
while|while
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
name|skipList
operator|.
name|addFirst
argument_list|(
name|diffs
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
name|index
operator|--
expr_stmt|;
block|}
comment|// verify that the both the children list obtained from hdfs and
comment|// DirectoryDiffList are same
name|verifyChildrenListForAllSnapshots
argument_list|(
name|skipList
argument_list|,
name|sdir1Node
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

