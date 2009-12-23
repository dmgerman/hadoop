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
name|security
operator|.
name|CodeSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|CodeSigner
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PermissionCollection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|ProtectionDomain
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|NetPermission
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|Subject
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
name|SecurityUtil
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
name|UnixUserGroupInformation
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
name|SecurityUtil
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
DECL|class|TestConfiguredPolicy
specifier|public
class|class
name|TestConfiguredPolicy
extends|extends
name|TestCase
block|{
DECL|field|USER1
specifier|private
specifier|static
specifier|final
name|String
name|USER1
init|=
literal|"drwho"
decl_stmt|;
DECL|field|USER2
specifier|private
specifier|static
specifier|final
name|String
name|USER2
init|=
literal|"joe"
decl_stmt|;
DECL|field|GROUPS1
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|GROUPS1
init|=
operator|new
name|String
index|[]
block|{
literal|"tardis"
block|}
decl_stmt|;
DECL|field|GROUPS2
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|GROUPS2
init|=
operator|new
name|String
index|[]
block|{
literal|"users"
block|}
decl_stmt|;
DECL|field|KEY_1
specifier|private
specifier|static
specifier|final
name|String
name|KEY_1
init|=
literal|"test.policy.1"
decl_stmt|;
DECL|field|KEY_2
specifier|private
specifier|static
specifier|final
name|String
name|KEY_2
init|=
literal|"test.policy.2"
decl_stmt|;
DECL|class|Protocol1
specifier|public
specifier|static
class|class
name|Protocol1
block|{
DECL|field|i
name|int
name|i
decl_stmt|;
block|}
DECL|class|Protocol2
specifier|public
specifier|static
class|class
name|Protocol2
block|{
DECL|field|j
name|int
name|j
decl_stmt|;
block|}
DECL|class|TestPolicyProvider
specifier|private
specifier|static
class|class
name|TestPolicyProvider
extends|extends
name|PolicyProvider
block|{
annotation|@
name|Override
DECL|method|getServices ()
specifier|public
name|Service
index|[]
name|getServices
parameter_list|()
block|{
return|return
operator|new
name|Service
index|[]
block|{
operator|new
name|Service
argument_list|(
name|KEY_1
argument_list|,
name|Protocol1
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|KEY_2
argument_list|,
name|Protocol2
operator|.
name|class
argument_list|)
block|,           }
return|;
block|}
block|}
DECL|method|testConfiguredPolicy ()
specifier|public
name|void
name|testConfiguredPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|ConfiguredPolicy
name|policy
init|=
name|createConfiguredPolicy
argument_list|()
decl_stmt|;
name|SecurityUtil
operator|.
name|setPolicy
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|Subject
name|user1
init|=
name|SecurityUtil
operator|.
name|getSubject
argument_list|(
operator|new
name|UnixUserGroupInformation
argument_list|(
name|USER1
argument_list|,
name|GROUPS1
argument_list|)
argument_list|)
decl_stmt|;
comment|// Should succeed
name|ServiceAuthorizationManager
operator|.
name|authorize
argument_list|(
name|user1
argument_list|,
name|Protocol1
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Should fail
name|Subject
name|user2
init|=
name|SecurityUtil
operator|.
name|getSubject
argument_list|(
operator|new
name|UnixUserGroupInformation
argument_list|(
name|USER2
argument_list|,
name|GROUPS2
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
try|try
block|{
name|ServiceAuthorizationManager
operator|.
name|authorize
argument_list|(
name|user2
argument_list|,
name|Protocol2
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthorizationException
name|ae
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|failed
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a configured policy with some keys    * @return a new configured policy    */
DECL|method|createConfiguredPolicy ()
specifier|private
name|ConfiguredPolicy
name|createConfiguredPolicy
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
name|KEY_1
argument_list|,
name|AccessControlList
operator|.
name|WILDCARD_ACL_VALUE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_2
argument_list|,
name|USER1
operator|+
literal|" "
operator|+
name|GROUPS1
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConfiguredPolicy
argument_list|(
name|conf
argument_list|,
operator|new
name|TestPolicyProvider
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a test code source against a test URL    * @return a new code source    * @throws MalformedURLException    */
DECL|method|createCodeSource ()
specifier|private
name|CodeSource
name|createCodeSource
parameter_list|()
throws|throws
name|MalformedURLException
block|{
return|return
operator|new
name|CodeSource
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://hadoop.apache.org"
argument_list|)
argument_list|,
operator|(
name|CodeSigner
index|[]
operator|)
literal|null
argument_list|)
return|;
block|}
comment|/**    * Assert that a permission collection can have new permissions added    * @param permissions the collection to check    */
DECL|method|assertWritable (PermissionCollection permissions)
specifier|private
name|void
name|assertWritable
parameter_list|(
name|PermissionCollection
name|permissions
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|permissions
operator|.
name|isReadOnly
argument_list|()
argument_list|)
expr_stmt|;
name|NetPermission
name|netPermission
init|=
operator|new
name|NetPermission
argument_list|(
literal|"something"
argument_list|)
decl_stmt|;
name|permissions
operator|.
name|add
argument_list|(
name|netPermission
argument_list|)
expr_stmt|;
block|}
comment|/**    * test that the {@link PermissionCollection} returned by    * {@link ConfiguredPolicy#getPermissions(CodeSource)} is writeable    * @throws Throwable on any failure    */
DECL|method|testPolicyWritable ()
specifier|public
name|void
name|testPolicyWritable
parameter_list|()
throws|throws
name|Throwable
block|{
name|ConfiguredPolicy
name|policy
init|=
name|createConfiguredPolicy
argument_list|()
decl_stmt|;
name|CodeSource
name|source
init|=
name|createCodeSource
argument_list|()
decl_stmt|;
name|PermissionCollection
name|permissions
init|=
name|policy
operator|.
name|getPermissions
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertWritable
argument_list|(
name|permissions
argument_list|)
expr_stmt|;
block|}
comment|/**    * test that the {@link PermissionCollection} returned by    * {@link ConfiguredPolicy#getPermissions(CodeSource)} is writeable    * @throws Throwable on any failure    */
DECL|method|testProtectionDomainPolicyWritable ()
specifier|public
name|void
name|testProtectionDomainPolicyWritable
parameter_list|()
throws|throws
name|Throwable
block|{
name|ConfiguredPolicy
name|policy
init|=
name|createConfiguredPolicy
argument_list|()
decl_stmt|;
name|CodeSource
name|source
init|=
name|createCodeSource
argument_list|()
decl_stmt|;
name|PermissionCollection
name|permissions
init|=
name|policy
operator|.
name|getPermissions
argument_list|(
operator|new
name|ProtectionDomain
argument_list|(
name|source
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertWritable
argument_list|(
name|permissions
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

