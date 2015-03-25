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
name|NNStorage
operator|.
name|getFinalizedEditsFileName
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NNStorage
operator|.
name|getImageFileName
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NNStorage
operator|.
name|getInProgressEditsFileName
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
name|GenericTestUtils
operator|.
name|assertGlobEquals
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_comment
comment|/**  * Functional tests for NNStorageRetentionManager. This differs from  * {@link TestNNStorageRetentionManager} in that the other test suite  * is only unit/mock-based tests whereas this suite starts miniclusters,  * etc.  */
end_comment

begin_class
DECL|class|TestNNStorageRetentionFunctional
specifier|public
class|class
name|TestNNStorageRetentionFunctional
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_ROOT_DIR
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestNNStorageRetentionFunctional
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**   * Test case where two directories are configured as NAME_AND_EDITS   * and one of them fails to save storage. Since the edits and image   * failure states are decoupled, the failure of image saving should   * not prevent the purging of logs from that dir.   */
annotation|@
name|Test
DECL|method|testPurgingWithNameEditsDirAfterFailure ()
specifier|public
name|void
name|testPurgingWithNameEditsDirAfterFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NUM_EXTRA_EDITS_RETAINED_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|File
name|sd0
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"nn0"
argument_list|)
decl_stmt|;
name|File
name|sd1
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"nn1"
argument_list|)
decl_stmt|;
name|File
name|cd0
init|=
operator|new
name|File
argument_list|(
name|sd0
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
name|File
name|cd1
init|=
operator|new
name|File
argument_list|(
name|sd1
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|sd0
argument_list|,
name|sd1
argument_list|)
argument_list|)
expr_stmt|;
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
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|manageNameDfsDirs
argument_list|(
literal|false
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
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|doSaveNamespace
argument_list|(
name|nn
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"After first save, images 0 and 2 should exist in both dirs"
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd0
argument_list|,
literal|"fsimage_\\d*"
argument_list|,
name|getImageFileName
argument_list|(
literal|0
argument_list|)
argument_list|,
name|getImageFileName
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd1
argument_list|,
literal|"fsimage_\\d*"
argument_list|,
name|getImageFileName
argument_list|(
literal|0
argument_list|)
argument_list|,
name|getImageFileName
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd0
argument_list|,
literal|"edits_.*"
argument_list|,
name|getFinalizedEditsFileName
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|getInProgressEditsFileName
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd1
argument_list|,
literal|"edits_.*"
argument_list|,
name|getFinalizedEditsFileName
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|getInProgressEditsFileName
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|doSaveNamespace
argument_list|(
name|nn
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"After second save, image 0 should be purged, "
operator|+
literal|"and image 4 should exist in both."
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd0
argument_list|,
literal|"fsimage_\\d*"
argument_list|,
name|getImageFileName
argument_list|(
literal|2
argument_list|)
argument_list|,
name|getImageFileName
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd1
argument_list|,
literal|"fsimage_\\d*"
argument_list|,
name|getImageFileName
argument_list|(
literal|2
argument_list|)
argument_list|,
name|getImageFileName
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd0
argument_list|,
literal|"edits_.*"
argument_list|,
name|getFinalizedEditsFileName
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd1
argument_list|,
literal|"edits_.*"
argument_list|,
name|getFinalizedEditsFileName
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Failing first storage dir by chmodding it"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|FileUtil
operator|.
name|chmod
argument_list|(
name|cd0
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"000"
argument_list|)
argument_list|)
expr_stmt|;
name|doSaveNamespace
argument_list|(
name|nn
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Restoring accessibility of first storage dir"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|FileUtil
operator|.
name|chmod
argument_list|(
name|cd0
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"755"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"nothing should have been purged in first storage dir"
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd0
argument_list|,
literal|"fsimage_\\d*"
argument_list|,
name|getImageFileName
argument_list|(
literal|2
argument_list|)
argument_list|,
name|getImageFileName
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd0
argument_list|,
literal|"edits_.*"
argument_list|,
name|getFinalizedEditsFileName
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"fsimage_2 should be purged in second storage dir"
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd1
argument_list|,
literal|"fsimage_\\d*"
argument_list|,
name|getImageFileName
argument_list|(
literal|4
argument_list|)
argument_list|,
name|getImageFileName
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd1
argument_list|,
literal|"edits_.*"
argument_list|,
name|getFinalizedEditsFileName
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|)
argument_list|,
name|getInProgressEditsFileName
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"On next save, we should purge logs from the failed dir,"
operator|+
literal|" but not images, since the image directory is in failed state."
argument_list|)
expr_stmt|;
name|doSaveNamespace
argument_list|(
name|nn
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd1
argument_list|,
literal|"fsimage_\\d*"
argument_list|,
name|getImageFileName
argument_list|(
literal|6
argument_list|)
argument_list|,
name|getImageFileName
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd1
argument_list|,
literal|"edits_.*"
argument_list|,
name|getFinalizedEditsFileName
argument_list|(
literal|7
argument_list|,
literal|8
argument_list|)
argument_list|,
name|getInProgressEditsFileName
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd0
argument_list|,
literal|"fsimage_\\d*"
argument_list|,
name|getImageFileName
argument_list|(
literal|2
argument_list|)
argument_list|,
name|getImageFileName
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertGlobEquals
argument_list|(
name|cd0
argument_list|,
literal|"edits_.*"
argument_list|,
name|getInProgressEditsFileName
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtil
operator|.
name|chmod
argument_list|(
name|cd0
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"755"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down..."
argument_list|)
expr_stmt|;
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
block|}
DECL|method|doSaveNamespace (NameNode nn)
specifier|private
specifier|static
name|void
name|doSaveNamespace
parameter_list|(
name|NameNode
name|nn
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Saving namespace..."
argument_list|)
expr_stmt|;
name|nn
operator|.
name|getRpcServer
argument_list|()
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|nn
operator|.
name|getRpcServer
argument_list|()
operator|.
name|saveNamespace
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|nn
operator|.
name|getRpcServer
argument_list|()
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

