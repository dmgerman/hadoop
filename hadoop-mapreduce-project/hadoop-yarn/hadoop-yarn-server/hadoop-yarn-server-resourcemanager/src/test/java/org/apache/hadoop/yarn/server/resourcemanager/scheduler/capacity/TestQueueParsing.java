begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|capacity
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|CapacityScheduler
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
name|capacity
operator|.
name|CapacitySchedulerConfiguration
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
DECL|class|TestQueueParsing
specifier|public
class|class
name|TestQueueParsing
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
name|TestQueueParsing
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DELTA
specifier|private
specifier|static
specifier|final
name|double
name|DELTA
init|=
literal|0.000001
decl_stmt|;
annotation|@
name|Test
DECL|method|testQueueParsing ()
specifier|public
name|void
name|testQueueParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|CapacitySchedulerConfiguration
name|conf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|setupQueueConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|CapacityScheduler
name|capacityScheduler
init|=
operator|new
name|CapacityScheduler
argument_list|()
decl_stmt|;
name|capacityScheduler
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|CSQueue
name|a
init|=
name|capacityScheduler
operator|.
name|getQueue
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0.10
argument_list|,
name|a
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0.15
argument_list|,
name|a
operator|.
name|getAbsoluteMaximumCapacity
argument_list|()
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
name|CSQueue
name|b1
init|=
name|capacityScheduler
operator|.
name|getQueue
argument_list|(
literal|"b1"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0.2
operator|*
literal|0.5
argument_list|,
name|b1
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Parent B has no MAX_CAP"
argument_list|,
literal|0.85
argument_list|,
name|b1
operator|.
name|getAbsoluteMaximumCapacity
argument_list|()
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
name|CSQueue
name|c12
init|=
name|capacityScheduler
operator|.
name|getQueue
argument_list|(
literal|"c12"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0.7
operator|*
literal|0.5
operator|*
literal|0.45
argument_list|,
name|c12
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0.7
operator|*
literal|0.55
operator|*
literal|0.7
argument_list|,
name|c12
operator|.
name|getAbsoluteMaximumCapacity
argument_list|()
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
DECL|method|setupQueueConfiguration (CapacitySchedulerConfiguration conf)
specifier|private
name|void
name|setupQueueConfiguration
parameter_list|(
name|CapacitySchedulerConfiguration
name|conf
parameter_list|)
block|{
comment|// Define top-level queues
name|conf
operator|.
name|setQueues
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
literal|100
argument_list|)
expr_stmt|;
specifier|final
name|String
name|A
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|A
argument_list|,
literal|15
argument_list|)
expr_stmt|;
specifier|final
name|String
name|B
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".b"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B
argument_list|,
literal|20
argument_list|)
expr_stmt|;
specifier|final
name|String
name|C
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".c"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C
argument_list|,
literal|70
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|C
argument_list|,
literal|70
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setup top-level queues"
argument_list|)
expr_stmt|;
comment|// Define 2nd-level queues
specifier|final
name|String
name|A1
init|=
name|A
operator|+
literal|".a1"
decl_stmt|;
specifier|final
name|String
name|A2
init|=
name|A
operator|+
literal|".a2"
decl_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|A
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a1"
block|,
literal|"a2"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A1
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|A1
argument_list|,
literal|45
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A2
argument_list|,
literal|70
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|A2
argument_list|,
literal|85
argument_list|)
expr_stmt|;
specifier|final
name|String
name|B1
init|=
name|B
operator|+
literal|".b1"
decl_stmt|;
specifier|final
name|String
name|B2
init|=
name|B
operator|+
literal|".b2"
decl_stmt|;
specifier|final
name|String
name|B3
init|=
name|B
operator|+
literal|".b3"
decl_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|B
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"b1"
block|,
literal|"b2"
block|,
literal|"b3"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B1
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|B1
argument_list|,
literal|85
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B2
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|B2
argument_list|,
literal|35
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B3
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|B3
argument_list|,
literal|35
argument_list|)
expr_stmt|;
specifier|final
name|String
name|C1
init|=
name|C
operator|+
literal|".c1"
decl_stmt|;
specifier|final
name|String
name|C2
init|=
name|C
operator|+
literal|".c2"
decl_stmt|;
specifier|final
name|String
name|C3
init|=
name|C
operator|+
literal|".c3"
decl_stmt|;
specifier|final
name|String
name|C4
init|=
name|C
operator|+
literal|".c4"
decl_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|C
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c1"
block|,
literal|"c2"
block|,
literal|"c3"
block|,
literal|"c4"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C1
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|C1
argument_list|,
literal|55
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C2
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|C2
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C3
argument_list|,
literal|35
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|C3
argument_list|,
literal|38
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C4
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|C4
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setup 2nd-level queues"
argument_list|)
expr_stmt|;
comment|// Define 3rd-level queues
specifier|final
name|String
name|C11
init|=
name|C1
operator|+
literal|".c11"
decl_stmt|;
specifier|final
name|String
name|C12
init|=
name|C1
operator|+
literal|".c12"
decl_stmt|;
specifier|final
name|String
name|C13
init|=
name|C1
operator|+
literal|".c13"
decl_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|C1
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c11"
block|,
literal|"c12"
block|,
literal|"c13"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C11
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|C11
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C12
argument_list|,
literal|45
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|C12
argument_list|,
literal|70
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C13
argument_list|,
literal|40
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|C13
argument_list|,
literal|40
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setup 3rd-level queues"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testRootQueueParsing ()
specifier|public
name|void
name|testRootQueueParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|CapacitySchedulerConfiguration
name|conf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
comment|// non-100 percent value will throw IllegalArgumentException
name|conf
operator|.
name|setCapacity
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
literal|90
argument_list|)
expr_stmt|;
name|CapacityScheduler
name|capacityScheduler
init|=
operator|new
name|CapacityScheduler
argument_list|()
decl_stmt|;
name|capacityScheduler
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxCapacity ()
specifier|public
name|void
name|testMaxCapacity
parameter_list|()
throws|throws
name|Exception
block|{
name|CapacitySchedulerConfiguration
name|conf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
literal|100
argument_list|)
expr_stmt|;
specifier|final
name|String
name|A
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|A
argument_list|,
literal|60
argument_list|)
expr_stmt|;
specifier|final
name|String
name|B
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".b"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|B
argument_list|,
literal|45
argument_list|)
expr_stmt|;
comment|// Should throw an exception
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
name|CapacityScheduler
name|capacityScheduler
decl_stmt|;
try|try
block|{
name|capacityScheduler
operator|=
operator|new
name|CapacityScheduler
argument_list|()
expr_stmt|;
name|capacityScheduler
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|fail
operator|=
literal|true
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Didn't throw IllegalArgumentException for wrong maxCap"
argument_list|,
name|fail
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaximumCapacity
argument_list|(
name|B
argument_list|,
literal|60
argument_list|)
expr_stmt|;
comment|// Now this should work
name|capacityScheduler
operator|=
operator|new
name|CapacityScheduler
argument_list|()
expr_stmt|;
name|capacityScheduler
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|LeafQueue
name|a
init|=
operator|(
name|LeafQueue
operator|)
name|capacityScheduler
operator|.
name|getQueue
argument_list|(
name|A
argument_list|)
decl_stmt|;
name|a
operator|.
name|setMaxCapacity
argument_list|(
literal|45
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|fail
operator|=
literal|true
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Didn't throw IllegalArgumentException for wrong "
operator|+
literal|"setMaxCap"
argument_list|,
name|fail
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

