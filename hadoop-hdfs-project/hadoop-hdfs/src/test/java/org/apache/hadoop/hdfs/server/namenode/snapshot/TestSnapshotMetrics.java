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
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertCounter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertGauge
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
name|assertEquals
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
name|SnapshottableDirectoryStatus
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
comment|/**  * Test the snapshot-related metrics  */
end_comment

begin_class
DECL|class|TestSnapshotMetrics
specifier|public
class|class
name|TestSnapshotMetrics
block|{
DECL|field|seed
specifier|private
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0
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
DECL|field|NN_METRICS
specifier|private
specifier|static
specifier|final
name|String
name|NN_METRICS
init|=
literal|"NameNodeActivity"
decl_stmt|;
DECL|field|NS_METRICS
specifier|private
specifier|static
specifier|final
name|String
name|NS_METRICS
init|=
literal|"FSNamesystem"
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
DECL|field|file2
specifier|private
specifier|final
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|hdfs
specifier|private
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
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
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
name|file2
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
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
comment|/**    * Test the metric SnapshottableDirectories, AllowSnapshotOps,    * DisallowSnapshotOps, and listSnapshottableDirOps    */
annotation|@
name|Test
DECL|method|testSnapshottableDirs ()
specifier|public
name|void
name|testSnapshottableDirs
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
name|assertGauge
argument_list|(
literal|"SnapshottableDirectories"
argument_list|,
literal|0
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"AllowSnapshotOps"
argument_list|,
literal|0L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"DisallowSnapshotOps"
argument_list|,
literal|0L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Allow snapshots for directories, and check the metrics
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|sub1
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"SnapshottableDirectories"
argument_list|,
literal|1
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"AllowSnapshotOps"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|sub2
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub2"
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|sub2
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
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|sub2
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"SnapshottableDirectories"
argument_list|,
literal|2
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"AllowSnapshotOps"
argument_list|,
literal|2L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
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
literal|"sub1sub1"
argument_list|)
decl_stmt|;
name|Path
name|subfile
init|=
operator|new
name|Path
argument_list|(
name|subsub1
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
name|subfile
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|subsub1
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"SnapshottableDirectories"
argument_list|,
literal|3
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"AllowSnapshotOps"
argument_list|,
literal|3L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set an already snapshottable directory to snapshottable, should not
comment|// change the metrics
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|sub1
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"SnapshottableDirectories"
argument_list|,
literal|3
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// But the number of allowSnapshot operations still increases
name|assertCounter
argument_list|(
literal|"AllowSnapshotOps"
argument_list|,
literal|4L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Disallow the snapshot for snapshottable directories, then check the
comment|// metrics again
name|hdfs
operator|.
name|disallowSnapshot
argument_list|(
name|sub1
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"SnapshottableDirectories"
argument_list|,
literal|2
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"DisallowSnapshotOps"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// delete subsub1, snapshottable directories should be 1
name|hdfs
operator|.
name|delete
argument_list|(
name|subsub1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"SnapshottableDirectories"
argument_list|,
literal|1
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// list all the snapshottable directories
name|SnapshottableDirectoryStatus
index|[]
name|status
init|=
name|hdfs
operator|.
name|getSnapshottableDirListing
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|status
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"ListSnapshottableDirOps"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the metrics Snapshots, CreateSnapshotOps, DeleteSnapshotOps,    * RenameSnapshotOps    */
annotation|@
name|Test
DECL|method|testSnapshots ()
specifier|public
name|void
name|testSnapshots
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
name|assertGauge
argument_list|(
literal|"Snapshots"
argument_list|,
literal|0
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"CreateSnapshotOps"
argument_list|,
literal|0L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create a snapshot for a non-snapshottable directory, thus should not
comment|// change the metrics
try|try
block|{
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|assertGauge
argument_list|(
literal|"Snapshots"
argument_list|,
literal|0
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"CreateSnapshotOps"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create snapshot for sub1
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|sub1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"Snapshots"
argument_list|,
literal|1
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"CreateSnapshotOps"
argument_list|,
literal|2L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"Snapshots"
argument_list|,
literal|2
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"CreateSnapshotOps"
argument_list|,
literal|3L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"SnapshotDiffReportOps"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create snapshot for a directory under sub1
name|Path
name|subsub1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub1sub1"
argument_list|)
decl_stmt|;
name|Path
name|subfile
init|=
operator|new
name|Path
argument_list|(
name|subsub1
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
name|subfile
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|allowSnapshot
argument_list|(
name|subsub1
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|createSnapshot
argument_list|(
name|subsub1
argument_list|,
literal|"s11"
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"Snapshots"
argument_list|,
literal|3
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"CreateSnapshotOps"
argument_list|,
literal|4L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// delete snapshot
name|hdfs
operator|.
name|deleteSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s2"
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"Snapshots"
argument_list|,
literal|2
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"DeleteSnapshotOps"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// rename snapshot
name|hdfs
operator|.
name|renameSnapshot
argument_list|(
name|sub1
argument_list|,
literal|"s1"
argument_list|,
literal|"NewS1"
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"Snapshots"
argument_list|,
literal|2
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"RenameSnapshotOps"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

