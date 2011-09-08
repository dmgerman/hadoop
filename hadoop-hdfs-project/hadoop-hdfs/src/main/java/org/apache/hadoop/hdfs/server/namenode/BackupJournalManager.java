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
name|NNStorageRetentionManager
operator|.
name|StoragePurger
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

begin_comment
comment|/**  * A JournalManager implementation that uses RPCs to log transactions  * to a BackupNode.  */
end_comment

begin_class
DECL|class|BackupJournalManager
class|class
name|BackupJournalManager
implements|implements
name|JournalManager
block|{
DECL|field|nnReg
specifier|private
specifier|final
name|NamenodeRegistration
name|nnReg
decl_stmt|;
DECL|field|bnReg
specifier|private
specifier|final
name|NamenodeRegistration
name|bnReg
decl_stmt|;
DECL|method|BackupJournalManager (NamenodeRegistration bnReg, NamenodeRegistration nnReg)
name|BackupJournalManager
parameter_list|(
name|NamenodeRegistration
name|bnReg
parameter_list|,
name|NamenodeRegistration
name|nnReg
parameter_list|)
block|{
name|this
operator|.
name|bnReg
operator|=
name|bnReg
expr_stmt|;
name|this
operator|.
name|nnReg
operator|=
name|nnReg
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startLogSegment (long txId)
specifier|public
name|EditLogOutputStream
name|startLogSegment
parameter_list|(
name|long
name|txId
parameter_list|)
throws|throws
name|IOException
block|{
name|EditLogBackupOutputStream
name|stm
init|=
operator|new
name|EditLogBackupOutputStream
argument_list|(
name|bnReg
argument_list|,
name|nnReg
argument_list|)
decl_stmt|;
name|stm
operator|.
name|startLogSegment
argument_list|(
name|txId
argument_list|)
expr_stmt|;
return|return
name|stm
return|;
block|}
annotation|@
name|Override
DECL|method|finalizeLogSegment (long firstTxId, long lastTxId)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|setOutputBufferCapacity (int size)
specifier|public
name|void
name|setOutputBufferCapacity
parameter_list|(
name|int
name|size
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|purgeLogsOlderThan (long minTxIdToKeep)
specifier|public
name|void
name|purgeLogsOlderThan
parameter_list|(
name|long
name|minTxIdToKeep
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|getNumberOfTransactions (long fromTxnId)
specifier|public
name|long
name|getNumberOfTransactions
parameter_list|(
name|long
name|fromTxnId
parameter_list|)
throws|throws
name|IOException
throws|,
name|CorruptionException
block|{
comment|// This JournalManager is never used for input. Therefore it cannot
comment|// return any transactions
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getInputStream (long fromTxnId)
specifier|public
name|EditLogInputStream
name|getInputStream
parameter_list|(
name|long
name|fromTxnId
parameter_list|)
throws|throws
name|IOException
block|{
comment|// This JournalManager is never used for input. Therefore it cannot
comment|// return any transactions
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unsupported operation"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|recoverUnfinalizedSegments ()
specifier|public
name|void
name|recoverUnfinalizedSegments
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|method|matchesRegistration (NamenodeRegistration bnReg)
specifier|public
name|boolean
name|matchesRegistration
parameter_list|(
name|NamenodeRegistration
name|bnReg
parameter_list|)
block|{
return|return
name|bnReg
operator|.
name|getAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|bnReg
operator|.
name|getAddress
argument_list|()
argument_list|)
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
literal|"BackupJournalManager"
return|;
block|}
block|}
end_class

end_unit

