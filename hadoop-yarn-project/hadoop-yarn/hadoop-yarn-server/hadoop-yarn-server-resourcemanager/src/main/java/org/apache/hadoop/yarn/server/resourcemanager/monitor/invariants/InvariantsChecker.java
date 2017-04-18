begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.monitor.invariants
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
operator|.
name|invariants
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|monitor
operator|.
name|SchedulingEditPolicy
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
name|scheduler
operator|.
name|PreemptableResourceScheduler
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

begin_comment
comment|/**  * Abstract invariant checker, that setup common context for invariants  * checkers.  */
end_comment

begin_class
DECL|class|InvariantsChecker
specifier|public
specifier|abstract
class|class
name|InvariantsChecker
implements|implements
name|SchedulingEditPolicy
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
name|InvariantsChecker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|THROW_ON_VIOLATION
specifier|public
specifier|static
specifier|final
name|String
name|THROW_ON_VIOLATION
init|=
literal|"yarn.resourcemanager.invariant-checker.throw-on-violation"
decl_stmt|;
DECL|field|INVARIANT_MONITOR_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|INVARIANT_MONITOR_INTERVAL
init|=
literal|"yarn.resourcemanager.invariant-checker.monitor-interval"
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|context
specifier|private
name|RMContext
name|context
decl_stmt|;
DECL|field|scheduler
specifier|private
name|PreemptableResourceScheduler
name|scheduler
decl_stmt|;
DECL|field|throwOnInvariantViolation
specifier|private
name|boolean
name|throwOnInvariantViolation
decl_stmt|;
DECL|field|monitoringInterval
specifier|private
name|long
name|monitoringInterval
decl_stmt|;
annotation|@
name|Override
DECL|method|init (Configuration config, RMContext rmContext, PreemptableResourceScheduler preemptableResourceScheduler)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|RMContext
name|rmContext
parameter_list|,
name|PreemptableResourceScheduler
name|preemptableResourceScheduler
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|rmContext
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|preemptableResourceScheduler
expr_stmt|;
name|this
operator|.
name|throwOnInvariantViolation
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|InvariantsChecker
operator|.
name|THROW_ON_VIOLATION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|monitoringInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|InvariantsChecker
operator|.
name|INVARIANT_MONITOR_INTERVAL
argument_list|,
literal|1000L
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Invariant checker "
operator|+
name|this
operator|.
name|getPolicyName
argument_list|()
operator|+
literal|" enabled. Monitoring every "
operator|+
name|monitoringInterval
operator|+
literal|"ms, throwOnViolation="
operator|+
name|throwOnInvariantViolation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMonitoringInterval ()
specifier|public
name|long
name|getMonitoringInterval
parameter_list|()
block|{
return|return
name|monitoringInterval
return|;
block|}
annotation|@
name|Override
DECL|method|getPolicyName ()
specifier|public
name|String
name|getPolicyName
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
DECL|method|logOrThrow (String message)
specifier|public
name|void
name|logOrThrow
parameter_list|(
name|String
name|message
parameter_list|)
throws|throws
name|InvariantViolationException
block|{
if|if
condition|(
name|getThrowOnInvariantViolation
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvariantViolationException
argument_list|(
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getThrowOnInvariantViolation ()
specifier|public
name|boolean
name|getThrowOnInvariantViolation
parameter_list|()
block|{
return|return
name|throwOnInvariantViolation
return|;
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|getContext ()
specifier|public
name|RMContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
DECL|method|getScheduler ()
specifier|public
name|PreemptableResourceScheduler
name|getScheduler
parameter_list|()
block|{
return|return
name|scheduler
return|;
block|}
block|}
end_class

end_unit

