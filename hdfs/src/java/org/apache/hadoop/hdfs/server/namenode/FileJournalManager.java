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
name|FSImageTransactionalStorageInspector
operator|.
name|FoundEditLog
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
DECL|method|purgeLogsOlderThan (long minTxIdToKeep, StoragePurger purger)
specifier|public
name|void
name|purgeLogsOlderThan
parameter_list|(
name|long
name|minTxIdToKeep
parameter_list|,
name|StoragePurger
name|purger
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
name|FoundEditLog
argument_list|>
name|editLogs
init|=
name|FSImageTransactionalStorageInspector
operator|.
name|matchEditLogs
argument_list|(
name|files
argument_list|)
decl_stmt|;
for|for
control|(
name|FoundEditLog
name|log
range|:
name|editLogs
control|)
block|{
if|if
condition|(
name|log
operator|.
name|getStartTxId
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
block|}
end_class

end_unit

