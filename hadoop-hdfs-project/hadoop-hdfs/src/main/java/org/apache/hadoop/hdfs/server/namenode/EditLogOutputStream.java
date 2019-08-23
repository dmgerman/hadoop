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
name|io
operator|.
name|Closeable
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
name|util
operator|.
name|Time
operator|.
name|monotonicNow
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
comment|/**  * A generic abstract class to support journaling of edits logs into   * a persistent storage.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|EditLogOutputStream
specifier|public
specifier|abstract
class|class
name|EditLogOutputStream
implements|implements
name|Closeable
block|{
comment|// these are statistics counters
DECL|field|numSync
specifier|private
name|long
name|numSync
decl_stmt|;
comment|// number of sync(s) to disk
DECL|field|totalTimeSync
specifier|private
name|long
name|totalTimeSync
decl_stmt|;
comment|// total time to sync
comment|// The version of the current edit log
DECL|field|currentLogVersion
specifier|private
name|int
name|currentLogVersion
decl_stmt|;
DECL|method|EditLogOutputStream ()
specifier|public
name|EditLogOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|numSync
operator|=
name|totalTimeSync
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Write edits log operation to the stream.    *     * @param op operation    * @throws IOException    */
DECL|method|write (FSEditLogOp op)
specifier|abstract
specifier|public
name|void
name|write
parameter_list|(
name|FSEditLogOp
name|op
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write raw data to an edit log. This data should already have    * the transaction ID, checksum, etc included. It is for use    * within the BackupNode when replicating edits from the    * NameNode.    *    * @param bytes the bytes to write.    * @param offset offset in the bytes to write from    * @param length number of bytes to write    * @throws IOException    */
DECL|method|writeRaw (byte[] bytes, int offset, int length)
specifier|abstract
specifier|public
name|void
name|writeRaw
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create and initialize underlying persistent edits log storage.    *     * @param layoutVersion The LayoutVersion of the journal    * @throws IOException    */
DECL|method|create (int layoutVersion)
specifier|abstract
specifier|public
name|void
name|create
parameter_list|(
name|int
name|layoutVersion
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close the journal.    * @throws IOException if the journal can't be closed,    *         or if there are unflushed edits    */
annotation|@
name|Override
DECL|method|close ()
specifier|abstract
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Close the stream without necessarily flushing any pending data.    * This may be called after a previous write or close threw an exception.    */
DECL|method|abort ()
specifier|abstract
specifier|public
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * All data that has been written to the stream so far will be flushed.    * New data can be still written to the stream while flushing is performed.    */
DECL|method|setReadyToFlush ()
specifier|abstract
specifier|public
name|void
name|setReadyToFlush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Flush and sync all data that is ready to be flush     * {@link #setReadyToFlush()} into underlying persistent store.    * @param durable if true, the edits should be made truly durable before    * returning    * @throws IOException    */
DECL|method|flushAndSync (boolean durable)
specifier|abstract
specifier|protected
name|void
name|flushAndSync
parameter_list|(
name|boolean
name|durable
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Flush data to persistent store.    * Collect sync metrics.    */
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|flush (boolean durable)
specifier|public
name|void
name|flush
parameter_list|(
name|boolean
name|durable
parameter_list|)
throws|throws
name|IOException
block|{
name|numSync
operator|++
expr_stmt|;
name|long
name|start
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
name|flushAndSync
argument_list|(
name|durable
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
name|totalTimeSync
operator|+=
operator|(
name|end
operator|-
name|start
operator|)
expr_stmt|;
block|}
comment|/**    * Implement the policy when to automatically sync the buffered edits log    * The buffered edits can be flushed when the buffer becomes full or    * a certain period of time is elapsed.    *     * @return true if the buffered data should be automatically synced to disk    */
DECL|method|shouldForceSync ()
specifier|public
name|boolean
name|shouldForceSync
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Return total time spent in {@link #flushAndSync(boolean)}    */
DECL|method|getTotalSyncTime ()
name|long
name|getTotalSyncTime
parameter_list|()
block|{
return|return
name|totalTimeSync
return|;
block|}
comment|/**    * Return number of calls to {@link #flushAndSync(boolean)}    */
DECL|method|getNumSync ()
specifier|protected
name|long
name|getNumSync
parameter_list|()
block|{
return|return
name|numSync
return|;
block|}
comment|/**    * @return a short text snippet suitable for describing the current    * status of the stream    */
DECL|method|generateReport ()
specifier|public
name|String
name|generateReport
parameter_list|()
block|{
return|return
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return The version of the current edit log    */
DECL|method|getCurrentLogVersion ()
specifier|public
name|int
name|getCurrentLogVersion
parameter_list|()
block|{
return|return
name|currentLogVersion
return|;
block|}
comment|/**    * @param logVersion The version of the current edit log    */
DECL|method|setCurrentLogVersion (int logVersion)
specifier|public
name|void
name|setCurrentLogVersion
parameter_list|(
name|int
name|logVersion
parameter_list|)
block|{
name|this
operator|.
name|currentLogVersion
operator|=
name|logVersion
expr_stmt|;
block|}
block|}
end_class

end_unit

