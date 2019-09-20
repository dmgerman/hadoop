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
name|TimeoutException
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|GenericTestUtils
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
DECL|field|LOG
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestValueQueue
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|method|waitForRefill (ValueQueue<?> valueQueue, String queueName, int queueSize)
specifier|private
name|void
name|waitForRefill
parameter_list|(
name|ValueQueue
argument_list|<
name|?
argument_list|>
name|valueQueue
parameter_list|,
name|String
name|queueName
parameter_list|,
name|int
name|queueSize
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
name|int
name|size
init|=
name|valueQueue
operator|.
name|getSize
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|!=
name|queueSize
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Current ValueQueue size is "
operator|+
name|size
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
argument_list|,
literal|100
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verifies that Queue is initially filled to "numInitValues"    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
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
literal|30000
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
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
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
literal|30000
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
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
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
literal|100
argument_list|,
literal|0.1f
argument_list|,
literal|30000
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
comment|// Trigger a prefill (10) and an async refill (91)
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
comment|// Wait for the async task to finish
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// Refill task should add 91 values to get to a full queue (10 produced by
comment|// the prefill to the low watermark, 1 consumed by getNext())
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|91
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
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
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
literal|30000
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
comment|// Trigger a prefill (5) and an async refill (6)
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
comment|// Wait for the async task to finish
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Refill task should add 6 values to get to a full queue (5 produced by
comment|// the prefill to the low watermark, 1 consumed by getNext())
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
comment|// Take another value, queue is still above the watermark
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
comment|// Wait a while to make sure that no async refills are triggered
try|try
block|{
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|ignored
parameter_list|)
block|{
comment|// This is the correct outcome - no refill is expected
block|}
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
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
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
specifier|final
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
literal|30000
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
comment|// Trigger a prefill (1) and an async refill (10)
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
comment|// Wait for the async task to finish
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Refill task should add 10 values to get to a full queue (1 produced by
comment|// the prefill to the low watermark, 1 consumed by getNext())
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
comment|// Drain completely, no further refills triggered
name|vq
operator|.
name|drain
argument_list|(
literal|"k1"
argument_list|)
expr_stmt|;
comment|// Wait a while to make sure that no async refills are triggered
try|try
block|{
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|ignored
parameter_list|)
block|{
comment|// This is the correct outcome - no refill is expected
block|}
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
comment|// Synchronous call:
comment|// 1. Synchronously fill returned list
comment|// 2. Start another async task to fill the queue in the cache
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed in sync call."
argument_list|,
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Sync call filler got wrong number."
argument_list|,
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
comment|// Wait for the async task to finish
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Refill task should add 10 values to get to a full queue
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed in async call."
argument_list|,
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
comment|// Drain completely after filled by the async thread
name|vq
operator|.
name|drain
argument_list|(
literal|"k1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed to drain completely after async."
argument_list|,
literal|0
argument_list|,
name|vq
operator|.
name|getSize
argument_list|(
literal|"k1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Synchronous call
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed to get all 19."
argument_list|,
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed in sync call."
argument_list|,
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
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
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
literal|30000
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
comment|// Trigger a prefill (3) and an async refill (8)
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
comment|// Wait for the async task to finish
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Refill task should add 8 values to get to a full queue (3 produced by
comment|// the prefill to the low watermark, 1 consumed by getNext())
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed in async call."
argument_list|,
literal|8
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
comment|// Drain completely, no further refills triggered
name|vq
operator|.
name|drain
argument_list|(
literal|"k1"
argument_list|)
expr_stmt|;
comment|// Queue is empty, sync will return a single value and trigger a refill
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
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
comment|// Wait for the async task to finish
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Refill task should add 10 values to get to a full queue
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed in async call."
argument_list|,
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
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
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
literal|30000
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
comment|// Trigger a prefill (3) and an async refill (8)
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
comment|// Wait for the async task to finish
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Refill task should add 8 values to get to a full queue (3 produced by
comment|// the prefill to the low watermark, 1 consumed by getNext())
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed in async call."
argument_list|,
literal|8
argument_list|,
name|filler
operator|.
name|getTop
argument_list|()
operator|.
name|num
argument_list|)
expr_stmt|;
comment|// Drain completely, no further refills triggered
name|vq
operator|.
name|drain
argument_list|(
literal|"k1"
argument_list|)
expr_stmt|;
comment|// Queue is empty, sync will return 3 values and trigger a refill
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
comment|// Wait for the async task to finish
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Refill task should add 10 values to get to a full queue
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed in async call."
argument_list|,
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
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
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
literal|30000
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
comment|// Trigger a prefill (1) and an async refill (10)
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
comment|// Wait for the async task to finish
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Refill task should add 10 values to get to a full queue (1 produced by
comment|// the prefill to the low watermark, 1 consumed by getNext())
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
comment|// Drain completely, no further refills triggered
name|vq
operator|.
name|drain
argument_list|(
literal|"k1"
argument_list|)
expr_stmt|;
comment|// Wait a while to make sure that no async refills are triggered
try|try
block|{
name|waitForRefill
argument_list|(
name|vq
argument_list|,
literal|"k1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|ignored
parameter_list|)
block|{
comment|// This is the correct outcome - no refill is expected
block|}
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

