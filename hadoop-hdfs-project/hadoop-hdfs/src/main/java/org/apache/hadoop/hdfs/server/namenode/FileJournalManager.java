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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|protocol
operator|.
name|HdfsConstants
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
name|NNStorageRetentionManager
operator|.
name|StoragePurger
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
name|FSEditLogLoader
operator|.
name|EditLogValidation
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
name|NameNodeFile
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
name|RemoteEditLog
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
name|annotations
operator|.
name|VisibleForTesting
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
name|ComparisonChain
import|;
end_import

begin_comment
comment|/**  * Journal manager for the common case of edits files being written  * to a storage directory.  *   * Note: this class is not thread-safe and should be externally  * synchronized.  */
end_comment

begin_class
DECL|class|FileJournalManager
class|class
name|FileJournalManager
implements|implements
name|JournalManager
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
name|FileJournalManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sd
specifier|private
specifier|final
name|StorageDirectory
name|sd
decl_stmt|;
DECL|field|storage
specifier|private
specifier|final
name|NNStorage
name|storage
decl_stmt|;
DECL|field|outputBufferCapacity
specifier|private
name|int
name|outputBufferCapacity
init|=
literal|512
operator|*
literal|1024
decl_stmt|;
DECL|field|EDITS_REGEX
specifier|private
specifier|static
specifier|final
name|Pattern
name|EDITS_REGEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|NameNodeFile
operator|.
name|EDITS
operator|.
name|getName
argument_list|()
operator|+
literal|"_(\\d+)-(\\d+)"
argument_list|)
decl_stmt|;
DECL|field|EDITS_INPROGRESS_REGEX
specifier|private
specifier|static
specifier|final
name|Pattern
name|EDITS_INPROGRESS_REGEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|NameNodeFile
operator|.
name|EDITS_INPROGRESS
operator|.
name|getName
argument_list|()
operator|+
literal|"_(\\d+)"
argument_list|)
decl_stmt|;
DECL|field|currentInProgress
specifier|private
name|File
name|currentInProgress
init|=
literal|null
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|purger
name|StoragePurger
name|purger
init|=
operator|new
name|NNStorageRetentionManager
operator|.
name|DeletionStoragePurger
argument_list|()
decl_stmt|;
DECL|method|FileJournalManager (StorageDirectory sd, NNStorage storage)
specifier|public
name|FileJournalManager
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|,
name|NNStorage
name|storage
parameter_list|)
block|{
name|this
operator|.
name|sd
operator|=
name|sd
expr_stmt|;
name|this
operator|.
name|storage
operator|=
name|storage
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|startLogSegment (long txid)
specifier|synchronized
specifier|public
name|EditLogOutputStream
name|startLogSegment
parameter_list|(
name|long
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|currentInProgress
operator|=
name|NNStorage
operator|.
name|getInProgressEditsFile
argument_list|(
name|sd
argument_list|,
name|txid
argument_list|)
expr_stmt|;
name|EditLogOutputStream
name|stm
init|=
operator|new
name|EditLogFileOutputStream
argument_list|(
name|currentInProgress
argument_list|,
name|outputBufferCapacity
argument_list|)
decl_stmt|;
name|stm
operator|.
name|create
argument_list|()
expr_stmt|;
return|return
name|stm
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|storage
operator|.
name|reportErrorsOnDirectory
argument_list|(
name|sd
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|finalizeLogSegment (long firstTxId, long lastTxId)
specifier|synchronized
specifier|public
name|void
name|finalizeLogSegment
parameter_list|(
name|long
name|firstTxId
parameter_list|,
name|long
name|lastTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|inprogressFile
init|=
name|NNStorage
operator|.
name|getInProgressEditsFile
argument_list|(
name|sd
argument_list|,
name|firstTxId
argument_list|)
decl_stmt|;
name|File
name|dstFile
init|=
name|NNStorage
operator|.
name|getFinalizedEditsFile
argument_list|(
name|sd
argument_list|,
name|firstTxId
argument_list|,
name|lastTxId
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finalizing edits file "
operator|+
name|inprogressFile
operator|+
literal|" -> "
operator|+
name|dstFile
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|dstFile
operator|.
name|exists
argument_list|()
argument_list|,
literal|"Can't finalize edits file "
operator|+
name|inprogressFile
operator|+
literal|" since finalized file "
operator|+
literal|"already exists"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|inprogressFile
operator|.
name|renameTo
argument_list|(
name|dstFile
argument_list|)
condition|)
block|{
name|storage
operator|.
name|reportErrorsOnDirectory
argument_list|(
name|sd
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to finalize edits file "
operator|+
name|inprogressFile
argument_list|)
throw|;
block|}
if|if
condition|(
name|inprogressFile
operator|.
name|equals
argument_list|(
name|currentInProgress
argument_list|)
condition|)
block|{
name|currentInProgress
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getStorageDirectory ()
specifier|public
name|StorageDirectory
name|getStorageDirectory
parameter_list|()
block|{
return|return
name|sd
return|;
block|}
annotation|@
name|Override
DECL|method|setOutputBufferCapacity (int size)
specifier|synchronized
specifier|public
name|void
name|setOutputBufferCapacity
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|outputBufferCapacity
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|purgeLogsOlderThan (long minTxIdToKeep)
specifier|public
name|void
name|purgeLogsOlderThan
parameter_list|(
name|long
name|minTxIdToKeep
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Purging logs older than "
operator|+
name|minTxIdToKeep
argument_list|)
expr_stmt|;
name|File
index|[]
name|files
init|=
name|FileUtil
operator|.
name|listFiles
argument_list|(
name|sd
operator|.
name|getCurrentDir
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|EditLogFile
argument_list|>
name|editLogs
init|=
name|FileJournalManager
operator|.
name|matchEditLogs
argument_list|(
name|files
argument_list|)
decl_stmt|;
for|for
control|(
name|EditLogFile
name|log
range|:
name|editLogs
control|)
block|{
if|if
condition|(
name|log
operator|.
name|getFirstTxId
argument_list|()
operator|<
name|minTxIdToKeep
operator|&&
name|log
operator|.
name|getLastTxId
argument_list|()
operator|<
name|minTxIdToKeep
condition|)
block|{
name|purger
operator|.
name|purgeLog
argument_list|(
name|log
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Find all editlog segments starting at or above the given txid.    * @param fromTxId the txnid which to start looking    * @return a list of remote edit logs    * @throws IOException if edit logs cannot be listed.    */
DECL|method|getRemoteEditLogs (long firstTxId)
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|getRemoteEditLogs
parameter_list|(
name|long
name|firstTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|currentDir
init|=
name|sd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|EditLogFile
argument_list|>
name|allLogFiles
init|=
name|matchEditLogs
argument_list|(
name|currentDir
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|ret
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|allLogFiles
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|EditLogFile
name|elf
range|:
name|allLogFiles
control|)
block|{
if|if
condition|(
name|elf
operator|.
name|hasCorruptHeader
argument_list|()
operator|||
name|elf
operator|.
name|isInProgress
argument_list|()
condition|)
continue|continue;
if|if
condition|(
name|elf
operator|.
name|getFirstTxId
argument_list|()
operator|>=
name|firstTxId
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
operator|new
name|RemoteEditLog
argument_list|(
name|elf
operator|.
name|firstTxId
argument_list|,
name|elf
operator|.
name|lastTxId
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|firstTxId
operator|>
name|elf
operator|.
name|getFirstTxId
argument_list|()
operator|)
operator|&&
operator|(
name|firstTxId
operator|<=
name|elf
operator|.
name|getLastTxId
argument_list|()
operator|)
condition|)
block|{
comment|// Note that this behavior is different from getLogFiles below.
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Asked for firstTxId "
operator|+
name|firstTxId
operator|+
literal|" which is in the middle of file "
operator|+
name|elf
operator|.
name|file
argument_list|)
throw|;
block|}
block|}
return|return
name|ret
return|;
block|}
comment|/**    * returns matching edit logs via the log directory. Simple helper function    * that lists the files in the logDir and calls matchEditLogs(File[])    *     * @param logDir    *          directory to match edit logs in    * @return matched edit logs    * @throws IOException    *           IOException thrown for invalid logDir    */
DECL|method|matchEditLogs (File logDir)
specifier|static
name|List
argument_list|<
name|EditLogFile
argument_list|>
name|matchEditLogs
parameter_list|(
name|File
name|logDir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|matchEditLogs
argument_list|(
name|FileUtil
operator|.
name|listFiles
argument_list|(
name|logDir
argument_list|)
argument_list|)
return|;
block|}
DECL|method|matchEditLogs (File[] filesInStorage)
specifier|static
name|List
argument_list|<
name|EditLogFile
argument_list|>
name|matchEditLogs
parameter_list|(
name|File
index|[]
name|filesInStorage
parameter_list|)
block|{
name|List
argument_list|<
name|EditLogFile
argument_list|>
name|ret
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|filesInStorage
control|)
block|{
name|String
name|name
init|=
name|f
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// Check for edits
name|Matcher
name|editsMatch
init|=
name|EDITS_REGEX
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|editsMatch
operator|.
name|matches
argument_list|()
condition|)
block|{
try|try
block|{
name|long
name|startTxId
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|editsMatch
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|endTxId
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|editsMatch
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
operator|new
name|EditLogFile
argument_list|(
name|f
argument_list|,
name|startTxId
argument_list|,
name|endTxId
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Edits file "
operator|+
name|f
operator|+
literal|" has improperly formatted "
operator|+
literal|"transaction ID"
argument_list|)
expr_stmt|;
comment|// skip
block|}
block|}
comment|// Check for in-progress edits
name|Matcher
name|inProgressEditsMatch
init|=
name|EDITS_INPROGRESS_REGEX
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|inProgressEditsMatch
operator|.
name|matches
argument_list|()
condition|)
block|{
try|try
block|{
name|long
name|startTxId
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|inProgressEditsMatch
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
operator|new
name|EditLogFile
argument_list|(
name|f
argument_list|,
name|startTxId
argument_list|,
name|startTxId
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"In-progress edits file "
operator|+
name|f
operator|+
literal|" has improperly "
operator|+
literal|"formatted transaction ID"
argument_list|)
expr_stmt|;
comment|// skip
block|}
block|}
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|getInputStream (long fromTxId, boolean inProgressOk)
specifier|synchronized
specifier|public
name|EditLogInputStream
name|getInputStream
parameter_list|(
name|long
name|fromTxId
parameter_list|,
name|boolean
name|inProgressOk
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|EditLogFile
name|elf
range|:
name|getLogFiles
argument_list|(
name|fromTxId
argument_list|)
control|)
block|{
if|if
condition|(
name|elf
operator|.
name|containsTxId
argument_list|(
name|fromTxId
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|inProgressOk
operator|&&
name|elf
operator|.
name|isInProgress
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|elf
operator|.
name|isInProgress
argument_list|()
condition|)
block|{
name|elf
operator|.
name|validateLog
argument_list|()
expr_stmt|;
block|}
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
literal|"Returning edit stream reading from "
operator|+
name|elf
argument_list|)
expr_stmt|;
block|}
name|EditLogFileInputStream
name|elfis
init|=
operator|new
name|EditLogFileInputStream
argument_list|(
name|elf
operator|.
name|getFile
argument_list|()
argument_list|,
name|elf
operator|.
name|getFirstTxId
argument_list|()
argument_list|,
name|elf
operator|.
name|getLastTxId
argument_list|()
argument_list|,
name|elf
operator|.
name|isInProgress
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|transactionsToSkip
init|=
name|fromTxId
operator|-
name|elf
operator|.
name|getFirstTxId
argument_list|()
decl_stmt|;
if|if
condition|(
name|transactionsToSkip
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Log begins at txid %d, but requested start "
operator|+
literal|"txid is %d. Skipping %d edits."
argument_list|,
name|elf
operator|.
name|getFirstTxId
argument_list|()
argument_list|,
name|fromTxId
argument_list|,
name|transactionsToSkip
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|elfis
operator|.
name|skipUntil
argument_list|(
name|fromTxId
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to advance input stream to txid "
operator|+
name|fromTxId
argument_list|)
throw|;
block|}
return|return
name|elfis
return|;
block|}
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot find editlog file containing "
operator|+
name|fromTxId
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getNumberOfTransactions (long fromTxId, boolean inProgressOk)
specifier|public
name|long
name|getNumberOfTransactions
parameter_list|(
name|long
name|fromTxId
parameter_list|,
name|boolean
name|inProgressOk
parameter_list|)
throws|throws
name|IOException
throws|,
name|CorruptionException
block|{
name|long
name|numTxns
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|EditLogFile
name|elf
range|:
name|getLogFiles
argument_list|(
name|fromTxId
argument_list|)
control|)
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
literal|"Counting "
operator|+
name|elf
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|elf
operator|.
name|getFirstTxId
argument_list|()
operator|>
name|fromTxId
condition|)
block|{
comment|// there must be a gap
name|LOG
operator|.
name|warn
argument_list|(
literal|"Gap in transactions in "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
operator|+
literal|". Gap is "
operator|+
name|fromTxId
operator|+
literal|" - "
operator|+
operator|(
name|elf
operator|.
name|getFirstTxId
argument_list|()
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|elf
operator|.
name|containsTxId
argument_list|(
name|fromTxId
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|inProgressOk
operator|&&
name|elf
operator|.
name|isInProgress
argument_list|()
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|elf
operator|.
name|isInProgress
argument_list|()
condition|)
block|{
name|elf
operator|.
name|validateLog
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|elf
operator|.
name|hasCorruptHeader
argument_list|()
condition|)
block|{
break|break;
block|}
name|numTxns
operator|+=
name|elf
operator|.
name|getLastTxId
argument_list|()
operator|+
literal|1
operator|-
name|fromTxId
expr_stmt|;
name|fromTxId
operator|=
name|elf
operator|.
name|getLastTxId
argument_list|()
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|elf
operator|.
name|isInProgress
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
block|}
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
literal|"Journal "
operator|+
name|this
operator|+
literal|" has "
operator|+
name|numTxns
operator|+
literal|" txns from "
operator|+
name|fromTxId
argument_list|)
expr_stmt|;
block|}
name|long
name|max
init|=
name|findMaxTransaction
argument_list|(
name|inProgressOk
argument_list|)
decl_stmt|;
comment|// fromTxId should be greater than max, as it points to the next
comment|// transaction we should expect to find. If it is less than or equal
comment|// to max, it means that a transaction with txid == max has not been found
if|if
condition|(
name|numTxns
operator|==
literal|0
operator|&&
name|fromTxId
operator|<=
name|max
condition|)
block|{
name|String
name|error
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Gap in transactions, max txnid is %d"
operator|+
literal|", 0 txns from %d"
argument_list|,
name|max
argument_list|,
name|fromTxId
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|error
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|CorruptionException
argument_list|(
name|error
argument_list|)
throw|;
block|}
return|return
name|numTxns
return|;
block|}
annotation|@
name|Override
DECL|method|recoverUnfinalizedSegments ()
specifier|synchronized
specifier|public
name|void
name|recoverUnfinalizedSegments
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|currentDir
init|=
name|sd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovering unfinalized segments in "
operator|+
name|currentDir
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|EditLogFile
argument_list|>
name|allLogFiles
init|=
name|matchEditLogs
argument_list|(
name|currentDir
argument_list|)
decl_stmt|;
for|for
control|(
name|EditLogFile
name|elf
range|:
name|allLogFiles
control|)
block|{
if|if
condition|(
name|elf
operator|.
name|getFile
argument_list|()
operator|.
name|equals
argument_list|(
name|currentInProgress
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|elf
operator|.
name|isInProgress
argument_list|()
condition|)
block|{
comment|// If the file is zero-length, we likely just crashed after opening the
comment|// file, but before writing anything to it. Safe to delete it.
if|if
condition|(
name|elf
operator|.
name|getFile
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting zero-length edit log file "
operator|+
name|elf
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|elf
operator|.
name|getFile
argument_list|()
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to delete file "
operator|+
name|elf
operator|.
name|getFile
argument_list|()
argument_list|)
throw|;
block|}
continue|continue;
block|}
name|elf
operator|.
name|validateLog
argument_list|()
expr_stmt|;
if|if
condition|(
name|elf
operator|.
name|hasCorruptHeader
argument_list|()
condition|)
block|{
name|elf
operator|.
name|moveAsideCorruptFile
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|CorruptionException
argument_list|(
literal|"In-progress edit log file is corrupt: "
operator|+
name|elf
argument_list|)
throw|;
block|}
comment|// If the file has a valid header (isn't corrupt) but contains no
comment|// transactions, we likely just crashed after opening the file and
comment|// writing the header, but before syncing any transactions. Safe to
comment|// delete the file.
if|if
condition|(
name|elf
operator|.
name|getNumTransactions
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting edit log file with zero transactions "
operator|+
name|elf
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|elf
operator|.
name|getFile
argument_list|()
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to delete "
operator|+
name|elf
operator|.
name|getFile
argument_list|()
argument_list|)
throw|;
block|}
continue|continue;
block|}
name|finalizeLogSegment
argument_list|(
name|elf
operator|.
name|getFirstTxId
argument_list|()
argument_list|,
name|elf
operator|.
name|getLastTxId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getLogFiles (long fromTxId)
name|List
argument_list|<
name|EditLogFile
argument_list|>
name|getLogFiles
parameter_list|(
name|long
name|fromTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|currentDir
init|=
name|sd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|EditLogFile
argument_list|>
name|allLogFiles
init|=
name|matchEditLogs
argument_list|(
name|currentDir
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|EditLogFile
argument_list|>
name|logFiles
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|EditLogFile
name|elf
range|:
name|allLogFiles
control|)
block|{
if|if
condition|(
name|fromTxId
operator|<=
name|elf
operator|.
name|getFirstTxId
argument_list|()
operator|||
name|elf
operator|.
name|containsTxId
argument_list|(
name|fromTxId
argument_list|)
condition|)
block|{
name|logFiles
operator|.
name|add
argument_list|(
name|elf
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|logFiles
argument_list|,
name|EditLogFile
operator|.
name|COMPARE_BY_START_TXID
argument_list|)
expr_stmt|;
return|return
name|logFiles
return|;
block|}
comment|/**     * Find the maximum transaction in the journal.    */
DECL|method|findMaxTransaction (boolean inProgressOk)
specifier|private
name|long
name|findMaxTransaction
parameter_list|(
name|boolean
name|inProgressOk
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|considerSeenTxId
init|=
literal|true
decl_stmt|;
name|long
name|seenTxId
init|=
name|NNStorage
operator|.
name|readTransactionIdFile
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|long
name|maxSeenTransaction
init|=
literal|0
decl_stmt|;
for|for
control|(
name|EditLogFile
name|elf
range|:
name|getLogFiles
argument_list|(
literal|0
argument_list|)
control|)
block|{
if|if
condition|(
name|elf
operator|.
name|isInProgress
argument_list|()
operator|&&
operator|!
name|inProgressOk
condition|)
block|{
if|if
condition|(
name|elf
operator|.
name|getFirstTxId
argument_list|()
operator|!=
name|HdfsConstants
operator|.
name|INVALID_TXID
operator|&&
name|elf
operator|.
name|getFirstTxId
argument_list|()
operator|<=
name|seenTxId
condition|)
block|{
comment|// don't look at the seen_txid file if in-progress logs are not to be
comment|// examined, and the value in seen_txid falls within the in-progress
comment|// segment.
name|considerSeenTxId
operator|=
literal|false
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|elf
operator|.
name|isInProgress
argument_list|()
condition|)
block|{
name|maxSeenTransaction
operator|=
name|Math
operator|.
name|max
argument_list|(
name|elf
operator|.
name|getFirstTxId
argument_list|()
argument_list|,
name|maxSeenTransaction
argument_list|)
expr_stmt|;
name|elf
operator|.
name|validateLog
argument_list|()
expr_stmt|;
block|}
name|maxSeenTransaction
operator|=
name|Math
operator|.
name|max
argument_list|(
name|elf
operator|.
name|getLastTxId
argument_list|()
argument_list|,
name|maxSeenTransaction
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|considerSeenTxId
condition|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|maxSeenTransaction
argument_list|,
name|seenTxId
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|maxSeenTransaction
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"FileJournalManager(root=%s)"
argument_list|,
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Record of an edit log that has been located and had its filename parsed.    */
DECL|class|EditLogFile
specifier|static
class|class
name|EditLogFile
block|{
DECL|field|file
specifier|private
name|File
name|file
decl_stmt|;
DECL|field|firstTxId
specifier|private
specifier|final
name|long
name|firstTxId
decl_stmt|;
DECL|field|lastTxId
specifier|private
name|long
name|lastTxId
decl_stmt|;
DECL|field|numTx
specifier|private
name|long
name|numTx
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|hasCorruptHeader
specifier|private
name|boolean
name|hasCorruptHeader
init|=
literal|false
decl_stmt|;
DECL|field|isInProgress
specifier|private
specifier|final
name|boolean
name|isInProgress
decl_stmt|;
DECL|field|COMPARE_BY_START_TXID
specifier|final
specifier|static
name|Comparator
argument_list|<
name|EditLogFile
argument_list|>
name|COMPARE_BY_START_TXID
init|=
operator|new
name|Comparator
argument_list|<
name|EditLogFile
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|EditLogFile
name|a
parameter_list|,
name|EditLogFile
name|b
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
operator|.
name|compare
argument_list|(
name|a
operator|.
name|getFirstTxId
argument_list|()
argument_list|,
name|b
operator|.
name|getFirstTxId
argument_list|()
argument_list|)
operator|.
name|compare
argument_list|(
name|a
operator|.
name|getLastTxId
argument_list|()
argument_list|,
name|b
operator|.
name|getLastTxId
argument_list|()
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|method|EditLogFile (File file, long firstTxId, long lastTxId)
name|EditLogFile
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|firstTxId
parameter_list|,
name|long
name|lastTxId
parameter_list|)
block|{
name|this
argument_list|(
name|file
argument_list|,
name|firstTxId
argument_list|,
name|lastTxId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|lastTxId
operator|!=
name|HdfsConstants
operator|.
name|INVALID_TXID
operator|)
operator|&&
operator|(
name|lastTxId
operator|>=
name|firstTxId
operator|)
assert|;
block|}
DECL|method|EditLogFile (File file, long firstTxId, long lastTxId, boolean isInProgress)
name|EditLogFile
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|firstTxId
parameter_list|,
name|long
name|lastTxId
parameter_list|,
name|boolean
name|isInProgress
parameter_list|)
block|{
assert|assert
operator|(
name|lastTxId
operator|==
name|HdfsConstants
operator|.
name|INVALID_TXID
operator|&&
name|isInProgress
operator|)
operator|||
operator|(
name|lastTxId
operator|!=
name|HdfsConstants
operator|.
name|INVALID_TXID
operator|&&
name|lastTxId
operator|>=
name|firstTxId
operator|)
assert|;
assert|assert
operator|(
name|firstTxId
operator|>
literal|0
operator|)
operator|||
operator|(
name|firstTxId
operator|==
name|HdfsConstants
operator|.
name|INVALID_TXID
operator|)
assert|;
assert|assert
name|file
operator|!=
literal|null
assert|;
name|this
operator|.
name|firstTxId
operator|=
name|firstTxId
expr_stmt|;
name|this
operator|.
name|lastTxId
operator|=
name|lastTxId
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|isInProgress
operator|=
name|isInProgress
expr_stmt|;
block|}
DECL|method|getFirstTxId ()
name|long
name|getFirstTxId
parameter_list|()
block|{
return|return
name|firstTxId
return|;
block|}
DECL|method|getLastTxId ()
name|long
name|getLastTxId
parameter_list|()
block|{
return|return
name|lastTxId
return|;
block|}
DECL|method|containsTxId (long txId)
name|boolean
name|containsTxId
parameter_list|(
name|long
name|txId
parameter_list|)
block|{
return|return
name|firstTxId
operator|<=
name|txId
operator|&&
name|txId
operator|<=
name|lastTxId
return|;
block|}
comment|/**       * Count the number of valid transactions in a log.      * This will update the lastTxId of the EditLogFile or      * mark it as corrupt if it is.      */
DECL|method|validateLog ()
name|void
name|validateLog
parameter_list|()
throws|throws
name|IOException
block|{
name|EditLogValidation
name|val
init|=
name|EditLogFileInputStream
operator|.
name|validateEditLog
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|this
operator|.
name|numTx
operator|=
name|val
operator|.
name|getNumTransactions
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastTxId
operator|=
name|val
operator|.
name|getEndTxId
argument_list|()
expr_stmt|;
name|this
operator|.
name|hasCorruptHeader
operator|=
name|val
operator|.
name|hasCorruptHeader
argument_list|()
expr_stmt|;
block|}
DECL|method|getNumTransactions ()
name|long
name|getNumTransactions
parameter_list|()
block|{
return|return
name|numTx
return|;
block|}
DECL|method|isInProgress ()
name|boolean
name|isInProgress
parameter_list|()
block|{
return|return
name|isInProgress
return|;
block|}
DECL|method|getFile ()
name|File
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
DECL|method|hasCorruptHeader ()
name|boolean
name|hasCorruptHeader
parameter_list|()
block|{
return|return
name|hasCorruptHeader
return|;
block|}
DECL|method|moveAsideCorruptFile ()
name|void
name|moveAsideCorruptFile
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|hasCorruptHeader
assert|;
name|File
name|src
init|=
name|file
decl_stmt|;
name|File
name|dst
init|=
operator|new
name|File
argument_list|(
name|src
operator|.
name|getParent
argument_list|()
argument_list|,
name|src
operator|.
name|getName
argument_list|()
operator|+
literal|".corrupt"
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
name|src
operator|.
name|renameTo
argument_list|(
name|dst
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Couldn't rename corrupt log "
operator|+
name|src
operator|+
literal|" to "
operator|+
name|dst
argument_list|)
throw|;
block|}
name|file
operator|=
name|dst
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"EditLogFile(file=%s,first=%019d,last=%019d,"
operator|+
literal|"inProgress=%b,hasCorruptHeader=%b,numTx=%d)"
argument_list|,
name|file
operator|.
name|toString
argument_list|()
argument_list|,
name|firstTxId
argument_list|,
name|lastTxId
argument_list|,
name|isInProgress
argument_list|()
argument_list|,
name|hasCorruptHeader
argument_list|,
name|numTx
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

