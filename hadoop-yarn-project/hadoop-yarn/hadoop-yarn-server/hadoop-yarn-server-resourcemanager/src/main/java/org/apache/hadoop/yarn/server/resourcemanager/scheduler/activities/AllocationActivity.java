begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.activities
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
name|activities
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/*  * It records an activity operation in allocation,  * which can be classified as queue, application or container activity.  * Other information include state, diagnostic, priority.  */
end_comment

begin_class
DECL|class|AllocationActivity
specifier|public
class|class
name|AllocationActivity
block|{
DECL|field|childName
specifier|private
name|String
name|childName
init|=
literal|null
decl_stmt|;
DECL|field|parentName
specifier|private
name|String
name|parentName
init|=
literal|null
decl_stmt|;
DECL|field|appPriority
specifier|private
name|String
name|appPriority
init|=
literal|null
decl_stmt|;
DECL|field|requestPriority
specifier|private
name|String
name|requestPriority
init|=
literal|null
decl_stmt|;
DECL|field|state
specifier|private
name|ActivityState
name|state
decl_stmt|;
DECL|field|diagnostic
specifier|private
name|String
name|diagnostic
init|=
literal|null
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AllocationActivity
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|AllocationActivity (String parentName, String queueName, String priority, ActivityState state, String diagnostic, String type)
specifier|public
name|AllocationActivity
parameter_list|(
name|String
name|parentName
parameter_list|,
name|String
name|queueName
parameter_list|,
name|String
name|priority
parameter_list|,
name|ActivityState
name|state
parameter_list|,
name|String
name|diagnostic
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|childName
operator|=
name|queueName
expr_stmt|;
name|this
operator|.
name|parentName
operator|=
name|parentName
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"app"
argument_list|)
condition|)
block|{
name|this
operator|.
name|appPriority
operator|=
name|priority
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"container"
argument_list|)
condition|)
block|{
name|this
operator|.
name|requestPriority
operator|=
name|priority
expr_stmt|;
block|}
block|}
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|diagnostic
operator|=
name|diagnostic
expr_stmt|;
block|}
DECL|method|createTreeNode ()
specifier|public
name|ActivityNode
name|createTreeNode
parameter_list|()
block|{
if|if
condition|(
name|appPriority
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ActivityNode
argument_list|(
name|this
operator|.
name|childName
argument_list|,
name|this
operator|.
name|parentName
argument_list|,
name|this
operator|.
name|appPriority
argument_list|,
name|this
operator|.
name|state
argument_list|,
name|this
operator|.
name|diagnostic
argument_list|,
literal|"app"
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|requestPriority
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ActivityNode
argument_list|(
name|this
operator|.
name|childName
argument_list|,
name|this
operator|.
name|parentName
argument_list|,
name|this
operator|.
name|requestPriority
argument_list|,
name|this
operator|.
name|state
argument_list|,
name|this
operator|.
name|diagnostic
argument_list|,
literal|"container"
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActivityNode
argument_list|(
name|this
operator|.
name|childName
argument_list|,
name|this
operator|.
name|parentName
argument_list|,
literal|null
argument_list|,
name|this
operator|.
name|state
argument_list|,
name|this
operator|.
name|diagnostic
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|childName
return|;
block|}
DECL|method|getState ()
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

