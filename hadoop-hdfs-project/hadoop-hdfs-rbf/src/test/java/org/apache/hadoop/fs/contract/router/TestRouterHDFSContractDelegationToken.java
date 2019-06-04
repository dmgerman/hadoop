begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
operator|.
name|router
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
name|fs
operator|.
name|contract
operator|.
name|router
operator|.
name|SecurityConfUtil
operator|.
name|initSecurity
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|metrics
operator|.
name|TestRBFMetrics
operator|.
name|ROUTER_BEAN
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
name|fs
operator|.
name|contract
operator|.
name|AbstractFSContract
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
name|contract
operator|.
name|AbstractFSContractTestBase
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
name|FederationTestUtils
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
name|metrics
operator|.
name|RouterMBean
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
name|junit
operator|.
name|AfterClass
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

begin_comment
comment|/**  * Test to verify router contracts for delegation token operations.  */
end_comment

begin_class
DECL|class|TestRouterHDFSContractDelegationToken
specifier|public
class|class
name|TestRouterHDFSContractDelegationToken
extends|extends
name|AbstractFSContractTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|createCluster ()
specifier|public
specifier|static
name|void
name|createCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|RouterHDFSContract
operator|.
name|createCluster
argument_list|(
literal|false
argument_list|,
literal|1
argument_list|,
name|initSecurity
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownCluster ()
specifier|public
specifier|static
name|void
name|teardownCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|RouterHDFSContract
operator|.
name|destroyCluster
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createContract (Configuration conf)
specifier|protected
name|AbstractFSContract
name|createContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|RouterHDFSContract
argument_list|(
name|conf
argument_list|)
return|;
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
DECL|method|testRouterDelegationToken ()
specifier|public
name|void
name|testRouterDelegationToken
parameter_list|()
throws|throws
name|Exception
block|{
name|RouterMBean
name|bean
init|=
name|FederationTestUtils
operator|.
name|getBean
argument_list|(
name|ROUTER_BEAN
argument_list|,
name|RouterMBean
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Initially there is no token in memory
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bean
operator|.
name|getCurrentTokensCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Generate delegation token
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
operator|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
operator|)
name|getFileSystem
argument_list|()
operator|.
name|getDelegationToken
argument_list|(
literal|"router"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
comment|// Verify properties of the token
name|assertEquals
argument_list|(
literal|"HDFS_DELEGATION_TOKEN"
argument_list|,
name|token
operator|.
name|getKind
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DelegationTokenIdentifier
name|identifier
init|=
name|token
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
name|String
name|owner
init|=
name|identifier
operator|.
name|getOwner
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Windows will not reverse name lookup "127.0.0.1" to "localhost".
name|String
name|host
init|=
name|Path
operator|.
name|WINDOWS
condition|?
literal|"127.0.0.1"
else|:
literal|"localhost"
decl_stmt|;
name|String
name|expectedOwner
init|=
literal|"router/"
operator|+
name|host
operator|+
literal|"@EXAMPLE.COM"
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedOwner
argument_list|,
name|owner
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"router"
argument_list|,
name|identifier
operator|.
name|getRenewer
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|masterKeyId
init|=
name|identifier
operator|.
name|getMasterKeyId
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|masterKeyId
operator|>
literal|0
argument_list|)
expr_stmt|;
name|int
name|sequenceNumber
init|=
name|identifier
operator|.
name|getSequenceNumber
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|sequenceNumber
operator|>
literal|0
argument_list|)
expr_stmt|;
name|long
name|existingMaxTime
init|=
name|token
operator|.
name|decodeIdentifier
argument_list|()
operator|.
name|getMaxDate
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|identifier
operator|.
name|getMaxDate
argument_list|()
operator|>=
name|identifier
operator|.
name|getIssueDate
argument_list|()
argument_list|)
expr_stmt|;
comment|// one token is expected after the generation
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bean
operator|.
name|getCurrentTokensCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Renew delegation token
name|long
name|expiryTime
init|=
name|token
operator|.
name|renew
argument_list|(
name|initSecurity
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|existingMaxTime
argument_list|,
name|token
operator|.
name|decodeIdentifier
argument_list|()
operator|.
name|getMaxDate
argument_list|()
argument_list|)
expr_stmt|;
comment|// Expiry time after renewal should never exceed max time of the token.
name|assertTrue
argument_list|(
name|expiryTime
operator|<=
name|existingMaxTime
argument_list|)
expr_stmt|;
comment|// Renewal should retain old master key id and sequence number
name|identifier
operator|=
name|token
operator|.
name|decodeIdentifier
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|identifier
operator|.
name|getMasterKeyId
argument_list|()
argument_list|,
name|masterKeyId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|identifier
operator|.
name|getSequenceNumber
argument_list|()
argument_list|,
name|sequenceNumber
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bean
operator|.
name|getCurrentTokensCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Cancel delegation token
name|token
operator|.
name|cancel
argument_list|(
name|initSecurity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bean
operator|.
name|getCurrentTokensCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Renew a cancelled token
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
name|token
operator|.
name|renew
argument_list|(
name|initSecurity
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

