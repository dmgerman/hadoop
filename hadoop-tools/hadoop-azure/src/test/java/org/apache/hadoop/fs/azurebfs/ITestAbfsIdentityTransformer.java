begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
package|;
end_package

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
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

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
name|Lists
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
name|azurebfs
operator|.
name|oauth2
operator|.
name|IdentityTransformer
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
name|permission
operator|.
name|AclEntry
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|AbfsHttpConstants
operator|.
name|SUPER_USER
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
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
operator|.
name|FS_AZURE_FILE_OWNER_DOMAINNAME
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
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
operator|.
name|FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME
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
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
operator|.
name|FS_AZURE_OVERRIDE_OWNER_SP
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
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
operator|.
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
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
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
operator|.
name|FS_AZURE_SKIP_SUPER_USER_REPLACEMENT
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
name|fs
operator|.
name|azurebfs
operator|.
name|utils
operator|.
name|AclTestHelpers
operator|.
name|aclEntry
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
name|fs
operator|.
name|permission
operator|.
name|AclEntryScope
operator|.
name|ACCESS
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
name|fs
operator|.
name|permission
operator|.
name|AclEntryScope
operator|.
name|DEFAULT
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
name|fs
operator|.
name|permission
operator|.
name|AclEntryType
operator|.
name|GROUP
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
name|fs
operator|.
name|permission
operator|.
name|AclEntryType
operator|.
name|MASK
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
name|fs
operator|.
name|permission
operator|.
name|AclEntryType
operator|.
name|OTHER
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
name|fs
operator|.
name|permission
operator|.
name|AclEntryType
operator|.
name|USER
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
name|fs
operator|.
name|permission
operator|.
name|FsAction
operator|.
name|ALL
import|;
end_import

begin_comment
comment|/**  * Test IdentityTransformer.  */
end_comment

begin_comment
comment|//@RunWith(Parameterized.class)
end_comment

