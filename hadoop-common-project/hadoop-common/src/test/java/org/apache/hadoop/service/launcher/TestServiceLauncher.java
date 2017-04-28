begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.service.launcher
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|launcher
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
name|BreakableService
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
name|launcher
operator|.
name|testservices
operator|.
name|FailingStopInStartService
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
name|launcher
operator|.
name|testservices
operator|.
name|InitInConstructorLaunchableService
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
name|launcher
operator|.
name|testservices
operator|.
name|LaunchableRunningService
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
name|launcher
operator|.
name|testservices
operator|.
name|NoArgsAllowedService
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
name|launcher
operator|.
name|testservices
operator|.
name|NullBindLaunchableService
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
name|launcher
operator|.
name|testservices
operator|.
name|RunningService
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
name|launcher
operator|.
name|testservices
operator|.
name|StoppingInStartLaunchableService
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
name|launcher
operator|.
name|testservices
operator|.
name|StringConstructorOnlyService
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
name|service
operator|.
name|launcher
operator|.
name|LauncherArguments
operator|.
name|*
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
name|test
operator|.
name|GenericTestUtils
operator|.
name|*
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
name|service
operator|.
name|launcher
operator|.
name|testservices
operator|.
name|ExceptionInExecuteLaunchableService
operator|.
name|*
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
DECL|class|TestServiceLauncher
specifier|public
class|class
name|TestServiceLauncher
extends|extends
name|AbstractServiceLauncherTestBase
block|{
annotation|@
name|Test
DECL|method|testRunService ()
specifier|public
name|void
name|testRunService
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertRuns
argument_list|(
name|RunningService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNullBindService ()
specifier|public
name|void
name|testNullBindService
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertRuns
argument_list|(
name|NullBindLaunchableService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testServiceLaunchStringConstructor ()
specifier|public
name|void
name|testServiceLaunchStringConstructor
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertRuns
argument_list|(
name|StringConstructorOnlyService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the behaviour of service stop logic.    */
annotation|@
name|Test
DECL|method|testStopInStartup ()
specifier|public
name|void
name|testStopInStartup
parameter_list|()
throws|throws
name|Throwable
block|{
name|FailingStopInStartService
name|svc
init|=
operator|new
name|FailingStopInStartService
argument_list|()
decl_stmt|;
name|svc
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|svc
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertStopped
argument_list|(
name|svc
argument_list|)
expr_stmt|;
name|Throwable
name|cause
init|=
name|svc
operator|.
name|getFailureCause
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cause
operator|instanceof
name|ServiceLaunchException
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|svc
operator|.
name|waitForServiceToStop
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ServiceLaunchException
name|e
init|=
operator|(
name|ServiceLaunchException
operator|)
name|cause
decl_stmt|;
name|assertEquals
argument_list|(
name|FailingStopInStartService
operator|.
name|EXIT_CODE
argument_list|,
name|e
operator|.
name|getExitCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEx ()
specifier|public
name|void
name|testEx
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertLaunchOutcome
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
name|OTHER_EXCEPTION_TEXT
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test verifies that exceptions in the    * {@link LaunchableService#execute()} method are relayed if an instance of    * an exit exceptions, and forwarded if not.    */
annotation|@
name|Test
DECL|method|testServiceLaunchException ()
specifier|public
name|void
name|testServiceLaunchException
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertLaunchOutcome
argument_list|(
name|EXIT_OTHER_FAILURE
argument_list|,
name|SLE_TEXT
argument_list|,
name|NAME
argument_list|,
name|ARG_THROW_SLE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIOE ()
specifier|public
name|void
name|testIOE
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertLaunchOutcome
argument_list|(
name|IOE_EXIT_CODE
argument_list|,
name|EXIT_IN_IOE_TEXT
argument_list|,
name|NAME
argument_list|,
name|ARG_THROW_IOE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThrowable ()
specifier|public
name|void
name|testThrowable
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertLaunchOutcome
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
literal|"java.lang.OutOfMemoryError"
argument_list|,
name|NAME
argument_list|,
name|ARG_THROWABLE
argument_list|)
expr_stmt|;
block|}
comment|/**    * As the exception is doing some formatting tricks, these    * tests verify that exception arguments are being correctly    * used as initializers.    */
annotation|@
name|Test
DECL|method|testBasicExceptionFormatting ()
specifier|public
name|void
name|testBasicExceptionFormatting
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceLaunchException
name|ex
init|=
operator|new
name|ServiceLaunchException
argument_list|(
literal|0
argument_list|,
literal|"%03x"
argument_list|,
literal|32
argument_list|)
decl_stmt|;
name|assertExceptionContains
argument_list|(
literal|"020"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNotEnoughArgsExceptionFormatting ()
specifier|public
name|void
name|testNotEnoughArgsExceptionFormatting
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceLaunchException
name|ex
init|=
operator|new
name|ServiceLaunchException
argument_list|(
literal|0
argument_list|,
literal|"%03x"
argument_list|)
decl_stmt|;
name|assertExceptionContains
argument_list|(
literal|"%03x"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInnerCause ()
specifier|public
name|void
name|testInnerCause
parameter_list|()
throws|throws
name|Throwable
block|{
name|Exception
name|cause
init|=
operator|new
name|Exception
argument_list|(
literal|"cause"
argument_list|)
decl_stmt|;
name|ServiceLaunchException
name|ex
init|=
operator|new
name|ServiceLaunchException
argument_list|(
literal|0
argument_list|,
literal|"%03x: %s"
argument_list|,
literal|32
argument_list|,
name|cause
argument_list|)
decl_stmt|;
name|assertExceptionContains
argument_list|(
literal|"020"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertExceptionContains
argument_list|(
literal|"cause"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|cause
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInnerCauseNotInFormat ()
specifier|public
name|void
name|testInnerCauseNotInFormat
parameter_list|()
throws|throws
name|Throwable
block|{
name|Exception
name|cause
init|=
operator|new
name|Exception
argument_list|(
literal|"cause"
argument_list|)
decl_stmt|;
name|ServiceLaunchException
name|ex
init|=
operator|new
name|ServiceLaunchException
argument_list|(
literal|0
argument_list|,
literal|"%03x:"
argument_list|,
literal|32
argument_list|,
name|cause
argument_list|)
decl_stmt|;
name|assertExceptionContains
argument_list|(
literal|"020"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"cause"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|cause
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testServiceInitInConstructor ()
specifier|public
name|void
name|testServiceInitInConstructor
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertRuns
argument_list|(
name|InitInConstructorLaunchableService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRunNoArgsAllowedService ()
specifier|public
name|void
name|testRunNoArgsAllowedService
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertRuns
argument_list|(
name|NoArgsAllowedService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoArgsOneArg ()
specifier|public
name|void
name|testNoArgsOneArg
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertLaunchOutcome
argument_list|(
name|EXIT_COMMAND_ARGUMENT_ERROR
argument_list|,
literal|"1"
argument_list|,
name|NoArgsAllowedService
operator|.
name|NAME
argument_list|,
literal|"one"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoArgsHasConfsStripped ()
specifier|public
name|void
name|testNoArgsHasConfsStripped
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertRuns
argument_list|(
name|NoArgsAllowedService
operator|.
name|NAME
argument_list|,
name|LauncherArguments
operator|.
name|ARG_CONF_PREFIXED
argument_list|,
name|configFile
argument_list|(
name|newConf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRunLaunchableService ()
specifier|public
name|void
name|testRunLaunchableService
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertRuns
argument_list|(
name|LaunchableRunningService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgBinding ()
specifier|public
name|void
name|testArgBinding
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertLaunchOutcome
argument_list|(
name|EXIT_OTHER_FAILURE
argument_list|,
literal|""
argument_list|,
name|LaunchableRunningService
operator|.
name|NAME
argument_list|,
name|LaunchableRunningService
operator|.
name|ARG_FAILING
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStoppingInStartLaunchableService ()
specifier|public
name|void
name|testStoppingInStartLaunchableService
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertRuns
argument_list|(
name|StoppingInStartLaunchableService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShutdownHookNullReference ()
specifier|public
name|void
name|testShutdownHookNullReference
parameter_list|()
throws|throws
name|Throwable
block|{
operator|new
name|ServiceShutdownHook
argument_list|(
literal|null
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShutdownHook ()
specifier|public
name|void
name|testShutdownHook
parameter_list|()
throws|throws
name|Throwable
block|{
name|BreakableService
name|service
init|=
operator|new
name|BreakableService
argument_list|()
decl_stmt|;
name|setServiceToTeardown
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|ServiceShutdownHook
name|hook
init|=
operator|new
name|ServiceShutdownHook
argument_list|(
name|service
argument_list|)
decl_stmt|;
name|hook
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertStopped
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailingHookCaught ()
specifier|public
name|void
name|testFailingHookCaught
parameter_list|()
throws|throws
name|Throwable
block|{
name|BreakableService
name|service
init|=
operator|new
name|BreakableService
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setServiceToTeardown
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|ServiceShutdownHook
name|hook
init|=
operator|new
name|ServiceShutdownHook
argument_list|(
name|service
argument_list|)
decl_stmt|;
name|hook
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertStopped
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

