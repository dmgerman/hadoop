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
name|DataOutputStream
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
name|FileOutputStream
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
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * An implementation of the abstract class {@link EditLogOutputStream}, which  * stores edits in a local file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|EditLogFileOutputStream
specifier|public
class|class
name|EditLogFileOutputStream
extends|extends
name|EditLogOutputStream
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|EditLogFileOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MIN_PREALLOCATION_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|MIN_PREALLOCATION_LENGTH
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|file
specifier|private
name|File
name|file
decl_stmt|;
DECL|field|fp
specifier|private
name|FileOutputStream
name|fp
decl_stmt|;
comment|// file stream for storing edit logs
DECL|field|fc
specifier|private
name|FileChannel
name|fc
decl_stmt|;
comment|// channel of the file stream for sync
DECL|field|doubleBuf
specifier|private
name|EditsDoubleBuffer
name|doubleBuf
decl_stmt|;
DECL|field|fill
specifier|static
name|ByteBuffer
name|fill
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|MIN_PREALLOCATION_LENGTH
argument_list|)
decl_stmt|;
DECL|field|shouldSyncWritesAndSkipFsync
specifier|private
name|boolean
name|shouldSyncWritesAndSkipFsync
init|=
literal|false
decl_stmt|;
DECL|field|shouldSkipFsyncForTests
specifier|private
specifier|static
name|boolean
name|shouldSkipFsyncForTests
init|=
literal|false
decl_stmt|;
static|static
block|{
name|fill
operator|.
name|position
argument_list|(
literal|0
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
name|fill
operator|.
name|capacity
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|fill
operator|.
name|put
argument_list|(
name|FSEditLogOpCodes
operator|.
name|OP_INVALID
operator|.
name|getOpCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates output buffers and file object.    *     * @param conf    *          Configuration object    * @param name    *          File name to store edit log    * @param size    *          Size of flush buffer    * @throws IOException    */
DECL|method|EditLogFileOutputStream (Configuration conf, File name, int size)
specifier|public
name|EditLogFileOutputStream
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|File
name|name
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|shouldSyncWritesAndSkipFsync
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_NOEDITLOGCHANNELFLUSH
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_NOEDITLOGCHANNELFLUSH_DEFAULT
argument_list|)
expr_stmt|;
name|file
operator|=
name|name
expr_stmt|;
name|doubleBuf
operator|=
operator|new
name|EditsDoubleBuffer
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|RandomAccessFile
name|rp
decl_stmt|;
if|if
condition|(
name|shouldSyncWritesAndSkipFsync
condition|)
block|{
name|rp
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|name
argument_list|,
literal|"rws"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rp
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|name
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
block|}
name|fp
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|rp
operator|.
name|getFD
argument_list|()
argument_list|)
expr_stmt|;
comment|// open for append
name|fc
operator|=
name|rp
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|fc
operator|.
name|position
argument_list|(
name|fc
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (FSEditLogOp op)
specifier|public
name|void
name|write
parameter_list|(
name|FSEditLogOp
name|op
parameter_list|)
throws|throws
name|IOException
block|{
name|doubleBuf
operator|.
name|writeOp
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write a transaction to the stream. The serialization format is:    *<ul>    *<li>the opcode (byte)</li>    *<li>the transaction id (long)</li>    *<li>the actual Writables for the transaction</li>    *</ul>    * */
annotation|@
name|Override
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
name|doubleBuf
operator|.
name|writeRaw
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create empty edits logs file.    */
annotation|@
name|Override
DECL|method|create ()
specifier|public
name|void
name|create
parameter_list|()
throws|throws
name|IOException
block|{
name|fc
operator|.
name|truncate
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fc
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writeHeader
argument_list|(
name|doubleBuf
operator|.
name|getCurrentBuf
argument_list|()
argument_list|)
expr_stmt|;
name|setReadyToFlush
argument_list|()
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Write header information for this EditLogFileOutputStream to the provided    * DataOutputSream.    *     * @param out the output stream to write the header to.    * @throws IOException in the event of error writing to the stream.    */
annotation|@
name|VisibleForTesting
DECL|method|writeHeader (DataOutputStream out)
specifier|public
specifier|static
name|void
name|writeHeader
parameter_list|(
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|HdfsConstants
operator|.
name|LAYOUT_VERSION
argument_list|)
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
block|{
if|if
condition|(
name|fp
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Trying to use aborted output stream"
argument_list|)
throw|;
block|}
try|try
block|{
comment|// close should have been called after all pending transactions
comment|// have been flushed& synced.
comment|// if already closed, just skip
if|if
condition|(
name|doubleBuf
operator|!=
literal|null
condition|)
block|{
name|doubleBuf
operator|.
name|close
argument_list|()
expr_stmt|;
name|doubleBuf
operator|=
literal|null
expr_stmt|;
block|}
comment|// remove any preallocated padding bytes from the transaction log.
if|if
condition|(
name|fc
operator|!=
literal|null
operator|&&
name|fc
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|fc
operator|.
name|truncate
argument_list|(
name|fc
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|fc
operator|.
name|close
argument_list|()
expr_stmt|;
name|fc
operator|=
literal|null
expr_stmt|;
block|}
name|fp
operator|.
name|close
argument_list|()
expr_stmt|;
name|fp
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|FSNamesystem
operator|.
name|LOG
argument_list|,
name|fc
argument_list|,
name|fp
argument_list|)
expr_stmt|;
name|doubleBuf
operator|=
literal|null
expr_stmt|;
name|fc
operator|=
literal|null
expr_stmt|;
name|fp
operator|=
literal|null
expr_stmt|;
block|}
name|fp
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort ()
specifier|public
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fp
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|fp
argument_list|)
expr_stmt|;
name|fp
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * All data that has been written to the stream so far will be flushed. New    * data can be still written to the stream while flushing is performed.    */
annotation|@
name|Override
DECL|method|setReadyToFlush ()
specifier|public
name|void
name|setReadyToFlush
parameter_list|()
throws|throws
name|IOException
block|{
name|doubleBuf
operator|.
name|setReadyToFlush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Flush ready buffer to persistent store. currentBuffer is not flushed as it    * accumulates new log records while readyBuffer will be flushed and synced.    */
annotation|@
name|Override
DECL|method|flushAndSync (boolean durable)
specifier|public
name|void
name|flushAndSync
parameter_list|(
name|boolean
name|durable
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fp
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Trying to use aborted output stream"
argument_list|)
throw|;
block|}
if|if
condition|(
name|doubleBuf
operator|.
name|isFlushed
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Nothing to flush"
argument_list|)
expr_stmt|;
return|return;
block|}
name|preallocate
argument_list|()
expr_stmt|;
comment|// preallocate file if necessary
name|doubleBuf
operator|.
name|flushTo
argument_list|(
name|fp
argument_list|)
expr_stmt|;
if|if
condition|(
name|durable
operator|&&
operator|!
name|shouldSkipFsyncForTests
operator|&&
operator|!
name|shouldSyncWritesAndSkipFsync
condition|)
block|{
name|fc
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// metadata updates not needed
block|}
block|}
comment|/**    * @return true if the number of buffered data exceeds the intial buffer size    */
annotation|@
name|Override
DECL|method|shouldForceSync ()
specifier|public
name|boolean
name|shouldForceSync
parameter_list|()
block|{
return|return
name|doubleBuf
operator|.
name|shouldForceSync
argument_list|()
return|;
block|}
DECL|method|preallocate ()
specifier|private
name|void
name|preallocate
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|position
init|=
name|fc
operator|.
name|position
argument_list|()
decl_stmt|;
name|long
name|size
init|=
name|fc
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|bufSize
init|=
name|doubleBuf
operator|.
name|getReadyBuf
argument_list|()
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|long
name|need
init|=
name|bufSize
operator|-
operator|(
name|size
operator|-
name|position
operator|)
decl_stmt|;
if|if
condition|(
name|need
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
name|long
name|oldSize
init|=
name|size
decl_stmt|;
name|long
name|total
init|=
literal|0
decl_stmt|;
name|long
name|fillCapacity
init|=
name|fill
operator|.
name|capacity
argument_list|()
decl_stmt|;
while|while
condition|(
name|need
operator|>
literal|0
condition|)
block|{
name|fill
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|writeFully
argument_list|(
name|fc
argument_list|,
name|fill
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|need
operator|-=
name|fillCapacity
expr_stmt|;
name|size
operator|+=
name|fillCapacity
expr_stmt|;
name|total
operator|+=
name|fillCapacity
expr_stmt|;
block|}
if|if
condition|(
name|FSNamesystem
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|FSNamesystem
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Preallocated "
operator|+
name|total
operator|+
literal|" bytes at the end of "
operator|+
literal|"the edit log (offset "
operator|+
name|oldSize
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the file associated with this stream.    */
DECL|method|getFile ()
name|File
name|getFile
parameter_list|()
block|{
return|return
name|file
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
literal|"EditLogFileOutputStream("
operator|+
name|file
operator|+
literal|")"
return|;
block|}
comment|/**    * @return true if this stream is currently open.    */
DECL|method|isOpen ()
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|fp
operator|!=
literal|null
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setFileChannelForTesting (FileChannel fc)
specifier|public
name|void
name|setFileChannelForTesting
parameter_list|(
name|FileChannel
name|fc
parameter_list|)
block|{
name|this
operator|.
name|fc
operator|=
name|fc
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getFileChannelForTesting ()
specifier|public
name|FileChannel
name|getFileChannelForTesting
parameter_list|()
block|{
return|return
name|fc
return|;
block|}
comment|/**    * For the purposes of unit tests, we don't need to actually    * write durably to disk. So, we can skip the fsync() calls    * for a speed improvement.    * @param skip true if fsync should<em>not</em> be called    */
annotation|@
name|VisibleForTesting
DECL|method|setShouldSkipFsyncForTesting (boolean skip)
specifier|public
specifier|static
name|void
name|setShouldSkipFsyncForTesting
parameter_list|(
name|boolean
name|skip
parameter_list|)
block|{
name|shouldSkipFsyncForTests
operator|=
name|skip
expr_stmt|;
block|}
block|}
end_class

end_unit

