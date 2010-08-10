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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestAccessControlList
specifier|public
class|class
name|TestAccessControlList
extends|extends
name|TestCase
block|{
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
literal|""
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
literal|""
argument_list|,
name|acl
operator|.
name|toString
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
literal|"drwho"
argument_list|,
name|acl
operator|.
name|toString
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
name|toString
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
name|toString
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
name|toString
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
literal|"drwho"
argument_list|,
name|acl
operator|.
name|toString
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
literal|""
argument_list|,
name|acl
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests adding/removing wild card as the user/group.    */
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
literal|"drwho@APACHE.ORG"
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
literal|"drwho2@APACHE.ORG"
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
name|toString
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
name|toString
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
literal|"drwho@APACHE.ORG"
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
literal|"susan@APACHE.ORG"
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
literal|"barbara@APACHE.ORG"
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
literal|"ian@APACHE.ORG"
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

