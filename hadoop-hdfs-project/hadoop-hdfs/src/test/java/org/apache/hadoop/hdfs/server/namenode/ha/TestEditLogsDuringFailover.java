begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
name|ha
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HAUtil
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
name|MiniDFSNNTopology
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
name|FSImageTestUtil
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
name|NNStorage
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * Test cases for the handling of edit logs during failover  * and startup of the standby node.  */
end_comment

begin_class
DECL|class|TestEditLogsDuringFailover
specifier|public
class|class
name|TestEditLogsDuringFailover
block|{
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
name|TestEditLogsDuringFailover
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NUM_DIRS_IN_LOG
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DIRS_IN_LOG
init|=
literal|5
decl_stmt|;
annotation|@
name|Test
DECL|method|testStartup ()
specifier|public
name|void
name|testStartup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|HAUtil
operator|.
name|setAllowStandbyReads
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
comment|// During HA startup, both nodes should be in
comment|// standby and we shouldn't have any edits files
comment|// in any edits directory!
name|List
argument_list|<
name|URI
argument_list|>
name|allDirs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|allDirs
operator|.
name|addAll
argument_list|(
name|cluster
operator|.
name|getNameDirs
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|allDirs
operator|.
name|addAll
argument_list|(
name|cluster
operator|.
name|getNameDirs
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|allDirs
operator|.
name|add
argument_list|(
name|cluster
operator|.
name|getSharedEditsDir
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoEditFiles
argument_list|(
name|allDirs
argument_list|)
expr_stmt|;
comment|// Set the first NN to active, make sure it creates edits
comment|// in its own dirs and the shared dir. The standby
comment|// should still have no edits!
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEditFiles
argument_list|(
name|cluster
operator|.
name|getNameDirs
argument_list|(
literal|0
argument_list|)
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEditFiles
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|cluster
operator|.
name|getSharedEditsDir
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoEditFiles
argument_list|(
name|cluster
operator|.
name|getNameDirs
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getRpcServer
argument_list|()
operator|.
name|mkdirs
argument_list|(
literal|"/test"
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Restarting the standby should not finalize any edits files
comment|// in the shared directory when it starts up!
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEditFiles
argument_list|(
name|cluster
operator|.
name|getNameDirs
argument_list|(
literal|0
argument_list|)
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEditFiles
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|cluster
operator|.
name|getSharedEditsDir
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoEditFiles
argument_list|(
name|cluster
operator|.
name|getNameDirs
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Additionally it should not have applied any in-progress logs
comment|// at start-up -- otherwise, it would have read half-way into
comment|// the current log segment, and on the next roll, it would have to
comment|// either replay starting in the middle of the segment (not allowed)
comment|// or double-replay the edits (incorrect).
name|assertNull
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"/test"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getRpcServer
argument_list|()
operator|.
name|mkdirs
argument_list|(
literal|"/test2"
argument_list|,
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// If we restart NN0, it'll come back as standby, and we can
comment|// transition NN1 to active and make sure it reads edits correctly at this point.
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// NN1 should have both the edits that came before its restart, and the edits that
comment|// came after its restart.
name|assertNotNull
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"/test"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|NameNodeAdapter
operator|.
name|getFileInfo
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"/test2"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFailoverFinalizesAndReadsInProgress ()
specifier|public
name|void
name|testFailoverFinalizesAndReadsInProgress
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Create a fake in-progress edit-log in the shared directory
name|URI
name|sharedUri
init|=
name|cluster
operator|.
name|getSharedEditsDir
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|File
name|sharedDir
init|=
operator|new
name|File
argument_list|(
name|sharedUri
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
name|FSImageTestUtil
operator|.
name|createAbortedLogWithMkdirs
argument_list|(
name|sharedDir
argument_list|,
name|NUM_DIRS_IN_LOG
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEditFiles
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|sharedUri
argument_list|)
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Transition one of the NNs to active
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// In the transition to active, it should have read the log -- and
comment|// hence see one of the dirs we made in the fake log.
name|String
name|testPath
init|=
literal|"/dir"
operator|+
name|NUM_DIRS_IN_LOG
decl_stmt|;
name|assertNotNull
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|testPath
argument_list|)
argument_list|)
expr_stmt|;
comment|// It also should have finalized that log in the shared directory and started
comment|// writing to a new one at the next txid.
name|assertEditFiles
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|sharedUri
argument_list|)
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
literal|1
argument_list|,
name|NUM_DIRS_IN_LOG
operator|+
literal|1
argument_list|)
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
name|NUM_DIRS_IN_LOG
operator|+
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Check that no edits files are present in the given storage dirs.    */
DECL|method|assertNoEditFiles (Iterable<URI> dirs)
specifier|private
name|void
name|assertNoEditFiles
parameter_list|(
name|Iterable
argument_list|<
name|URI
argument_list|>
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEditFiles
argument_list|(
name|dirs
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that the given list of edits files are present in the given storage    * dirs.    */
DECL|method|assertEditFiles (Iterable<URI> dirs, String ... files)
specifier|private
name|void
name|assertEditFiles
parameter_list|(
name|Iterable
argument_list|<
name|URI
argument_list|>
name|dirs
parameter_list|,
name|String
modifier|...
name|files
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|URI
name|u
range|:
name|dirs
control|)
block|{
name|File
name|editDirRoot
init|=
operator|new
name|File
argument_list|(
name|u
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|editDir
init|=
operator|new
name|File
argument_list|(
name|editDirRoot
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|assertExists
argument_list|(
name|editDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|files
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking no edit files exist in "
operator|+
name|editDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking for following edit files in "
operator|+
name|editDir
operator|+
literal|": "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|files
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|GenericTestUtils
operator|.
name|assertGlobEquals
argument_list|(
name|editDir
argument_list|,
literal|"edits_.*"
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

