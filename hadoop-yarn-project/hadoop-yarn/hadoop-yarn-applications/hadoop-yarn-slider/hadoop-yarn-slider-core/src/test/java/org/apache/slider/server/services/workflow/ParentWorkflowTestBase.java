begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.services.workflow
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|workflow
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
name|service
operator|.
name|Service
import|;
end_import

begin_comment
comment|/**  * Extends {@link WorkflowServiceTestBase} with parent-specific operations  * and logic to build up and run the parent service  */
end_comment

begin_class
DECL|class|ParentWorkflowTestBase
specifier|public
specifier|abstract
class|class
name|ParentWorkflowTestBase
extends|extends
name|WorkflowServiceTestBase
block|{
comment|/**    * Wait a second for the service parent to stop    * @param parent the service to wait for    */
DECL|method|waitForParentToStop (ServiceParent parent)
specifier|protected
name|void
name|waitForParentToStop
parameter_list|(
name|ServiceParent
name|parent
parameter_list|)
block|{
name|waitForParentToStop
argument_list|(
name|parent
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Wait for the service parent to stop    * @param parent the service to wait for    * @param timeout time in milliseconds    */
DECL|method|waitForParentToStop (ServiceParent parent, int timeout)
specifier|protected
name|void
name|waitForParentToStop
parameter_list|(
name|ServiceParent
name|parent
parameter_list|,
name|int
name|timeout
parameter_list|)
block|{
name|boolean
name|stop
init|=
name|parent
operator|.
name|waitForServiceToStop
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stop
condition|)
block|{
name|logState
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Service failed to stop : after "
operator|+
name|timeout
operator|+
literal|" millis "
operator|+
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Subclasses are require to implement this and return an instance of a    * ServiceParent    * @param services a possibly empty list of services    * @return an inited -but -not-started- service parent instance    */
DECL|method|buildService (Service... services)
specifier|protected
specifier|abstract
name|ServiceParent
name|buildService
parameter_list|(
name|Service
modifier|...
name|services
parameter_list|)
function_decl|;
comment|/**    * Use {@link #buildService(Service...)} to create service and then start it    * @param services    * @return    */
DECL|method|startService (Service... services)
specifier|protected
name|ServiceParent
name|startService
parameter_list|(
name|Service
modifier|...
name|services
parameter_list|)
block|{
name|ServiceParent
name|parent
init|=
name|buildService
argument_list|(
name|services
argument_list|)
decl_stmt|;
comment|//expect service to start and stay started
name|parent
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|parent
return|;
block|}
block|}
end_class

end_unit

