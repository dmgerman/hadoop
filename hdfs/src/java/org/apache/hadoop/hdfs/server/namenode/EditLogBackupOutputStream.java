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
name|ArrayList
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
name|NamenodeProtocol
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
name|Writable
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
name|ipc
operator|.
name|RPC
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
name|NamenodeProtocol
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
DECL|field|bufCurrent
specifier|private
name|ArrayList
argument_list|<
name|JournalRecord
argument_list|>
name|bufCurrent
decl_stmt|;
comment|// current buffer for writing
DECL|field|bufReady
specifier|private
name|ArrayList
argument_list|<
name|JournalRecord
argument_list|>
name|bufReady
decl_stmt|;
comment|// buffer ready for flushing
DECL|field|out
specifier|private
name|DataOutputBuffer
name|out
decl_stmt|;
comment|// serialized output sent to backup node
DECL|class|JournalRecord
specifier|static
class|class
name|JournalRecord
block|{
DECL|field|op
name|byte
name|op
decl_stmt|;
DECL|field|args
name|Writable
index|[]
name|args
decl_stmt|;
DECL|method|JournalRecord (byte op, Writable ... writables)
name|JournalRecord
parameter_list|(
name|byte
name|op
parameter_list|,
name|Writable
modifier|...
name|writables
parameter_list|)
block|{
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|writables
expr_stmt|;
block|}
DECL|method|write (DataOutputStream out)
name|void
name|write
parameter_list|(
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|op
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|==
literal|null
condition|)
return|return;
for|for
control|(
name|Writable
name|w
range|:
name|args
control|)
name|w
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Storage
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"EditLogBackupOutputStream connects to: "
operator|+
name|bnAddress
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|backupNode
operator|=
operator|(
name|NamenodeProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|NamenodeProtocol
operator|.
name|class
argument_list|,
name|NamenodeProtocol
operator|.
name|versionID
argument_list|,
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
name|bufCurrent
operator|=
operator|new
name|ArrayList
argument_list|<
name|JournalRecord
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|bufReady
operator|=
operator|new
name|ArrayList
argument_list|<
name|JournalRecord
argument_list|>
argument_list|()
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
comment|// JournalStream
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|bnRegistration
operator|.
name|getAddress
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
name|BACKUP
return|;
block|}
annotation|@
name|Override
comment|// EditLogOutputStream
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
comment|// EditLogOutputStream
DECL|method|write (byte op, Writable ... writables)
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
name|bufCurrent
operator|.
name|add
argument_list|(
operator|new
name|JournalRecord
argument_list|(
name|op
argument_list|,
name|writables
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * There is no persistent storage. Just clear the buffers.    */
annotation|@
name|Override
comment|// EditLogOutputStream
DECL|method|create ()
name|void
name|create
parameter_list|()
throws|throws
name|IOException
block|{
name|bufCurrent
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|bufCurrent
operator|.
name|size
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
name|RPC
operator|.
name|stopProxy
argument_list|(
name|backupNode
argument_list|)
expr_stmt|;
comment|// stop the RPC threads
name|bufCurrent
operator|=
name|bufReady
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
comment|// EditLogOutputStream
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
name|ArrayList
argument_list|<
name|JournalRecord
argument_list|>
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
name|size
argument_list|()
operator|==
literal|0
operator|:
literal|"Output buffer is not empty"
assert|;
name|int
name|bufReadySize
init|=
name|bufReady
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|bufReadySize
condition|;
name|idx
operator|++
control|)
block|{
name|JournalRecord
name|jRec
init|=
literal|null
decl_stmt|;
for|for
control|(
init|;
name|idx
operator|<
name|bufReadySize
condition|;
name|idx
operator|++
control|)
block|{
name|jRec
operator|=
name|bufReady
operator|.
name|get
argument_list|(
name|idx
argument_list|)
expr_stmt|;
if|if
condition|(
name|jRec
operator|.
name|op
operator|>=
name|FSEditLogOpCodes
operator|.
name|OP_JSPOOL_START
operator|.
name|getOpCode
argument_list|()
condition|)
break|break;
comment|// special operation should be sent in a separate call to BN
name|jRec
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|out
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|send
argument_list|(
name|NamenodeProtocol
operator|.
name|JA_JOURNAL
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|==
name|bufReadySize
condition|)
break|break;
comment|// operation like start journal spool or increment checkpoint time
comment|// is a separate call to BN
name|jRec
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|send
argument_list|(
name|jRec
operator|.
name|op
argument_list|)
expr_stmt|;
block|}
name|bufReady
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// erase all data in the buffer
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// reset buffer to the start position
block|}
comment|/**    * There is no persistent storage. Therefore length is 0.<p>    * Length is used to check when it is large enough to start a checkpoint.    * This criteria should not be used for backup streams.    */
annotation|@
name|Override
comment|// EditLogOutputStream
DECL|method|length ()
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
DECL|method|send (int ja)
specifier|private
name|void
name|send
parameter_list|(
name|int
name|ja
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|int
name|length
init|=
name|out
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|out
operator|.
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
name|backupNode
operator|.
name|journal
argument_list|(
name|nnRegistration
argument_list|,
name|ja
argument_list|,
name|length
argument_list|,
name|out
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|reset
argument_list|()
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
comment|/**    * Verify that the backup node is alive.    */
DECL|method|isAlive ()
name|boolean
name|isAlive
parameter_list|()
block|{
try|try
block|{
name|send
argument_list|(
name|NamenodeProtocol
operator|.
name|JA_IS_ALIVE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ei
parameter_list|)
block|{
name|Storage
operator|.
name|LOG
operator|.
name|info
argument_list|(
name|bnRegistration
operator|.
name|getRole
argument_list|()
operator|+
literal|" "
operator|+
name|bnRegistration
operator|.
name|getAddress
argument_list|()
operator|+
literal|" is not alive. "
argument_list|,
name|ei
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

