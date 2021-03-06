begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webproxy.amfilter
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|webproxy
operator|.
name|amfilter
package|;
end_package

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
name|net
operator|.
name|URI
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
name|util
operator|.
name|Set
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
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
name|assertTrue
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
name|CommonConfigurationKeysPublic
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
name|http
operator|.
name|HttpServer2
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
name|MiniKdc
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
name|net
operator|.
name|NetUtils
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
name|authentication
operator|.
name|KerberosTestUtils
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
name|AuthenticationFilterInitializer
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
name|authorize
operator|.
name|AccessControlList
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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

begin_comment
comment|/**  * Test AmIpFilter. Requests to a no declared hosts should has way through  * proxy. Another requests can be filtered with (without) user name.  *  */
end_comment

begin_class
DECL|class|TestSecureAmFilter
specifier|public
class|class
name|TestSecureAmFilter
block|{
DECL|field|proxyHost
specifier|private
name|String
name|proxyHost
init|=
literal|"localhost"
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_ROOT_DIR
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestSecureAmFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-root"
argument_list|)
decl_stmt|;
DECL|field|PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"hadoop.http.authentication."
decl_stmt|;
DECL|field|httpSpnegoKeytabFile
specifier|private
specifier|static
name|File
name|httpSpnegoKeytabFile
init|=
operator|new
name|File
argument_list|(
name|KerberosTestUtils
operator|.
name|getKeytabFile
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|rmconf
specifier|private
specifier|static
name|Configuration
name|rmconf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|httpSpnegoPrincipal
specifier|private
specifier|static
name|String
name|httpSpnegoPrincipal
init|=
name|KerberosTestUtils
operator|.
name|getServerPrincipal
argument_list|()
decl_stmt|;
DECL|field|miniKDCStarted
specifier|private
specifier|static
name|boolean
name|miniKDCStarted
init|=
literal|false
decl_stmt|;
DECL|field|testMiniKDC
specifier|private
specifier|static
name|MiniKdc
name|testMiniKDC
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
block|{
name|rmconf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_DELEGATION_TOKEN_AUTH_FILTER
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
literal|"hadoop.http.filter.initializers"
argument_list|,
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"kerberos.keytab"
argument_list|,
name|httpSpnegoKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"kerberos.principal"
argument_list|,
name|httpSpnegoPrincipal
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_SPNEGO_USER_NAME_KEY
argument_list|,
name|httpSpnegoPrincipal
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_KEYTAB
argument_list|,
name|httpSpnegoKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_SPNEGO_KEYTAB_FILE_KEY
argument_list|,
name|httpSpnegoKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|rmconf
argument_list|)
expr_stmt|;
try|try
block|{
name|testMiniKDC
operator|=
operator|new
name|MiniKdc
argument_list|(
name|MiniKdc
operator|.
name|createConf
argument_list|()
argument_list|,
name|TEST_ROOT_DIR
argument_list|)
expr_stmt|;
name|setupKDC
argument_list|()
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
literal|"Couldn't create MiniKDC"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|testMiniKDC
operator|!=
literal|null
condition|)
block|{
name|testMiniKDC
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setupKDC ()
specifier|private
specifier|static
name|void
name|setupKDC
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|miniKDCStarted
condition|)
block|{
name|testMiniKDC
operator|.
name|start
argument_list|()
expr_stmt|;
name|getKdc
argument_list|()
operator|.
name|createPrincipal
argument_list|(
name|httpSpnegoKeytabFile
argument_list|,
literal|"HTTP/localhost"
argument_list|)
expr_stmt|;
name|miniKDCStarted
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|getKdc ()
specifier|private
specifier|static
name|MiniKdc
name|getKdc
parameter_list|()
block|{
return|return
name|testMiniKDC
return|;
block|}
DECL|class|TestAmIpFilter
specifier|private
class|class
name|TestAmIpFilter
extends|extends
name|AmIpFilter
block|{
DECL|field|proxyAddresses
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|proxyAddresses
init|=
literal|null
decl_stmt|;
DECL|method|getProxyAddresses ()
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getProxyAddresses
parameter_list|()
block|{
if|if
condition|(
name|proxyAddresses
operator|==
literal|null
condition|)
block|{
name|proxyAddresses
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|proxyAddresses
operator|.
name|add
argument_list|(
name|proxyHost
argument_list|)
expr_stmt|;
return|return
name|proxyAddresses
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFindRedirectUrl ()
specifier|public
name|void
name|testFindRedirectUrl
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|rm1
init|=
literal|"rm1"
decl_stmt|;
specifier|final
name|String
name|rm2
init|=
literal|"rm2"
decl_stmt|;
comment|// generate a valid URL
specifier|final
name|String
name|rm1Url
init|=
name|startSecureHttpServer
argument_list|()
decl_stmt|;
comment|// invalid url
specifier|final
name|String
name|rm2Url
init|=
literal|"host2:8088"
decl_stmt|;
name|TestAmIpFilter
name|filter
init|=
operator|new
name|TestAmIpFilter
argument_list|()
decl_stmt|;
name|TestAmIpFilter
name|spy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|filter
argument_list|)
decl_stmt|;
comment|// make sure findRedirectUrl() go to HA branch
name|spy
operator|.
name|proxyUriBases
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|spy
operator|.
name|proxyUriBases
operator|.
name|put
argument_list|(
name|rm1
argument_list|,
name|rm1Url
argument_list|)
expr_stmt|;
name|spy
operator|.
name|proxyUriBases
operator|.
name|put
argument_list|(
name|rm2
argument_list|,
name|rm2Url
argument_list|)
expr_stmt|;
name|spy
operator|.
name|rmUrls
operator|=
operator|new
name|String
index|[]
block|{
name|rm1
block|,
name|rm2
block|}
expr_stmt|;
name|assertTrue
argument_list|(
name|spy
operator|.
name|isValidUrl
argument_list|(
name|rm1Url
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|spy
operator|.
name|isValidUrl
argument_list|(
name|rm2Url
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|spy
operator|.
name|findRedirectUrl
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|rm1Url
argument_list|)
expr_stmt|;
block|}
DECL|method|startSecureHttpServer ()
specifier|private
name|String
name|startSecureHttpServer
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpServer2
operator|.
name|Builder
name|builder
init|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setConf
argument_list|(
name|rmconf
argument_list|)
operator|.
name|addEndpoint
argument_list|(
operator|new
name|URI
argument_list|(
literal|"http://localhost"
argument_list|)
argument_list|)
operator|.
name|setACL
argument_list|(
operator|new
name|AccessControlList
argument_list|(
name|rmconf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ADMIN_ACL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_YARN_ADMIN_ACL
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setUsernameConfKey
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_SPNEGO_USER_NAME_KEY
argument_list|)
operator|.
name|setKeytabConfKey
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_SPNEGO_KEYTAB_FILE_KEY
argument_list|)
operator|.
name|setSecurityEnabled
argument_list|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|HttpServer2
name|server
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|URL
name|baseUrl
init|=
operator|new
name|URL
argument_list|(
literal|"http://"
operator|+
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|server
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|baseUrl
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

