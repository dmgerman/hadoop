begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.servicemonitor
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|servicemonitor
package|;
end_package

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
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * This is the monitor service  */
end_comment

begin_class
DECL|class|ReportingLoop
specifier|public
specifier|final
class|class
name|ReportingLoop
implements|implements
name|Runnable
implements|,
name|ProbeReportHandler
implements|,
name|MonitorKeys
implements|,
name|Closeable
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ReportingLoop
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|worker
specifier|private
specifier|final
name|ProbeWorker
name|worker
decl_stmt|;
DECL|field|workerThread
specifier|private
specifier|final
name|Thread
name|workerThread
decl_stmt|;
DECL|field|reportInterval
specifier|private
specifier|final
name|int
name|reportInterval
decl_stmt|;
DECL|field|probeTimeout
specifier|private
specifier|final
name|int
name|probeTimeout
decl_stmt|;
DECL|field|bootstrapTimeout
specifier|private
specifier|final
name|int
name|bootstrapTimeout
decl_stmt|;
DECL|field|reporter
specifier|private
name|ProbeReportHandler
name|reporter
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|mustExit
specifier|private
specifier|volatile
name|boolean
name|mustExit
decl_stmt|;
DECL|method|ReportingLoop (String name, ProbeReportHandler reporter, List<Probe> probes, List<Probe> dependencyProbes, int probeInterval, int reportInterval, int probeTimeout, int bootstrapTimeout)
specifier|public
name|ReportingLoop
parameter_list|(
name|String
name|name
parameter_list|,
name|ProbeReportHandler
name|reporter
parameter_list|,
name|List
argument_list|<
name|Probe
argument_list|>
name|probes
parameter_list|,
name|List
argument_list|<
name|Probe
argument_list|>
name|dependencyProbes
parameter_list|,
name|int
name|probeInterval
parameter_list|,
name|int
name|reportInterval
parameter_list|,
name|int
name|probeTimeout
parameter_list|,
name|int
name|bootstrapTimeout
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|name
argument_list|,
name|reporter
argument_list|,
operator|new
name|ProbeWorker
argument_list|(
name|probes
argument_list|,
name|dependencyProbes
argument_list|,
name|probeInterval
argument_list|,
name|bootstrapTimeout
argument_list|)
argument_list|,
name|reportInterval
argument_list|,
name|probeTimeout
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new reporting loop -and bond the worker's ProbeReportHandler    * to us    * @param name    * @param reporter    * @param worker    * @param reportInterval    * @param probeTimeout    */
DECL|method|ReportingLoop (String name, ProbeReportHandler reporter, ProbeWorker worker, int reportInterval, int probeTimeout)
specifier|public
name|ReportingLoop
parameter_list|(
name|String
name|name
parameter_list|,
name|ProbeReportHandler
name|reporter
parameter_list|,
name|ProbeWorker
name|worker
parameter_list|,
name|int
name|reportInterval
parameter_list|,
name|int
name|probeTimeout
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|reporter
operator|=
name|reporter
expr_stmt|;
name|this
operator|.
name|reportInterval
operator|=
name|reportInterval
expr_stmt|;
name|this
operator|.
name|probeTimeout
operator|=
name|probeTimeout
expr_stmt|;
name|this
operator|.
name|worker
operator|=
name|worker
expr_stmt|;
name|this
operator|.
name|bootstrapTimeout
operator|=
name|worker
operator|.
name|getBootstrapTimeout
argument_list|()
expr_stmt|;
name|worker
operator|.
name|setReportHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|workerThread
operator|=
operator|new
name|Thread
argument_list|(
name|worker
argument_list|,
literal|"probe thread - "
operator|+
name|name
argument_list|)
expr_stmt|;
name|worker
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|getBootstrapTimeout ()
specifier|public
name|int
name|getBootstrapTimeout
parameter_list|()
block|{
return|return
name|bootstrapTimeout
return|;
block|}
DECL|method|withReporter (ProbeReportHandler reporter)
specifier|public
name|ReportingLoop
name|withReporter
parameter_list|(
name|ProbeReportHandler
name|reporter
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|reporter
operator|==
literal|null
operator|:
literal|"attempting to reassign reporter "
assert|;
assert|assert
name|reporter
operator|!=
literal|null
operator|:
literal|"new reporter is null"
assert|;
name|this
operator|.
name|reporter
operator|=
name|reporter
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Start the monitoring.    *    * @return false if the monitoring did not start and that the worker threads    *         should be run up.    */
DECL|method|startReporting ()
specifier|public
name|boolean
name|startReporting
parameter_list|()
block|{
name|String
name|description
init|=
literal|"Service Monitor for "
operator|+
name|name
operator|+
literal|", probe-interval= "
operator|+
name|MonitorUtils
operator|.
name|millisToHumanTime
argument_list|(
name|worker
operator|.
name|interval
argument_list|)
operator|+
literal|", report-interval="
operator|+
name|MonitorUtils
operator|.
name|millisToHumanTime
argument_list|(
name|reportInterval
argument_list|)
operator|+
literal|", probe-timeout="
operator|+
name|timeoutToStr
argument_list|(
name|probeTimeout
argument_list|)
operator|+
literal|", bootstrap-timeout="
operator|+
name|timeoutToStr
argument_list|(
name|bootstrapTimeout
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting reporting"
operator|+
literal|" to "
operator|+
name|reporter
operator|+
name|description
argument_list|)
expr_stmt|;
return|return
name|reporter
operator|.
name|commence
argument_list|(
name|name
argument_list|,
name|description
argument_list|)
return|;
block|}
DECL|method|timeoutToStr (int timeout)
specifier|private
name|String
name|timeoutToStr
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
return|return
name|timeout
operator|>=
literal|0
condition|?
name|MonitorUtils
operator|.
name|millisToHumanTime
argument_list|(
name|timeout
argument_list|)
else|:
literal|"not set"
return|;
block|}
DECL|method|startWorker ()
specifier|private
name|void
name|startWorker
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Starting reporting worker thread "
argument_list|)
expr_stmt|;
name|workerThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|workerThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * This exits the process cleanly    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Stopping reporting"
argument_list|)
expr_stmt|;
name|mustExit
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|worker
operator|!=
literal|null
condition|)
block|{
name|worker
operator|.
name|setMustExit
argument_list|()
expr_stmt|;
name|workerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reporter
operator|!=
literal|null
condition|)
block|{
name|reporter
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|probeFailure (ProbeFailedException exception)
specifier|public
name|void
name|probeFailure
parameter_list|(
name|ProbeFailedException
name|exception
parameter_list|)
block|{
name|reporter
operator|.
name|probeFailure
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|probeProcessStateChange (ProbePhase probePhase)
specifier|public
name|void
name|probeProcessStateChange
parameter_list|(
name|ProbePhase
name|probePhase
parameter_list|)
block|{
name|reporter
operator|.
name|probeProcessStateChange
argument_list|(
name|probePhase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|probeBooted (ProbeStatus status)
specifier|public
name|void
name|probeBooted
parameter_list|(
name|ProbeStatus
name|status
parameter_list|)
block|{
name|reporter
operator|.
name|probeBooted
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
DECL|method|now ()
specifier|private
name|long
name|now
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|probeResult (ProbePhase phase, ProbeStatus status)
specifier|public
name|void
name|probeResult
parameter_list|(
name|ProbePhase
name|phase
parameter_list|,
name|ProbeStatus
name|status
parameter_list|)
block|{
name|reporter
operator|.
name|probeResult
argument_list|(
name|phase
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commence (String n, String description)
specifier|public
name|boolean
name|commence
parameter_list|(
name|String
name|n
parameter_list|,
name|String
name|description
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|unregister ()
specifier|public
name|void
name|unregister
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|heartbeat (ProbeStatus status)
specifier|public
name|void
name|heartbeat
parameter_list|(
name|ProbeStatus
name|status
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|probeTimedOut (ProbePhase currentPhase, Probe probe, ProbeStatus lastStatus, long currentTime)
specifier|public
name|void
name|probeTimedOut
parameter_list|(
name|ProbePhase
name|currentPhase
parameter_list|,
name|Probe
name|probe
parameter_list|,
name|ProbeStatus
name|lastStatus
parameter_list|,
name|long
name|currentTime
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|liveProbeCycleCompleted ()
specifier|public
name|void
name|liveProbeCycleCompleted
parameter_list|()
block|{
comment|//delegate to the reporter
name|reporter
operator|.
name|liveProbeCycleCompleted
argument_list|()
expr_stmt|;
block|}
comment|/**    * The reporting loop    */
DECL|method|reportingLoop ()
name|void
name|reportingLoop
parameter_list|()
block|{
while|while
condition|(
operator|!
name|mustExit
condition|)
block|{
try|try
block|{
name|ProbeStatus
name|workerStatus
init|=
name|worker
operator|.
name|getLastStatus
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|now
argument_list|()
decl_stmt|;
name|long
name|lastStatusIssued
init|=
name|workerStatus
operator|.
name|getTimestamp
argument_list|()
decl_stmt|;
name|long
name|timeSinceLastStatusIssued
init|=
name|now
operator|-
name|lastStatusIssued
decl_stmt|;
comment|//two actions can occur here: a heartbeat is issued or a timeout reported.
comment|//this flag decides which
name|boolean
name|heartbeat
decl_stmt|;
comment|//based on phase, decide whether to heartbeat or timeout
name|ProbePhase
name|probePhase
init|=
name|worker
operator|.
name|getProbePhase
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|probePhase
condition|)
block|{
case|case
name|DEPENDENCY_CHECKING
case|:
comment|//no timeouts in dependency phase
name|heartbeat
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|BOOTSTRAPPING
case|:
comment|//the timeout here is fairly straightforward: heartbeats are
comment|//raised while the worker hasn't timed out
name|heartbeat
operator|=
name|bootstrapTimeout
operator|<
literal|0
operator|||
name|timeSinceLastStatusIssued
operator|<
name|bootstrapTimeout
expr_stmt|;
break|break;
case|case
name|LIVE
case|:
comment|//use the probe timeout interval between the current time
comment|//and the time the last status event was received.
name|heartbeat
operator|=
name|timeSinceLastStatusIssued
operator|<
name|probeTimeout
expr_stmt|;
break|break;
case|case
name|INIT
case|:
case|case
name|TERMINATING
case|:
default|default:
comment|//send a heartbeat, because this isn't the time to be failing
name|heartbeat
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|heartbeat
condition|)
block|{
comment|//a heartbeat is sent to the reporter
name|reporter
operator|.
name|heartbeat
argument_list|(
name|workerStatus
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//no response from the worker -it is hung.
name|reporter
operator|.
name|probeTimedOut
argument_list|(
name|probePhase
argument_list|,
name|worker
operator|.
name|getCurrentProbe
argument_list|()
argument_list|,
name|workerStatus
argument_list|,
name|now
argument_list|)
expr_stmt|;
block|}
comment|//now sleep
name|Thread
operator|.
name|sleep
argument_list|(
name|reportInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//interrupted -always exit the loop.
break|break;
block|}
block|}
comment|//this point is reached if and only if a clean exit was requested or something failed.
block|}
comment|/**    * This can be run in a separate thread, or it can be run directly from the caller.    * Test runs do the latter, HAM runs multiple reporting threads.    */
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
name|startWorker
argument_list|()
expr_stmt|;
name|reportingLoop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failure in the reporting loop: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//rethrow so that inline code can pick it up (e.g. test runs)
throw|throw
name|e
throw|;
block|}
block|}
block|}
end_class

end_unit

