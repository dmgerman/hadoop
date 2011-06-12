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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
operator|.
name|JobStory
import|;
end_import

begin_comment
comment|/**  * {@link SimulatorEvent} for trigging the submission of a job to the job tracker.  */
end_comment

begin_class
DECL|class|JobSubmissionEvent
specifier|public
class|class
name|JobSubmissionEvent
extends|extends
name|SimulatorEvent
block|{
DECL|field|job
specifier|private
specifier|final
name|JobStory
name|job
decl_stmt|;
DECL|method|JobSubmissionEvent (SimulatorEventListener listener, long timestamp, JobStory job)
specifier|public
name|JobSubmissionEvent
parameter_list|(
name|SimulatorEventListener
name|listener
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|JobStory
name|job
parameter_list|)
block|{
name|super
argument_list|(
name|listener
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
name|this
operator|.
name|job
operator|=
name|job
expr_stmt|;
block|}
DECL|method|getJob ()
specifier|public
name|JobStory
name|getJob
parameter_list|()
block|{
return|return
name|job
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
literal|", jobID="
operator|+
name|job
operator|.
name|getJobID
argument_list|()
return|;
block|}
block|}
end_class

end_unit

