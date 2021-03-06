begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|capacity
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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

begin_comment
comment|/**  * PriorityACLGroup will hold all ACL related information per priority.  *  */
end_comment

begin_class
DECL|class|AppPriorityACLGroup
specifier|public
class|class
name|AppPriorityACLGroup
implements|implements
name|Comparable
argument_list|<
name|AppPriorityACLGroup
argument_list|>
block|{
DECL|field|maxPriority
specifier|private
name|Priority
name|maxPriority
init|=
literal|null
decl_stmt|;
DECL|field|defaultPriority
specifier|private
name|Priority
name|defaultPriority
init|=
literal|null
decl_stmt|;
DECL|field|aclList
specifier|private
name|AccessControlList
name|aclList
init|=
literal|null
decl_stmt|;
DECL|method|AppPriorityACLGroup (Priority maxPriority, Priority defaultPriority, AccessControlList aclList)
specifier|public
name|AppPriorityACLGroup
parameter_list|(
name|Priority
name|maxPriority
parameter_list|,
name|Priority
name|defaultPriority
parameter_list|,
name|AccessControlList
name|aclList
parameter_list|)
block|{
name|this
operator|.
name|setMaxPriority
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
name|maxPriority
operator|.
name|getPriority
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|setDefaultPriority
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
name|defaultPriority
operator|.
name|getPriority
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|setACLList
argument_list|(
name|aclList
argument_list|)
expr_stmt|;
block|}
DECL|method|AppPriorityACLGroup ()
specifier|public
name|AppPriorityACLGroup
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|compareTo (AppPriorityACLGroup o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|AppPriorityACLGroup
name|o
parameter_list|)
block|{
return|return
name|getMaxPriority
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getMaxPriority
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AppPriorityACLGroup
name|other
init|=
operator|(
name|AppPriorityACLGroup
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|getMaxPriority
argument_list|()
operator|!=
name|other
operator|.
name|getMaxPriority
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getDefaultPriority
argument_list|()
operator|!=
name|other
operator|.
name|getDefaultPriority
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|517861
decl_stmt|;
name|int
name|result
init|=
literal|9511
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|getMaxPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|getDefaultPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|getMaxPriority ()
specifier|public
name|Priority
name|getMaxPriority
parameter_list|()
block|{
return|return
name|maxPriority
return|;
block|}
DECL|method|getDefaultPriority ()
specifier|public
name|Priority
name|getDefaultPriority
parameter_list|()
block|{
return|return
name|defaultPriority
return|;
block|}
DECL|method|getACLList ()
specifier|public
name|AccessControlList
name|getACLList
parameter_list|()
block|{
return|return
name|aclList
return|;
block|}
DECL|method|setMaxPriority (Priority maxPriority)
specifier|public
name|void
name|setMaxPriority
parameter_list|(
name|Priority
name|maxPriority
parameter_list|)
block|{
name|this
operator|.
name|maxPriority
operator|=
name|maxPriority
expr_stmt|;
block|}
DECL|method|setDefaultPriority (Priority defaultPriority)
specifier|public
name|void
name|setDefaultPriority
parameter_list|(
name|Priority
name|defaultPriority
parameter_list|)
block|{
name|this
operator|.
name|defaultPriority
operator|=
name|defaultPriority
expr_stmt|;
block|}
DECL|method|setACLList (AccessControlList accessControlList)
specifier|public
name|void
name|setACLList
parameter_list|(
name|AccessControlList
name|accessControlList
parameter_list|)
block|{
name|this
operator|.
name|aclList
operator|=
name|accessControlList
expr_stmt|;
block|}
block|}
end_class

end_unit

