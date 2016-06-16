begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
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
name|DFS_JOURNALNODE_HTTPS_ADDRESS_KEY
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
name|DFS_JOURNALNODE_KERBEROS_INTERNAL_SPNEGO_PRINCIPAL_KEY
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
name|DFS_JOURNALNODE_KERBEROS_PRINCIPAL_KEY
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
name|DFS_JOURNALNODE_KEYTAB_FILE_KEY
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
name|DFS_NAMENODE_EDITS_DIR_KEY
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
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
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
name|MiniDFSCluster
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
name|io
operator|.
name|IOUtils
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
name|AfterClass
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
name|Timeout
import|;
end_import

begin_class
DECL|class|TestSecureNNWithQJM
specifier|public
class|class
name|TestSecureNNWithQJM
block|{
DECL|field|TEST_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/test-dir"
argument_list|)
decl_stmt|;
DECL|field|TEST_PATH_2
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_PATH_2
init|=
operator|new
name|Path
argument_list|(
literal|"/test-dir-2"
argument_list|)
decl_stmt|;
DECL|field|baseConf
specifier|private
specifier|static
name|HdfsConfiguration
name|baseConf
decl_stmt|;
DECL|field|baseDir
specifier|private
specifier|static
name|File
name|baseDir
decl_stmt|;
DECL|field|kdc
specifier|private
specifier|static
name|MiniKdc
name|kdc
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|HdfsConfiguration
name|conf
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|mjc
specifier|private
name|MiniJournalCluster
name|mjc
decl_stmt|;
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|30000
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|baseDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestSecureNNWithQJM
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
name|baseConf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
name|AuthenticationMethod
operator|.
name|KERBEROS
argument_list|,
name|baseConf
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|baseConf
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
name|String
name|userName
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|File
name|keytabFile
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
name|userName
operator|+
literal|"/"
operator|+
name|krbInstance
argument_list|,
literal|"HTTP/"
operator|+
name|krbInstance
argument_list|)
expr_stmt|;
name|String
name|hdfsPrincipal
init|=
name|userName
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
decl_stmt|;
name|String
name|spnegoPrincipal
init|=
literal|"HTTP/"
operator|+
name|krbInstance
operator|+
literal|"@"
operator|+
name|kdc
operator|.
name|getRealm
argument_list|()
decl_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|hdfsPrincipal
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_KEYTAB_FILE_KEY
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|hdfsPrincipal
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_KEYTAB_FILE_KEY
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|spnegoPrincipal
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_JOURNALNODE_KEYTAB_FILE_KEY
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_JOURNALNODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|hdfsPrincipal
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_JOURNALNODE_KERBEROS_INTERNAL_SPNEGO_PRINCIPAL_KEY
argument_list|,
name|spnegoPrincipal
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|setBoolean
argument_list|(
name|DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_DATA_TRANSFER_PROTECTION_KEY
argument_list|,
literal|"authentication"
argument_list|)
expr_stmt|;
name|baseConf
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
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|DFS_JOURNALNODE_HTTPS_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|baseConf
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
name|TestSecureNNWithQJM
operator|.
name|class
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
name|baseConf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|baseConf
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
name|baseConf
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
block|}
annotation|@
name|AfterClass
DECL|method|destroy ()
specifier|public
specifier|static
name|void
name|destroy
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
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|(
name|baseConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|fs
argument_list|)
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|mjc
operator|!=
literal|null
condition|)
block|{
name|mjc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|mjc
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSecureMode ()
specifier|public
name|void
name|testSecureMode
parameter_list|()
throws|throws
name|Exception
block|{
name|doNNWithQJMTest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSecondaryNameNodeHttpAddressNotNeeded ()
specifier|public
name|void
name|testSecondaryNameNodeHttpAddressNotNeeded
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|,
literal|"null"
argument_list|)
expr_stmt|;
name|doNNWithQJMTest
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests use of QJM with the defined cluster.    *    * @throws IOException if there is an I/O error    */
DECL|method|doNNWithQJMTest ()
specifier|private
name|void
name|doNNWithQJMTest
parameter_list|()
throws|throws
name|IOException
block|{
name|startCluster
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
comment|// Restart the NN and make sure the edit was persisted
comment|// and loaded again
name|restartNameNode
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|TEST_PATH_2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Restart the NN again and make sure both edits are persisted.
name|restartNameNode
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|TEST_PATH_2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Restarts the NameNode and obtains a new FileSystem.    *    * @throws IOException if there is an I/O error    */
DECL|method|restartNameNode ()
specifier|private
name|void
name|restartNameNode
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
comment|/**    * Starts a cluster using QJM with the defined configuration.    *    * @throws IOException if there is an I/O error    */
DECL|method|startCluster ()
specifier|private
name|void
name|startCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|mjc
operator|=
operator|new
name|MiniJournalCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|mjc
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
name|mjc
operator|.
name|getQuorumJournalURI
argument_list|(
literal|"myjournal"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

