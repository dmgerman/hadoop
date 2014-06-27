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
name|util
operator|.
name|MachineList
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
DECL|class|DefaultImpersonationProvider
specifier|public
class|class
name|DefaultImpersonationProvider
implements|implements
name|ImpersonationProvider
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
DECL|field|CONF_HADOOP_PROXYUSER_RE_USERS_GROUPS
specifier|private
specifier|static
specifier|final
name|String
name|CONF_HADOOP_PROXYUSER_RE_USERS_GROUPS
init|=
name|CONF_HADOOP_PROXYUSER_RE
operator|+
literal|"[^.]*("
operator|+
name|Pattern
operator|.
name|quote
argument_list|(
name|CONF_USERS
argument_list|)
operator|+
literal|"|"
operator|+
name|Pattern
operator|.
name|quote
argument_list|(
name|CONF_GROUPS
argument_list|)
operator|+
literal|")"
decl_stmt|;
DECL|field|CONF_HADOOP_PROXYUSER_RE_HOSTS
specifier|private
specifier|static
specifier|final
name|String
name|CONF_HADOOP_PROXYUSER_RE_HOSTS
init|=
name|CONF_HADOOP_PROXYUSER_RE
operator|+
literal|"[^.]*"
operator|+
name|Pattern
operator|.
name|quote
argument_list|(
name|CONF_HOSTS
argument_list|)
decl_stmt|;
comment|// acl and list of hosts per proxyuser
DECL|field|proxyUserAcl
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|AccessControlList
argument_list|>
name|proxyUserAcl
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AccessControlList
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
name|MachineList
argument_list|>
name|proxyHosts
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MachineList
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
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
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
comment|// get list of users and groups per proxyuser
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
name|CONF_HADOOP_PROXYUSER_RE_USERS_GROUPS
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
name|String
name|aclKey
init|=
name|getAclKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|proxyUserAcl
operator|.
name|containsKey
argument_list|(
name|aclKey
argument_list|)
condition|)
block|{
name|proxyUserAcl
operator|.
name|put
argument_list|(
name|aclKey
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|allMatchKeys
operator|.
name|get
argument_list|(
name|aclKey
operator|+
name|CONF_USERS
argument_list|)
argument_list|,
name|allMatchKeys
operator|.
name|get
argument_list|(
name|aclKey
operator|+
name|CONF_GROUPS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// get hosts per proxyuser
name|allMatchKeys
operator|=
name|conf
operator|.
name|getValByRegex
argument_list|(
name|CONF_HADOOP_PROXYUSER_RE_HOSTS
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
operator|new
name|MachineList
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|authorize (UserGroupInformation user, String remoteAddress)
specifier|public
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
name|UserGroupInformation
name|realUser
init|=
name|user
operator|.
name|getRealUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|realUser
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|AccessControlList
name|acl
init|=
name|proxyUserAcl
operator|.
name|get
argument_list|(
name|CONF_HADOOP_PROXYUSER
operator|+
name|realUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|acl
operator|==
literal|null
operator|||
operator|!
name|acl
operator|.
name|isUserAllowed
argument_list|(
name|user
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthorizationException
argument_list|(
literal|"User: "
operator|+
name|realUser
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
name|MachineList
name|MachineList
init|=
name|proxyHosts
operator|.
name|get
argument_list|(
name|getProxySuperuserIpConfKey
argument_list|(
name|realUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|MachineList
operator|.
name|includes
argument_list|(
name|remoteAddress
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthorizationException
argument_list|(
literal|"Unauthorized connection for super-user: "
operator|+
name|realUser
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
DECL|method|getAclKey (String key)
specifier|private
name|String
name|getAclKey
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|int
name|endIndex
init|=
name|key
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|endIndex
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|endIndex
argument_list|)
return|;
block|}
return|return
name|key
return|;
block|}
comment|/**    * Returns configuration key for effective usergroups allowed for a superuser    *     * @param userName name of the superuser    * @return configuration key for superuser usergroups    */
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
name|CONF_HADOOP_PROXYUSER
operator|+
name|userName
operator|+
name|CONF_USERS
return|;
block|}
comment|/**    * Returns configuration key for effective groups allowed for a superuser    *     * @param userName name of the superuser    * @return configuration key for superuser groups    */
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
name|CONF_HADOOP_PROXYUSER
operator|+
name|userName
operator|+
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
name|CONF_HADOOP_PROXYUSER
operator|+
name|userName
operator|+
name|CONF_HOSTS
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getProxyGroups ()
specifier|public
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
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|AccessControlList
argument_list|>
name|entry
range|:
name|proxyUserAcl
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|proxyGroups
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
name|CONF_GROUPS
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getGroups
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|proxyGroups
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getProxyHosts ()
specifier|public
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
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|tmpProxyHosts
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
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MachineList
argument_list|>
name|proxyHostEntry
range|:
name|proxyHosts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|tmpProxyHosts
operator|.
name|put
argument_list|(
name|proxyHostEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|proxyHostEntry
operator|.
name|getValue
argument_list|()
operator|.
name|getCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|tmpProxyHosts
return|;
block|}
block|}
end_class

end_unit

