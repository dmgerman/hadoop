begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.monitor
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
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
name|Date
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
name|hadoop
operator|.
name|util
operator|.
name|ExitUtil
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
name|service
operator|.
name|component
operator|.
name|Component
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
comment|/**  * Monitors the health of containers of a specific component at a regular  * interval. It takes necessary actions when the health of a component drops  * below a desired threshold.  */
end_comment

begin_class
DECL|class|ComponentHealthThresholdMonitor
specifier|public
class|class
name|ComponentHealthThresholdMonitor
implements|implements
name|Runnable
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
name|ComponentHealthThresholdMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|component
specifier|private
specifier|final
name|Component
name|component
decl_stmt|;
DECL|field|healthThresholdPercent
specifier|private
specifier|final
name|int
name|healthThresholdPercent
decl_stmt|;
DECL|field|healthThresholdWindowSecs
specifier|private
specifier|final
name|long
name|healthThresholdWindowSecs
decl_stmt|;
DECL|field|healthThresholdWindowNanos
specifier|private
specifier|final
name|long
name|healthThresholdWindowNanos
decl_stmt|;
DECL|field|firstOccurrenceTimestamp
specifier|private
name|long
name|firstOccurrenceTimestamp
init|=
literal|0
decl_stmt|;
comment|// Sufficient logging happens when component health is below threshold.
comment|// However, there has to be some logging when it is above threshold, otherwise
comment|// service owners have no idea how the health is fluctuating. So let's log
comment|// whenever there is a change in component health, thereby preventing
comment|// excessive logging on every poll.
DECL|field|prevReadyContainerFraction
specifier|private
name|float
name|prevReadyContainerFraction
init|=
literal|0
decl_stmt|;
DECL|method|ComponentHealthThresholdMonitor (Component component, int healthThresholdPercent, long healthThresholdWindowSecs)
specifier|public
name|ComponentHealthThresholdMonitor
parameter_list|(
name|Component
name|component
parameter_list|,
name|int
name|healthThresholdPercent
parameter_list|,
name|long
name|healthThresholdWindowSecs
parameter_list|)
block|{
name|this
operator|.
name|component
operator|=
name|component
expr_stmt|;
name|this
operator|.
name|healthThresholdPercent
operator|=
name|healthThresholdPercent
expr_stmt|;
name|this
operator|.
name|healthThresholdWindowSecs
operator|=
name|healthThresholdWindowSecs
expr_stmt|;
name|this
operator|.
name|healthThresholdWindowNanos
operator|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|healthThresholdWindowSecs
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"ComponentHealthThresholdMonitor run method"
argument_list|)
expr_stmt|;
comment|// Perform container health checks against desired threshold
name|long
name|desiredContainerCount
init|=
name|component
operator|.
name|getNumDesiredInstances
argument_list|()
decl_stmt|;
comment|// If desired container count for this component is 0 then nothing to do
if|if
condition|(
name|desiredContainerCount
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|long
name|readyContainerCount
init|=
name|component
operator|.
name|getNumReadyInstances
argument_list|()
decl_stmt|;
name|float
name|thresholdFraction
init|=
operator|(
name|float
operator|)
name|healthThresholdPercent
operator|/
literal|100
decl_stmt|;
comment|// No possibility of div by 0 since desiredContainerCount won't be 0 here
name|float
name|readyContainerFraction
init|=
operator|(
name|float
operator|)
name|readyContainerCount
operator|/
name|desiredContainerCount
decl_stmt|;
name|boolean
name|healthChanged
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|readyContainerFraction
operator|-
name|prevReadyContainerFraction
argument_list|)
operator|>
literal|.0000001
condition|)
block|{
name|prevReadyContainerFraction
operator|=
name|readyContainerFraction
expr_stmt|;
name|healthChanged
operator|=
literal|true
expr_stmt|;
block|}
name|String
name|readyContainerPercentStr
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%.2f"
argument_list|,
name|readyContainerFraction
operator|*
literal|100
argument_list|)
decl_stmt|;
comment|// Check if the current ready container percent is less than the
comment|// threshold percent
if|if
condition|(
name|readyContainerFraction
operator|<
name|thresholdFraction
condition|)
block|{
comment|// Check if it is the first occurrence and if yes set the timestamp
name|long
name|currentTimestamp
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstOccurrenceTimestamp
operator|==
literal|0
condition|)
block|{
name|firstOccurrenceTimestamp
operator|=
name|currentTimestamp
expr_stmt|;
name|Date
name|date
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[COMPONENT {}] Health has gone below threshold. Starting health "
operator|+
literal|"threshold timer at ts = {} ({})"
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|date
argument_list|)
expr_stmt|;
block|}
name|long
name|elapsedTime
init|=
name|currentTimestamp
operator|-
name|firstOccurrenceTimestamp
decl_stmt|;
name|long
name|elapsedTimeSecs
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|convert
argument_list|(
name|elapsedTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"[COMPONENT {}] Current health {}% is below health threshold of "
operator|+
literal|"{}% for {} secs (threshold window = {} secs)"
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|readyContainerPercentStr
argument_list|,
name|healthThresholdPercent
argument_list|,
name|elapsedTimeSecs
argument_list|,
name|healthThresholdWindowSecs
argument_list|)
expr_stmt|;
if|if
condition|(
name|elapsedTime
operator|>
name|healthThresholdWindowNanos
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"[COMPONENT {}] Current health {}% has been below health "
operator|+
literal|"threshold of {}% for {} secs (threshold window = {} secs)"
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|readyContainerPercentStr
argument_list|,
name|healthThresholdPercent
argument_list|,
name|elapsedTimeSecs
argument_list|,
name|healthThresholdWindowSecs
argument_list|)
expr_stmt|;
comment|// Trigger service stop
name|String
name|exitDiag
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Service is being killed because container health for component "
operator|+
literal|"%s was %s%% (health threshold = %d%%) for %d secs "
operator|+
literal|"(threshold window = %d secs)"
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|readyContainerPercentStr
argument_list|,
name|healthThresholdPercent
argument_list|,
name|elapsedTimeSecs
argument_list|,
name|healthThresholdWindowSecs
argument_list|)
decl_stmt|;
comment|// Append to global diagnostics that will be reported to RM.
name|component
operator|.
name|getScheduler
argument_list|()
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|append
argument_list|(
name|exitDiag
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|exitDiag
argument_list|)
expr_stmt|;
comment|// Sleep for 5 seconds in hope that the state can be recorded in ATS.
comment|// In case there's a client polling the component state, it can be
comment|// notified.
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Interrupted on sleep while exiting."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|ExitUtil
operator|.
name|terminate
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|logMsg
init|=
literal|"[COMPONENT {}] Health threshold = {}%, Current health "
operator|+
literal|"= {}% (Current Ready count = {}, Desired count = {})"
decl_stmt|;
if|if
condition|(
name|healthChanged
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|logMsg
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|healthThresholdPercent
argument_list|,
name|readyContainerPercentStr
argument_list|,
name|readyContainerCount
argument_list|,
name|desiredContainerCount
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|logMsg
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|healthThresholdPercent
argument_list|,
name|readyContainerPercentStr
argument_list|,
name|readyContainerCount
argument_list|,
name|desiredContainerCount
argument_list|)
expr_stmt|;
block|}
comment|// The container health might have recovered above threshold after being
comment|// below for less than the threshold window amount of time. So we need
comment|// to reset firstOccurrenceTimestamp to 0.
if|if
condition|(
name|firstOccurrenceTimestamp
operator|!=
literal|0
condition|)
block|{
name|Date
name|date
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[COMPONENT {}] Health recovered above threshold at ts = {} ({})"
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|date
argument_list|)
expr_stmt|;
name|firstOccurrenceTimestamp
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

