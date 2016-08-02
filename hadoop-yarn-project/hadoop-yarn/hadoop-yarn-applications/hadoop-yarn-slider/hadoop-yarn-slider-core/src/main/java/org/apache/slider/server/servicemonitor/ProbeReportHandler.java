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

begin_comment
comment|/**  * This interface is for use by the Poll Workers to send events to the reporters.  *  * It is up the reporters what to do with the specific events.  */
end_comment

begin_interface
DECL|interface|ProbeReportHandler
specifier|public
interface|interface
name|ProbeReportHandler
block|{
comment|/**    * The probe process has changed state.     * @param probePhase the new process phrase    */
DECL|method|probeProcessStateChange (ProbePhase probePhase)
name|void
name|probeProcessStateChange
parameter_list|(
name|ProbePhase
name|probePhase
parameter_list|)
function_decl|;
comment|/**    * Report a probe outcome    * @param phase the current phase of probing    * @param status the probe status    */
DECL|method|probeResult (ProbePhase phase, ProbeStatus status)
name|void
name|probeResult
parameter_list|(
name|ProbePhase
name|phase
parameter_list|,
name|ProbeStatus
name|status
parameter_list|)
function_decl|;
comment|/**    * A probe has failed    */
DECL|method|probeFailure (ProbeFailedException exception)
name|void
name|probeFailure
parameter_list|(
name|ProbeFailedException
name|exception
parameter_list|)
function_decl|;
comment|/**    * A probe has just booted    * @param status probe status    */
DECL|method|probeBooted (ProbeStatus status)
name|void
name|probeBooted
parameter_list|(
name|ProbeStatus
name|status
parameter_list|)
function_decl|;
DECL|method|commence (String name, String description)
name|boolean
name|commence
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|)
function_decl|;
DECL|method|unregister ()
name|void
name|unregister
parameter_list|()
function_decl|;
comment|/**    * A heartbeat event should be raised    * @param status the probe status    */
DECL|method|heartbeat (ProbeStatus status)
name|void
name|heartbeat
parameter_list|(
name|ProbeStatus
name|status
parameter_list|)
function_decl|;
comment|/**    * A probe has timed out    * @param currentPhase the current execution phase    * @param probe the probe that timed out    * @param lastStatus the last status that was successfully received -which is implicitly     * not the status of the timed out probe    * @param currentTime the current time    */
DECL|method|probeTimedOut (ProbePhase currentPhase, Probe probe, ProbeStatus lastStatus, long currentTime)
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
function_decl|;
comment|/**    * Event to say that the live probe cycle completed so the entire    * system can be considered functional.    */
DECL|method|liveProbeCycleCompleted ()
name|void
name|liveProbeCycleCompleted
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

