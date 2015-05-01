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
name|HdfsServerConstants
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
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A generic abstract class to support reading edits log data from   * persistent storage.  *   * It should stream bytes from the storage exactly as they were written  * into the #{@link EditLogOutputStream}.  */
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
DECL|class|EditLogInputStream
specifier|public
specifier|abstract
class|class
name|EditLogInputStream
implements|implements
name|Closeable
block|{
DECL|field|cachedOp
specifier|private
name|FSEditLogOp
name|cachedOp
init|=
literal|null
decl_stmt|;
comment|/**    * Returns the name of the currently active underlying stream.  The default    * implementation returns the same value as getName unless overridden by the    * subclass.    *     * @return String name of the currently active underlying stream    */
DECL|method|getCurrentStreamName ()
specifier|public
name|String
name|getCurrentStreamName
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
comment|/**     * @return the name of the EditLogInputStream    */
DECL|method|getName ()
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**     * @return the first transaction which will be found in this stream    */
DECL|method|getFirstTxId ()
specifier|public
specifier|abstract
name|long
name|getFirstTxId
parameter_list|()
function_decl|;
comment|/**     * @return the last transaction which will be found in this stream    */
DECL|method|getLastTxId ()
specifier|public
specifier|abstract
name|long
name|getLastTxId
parameter_list|()
function_decl|;
comment|/**    * Close the stream.    * @throws IOException if an error occurred while closing    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Read an operation from the stream    * @return an operation from the stream or null if at end of stream    * @throws IOException if there is an error reading from the stream    */
DECL|method|readOp ()
specifier|public
name|FSEditLogOp
name|readOp
parameter_list|()
throws|throws
name|IOException
block|{
name|FSEditLogOp
name|ret
decl_stmt|;
if|if
condition|(
name|cachedOp
operator|!=
literal|null
condition|)
block|{
name|ret
operator|=
name|cachedOp
expr_stmt|;
name|cachedOp
operator|=
literal|null
expr_stmt|;
return|return
name|ret
return|;
block|}
return|return
name|nextOp
argument_list|()
return|;
block|}
comment|/**     * Position the stream so that a valid operation can be read from it with    * readOp().    *     * This method can be used to skip over corrupted sections of edit logs.    */
DECL|method|resync ()
specifier|public
name|void
name|resync
parameter_list|()
block|{
if|if
condition|(
name|cachedOp
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|cachedOp
operator|=
name|nextValidOp
argument_list|()
expr_stmt|;
block|}
comment|/**     * Get the next operation from the stream storage.    *     * @return an operation from the stream or null if at end of stream    * @throws IOException if there is an error reading from the stream    */
DECL|method|nextOp ()
specifier|protected
specifier|abstract
name|FSEditLogOp
name|nextOp
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Go through the next operation from the stream storage.    * @return the txid of the next operation.    */
DECL|method|scanNextOp ()
specifier|protected
name|long
name|scanNextOp
parameter_list|()
throws|throws
name|IOException
block|{
name|FSEditLogOp
name|next
init|=
name|readOp
argument_list|()
decl_stmt|;
return|return
name|next
operator|!=
literal|null
condition|?
name|next
operator|.
name|txid
else|:
name|HdfsServerConstants
operator|.
name|INVALID_TXID
return|;
block|}
comment|/**     * Get the next valid operation from the stream storage.    *     * This is exactly like nextOp, except that we attempt to skip over damaged    * parts of the edit log    *     * @return an operation from the stream or null if at end of stream    */
DECL|method|nextValidOp ()
specifier|protected
name|FSEditLogOp
name|nextValidOp
parameter_list|()
block|{
comment|// This is a trivial implementation which just assumes that any errors mean
comment|// that there is nothing more of value in the log.  Subclasses that support
comment|// error recovery will want to override this.
try|try
block|{
return|return
name|nextOp
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**     * Skip edit log operations up to a given transaction ID, or until the    * end of the edit log is reached.    *    * After this function returns, the next call to readOp will return either    * end-of-file (null) or a transaction with a txid equal to or higher than    * the one we asked for.    *    * @param txid    The transaction ID to read up until.    * @return        Returns true if we found a transaction ID greater than    *                or equal to 'txid' in the log.    */
DECL|method|skipUntil (long txid)
specifier|public
name|boolean
name|skipUntil
parameter_list|(
name|long
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|FSEditLogOp
name|op
init|=
name|readOp
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|op
operator|.
name|getTransactionId
argument_list|()
operator|>=
name|txid
condition|)
block|{
name|cachedOp
operator|=
name|op
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
comment|/**    * return the cachedOp, and reset it to null.     */
DECL|method|getCachedOp ()
name|FSEditLogOp
name|getCachedOp
parameter_list|()
block|{
name|FSEditLogOp
name|op
init|=
name|this
operator|.
name|cachedOp
decl_stmt|;
name|cachedOp
operator|=
literal|null
expr_stmt|;
return|return
name|op
return|;
block|}
comment|/**     * Get the layout version of the data in the stream.    * @return the layout version of the ops in the stream.    * @throws IOException if there is an error reading the version    */
DECL|method|getVersion (boolean verifyVersion)
specifier|public
specifier|abstract
name|int
name|getVersion
parameter_list|(
name|boolean
name|verifyVersion
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the "position" of in the stream. This is useful for     * debugging and operational purposes.    *    * Different stream types can have a different meaning for     * what the position is. For file streams it means the byte offset    * from the start of the file.    *    * @return the position in the stream    */
DECL|method|getPosition ()
specifier|public
specifier|abstract
name|long
name|getPosition
parameter_list|()
function_decl|;
comment|/**    * Return the size of the current edits log or -1 if unknown.    *     * @return long size of the current edits log or -1 if unknown    */
DECL|method|length ()
specifier|public
specifier|abstract
name|long
name|length
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return true if this stream is in progress, false if it is finalized.    */
DECL|method|isInProgress ()
specifier|public
specifier|abstract
name|boolean
name|isInProgress
parameter_list|()
function_decl|;
comment|/**    * Set the maximum opcode size in bytes.    */
DECL|method|setMaxOpSize (int maxOpSize)
specifier|public
specifier|abstract
name|void
name|setMaxOpSize
parameter_list|(
name|int
name|maxOpSize
parameter_list|)
function_decl|;
comment|/**    * Returns true if we are currently reading the log from a local disk or an    * even faster data source (e.g. a byte buffer).    */
DECL|method|isLocalLog ()
specifier|public
specifier|abstract
name|boolean
name|isLocalLog
parameter_list|()
function_decl|;
block|}
end_class

end_unit

