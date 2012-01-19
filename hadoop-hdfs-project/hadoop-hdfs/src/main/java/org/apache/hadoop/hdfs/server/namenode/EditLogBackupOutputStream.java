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
name|net
operator|.
name|InetSocketAddress
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
name|hadoop
operator|.
name|hdfs
operator|.
name|HdfsConfiguration
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
name|protocolPB
operator|.
name|JournalProtocolTranslatorPB
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
name|protocol
operator|.
name|NamenodeRegistration
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
name|net
operator|.
name|NetUtils
import|;
end_import

begin_comment
comment|/**  * An implementation of the abstract class {@link EditLogOutputStream},  * which streams edits to a backup node.  *   * @see org.apache.hadoop.hdfs.server.protocol.NamenodeProtocol#journal  * (org.apache.hadoop.hdfs.server.protocol.NamenodeRegistration,  *  int, int, byte[])  */
end_comment

begin_class
DECL|class|EditLogBackupOutputStream
class|class
name|EditLogBackupOutputStream
extends|extends
name|EditLogOutputStream
block|{
DECL|field|DEFAULT_BUFFER_SIZE
specifier|static
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|256
decl_stmt|;
DECL|field|backupNode
specifier|private
name|JournalProtocolTranslatorPB
name|backupNode
decl_stmt|;
comment|// RPC proxy to backup node
DECL|field|bnRegistration
specifier|private
name|NamenodeRegistration
name|bnRegistration
decl_stmt|;
comment|// backup node registration
DECL|field|nnRegistration
specifier|private
name|NamenodeRegistration
name|nnRegistration
decl_stmt|;
comment|// active node registration
DECL|field|doubleBuf
specifier|private
name|EditsDoubleBuffer
name|doubleBuf
decl_stmt|;
DECL|field|out
specifier|private
name|DataOutputBuffer
name|out
decl_stmt|;
comment|// serialized output sent to backup node
DECL|method|EditLogBackupOutputStream (NamenodeRegistration bnReg, NamenodeRegistration nnReg)
name|EditLogBackupOutputStream
parameter_list|(
name|NamenodeRegistration
name|bnReg
parameter_list|,
comment|// backup node
name|NamenodeRegistration
name|nnReg
parameter_list|)
comment|// active name-node
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|bnRegistration
operator|=
name|bnReg
expr_stmt|;
name|this
operator|.
name|nnRegistration
operator|=
name|nnReg
expr_stmt|;
name|InetSocketAddress
name|bnAddress
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|bnRegistration
operator|.
name|getAddress
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|backupNode
operator|=
operator|new
name|JournalProtocolTranslatorPB
argument_list|(
name|bnAddress
argument_list|,
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Storage
operator|.
name|LOG
operator|.
name|error
argument_list|(
literal|"Error connecting to: "
operator|+
name|bnAddress
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|this
operator|.
name|doubleBuf
operator|=
operator|new
name|EditsDoubleBuffer
argument_list|(
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
operator|new
name|DataOutputBuffer
argument_list|(
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|// EditLogOutputStream
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
comment|/**    * There is no persistent storage. Just clear the buffers.    */
annotation|@
name|Override
comment|// EditLogOutputStream
DECL|method|create ()
specifier|public
name|void
name|create
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|doubleBuf
operator|.
name|isFlushed
argument_list|()
operator|:
literal|"previous data is not flushed yet"
assert|;
name|this
operator|.
name|doubleBuf
operator|=
operator|new
name|EditsDoubleBuffer
argument_list|(
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|// EditLogOutputStream
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// close should have been called after all pending transactions
comment|// have been flushed& synced.
name|int
name|size
init|=
name|doubleBuf
operator|.
name|countBufferedBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"BackupEditStream has "
operator|+
name|size
operator|+
literal|" records still to be flushed and cannot be closed."
argument_list|)
throw|;
block|}
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|Storage
operator|.
name|LOG
argument_list|,
name|backupNode
argument_list|)
expr_stmt|;
comment|// stop the RPC threads
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
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|Storage
operator|.
name|LOG
argument_list|,
name|backupNode
argument_list|)
expr_stmt|;
name|doubleBuf
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
comment|// EditLogOutputStream
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
annotation|@
name|Override
comment|// EditLogOutputStream
DECL|method|flushAndSync ()
specifier|protected
name|void
name|flushAndSync
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|out
operator|.
name|getLength
argument_list|()
operator|==
literal|0
operator|:
literal|"Output buffer is not empty"
assert|;
name|int
name|numReadyTxns
init|=
name|doubleBuf
operator|.
name|countReadyTxns
argument_list|()
decl_stmt|;
name|long
name|firstTxToFlush
init|=
name|doubleBuf
operator|.
name|getFirstReadyTxId
argument_list|()
decl_stmt|;
name|doubleBuf
operator|.
name|flushTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
assert|assert
name|numReadyTxns
operator|>
literal|0
assert|;
name|byte
index|[]
name|data
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
assert|assert
name|out
operator|.
name|getLength
argument_list|()
operator|==
literal|0
operator|:
literal|"Output buffer is not empty"
assert|;
name|backupNode
operator|.
name|journal
argument_list|(
name|nnRegistration
argument_list|,
name|firstTxToFlush
argument_list|,
name|numReadyTxns
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get backup node registration.    */
DECL|method|getRegistration ()
name|NamenodeRegistration
name|getRegistration
parameter_list|()
block|{
return|return
name|bnRegistration
return|;
block|}
DECL|method|startLogSegment (long txId)
name|void
name|startLogSegment
parameter_list|(
name|long
name|txId
parameter_list|)
throws|throws
name|IOException
block|{
name|backupNode
operator|.
name|startLogSegment
argument_list|(
name|nnRegistration
argument_list|,
name|txId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

