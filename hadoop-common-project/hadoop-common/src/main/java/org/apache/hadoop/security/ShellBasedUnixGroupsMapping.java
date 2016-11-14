begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|StringTokenizer
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
name|TimeUnit
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
name|annotations
operator|.
name|VisibleForTesting
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
name|base
operator|.
name|Joiner
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
name|lang
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
name|conf
operator|.
name|Configured
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|Shell
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
name|Shell
operator|.
name|ExitCodeException
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
name|Shell
operator|.
name|ShellCommandExecutor
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

begin_comment
comment|/**  * A simple shell-based implementation of {@link GroupMappingServiceProvider}   * that exec's the<code>groups</code> shell command to fetch the group  * memberships of a given user.  */
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
DECL|class|ShellBasedUnixGroupsMapping
specifier|public
class|class
name|ShellBasedUnixGroupsMapping
extends|extends
name|Configured
implements|implements
name|GroupMappingServiceProvider
block|{
annotation|@
name|VisibleForTesting
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ShellBasedUnixGroupsMapping
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|timeout
specifier|private
name|long
name|timeout
init|=
literal|0L
decl_stmt|;
DECL|field|EMPTY_GROUPS
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|EMPTY_GROUPS
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|timeout
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_SHELL_COMMAND_TIMEOUT_SECS
argument_list|,
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_SHELL_COMMAND_TIMEOUT_SECS_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|PartialGroupNameException
specifier|private
specifier|static
class|class
name|PartialGroupNameException
extends|extends
name|IOException
block|{
DECL|method|PartialGroupNameException (String message)
specifier|public
name|PartialGroupNameException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|PartialGroupNameException (String message, Throwable err)
specifier|public
name|PartialGroupNameException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|err
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"PartialGroupNameException "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|super
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * Returns list of groups for a user    *    * @param userName get groups for this user    * @return list of groups for a given user    */
annotation|@
name|Override
DECL|method|getGroups (String userName)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getUnixGroups
argument_list|(
name|userName
argument_list|)
return|;
block|}
comment|/**    * Caches groups, no need to do that for this provider    */
annotation|@
name|Override
DECL|method|cacheGroupsRefresh ()
specifier|public
name|void
name|cacheGroupsRefresh
parameter_list|()
throws|throws
name|IOException
block|{
comment|// does nothing in this provider of user to groups mapping
block|}
comment|/**     * Adds groups to cache, no need to do that for this provider    *    * @param groups unused    */
annotation|@
name|Override
DECL|method|cacheGroupsAdd (List<String> groups)
specifier|public
name|void
name|cacheGroupsAdd
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|groups
parameter_list|)
throws|throws
name|IOException
block|{
comment|// does nothing in this provider of user to groups mapping
block|}
comment|/**    * Create a ShellCommandExecutor object using the user's name.    *    * @param userName user's name    * @return a ShellCommandExecutor object    */
DECL|method|createGroupExecutor (String userName)
specifier|protected
name|ShellCommandExecutor
name|createGroupExecutor
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
operator|new
name|ShellCommandExecutor
argument_list|(
name|getGroupsForUserCommand
argument_list|(
name|userName
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|timeout
argument_list|)
return|;
block|}
comment|/**    * Returns just the shell command to be used to fetch a user's groups list.    * This is mainly separate to make some tests easier.    * @param userName The username that needs to be passed into the command built    * @return An appropriate shell command with arguments    */
DECL|method|getGroupsForUserCommand (String userName)
specifier|protected
name|String
index|[]
name|getGroupsForUserCommand
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
name|Shell
operator|.
name|getGroupsForUserCommand
argument_list|(
name|userName
argument_list|)
return|;
block|}
comment|/**    * Create a ShellCommandExecutor object for fetch a user's group id list.    *    * @param userName the user's name    * @return a ShellCommandExecutor object    */
DECL|method|createGroupIDExecutor (String userName)
specifier|protected
name|ShellCommandExecutor
name|createGroupIDExecutor
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
operator|new
name|ShellCommandExecutor
argument_list|(
name|getGroupsIDForUserCommand
argument_list|(
name|userName
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|timeout
argument_list|)
return|;
block|}
comment|/**    * Returns just the shell command to be used to fetch a user's group IDs list.    * This is mainly separate to make some tests easier.    * @param userName The username that needs to be passed into the command built    * @return An appropriate shell command with arguments    */
DECL|method|getGroupsIDForUserCommand (String userName)
specifier|protected
name|String
index|[]
name|getGroupsIDForUserCommand
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
name|Shell
operator|.
name|getGroupsIDForUserCommand
argument_list|(
name|userName
argument_list|)
return|;
block|}
comment|/**    * Get the current user's group list from Unix by running the command 'groups'    * NOTE. For non-existing user it will return EMPTY list.    *    * @param user get groups for this user    * @return the groups list that the<code>user</code> belongs to. The primary    *         group is returned first.    * @throws IOException if encounter any error when running the command    */
DECL|method|getUnixGroups (String user)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getUnixGroups
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|ShellCommandExecutor
name|executor
init|=
name|createGroupExecutor
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
decl_stmt|;
try|try
block|{
name|executor
operator|.
name|execute
argument_list|()
expr_stmt|;
name|groups
operator|=
name|resolveFullGroupNames
argument_list|(
name|executor
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExitCodeException
name|e
parameter_list|)
block|{
try|try
block|{
name|groups
operator|=
name|resolvePartialGroupNames
argument_list|(
name|user
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|executor
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PartialGroupNameException
name|pge
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"unable to return groups for user {}"
argument_list|,
name|user
argument_list|,
name|pge
argument_list|)
expr_stmt|;
return|return
name|EMPTY_GROUPS
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// If its a shell executor timeout, indicate so in the message
comment|// but treat the result as empty instead of throwing it up,
comment|// similar to how partial resolution failures are handled above
if|if
condition|(
name|executor
operator|.
name|isTimedOut
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to return groups for user '{}' as shell group lookup "
operator|+
literal|"command '{}' ran longer than the configured timeout limit of "
operator|+
literal|"{} seconds."
argument_list|,
name|user
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
operator|.
name|join
argument_list|(
name|executor
operator|.
name|getExecString
argument_list|()
argument_list|)
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
return|return
name|EMPTY_GROUPS
return|;
block|}
else|else
block|{
comment|// If its not an executor timeout, we should let the caller handle it
throw|throw
name|ioe
throw|;
block|}
block|}
comment|// remove duplicated primary group
if|if
condition|(
operator|!
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|groups
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|groups
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|groups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
name|groups
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|groups
return|;
block|}
comment|/**    * Attempt to parse group names given that some names are not resolvable.    * Use the group id list to identify those that are not resolved.    *    * @param groupNames a string representing a list of group names    * @param groupIDs a string representing a list of group ids    * @return a linked list of group names    * @throws PartialGroupNameException    */
DECL|method|parsePartialGroupNames (String groupNames, String groupIDs)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|parsePartialGroupNames
parameter_list|(
name|String
name|groupNames
parameter_list|,
name|String
name|groupIDs
parameter_list|)
throws|throws
name|PartialGroupNameException
block|{
name|StringTokenizer
name|nameTokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|groupNames
argument_list|,
name|Shell
operator|.
name|TOKEN_SEPARATOR_REGEX
argument_list|)
decl_stmt|;
name|StringTokenizer
name|idTokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|groupIDs
argument_list|,
name|Shell
operator|.
name|TOKEN_SEPARATOR_REGEX
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|nameTokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
comment|// check for unresolvable group names.
if|if
condition|(
operator|!
name|idTokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PartialGroupNameException
argument_list|(
literal|"Number of group names and ids do"
operator|+
literal|" not match. group name ="
operator|+
name|groupNames
operator|+
literal|", group id = "
operator|+
name|groupIDs
argument_list|)
throw|;
block|}
name|String
name|groupName
init|=
name|nameTokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|String
name|groupID
init|=
name|idTokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isNumeric
argument_list|(
name|groupName
argument_list|)
operator|||
operator|!
name|groupName
operator|.
name|equals
argument_list|(
name|groupID
argument_list|)
condition|)
block|{
comment|// if the group name is non-numeric, it is resolved.
comment|// if the group name is numeric, but is not the same as group id,
comment|// regard it as a group name.
comment|// if unfortunately, some group names are not resolvable, and
comment|// the group name is the same as the group id, regard it as not
comment|// resolved.
name|groups
operator|.
name|add
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|groups
return|;
block|}
comment|/**    * Attempt to partially resolve group names.    *    * @param userName the user's name    * @param errMessage error message from the shell command    * @param groupNames the incomplete list of group names    * @return a list of resolved group names    * @throws PartialGroupNameException if the resolution fails or times out    */
DECL|method|resolvePartialGroupNames (String userName, String errMessage, String groupNames)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|resolvePartialGroupNames
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|errMessage
parameter_list|,
name|String
name|groupNames
parameter_list|)
throws|throws
name|PartialGroupNameException
block|{
comment|// Exception may indicate that some group names are not resolvable.
comment|// Shell-based implementation should tolerate unresolvable groups names,
comment|// and return resolvable ones, similar to what JNI-based implementation
comment|// does.
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
throw|throw
operator|new
name|PartialGroupNameException
argument_list|(
literal|"Does not support partial group"
operator|+
literal|" name resolution on Windows. "
operator|+
name|errMessage
argument_list|)
throw|;
block|}
if|if
condition|(
name|groupNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PartialGroupNameException
argument_list|(
literal|"The user name '"
operator|+
name|userName
operator|+
literal|"' is not found. "
operator|+
name|errMessage
argument_list|)
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Some group names for '{}' are not resolvable. {}"
argument_list|,
name|userName
argument_list|,
name|errMessage
argument_list|)
expr_stmt|;
comment|// attempt to partially resolve group names
name|ShellCommandExecutor
name|partialResolver
init|=
name|createGroupIDExecutor
argument_list|(
name|userName
argument_list|)
decl_stmt|;
try|try
block|{
name|partialResolver
operator|.
name|execute
argument_list|()
expr_stmt|;
return|return
name|parsePartialGroupNames
argument_list|(
name|groupNames
argument_list|,
name|partialResolver
operator|.
name|getOutput
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExitCodeException
name|ece
parameter_list|)
block|{
comment|// If exception is thrown trying to get group id list,
comment|// something is terribly wrong, so give up.
throw|throw
operator|new
name|PartialGroupNameException
argument_list|(
literal|"failed to get group id list for user '"
operator|+
name|userName
operator|+
literal|"'"
argument_list|,
name|ece
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Can't execute the shell command to "
operator|+
literal|"get the list of group id for user '"
operator|+
name|userName
operator|+
literal|"'"
decl_stmt|;
if|if
condition|(
name|partialResolver
operator|.
name|isTimedOut
argument_list|()
condition|)
block|{
name|message
operator|+=
literal|" because of the command taking longer than "
operator|+
literal|"the configured timeout: "
operator|+
name|timeout
operator|+
literal|" seconds"
expr_stmt|;
block|}
throw|throw
operator|new
name|PartialGroupNameException
argument_list|(
name|message
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Split group names into a linked list.    *    * @param groupNames a string representing the user's group names    * @return a linked list of group names    */
annotation|@
name|VisibleForTesting
DECL|method|resolveFullGroupNames (String groupNames)
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|resolveFullGroupNames
parameter_list|(
name|String
name|groupNames
parameter_list|)
block|{
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|groupNames
argument_list|,
name|Shell
operator|.
name|TOKEN_SEPARATOR_REGEX
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|groups
operator|.
name|add
argument_list|(
name|tokenizer
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|groups
return|;
block|}
block|}
end_class

end_unit

