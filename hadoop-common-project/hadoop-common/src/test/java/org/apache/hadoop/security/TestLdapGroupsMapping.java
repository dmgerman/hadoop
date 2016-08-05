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
name|CONNECTION_TIMEOUT
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
name|READ_TIMEOUT
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
name|test
operator|.
name|GenericTestUtils
operator|.
name|assertExceptionContains
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
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
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
name|Matchers
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
name|IOException
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
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|javax
operator|.
name|naming
operator|.
name|CommunicationException
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
name|io
operator|.
name|IOUtils
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
name|Assert
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
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|TestLdapGroupsMapping
specifier|public
class|class
name|TestLdapGroupsMapping
extends|extends
name|TestLdapGroupsMappingBase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestLdapGroupsMapping
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * To construct a LDAP InitialDirContext object, it will firstly initiate a    * protocol session to server for authentication. After a session is    * established, a method of authentication is negotiated between the server    * and the client. When the client is authenticated, the LDAP server will send    * a bind response, whose message contents are bytes as the    * {@link #AUTHENTICATE_SUCCESS_MSG}. After receiving this bind response    * message, the LDAP context is considered connected to the server and thus    * can issue query requests for determining group membership.    */
DECL|field|AUTHENTICATE_SUCCESS_MSG
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|AUTHENTICATE_SUCCESS_MSG
init|=
block|{
literal|48
block|,
literal|12
block|,
literal|2
block|,
literal|1
block|,
literal|1
block|,
literal|97
block|,
literal|7
block|,
literal|10
block|,
literal|1
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|}
decl_stmt|;
annotation|@
name|Before
DECL|method|setupMocks ()
specifier|public
name|void
name|setupMocks
parameter_list|()
throws|throws
name|NamingException
block|{
name|when
argument_list|(
name|getUserSearchResult
argument_list|()
operator|.
name|getNameInNamespace
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"CN=some_user,DC=test,DC=com"
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
name|IOException
throws|,
name|NamingException
block|{
comment|// The search functionality of the mock context is reused, so we will
comment|// return the user NamingEnumeration first, and then the group
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
name|thenReturn
argument_list|(
name|getUserNames
argument_list|()
argument_list|,
name|getGroupNames
argument_list|()
argument_list|)
expr_stmt|;
name|doTestGetGroups
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getTestGroups
argument_list|()
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetGroupsWithHierarchy ()
specifier|public
name|void
name|testGetGroupsWithHierarchy
parameter_list|()
throws|throws
name|IOException
throws|,
name|NamingException
block|{
comment|// The search functionality of the mock context is reused, so we will
comment|// return the user NamingEnumeration first, and then the group
comment|// The parent search is run once for each level, and is a different search
comment|// The parent group is returned once for each group, yet the final list
comment|// should be unique
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
name|thenReturn
argument_list|(
name|getUserNames
argument_list|()
argument_list|,
name|getGroupNames
argument_list|()
argument_list|)
expr_stmt|;
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
name|SearchControls
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getParentGroupNames
argument_list|()
argument_list|)
expr_stmt|;
name|doTestGetGroupsWithParent
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getTestParentGroups
argument_list|()
argument_list|)
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetGroupsWithConnectionClosed ()
specifier|public
name|void
name|testGetGroupsWithConnectionClosed
parameter_list|()
throws|throws
name|IOException
throws|,
name|NamingException
block|{
comment|// The case mocks connection is closed/gc-ed, so the first search call throws CommunicationException,
comment|// then after reconnected return the user NamingEnumeration first, and then the group
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
name|thenThrow
argument_list|(
operator|new
name|CommunicationException
argument_list|(
literal|"Connection is closed"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getUserNames
argument_list|()
argument_list|,
name|getGroupNames
argument_list|()
argument_list|)
expr_stmt|;
comment|// Although connection is down but after reconnected
comment|// it still should retrieve the result groups
comment|// 1 is the first failure call
name|doTestGetGroups
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getTestGroups
argument_list|()
argument_list|)
argument_list|,
literal|1
operator|+
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetGroupsWithLdapDown ()
specifier|public
name|void
name|testGetGroupsWithLdapDown
parameter_list|()
throws|throws
name|IOException
throws|,
name|NamingException
block|{
comment|// This mocks the case where Ldap server is down, and always throws CommunicationException
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
name|thenThrow
argument_list|(
operator|new
name|CommunicationException
argument_list|(
literal|"Connection is closed"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ldap server is down, no groups should be retrieved
name|doTestGetGroups
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
argument_list|,
name|LdapGroupsMapping
operator|.
name|RECONNECT_RETRY_COUNT
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestGetGroups (List<String> expectedGroups, int searchTimes)
specifier|private
name|void
name|doTestGetGroups
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|expectedGroups
parameter_list|,
name|int
name|searchTimes
parameter_list|)
throws|throws
name|IOException
throws|,
name|NamingException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Set this, so we don't throw an exception
name|conf
operator|.
name|set
argument_list|(
name|LdapGroupsMapping
operator|.
name|LDAP_URL_KEY
argument_list|,
literal|"ldap://test"
argument_list|)
expr_stmt|;
name|LdapGroupsMapping
name|groupsMapping
init|=
name|getGroupsMapping
argument_list|()
decl_stmt|;
name|groupsMapping
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Username is arbitrary, since the spy is mocked to respond the same,
comment|// regardless of input
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
literal|"some_user"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedGroups
argument_list|,
name|groups
argument_list|)
expr_stmt|;
comment|// We should have searched for a user, and then two groups
name|verify
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|times
argument_list|(
name|searchTimes
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
DECL|method|doTestGetGroupsWithParent (List<String> expectedGroups, int searchTimesGroup, int searchTimesParentGroup)
specifier|private
name|void
name|doTestGetGroupsWithParent
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|expectedGroups
parameter_list|,
name|int
name|searchTimesGroup
parameter_list|,
name|int
name|searchTimesParentGroup
parameter_list|)
throws|throws
name|IOException
throws|,
name|NamingException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Set this, so we don't throw an exception
name|conf
operator|.
name|set
argument_list|(
name|LdapGroupsMapping
operator|.
name|LDAP_URL_KEY
argument_list|,
literal|"ldap://test"
argument_list|)
expr_stmt|;
comment|// Set the config to get parents 1 level up
name|conf
operator|.
name|setInt
argument_list|(
name|LdapGroupsMapping
operator|.
name|GROUP_HIERARCHY_LEVELS_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|LdapGroupsMapping
name|groupsMapping
init|=
name|getGroupsMapping
argument_list|()
decl_stmt|;
name|groupsMapping
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Username is arbitrary, since the spy is mocked to respond the same,
comment|// regardless of input
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
literal|"some_user"
argument_list|)
decl_stmt|;
comment|// compare lists, ignoring the order
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|expectedGroups
argument_list|)
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|groups
argument_list|)
argument_list|)
expr_stmt|;
comment|// We should have searched for a user, and group
name|verify
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|times
argument_list|(
name|searchTimesGroup
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
comment|// One groups search for the parent group should have been done
name|verify
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|times
argument_list|(
name|searchTimesParentGroup
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
name|SearchControls
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExtractPassword ()
specifier|public
name|void
name|testExtractPassword
parameter_list|()
throws|throws
name|IOException
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
literal|"secret.txt"
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
literal|"hadoop"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|LdapGroupsMapping
name|mapping
init|=
operator|new
name|LdapGroupsMapping
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"hadoop"
argument_list|,
name|mapping
operator|.
name|extractPassword
argument_list|(
name|secretFile
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConfGetPassword ()
specifier|public
name|void
name|testConfGetPassword
parameter_list|()
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
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
block|{
literal|'b'
block|,
literal|'i'
block|,
literal|'n'
block|,
literal|'d'
block|,
literal|'p'
block|,
literal|'a'
block|,
literal|'s'
block|,
literal|'s'
block|}
decl_stmt|;
name|char
index|[]
name|storepass
init|=
block|{
literal|'s'
block|,
literal|'t'
block|,
literal|'o'
block|,
literal|'r'
block|,
literal|'e'
block|,
literal|'p'
block|,
literal|'a'
block|,
literal|'s'
block|,
literal|'s'
block|}
decl_stmt|;
comment|// ensure that we get nulls when the key isn't there
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|LdapGroupsMapping
operator|.
name|BIND_PASSWORD_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|LdapGroupsMapping
operator|.
name|LDAP_KEYSTORE_PASSWORD_KEY
argument_list|)
argument_list|)
expr_stmt|;
comment|// create new aliases
try|try
block|{
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|LdapGroupsMapping
operator|.
name|BIND_PASSWORD_KEY
argument_list|,
name|bindpass
argument_list|)
expr_stmt|;
name|provider
operator|.
name|createCredentialEntry
argument_list|(
name|LdapGroupsMapping
operator|.
name|LDAP_KEYSTORE_PASSWORD_KEY
argument_list|,
name|storepass
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|// make sure we get back the right key
name|assertArrayEquals
argument_list|(
name|bindpass
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|LdapGroupsMapping
operator|.
name|BIND_PASSWORD_KEY
argument_list|)
operator|.
name|getCredential
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|storepass
argument_list|,
name|provider
operator|.
name|getCredentialEntry
argument_list|(
name|LdapGroupsMapping
operator|.
name|LDAP_KEYSTORE_PASSWORD_KEY
argument_list|)
operator|.
name|getCredential
argument_list|()
argument_list|)
expr_stmt|;
name|LdapGroupsMapping
name|mapping
init|=
operator|new
name|LdapGroupsMapping
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bindpass"
argument_list|,
name|mapping
operator|.
name|getPassword
argument_list|(
name|conf
argument_list|,
name|LdapGroupsMapping
operator|.
name|BIND_PASSWORD_KEY
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"storepass"
argument_list|,
name|mapping
operator|.
name|getPassword
argument_list|(
name|conf
argument_list|,
name|LdapGroupsMapping
operator|.
name|LDAP_KEYSTORE_PASSWORD_KEY
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// let's make sure that a password that doesn't exist returns an
comment|// empty string as currently expected and used to trigger a call to
comment|// extract password
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|mapping
operator|.
name|getPassword
argument_list|(
name|conf
argument_list|,
literal|"invalid-alias"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that if the {@link LdapGroupsMapping#CONNECTION_TIMEOUT} is set in the    * configuration, the LdapGroupsMapping connection will timeout by this value    * if it does not get a LDAP response from the server.    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testLdapConnectionTimeout ()
specifier|public
name|void
name|testLdapConnectionTimeout
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|int
name|connectionTimeoutMs
init|=
literal|3
operator|*
literal|1000
decl_stmt|;
comment|// 3s
try|try
init|(
name|ServerSocket
name|serverSock
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
init|)
block|{
specifier|final
name|CountDownLatch
name|finLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Below we create a LDAP server which will accept a client request;
comment|// but it will never reply to the bind (connect) request.
comment|// Client of this LDAP server is expected to get a connection timeout.
specifier|final
name|Thread
name|ldapServer
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
try|try
init|(
name|Socket
name|ignored
init|=
name|serverSock
operator|.
name|accept
argument_list|()
init|)
block|{
name|finLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|ldapServer
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|LdapGroupsMapping
name|mapping
init|=
operator|new
name|LdapGroupsMapping
argument_list|()
decl_stmt|;
specifier|final
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
name|LdapGroupsMapping
operator|.
name|LDAP_URL_KEY
argument_list|,
literal|"ldap://localhost:"
operator|+
name|serverSock
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|CONNECTION_TIMEOUT
argument_list|,
name|connectionTimeoutMs
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|mapping
operator|.
name|doGetGroups
argument_list|(
literal|"hadoop"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The LDAP query should have timed out!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ne
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got the exception while LDAP querying: "
argument_list|,
name|ne
argument_list|)
expr_stmt|;
name|assertExceptionContains
argument_list|(
literal|"LDAP response read timed out, timeout used:"
operator|+
name|connectionTimeoutMs
operator|+
literal|"ms"
argument_list|,
name|ne
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ne
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"remaining name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|finLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
name|ldapServer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that if the {@link LdapGroupsMapping#READ_TIMEOUT} is set in the    * configuration, the LdapGroupsMapping query will timeout by this value if    * it does not get a LDAP response from the server.    *    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testLdapReadTimeout ()
specifier|public
name|void
name|testLdapReadTimeout
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|int
name|readTimeoutMs
init|=
literal|4
operator|*
literal|1000
decl_stmt|;
comment|// 4s
try|try
init|(
name|ServerSocket
name|serverSock
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
init|)
block|{
specifier|final
name|CountDownLatch
name|finLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Below we create a LDAP server which will accept a client request,
comment|// authenticate it successfully; but it will never reply to the following
comment|// query request.
comment|// Client of this LDAP server is expected to get a read timeout.
specifier|final
name|Thread
name|ldapServer
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
try|try
init|(
name|Socket
name|clientSock
init|=
name|serverSock
operator|.
name|accept
argument_list|()
init|)
block|{
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|clientSock
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|clientSock
operator|.
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
name|AUTHENTICATE_SUCCESS_MSG
argument_list|)
expr_stmt|;
name|finLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|ldapServer
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|LdapGroupsMapping
name|mapping
init|=
operator|new
name|LdapGroupsMapping
argument_list|()
decl_stmt|;
specifier|final
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
name|LdapGroupsMapping
operator|.
name|LDAP_URL_KEY
argument_list|,
literal|"ldap://localhost:"
operator|+
name|serverSock
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|READ_TIMEOUT
argument_list|,
name|readTimeoutMs
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|mapping
operator|.
name|doGetGroups
argument_list|(
literal|"hadoop"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The LDAP query should have timed out!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ne
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got the exception while LDAP querying: "
argument_list|,
name|ne
argument_list|)
expr_stmt|;
name|assertExceptionContains
argument_list|(
literal|"LDAP response read timed out, timeout used:"
operator|+
name|readTimeoutMs
operator|+
literal|"ms"
argument_list|,
name|ne
argument_list|)
expr_stmt|;
name|assertExceptionContains
argument_list|(
literal|"remaining name"
argument_list|,
name|ne
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|finLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
name|ldapServer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Make sure that when    * {@link Configuration#getPassword(String)} throws an IOException,    * {@link LdapGroupsMapping#setConf(Configuration)} does not throw an NPE.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testSetConf ()
specifier|public
name|void
name|testSetConf
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
name|Configuration
name|mockConf
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockConf
operator|.
name|getPassword
argument_list|(
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"injected IOException"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set a dummy LDAP server URL.
name|mockConf
operator|.
name|set
argument_list|(
name|LdapGroupsMapping
operator|.
name|LDAP_URL_KEY
argument_list|,
literal|"ldap://test"
argument_list|)
expr_stmt|;
name|LdapGroupsMapping
name|groupsMapping
init|=
name|getGroupsMapping
argument_list|()
decl_stmt|;
name|groupsMapping
operator|.
name|setConf
argument_list|(
name|mockConf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

