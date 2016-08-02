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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
import|;
end_import

begin_comment
comment|/**  * Scheduled executor or subclass thereof  * @param<E> scheduled executor service type  */
end_comment

begin_class
DECL|class|WorkflowScheduledExecutorService
specifier|public
class|class
name|WorkflowScheduledExecutorService
parameter_list|<
name|E
extends|extends
name|ScheduledExecutorService
parameter_list|>
extends|extends
name|WorkflowExecutorService
argument_list|<
name|E
argument_list|>
block|{
DECL|method|WorkflowScheduledExecutorService (String name)
specifier|public
name|WorkflowScheduledExecutorService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|WorkflowScheduledExecutorService (String name, E executor)
specifier|public
name|WorkflowScheduledExecutorService
parameter_list|(
name|String
name|name
parameter_list|,
name|E
name|executor
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|executor
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

