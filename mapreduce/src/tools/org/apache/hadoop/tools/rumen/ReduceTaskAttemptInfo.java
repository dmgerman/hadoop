begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
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
name|mapred
operator|.
name|TaskStatus
operator|.
name|State
import|;
end_import

begin_comment
comment|/**  * {@link ReduceTaskAttemptInfo} represents the information with regard to a  * reduce task attempt.  */
end_comment

begin_class
DECL|class|ReduceTaskAttemptInfo
specifier|public
class|class
name|ReduceTaskAttemptInfo
extends|extends
name|TaskAttemptInfo
block|{
DECL|field|shuffleTime
specifier|private
name|long
name|shuffleTime
decl_stmt|;
DECL|field|mergeTime
specifier|private
name|long
name|mergeTime
decl_stmt|;
DECL|field|reduceTime
specifier|private
name|long
name|reduceTime
decl_stmt|;
DECL|method|ReduceTaskAttemptInfo (State state, TaskInfo taskInfo, long shuffleTime, long mergeTime, long reduceTime)
specifier|public
name|ReduceTaskAttemptInfo
parameter_list|(
name|State
name|state
parameter_list|,
name|TaskInfo
name|taskInfo
parameter_list|,
name|long
name|shuffleTime
parameter_list|,
name|long
name|mergeTime
parameter_list|,
name|long
name|reduceTime
parameter_list|)
block|{
name|super
argument_list|(
name|state
argument_list|,
name|taskInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|shuffleTime
operator|=
name|shuffleTime
expr_stmt|;
name|this
operator|.
name|mergeTime
operator|=
name|mergeTime
expr_stmt|;
name|this
operator|.
name|reduceTime
operator|=
name|reduceTime
expr_stmt|;
block|}
comment|/**    * Get the runtime for the<b>reduce</b> phase of the reduce task-attempt.    *     * @return the runtime for the<b>reduce</b> phase of the reduce task-attempt    */
DECL|method|getReduceRuntime ()
specifier|public
name|long
name|getReduceRuntime
parameter_list|()
block|{
return|return
name|reduceTime
return|;
block|}
comment|/**    * Get the runtime for the<b>shuffle</b> phase of the reduce task-attempt.    *     * @return the runtime for the<b>shuffle</b> phase of the reduce task-attempt    */
DECL|method|getShuffleRuntime ()
specifier|public
name|long
name|getShuffleRuntime
parameter_list|()
block|{
return|return
name|shuffleTime
return|;
block|}
comment|/**    * Get the runtime for the<b>merge</b> phase of the reduce task-attempt    *     * @return the runtime for the<b>merge</b> phase of the reduce task-attempt    */
DECL|method|getMergeRuntime ()
specifier|public
name|long
name|getMergeRuntime
parameter_list|()
block|{
return|return
name|mergeTime
return|;
block|}
annotation|@
name|Override
DECL|method|getRuntime ()
specifier|public
name|long
name|getRuntime
parameter_list|()
block|{
return|return
operator|(
name|getShuffleRuntime
argument_list|()
operator|+
name|getMergeRuntime
argument_list|()
operator|+
name|getReduceRuntime
argument_list|()
operator|)
return|;
block|}
block|}
end_class

end_unit

