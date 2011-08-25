begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.event
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
name|scheduler
operator|.
name|event
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptState
import|;
end_import

begin_class
DECL|class|AppRemovedSchedulerEvent
specifier|public
class|class
name|AppRemovedSchedulerEvent
extends|extends
name|SchedulerEvent
block|{
DECL|field|applicationAttemptId
specifier|private
specifier|final
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
DECL|field|finalAttemptState
specifier|private
specifier|final
name|RMAppAttemptState
name|finalAttemptState
decl_stmt|;
DECL|method|AppRemovedSchedulerEvent (ApplicationAttemptId applicationAttemptId, RMAppAttemptState finalAttemptState)
specifier|public
name|AppRemovedSchedulerEvent
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|RMAppAttemptState
name|finalAttemptState
parameter_list|)
block|{
name|super
argument_list|(
name|SchedulerEventType
operator|.
name|APP_REMOVED
argument_list|)
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|applicationAttemptId
expr_stmt|;
name|this
operator|.
name|finalAttemptState
operator|=
name|finalAttemptState
expr_stmt|;
block|}
DECL|method|getApplicationAttemptID ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptID
parameter_list|()
block|{
return|return
name|this
operator|.
name|applicationAttemptId
return|;
block|}
DECL|method|getFinalAttemptState ()
specifier|public
name|RMAppAttemptState
name|getFinalAttemptState
parameter_list|()
block|{
return|return
name|this
operator|.
name|finalAttemptState
return|;
block|}
block|}
end_class

end_unit

