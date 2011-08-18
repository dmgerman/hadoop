begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.test.system
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|test
operator|.
name|system
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
name|TaskID
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
name|test
operator|.
name|system
operator|.
name|ControlAction
import|;
end_import

begin_comment
comment|/**  * Control Action which signals a controlled task to proceed to completion.<br/>  */
end_comment

begin_class
DECL|class|FinishTaskControlAction
specifier|public
class|class
name|FinishTaskControlAction
extends|extends
name|ControlAction
argument_list|<
name|TaskID
argument_list|>
block|{
DECL|field|ENABLE_CONTROLLED_TASK_COMPLETION
specifier|private
specifier|static
specifier|final
name|String
name|ENABLE_CONTROLLED_TASK_COMPLETION
init|=
literal|"test.system.enabled.task.completion.control"
decl_stmt|;
comment|/**    * Create a default control action.<br/>    *     */
DECL|method|FinishTaskControlAction ()
specifier|public
name|FinishTaskControlAction
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|TaskID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a control action specific to a particular task.<br/>    *     * @param id    *          of the task.    */
DECL|method|FinishTaskControlAction (TaskID id)
specifier|public
name|FinishTaskControlAction
parameter_list|(
name|TaskID
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets up the job to be controlled using the finish task control action.     *<br/>    *     * @param conf    *          configuration to be used submit the job.    */
DECL|method|configureControlActionForJob (Configuration conf)
specifier|public
specifier|static
name|void
name|configureControlActionForJob
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|ENABLE_CONTROLLED_TASK_COMPLETION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks if the control action is enabled in the passed configuration.<br/>    * @param conf configuration    * @return true if action is enabled.    */
DECL|method|isControlActionEnabled (Configuration conf)
specifier|public
specifier|static
name|boolean
name|isControlActionEnabled
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|ENABLE_CONTROLLED_TASK_COMPLETION
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
end_class

end_unit

