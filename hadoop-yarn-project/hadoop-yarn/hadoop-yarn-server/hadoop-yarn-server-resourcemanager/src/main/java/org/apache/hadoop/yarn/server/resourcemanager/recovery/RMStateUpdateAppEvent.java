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
name|records
operator|.
name|ApplicationStateData
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|SettableFuture
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
name|ApplicationStateData
name|appState
decl_stmt|;
comment|// After application state is updated in state store,
comment|// should notify back to application or not
DECL|field|notifyApplication
specifier|private
name|boolean
name|notifyApplication
decl_stmt|;
DECL|field|future
specifier|private
name|SettableFuture
argument_list|<
name|Object
argument_list|>
name|future
decl_stmt|;
DECL|method|RMStateUpdateAppEvent (ApplicationStateData appState)
specifier|public
name|RMStateUpdateAppEvent
parameter_list|(
name|ApplicationStateData
name|appState
parameter_list|)
block|{
name|this
argument_list|(
name|appState
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|RMStateUpdateAppEvent (ApplicationStateData appState, boolean notifyApplication)
specifier|public
name|RMStateUpdateAppEvent
parameter_list|(
name|ApplicationStateData
name|appState
parameter_list|,
name|boolean
name|notifyApplication
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
name|this
operator|.
name|notifyApplication
operator|=
name|notifyApplication
expr_stmt|;
name|this
operator|.
name|future
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|RMStateUpdateAppEvent (ApplicationStateData appState, boolean notifyApp, SettableFuture<Object> future)
specifier|public
name|RMStateUpdateAppEvent
parameter_list|(
name|ApplicationStateData
name|appState
parameter_list|,
name|boolean
name|notifyApp
parameter_list|,
name|SettableFuture
argument_list|<
name|Object
argument_list|>
name|future
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
name|this
operator|.
name|notifyApplication
operator|=
name|notifyApp
expr_stmt|;
name|this
operator|.
name|future
operator|=
name|future
expr_stmt|;
block|}
DECL|method|getAppState ()
specifier|public
name|ApplicationStateData
name|getAppState
parameter_list|()
block|{
return|return
name|appState
return|;
block|}
DECL|method|isNotifyApplication ()
specifier|public
name|boolean
name|isNotifyApplication
parameter_list|()
block|{
return|return
name|notifyApplication
return|;
block|}
DECL|method|getResult ()
specifier|public
name|SettableFuture
argument_list|<
name|Object
argument_list|>
name|getResult
parameter_list|()
block|{
return|return
name|future
return|;
block|}
block|}
end_class

end_unit

