begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|RMStateStore
operator|.
name|ApplicationState
import|;
end_import

begin_class
DECL|class|RMStateUpdateAppEvent
specifier|public
class|class
name|RMStateUpdateAppEvent
extends|extends
name|RMStateStoreEvent
block|{
DECL|field|appState
specifier|private
specifier|final
name|ApplicationState
name|appState
decl_stmt|;
DECL|method|RMStateUpdateAppEvent (ApplicationState appState)
specifier|public
name|RMStateUpdateAppEvent
parameter_list|(
name|ApplicationState
name|appState
parameter_list|)
block|{
name|super
argument_list|(
name|RMStateStoreEventType
operator|.
name|UPDATE_APP
argument_list|)
expr_stmt|;
name|this
operator|.
name|appState
operator|=
name|appState
expr_stmt|;
block|}
DECL|method|getAppState ()
specifier|public
name|ApplicationState
name|getAppState
parameter_list|()
block|{
return|return
name|appState
return|;
block|}
block|}
end_class

end_unit

