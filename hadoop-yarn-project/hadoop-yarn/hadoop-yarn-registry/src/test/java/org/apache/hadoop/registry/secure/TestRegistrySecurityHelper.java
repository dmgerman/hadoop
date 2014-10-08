begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.secure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|secure
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryConstants
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|RegistrySecurity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooDefs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
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
name|BeforeClass
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
name|util
operator|.
name|List
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryConstants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test for registry security operations  */
end_comment

begin_class
DECL|class|TestRegistrySecurityHelper
specifier|public
class|class
name|TestRegistrySecurityHelper
extends|extends
name|Assert
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
name|TestRegistrySecurityHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|YARN_EXAMPLE_COM
specifier|public
specifier|static
specifier|final
name|String
name|YARN_EXAMPLE_COM
init|=
literal|"yarn@example.com"
decl_stmt|;
DECL|field|SASL_YARN_EXAMPLE_COM
specifier|public
specifier|static
specifier|final
name|String
name|SASL_YARN_EXAMPLE_COM
init|=
literal|"sasl:"
operator|+
name|YARN_EXAMPLE_COM
decl_stmt|;
DECL|field|MAPRED_EXAMPLE_COM
specifier|public
specifier|static
specifier|final
name|String
name|MAPRED_EXAMPLE_COM
init|=
literal|"mapred@example.com"
decl_stmt|;
DECL|field|SASL_MAPRED_EXAMPLE_COM
specifier|public
specifier|static
specifier|final
name|String
name|SASL_MAPRED_EXAMPLE_COM
init|=
literal|"sasl:"
operator|+
name|MAPRED_EXAMPLE_COM
decl_stmt|;
DECL|field|SASL_MAPRED_APACHE
specifier|public
specifier|static
specifier|final
name|String
name|SASL_MAPRED_APACHE
init|=
literal|"sasl:mapred@APACHE"
decl_stmt|;
DECL|field|DIGEST_F0AF
specifier|public
specifier|static
specifier|final
name|String
name|DIGEST_F0AF
init|=
literal|"digest:f0afbeeb00baa"
decl_stmt|;
DECL|field|SASL_YARN_SHORT
specifier|public
specifier|static
specifier|final
name|String
name|SASL_YARN_SHORT
init|=
literal|"sasl:yarn@"
decl_stmt|;
DECL|field|SASL_MAPRED_SHORT
specifier|public
specifier|static
specifier|final
name|String
name|SASL_MAPRED_SHORT
init|=
literal|"sasl:mapred@"
decl_stmt|;
DECL|field|REALM_EXAMPLE_COM
specifier|public
specifier|static
specifier|final
name|String
name|REALM_EXAMPLE_COM
init|=
literal|"example.com"
decl_stmt|;
DECL|field|registrySecurity
specifier|private
specifier|static
name|RegistrySecurity
name|registrySecurity
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupTestRegistrySecurityHelper ()
specifier|public
specifier|static
name|void
name|setupTestRegistrySecurityHelper
parameter_list|()
throws|throws
name|IOException
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
name|setBoolean
argument_list|(
name|KEY_REGISTRY_SECURE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_KERBEROS_REALM
argument_list|,
literal|"KERBEROS"
argument_list|)
expr_stmt|;
name|registrySecurity
operator|=
operator|new
name|RegistrySecurity
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// init the ACLs OUTSIDE A KERBEROS CLUSTER
name|registrySecurity
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testACLSplitRealmed ()
specifier|public
name|void
name|testACLSplitRealmed
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|String
argument_list|>
name|pairs
init|=
name|registrySecurity
operator|.
name|splitAclPairs
argument_list|(
name|SASL_YARN_EXAMPLE_COM
operator|+
literal|", "
operator|+
name|SASL_MAPRED_EXAMPLE_COM
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SASL_YARN_EXAMPLE_COM
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SASL_MAPRED_EXAMPLE_COM
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuildAclsRealmed ()
specifier|public
name|void
name|testBuildAclsRealmed
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|ACL
argument_list|>
name|acls
init|=
name|registrySecurity
operator|.
name|buildACLs
argument_list|(
name|SASL_YARN_EXAMPLE_COM
operator|+
literal|", "
operator|+
name|SASL_MAPRED_EXAMPLE_COM
argument_list|,
literal|""
argument_list|,
name|ZooDefs
operator|.
name|Perms
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|YARN_EXAMPLE_COM
argument_list|,
name|acls
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAPRED_EXAMPLE_COM
argument_list|,
name|acls
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testACLDefaultRealm ()
specifier|public
name|void
name|testACLDefaultRealm
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|String
argument_list|>
name|pairs
init|=
name|registrySecurity
operator|.
name|splitAclPairs
argument_list|(
name|SASL_YARN_SHORT
operator|+
literal|", "
operator|+
name|SASL_MAPRED_SHORT
argument_list|,
name|REALM_EXAMPLE_COM
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SASL_YARN_EXAMPLE_COM
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SASL_MAPRED_EXAMPLE_COM
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuildAclsDefaultRealm ()
specifier|public
name|void
name|testBuildAclsDefaultRealm
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|ACL
argument_list|>
name|acls
init|=
name|registrySecurity
operator|.
name|buildACLs
argument_list|(
name|SASL_YARN_SHORT
operator|+
literal|", "
operator|+
name|SASL_MAPRED_SHORT
argument_list|,
name|REALM_EXAMPLE_COM
argument_list|,
name|ZooDefs
operator|.
name|Perms
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|YARN_EXAMPLE_COM
argument_list|,
name|acls
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAPRED_EXAMPLE_COM
argument_list|,
name|acls
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testACLSplitNullRealm ()
specifier|public
name|void
name|testACLSplitNullRealm
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|String
argument_list|>
name|pairs
init|=
name|registrySecurity
operator|.
name|splitAclPairs
argument_list|(
name|SASL_YARN_SHORT
operator|+
literal|", "
operator|+
name|SASL_MAPRED_SHORT
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SASL_YARN_SHORT
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SASL_MAPRED_SHORT
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testBuildAclsNullRealm ()
specifier|public
name|void
name|testBuildAclsNullRealm
parameter_list|()
throws|throws
name|Throwable
block|{
name|registrySecurity
operator|.
name|buildACLs
argument_list|(
name|SASL_YARN_SHORT
operator|+
literal|", "
operator|+
name|SASL_MAPRED_SHORT
argument_list|,
literal|""
argument_list|,
name|ZooDefs
operator|.
name|Perms
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testACLDefaultRealmOnlySASL ()
specifier|public
name|void
name|testACLDefaultRealmOnlySASL
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|String
argument_list|>
name|pairs
init|=
name|registrySecurity
operator|.
name|splitAclPairs
argument_list|(
name|SASL_YARN_SHORT
operator|+
literal|", "
operator|+
name|DIGEST_F0AF
argument_list|,
name|REALM_EXAMPLE_COM
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SASL_YARN_EXAMPLE_COM
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DIGEST_F0AF
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testACLSplitMixed ()
specifier|public
name|void
name|testACLSplitMixed
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|String
argument_list|>
name|pairs
init|=
name|registrySecurity
operator|.
name|splitAclPairs
argument_list|(
name|SASL_YARN_SHORT
operator|+
literal|", "
operator|+
name|SASL_MAPRED_APACHE
operator|+
literal|", ,,"
operator|+
name|DIGEST_F0AF
argument_list|,
name|REALM_EXAMPLE_COM
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SASL_YARN_EXAMPLE_COM
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SASL_MAPRED_APACHE
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DIGEST_F0AF
argument_list|,
name|pairs
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultAClsValid ()
specifier|public
name|void
name|testDefaultAClsValid
parameter_list|()
throws|throws
name|Throwable
block|{
name|registrySecurity
operator|.
name|buildACLs
argument_list|(
name|RegistryConstants
operator|.
name|DEFAULT_REGISTRY_SYSTEM_ACCOUNTS
argument_list|,
name|REALM_EXAMPLE_COM
argument_list|,
name|ZooDefs
operator|.
name|Perms
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultRealm ()
specifier|public
name|void
name|testDefaultRealm
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|realm
init|=
name|RegistrySecurity
operator|.
name|getDefaultRealmInJVM
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Realm {}"
argument_list|,
name|realm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUGIProperties ()
specifier|public
name|void
name|testUGIProperties
parameter_list|()
throws|throws
name|Throwable
block|{
name|UserGroupInformation
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|ACL
name|acl
init|=
name|registrySecurity
operator|.
name|createACLForUser
argument_list|(
name|user
argument_list|,
name|ZooDefs
operator|.
name|Perms
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|RegistrySecurity
operator|.
name|ALL_READWRITE_ACCESS
operator|.
name|equals
argument_list|(
name|acl
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"User {} has ACL {}"
argument_list|,
name|user
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSecurityImpliesKerberos ()
specifier|public
name|void
name|testSecurityImpliesKerberos
parameter_list|()
throws|throws
name|Throwable
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
name|setBoolean
argument_list|(
literal|"hadoop.security.authentication"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|KEY_REGISTRY_SECURE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_KERBEROS_REALM
argument_list|,
literal|"KERBEROS"
argument_list|)
expr_stmt|;
name|RegistrySecurity
name|security
init|=
operator|new
name|RegistrySecurity
argument_list|(
literal|"registry security"
argument_list|)
decl_stmt|;
try|try
block|{
name|security
operator|.
name|init
argument_list|(
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
name|assertTrue
argument_list|(
literal|"did not find "
operator|+
name|RegistrySecurity
operator|.
name|E_NO_KERBEROS
operator|+
literal|" in "
operator|+
name|e
argument_list|,
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|RegistrySecurity
operator|.
name|E_NO_KERBEROS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

