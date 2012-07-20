begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
operator|.
name|client
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
name|EditLogOutputStream
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
name|EditsDoubleBuffer
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

begin_comment
comment|/**  * EditLogOutputStream implementation that writes to a quorum of  * remote journals.  */
end_comment

begin_class
DECL|class|QuorumOutputStream
class|class
name|QuorumOutputStream
extends|extends
name|EditLogOutputStream
block|{
DECL|field|loggers
specifier|private
specifier|final
name|AsyncLoggerSet
name|loggers
decl_stmt|;
DECL|field|buf
specifier|private
name|EditsDoubleBuffer
name|buf
decl_stmt|;
DECL|method|QuorumOutputStream (AsyncLoggerSet loggers)
specifier|public
name|QuorumOutputStream
parameter_list|(
name|AsyncLoggerSet
name|loggers
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|buf
operator|=
operator|new
name|EditsDoubleBuffer
argument_list|(
literal|256
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// TODO: conf
name|this
operator|.
name|loggers
operator|=
name|loggers
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
name|buf
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
name|buf
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
name|buf
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|close
argument_list|()
expr_stmt|;
name|buf
operator|=
literal|null
expr_stmt|;
block|}
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
name|QuorumJournalManager
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Aborting "
operator|+
name|this
argument_list|)
expr_stmt|;
name|buf
operator|=
literal|null
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
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
name|buf
operator|.
name|setReadyToFlush
argument_list|()
expr_stmt|;
block|}
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
name|int
name|numReadyBytes
init|=
name|buf
operator|.
name|countReadyBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|numReadyBytes
operator|>
literal|0
condition|)
block|{
name|int
name|numReadyTxns
init|=
name|buf
operator|.
name|countReadyTxns
argument_list|()
decl_stmt|;
name|long
name|firstTxToFlush
init|=
name|buf
operator|.
name|getFirstReadyTxId
argument_list|()
decl_stmt|;
assert|assert
name|numReadyTxns
operator|>
literal|0
assert|;
comment|// Copy from our double-buffer into a new byte array. This is for
comment|// two reasons:
comment|// 1) The IPC code has no way of specifying to send only a slice of
comment|//    a larger array.
comment|// 2) because the calls to the underlying nodes are asynchronous, we
comment|//    need a defensive copy to avoid accidentally mutating the buffer
comment|//    before it is sent.
name|DataOutputBuffer
name|bufToSend
init|=
operator|new
name|DataOutputBuffer
argument_list|(
name|numReadyBytes
argument_list|)
decl_stmt|;
name|buf
operator|.
name|flushTo
argument_list|(
name|bufToSend
argument_list|)
expr_stmt|;
assert|assert
name|bufToSend
operator|.
name|getLength
argument_list|()
operator|==
name|numReadyBytes
assert|;
name|byte
index|[]
name|data
init|=
name|bufToSend
operator|.
name|getData
argument_list|()
decl_stmt|;
assert|assert
name|data
operator|.
name|length
operator|==
name|bufToSend
operator|.
name|getLength
argument_list|()
assert|;
name|QuorumCall
argument_list|<
name|AsyncLogger
argument_list|,
name|Void
argument_list|>
name|qcall
init|=
name|loggers
operator|.
name|sendEdits
argument_list|(
name|firstTxToFlush
argument_list|,
name|numReadyTxns
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|loggers
operator|.
name|waitForWriteQuorum
argument_list|(
name|qcall
argument_list|,
literal|20000
argument_list|)
expr_stmt|;
comment|// TODO: configurable timeout
block|}
block|}
block|}
end_class

end_unit

