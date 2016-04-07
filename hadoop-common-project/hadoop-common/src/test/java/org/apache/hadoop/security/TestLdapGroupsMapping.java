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
name|mockito
operator|.
name|Mockito
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
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|SearchResult
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
name|SearchResult
name|mockUserResult
init|=
name|mock
argument_list|(
name|SearchResult
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockUserNamingEnum
operator|.
name|nextElement
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockUserResult
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockUserResult
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
name|mockContext
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
name|mockUserNamingEnum
argument_list|,
name|mockGroupNamingEnum
argument_list|)
expr_stmt|;
name|doTestGetGroups
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|testGroups
argument_list|)
argument_list|,
literal|2
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
name|mockContext
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
name|mockUserNamingEnum
argument_list|,
name|mockGroupNamingEnum
argument_list|)
expr_stmt|;
comment|// Although connection is down but after reconnected it still should retrieve the result groups
name|doTestGetGroups
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|testGroups
argument_list|)
argument_list|,
literal|1
operator|+
literal|2
argument_list|)
expr_stmt|;
comment|// 1 is the first failure call
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
name|mockContext
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
literal|1
operator|+
name|LdapGroupsMapping
operator|.
name|RECONNECT_RETRY_COUNT
argument_list|)
expr_stmt|;
comment|// 1 is the first normal call
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
name|mappingSpy
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
name|mappingSpy
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
name|mockContext
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
block|}
end_class

end_unit

