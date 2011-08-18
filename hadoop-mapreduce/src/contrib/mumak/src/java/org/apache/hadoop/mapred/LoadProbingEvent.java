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
comment|/**  * {@link LoadProbingEvent} is created by {@link SimulatorJobTracker} when the  * {@link SimulatorJobSubmissionPolicy} is STRESS. {@link SimulatorJobClient}  * picks up the event, and would check whether the system load is stressed. If  * not, it would submit the next job.  */
end_comment

begin_class
DECL|class|LoadProbingEvent
specifier|public
class|class
name|LoadProbingEvent
extends|extends
name|SimulatorEvent
block|{
DECL|method|LoadProbingEvent (SimulatorJobClient jc, long timestamp)
specifier|public
name|LoadProbingEvent
parameter_list|(
name|SimulatorJobClient
name|jc
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|super
argument_list|(
name|jc
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

