begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authorize
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|InterfaceStability
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|Writable
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

begin_comment
comment|/**  * Class representing a configured access control list.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AccessControlList
specifier|public
class|class
name|AccessControlList
implements|implements
name|Writable
block|{
comment|// Indicates an ACL string that represents access to all users
DECL|field|WILDCARD_ACL_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|WILDCARD_ACL_VALUE
init|=
literal|"*"
decl_stmt|;
DECL|field|INITIAL_CAPACITY
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_CAPACITY
init|=
literal|256
decl_stmt|;
comment|// Set of users who are granted access.
DECL|field|users
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|users
decl_stmt|;
comment|// Set of groups which are granted access
DECL|field|groups
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|groups
decl_stmt|;
comment|// Whether all users are granted access.
DECL|field|allAllowed
specifier|private
name|boolean
name|allAllowed
decl_stmt|;
comment|/**    * Construct a new ACL from a String representation of the same.    *     * The String is a a comma separated list of users and groups.    * The user list comes first and is separated by a space followed     * by the group list. For e.g. "user1,user2 group1,group2"    *     * @param aclString String representation of the ACL    */
DECL|method|AccessControlList (String aclString)
specifier|public
name|AccessControlList
parameter_list|(
name|String
name|aclString
parameter_list|)
block|{
name|buildACL
argument_list|(
name|aclString
argument_list|)
expr_stmt|;
block|}
comment|// build ACL from the given string
DECL|method|buildACL (String aclString)
specifier|private
name|void
name|buildACL
parameter_list|(
name|String
name|aclString
parameter_list|)
block|{
name|users
operator|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|groups
operator|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|isWildCardACLValue
argument_list|(
name|aclString
argument_list|)
condition|)
block|{
name|allAllowed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|allAllowed
operator|=
literal|false
expr_stmt|;
name|String
index|[]
name|userGroupStrings
init|=
name|aclString
operator|.
name|split
argument_list|(
literal|" "
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|userGroupStrings
operator|.
name|length
operator|>=
literal|1
condition|)
block|{
name|String
index|[]
name|usersStr
init|=
name|userGroupStrings
index|[
literal|0
index|]
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|usersStr
operator|.
name|length
operator|>=
literal|1
condition|)
block|{
name|addToSet
argument_list|(
name|users
argument_list|,
name|usersStr
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|userGroupStrings
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|String
index|[]
name|groupsStr
init|=
name|userGroupStrings
index|[
literal|1
index|]
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupsStr
operator|.
name|length
operator|>=
literal|1
condition|)
block|{
name|addToSet
argument_list|(
name|groups
argument_list|,
name|groupsStr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|isWildCardACLValue (String aclString)
specifier|private
name|boolean
name|isWildCardACLValue
parameter_list|(
name|String
name|aclString
parameter_list|)
block|{
if|if
condition|(
name|aclString
operator|.
name|contains
argument_list|(
name|WILDCARD_ACL_VALUE
argument_list|)
operator|&&
name|aclString
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
name|WILDCARD_ACL_VALUE
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|isAllAllowed ()
specifier|public
name|boolean
name|isAllAllowed
parameter_list|()
block|{
return|return
name|allAllowed
return|;
block|}
comment|/**    * Add user to the names of users allowed for this service.    *     * @param user    *          The user name    */
DECL|method|addUser (String user)
specifier|public
name|void
name|addUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
if|if
condition|(
name|isWildCardACLValue
argument_list|(
name|user
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"User "
operator|+
name|user
operator|+
literal|" can not be added"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isAllAllowed
argument_list|()
condition|)
block|{
name|users
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add group to the names of groups allowed for this service.    *     * @param group    *          The group name    */
DECL|method|addGroup (String group)
specifier|public
name|void
name|addGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
if|if
condition|(
name|isWildCardACLValue
argument_list|(
name|group
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Group "
operator|+
name|group
operator|+
literal|" can not be added"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isAllAllowed
argument_list|()
condition|)
block|{
name|groups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Remove user from the names of users allowed for this service.    *     * @param user    *          The user name    */
DECL|method|removeUser (String user)
specifier|public
name|void
name|removeUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
if|if
condition|(
name|isWildCardACLValue
argument_list|(
name|user
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"User "
operator|+
name|user
operator|+
literal|" can not be removed"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isAllAllowed
argument_list|()
condition|)
block|{
name|users
operator|.
name|remove
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Remove group from the names of groups allowed for this service.    *     * @param group    *          The group name    */
DECL|method|removeGroup (String group)
specifier|public
name|void
name|removeGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
if|if
condition|(
name|isWildCardACLValue
argument_list|(
name|group
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Group "
operator|+
name|group
operator|+
literal|" can not be removed"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isAllAllowed
argument_list|()
condition|)
block|{
name|groups
operator|.
name|remove
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the names of users allowed for this service.    * @return the set of user names. the set must not be modified.    */
DECL|method|getUsers ()
name|Set
argument_list|<
name|String
argument_list|>
name|getUsers
parameter_list|()
block|{
return|return
name|users
return|;
block|}
comment|/**    * Get the names of user groups allowed for this service.    * @return the set of group names. the set must not be modified.    */
DECL|method|getGroups ()
name|Set
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|()
block|{
return|return
name|groups
return|;
block|}
DECL|method|isUserAllowed (UserGroupInformation ugi)
specifier|public
name|boolean
name|isUserAllowed
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
if|if
condition|(
name|allAllowed
operator|||
name|users
operator|.
name|contains
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
for|for
control|(
name|String
name|group
range|:
name|ugi
operator|.
name|getGroupNames
argument_list|()
control|)
block|{
if|if
condition|(
name|groups
operator|.
name|contains
argument_list|(
name|group
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|addToSet (Set<String> set, String[] strings)
specifier|private
specifier|static
specifier|final
name|void
name|addToSet
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|set
parameter_list|,
name|String
index|[]
name|strings
parameter_list|)
block|{
for|for
control|(
name|String
name|s
range|:
name|strings
control|)
block|{
name|s
operator|=
name|s
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns descriptive way of users and groups that are part of this ACL.    * Use {@link #getAclString()} to get the exact String that can be given to    * the constructor of AccessControlList to create a new instance.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|str
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|allAllowed
condition|)
block|{
name|str
operator|=
literal|"All users are allowed"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|users
operator|.
name|isEmpty
argument_list|()
operator|&&
name|groups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|str
operator|=
literal|"No users are allowed"
expr_stmt|;
block|}
else|else
block|{
name|String
name|usersStr
init|=
literal|null
decl_stmt|;
name|String
name|groupsStr
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|users
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|usersStr
operator|=
name|users
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|groups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|groupsStr
operator|=
name|groups
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|users
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|groups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|str
operator|=
literal|"Users "
operator|+
name|usersStr
operator|+
literal|" and members of the groups "
operator|+
name|groupsStr
operator|+
literal|" are allowed"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|users
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|str
operator|=
literal|"Users "
operator|+
name|usersStr
operator|+
literal|" are allowed"
expr_stmt|;
block|}
else|else
block|{
comment|// users is empty array and groups is nonempty
name|str
operator|=
literal|"Members of the groups "
operator|+
name|groupsStr
operator|+
literal|" are allowed"
expr_stmt|;
block|}
block|}
return|return
name|str
return|;
block|}
comment|/**    * Returns the access control list as a String that can be used for building a    * new instance by sending it to the constructor of {@link AccessControlList}.    */
DECL|method|getAclString ()
specifier|public
name|String
name|getAclString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|INITIAL_CAPACITY
argument_list|)
decl_stmt|;
if|if
condition|(
name|allAllowed
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|getUsersString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getGroupsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Serializes the AccessControlList object    */
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|aclString
init|=
name|getAclString
argument_list|()
decl_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|aclString
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deserializes the AccessControlList object    */
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|aclString
init|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|buildACL
argument_list|(
name|aclString
argument_list|)
expr_stmt|;
block|}
comment|// Returns comma-separated concatenated single String of the set 'users'
DECL|method|getUsersString ()
specifier|private
name|String
name|getUsersString
parameter_list|()
block|{
return|return
name|getString
argument_list|(
name|users
argument_list|)
return|;
block|}
comment|// Returns comma-separated concatenated single String of the set 'groups'
DECL|method|getGroupsString ()
specifier|private
name|String
name|getGroupsString
parameter_list|()
block|{
return|return
name|getString
argument_list|(
name|groups
argument_list|)
return|;
block|}
comment|// Returns comma-separated concatenated single String of all strings of
comment|// the given set
DECL|method|getString (Set<String> strings)
specifier|private
name|String
name|getString
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|INITIAL_CAPACITY
argument_list|)
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|String
name|str
range|:
name|strings
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

