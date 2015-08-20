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
name|FilenameFilter
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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|Storage
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
name|common
operator|.
name|StorageInfo
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
name|Preconditions
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_class
DECL|class|NNUpgradeUtil
specifier|public
specifier|abstract
class|class
name|NNUpgradeUtil
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
name|NNUpgradeUtil
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Return true if this storage dir can roll back to the previous storage    * state, false otherwise. The NN will refuse to run the rollback operation    * unless at least one JM or fsimage storage directory can roll back.    *     * @param storage the storage info for the current state    * @param prevStorage the storage info for the previous (unupgraded) state    * @param targetLayoutVersion the layout version we intend to roll back to    * @return true if this JM can roll back, false otherwise.    * @throws IOException in the event of error    */
DECL|method|canRollBack (StorageDirectory sd, StorageInfo storage, StorageInfo prevStorage, int targetLayoutVersion)
specifier|static
name|boolean
name|canRollBack
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|,
name|StorageInfo
name|storage
parameter_list|,
name|StorageInfo
name|prevStorage
parameter_list|,
name|int
name|targetLayoutVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|prevDir
init|=
name|sd
operator|.
name|getPreviousDir
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|prevDir
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// use current directory then
name|LOG
operator|.
name|info
argument_list|(
literal|"Storage directory "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
operator|+
literal|" does not contain previous fs state."
argument_list|)
expr_stmt|;
comment|// read and verify consistency with other directories
name|storage
operator|.
name|readProperties
argument_list|(
name|sd
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// read and verify consistency of the prev dir
name|prevStorage
operator|.
name|readPreviousVersionProperties
argument_list|(
name|sd
argument_list|)
expr_stmt|;
if|if
condition|(
name|prevStorage
operator|.
name|getLayoutVersion
argument_list|()
operator|!=
name|targetLayoutVersion
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot rollback to storage version "
operator|+
name|prevStorage
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|" using this version of the NameNode, which uses storage version "
operator|+
name|targetLayoutVersion
operator|+
literal|". "
operator|+
literal|"Please use the previous version of HDFS to perform the rollback."
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Finalize the upgrade. The previous dir, if any, will be renamed and    * removed. After this is completed, rollback is no longer allowed.    *     * @param sd the storage directory to finalize    * @throws IOException in the event of error    */
DECL|method|doFinalize (StorageDirectory sd)
specifier|static
name|void
name|doFinalize
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|prevDir
init|=
name|sd
operator|.
name|getPreviousDir
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|prevDir
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// already discarded
name|LOG
operator|.
name|info
argument_list|(
literal|"Directory "
operator|+
name|prevDir
operator|+
literal|" does not exist."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finalize upgrade for "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
operator|+
literal|" is not required."
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Finalizing upgrade of storage directory "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|sd
operator|.
name|getCurrentDir
argument_list|()
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Current directory must exist."
argument_list|)
expr_stmt|;
specifier|final
name|File
name|tmpDir
init|=
name|sd
operator|.
name|getFinalizedTmp
argument_list|()
decl_stmt|;
comment|// rename previous to tmp and remove
name|NNStorage
operator|.
name|rename
argument_list|(
name|prevDir
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
name|NNStorage
operator|.
name|deleteDir
argument_list|(
name|tmpDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finalize upgrade for "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
operator|+
literal|" is complete."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Perform any steps that must succeed across all storage dirs/JournalManagers    * involved in an upgrade before proceeding onto the actual upgrade stage. If    * a call to any JM's or local storage dir's doPreUpgrade method fails, then    * doUpgrade will not be called for any JM. The existing current dir is    * renamed to previous.tmp, and then a new, empty current dir is created.    *    * @param conf configuration for creating {@link EditLogFileOutputStream}    * @param sd the storage directory to perform the pre-upgrade procedure.    * @throws IOException in the event of error    */
DECL|method|doPreUpgrade (Configuration conf, StorageDirectory sd)
specifier|static
name|void
name|doPreUpgrade
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting upgrade of storage directory "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
comment|// rename current to tmp
name|renameCurToTmp
argument_list|(
name|sd
argument_list|)
expr_stmt|;
specifier|final
name|File
name|curDir
init|=
name|sd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
specifier|final
name|File
name|tmpDir
init|=
name|sd
operator|.
name|getPreviousTmp
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fileNameList
init|=
name|IOUtils
operator|.
name|listDirectory
argument_list|(
name|tmpDir
argument_list|,
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|dir
operator|.
name|equals
argument_list|(
name|tmpDir
argument_list|)
operator|&&
name|name
operator|.
name|startsWith
argument_list|(
name|NNStorage
operator|.
name|NameNodeFile
operator|.
name|EDITS
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|fileNameList
control|)
block|{
name|File
name|prevFile
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
name|s
argument_list|)
decl_stmt|;
name|File
name|newFile
init|=
operator|new
name|File
argument_list|(
name|curDir
argument_list|,
name|prevFile
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createLink
argument_list|(
name|newFile
operator|.
name|toPath
argument_list|()
argument_list|,
name|prevFile
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Rename the existing current dir to previous.tmp, and create a new empty    * current dir.    */
DECL|method|renameCurToTmp (StorageDirectory sd)
specifier|public
specifier|static
name|void
name|renameCurToTmp
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|curDir
init|=
name|sd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|File
name|prevDir
init|=
name|sd
operator|.
name|getPreviousDir
argument_list|()
decl_stmt|;
specifier|final
name|File
name|tmpDir
init|=
name|sd
operator|.
name|getPreviousTmp
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|curDir
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Current directory must exist for preupgrade."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|prevDir
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Previous directory must not exist for preupgrade."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|tmpDir
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Previous.tmp directory must not exist for preupgrade."
operator|+
literal|"Consider restarting for recovery."
argument_list|)
expr_stmt|;
comment|// rename current to tmp
name|NNStorage
operator|.
name|rename
argument_list|(
name|curDir
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|curDir
operator|.
name|mkdir
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create directory "
operator|+
name|curDir
argument_list|)
throw|;
block|}
block|}
comment|/**    * Perform the upgrade of the storage dir to the given storage info. The new    * storage info is written into the current directory, and the previous.tmp    * directory is renamed to previous.    *     * @param sd the storage directory to upgrade    * @param storage info about the new upgraded versions.    * @throws IOException in the event of error    */
DECL|method|doUpgrade (StorageDirectory sd, Storage storage)
specifier|public
specifier|static
name|void
name|doUpgrade
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|,
name|Storage
name|storage
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Performing upgrade of storage directory "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Write the version file, since saveFsImage only makes the
comment|// fsimage_<txid>, and the directory is otherwise empty.
name|storage
operator|.
name|writeProperties
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|File
name|prevDir
init|=
name|sd
operator|.
name|getPreviousDir
argument_list|()
decl_stmt|;
name|File
name|tmpDir
init|=
name|sd
operator|.
name|getPreviousTmp
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|prevDir
operator|.
name|exists
argument_list|()
argument_list|,
literal|"previous directory must not exist for upgrade."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|tmpDir
operator|.
name|exists
argument_list|()
argument_list|,
literal|"previous.tmp directory must exist for upgrade."
argument_list|)
expr_stmt|;
comment|// rename tmp to previous
name|NNStorage
operator|.
name|rename
argument_list|(
name|tmpDir
argument_list|,
name|prevDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to rename temp to previous for "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
comment|/**    * Perform rollback of the storage dir to the previous state. The existing    * current dir is removed, and the previous dir is renamed to current.    *     * @param sd the storage directory to roll back.    * @throws IOException in the event of error    */
DECL|method|doRollBack (StorageDirectory sd)
specifier|static
name|void
name|doRollBack
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|prevDir
init|=
name|sd
operator|.
name|getPreviousDir
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|prevDir
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return;
block|}
name|File
name|tmpDir
init|=
name|sd
operator|.
name|getRemovedTmp
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|tmpDir
operator|.
name|exists
argument_list|()
argument_list|,
literal|"removed.tmp directory must not exist for rollback."
operator|+
literal|"Consider restarting for recovery."
argument_list|)
expr_stmt|;
comment|// rename current to tmp
name|File
name|curDir
init|=
name|sd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|curDir
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Current directory must exist for rollback."
argument_list|)
expr_stmt|;
name|NNStorage
operator|.
name|rename
argument_list|(
name|curDir
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
comment|// rename previous to current
name|NNStorage
operator|.
name|rename
argument_list|(
name|prevDir
argument_list|,
name|curDir
argument_list|)
expr_stmt|;
comment|// delete tmp dir
name|NNStorage
operator|.
name|deleteDir
argument_list|(
name|tmpDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Rollback of "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
operator|+
literal|" is complete."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

