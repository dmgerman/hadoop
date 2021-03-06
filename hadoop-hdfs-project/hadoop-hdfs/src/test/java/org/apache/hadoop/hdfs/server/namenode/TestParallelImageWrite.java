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
name|NNStorage
operator|.
name|NameNodeDirType
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
comment|/**  * A JUnit test for checking if restarting DFS preserves integrity.  * Specifically with FSImage being written in parallel  */
end_comment

begin_class
DECL|class|TestParallelImageWrite
specifier|public
class|class
name|TestParallelImageWrite
block|{
DECL|field|NUM_DATANODES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DATANODES
init|=
literal|4
decl_stmt|;
comment|/** check if DFS remains in proper condition after a restart */
annotation|@
name|Test
DECL|method|testRestartDFS ()
specifier|public
name|void
name|testRestartDFS
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|FSNamesystem
name|fsn
init|=
literal|null
decl_stmt|;
name|int
name|numNamenodeDirs
decl_stmt|;
name|DFSTestUtil
name|files
init|=
operator|new
name|DFSTestUtil
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"TestRestartDFS"
argument_list|)
operator|.
name|setNumFiles
argument_list|(
literal|200
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|dir
init|=
literal|"/srcdat"
decl_stmt|;
specifier|final
name|Path
name|rootpath
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|dirpath
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|long
name|rootmtime
decl_stmt|;
name|FileStatus
name|rootstatus
decl_stmt|;
name|FileStatus
name|dirstatus
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
name|format
argument_list|(
literal|true
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|NUM_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|String
index|[]
name|nameNodeDirs
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
decl_stmt|;
name|numNamenodeDirs
operator|=
name|nameNodeDirs
operator|.
name|length
expr_stmt|;
name|assertTrue
argument_list|(
literal|"failed to get number of Namenode StorageDirs"
argument_list|,
name|numNamenodeDirs
operator|!=
literal|0
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
name|files
operator|.
name|createFiles
argument_list|(
name|fs
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|rootmtime
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|rootpath
argument_list|)
operator|.
name|getModificationTime
argument_list|()
expr_stmt|;
name|rootstatus
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|dirpath
argument_list|)
expr_stmt|;
name|dirstatus
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|dirpath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|rootpath
argument_list|,
name|rootstatus
operator|.
name|getOwner
argument_list|()
operator|+
literal|"_XXX"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|dirpath
argument_list|,
literal|null
argument_list|,
name|dirstatus
operator|.
name|getGroup
argument_list|()
operator|+
literal|"_XXX"
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
block|}
try|try
block|{
comment|// Force the NN to save its images on startup so long as
comment|// there are any uncheckpointed txns
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_TXNS_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Here we restart the MiniDFScluster without formatting namenode
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
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|NUM_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Filesystem corrupted after restart."
argument_list|,
name|files
operator|.
name|checkFiles
argument_list|(
name|fs
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|FileStatus
name|newrootstatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|rootpath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|rootmtime
argument_list|,
name|newrootstatus
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootstatus
operator|.
name|getOwner
argument_list|()
operator|+
literal|"_XXX"
argument_list|,
name|newrootstatus
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootstatus
operator|.
name|getGroup
argument_list|()
argument_list|,
name|newrootstatus
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FileStatus
name|newdirstatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|dirpath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|dirstatus
operator|.
name|getOwner
argument_list|()
argument_list|,
name|newdirstatus
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dirstatus
operator|.
name|getGroup
argument_list|()
operator|+
literal|"_XXX"
argument_list|,
name|newdirstatus
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|rootmtime
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|rootpath
argument_list|)
operator|.
name|getModificationTime
argument_list|()
expr_stmt|;
specifier|final
name|String
name|checkAfterRestart
init|=
name|checkImages
argument_list|(
name|fsn
argument_list|,
name|numNamenodeDirs
argument_list|)
decl_stmt|;
comment|// Modify the system and then perform saveNamespace
name|files
operator|.
name|cleanup
argument_list|(
name|fs
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|files
operator|.
name|createFiles
argument_list|(
name|fs
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|saveNamespace
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|checkAfterModify
init|=
name|checkImages
argument_list|(
name|fsn
argument_list|,
name|numNamenodeDirs
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Modified namespace should change fsimage contents. "
operator|+
literal|"was: "
operator|+
name|checkAfterRestart
operator|+
literal|" now: "
operator|+
name|checkAfterModify
argument_list|,
name|checkAfterRestart
operator|.
name|equals
argument_list|(
name|checkAfterModify
argument_list|)
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
name|files
operator|.
name|cleanup
argument_list|(
name|fs
argument_list|,
name|dir
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
block|}
block|}
comment|/**    * Confirm that FSImage files in all StorageDirectory are the same,    * and non-empty, and there are the expected number of them.    * @param fsn - the FSNamesystem being checked.    * @param numImageDirs - the configured number of StorageDirectory of type IMAGE.     * @return - the md5 hash of the most recent FSImage files, which must all be the same.    * @throws AssertionError if image files are empty or different,    *     if less than two StorageDirectory are provided, or if the    *     actual number of StorageDirectory is less than configured.    */
DECL|method|checkImages ( FSNamesystem fsn, int numImageDirs)
specifier|public
specifier|static
name|String
name|checkImages
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|int
name|numImageDirs
parameter_list|)
throws|throws
name|Exception
block|{
name|NNStorage
name|stg
init|=
name|fsn
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
decl_stmt|;
comment|//any failed StorageDirectory is removed from the storageDirs list
name|assertEquals
argument_list|(
literal|"Some StorageDirectories failed Upgrade"
argument_list|,
name|numImageDirs
argument_list|,
name|stg
operator|.
name|getNumStorageDirs
argument_list|(
name|NameNodeDirType
operator|.
name|IMAGE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not enough fsimage copies in MiniDFSCluster "
operator|+
literal|"to test parallel write"
argument_list|,
name|numImageDirs
operator|>
literal|1
argument_list|)
expr_stmt|;
comment|// List of "current/" directory from each SD
name|List
argument_list|<
name|File
argument_list|>
name|dirs
init|=
name|FSImageTestUtil
operator|.
name|getCurrentDirs
argument_list|(
name|stg
argument_list|,
name|NameNodeDirType
operator|.
name|IMAGE
argument_list|)
decl_stmt|;
comment|// across directories, all files with same names should be identical hashes
name|FSImageTestUtil
operator|.
name|assertParallelFilesAreIdentical
argument_list|(
name|dirs
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|FSImageTestUtil
operator|.
name|assertSameNewestImage
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
comment|// Return the hash of the newest image file
name|StorageDirectory
name|firstSd
init|=
name|stg
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|IMAGE
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
name|File
name|latestImage
init|=
name|FSImageTestUtil
operator|.
name|findLatestImageFile
argument_list|(
name|firstSd
argument_list|)
decl_stmt|;
name|String
name|md5
init|=
name|FSImageTestUtil
operator|.
name|getImageFileMD5IgnoringTxId
argument_list|(
name|latestImage
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"md5 of "
operator|+
name|latestImage
operator|+
literal|": "
operator|+
name|md5
argument_list|)
expr_stmt|;
return|return
name|md5
return|;
block|}
block|}
end_class

end_unit

