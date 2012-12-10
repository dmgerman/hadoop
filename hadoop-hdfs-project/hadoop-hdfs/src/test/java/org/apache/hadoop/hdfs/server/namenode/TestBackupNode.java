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
name|assertNotNull
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
name|net
operator|.
name|InetSocketAddress
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|HdfsServerConstants
operator|.
name|NamenodeRole
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
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|FileJournalManager
operator|.
name|EditLogFile
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
name|protocol
operator|.
name|NamenodeProtocols
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
name|net
operator|.
name|NetUtils
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
name|log4j
operator|.
name|Level
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|ImmutableList
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

begin_class
DECL|class|TestBackupNode
specifier|public
class|class
name|TestBackupNode
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestBackupNode
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|Checkpointer
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|BackupImage
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|BASE_DIR
specifier|static
specifier|final
name|String
name|BASE_DIR
init|=
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
decl_stmt|;
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
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
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|BASE_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseDir
operator|.
name|exists
argument_list|()
condition|)
if|if
condition|(
operator|!
operator|(
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|baseDir
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot remove directory: "
operator|+
name|baseDir
argument_list|)
throw|;
name|File
name|dirC
init|=
operator|new
name|File
argument_list|(
name|getBackupNodeDir
argument_list|(
name|StartupOption
operator|.
name|CHECKPOINT
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|dirC
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|dirB
init|=
operator|new
name|File
argument_list|(
name|getBackupNodeDir
argument_list|(
name|StartupOption
operator|.
name|BACKUP
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|dirB
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dirB
operator|=
operator|new
name|File
argument_list|(
name|getBackupNodeDir
argument_list|(
name|StartupOption
operator|.
name|BACKUP
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|dirB
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
DECL|method|getBackupNodeDir (StartupOption t, int idx)
specifier|static
name|String
name|getBackupNodeDir
parameter_list|(
name|StartupOption
name|t
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
return|return
name|BASE_DIR
operator|+
literal|"name"
operator|+
name|t
operator|.
name|getName
argument_list|()
operator|+
name|idx
operator|+
literal|"/"
return|;
block|}
DECL|method|startBackupNode (Configuration conf, StartupOption startupOpt, int idx)
name|BackupNode
name|startBackupNode
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|StartupOption
name|startupOpt
parameter_list|,
name|int
name|idx
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|c
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|dirs
init|=
name|getBackupNodeDir
argument_list|(
name|startupOpt
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|c
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|dirs
argument_list|)
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
literal|"${"
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|BackupNode
name|bn
init|=
operator|(
name|BackupNode
operator|)
name|NameNode
operator|.
name|createNameNode
argument_list|(
operator|new
name|String
index|[]
block|{
name|startupOpt
operator|.
name|getName
argument_list|()
block|}
argument_list|,
name|c
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bn
operator|.
name|getRole
argument_list|()
operator|+
literal|" must be in SafeMode."
argument_list|,
name|bn
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bn
operator|.
name|getRole
argument_list|()
operator|+
literal|" must be in StandbyState"
argument_list|,
name|bn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getHAState
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bn
return|;
block|}
DECL|method|waitCheckpointDone (MiniDFSCluster cluster, long txid)
name|void
name|waitCheckpointDone
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|long
name|txid
parameter_list|)
block|{
name|long
name|thisCheckpointTxId
decl_stmt|;
do|do
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting checkpoint to complete... "
operator|+
literal|"checkpoint txid should increase above "
operator|+
name|txid
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
comment|// The checkpoint is not done until the nn has received it from the bn
name|thisCheckpointTxId
operator|=
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
name|getMostRecentCheckpointTxId
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|thisCheckpointTxId
operator|<
name|txid
condition|)
do|;
comment|// Check that the checkpoint got uploaded to NN successfully
name|FSImageTestUtil
operator|.
name|assertNNHasCheckpoints
argument_list|(
name|cluster
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|(
name|int
operator|)
name|thisCheckpointTxId
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCheckpointNode ()
specifier|public
name|void
name|testCheckpointNode
parameter_list|()
throws|throws
name|Exception
block|{
name|testCheckpoint
argument_list|(
name|StartupOption
operator|.
name|CHECKPOINT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensure that the backupnode will tail edits from the NN    * and keep in sync, even while the NN rolls, checkpoints    * occur, etc.    */
annotation|@
name|Test
DECL|method|testBackupNodeTailsEdits ()
specifier|public
name|void
name|testBackupNodeTailsEdits
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
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
literal|null
decl_stmt|;
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
name|BackupNode
name|backup
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
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fileSys
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|backup
operator|=
name|startBackupNode
argument_list|(
name|conf
argument_list|,
name|StartupOption
operator|.
name|BACKUP
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|BackupImage
name|bnImage
init|=
operator|(
name|BackupImage
operator|)
name|backup
operator|.
name|getFSImage
argument_list|()
decl_stmt|;
name|testBNInSync
argument_list|(
name|cluster
argument_list|,
name|backup
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Force a roll -- BN should roll with NN.
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|NamenodeProtocols
name|nnRpc
init|=
name|nn
operator|.
name|getRpcServer
argument_list|()
decl_stmt|;
name|nnRpc
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|bnImage
operator|.
name|getEditLog
argument_list|()
operator|.
name|getCurSegmentTxId
argument_list|()
argument_list|,
name|nn
operator|.
name|getFSImage
argument_list|()
operator|.
name|getEditLog
argument_list|()
operator|.
name|getCurSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
comment|// BN should stay in sync after roll
name|testBNInSync
argument_list|(
name|cluster
argument_list|,
name|backup
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|long
name|nnImageBefore
init|=
name|nn
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
decl_stmt|;
comment|// BN checkpoint
name|backup
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
comment|// NN should have received a new image
name|long
name|nnImageAfter
init|=
name|nn
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"nn should have received new checkpoint. before: "
operator|+
name|nnImageBefore
operator|+
literal|" after: "
operator|+
name|nnImageAfter
argument_list|,
name|nnImageAfter
operator|>
name|nnImageBefore
argument_list|)
expr_stmt|;
comment|// BN should stay in sync after checkpoint
name|testBNInSync
argument_list|(
name|cluster
argument_list|,
name|backup
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// Stop BN
name|StorageDirectory
name|sd
init|=
name|bnImage
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageDir
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|backup
operator|.
name|stop
argument_list|()
expr_stmt|;
name|backup
operator|=
literal|null
expr_stmt|;
comment|// When shutting down the BN, it shouldn't finalize logs that are
comment|// still open on the NN
name|EditLogFile
name|editsLog
init|=
name|FSImageTestUtil
operator|.
name|findLatestEditsLog
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|editsLog
operator|.
name|getFirstTxId
argument_list|()
argument_list|,
name|nn
operator|.
name|getFSImage
argument_list|()
operator|.
name|getEditLog
argument_list|()
operator|.
name|getCurSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should not have finalized "
operator|+
name|editsLog
argument_list|,
name|editsLog
operator|.
name|isInProgress
argument_list|()
argument_list|)
expr_stmt|;
comment|// do some edits
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/edit-while-bn-down"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// start a new backup node
name|backup
operator|=
name|startBackupNode
argument_list|(
name|conf
argument_list|,
name|StartupOption
operator|.
name|BACKUP
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|testBNInSync
argument_list|(
name|cluster
argument_list|,
name|backup
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|backup
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFileInfo
argument_list|(
literal|"/edit-while-bn-down"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Trigger an unclean shutdown of the backup node. Backup node will not
comment|// unregister from the active when this is done simulating a node crash.
name|backup
operator|.
name|stop
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// do some edits on the active. This should go through without failing.
comment|// This will verify that active is still up and can add entries to
comment|// master editlog.
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/edit-while-bn-down-2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down..."
argument_list|)
expr_stmt|;
if|if
condition|(
name|backup
operator|!=
literal|null
condition|)
name|backup
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
name|fileSys
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|assertStorageDirsMatch
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
name|backup
argument_list|)
expr_stmt|;
block|}
DECL|method|testBNInSync (MiniDFSCluster cluster, final BackupNode backup, int testIdx)
specifier|private
name|void
name|testBNInSync
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
specifier|final
name|BackupNode
name|backup
parameter_list|,
name|int
name|testIdx
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Do a bunch of namespace operations, make sure they're replicated
comment|// to the BN.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|src
init|=
literal|"/test_"
operator|+
name|testIdx
operator|+
literal|"_"
operator|+
name|i
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating "
operator|+
name|src
operator|+
literal|" on NN"
argument_list|)
expr_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking for "
operator|+
name|src
operator|+
literal|" on BN"
argument_list|)
expr_stmt|;
try|try
block|{
name|boolean
name|hasFile
init|=
name|backup
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|src
argument_list|,
literal|false
argument_list|)
operator|!=
literal|null
decl_stmt|;
name|boolean
name|txnIdMatch
init|=
name|backup
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getTransactionID
argument_list|()
operator|==
name|nn
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getTransactionID
argument_list|()
decl_stmt|;
return|return
name|hasFile
operator|&&
name|txnIdMatch
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|,
literal|30
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
name|assertStorageDirsMatch
argument_list|(
name|nn
argument_list|,
name|backup
argument_list|)
expr_stmt|;
block|}
DECL|method|assertStorageDirsMatch (final NameNode nn, final BackupNode backup)
specifier|private
name|void
name|assertStorageDirsMatch
parameter_list|(
specifier|final
name|NameNode
name|nn
parameter_list|,
specifier|final
name|BackupNode
name|backup
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Check that the stored files in the name dirs are identical
name|List
argument_list|<
name|File
argument_list|>
name|dirs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|FSImageTestUtil
operator|.
name|getCurrentDirs
argument_list|(
name|nn
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|dirs
operator|.
name|addAll
argument_list|(
name|FSImageTestUtil
operator|.
name|getCurrentDirs
argument_list|(
name|backup
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|FSImageTestUtil
operator|.
name|assertParallelFilesAreIdentical
argument_list|(
name|dirs
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"VERSION"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBackupNode ()
specifier|public
name|void
name|testBackupNode
parameter_list|()
throws|throws
name|Exception
block|{
name|testCheckpoint
argument_list|(
name|StartupOption
operator|.
name|BACKUP
argument_list|)
expr_stmt|;
block|}
DECL|method|testCheckpoint (StartupOption op)
name|void
name|testCheckpoint
parameter_list|(
name|StartupOption
name|op
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/checkpoint.dat"
argument_list|)
decl_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
literal|"/checkpoint2.dat"
argument_list|)
decl_stmt|;
name|Path
name|file3
init|=
operator|new
name|Path
argument_list|(
literal|"/backup.dat"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
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
name|short
name|replication
init|=
operator|(
name|short
operator|)
name|conf
operator|.
name|getInt
argument_list|(
literal|"dfs.replication"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|int
name|numDatanodes
init|=
name|Math
operator|.
name|max
argument_list|(
literal|3
argument_list|,
name|replication
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INITIAL_DELAY_KEY
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SCAN_PERIOD_HOURS_KEY
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// disable block scanner
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
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
name|BackupNode
name|backup
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
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fileSys
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
comment|//
comment|// verify that 'format' really blew away all pre-existing files
comment|//
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|file2
argument_list|)
argument_list|)
expr_stmt|;
comment|//
comment|// Create file1
comment|//
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|mkdirs
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
comment|//
comment|// Take a checkpoint
comment|//
name|long
name|txid
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getTransactionID
argument_list|()
decl_stmt|;
name|backup
operator|=
name|startBackupNode
argument_list|(
name|conf
argument_list|,
name|op
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|waitCheckpointDone
argument_list|(
name|cluster
argument_list|,
name|txid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in TestBackupNode:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|backup
operator|!=
literal|null
condition|)
name|backup
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
name|fileSys
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|File
name|nnCurDir
init|=
operator|new
name|File
argument_list|(
name|BASE_DIR
argument_list|,
literal|"name1/current/"
argument_list|)
decl_stmt|;
name|File
name|bnCurDir
init|=
operator|new
name|File
argument_list|(
name|getBackupNodeDir
argument_list|(
name|op
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"/current/"
argument_list|)
decl_stmt|;
name|FSImageTestUtil
operator|.
name|assertParallelFilesAreIdentical
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|bnCurDir
argument_list|,
name|nnCurDir
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|of
argument_list|(
literal|"VERSION"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
comment|//
comment|// Restart cluster and verify that file1 still exist.
comment|//
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
name|numDatanodes
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fileSys
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
comment|// check that file1 still exists
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|file1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// create new file file2
name|fileSys
operator|.
name|mkdirs
argument_list|(
name|file2
argument_list|)
expr_stmt|;
comment|//
comment|// Take a checkpoint
comment|//
name|backup
operator|=
name|startBackupNode
argument_list|(
name|conf
argument_list|,
name|op
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|long
name|txid
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getTransactionID
argument_list|()
decl_stmt|;
name|waitCheckpointDone
argument_list|(
name|cluster
argument_list|,
name|txid
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|fileSys
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file_"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|txid
operator|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getTransactionID
argument_list|()
expr_stmt|;
name|backup
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
name|waitCheckpointDone
argument_list|(
name|cluster
argument_list|,
name|txid
argument_list|)
expr_stmt|;
name|txid
operator|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getTransactionID
argument_list|()
expr_stmt|;
name|backup
operator|.
name|doCheckpoint
argument_list|()
expr_stmt|;
name|waitCheckpointDone
argument_list|(
name|cluster
argument_list|,
name|txid
argument_list|)
expr_stmt|;
comment|// Try BackupNode operations
name|InetSocketAddress
name|add
init|=
name|backup
operator|.
name|getNameNodeAddress
argument_list|()
decl_stmt|;
comment|// Write to BN
name|FileSystem
name|bnFS
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|Path
argument_list|(
literal|"hdfs://"
operator|+
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|add
argument_list|)
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|boolean
name|canWrite
init|=
literal|true
decl_stmt|;
try|try
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|bnFS
argument_list|,
name|file3
argument_list|,
name|fileSize
argument_list|,
name|fileSize
argument_list|,
name|blockSize
argument_list|,
name|replication
argument_list|,
name|seed
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|eio
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Write to "
operator|+
name|backup
operator|.
name|getRole
argument_list|()
operator|+
literal|" failed as expected: "
argument_list|,
name|eio
argument_list|)
expr_stmt|;
name|canWrite
operator|=
literal|false
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"Write to BackupNode must be prohibited."
argument_list|,
name|canWrite
argument_list|)
expr_stmt|;
comment|// Reads are allowed for BackupNode, but not for CheckpointNode
name|boolean
name|canRead
init|=
literal|true
decl_stmt|;
try|try
block|{
name|bnFS
operator|.
name|exists
argument_list|(
name|file2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|eio
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Read from "
operator|+
name|backup
operator|.
name|getRole
argument_list|()
operator|+
literal|" failed: "
argument_list|,
name|eio
argument_list|)
expr_stmt|;
name|canRead
operator|=
literal|false
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Reads to BackupNode are allowed, but not CheckpointNode."
argument_list|,
name|canRead
argument_list|,
name|backup
operator|.
name|isRole
argument_list|(
name|NamenodeRole
operator|.
name|BACKUP
argument_list|)
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|file3
argument_list|,
name|fileSize
argument_list|,
name|fileSize
argument_list|,
name|blockSize
argument_list|,
name|replication
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|TestCheckpoint
operator|.
name|checkFile
argument_list|(
name|fileSys
argument_list|,
name|file3
argument_list|,
name|replication
argument_list|)
expr_stmt|;
comment|// should also be on BN right away
name|assertTrue
argument_list|(
literal|"file3 does not exist on BackupNode"
argument_list|,
name|op
operator|!=
name|StartupOption
operator|.
name|BACKUP
operator|||
name|backup
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|file3
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in TestBackupNode:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|backup
operator|!=
literal|null
condition|)
name|backup
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
name|fileSys
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|FSImageTestUtil
operator|.
name|assertParallelFilesAreIdentical
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|bnCurDir
argument_list|,
name|nnCurDir
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|of
argument_list|(
literal|"VERSION"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
comment|//
comment|// Restart cluster and verify that file2 exists and
comment|// file1 does not exist.
comment|//
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
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fileSys
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify that file2 exists
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|file2
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in TestBackupNode:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileSys
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

