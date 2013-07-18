begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|server
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|ArrayList
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
name|List
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
name|HadoopIllegalArgumentException
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
name|ipc
operator|.
name|RemoteException
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|client
operator|.
name|HSAdmin
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
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|GroupMappingServiceProvider
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
name|security
operator|.
name|authorize
operator|.
name|ProxyUsers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|AuthorizationException
import|;
end_import

begin_class
DECL|class|TestHSAdminServer
specifier|public
class|class
name|TestHSAdminServer
block|{
DECL|field|hsAdminServer
specifier|private
name|HSAdminServer
name|hsAdminServer
init|=
literal|null
decl_stmt|;
DECL|field|hsAdminClient
specifier|private
name|HSAdmin
name|hsAdminClient
init|=
literal|null
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|groupRefreshTimeoutSec
specifier|private
specifier|static
name|long
name|groupRefreshTimeoutSec
init|=
literal|1
decl_stmt|;
DECL|class|MockUnixGroupsMapping
specifier|public
specifier|static
class|class
name|MockUnixGroupsMapping
implements|implements
name|GroupMappingServiceProvider
block|{
DECL|field|i
specifier|private
name|int
name|i
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|getGroups (String user)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Getting groups in MockUnixGroupsMapping"
argument_list|)
expr_stmt|;
name|String
name|g1
init|=
name|user
operator|+
operator|(
literal|10
operator|*
name|i
operator|+
literal|1
operator|)
decl_stmt|;
name|String
name|g2
init|=
name|user
operator|+
operator|(
literal|10
operator|*
name|i
operator|+
literal|2
operator|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|g1
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|g2
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
return|return
name|l
return|;
block|}
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Refreshing groups in MockUnixGroupsMapping"
argument_list|)
expr_stmt|;
block|}
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
block|{     }
block|}
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|HadoopIllegalArgumentException
throws|,
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|JHS_ADMIN_ADDRESS
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
literal|"hadoop.security.group.mapping"
argument_list|,
name|MockUnixGroupsMapping
operator|.
name|class
argument_list|,
name|GroupMappingServiceProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
literal|"hadoop.security.groups.cache.secs"
argument_list|,
name|groupRefreshTimeoutSec
argument_list|)
expr_stmt|;
name|Groups
operator|.
name|getUserToGroupsMappingService
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|hsAdminServer
operator|=
operator|new
name|HSAdminServer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Configuration
name|createConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
block|}
expr_stmt|;
name|hsAdminServer
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|hsAdminServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setSocketAddr
argument_list|(
name|JHAdminConfig
operator|.
name|JHS_ADMIN_ADDRESS
argument_list|,
name|hsAdminServer
operator|.
name|clientRpcServer
operator|.
name|getListenerAddress
argument_list|()
argument_list|)
expr_stmt|;
name|hsAdminClient
operator|=
operator|new
name|HSAdmin
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetGroups ()
specifier|public
name|void
name|testGetGroups
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Get the current user
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"-getGroups"
expr_stmt|;
name|args
index|[
literal|1
index|]
operator|=
name|user
expr_stmt|;
comment|// Run the getGroups command
name|int
name|exitCode
init|=
name|hsAdminClient
operator|.
name|run
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Exit code should be 0 but was: "
operator|+
name|exitCode
argument_list|,
literal|0
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRefreshUserToGroupsMappings ()
specifier|public
name|void
name|testRefreshUserToGroupsMappings
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-refreshUserToGroupsMappings"
block|}
decl_stmt|;
name|Groups
name|groups
init|=
name|Groups
operator|.
name|getUserToGroupsMappingService
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"first attempt:"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|g1
init|=
name|groups
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|String
index|[]
name|str_groups
init|=
operator|new
name|String
index|[
name|g1
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|g1
operator|.
name|toArray
argument_list|(
name|str_groups
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|str_groups
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now groups of this user has changed but getGroups returns from the
comment|// cache,so we would see same groups as before
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"second attempt, should be same:"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|g2
init|=
name|groups
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|g2
operator|.
name|toArray
argument_list|(
name|str_groups
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|str_groups
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|g2
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Should be same group "
argument_list|,
name|g1
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|g2
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// run the command,which clears the cache
name|hsAdminClient
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"third attempt(after refresh command), should be different:"
argument_list|)
expr_stmt|;
comment|// Now get groups should return new groups
name|List
argument_list|<
name|String
argument_list|>
name|g3
init|=
name|groups
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|g3
operator|.
name|toArray
argument_list|(
name|str_groups
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|str_groups
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|g3
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
literal|"Should be different group: "
operator|+
name|g1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|+
literal|" and "
operator|+
name|g3
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|g1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|g3
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRefreshSuperUserGroups ()
specifier|public
name|void
name|testRefreshSuperUserGroups
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|mock
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|superUser
init|=
name|mock
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|ugi
operator|.
name|getRealUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|superUser
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|superUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"superuser"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|superUser
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"superuser"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ugi
operator|.
name|getGroupNames
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"group3"
block|}
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"regularUser"
argument_list|)
expr_stmt|;
comment|// Set super user groups not to include groups of regularUser
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser.superuser.groups"
argument_list|,
literal|"group1,group2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser.superuser.hosts"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|1
index|]
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"-refreshSuperUserGroupsConfiguration"
expr_stmt|;
name|hsAdminClient
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ProxyUsers
operator|.
name|authorize
argument_list|(
name|ugi
argument_list|,
literal|"127.0.0.1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|th
operator|=
name|e
expr_stmt|;
block|}
comment|// Exception should be thrown
name|assertTrue
argument_list|(
name|th
operator|instanceof
name|AuthorizationException
argument_list|)
expr_stmt|;
comment|// Now add regularUser group to superuser group but not execute
comment|// refreshSuperUserGroupMapping
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser.superuser.groups"
argument_list|,
literal|"group1,group2,group3"
argument_list|)
expr_stmt|;
comment|// Again,lets run ProxyUsers.authorize and see if regularUser can be
comment|// impersonated
comment|// resetting th
name|th
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|ProxyUsers
operator|.
name|authorize
argument_list|(
name|ugi
argument_list|,
literal|"127.0.0.1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|th
operator|=
name|e
expr_stmt|;
block|}
comment|// Exception should be thrown again since we didn't refresh the configs
name|assertTrue
argument_list|(
name|th
operator|instanceof
name|AuthorizationException
argument_list|)
expr_stmt|;
comment|// Lets refresh the config by running refreshSuperUserGroupsConfiguration
name|hsAdminClient
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|th
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|ProxyUsers
operator|.
name|authorize
argument_list|(
name|ugi
argument_list|,
literal|"127.0.0.1"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|th
operator|=
name|e
expr_stmt|;
block|}
comment|// No exception thrown since regularUser can be impersonated.
name|assertNull
argument_list|(
literal|"Unexpected exception thrown: "
operator|+
name|th
argument_list|,
name|th
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRefreshAdminAcls ()
specifier|public
name|void
name|testRefreshAdminAcls
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setting current user to admin acl
name|conf
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|JHS_ADMIN_ACL
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|1
index|]
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"-refreshAdminAcls"
expr_stmt|;
name|hsAdminClient
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// Now I should be able to run any hsadmin command without any exception
comment|// being thrown
name|args
index|[
literal|0
index|]
operator|=
literal|"-refreshSuperUserGroupsConfiguration"
expr_stmt|;
name|hsAdminClient
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// Lets remove current user from admin acl
name|conf
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|JHS_ADMIN_ACL
argument_list|,
literal|"notCurrentUser"
argument_list|)
expr_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"-refreshAdminAcls"
expr_stmt|;
name|hsAdminClient
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// Now I should get an exception if i run any hsadmin command
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"-refreshSuperUserGroupsConfiguration"
expr_stmt|;
try|try
block|{
name|hsAdminClient
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|th
operator|=
name|e
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|th
operator|instanceof
name|RemoteException
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
block|{
if|if
condition|(
name|hsAdminServer
operator|!=
literal|null
condition|)
name|hsAdminServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

