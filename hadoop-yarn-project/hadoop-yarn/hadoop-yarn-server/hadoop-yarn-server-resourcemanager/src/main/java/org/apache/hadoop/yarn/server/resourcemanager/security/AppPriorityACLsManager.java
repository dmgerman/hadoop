begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.security
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
name|security
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|conf
operator|.
name|Configuration
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
name|security
operator|.
name|UserGroupInformation
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
name|conf
operator|.
name|YarnConfiguration
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
name|scheduler
operator|.
name|capacity
operator|.
name|AppPriorityACLGroup
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ConcurrentHashMap
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
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/**  *  * Manager class to store and check permission for Priority ACLs.  */
end_comment

begin_class
DECL|class|AppPriorityACLsManager
specifier|public
class|class
name|AppPriorityACLsManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AppPriorityACLsManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/*    * An internal class to store ACLs specific to each priority. This will be    * used to read and process acl's during app submission time as well.    */
DECL|class|PriorityACL
specifier|private
specifier|static
class|class
name|PriorityACL
block|{
DECL|field|priority
specifier|private
name|Priority
name|priority
decl_stmt|;
DECL|field|defaultPriority
specifier|private
name|Priority
name|defaultPriority
decl_stmt|;
DECL|field|acl
specifier|private
name|AccessControlList
name|acl
decl_stmt|;
DECL|method|PriorityACL (Priority priority, Priority defaultPriority, AccessControlList acl)
name|PriorityACL
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|Priority
name|defaultPriority
parameter_list|,
name|AccessControlList
name|acl
parameter_list|)
block|{
name|this
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|this
operator|.
name|setDefaultPriority
argument_list|(
name|defaultPriority
argument_list|)
expr_stmt|;
name|this
operator|.
name|setAcl
argument_list|(
name|acl
argument_list|)
expr_stmt|;
block|}
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
DECL|method|setPriority (Priority maxPriority)
specifier|public
name|void
name|setPriority
parameter_list|(
name|Priority
name|maxPriority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|maxPriority
expr_stmt|;
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
DECL|method|getAcl ()
specifier|public
name|AccessControlList
name|getAcl
parameter_list|()
block|{
return|return
name|acl
return|;
block|}
DECL|method|setAcl (AccessControlList acl)
specifier|public
name|void
name|setAcl
parameter_list|(
name|AccessControlList
name|acl
parameter_list|)
block|{
name|this
operator|.
name|acl
operator|=
name|acl
expr_stmt|;
block|}
block|}
DECL|field|isACLsEnable
specifier|private
name|boolean
name|isACLsEnable
decl_stmt|;
DECL|field|allAcls
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PriorityACL
argument_list|>
argument_list|>
name|allAcls
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|AppPriorityACLsManager (Configuration conf)
specifier|public
name|AppPriorityACLsManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|isACLsEnable
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_YARN_ACL_ENABLE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clear priority acl during refresh.    *    * @param queueName    *          Queue Name    */
DECL|method|clearPriorityACLs (String queueName)
specifier|public
name|void
name|clearPriorityACLs
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|allAcls
operator|.
name|remove
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Each Queue could have configured with different priority acl's groups. This    * method helps to store each such ACL list against queue.    *    * @param priorityACLGroups    *          List of Priority ACL Groups.    * @param queueName    *          Queue Name associate with priority acl groups.    */
DECL|method|addPrioirityACLs (List<AppPriorityACLGroup> priorityACLGroups, String queueName)
specifier|public
name|void
name|addPrioirityACLs
parameter_list|(
name|List
argument_list|<
name|AppPriorityACLGroup
argument_list|>
name|priorityACLGroups
parameter_list|,
name|String
name|queueName
parameter_list|)
block|{
name|List
argument_list|<
name|PriorityACL
argument_list|>
name|priorityACL
init|=
name|allAcls
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|priorityACL
condition|)
block|{
name|priorityACL
operator|=
operator|new
name|ArrayList
argument_list|<
name|PriorityACL
argument_list|>
argument_list|()
expr_stmt|;
name|allAcls
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|priorityACL
argument_list|)
expr_stmt|;
block|}
comment|// Ensure lowest priority PriorityACLGroup comes first in the list.
name|Collections
operator|.
name|sort
argument_list|(
name|priorityACLGroups
argument_list|)
expr_stmt|;
for|for
control|(
name|AppPriorityACLGroup
name|priorityACLGroup
range|:
name|priorityACLGroups
control|)
block|{
name|priorityACL
operator|.
name|add
argument_list|(
operator|new
name|PriorityACL
argument_list|(
name|priorityACLGroup
operator|.
name|getMaxPriority
argument_list|()
argument_list|,
name|priorityACLGroup
operator|.
name|getDefaultPriority
argument_list|()
argument_list|,
name|priorityACLGroup
operator|.
name|getACLList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Priority ACL group added: max-priority - "
operator|+
name|priorityACLGroup
operator|.
name|getMaxPriority
argument_list|()
operator|+
literal|"default-priority - "
operator|+
name|priorityACLGroup
operator|.
name|getDefaultPriority
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Priority based checkAccess to ensure that given user has enough permission    * to submit application at a given priority level.    *    * @param callerUGI    *          User who submits the application.    * @param queueName    *          Queue to which application is submitted.    * @param submittedPriority    *          priority of the application.    * @return True or False to indicate whether application can be submitted at    *         submitted priority level or not.    */
DECL|method|checkAccess (UserGroupInformation callerUGI, String queueName, Priority submittedPriority)
specifier|public
name|boolean
name|checkAccess
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|String
name|queueName
parameter_list|,
name|Priority
name|submittedPriority
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isACLsEnable
condition|)
block|{
return|return
literal|true
return|;
block|}
name|List
argument_list|<
name|PriorityACL
argument_list|>
name|acls
init|=
name|allAcls
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
name|acls
operator|==
literal|null
operator|||
name|acls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|PriorityACL
name|approvedPriorityACL
init|=
name|getMappedPriorityAclForUGI
argument_list|(
name|acls
argument_list|,
name|callerUGI
argument_list|,
name|submittedPriority
argument_list|)
decl_stmt|;
if|if
condition|(
name|approvedPriorityACL
operator|==
literal|null
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
comment|/**    * If an application is submitted without any priority, and submitted user has    * a default priority, this method helps to update this default priority as    * app's priority.    *    * @param queueName    *          Submitted queue    * @param user    *          User who submitted this application    * @return Default priority associated with given user.    */
DECL|method|getDefaultPriority (String queueName, UserGroupInformation user)
specifier|public
name|Priority
name|getDefaultPriority
parameter_list|(
name|String
name|queueName
parameter_list|,
name|UserGroupInformation
name|user
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isACLsEnable
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|PriorityACL
argument_list|>
name|acls
init|=
name|allAcls
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
name|acls
operator|==
literal|null
operator|||
name|acls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PriorityACL
name|approvedPriorityACL
init|=
name|getMappedPriorityAclForUGI
argument_list|(
name|acls
argument_list|,
name|user
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|approvedPriorityACL
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Priority
name|defaultPriority
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
name|approvedPriorityACL
operator|.
name|getDefaultPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|defaultPriority
return|;
block|}
DECL|method|getMappedPriorityAclForUGI (List<PriorityACL> acls , UserGroupInformation user, Priority submittedPriority)
specifier|private
name|PriorityACL
name|getMappedPriorityAclForUGI
parameter_list|(
name|List
argument_list|<
name|PriorityACL
argument_list|>
name|acls
parameter_list|,
name|UserGroupInformation
name|user
parameter_list|,
name|Priority
name|submittedPriority
parameter_list|)
block|{
comment|// Iterate through all configured ACLs starting from lower priority.
comment|// If user is found corresponding to a configured priority, then store
comment|// that entry. if failed, continue iterate through whole acl list.
name|PriorityACL
name|selectedAcl
init|=
literal|null
decl_stmt|;
for|for
control|(
name|PriorityACL
name|entry
range|:
name|acls
control|)
block|{
name|AccessControlList
name|list
init|=
name|entry
operator|.
name|getAcl
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|isUserAllowed
argument_list|(
name|user
argument_list|)
condition|)
block|{
name|selectedAcl
operator|=
name|entry
expr_stmt|;
comment|// If submittedPriority is passed through the argument, also check
comment|// whether submittedPriority is under max-priority of each ACL group.
if|if
condition|(
name|submittedPriority
operator|!=
literal|null
condition|)
block|{
name|selectedAcl
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|submittedPriority
operator|.
name|getPriority
argument_list|()
operator|<=
name|entry
operator|.
name|getPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
condition|)
block|{
return|return
name|entry
return|;
block|}
block|}
block|}
block|}
return|return
name|selectedAcl
return|;
block|}
block|}
end_class

end_unit

