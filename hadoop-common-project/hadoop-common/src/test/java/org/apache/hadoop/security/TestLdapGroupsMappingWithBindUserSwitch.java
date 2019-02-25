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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
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
name|Path
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
name|alias
operator|.
name|CredentialProvider
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
name|alias
operator|.
name|CredentialProviderFactory
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
name|alias
operator|.
name|JavaKeyStoreProvider
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
name|test
operator|.
name|GenericTestUtils
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
import|import
name|javax
operator|.
name|naming
operator|.
name|AuthenticationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|SearchControls
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Iterator
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|LdapGroupsMapping
operator|.
name|BIND_PASSWORD_ALIAS_SUFFIX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|LdapGroupsMapping
operator|.
name|BIND_PASSWORD_FILE_SUFFIX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|LdapGroupsMapping
operator|.
name|BIND_PASSWORD_SUFFIX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|LdapGroupsMapping
operator|.
name|BIND_USERS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|LdapGroupsMapping
operator|.
name|BIND_USER_SUFFIX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|LdapGroupsMapping
operator|.
name|LDAP_NUM_ATTEMPTS_KEY
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
name|assertNull
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
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyString
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
name|times
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
name|verify
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

begin_comment
comment|/**  * Test functionality for switching bind user information if  * AuthenticationExceptions are encountered.  */
end_comment

