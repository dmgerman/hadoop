begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.actions
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|actions
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|SliderAppMaster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|AppState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|RoleInstance
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Notify the app master that it should register a component instance  * in the registry  * {@link SliderAppMaster#registerComponent(ContainerId)}  */
end_comment

begin_class
DECL|class|RegisterComponentInstance
specifier|public
class|class
name|RegisterComponentInstance
extends|extends
name|AsyncAction
block|{
DECL|field|containerId
specifier|public
specifier|final
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|roleInstance
specifier|public
specifier|final
name|RoleInstance
name|roleInstance
decl_stmt|;
DECL|method|RegisterComponentInstance (ContainerId containerId, RoleInstance roleInstance, long delay, TimeUnit timeUnit)
specifier|public
name|RegisterComponentInstance
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|RoleInstance
name|roleInstance
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
name|super
argument_list|(
literal|"RegisterComponentInstance :"
operator|+
name|containerId
argument_list|,
name|delay
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
name|this
operator|.
name|roleInstance
operator|=
name|roleInstance
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|containerId
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute (SliderAppMaster appMaster, QueueAccess queueService, AppState appState)
specifier|public
name|void
name|execute
parameter_list|(
name|SliderAppMaster
name|appMaster
parameter_list|,
name|QueueAccess
name|queueService
parameter_list|,
name|AppState
name|appState
parameter_list|)
throws|throws
name|Exception
block|{
name|appMaster
operator|.
name|registerComponent
argument_list|(
name|containerId
argument_list|,
name|roleInstance
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

