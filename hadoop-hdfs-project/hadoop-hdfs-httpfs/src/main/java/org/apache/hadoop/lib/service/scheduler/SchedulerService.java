begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.service.scheduler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|service
operator|.
name|scheduler
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
name|classification
operator|.
name|InterfaceAudience
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
name|lib
operator|.
name|lang
operator|.
name|RunnableCallable
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
name|lib
operator|.
name|server
operator|.
name|BaseService
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
name|lib
operator|.
name|server
operator|.
name|Server
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
name|lib
operator|.
name|server
operator|.
name|ServiceException
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
name|lib
operator|.
name|service
operator|.
name|Instrumentation
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
name|lib
operator|.
name|service
operator|.
name|Scheduler
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
name|lib
operator|.
name|util
operator|.
name|Check
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
name|util
operator|.
name|Time
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
name|text
operator|.
name|MessageFormat
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
name|Callable
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
name|ScheduledThreadPoolExecutor
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SchedulerService
specifier|public
class|class
name|SchedulerService
extends|extends
name|BaseService
implements|implements
name|Scheduler
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
name|SchedulerService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|INST_GROUP
specifier|private
specifier|static
specifier|final
name|String
name|INST_GROUP
init|=
literal|"scheduler"
decl_stmt|;
DECL|field|PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"scheduler"
decl_stmt|;
DECL|field|CONF_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|CONF_THREADS
init|=
literal|"threads"
decl_stmt|;
DECL|field|scheduler
specifier|private
name|ScheduledExecutorService
name|scheduler
decl_stmt|;
DECL|method|SchedulerService ()
specifier|public
name|SchedulerService
parameter_list|()
block|{
name|super
argument_list|(
name|PREFIX
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServiceException
block|{
name|int
name|threads
init|=
name|getServiceConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|CONF_THREADS
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|scheduler
operator|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scheduler started"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
try|try
block|{
name|long
name|limit
init|=
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|30
operator|*
literal|1000
decl_stmt|;
name|scheduler
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|scheduler
operator|.
name|awaitTermination
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Waiting for scheduler to shutdown"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Time
operator|.
name|now
argument_list|()
operator|>
name|limit
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Gave up waiting for scheduler to shutdown"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|scheduler
operator|.
name|isTerminated
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scheduler shutdown"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getServiceDependencies ()
specifier|public
name|Class
index|[]
name|getServiceDependencies
parameter_list|()
block|{
return|return
operator|new
name|Class
index|[]
block|{
name|Instrumentation
operator|.
name|class
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
name|getInterface
parameter_list|()
block|{
return|return
name|Scheduler
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|schedule (final Callable<?> callable, long delay, long interval, TimeUnit unit)
specifier|public
name|void
name|schedule
parameter_list|(
specifier|final
name|Callable
argument_list|<
name|?
argument_list|>
name|callable
parameter_list|,
name|long
name|delay
parameter_list|,
name|long
name|interval
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|Check
operator|.
name|notNull
argument_list|(
name|callable
argument_list|,
literal|"callable"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|scheduler
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scheduling callable [{}], interval [{}] seconds, delay [{}] in [{}]"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|callable
block|,
name|delay
block|,
name|interval
block|,
name|unit
block|}
argument_list|)
expr_stmt|;
name|Runnable
name|r
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|String
name|instrName
init|=
name|callable
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|Instrumentation
name|instr
init|=
name|getServer
argument_list|()
operator|.
name|get
argument_list|(
name|Instrumentation
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|getServer
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|Server
operator|.
name|Status
operator|.
name|HALTED
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipping [{}], server status [{}]"
argument_list|,
name|callable
argument_list|,
name|getServer
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|instr
operator|.
name|incr
argument_list|(
name|INST_GROUP
argument_list|,
name|instrName
operator|+
literal|".skips"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Executing [{}]"
argument_list|,
name|callable
argument_list|)
expr_stmt|;
name|instr
operator|.
name|incr
argument_list|(
name|INST_GROUP
argument_list|,
name|instrName
operator|+
literal|".execs"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Instrumentation
operator|.
name|Cron
name|cron
init|=
name|instr
operator|.
name|createCron
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
try|try
block|{
name|callable
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|instr
operator|.
name|incr
argument_list|(
name|INST_GROUP
argument_list|,
name|instrName
operator|+
literal|".fails"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Error executing [{}], {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|callable
block|,
name|ex
operator|.
name|getMessage
argument_list|()
block|,
name|ex
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|instr
operator|.
name|addCron
argument_list|(
name|INST_GROUP
argument_list|,
name|instrName
argument_list|,
name|cron
operator|.
name|stop
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|scheduler
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|r
argument_list|,
name|delay
argument_list|,
name|interval
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Scheduler shutting down, ignoring scheduling of [{}]"
argument_list|,
name|callable
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|schedule (Runnable runnable, long delay, long interval, TimeUnit unit)
specifier|public
name|void
name|schedule
parameter_list|(
name|Runnable
name|runnable
parameter_list|,
name|long
name|delay
parameter_list|,
name|long
name|interval
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|schedule
argument_list|(
operator|(
name|Callable
argument_list|<
name|?
argument_list|>
operator|)
operator|new
name|RunnableCallable
argument_list|(
name|runnable
argument_list|)
argument_list|,
name|delay
argument_list|,
name|interval
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

