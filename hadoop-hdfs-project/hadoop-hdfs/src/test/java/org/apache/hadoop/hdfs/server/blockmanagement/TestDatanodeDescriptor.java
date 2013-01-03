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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|DFSTestUtil
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
name|Block
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
name|GenerationStamp
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

begin_comment
comment|/**  * This class tests that methods in DatanodeDescriptor  */
end_comment

begin_class
DECL|class|TestDatanodeDescriptor
specifier|public
class|class
name|TestDatanodeDescriptor
block|{
comment|/**    * Test that getInvalidateBlocks observes the maxlimit.    */
annotation|@
name|Test
DECL|method|testGetInvalidateBlocks ()
specifier|public
name|void
name|testGetInvalidateBlocks
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|MAX_BLOCKS
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|REMAINING_BLOCKS
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|MAX_LIMIT
init|=
name|MAX_BLOCKS
operator|-
name|REMAINING_BLOCKS
decl_stmt|;
name|DatanodeDescriptor
name|dd
init|=
name|DFSTestUtil
operator|.
name|getLocalDatanodeDescriptor
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Block
argument_list|>
name|blockList
init|=
operator|new
name|ArrayList
argument_list|<
name|Block
argument_list|>
argument_list|(
name|MAX_BLOCKS
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MAX_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
name|blockList
operator|.
name|add
argument_list|(
operator|new
name|Block
argument_list|(
name|i
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dd
operator|.
name|addBlocksToBeInvalidated
argument_list|(
name|blockList
argument_list|)
expr_stmt|;
name|Block
index|[]
name|bc
init|=
name|dd
operator|.
name|getInvalidateBlocks
argument_list|(
name|MAX_LIMIT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bc
operator|.
name|length
argument_list|,
name|MAX_LIMIT
argument_list|)
expr_stmt|;
name|bc
operator|=
name|dd
operator|.
name|getInvalidateBlocks
argument_list|(
name|MAX_LIMIT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bc
operator|.
name|length
argument_list|,
name|REMAINING_BLOCKS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlocksCounter ()
specifier|public
name|void
name|testBlocksCounter
parameter_list|()
throws|throws
name|Exception
block|{
name|DatanodeDescriptor
name|dd
init|=
name|DFSTestUtil
operator|.
name|getLocalDatanodeDescriptor
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dd
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|BlockInfo
name|blk
init|=
operator|new
name|BlockInfo
argument_list|(
operator|new
name|Block
argument_list|(
literal|1L
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|BlockInfo
name|blk1
init|=
operator|new
name|BlockInfo
argument_list|(
operator|new
name|Block
argument_list|(
literal|2L
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// add first block
name|assertTrue
argument_list|(
name|dd
operator|.
name|addBlock
argument_list|(
name|blk
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dd
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove a non-existent block
name|assertFalse
argument_list|(
name|dd
operator|.
name|removeBlock
argument_list|(
name|blk1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dd
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
comment|// add an existent block
name|assertFalse
argument_list|(
name|dd
operator|.
name|addBlock
argument_list|(
name|blk
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dd
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
comment|// add second block
name|assertTrue
argument_list|(
name|dd
operator|.
name|addBlock
argument_list|(
name|blk1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dd
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove first block
name|assertTrue
argument_list|(
name|dd
operator|.
name|removeBlock
argument_list|(
name|blk
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dd
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove second block
name|assertTrue
argument_list|(
name|dd
operator|.
name|removeBlock
argument_list|(
name|blk1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dd
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

