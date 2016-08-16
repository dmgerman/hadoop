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
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_comment
comment|/**  * Basic tests for executor service  */
end_comment

begin_class
DECL|class|TestWorkflowExecutorService
specifier|public
class|class
name|TestWorkflowExecutorService
extends|extends
name|WorkflowServiceTestBase
block|{
annotation|@
name|Test
DECL|method|testAsyncRun ()
specifier|public
name|void
name|testAsyncRun
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExecutorSvc
name|svc
init|=
name|run
argument_list|(
operator|new
name|ExecutorSvc
argument_list|()
argument_list|)
decl_stmt|;
name|ServiceTerminatingRunnable
name|runnable
init|=
operator|new
name|ServiceTerminatingRunnable
argument_list|(
name|svc
argument_list|,
operator|new
name|SimpleRunnable
argument_list|()
argument_list|)
decl_stmt|;
comment|// synchronous in-thread execution
name|svc
operator|.
name|execute
argument_list|(
name|runnable
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|svc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureRun ()
specifier|public
name|void
name|testFailureRun
parameter_list|()
throws|throws
name|Throwable
block|{
name|ExecutorSvc
name|svc
init|=
name|run
argument_list|(
operator|new
name|ExecutorSvc
argument_list|()
argument_list|)
decl_stmt|;
name|ServiceTerminatingRunnable
name|runnable
init|=
operator|new
name|ServiceTerminatingRunnable
argument_list|(
name|svc
argument_list|,
operator|new
name|SimpleRunnable
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
comment|// synchronous in-thread execution
name|svc
operator|.
name|execute
argument_list|(
name|runnable
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertStopped
argument_list|(
name|svc
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|runnable
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|ExecutorSvc
specifier|private
specifier|static
class|class
name|ExecutorSvc
extends|extends
name|WorkflowExecutorService
argument_list|<
name|ExecutorService
argument_list|>
block|{
DECL|method|ExecutorSvc ()
specifier|private
name|ExecutorSvc
parameter_list|()
block|{
name|super
argument_list|(
literal|"ExecutorService"
argument_list|,
name|ServiceThreadFactory
operator|.
name|singleThreadExecutor
argument_list|(
literal|"test"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

