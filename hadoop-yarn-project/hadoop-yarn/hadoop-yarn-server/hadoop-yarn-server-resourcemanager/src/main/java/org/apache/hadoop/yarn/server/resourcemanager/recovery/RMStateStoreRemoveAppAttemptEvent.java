begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
import|;
end_import

begin_comment
comment|/**  * A event used to remove an attempt.  */
end_comment

begin_class
DECL|class|RMStateStoreRemoveAppAttemptEvent
specifier|public
class|class
name|RMStateStoreRemoveAppAttemptEvent
extends|extends
name|RMStateStoreEvent
block|{
DECL|field|applicationAttemptId
specifier|private
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
DECL|method|RMStateStoreRemoveAppAttemptEvent (ApplicationAttemptId applicationAttemptId)
name|RMStateStoreRemoveAppAttemptEvent
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
block|{
name|super
argument_list|(
name|RMStateStoreEventType
operator|.
name|REMOVE_APP_ATTEMPT
argument_list|)
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|applicationAttemptId
expr_stmt|;
block|}
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|applicationAttemptId
return|;
block|}
block|}
end_class

end_unit

