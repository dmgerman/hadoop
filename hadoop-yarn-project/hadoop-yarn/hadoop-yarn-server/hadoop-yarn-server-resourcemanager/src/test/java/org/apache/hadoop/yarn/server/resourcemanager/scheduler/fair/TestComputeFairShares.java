begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
package|;
end_package

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
name|List
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ResourceInformation
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|resource
operator|.
name|Resources
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|policies
operator|.
name|ComputeFairShares
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

begin_comment
comment|/**  * Exercise the computeFairShares method in SchedulingAlgorithms.  */
end_comment

begin_class
DECL|class|TestComputeFairShares
specifier|public
class|class
name|TestComputeFairShares
block|{
DECL|field|scheds
specifier|private
name|List
argument_list|<
name|Schedulable
argument_list|>
name|scheds
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|scheds
operator|=
operator|new
name|ArrayList
argument_list|<
name|Schedulable
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**     * Basic test - pools with different demands that are all higher than their    * fair share (of 10 slots) should each get their fair share.    */
annotation|@
name|Test
DECL|method|testEqualSharing ()
specifier|public
name|void
name|testEqualSharing
parameter_list|()
block|{
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|()
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|()
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|()
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|()
argument_list|)
expr_stmt|;
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|scheds
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|40
argument_list|)
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verifyMemoryShares
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**    * In this test, pool 4 has a smaller demand than the 40 / 4 = 10 slots that    * it would be assigned with equal sharing. It should only get the 3 slots    * it demands. The other pools must then split the remaining 37 slots, but    * pool 3, with 11 slots demanded, is now below its share of 37/3 ~= 12.3,    * so it only gets 11 slots. Pools 1 and 2 split the rest and get 13 each.     */
annotation|@
name|Test
DECL|method|testLowMaxShares ()
specifier|public
name|void
name|testLowMaxShares
parameter_list|()
block|{
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|11
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|scheds
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|40
argument_list|)
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verifyMemoryShares
argument_list|(
literal|13
argument_list|,
literal|13
argument_list|,
literal|11
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
comment|/**    * In this test, some pools have minimum shares set. Pool 1 has a min share    * of 20 so it gets 20 slots. Pool 2 also has a min share of 20, but its    * demand is only 10 so it can only get 10 slots. The remaining pools have    * 10 slots to split between them. Pool 4 gets 3 slots because its demand is    * only 3, and pool 3 gets the remaining 7 slots. Pool 4 also had a min share    * of 2 slots but this should not affect the outcome.    */
annotation|@
name|Test
DECL|method|testMinShares ()
specifier|public
name|void
name|testMinShares
parameter_list|()
block|{
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|18
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|scheds
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|40
argument_list|)
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verifyMemoryShares
argument_list|(
literal|20
argument_list|,
literal|18
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Basic test for weighted shares with no minimum shares and no low demands.    * Each pool should get slots in proportion to its weight.    */
annotation|@
name|Test
DECL|method|testWeightedSharing ()
specifier|public
name|void
name|testWeightedSharing
parameter_list|()
block|{
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|0.5f
argument_list|)
argument_list|)
expr_stmt|;
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|scheds
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|45
argument_list|)
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verifyMemoryShares
argument_list|(
literal|20
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
comment|/**    * Weighted sharing test where pools 1 and 2 are now given lower demands than    * above. Pool 1 stops at 10 slots, leaving 35. If the remaining pools split    * this into a 1:1:0.5 ratio, they would get 14:14:7 slots respectively, but    * pool 2's demand is only 11, so it only gets 11. The remaining 2 pools split    * the 24 slots left into a 1:0.5 ratio, getting 16 and 8 slots respectively.    */
annotation|@
name|Test
DECL|method|testWeightedSharingWithMaxShares ()
specifier|public
name|void
name|testWeightedSharingWithMaxShares
parameter_list|()
block|{
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|,
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|11
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|30
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|,
literal|0.5f
argument_list|)
argument_list|)
expr_stmt|;
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|scheds
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|45
argument_list|)
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verifyMemoryShares
argument_list|(
literal|10
argument_list|,
literal|11
argument_list|,
literal|16
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
comment|/**    * Weighted fair sharing test with min shares. As in the min share test above,    * pool 1 has a min share greater than its demand so it only gets its demand.    * Pool 3 has a min share of 15 even though its weight is very small, so it    * gets 15 slots. The remaining pools share the remaining 20 slots equally,    * getting 10 each. Pool 3's min share of 5 slots doesn't affect this.    */
annotation|@
name|Test
DECL|method|testWeightedSharingWithMinShares ()
specifier|public
name|void
name|testWeightedSharingWithMinShares
parameter_list|()
block|{
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|20
argument_list|,
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|0
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|5
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
literal|15
argument_list|,
literal|0.5f
argument_list|)
argument_list|)
expr_stmt|;
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|scheds
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|45
argument_list|)
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verifyMemoryShares
argument_list|(
literal|20
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|,
literal|15
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that shares are computed accurately even when the number of slots is    * very large.    */
annotation|@
name|Test
DECL|method|testLargeShares ()
specifier|public
name|void
name|testLargeShares
parameter_list|()
block|{
name|int
name|million
init|=
literal|1000
operator|*
literal|1000
decl_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|()
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|()
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|()
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|()
argument_list|)
expr_stmt|;
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|scheds
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|40
operator|*
name|million
argument_list|)
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verifyMemoryShares
argument_list|(
literal|10
operator|*
name|million
argument_list|,
literal|10
operator|*
name|million
argument_list|,
literal|10
operator|*
name|million
argument_list|,
literal|10
operator|*
name|million
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that being called on an empty list doesn't confuse the algorithm.    */
annotation|@
name|Test
DECL|method|testEmptyList ()
specifier|public
name|void
name|testEmptyList
parameter_list|()
block|{
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|scheds
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|40
argument_list|)
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verifyMemoryShares
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that CPU works as well as memory    */
annotation|@
name|Test
DECL|method|testCPU ()
specifier|public
name|void
name|testCPU
parameter_list|()
block|{
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
argument_list|,
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|scheds
operator|.
name|add
argument_list|(
operator|new
name|FakeSchedulable
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|,
literal|15
argument_list|)
argument_list|,
literal|0.5f
argument_list|)
argument_list|)
expr_stmt|;
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|scheds
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|,
literal|45
argument_list|)
argument_list|,
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verifyCPUShares
argument_list|(
literal|20
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|,
literal|15
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that a given list of shares have been assigned to this.scheds.    */
DECL|method|verifyMemoryShares (int... shares)
specifier|private
name|void
name|verifyMemoryShares
parameter_list|(
name|int
modifier|...
name|shares
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|scheds
operator|.
name|size
argument_list|()
argument_list|,
name|shares
operator|.
name|length
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
name|shares
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|shares
index|[
name|i
index|]
argument_list|,
name|scheds
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Check that a given list of shares have been assigned to this.scheds.    */
DECL|method|verifyCPUShares (int... shares)
specifier|private
name|void
name|verifyCPUShares
parameter_list|(
name|int
modifier|...
name|shares
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|scheds
operator|.
name|size
argument_list|()
argument_list|,
name|shares
operator|.
name|length
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
name|shares
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|shares
index|[
name|i
index|]
argument_list|,
name|scheds
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

