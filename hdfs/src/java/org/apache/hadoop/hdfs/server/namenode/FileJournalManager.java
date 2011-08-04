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
DECL|method|FileJournalManager (StorageDirectory sd)
specifier|public
name|FileJournalManager
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
block|{
name|this
operator|.
name|sd
operator|=
name|sd
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startLogSegment (long txid)
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
name|File
name|newInProgress
init|=
name|NNStorage
operator|.
name|getInProgressEditsFile
argument_list|(
name|sd
argument_list|,
name|txid
argument_list|)
decl_stmt|;
name|EditLogOutputStream
name|stm
init|=
operator|new
name|EditLogFileOutputStream
argument_list|(
name|newInProgress
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
annotation|@
name|Override
DECL|method|finalizeLogSegment (long firstTxId, long lastTxId)
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
name|debug
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to finalize edits file "
operator|+
name|inprogressFile
argument_list|)
throw|;
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
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FileJournalManager for storage directory "
operator|+
name|sd
return|;
block|}
annotation|@
name|Override
DECL|method|setOutputBufferCapacity (int size)
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
annotation|@
name|Override
DECL|method|getInProgressInputStream (long segmentStartsAtTxId)
specifier|public
name|EditLogInputStream
name|getInProgressInputStream
parameter_list|(
name|long
name|segmentStartsAtTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|NNStorage
operator|.
name|getInProgressEditsFile
argument_list|(
name|sd
argument_list|,
name|segmentStartsAtTxId
argument_list|)
decl_stmt|;
return|return
operator|new
name|EditLogFileInputStream
argument_list|(
name|f
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
name|EditLogFile
operator|.
name|UNKNOWN_END
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
DECL|field|cachedValidation
specifier|private
name|EditLogValidation
name|cachedValidation
init|=
literal|null
decl_stmt|;
DECL|field|isCorrupt
specifier|private
name|boolean
name|isCorrupt
init|=
literal|false
decl_stmt|;
DECL|field|UNKNOWN_END
specifier|static
specifier|final
name|long
name|UNKNOWN_END
init|=
operator|-
literal|1
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
assert|assert
name|lastTxId
operator|==
name|UNKNOWN_END
operator|||
name|lastTxId
operator|>=
name|firstTxId
assert|;
assert|assert
name|firstTxId
operator|>
literal|0
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
block|}
DECL|method|finalizeLog ()
specifier|public
name|void
name|finalizeLog
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|numTransactions
init|=
name|validateLog
argument_list|()
operator|.
name|numTransactions
decl_stmt|;
name|long
name|lastTxId
init|=
name|firstTxId
operator|+
name|numTransactions
operator|-
literal|1
decl_stmt|;
name|File
name|dst
init|=
operator|new
name|File
argument_list|(
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
name|firstTxId
argument_list|,
name|lastTxId
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finalizing edits log "
operator|+
name|file
operator|+
literal|" by renaming to "
operator|+
name|dst
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|renameTo
argument_list|(
name|dst
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Couldn't finalize log "
operator|+
name|file
operator|+
literal|" to "
operator|+
name|dst
argument_list|)
throw|;
block|}
name|this
operator|.
name|lastTxId
operator|=
name|lastTxId
expr_stmt|;
name|file
operator|=
name|dst
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
DECL|method|validateLog ()
name|EditLogValidation
name|validateLog
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cachedValidation
operator|==
literal|null
condition|)
block|{
name|cachedValidation
operator|=
name|EditLogFileInputStream
operator|.
name|validateEditLog
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
return|return
name|cachedValidation
return|;
block|}
DECL|method|isInProgress ()
name|boolean
name|isInProgress
parameter_list|()
block|{
return|return
operator|(
name|lastTxId
operator|==
name|UNKNOWN_END
operator|)
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
DECL|method|markCorrupt ()
name|void
name|markCorrupt
parameter_list|()
block|{
name|isCorrupt
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|isCorrupt ()
name|boolean
name|isCorrupt
parameter_list|()
block|{
return|return
name|isCorrupt
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
name|isCorrupt
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
literal|"inProgress=%b,corrupt=%b)"
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
name|isCorrupt
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

