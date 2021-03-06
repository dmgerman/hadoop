begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*******************************************************************************  *   Licensed to the Apache Software Foundation (ASF) under one  *   or more contributor license agreements.  See the NOTICE file  *   distributed with this work for additional information  *   regarding copyright ownership.  The ASF licenses this file  *   to you under the Apache License, Version 2.0 (the  *   "License"); you may not use this file except in compliance  *   with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *   Unless required by applicable law or agreed to in writing, software  *   distributed under the License is distributed on an "AS IS" BASIS,  *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *   See the License for the specific language governing permissions and  *   limitations under the License.  *******************************************************************************/
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation
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
name|reservation
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|ReservationAllocationState
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
name|ReservationDefinition
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
name|ReservationId
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
name|Resource
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
name|impl
operator|.
name|pb
operator|.
name|ResourcePBImpl
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
name|ResourceCalculator
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|TestReservationSystemUtil
specifier|public
class|class
name|TestReservationSystemUtil
block|{
annotation|@
name|Test
DECL|method|testConvertAllocationsToReservationInfo ()
specifier|public
name|void
name|testConvertAllocationsToReservationInfo
parameter_list|()
block|{
name|long
name|startTime
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|long
name|step
init|=
literal|10000
decl_stmt|;
name|int
index|[]
name|alloc
init|=
block|{
literal|10
block|,
literal|10
block|,
literal|10
block|}
decl_stmt|;
name|ReservationId
name|id
init|=
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
decl_stmt|;
name|ReservationAllocation
name|allocation
init|=
name|createReservationAllocation
argument_list|(
name|startTime
argument_list|,
name|startTime
operator|+
literal|10
operator|*
name|step
argument_list|,
name|step
argument_list|,
name|alloc
argument_list|,
name|id
argument_list|,
name|createResource
argument_list|(
literal|4000
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ReservationAllocationState
argument_list|>
name|infoList
init|=
name|ReservationSystemUtil
operator|.
name|convertAllocationsToReservationInfo
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|allocation
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|infoList
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|infoList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getReservationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|infoList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResourceAllocationRequests
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertAllocationsToReservationInfoNoAllocations ()
specifier|public
name|void
name|testConvertAllocationsToReservationInfoNoAllocations
parameter_list|()
block|{
name|long
name|startTime
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|long
name|step
init|=
literal|10000
decl_stmt|;
name|int
index|[]
name|alloc
init|=
block|{
literal|10
block|,
literal|10
block|,
literal|10
block|}
decl_stmt|;
name|ReservationId
name|id
init|=
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
decl_stmt|;
name|ReservationAllocation
name|allocation
init|=
name|createReservationAllocation
argument_list|(
name|startTime
argument_list|,
name|startTime
operator|+
literal|10
operator|*
name|step
argument_list|,
name|step
argument_list|,
name|alloc
argument_list|,
name|id
argument_list|,
name|createResource
argument_list|(
literal|4000
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ReservationAllocationState
argument_list|>
name|infoList
init|=
name|ReservationSystemUtil
operator|.
name|convertAllocationsToReservationInfo
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|allocation
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|infoList
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|infoList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getReservationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|infoList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResourceAllocationRequests
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertAllocationsToReservationInfoEmptyAllocations ()
specifier|public
name|void
name|testConvertAllocationsToReservationInfoEmptyAllocations
parameter_list|()
block|{
name|long
name|startTime
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|long
name|step
init|=
literal|10000
decl_stmt|;
name|int
index|[]
name|alloc
init|=
block|{}
decl_stmt|;
name|ReservationId
name|id
init|=
name|ReservationSystemTestUtil
operator|.
name|getNewReservationId
argument_list|()
decl_stmt|;
name|ReservationAllocation
name|allocation
init|=
name|createReservationAllocation
argument_list|(
name|startTime
argument_list|,
name|startTime
operator|+
literal|10
operator|*
name|step
argument_list|,
name|step
argument_list|,
name|alloc
argument_list|,
name|id
argument_list|,
name|createResource
argument_list|(
literal|4000
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ReservationAllocationState
argument_list|>
name|infoList
init|=
name|ReservationSystemUtil
operator|.
name|convertAllocationsToReservationInfo
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|allocation
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|infoList
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|infoList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getReservationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|infoList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResourceAllocationRequests
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertAllocationsToReservationInfoEmptySet ()
specifier|public
name|void
name|testConvertAllocationsToReservationInfoEmptySet
parameter_list|()
block|{
name|List
argument_list|<
name|ReservationAllocationState
argument_list|>
name|infoList
init|=
name|ReservationSystemUtil
operator|.
name|convertAllocationsToReservationInfo
argument_list|(
name|Collections
operator|.
expr|<
name|ReservationAllocation
operator|>
name|emptySet
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|infoList
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
DECL|method|createReservationAllocation (long startTime, long deadline, long step, int[] alloc, ReservationId id, Resource minAlloc)
specifier|private
name|ReservationAllocation
name|createReservationAllocation
parameter_list|(
name|long
name|startTime
parameter_list|,
name|long
name|deadline
parameter_list|,
name|long
name|step
parameter_list|,
name|int
index|[]
name|alloc
parameter_list|,
name|ReservationId
name|id
parameter_list|,
name|Resource
name|minAlloc
parameter_list|)
block|{
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|allocations
init|=
name|ReservationSystemTestUtil
operator|.
name|generateAllocation
argument_list|(
name|startTime
argument_list|,
name|step
argument_list|,
name|alloc
argument_list|)
decl_stmt|;
name|ResourceCalculator
name|rs
init|=
name|mock
argument_list|(
name|ResourceCalculator
operator|.
name|class
argument_list|)
decl_stmt|;
name|ReservationDefinition
name|definition
init|=
name|ReservationSystemTestUtil
operator|.
name|createSimpleReservationDefinition
argument_list|(
name|startTime
argument_list|,
name|deadline
argument_list|,
name|step
argument_list|)
decl_stmt|;
return|return
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|id
argument_list|,
name|definition
argument_list|,
literal|"user"
argument_list|,
name|ReservationSystemTestUtil
operator|.
name|reservationQ
argument_list|,
name|startTime
argument_list|,
name|startTime
operator|+
name|step
argument_list|,
name|allocations
argument_list|,
name|rs
argument_list|,
name|minAlloc
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|createResource (int memory, int vCores)
specifier|public
name|Resource
name|createResource
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|vCores
parameter_list|)
block|{
name|Resource
name|resource
init|=
operator|new
name|ResourcePBImpl
argument_list|()
decl_stmt|;
name|resource
operator|.
name|setMemorySize
argument_list|(
name|memory
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setVirtualCores
argument_list|(
name|vCores
argument_list|)
expr_stmt|;
return|return
name|resource
return|;
block|}
block|}
end_class

end_unit

