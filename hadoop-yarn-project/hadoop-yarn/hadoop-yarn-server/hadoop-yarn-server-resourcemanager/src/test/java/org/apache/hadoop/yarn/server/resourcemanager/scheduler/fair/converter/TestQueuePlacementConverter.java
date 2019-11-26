begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.converter
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
operator|.
name|converter
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
operator|.
name|ENABLE_QUEUE_MAPPING_OVERRIDE
import|;
end_import

begin_import
import|import static
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
operator|.
name|QUEUE_MAPPING
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
name|assertNotNull
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
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
name|verifyZeroInteractions
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
name|when
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
name|ApplicationSubmissionContext
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
name|exceptions
operator|.
name|YarnException
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
name|ApplicationPlacementContext
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
name|DefaultPlacementRule
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
name|FSPlacementRule
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
name|PlacementManager
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
name|PlacementRule
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
name|PrimaryGroupPlacementRule
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
name|RejectPlacementRule
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
name|SecondaryGroupExistingPlacementRule
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
name|SpecifiedPlacementRule
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
name|UserPlacementRule
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|junit
operator|.
name|MockitoJUnitRunner
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
name|Lists
import|;
end_import

begin_comment
comment|/**  * Unit tests for QueuePlacementConverter.  *  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|MockitoJUnitRunner
operator|.
name|class
argument_list|)
DECL|class|TestQueuePlacementConverter
specifier|public
class|class
name|TestQueuePlacementConverter
block|{
annotation|@
name|Mock
DECL|field|placementManager
specifier|private
name|PlacementManager
name|placementManager
decl_stmt|;
annotation|@
name|Mock
DECL|field|ruleHandler
specifier|private
name|FSConfigToCSConfigRuleHandler
name|ruleHandler
decl_stmt|;
DECL|field|converter
specifier|private
name|QueuePlacementConverter
name|converter
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|this
operator|.
name|converter
operator|=
operator|new
name|QueuePlacementConverter
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertUserAsDefaultQueue ()
specifier|public
name|void
name|testConvertUserAsDefaultQueue
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|verifyMapping
argument_list|(
name|properties
argument_list|,
literal|"u:%user:%user"
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|ruleHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertUserPlacementRuleWithoutUserAsDefaultQueue ()
specifier|public
name|void
name|testConvertUserPlacementRuleWithoutUserAsDefaultQueue
parameter_list|()
block|{
name|testConvertUserPlacementRule
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertUserPlacementRuleWithUserAsDefaultQueue ()
specifier|public
name|void
name|testConvertUserPlacementRuleWithUserAsDefaultQueue
parameter_list|()
block|{
name|testConvertUserPlacementRule
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testConvertUserPlacementRule (boolean userAsDefaultQueue)
specifier|private
name|void
name|testConvertUserPlacementRule
parameter_list|(
name|boolean
name|userAsDefaultQueue
parameter_list|)
block|{
name|PlacementRule
name|rule
init|=
name|mock
argument_list|(
name|UserPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
name|userAsDefaultQueue
argument_list|)
decl_stmt|;
name|verifyMapping
argument_list|(
name|properties
argument_list|,
literal|"u:%user:%user"
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|ruleHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertSpecifiedPlacementRule ()
specifier|public
name|void
name|testConvertSpecifiedPlacementRule
parameter_list|()
block|{
name|PlacementRule
name|rule
init|=
name|mock
argument_list|(
name|SpecifiedPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|verifyMappingNoOverride
argument_list|(
name|properties
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|ruleHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertSpecifiedPlacementRuleAtSecondPlace ()
specifier|public
name|void
name|testConvertSpecifiedPlacementRuleAtSecondPlace
parameter_list|()
block|{
name|PlacementRule
name|rule
init|=
name|mock
argument_list|(
name|UserPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|PlacementRule
name|rule2
init|=
name|mock
argument_list|(
name|SpecifiedPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|,
name|rule2
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|verifyMappingNoOverride
argument_list|(
name|properties
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|ruleHandler
argument_list|)
operator|.
name|handleSpecifiedNotFirstRule
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertPrimaryGroupPlacementRule ()
specifier|public
name|void
name|testConvertPrimaryGroupPlacementRule
parameter_list|()
block|{
name|PlacementRule
name|rule
init|=
name|mock
argument_list|(
name|PrimaryGroupPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|verifyMapping
argument_list|(
name|properties
argument_list|,
literal|"u:%user:%primary_group"
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|ruleHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertSecondaryGroupPlacementRule ()
specifier|public
name|void
name|testConvertSecondaryGroupPlacementRule
parameter_list|()
block|{
name|PlacementRule
name|rule
init|=
name|mock
argument_list|(
name|SecondaryGroupExistingPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|verifyMapping
argument_list|(
name|properties
argument_list|,
literal|"u:%user:%secondary_group"
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|ruleHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertDefaultPlacementRule ()
specifier|public
name|void
name|testConvertDefaultPlacementRule
parameter_list|()
block|{
name|DefaultPlacementRule
name|rule
init|=
name|mock
argument_list|(
name|DefaultPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|rule
operator|.
name|defaultQueueName
operator|=
literal|"abc"
expr_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|verifyMapping
argument_list|(
name|properties
argument_list|,
literal|"u:%user:abc"
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|ruleHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testConvertUnsupportedPlacementRule ()
specifier|public
name|void
name|testConvertUnsupportedPlacementRule
parameter_list|()
block|{
name|PlacementRule
name|rule
init|=
name|mock
argument_list|(
name|TestPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|)
expr_stmt|;
comment|// throws exception
name|convert
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertRejectPlacementRule ()
specifier|public
name|void
name|testConvertRejectPlacementRule
parameter_list|()
block|{
name|PlacementRule
name|rule
init|=
name|mock
argument_list|(
name|RejectPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Map is not empty"
argument_list|,
literal|0
argument_list|,
name|properties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertNestedPrimaryGroupRule ()
specifier|public
name|void
name|testConvertNestedPrimaryGroupRule
parameter_list|()
block|{
name|UserPlacementRule
name|rule
init|=
name|mock
argument_list|(
name|UserPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|PrimaryGroupPlacementRule
name|parent
init|=
name|mock
argument_list|(
name|PrimaryGroupPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rule
operator|.
name|getParentRule
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|verifyMapping
argument_list|(
name|properties
argument_list|,
literal|"u:%user:%primary_group.%user"
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|ruleHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertNestedSecondaryGroupRule ()
specifier|public
name|void
name|testConvertNestedSecondaryGroupRule
parameter_list|()
block|{
name|UserPlacementRule
name|rule
init|=
name|mock
argument_list|(
name|UserPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|SecondaryGroupExistingPlacementRule
name|parent
init|=
name|mock
argument_list|(
name|SecondaryGroupExistingPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rule
operator|.
name|getParentRule
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|verifyMapping
argument_list|(
name|properties
argument_list|,
literal|"u:%user:%secondary_group.%user"
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|ruleHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertNestedDefaultRule ()
specifier|public
name|void
name|testConvertNestedDefaultRule
parameter_list|()
block|{
name|UserPlacementRule
name|rule
init|=
name|mock
argument_list|(
name|UserPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|DefaultPlacementRule
name|parent
init|=
name|mock
argument_list|(
name|DefaultPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|parent
operator|.
name|defaultQueueName
operator|=
literal|"abc"
expr_stmt|;
name|when
argument_list|(
name|rule
operator|.
name|getParentRule
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|verifyMapping
argument_list|(
name|properties
argument_list|,
literal|"u:%user:abc.%user"
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|ruleHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertMultiplePlacementRules ()
specifier|public
name|void
name|testConvertMultiplePlacementRules
parameter_list|()
block|{
name|UserPlacementRule
name|rule1
init|=
name|mock
argument_list|(
name|UserPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|PrimaryGroupPlacementRule
name|rule2
init|=
name|mock
argument_list|(
name|PrimaryGroupPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|SecondaryGroupExistingPlacementRule
name|rule3
init|=
name|mock
argument_list|(
name|SecondaryGroupExistingPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
name|initPlacementManagerMock
argument_list|(
name|rule1
argument_list|,
name|rule2
argument_list|,
name|rule3
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
name|convert
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|verifyMapping
argument_list|(
name|properties
argument_list|,
literal|"u:%user:%user;u:%user:%primary_group;u:%user:%secondary_group"
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|ruleHandler
argument_list|)
expr_stmt|;
block|}
DECL|method|initPlacementManagerMock ( PlacementRule... rules)
specifier|private
name|void
name|initPlacementManagerMock
parameter_list|(
name|PlacementRule
modifier|...
name|rules
parameter_list|)
block|{
name|List
argument_list|<
name|PlacementRule
argument_list|>
name|listOfRules
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|rules
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|placementManager
operator|.
name|getPlacementRules
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|listOfRules
argument_list|)
expr_stmt|;
block|}
DECL|method|convert (boolean userAsDefaultQueue)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|convert
parameter_list|(
name|boolean
name|userAsDefaultQueue
parameter_list|)
block|{
return|return
name|converter
operator|.
name|convertPlacementPolicy
argument_list|(
name|placementManager
argument_list|,
name|ruleHandler
argument_list|,
name|userAsDefaultQueue
argument_list|)
return|;
block|}
DECL|method|verifyMapping (Map<String, String> properties, String expectedValue)
specifier|private
name|void
name|verifyMapping
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|,
name|String
name|expectedValue
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Map size"
argument_list|,
literal|1
argument_list|,
name|properties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|properties
operator|.
name|get
argument_list|(
name|QUEUE_MAPPING
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No mapping property found"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Mapping"
argument_list|,
name|expectedValue
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyMappingNoOverride (Map<String, String> properties, int expectedSize)
specifier|private
name|void
name|verifyMappingNoOverride
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|,
name|int
name|expectedSize
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Map size"
argument_list|,
name|expectedSize
argument_list|,
name|properties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|properties
operator|.
name|get
argument_list|(
name|ENABLE_QUEUE_MAPPING_OVERRIDE
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No mapping property found"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Override mapping"
argument_list|,
literal|"false"
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|class|TestPlacementRule
specifier|private
class|class
name|TestPlacementRule
extends|extends
name|FSPlacementRule
block|{
annotation|@
name|Override
DECL|method|getPlacementForApp ( ApplicationSubmissionContext asc, String user)
specifier|public
name|ApplicationPlacementContext
name|getPlacementForApp
parameter_list|(
name|ApplicationSubmissionContext
name|asc
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|YarnException
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

