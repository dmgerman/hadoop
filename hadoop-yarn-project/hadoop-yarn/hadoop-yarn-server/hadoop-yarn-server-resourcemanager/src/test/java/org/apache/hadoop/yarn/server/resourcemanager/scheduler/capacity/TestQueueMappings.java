begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|RMContext
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
name|placement
operator|.
name|UserGroupMappingPlacementRule
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
name|placement
operator|.
name|UserGroupMappingPlacementRule
operator|.
name|QueueMapping
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
name|placement
operator|.
name|UserGroupMappingPlacementRule
operator|.
name|QueueMapping
operator|.
name|MappingType
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
DECL|class|TestQueueMappings
specifier|public
class|class
name|TestQueueMappings
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestQueueMappings
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|Q1
specifier|private
specifier|static
specifier|final
name|String
name|Q1
init|=
literal|"q1"
decl_stmt|;
DECL|field|Q2
specifier|private
specifier|static
specifier|final
name|String
name|Q2
init|=
literal|"q2"
decl_stmt|;
DECL|field|Q1_PATH
specifier|private
specifier|final
specifier|static
name|String
name|Q1_PATH
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|Q1
decl_stmt|;
DECL|field|Q2_PATH
specifier|private
specifier|final
specifier|static
name|String
name|Q2_PATH
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|Q2
decl_stmt|;
DECL|field|cs
specifier|private
name|CapacityScheduler
name|cs
decl_stmt|;
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|setupQueueConfiguration
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|cs
operator|=
operator|new
name|CapacityScheduler
argument_list|()
expr_stmt|;
name|RMContext
name|rmContext
init|=
name|TestUtils
operator|.
name|getMockRMContext
argument_list|()
decl_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cs
operator|.
name|setRMContext
argument_list|(
name|rmContext
argument_list|)
expr_stmt|;
name|cs
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cs
operator|.
name|start
argument_list|()
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
name|Q1
block|,
name|Q2
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|Q1_PATH
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|Q2_PATH
argument_list|,
literal|90
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setup top-level queues q1 and q2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueueMappingSpecifyingNotExistedQueue ()
specifier|public
name|void
name|testQueueMappingSpecifyingNotExistedQueue
parameter_list|()
block|{
comment|// if the mapping specifies a queue that does not exist, reinitialize will
comment|// be failed
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|QUEUE_MAPPING
argument_list|,
literal|"u:user:non_existent_queue"
argument_list|)
expr_stmt|;
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
try|try
block|{
name|cs
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioex
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
literal|"queue initialization failed for non-existent q"
argument_list|,
name|fail
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueueMappingTrimSpaces ()
specifier|public
name|void
name|testQueueMappingTrimSpaces
parameter_list|()
throws|throws
name|IOException
block|{
comment|// space trimming
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|QUEUE_MAPPING
argument_list|,
literal|"    u : a : "
operator|+
name|Q1
argument_list|)
expr_stmt|;
name|cs
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkQMapping
argument_list|(
operator|new
name|QueueMapping
argument_list|(
name|MappingType
operator|.
name|USER
argument_list|,
literal|"a"
argument_list|,
name|Q1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testQueueMappingParsingInvalidCases ()
specifier|public
name|void
name|testQueueMappingParsingInvalidCases
parameter_list|()
throws|throws
name|Exception
block|{
comment|// configuration parsing tests - negative test cases
name|checkInvalidQMapping
argument_list|(
name|conf
argument_list|,
name|cs
argument_list|,
literal|"x:a:b"
argument_list|,
literal|"invalid specifier"
argument_list|)
expr_stmt|;
name|checkInvalidQMapping
argument_list|(
name|conf
argument_list|,
name|cs
argument_list|,
literal|"u:a"
argument_list|,
literal|"no queue specified"
argument_list|)
expr_stmt|;
name|checkInvalidQMapping
argument_list|(
name|conf
argument_list|,
name|cs
argument_list|,
literal|"g:a"
argument_list|,
literal|"no queue specified"
argument_list|)
expr_stmt|;
name|checkInvalidQMapping
argument_list|(
name|conf
argument_list|,
name|cs
argument_list|,
literal|"u:a:b,g:a"
argument_list|,
literal|"multiple mappings with invalid mapping"
argument_list|)
expr_stmt|;
name|checkInvalidQMapping
argument_list|(
name|conf
argument_list|,
name|cs
argument_list|,
literal|"u:a:b,g:a:d:e"
argument_list|,
literal|"too many path segments"
argument_list|)
expr_stmt|;
name|checkInvalidQMapping
argument_list|(
name|conf
argument_list|,
name|cs
argument_list|,
literal|"u::"
argument_list|,
literal|"empty source and queue"
argument_list|)
expr_stmt|;
name|checkInvalidQMapping
argument_list|(
name|conf
argument_list|,
name|cs
argument_list|,
literal|"u:"
argument_list|,
literal|"missing source missing queue"
argument_list|)
expr_stmt|;
name|checkInvalidQMapping
argument_list|(
name|conf
argument_list|,
name|cs
argument_list|,
literal|"u:a:"
argument_list|,
literal|"empty source missing q"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkInvalidQMapping (YarnConfiguration conf, CapacityScheduler cs, String mapping, String reason)
specifier|private
name|void
name|checkInvalidQMapping
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|,
name|CapacityScheduler
name|cs
parameter_list|,
name|String
name|mapping
parameter_list|,
name|String
name|reason
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
try|try
block|{
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|QUEUE_MAPPING
argument_list|,
name|mapping
argument_list|)
expr_stmt|;
name|cs
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
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
literal|"invalid mapping did not throw exception for "
operator|+
name|reason
argument_list|,
name|fail
argument_list|)
expr_stmt|;
block|}
DECL|method|checkQMapping (QueueMapping expected)
specifier|private
name|void
name|checkQMapping
parameter_list|(
name|QueueMapping
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupMappingPlacementRule
name|rule
init|=
operator|(
name|UserGroupMappingPlacementRule
operator|)
name|cs
operator|.
name|getRMContext
argument_list|()
operator|.
name|getQueuePlacementManager
argument_list|()
operator|.
name|getPlacementRules
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|QueueMapping
name|queueMapping
init|=
name|rule
operator|.
name|getQueueMappings
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|queueMapping
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

