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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
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
name|io
operator|.
name|IOUtils
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|security
operator|.
name|GroupMappingServiceProvider
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
name|BeforeClass
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
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_class
DECL|class|TestQueuePlacementPolicy
specifier|public
class|class
name|TestQueuePlacementPolicy
block|{
DECL|field|conf
specifier|private
specifier|final
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|configuredQueues
specifier|private
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
name|conf
operator|.
name|setClass
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_MAPPING
argument_list|,
name|SimpleGroupsMapping
operator|.
name|class
argument_list|,
name|GroupMappingServiceProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|initTest ()
specifier|public
name|void
name|initTest
parameter_list|()
block|{
name|configuredQueues
operator|=
operator|new
name|HashMap
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|FSQueueType
name|type
range|:
name|FSQueueType
operator|.
name|values
argument_list|()
control|)
block|{
name|configuredQueues
operator|.
name|put
argument_list|(
name|type
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSpecifiedUserPolicy ()
specifier|public
name|void
name|testSpecifiedUserPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='user' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|QueuePlacementPolicy
name|policy
init|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"root.specifiedq"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"specifiedq"
argument_list|,
literal|"someuser"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root.someuser"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"default"
argument_list|,
literal|"someuser"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root.otheruser"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"default"
argument_list|,
literal|"otheruser"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoCreate ()
specifier|public
name|void
name|testNoCreate
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='user' create=\"false\" />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|LEAF
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.someuser"
argument_list|)
expr_stmt|;
name|QueuePlacementPolicy
name|policy
init|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"root.specifiedq"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"specifiedq"
argument_list|,
literal|"someuser"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root.someuser"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"default"
argument_list|,
literal|"someuser"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root.specifiedq"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"specifiedq"
argument_list|,
literal|"otheruser"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root.default"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"default"
argument_list|,
literal|"otheruser"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSpecifiedThenReject ()
specifier|public
name|void
name|testSpecifiedThenReject
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='reject' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|QueuePlacementPolicy
name|policy
init|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"root.specifiedq"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"specifiedq"
argument_list|,
literal|"someuser"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"default"
argument_list|,
literal|"someuser"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AllocationConfigurationException
operator|.
name|class
argument_list|)
DECL|method|testOmittedTerminalRule ()
specifier|public
name|void
name|testOmittedTerminalRule
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='user' create=\"false\" />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AllocationConfigurationException
operator|.
name|class
argument_list|)
DECL|method|testTerminalRuleInMiddle ()
specifier|public
name|void
name|testTerminalRuleInMiddle
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='user' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTerminals ()
specifier|public
name|void
name|testTerminals
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Should make it through without an exception
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='secondaryGroupExistingQueue' create='true'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' create='false'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultRuleWithQueueAttribute ()
specifier|public
name|void
name|testDefaultRuleWithQueueAttribute
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This test covers the use case where we would like default rule
comment|// to point to a different queue by default rather than root.default
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|LEAF
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.someDefaultQueue"
argument_list|)
expr_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' create='false' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' queue='root.someDefaultQueue'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|QueuePlacementPolicy
name|policy
init|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"root.someDefaultQueue"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.default"
argument_list|,
literal|"user1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedUserQueueParsingErrors ()
specifier|public
name|void
name|testNestedUserQueueParsingErrors
parameter_list|()
block|{
comment|// No nested rule specified in hierarchical user queue
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='nestedUserQueue'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|assertIfExceptionThrown
argument_list|(
name|sb
argument_list|)
expr_stmt|;
comment|// Specified nested rule is not a QueuePlacementRule
name|sb
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='nestedUserQueue'>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='unknownRule'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</rule>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|assertIfExceptionThrown
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
DECL|method|assertIfExceptionThrown (StringBuffer sb)
specifier|private
name|void
name|assertIfExceptionThrown
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|th
operator|=
name|e
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|th
operator|instanceof
name|AllocationConfigurationException
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedUserQueueParsing ()
specifier|public
name|void
name|testNestedUserQueueParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='nestedUserQueue'>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='primaryGroup'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</rule>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|th
operator|=
name|e
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|th
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedUserQueuePrimaryGroup ()
specifier|public
name|void
name|testNestedUserQueuePrimaryGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' create='false' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='nestedUserQueue'>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='primaryGroup'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</rule>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
comment|// User queue would be created under primary group queue
name|QueuePlacementPolicy
name|policy
init|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"root.user1group.user1"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.default"
argument_list|,
literal|"user1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Other rules above and below hierarchical user queue rule should work as
comment|// usual
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|LEAF
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.specifiedq"
argument_list|)
expr_stmt|;
comment|// test if specified rule(above nestedUserQueue rule) works ok
name|assertEquals
argument_list|(
literal|"root.specifiedq"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.specifiedq"
argument_list|,
literal|"user2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test if default rule(below nestedUserQueue rule) works
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|LEAF
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.user3group"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root.default"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.default"
argument_list|,
literal|"user3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedUserQueuePrimaryGroupNoCreate ()
specifier|public
name|void
name|testNestedUserQueuePrimaryGroupNoCreate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Primary group rule has create='false'
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='nestedUserQueue'>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='primaryGroup' create='false'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</rule>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|QueuePlacementPolicy
name|policy
init|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// Should return root.default since primary group 'root.user1group' is not
comment|// configured
name|assertEquals
argument_list|(
literal|"root.default"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.default"
argument_list|,
literal|"user1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Let's configure primary group and check if user queue is created
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|PARENT
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.user1group"
argument_list|)
expr_stmt|;
name|policy
operator|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root.user1group.user1"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.default"
argument_list|,
literal|"user1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Both Primary group and nestedUserQueue rule has create='false'
name|sb
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='nestedUserQueue' create='false'>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='primaryGroup' create='false'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</rule>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
comment|// Should return root.default since primary group and user queue for user 2
comment|// are not configured.
name|assertEquals
argument_list|(
literal|"root.default"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.default"
argument_list|,
literal|"user2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now configure both primary group and the user queue for user2
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|PARENT
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.user2group"
argument_list|)
expr_stmt|;
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|LEAF
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.user2group.user2"
argument_list|)
expr_stmt|;
name|policy
operator|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root.user2group.user2"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.default"
argument_list|,
literal|"user2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedUserQueueSecondaryGroup ()
specifier|public
name|void
name|testNestedUserQueueSecondaryGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='nestedUserQueue'>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='secondaryGroupExistingQueue'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</rule>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|QueuePlacementPolicy
name|policy
init|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// Should return root.default since secondary groups are not configured
name|assertEquals
argument_list|(
literal|"root.default"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.default"
argument_list|,
literal|"user1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// configure secondary group for user1
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|PARENT
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.user1subgroup1"
argument_list|)
expr_stmt|;
name|policy
operator|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// user queue created should be created under secondary group
name|assertEquals
argument_list|(
literal|"root.user1subgroup1.user1"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.default"
argument_list|,
literal|"user1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedUserQueueSpecificRule ()
specifier|public
name|void
name|testNestedUserQueueSpecificRule
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This test covers the use case where users can specify different parent
comment|// queues and want user queues under those.
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='nestedUserQueue'>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' create='false'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</rule>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
comment|// Let's create couple of parent queues
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|PARENT
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.parent1"
argument_list|)
expr_stmt|;
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|PARENT
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.parent2"
argument_list|)
expr_stmt|;
name|QueuePlacementPolicy
name|policy
init|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"root.parent1.user1"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.parent1"
argument_list|,
literal|"user1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root.parent2.user2"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.parent2"
argument_list|,
literal|"user2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedUserQueueDefaultRule ()
specifier|public
name|void
name|testNestedUserQueueDefaultRule
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This test covers the use case where we would like user queues to be
comment|// created under a default parent queue
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|PARENT
argument_list|)
operator|.
name|add
argument_list|(
literal|"root.parentq"
argument_list|)
expr_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='specified' create='false' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='nestedUserQueue'>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' queue='root.parentq'/>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</rule>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<rule name='default' />"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</queuePlacementPolicy>"
argument_list|)
expr_stmt|;
name|QueuePlacementPolicy
name|policy
init|=
name|parse
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"root.parentq.user1"
argument_list|,
name|policy
operator|.
name|assignAppToQueue
argument_list|(
literal|"root.default"
argument_list|,
literal|"user1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|parse (String str)
specifier|private
name|QueuePlacementPolicy
name|parse
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Read and parse the allocations file.
name|DocumentBuilderFactory
name|docBuilderFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|docBuilderFactory
operator|.
name|setIgnoringComments
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|docBuilderFactory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|str
argument_list|)
argument_list|)
decl_stmt|;
name|Element
name|root
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
return|return
name|QueuePlacementPolicy
operator|.
name|fromXml
argument_list|(
name|root
argument_list|,
name|configuredQueues
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

