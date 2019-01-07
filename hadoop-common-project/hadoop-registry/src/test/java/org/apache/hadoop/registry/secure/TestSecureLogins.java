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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|Set
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
name|callback
operator|.
name|CallbackHandler
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
name|LoginContext
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
name|LoginException
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
name|Environment
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
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|HadoopKerberosName
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
name|util
operator|.
name|KerberosName
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
name|ZookeeperConfigOptions
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
name|security
operator|.
name|authentication
operator|.
name|util
operator|.
name|KerberosName
operator|.
name|DEFAULT_MECHANISM
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
name|security
operator|.
name|authentication
operator|.
name|util
operator|.
name|KerberosName
operator|.
name|MECHANISM_HADOOP
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
name|security
operator|.
name|authentication
operator|.
name|util
operator|.
name|KerberosName
operator|.
name|MECHANISM_MIT
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
name|util
operator|.
name|PlatformName
operator|.
name|IBM_JAVA
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

begin_comment
comment|/**  * Verify that logins work  */
end_comment

begin_class
DECL|class|TestSecureLogins
specifier|public
class|class
name|TestSecureLogins
extends|extends
name|AbstractSecureRegistryTest
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
name|TestSecureLogins
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testHasRealm ()
specifier|public
name|void
name|testHasRealm
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertNotNull
argument_list|(
name|getRealm
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ZK principal = {}"
argument_list|,
name|getPrincipalAndRealm
argument_list|(
name|ZOOKEEPER_LOCALHOST
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJaasFileSetup ()
specifier|public
name|void
name|testJaasFileSetup
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// the JVM has seemed inconsistent on setting up here
name|assertNotNull
argument_list|(
literal|"jaasFile"
argument_list|,
name|jaasFile
argument_list|)
expr_stmt|;
name|String
name|confFilename
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|Environment
operator|.
name|JAAS_CONF_KEY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|jaasFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|confFilename
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJaasFileBinding ()
specifier|public
name|void
name|testJaasFileBinding
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// the JVM has seemed inconsistent on setting up here
name|assertNotNull
argument_list|(
literal|"jaasFile"
argument_list|,
name|jaasFile
argument_list|)
expr_stmt|;
name|RegistrySecurity
operator|.
name|bindJVMtoJAASFile
argument_list|(
name|jaasFile
argument_list|)
expr_stmt|;
name|String
name|confFilename
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|Environment
operator|.
name|JAAS_CONF_KEY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|jaasFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|confFilename
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClientLogin ()
specifier|public
name|void
name|testClientLogin
parameter_list|()
throws|throws
name|Throwable
block|{
name|LoginContext
name|client
init|=
name|login
argument_list|(
name|ALICE_LOCALHOST
argument_list|,
name|ALICE_CLIENT_CONTEXT
argument_list|,
name|keytab_alice
argument_list|)
decl_stmt|;
try|try
block|{
name|logLoginDetails
argument_list|(
name|ALICE_LOCALHOST
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|String
name|confFilename
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|Environment
operator|.
name|JAAS_CONF_KEY
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Unset: "
operator|+
name|Environment
operator|.
name|JAAS_CONF_KEY
argument_list|,
name|confFilename
argument_list|)
expr_stmt|;
name|String
name|config
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
operator|new
name|File
argument_list|(
name|confFilename
argument_list|)
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{}=\n{}"
argument_list|,
name|confFilename
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|RegistrySecurity
operator|.
name|setZKSaslClientProperties
argument_list|(
name|ALICE
argument_list|,
name|ALICE_CLIENT_CONTEXT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testZKServerContextLogin ()
specifier|public
name|void
name|testZKServerContextLogin
parameter_list|()
throws|throws
name|Throwable
block|{
name|LoginContext
name|client
init|=
name|login
argument_list|(
name|ZOOKEEPER_LOCALHOST
argument_list|,
name|ZOOKEEPER_SERVER_CONTEXT
argument_list|,
name|keytab_zk
argument_list|)
decl_stmt|;
name|logLoginDetails
argument_list|(
name|ZOOKEEPER_LOCALHOST
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|client
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testServerLogin ()
specifier|public
name|void
name|testServerLogin
parameter_list|()
throws|throws
name|Throwable
block|{
name|LoginContext
name|loginContext
init|=
name|createLoginContextZookeeperLocalhost
argument_list|()
decl_stmt|;
name|loginContext
operator|.
name|login
argument_list|()
expr_stmt|;
name|loginContext
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
DECL|method|createLoginContextZookeeperLocalhost ()
specifier|public
name|LoginContext
name|createLoginContextZookeeperLocalhost
parameter_list|()
throws|throws
name|LoginException
block|{
name|String
name|principalAndRealm
init|=
name|getPrincipalAndRealm
argument_list|(
name|ZOOKEEPER_LOCALHOST
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
name|ZOOKEEPER_LOCALHOST
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|LoginContext
argument_list|(
literal|""
argument_list|,
name|subject
argument_list|,
literal|null
argument_list|,
name|KerberosConfiguration
operator|.
name|createServerConfig
argument_list|(
name|ZOOKEEPER_LOCALHOST
argument_list|,
name|keytab_zk
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testKerberosAuth ()
specifier|public
name|void
name|testKerberosAuth
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|krb5conf
init|=
name|getKdc
argument_list|()
operator|.
name|getKrb5conf
argument_list|()
decl_stmt|;
name|String
name|krbConfig
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|krb5conf
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"krb5.conf at {}:\n{}"
argument_list|,
name|krb5conf
argument_list|,
name|krbConfig
argument_list|)
expr_stmt|;
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|kerb5LoginClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|KerberosUtil
operator|.
name|getKrb5LoginModuleName
argument_list|()
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|?
argument_list|>
name|kerb5LoginConstr
init|=
name|kerb5LoginClass
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
name|Object
name|kerb5LoginObject
init|=
name|kerb5LoginConstr
operator|.
name|newInstance
argument_list|()
decl_stmt|;
specifier|final
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|IBM_JAVA
condition|)
block|{
name|options
operator|.
name|put
argument_list|(
literal|"useKeytab"
argument_list|,
name|keytab_alice
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"file://"
argument_list|)
condition|?
name|keytab_alice
operator|.
name|getAbsolutePath
argument_list|()
else|:
literal|"file://"
operator|+
name|keytab_alice
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"principal"
argument_list|,
name|ALICE_LOCALHOST
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
name|options
operator|.
name|put
argument_list|(
literal|"credsType"
argument_list|,
literal|"both"
argument_list|)
expr_stmt|;
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
comment|// IBM JAVA only respect system property and not env variable
comment|// The first value searched when "useDefaultCcache" is used.
name|System
operator|.
name|setProperty
argument_list|(
literal|"KRB5CCNAME"
argument_list|,
name|ticketCache
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"useDefaultCcache"
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
block|}
block|}
else|else
block|{
name|options
operator|.
name|put
argument_list|(
literal|"keyTab"
argument_list|,
name|keytab_alice
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"principal"
argument_list|,
name|ALICE_LOCALHOST
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
literal|"isInitiator"
argument_list|,
literal|"true"
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
literal|"storeKey"
argument_list|,
literal|"true"
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
literal|"useTicketCache"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|Method
name|methodInitialize
init|=
name|kerb5LoginObject
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"initialize"
argument_list|,
name|Subject
operator|.
name|class
argument_list|,
name|CallbackHandler
operator|.
name|class
argument_list|,
name|Map
operator|.
name|class
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
name|methodInitialize
operator|.
name|invoke
argument_list|(
name|kerb5LoginObject
argument_list|,
name|subject
argument_list|,
literal|null
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|Method
name|methodLogin
init|=
name|kerb5LoginObject
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"login"
argument_list|)
decl_stmt|;
name|boolean
name|loginOk
init|=
operator|(
name|Boolean
operator|)
name|methodLogin
operator|.
name|invoke
argument_list|(
name|kerb5LoginObject
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to login"
argument_list|,
name|loginOk
argument_list|)
expr_stmt|;
name|Method
name|methodCommit
init|=
name|kerb5LoginObject
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"commit"
argument_list|)
decl_stmt|;
name|boolean
name|commitOk
init|=
operator|(
name|Boolean
operator|)
name|methodCommit
operator|.
name|invoke
argument_list|(
name|kerb5LoginObject
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to Commit"
argument_list|,
name|commitOk
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultRealmValid ()
specifier|public
name|void
name|testDefaultRealmValid
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|defaultRealm
init|=
name|KerberosUtil
operator|.
name|getDefaultRealm
argument_list|()
decl_stmt|;
name|assertNotEmpty
argument_list|(
literal|"No default Kerberos Realm"
argument_list|,
name|defaultRealm
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Default Realm '{}'"
argument_list|,
name|defaultRealm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKerberosRulesValid ()
specifier|public
name|void
name|testKerberosRulesValid
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertTrue
argument_list|(
literal|"!KerberosName.hasRulesBeenSet()"
argument_list|,
name|KerberosName
operator|.
name|hasRulesBeenSet
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|rules
init|=
name|KerberosName
operator|.
name|getRules
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|kerberosRule
argument_list|,
name|rules
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|rules
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidKerberosName ()
specifier|public
name|void
name|testValidKerberosName
parameter_list|()
throws|throws
name|Throwable
block|{
name|KerberosName
operator|.
name|setRuleMechanism
argument_list|(
name|MECHANISM_HADOOP
argument_list|)
expr_stmt|;
operator|new
name|HadoopKerberosName
argument_list|(
name|ZOOKEEPER
argument_list|)
operator|.
name|getShortName
argument_list|()
expr_stmt|;
comment|// MECHANISM_MIT allows '/' and '@' in username
name|KerberosName
operator|.
name|setRuleMechanism
argument_list|(
name|MECHANISM_MIT
argument_list|)
expr_stmt|;
operator|new
name|HadoopKerberosName
argument_list|(
name|ZOOKEEPER
argument_list|)
operator|.
name|getShortName
argument_list|()
expr_stmt|;
operator|new
name|HadoopKerberosName
argument_list|(
name|ZOOKEEPER_LOCALHOST
argument_list|)
operator|.
name|getShortName
argument_list|()
expr_stmt|;
operator|new
name|HadoopKerberosName
argument_list|(
name|ZOOKEEPER_REALM
argument_list|)
operator|.
name|getShortName
argument_list|()
expr_stmt|;
operator|new
name|HadoopKerberosName
argument_list|(
name|ZOOKEEPER_LOCALHOST_REALM
argument_list|)
operator|.
name|getShortName
argument_list|()
expr_stmt|;
name|KerberosName
operator|.
name|setRuleMechanism
argument_list|(
name|DEFAULT_MECHANISM
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUGILogin ()
specifier|public
name|void
name|testUGILogin
parameter_list|()
throws|throws
name|Throwable
block|{
name|UserGroupInformation
name|ugi
init|=
name|loginUGI
argument_list|(
name|ZOOKEEPER
argument_list|,
name|keytab_zk
argument_list|)
decl_stmt|;
name|RegistrySecurity
operator|.
name|UgiInfo
name|ugiInfo
init|=
operator|new
name|RegistrySecurity
operator|.
name|UgiInfo
argument_list|(
name|ugi
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"logged in as: {}"
argument_list|,
name|ugiInfo
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"security is not enabled: "
operator|+
name|ugiInfo
argument_list|,
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"login is keytab based: "
operator|+
name|ugiInfo
argument_list|,
name|ugi
operator|.
name|isFromKeytab
argument_list|()
argument_list|)
expr_stmt|;
comment|// now we are here, build a SASL ACL
name|ACL
name|acl
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|ACL
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ACL
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|registrySecurity
operator|.
name|createSaslACLFromCurrentUser
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ZOOKEEPER_REALM
argument_list|,
name|acl
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
name|ZookeeperConfigOptions
operator|.
name|SCHEME_SASL
argument_list|,
name|acl
operator|.
name|getId
argument_list|()
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
name|registrySecurity
operator|.
name|addSystemACL
argument_list|(
name|acl
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

