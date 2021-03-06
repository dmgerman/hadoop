begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
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
name|hdfs
operator|.
name|protocol
operator|.
name|Block
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * This class contains unit tests for PendingRecoveryBlocks.java functionality.  */
end_comment

begin_class
DECL|class|TestPendingRecoveryBlocks
specifier|public
class|class
name|TestPendingRecoveryBlocks
block|{
DECL|field|pendingRecoveryBlocks
specifier|private
name|PendingRecoveryBlocks
name|pendingRecoveryBlocks
decl_stmt|;
DECL|field|recoveryTimeout
specifier|private
specifier|final
name|long
name|recoveryTimeout
init|=
literal|1000L
decl_stmt|;
DECL|field|blk1
specifier|private
specifier|final
name|BlockInfo
name|blk1
init|=
name|getBlock
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|blk2
specifier|private
specifier|final
name|BlockInfo
name|blk2
init|=
name|getBlock
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|blk3
specifier|private
specifier|final
name|BlockInfo
name|blk3
init|=
name|getBlock
argument_list|(
literal|3
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|pendingRecoveryBlocks
operator|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|PendingRecoveryBlocks
argument_list|(
name|recoveryTimeout
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlock (long blockId)
name|BlockInfo
name|getBlock
parameter_list|(
name|long
name|blockId
parameter_list|)
block|{
return|return
operator|new
name|BlockInfoContiguous
argument_list|(
operator|new
name|Block
argument_list|(
name|blockId
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testAddDifferentBlocks ()
specifier|public
name|void
name|testAddDifferentBlocks
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|add
argument_list|(
name|blk1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|isUnderRecovery
argument_list|(
name|blk1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|add
argument_list|(
name|blk2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|isUnderRecovery
argument_list|(
name|blk2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|add
argument_list|(
name|blk3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|isUnderRecovery
argument_list|(
name|blk3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddAndRemoveBlocks ()
specifier|public
name|void
name|testAddAndRemoveBlocks
parameter_list|()
block|{
comment|// Add blocks
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|add
argument_list|(
name|blk1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|add
argument_list|(
name|blk2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove blk1
name|pendingRecoveryBlocks
operator|.
name|remove
argument_list|(
name|blk1
argument_list|)
expr_stmt|;
comment|// Adding back blk1 should succeed
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|add
argument_list|(
name|blk1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddBlockWithPreviousRecoveryTimedOut ()
specifier|public
name|void
name|testAddBlockWithPreviousRecoveryTimedOut
parameter_list|()
block|{
comment|// Add blk
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|0L
argument_list|)
operator|.
name|when
argument_list|(
name|pendingRecoveryBlocks
argument_list|)
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|add
argument_list|(
name|blk1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should fail, has not timed out yet
name|Mockito
operator|.
name|doReturn
argument_list|(
name|recoveryTimeout
operator|/
literal|2
argument_list|)
operator|.
name|when
argument_list|(
name|pendingRecoveryBlocks
argument_list|)
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|add
argument_list|(
name|blk1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should succeed after timing out
name|Mockito
operator|.
name|doReturn
argument_list|(
name|recoveryTimeout
operator|*
literal|2
argument_list|)
operator|.
name|when
argument_list|(
name|pendingRecoveryBlocks
argument_list|)
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|pendingRecoveryBlocks
operator|.
name|add
argument_list|(
name|blk1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

