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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Checksum
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
name|FSConstants
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Writable
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
DECL|class|EditLogFileOutputStream
class|class
name|EditLogFileOutputStream
extends|extends
name|EditLogOutputStream
block|{
DECL|field|EDITS_FILE_HEADER_SIZE_BYTES
specifier|private
specifier|static
name|int
name|EDITS_FILE_HEADER_SIZE_BYTES
init|=
name|Integer
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
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
DECL|field|bufCurrent
specifier|private
name|DataOutputBuffer
name|bufCurrent
decl_stmt|;
comment|// current buffer for writing
DECL|field|bufReady
specifier|private
name|DataOutputBuffer
name|bufReady
decl_stmt|;
comment|// buffer ready for flushing
DECL|field|initBufferSize
specifier|final
specifier|private
name|int
name|initBufferSize
decl_stmt|;
comment|// inital buffer size
DECL|field|fill
specifier|static
name|ByteBuffer
name|fill
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
comment|// preallocation, 1MB
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
comment|/**    * Creates output buffers and file object.    *     * @param name    *          File name to store edit log    * @param size    *          Size of flush buffer    * @throws IOException    */
DECL|method|EditLogFileOutputStream (File name, int size)
name|EditLogFileOutputStream
parameter_list|(
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
name|file
operator|=
name|name
expr_stmt|;
name|initBufferSize
operator|=
name|size
expr_stmt|;
name|bufCurrent
operator|=
operator|new
name|DataOutputBuffer
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|bufReady
operator|=
operator|new
name|DataOutputBuffer
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|RandomAccessFile
name|rp
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|name
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
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
comment|// JournalStream
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|file
operator|.
name|getPath
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// JournalStream
DECL|method|getType ()
specifier|public
name|JournalType
name|getType
parameter_list|()
block|{
return|return
name|JournalType
operator|.
name|FILE
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|write (int b)
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|bufCurrent
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|write (byte op, Writable... writables)
name|void
name|write
parameter_list|(
name|byte
name|op
parameter_list|,
name|Writable
modifier|...
name|writables
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|start
init|=
name|bufCurrent
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|write
argument_list|(
name|op
argument_list|)
expr_stmt|;
for|for
control|(
name|Writable
name|w
range|:
name|writables
control|)
block|{
name|w
operator|.
name|write
argument_list|(
name|bufCurrent
argument_list|)
expr_stmt|;
block|}
comment|// write transaction checksum
name|int
name|end
init|=
name|bufCurrent
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|Checksum
name|checksum
init|=
name|FSEditLog
operator|.
name|getChecksum
argument_list|()
decl_stmt|;
name|checksum
operator|.
name|reset
argument_list|()
expr_stmt|;
name|checksum
operator|.
name|update
argument_list|(
name|bufCurrent
operator|.
name|getData
argument_list|()
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
name|int
name|sum
init|=
operator|(
name|int
operator|)
name|checksum
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|bufCurrent
operator|.
name|writeInt
argument_list|(
name|sum
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create empty edits logs file.    */
annotation|@
name|Override
DECL|method|create ()
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
name|bufCurrent
operator|.
name|writeInt
argument_list|(
name|FSConstants
operator|.
name|LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|setReadyToFlush
argument_list|()
expr_stmt|;
name|flush
argument_list|()
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
try|try
block|{
comment|// close should have been called after all pending transactions
comment|// have been flushed& synced.
comment|// if already closed, just skip
if|if
condition|(
name|bufCurrent
operator|!=
literal|null
condition|)
block|{
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"FSEditStream has "
operator|+
name|bufSize
operator|+
literal|" bytes still to be flushed and cannot "
operator|+
literal|"be closed."
argument_list|)
throw|;
block|}
name|bufCurrent
operator|.
name|close
argument_list|()
expr_stmt|;
name|bufCurrent
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|bufReady
operator|!=
literal|null
condition|)
block|{
name|bufReady
operator|.
name|close
argument_list|()
expr_stmt|;
name|bufReady
operator|=
literal|null
expr_stmt|;
block|}
comment|// remove the last INVALID marker from transaction log.
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
if|if
condition|(
name|fp
operator|!=
literal|null
condition|)
block|{
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
name|bufCurrent
argument_list|,
name|bufReady
argument_list|,
name|fc
argument_list|,
name|fp
argument_list|)
expr_stmt|;
name|bufCurrent
operator|=
name|bufReady
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
block|}
comment|/**    * All data that has been written to the stream so far will be flushed. New    * data can be still written to the stream while flushing is performed.    */
annotation|@
name|Override
DECL|method|setReadyToFlush ()
name|void
name|setReadyToFlush
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|bufReady
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|:
literal|"previous data is not flushed yet"
assert|;
name|write
argument_list|(
name|FSEditLogOpCodes
operator|.
name|OP_INVALID
operator|.
name|getOpCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// insert eof marker
name|DataOutputBuffer
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
comment|/**    * Flush ready buffer to persistent store. currentBuffer is not flushed as it    * accumulates new log records while readyBuffer will be flushed and synced.    */
annotation|@
name|Override
DECL|method|flushAndSync ()
specifier|protected
name|void
name|flushAndSync
parameter_list|()
throws|throws
name|IOException
block|{
name|preallocate
argument_list|()
expr_stmt|;
comment|// preallocate file if necessary
name|bufReady
operator|.
name|writeTo
argument_list|(
name|fp
argument_list|)
expr_stmt|;
comment|// write data to file
name|bufReady
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// erase all data in the buffer
name|fc
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// metadata updates not needed because of preallocation
name|fc
operator|.
name|position
argument_list|(
name|fc
operator|.
name|position
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// skip back the end-of-file marker
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
name|bufReady
operator|.
name|size
argument_list|()
operator|>=
name|initBufferSize
return|;
block|}
comment|/**    * Return the size of the current edit log including buffered data.    */
annotation|@
name|Override
DECL|method|length ()
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
comment|// file size - header size + size of both buffers
return|return
name|fc
operator|.
name|size
argument_list|()
operator|-
name|EDITS_FILE_HEADER_SIZE_BYTES
operator|+
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
comment|// allocate a big chunk of data
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
if|if
condition|(
name|position
operator|+
literal|4096
operator|>=
name|fc
operator|.
name|size
argument_list|()
condition|)
block|{
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
literal|"Preallocating Edit log, current size "
operator|+
name|fc
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fill
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|written
init|=
name|fc
operator|.
name|write
argument_list|(
name|fill
argument_list|,
name|position
argument_list|)
decl_stmt|;
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
literal|"Edit log size is now "
operator|+
name|fc
operator|.
name|size
argument_list|()
operator|+
literal|" written "
operator|+
name|written
operator|+
literal|" bytes "
operator|+
literal|" at offset "
operator|+
name|position
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Operations like OP_JSPOOL_START and OP_CHECKPOINT_TIME should not be    * written into edits file.    */
annotation|@
name|Override
DECL|method|isOperationSupported (byte op)
name|boolean
name|isOperationSupported
parameter_list|(
name|byte
name|op
parameter_list|)
block|{
return|return
name|op
operator|<
name|FSEditLogOpCodes
operator|.
name|OP_JSPOOL_START
operator|.
name|getOpCode
argument_list|()
operator|-
literal|1
return|;
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
block|}
end_class

end_unit

