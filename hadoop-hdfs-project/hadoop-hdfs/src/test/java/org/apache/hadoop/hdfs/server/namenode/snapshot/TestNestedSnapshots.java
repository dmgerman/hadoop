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
name|DirectorySnapshottableFeature
operator|.
name|SNAPSHOT_LIMIT
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
name|IOException
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|UnresolvedLinkException
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
name|DFSUtil
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
name|NSQuotaExceededException
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
name|EditLogFileOutputStream
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
name|ipc
operator|.
name|RemoteException
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

begin_comment
comment|/** Testing nested snapshots. */
end_comment

begin_class
DECL|class|TestNestedSnapshots
specifier|public
class|class
name|TestNestedSnapshots
block|{
static|static
block|{
comment|// These tests generate a large number of edits, and repeated edit log
comment|// flushes can be a bottleneck.
name|EditLogFileOutputStream
operator|.
name|setShouldSkipFsyncForTesting
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|{
name|SnapshotTestHelper
operator|.
name|disableLogs
parameter_list|()
constructor_decl|;
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
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|(
name|SEED
argument_list|)
decl_stmt|;
DECL|field|REPLICATION
specifier|private
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
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
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
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
block|}
block|}
comment|/**    * Create a snapshot for /test/foo and create another snapshot for    * /test/foo/bar.  Files created before the snapshots should appear in both    * snapshots and the files created after the snapshots should not appear in    * any of the snapshots.      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testNestedSnapshots ()
specifier|public
name|void
name|testNestedSnapshots
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSnapshotManager
argument_list|()
operator|.
name|setAllowNestedSnapshots
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/testNestedSnapshots/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
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
name|SEED
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"create file "
operator|+
name|file1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|s1name
init|=
literal|"foo-s1"
decl_stmt|;
specifier|final
name|Path
name|s1path
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotRoot
argument_list|(
name|foo
argument_list|,
name|s1name
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"allow snapshot "
operator|+
name|foo
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|foo
argument_list|,
name|s1name
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"create snapshot "
operator|+
name|s1name
argument_list|)
expr_stmt|;
specifier|final
name|String
name|s2name
init|=
literal|"bar-s2"
decl_stmt|;
specifier|final
name|Path
name|s2path
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotRoot
argument_list|(
name|bar
argument_list|,
name|s2name
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"allow snapshot "
operator|+
name|bar
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|bar
argument_list|,
name|s2name
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"create snapshot "
operator|+
name|s2name
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file2
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"create file "
operator|+
name|file2
argument_list|)
expr_stmt|;
name|assertFile
argument_list|(
name|s1path
argument_list|,
name|s2path
argument_list|,
name|file1
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFile
argument_list|(
name|s1path
argument_list|,
name|s2path
argument_list|,
name|file2
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//test root
specifier|final
name|String
name|rootStr
init|=
literal|"/"
decl_stmt|;
specifier|final
name|Path
name|rootPath
init|=
operator|new
name|Path
argument_list|(
name|rootStr
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|rootPath
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"allow snapshot "
operator|+
name|rootStr
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|rootSnapshot
init|=
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|rootPath
argument_list|)
decl_stmt|;
name|print
argument_list|(
literal|"create snapshot "
operator|+
name|rootSnapshot
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|deleteSnapshot
argument_list|(
name|rootPath
argument_list|,
name|rootSnapshot
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"delete snapshot "
operator|+
name|rootSnapshot
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|disallowSnapshot
argument_list|(
name|rootPath
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"disallow snapshot "
operator|+
name|rootStr
argument_list|)
expr_stmt|;
comment|//change foo to non-snapshottable
name|hdfs
operator|.
name|deleteSnapshot
argument_list|(
name|foo
argument_list|,
name|s1name
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|disallowSnapshot
argument_list|(
name|foo
argument_list|)
expr_stmt|;
comment|//test disallow nested snapshots
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSnapshotManager
argument_list|()
operator|.
name|setAllowNestedSnapshots
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|rootPath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SnapshotException
name|se
parameter_list|)
block|{
name|assertNestedSnapshotException
argument_list|(
name|se
argument_list|,
literal|"subdirectory"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SnapshotException
name|se
parameter_list|)
block|{
name|assertNestedSnapshotException
argument_list|(
name|se
argument_list|,
literal|"subdirectory"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Path
name|sub1Bar
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|sub2Bar
init|=
operator|new
name|Path
argument_list|(
name|sub1Bar
argument_list|,
literal|"sub2"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|sub2Bar
argument_list|)
expr_stmt|;
try|try
block|{
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|sub1Bar
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SnapshotException
name|se
parameter_list|)
block|{
name|assertNestedSnapshotException
argument_list|(
name|se
argument_list|,
literal|"ancestor"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|sub2Bar
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SnapshotException
name|se
parameter_list|)
block|{
name|assertNestedSnapshotException
argument_list|(
name|se
argument_list|,
literal|"ancestor"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertNestedSnapshotException (SnapshotException se, String substring)
specifier|static
name|void
name|assertNestedSnapshotException
parameter_list|(
name|SnapshotException
name|se
parameter_list|,
name|String
name|substring
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|se
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Nested snapshottable directories not allowed"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|se
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|substring
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|print (String message)
specifier|private
specifier|static
name|void
name|print
parameter_list|(
name|String
name|message
parameter_list|)
throws|throws
name|UnresolvedLinkException
block|{
name|SnapshotTestHelper
operator|.
name|dumpTree
argument_list|(
name|message
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
block|}
DECL|method|assertFile (Path s1, Path s2, Path file, Boolean... expected)
specifier|private
specifier|static
name|void
name|assertFile
parameter_list|(
name|Path
name|s1
parameter_list|,
name|Path
name|s2
parameter_list|,
name|Path
name|file
parameter_list|,
name|Boolean
modifier|...
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
index|[]
name|paths
init|=
block|{
name|file
block|,
operator|new
name|Path
argument_list|(
name|s1
argument_list|,
literal|"bar/"
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
block|,
operator|new
name|Path
argument_list|(
name|s2
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|paths
operator|.
name|length
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
name|paths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|boolean
name|computed
init|=
name|hdfs
operator|.
name|exists
argument_list|(
name|paths
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed on "
operator|+
name|paths
index|[
name|i
index|]
argument_list|,
name|expected
index|[
name|i
index|]
argument_list|,
name|computed
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test the snapshot limit of a single snapshottable directory.    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testSnapshotLimit ()
specifier|public
name|void
name|testSnapshotLimit
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|step
init|=
literal|1000
decl_stmt|;
specifier|final
name|String
name|dirStr
init|=
literal|"/testSnapshotLimit/dir"
decl_stmt|;
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|dirStr
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|int
name|s
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|s
operator|<
name|SNAPSHOT_LIMIT
condition|;
name|s
operator|++
control|)
block|{
specifier|final
name|String
name|snapshotName
init|=
literal|"s"
operator|+
name|s
decl_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
comment|//create a file occasionally
if|if
condition|(
name|s
operator|%
name|step
operator|==
literal|0
condition|)
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|dirStr
argument_list|,
literal|"f"
operator|+
name|s
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
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s"
operator|+
name|s
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected to fail to create snapshot, but didn't."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|SnapshotTestHelper
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"The exception is expected."
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|f
init|=
literal|0
init|;
name|f
operator|<
name|SNAPSHOT_LIMIT
condition|;
name|f
operator|+=
name|step
control|)
block|{
specifier|final
name|String
name|file
init|=
literal|"f"
operator|+
name|f
decl_stmt|;
name|s
operator|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|step
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|s
operator|<
name|SNAPSHOT_LIMIT
condition|;
name|s
operator|+=
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|step
argument_list|)
control|)
block|{
specifier|final
name|Path
name|p
init|=
name|SnapshotTestHelper
operator|.
name|getSnapshotPath
argument_list|(
name|dir
argument_list|,
literal|"s"
operator|+
name|s
argument_list|,
name|file
argument_list|)
decl_stmt|;
comment|//the file #f exists in snapshot #s iff s> f.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s
operator|>
name|f
argument_list|,
name|hdfs
operator|.
name|exists
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testSnapshotWithQuota ()
specifier|public
name|void
name|testSnapshotWithQuota
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|dirStr
init|=
literal|"/testSnapshotWithQuota/dir"
decl_stmt|;
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|dirStr
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// set namespace quota
specifier|final
name|int
name|NS_QUOTA
init|=
literal|6
decl_stmt|;
name|hdfs
operator|.
name|setQuota
argument_list|(
name|dir
argument_list|,
name|NS_QUOTA
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
comment|// create object to use up the quota.
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|f1
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"f1"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|f1
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
block|{
comment|//create a snapshot with default snapshot name
specifier|final
name|Path
name|snapshotPath
init|=
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|//check snapshot path and the default snapshot name
specifier|final
name|String
name|snapshotName
init|=
name|snapshotPath
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"snapshotName="
operator|+
name|snapshotName
argument_list|,
name|Pattern
operator|.
name|matches
argument_list|(
literal|"s\\d\\d\\d\\d\\d\\d\\d\\d-\\d\\d\\d\\d\\d\\d\\.\\d\\d\\d"
argument_list|,
name|snapshotName
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|parent
init|=
name|snapshotPath
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
argument_list|,
name|parent
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|dir
argument_list|,
name|parent
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Path
name|f2
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"f2"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|f2
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
try|try
block|{
comment|// normal create file should fail with quota
specifier|final
name|Path
name|f3
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"f3"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|f3
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NSQuotaExceededException
name|e
parameter_list|)
block|{
name|SnapshotTestHelper
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"The exception is expected."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// createSnapshot should fail with quota
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NSQuotaExceededException
name|e
parameter_list|)
block|{
name|SnapshotTestHelper
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"The exception is expected."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// setPermission f1 should fail with quote since it cannot add diff.
name|hdfs
operator|.
name|setPermission
argument_list|(
name|f1
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertSame
argument_list|(
name|NSQuotaExceededException
operator|.
name|class
argument_list|,
name|e
operator|.
name|unwrapRemoteException
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"The exception is expected."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// setPermission f2 since it was created after the snapshot
name|hdfs
operator|.
name|setPermission
argument_list|(
name|f2
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// increase quota and retry the commands.
name|hdfs
operator|.
name|setQuota
argument_list|(
name|dir
argument_list|,
name|NS_QUOTA
operator|+
literal|2
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|dir
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|setPermission
argument_list|(
name|foo
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0444
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test {@link Snapshot#ID_COMPARATOR}.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testIdCmp ()
specifier|public
name|void
name|testIdCmp
parameter_list|()
block|{
specifier|final
name|PermissionStatus
name|perm
init|=
name|PermissionStatus
operator|.
name|createImmutable
argument_list|(
literal|"user"
argument_list|,
literal|"group"
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|INodeDirectory
name|snapshottable
init|=
operator|new
name|INodeDirectory
argument_list|(
literal|0
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|perm
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|snapshottable
operator|.
name|addSnapshottableFeature
argument_list|()
expr_stmt|;
specifier|final
name|Snapshot
index|[]
name|snapshots
init|=
block|{
operator|new
name|Snapshot
argument_list|(
literal|1
argument_list|,
literal|"s1"
argument_list|,
name|snapshottable
argument_list|)
block|,
operator|new
name|Snapshot
argument_list|(
literal|1
argument_list|,
literal|"s1"
argument_list|,
name|snapshottable
argument_list|)
block|,
operator|new
name|Snapshot
argument_list|(
literal|2
argument_list|,
literal|"s2"
argument_list|,
name|snapshottable
argument_list|)
block|,
operator|new
name|Snapshot
argument_list|(
literal|2
argument_list|,
literal|"s2"
argument_list|,
name|snapshottable
argument_list|)
block|,     }
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Snapshot
operator|.
name|ID_COMPARATOR
operator|.
name|compare
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Snapshot
name|s
range|:
name|snapshots
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Snapshot
operator|.
name|ID_COMPARATOR
operator|.
name|compare
argument_list|(
literal|null
argument_list|,
name|s
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Snapshot
operator|.
name|ID_COMPARATOR
operator|.
name|compare
argument_list|(
name|s
argument_list|,
literal|null
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|Snapshot
name|t
range|:
name|snapshots
control|)
block|{
specifier|final
name|int
name|expected
init|=
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|getLocalName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|t
operator|.
name|getRoot
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|computed
init|=
name|Snapshot
operator|.
name|ID_COMPARATOR
operator|.
name|compare
argument_list|(
name|s
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
operator|>
literal|0
argument_list|,
name|computed
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
operator|==
literal|0
argument_list|,
name|computed
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
operator|<
literal|0
argument_list|,
name|computed
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * When we have nested snapshottable directories and if we try to reset the    * snapshottable descendant back to an regular directory, we need to replace    * the snapshottable descendant with an INodeDirectoryWithSnapshot    */
annotation|@
name|Test
DECL|method|testDisallowNestedSnapshottableDir ()
specifier|public
name|void
name|testDisallowNestedSnapshottableDir
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSnapshotManager
argument_list|()
operator|.
name|setAllowNestedSnapshots
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|sub
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|hdfs
argument_list|,
name|dir
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|sub
argument_list|,
literal|"file"
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
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
name|FSDirectory
name|fsdir
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
name|INode
name|subNode
init|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|sub
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|subNode
operator|.
name|asDirectory
argument_list|()
operator|.
name|isWithSnapshot
argument_list|()
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|subNode
operator|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|sub
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|subNode
operator|.
name|isDirectory
argument_list|()
operator|&&
name|subNode
operator|.
name|asDirectory
argument_list|()
operator|.
name|isSnapshottable
argument_list|()
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|disallowSnapshot
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|subNode
operator|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|sub
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|subNode
operator|.
name|asDirectory
argument_list|()
operator|.
name|isWithSnapshot
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

