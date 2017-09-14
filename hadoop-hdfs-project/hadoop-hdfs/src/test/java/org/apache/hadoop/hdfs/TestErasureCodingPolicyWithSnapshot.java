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
name|assertNull
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
name|fs
operator|.
name|contract
operator|.
name|ContractTestUtils
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
name|protocol
operator|.
name|ErasureCodingPolicy
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
operator|.
name|SafeModeAction
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
name|ToolRunner
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
DECL|class|TestErasureCodingPolicyWithSnapshot
specifier|public
class|class
name|TestErasureCodingPolicyWithSnapshot
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|SUCCESS
specifier|private
specifier|final
specifier|static
name|int
name|SUCCESS
init|=
literal|0
decl_stmt|;
DECL|field|ecPolicy
specifier|private
name|ErasureCodingPolicy
name|ecPolicy
decl_stmt|;
DECL|field|groupSize
specifier|private
name|short
name|groupSize
decl_stmt|;
DECL|method|getEcPolicy ()
specifier|public
name|ErasureCodingPolicy
name|getEcPolicy
parameter_list|()
block|{
return|return
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
return|;
block|}
annotation|@
name|Before
DECL|method|setupCluster ()
specifier|public
name|void
name|setupCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|ecPolicy
operator|=
name|getEcPolicy
argument_list|()
expr_stmt|;
name|groupSize
operator|=
call|(
name|short
call|)
argument_list|(
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
operator|+
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
argument_list|)
expr_stmt|;
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
name|numDataNodes
argument_list|(
name|groupSize
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
name|fs
operator|.
name|enableErasureCodingPolicy
argument_list|(
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdownCluster ()
specifier|public
name|void
name|shutdownCluster
parameter_list|()
throws|throws
name|IOException
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
comment|/**    * Test correctness of successive snapshot creation and deletion with erasure    * coding policies. Create snapshot of ecDir's parent directory.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testSnapshotsOnErasureCodingDirsParentDir ()
specifier|public
name|void
name|testSnapshotsOnErasureCodingDirsParentDir
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|len
init|=
literal|1024
decl_stmt|;
specifier|final
name|Path
name|ecDirParent
init|=
operator|new
name|Path
argument_list|(
literal|"/parent"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|ecDir
init|=
operator|new
name|Path
argument_list|(
name|ecDirParent
argument_list|,
literal|"ecdir"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|ecFile
init|=
operator|new
name|Path
argument_list|(
name|ecDir
argument_list|,
literal|"ecfile"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|ecDir
argument_list|)
expr_stmt|;
name|fs
operator|.
name|allowSnapshot
argument_list|(
name|ecDirParent
argument_list|)
expr_stmt|;
comment|// set erasure coding policy
name|fs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|ecDir
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|ecFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
name|String
name|contents
init|=
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|ecFile
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|snap1
init|=
name|fs
operator|.
name|createSnapshot
argument_list|(
name|ecDirParent
argument_list|,
literal|"snap1"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|snap1ECDir
init|=
operator|new
name|Path
argument_list|(
name|snap1
argument_list|,
name|ecDir
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap1ECDir
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now delete the dir which has erasure coding policy. Re-create the dir again, and
comment|// take another snapshot
name|fs
operator|.
name|delete
argument_list|(
name|ecDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|ecDir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|snap2
init|=
name|fs
operator|.
name|createSnapshot
argument_list|(
name|ecDirParent
argument_list|,
literal|"snap2"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|snap2ECDir
init|=
operator|new
name|Path
argument_list|(
name|snap2
argument_list|,
name|ecDir
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Expected null erasure coding policy"
argument_list|,
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap2ECDir
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make dir again with system default ec policy
name|fs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|ecDir
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|snap3
init|=
name|fs
operator|.
name|createSnapshot
argument_list|(
name|ecDirParent
argument_list|,
literal|"snap3"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|snap3ECDir
init|=
operator|new
name|Path
argument_list|(
name|snap3
argument_list|,
name|ecDir
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Check that snap3's ECPolicy has the correct settings
name|ErasureCodingPolicy
name|ezSnap3
init|=
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap3ECDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|ezSnap3
argument_list|)
expr_stmt|;
comment|// Check that older snapshots still have the old ECPolicy settings
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap1ECDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap2ECDir
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify contents of the snapshotted file
specifier|final
name|Path
name|snapshottedECFile
init|=
operator|new
name|Path
argument_list|(
name|snap1
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|ecDir
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|ecFile
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Contents of snapshotted file have changed unexpectedly"
argument_list|,
name|contents
argument_list|,
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|snapshottedECFile
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now delete the snapshots out of order and verify the EC policy
comment|// correctness
name|fs
operator|.
name|deleteSnapshot
argument_list|(
name|ecDirParent
argument_list|,
name|snap2
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap1ECDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap3ECDir
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|deleteSnapshot
argument_list|(
name|ecDirParent
argument_list|,
name|snap1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap3ECDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test creation of snapshot on directory has erasure coding policy.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testSnapshotsOnErasureCodingDir ()
specifier|public
name|void
name|testSnapshotsOnErasureCodingDir
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|ecDir
init|=
operator|new
name|Path
argument_list|(
literal|"/ecdir"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|ecDir
argument_list|)
expr_stmt|;
name|fs
operator|.
name|allowSnapshot
argument_list|(
name|ecDir
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|ecDir
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|snap1
init|=
name|fs
operator|.
name|createSnapshot
argument_list|(
name|ecDir
argument_list|,
literal|"snap1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test verify erasure coding policy is present after restarting the NameNode.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testSnapshotsOnErasureCodingDirAfterNNRestart ()
specifier|public
name|void
name|testSnapshotsOnErasureCodingDirAfterNNRestart
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|ecDir
init|=
operator|new
name|Path
argument_list|(
literal|"/ecdir"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|ecDir
argument_list|)
expr_stmt|;
name|fs
operator|.
name|allowSnapshot
argument_list|(
name|ecDir
argument_list|)
expr_stmt|;
comment|// set erasure coding policy
name|fs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|ecDir
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|snap1
init|=
name|fs
operator|.
name|createSnapshot
argument_list|(
name|ecDir
argument_list|,
literal|"snap1"
argument_list|)
decl_stmt|;
name|ErasureCodingPolicy
name|ecSnap
init|=
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|ecSnap
argument_list|)
expr_stmt|;
comment|// save namespace, restart namenode, and check ec policy correctness.
name|fs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|fs
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
name|fs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ErasureCodingPolicy
name|ecSnap1
init|=
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|ecSnap1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected ecSchema"
argument_list|,
name|ecSnap
operator|.
name|getSchema
argument_list|()
argument_list|,
name|ecSnap1
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test copy a snapshot will not preserve its erasure coding policy info.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testCopySnapshotWillNotPreserveErasureCodingPolicy ()
specifier|public
name|void
name|testCopySnapshotWillNotPreserveErasureCodingPolicy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|len
init|=
literal|1024
decl_stmt|;
specifier|final
name|Path
name|ecDir
init|=
operator|new
name|Path
argument_list|(
literal|"/ecdir"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|ecFile
init|=
operator|new
name|Path
argument_list|(
name|ecDir
argument_list|,
literal|"ecFile"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|ecDir
argument_list|)
expr_stmt|;
name|fs
operator|.
name|allowSnapshot
argument_list|(
name|ecDir
argument_list|)
expr_stmt|;
comment|// set erasure coding policy
name|fs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|ecDir
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|ecFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|snap1
init|=
name|fs
operator|.
name|createSnapshot
argument_list|(
name|ecDir
argument_list|,
literal|"snap1"
argument_list|)
decl_stmt|;
name|Path
name|snap1Copy
init|=
operator|new
name|Path
argument_list|(
name|ecDir
operator|.
name|toString
argument_list|()
operator|+
literal|"-copy"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|snap1CopyECDir
init|=
operator|new
name|Path
argument_list|(
literal|"/ecdir-copy"
argument_list|)
decl_stmt|;
name|String
index|[]
name|argv
init|=
operator|new
name|String
index|[]
block|{
literal|"-cp"
block|,
literal|"-px"
block|,
name|snap1
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
block|,
name|snap1Copy
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
block|}
decl_stmt|;
name|int
name|ret
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|FsShell
argument_list|(
name|conf
argument_list|)
argument_list|,
name|argv
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"cp -px is not working on a snapshot"
argument_list|,
name|SUCCESS
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap1CopyECDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got unexpected erasure coding policy"
argument_list|,
name|ecPolicy
argument_list|,
name|fs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|snap1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testFileStatusAcrossNNRestart ()
specifier|public
name|void
name|testFileStatusAcrossNNRestart
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|len
init|=
literal|1024
decl_stmt|;
specifier|final
name|Path
name|normalFile
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
literal|"normalFile"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|normalFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|ecDir
init|=
operator|new
name|Path
argument_list|(
literal|"/ecdir"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|ecFile
init|=
operator|new
name|Path
argument_list|(
name|ecDir
argument_list|,
literal|"ecFile"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|ecDir
argument_list|)
expr_stmt|;
comment|// Set erasure coding policy
name|fs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|ecDir
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|ecFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
comment|// Verify FileStatus for normal and EC files
name|ContractTestUtils
operator|.
name|assertNotErasureCoded
argument_list|(
name|fs
argument_list|,
name|normalFile
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertErasureCoded
argument_list|(
name|fs
argument_list|,
name|ecFile
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Verify FileStatus for normal and EC files
name|ContractTestUtils
operator|.
name|assertNotErasureCoded
argument_list|(
name|fs
argument_list|,
name|normalFile
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertErasureCoded
argument_list|(
name|fs
argument_list|,
name|ecFile
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

