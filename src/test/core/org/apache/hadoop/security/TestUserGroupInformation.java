begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
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
name|assertTrue
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
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginContext
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
operator|.
name|AuthenticationMethod
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
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenIdentifier
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
DECL|class|TestUserGroupInformation
specifier|public
class|class
name|TestUserGroupInformation
block|{
DECL|field|USER_NAME
specifier|final
specifier|private
specifier|static
name|String
name|USER_NAME
init|=
literal|"user1@HADOOP.APACHE.ORG"
decl_stmt|;
DECL|field|GROUP1_NAME
specifier|final
specifier|private
specifier|static
name|String
name|GROUP1_NAME
init|=
literal|"group1"
decl_stmt|;
DECL|field|GROUP2_NAME
specifier|final
specifier|private
specifier|static
name|String
name|GROUP2_NAME
init|=
literal|"group2"
decl_stmt|;
DECL|field|GROUP3_NAME
specifier|final
specifier|private
specifier|static
name|String
name|GROUP3_NAME
init|=
literal|"group3"
decl_stmt|;
DECL|field|GROUP_NAMES
specifier|final
specifier|private
specifier|static
name|String
index|[]
name|GROUP_NAMES
init|=
operator|new
name|String
index|[]
block|{
name|GROUP1_NAME
block|,
name|GROUP2_NAME
block|,
name|GROUP3_NAME
block|}
decl_stmt|;
static|static
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
literal|"hadoop.security.auth_to_local"
argument_list|,
literal|"RULE:[2:$1@$0](.*@HADOOP.APACHE.ORG)s/@.*//"
operator|+
literal|"RULE:[1:$1@$0](.*@HADOOP.APACHE.ORG)s/@.*//"
operator|+
literal|"DEFAULT"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * given user name - get all the groups.    * Needs to happen before creating the test users    */
annotation|@
name|Test
DECL|method|testGetServerSideGroups ()
specifier|public
name|void
name|testGetServerSideGroups
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// get the user name
name|Process
name|pp
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
literal|"whoami"
argument_list|)
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|pp
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
name|br
operator|.
name|readLine
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// get the groups
name|pp
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
literal|"id -Gn"
argument_list|)
expr_stmt|;
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|pp
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|userName
operator|+
literal|":"
operator|+
name|line
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|line
operator|.
name|split
argument_list|(
literal|"[\\s]"
argument_list|)
control|)
block|{
name|groups
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
specifier|final
name|UserGroupInformation
name|login
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|userName
argument_list|,
name|login
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|gi
init|=
name|login
operator|.
name|getGroupNames
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|,
name|gi
operator|.
name|length
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
name|gi
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|groups
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|gi
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
specifier|final
name|UserGroupInformation
name|fakeUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"foo.bar"
argument_list|)
decl_stmt|;
name|fakeUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|current
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|current
operator|.
name|equals
argument_list|(
name|login
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|current
argument_list|,
name|fakeUser
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|current
operator|.
name|getGroupNames
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test login method */
annotation|@
name|Test
DECL|method|testLogin ()
specifier|public
name|void
name|testLogin
parameter_list|()
throws|throws
name|Exception
block|{
comment|// login from unix
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ugi
operator|.
name|getGroupNames
argument_list|()
operator|.
name|length
operator|>=
literal|1
argument_list|)
expr_stmt|;
comment|// ensure that doAs works correctly
name|UserGroupInformation
name|userGroupInfo
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|USER_NAME
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|curUGI
init|=
name|userGroupInfo
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|UserGroupInformation
argument_list|>
argument_list|()
block|{
specifier|public
name|UserGroupInformation
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// make sure in the scope of the doAs, the right user is current
name|assertEquals
argument_list|(
name|curUGI
argument_list|,
name|userGroupInfo
argument_list|)
expr_stmt|;
comment|// make sure it is not the same as the login user
name|assertFalse
argument_list|(
name|curUGI
operator|.
name|equals
argument_list|(
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** test constructor */
annotation|@
name|Test
DECL|method|testConstructor ()
specifier|public
name|void
name|testConstructor
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"user2/cron@HADOOP.APACHE.ORG"
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
comment|// make sure the short and full user names are correct
name|assertEquals
argument_list|(
literal|"user2/cron@HADOOP.APACHE.ORG"
argument_list|,
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"user2"
argument_list|,
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|USER_NAME
argument_list|,
name|GROUP_NAMES
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"user1"
argument_list|,
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
comment|// failure test
name|testConstructorFailures
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|testConstructorFailures
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstructorFailures (String userName)
specifier|private
name|void
name|testConstructorFailures
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|userName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gotException
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEquals ()
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|uugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|USER_NAME
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|uugi
argument_list|,
name|uugi
argument_list|)
expr_stmt|;
comment|// The subjects should be different, so this should fail
name|UserGroupInformation
name|ugi2
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|USER_NAME
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|uugi
operator|.
name|equals
argument_list|(
name|ugi2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|uugi
operator|.
name|hashCode
argument_list|()
operator|==
name|ugi2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// two ugi that have the same subject need to be equal
name|UserGroupInformation
name|ugi3
init|=
operator|new
name|UserGroupInformation
argument_list|(
name|uugi
operator|.
name|getSubject
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|uugi
argument_list|,
name|ugi3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uugi
operator|.
name|hashCode
argument_list|()
argument_list|,
name|ugi3
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEqualsWithRealUser ()
specifier|public
name|void
name|testEqualsWithRealUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|realUgi1
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"RealUser"
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|realUgi2
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"RealUser"
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUgi1
init|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|USER_NAME
argument_list|,
name|realUgi1
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUgi2
init|=
operator|new
name|UserGroupInformation
argument_list|(
name|proxyUgi1
operator|.
name|getSubject
argument_list|()
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|remoteUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|USER_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|proxyUgi1
argument_list|,
name|proxyUgi2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|remoteUgi
operator|.
name|equals
argument_list|(
name|proxyUgi1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGettingGroups ()
specifier|public
name|void
name|testGettingGroups
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|uugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|USER_NAME
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|USER_NAME
argument_list|,
name|uugi
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
name|GROUP1_NAME
block|,
name|GROUP2_NAME
block|,
name|GROUP3_NAME
block|}
argument_list|,
name|uugi
operator|.
name|getGroupNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// from Mockito mocks
annotation|@
name|Test
DECL|method|testUGITokens ()
specifier|public
parameter_list|<
name|T
extends|extends
name|TokenIdentifier
parameter_list|>
name|void
name|testUGITokens
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"TheDoctor"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"TheTARDIS"
block|}
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|T
argument_list|>
name|t1
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|T
argument_list|>
name|t2
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|z
init|=
name|ugi
operator|.
name|getTokens
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|z
operator|.
name|contains
argument_list|(
name|t1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|z
operator|.
name|contains
argument_list|(
name|t2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|z
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|z
operator|.
name|remove
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to modify token collection from UGI"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|// Can't modify tokens
block|}
comment|// ensure that the tokens are passed through doAs
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|otherSet
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getTokens
argument_list|()
return|;
block|}
block|}
block|)
function|;
name|assertTrue
argument_list|(
name|otherSet
operator|.
name|contains
argument_list|(
name|t1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|otherSet
operator|.
name|contains
argument_list|(
name|t2
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_class

begin_function
annotation|@
name|Test
DECL|method|testTokenIdentifiers ()
specifier|public
name|void
name|testTokenIdentifiers
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"TheDoctor"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"TheTARDIS"
block|}
argument_list|)
decl_stmt|;
name|TokenIdentifier
name|t1
init|=
name|mock
argument_list|(
name|TokenIdentifier
operator|.
name|class
argument_list|)
decl_stmt|;
name|TokenIdentifier
name|t2
init|=
name|mock
argument_list|(
name|TokenIdentifier
operator|.
name|class
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addTokenIdentifier
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|addTokenIdentifier
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|TokenIdentifier
argument_list|>
name|z
init|=
name|ugi
operator|.
name|getTokenIdentifiers
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|z
operator|.
name|contains
argument_list|(
name|t1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|z
operator|.
name|contains
argument_list|(
name|t2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|z
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// ensure that the token identifiers are passed through doAs
name|Collection
argument_list|<
name|TokenIdentifier
argument_list|>
name|otherSet
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Collection
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Collection
argument_list|<
name|TokenIdentifier
argument_list|>
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getTokenIdentifiers
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|otherSet
operator|.
name|contains
argument_list|(
name|t1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|otherSet
operator|.
name|contains
argument_list|(
name|t2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|otherSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
DECL|method|testUGIAuthMethod ()
specifier|public
name|void
name|testUGIAuthMethod
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
specifier|final
name|AuthenticationMethod
name|am
init|=
name|AuthenticationMethod
operator|.
name|KERBEROS
decl_stmt|;
name|ugi
operator|.
name|setAuthenticationMethod
argument_list|(
name|am
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|am
argument_list|,
name|ugi
operator|.
name|getAuthenticationMethod
argument_list|()
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|am
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getAuthenticationMethod
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
DECL|method|testUGIAuthMethodInRealUser ()
specifier|public
name|void
name|testUGIAuthMethodInRealUser
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|proxyUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
literal|"proxy"
argument_list|,
name|ugi
argument_list|)
decl_stmt|;
specifier|final
name|AuthenticationMethod
name|am
init|=
name|AuthenticationMethod
operator|.
name|KERBEROS
decl_stmt|;
name|ugi
operator|.
name|setAuthenticationMethod
argument_list|(
name|am
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|am
argument_list|,
name|ugi
operator|.
name|getAuthenticationMethod
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|AuthenticationMethod
operator|.
name|PROXY
argument_list|,
name|proxyUgi
operator|.
name|getAuthenticationMethod
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|am
argument_list|,
name|UserGroupInformation
operator|.
name|getRealAuthenticationMethod
argument_list|(
name|proxyUgi
argument_list|)
argument_list|)
expr_stmt|;
name|proxyUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|AuthenticationMethod
operator|.
name|PROXY
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getAuthenticationMethod
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|am
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getRealUser
argument_list|()
operator|.
name|getAuthenticationMethod
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|proxyUgi2
init|=
operator|new
name|UserGroupInformation
argument_list|(
name|proxyUgi
operator|.
name|getSubject
argument_list|()
argument_list|)
decl_stmt|;
name|proxyUgi2
operator|.
name|setAuthenticationMethod
argument_list|(
name|AuthenticationMethod
operator|.
name|PROXY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|proxyUgi
argument_list|,
name|proxyUgi2
argument_list|)
expr_stmt|;
comment|// Equality should work if authMethod is null
name|UserGroupInformation
name|realugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|proxyUgi3
init|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
literal|"proxyAnother"
argument_list|,
name|realugi
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUgi4
init|=
operator|new
name|UserGroupInformation
argument_list|(
name|proxyUgi3
operator|.
name|getSubject
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|proxyUgi3
argument_list|,
name|proxyUgi4
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
DECL|method|testLoginObjectInSubject ()
specifier|public
name|void
name|testLoginObjectInSubject
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|loginUgi
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|anotherUgi
init|=
operator|new
name|UserGroupInformation
argument_list|(
name|loginUgi
operator|.
name|getSubject
argument_list|()
argument_list|)
decl_stmt|;
name|LoginContext
name|login1
init|=
name|loginUgi
operator|.
name|getSubject
argument_list|()
operator|.
name|getPrincipals
argument_list|(
name|User
operator|.
name|class
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getLogin
argument_list|()
decl_stmt|;
name|LoginContext
name|login2
init|=
name|anotherUgi
operator|.
name|getSubject
argument_list|()
operator|.
name|getPrincipals
argument_list|(
name|User
operator|.
name|class
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getLogin
argument_list|()
decl_stmt|;
comment|//login1 and login2 must be same instances
name|Assert
operator|.
name|assertTrue
argument_list|(
name|login1
operator|==
name|login2
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
DECL|method|testLoginModuleCommit ()
specifier|public
name|void
name|testLoginModuleCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|loginUgi
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
name|User
name|user1
init|=
name|loginUgi
operator|.
name|getSubject
argument_list|()
operator|.
name|getPrincipals
argument_list|(
name|User
operator|.
name|class
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|LoginContext
name|login
init|=
name|user1
operator|.
name|getLogin
argument_list|()
decl_stmt|;
name|login
operator|.
name|logout
argument_list|()
expr_stmt|;
name|login
operator|.
name|login
argument_list|()
expr_stmt|;
name|User
name|user2
init|=
name|loginUgi
operator|.
name|getSubject
argument_list|()
operator|.
name|getPrincipals
argument_list|(
name|User
operator|.
name|class
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// user1 and user2 must be same instances.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|user1
operator|==
name|user2
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

