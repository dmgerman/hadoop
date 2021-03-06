begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed under the Apache License, Version 2.0 (the "License");  *   you may not use this file except in compliance with the License.  *   You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  *   Unless required by applicable law or agreed to in writing, software  *   distributed under the License is distributed on an "AS IS" BASIS,  *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *   See the License for the specific language governing permissions and  *   limitations under the License. See accompanying LICENSE file.  */
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_HTTPS_KEYSTORE_RESOURCE_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_KERBEROS_PRINCIPAL_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_KEYTAB_FILE_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_HTTP_POLICY_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KEYTAB_FILE_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_KEY
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
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_DATA_TRANSFER_PROTECTION_KEY
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
name|DFS_ROUTER_KERBEROS_INTERNAL_SPNEGO_PRINCIPAL_KEY
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
name|DFS_ROUTER_KERBEROS_PRINCIPAL_KEY
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
name|DFS_ROUTER_RPC_BIND_HOST_KEY
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
name|DFS_ROUTER_DELEGATION_TOKEN_DRIVER_CLASS
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
name|util
operator|.
name|Properties
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
name|FileUtil
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|RBFConfigKeys
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
name|store
operator|.
name|driver
operator|.
name|StateStoreDriver
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
name|store
operator|.
name|driver
operator|.
name|impl
operator|.
name|StateStoreFileImpl
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
name|security
operator|.
name|MockDelegationTokenSecretManager
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
name|HttpConfig
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_comment
comment|/**  * Test utility to provide a standard routine to initialize the configuration  * for secure RBF HDFS cluster.  */
end_comment

begin_class
DECL|class|SecurityConfUtil
specifier|public
specifier|final
class|class
name|SecurityConfUtil
block|{
comment|// SSL keystore
DECL|field|keystoresDir
specifier|private
specifier|static
name|String
name|keystoresDir
decl_stmt|;
DECL|field|sslConfDir
specifier|private
specifier|static
name|String
name|sslConfDir
decl_stmt|;
comment|// State string for mini dfs
DECL|field|SPNEGO_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SPNEGO_USER_NAME
init|=
literal|"HTTP"
decl_stmt|;
DECL|field|ROUTER_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|ROUTER_USER_NAME
init|=
literal|"router"
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
DECL|field|kdc
specifier|private
specifier|static
name|MiniKdc
name|kdc
decl_stmt|;
DECL|field|baseDir
specifier|private
specifier|static
name|File
name|baseDir
decl_stmt|;
DECL|field|spnegoPrincipal
specifier|private
specifier|static
name|String
name|spnegoPrincipal
decl_stmt|;
DECL|field|routerPrincipal
specifier|private
specifier|static
name|String
name|routerPrincipal
decl_stmt|;
DECL|method|SecurityConfUtil ()
specifier|private
name|SecurityConfUtil
parameter_list|()
block|{
comment|// Utility Class
block|}
DECL|method|getRouterUserName ()
specifier|public
specifier|static
name|String
name|getRouterUserName
parameter_list|()
block|{
return|return
name|ROUTER_USER_NAME
return|;
block|}
DECL|method|initSecurity ()
specifier|public
specifier|static
name|Configuration
name|initSecurity
parameter_list|()
throws|throws
name|Exception
block|{
comment|// delete old test dir
name|baseDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|SecurityConfUtil
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|baseDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// start a mini kdc with default conf
name|Properties
name|kdcConf
init|=
name|MiniKdc
operator|.
name|createConf
argument_list|()
decl_stmt|;
name|kdc
operator|=
operator|new
name|MiniKdc
argument_list|(
name|kdcConf
argument_list|,
name|baseDir
argument_list|)
expr_stmt|;
name|kdc
operator|.
name|start
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
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
name|assertTrue
argument_list|(
literal|"Expected configuration to enable security"
argument_list|,
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|)
expr_stmt|;
comment|// Setup the keytab
name|File
name|keytabFile
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"test.keytab"
argument_list|)
decl_stmt|;
name|String
name|keytab
init|=
name|keytabFile
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
comment|// Windows will not reverse name lookup "127.0.0.1" to "localhost".
name|String
name|krbInstance
init|=
name|Path
operator|.
name|WINDOWS
condition|?
literal|"127.0.0.1"
else|:
literal|"localhost"
decl_stmt|;
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|keytabFile
argument_list|,
name|SPNEGO_USER_NAME
operator|+
literal|"/"
operator|+
name|krbInstance
argument_list|,
name|ROUTER_USER_NAME
operator|+
literal|"/"
operator|+
name|krbInstance
argument_list|)
expr_stmt|;
name|routerPrincipal
operator|=
name|ROUTER_USER_NAME
operator|+
literal|"/"
operator|+
name|krbInstance
operator|+
literal|"@"
operator|+
name|kdc
operator|.
name|getRealm
argument_list|()
expr_stmt|;
name|spnegoPrincipal
operator|=
name|SPNEGO_USER_NAME
operator|+
literal|"/"
operator|+
name|krbInstance
operator|+
literal|"@"
operator|+
name|kdc
operator|.
name|getRealm
argument_list|()
expr_stmt|;
comment|// Setup principles and keytabs for dfs
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|routerPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_KEYTAB_FILE_KEY
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|routerPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_KEYTAB_FILE_KEY
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|conf
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
name|conf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"kerberos.principal"
argument_list|,
name|spnegoPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"kerberos.keytab"
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATA_TRANSFER_PROTECTION_KEY
argument_list|,
literal|"authentication"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_HTTP_POLICY_KEY
argument_list|,
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTPS_ONLY
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
comment|// Setup SSL configuration
name|keystoresDir
operator|=
name|baseDir
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|sslConfDir
operator|=
name|KeyStoreTestUtil
operator|.
name|getClasspathDir
argument_list|(
name|SecurityConfUtil
operator|.
name|class
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|setupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_CLIENT_HTTPS_KEYSTORE_RESOURCE_KEY
argument_list|,
name|KeyStoreTestUtil
operator|.
name|getClientSSLConfigFileName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_KEY
argument_list|,
name|KeyStoreTestUtil
operator|.
name|getServerSSLConfigFileName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Setup principals and keytabs for router
name|conf
operator|.
name|set
argument_list|(
name|DFS_ROUTER_KEYTAB_FILE_KEY
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_ROUTER_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|routerPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_ROUTER_KERBEROS_INTERNAL_SPNEGO_PRINCIPAL_KEY
argument_list|,
name|spnegoPrincipal
argument_list|)
expr_stmt|;
comment|// Setup basic state store
name|conf
operator|.
name|setClass
argument_list|(
name|RBFConfigKeys
operator|.
name|FEDERATION_STORE_DRIVER_CLASS
argument_list|,
name|StateStoreFileImpl
operator|.
name|class
argument_list|,
name|StateStoreDriver
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// We need to specify the host to prevent 0.0.0.0 as the host address
name|conf
operator|.
name|set
argument_list|(
name|DFS_ROUTER_RPC_BIND_HOST_KEY
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_ROUTER_DELEGATION_TOKEN_DRIVER_CLASS
argument_list|,
name|MockDelegationTokenSecretManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|destroy ()
specifier|public
specifier|static
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
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
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|cleanupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

