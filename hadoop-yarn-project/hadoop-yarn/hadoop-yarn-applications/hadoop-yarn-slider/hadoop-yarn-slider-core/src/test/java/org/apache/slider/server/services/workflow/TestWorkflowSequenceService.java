begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.services.workflow
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|workflow
package|;
end_package

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
name|service
operator|.
name|Service
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

begin_class
DECL|class|TestWorkflowSequenceService
specifier|public
class|class
name|TestWorkflowSequenceService
extends|extends
name|ParentWorkflowTestBase
block|{
specifier|private
specifier|static
specifier|final
name|Logger
DECL|field|log
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestWorkflowSequenceService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//@Test
DECL|method|testSingleSequence ()
specifier|public
name|void
name|testSingleSequence
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceParent
name|parent
init|=
name|startService
argument_list|(
operator|new
name|MockService
argument_list|()
argument_list|)
decl_stmt|;
name|parent
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|//@Test
DECL|method|testEmptySequence ()
specifier|public
name|void
name|testEmptySequence
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceParent
name|parent
init|=
name|startService
argument_list|()
decl_stmt|;
name|waitForParentToStop
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testSequence ()
specifier|public
name|void
name|testSequence
parameter_list|()
throws|throws
name|Throwable
block|{
name|MockService
name|one
init|=
operator|new
name|MockService
argument_list|(
literal|"one"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|MockService
name|two
init|=
operator|new
name|MockService
argument_list|(
literal|"two"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|ServiceParent
name|parent
init|=
name|startService
argument_list|(
name|one
argument_list|,
name|two
argument_list|)
decl_stmt|;
name|waitForParentToStop
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|one
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|two
argument_list|)
expr_stmt|;
assert|assert
operator|(
operator|(
name|WorkflowSequenceService
operator|)
name|parent
operator|)
operator|.
name|getPreviousService
argument_list|()
operator|.
name|equals
argument_list|(
name|two
argument_list|)
assert|;
block|}
comment|//@Test
DECL|method|testCallableChild ()
specifier|public
name|void
name|testCallableChild
parameter_list|()
throws|throws
name|Throwable
block|{
name|MockService
name|one
init|=
operator|new
name|MockService
argument_list|(
literal|"one"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|CallableHandler
name|handler
init|=
operator|new
name|CallableHandler
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|WorkflowCallbackService
argument_list|<
name|String
argument_list|>
name|ens
init|=
operator|new
name|WorkflowCallbackService
argument_list|<
name|String
argument_list|>
argument_list|(
literal|"handler"
argument_list|,
name|handler
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MockService
name|two
init|=
operator|new
name|MockService
argument_list|(
literal|"two"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|ServiceParent
name|parent
init|=
name|startService
argument_list|(
name|one
argument_list|,
name|ens
argument_list|,
name|two
argument_list|)
decl_stmt|;
name|waitForParentToStop
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|one
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|ens
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|two
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|.
name|notified
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|ens
operator|.
name|getScheduledFuture
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testFailingSequence ()
specifier|public
name|void
name|testFailingSequence
parameter_list|()
throws|throws
name|Throwable
block|{
name|MockService
name|one
init|=
operator|new
name|MockService
argument_list|(
literal|"one"
argument_list|,
literal|true
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|MockService
name|two
init|=
operator|new
name|MockService
argument_list|(
literal|"two"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|WorkflowSequenceService
name|parent
init|=
operator|(
name|WorkflowSequenceService
operator|)
name|startService
argument_list|(
name|one
argument_list|,
name|two
argument_list|)
decl_stmt|;
name|waitForParentToStop
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|one
argument_list|)
expr_stmt|;
name|assertInState
argument_list|(
name|two
argument_list|,
name|Service
operator|.
name|STATE
operator|.
name|NOTINITED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|one
argument_list|,
name|parent
operator|.
name|getPreviousService
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testFailInStartNext ()
specifier|public
name|void
name|testFailInStartNext
parameter_list|()
throws|throws
name|Throwable
block|{
name|MockService
name|one
init|=
operator|new
name|MockService
argument_list|(
literal|"one"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|MockService
name|two
init|=
operator|new
name|MockService
argument_list|(
literal|"two"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|MockService
name|three
init|=
operator|new
name|MockService
argument_list|(
literal|"3"
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ServiceParent
name|parent
init|=
name|startService
argument_list|(
name|one
argument_list|,
name|two
argument_list|,
name|three
argument_list|)
decl_stmt|;
name|waitForParentToStop
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|one
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|two
argument_list|)
expr_stmt|;
name|Throwable
name|failureCause
init|=
name|two
operator|.
name|getFailureCause
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|failureCause
argument_list|)
expr_stmt|;
name|Throwable
name|parentFailureCause
init|=
name|parent
operator|.
name|getFailureCause
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|parentFailureCause
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parentFailureCause
argument_list|,
name|failureCause
argument_list|)
expr_stmt|;
name|assertInState
argument_list|(
name|three
argument_list|,
name|Service
operator|.
name|STATE
operator|.
name|NOTINITED
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testSequenceInSequence ()
specifier|public
name|void
name|testSequenceInSequence
parameter_list|()
throws|throws
name|Throwable
block|{
name|MockService
name|one
init|=
operator|new
name|MockService
argument_list|(
literal|"one"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|MockService
name|two
init|=
operator|new
name|MockService
argument_list|(
literal|"two"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|ServiceParent
name|parent
init|=
name|buildService
argument_list|(
name|one
argument_list|,
name|two
argument_list|)
decl_stmt|;
name|ServiceParent
name|outer
init|=
name|startService
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|waitForParentToStop
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|one
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|two
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testVarargsConstructor ()
specifier|public
name|void
name|testVarargsConstructor
parameter_list|()
throws|throws
name|Throwable
block|{
name|MockService
name|one
init|=
operator|new
name|MockService
argument_list|(
literal|"one"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|MockService
name|two
init|=
operator|new
name|MockService
argument_list|(
literal|"two"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|ServiceParent
name|parent
init|=
operator|new
name|WorkflowSequenceService
argument_list|(
literal|"test"
argument_list|,
name|one
argument_list|,
name|two
argument_list|)
decl_stmt|;
name|parent
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|parent
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForParentToStop
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|one
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|two
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testAddChild ()
specifier|public
name|void
name|testAddChild
parameter_list|()
throws|throws
name|Throwable
block|{
name|MockService
name|one
init|=
operator|new
name|MockService
argument_list|(
literal|"one"
argument_list|,
literal|false
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|MockService
name|two
init|=
operator|new
name|MockService
argument_list|(
literal|"two"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|ServiceParent
name|parent
init|=
name|startService
argument_list|(
name|one
argument_list|,
name|two
argument_list|)
decl_stmt|;
name|CallableHandler
name|handler
init|=
operator|new
name|CallableHandler
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|WorkflowCallbackService
argument_list|<
name|String
argument_list|>
name|ens
init|=
operator|new
name|WorkflowCallbackService
argument_list|<
name|String
argument_list|>
argument_list|(
literal|"handler"
argument_list|,
name|handler
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|parent
operator|.
name|addService
argument_list|(
name|ens
argument_list|)
expr_stmt|;
name|waitForParentToStop
argument_list|(
name|parent
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|one
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|two
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|ens
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|two
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|ens
operator|.
name|getScheduledFuture
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|buildService (Service... services)
specifier|public
name|WorkflowSequenceService
name|buildService
parameter_list|(
name|Service
modifier|...
name|services
parameter_list|)
block|{
name|WorkflowSequenceService
name|parent
init|=
operator|new
name|WorkflowSequenceService
argument_list|(
literal|"test"
argument_list|,
name|services
argument_list|)
decl_stmt|;
name|parent
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|parent
return|;
block|}
block|}
end_class

end_unit

