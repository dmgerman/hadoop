begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|router
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
name|fs
operator|.
name|contract
operator|.
name|router
operator|.
name|web
operator|.
name|RouterWebHDFSContract
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
name|assertNotNull
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|router
operator|.
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_KEYTAB_FILE_KEY
import|;
end_import

begin_comment
comment|/**  * Test secure router start up scenarios.  */
end_comment

begin_class
DECL|class|TestRouterWithSecureStartup
specifier|public
class|class
name|TestRouterWithSecureStartup
block|{
DECL|field|HTTP_KERBEROS_PRINCIPAL_CONF_KEY
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_KERBEROS_PRINCIPAL_CONF_KEY
init|=
literal|"hadoop.http.authentication.kerberos.principal"
decl_stmt|;
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
comment|/*    * hadoop.http.authentication.kerberos.principal has default value, so if we    * don't config the spnego principal, cluster will still start normally    */
annotation|@
name|Test
DECL|method|testStartupWithoutSpnegoPrincipal ()
specifier|public
name|void
name|testStartupWithoutSpnegoPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|initSecurity
argument_list|()
decl_stmt|;
name|conf
operator|.
name|unset
argument_list|(
name|HTTP_KERBEROS_PRINCIPAL_CONF_KEY
argument_list|)
expr_stmt|;
name|RouterWebHDFSContract
operator|.
name|createCluster
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|RouterWebHDFSContract
operator|.
name|getCluster
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStartupWithoutKeytab ()
specifier|public
name|void
name|testStartupWithoutKeytab
parameter_list|()
throws|throws
name|Exception
block|{
name|testCluster
argument_list|(
name|DFS_ROUTER_KEYTAB_FILE_KEY
argument_list|,
literal|"Running in secure mode, but config doesn't have a keytab"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSuccessfulStartup ()
specifier|public
name|void
name|testSuccessfulStartup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|initSecurity
argument_list|()
decl_stmt|;
name|RouterWebHDFSContract
operator|.
name|createCluster
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|RouterWebHDFSContract
operator|.
name|getCluster
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCluster (String configToTest, String message)
specifier|private
name|void
name|testCluster
parameter_list|(
name|String
name|configToTest
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|initSecurity
argument_list|()
decl_stmt|;
name|conf
operator|.
name|unset
argument_list|(
name|configToTest
argument_list|)
expr_stmt|;
name|exceptionRule
operator|.
name|expect
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exceptionRule
operator|.
name|expectMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|RouterWebHDFSContract
operator|.
name|createCluster
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

