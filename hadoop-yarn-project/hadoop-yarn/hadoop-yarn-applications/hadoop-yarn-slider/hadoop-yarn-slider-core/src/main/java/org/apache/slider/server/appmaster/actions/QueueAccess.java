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

begin_comment
comment|/**  * Access for queue operations  */
end_comment

begin_interface
DECL|interface|QueueAccess
specifier|public
interface|interface
name|QueueAccess
block|{
comment|/**    * Put an action on the immediate queue -to be executed when the queue    * reaches it.    * @param action action to queue    */
DECL|method|put (AsyncAction action)
name|void
name|put
parameter_list|(
name|AsyncAction
name|action
parameter_list|)
function_decl|;
comment|/**    * Put a delayed action: this will only be added to the main queue    * after its action time has been reached    * @param action action to queue    */
DECL|method|schedule (AsyncAction action)
name|void
name|schedule
parameter_list|(
name|AsyncAction
name|action
parameter_list|)
function_decl|;
comment|/**    * Remove an action from the queues.    * @param action action to remove    * @return true if the action was removed    */
DECL|method|remove (AsyncAction action)
name|boolean
name|remove
parameter_list|(
name|AsyncAction
name|action
parameter_list|)
function_decl|;
comment|/**    * Add a named renewing action    * @param name name    * @param renewingAction wrapped action    */
DECL|method|renewing (String name, RenewingAction<? extends AsyncAction> renewingAction)
name|void
name|renewing
parameter_list|(
name|String
name|name
parameter_list|,
name|RenewingAction
argument_list|<
name|?
extends|extends
name|AsyncAction
argument_list|>
name|renewingAction
parameter_list|)
function_decl|;
comment|/**    * Look up a renewing action    * @param name name of the action    * @return the action or null if none was found    */
DECL|method|lookupRenewingAction (String name)
name|RenewingAction
argument_list|<
name|?
extends|extends
name|AsyncAction
argument_list|>
name|lookupRenewingAction
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Remove a renewing action    * @param name action name name of the action    * @return true if the action was found and removed.    */
DECL|method|removeRenewingAction (String name)
name|boolean
name|removeRenewingAction
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Look in the immediate queue for any actions of a specific attribute    */
DECL|method|hasQueuedActionWithAttribute (int attr)
name|boolean
name|hasQueuedActionWithAttribute
parameter_list|(
name|int
name|attr
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

