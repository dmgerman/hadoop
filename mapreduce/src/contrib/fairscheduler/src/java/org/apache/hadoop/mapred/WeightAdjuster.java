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
name|Configurable
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
name|mapreduce
operator|.
name|TaskType
import|;
end_import

begin_comment
comment|/**  * A pluggable object for altering the weights of jobs in the fair scheduler,  * which is used for example by {@link NewJobWeightBooster} to give higher  * weight to new jobs so that short jobs finish faster.  *   * May implement {@link Configurable} to access configuration parameters.  */
end_comment

begin_interface
DECL|interface|WeightAdjuster
specifier|public
interface|interface
name|WeightAdjuster
block|{
DECL|method|adjustWeight (JobInProgress job, TaskType taskType, double curWeight)
specifier|public
name|double
name|adjustWeight
parameter_list|(
name|JobInProgress
name|job
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|double
name|curWeight
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

