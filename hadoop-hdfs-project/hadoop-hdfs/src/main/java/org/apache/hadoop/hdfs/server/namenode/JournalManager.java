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
block|{
comment|/**    * Begin writing to a new segment of the log stream, which starts at    * the given transaction ID.    */
DECL|method|startLogSegment (long txId)
name|EditLogOutputStream
name|startLogSegment
parameter_list|(
name|long
name|txId
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
comment|/**    * Get a list of edit log input streams.  The list will start with the    * stream that contains fromTxnId, and continue until the end of the journal    * being managed.    *     * @param fromTxnId the first transaction id we want to read    * @param inProgressOk whether or not in-progress streams should be returned    *    * @return a list of streams    */
DECL|method|selectInputStreams (Collection<EditLogInputStream> streams, long fromTxnId, boolean inProgressOk)
name|void
name|selectInputStreams
parameter_list|(
name|Collection
argument_list|<
name|EditLogInputStream
argument_list|>
name|streams
parameter_list|,
name|long
name|fromTxnId
parameter_list|,
name|boolean
name|inProgressOk
parameter_list|)
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
comment|/**    * The JournalManager may archive/purge any logs for transactions less than    * or equal to minImageTxId.    *    * @param minTxIdToKeep the earliest txid that must be retained after purging    *                      old logs    * @throws IOException if purging fails    */
DECL|method|purgeLogsOlderThan (long minTxIdToKeep)
name|void
name|purgeLogsOlderThan
parameter_list|(
name|long
name|minTxIdToKeep
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Recover segments which have not been finalized.    */
DECL|method|recoverUnfinalizedSegments ()
name|void
name|recoverUnfinalizedSegments
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

