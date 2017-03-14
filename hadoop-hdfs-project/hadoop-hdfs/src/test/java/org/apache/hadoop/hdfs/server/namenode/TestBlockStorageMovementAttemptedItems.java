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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
operator|.
name|monotonicNow
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
name|BlocksStorageMovementResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_comment
comment|/**  * Tests that block storage movement attempt failures are reported from DN and  * processed them correctly or not.  */
end_comment

begin_class
DECL|class|TestBlockStorageMovementAttemptedItems
specifier|public
class|class
name|TestBlockStorageMovementAttemptedItems
block|{
DECL|field|bsmAttemptedItems
specifier|private
name|BlockStorageMovementAttemptedItems
name|bsmAttemptedItems
init|=
literal|null
decl_stmt|;
DECL|field|unsatisfiedStorageMovementFiles
specifier|private
name|BlockStorageMovementNeeded
name|unsatisfiedStorageMovementFiles
init|=
literal|null
decl_stmt|;
DECL|field|selfRetryTimeout
specifier|private
specifier|final
name|int
name|selfRetryTimeout
init|=
literal|500
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|unsatisfiedStorageMovementFiles
operator|=
operator|new
name|BlockStorageMovementNeeded
argument_list|()
expr_stmt|;
name|StoragePolicySatisfier
name|sps
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StoragePolicySatisfier
operator|.
name|class
argument_list|)
decl_stmt|;
name|bsmAttemptedItems
operator|=
operator|new
name|BlockStorageMovementAttemptedItems
argument_list|(
literal|100
argument_list|,
name|selfRetryTimeout
argument_list|,
name|unsatisfiedStorageMovementFiles
argument_list|,
name|sps
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
if|if
condition|(
name|bsmAttemptedItems
operator|!=
literal|null
condition|)
block|{
name|bsmAttemptedItems
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checkItemMovedForRetry (Long item, long retryTimeout)
specifier|private
name|boolean
name|checkItemMovedForRetry
parameter_list|(
name|Long
name|item
parameter_list|,
name|long
name|retryTimeout
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|stopTime
init|=
name|monotonicNow
argument_list|()
operator|+
operator|(
name|retryTimeout
operator|*
literal|2
operator|)
decl_stmt|;
name|boolean
name|isItemFound
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|monotonicNow
argument_list|()
operator|<
operator|(
name|stopTime
operator|)
condition|)
block|{
name|Long
name|ele
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|ele
operator|=
name|unsatisfiedStorageMovementFiles
operator|.
name|get
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|item
operator|.
name|longValue
argument_list|()
operator|==
name|ele
operator|.
name|longValue
argument_list|()
condition|)
block|{
name|isItemFound
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|isItemFound
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|isItemFound
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testAddResultWithFailureResult ()
specifier|public
name|void
name|testAddResultWithFailureResult
parameter_list|()
throws|throws
name|Exception
block|{
name|bsmAttemptedItems
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// start block movement result monitor thread
name|Long
name|item
init|=
operator|new
name|Long
argument_list|(
literal|1234
argument_list|)
decl_stmt|;
name|bsmAttemptedItems
operator|.
name|add
argument_list|(
name|item
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|bsmAttemptedItems
operator|.
name|addResults
argument_list|(
operator|new
name|BlocksStorageMovementResult
index|[]
block|{
operator|new
name|BlocksStorageMovementResult
argument_list|(
name|item
operator|.
name|longValue
argument_list|()
argument_list|,
name|BlocksStorageMovementResult
operator|.
name|Status
operator|.
name|FAILURE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|checkItemMovedForRetry
argument_list|(
name|item
argument_list|,
literal|200
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testAddResultWithSucessResult ()
specifier|public
name|void
name|testAddResultWithSucessResult
parameter_list|()
throws|throws
name|Exception
block|{
name|bsmAttemptedItems
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// start block movement result monitor thread
name|Long
name|item
init|=
operator|new
name|Long
argument_list|(
literal|1234
argument_list|)
decl_stmt|;
name|bsmAttemptedItems
operator|.
name|add
argument_list|(
name|item
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|bsmAttemptedItems
operator|.
name|addResults
argument_list|(
operator|new
name|BlocksStorageMovementResult
index|[]
block|{
operator|new
name|BlocksStorageMovementResult
argument_list|(
name|item
operator|.
name|longValue
argument_list|()
argument_list|,
name|BlocksStorageMovementResult
operator|.
name|Status
operator|.
name|SUCCESS
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|checkItemMovedForRetry
argument_list|(
name|item
argument_list|,
literal|200
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testNoResultAdded ()
specifier|public
name|void
name|testNoResultAdded
parameter_list|()
throws|throws
name|Exception
block|{
name|bsmAttemptedItems
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// start block movement result monitor thread
name|Long
name|item
init|=
operator|new
name|Long
argument_list|(
literal|1234
argument_list|)
decl_stmt|;
name|bsmAttemptedItems
operator|.
name|add
argument_list|(
name|item
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// After self retry timeout, it should be added back for retry
name|assertTrue
argument_list|(
literal|"Failed to add to the retry list"
argument_list|,
name|checkItemMovedForRetry
argument_list|(
name|item
argument_list|,
literal|600
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed to remove from the attempted list"
argument_list|,
literal|0
argument_list|,
name|bsmAttemptedItems
operator|.
name|getAttemptedItemsCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Partial block movement with BlocksStorageMovementResult#SUCCESS. Here,    * first occurrence is #blockStorageMovementResultCheck() and then    * #blocksStorageMovementUnReportedItemsCheck().    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testPartialBlockMovementShouldBeRetried1 ()
specifier|public
name|void
name|testPartialBlockMovementShouldBeRetried1
parameter_list|()
throws|throws
name|Exception
block|{
name|Long
name|item
init|=
operator|new
name|Long
argument_list|(
literal|1234
argument_list|)
decl_stmt|;
name|bsmAttemptedItems
operator|.
name|add
argument_list|(
name|item
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|bsmAttemptedItems
operator|.
name|addResults
argument_list|(
operator|new
name|BlocksStorageMovementResult
index|[]
block|{
operator|new
name|BlocksStorageMovementResult
argument_list|(
name|item
operator|.
name|longValue
argument_list|()
argument_list|,
name|BlocksStorageMovementResult
operator|.
name|Status
operator|.
name|SUCCESS
argument_list|)
block|}
argument_list|)
expr_stmt|;
comment|// start block movement result monitor thread
name|bsmAttemptedItems
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to add to the retry list"
argument_list|,
name|checkItemMovedForRetry
argument_list|(
name|item
argument_list|,
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed to remove from the attempted list"
argument_list|,
literal|0
argument_list|,
name|bsmAttemptedItems
operator|.
name|getAttemptedItemsCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Partial block movement with BlocksStorageMovementResult#SUCCESS. Here,    * first occurrence is #blocksStorageMovementUnReportedItemsCheck() and then    * #blockStorageMovementResultCheck().    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testPartialBlockMovementShouldBeRetried2 ()
specifier|public
name|void
name|testPartialBlockMovementShouldBeRetried2
parameter_list|()
throws|throws
name|Exception
block|{
name|Long
name|item
init|=
operator|new
name|Long
argument_list|(
literal|1234
argument_list|)
decl_stmt|;
name|bsmAttemptedItems
operator|.
name|add
argument_list|(
name|item
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|bsmAttemptedItems
operator|.
name|addResults
argument_list|(
operator|new
name|BlocksStorageMovementResult
index|[]
block|{
operator|new
name|BlocksStorageMovementResult
argument_list|(
name|item
operator|.
name|longValue
argument_list|()
argument_list|,
name|BlocksStorageMovementResult
operator|.
name|Status
operator|.
name|SUCCESS
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|selfRetryTimeout
operator|*
literal|2
argument_list|)
expr_stmt|;
comment|// Waiting to get timed out
name|bsmAttemptedItems
operator|.
name|blocksStorageMovementUnReportedItemsCheck
argument_list|()
expr_stmt|;
name|bsmAttemptedItems
operator|.
name|blockStorageMovementResultCheck
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to add to the retry list"
argument_list|,
name|checkItemMovedForRetry
argument_list|(
name|item
argument_list|,
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed to remove from the attempted list"
argument_list|,
literal|0
argument_list|,
name|bsmAttemptedItems
operator|.
name|getAttemptedItemsCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Partial block movement with only BlocksStorageMovementResult#FAILURE result    * and storageMovementAttemptedItems list is empty.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testPartialBlockMovementShouldBeRetried3 ()
specifier|public
name|void
name|testPartialBlockMovementShouldBeRetried3
parameter_list|()
throws|throws
name|Exception
block|{
name|Long
name|item
init|=
operator|new
name|Long
argument_list|(
literal|1234
argument_list|)
decl_stmt|;
name|bsmAttemptedItems
operator|.
name|addResults
argument_list|(
operator|new
name|BlocksStorageMovementResult
index|[]
block|{
operator|new
name|BlocksStorageMovementResult
argument_list|(
name|item
operator|.
name|longValue
argument_list|()
argument_list|,
name|BlocksStorageMovementResult
operator|.
name|Status
operator|.
name|FAILURE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|bsmAttemptedItems
operator|.
name|blockStorageMovementResultCheck
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to add to the retry list"
argument_list|,
name|checkItemMovedForRetry
argument_list|(
name|item
argument_list|,
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed to remove from the attempted list"
argument_list|,
literal|0
argument_list|,
name|bsmAttemptedItems
operator|.
name|getAttemptedItemsCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Partial block movement with BlocksStorageMovementResult#FAILURE result and    * storageMovementAttemptedItems.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testPartialBlockMovementShouldBeRetried4 ()
specifier|public
name|void
name|testPartialBlockMovementShouldBeRetried4
parameter_list|()
throws|throws
name|Exception
block|{
name|Long
name|item
init|=
operator|new
name|Long
argument_list|(
literal|1234
argument_list|)
decl_stmt|;
name|bsmAttemptedItems
operator|.
name|add
argument_list|(
name|item
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|bsmAttemptedItems
operator|.
name|addResults
argument_list|(
operator|new
name|BlocksStorageMovementResult
index|[]
block|{
operator|new
name|BlocksStorageMovementResult
argument_list|(
name|item
operator|.
name|longValue
argument_list|()
argument_list|,
name|BlocksStorageMovementResult
operator|.
name|Status
operator|.
name|FAILURE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|bsmAttemptedItems
operator|.
name|blockStorageMovementResultCheck
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to add to the retry list"
argument_list|,
name|checkItemMovedForRetry
argument_list|(
name|item
argument_list|,
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed to remove from the attempted list"
argument_list|,
literal|0
argument_list|,
name|bsmAttemptedItems
operator|.
name|getAttemptedItemsCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

