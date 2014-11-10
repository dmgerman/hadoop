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
name|CommonConfigurationKeys
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
name|junit
operator|.
name|After
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
name|TemporaryFolder
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

begin_comment
comment|/**  * Verify UGI login from keytab. Check that the UGI is  * configured to use keytab to catch regressions like  * HADOOP-10786.  */
end_comment

begin_class
DECL|class|TestUGILoginFromKeytab
specifier|public
class|class
name|TestUGILoginFromKeytab
block|{
DECL|field|kdc
specifier|private
name|MiniKdc
name|kdc
decl_stmt|;
DECL|field|workDir
specifier|private
name|File
name|workDir
decl_stmt|;
annotation|@
name|Rule
DECL|field|folder
specifier|public
specifier|final
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|startMiniKdc ()
specifier|public
name|void
name|startMiniKdc
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This setting below is required. If not enabled, UGI will abort
comment|// any attempt to loginUserFromKeytab.
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
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|workDir
operator|=
name|folder
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|kdc
operator|=
operator|new
name|MiniKdc
argument_list|(
name|MiniKdc
operator|.
name|createConf
argument_list|()
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|kdc
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|stopMiniKdc ()
specifier|public
name|void
name|stopMiniKdc
parameter_list|()
block|{
if|if
condition|(
name|kdc
operator|!=
literal|null
condition|)
block|{
name|kdc
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Login from keytab using the MiniKDC and verify the UGI can successfully    * relogin from keytab as well. This will catch regressions like HADOOP-10786.    */
annotation|@
name|Test
DECL|method|testUGILoginFromKeytab ()
specifier|public
name|void
name|testUGILoginFromKeytab
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
operator|.
name|setShouldRenewImmediatelyForTests
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|principal
init|=
literal|"foo"
decl_stmt|;
name|File
name|keytab
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"foo.keytab"
argument_list|)
decl_stmt|;
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|keytab
argument_list|,
name|principal
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|loginUserFromKeytab
argument_list|(
name|principal
argument_list|,
name|keytab
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"UGI should be configured to login from keytab"
argument_list|,
name|ugi
operator|.
name|isFromKeytab
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify relogin from keytab.
name|User
name|user
init|=
name|ugi
operator|.
name|getSubject
argument_list|()
operator|.
name|getPrincipals
argument_list|(
name|User
operator|.
name|class
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|long
name|firstLogin
init|=
name|user
operator|.
name|getLastLogin
argument_list|()
decl_stmt|;
name|ugi
operator|.
name|reloginFromKeytab
argument_list|()
expr_stmt|;
specifier|final
name|long
name|secondLogin
init|=
name|user
operator|.
name|getLastLogin
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"User should have been able to relogin from keytab"
argument_list|,
name|secondLogin
operator|>
name|firstLogin
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

