begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol.datatransfer.sasl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|sasl
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
name|CommonConfigurationKeys
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SASL_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY
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
name|commons
operator|.
name|lang
operator|.
name|RandomStringUtils
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
operator|.
name|AuthenticationMethod
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

begin_class
DECL|class|SaslDataTransferTestCase
specifier|public
specifier|abstract
class|class
name|SaslDataTransferTestCase
block|{
DECL|field|baseDir
specifier|private
specifier|static
name|File
name|baseDir
decl_stmt|;
DECL|field|hdfsPrincipal
specifier|private
specifier|static
name|String
name|hdfsPrincipal
decl_stmt|;
DECL|field|userPrincipal
specifier|private
specifier|static
name|String
name|userPrincipal
decl_stmt|;
DECL|field|kdc
specifier|private
specifier|static
name|MiniKdc
name|kdc
decl_stmt|;
DECL|field|hdfsKeytab
specifier|private
specifier|static
name|String
name|hdfsKeytab
decl_stmt|;
DECL|field|userKeyTab
specifier|private
specifier|static
name|String
name|userKeyTab
decl_stmt|;
DECL|field|spnegoPrincipal
specifier|private
specifier|static
name|String
name|spnegoPrincipal
decl_stmt|;
DECL|method|getUserKeyTab ()
specifier|public
specifier|static
name|String
name|getUserKeyTab
parameter_list|()
block|{
return|return
name|userKeyTab
return|;
block|}
DECL|method|getUserPrincipal ()
specifier|public
specifier|static
name|String
name|getUserPrincipal
parameter_list|()
block|{
return|return
name|userPrincipal
return|;
block|}
DECL|method|getHdfsPrincipal ()
specifier|public
specifier|static
name|String
name|getHdfsPrincipal
parameter_list|()
block|{
return|return
name|hdfsPrincipal
return|;
block|}
DECL|method|getHdfsKeytab ()
specifier|public
specifier|static
name|String
name|getHdfsKeytab
parameter_list|()
block|{
return|return
name|hdfsKeytab
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|initKdc ()
specifier|public
specifier|static
name|void
name|initKdc
parameter_list|()
throws|throws
name|Exception
block|{
name|baseDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.dir"
argument_list|,
literal|"target/test-dir"
argument_list|)
argument_list|,
name|SaslDataTransferTestCase
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
name|String
name|userName
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|File
name|userKeytabFile
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|userName
operator|+
literal|".keytab"
argument_list|)
decl_stmt|;
name|userKeyTab
operator|=
name|userKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|userKeytabFile
argument_list|,
name|userName
operator|+
literal|"/localhost"
argument_list|)
expr_stmt|;
name|userPrincipal
operator|=
name|userName
operator|+
literal|"/localhost@"
operator|+
name|kdc
operator|.
name|getRealm
argument_list|()
expr_stmt|;
name|String
name|superUserName
init|=
literal|"hdfs"
decl_stmt|;
name|File
name|hdfsKeytabFile
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|superUserName
operator|+
literal|".keytab"
argument_list|)
decl_stmt|;
name|hdfsKeytab
operator|=
name|hdfsKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|hdfsKeytabFile
argument_list|,
name|superUserName
operator|+
literal|"/localhost"
argument_list|,
literal|"HTTP/localhost"
argument_list|)
expr_stmt|;
name|hdfsPrincipal
operator|=
name|superUserName
operator|+
literal|"/localhost@"
operator|+
name|kdc
operator|.
name|getRealm
argument_list|()
expr_stmt|;
name|spnegoPrincipal
operator|=
literal|"HTTP/localhost@"
operator|+
name|kdc
operator|.
name|getRealm
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdownKdc ()
specifier|public
specifier|static
name|void
name|shutdownKdc
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
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates configuration for starting a secure cluster.    *    * @param dataTransferProtection supported QOPs    * @return configuration for starting a secure cluster    * @throws Exception if there is any failure    */
DECL|method|createSecureConfig ( String dataTransferProtection)
specifier|protected
name|HdfsConfiguration
name|createSecureConfig
parameter_list|(
name|String
name|dataTransferProtection
parameter_list|)
throws|throws
name|Exception
block|{
name|HdfsConfiguration
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
name|AuthenticationMethod
operator|.
name|KERBEROS
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|hdfsPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_KEYTAB_FILE_KEY
argument_list|,
name|hdfsKeytab
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|hdfsPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_KEYTAB_FILE_KEY
argument_list|,
name|hdfsKeytab
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|spnegoPrincipal
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
name|dataTransferProtection
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
name|setInt
argument_list|(
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SASL_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|String
name|keystoresDir
init|=
name|baseDir
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|sslConfDir
init|=
name|KeyStoreTestUtil
operator|.
name|getClasspathDir
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
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
return|return
name|conf
return|;
block|}
block|}
end_class

end_unit

