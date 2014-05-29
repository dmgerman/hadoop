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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|Arrays
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|NativeCodeLoader
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestProxyUsers
specifier|public
class|class
name|TestProxyUsers
block|{
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
name|TestProxyUsers
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REAL_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|REAL_USER_NAME
init|=
literal|"proxier"
decl_stmt|;
DECL|field|PROXY_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|PROXY_USER_NAME
init|=
literal|"proxied_user"
decl_stmt|;
DECL|field|AUTHORIZED_PROXY_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|AUTHORIZED_PROXY_USER_NAME
init|=
literal|"authorized_proxied_user"
decl_stmt|;
DECL|field|GROUP_NAMES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|GROUP_NAMES
init|=
operator|new
name|String
index|[]
block|{
literal|"foo_group"
block|}
decl_stmt|;
DECL|field|NETGROUP_NAMES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|NETGROUP_NAMES
init|=
operator|new
name|String
index|[]
block|{
literal|"@foo_group"
block|}
decl_stmt|;
DECL|field|OTHER_GROUP_NAMES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|OTHER_GROUP_NAMES
init|=
operator|new
name|String
index|[]
block|{
literal|"bar_group"
block|}
decl_stmt|;
DECL|field|SUDO_GROUP_NAMES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|SUDO_GROUP_NAMES
init|=
operator|new
name|String
index|[]
block|{
literal|"sudo_proxied_user"
block|}
decl_stmt|;
DECL|field|PROXY_IP
specifier|private
specifier|static
specifier|final
name|String
name|PROXY_IP
init|=
literal|"1.2.3.4"
decl_stmt|;
comment|/**    * Test the netgroups (groups in ACL rules that start with @)    *    * This is a  manual test because it requires:    *   - host setup    *   - native code compiled    *   - specify the group mapping class    *    * Host setup:    *    * /etc/nsswitch.conf should have a line like this:    * netgroup: files    *    * /etc/netgroup should be (the whole file):    * foo_group (,proxied_user,)    *    * To run this test:    *    * export JAVA_HOME='path/to/java'    * mvn test \    *   -Dtest=TestProxyUsers \    *   -DTestProxyUsersGroupMapping=$className \    *       * where $className is one of the classes that provide group    * mapping services, i.e. classes that implement    * GroupMappingServiceProvider interface, at this time:    *   - org.apache.hadoop.security.JniBasedUnixGroupsNetgroupMapping    *   - org.apache.hadoop.security.ShellBasedUnixGroupsNetgroupMapping    *    */
annotation|@
name|Test
DECL|method|testNetgroups ()
specifier|public
name|void
name|testNetgroups
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Not testing netgroups, "
operator|+
literal|"this test only runs when native code is compiled"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|groupMappingClassName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"TestProxyUsersGroupMapping"
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupMappingClassName
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Not testing netgroups, no group mapping class specified, "
operator|+
literal|"use -DTestProxyUsersGroupMapping=$className to specify "
operator|+
literal|"group mapping class (must implement GroupMappingServiceProvider "
operator|+
literal|"interface and support netgroups)"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing netgroups using: "
operator|+
name|groupMappingClassName
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_GROUP_MAPPING
argument_list|,
name|groupMappingClassName
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserGroupConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|NETGROUP_NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|PROXY_IP
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
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
comment|// try proxying a group that's allowed
name|UserGroupInformation
name|realUserUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUserUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|groups
operator|.
name|getGroups
argument_list|(
name|PROXY_USER_NAME
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|groups
operator|.
name|getGroups
argument_list|(
name|PROXY_USER_NAME
argument_list|)
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
name|PROXY_IP
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProxyUsers ()
specifier|public
name|void
name|testProxyUsers
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserGroupConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|GROUP_NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|PROXY_IP
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// First try proxying a group that's allowed
name|UserGroupInformation
name|realUserUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUserUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
comment|// From good IP
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
comment|// From bad IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
comment|// Now try proxying a group that's not allowed
name|realUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
expr_stmt|;
name|proxyUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|OTHER_GROUP_NAMES
argument_list|)
expr_stmt|;
comment|// From good IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
comment|// From bad IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProxyUsersWithUserConf ()
specifier|public
name|void
name|testProxyUsersWithUserConf
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserUserConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|AUTHORIZED_PROXY_USER_NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|PROXY_IP
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// First try proxying a user that's allowed
name|UserGroupInformation
name|realUserUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUserUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|AUTHORIZED_PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
comment|// From good IP
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
comment|// From bad IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
comment|// Now try proxying a user that's not allowed
name|realUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
expr_stmt|;
name|proxyUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|GROUP_NAMES
argument_list|)
expr_stmt|;
comment|// From good IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
comment|// From bad IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWildcardGroup ()
specifier|public
name|void
name|testWildcardGroup
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserGroupConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|PROXY_IP
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// First try proxying a group that's allowed
name|UserGroupInformation
name|realUserUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUserUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
comment|// From good IP
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
comment|// From bad IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
comment|// Now try proxying a different group (just to make sure we aren't getting spill over
comment|// from the other test case!)
name|realUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
expr_stmt|;
name|proxyUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|OTHER_GROUP_NAMES
argument_list|)
expr_stmt|;
comment|// From good IP
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
comment|// From bad IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWildcardUser ()
specifier|public
name|void
name|testWildcardUser
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserUserConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|PROXY_IP
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// First try proxying a user that's allowed
name|UserGroupInformation
name|realUserUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUserUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|AUTHORIZED_PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
comment|// From good IP
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
comment|// From bad IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
comment|// Now try proxying a different user (just to make sure we aren't getting spill over
comment|// from the other test case!)
name|realUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
expr_stmt|;
name|proxyUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|OTHER_GROUP_NAMES
argument_list|)
expr_stmt|;
comment|// From good IP
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
comment|// From bad IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWildcardIP ()
specifier|public
name|void
name|testWildcardIP
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserGroupConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|GROUP_NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// First try proxying a group that's allowed
name|UserGroupInformation
name|realUserUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUserUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
comment|// From either IP should be fine
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
comment|// Now set up an unallowed group
name|realUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|REAL_USER_NAME
argument_list|)
expr_stmt|;
name|proxyUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|OTHER_GROUP_NAMES
argument_list|)
expr_stmt|;
comment|// Neither IP should be OK
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithDuplicateProxyGroups ()
specifier|public
name|void
name|testWithDuplicateProxyGroups
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserGroupConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|GROUP_NAMES
argument_list|,
name|GROUP_NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|PROXY_IP
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|groupsToBeProxied
init|=
name|ProxyUsers
operator|.
name|getDefaultImpersonationProvider
argument_list|()
operator|.
name|getProxyGroups
argument_list|()
operator|.
name|get
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserGroupConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|groupsToBeProxied
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithDuplicateProxyHosts ()
specifier|public
name|void
name|testWithDuplicateProxyHosts
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserGroupConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|GROUP_NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|PROXY_IP
argument_list|,
name|PROXY_IP
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|hosts
init|=
name|ProxyUsers
operator|.
name|getDefaultImpersonationProvider
argument_list|()
operator|.
name|getProxyHosts
argument_list|()
operator|.
name|get
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|REAL_USER_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hosts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProxyUsersWithProviderOverride ()
specifier|public
name|void
name|testProxyUsersWithProviderOverride
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_IMPERSONATION_PROVIDER_CLASS
argument_list|,
literal|"org.apache.hadoop.security.authorize.TestProxyUsers$TestDummyImpersonationProvider"
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// First try proxying a group that's allowed
name|UserGroupInformation
name|realUserUgi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|REAL_USER_NAME
argument_list|,
name|SUDO_GROUP_NAMES
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUserUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
comment|// From good IP
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
comment|// From bad IP
name|assertAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
comment|// Now try proxying a group that's not allowed
name|realUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|REAL_USER_NAME
argument_list|,
name|GROUP_NAMES
argument_list|)
expr_stmt|;
name|proxyUserUgi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|PROXY_USER_NAME
argument_list|,
name|realUserUgi
argument_list|,
name|GROUP_NAMES
argument_list|)
expr_stmt|;
comment|// From good IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.4"
argument_list|)
expr_stmt|;
comment|// From bad IP
name|assertNotAuthorized
argument_list|(
name|proxyUserUgi
argument_list|,
literal|"1.2.3.5"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNotAuthorized (UserGroupInformation proxyUgi, String host)
specifier|private
name|void
name|assertNotAuthorized
parameter_list|(
name|UserGroupInformation
name|proxyUgi
parameter_list|,
name|String
name|host
parameter_list|)
block|{
try|try
block|{
name|ProxyUsers
operator|.
name|authorize
argument_list|(
name|proxyUgi
argument_list|,
name|host
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Allowed authorization of "
operator|+
name|proxyUgi
operator|+
literal|" from "
operator|+
name|host
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthorizationException
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
DECL|method|assertAuthorized (UserGroupInformation proxyUgi, String host)
specifier|private
name|void
name|assertAuthorized
parameter_list|(
name|UserGroupInformation
name|proxyUgi
parameter_list|,
name|String
name|host
parameter_list|)
block|{
try|try
block|{
name|ProxyUsers
operator|.
name|authorize
argument_list|(
name|proxyUgi
argument_list|,
name|host
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthorizationException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Did not allow authorization of "
operator|+
name|proxyUgi
operator|+
literal|" from "
operator|+
name|host
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestDummyImpersonationProvider
specifier|static
class|class
name|TestDummyImpersonationProvider
implements|implements
name|ImpersonationProvider
block|{
comment|/**      * Authorize a user (superuser) to impersonate another user (user1) if the       * superuser belongs to the group "sudo_user1" .      */
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
name|superUser
init|=
name|user
operator|.
name|getRealUser
argument_list|()
decl_stmt|;
name|String
name|sudoGroupName
init|=
literal|"sudo_"
operator|+
name|user
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|asList
argument_list|(
name|superUser
operator|.
name|getGroupNames
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|sudoGroupName
argument_list|)
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
block|{      }
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

