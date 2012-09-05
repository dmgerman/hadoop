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
name|Iterator
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
name|List
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
name|assertFalse
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
name|assertNotNull
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
name|assertTrue
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|TestAccessControlList
specifier|public
class|class
name|TestAccessControlList
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
name|TestAccessControlList
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Test the netgroups (groups in ACL rules that start with @)    *    * This is a  manual test because it requires:    *   - host setup    *   - native code compiled    *   - specify the group mapping class    *    * Host setup:    *    * /etc/nsswitch.conf should have a line like this:    * netgroup: files    *    * /etc/netgroup should be (the whole file):    * lasVegas (,elvis,)    * memphis (,elvis,) (,jerryLeeLewis,)    *    * To run this test:    *    * export JAVA_HOME='path/to/java'    * ant \    *   -Dtestcase=TestAccessControlList \    *   -Dtest.output=yes \    *   -DTestAccessControlListGroupMapping=$className \    *   compile-native test    *    * where $className is one of the classes that provide group    * mapping services, i.e. classes that implement    * GroupMappingServiceProvider interface, at this time:    *   - org.apache.hadoop.security.JniBasedUnixGroupsNetgroupMapping    *   - org.apache.hadoop.security.ShellBasedUnixGroupsNetgroupMapping    *    */
annotation|@
name|Test
DECL|method|testNetgroups ()
specifier|public
name|void
name|testNetgroups
parameter_list|()
throws|throws
name|Exception
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
literal|"TestAccessControlListGroupMapping"
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
literal|"use -DTestAccessControlListGroupMapping=$className to specify "
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
name|AccessControlList
name|acl
decl_stmt|;
comment|// create these ACLs to populate groups cache
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"ja my"
argument_list|)
expr_stmt|;
comment|// plain
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"sinatra ratpack,@lasVegas"
argument_list|)
expr_stmt|;
comment|// netgroup
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|" somegroup,@someNetgroup"
argument_list|)
expr_stmt|;
comment|// no user
comment|// this ACL will be used for testing ACLs
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"carlPerkins ratpack,@lasVegas"
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addGroup
argument_list|(
literal|"@memphis"
argument_list|)
expr_stmt|;
comment|// validate the netgroups before and after rehresh to make
comment|// sure refresh works correctly
name|validateNetgroups
argument_list|(
name|groups
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|groups
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|validateNetgroups
argument_list|(
name|groups
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate the netgroups, both group membership and ACL    * functionality    *    * Note: assumes a specific acl setup done by testNetgroups    *    * @param groups group to user mapping service    * @param acl ACL set up in a specific way, see testNetgroups    */
DECL|method|validateNetgroups (Groups groups, AccessControlList acl)
specifier|private
name|void
name|validateNetgroups
parameter_list|(
name|Groups
name|groups
parameter_list|,
name|AccessControlList
name|acl
parameter_list|)
throws|throws
name|Exception
block|{
comment|// check that the netgroups are working
name|List
argument_list|<
name|String
argument_list|>
name|elvisGroups
init|=
name|groups
operator|.
name|getGroups
argument_list|(
literal|"elvis"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|elvisGroups
operator|.
name|contains
argument_list|(
literal|"@lasVegas"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|elvisGroups
operator|.
name|contains
argument_list|(
literal|"@memphis"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|jerryLeeLewisGroups
init|=
name|groups
operator|.
name|getGroups
argument_list|(
literal|"jerryLeeLewis"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|jerryLeeLewisGroups
operator|.
name|contains
argument_list|(
literal|"@memphis"
argument_list|)
argument_list|)
expr_stmt|;
comment|// allowed becuase his netgroup is in ACL
name|UserGroupInformation
name|elvis
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"elvis"
argument_list|)
decl_stmt|;
name|assertUserAllowed
argument_list|(
name|elvis
argument_list|,
name|acl
argument_list|)
expr_stmt|;
comment|// allowed because he's in ACL
name|UserGroupInformation
name|carlPerkins
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"carlPerkins"
argument_list|)
decl_stmt|;
name|assertUserAllowed
argument_list|(
name|carlPerkins
argument_list|,
name|acl
argument_list|)
expr_stmt|;
comment|// not allowed because he's not in ACL and has no netgroups
name|UserGroupInformation
name|littleRichard
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"littleRichard"
argument_list|)
decl_stmt|;
name|assertUserNotAllowed
argument_list|(
name|littleRichard
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWildCardAccessControlList ()
specifier|public
name|void
name|testWildCardAccessControlList
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlList
name|acl
decl_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|isAllAllowed
argument_list|()
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"  * "
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|isAllAllowed
argument_list|()
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|" *"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|isAllAllowed
argument_list|()
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"*  "
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|isAllAllowed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check if AccessControlList.toString() works as expected.
comment|// Also validate if getAclString() for various cases.
annotation|@
name|Test
DECL|method|testAclString ()
specifier|public
name|void
name|testAclString
parameter_list|()
block|{
name|AccessControlList
name|acl
decl_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"All users are allowed"
argument_list|)
argument_list|)
expr_stmt|;
name|validateGetAclString
argument_list|(
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"No users are allowed"
argument_list|)
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"user1,user2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Users [user1, user2] are allowed"
argument_list|)
argument_list|)
expr_stmt|;
name|validateGetAclString
argument_list|(
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"user1,user2 "
argument_list|)
expr_stmt|;
comment|// with space
name|assertTrue
argument_list|(
name|acl
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Users [user1, user2] are allowed"
argument_list|)
argument_list|)
expr_stmt|;
name|validateGetAclString
argument_list|(
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|" group1,group2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Members of the groups [group1, group2] are allowed"
argument_list|)
argument_list|)
expr_stmt|;
name|validateGetAclString
argument_list|(
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"user1,user2 group1,group2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Users [user1, user2] and "
operator|+
literal|"members of the groups [group1, group2] are allowed"
argument_list|)
argument_list|)
expr_stmt|;
name|validateGetAclString
argument_list|(
name|acl
argument_list|)
expr_stmt|;
block|}
comment|// Validates if getAclString() is working as expected. i.e. if we can build
comment|// a new ACL instance from the value returned by getAclString().
DECL|method|validateGetAclString (AccessControlList acl)
specifier|private
name|void
name|validateGetAclString
parameter_list|(
name|AccessControlList
name|acl
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|acl
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
operator|new
name|AccessControlList
argument_list|(
name|acl
operator|.
name|getAclString
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAccessControlList ()
specifier|public
name|void
name|testAccessControlList
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlList
name|acl
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|users
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|groups
decl_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"drwho tardis"
argument_list|)
expr_stmt|;
name|users
operator|=
name|acl
operator|.
name|getUsers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
literal|"drwho"
argument_list|)
expr_stmt|;
name|groups
operator|=
name|acl
operator|.
name|getGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
literal|"tardis"
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"drwho"
argument_list|)
expr_stmt|;
name|users
operator|=
name|acl
operator|.
name|getUsers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
literal|"drwho"
argument_list|)
expr_stmt|;
name|groups
operator|=
name|acl
operator|.
name|getGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"drwho "
argument_list|)
expr_stmt|;
name|users
operator|=
name|acl
operator|.
name|getUsers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
literal|"drwho"
argument_list|)
expr_stmt|;
name|groups
operator|=
name|acl
operator|.
name|getGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|" tardis"
argument_list|)
expr_stmt|;
name|users
operator|=
name|acl
operator|.
name|getUsers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|groups
operator|=
name|acl
operator|.
name|getGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
literal|"tardis"
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
decl_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"drwho,joe tardis, users"
argument_list|)
expr_stmt|;
name|users
operator|=
name|acl
operator|.
name|getUsers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|iter
operator|=
name|users
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|,
literal|"drwho"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|,
literal|"joe"
argument_list|)
expr_stmt|;
name|groups
operator|=
name|acl
operator|.
name|getGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|iter
operator|=
name|groups
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|,
literal|"tardis"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|,
literal|"users"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test addUser/Group and removeUser/Group api.    */
annotation|@
name|Test
DECL|method|testAddRemoveAPI ()
specifier|public
name|void
name|testAddRemoveAPI
parameter_list|()
block|{
name|AccessControlList
name|acl
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|users
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|groups
decl_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acl
operator|.
name|getUsers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acl
operator|.
name|getGroups
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|" "
argument_list|,
name|acl
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addUser
argument_list|(
literal|"drwho"
argument_list|)
expr_stmt|;
name|users
operator|=
name|acl
operator|.
name|getUsers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
literal|"drwho"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"drwho "
argument_list|,
name|acl
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addGroup
argument_list|(
literal|"tardis"
argument_list|)
expr_stmt|;
name|groups
operator|=
name|acl
operator|.
name|getGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
literal|"tardis"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"drwho tardis"
argument_list|,
name|acl
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addUser
argument_list|(
literal|"joe"
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addGroup
argument_list|(
literal|"users"
argument_list|)
expr_stmt|;
name|users
operator|=
name|acl
operator|.
name|getUsers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|users
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|,
literal|"drwho"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|,
literal|"joe"
argument_list|)
expr_stmt|;
name|groups
operator|=
name|acl
operator|.
name|getGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|iter
operator|=
name|groups
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|,
literal|"tardis"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|,
literal|"users"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"drwho,joe tardis,users"
argument_list|,
name|acl
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
name|acl
operator|.
name|removeUser
argument_list|(
literal|"joe"
argument_list|)
expr_stmt|;
name|acl
operator|.
name|removeGroup
argument_list|(
literal|"users"
argument_list|)
expr_stmt|;
name|users
operator|=
name|acl
operator|.
name|getUsers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|users
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|users
operator|.
name|contains
argument_list|(
literal|"joe"
argument_list|)
argument_list|)
expr_stmt|;
name|groups
operator|=
name|acl
operator|.
name|getGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
literal|"users"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"drwho tardis"
argument_list|,
name|acl
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
name|acl
operator|.
name|removeGroup
argument_list|(
literal|"tardis"
argument_list|)
expr_stmt|;
name|groups
operator|=
name|acl
operator|.
name|getGroups
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|groups
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
literal|"tardis"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"drwho "
argument_list|,
name|acl
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
name|acl
operator|.
name|removeUser
argument_list|(
literal|"drwho"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|users
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|users
operator|.
name|contains
argument_list|(
literal|"drwho"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acl
operator|.
name|getGroups
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acl
operator|.
name|getUsers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|" "
argument_list|,
name|acl
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests adding/removing wild card as the user/group.    */
annotation|@
name|Test
DECL|method|testAddRemoveWildCard ()
specifier|public
name|void
name|testAddRemoveWildCard
parameter_list|()
block|{
name|AccessControlList
name|acl
init|=
operator|new
name|AccessControlList
argument_list|(
literal|"drwho tardis"
argument_list|)
decl_stmt|;
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
try|try
block|{
name|acl
operator|.
name|addUser
argument_list|(
literal|" * "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|th
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|th
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
name|th
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|acl
operator|.
name|addGroup
argument_list|(
literal|" * "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|th
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|th
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
name|th
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|acl
operator|.
name|removeUser
argument_list|(
literal|" * "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|th
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|th
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
name|th
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|acl
operator|.
name|removeGroup
argument_list|(
literal|" * "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|th
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|th
operator|instanceof
name|IllegalArgumentException
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests adding user/group to an wild card acl.    */
annotation|@
name|Test
DECL|method|testAddRemoveToWildCardACL ()
specifier|public
name|void
name|testAddRemoveToWildCardACL
parameter_list|()
block|{
name|AccessControlList
name|acl
init|=
operator|new
name|AccessControlList
argument_list|(
literal|" * "
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|isAllAllowed
argument_list|()
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|drwho
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"drwho@EXAMPLE.COM"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aliens"
block|}
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|drwho2
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"drwho2@EXAMPLE.COM"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"tardis"
block|}
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addUser
argument_list|(
literal|"drwho"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|isAllAllowed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|getAclString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"drwho"
argument_list|)
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addGroup
argument_list|(
literal|"tardis"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|isAllAllowed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|getAclString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"tardis"
argument_list|)
argument_list|)
expr_stmt|;
name|acl
operator|.
name|removeUser
argument_list|(
literal|"drwho"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|isAllAllowed
argument_list|()
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|drwho
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|.
name|removeGroup
argument_list|(
literal|"tardis"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|isAllAllowed
argument_list|()
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|drwho2
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify the method isUserAllowed()    */
annotation|@
name|Test
DECL|method|testIsUserAllowed ()
specifier|public
name|void
name|testIsUserAllowed
parameter_list|()
block|{
name|AccessControlList
name|acl
decl_stmt|;
name|UserGroupInformation
name|drwho
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"drwho@EXAMPLE.COM"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aliens"
block|,
literal|"humanoids"
block|,
literal|"timelord"
block|}
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|susan
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"susan@EXAMPLE.COM"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aliens"
block|,
literal|"humanoids"
block|,
literal|"timelord"
block|}
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|barbara
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"barbara@EXAMPLE.COM"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"humans"
block|,
literal|"teachers"
block|}
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ian
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"ian@EXAMPLE.COM"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"humans"
block|,
literal|"teachers"
block|}
argument_list|)
decl_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"drwho humanoids"
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|drwho
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|susan
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserNotAllowed
argument_list|(
name|barbara
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserNotAllowed
argument_list|(
name|ian
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"drwho"
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|drwho
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserNotAllowed
argument_list|(
name|susan
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserNotAllowed
argument_list|(
name|barbara
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserNotAllowed
argument_list|(
name|ian
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"drwho "
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|drwho
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserNotAllowed
argument_list|(
name|susan
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserNotAllowed
argument_list|(
name|barbara
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserNotAllowed
argument_list|(
name|ian
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|" humanoids"
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|drwho
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|susan
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserNotAllowed
argument_list|(
name|barbara
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserNotAllowed
argument_list|(
name|ian
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|=
operator|new
name|AccessControlList
argument_list|(
literal|"drwho,ian aliens,teachers"
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|drwho
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|susan
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|barbara
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|assertUserAllowed
argument_list|(
name|ian
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
DECL|method|assertUserAllowed (UserGroupInformation ugi, AccessControlList acl)
specifier|private
name|void
name|assertUserAllowed
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
name|AccessControlList
name|acl
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"User "
operator|+
name|ugi
operator|+
literal|" is not granted the access-control!!"
argument_list|,
name|acl
operator|.
name|isUserAllowed
argument_list|(
name|ugi
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertUserNotAllowed (UserGroupInformation ugi, AccessControlList acl)
specifier|private
name|void
name|assertUserNotAllowed
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
name|AccessControlList
name|acl
parameter_list|)
block|{
name|assertFalse
argument_list|(
literal|"User "
operator|+
name|ugi
operator|+
literal|" is incorrectly granted the access-control!!"
argument_list|,
name|acl
operator|.
name|isUserAllowed
argument_list|(
name|ugi
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

