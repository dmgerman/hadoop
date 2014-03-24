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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|SnapshotDiffReport
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
name|SnapshotDiffReport
operator|.
name|DiffReportEntry
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
name|SnapshotDiffReport
operator|.
name|DiffType
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests snapshot deletion.  */
end_comment

begin_class
DECL|class|TestSnapshotDiffReport
specifier|public
class|class
name|TestSnapshotDiffReport
block|{
DECL|field|seed
specifier|protected
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0
decl_stmt|;
DECL|field|REPLICATION
specifier|protected
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|REPLICATION_1
specifier|protected
specifier|static
specifier|final
name|short
name|REPLICATION_1
init|=
literal|2
decl_stmt|;
DECL|field|BLOCKSIZE
specifier|protected
specifier|static
specifier|final
name|long
name|BLOCKSIZE
init|=
literal|1024
decl_stmt|;
DECL|field|SNAPSHOTNUMBER
specifier|public
specifier|static
specifier|final
name|int
name|SNAPSHOTNUMBER
init|=
literal|10
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
DECL|field|conf
specifier|protected
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|protected
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|hdfs
specifier|protected
name|DistributedFileSystem
name|hdfs
decl_stmt|;
DECL|field|snapshotNumberMap
specifier|private
specifier|final
name|HashMap
argument_list|<
name|Path
argument_list|,
name|Integer
argument_list|>
name|snapshotNumberMap
init|=
operator|new
name|HashMap
argument_list|<
name|Path
argument_list|,
name|Integer
argument_list|>
argument_list|()
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
DECL|method|genSnapshotName (Path snapshotDir)
specifier|private
name|String
name|genSnapshotName
parameter_list|(
name|Path
name|snapshotDir
parameter_list|)
block|{
name|int
name|sNum
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|snapshotNumberMap
operator|.
name|containsKey
argument_list|(
name|snapshotDir
argument_list|)
condition|)
block|{
name|sNum
operator|=
name|snapshotNumberMap
operator|.
name|get
argument_list|(
name|snapshotDir
argument_list|)
expr_stmt|;
block|}
name|snapshotNumberMap
operator|.
name|put
argument_list|(
name|snapshotDir
argument_list|,
operator|++
name|sNum
argument_list|)
expr_stmt|;
return|return
literal|"s"
operator|+
name|sNum
return|;
block|}
comment|/**    * Create/modify/delete files under a given directory, also create snapshots    * of directories.    */
DECL|method|modifyAndCreateSnapshot (Path modifyDir, Path[] snapshotDirs)
specifier|private
name|void
name|modifyAndCreateSnapshot
parameter_list|(
name|Path
name|modifyDir
parameter_list|,
name|Path
index|[]
name|snapshotDirs
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|file10
init|=
operator|new
name|Path
argument_list|(
name|modifyDir
argument_list|,
literal|"file10"
argument_list|)
decl_stmt|;
name|Path
name|file11
init|=
operator|new
name|Path
argument_list|(
name|modifyDir
argument_list|,
literal|"file11"
argument_list|)
decl_stmt|;
name|Path
name|file12
init|=
operator|new
name|Path
argument_list|(
name|modifyDir
argument_list|,
literal|"file12"
argument_list|)
decl_stmt|;
name|Path
name|file13
init|=
operator|new
name|Path
argument_list|(
name|modifyDir
argument_list|,
literal|"file13"
argument_list|)
decl_stmt|;
name|Path
name|link13
init|=
operator|new
name|Path
argument_list|(
name|modifyDir
argument_list|,
literal|"link13"
argument_list|)
decl_stmt|;
name|Path
name|file14
init|=
operator|new
name|Path
argument_list|(
name|modifyDir
argument_list|,
literal|"file14"
argument_list|)
decl_stmt|;
name|Path
name|file15
init|=
operator|new
name|Path
argument_list|(
name|modifyDir
argument_list|,
literal|"file15"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file10
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION_1
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file11
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION_1
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file12
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION_1
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file13
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION_1
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// create link13
name|hdfs
operator|.
name|createSymlink
argument_list|(
name|file13
argument_list|,
name|link13
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// create snapshot
for|for
control|(
name|Path
name|snapshotDir
range|:
name|snapshotDirs
control|)
block|{
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|snapshotDir
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|snapshotDir
argument_list|,
name|genSnapshotName
argument_list|(
name|snapshotDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// delete file11
name|hdfs
operator|.
name|delete
argument_list|(
name|file11
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// modify file12
name|hdfs
operator|.
name|setReplication
argument_list|(
name|file12
argument_list|,
name|REPLICATION
argument_list|)
expr_stmt|;
comment|// modify file13
name|hdfs
operator|.
name|setReplication
argument_list|(
name|file13
argument_list|,
name|REPLICATION
argument_list|)
expr_stmt|;
comment|// delete link13
name|hdfs
operator|.
name|delete
argument_list|(
name|link13
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// create file14
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file14
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// create file15
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file15
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// create snapshot
for|for
control|(
name|Path
name|snapshotDir
range|:
name|snapshotDirs
control|)
block|{
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|snapshotDir
argument_list|,
name|genSnapshotName
argument_list|(
name|snapshotDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// create file11 again
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file11
argument_list|,
name|BLOCKSIZE
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// delete file12
name|hdfs
operator|.
name|delete
argument_list|(
name|file12
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// modify file13
name|hdfs
operator|.
name|setReplication
argument_list|(
name|file13
argument_list|,
call|(
name|short
call|)
argument_list|(
name|REPLICATION
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// create link13 again
name|hdfs
operator|.
name|createSymlink
argument_list|(
name|file13
argument_list|,
name|link13
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// delete file14
name|hdfs
operator|.
name|delete
argument_list|(
name|file14
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// modify file15
name|hdfs
operator|.
name|setReplication
argument_list|(
name|file15
argument_list|,
call|(
name|short
call|)
argument_list|(
name|REPLICATION
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// create snapshot
for|for
control|(
name|Path
name|snapshotDir
range|:
name|snapshotDirs
control|)
block|{
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|snapshotDir
argument_list|,
name|genSnapshotName
argument_list|(
name|snapshotDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// modify file10
name|hdfs
operator|.
name|setReplication
argument_list|(
name|file10
argument_list|,
call|(
name|short
call|)
argument_list|(
name|REPLICATION
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** check the correctness of the diff reports */
DECL|method|verifyDiffReport (Path dir, String from, String to, DiffReportEntry... entries)
specifier|private
name|void
name|verifyDiffReport
parameter_list|(
name|Path
name|dir
parameter_list|,
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|,
name|DiffReportEntry
modifier|...
name|entries
parameter_list|)
throws|throws
name|IOException
block|{
name|SnapshotDiffReport
name|report
init|=
name|hdfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|dir
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
comment|// reverse the order of from and to
name|SnapshotDiffReport
name|inverseReport
init|=
name|hdfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|dir
argument_list|,
name|to
argument_list|,
name|from
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|report
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|inverseReport
operator|.
name|toString
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entries
operator|.
name|length
argument_list|,
name|report
operator|.
name|getDiffList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entries
operator|.
name|length
argument_list|,
name|inverseReport
operator|.
name|getDiffList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|DiffReportEntry
name|entry
range|:
name|entries
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getType
argument_list|()
operator|==
name|DiffType
operator|.
name|MODIFY
condition|)
block|{
name|assertTrue
argument_list|(
name|report
operator|.
name|getDiffList
argument_list|()
operator|.
name|contains
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|inverseReport
operator|.
name|getDiffList
argument_list|()
operator|.
name|contains
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getType
argument_list|()
operator|==
name|DiffType
operator|.
name|DELETE
condition|)
block|{
name|assertTrue
argument_list|(
name|report
operator|.
name|getDiffList
argument_list|()
operator|.
name|contains
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|inverseReport
operator|.
name|getDiffList
argument_list|()
operator|.
name|contains
argument_list|(
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|entry
operator|.
name|getRelativePath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getType
argument_list|()
operator|==
name|DiffType
operator|.
name|CREATE
condition|)
block|{
name|assertTrue
argument_list|(
name|report
operator|.
name|getDiffList
argument_list|()
operator|.
name|contains
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|inverseReport
operator|.
name|getDiffList
argument_list|()
operator|.
name|contains
argument_list|(
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|entry
operator|.
name|getRelativePath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Test the computation and representation of diff between snapshots */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testDiffReport ()
specifier|public
name|void
name|testDiffReport
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
name|Path
name|subsub1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"subsub1"
argument_list|)
decl_stmt|;
name|Path
name|subsubsub1
init|=
operator|new
name|Path
argument_list|(
name|subsub1
argument_list|,
literal|"subsubsub1"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|subsubsub1
argument_list|)
expr_stmt|;
name|modifyAndCreateSnapshot
argument_list|(
name|sub1
argument_list|,
operator|new
name|Path
index|[]
block|{
name|sub1
block|,
name|subsubsub1
block|}
argument_list|)
expr_stmt|;
name|modifyAndCreateSnapshot
argument_list|(
name|subsubsub1
argument_list|,
operator|new
name|Path
index|[]
block|{
name|sub1
block|,
name|subsubsub1
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|hdfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|subsub1
argument_list|,
literal|"s1"
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expect exception when getting snapshot diff report: "
operator|+
name|subsub1
operator|+
literal|" is not a snapshottable directory."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Directory is not a snapshottable directory: "
operator|+
name|subsub1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|invalidName
init|=
literal|"invalid"
decl_stmt|;
try|try
block|{
name|hdfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|sub1
argument_list|,
name|invalidName
argument_list|,
name|invalidName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expect exception when providing invalid snapshot name for diff report"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Cannot find the snapshot of directory "
operator|+
name|sub1
operator|+
literal|" with name "
operator|+
name|invalidName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// diff between the same snapshot
name|SnapshotDiffReport
name|report
init|=
name|hdfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|sub1
argument_list|,
literal|"s0"
argument_list|,
literal|"s0"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|report
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|report
operator|.
name|getDiffList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|=
name|hdfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|sub1
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|report
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|report
operator|.
name|getDiffList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|=
name|hdfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|subsubsub1
argument_list|,
literal|"s0"
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|report
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|report
operator|.
name|getDiffList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// test path with scheme also works
name|report
operator|=
name|hdfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|hdfs
operator|.
name|makeQualified
argument_list|(
name|subsubsub1
argument_list|)
argument_list|,
literal|"s0"
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|report
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|report
operator|.
name|getDiffList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|verifyDiffReport
argument_list|(
name|sub1
argument_list|,
literal|"s0"
argument_list|,
literal|"s2"
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file15"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file12"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file11"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file11"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"link13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"link13"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyDiffReport
argument_list|(
name|sub1
argument_list|,
literal|"s0"
argument_list|,
literal|"s5"
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file15"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file12"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file10"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file11"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file11"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"link13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"link13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file10"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file11"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/link13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file15"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyDiffReport
argument_list|(
name|sub1
argument_list|,
literal|"s2"
argument_list|,
literal|"s5"
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"file10"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file10"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file11"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/link13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file15"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyDiffReport
argument_list|(
name|sub1
argument_list|,
literal|"s3"
argument_list|,
literal|""
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file15"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file12"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file10"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file11"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file11"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/link13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/link13"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make changes under a sub-directory, then delete the sub-directory. Make    * sure the diff report computation correctly retrieve the diff from the    * deleted sub-directory.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testDiffReport2 ()
specifier|public
name|void
name|testDiffReport2
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|subsub1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"subsub1"
argument_list|)
decl_stmt|;
name|Path
name|subsubsub1
init|=
operator|new
name|Path
argument_list|(
name|subsub1
argument_list|,
literal|"subsubsub1"
argument_list|)
decl_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|subsubsub1
argument_list|)
expr_stmt|;
name|modifyAndCreateSnapshot
argument_list|(
name|subsubsub1
argument_list|,
operator|new
name|Path
index|[]
block|{
name|sub1
block|}
argument_list|)
expr_stmt|;
comment|// delete subsub1
name|hdfs
operator|.
name|delete
argument_list|(
name|subsub1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// check diff report between s0 and s2
name|verifyDiffReport
argument_list|(
name|sub1
argument_list|,
literal|"s0"
argument_list|,
literal|"s2"
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file15"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file12"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file11"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file11"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/file13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|CREATE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/link13"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1/subsubsub1/link13"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// check diff report between s0 and the current status
name|verifyDiffReport
argument_list|(
name|sub1
argument_list|,
literal|"s0"
argument_list|,
literal|""
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|DELETE
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"subsub1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

