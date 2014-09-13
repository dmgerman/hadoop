begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|HashMap
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
name|ReservationRequest
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
name|ReservationRequestInterpreter
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
name|ReservationRequests
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
name|ReservationDefinitionPBImpl
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
name|ReservationRequestsPBImpl
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
name|DefaultResourceCalculator
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
name|After
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

begin_class
DECL|class|TestInMemoryReservationAllocation
specifier|public
class|class
name|TestInMemoryReservationAllocation
block|{
DECL|field|user
specifier|private
name|String
name|user
init|=
literal|"yarn"
decl_stmt|;
DECL|field|planName
specifier|private
name|String
name|planName
init|=
literal|"test-reservation"
decl_stmt|;
DECL|field|resCalc
specifier|private
name|ResourceCalculator
name|resCalc
decl_stmt|;
DECL|field|minAlloc
specifier|private
name|Resource
name|minAlloc
decl_stmt|;
DECL|field|rand
specifier|private
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|resCalc
operator|=
operator|new
name|DefaultResourceCalculator
argument_list|()
expr_stmt|;
name|minAlloc
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|user
operator|=
literal|null
expr_stmt|;
name|planName
operator|=
literal|null
expr_stmt|;
name|resCalc
operator|=
literal|null
expr_stmt|;
name|minAlloc
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlocks ()
specifier|public
name|void
name|testBlocks
parameter_list|()
block|{
name|ReservationId
name|reservationID
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|,
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
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
block|,
literal|10
block|,
literal|10
block|,
literal|10
block|}
decl_stmt|;
name|int
name|start
init|=
literal|100
decl_stmt|;
name|ReservationDefinition
name|rDef
init|=
name|createSimpleReservationDefinition
argument_list|(
name|start
argument_list|,
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|alloc
operator|.
name|length
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|ReservationRequest
argument_list|>
name|allocations
init|=
name|generateAllocation
argument_list|(
name|start
argument_list|,
name|alloc
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ReservationAllocation
name|rAllocation
init|=
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|reservationID
argument_list|,
name|rDef
argument_list|,
name|user
argument_list|,
name|planName
argument_list|,
name|start
argument_list|,
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|allocations
argument_list|,
name|resCalc
argument_list|,
name|minAlloc
argument_list|)
decl_stmt|;
name|doAssertions
argument_list|(
name|rAllocation
argument_list|,
name|reservationID
argument_list|,
name|rDef
argument_list|,
name|allocations
argument_list|,
name|start
argument_list|,
name|alloc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|rAllocation
operator|.
name|containsGangs
argument_list|()
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
name|alloc
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
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
operator|*
operator|(
name|alloc
index|[
name|i
index|]
operator|)
argument_list|,
operator|(
name|alloc
index|[
name|i
index|]
operator|)
argument_list|)
argument_list|,
name|rAllocation
operator|.
name|getResourcesAtTime
argument_list|(
name|start
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSteps ()
specifier|public
name|void
name|testSteps
parameter_list|()
block|{
name|ReservationId
name|reservationID
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|,
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
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
block|,
literal|10
block|,
literal|10
block|,
literal|10
block|}
decl_stmt|;
name|int
name|start
init|=
literal|100
decl_stmt|;
name|ReservationDefinition
name|rDef
init|=
name|createSimpleReservationDefinition
argument_list|(
name|start
argument_list|,
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|alloc
operator|.
name|length
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|ReservationRequest
argument_list|>
name|allocations
init|=
name|generateAllocation
argument_list|(
name|start
argument_list|,
name|alloc
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ReservationAllocation
name|rAllocation
init|=
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|reservationID
argument_list|,
name|rDef
argument_list|,
name|user
argument_list|,
name|planName
argument_list|,
name|start
argument_list|,
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|allocations
argument_list|,
name|resCalc
argument_list|,
name|minAlloc
argument_list|)
decl_stmt|;
name|doAssertions
argument_list|(
name|rAllocation
argument_list|,
name|reservationID
argument_list|,
name|rDef
argument_list|,
name|allocations
argument_list|,
name|start
argument_list|,
name|alloc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|rAllocation
operator|.
name|containsGangs
argument_list|()
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
name|alloc
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
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
operator|*
operator|(
name|alloc
index|[
name|i
index|]
operator|+
name|i
operator|)
argument_list|,
operator|(
name|alloc
index|[
name|i
index|]
operator|+
name|i
operator|)
argument_list|)
argument_list|,
name|rAllocation
operator|.
name|getResourcesAtTime
argument_list|(
name|start
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSkyline ()
specifier|public
name|void
name|testSkyline
parameter_list|()
block|{
name|ReservationId
name|reservationID
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|,
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|int
index|[]
name|alloc
init|=
block|{
literal|0
block|,
literal|5
block|,
literal|10
block|,
literal|10
block|,
literal|5
block|,
literal|0
block|}
decl_stmt|;
name|int
name|start
init|=
literal|100
decl_stmt|;
name|ReservationDefinition
name|rDef
init|=
name|createSimpleReservationDefinition
argument_list|(
name|start
argument_list|,
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|alloc
operator|.
name|length
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|ReservationRequest
argument_list|>
name|allocations
init|=
name|generateAllocation
argument_list|(
name|start
argument_list|,
name|alloc
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ReservationAllocation
name|rAllocation
init|=
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|reservationID
argument_list|,
name|rDef
argument_list|,
name|user
argument_list|,
name|planName
argument_list|,
name|start
argument_list|,
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|allocations
argument_list|,
name|resCalc
argument_list|,
name|minAlloc
argument_list|)
decl_stmt|;
name|doAssertions
argument_list|(
name|rAllocation
argument_list|,
name|reservationID
argument_list|,
name|rDef
argument_list|,
name|allocations
argument_list|,
name|start
argument_list|,
name|alloc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|rAllocation
operator|.
name|containsGangs
argument_list|()
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
name|alloc
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
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
operator|*
operator|(
name|alloc
index|[
name|i
index|]
operator|+
name|i
operator|)
argument_list|,
operator|(
name|alloc
index|[
name|i
index|]
operator|+
name|i
operator|)
argument_list|)
argument_list|,
name|rAllocation
operator|.
name|getResourcesAtTime
argument_list|(
name|start
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testZeroAlloaction ()
specifier|public
name|void
name|testZeroAlloaction
parameter_list|()
block|{
name|ReservationId
name|reservationID
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|,
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|int
index|[]
name|alloc
init|=
block|{}
decl_stmt|;
name|long
name|start
init|=
literal|0
decl_stmt|;
name|ReservationDefinition
name|rDef
init|=
name|createSimpleReservationDefinition
argument_list|(
name|start
argument_list|,
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|alloc
operator|.
name|length
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|ReservationRequest
argument_list|>
name|allocations
init|=
operator|new
name|HashMap
argument_list|<
name|ReservationInterval
argument_list|,
name|ReservationRequest
argument_list|>
argument_list|()
decl_stmt|;
name|ReservationAllocation
name|rAllocation
init|=
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|reservationID
argument_list|,
name|rDef
argument_list|,
name|user
argument_list|,
name|planName
argument_list|,
name|start
argument_list|,
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|allocations
argument_list|,
name|resCalc
argument_list|,
name|minAlloc
argument_list|)
decl_stmt|;
name|doAssertions
argument_list|(
name|rAllocation
argument_list|,
name|reservationID
argument_list|,
name|rDef
argument_list|,
name|allocations
argument_list|,
operator|(
name|int
operator|)
name|start
argument_list|,
name|alloc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|rAllocation
operator|.
name|containsGangs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGangAlloaction ()
specifier|public
name|void
name|testGangAlloaction
parameter_list|()
block|{
name|ReservationId
name|reservationID
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|,
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
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
block|,
literal|10
block|,
literal|10
block|,
literal|10
block|}
decl_stmt|;
name|int
name|start
init|=
literal|100
decl_stmt|;
name|ReservationDefinition
name|rDef
init|=
name|createSimpleReservationDefinition
argument_list|(
name|start
argument_list|,
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|alloc
operator|.
name|length
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|ReservationRequest
argument_list|>
name|allocations
init|=
name|generateAllocation
argument_list|(
name|start
argument_list|,
name|alloc
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ReservationAllocation
name|rAllocation
init|=
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|reservationID
argument_list|,
name|rDef
argument_list|,
name|user
argument_list|,
name|planName
argument_list|,
name|start
argument_list|,
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|allocations
argument_list|,
name|resCalc
argument_list|,
name|minAlloc
argument_list|)
decl_stmt|;
name|doAssertions
argument_list|(
name|rAllocation
argument_list|,
name|reservationID
argument_list|,
name|rDef
argument_list|,
name|allocations
argument_list|,
name|start
argument_list|,
name|alloc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rAllocation
operator|.
name|containsGangs
argument_list|()
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
name|alloc
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
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
operator|*
operator|(
name|alloc
index|[
name|i
index|]
operator|)
argument_list|,
operator|(
name|alloc
index|[
name|i
index|]
operator|)
argument_list|)
argument_list|,
name|rAllocation
operator|.
name|getResourcesAtTime
argument_list|(
name|start
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doAssertions (ReservationAllocation rAllocation, ReservationId reservationID, ReservationDefinition rDef, Map<ReservationInterval, ReservationRequest> allocations, int start, int[] alloc)
specifier|private
name|void
name|doAssertions
parameter_list|(
name|ReservationAllocation
name|rAllocation
parameter_list|,
name|ReservationId
name|reservationID
parameter_list|,
name|ReservationDefinition
name|rDef
parameter_list|,
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|ReservationRequest
argument_list|>
name|allocations
parameter_list|,
name|int
name|start
parameter_list|,
name|int
index|[]
name|alloc
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|reservationID
argument_list|,
name|rAllocation
operator|.
name|getReservationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|rDef
argument_list|,
name|rAllocation
operator|.
name|getReservationDefinition
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|allocations
argument_list|,
name|rAllocation
operator|.
name|getAllocationRequests
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|user
argument_list|,
name|rAllocation
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|planName
argument_list|,
name|rAllocation
operator|.
name|getPlanName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|start
argument_list|,
name|rAllocation
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|start
operator|+
name|alloc
operator|.
name|length
operator|+
literal|1
argument_list|,
name|rAllocation
operator|.
name|getEndTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createSimpleReservationDefinition (long arrival, long deadline, long duration)
specifier|private
name|ReservationDefinition
name|createSimpleReservationDefinition
parameter_list|(
name|long
name|arrival
parameter_list|,
name|long
name|deadline
parameter_list|,
name|long
name|duration
parameter_list|)
block|{
comment|// create a request with a single atomic ask
name|ReservationRequest
name|r
init|=
name|ReservationRequest
operator|.
name|newInstance
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|duration
argument_list|)
decl_stmt|;
name|ReservationDefinition
name|rDef
init|=
operator|new
name|ReservationDefinitionPBImpl
argument_list|()
decl_stmt|;
name|ReservationRequests
name|reqs
init|=
operator|new
name|ReservationRequestsPBImpl
argument_list|()
decl_stmt|;
name|reqs
operator|.
name|setReservationResources
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|reqs
operator|.
name|setInterpreter
argument_list|(
name|ReservationRequestInterpreter
operator|.
name|R_ALL
argument_list|)
expr_stmt|;
name|rDef
operator|.
name|setReservationRequests
argument_list|(
name|reqs
argument_list|)
expr_stmt|;
name|rDef
operator|.
name|setArrival
argument_list|(
name|arrival
argument_list|)
expr_stmt|;
name|rDef
operator|.
name|setDeadline
argument_list|(
name|deadline
argument_list|)
expr_stmt|;
return|return
name|rDef
return|;
block|}
DECL|method|generateAllocation ( int startTime, int[] alloc, boolean isStep, boolean isGang)
specifier|private
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|ReservationRequest
argument_list|>
name|generateAllocation
parameter_list|(
name|int
name|startTime
parameter_list|,
name|int
index|[]
name|alloc
parameter_list|,
name|boolean
name|isStep
parameter_list|,
name|boolean
name|isGang
parameter_list|)
block|{
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|ReservationRequest
argument_list|>
name|req
init|=
operator|new
name|HashMap
argument_list|<
name|ReservationInterval
argument_list|,
name|ReservationRequest
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|numContainers
init|=
literal|0
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
name|alloc
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isStep
condition|)
block|{
name|numContainers
operator|=
name|alloc
index|[
name|i
index|]
operator|+
name|i
expr_stmt|;
block|}
else|else
block|{
name|numContainers
operator|=
name|alloc
index|[
name|i
index|]
expr_stmt|;
block|}
name|ReservationRequest
name|rr
init|=
name|ReservationRequest
operator|.
name|newInstance
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|(
name|numContainers
operator|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isGang
condition|)
block|{
name|rr
operator|.
name|setConcurrency
argument_list|(
name|numContainers
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|put
argument_list|(
operator|new
name|ReservationInterval
argument_list|(
name|startTime
operator|+
name|i
argument_list|,
name|startTime
operator|+
name|i
operator|+
literal|1
argument_list|)
argument_list|,
name|rr
argument_list|)
expr_stmt|;
block|}
return|return
name|req
return|;
block|}
block|}
end_class

end_unit

