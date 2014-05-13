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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|Groups
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
name|util
operator|.
name|StringUtils
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
block|,
literal|"HBase"
block|,
literal|"Hive"
block|}
argument_list|)
DECL|class|ProxyUsers
specifier|public
class|class
name|ProxyUsers
block|{
DECL|field|CONF_HOSTS
specifier|private
specifier|static
specifier|final
name|String
name|CONF_HOSTS
init|=
literal|".hosts"
decl_stmt|;
DECL|field|CONF_USERS
specifier|private
specifier|static
specifier|final
name|String
name|CONF_USERS
init|=
literal|".users"
decl_stmt|;
DECL|field|CONF_GROUPS
specifier|private
specifier|static
specifier|final
name|String
name|CONF_GROUPS
init|=
literal|".groups"
decl_stmt|;
DECL|field|CONF_HADOOP_PROXYUSER
specifier|private
specifier|static
specifier|final
name|String
name|CONF_HADOOP_PROXYUSER
init|=
literal|"hadoop.proxyuser."
decl_stmt|;
DECL|field|CONF_HADOOP_PROXYUSER_RE
specifier|private
specifier|static
specifier|final
name|String
name|CONF_HADOOP_PROXYUSER_RE
init|=
literal|"hadoop\\.proxyuser\\."
decl_stmt|;
DECL|field|init
specifier|private
specifier|static
name|boolean
name|init
init|=
literal|false
decl_stmt|;
comment|//list of users, groups and hosts per proxyuser
DECL|field|proxyUsers
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|proxyUsers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|proxyGroups
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|proxyGroups
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|proxyHosts
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|proxyHosts
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * reread the conf and get new values for "hadoop.proxyuser.*.groups/users/hosts"    */
DECL|method|refreshSuperUserGroupsConfiguration ()
specifier|public
specifier|static
name|void
name|refreshSuperUserGroupsConfiguration
parameter_list|()
block|{
comment|//load server side configuration;
name|refreshSuperUserGroupsConfiguration
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * refresh configuration    * @param conf    */
DECL|method|refreshSuperUserGroupsConfiguration (Configuration conf)
specifier|public
specifier|static
specifier|synchronized
name|void
name|refreshSuperUserGroupsConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// remove all existing stuff
name|proxyGroups
operator|.
name|clear
argument_list|()
expr_stmt|;
name|proxyHosts
operator|.
name|clear
argument_list|()
expr_stmt|;
name|proxyUsers
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// get all the new keys for users
name|String
name|regex
init|=
name|CONF_HADOOP_PROXYUSER_RE
operator|+
literal|"[^.]*\\"
operator|+
name|CONF_USERS
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|allMatchKeys
init|=
name|conf
operator|.
name|getValByRegex
argument_list|(
name|regex
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|allMatchKeys
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|users
init|=
name|StringUtils
operator|.
name|getTrimmedStringCollection
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|proxyUsers
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|users
argument_list|)
expr_stmt|;
block|}
comment|// get all the new keys for groups
name|regex
operator|=
name|CONF_HADOOP_PROXYUSER_RE
operator|+
literal|"[^.]*\\"
operator|+
name|CONF_GROUPS
expr_stmt|;
name|allMatchKeys
operator|=
name|conf
operator|.
name|getValByRegex
argument_list|(
name|regex
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|allMatchKeys
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|StringUtils
operator|.
name|getTrimmedStringCollection
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|proxyGroups
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|groups
argument_list|)
expr_stmt|;
comment|//cache the groups. This is needed for NetGroups
name|Groups
operator|.
name|getUserToGroupsMappingService
argument_list|(
name|conf
argument_list|)
operator|.
name|cacheGroupsAdd
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|groups
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now hosts
name|regex
operator|=
name|CONF_HADOOP_PROXYUSER_RE
operator|+
literal|"[^.]*\\"
operator|+
name|CONF_HOSTS
expr_stmt|;
name|allMatchKeys
operator|=
name|conf
operator|.
name|getValByRegex
argument_list|(
name|regex
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|allMatchKeys
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|proxyHosts
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|StringUtils
operator|.
name|getTrimmedStringCollection
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|init
operator|=
literal|true
expr_stmt|;
name|ProxyServers
operator|.
name|refresh
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns configuration key for effective users allowed for a superuser    *     * @param userName name of the superuser    * @return configuration key for superuser users    */
DECL|method|getProxySuperuserUserConfKey (String userName)
specifier|public
specifier|static
name|String
name|getProxySuperuserUserConfKey
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
name|ProxyUsers
operator|.
name|CONF_HADOOP_PROXYUSER
operator|+
name|userName
operator|+
name|ProxyUsers
operator|.
name|CONF_USERS
return|;
block|}
comment|/**    * Returns configuration key for effective user groups allowed for a superuser    *     * @param userName name of the superuser    * @return configuration key for superuser groups    */
DECL|method|getProxySuperuserGroupConfKey (String userName)
specifier|public
specifier|static
name|String
name|getProxySuperuserGroupConfKey
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
name|ProxyUsers
operator|.
name|CONF_HADOOP_PROXYUSER
operator|+
name|userName
operator|+
name|ProxyUsers
operator|.
name|CONF_GROUPS
return|;
block|}
comment|/**    * Return configuration key for superuser ip addresses    *     * @param userName name of the superuser    * @return configuration key for superuser ip-addresses    */
DECL|method|getProxySuperuserIpConfKey (String userName)
specifier|public
specifier|static
name|String
name|getProxySuperuserIpConfKey
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
name|ProxyUsers
operator|.
name|CONF_HADOOP_PROXYUSER
operator|+
name|userName
operator|+
name|ProxyUsers
operator|.
name|CONF_HOSTS
return|;
block|}
comment|/**    * Authorize the superuser which is doing doAs    *     * @param user ugi of the effective or proxy user which contains a real user    * @param remoteAddress the ip address of client    * @throws AuthorizationException    */
DECL|method|authorize (UserGroupInformation user, String remoteAddress)
specifier|public
specifier|static
specifier|synchronized
name|void
name|authorize
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|String
name|remoteAddress
parameter_list|)
throws|throws
name|AuthorizationException
block|{
if|if
condition|(
operator|!
name|init
condition|)
block|{
name|refreshSuperUserGroupsConfiguration
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|user
operator|.
name|getRealUser
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|boolean
name|userAuthorized
init|=
literal|false
decl_stmt|;
name|boolean
name|ipAuthorized
init|=
literal|false
decl_stmt|;
name|UserGroupInformation
name|superUser
init|=
name|user
operator|.
name|getRealUser
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|allowedUsers
init|=
name|proxyUsers
operator|.
name|get
argument_list|(
name|getProxySuperuserUserConfKey
argument_list|(
name|superUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isWildcardList
argument_list|(
name|allowedUsers
argument_list|)
condition|)
block|{
name|userAuthorized
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|allowedUsers
operator|!=
literal|null
operator|&&
operator|!
name|allowedUsers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|allowedUsers
operator|.
name|contains
argument_list|(
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|)
condition|)
block|{
name|userAuthorized
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|userAuthorized
condition|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|allowedUserGroups
init|=
name|proxyGroups
operator|.
name|get
argument_list|(
name|getProxySuperuserGroupConfKey
argument_list|(
name|superUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isWildcardList
argument_list|(
name|allowedUserGroups
argument_list|)
condition|)
block|{
name|userAuthorized
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|allowedUserGroups
operator|!=
literal|null
operator|&&
operator|!
name|allowedUserGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|group
range|:
name|user
operator|.
name|getGroupNames
argument_list|()
control|)
block|{
if|if
condition|(
name|allowedUserGroups
operator|.
name|contains
argument_list|(
name|group
argument_list|)
condition|)
block|{
name|userAuthorized
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|userAuthorized
condition|)
block|{
throw|throw
operator|new
name|AuthorizationException
argument_list|(
literal|"User: "
operator|+
name|superUser
operator|.
name|getUserName
argument_list|()
operator|+
literal|" is not allowed to impersonate "
operator|+
name|user
operator|.
name|getUserName
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|ipList
init|=
name|proxyHosts
operator|.
name|get
argument_list|(
name|getProxySuperuserIpConfKey
argument_list|(
name|superUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isWildcardList
argument_list|(
name|ipList
argument_list|)
condition|)
block|{
name|ipAuthorized
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ipList
operator|!=
literal|null
operator|&&
operator|!
name|ipList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|allowedHost
range|:
name|ipList
control|)
block|{
name|InetAddress
name|hostAddr
decl_stmt|;
try|try
block|{
name|hostAddr
operator|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|allowedHost
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
continue|continue;
block|}
if|if
condition|(
name|hostAddr
operator|.
name|getHostAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|remoteAddress
argument_list|)
condition|)
block|{
comment|// Authorization is successful
name|ipAuthorized
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|ipAuthorized
condition|)
block|{
throw|throw
operator|new
name|AuthorizationException
argument_list|(
literal|"Unauthorized connection for super-user: "
operator|+
name|superUser
operator|.
name|getUserName
argument_list|()
operator|+
literal|" from IP "
operator|+
name|remoteAddress
argument_list|)
throw|;
block|}
block|}
comment|/**    * This function is kept to provide backward compatibility.    * @param user    * @param remoteAddress    * @param conf    * @throws AuthorizationException    * @deprecated use {@link #authorize(UserGroupInformation, String) instead.     */
annotation|@
name|Deprecated
DECL|method|authorize (UserGroupInformation user, String remoteAddress, Configuration conf)
specifier|public
specifier|static
specifier|synchronized
name|void
name|authorize
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|String
name|remoteAddress
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|AuthorizationException
block|{
name|authorize
argument_list|(
name|user
argument_list|,
name|remoteAddress
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return true if the configuration specifies the special configuration value    * "*", indicating that any group or host list is allowed to use this configuration.    */
DECL|method|isWildcardList (Collection<String> list)
specifier|private
specifier|static
name|boolean
name|isWildcardList
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|list
parameter_list|)
block|{
return|return
operator|(
name|list
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
operator|&&
operator|(
name|list
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
operator|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getProxyUsers ()
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|getProxyUsers
parameter_list|()
block|{
return|return
name|proxyUsers
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getProxyGroups ()
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|getProxyGroups
parameter_list|()
block|{
return|return
name|proxyGroups
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getProxyHosts ()
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|getProxyHosts
parameter_list|()
block|{
return|return
name|proxyHosts
return|;
block|}
block|}
end_class

end_unit

