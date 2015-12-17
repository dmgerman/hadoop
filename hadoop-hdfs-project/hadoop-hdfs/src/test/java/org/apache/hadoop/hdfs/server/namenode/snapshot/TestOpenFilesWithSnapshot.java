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
name|DFSOutputStream
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
name|client
operator|.
name|HdfsDataOutputStream
operator|.
name|SyncFlag
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
name|server
operator|.
name|namenode
operator|.
name|NameNode
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

begin_class
DECL|class|TestOpenFilesWithSnapshot
specifier|public
class|class
name|TestOpenFilesWithSnapshot
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|fs
name|DistributedFileSystem
name|fs
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.blocksize"
argument_list|,
literal|"1048576"
argument_list|)
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
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUCFileDeleteWithSnapShot ()
specifier|public
name|void
name|testUCFileDeleteWithSnapShot
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|doWriteAndAbort
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|// delete files separately
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/test/test2"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/test/test3"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParentDirWithUCFileDeleteWithSnapShot ()
specifier|public
name|void
name|testParentDirWithUCFileDeleteWithSnapShot
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|doWriteAndAbort
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|// delete parent directory
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/test"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithCheckpoint ()
specifier|public
name|void
name|testWithCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|doWriteAndAbort
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/test"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|nameNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|saveNamespace
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|leaveSafeMode
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// read snapshot file after restart
name|String
name|test2snapshotPath
init|=
name|Snapshot
operator|.
name|getSnapshotPath
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|"s1/test/test2"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|test2snapshotPath
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|test3snapshotPath
init|=
name|Snapshot
operator|.
name|getSnapshotPath
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|"s1/test/test3"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|test3snapshotPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFilesDeletionWithCheckpoint ()
specifier|public
name|void
name|testFilesDeletionWithCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|doWriteAndAbort
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/test/test2"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/test/test3"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|nameNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|saveNamespace
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|leaveSafeMode
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// read snapshot file after restart
name|String
name|test2snapshotPath
init|=
name|Snapshot
operator|.
name|getSnapshotPath
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|"s1/test/test2"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|test2snapshotPath
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|test3snapshotPath
init|=
name|Snapshot
operator|.
name|getSnapshotPath
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|"s1/test/test3"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|test3snapshotPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doWriteAndAbort (DistributedFileSystem fs, Path path)
specifier|private
name|void
name|doWriteAndAbort
parameter_list|(
name|DistributedFileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|fs
operator|.
name|allowSnapshot
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/test/test1"
argument_list|)
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|100024L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/test/test2"
argument_list|)
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|100024L
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/test/test/test2"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|file
argument_list|)
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
literal|1048576
condition|)
block|{
name|out
operator|.
name|writeBytes
argument_list|(
literal|"hell"
argument_list|)
expr_stmt|;
name|count
operator|+=
literal|4
expr_stmt|;
block|}
block|}
operator|(
operator|(
name|DFSOutputStream
operator|)
name|out
operator|.
name|getWrappedStream
argument_list|()
operator|)
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|abortStream
argument_list|(
operator|(
name|DFSOutputStream
operator|)
name|out
operator|.
name|getWrappedStream
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
literal|"/test/test/test3"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out2
init|=
name|fs
operator|.
name|create
argument_list|(
name|file2
argument_list|)
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
literal|1048576
condition|)
block|{
name|out2
operator|.
name|writeBytes
argument_list|(
literal|"hell"
argument_list|)
expr_stmt|;
name|count
operator|+=
literal|4
expr_stmt|;
block|}
block|}
operator|(
operator|(
name|DFSOutputStream
operator|)
name|out2
operator|.
name|getWrappedStream
argument_list|()
operator|)
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|abortStream
argument_list|(
operator|(
name|DFSOutputStream
operator|)
name|out2
operator|.
name|getWrappedStream
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|createSnapshot
argument_list|(
name|path
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenFilesWithMultipleSnapshots ()
specifier|public
name|void
name|testOpenFilesWithMultipleSnapshots
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMultipleSnapshots
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenFilesWithMultipleSnapshotsWithoutCheckpoint ()
specifier|public
name|void
name|testOpenFilesWithMultipleSnapshotsWithoutCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMultipleSnapshots
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestMultipleSnapshots (boolean saveNamespace)
specifier|private
name|void
name|doTestMultipleSnapshots
parameter_list|(
name|boolean
name|saveNamespace
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|doWriteAndAbort
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|fs
operator|.
name|createSnapshot
argument_list|(
name|path
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/test"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|deleteSnapshot
argument_list|(
name|path
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
if|if
condition|(
name|saveNamespace
condition|)
block|{
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|nameNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|saveNamespace
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|leaveSafeMode
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenFilesWithRename ()
specifier|public
name|void
name|testOpenFilesWithRename
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|doWriteAndAbort
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|// check for zero sized blocks
name|Path
name|fileWithEmptyBlock
init|=
operator|new
name|Path
argument_list|(
literal|"/test/test/test4"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|fileWithEmptyBlock
argument_list|)
expr_stmt|;
name|NamenodeProtocols
name|nameNodeRpc
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
name|String
name|clientName
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getClientName
argument_list|()
decl_stmt|;
comment|// create one empty block
name|nameNodeRpc
operator|.
name|addBlock
argument_list|(
name|fileWithEmptyBlock
operator|.
name|toString
argument_list|()
argument_list|,
name|clientName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|HdfsConstants
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fs
operator|.
name|createSnapshot
argument_list|(
name|path
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/test"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/test/test-renamed"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/test-renamed"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|nameNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|saveNamespace
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|leaveSafeMode
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

