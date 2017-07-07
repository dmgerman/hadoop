begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|security
operator|.
name|authentication
operator|.
name|util
operator|.
name|KerberosUtil
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
name|test
operator|.
name|GenericTestUtils
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
name|test
operator|.
name|LambdaTestUtils
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
name|util
operator|.
name|PlatformName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
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
name|Test
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|kerberos
operator|.
name|KerberosPrincipal
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
name|login
operator|.
name|AppConfigurationEntry
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
name|login
operator|.
name|LoginContext
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
name|java
operator|.
name|security
operator|.
name|Principal
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN
import|;
end_import

begin_comment
comment|/**  * Test {@link UserGroupInformation} with a minikdc.  */
end_comment

begin_class
DECL|class|TestUGIWithMiniKdc
specifier|public
class|class
name|TestUGIWithMiniKdc
block|{
DECL|field|kdc
specifier|private
specifier|static
name|MiniKdc
name|kdc
decl_stmt|;
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|UserGroupInformation
operator|.
name|reset
argument_list|()
expr_stmt|;
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
DECL|method|setupKdc ()
specifier|private
name|void
name|setupKdc
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|kdcConf
init|=
name|MiniKdc
operator|.
name|createConf
argument_list|()
decl_stmt|;
comment|// tgt expire time = 30 seconds
name|kdcConf
operator|.
name|setProperty
argument_list|(
name|MiniKdc
operator|.
name|MAX_TICKET_LIFETIME
argument_list|,
literal|"30"
argument_list|)
expr_stmt|;
name|kdcConf
operator|.
name|setProperty
argument_list|(
name|MiniKdc
operator|.
name|MIN_TICKET_LIFETIME
argument_list|,
literal|"30"
argument_list|)
expr_stmt|;
name|File
name|kdcDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.dir"
argument_list|,
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
name|kdc
operator|=
operator|new
name|MiniKdc
argument_list|(
name|kdcConf
argument_list|,
name|kdcDir
argument_list|)
expr_stmt|;
name|kdc
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testAutoRenewalThreadRetryWithKdc ()
specifier|public
name|void
name|testAutoRenewalThreadRetryWithKdc
parameter_list|()
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|UserGroupInformation
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Relogin every 1 second
name|conf
operator|.
name|setLong
argument_list|(
name|HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
name|UserGroupInformation
operator|.
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
name|setEnableRenewThreadCreationForTest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LoginContext
name|loginContext
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|principal
init|=
literal|"foo"
decl_stmt|;
specifier|final
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.dir"
argument_list|,
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
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
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
name|principal
argument_list|)
argument_list|)
expr_stmt|;
name|setupKdc
argument_list|()
expr_stmt|;
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|keytab
argument_list|,
name|principal
argument_list|)
expr_stmt|;
comment|// client login
specifier|final
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|false
argument_list|,
name|principals
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|loginContext
operator|=
operator|new
name|LoginContext
argument_list|(
literal|""
argument_list|,
name|subject
argument_list|,
literal|null
argument_list|,
operator|new
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"principal"
argument_list|,
name|principal
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"refreshKrb5Config"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|PlatformName
operator|.
name|IBM_JAVA
condition|)
block|{
name|options
operator|.
name|put
argument_list|(
literal|"useKeytab"
argument_list|,
name|keytab
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"credsType"
argument_list|,
literal|"both"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|options
operator|.
name|put
argument_list|(
literal|"keyTab"
argument_list|,
name|keytab
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"useKeyTab"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"storeKey"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"doNotPrompt"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"useTicketCache"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"renewTGT"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"isInitiator"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|ticketCache
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"KRB5CCNAME"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ticketCache
operator|!=
literal|null
condition|)
block|{
name|options
operator|.
name|put
argument_list|(
literal|"ticketCache"
argument_list|,
name|ticketCache
argument_list|)
expr_stmt|;
block|}
name|options
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|KerberosUtil
operator|.
name|getKrb5LoginModuleName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|options
argument_list|)
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|loginContext
operator|.
name|login
argument_list|()
expr_stmt|;
specifier|final
name|Subject
name|loginSubject
init|=
name|loginContext
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|UserGroupInformation
operator|.
name|loginUserFromSubject
argument_list|(
name|loginSubject
argument_list|)
expr_stmt|;
comment|// Verify retry happens. Do not verify retry count to reduce flakiness.
comment|// Detailed back-off logic is tested separately in
comment|// TestUserGroupInformation#testGetNextRetryTime
name|LambdaTestUtils
operator|.
name|await
argument_list|(
literal|30000
argument_list|,
literal|500
argument_list|,
parameter_list|()
lambda|->
block|{
specifier|final
name|int
name|count
init|=
name|UserGroupInformation
operator|.
name|metrics
operator|.
name|getRenewalFailures
argument_list|()
operator|.
name|value
argument_list|()
decl_stmt|;
name|UserGroupInformation
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Renew failure count is {}"
argument_list|,
name|count
argument_list|)
expr_stmt|;
return|return
name|count
operator|>
literal|0
return|;
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|loginContext
operator|!=
literal|null
condition|)
block|{
name|loginContext
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

