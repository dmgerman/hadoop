begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
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
name|util
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|LinkedBlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|ValueQueue
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|ValueQueue
operator|.
name|QueueRefiller
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|ValueQueue
operator|.
name|SyncGenerationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_class
DECL|class|TestValueQueue
specifier|public
class|class
name|TestValueQueue
block|{
DECL|class|FillInfo
specifier|private
specifier|static
class|class
name|FillInfo
block|{
DECL|field|num
specifier|final
name|int
name|num
decl_stmt|;
DECL|field|key
specifier|final
name|String
name|key
decl_stmt|;
DECL|method|FillInfo (int num, String key)
name|FillInfo
parameter_list|(
name|int
name|num
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|num
operator|=
name|num
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
block|}
DECL|class|MockFiller
specifier|private
specifier|static
class|class
name|MockFiller
implements|implements
name|QueueRefiller
argument_list|<
name|String
argument_list|>
block|{
DECL|field|fillCalls
specifier|final
name|LinkedBlockingQueue
argument_list|<
name|FillInfo
argument_list|>
name|fillCalls
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|FillInfo
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|fillQueueForKey (String keyName, Queue<String> keyQueue, int numValues)
specifier|public
name|void
name|fillQueueForKey
parameter_list|(
name|String
name|keyName
parameter_list|,
name|Queue
argument_list|<
name|String
argument_list|>
name|keyQueue
parameter_list|,
name|int
name|numValues
parameter_list|)
throws|throws
name|IOException
block|{
name|fillCalls
operator|.
name|add
argument_list|(
operator|new
name|FillInfo
argument_list|(
name|numValues
argument_list|,
name|keyName
argument_list|)
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
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|keyQueue
operator|.
name|add
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getTop ()
specifier|public
name|FillInfo
name|getTop
parameter_list|()
throws|throws
name|InterruptedException
block|{
return|return
name|fillCalls
operator|.
name|poll
argument_list|(
literal|500
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
block|}
comment|/**    * Verifies that Queue is initially filled to "numInitValues"    */
annotation|@
name|Test
DECL|method|testInitFill ()
specifier|public
name|void
name|testInitFill
parameter_list|()
throws|throws
name|Exception
block|{
name|MockFiller
name|filler
init|=
operator|new
name|MockFiller
argument_list|()
decl_stmt|;
name|ValueQueue
argument_list|<
name|String
argument_list|>
name|vq
init|=
operator|new
name|ValueQueue
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|,
literal|0.1f
argument_list|,
literal|300
argument_list|,
literal|1
argument_list|,
name|SyncGenerationPolicy
operator|.
name|ALL
argument_list|,
name|filler
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|vq
operator|.
name|getNext
argument_list|(
literal|"k1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
name|vq
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verifies that Queue is initialized (Warmed-up) for provided keys    */
annotation|@
name|Test
DECL|method|testWarmUp ()
specifier|public
name|void
name|testWarmUp
parameter_list|()
throws|throws
name|Exception
block|{
name|MockFiller
name|filler
init|=
operator|new
name|MockFiller
argument_list|()
decl_stmt|;
name|ValueQueue
argument_list|<
name|String
argument_list|>
name|vq
init|=
operator|new
name|ValueQueue
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|,
literal|0.5f
argument_list|,
literal|300
argument_list|,
literal|1
argument_list|,
name|SyncGenerationPolicy
operator|.
name|ALL
argument_list|,
name|filler
argument_list|)
decl_stmt|;
name|vq
operator|.
name|initializeQueuesForKeys
argument_list|(
literal|"k1"
argument_list|,
literal|"k2"
argument_list|,
literal|"k3"
argument_list|)
expr_stmt|;
name|FillInfo
index|[]
name|fillInfos
init|=
block|{
name|filler
operator|.
name|getTop
argument_list|()
block|,
name|filler
operator|.
name|getTop
argument_list|()
block|,
name|filler
operator|.
name|getTop
argument_list|()
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fillInfos
index|[
literal|0
index|]
operator|.
name|num
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fillInfos
index|[
literal|1
index|]
operator|.
name|num
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fillInfos
index|[
literal|2
index|]
operator|.
name|num
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"k1"
argument_list|,
literal|"k2"
argument_list|,
literal|"k3"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|fillInfos
index|[
literal|0
index|]
operator|.
name|key
argument_list|,
name|fillInfos
index|[
literal|1
index|]
operator|.
name|key
argument_list|,
name|fillInfos
index|[
literal|2
index|]
operator|.
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|vq
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verifies that the refill task is executed after "checkInterval" if    * num values below "lowWatermark"    */
annotation|@
name|Test
DECL|method|testRefill ()
specifier|public
name|void
name|testRefill
parameter_list|()
throws|throws
name|Exception
block|{
name|MockFiller
name|filler
init|=
operator|new
name|MockFiller
argument_list|()
decl_stmt|;
name|ValueQueue
argument_list|<
name|String
argument_list|>
name|vq
init|=
operator|new
name|ValueQueue
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|,
literal|0.1f
argument_list|,
literal|300
argument_list|,
literal|1
argument_list|,
name|SyncGenerationPolicy
operator|.
name|ALL
argument_list|,
name|filler
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|vq
operator|.
name|getNext
argument_list|(
literal|"k1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
comment|// Trigger refill
name|vq
operator|.
name|getNext
argument_list|(
literal|"k1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
name|vq
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verifies that the No refill Happens after "checkInterval" if    * num values above "lowWatermark"    */
annotation|@
name|Test
DECL|method|testNoRefill ()
specifier|public
name|void
name|testNoRefill
parameter_list|()
throws|throws
name|Exception
block|{
name|MockFiller
name|filler
init|=
operator|new
name|MockFiller
argument_list|()
decl_stmt|;
name|ValueQueue
argument_list|<
name|String
argument_list|>
name|vq
init|=
operator|new
name|ValueQueue
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|,
literal|0.5f
argument_list|,
literal|300
argument_list|,
literal|1
argument_list|,
name|SyncGenerationPolicy
operator|.
name|ALL
argument_list|,
name|filler
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|vq
operator|.
name|getNext
argument_list|(
literal|"k1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
argument_list|)
expr_stmt|;
name|vq
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verify getAtMost when SyncGeneration Policy = ALL    */
annotation|@
name|Test
DECL|method|testgetAtMostPolicyALL ()
specifier|public
name|void
name|testgetAtMostPolicyALL
parameter_list|()
throws|throws
name|Exception
block|{
name|MockFiller
name|filler
init|=
operator|new
name|MockFiller
argument_list|()
decl_stmt|;
name|ValueQueue
argument_list|<
name|String
argument_list|>
name|vq
init|=
operator|new
name|ValueQueue
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|,
literal|0.1f
argument_list|,
literal|300
argument_list|,
literal|1
argument_list|,
name|SyncGenerationPolicy
operator|.
name|ALL
argument_list|,
name|filler
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|vq
operator|.
name|getNext
argument_list|(
literal|"k1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
comment|// Drain completely
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|vq
operator|.
name|getAtMost
argument_list|(
literal|"k1"
argument_list|,
literal|10
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Synchronous call
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
comment|// Ask for more... return all
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|19
argument_list|,
name|vq
operator|.
name|getAtMost
argument_list|(
literal|"k1"
argument_list|,
literal|19
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Synchronous call (No Async call since num> lowWatermark)
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|19
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
name|vq
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verify getAtMost when SyncGeneration Policy = ALL    */
annotation|@
name|Test
DECL|method|testgetAtMostPolicyATLEAST_ONE ()
specifier|public
name|void
name|testgetAtMostPolicyATLEAST_ONE
parameter_list|()
throws|throws
name|Exception
block|{
name|MockFiller
name|filler
init|=
operator|new
name|MockFiller
argument_list|()
decl_stmt|;
name|ValueQueue
argument_list|<
name|String
argument_list|>
name|vq
init|=
operator|new
name|ValueQueue
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|,
literal|0.3f
argument_list|,
literal|300
argument_list|,
literal|1
argument_list|,
name|SyncGenerationPolicy
operator|.
name|ATLEAST_ONE
argument_list|,
name|filler
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|vq
operator|.
name|getNext
argument_list|(
literal|"k1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
comment|// Drain completely
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|vq
operator|.
name|getAtMost
argument_list|(
literal|"k1"
argument_list|,
literal|10
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Asynch Refill call
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
name|vq
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verify getAtMost when SyncGeneration Policy = LOW_WATERMARK    */
annotation|@
name|Test
DECL|method|testgetAtMostPolicyLOW_WATERMARK ()
specifier|public
name|void
name|testgetAtMostPolicyLOW_WATERMARK
parameter_list|()
throws|throws
name|Exception
block|{
name|MockFiller
name|filler
init|=
operator|new
name|MockFiller
argument_list|()
decl_stmt|;
name|ValueQueue
argument_list|<
name|String
argument_list|>
name|vq
init|=
operator|new
name|ValueQueue
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|,
literal|0.3f
argument_list|,
literal|300
argument_list|,
literal|1
argument_list|,
name|SyncGenerationPolicy
operator|.
name|LOW_WATERMARK
argument_list|,
name|filler
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|vq
operator|.
name|getNext
argument_list|(
literal|"k1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
comment|// Drain completely
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|vq
operator|.
name|getAtMost
argument_list|(
literal|"k1"
argument_list|,
literal|10
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Synchronous call
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
comment|// Asynch Refill call
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
name|vq
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDrain ()
specifier|public
name|void
name|testDrain
parameter_list|()
throws|throws
name|Exception
block|{
name|MockFiller
name|filler
init|=
operator|new
name|MockFiller
argument_list|()
decl_stmt|;
name|ValueQueue
argument_list|<
name|String
argument_list|>
name|vq
init|=
operator|new
name|ValueQueue
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|,
literal|0.1f
argument_list|,
literal|300
argument_list|,
literal|1
argument_list|,
name|SyncGenerationPolicy
operator|.
name|ALL
argument_list|,
name|filler
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|vq
operator|.
name|getNext
argument_list|(
literal|"k1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
name|vq
operator|.
name|drain
argument_list|(
literal|"k1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|filler
operator|.
name|getTop
argument_list|()
argument_list|)
expr_stmt|;
name|vq
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

