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
name|service
operator|.
name|launcher
operator|.
name|testservices
operator|.
name|FailInConstructorService
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
name|FailInInitService
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
name|FailInStartService
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Explore the ways in which the launcher is expected to (safely) fail.  */
end_comment

begin_class
DECL|class|TestServiceLauncherCreationFailures
specifier|public
class|class
name|TestServiceLauncherCreationFailures
extends|extends
name|AbstractServiceLauncherTestBase
block|{
DECL|field|SELF
specifier|public
specifier|static
specifier|final
name|String
name|SELF
init|=
literal|"org.apache.hadoop.service.launcher.TestServiceLauncherCreationFailures"
decl_stmt|;
annotation|@
name|Test
DECL|method|testNoArgs ()
specifier|public
name|void
name|testNoArgs
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|ServiceLauncher
operator|.
name|serviceMain
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceLaunchException
name|e
parameter_list|)
block|{
name|assertExceptionDetails
argument_list|(
name|EXIT_USAGE
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUnknownClass ()
specifier|public
name|void
name|testUnknownClass
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertServiceCreationFails
argument_list|(
literal|"no.such.classname"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNotAService ()
specifier|public
name|void
name|testNotAService
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertServiceCreationFails
argument_list|(
name|SELF
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoSimpleConstructor ()
specifier|public
name|void
name|testNoSimpleConstructor
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertServiceCreationFails
argument_list|(
literal|"org.apache.hadoop.service.launcher.FailureTestService"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailInConstructor ()
specifier|public
name|void
name|testFailInConstructor
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertServiceCreationFails
argument_list|(
name|FailInConstructorService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailInInit ()
specifier|public
name|void
name|testFailInInit
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertLaunchOutcome
argument_list|(
name|FailInInitService
operator|.
name|EXIT_CODE
argument_list|,
literal|""
argument_list|,
name|FailInInitService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailInStart ()
specifier|public
name|void
name|testFailInStart
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertLaunchOutcome
argument_list|(
name|FailInStartService
operator|.
name|EXIT_CODE
argument_list|,
literal|""
argument_list|,
name|FailInStartService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailInStopIsIgnored ()
specifier|public
name|void
name|testFailInStopIsIgnored
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertRuns
argument_list|(
name|FailingStopInStartService
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

