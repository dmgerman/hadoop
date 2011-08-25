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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Job submission policies. The set of policies is closed and encapsulated in  * {@link SimulatorJobSubmissionPolicy}. The handling of submission policies is  * embedded in the {@link SimulatorEngine} (through various events).  *   */
end_comment

begin_enum
DECL|enum|SimulatorJobSubmissionPolicy
specifier|public
enum|enum
name|SimulatorJobSubmissionPolicy
block|{
comment|/**    * replay the trace by following the job inter-arrival rate faithfully.    */
DECL|enumConstant|REPLAY
name|REPLAY
block|,
comment|/**    * ignore submission time, keep submitting jobs until the cluster is saturated.    */
DECL|enumConstant|STRESS
name|STRESS
block|,
comment|/**    * submitting jobs sequentially.    */
DECL|enumConstant|SERIAL
name|SERIAL
block|;
DECL|field|JOB_SUBMISSION_POLICY
specifier|public
specifier|static
specifier|final
name|String
name|JOB_SUBMISSION_POLICY
init|=
literal|"mumak.job-submission.policy"
decl_stmt|;
DECL|method|getPolicy (Configuration conf)
specifier|static
specifier|public
name|SimulatorJobSubmissionPolicy
name|getPolicy
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|policy
init|=
name|conf
operator|.
name|get
argument_list|(
name|JOB_SUBMISSION_POLICY
argument_list|,
name|REPLAY
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|valueOf
argument_list|(
name|policy
operator|.
name|toUpperCase
argument_list|()
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

