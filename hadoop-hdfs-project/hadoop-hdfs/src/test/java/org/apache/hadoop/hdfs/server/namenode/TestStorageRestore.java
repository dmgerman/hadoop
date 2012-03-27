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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|cli
operator|.
name|CLITestCmdDFS
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
name|cli
operator|.
name|util
operator|.
name|CLICommandDFSAdmin
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
name|cli
operator|.
name|util
operator|.
name|CommandExecutor
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
name|server
operator|.
name|common
operator|.
name|Storage
operator|.
name|StorageDirectory
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
name|mockito
operator|.
name|Mockito
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
name|ImmutableSet
import|;
end_import

begin_comment
comment|/**  * Startup and checkpoint tests  *   */
end_comment

begin_class
DECL|class|TestStorageRestore
specifier|public
class|class
name|TestStorageRestore
block|{
DECL|field|NAME_NODE_HOST
specifier|public
specifier|static
specifier|final
name|String
name|NAME_NODE_HOST
init|=
literal|"localhost:"
decl_stmt|;
DECL|field|NAME_NODE_HTTP_HOST
specifier|public
specifier|static
specifier|final
name|String
name|NAME_NODE_HTTP_HOST
init|=
literal|"0.0.0.0:"
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
name|TestStorageRestore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|config
specifier|private
name|Configuration
name|config
decl_stmt|;
DECL|field|hdfsDir
specifier|private
name|File
name|hdfsDir
init|=
literal|null
decl_stmt|;
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xAAAAEEFL
decl_stmt|;
DECL|field|blockSize
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|4096
decl_stmt|;
DECL|field|fileSize
specifier|static
specifier|final
name|int
name|fileSize
init|=
literal|8192
decl_stmt|;
DECL|field|path1
DECL|field|path2
DECL|field|path3
specifier|private
name|File
name|path1
decl_stmt|,
name|path2
decl_stmt|,
name|path3
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|Before
DECL|method|setUpNameDirs ()
specifier|public
name|void
name|setUpNameDirs
parameter_list|()
throws|throws
name|Exception
block|{
name|config
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|hdfsDir
operator|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
operator|.
name|getCanonicalFile
argument_list|()
expr_stmt|;
if|if
condition|(
name|hdfsDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|hdfsDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not delete hdfs directory '"
operator|+
name|hdfsDir
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|hdfsDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|path1
operator|=
operator|new
name|File
argument_list|(
name|hdfsDir
argument_list|,
literal|"name1"
argument_list|)
expr_stmt|;
name|path2
operator|=
operator|new
name|File
argument_list|(
name|hdfsDir
argument_list|,
literal|"name2"
argument_list|)
expr_stmt|;
name|path3
operator|=
operator|new
name|File
argument_list|(
name|hdfsDir
argument_list|,
literal|"name3"
argument_list|)
expr_stmt|;
name|path1
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|path2
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|path3
operator|.
name|mkdir
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|path2
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|path3
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|path1
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Couldn't create dfs.name dirs in "
operator|+
name|hdfsDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|dfs_name_dir
init|=
operator|new
name|String
argument_list|(
name|path1
operator|.
name|getPath
argument_list|()
operator|+
literal|","
operator|+
name|path2
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"configuring hdfsdir is "
operator|+
name|hdfsDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"; dfs_name_dir = "
operator|+
name|dfs_name_dir
operator|+
literal|";dfs_name_edits_dir(only)="
operator|+
name|path3
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|dfs_name_dir
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
name|dfs_name_dir
operator|+
literal|","
operator|+
name|path3
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_DIR_KEY
argument_list|,
operator|new
name|File
argument_list|(
name|hdfsDir
argument_list|,
literal|"secondary"
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|config
argument_list|,
literal|"hdfs://"
operator|+
name|NAME_NODE_HOST
operator|+
literal|"0"
argument_list|)
expr_stmt|;
name|config
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
comment|// set the restore feature on
name|config
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_RESTORE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * invalidate storage by removing the second and third storage directories    */
DECL|method|invalidateStorage (FSImage fi, Set<File> filesToInvalidate)
specifier|public
name|void
name|invalidateStorage
parameter_list|(
name|FSImage
name|fi
parameter_list|,
name|Set
argument_list|<
name|File
argument_list|>
name|filesToInvalidate
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|StorageDirectory
argument_list|>
name|al
init|=
operator|new
name|ArrayList
argument_list|<
name|StorageDirectory
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|StorageDirectory
argument_list|>
name|it
init|=
name|fi
operator|.
name|getStorage
argument_list|()
operator|.
name|dirIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|StorageDirectory
name|sd
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|filesToInvalidate
operator|.
name|contains
argument_list|(
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"causing IO error on "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|al
operator|.
name|add
argument_list|(
name|sd
argument_list|)
expr_stmt|;
block|}
block|}
comment|// simulate an error
name|fi
operator|.
name|getStorage
argument_list|()
operator|.
name|reportErrorsOnDirectories
argument_list|(
name|al
argument_list|)
expr_stmt|;
for|for
control|(
name|JournalAndStream
name|j
range|:
name|fi
operator|.
name|getEditLog
argument_list|()
operator|.
name|getJournals
argument_list|()
control|)
block|{
if|if
condition|(
name|j
operator|.
name|getManager
argument_list|()
operator|instanceof
name|FileJournalManager
condition|)
block|{
name|FileJournalManager
name|fm
init|=
operator|(
name|FileJournalManager
operator|)
name|j
operator|.
name|getManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|fm
operator|.
name|getStorageDirectory
argument_list|()
operator|.
name|getRoot
argument_list|()
operator|.
name|equals
argument_list|(
name|path2
argument_list|)
operator|||
name|fm
operator|.
name|getStorageDirectory
argument_list|()
operator|.
name|getRoot
argument_list|()
operator|.
name|equals
argument_list|(
name|path3
argument_list|)
condition|)
block|{
name|EditLogOutputStream
name|mockStream
init|=
name|spy
argument_list|(
name|j
operator|.
name|getCurrentStream
argument_list|()
argument_list|)
decl_stmt|;
name|j
operator|.
name|setCurrentStreamForTests
argument_list|(
name|mockStream
argument_list|)
expr_stmt|;
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Injected fault: write"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockStream
argument_list|)
operator|.
name|write
argument_list|(
name|Mockito
operator|.
expr|<
name|FSEditLogOp
operator|>
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * test    */
DECL|method|printStorages (FSImage image)
specifier|private
name|void
name|printStorages
parameter_list|(
name|FSImage
name|image
parameter_list|)
block|{
name|FSImageTestUtil
operator|.
name|logStorageContents
argument_list|(
name|LOG
argument_list|,
name|image
operator|.
name|getStorage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * test     * 1. create DFS cluster with 3 storage directories - 2 EDITS_IMAGE, 1 EDITS    * 2. create a cluster and write a file    * 3. corrupt/disable one storage (or two) by removing    * 4. run doCheckpoint - it will fail on removed dirs (which    * will invalidate the storages)    * 5. write another file    * 6. check that edits and fsimage differ     * 7. run doCheckpoint    * 8. verify that all the image and edits files are the same.    */
annotation|@
name|Test
DECL|method|testStorageRestore ()
specifier|public
name|void
name|testStorageRestore
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numDatanodes
init|=
literal|0
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDatanodes
argument_list|)
operator|.
name|manageNameDfsDirs
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
name|SecondaryNameNode
name|secondary
init|=
operator|new
name|SecondaryNameNode
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****testStorageRestore: Cluster and SNN started"
argument_list|)
expr_stmt|;
name|printStorages
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****testStorageRestore: dir 'test' created, invalidating storage..."
argument_list|)
expr_stmt|;
name|invalidateStorage
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|path2
argument_list|,
name|path3
argument_list|)
argument_list|)
expr_stmt|;
name|printStorages
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****testStorageRestore: storage invalidated"
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
literal|"test1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****testStorageRestore: dir 'test1' created"
argument_list|)
expr_stmt|;
comment|// We did another edit, so the still-active directory at 'path1'
comment|// should now differ from the others
name|FSImageTestUtil
operator|.
name|assertFileContentsDifferent
argument_list|(
literal|2
argument_list|,
operator|new
name|File
argument_list|(
name|path1
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path2
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path3
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|FSImageTestUtil
operator|.
name|assertFileContentsSame
argument_list|(
operator|new
name|File
argument_list|(
name|path2
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path3
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****testStorageRestore: checkfiles(false) run"
argument_list|)
expr_stmt|;
name|secondary
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
comment|///should enable storage..
comment|// We should have a checkpoint through txid 4 in the two image dirs
comment|// (txid=4 for BEGIN, mkdir, mkdir, END)
name|FSImageTestUtil
operator|.
name|assertFileContentsSame
argument_list|(
operator|new
name|File
argument_list|(
name|path1
argument_list|,
literal|"current/"
operator|+
name|getImageFileName
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path2
argument_list|,
literal|"current/"
operator|+
name|getImageFileName
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Should not have any image in an edits-only directory"
argument_list|,
operator|new
name|File
argument_list|(
name|path3
argument_list|,
literal|"current/"
operator|+
name|getImageFileName
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should have finalized logs in the directory that didn't fail
name|assertTrue
argument_list|(
literal|"Should have finalized logs in the directory that didn't fail"
argument_list|,
operator|new
name|File
argument_list|(
name|path1
argument_list|,
literal|"current/"
operator|+
name|getFinalizedEditsFileName
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should not have finalized logs in the failed directories
name|assertFalse
argument_list|(
literal|"Should not have finalized logs in the failed directories"
argument_list|,
operator|new
name|File
argument_list|(
name|path2
argument_list|,
literal|"current/"
operator|+
name|getFinalizedEditsFileName
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Should not have finalized logs in the failed directories"
argument_list|,
operator|new
name|File
argument_list|(
name|path3
argument_list|,
literal|"current/"
operator|+
name|getFinalizedEditsFileName
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// The new log segment should be in all of the directories.
name|FSImageTestUtil
operator|.
name|assertFileContentsSame
argument_list|(
operator|new
name|File
argument_list|(
name|path1
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path2
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path3
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|md5BeforeEdit
init|=
name|FSImageTestUtil
operator|.
name|getFileMD5
argument_list|(
operator|new
name|File
argument_list|(
name|path1
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// The original image should still be the previously failed image
comment|// directory after it got restored, since it's still useful for
comment|// a recovery!
name|FSImageTestUtil
operator|.
name|assertFileContentsSame
argument_list|(
operator|new
name|File
argument_list|(
name|path1
argument_list|,
literal|"current/"
operator|+
name|getImageFileName
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path2
argument_list|,
literal|"current/"
operator|+
name|getImageFileName
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Do another edit to verify that all the logs are active.
name|path
operator|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
comment|// Logs should be changed by the edit.
name|String
name|md5AfterEdit
init|=
name|FSImageTestUtil
operator|.
name|getFileMD5
argument_list|(
operator|new
name|File
argument_list|(
name|path1
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|md5BeforeEdit
operator|.
name|equals
argument_list|(
name|md5AfterEdit
argument_list|)
argument_list|)
expr_stmt|;
comment|// And all logs should be changed.
name|FSImageTestUtil
operator|.
name|assertFileContentsSame
argument_list|(
operator|new
name|File
argument_list|(
name|path1
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path2
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path3
argument_list|,
literal|"current/"
operator|+
name|getInProgressEditsFileName
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|secondary
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// All logs should be finalized by clean shutdown
name|FSImageTestUtil
operator|.
name|assertFileContentsSame
argument_list|(
operator|new
name|File
argument_list|(
name|path1
argument_list|,
literal|"current/"
operator|+
name|getFinalizedEditsFileName
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path2
argument_list|,
literal|"current/"
operator|+
name|getFinalizedEditsFileName
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|path3
argument_list|,
literal|"current/"
operator|+
name|getFinalizedEditsFileName
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test dfsadmin -restoreFailedStorage command    * @throws Exception    */
annotation|@
name|Test
DECL|method|testDfsAdminCmd ()
specifier|public
name|void
name|testDfsAdminCmd
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
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|manageNameDfsDirs
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
try|try
block|{
name|FSImage
name|fsi
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
decl_stmt|;
comment|// it is started with dfs.namenode.name.dir.restore set to true (in SetUp())
name|boolean
name|restore
init|=
name|fsi
operator|.
name|getStorage
argument_list|()
operator|.
name|getRestoreFailedStorage
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Restore is "
operator|+
name|restore
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|restore
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// now run DFSAdmnin command
name|String
name|cmd
init|=
literal|"-fs NAMENODE -restoreFailedStorage false"
decl_stmt|;
name|String
name|namenode
init|=
name|config
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
literal|"file:///"
argument_list|)
decl_stmt|;
name|CommandExecutor
name|executor
init|=
operator|new
name|CLITestCmdDFS
argument_list|(
name|cmd
argument_list|,
operator|new
name|CLICommandDFSAdmin
argument_list|()
argument_list|)
operator|.
name|getExecutor
argument_list|(
name|namenode
argument_list|)
decl_stmt|;
name|executor
operator|.
name|executeCommand
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|restore
operator|=
name|fsi
operator|.
name|getStorage
argument_list|()
operator|.
name|getRestoreFailedStorage
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"After set true call restore is "
operator|+
name|restore
argument_list|,
name|restore
argument_list|)
expr_stmt|;
comment|// run one more time - to set it to true again
name|cmd
operator|=
literal|"-fs NAMENODE -restoreFailedStorage true"
expr_stmt|;
name|executor
operator|.
name|executeCommand
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|restore
operator|=
name|fsi
operator|.
name|getStorage
argument_list|()
operator|.
name|getRestoreFailedStorage
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"After set false call restore is "
operator|+
name|restore
argument_list|,
name|restore
argument_list|)
expr_stmt|;
comment|// run one more time - no change in value
name|cmd
operator|=
literal|"-fs NAMENODE -restoreFailedStorage check"
expr_stmt|;
name|CommandExecutor
operator|.
name|Result
name|cmdResult
init|=
name|executor
operator|.
name|executeCommand
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|restore
operator|=
name|fsi
operator|.
name|getStorage
argument_list|()
operator|.
name|getRestoreFailedStorage
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"After check call restore is "
operator|+
name|restore
argument_list|,
name|restore
argument_list|)
expr_stmt|;
name|String
name|commandOutput
init|=
name|cmdResult
operator|.
name|getCommandOutput
argument_list|()
decl_stmt|;
name|commandOutput
operator|.
name|trim
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|commandOutput
operator|.
name|contains
argument_list|(
literal|"restoreFailedStorage is set to true"
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
comment|/**    * Test to simulate interleaved checkpointing by 2 2NNs after a storage    * directory has been taken offline. The first will cause the directory to    * come back online, but it won't have any valid contents. The second 2NN will    * then try to perform a checkpoint. The NN should not serve up the image or    * edits from the restored (empty) dir.    */
annotation|@
name|Test
DECL|method|testMultipleSecondaryCheckpoint ()
specifier|public
name|void
name|testMultipleSecondaryCheckpoint
parameter_list|()
throws|throws
name|IOException
block|{
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
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|manageNameDfsDirs
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
name|secondary
operator|=
operator|new
name|SecondaryNameNode
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|FSImage
name|fsImage
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
decl_stmt|;
name|printStorages
argument_list|(
name|fsImage
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|testPath
argument_list|)
argument_list|)
expr_stmt|;
name|printStorages
argument_list|(
name|fsImage
argument_list|)
expr_stmt|;
comment|// Take name1 offline
name|invalidateStorage
argument_list|(
name|fsImage
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|path1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Simulate a 2NN beginning a checkpoint, but not finishing. This will
comment|// cause name1 to be restored.
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|printStorages
argument_list|(
name|fsImage
argument_list|)
expr_stmt|;
comment|// Now another 2NN comes along to do a full checkpoint.
name|secondary
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
name|printStorages
argument_list|(
name|fsImage
argument_list|)
expr_stmt|;
comment|// The created file should still exist in the in-memory FS state after the
comment|// checkpoint.
name|assertTrue
argument_list|(
literal|"path exists before restart"
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|testPath
argument_list|)
argument_list|)
expr_stmt|;
name|secondary
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// Restart the NN so it reloads the edits from on-disk.
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
comment|// The created file should still exist after the restart.
name|assertTrue
argument_list|(
literal|"path should still exist after restart"
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|testPath
argument_list|)
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
comment|/**    * 1. create DFS cluster with 3 storage directories    *    - 2 EDITS_IMAGE(name1, name2), 1 EDITS(name3)    * 2. create a file    * 3. corrupt/disable name2 and name3 by removing rwx permission    * 4. run doCheckpoint    *    - will fail on removed dirs (which invalidates them)    * 5. write another file    * 6. check there is only one healthy storage dir    * 7. run doCheckpoint - recover should fail but checkpoint should succeed    * 8. check there is still only one healthy storage dir    * 9. restore the access permission for name2 and name 3, run checkpoint again    * 10.verify there are 3 healthy storage dirs.    */
annotation|@
name|Test
DECL|method|testStorageRestoreFailure ()
specifier|public
name|void
name|testStorageRestoreFailure
parameter_list|()
throws|throws
name|Exception
block|{
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
name|config
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
name|config
argument_list|)
expr_stmt|;
name|printStorages
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
comment|// invalidate storage by removing rwx permission from name2 and name3
name|FileUtil
operator|.
name|chmod
argument_list|(
name|path2
operator|.
name|toString
argument_list|()
argument_list|,
literal|"000"
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|chmod
argument_list|(
name|path3
operator|.
name|toString
argument_list|()
argument_list|,
literal|"000"
argument_list|)
expr_stmt|;
name|secondary
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
comment|// should remove name2 and name3
name|printStorages
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
literal|"test1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getNumStorageDirs
argument_list|()
operator|==
literal|1
operator|)
assert|;
name|secondary
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
comment|// shouldn't be able to restore name 2 and 3
assert|assert
operator|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getNumStorageDirs
argument_list|()
operator|==
literal|1
operator|)
assert|;
name|FileUtil
operator|.
name|chmod
argument_list|(
name|path2
operator|.
name|toString
argument_list|()
argument_list|,
literal|"755"
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|chmod
argument_list|(
name|path3
operator|.
name|toString
argument_list|()
argument_list|,
literal|"755"
argument_list|)
expr_stmt|;
name|secondary
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
comment|// should restore name 2 and 3
assert|assert
operator|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getNumStorageDirs
argument_list|()
operator|==
literal|3
operator|)
assert|;
block|}
finally|finally
block|{
if|if
condition|(
name|path2
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtil
operator|.
name|chmod
argument_list|(
name|path2
operator|.
name|toString
argument_list|()
argument_list|,
literal|"755"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|path3
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtil
operator|.
name|chmod
argument_list|(
name|path3
operator|.
name|toString
argument_list|()
argument_list|,
literal|"755"
argument_list|)
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

