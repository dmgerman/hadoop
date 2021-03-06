begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|util
operator|.
name|StringUtils
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
comment|/**  *  * PriorityACLConfiguration class is used to parse Application Priority ACL  * configuration from capcity-scheduler.xml  */
end_comment

begin_class
DECL|class|AppPriorityACLConfigurationParser
specifier|public
class|class
name|AppPriorityACLConfigurationParser
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
name|AppPriorityACLConfigurationParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|enum|AppPriorityACLKeyType
specifier|public
enum|enum
name|AppPriorityACLKeyType
block|{
DECL|enumConstant|USER
DECL|enumConstant|GROUP
DECL|enumConstant|MAX_PRIORITY
DECL|enumConstant|DEFAULT_PRIORITY
name|USER
argument_list|(
literal|1
argument_list|)
block|,
name|GROUP
argument_list|(
literal|2
argument_list|)
block|,
name|MAX_PRIORITY
argument_list|(
literal|3
argument_list|)
block|,
name|DEFAULT_PRIORITY
argument_list|(
literal|4
argument_list|)
block|;
DECL|field|id
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
DECL|method|AppPriorityACLKeyType (int id)
name|AppPriorityACLKeyType
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
block|}
DECL|field|PATTERN_FOR_PRIORITY_ACL
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN_FOR_PRIORITY_ACL
init|=
literal|"\\[([^\\]]+)"
decl_stmt|;
annotation|@
name|Private
DECL|field|ALL_ACL
specifier|public
specifier|static
specifier|final
name|String
name|ALL_ACL
init|=
literal|"*"
decl_stmt|;
annotation|@
name|Private
DECL|field|NONE_ACL
specifier|public
specifier|static
specifier|final
name|String
name|NONE_ACL
init|=
literal|" "
decl_stmt|;
DECL|method|getPriorityAcl (Priority clusterMaxPriority, String aclString)
specifier|public
name|List
argument_list|<
name|AppPriorityACLGroup
argument_list|>
name|getPriorityAcl
parameter_list|(
name|Priority
name|clusterMaxPriority
parameter_list|,
name|String
name|aclString
parameter_list|)
block|{
name|List
argument_list|<
name|AppPriorityACLGroup
argument_list|>
name|aclList
init|=
operator|new
name|ArrayList
argument_list|<
name|AppPriorityACLGroup
argument_list|>
argument_list|()
decl_stmt|;
name|Matcher
name|matcher
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|PATTERN_FOR_PRIORITY_ACL
argument_list|)
operator|.
name|matcher
argument_list|(
name|aclString
argument_list|)
decl_stmt|;
comment|/*      * Each ACL group will be separated by "[]". Syntax of each ACL group could      * be like below "user=b1,b2 group=g1 max-priority=a2 default-priority=a1"      * Ideally this means "for this given user/group, maximum possible priority      * is a2 and if the user has not specified any priority, then it is a1."      */
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
comment|// Get the first ACL sub-group.
name|String
name|aclSubGroup
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|aclSubGroup
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|/*        * Internal storage is PriorityACLGroup which stores each parsed priority        * ACLs group. This will help while looking for a user to priority mapping        * during app submission time. ACLs will be passed in below order only. 1.        * user/group 2. max-priority 3. default-priority        */
name|AppPriorityACLGroup
name|userPriorityACL
init|=
operator|new
name|AppPriorityACLGroup
argument_list|()
decl_stmt|;
comment|// userAndGroupName will hold user acl and group acl as interim storage
comment|// since both user/group acl comes with separate key value pairs.
name|List
argument_list|<
name|StringBuilder
argument_list|>
name|userAndGroupName
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|kvPair
range|:
name|aclSubGroup
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|" +"
argument_list|)
control|)
block|{
comment|/*          * There are 3 possible options for key here: 1. user/group 2.          * max-priority 3. default-priority          */
name|String
index|[]
name|splits
init|=
name|kvPair
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
comment|// Ensure that each ACL sub string is key value pair separated by '='.
if|if
condition|(
name|splits
operator|!=
literal|null
operator|&&
name|splits
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|parsePriorityACLType
argument_list|(
name|userPriorityACL
argument_list|,
name|splits
argument_list|,
name|userAndGroupName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If max_priority is higher to clusterMaxPriority, its better to
comment|// handle here.
if|if
condition|(
name|userPriorityACL
operator|.
name|getMaxPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
operator|>
name|clusterMaxPriority
operator|.
name|getPriority
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"ACL configuration for '"
operator|+
name|userPriorityACL
operator|.
name|getMaxPriority
argument_list|()
operator|+
literal|"' is greater that cluster max priority. Resetting ACLs to "
operator|+
name|clusterMaxPriority
argument_list|)
expr_stmt|;
name|userPriorityACL
operator|.
name|setMaxPriority
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
name|clusterMaxPriority
operator|.
name|getPriority
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|AccessControlList
name|acl
init|=
name|createACLStringForPriority
argument_list|(
name|userAndGroupName
argument_list|)
decl_stmt|;
name|userPriorityACL
operator|.
name|setACLList
argument_list|(
name|acl
argument_list|)
expr_stmt|;
name|aclList
operator|.
name|add
argument_list|(
name|userPriorityACL
argument_list|)
expr_stmt|;
block|}
return|return
name|aclList
return|;
block|}
comment|/*    * Parse different types of ACLs sub parts for on priority group and store in    * a map for later processing.    */
DECL|method|parsePriorityACLType (AppPriorityACLGroup userPriorityACL, String[] splits, List<StringBuilder> userAndGroupName)
specifier|private
name|void
name|parsePriorityACLType
parameter_list|(
name|AppPriorityACLGroup
name|userPriorityACL
parameter_list|,
name|String
index|[]
name|splits
parameter_list|,
name|List
argument_list|<
name|StringBuilder
argument_list|>
name|userAndGroupName
parameter_list|)
block|{
comment|// Here splits will have the key value pair at index 0 and 1 respectively.
comment|// To parse all keys, its better to convert to PriorityACLConfig enum.
name|AppPriorityACLKeyType
name|aclType
init|=
name|AppPriorityACLKeyType
operator|.
name|valueOf
argument_list|(
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|splits
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|aclType
condition|)
block|{
case|case
name|MAX_PRIORITY
case|:
name|userPriorityACL
operator|.
name|setMaxPriority
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|USER
case|:
name|userAndGroupName
operator|.
name|add
argument_list|(
name|getUserOrGroupACLStringFromConfig
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|GROUP
case|:
name|userAndGroupName
operator|.
name|add
argument_list|(
name|getUserOrGroupACLStringFromConfig
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DEFAULT_PRIORITY
case|:
name|int
name|defaultPriority
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|Priority
name|priority
init|=
operator|(
name|defaultPriority
operator|<
literal|0
operator|)
condition|?
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
else|:
name|Priority
operator|.
name|newInstance
argument_list|(
name|defaultPriority
argument_list|)
decl_stmt|;
name|userPriorityACL
operator|.
name|setDefaultPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
comment|/*    * This method will help to append different types of ACLs keys against one    * priority. For eg,USER will be appended with GROUP as "user2,user4 group1".    */
DECL|method|createACLStringForPriority ( List<StringBuilder> acls)
specifier|private
name|AccessControlList
name|createACLStringForPriority
parameter_list|(
name|List
argument_list|<
name|StringBuilder
argument_list|>
name|acls
parameter_list|)
block|{
name|String
name|finalACL
init|=
literal|""
decl_stmt|;
name|String
name|userACL
init|=
name|acls
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// If any of user/group is *, consider it as acceptable for all.
comment|// "user" is at index 0, and "group" is at index 1.
if|if
condition|(
name|userACL
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
name|ALL_ACL
argument_list|)
condition|)
block|{
name|finalACL
operator|=
name|ALL_ACL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|userACL
operator|.
name|equals
argument_list|(
name|NONE_ACL
argument_list|)
condition|)
block|{
name|finalACL
operator|=
name|NONE_ACL
expr_stmt|;
block|}
else|else
block|{
comment|// Get USER segment
if|if
condition|(
operator|!
name|userACL
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// skip last appended ","
name|finalACL
operator|=
name|acls
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|// Get GROUP segment if any
if|if
condition|(
name|acls
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|String
name|groupACL
init|=
name|acls
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|groupACL
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|finalACL
operator|=
name|finalACL
operator|+
literal|" "
operator|+
name|acls
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Here ACL will look like "user1,user2 group" in ideal cases.
return|return
operator|new
name|AccessControlList
argument_list|(
name|finalACL
operator|.
name|trim
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * This method will help to append user/group acl string against given    * priority. For example "user1,user2 group1,group2"    */
DECL|method|getUserOrGroupACLStringFromConfig (String value)
specifier|private
name|StringBuilder
name|getUserOrGroupACLStringFromConfig
parameter_list|(
name|String
name|value
parameter_list|)
block|{
comment|// ACL strings could be generate for USER or GRUOP.
comment|// aclList in map contains two entries. 1. USER, 2. GROUP.
name|StringBuilder
name|aclTypeName
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
name|ALL_ACL
argument_list|)
condition|)
block|{
name|aclTypeName
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|aclTypeName
operator|.
name|append
argument_list|(
name|ALL_ACL
argument_list|)
expr_stmt|;
return|return
name|aclTypeName
return|;
block|}
name|aclTypeName
operator|.
name|append
argument_list|(
name|value
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|aclTypeName
return|;
block|}
block|}
end_class

end_unit

