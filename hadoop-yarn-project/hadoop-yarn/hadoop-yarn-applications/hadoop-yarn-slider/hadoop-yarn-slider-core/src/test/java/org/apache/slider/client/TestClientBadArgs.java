begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.client
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|client
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
name|slider
operator|.
name|common
operator|.
name|params
operator|.
name|Arguments
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|params
operator|.
name|SliderActions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|BadCommandArgumentsException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|ErrorStrings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|UsageException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|utils
operator|.
name|SliderTestBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * Test the argument parsing/validation logic.  */
end_comment

begin_class
DECL|class|TestClientBadArgs
specifier|public
class|class
name|TestClientBadArgs
extends|extends
name|SliderTestBase
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
name|TestClientBadArgs
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testNoAction ()
specifier|public
name|void
name|testNoAction
parameter_list|()
throws|throws
name|Throwable
block|{
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"Usage: slider COMMAND"
argument_list|,
name|EMPTY_LIST
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnknownAction ()
specifier|public
name|void
name|testUnknownAction
parameter_list|()
throws|throws
name|Throwable
block|{
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"not-a-known-action"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"not-a-known-action"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testActionWithoutOptions ()
specifier|public
name|void
name|testActionWithoutOptions
parameter_list|()
throws|throws
name|Throwable
block|{
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"Usage: slider build<application>"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_BUILD
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testActionWithoutEnoughArgs ()
specifier|public
name|void
name|testActionWithoutEnoughArgs
parameter_list|()
throws|throws
name|Throwable
block|{
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
name|ErrorStrings
operator|.
name|ERROR_NOT_ENOUGH_ARGUMENTS
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_START
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testActionWithTooManyArgs ()
specifier|public
name|void
name|testActionWithTooManyArgs
parameter_list|()
throws|throws
name|Throwable
block|{
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
name|ErrorStrings
operator|.
name|ERROR_TOO_MANY_ARGUMENTS
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_HELP
argument_list|,
literal|"hello, world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBadImageArg ()
specifier|public
name|void
name|testBadImageArg
parameter_list|()
throws|throws
name|Throwable
block|{
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"Unknown option: --image"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_HELP
argument_list|,
name|Arguments
operator|.
name|ARG_IMAGE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegistryUsage ()
specifier|public
name|void
name|testRegistryUsage
parameter_list|()
throws|throws
name|Throwable
block|{
name|Throwable
name|exception
init|=
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"org.apache.slider.core.exceptions.UsageException: Argument --name "
operator|+
literal|"missing"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_REGISTRY
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|instanceof
name|UsageException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|exception
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegistryExportBadUsage1 ()
specifier|public
name|void
name|testRegistryExportBadUsage1
parameter_list|()
throws|throws
name|Throwable
block|{
name|Throwable
name|exception
init|=
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"Expected a value after parameter --getexp"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_REGISTRY
argument_list|,
name|Arguments
operator|.
name|ARG_NAME
argument_list|,
literal|"cl1"
argument_list|,
name|Arguments
operator|.
name|ARG_GETEXP
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|instanceof
name|BadCommandArgumentsException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|exception
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegistryExportBadUsage2 ()
specifier|public
name|void
name|testRegistryExportBadUsage2
parameter_list|()
throws|throws
name|Throwable
block|{
name|Throwable
name|exception
init|=
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"Expected a value after parameter --getexp"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_REGISTRY
argument_list|,
name|Arguments
operator|.
name|ARG_NAME
argument_list|,
literal|"cl1"
argument_list|,
name|Arguments
operator|.
name|ARG_LISTEXP
argument_list|,
name|Arguments
operator|.
name|ARG_GETEXP
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|instanceof
name|BadCommandArgumentsException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|exception
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegistryExportBadUsage3 ()
specifier|public
name|void
name|testRegistryExportBadUsage3
parameter_list|()
throws|throws
name|Throwable
block|{
name|Throwable
name|exception
init|=
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"Usage: registry"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_REGISTRY
argument_list|,
name|Arguments
operator|.
name|ARG_NAME
argument_list|,
literal|"cl1"
argument_list|,
name|Arguments
operator|.
name|ARG_LISTEXP
argument_list|,
name|Arguments
operator|.
name|ARG_GETEXP
argument_list|,
literal|"export1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|instanceof
name|UsageException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|exception
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpgradeUsage ()
specifier|public
name|void
name|testUpgradeUsage
parameter_list|()
throws|throws
name|Throwable
block|{
name|Throwable
name|exception
init|=
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"org.apache.slider.core.exceptions.BadCommandArgumentsException: Not "
operator|+
literal|"enough arguments for action: upgrade Expected minimum 1 but got 0"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_UPGRADE
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|instanceof
name|BadCommandArgumentsException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|exception
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createTestConfig ()
specifier|public
name|Configuration
name|createTestConfig
parameter_list|()
block|{
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
literal|"127.0.0.1:8032"
argument_list|)
expr_stmt|;
return|return
name|configuration
return|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testUpgradeWithTemplateResourcesAndContainersOption ()
specifier|public
name|void
name|testUpgradeWithTemplateResourcesAndContainersOption
parameter_list|()
throws|throws
name|Throwable
block|{
comment|//TODO test upgrade args
name|String
name|appName
init|=
literal|"test_hbase"
decl_stmt|;
name|Throwable
name|exception
init|=
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"BadCommandArgumentsException: Option --containers cannot be "
operator|+
literal|"specified with --appdef"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_UPGRADE
argument_list|,
name|appName
argument_list|,
name|Arguments
operator|.
name|ARG_APPDEF
argument_list|,
literal|"/tmp/app.json"
argument_list|,
name|Arguments
operator|.
name|ARG_CONTAINERS
argument_list|,
literal|"container_1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|instanceof
name|BadCommandArgumentsException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|exception
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testUpgradeWithTemplateResourcesAndComponentsOption ()
specifier|public
name|void
name|testUpgradeWithTemplateResourcesAndComponentsOption
parameter_list|()
throws|throws
name|Throwable
block|{
comment|//TODO test upgrade args
name|String
name|appName
init|=
literal|"test_hbase"
decl_stmt|;
name|Throwable
name|exception
init|=
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"BadCommandArgumentsException: Option --components cannot be "
operator|+
literal|"specified with --appdef"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_UPGRADE
argument_list|,
name|appName
argument_list|,
name|Arguments
operator|.
name|ARG_APPDEF
argument_list|,
literal|"/tmp/app.json"
argument_list|,
name|Arguments
operator|.
name|ARG_COMPONENTS
argument_list|,
literal|"HBASE_MASTER"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|instanceof
name|BadCommandArgumentsException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|exception
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodesMissingFile ()
specifier|public
name|void
name|testNodesMissingFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|Throwable
name|exception
init|=
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
name|createTestConfig
argument_list|()
argument_list|,
literal|"after parameter --out"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_NODES
argument_list|,
name|Arguments
operator|.
name|ARG_OUTPUT
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|instanceof
name|BadCommandArgumentsException
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFlexWithNoComponents ()
specifier|public
name|void
name|testFlexWithNoComponents
parameter_list|()
throws|throws
name|Throwable
block|{
name|Throwable
name|exception
init|=
name|launchExpectingException
argument_list|(
name|SliderClient
operator|.
name|class
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|,
literal|"Usage: slider flex<application>"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|SliderActions
operator|.
name|ACTION_FLEX
argument_list|,
literal|"flex1"
argument_list|,
name|Arguments
operator|.
name|ARG_DEFINE
argument_list|,
name|YarnConfiguration
operator|.
name|RM_ADDRESS
operator|+
literal|"=127.0.0.1:8032"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exception
operator|instanceof
name|UsageException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|exception
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

