begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|client
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
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|Sasl
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|minikdc
operator|.
name|KerberosSecurityTestcase
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
name|SaslRpcServer
operator|.
name|QualityOfProtection
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

begin_comment
comment|/**  * Test Spnego Client Login.  */
end_comment

begin_class
DECL|class|TestSecureApiServiceClient
specifier|public
class|class
name|TestSecureApiServiceClient
extends|extends
name|KerberosSecurityTestcase
block|{
DECL|field|clientPrincipal
specifier|private
name|String
name|clientPrincipal
init|=
literal|"client"
decl_stmt|;
DECL|field|server1Protocol
specifier|private
name|String
name|server1Protocol
init|=
literal|"HTTP"
decl_stmt|;
DECL|field|server2Protocol
specifier|private
name|String
name|server2Protocol
init|=
literal|"server2"
decl_stmt|;
DECL|field|host
specifier|private
name|String
name|host
init|=
literal|"localhost"
decl_stmt|;
DECL|field|server1Principal
specifier|private
name|String
name|server1Principal
init|=
name|server1Protocol
operator|+
literal|"/"
operator|+
name|host
decl_stmt|;
DECL|field|server2Principal
specifier|private
name|String
name|server2Principal
init|=
name|server2Protocol
operator|+
literal|"/"
operator|+
name|host
decl_stmt|;
DECL|field|keytabFile
specifier|private
name|File
name|keytabFile
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|props
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|keytabFile
operator|=
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
literal|"keytab"
argument_list|)
expr_stmt|;
name|getKdc
argument_list|()
operator|.
name|createPrincipal
argument_list|(
name|keytabFile
argument_list|,
name|clientPrincipal
argument_list|,
name|server1Principal
argument_list|,
name|server2Principal
argument_list|)
expr_stmt|;
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
name|AuthenticationMethod
operator|.
name|KERBEROS
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setShouldRenewImmediatelyForTests
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|props
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|Sasl
operator|.
name|QOP
argument_list|,
name|QualityOfProtection
operator|.
name|AUTHENTICATION
operator|.
name|saslQop
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHttpSpnegoChallenge ()
specifier|public
name|void
name|testHttpSpnegoChallenge
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
operator|.
name|loginUserFromKeytab
argument_list|(
name|clientPrincipal
argument_list|,
name|keytabFile
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|ApiServiceClient
name|asc
init|=
operator|new
name|ApiServiceClient
argument_list|()
decl_stmt|;
name|String
name|challenge
init|=
name|asc
operator|.
name|generateToken
argument_list|(
literal|"localhost"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|challenge
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

