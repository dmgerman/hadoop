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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|codec
operator|.
name|binary
operator|.
name|Hex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|namenode
operator|.
name|FSEditLogOp
operator|.
name|Writer
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
name|DataOutputBuffer
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
comment|/**  * A double-buffer for edits. New edits are written into the first buffer  * while the second is available to be flushed. Each time the double-buffer  * is flushed, the two internal buffers are swapped. This allows edits  * to progress concurrently to flushes without allocating new buffers each  * time.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|EditsDoubleBuffer
specifier|public
class|class
name|EditsDoubleBuffer
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EditsDoubleBuffer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|bufCurrent
specifier|private
name|TxnBuffer
name|bufCurrent
decl_stmt|;
comment|// current buffer for writing
DECL|field|bufReady
specifier|private
name|TxnBuffer
name|bufReady
decl_stmt|;
comment|// buffer ready for flushing
DECL|field|initBufferSize
specifier|private
specifier|final
name|int
name|initBufferSize
decl_stmt|;
DECL|method|EditsDoubleBuffer (int defaultBufferSize)
specifier|public
name|EditsDoubleBuffer
parameter_list|(
name|int
name|defaultBufferSize
parameter_list|)
block|{
name|initBufferSize
operator|=
name|defaultBufferSize
expr_stmt|;
name|bufCurrent
operator|=
operator|new
name|TxnBuffer
argument_list|(
name|initBufferSize
argument_list|)
expr_stmt|;
name|bufReady
operator|=
operator|new
name|TxnBuffer
argument_list|(
name|initBufferSize
argument_list|)
expr_stmt|;
block|}
DECL|method|writeOp (FSEditLogOp op, int logVersion)
specifier|public
name|void
name|writeOp
parameter_list|(
name|FSEditLogOp
name|op
parameter_list|,
name|int
name|logVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|bufCurrent
operator|.
name|writeOp
argument_list|(
name|op
argument_list|,
name|logVersion
argument_list|)
expr_stmt|;
block|}
DECL|method|writeRaw (byte[] bytes, int offset, int length)
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
block|{
name|bufCurrent
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bufCurrent
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bufReady
argument_list|)
expr_stmt|;
name|int
name|bufSize
init|=
name|bufCurrent
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|bufSize
operator|!=
literal|0
condition|)
block|{
name|bufCurrent
operator|.
name|dumpRemainingEditLogs
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"FSEditStream has "
operator|+
name|bufSize
operator|+
literal|" bytes still to be flushed and cannot be closed."
argument_list|)
throw|;
block|}
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|bufCurrent
argument_list|,
name|bufReady
argument_list|)
expr_stmt|;
name|bufCurrent
operator|=
name|bufReady
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|setReadyToFlush ()
specifier|public
name|void
name|setReadyToFlush
parameter_list|()
block|{
assert|assert
name|isFlushed
argument_list|()
operator|:
literal|"previous data not flushed yet"
assert|;
name|TxnBuffer
name|tmp
init|=
name|bufReady
decl_stmt|;
name|bufReady
operator|=
name|bufCurrent
expr_stmt|;
name|bufCurrent
operator|=
name|tmp
expr_stmt|;
block|}
comment|/**    * Writes the content of the "ready" buffer to the given output stream,    * and resets it. Does not swap any buffers.    */
DECL|method|flushTo (OutputStream out)
specifier|public
name|void
name|flushTo
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|bufReady
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// write data to file
name|bufReady
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// erase all data in the buffer
block|}
DECL|method|shouldForceSync ()
specifier|public
name|boolean
name|shouldForceSync
parameter_list|()
block|{
return|return
name|bufCurrent
operator|.
name|size
argument_list|()
operator|>=
name|initBufferSize
return|;
block|}
DECL|method|getReadyBuf ()
name|DataOutputBuffer
name|getReadyBuf
parameter_list|()
block|{
return|return
name|bufReady
return|;
block|}
DECL|method|getCurrentBuf ()
name|DataOutputBuffer
name|getCurrentBuf
parameter_list|()
block|{
return|return
name|bufCurrent
return|;
block|}
DECL|method|isFlushed ()
specifier|public
name|boolean
name|isFlushed
parameter_list|()
block|{
return|return
name|bufReady
operator|.
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|countBufferedBytes ()
specifier|public
name|int
name|countBufferedBytes
parameter_list|()
block|{
return|return
name|bufReady
operator|.
name|size
argument_list|()
operator|+
name|bufCurrent
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * @return the transaction ID of the first transaction ready to be flushed     */
DECL|method|getFirstReadyTxId ()
specifier|public
name|long
name|getFirstReadyTxId
parameter_list|()
block|{
assert|assert
name|bufReady
operator|.
name|firstTxId
operator|>
literal|0
assert|;
return|return
name|bufReady
operator|.
name|firstTxId
return|;
block|}
comment|/**    * @return the number of transactions that are ready to be flushed    */
DECL|method|countReadyTxns ()
specifier|public
name|int
name|countReadyTxns
parameter_list|()
block|{
return|return
name|bufReady
operator|.
name|numTxns
return|;
block|}
comment|/**    * @return the number of bytes that are ready to be flushed    */
DECL|method|countReadyBytes ()
specifier|public
name|int
name|countReadyBytes
parameter_list|()
block|{
return|return
name|bufReady
operator|.
name|size
argument_list|()
return|;
block|}
DECL|class|TxnBuffer
specifier|private
specifier|static
class|class
name|TxnBuffer
extends|extends
name|DataOutputBuffer
block|{
DECL|field|firstTxId
name|long
name|firstTxId
decl_stmt|;
DECL|field|numTxns
name|int
name|numTxns
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|Writer
name|writer
decl_stmt|;
DECL|method|TxnBuffer (int initBufferSize)
specifier|public
name|TxnBuffer
parameter_list|(
name|int
name|initBufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|initBufferSize
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|FSEditLogOp
operator|.
name|Writer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|writeOp (FSEditLogOp op, int logVersion)
specifier|public
name|void
name|writeOp
parameter_list|(
name|FSEditLogOp
name|op
parameter_list|,
name|int
name|logVersion
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|firstTxId
operator|==
name|HdfsServerConstants
operator|.
name|INVALID_TXID
condition|)
block|{
name|firstTxId
operator|=
name|op
operator|.
name|txid
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|op
operator|.
name|txid
operator|>
name|firstTxId
assert|;
block|}
name|writer
operator|.
name|writeOp
argument_list|(
name|op
argument_list|,
name|logVersion
argument_list|)
expr_stmt|;
name|numTxns
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|DataOutputBuffer
name|reset
parameter_list|()
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|firstTxId
operator|=
name|HdfsServerConstants
operator|.
name|INVALID_TXID
expr_stmt|;
name|numTxns
operator|=
literal|0
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|dumpRemainingEditLogs ()
specifier|private
name|void
name|dumpRemainingEditLogs
parameter_list|()
block|{
name|byte
index|[]
name|buf
init|=
name|this
operator|.
name|getData
argument_list|()
decl_stmt|;
name|byte
index|[]
name|remainingRawEdits
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|bis
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|remainingRawEdits
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|bis
argument_list|)
decl_stmt|;
name|FSEditLogLoader
operator|.
name|PositionTrackingInputStream
name|tracker
init|=
operator|new
name|FSEditLogLoader
operator|.
name|PositionTrackingInputStream
argument_list|(
name|bis
argument_list|)
decl_stmt|;
name|FSEditLogOp
operator|.
name|Reader
name|reader
init|=
name|FSEditLogOp
operator|.
name|Reader
operator|.
name|create
argument_list|(
name|dis
argument_list|,
name|tracker
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
decl_stmt|;
name|FSEditLogOp
name|op
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"The edits buffer is "
operator|+
name|size
argument_list|()
operator|+
literal|" bytes long with "
operator|+
name|numTxns
operator|+
literal|" unflushed transactions. "
operator|+
literal|"Below is the list of unflushed transactions:"
argument_list|)
expr_stmt|;
name|int
name|numTransactions
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|op
operator|=
name|reader
operator|.
name|readOp
argument_list|(
literal|false
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unflushed op ["
operator|+
name|numTransactions
operator|+
literal|"]: "
operator|+
name|op
argument_list|)
expr_stmt|;
name|numTransactions
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// If any exceptions, print raw bytes and stop.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to dump remaining ops. Remaining raw bytes: "
operator|+
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|remainingRawEdits
argument_list|)
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

