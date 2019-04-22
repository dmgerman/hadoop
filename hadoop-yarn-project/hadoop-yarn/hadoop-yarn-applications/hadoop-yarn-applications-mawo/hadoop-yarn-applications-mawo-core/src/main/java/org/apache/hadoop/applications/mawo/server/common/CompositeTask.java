begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.applications.mawo.server.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|applications
operator|.
name|mawo
operator|.
name|server
operator|.
name|common
package|;
end_package

begin_comment
comment|/**  * Composite Task is Task with multiple commands.  */
end_comment

begin_class
DECL|class|CompositeTask
specifier|public
class|class
name|CompositeTask
extends|extends
name|AbstractTask
block|{
comment|/**    * Composite Task initializer.    */
DECL|method|CompositeTask ()
specifier|public
name|CompositeTask
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|setTaskType
argument_list|(
name|TaskType
operator|.
name|COMPOSITE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Composite initializer for specific task.    * @param task : Task object    */
DECL|method|CompositeTask (final Task task)
specifier|public
name|CompositeTask
parameter_list|(
specifier|final
name|Task
name|task
parameter_list|)
block|{
name|super
argument_list|(
name|task
operator|.
name|getTaskId
argument_list|()
argument_list|,
name|task
operator|.
name|getEnvironment
argument_list|()
argument_list|,
name|task
operator|.
name|getTaskCmd
argument_list|()
argument_list|,
name|task
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setTaskType
argument_list|(
name|TaskType
operator|.
name|COMPOSITE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

