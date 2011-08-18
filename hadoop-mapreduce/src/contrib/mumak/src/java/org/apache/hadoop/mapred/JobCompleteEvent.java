begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_comment
comment|/**  * {@link JobCompleteEvent} is created by {@link SimulatorJobTracker} when a job  * is completed. {@link SimulatorJobClient} picks up the event, and mark the job  * as completed. When all jobs are completed, the simulation is terminated.  */
end_comment

begin_class
DECL|class|JobCompleteEvent
specifier|public
class|class
name|JobCompleteEvent
extends|extends
name|SimulatorEvent
block|{
DECL|field|engine
specifier|private
name|SimulatorEngine
name|engine
decl_stmt|;
DECL|field|jobStatus
specifier|private
name|JobStatus
name|jobStatus
decl_stmt|;
DECL|method|JobCompleteEvent (SimulatorJobClient jc, long timestamp, JobStatus jobStatus, SimulatorEngine engine)
specifier|public
name|JobCompleteEvent
parameter_list|(
name|SimulatorJobClient
name|jc
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|JobStatus
name|jobStatus
parameter_list|,
name|SimulatorEngine
name|engine
parameter_list|)
block|{
name|super
argument_list|(
name|jc
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
name|this
operator|.
name|engine
operator|=
name|engine
expr_stmt|;
name|this
operator|.
name|jobStatus
operator|=
name|jobStatus
expr_stmt|;
block|}
DECL|method|getEngine ()
specifier|public
name|SimulatorEngine
name|getEngine
parameter_list|()
block|{
return|return
name|engine
return|;
block|}
DECL|method|getJobStatus ()
specifier|public
name|JobStatus
name|getJobStatus
parameter_list|()
block|{
return|return
name|jobStatus
return|;
block|}
annotation|@
name|Override
DECL|method|realToString ()
specifier|protected
name|String
name|realToString
parameter_list|()
block|{
return|return
name|super
operator|.
name|realToString
argument_list|()
operator|+
literal|", status=("
operator|+
name|jobStatus
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

