begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.metrics
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
name|metrics
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ipc
operator|.
name|CallerContext
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
name|ApplicationId
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
name|Priority
import|;
end_import

begin_class
DECL|class|ApplicationCreatedEvent
specifier|public
class|class
name|ApplicationCreatedEvent
extends|extends
name|SystemMetricsEvent
block|{
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|queue
specifier|private
name|String
name|queue
decl_stmt|;
DECL|field|submittedTime
specifier|private
name|long
name|submittedTime
decl_stmt|;
DECL|field|appTags
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|appTags
decl_stmt|;
DECL|field|unmanagedApplication
specifier|private
name|boolean
name|unmanagedApplication
decl_stmt|;
DECL|field|applicationPriority
specifier|private
name|Priority
name|applicationPriority
decl_stmt|;
DECL|field|appNodeLabelsExpression
specifier|private
name|String
name|appNodeLabelsExpression
decl_stmt|;
DECL|field|amNodeLabelsExpression
specifier|private
name|String
name|amNodeLabelsExpression
decl_stmt|;
DECL|field|callerContext
specifier|private
specifier|final
name|CallerContext
name|callerContext
decl_stmt|;
DECL|method|ApplicationCreatedEvent (ApplicationId appId, String name, String type, String user, String queue, long submittedTime, long createdTime, Set<String> appTags, boolean unmanagedApplication, Priority applicationPriority, String appNodeLabelsExpression, String amNodeLabelsExpression, CallerContext callerContext)
specifier|public
name|ApplicationCreatedEvent
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|,
name|long
name|submittedTime
parameter_list|,
name|long
name|createdTime
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|appTags
parameter_list|,
name|boolean
name|unmanagedApplication
parameter_list|,
name|Priority
name|applicationPriority
parameter_list|,
name|String
name|appNodeLabelsExpression
parameter_list|,
name|String
name|amNodeLabelsExpression
parameter_list|,
name|CallerContext
name|callerContext
parameter_list|)
block|{
name|super
argument_list|(
name|SystemMetricsEventType
operator|.
name|APP_CREATED
argument_list|,
name|createdTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|submittedTime
operator|=
name|submittedTime
expr_stmt|;
name|this
operator|.
name|appTags
operator|=
name|appTags
expr_stmt|;
name|this
operator|.
name|unmanagedApplication
operator|=
name|unmanagedApplication
expr_stmt|;
name|this
operator|.
name|applicationPriority
operator|=
name|applicationPriority
expr_stmt|;
name|this
operator|.
name|appNodeLabelsExpression
operator|=
name|appNodeLabelsExpression
expr_stmt|;
name|this
operator|.
name|amNodeLabelsExpression
operator|=
name|amNodeLabelsExpression
expr_stmt|;
name|this
operator|.
name|callerContext
operator|=
name|callerContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|appId
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
DECL|method|getApplicationName ()
specifier|public
name|String
name|getApplicationName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getApplicationType ()
specifier|public
name|String
name|getApplicationType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
DECL|method|getSubmittedTime ()
specifier|public
name|long
name|getSubmittedTime
parameter_list|()
block|{
return|return
name|submittedTime
return|;
block|}
DECL|method|getAppTags ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAppTags
parameter_list|()
block|{
return|return
name|appTags
return|;
block|}
DECL|method|isUnmanagedApp ()
specifier|public
name|boolean
name|isUnmanagedApp
parameter_list|()
block|{
return|return
name|unmanagedApplication
return|;
block|}
DECL|method|getApplicationPriority ()
specifier|public
name|Priority
name|getApplicationPriority
parameter_list|()
block|{
return|return
name|applicationPriority
return|;
block|}
DECL|method|getAppNodeLabelsExpression ()
specifier|public
name|String
name|getAppNodeLabelsExpression
parameter_list|()
block|{
return|return
name|appNodeLabelsExpression
return|;
block|}
DECL|method|getAmNodeLabelsExpression ()
specifier|public
name|String
name|getAmNodeLabelsExpression
parameter_list|()
block|{
return|return
name|amNodeLabelsExpression
return|;
block|}
DECL|method|getCallerContext ()
specifier|public
name|CallerContext
name|getCallerContext
parameter_list|()
block|{
return|return
name|callerContext
return|;
block|}
block|}
end_class

end_unit

