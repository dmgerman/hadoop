begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.TestUtils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|TestUtils
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerManager
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|background
operator|.
name|BlockDeletingService
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
name|CountDownLatch
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
name|TimeUnit
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
name|Future
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * A test class implementation for {@link BlockDeletingService}.  */
end_comment

begin_class
DECL|class|BlockDeletingServiceTestImpl
specifier|public
class|class
name|BlockDeletingServiceTestImpl
extends|extends
name|BlockDeletingService
block|{
comment|// the service timeout
DECL|field|SERVICE_TIMEOUT_IN_MILLISECONDS
specifier|private
specifier|static
specifier|final
name|int
name|SERVICE_TIMEOUT_IN_MILLISECONDS
init|=
literal|0
decl_stmt|;
comment|// tests only
DECL|field|latch
specifier|private
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|testingThread
specifier|private
name|Thread
name|testingThread
decl_stmt|;
DECL|field|numOfProcessed
specifier|private
name|AtomicInteger
name|numOfProcessed
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|BlockDeletingServiceTestImpl (ContainerManager containerManager, int serviceInterval, Configuration conf)
specifier|public
name|BlockDeletingServiceTestImpl
parameter_list|(
name|ContainerManager
name|containerManager
parameter_list|,
name|int
name|serviceInterval
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|containerManager
argument_list|,
name|serviceInterval
argument_list|,
name|SERVICE_TIMEOUT_IN_MILLISECONDS
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|runDeletingTasks ()
specifier|public
name|void
name|runDeletingTasks
parameter_list|()
block|{
if|if
condition|(
name|latch
operator|.
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Count already reaches zero"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|isStarted ()
specifier|public
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
name|latch
operator|!=
literal|null
operator|&&
name|testingThread
operator|.
name|isAlive
argument_list|()
return|;
block|}
DECL|method|getTimesOfProcessed ()
specifier|public
name|int
name|getTimesOfProcessed
parameter_list|()
block|{
return|return
name|numOfProcessed
operator|.
name|get
argument_list|()
return|;
block|}
comment|// Override the implementation to start a single on-call control thread.
DECL|method|start ()
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{
name|PeriodicalTask
name|svc
init|=
operator|new
name|PeriodicalTask
argument_list|()
decl_stmt|;
comment|// In test mode, relies on a latch countdown to runDeletingTasks tasks.
name|Runnable
name|r
init|=
parameter_list|()
lambda|->
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|latch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
break|break;
block|}
name|Future
argument_list|<
name|?
argument_list|>
name|future
init|=
name|this
operator|.
name|getExecutorService
argument_list|()
operator|.
name|submit
argument_list|(
name|svc
argument_list|)
decl_stmt|;
try|try
block|{
comment|// for tests, we only wait for 3s for completion
name|future
operator|.
name|get
argument_list|(
literal|3
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|numOfProcessed
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return;
block|}
block|}
block|}
decl_stmt|;
name|testingThread
operator|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|newThread
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|testingThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|testingThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|super
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

