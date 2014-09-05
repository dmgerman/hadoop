begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|Path
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
name|AfterClass
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
name|BeforeClass
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
comment|/**  * This class includes end-to-end tests for snapshot related FsShell and  * DFSAdmin commands.  */
end_comment

begin_class
DECL|class|TestSnapshotCommands
specifier|public
class|class
name|TestSnapshotCommands
block|{
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|DistributedFileSystem
name|fs
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|clusterSetUp ()
specifier|public
specifier|static
name|void
name|clusterSetUp
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
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
name|AfterClass
DECL|method|clusterShutdown ()
specifier|public
specifier|static
name|void
name|clusterShutdown
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
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/sub1"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|allowSnapshot
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/sub1"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/sub1/sub1sub1"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/sub1/sub1sub2"
argument_list|)
argument_list|)
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
name|IOException
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/sub1"
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/sub1/.snapshot"
argument_list|)
argument_list|)
condition|)
block|{
for|for
control|(
name|FileStatus
name|st
range|:
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/sub1/.snapshot"
argument_list|)
argument_list|)
control|)
block|{
name|fs
operator|.
name|deleteSnapshot
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/sub1"
argument_list|)
argument_list|,
name|st
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|disallowSnapshot
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/sub1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/sub1"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAllowSnapshot ()
specifier|public
name|void
name|testAllowSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Idempotent test
name|DFSTestUtil
operator|.
name|DFSAdminRun
argument_list|(
literal|"-allowSnapshot /sub1"
argument_list|,
literal|0
argument_list|,
literal|"Allowing snaphot on /sub1 succeeded"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// allow normal dir success
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-mkdir /sub2"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|DFSAdminRun
argument_list|(
literal|"-allowSnapshot /sub2"
argument_list|,
literal|0
argument_list|,
literal|"Allowing snaphot on /sub2 succeeded"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// allow non-exists dir failed
name|DFSTestUtil
operator|.
name|DFSAdminRun
argument_list|(
literal|"-allowSnapshot /sub3"
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateSnapshot ()
specifier|public
name|void
name|testCreateSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test createSnapshot
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-createSnapshot /sub1 sn0"
argument_list|,
literal|0
argument_list|,
literal|"Created snapshot /sub1/.snapshot/sn0"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-createSnapshot /sub1 sn0"
argument_list|,
literal|1
argument_list|,
literal|"there is already a snapshot with the same name \"sn0\""
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-rmr /sub1/sub1sub2"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-mkdir /sub1/sub1sub3"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-createSnapshot /sub1 sn1"
argument_list|,
literal|0
argument_list|,
literal|"Created snapshot /sub1/.snapshot/sn1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// check snapshot contents
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/sub1sub1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/sub1sub3"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1/.snapshot"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/.snapshot/sn0"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1/.snapshot"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/.snapshot/sn1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1/.snapshot/sn0"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/.snapshot/sn0/sub1sub1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1/.snapshot/sn0"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/.snapshot/sn0/sub1sub2"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1/.snapshot/sn1"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/.snapshot/sn1/sub1sub1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1/.snapshot/sn1"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/.snapshot/sn1/sub1sub3"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirUsingReservedName ()
specifier|public
name|void
name|testMkdirUsingReservedName
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test can not create dir with reserved name: .snapshot
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-mkdir /.snapshot"
argument_list|,
literal|1
argument_list|,
literal|"File exists"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-mkdir /sub1/.snapshot"
argument_list|,
literal|1
argument_list|,
literal|"File exists"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// mkdir -p ignore reserved name check if dir already exists
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-mkdir -p /sub1/.snapshot"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-mkdir -p /sub1/sub1sub1/.snapshot"
argument_list|,
literal|1
argument_list|,
literal|"mkdir: \".snapshot\" is a reserved name."
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameSnapshot ()
specifier|public
name|void
name|testRenameSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-createSnapshot /sub1 sn.orig"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-renameSnapshot /sub1 sn.orig sn.rename"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1/.snapshot"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/.snapshot/sn.rename"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1/.snapshot/sn.rename"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/.snapshot/sn.rename/sub1sub1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-ls /sub1/.snapshot/sn.rename"
argument_list|,
literal|0
argument_list|,
literal|"/sub1/.snapshot/sn.rename/sub1sub2"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|//try renaming from a non-existing snapshot
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-renameSnapshot /sub1 sn.nonexist sn.rename"
argument_list|,
literal|1
argument_list|,
literal|"renameSnapshot: The snapshot sn.nonexist does not exist for directory /sub1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|//try renaming to existing snapshots
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-createSnapshot /sub1 sn.new"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-renameSnapshot /sub1 sn.new sn.rename"
argument_list|,
literal|1
argument_list|,
literal|"renameSnapshot: The snapshot sn.rename already exists for directory /sub1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-renameSnapshot /sub1 sn.rename sn.new"
argument_list|,
literal|1
argument_list|,
literal|"renameSnapshot: The snapshot sn.new already exists for directory /sub1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteSnapshot ()
specifier|public
name|void
name|testDeleteSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-createSnapshot /sub1 sn1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-deleteSnapshot /sub1 sn1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-deleteSnapshot /sub1 sn1"
argument_list|,
literal|1
argument_list|,
literal|"deleteSnapshot: Cannot delete snapshot sn1 from path /sub1: the snapshot does not exist."
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDisallowSnapshot ()
specifier|public
name|void
name|testDisallowSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-createSnapshot /sub1 sn1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// cannot delete snapshotable dir
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-rmr /sub1"
argument_list|,
literal|1
argument_list|,
literal|"The directory /sub1 cannot be deleted since /sub1 is snapshottable and already has snapshots"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|DFSAdminRun
argument_list|(
literal|"-disallowSnapshot /sub1"
argument_list|,
operator|-
literal|1
argument_list|,
literal|"disallowSnapshot: The directory /sub1 has snapshot(s). Please redo the operation after removing all the snapshots."
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-deleteSnapshot /sub1 sn1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|DFSAdminRun
argument_list|(
literal|"-disallowSnapshot /sub1"
argument_list|,
literal|0
argument_list|,
literal|"Disallowing snaphot on /sub1 succeeded"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Idempotent test
name|DFSTestUtil
operator|.
name|DFSAdminRun
argument_list|(
literal|"-disallowSnapshot /sub1"
argument_list|,
literal|0
argument_list|,
literal|"Disallowing snaphot on /sub1 succeeded"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// now it can be deleted
name|DFSTestUtil
operator|.
name|FsShellRun
argument_list|(
literal|"-rmr /sub1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

