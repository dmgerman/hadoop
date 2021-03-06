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
name|assertFalse
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
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|FsShell
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
name|SnapshotException
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
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|RemoteException
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_comment
comment|/**  * Test for renaming snapshot  */
end_comment

begin_class
DECL|class|TestSnapshotRename
specifier|public
class|class
name|TestSnapshotRename
block|{
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0
decl_stmt|;
DECL|field|REPLICATION
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|BLOCKSIZE
specifier|static
specifier|final
name|long
name|BLOCKSIZE
init|=
literal|1024
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/TestSnapshot"
argument_list|)
decl_stmt|;
DECL|field|sub1
specifier|private
specifier|final
name|Path
name|sub1
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
DECL|field|file1
specifier|private
specifier|final
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fsn
name|FSNamesystem
name|fsn
decl_stmt|;
DECL|field|hdfs
name|DistributedFileSystem
name|hdfs
decl_stmt|;
DECL|field|fsdir
name|FSDirectory
name|fsdir
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
name|Configuration
argument_list|()
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
name|REPLICATION
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
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|fsdir
operator|=
name|fsn
operator|.
name|getFSDirectory
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
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
comment|/**    * Check the correctness of snapshot list within snapshottable dir    */
DECL|method|checkSnapshotList (INodeDirectory srcRoot, String[] sortedNames, String[] names)
specifier|private
name|void
name|checkSnapshotList
parameter_list|(
name|INodeDirectory
name|srcRoot
parameter_list|,
name|String
index|[]
name|sortedNames
parameter_list|,
name|String
index|[]
name|names
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|srcRoot
operator|.
name|isSnapshottable
argument_list|()
argument_list|)
expr_stmt|;
name|ReadOnlyList
argument_list|<
name|Snapshot
argument_list|>
name|listByName
init|=
name|srcRoot
operator|.
name|getDirectorySnapshottableFeature
argument_list|()
operator|.
name|getSnapshotList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|sortedNames
operator|.
name|length
argument_list|,
name|listByName
operator|.
name|size
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
name|listByName
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|sortedNames
index|[
name|i
index|]
argument_list|,
name|listByName
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getRoot
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DiffList
argument_list|<
name|DirectoryDiff
argument_list|>
name|listByTime
init|=
name|srcRoot
operator|.
name|getDiffs
argument_list|()
operator|.
name|asList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|names
operator|.
name|length
argument_list|,
name|listByTime
operator|.
name|size
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
name|listByTime
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Snapshot
name|s
init|=
name|srcRoot
operator|.
name|getDirectorySnapshottableFeature
argument_list|()
operator|.
name|getSnapshotById
argument_list|(
name|listByTime
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getSnapshotId
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|names
index|[
name|i
index|]
argument_list|,
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Rename snapshot(s), and check the correctness of the snapshot list within    * {@link INodeDirectorySnapshottable}    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSnapshotList ()
specifier|public
name|void
name|testSnapshotList
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// Create three snapshots for sub1
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
literal|"s3"
argument_list|)
expr_stmt|;
comment|// Rename s3 to s22
name|hdfs
operator|.
name|renameSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s3"
argument_list|,
literal|"s22"
argument_list|)
expr_stmt|;
comment|// Check the snapshots list
name|INodeDirectory
name|srcRoot
init|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|sub1
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asDirectory
argument_list|()
decl_stmt|;
name|checkSnapshotList
argument_list|(
name|srcRoot
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s1"
block|,
literal|"s2"
block|,
literal|"s22"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s1"
block|,
literal|"s2"
block|,
literal|"s22"
block|}
argument_list|)
expr_stmt|;
comment|// Rename s1 to s4
name|hdfs
operator|.
name|renameSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|,
literal|"s4"
argument_list|)
expr_stmt|;
name|checkSnapshotList
argument_list|(
name|srcRoot
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s2"
block|,
literal|"s22"
block|,
literal|"s4"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s4"
block|,
literal|"s2"
block|,
literal|"s22"
block|}
argument_list|)
expr_stmt|;
comment|// Rename s22 to s0
name|hdfs
operator|.
name|renameSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s22"
argument_list|,
literal|"s0"
argument_list|)
expr_stmt|;
name|checkSnapshotList
argument_list|(
name|srcRoot
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s0"
block|,
literal|"s2"
block|,
literal|"s4"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s4"
block|,
literal|"s2"
block|,
literal|"s0"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test FileStatus of snapshot file before/after rename    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSnapshotRename ()
specifier|public
name|void
name|testSnapshotRename
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// Create snapshot for sub1
name|Path
name|snapshotRoot
init|=
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
literal|"s1"
argument_list|)
decl_stmt|;
name|Path
name|ssPath
init|=
operator|new
name|Path
argument_list|(
name|snapshotRoot
argument_list|,
name|file1
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|exists
argument_list|(
name|ssPath
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|statusBeforeRename
init|=
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|ssPath
argument_list|)
decl_stmt|;
comment|// Rename the snapshot
name|hdfs
operator|.
name|renameSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
comment|//<sub1>/.snapshot/s1/file1 should no longer exist
name|assertFalse
argument_list|(
name|hdfs
operator|.
name|exists
argument_list|(
name|ssPath
argument_list|)
argument_list|)
expr_stmt|;
name|snapshotRoot
operator|=
name|SnapshotTestHelper
operator|.
name|getSnapshotRoot
argument_list|(
name|sub1
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|ssPath
operator|=
operator|new
name|Path
argument_list|(
name|snapshotRoot
argument_list|,
name|file1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Instead,<sub1>/.snapshot/s2/file1 should exist
name|assertTrue
argument_list|(
name|hdfs
operator|.
name|exists
argument_list|(
name|ssPath
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|statusAfterRename
init|=
name|hdfs
operator|.
name|getFileStatus
argument_list|(
name|ssPath
argument_list|)
decl_stmt|;
comment|// FileStatus of the snapshot should not change except the path
name|assertFalse
argument_list|(
name|statusBeforeRename
operator|.
name|equals
argument_list|(
name|statusAfterRename
argument_list|)
argument_list|)
expr_stmt|;
name|statusBeforeRename
operator|.
name|setPath
argument_list|(
name|statusAfterRename
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|statusBeforeRename
operator|.
name|toString
argument_list|()
argument_list|,
name|statusAfterRename
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test rename a non-existing snapshot    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testRenameNonExistingSnapshot ()
specifier|public
name|void
name|testRenameNonExistingSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// Create snapshot for sub1
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|SnapshotException
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
name|error
init|=
literal|"The snapshot wrongName does not exist for directory "
operator|+
name|sub1
operator|.
name|toString
argument_list|()
decl_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|renameSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"wrongName"
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test rename a non-existing snapshot to itself.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testRenameNonExistingSnapshotToItself ()
specifier|public
name|void
name|testRenameNonExistingSnapshotToItself
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// Create snapshot for sub1
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|SnapshotException
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
name|error
init|=
literal|"The snapshot wrongName does not exist for directory "
operator|+
name|sub1
operator|.
name|toString
argument_list|()
decl_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|renameSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"wrongName"
argument_list|,
literal|"wrongName"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test rename a snapshot to another existing snapshot     */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testRenameToExistingSnapshot ()
specifier|public
name|void
name|testRenameToExistingSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// Create snapshots for sub1
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|SnapshotException
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
name|error
init|=
literal|"The snapshot s2 already exists for directory "
operator|+
name|sub1
operator|.
name|toString
argument_list|()
decl_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|renameSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test renaming a snapshot with illegal name    */
annotation|@
name|Test
DECL|method|testRenameWithIllegalName ()
specifier|public
name|void
name|testRenameWithIllegalName
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// Create snapshots for sub1
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|sub1
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name1
init|=
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
decl_stmt|;
try|try
block|{
name|hdfs
operator|.
name|renameSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|,
name|name1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected when an illegal name is given for rename"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
name|String
name|errorMsg
init|=
literal|"\""
operator|+
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
operator|+
literal|"\" is a reserved name."
decl_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
name|errorMsg
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|String
name|errorMsg
init|=
literal|"Snapshot name cannot contain \""
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"\""
decl_stmt|;
specifier|final
name|String
index|[]
name|badNames
init|=
operator|new
name|String
index|[]
block|{
literal|"foo"
operator|+
name|Path
operator|.
name|SEPARATOR
block|,
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"foo"
block|,
name|Path
operator|.
name|SEPARATOR
block|,
literal|"foo"
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"bar"
block|}
decl_stmt|;
for|for
control|(
name|String
name|badName
range|:
name|badNames
control|)
block|{
try|try
block|{
name|hdfs
operator|.
name|renameSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|,
name|badName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected when an illegal name is given"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
name|errorMsg
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testRenameSnapshotCommandWithIllegalArguments ()
specifier|public
name|void
name|testRenameSnapshotCommandWithIllegalArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|psOut
init|=
operator|new
name|PrintStream
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|PrintStream
name|oldOut
init|=
name|System
operator|.
name|out
decl_stmt|;
name|PrintStream
name|oldErr
init|=
name|System
operator|.
name|err
decl_stmt|;
try|try
block|{
name|System
operator|.
name|setOut
argument_list|(
name|psOut
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|psOut
argument_list|)
expr_stmt|;
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|()
decl_stmt|;
name|shell
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
index|[]
name|argv1
init|=
block|{
literal|"-renameSnapshot"
block|,
literal|"/tmp"
block|,
literal|"s1"
block|}
decl_stmt|;
name|int
name|val
init|=
name|shell
operator|.
name|run
argument_list|(
name|argv1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|val
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|argv1
index|[
literal|0
index|]
operator|+
literal|": Incorrect number of arguments."
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|argv2
init|=
block|{
literal|"-renameSnapshot"
block|,
literal|"/tmp"
block|,
literal|"s1"
block|,
literal|"s2"
block|,
literal|"s3"
block|}
decl_stmt|;
name|val
operator|=
name|shell
operator|.
name|run
argument_list|(
name|argv2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|val
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|out
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|argv2
index|[
literal|0
index|]
operator|+
literal|": Incorrect number of arguments."
argument_list|)
argument_list|)
expr_stmt|;
name|psOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|oldOut
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|oldErr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

