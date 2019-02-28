begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.extensions
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
operator|.
name|extensions
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
name|io
operator|.
name|IOException
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
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Duration
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
name|fs
operator|.
name|FileSystem
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
name|mapred
operator|.
name|JobConf
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
name|KDiag
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
name|ssl
operator|.
name|KeyStoreTestUtil
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
name|service
operator|.
name|CompositeService
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|HADOOP_SECURITY_AUTHENTICATION
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
name|UserGroupInformation
operator|.
name|loginUserFromKeytabAndReturnUGI
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

begin_comment
comment|/**  * composite service for adding kerberos login for ABFS  * tests which require a logged in user.  * Based on  * {@code org.apache.hadoop.fs.s3a.auth.delegation.MiniKerberizedHadoopCluster}  */
end_comment

begin_class
DECL|class|KerberizedAbfsCluster
specifier|public
class|class
name|KerberizedAbfsCluster
extends|extends
name|CompositeService
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
name|KerberizedAbfsCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ALICE
specifier|public
specifier|static
specifier|final
name|String
name|ALICE
init|=
literal|"alice"
decl_stmt|;
DECL|field|BOB
specifier|public
specifier|static
specifier|final
name|String
name|BOB
init|=
literal|"bob"
decl_stmt|;
DECL|field|HTTP_LOCALHOST
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_LOCALHOST
init|=
literal|"HTTP/localhost@$LOCALHOST"
decl_stmt|;
comment|/**    * The hostname is dynamically determined based on OS, either    * "localhost" (non-windows) or 127.0.0.1 (windows).    */
DECL|field|LOCALHOST_NAME
specifier|public
specifier|static
specifier|final
name|String
name|LOCALHOST_NAME
init|=
name|Path
operator|.
name|WINDOWS
condition|?
literal|"127.0.0.1"
else|:
literal|"localhost"
decl_stmt|;
DECL|field|kdc
specifier|private
name|MiniKdc
name|kdc
decl_stmt|;
DECL|field|keytab
specifier|private
name|File
name|keytab
decl_stmt|;
DECL|field|workDir
specifier|private
name|File
name|workDir
decl_stmt|;
DECL|field|krbInstance
specifier|private
name|String
name|krbInstance
decl_stmt|;
DECL|field|loginUsername
specifier|private
name|String
name|loginUsername
decl_stmt|;
DECL|field|loginPrincipal
specifier|private
name|String
name|loginPrincipal
decl_stmt|;
DECL|field|sslConfDir
specifier|private
name|String
name|sslConfDir
decl_stmt|;
DECL|field|clientSSLConfigFileName
specifier|private
name|String
name|clientSSLConfigFileName
decl_stmt|;
DECL|field|serverSSLConfigFileName
specifier|private
name|String
name|serverSSLConfigFileName
decl_stmt|;
DECL|field|alicePrincipal
specifier|private
name|String
name|alicePrincipal
decl_stmt|;
DECL|field|bobPrincipal
specifier|private
name|String
name|bobPrincipal
decl_stmt|;
comment|/**    * Create the cluster.    * If this class's log is at DEBUG level, this also turns    * Kerberos diagnostics on in the JVM.    */
DECL|method|KerberizedAbfsCluster ()
specifier|public
name|KerberizedAbfsCluster
parameter_list|()
block|{
name|super
argument_list|(
literal|"KerberizedAbfsCluster"
argument_list|)
expr_stmt|;
comment|// load all the configs to force in the -default.xml files
operator|new
name|JobConf
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
comment|// turn on kerberos logging @ debug.
name|System
operator|.
name|setProperty
argument_list|(
name|KDiag
operator|.
name|SUN_SECURITY_KRB5_DEBUG
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|KDiag
operator|.
name|SUN_SECURITY_SPNEGO_DEBUG
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getKdc ()
specifier|public
name|MiniKdc
name|getKdc
parameter_list|()
block|{
return|return
name|kdc
return|;
block|}
DECL|method|getKeytab ()
specifier|public
name|File
name|getKeytab
parameter_list|()
block|{
return|return
name|keytab
return|;
block|}
DECL|method|getKeytabPath ()
specifier|public
name|String
name|getKeytabPath
parameter_list|()
block|{
return|return
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
DECL|method|createBobUser ()
specifier|public
name|UserGroupInformation
name|createBobUser
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|loginUserFromKeytabAndReturnUGI
argument_list|(
name|bobPrincipal
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createAliceUser ()
specifier|public
name|UserGroupInformation
name|createAliceUser
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|loginUserFromKeytabAndReturnUGI
argument_list|(
name|alicePrincipal
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getWorkDir ()
specifier|public
name|File
name|getWorkDir
parameter_list|()
block|{
return|return
name|workDir
return|;
block|}
DECL|method|getKrbInstance ()
specifier|public
name|String
name|getKrbInstance
parameter_list|()
block|{
return|return
name|krbInstance
return|;
block|}
DECL|method|getLoginUsername ()
specifier|public
name|String
name|getLoginUsername
parameter_list|()
block|{
return|return
name|loginUsername
return|;
block|}
DECL|method|getLoginPrincipal ()
specifier|public
name|String
name|getLoginPrincipal
parameter_list|()
block|{
return|return
name|loginPrincipal
return|;
block|}
DECL|method|withRealm (String user)
specifier|public
name|String
name|withRealm
parameter_list|(
name|String
name|user
parameter_list|)
block|{
return|return
name|user
operator|+
literal|"@EXAMPLE.COM"
return|;
block|}
comment|/**    * Service init creates the KDC.    * @param conf configuration    */
annotation|@
name|Override
DECL|method|serviceInit (final Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|patchConfigAtInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Properties
name|kdcConf
init|=
name|MiniKdc
operator|.
name|createConf
argument_list|()
decl_stmt|;
name|workDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
literal|"kerberos"
argument_list|)
expr_stmt|;
name|workDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|kdc
operator|=
operator|new
name|MiniKdc
argument_list|(
name|kdcConf
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|krbInstance
operator|=
name|LOCALHOST_NAME
expr_stmt|;
block|}
comment|/**    * Start the KDC, create the keytab and the alice and bob users,    * and UGI instances of them logged in from the keytab.    */
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
name|kdc
operator|.
name|start
argument_list|()
expr_stmt|;
name|keytab
operator|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"keytab.bin"
argument_list|)
expr_stmt|;
name|loginUsername
operator|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
name|loginPrincipal
operator|=
name|loginUsername
operator|+
literal|"/"
operator|+
name|krbInstance
expr_stmt|;
name|alicePrincipal
operator|=
name|ALICE
operator|+
literal|"/"
operator|+
name|krbInstance
expr_stmt|;
name|bobPrincipal
operator|=
name|BOB
operator|+
literal|"/"
operator|+
name|krbInstance
expr_stmt|;
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|keytab
argument_list|,
name|alicePrincipal
argument_list|,
name|bobPrincipal
argument_list|,
literal|"HTTP/"
operator|+
name|krbInstance
argument_list|,
name|HTTP_LOCALHOST
argument_list|,
name|loginPrincipal
argument_list|)
expr_stmt|;
specifier|final
name|File
name|keystoresDir
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"ssl"
argument_list|)
decl_stmt|;
name|keystoresDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|sslConfDir
operator|=
name|KeyStoreTestUtil
operator|.
name|getClasspathDir
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|setupSSLConfig
argument_list|(
name|keystoresDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|sslConfDir
argument_list|,
name|getConfig
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|clientSSLConfigFileName
operator|=
name|KeyStoreTestUtil
operator|.
name|getClientSSLConfigFileName
argument_list|()
expr_stmt|;
name|serverSSLConfigFileName
operator|=
name|KeyStoreTestUtil
operator|.
name|getServerSSLConfigFileName
argument_list|()
expr_stmt|;
name|String
name|kerberosRule
init|=
literal|"RULE:[1:$1@$0](.*@EXAMPLE.COM)s/@.*//\nDEFAULT"
decl_stmt|;
name|KerberosName
operator|.
name|setRules
argument_list|(
name|kerberosRule
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
comment|// this can throw an exception, but it will get caught by the superclass.
name|kdc
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|patchConfigAtInit (final Configuration conf)
specifier|protected
name|void
name|patchConfigAtInit
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
comment|// turn off some noise during debugging
name|int
name|timeout
init|=
operator|(
name|int
operator|)
name|Duration
operator|.
name|ofHours
argument_list|(
literal|1
argument_list|)
operator|.
name|toMillis
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"jvm.pause.info-threshold.ms"
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"jvm.pause.warn-threshold.ms"
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
DECL|method|resetUGI ()
specifier|public
name|void
name|resetUGI
parameter_list|()
block|{
name|UserGroupInformation
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**    * Given a shortname, built a long name with the krb instance and realm info.    * @param shortname short name of the user    * @return a long name    */
DECL|method|userOnHost (final String shortname)
specifier|private
name|String
name|userOnHost
parameter_list|(
specifier|final
name|String
name|shortname
parameter_list|)
block|{
return|return
name|shortname
operator|+
literal|"/"
operator|+
name|krbInstance
operator|+
literal|"@"
operator|+
name|getRealm
argument_list|()
return|;
block|}
DECL|method|getRealm ()
specifier|public
name|String
name|getRealm
parameter_list|()
block|{
return|return
name|kdc
operator|.
name|getRealm
argument_list|()
return|;
block|}
comment|/**    * Log in a user to UGI.currentUser.    * @param user user to log in from    * @throws IOException failure    */
DECL|method|loginUser (final String user)
specifier|public
name|void
name|loginUser
parameter_list|(
specifier|final
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
operator|.
name|loginUserFromKeytab
argument_list|(
name|user
argument_list|,
name|getKeytabPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Log in the login principal as the current user.    * @throws IOException failure    */
DECL|method|loginPrincipal ()
specifier|public
name|void
name|loginPrincipal
parameter_list|()
throws|throws
name|IOException
block|{
name|loginUser
argument_list|(
name|getLoginPrincipal
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * General assertion that security is turred on for a cluster.    */
DECL|method|assertSecurityEnabled ()
specifier|public
specifier|static
name|void
name|assertSecurityEnabled
parameter_list|()
block|{
name|assertTrue
argument_list|(
literal|"Security is needed for this test"
argument_list|,
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Close filesystems for a user, downgrading a null user to a no-op.    * @param ugi user    * @throws IOException if a close operation raised one.    */
DECL|method|closeUserFileSystems (UserGroupInformation ugi)
specifier|public
specifier|static
name|void
name|closeUserFileSystems
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ugi
operator|!=
literal|null
condition|)
block|{
name|FileSystem
operator|.
name|closeAllForUGI
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Modify a configuration to use Kerberos as the auth method.    * @param conf configuration to patch.    */
DECL|method|bindConfToCluster (Configuration conf)
specifier|public
name|void
name|bindConfToCluster
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
name|UserGroupInformation
operator|.
name|AuthenticationMethod
operator|.
name|KERBEROS
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_USER_GROUP_STATIC_OVERRIDES
argument_list|,
literal|"alice,alice"
argument_list|)
expr_stmt|;
comment|// a shortname for the RM principal avoids kerberos mapping problems.
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_PRINCIPAL
argument_list|,
name|BOB
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility method to create a URI, converting URISyntaxException    * to RuntimeExceptions. This makes it easier to set up URIs    * in static fields.    * @param uri URI to create.    * @return the URI.    * @throws RuntimeException syntax error.    */
DECL|method|newURI (String uri)
specifier|public
specifier|static
name|URI
name|newURI
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create the filename for a temporary token file, in the    * work dir of this cluster.    * @return a filename which does not exist.    * @throws IOException failure    */
DECL|method|createTempTokenFile ()
specifier|public
name|File
name|createTempTokenFile
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tokenfile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"tokens"
argument_list|,
literal|".bin"
argument_list|,
name|getWorkDir
argument_list|()
argument_list|)
decl_stmt|;
name|tokenfile
operator|.
name|delete
argument_list|()
expr_stmt|;
return|return
name|tokenfile
return|;
block|}
block|}
end_class

end_unit

