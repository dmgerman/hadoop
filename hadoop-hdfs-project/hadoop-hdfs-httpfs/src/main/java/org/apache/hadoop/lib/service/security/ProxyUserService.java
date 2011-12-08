begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.service.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|service
operator|.
name|security
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
name|lib
operator|.
name|lang
operator|.
name|XException
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
name|lib
operator|.
name|server
operator|.
name|BaseService
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
name|lib
operator|.
name|server
operator|.
name|ServiceException
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
name|lib
operator|.
name|service
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
name|lib
operator|.
name|service
operator|.
name|ProxyUser
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
name|lib
operator|.
name|util
operator|.
name|Check
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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
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

begin_class
DECL|class|ProxyUserService
specifier|public
class|class
name|ProxyUserService
extends|extends
name|BaseService
implements|implements
name|ProxyUser
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ProxyUserService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|enum|ERROR
specifier|public
enum|enum
name|ERROR
implements|implements
name|XException
operator|.
name|ERROR
block|{
DECL|enumConstant|PRXU01
name|PRXU01
argument_list|(
literal|"Could not normalize host name [{0}], {1}"
argument_list|)
block|,
DECL|enumConstant|PRXU02
name|PRXU02
argument_list|(
literal|"Missing [{0}] property"
argument_list|)
block|;
DECL|field|template
specifier|private
name|String
name|template
decl_stmt|;
DECL|method|ERROR (String template)
name|ERROR
parameter_list|(
name|String
name|template
parameter_list|)
block|{
name|this
operator|.
name|template
operator|=
name|template
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTemplate ()
specifier|public
name|String
name|getTemplate
parameter_list|()
block|{
return|return
name|template
return|;
block|}
block|}
DECL|field|PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"proxyuser"
decl_stmt|;
DECL|field|GROUPS
specifier|private
specifier|static
specifier|final
name|String
name|GROUPS
init|=
literal|".groups"
decl_stmt|;
DECL|field|HOSTS
specifier|private
specifier|static
specifier|final
name|String
name|HOSTS
init|=
literal|".hosts"
decl_stmt|;
DECL|field|proxyUserHosts
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|proxyUserHosts
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|proxyUserGroups
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|proxyUserGroups
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ProxyUserService ()
specifier|public
name|ProxyUserService
parameter_list|()
block|{
name|super
argument_list|(
name|PREFIX
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
name|getInterface
parameter_list|()
block|{
return|return
name|ProxyUser
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|getServiceDependencies ()
specifier|public
name|Class
index|[]
name|getServiceDependencies
parameter_list|()
block|{
return|return
operator|new
name|Class
index|[]
block|{
name|Groups
operator|.
name|class
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|ServiceException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|getServiceConfig
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|endsWith
argument_list|(
name|GROUPS
argument_list|)
condition|)
block|{
name|String
name|proxyUser
init|=
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|key
operator|.
name|lastIndexOf
argument_list|(
name|GROUPS
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|getServiceConfig
argument_list|()
operator|.
name|get
argument_list|(
name|proxyUser
operator|+
name|HOSTS
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|ERROR
operator|.
name|PRXU02
argument_list|,
name|getPrefixedName
argument_list|(
name|proxyUser
operator|+
name|HOSTS
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading proxyuser settings [{}]=[{}]"
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|values
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|value
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|values
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|value
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|proxyUserGroups
operator|.
name|put
argument_list|(
name|proxyUser
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|.
name|endsWith
argument_list|(
name|HOSTS
argument_list|)
condition|)
block|{
name|String
name|proxyUser
init|=
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|key
operator|.
name|lastIndexOf
argument_list|(
name|HOSTS
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|getServiceConfig
argument_list|()
operator|.
name|get
argument_list|(
name|proxyUser
operator|+
name|GROUPS
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|ERROR
operator|.
name|PRXU02
argument_list|,
name|getPrefixedName
argument_list|(
name|proxyUser
operator|+
name|GROUPS
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading proxyuser settings [{}]=[{}]"
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|values
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|value
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|String
index|[]
name|hosts
init|=
name|value
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hosts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|originalName
init|=
name|hosts
index|[
name|i
index|]
decl_stmt|;
try|try
block|{
name|hosts
index|[
name|i
index|]
operator|=
name|normalizeHostname
argument_list|(
name|originalName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|ERROR
operator|.
name|PRXU01
argument_list|,
name|originalName
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"  Hostname, original [{}], normalized [{}]"
argument_list|,
name|originalName
argument_list|,
name|hosts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|values
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|hosts
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|proxyUserHosts
operator|.
name|put
argument_list|(
name|proxyUser
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|validate (String proxyUser, String proxyHost, String doAsUser)
specifier|public
name|void
name|validate
parameter_list|(
name|String
name|proxyUser
parameter_list|,
name|String
name|proxyHost
parameter_list|,
name|String
name|doAsUser
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
block|{
name|Check
operator|.
name|notEmpty
argument_list|(
name|proxyUser
argument_list|,
literal|"proxyUser"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|notEmpty
argument_list|(
name|proxyHost
argument_list|,
literal|"proxyHost"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|notEmpty
argument_list|(
name|doAsUser
argument_list|,
literal|"doAsUser"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Authorization check proxyuser [{}] host [{}] doAs [{}]"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|proxyUser
block|,
name|proxyHost
block|,
name|doAsUser
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|proxyUserHosts
operator|.
name|containsKey
argument_list|(
name|proxyUser
argument_list|)
condition|)
block|{
name|proxyHost
operator|=
name|normalizeHostname
argument_list|(
name|proxyHost
argument_list|)
expr_stmt|;
name|validateRequestorHost
argument_list|(
name|proxyUser
argument_list|,
name|proxyHost
argument_list|,
name|proxyUserHosts
operator|.
name|get
argument_list|(
name|proxyUser
argument_list|)
argument_list|)
expr_stmt|;
name|validateGroup
argument_list|(
name|proxyUser
argument_list|,
name|doAsUser
argument_list|,
name|proxyUserGroups
operator|.
name|get
argument_list|(
name|proxyUser
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"User [{0}] not defined as proxyuser"
argument_list|,
name|proxyUser
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|validateRequestorHost (String proxyUser, String hostname, Set<String> validHosts)
specifier|private
name|void
name|validateRequestorHost
parameter_list|(
name|String
name|proxyUser
parameter_list|,
name|String
name|hostname
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|validHosts
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
block|{
if|if
condition|(
name|validHosts
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|validHosts
operator|.
name|contains
argument_list|(
name|hostname
argument_list|)
operator|&&
operator|!
name|validHosts
operator|.
name|contains
argument_list|(
name|normalizeHostname
argument_list|(
name|hostname
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Unauthorized host [{0}] for proxyuser [{1}]"
argument_list|,
name|hostname
argument_list|,
name|proxyUser
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|validateGroup (String proxyUser, String user, Set<String> validGroups)
specifier|private
name|void
name|validateGroup
parameter_list|(
name|String
name|proxyUser
parameter_list|,
name|String
name|user
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|validGroups
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
block|{
if|if
condition|(
name|validGroups
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|userGroups
init|=
name|getServer
argument_list|()
operator|.
name|get
argument_list|(
name|Groups
operator|.
name|class
argument_list|)
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|g
range|:
name|validGroups
control|)
block|{
if|if
condition|(
name|userGroups
operator|.
name|contains
argument_list|(
name|g
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Unauthorized proxyuser [{0}] for user [{1}], not in proxyuser groups"
argument_list|,
name|proxyUser
argument_list|,
name|user
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|normalizeHostname (String name)
specifier|private
name|String
name|normalizeHostname
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|InetAddress
name|address
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|address
operator|.
name|getCanonicalHostName
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Could not resolve host [{0}], {1}"
argument_list|,
name|name
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

