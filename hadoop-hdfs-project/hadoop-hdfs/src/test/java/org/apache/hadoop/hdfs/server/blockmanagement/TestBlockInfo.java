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
name|junit
operator|.
name|Test
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
name|*
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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|BlockInfo
import|;
end_import

begin_comment
comment|/**  * This class provides tests for BlockInfo class, which is used in BlocksMap.  * The test covers BlockList.listMoveToHead, used for faster block report  * processing in DatanodeDescriptor.reportDiff.  */
end_comment

begin_class
DECL|class|TestBlockInfo
specifier|public
class|class
name|TestBlockInfo
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.hadoop.hdfs.TestBlockInfo"
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testBlockListMoveToHead ()
specifier|public
name|void
name|testBlockListMoveToHead
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"BlockInfo moveToHead tests..."
argument_list|)
expr_stmt|;
specifier|final
name|int
name|MAX_BLOCKS
init|=
literal|10
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
name|ArrayList
argument_list|<
name|BlockInfo
argument_list|>
name|blockInfoList
init|=
operator|new
name|ArrayList
argument_list|<
name|BlockInfo
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|headIndex
decl_stmt|;
name|int
name|curIndex
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Building block list..."
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
name|FIRST_VALID_STAMP
argument_list|)
argument_list|)
expr_stmt|;
name|blockInfoList
operator|.
name|add
argument_list|(
operator|new
name|BlockInfo
argument_list|(
name|blockList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|dd
operator|.
name|addBlock
argument_list|(
name|blockInfoList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// index of the datanode should be 0
name|assertEquals
argument_list|(
literal|"Find datanode should be 0"
argument_list|,
literal|0
argument_list|,
name|blockInfoList
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|findDatanode
argument_list|(
name|dd
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// list length should be equal to the number of blocks we inserted
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking list length..."
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Length should be MAX_BLOCK"
argument_list|,
name|MAX_BLOCKS
argument_list|,
name|dd
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|BlockInfo
argument_list|>
name|it
init|=
name|dd
operator|.
name|getBlockIterator
argument_list|()
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|len
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"There should be MAX_BLOCK blockInfo's"
argument_list|,
name|MAX_BLOCKS
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|headIndex
operator|=
name|dd
operator|.
name|getHead
argument_list|()
operator|.
name|findDatanode
argument_list|(
name|dd
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Moving each block to the head of the list..."
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
name|MAX_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
name|curIndex
operator|=
name|blockInfoList
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|findDatanode
argument_list|(
name|dd
argument_list|)
expr_stmt|;
name|headIndex
operator|=
name|dd
operator|.
name|moveBlockToHead
argument_list|(
name|blockInfoList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|curIndex
argument_list|,
name|headIndex
argument_list|)
expr_stmt|;
comment|// the moved element must be at the head of the list
name|assertEquals
argument_list|(
literal|"Block should be at the head of the list now."
argument_list|,
name|blockInfoList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|dd
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// move head of the list to the head - this should not change the list
name|LOG
operator|.
name|info
argument_list|(
literal|"Moving head to the head..."
argument_list|)
expr_stmt|;
name|BlockInfo
name|temp
init|=
name|dd
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|curIndex
operator|=
literal|0
expr_stmt|;
name|headIndex
operator|=
literal|0
expr_stmt|;
name|dd
operator|.
name|moveBlockToHead
argument_list|(
name|temp
argument_list|,
name|curIndex
argument_list|,
name|headIndex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Moving head to the head of the list shopuld not change the list"
argument_list|,
name|temp
argument_list|,
name|dd
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
comment|// check all elements of the list against the original blockInfoList
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking elements of the list..."
argument_list|)
expr_stmt|;
name|temp
operator|=
name|dd
operator|.
name|getHead
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Head should not be null"
argument_list|,
name|temp
argument_list|)
expr_stmt|;
name|int
name|c
init|=
name|MAX_BLOCKS
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|temp
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Expected element is not on the list"
argument_list|,
name|blockInfoList
operator|.
name|get
argument_list|(
name|c
operator|--
argument_list|)
argument_list|,
name|temp
argument_list|)
expr_stmt|;
name|temp
operator|=
name|temp
operator|.
name|getNext
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Moving random blocks to the head of the list..."
argument_list|)
expr_stmt|;
name|headIndex
operator|=
name|dd
operator|.
name|getHead
argument_list|()
operator|.
name|findDatanode
argument_list|(
name|dd
argument_list|)
expr_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
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
name|int
name|j
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|MAX_BLOCKS
argument_list|)
decl_stmt|;
name|curIndex
operator|=
name|blockInfoList
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|findDatanode
argument_list|(
name|dd
argument_list|)
expr_stmt|;
name|headIndex
operator|=
name|dd
operator|.
name|moveBlockToHead
argument_list|(
name|blockInfoList
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|,
name|curIndex
argument_list|,
name|headIndex
argument_list|)
expr_stmt|;
comment|// the moved element must be at the head of the list
name|assertEquals
argument_list|(
literal|"Block should be at the head of the list now."
argument_list|,
name|blockInfoList
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|,
name|dd
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

