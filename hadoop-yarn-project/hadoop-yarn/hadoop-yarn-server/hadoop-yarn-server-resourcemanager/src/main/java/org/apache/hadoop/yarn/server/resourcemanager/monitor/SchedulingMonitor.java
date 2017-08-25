begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.monitor
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
name|monitor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
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
name|ScheduledExecutorService
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
name|ScheduledFuture
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
name|ThreadFactory
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
name|AbstractService
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
name|YarnRuntimeException
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

begin_class
DECL|class|SchedulingMonitor
specifier|public
class|class
name|SchedulingMonitor
extends|extends
name|AbstractService
block|{
DECL|field|scheduleEditPolicy
specifier|private
specifier|final
name|SchedulingEditPolicy
name|scheduleEditPolicy
decl_stmt|;
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
name|SchedulingMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// ScheduledExecutorService which schedules the PreemptionChecker to run
comment|// periodically.
DECL|field|ses
specifier|private
name|ScheduledExecutorService
name|ses
decl_stmt|;
DECL|field|handler
specifier|private
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|handler
decl_stmt|;
DECL|field|stopped
specifier|private
specifier|volatile
name|boolean
name|stopped
decl_stmt|;
DECL|field|monitorInterval
specifier|private
name|long
name|monitorInterval
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|SchedulingMonitor (RMContext rmContext, SchedulingEditPolicy scheduleEditPolicy)
specifier|public
name|SchedulingMonitor
parameter_list|(
name|RMContext
name|rmContext
parameter_list|,
name|SchedulingEditPolicy
name|scheduleEditPolicy
parameter_list|)
block|{
name|super
argument_list|(
literal|"SchedulingMonitor ("
operator|+
name|scheduleEditPolicy
operator|.
name|getPolicyName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduleEditPolicy
operator|=
name|scheduleEditPolicy
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getSchedulingEditPolicy ()
specifier|public
specifier|synchronized
name|SchedulingEditPolicy
name|getSchedulingEditPolicy
parameter_list|()
block|{
return|return
name|scheduleEditPolicy
return|;
block|}
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|scheduleEditPolicy
operator|.
name|init
argument_list|(
name|conf
argument_list|,
name|rmContext
argument_list|,
name|rmContext
operator|.
name|getScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|monitorInterval
operator|=
name|scheduleEditPolicy
operator|.
name|getMonitoringInterval
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|public
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
assert|assert
operator|!
name|stopped
operator|:
literal|"starting when already stopped"
assert|;
name|ses
operator|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|(
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|handler
operator|=
name|ses
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|PreemptionChecker
argument_list|()
argument_list|,
literal|0
argument_list|,
name|monitorInterval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|stopped
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stop "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ses
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|invokePolicy ()
specifier|public
name|void
name|invokePolicy
parameter_list|()
block|{
name|scheduleEditPolicy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
block|}
DECL|class|PreemptionChecker
specifier|private
class|class
name|PreemptionChecker
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|//invoke the preemption policy
name|invokePolicy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// The preemption monitor does not alter structures nor do structures
comment|// persist across invocations. Therefore, log, skip, and retry.
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception raised while executing preemption"
operator|+
literal|" checker, skip this run..., exception="
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

