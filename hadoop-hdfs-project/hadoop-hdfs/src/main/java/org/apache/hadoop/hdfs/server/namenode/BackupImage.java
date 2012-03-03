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
name|IOException
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
name|classification
operator|.
name|InterfaceAudience
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
name|HdfsServerConstants
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
name|InconsistentFSStateException
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
name|Storage
operator|.
name|StorageState
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
name|StringUtils
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
comment|/**  * Extension of FSImage for the backup node.  * This class handles the setup of the journaling   * spool on the backup namenode.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BackupImage
specifier|public
class|class
name|BackupImage
extends|extends
name|FSImage
block|{
comment|/** Backup input stream for loading edits into memory */
DECL|field|backupInputStream
specifier|private
name|EditLogBackupInputStream
name|backupInputStream
init|=
operator|new
name|EditLogBackupInputStream
argument_list|(
literal|"Data from remote NameNode"
argument_list|)
decl_stmt|;
comment|/**    * Current state of the BackupNode. The BackupNode's state    * transitions are as follows:    *     * Initial: DROP_UNTIL_NEXT_ROLL    * - Transitions to JOURNAL_ONLY the next time the log rolls    * - Transitions to IN_SYNC in convergeJournalSpool    * - Transitions back to JOURNAL_ONLY if the log rolls while    *   stopApplyingOnNextRoll is true.    */
DECL|field|bnState
specifier|volatile
name|BNState
name|bnState
decl_stmt|;
DECL|enum|BNState
specifier|static
enum|enum
name|BNState
block|{
comment|/**      * Edits from the NN should be dropped. On the next log roll,      * transition to JOURNAL_ONLY state      */
DECL|enumConstant|DROP_UNTIL_NEXT_ROLL
name|DROP_UNTIL_NEXT_ROLL
block|,
comment|/**      * Edits from the NN should be written to the local edits log      * but not applied to the namespace.      */
DECL|enumConstant|JOURNAL_ONLY
name|JOURNAL_ONLY
block|,
comment|/**      * Edits should be written to the local edits log and applied      * to the local namespace.      */
DECL|enumConstant|IN_SYNC
name|IN_SYNC
block|;   }
comment|/**    * Flag to indicate that the next time the NN rolls, the BN    * should transition from to JOURNAL_ONLY state.    * {@see #freezeNamespaceAtNextRoll()}    */
DECL|field|stopApplyingEditsOnNextRoll
specifier|private
name|boolean
name|stopApplyingEditsOnNextRoll
init|=
literal|false
decl_stmt|;
DECL|field|namesystem
specifier|private
name|FSNamesystem
name|namesystem
decl_stmt|;
comment|/**    * Construct a backup image.    * @param conf Configuration    * @throws IOException if storage cannot be initialised.    */
DECL|method|BackupImage (Configuration conf)
name|BackupImage
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|storage
operator|.
name|setDisablePreUpgradableLayoutCheck
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bnState
operator|=
name|BNState
operator|.
name|DROP_UNTIL_NEXT_ROLL
expr_stmt|;
block|}
DECL|method|setNamesystem (FSNamesystem fsn)
name|void
name|setNamesystem
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|)
block|{
name|this
operator|.
name|namesystem
operator|=
name|fsn
expr_stmt|;
block|}
comment|/**    * Analyze backup storage directories for consistency.<br>    * Recover from incomplete checkpoints if required.<br>    * Read VERSION and fstime files if exist.<br>    * Do not load image or edits.    *    * @throws IOException if the node should shutdown.    */
DECL|method|recoverCreateRead ()
name|void
name|recoverCreateRead
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|Iterator
argument_list|<
name|StorageDirectory
argument_list|>
name|it
init|=
name|storage
operator|.
name|dirIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|StorageDirectory
name|sd
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|StorageState
name|curState
decl_stmt|;
try|try
block|{
name|curState
operator|=
name|sd
operator|.
name|analyzeStorage
argument_list|(
name|HdfsServerConstants
operator|.
name|StartupOption
operator|.
name|REGULAR
argument_list|,
name|storage
argument_list|)
expr_stmt|;
comment|// sd is locked but not opened
switch|switch
condition|(
name|curState
condition|)
block|{
case|case
name|NON_EXISTENT
case|:
comment|// fail if any of the configured storage dirs are inaccessible
throw|throw
operator|new
name|InconsistentFSStateException
argument_list|(
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"checkpoint directory does not exist or is not accessible."
argument_list|)
throw|;
case|case
name|NOT_FORMATTED
case|:
comment|// for backup node all directories may be unformatted initially
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
literal|" is not formatted."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Formatting ..."
argument_list|)
expr_stmt|;
name|sd
operator|.
name|clearDirectory
argument_list|()
expr_stmt|;
comment|// create empty current
break|break;
case|case
name|NORMAL
case|:
break|break;
default|default:
comment|// recovery is possible
name|sd
operator|.
name|doRecover
argument_list|(
name|curState
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|curState
operator|!=
name|StorageState
operator|.
name|NOT_FORMATTED
condition|)
block|{
comment|// read and verify consistency with other directories
name|storage
operator|.
name|readProperties
argument_list|(
name|sd
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|sd
operator|.
name|unlock
argument_list|()
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
block|}
comment|/**    * Save meta-data into fsimage files.    * and create empty edits.    */
DECL|method|saveCheckpoint ()
name|void
name|saveCheckpoint
parameter_list|()
throws|throws
name|IOException
block|{
name|saveNamespace
argument_list|(
name|namesystem
argument_list|)
expr_stmt|;
block|}
comment|/**    * Receive a batch of edits from the NameNode.    *     * Depending on bnState, different actions are taken. See    * {@link BackupImage.BNState}    *     * @param firstTxId first txid in batch    * @param numTxns number of transactions    * @param data serialized journal records.    * @throws IOException    * @see #convergeJournalSpool()    */
DECL|method|journal (long firstTxId, int numTxns, byte[] data)
specifier|synchronized
name|void
name|journal
parameter_list|(
name|long
name|firstTxId
parameter_list|,
name|int
name|numTxns
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Got journal, "
operator|+
literal|"state = "
operator|+
name|bnState
operator|+
literal|"; firstTxId = "
operator|+
name|firstTxId
operator|+
literal|"; numTxns = "
operator|+
name|numTxns
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|bnState
condition|)
block|{
case|case
name|DROP_UNTIL_NEXT_ROLL
case|:
return|return;
case|case
name|IN_SYNC
case|:
comment|// update NameSpace in memory
name|applyEdits
argument_list|(
name|firstTxId
argument_list|,
name|numTxns
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
name|JOURNAL_ONLY
case|:
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unhandled state: "
operator|+
name|bnState
argument_list|)
throw|;
block|}
comment|// write to BN's local edit log.
name|logEditsLocally
argument_list|(
name|firstTxId
argument_list|,
name|numTxns
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write the batch of edits to the local copy of the edit logs.    */
DECL|method|logEditsLocally (long firstTxId, int numTxns, byte[] data)
specifier|private
name|void
name|logEditsLocally
parameter_list|(
name|long
name|firstTxId
parameter_list|,
name|int
name|numTxns
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
name|long
name|expectedTxId
init|=
name|editLog
operator|.
name|getLastWrittenTxId
argument_list|()
operator|+
literal|1
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|firstTxId
operator|==
name|expectedTxId
argument_list|,
literal|"received txid batch starting at %s but expected txn %s"
argument_list|,
name|firstTxId
argument_list|,
name|expectedTxId
argument_list|)
expr_stmt|;
name|editLog
operator|.
name|setNextTxId
argument_list|(
name|firstTxId
operator|+
name|numTxns
operator|-
literal|1
argument_list|)
expr_stmt|;
name|editLog
operator|.
name|logEdit
argument_list|(
name|data
operator|.
name|length
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|editLog
operator|.
name|logSync
argument_list|()
expr_stmt|;
block|}
comment|/**    * Apply the batch of edits to the local namespace.    */
DECL|method|applyEdits (long firstTxId, int numTxns, byte[] data)
specifier|private
specifier|synchronized
name|void
name|applyEdits
parameter_list|(
name|long
name|firstTxId
parameter_list|,
name|int
name|numTxns
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|firstTxId
operator|==
name|lastAppliedTxId
operator|+
literal|1
argument_list|,
literal|"Received txn batch starting at %s but expected %s"
argument_list|,
name|firstTxId
argument_list|,
name|lastAppliedTxId
operator|+
literal|1
argument_list|)
expr_stmt|;
assert|assert
name|backupInputStream
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|:
literal|"backup input stream is not empty"
assert|;
try|try
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"data:"
operator|+
name|StringUtils
operator|.
name|byteToHexString
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FSEditLogLoader
name|logLoader
init|=
operator|new
name|FSEditLogLoader
argument_list|(
name|namesystem
argument_list|)
decl_stmt|;
name|int
name|logVersion
init|=
name|storage
operator|.
name|getLayoutVersion
argument_list|()
decl_stmt|;
name|backupInputStream
operator|.
name|setBytes
argument_list|(
name|data
argument_list|,
name|logVersion
argument_list|)
expr_stmt|;
name|long
name|numLoaded
init|=
name|logLoader
operator|.
name|loadEditRecords
argument_list|(
name|logVersion
argument_list|,
name|backupInputStream
argument_list|,
literal|true
argument_list|,
name|lastAppliedTxId
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|numLoaded
operator|!=
name|numTxns
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Batch of txns starting at txnid "
operator|+
name|firstTxId
operator|+
literal|" was supposed to contain "
operator|+
name|numTxns
operator|+
literal|" transactions but only was able to apply "
operator|+
name|numLoaded
argument_list|)
throw|;
block|}
name|lastAppliedTxId
operator|+=
name|numTxns
expr_stmt|;
name|namesystem
operator|.
name|dir
operator|.
name|updateCountForINodeWithQuota
argument_list|()
expr_stmt|;
comment|// inefficient!
block|}
finally|finally
block|{
name|backupInputStream
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Transition the BackupNode from JOURNAL_ONLY state to IN_SYNC state.    * This is done by repeated invocations of tryConvergeJournalSpool until    * we are caught up to the latest in-progress edits file.    */
DECL|method|convergeJournalSpool ()
name|void
name|convergeJournalSpool
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|bnState
operator|==
name|BNState
operator|.
name|JOURNAL_ONLY
argument_list|,
literal|"bad state: %s"
argument_list|,
name|bnState
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|tryConvergeJournalSpool
argument_list|()
condition|)
block|{
empty_stmt|;
block|}
assert|assert
name|bnState
operator|==
name|BNState
operator|.
name|IN_SYNC
assert|;
block|}
DECL|method|tryConvergeJournalSpool ()
specifier|private
name|boolean
name|tryConvergeJournalSpool
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|bnState
operator|==
name|BNState
operator|.
name|JOURNAL_ONLY
argument_list|,
literal|"bad state: %s"
argument_list|,
name|bnState
argument_list|)
expr_stmt|;
comment|// This section is unsynchronized so we can continue to apply
comment|// ahead of where we're reading, concurrently. Since the state
comment|// is JOURNAL_ONLY at this point, we know that lastAppliedTxId
comment|// doesn't change, and curSegmentTxId only increases
while|while
condition|(
name|lastAppliedTxId
operator|<
name|editLog
operator|.
name|getCurSegmentTxId
argument_list|()
operator|-
literal|1
condition|)
block|{
name|long
name|target
init|=
name|editLog
operator|.
name|getCurSegmentTxId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading edits into backupnode to try to catch up from txid "
operator|+
name|lastAppliedTxId
operator|+
literal|" to "
operator|+
name|target
argument_list|)
expr_stmt|;
name|FSImageTransactionalStorageInspector
name|inspector
init|=
operator|new
name|FSImageTransactionalStorageInspector
argument_list|()
decl_stmt|;
name|storage
operator|.
name|inspectStorageDirs
argument_list|(
name|inspector
argument_list|)
expr_stmt|;
name|editLog
operator|.
name|recoverUnclosedStreams
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|EditLogInputStream
argument_list|>
name|editStreamsAll
init|=
name|editLog
operator|.
name|selectInputStreams
argument_list|(
name|lastAppliedTxId
argument_list|,
name|target
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// remove inprogress
name|List
argument_list|<
name|EditLogInputStream
argument_list|>
name|editStreams
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|EditLogInputStream
name|s
range|:
name|editStreamsAll
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getFirstTxId
argument_list|()
operator|!=
name|editLog
operator|.
name|getCurSegmentTxId
argument_list|()
condition|)
block|{
name|editStreams
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|loadEdits
argument_list|(
name|editStreams
argument_list|,
name|namesystem
argument_list|)
expr_stmt|;
block|}
comment|// now, need to load the in-progress file
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|lastAppliedTxId
operator|!=
name|editLog
operator|.
name|getCurSegmentTxId
argument_list|()
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Logs rolled while catching up to current segment"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
comment|// drop lock and try again to load local logs
block|}
name|EditLogInputStream
name|stream
init|=
literal|null
decl_stmt|;
name|Collection
argument_list|<
name|EditLogInputStream
argument_list|>
name|editStreams
init|=
name|getEditLog
argument_list|()
operator|.
name|selectInputStreams
argument_list|(
name|getEditLog
argument_list|()
operator|.
name|getCurSegmentTxId
argument_list|()
argument_list|,
name|getEditLog
argument_list|()
operator|.
name|getCurSegmentTxId
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|EditLogInputStream
name|s
range|:
name|editStreams
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getFirstTxId
argument_list|()
operator|==
name|getEditLog
argument_list|()
operator|.
name|getCurSegmentTxId
argument_list|()
condition|)
block|{
name|stream
operator|=
name|s
expr_stmt|;
block|}
break|break;
block|}
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to find stream starting with "
operator|+
name|editLog
operator|.
name|getCurSegmentTxId
argument_list|()
operator|+
literal|". This indicates that there is an error in synchronization in BackupImage"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
try|try
block|{
name|long
name|remainingTxns
init|=
name|getEditLog
argument_list|()
operator|.
name|getLastWrittenTxId
argument_list|()
operator|-
name|lastAppliedTxId
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Going to finish converging with remaining "
operator|+
name|remainingTxns
operator|+
literal|" txns from in-progress stream "
operator|+
name|stream
argument_list|)
expr_stmt|;
name|FSEditLogLoader
name|loader
init|=
operator|new
name|FSEditLogLoader
argument_list|(
name|namesystem
argument_list|)
decl_stmt|;
name|long
name|numLoaded
init|=
name|loader
operator|.
name|loadFSEdits
argument_list|(
name|stream
argument_list|,
name|lastAppliedTxId
operator|+
literal|1
argument_list|)
decl_stmt|;
name|lastAppliedTxId
operator|+=
name|numLoaded
expr_stmt|;
assert|assert
name|numLoaded
operator|==
name|remainingTxns
operator|:
literal|"expected to load "
operator|+
name|remainingTxns
operator|+
literal|" but loaded "
operator|+
name|numLoaded
operator|+
literal|" from "
operator|+
name|stream
assert|;
block|}
finally|finally
block|{
name|FSEditLog
operator|.
name|closeAllStreams
argument_list|(
name|editStreams
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully synced BackupNode with NameNode at txnid "
operator|+
name|lastAppliedTxId
argument_list|)
expr_stmt|;
name|setState
argument_list|(
name|BNState
operator|.
name|IN_SYNC
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Transition edit log to a new state, logging as necessary.    */
DECL|method|setState (BNState newState)
specifier|private
specifier|synchronized
name|void
name|setState
parameter_list|(
name|BNState
name|newState
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"State transition "
operator|+
name|bnState
operator|+
literal|" -> "
operator|+
name|newState
argument_list|,
operator|new
name|Exception
argument_list|(
literal|"trace"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bnState
operator|=
name|newState
expr_stmt|;
block|}
comment|/**    * Receive a notification that the NameNode has begun a new edit log.    * This causes the BN to also start the new edit log in its local    * directories.    */
DECL|method|namenodeStartedLogSegment (long txid)
specifier|synchronized
name|void
name|namenodeStartedLogSegment
parameter_list|(
name|long
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"NameNode started a new log segment at txid "
operator|+
name|txid
argument_list|)
expr_stmt|;
if|if
condition|(
name|editLog
operator|.
name|isSegmentOpen
argument_list|()
condition|)
block|{
if|if
condition|(
name|editLog
operator|.
name|getLastWrittenTxId
argument_list|()
operator|==
name|txid
operator|-
literal|1
condition|)
block|{
comment|// We are in sync with the NN, so end and finalize the current segment
name|editLog
operator|.
name|endCurrentLogSegment
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// We appear to have missed some transactions -- the NN probably
comment|// lost contact with us temporarily. So, mark the current segment
comment|// as aborted.
name|LOG
operator|.
name|warn
argument_list|(
literal|"NN started new log segment at txid "
operator|+
name|txid
operator|+
literal|", but BN had only written up to txid "
operator|+
name|editLog
operator|.
name|getLastWrittenTxId
argument_list|()
operator|+
literal|"in the log segment starting at "
operator|+
name|editLog
operator|.
name|getCurSegmentTxId
argument_list|()
operator|+
literal|". Aborting this "
operator|+
literal|"log segment."
argument_list|)
expr_stmt|;
name|editLog
operator|.
name|abortCurrentLogSegment
argument_list|()
expr_stmt|;
block|}
block|}
name|editLog
operator|.
name|setNextTxId
argument_list|(
name|txid
argument_list|)
expr_stmt|;
name|editLog
operator|.
name|startLogSegment
argument_list|(
name|txid
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|bnState
operator|==
name|BNState
operator|.
name|DROP_UNTIL_NEXT_ROLL
condition|)
block|{
name|setState
argument_list|(
name|BNState
operator|.
name|JOURNAL_ONLY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stopApplyingEditsOnNextRoll
condition|)
block|{
if|if
condition|(
name|bnState
operator|==
name|BNState
operator|.
name|IN_SYNC
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopped applying edits to prepare for checkpoint."
argument_list|)
expr_stmt|;
name|setState
argument_list|(
name|BNState
operator|.
name|JOURNAL_ONLY
argument_list|)
expr_stmt|;
block|}
name|stopApplyingEditsOnNextRoll
operator|=
literal|false
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Request that the next time the BN receives a log roll, it should    * stop applying the edits log to the local namespace. This is    * typically followed on by a call to {@link #waitUntilNamespaceFrozen()}    */
DECL|method|freezeNamespaceAtNextRoll ()
specifier|synchronized
name|void
name|freezeNamespaceAtNextRoll
parameter_list|()
block|{
name|stopApplyingEditsOnNextRoll
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * After {@link #freezeNamespaceAtNextRoll()} has been called, wait until    * the BN receives notification of the next log roll.    */
DECL|method|waitUntilNamespaceFrozen ()
specifier|synchronized
name|void
name|waitUntilNamespaceFrozen
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bnState
operator|!=
name|BNState
operator|.
name|IN_SYNC
condition|)
return|return;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting until the NameNode rolls its edit logs in order "
operator|+
literal|"to freeze the BackupNode namespace."
argument_list|)
expr_stmt|;
while|while
condition|(
name|bnState
operator|==
name|BNState
operator|.
name|IN_SYNC
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|stopApplyingEditsOnNextRoll
argument_list|,
literal|"If still in sync, we should still have the flag set to "
operator|+
literal|"freeze at next roll"
argument_list|)
expr_stmt|;
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted waiting for namespace to freeze"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"BackupNode namespace frozen."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Override close() so that we don't finalize edit logs.    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|editLog
operator|.
name|abortCurrentLogSegment
argument_list|()
expr_stmt|;
name|storage
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

