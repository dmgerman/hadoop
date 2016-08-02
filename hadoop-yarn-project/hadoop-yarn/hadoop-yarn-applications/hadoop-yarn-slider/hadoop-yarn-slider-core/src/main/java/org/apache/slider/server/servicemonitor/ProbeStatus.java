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
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * Status message of a probe. This is designed to be sent over the wire, though the exception  * Had better be unserializable at the far end if that is to work.  */
end_comment

begin_class
DECL|class|ProbeStatus
specifier|public
specifier|final
class|class
name|ProbeStatus
implements|implements
name|Serializable
block|{
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
DECL|field|timestampText
specifier|private
name|String
name|timestampText
decl_stmt|;
DECL|field|success
specifier|private
name|boolean
name|success
decl_stmt|;
DECL|field|realOutcome
specifier|private
name|boolean
name|realOutcome
decl_stmt|;
DECL|field|message
specifier|private
name|String
name|message
decl_stmt|;
DECL|field|thrown
specifier|private
name|Throwable
name|thrown
decl_stmt|;
DECL|field|originator
specifier|private
specifier|transient
name|Probe
name|originator
decl_stmt|;
DECL|field|probePhase
specifier|private
name|ProbePhase
name|probePhase
decl_stmt|;
DECL|method|ProbeStatus ()
specifier|public
name|ProbeStatus
parameter_list|()
block|{   }
DECL|method|ProbeStatus (long timestamp, String message, Throwable thrown)
specifier|public
name|ProbeStatus
parameter_list|(
name|long
name|timestamp
parameter_list|,
name|String
name|message
parameter_list|,
name|Throwable
name|thrown
parameter_list|)
block|{
name|this
operator|.
name|success
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|thrown
operator|=
name|thrown
expr_stmt|;
name|setTimestamp
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
block|}
DECL|method|ProbeStatus (long timestamp, String message)
specifier|public
name|ProbeStatus
parameter_list|(
name|long
name|timestamp
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|success
operator|=
literal|true
expr_stmt|;
name|setTimestamp
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|thrown
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
DECL|method|setTimestamp (long timestamp)
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|timestampText
operator|=
operator|new
name|Date
argument_list|(
name|timestamp
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|isSuccess ()
specifier|public
name|boolean
name|isSuccess
parameter_list|()
block|{
return|return
name|success
return|;
block|}
comment|/**    * Set both the success and the real outcome bits to the same value    * @param success the new value    */
DECL|method|setSuccess (boolean success)
specifier|public
name|void
name|setSuccess
parameter_list|(
name|boolean
name|success
parameter_list|)
block|{
name|this
operator|.
name|success
operator|=
name|success
expr_stmt|;
name|realOutcome
operator|=
name|success
expr_stmt|;
block|}
DECL|method|getTimestampText ()
specifier|public
name|String
name|getTimestampText
parameter_list|()
block|{
return|return
name|timestampText
return|;
block|}
DECL|method|getRealOutcome ()
specifier|public
name|boolean
name|getRealOutcome
parameter_list|()
block|{
return|return
name|realOutcome
return|;
block|}
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
DECL|method|setMessage (String message)
specifier|public
name|void
name|setMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
DECL|method|getThrown ()
specifier|public
name|Throwable
name|getThrown
parameter_list|()
block|{
return|return
name|thrown
return|;
block|}
DECL|method|setThrown (Throwable thrown)
specifier|public
name|void
name|setThrown
parameter_list|(
name|Throwable
name|thrown
parameter_list|)
block|{
name|this
operator|.
name|thrown
operator|=
name|thrown
expr_stmt|;
block|}
DECL|method|getProbePhase ()
specifier|public
name|ProbePhase
name|getProbePhase
parameter_list|()
block|{
return|return
name|probePhase
return|;
block|}
DECL|method|setProbePhase (ProbePhase probePhase)
specifier|public
name|void
name|setProbePhase
parameter_list|(
name|ProbePhase
name|probePhase
parameter_list|)
block|{
name|this
operator|.
name|probePhase
operator|=
name|probePhase
expr_stmt|;
block|}
comment|/**    * Get the probe that generated this result. May be null    * @return a possibly null reference to a probe    */
DECL|method|getOriginator ()
specifier|public
name|Probe
name|getOriginator
parameter_list|()
block|{
return|return
name|originator
return|;
block|}
comment|/**    * The probe has succeeded -capture the current timestamp, set    * success to true, and record any other data needed.    * @param probe probe    */
DECL|method|succeed (Probe probe)
specifier|public
name|void
name|succeed
parameter_list|(
name|Probe
name|probe
parameter_list|)
block|{
name|finish
argument_list|(
name|probe
argument_list|,
literal|true
argument_list|,
name|probe
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * A probe has failed either because the test returned false, or an exception    * was thrown. The {@link #success} field is set to false, any exception     * thrown is recorded.    * @param probe probe that failed    * @param thrown an exception that was thrown.    */
DECL|method|fail (Probe probe, Throwable thrown)
specifier|public
name|void
name|fail
parameter_list|(
name|Probe
name|probe
parameter_list|,
name|Throwable
name|thrown
parameter_list|)
block|{
name|finish
argument_list|(
name|probe
argument_list|,
literal|false
argument_list|,
literal|"Failure in "
operator|+
name|probe
argument_list|,
name|thrown
argument_list|)
expr_stmt|;
block|}
DECL|method|finish (Probe probe, boolean succeeded, String text, Throwable thrown)
specifier|public
name|void
name|finish
parameter_list|(
name|Probe
name|probe
parameter_list|,
name|boolean
name|succeeded
parameter_list|,
name|String
name|text
parameter_list|,
name|Throwable
name|thrown
parameter_list|)
block|{
name|setTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|setSuccess
argument_list|(
name|succeeded
argument_list|)
expr_stmt|;
name|originator
operator|=
name|probe
expr_stmt|;
name|message
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|thrown
operator|=
name|thrown
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|LogEntryBuilder
name|builder
init|=
operator|new
name|LogEntryBuilder
argument_list|(
literal|"Probe Status"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|elt
argument_list|(
literal|"time"
argument_list|,
name|timestampText
argument_list|)
operator|.
name|elt
argument_list|(
literal|"phase"
argument_list|,
name|probePhase
argument_list|)
operator|.
name|elt
argument_list|(
literal|"outcome"
argument_list|,
operator|(
name|success
condition|?
literal|"success"
else|:
literal|"failure"
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|success
operator|!=
name|realOutcome
condition|)
block|{
name|builder
operator|.
name|elt
argument_list|(
literal|"originaloutcome"
argument_list|,
operator|(
name|realOutcome
condition|?
literal|"success"
else|:
literal|"failure"
operator|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|elt
argument_list|(
literal|"message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|thrown
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|elt
argument_list|(
literal|"exception"
argument_list|,
name|thrown
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|inPhase (ProbePhase phase)
specifier|public
name|boolean
name|inPhase
parameter_list|(
name|ProbePhase
name|phase
parameter_list|)
block|{
return|return
name|getProbePhase
argument_list|()
operator|.
name|equals
argument_list|(
name|phase
argument_list|)
return|;
block|}
comment|/**    * Flip the success bit on while the real outcome bit is kept false    */
DECL|method|markAsSuccessful ()
specifier|public
name|void
name|markAsSuccessful
parameter_list|()
block|{
name|success
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