begin_class
DECL|class|ITestAbfsIdentityTransformer
specifier|public
class|class
name|ITestAbfsIdentityTransformer
extends|extends
name|AbstractAbfsScaleTest
block|{
DECL|field|userGroupInfo
specifier|private
specifier|final
name|UserGroupInformation
name|userGroupInfo
decl_stmt|;
DECL|field|localUser
specifier|private
specifier|final
name|String
name|localUser
decl_stmt|;
DECL|field|localGroup
specifier|private
specifier|final
name|String
name|localGroup
decl_stmt|;
DECL|field|DAEMON
specifier|private
specifier|static
specifier|final
name|String
name|DAEMON
init|=
literal|"daemon"
decl_stmt|;
DECL|field|ASTERISK
specifier|private
specifier|static
specifier|final
name|String
name|ASTERISK
init|=
literal|"*"
decl_stmt|;
DECL|field|SHORT_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SHORT_NAME
init|=
literal|"abc"
decl_stmt|;
DECL|field|DOMAIN
specifier|private
specifier|static
specifier|final
name|String
name|DOMAIN
init|=
literal|"domain.com"
decl_stmt|;
DECL|field|FULLY_QUALIFIED_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FULLY_QUALIFIED_NAME
init|=
literal|"abc@domain.com"
decl_stmt|;
DECL|field|SERVICE_PRINCIPAL_ID
specifier|private
specifier|static
specifier|final
name|String
name|SERVICE_PRINCIPAL_ID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|method|ITestAbfsIdentityTransformer ()
specifier|public
name|ITestAbfsIdentityTransformer
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
name|UserGroupInformation
operator|.
name|reset
argument_list|()
expr_stmt|;
name|userGroupInfo
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|localUser
operator|=
name|userGroupInfo
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
name|localGroup
operator|=
name|userGroupInfo
operator|.
name|getPrimaryGroupName
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDaemonServiceSettingIdentity ()
specifier|public
name|void
name|testDaemonServiceSettingIdentity
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|config
init|=
name|this
operator|.
name|getRawConfiguration
argument_list|()
decl_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// Default config
name|IdentityTransformer
name|identityTransformer
init|=
name|getTransformerWithDefaultIdentityConfig
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Identity should not change for default config"
argument_list|,
name|DAEMON
argument_list|,
name|identityTransformer
operator|.
name|transformUserOrGroupForSetRequest
argument_list|(
name|DAEMON
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add service principal id
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|)
expr_stmt|;
comment|// case 1: substitution list doesn't contain daemon
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
literal|"a,b,c,d"
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Identity should not change when substitution list doesn't contain daemon"
argument_list|,
name|DAEMON
argument_list|,
name|identityTransformer
operator|.
name|transformUserOrGroupForSetRequest
argument_list|(
name|DAEMON
argument_list|)
argument_list|)
expr_stmt|;
comment|// case 2: substitution list contains daemon name
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
name|DAEMON
operator|+
literal|",a,b,c,d"
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Identity should be replaced to servicePrincipalId"
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|identityTransformer
operator|.
name|transformUserOrGroupForSetRequest
argument_list|(
name|DAEMON
argument_list|)
argument_list|)
expr_stmt|;
comment|// case 3: substitution list is *
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
name|ASTERISK
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Identity should be replaced to servicePrincipalId"
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|identityTransformer
operator|.
name|transformUserOrGroupForSetRequest
argument_list|(
name|DAEMON
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFullyQualifiedNameSettingIdentity ()
specifier|public
name|void
name|testFullyQualifiedNameSettingIdentity
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|config
init|=
name|this
operator|.
name|getRawConfiguration
argument_list|()
decl_stmt|;
comment|// Default config
name|IdentityTransformer
name|identityTransformer
init|=
name|getTransformerWithDefaultIdentityConfig
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"short name should not be converted to full name by default"
argument_list|,
name|SHORT_NAME
argument_list|,
name|identityTransformer
operator|.
name|transformUserOrGroupForSetRequest
argument_list|(
name|SHORT_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// Add config to get fully qualified username
name|config
operator|.
name|setBoolean
argument_list|(
name|FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_FILE_OWNER_DOMAINNAME
argument_list|,
name|DOMAIN
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"short name should be converted to full name"
argument_list|,
name|FULLY_QUALIFIED_NAME
argument_list|,
name|identityTransformer
operator|.
name|transformUserOrGroupForSetRequest
argument_list|(
name|SHORT_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoOpForSettingOidAsIdentity ()
specifier|public
name|void
name|testNoOpForSettingOidAsIdentity
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|config
init|=
name|this
operator|.
name|getRawConfiguration
argument_list|()
decl_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_FILE_OWNER_DOMAINNAME
argument_list|,
name|DOMAIN
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
literal|"a,b,c,d"
argument_list|)
expr_stmt|;
name|IdentityTransformer
name|identityTransformer
init|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
decl_stmt|;
specifier|final
name|String
name|principalId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Identity should not be changed when owner is already a principal id "
argument_list|,
name|principalId
argument_list|,
name|identityTransformer
operator|.
name|transformUserOrGroupForSetRequest
argument_list|(
name|principalId
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoOpWhenSettingSuperUserAsdentity ()
specifier|public
name|void
name|testNoOpWhenSettingSuperUserAsdentity
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|config
init|=
name|this
operator|.
name|getRawConfiguration
argument_list|()
decl_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_FILE_OWNER_DOMAINNAME
argument_list|,
name|DOMAIN
argument_list|)
expr_stmt|;
comment|// Default config
name|IdentityTransformer
name|identityTransformer
init|=
name|getTransformerWithDefaultIdentityConfig
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Identity should not be changed because it is not in substitution list"
argument_list|,
name|SUPER_USER
argument_list|,
name|identityTransformer
operator|.
name|transformUserOrGroupForSetRequest
argument_list|(
name|SUPER_USER
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIdentityReplacementForSuperUserGetRequest ()
specifier|public
name|void
name|testIdentityReplacementForSuperUserGetRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|config
init|=
name|this
operator|.
name|getRawConfiguration
argument_list|()
decl_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// with default config, identityTransformer should do $superUser replacement
name|IdentityTransformer
name|identityTransformer
init|=
name|getTransformerWithDefaultIdentityConfig
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"$superuser should be replaced with local user by default"
argument_list|,
name|localUser
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|SUPER_USER
argument_list|,
literal|true
argument_list|,
name|localUser
argument_list|)
argument_list|)
expr_stmt|;
comment|// Disable $supeuser replacement
name|config
operator|.
name|setBoolean
argument_list|(
name|FS_AZURE_SKIP_SUPER_USER_REPLACEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"$superuser should not be replaced"
argument_list|,
name|SUPER_USER
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|SUPER_USER
argument_list|,
literal|true
argument_list|,
name|localUser
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIdentityReplacementForDaemonServiceGetRequest ()
specifier|public
name|void
name|testIdentityReplacementForDaemonServiceGetRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|config
init|=
name|this
operator|.
name|getRawConfiguration
argument_list|()
decl_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// Default config
name|IdentityTransformer
name|identityTransformer
init|=
name|getTransformerWithDefaultIdentityConfig
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"By default servicePrincipalId should not be converted for GetFileStatus(), listFileStatus(), getAcl()"
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|SERVICE_PRINCIPAL_ID
argument_list|,
literal|true
argument_list|,
name|localUser
argument_list|)
argument_list|)
expr_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// 1. substitution list doesn't contain currentUser
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
literal|"a,b,c,d"
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"servicePrincipalId should not be replaced if local daemon user is not in substitution list"
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|SERVICE_PRINCIPAL_ID
argument_list|,
literal|true
argument_list|,
name|localUser
argument_list|)
argument_list|)
expr_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// 2. substitution list contains currentUser(daemon name) but the service principal id in config doesn't match
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
name|localUser
operator|+
literal|",a,b,c,d"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"servicePrincipalId should not be replaced if it is not equal to the SPN set in config"
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|SERVICE_PRINCIPAL_ID
argument_list|,
literal|true
argument_list|,
name|localUser
argument_list|)
argument_list|)
expr_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// 3. substitution list contains currentUser(daemon name) and the service principal id in config matches
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
name|localUser
operator|+
literal|",a,b,c,d"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"servicePrincipalId should be transformed to local use"
argument_list|,
name|localUser
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|SERVICE_PRINCIPAL_ID
argument_list|,
literal|true
argument_list|,
name|localUser
argument_list|)
argument_list|)
expr_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// 4. substitution is "*" but the service principal id in config doesn't match the input
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
name|ASTERISK
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"servicePrincipalId should not be replaced if it is not equal to the SPN set in config"
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|SERVICE_PRINCIPAL_ID
argument_list|,
literal|true
argument_list|,
name|localUser
argument_list|)
argument_list|)
expr_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// 5. substitution is "*" and the service principal id in config match the input
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
name|ASTERISK
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"servicePrincipalId should be transformed to local user"
argument_list|,
name|localUser
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|SERVICE_PRINCIPAL_ID
argument_list|,
literal|true
argument_list|,
name|localUser
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIdentityReplacementForKinitUserGetRequest ()
specifier|public
name|void
name|testIdentityReplacementForKinitUserGetRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|config
init|=
name|this
operator|.
name|getRawConfiguration
argument_list|()
decl_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// Default config
name|IdentityTransformer
name|identityTransformer
init|=
name|getTransformerWithDefaultIdentityConfig
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"full name should not be transformed if shortname is not enabled"
argument_list|,
name|FULLY_QUALIFIED_NAME
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|FULLY_QUALIFIED_NAME
argument_list|,
literal|true
argument_list|,
name|localUser
argument_list|)
argument_list|)
expr_stmt|;
comment|// add config to get short name
name|config
operator|.
name|setBoolean
argument_list|(
name|FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"should convert the full owner name to shortname "
argument_list|,
name|SHORT_NAME
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|FULLY_QUALIFIED_NAME
argument_list|,
literal|true
argument_list|,
name|localUser
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"group name should not be converted to shortname "
argument_list|,
name|FULLY_QUALIFIED_NAME
argument_list|,
name|identityTransformer
operator|.
name|transformIdentityForGetRequest
argument_list|(
name|FULLY_QUALIFIED_NAME
argument_list|,
literal|false
argument_list|,
name|localGroup
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|transformAclEntriesForSetRequest ()
specifier|public
name|void
name|transformAclEntriesForSetRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|config
init|=
name|this
operator|.
name|getRawConfiguration
argument_list|()
decl_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclEntriesToBeTransformed
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
name|DAEMON
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
name|FULLY_QUALIFIED_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|SUPER_USER
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|SHORT_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|GROUP
argument_list|,
name|DAEMON
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|GROUP
argument_list|,
name|SHORT_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
comment|// Notice: for group type ACL entry, if name is shortName,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|OTHER
argument_list|,
name|ALL
argument_list|)
argument_list|,
comment|//         It won't be converted to Full Name. This is
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|MASK
argument_list|,
name|ALL
argument_list|)
comment|//         to make the behavior consistent with HDI.
argument_list|)
decl_stmt|;
comment|// make a copy
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclEntries
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntriesToBeTransformed
argument_list|)
decl_stmt|;
comment|// Default config should not change the identities
name|IdentityTransformer
name|identityTransformer
init|=
name|getTransformerWithDefaultIdentityConfig
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|identityTransformer
operator|.
name|transformAclEntriesForSetRequest
argument_list|(
name|aclEntries
argument_list|)
expr_stmt|;
name|checkAclEntriesList
argument_list|(
name|aclEntriesToBeTransformed
argument_list|,
name|aclEntries
argument_list|)
expr_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// With config
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
name|DAEMON
operator|+
literal|",a,b,c,d"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_FILE_OWNER_DOMAINNAME
argument_list|,
name|DOMAIN
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|identityTransformer
operator|.
name|transformAclEntriesForSetRequest
argument_list|(
name|aclEntries
argument_list|)
expr_stmt|;
comment|// expected acl entries
name|List
argument_list|<
name|AclEntry
argument_list|>
name|expectedAclEntries
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
name|FULLY_QUALIFIED_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|SUPER_USER
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|FULLY_QUALIFIED_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|GROUP
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|GROUP
argument_list|,
name|SHORT_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|OTHER
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|MASK
argument_list|,
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|checkAclEntriesList
argument_list|(
name|aclEntries
argument_list|,
name|expectedAclEntries
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|transformAclEntriesForGetRequest ()
specifier|public
name|void
name|transformAclEntriesForGetRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|config
init|=
name|this
operator|.
name|getRawConfiguration
argument_list|()
decl_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclEntriesToBeTransformed
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
name|FULLY_QUALIFIED_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|SUPER_USER
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|SHORT_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|GROUP
argument_list|,
name|SHORT_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|OTHER
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|MASK
argument_list|,
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
comment|// make a copy
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclEntries
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntriesToBeTransformed
argument_list|)
decl_stmt|;
comment|// Default config should not change the identities
name|IdentityTransformer
name|identityTransformer
init|=
name|getTransformerWithDefaultIdentityConfig
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|identityTransformer
operator|.
name|transformAclEntriesForGetRequest
argument_list|(
name|aclEntries
argument_list|,
name|localUser
argument_list|,
name|localGroup
argument_list|)
expr_stmt|;
name|checkAclEntriesList
argument_list|(
name|aclEntriesToBeTransformed
argument_list|,
name|aclEntries
argument_list|)
expr_stmt|;
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// With config
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|,
name|localUser
operator|+
literal|",a,b,c,d"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_FILE_OWNER_DOMAINNAME
argument_list|,
name|DOMAIN
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP
argument_list|,
name|SERVICE_PRINCIPAL_ID
argument_list|)
expr_stmt|;
name|identityTransformer
operator|=
name|getTransformerWithCustomizedIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// make a copy
name|aclEntries
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntriesToBeTransformed
argument_list|)
expr_stmt|;
name|identityTransformer
operator|.
name|transformAclEntriesForGetRequest
argument_list|(
name|aclEntries
argument_list|,
name|localUser
argument_list|,
name|localGroup
argument_list|)
expr_stmt|;
comment|// expected acl entries
name|List
argument_list|<
name|AclEntry
argument_list|>
name|expectedAclEntries
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
name|SHORT_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
comment|// Full UPN should be transformed to shortName
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|localUser
argument_list|,
name|ALL
argument_list|)
argument_list|,
comment|// $SuperUser should be transformed to shortName
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|localUser
argument_list|,
name|ALL
argument_list|)
argument_list|,
comment|// principal Id should be transformed to local user name
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|USER
argument_list|,
name|SHORT_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|GROUP
argument_list|,
name|SHORT_NAME
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|OTHER
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|DEFAULT
argument_list|,
name|MASK
argument_list|,
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|checkAclEntriesList
argument_list|(
name|aclEntries
argument_list|,
name|expectedAclEntries
argument_list|)
expr_stmt|;
block|}
DECL|method|resetIdentityConfig (Configuration config)
specifier|private
name|void
name|resetIdentityConfig
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|config
operator|.
name|unset
argument_list|(
name|FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME
argument_list|)
expr_stmt|;
name|config
operator|.
name|unset
argument_list|(
name|FS_AZURE_FILE_OWNER_DOMAINNAME
argument_list|)
expr_stmt|;
name|config
operator|.
name|unset
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP
argument_list|)
expr_stmt|;
name|config
operator|.
name|unset
argument_list|(
name|FS_AZURE_OVERRIDE_OWNER_SP_LIST
argument_list|)
expr_stmt|;
name|config
operator|.
name|unset
argument_list|(
name|FS_AZURE_SKIP_SUPER_USER_REPLACEMENT
argument_list|)
expr_stmt|;
block|}
DECL|method|getTransformerWithDefaultIdentityConfig (Configuration config)
specifier|private
name|IdentityTransformer
name|getTransformerWithDefaultIdentityConfig
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|resetIdentityConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
operator|new
name|IdentityTransformer
argument_list|(
name|config
argument_list|)
return|;
block|}
DECL|method|getTransformerWithCustomizedIdentityConfig (Configuration config)
specifier|private
name|IdentityTransformer
name|getTransformerWithCustomizedIdentityConfig
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IdentityTransformer
argument_list|(
name|config
argument_list|)
return|;
block|}
DECL|method|checkAclEntriesList (List<AclEntry> aclEntries, List<AclEntry> expected)
specifier|private
name|void
name|checkAclEntriesList
parameter_list|(
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclEntries
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"list size not equals"
argument_list|,
name|aclEntries
operator|.
name|size
argument_list|()
operator|==
name|expected
operator|.
name|size
argument_list|()
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
name|aclEntries
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
literal|"Identity doesn't match"
argument_list|,
name|expected
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|aclEntries
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

