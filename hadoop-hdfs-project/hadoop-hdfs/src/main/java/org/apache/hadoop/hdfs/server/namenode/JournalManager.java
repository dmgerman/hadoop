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
name|Closeable
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
name|classification
operator|.
name|InterfaceStability
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
name|FormatConfirmable
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
name|NamespaceInfo
import|;
end_import

begin_comment
comment|/**  * A JournalManager is responsible for managing a single place of storing  * edit logs. It may correspond to multiple files, a backup node, etc.  * Even when the actual underlying storage is rolled, or failed and restored,  * each conceptual place of storage corresponds to exactly one instance of  * this class, which is created when the EditLog is first opened.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|JournalManager
specifier|public
interface|interface
name|JournalManager
extends|extends
name|Closeable
extends|,
name|FormatConfirmable
extends|,
name|LogsPurgeable
block|{
comment|/**    * Format the underlying storage, removing any previously    * stored data.    */
DECL|method|format (NamespaceInfo ns, boolean force)
name|void
name|format
parameter_list|(
name|NamespaceInfo
name|ns
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Begin writing to a new segment of the log stream, which starts at    * the given transaction ID.    */
DECL|method|startLogSegment (long txId, int layoutVersion)
name|EditLogOutputStream
name|startLogSegment
parameter_list|(
name|long
name|txId
parameter_list|,
name|int
name|layoutVersion
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Mark the log segment that spans from firstTxId to lastTxId    * as finalized and complete.    */
DECL|method|finalizeLogSegment (long firstTxId, long lastTxId)
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
function_decl|;
comment|/**    * Set the amount of memory that this stream should use to buffer edits    */
DECL|method|setOutputBufferCapacity (int size)
name|void
name|setOutputBufferCapacity
parameter_list|(
name|int
name|size
parameter_list|)
function_decl|;
comment|/**    * Recover segments which have not been finalized.    */
DECL|method|recoverUnfinalizedSegments ()
name|void
name|recoverUnfinalizedSegments
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Perform any steps that must succeed across all JournalManagers involved in    * an upgrade before proceeding onto the actual upgrade stage. If a call to    * any JM's doPreUpgrade method fails, then doUpgrade will not be called for    * any JM.    */
DECL|method|doPreUpgrade ()
name|void
name|doPreUpgrade
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Perform the actual upgrade of the JM. After this is completed, the NN can    * begin to use the new upgraded metadata. This metadata may later be either    * finalized or rolled back to the previous state.    *     * @param storage info about the new upgraded versions.    */
DECL|method|doUpgrade (Storage storage)
name|void
name|doUpgrade
parameter_list|(
name|Storage
name|storage
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Finalize the upgrade. JMs should purge any state that they had been keeping    * around during the upgrade process. After this is completed, rollback is no    * longer allowed.    */
DECL|method|doFinalize ()
name|void
name|doFinalize
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return true if this JM can roll back to the previous storage state, false    * otherwise. The NN will refuse to run the rollback operation unless at least    * one JM or fsimage storage directory can roll back.    *     * @param storage the storage info for the current state    * @param prevStorage the storage info for the previous (unupgraded) state    * @param targetLayoutVersion the layout version we intend to roll back to    * @return true if this JM can roll back, false otherwise.    */
DECL|method|canRollBack (StorageInfo storage, StorageInfo prevStorage, int targetLayoutVersion)
name|boolean
name|canRollBack
parameter_list|(
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
function_decl|;
comment|/**    * Perform the rollback to the previous FS state. JMs which do not need to    * roll back their state should just return without error.    */
DECL|method|doRollback ()
name|void
name|doRollback
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Discard the segments whose first txid is {@literal>=} the given txid.    * @param startTxId The given txid should be right at the segment boundary,     * i.e., it should be the first txid of some segment, if segment corresponding    * to the txid exists.    */
DECL|method|discardSegments (long startTxId)
name|void
name|discardSegments
parameter_list|(
name|long
name|startTxId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return the CTime of the journal manager.    */
DECL|method|getJournalCTime ()
name|long
name|getJournalCTime
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Close the journal manager, freeing any resources it may hold.    */
annotation|@
name|Override
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Indicate that a journal is cannot be used to load a certain range of     * edits.    * This exception occurs in the case of a gap in the transactions, or a    * corrupt edit file.    */
DECL|class|CorruptionException
specifier|public
specifier|static
class|class
name|CorruptionException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4687802717006172702L
decl_stmt|;
DECL|method|CorruptionException (String reason)
specifier|public
name|CorruptionException
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|super
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

