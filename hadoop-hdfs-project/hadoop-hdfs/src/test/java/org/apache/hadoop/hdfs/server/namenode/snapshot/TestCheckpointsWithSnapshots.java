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
name|*
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
name|io
operator|.
name|IOException
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
name|client
operator|.
name|HdfsAdmin
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
name|namenode
operator|.
name|SecondaryNameNode
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
DECL|class|TestCheckpointsWithSnapshots
specifier|public
class|class
name|TestCheckpointsWithSnapshots
block|{
DECL|field|TEST_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
static|static
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDeleteContents
argument_list|(
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Regression test for HDFS-5433 - "When reloading fsimage during    * checkpointing, we should clear existing snapshottable directories"    */
annotation|@
name|Test
DECL|method|testCheckpoint ()
specifier|public
name|void
name|testCheckpoint
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|SecondaryNameNode
name|secondary
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
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|secondary
operator|=
operator|new
name|SecondaryNameNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|SnapshotManager
name|nnSnapshotManager
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSnapshotManager
argument_list|()
decl_stmt|;
name|SnapshotManager
name|secondarySnapshotManager
init|=
name|secondary
operator|.
name|getFSNamesystem
argument_list|()
operator|.
name|getSnapshotManager
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|HdfsAdmin
name|admin
init|=
operator|new
name|HdfsAdmin
argument_list|(
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nnSnapshotManager
operator|.
name|getNumSnapshots
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nnSnapshotManager
operator|.
name|getNumSnapshottableDirs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|secondarySnapshotManager
operator|.
name|getNumSnapshots
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|secondarySnapshotManager
operator|.
name|getNumSnapshottableDirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1. Create a snapshottable directory foo on the NN.
name|fs
operator|.
name|mkdirs
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
name|admin
operator|.
name|allowSnapshot
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nnSnapshotManager
operator|.
name|getNumSnapshots
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nnSnapshotManager
operator|.
name|getNumSnapshottableDirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2. Create a snapshot of the dir foo. This will be referenced both in
comment|// the SnapshotManager as well as in the file system tree. The snapshot
comment|// count will go up to 1.
name|Path
name|snapshotPath
init|=
name|fs
operator|.
name|createSnapshot
argument_list|(
name|TEST_PATH
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nnSnapshotManager
operator|.
name|getNumSnapshots
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nnSnapshotManager
operator|.
name|getNumSnapshottableDirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// 3. Start up a 2NN and have it do a checkpoint. It will have foo and its
comment|// snapshot in its list of snapshottable dirs referenced from the
comment|// SnapshotManager, as well as in the file system tree.
name|secondary
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|secondarySnapshotManager
operator|.
name|getNumSnapshots
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|secondarySnapshotManager
operator|.
name|getNumSnapshottableDirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// 4. Disallow snapshots on and delete foo on the NN. The snapshot count
comment|// will go down to 0 and the snapshottable dir will be removed from the fs
comment|// tree.
name|fs
operator|.
name|deleteSnapshot
argument_list|(
name|TEST_PATH
argument_list|,
name|snapshotPath
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|admin
operator|.
name|disallowSnapshot
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nnSnapshotManager
operator|.
name|getNumSnapshots
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nnSnapshotManager
operator|.
name|getNumSnapshottableDirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// 5. Have the NN do a saveNamespace, writing out a new fsimage with
comment|// snapshot count 0.
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|saveNamespace
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|leaveSafeMode
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|)
expr_stmt|;
comment|// 6. Have the still-running 2NN do a checkpoint. It will notice that the
comment|// fsimage has changed on the NN and redownload/reload from that image.
comment|// This will replace all INodes in the file system tree as well as reset
comment|// the snapshot counter to 0 in the SnapshotManager. However, it will not
comment|// clear the list of snapshottable dirs referenced from the
comment|// SnapshotManager. When it writes out an fsimage, the 2NN will write out
comment|// 0 for the snapshot count, but still serialize the snapshottable dir
comment|// referenced in the SnapshotManager even though it no longer appears in
comment|// the file system tree. The NN will not be able to start up with this.
name|secondary
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|secondarySnapshotManager
operator|.
name|getNumSnapshots
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|secondarySnapshotManager
operator|.
name|getNumSnapshottableDirs
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
if|if
condition|(
name|secondary
operator|!=
literal|null
condition|)
block|{
name|secondary
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

