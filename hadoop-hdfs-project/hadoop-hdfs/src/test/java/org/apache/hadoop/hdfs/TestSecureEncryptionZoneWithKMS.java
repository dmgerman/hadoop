begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|assertTrue
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|KMS_CLIENT_ENC_KEY_CACHE_LOW_WATERMARK
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
name|KMS_CLIENT_ENC_KEY_CACHE_SIZE
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
name|FileWriter
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
name|io
operator|.
name|Writer
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
name|EnumSet
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
name|UUID
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|KMSClientProvider
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|KMSConfiguration
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|MiniKMS
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
name|FileSystemTestWrapper
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|client
operator|.
name|CreateEncryptionZoneFlag
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
name|client
operator|.
name|HdfsAdmin
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
comment|/**  * Test for HDFS encryption zone without external Kerberos KDC by leveraging  * Kerby-based MiniKDC, MiniKMS and MiniDFSCluster. This provides additional  * unit test coverage on Secure(Kerberos) KMS + HDFS.  */
end_comment

begin_class
DECL|class|TestSecureEncryptionZoneWithKMS
specifier|public
class|class
name|TestSecureEncryptionZoneWithKMS
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestSecureEncryptionZoneWithKMS
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|field|NO_TRASH
specifier|private
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|CreateEncryptionZoneFlag
argument_list|>
name|NO_TRASH
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateEncryptionZoneFlag
operator|.
name|NO_TRASH
argument_list|)
decl_stmt|;
DECL|field|HDFS_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|HDFS_USER_NAME
init|=
literal|"hdfs"
decl_stmt|;
DECL|field|SPNEGO_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SPNEGO_USER_NAME
init|=
literal|"HTTP"
decl_stmt|;
DECL|field|OOZIE_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|OOZIE_USER_NAME
init|=
literal|"oozie"
decl_stmt|;
DECL|field|OOZIE_PROXIED_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|OOZIE_PROXIED_USER_NAME
init|=
literal|"oozie_user"
decl_stmt|;
DECL|field|hdfsPrincipal
specifier|private
specifier|static
name|String
name|hdfsPrincipal
decl_stmt|;
DECL|field|spnegoPrincipal
specifier|private
specifier|static
name|String
name|spnegoPrincipal
decl_stmt|;
DECL|field|ooziePrincipal
specifier|private
specifier|static
name|String
name|ooziePrincipal
decl_stmt|;
DECL|field|keytab
specifier|private
specifier|static
name|String
name|keytab
decl_stmt|;
comment|// MiniKDC
DECL|field|kdc
specifier|private
specifier|static
name|MiniKdc
name|kdc
decl_stmt|;
comment|// MiniKMS
DECL|field|miniKMS
specifier|private
specifier|static
name|MiniKMS
name|miniKMS
decl_stmt|;
DECL|field|testKey
specifier|private
specifier|final
name|String
name|testKey
init|=
literal|"test_key"
decl_stmt|;
DECL|field|testKeyCreated
specifier|private
specifier|static
name|boolean
name|testKeyCreated
init|=
literal|false
decl_stmt|;
DECL|field|AUTH_TOKEN_VALIDITY
specifier|private
specifier|static
specifier|final
name|long
name|AUTH_TOKEN_VALIDITY
init|=
literal|1
decl_stmt|;
comment|// MiniDFS
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
DECL|field|dfsAdmin
specifier|private
name|HdfsAdmin
name|dfsAdmin
decl_stmt|;
DECL|field|fsWrapper
specifier|private
name|FileSystemTestWrapper
name|fsWrapper
decl_stmt|;
DECL|method|getTestDir ()
specifier|public
specifier|static
name|File
name|getTestDir
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"dummy"
argument_list|)
decl_stmt|;
name|file
operator|=
name|file
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|file
operator|=
name|file
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
literal|"target"
argument_list|)
expr_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not create test directory: "
operator|+
name|file
argument_list|)
throw|;
block|}
return|return
name|file
return|;
block|}
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
literal|120000
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
name|getTestDir
argument_list|()
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
name|keytab
operator|=
name|keytabFile
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
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
name|HDFS_USER_NAME
operator|+
literal|"/"
operator|+
name|krbInstance
argument_list|,
name|SPNEGO_USER_NAME
operator|+
literal|"/"
operator|+
name|krbInstance
argument_list|,
name|OOZIE_USER_NAME
operator|+
literal|"/"
operator|+
name|krbInstance
argument_list|,
name|OOZIE_PROXIED_USER_NAME
operator|+
literal|"/"
operator|+
name|krbInstance
argument_list|)
expr_stmt|;
name|hdfsPrincipal
operator|=
name|HDFS_USER_NAME
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
name|ooziePrincipal
operator|=
name|OOZIE_USER_NAME
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
comment|// Allow oozie to proxy user
name|baseConf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser.oozie.hosts"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser.oozie.groups"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
literal|"hadoop.user.group.static.mapping.overrides"
argument_list|,
name|OOZIE_PROXIED_USER_NAME
operator|+
literal|"=oozie"
argument_list|)
expr_stmt|;
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
comment|// Set a small (2=4*0.5) KMSClient EDEK cache size to trigger
comment|// on demand refill upon the 3rd file creation
name|baseConf
operator|.
name|set
argument_list|(
name|KMS_CLIENT_ENC_KEY_CACHE_SIZE
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|set
argument_list|(
name|KMS_CLIENT_ENC_KEY_CACHE_LOW_WATERMARK
argument_list|,
literal|"0.5"
argument_list|)
expr_stmt|;
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
name|TestSecureEncryptionZoneWithKMS
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
name|File
name|kmsFile
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"kms-site.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|kmsFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|kmsFile
argument_list|)
expr_stmt|;
block|}
name|Configuration
name|kmsConf
init|=
operator|new
name|Configuration
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|kmsConf
operator|.
name|set
argument_list|(
name|KMSConfiguration
operator|.
name|KEY_PROVIDER_URI
argument_list|,
literal|"jceks://file@"
operator|+
operator|new
name|Path
argument_list|(
name|baseDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"kms.keystore"
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|kmsConf
operator|.
name|set
argument_list|(
literal|"hadoop.kms.authentication.type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|kmsConf
operator|.
name|set
argument_list|(
literal|"hadoop.kms.authentication.kerberos.keytab"
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|kmsConf
operator|.
name|set
argument_list|(
literal|"hadoop.kms.authentication.kerberos.principal"
argument_list|,
literal|"HTTP/localhost"
argument_list|)
expr_stmt|;
name|kmsConf
operator|.
name|set
argument_list|(
literal|"hadoop.kms.authentication.kerberos.name.rules"
argument_list|,
literal|"DEFAULT"
argument_list|)
expr_stmt|;
name|kmsConf
operator|.
name|set
argument_list|(
literal|"hadoop.kms.acl.GENERATE_EEK"
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
comment|// set kms auth token expiration low for testCreateZoneAfterAuthTokenExpiry
name|kmsConf
operator|.
name|setLong
argument_list|(
literal|"hadoop.kms.authentication.token.validity"
argument_list|,
name|AUTH_TOKEN_VALIDITY
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|kmsFile
argument_list|)
decl_stmt|;
name|kmsConf
operator|.
name|writeXml
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Start MiniKMS
name|MiniKMS
operator|.
name|Builder
name|miniKMSBuilder
init|=
operator|new
name|MiniKMS
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|miniKMS
operator|=
name|miniKMSBuilder
operator|.
name|setKmsConfDir
argument_list|(
name|baseDir
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|miniKMS
operator|.
name|start
argument_list|()
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
block|}
if|if
condition|(
name|miniKMS
operator|!=
literal|null
condition|)
block|{
name|miniKMS
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
comment|// Start MiniDFS Cluster
name|baseConf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_KEY_PROVIDER_PATH
argument_list|,
name|getKeyProviderURI
argument_list|()
argument_list|)
expr_stmt|;
name|baseConf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|(
name|baseConf
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
name|fsWrapper
operator|=
operator|new
name|FileSystemTestWrapper
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|dfsAdmin
operator|=
operator|new
name|HdfsAdmin
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Wait cluster to be active
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Create a test key
if|if
condition|(
operator|!
name|testKeyCreated
condition|)
block|{
name|DFSTestUtil
operator|.
name|createKey
argument_list|(
name|testKey
argument_list|,
name|cluster
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|testKeyCreated
operator|=
literal|true
expr_stmt|;
block|}
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
block|}
DECL|method|getKeyProviderURI ()
specifier|private
name|String
name|getKeyProviderURI
parameter_list|()
block|{
return|return
name|KMSClientProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://"
operator|+
name|miniKMS
operator|.
name|getKMSUrl
argument_list|()
operator|.
name|toExternalForm
argument_list|()
operator|.
name|replace
argument_list|(
literal|"://"
argument_list|,
literal|"@"
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testSecureEncryptionZoneWithKMS ()
specifier|public
name|void
name|testSecureEncryptionZoneWithKMS
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|Path
name|zonePath
init|=
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|,
literal|"TestEZ1"
argument_list|)
decl_stmt|;
name|fsWrapper
operator|.
name|mkdir
argument_list|(
name|zonePath
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fsWrapper
operator|.
name|setOwner
argument_list|(
name|zonePath
argument_list|,
name|OOZIE_PROXIED_USER_NAME
argument_list|,
literal|"supergroup"
argument_list|)
expr_stmt|;
name|dfsAdmin
operator|.
name|createEncryptionZone
argument_list|(
name|zonePath
argument_list|,
name|testKey
argument_list|,
name|NO_TRASH
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|oozieUgi
init|=
name|UserGroupInformation
operator|.
name|loginUserFromKeytabAndReturnUGI
argument_list|(
name|ooziePrincipal
argument_list|,
name|keytab
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUserUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|OOZIE_PROXIED_USER_NAME
argument_list|,
name|oozieUgi
argument_list|)
decl_stmt|;
name|proxyUserUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Get a client handler within the proxy user context for createFile
try|try
init|(
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|zonePath
argument_list|,
literal|"testData."
operator|+
name|i
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|filePath
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateZoneAfterAuthTokenExpiry ()
specifier|public
name|void
name|testCreateZoneAfterAuthTokenExpiry
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|loginUserFromKeytabAndReturnUGI
argument_list|(
name|hdfsPrincipal
argument_list|,
name|keytab
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created ugi: {} "
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
call|(
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
call|)
argument_list|()
operator|->
block|{
name|final
name|Path
name|zone
operator|=
operator|new
name|Path
argument_list|(
literal|"/expire1"
argument_list|)
block|;
name|fsWrapper
operator|.
name|mkdir
argument_list|(
name|zone
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|,
literal|true
argument_list|)
block|;
name|dfsAdmin
operator|.
name|createEncryptionZone
argument_list|(
name|zone
argument_list|,
name|testKey
argument_list|,
name|NO_TRASH
argument_list|)
block|;
name|final
name|Path
name|zone1
operator|=
operator|new
name|Path
argument_list|(
literal|"/expire2"
argument_list|)
block|;
name|fsWrapper
operator|.
name|mkdir
argument_list|(
name|zone1
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|,
literal|true
argument_list|)
block|;
name|final
name|long
name|sleepInterval
operator|=
operator|(
name|AUTH_TOKEN_VALIDITY
operator|+
literal|1
operator|)
operator|*
literal|1000
block|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleeping {} seconds to wait for kms auth token expiration"
argument_list|,
name|sleepInterval
argument_list|)
block|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepInterval
argument_list|)
block|;
name|dfsAdmin
operator|.
name|createEncryptionZone
argument_list|(
name|zone1
argument_list|,
name|testKey
argument_list|,
name|NO_TRASH
argument_list|)
block|;
return|return
literal|null
return|;
block|}
block|)
function|;
block|}
end_class

unit|}
end_unit