begin_class
DECL|class|TestLdapGroupsMappingWithBindUserSwitch
specifier|public
class|class
name|TestLdapGroupsMappingWithBindUserSwitch
extends|extends
name|TestLdapGroupsMappingBase
block|{
DECL|field|TEST_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_USER_NAME
init|=
literal|"some_user"
decl_stmt|;
annotation|@
name|Test
DECL|method|testIncorrectConfiguration ()
specifier|public
name|void
name|testIncorrectConfiguration
parameter_list|()
block|{
comment|// No bind user configured for user2
name|Configuration
name|conf
init|=
name|getBaseConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
argument_list|,
literal|"user1,user2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".user1"
operator|+
name|BIND_USER_SUFFIX
argument_list|,
literal|"bindUsername1"
argument_list|)
expr_stmt|;
name|LdapGroupsMapping
name|groupsMapping
init|=
operator|new
name|LdapGroupsMapping
argument_list|()
decl_stmt|;
try|try
block|{
name|groupsMapping
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|groupsMapping
operator|.
name|getGroups
argument_list|(
name|TEST_USER_NAME
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed with RuntimeException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Bind username or password not configured for user: user2"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBindUserSwitchPasswordPlaintext ()
specifier|public
name|void
name|testBindUserSwitchPasswordPlaintext
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getBaseConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
argument_list|,
literal|"user1,user2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".user1"
operator|+
name|BIND_USER_SUFFIX
argument_list|,
literal|"bindUsername1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".user2"
operator|+
name|BIND_USER_SUFFIX
argument_list|,
literal|"bindUsername2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".user1"
operator|+
name|BIND_PASSWORD_SUFFIX
argument_list|,
literal|"bindPassword1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".user2"
operator|+
name|BIND_PASSWORD_SUFFIX
argument_list|,
literal|"bindPassword2"
argument_list|)
expr_stmt|;
name|doTestBindUserSwitch
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"bindUsername1"
argument_list|,
literal|"bindUsername2"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"bindPassword1"
argument_list|,
literal|"bindPassword2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBindUserSwitchPasswordFromAlias ()
specifier|public
name|void
name|testBindUserSwitchPasswordFromAlias
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getBaseConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
argument_list|,
literal|"joe,lukas"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".joe"
operator|+
name|BIND_USER_SUFFIX
argument_list|,
literal|"joeBindUsername"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".lukas"
operator|+
name|BIND_USER_SUFFIX
argument_list|,
literal|"lukasBindUsername"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".joe"
operator|+
name|BIND_PASSWORD_ALIAS_SUFFIX
argument_list|,
literal|"joeBindPasswordAlias"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".lukas"
operator|+
name|BIND_PASSWORD_ALIAS_SUFFIX
argument_list|,
literal|"lukasBindPasswordAlias"
argument_list|)
expr_stmt|;
name|setupCredentialProvider
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|createCredentialForAlias
argument_list|(
name|conf
argument_list|,
literal|"joeBindPasswordAlias"
argument_list|,
literal|"joeBindPassword"
argument_list|)
expr_stmt|;
name|createCredentialForAlias
argument_list|(
name|conf
argument_list|,
literal|"lukasBindPasswordAlias"
argument_list|,
literal|"lukasBindPassword"
argument_list|)
expr_stmt|;
comment|// Simulate 2 failures to test cycling through the bind users
name|List
argument_list|<
name|String
argument_list|>
name|expectedBindUsers
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"joeBindUsername"
argument_list|,
literal|"lukasBindUsername"
argument_list|,
literal|"joeBindUsername"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedBindPasswords
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"joeBindPassword"
argument_list|,
literal|"lukasBindPassword"
argument_list|,
literal|"joeBindPassword"
argument_list|)
decl_stmt|;
name|doTestBindUserSwitch
argument_list|(
name|conf
argument_list|,
literal|2
argument_list|,
name|expectedBindUsers
argument_list|,
name|expectedBindPasswords
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBindUserSwitchPasswordFromFile ()
specifier|public
name|void
name|testBindUserSwitchPasswordFromFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getBaseConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|LDAP_NUM_ATTEMPTS_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
argument_list|,
literal|"bob,alice"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".bob"
operator|+
name|BIND_USER_SUFFIX
argument_list|,
literal|"bobUsername"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".alice"
operator|+
name|BIND_USER_SUFFIX
argument_list|,
literal|"aliceUsername"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".bob"
operator|+
name|BIND_PASSWORD_FILE_SUFFIX
argument_list|,
name|createPasswordFile
argument_list|(
literal|"bobPasswordFile1.txt"
argument_list|,
literal|"bobBindPassword"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|BIND_USERS_KEY
operator|+
literal|".alice"
operator|+
name|BIND_PASSWORD_FILE_SUFFIX
argument_list|,
name|createPasswordFile
argument_list|(
literal|"alicePasswordFile2.txt"
argument_list|,
literal|"aliceBindPassword"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Simulate 4 failures to test cycling through the bind users
name|List
argument_list|<
name|String
argument_list|>
name|expectedBindUsers
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"bobUsername"
argument_list|,
literal|"aliceUsername"
argument_list|,
literal|"bobUsername"
argument_list|,
literal|"aliceUsername"
argument_list|,
literal|"bobUsername"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedBindPasswords
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"bobBindPassword"
argument_list|,
literal|"aliceBindPassword"
argument_list|,
literal|"bobBindPassword"
argument_list|,
literal|"aliceBindPassword"
argument_list|,
literal|"bobBindPassword"
argument_list|)
decl_stmt|;
name|doTestBindUserSwitch
argument_list|(
name|conf
argument_list|,
literal|4
argument_list|,
name|expectedBindUsers
argument_list|,
name|expectedBindPasswords
argument_list|)
expr_stmt|;
block|}
DECL|method|setupCredentialProvider (Configuration conf)
specifier|private
name|void
name|setupCredentialProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|File
name|testDir
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|jksPath
init|=
operator|new
name|Path
argument_list|(
name|testDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test.jks"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|ourUrl
init|=
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://file"
operator|+
name|jksPath
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"test.jks"
argument_list|)
decl_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CredentialProviderFactory
operator|.
name|CREDENTIAL_PROVIDER_PATH
argument_list|,
name|ourUrl
argument_list|)
expr_stmt|;
block|}
DECL|method|createCredentialForAlias ( Configuration conf, String alias, String password)
specifier|private
name|void
name|createCredentialForAlias
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|alias
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
block|{
name|CredentialProvider
name|provider
init|=
name|CredentialProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|char
index|[]
name|bindpass
init|=
name|password
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
comment|// Ensure that we get null when the key isn't there
name|assertNull
argument_list|(
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|alias
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create credential for the alias
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|alias
argument_list|,
name|bindpass
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Make sure we get back the right key
name|assertArrayEquals
argument_list|(
name|bindpass
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|alias
argument_list|)
operator|.
name|getCredential
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createPasswordFile (String filename, String password)
specifier|private
name|String
name|createPasswordFile
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|testDir
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|()
decl_stmt|;
name|testDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|secretFile
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|secretFile
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|secretFile
operator|.
name|getPath
argument_list|()
return|;
block|}
DECL|method|doTestBindUserSwitch ( Configuration conf, Integer numFailures, List<String> expectedBindUsers, List<String> expectedBindPasswords)
specifier|private
name|void
name|doTestBindUserSwitch
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Integer
name|numFailures
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedBindUsers
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedBindPasswords
parameter_list|)
throws|throws
name|NamingException
block|{
name|doTestBindUserSwitch
argument_list|(
name|conf
argument_list|,
name|numFailures
argument_list|,
name|Iterators
operator|.
name|cycle
argument_list|(
name|expectedBindUsers
argument_list|)
argument_list|,
name|Iterators
operator|.
name|cycle
argument_list|(
name|expectedBindPasswords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *    * @param conf Configuration to be used    * @param numFailures number of AuthenticationException failures to simulate    * @param expectedBindUsers expected sequence of distinguished user names    *                          when binding to LDAP    * @param expectedBindPasswords expected sequence of passwords to be used when    *                              binding to LDAP    * @throws NamingException from DirContext.search()    */
DECL|method|doTestBindUserSwitch ( Configuration conf, Integer numFailures, Iterator<String> expectedBindUsers, Iterator<String> expectedBindPasswords)
specifier|private
name|void
name|doTestBindUserSwitch
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Integer
name|numFailures
parameter_list|,
name|Iterator
argument_list|<
name|String
argument_list|>
name|expectedBindUsers
parameter_list|,
name|Iterator
argument_list|<
name|String
argument_list|>
name|expectedBindPasswords
parameter_list|)
throws|throws
name|NamingException
block|{
name|DummyLdapCtxFactory
operator|.
name|setExpectedBindUser
argument_list|(
name|expectedBindUsers
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|DummyLdapCtxFactory
operator|.
name|setExpectedBindPassword
argument_list|(
name|expectedBindPasswords
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|failuresLeft
init|=
operator|new
name|AtomicInteger
argument_list|(
name|numFailures
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|getContext
argument_list|()
operator|.
name|search
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|SearchControls
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocationOnMock
lambda|->
block|{
if|if
condition|(
name|failuresLeft
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DummyLdapCtxFactory
operator|.
name|setExpectedBindUser
argument_list|(
name|expectedBindUsers
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|DummyLdapCtxFactory
operator|.
name|setExpectedBindPassword
argument_list|(
name|expectedBindPasswords
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|failuresLeft
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|AuthenticationException
argument_list|()
throw|;
block|}
comment|// Return userNames for the first successful search()
if|if
condition|(
name|failuresLeft
operator|.
name|getAndDecrement
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|getUserNames
argument_list|()
return|;
block|}
comment|// Return groupNames for the second successful search()
return|return
name|getGroupNames
argument_list|()
return|;
block|}
argument_list|)
expr_stmt|;
name|LdapGroupsMapping
name|groupsMapping
init|=
operator|new
name|LdapGroupsMapping
argument_list|()
decl_stmt|;
name|groupsMapping
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|groupsMapping
operator|.
name|getGroups
argument_list|(
name|TEST_USER_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"group1"
argument_list|,
literal|"group2"
argument_list|)
argument_list|,
name|groups
argument_list|)
expr_stmt|;
comment|// There will be one search() call for each failure and
comment|// 2 calls for the successful case; one for retrieving the
comment|// user and one for retrieving their groups.
name|int
name|numExpectedSearchCalls
init|=
name|numFailures
operator|+
literal|2
decl_stmt|;
name|verify
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|times
argument_list|(
name|numExpectedSearchCalls
argument_list|)
argument_list|)
operator|.
name|search
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|SearchControls
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

