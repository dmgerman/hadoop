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
name|NamingEnumeration
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
name|Attribute
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
name|Matchers
operator|.
name|eq
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
name|doReturn
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
comment|/**  * Test LdapGroupsMapping with one-query lookup enabled.  * Mockito is used to simulate the LDAP server response.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|TestLdapGroupsMappingWithOneQuery
specifier|public
class|class
name|TestLdapGroupsMappingWithOneQuery
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
name|Attribute
name|groupDN
init|=
name|mock
argument_list|(
name|Attribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|NamingEnumeration
argument_list|<
name|SearchResult
argument_list|>
name|groupNames
init|=
name|getGroupNames
argument_list|()
decl_stmt|;
name|doReturn
argument_list|(
name|groupNames
argument_list|)
operator|.
name|when
argument_list|(
name|groupDN
argument_list|)
operator|.
name|getAll
argument_list|()
expr_stmt|;
name|String
name|groupName1
init|=
literal|"CN=abc,DC=foo,DC=bar,DC=com"
decl_stmt|;
name|String
name|groupName2
init|=
literal|"CN=xyz,DC=foo,DC=bar,DC=com"
decl_stmt|;
name|String
name|groupName3
init|=
literal|"CN=sss,CN=foo,DC=bar,DC=com"
decl_stmt|;
name|doReturn
argument_list|(
name|groupName1
argument_list|)
operator|.
name|doReturn
argument_list|(
name|groupName2
argument_list|)
operator|.
name|doReturn
argument_list|(
name|groupName3
argument_list|)
operator|.
name|when
argument_list|(
name|groupNames
argument_list|)
operator|.
name|next
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|groupNames
operator|.
name|hasMore
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|getAttributes
argument_list|()
operator|.
name|get
argument_list|(
name|eq
argument_list|(
literal|"memberOf"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|groupDN
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
name|NamingException
block|{
comment|// given a user whose ldap query returns a user object with three "memberOf"
comment|// properties, return an array of strings representing its groups.
name|String
index|[]
name|testGroups
init|=
operator|new
name|String
index|[]
block|{
literal|"abc"
block|,
literal|"xyz"
block|,
literal|"sss"
block|}
decl_stmt|;
name|doTestGetGroups
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|testGroups
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestGetGroups (List<String> expectedGroups)
specifier|private
name|void
name|doTestGetGroups
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|expectedGroups
parameter_list|)
throws|throws
name|NamingException
block|{
name|String
name|ldapUrl
init|=
literal|"ldap://test"
decl_stmt|;
name|Configuration
name|conf
init|=
name|getBaseConf
argument_list|(
name|ldapUrl
argument_list|)
decl_stmt|;
comment|// enable single-query lookup
name|conf
operator|.
name|set
argument_list|(
name|LdapGroupsMapping
operator|.
name|MEMBEROF_ATTR_KEY
argument_list|,
literal|"memberOf"
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
comment|// We should have only made one query because single-query lookup is enabled
name|verify
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|times
argument_list|(
literal|1
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

