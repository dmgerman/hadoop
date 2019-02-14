begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|security
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|security
operator|.
name|RouterSecurityManager
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
name|Text
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
name|token
operator|.
name|SecretManager
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
name|delegation
operator|.
name|AbstractDelegationTokenSecretManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
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
name|Rule
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
name|assertNotNull
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

begin_comment
comment|/**  * Test functionality of {@link RouterSecurityManager}, which manages  * delegation tokens for router.  */
end_comment

begin_class
DECL|class|TestRouterSecurityManager
specifier|public
class|class
name|TestRouterSecurityManager
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
name|TestRouterSecurityManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|securityManager
specifier|private
specifier|static
name|RouterSecurityManager
name|securityManager
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|createMockSecretManager ()
specifier|public
specifier|static
name|void
name|createMockSecretManager
parameter_list|()
throws|throws
name|IOException
block|{
name|AbstractDelegationTokenSecretManager
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|mockDelegationTokenSecretManager
init|=
operator|new
name|MockDelegationTokenSecretManager
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|mockDelegationTokenSecretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|securityManager
operator|=
operator|new
name|RouterSecurityManager
argument_list|(
name|mockDelegationTokenSecretManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Rule
DECL|field|exceptionRule
specifier|public
name|ExpectedException
name|exceptionRule
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testDelegationTokens ()
specifier|public
name|void
name|testDelegationTokens
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|groupsForTesting
init|=
operator|new
name|String
index|[
literal|1
index|]
decl_stmt|;
name|groupsForTesting
index|[
literal|0
index|]
operator|=
literal|"router_group"
expr_stmt|;
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"router"
argument_list|,
name|groupsForTesting
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get a delegation token
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|securityManager
operator|.
name|getDelegationToken
argument_list|(
operator|new
name|Text
argument_list|(
literal|"some_renewer"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
comment|// Renew the delegation token
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"some_renewer"
argument_list|,
name|groupsForTesting
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|updatedExpirationTime
init|=
name|securityManager
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|updatedExpirationTime
operator|>=
name|token
operator|.
name|decodeIdentifier
argument_list|()
operator|.
name|getMaxDate
argument_list|()
argument_list|)
expr_stmt|;
comment|// Cancel the delegation token
name|securityManager
operator|.
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|String
name|exceptionCause
init|=
literal|"Renewal request for unknown token"
decl_stmt|;
name|exceptionRule
operator|.
name|expect
argument_list|(
name|SecretManager
operator|.
name|InvalidToken
operator|.
name|class
argument_list|)
expr_stmt|;
name|exceptionRule
operator|.
name|expectMessage
argument_list|(
name|exceptionCause
argument_list|)
expr_stmt|;
comment|// This throws an exception as token has been cancelled.
name|securityManager
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

