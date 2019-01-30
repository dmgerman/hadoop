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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyInt
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doThrow
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|lang3
operator|.
name|StringUtils
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
name|server
operator|.
name|namenode
operator|.
name|JournalSet
operator|.
name|JournalAndStream
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
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|PathUtils
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
name|ExitUtil
operator|.
name|ExitException
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestEditLogJournalFailures
specifier|public
class|class
name|TestEditLogJournalFailures
block|{
DECL|field|editsPerformed
specifier|private
name|int
name|editsPerformed
init|=
literal|0
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|useAsyncEdits
specifier|private
name|boolean
name|useAsyncEdits
decl_stmt|;
annotation|@
name|Parameters
DECL|method|data ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|}
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
DECL|method|TestEditLogJournalFailures (boolean useAsyncEdits)
specifier|public
name|TestEditLogJournalFailures
parameter_list|(
name|boolean
name|useAsyncEdits
parameter_list|)
block|{
name|this
operator|.
name|useAsyncEdits
operator|=
name|useAsyncEdits
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|private
name|Configuration
name|getConf
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_ASYNC_LOGGING
argument_list|,
name|useAsyncEdits
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * Create the mini cluster for testing and sub in a custom runtime so that    * edit log journal failures don't actually cause the JVM to exit.    */
annotation|@
name|Before
DECL|method|setUpMiniCluster ()
specifier|public
name|void
name|setUpMiniCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|setUpMiniCluster
argument_list|(
name|getConf
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|setUpMiniCluster (Configuration conf, boolean manageNameDfsDirs)
specifier|public
name|void
name|setUpMiniCluster
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|boolean
name|manageNameDfsDirs
parameter_list|)
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
literal|0
argument_list|)
operator|.
name|manageNameDfsDirs
argument_list|(
name|manageNameDfsDirs
argument_list|)
operator|.
name|checkExitOnShutdown
argument_list|(
literal|false
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
name|After
DECL|method|shutDownMiniCluster ()
specifier|public
name|void
name|shutDownMiniCluster
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
try|try
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
catch|catch
parameter_list|(
name|ExitException
name|ee
parameter_list|)
block|{
comment|// Ignore ExitExceptions as the tests may result in the
comment|// NameNode doing an immediate shutdown.
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testSingleFailedEditsDirOnFlush ()
specifier|public
name|void
name|testSingleFailedEditsDirOnFlush
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Invalidate one edits journal.
name|invalidateEditsDirAtIndex
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// The NN has not terminated (no ExitException thrown)
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// A single journal failure should not result in a call to terminate
name|assertFalse
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllEditsDirsFailOnFlush ()
specifier|public
name|void
name|testAllEditsDirsFailOnFlush
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Invalidate both edits journals.
name|invalidateEditsDirAtIndex
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|invalidateEditsDirAtIndex
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// The NN has not terminated (no ExitException thrown)
try|try
block|{
name|doAnEdit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"The previous edit could not be synced to any persistent storage, "
operator|+
literal|"should have halted the NN"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|re
operator|.
name|getClassName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ExitException"
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Could not sync enough journals to persistent storage. "
operator|+
literal|"Unsynced transactions: 1"
argument_list|,
name|re
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAllEditsDirFailOnWrite ()
specifier|public
name|void
name|testAllEditsDirFailOnWrite
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Invalidate both edits journals.
name|invalidateEditsDirAtIndex
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|invalidateEditsDirAtIndex
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// The NN has not terminated (no ExitException thrown)
try|try
block|{
name|doAnEdit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"The previous edit could not be synced to any persistent storage, "
operator|+
literal|" should have halted the NN"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|re
operator|.
name|getClassName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ExitException"
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Could not sync enough journals to persistent storage due to "
operator|+
literal|"No journals available to flush. "
operator|+
literal|"Unsynced transactions: 1"
argument_list|,
name|re
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSingleFailedEditsDirOnSetReadyToFlush ()
specifier|public
name|void
name|testSingleFailedEditsDirOnSetReadyToFlush
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Invalidate one edits journal.
name|invalidateEditsDirAtIndex
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// The NN has not terminated (no ExitException thrown)
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// A single journal failure should not result in a call to terminate
name|assertFalse
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleRequiredFailedEditsDirOnSetReadyToFlush ()
specifier|public
name|void
name|testSingleRequiredFailedEditsDirOnSetReadyToFlush
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Set one of the edits dirs to be required.
name|String
index|[]
name|editsDirs
init|=
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
operator|.
name|getTrimmedStrings
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|)
decl_stmt|;
name|shutDownMiniCluster
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_REQUIRED_KEY
argument_list|,
name|editsDirs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_MINIMUM_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKED_VOLUMES_MINIMUM_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|setUpMiniCluster
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Invalidated the one required edits journal.
name|invalidateEditsDirAtIndex
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|JournalAndStream
name|nonRequiredJas
init|=
name|getJournalAndStream
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|EditLogFileOutputStream
name|nonRequiredSpy
init|=
name|spyOnStream
argument_list|(
name|nonRequiredJas
argument_list|)
decl_stmt|;
comment|// The NN has not terminated (no ExitException thrown)
comment|// ..and that the other stream is active.
name|assertTrue
argument_list|(
name|nonRequiredJas
operator|.
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|doAnEdit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"A single failure of a required journal should have halted the NN"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|re
operator|.
name|getClassName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ExitException"
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"setReadyToFlush failed for required journal"
argument_list|,
name|re
argument_list|)
expr_stmt|;
block|}
comment|// Since the required directory failed setReadyToFlush, and that
comment|// directory was listed prior to the non-required directory,
comment|// we should not call setReadyToFlush on the non-required
comment|// directory. Regression test for HDFS-2874.
name|Mockito
operator|.
name|verify
argument_list|(
name|nonRequiredSpy
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|setReadyToFlush
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|nonRequiredJas
operator|.
name|isActive
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleRedundantFailedEditsDirOnSetReadyToFlush ()
specifier|public
name|void
name|testMultipleRedundantFailedEditsDirOnSetReadyToFlush
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Set up 4 name/edits dirs.
name|shutDownMiniCluster
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|String
index|[]
name|nameDirs
init|=
operator|new
name|String
index|[
literal|4
index|]
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
name|nameDirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|nameDir
init|=
operator|new
name|File
argument_list|(
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|,
literal|"name-dir"
operator|+
name|i
argument_list|)
decl_stmt|;
name|nameDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|nameDirs
index|[
name|i
index|]
operator|=
name|nameDir
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
name|nameDirs
argument_list|,
literal|","
argument_list|)
argument_list|)
expr_stmt|;
comment|// Keep running unless there are less than 2 edits dirs remaining.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_MINIMUM_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|setUpMiniCluster
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// All journals active.
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// The NN has not terminated (no ExitException thrown)
comment|// Invalidate 1/4 of the redundant journals.
name|invalidateEditsDirAtIndex
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// The NN has not terminated (no ExitException thrown)
comment|// Invalidate 2/4 of the redundant journals.
name|invalidateEditsDirAtIndex
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// The NN has not terminated (no ExitException thrown)
comment|// Invalidate 3/4 of the redundant journals.
name|invalidateEditsDirAtIndex
argument_list|(
literal|2
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|doAnEdit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"A failure of more than the minimum number of redundant journals "
operator|+
literal|"should have halted "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|re
operator|.
name|getClassName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ExitException"
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Could not sync enough journals to persistent storage due to "
operator|+
literal|"setReadyToFlush failed for too many journals. "
operator|+
literal|"Unsynced transactions: 1"
argument_list|,
name|re
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMultipleRedundantFailedEditsDirOnStartLogSegment ()
specifier|public
name|void
name|testMultipleRedundantFailedEditsDirOnStartLogSegment
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Set up 4 name/edits dirs.
name|shutDownMiniCluster
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|String
index|[]
name|nameDirs
init|=
operator|new
name|String
index|[
literal|4
index|]
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
name|nameDirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|nameDir
init|=
operator|new
name|File
argument_list|(
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|,
literal|"name-dir"
operator|+
name|i
argument_list|)
decl_stmt|;
name|nameDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|nameDirs
index|[
name|i
index|]
operator|=
name|nameDir
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
name|nameDirs
argument_list|,
literal|","
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_REQUIRED_KEY
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
name|nameDirs
argument_list|,
literal|","
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|setUpMiniCluster
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// All journals active.
name|assertTrue
argument_list|(
name|doAnEdit
argument_list|()
argument_list|)
expr_stmt|;
comment|// The NN has not terminated (no ExitException thrown)
name|spyOnJASjournal
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|RemoteException
name|re
init|=
name|intercept
argument_list|(
name|RemoteException
operator|.
name|class
argument_list|,
literal|"too few journals successfully started."
argument_list|,
parameter_list|()
lambda|->
operator|(
operator|(
name|DistributedFileSystem
operator|)
name|fs
operator|)
operator|.
name|rollEdits
argument_list|()
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"ExitException"
argument_list|,
name|re
argument_list|)
expr_stmt|;
block|}
DECL|method|spyOnJASjournal (int index)
specifier|private
name|JournalManager
name|spyOnJASjournal
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|Exception
block|{
name|JournalAndStream
name|jas
init|=
name|getJournalAndStream
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|JournalManager
name|manager
init|=
name|jas
operator|.
name|getManager
argument_list|()
decl_stmt|;
name|JournalManager
name|spyManager
init|=
name|spy
argument_list|(
name|manager
argument_list|)
decl_stmt|;
name|jas
operator|.
name|setJournalForTests
argument_list|(
name|spyManager
argument_list|)
expr_stmt|;
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Unable to start log segment "
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spyManager
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spyManager
return|;
block|}
comment|/**    * Replace the journal at index<code>index</code> with one that throws an    * exception on flush.    *     * @param index the index of the journal to take offline.    * @return the original<code>EditLogOutputStream</code> of the journal.    */
DECL|method|invalidateEditsDirAtIndex (int index, boolean failOnFlush, boolean failOnWrite)
specifier|private
name|void
name|invalidateEditsDirAtIndex
parameter_list|(
name|int
name|index
parameter_list|,
name|boolean
name|failOnFlush
parameter_list|,
name|boolean
name|failOnWrite
parameter_list|)
throws|throws
name|IOException
block|{
name|JournalAndStream
name|jas
init|=
name|getJournalAndStream
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|EditLogFileOutputStream
name|spyElos
init|=
name|spyOnStream
argument_list|(
name|jas
argument_list|)
decl_stmt|;
if|if
condition|(
name|failOnWrite
condition|)
block|{
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"fail on write()"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spyElos
argument_list|)
operator|.
name|write
argument_list|(
operator|(
name|FSEditLogOp
operator|)
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|failOnFlush
condition|)
block|{
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"fail on flush()"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spyElos
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"fail on setReadyToFlush()"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spyElos
argument_list|)
operator|.
name|setReadyToFlush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|spyOnStream (JournalAndStream jas)
specifier|private
name|EditLogFileOutputStream
name|spyOnStream
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
block|{
name|EditLogFileOutputStream
name|elos
init|=
operator|(
name|EditLogFileOutputStream
operator|)
name|jas
operator|.
name|getCurrentStream
argument_list|()
decl_stmt|;
name|EditLogFileOutputStream
name|spyElos
init|=
name|spy
argument_list|(
name|elos
argument_list|)
decl_stmt|;
name|jas
operator|.
name|setCurrentStreamForTests
argument_list|(
name|spyElos
argument_list|)
expr_stmt|;
return|return
name|spyElos
return|;
block|}
comment|/**    * Pull out one of the JournalAndStream objects from the edit log.    */
DECL|method|getJournalAndStream (int index)
specifier|private
name|JournalAndStream
name|getJournalAndStream
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|FSImage
name|fsimage
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
decl_stmt|;
name|FSEditLog
name|editLog
init|=
name|fsimage
operator|.
name|getEditLog
argument_list|()
decl_stmt|;
return|return
name|editLog
operator|.
name|getJournals
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**    * Do a mutative metadata operation on the file system.    *     * @return true if the operation was successful, false otherwise.    */
DECL|method|doAnEdit ()
specifier|private
name|boolean
name|doAnEdit
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|editsPerformed
operator|++
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

