begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|security
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
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|DFSTestUtil
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
name|DistributedFileSystem
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenSecretManager
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|namenode
operator|.
name|NameNode
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
name|namenode
operator|.
name|NameNodeAdapter
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
name|namenode
operator|.
name|web
operator|.
name|resources
operator|.
name|NamenodeWebHdfsMethods
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
name|web
operator|.
name|WebHdfsFileSystem
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
name|Text
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
name|AccessControlException
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
name|Credentials
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
name|token
operator|.
name|SecretManager
operator|.
name|InvalidToken
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
name|token
operator|.
name|Token
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
name|Test
import|;
end_import

begin_class
DECL|class|TestDelegationToken
specifier|public
class|class
name|TestDelegationToken
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dtSecretManager
specifier|private
name|DelegationTokenSecretManager
name|dtSecretManager
decl_stmt|;
DECL|field|config
specifier|private
name|Configuration
name|config
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDelegationToken
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|config
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_MAX_LIFETIME_KEY
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|config
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_RENEW_INTERVAL_KEY
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|config
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
name|config
operator|.
name|set
argument_list|(
literal|"hadoop.security.auth_to_local"
argument_list|,
literal|"RULE:[2:$1@$0](JobTracker@.*FOO.COM)s/@.*//"
operator|+
literal|"DEFAULT"
argument_list|)
expr_stmt|;
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|config
argument_list|,
literal|"hdfs://localhost:"
operator|+
literal|"0"
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
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
name|dtSecretManager
operator|=
name|NameNodeAdapter
operator|.
name|getDtSecretManager
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
block|}
DECL|method|generateDelegationToken ( String owner, String renewer)
specifier|private
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|generateDelegationToken
parameter_list|(
name|String
name|owner
parameter_list|,
name|String
name|renewer
parameter_list|)
block|{
name|DelegationTokenIdentifier
name|dtId
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
name|owner
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|(
name|dtId
argument_list|,
name|dtSecretManager
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenSecretManager ()
specifier|public
name|void
name|testDelegationTokenSecretManager
parameter_list|()
throws|throws
name|Exception
block|{
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|generateDelegationToken
argument_list|(
literal|"SomeUser"
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
comment|// Fake renewer should not be able to renew
try|try
block|{
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"FakeRenewer"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ace
parameter_list|)
block|{
comment|// PASS
block|}
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
name|DelegationTokenIdentifier
name|identifier
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenId
init|=
name|token
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|identifier
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|tokenId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|null
operator|!=
name|dtSecretManager
operator|.
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleep to expire the token"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|6000
argument_list|)
expr_stmt|;
comment|//Token should be expired
try|try
block|{
name|dtSecretManager
operator|.
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
comment|//Should not come here
name|Assert
operator|.
name|fail
argument_list|(
literal|"Token should have expired"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidToken
name|e
parameter_list|)
block|{
comment|//Success
block|}
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleep beyond the max lifetime"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
try|try
block|{
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"should have been expired"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidToken
name|it
parameter_list|)
block|{
comment|// PASS
block|}
block|}
annotation|@
name|Test
DECL|method|testCancelDelegationToken ()
specifier|public
name|void
name|testCancelDelegationToken
parameter_list|()
throws|throws
name|Exception
block|{
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|generateDelegationToken
argument_list|(
literal|"SomeUser"
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
comment|//Fake renewer should not be able to renew
try|try
block|{
name|dtSecretManager
operator|.
name|cancelToken
argument_list|(
name|token
argument_list|,
literal|"FakeCanceller"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ace
parameter_list|)
block|{
comment|// PASS
block|}
name|dtSecretManager
operator|.
name|cancelToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
try|try
block|{
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidToken
name|it
parameter_list|)
block|{
comment|// PASS
block|}
block|}
annotation|@
name|Test
DECL|method|testAddDelegationTokensDFSApi ()
specifier|public
name|void
name|testAddDelegationTokensDFSApi
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Credentials
name|creds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|tokens
index|[]
init|=
name|dfs
operator|.
name|addDelegationTokens
argument_list|(
literal|"JobTracker"
argument_list|,
name|creds
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tokens
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|creds
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
name|checkTokenIdentifier
argument_list|(
name|ugi
argument_list|,
name|tokens
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|tokens2
index|[]
init|=
name|dfs
operator|.
name|addDelegationTokens
argument_list|(
literal|"JobTracker"
argument_list|,
name|creds
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tokens2
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// already have token
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|creds
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenWebHdfsApi ()
specifier|public
name|void
name|testDelegationTokenWebHdfsApi
parameter_list|()
throws|throws
name|Exception
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|NamenodeWebHdfsMethods
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
specifier|final
name|String
name|uri
init|=
name|WebHdfsFileSystem
operator|.
name|SCHEME
operator|+
literal|"://"
operator|+
name|config
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|)
decl_stmt|;
comment|//get file system as JobTracker
specifier|final
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"JobTracker"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"user"
block|}
argument_list|)
decl_stmt|;
specifier|final
name|WebHdfsFileSystem
name|webhdfs
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|WebHdfsFileSystem
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|WebHdfsFileSystem
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|WebHdfsFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
argument_list|,
name|config
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
block|{
comment|//test addDelegationTokens(..)
name|Credentials
name|creds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|tokens
index|[]
init|=
name|webhdfs
operator|.
name|addDelegationTokens
argument_list|(
literal|"JobTracker"
argument_list|,
name|creds
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tokens
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|creds
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertSame
argument_list|(
name|tokens
index|[
literal|0
index|]
argument_list|,
name|creds
operator|.
name|getAllTokens
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|checkTokenIdentifier
argument_list|(
name|ugi
argument_list|,
name|tokens
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|tokens2
index|[]
init|=
name|webhdfs
operator|.
name|addDelegationTokens
argument_list|(
literal|"JobTracker"
argument_list|,
name|creds
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tokens2
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
DECL|method|testDelegationTokenWithDoAs ()
specifier|public
name|void
name|testDelegationTokenWithDoAs
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Credentials
name|creds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|tokens
index|[]
init|=
name|dfs
operator|.
name|addDelegationTokens
argument_list|(
literal|"JobTracker"
argument_list|,
name|creds
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tokens
operator|.
name|length
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
operator|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
operator|)
name|tokens
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|UserGroupInformation
name|longUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"JobTracker/foo.com@FOO.COM"
argument_list|)
decl_stmt|;
specifier|final
name|UserGroupInformation
name|shortUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|longUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
comment|//try renew with long name
name|dfs
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Could not renew delegation token for user "
operator|+
name|longUgi
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|shortUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|dfs
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|longUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
comment|//try cancel with long name
name|dfs
operator|.
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Could not cancel delegation token for user "
operator|+
name|longUgi
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the delegation token secret manager only runs when the    * NN is out of safe mode. This is because the secret manager    * has to log to the edit log, which should not be written in    * safe mode. Regression test for HDFS-2579.    */
annotation|@
name|Test
DECL|method|testDTManagerInSafeMode ()
specifier|public
name|void
name|testDTManagerInSafeMode
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|config
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
name|StartupOption
operator|.
name|REGULAR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/test-"
operator|+
name|i
argument_list|)
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_KEY_UPDATE_INTERVAL_KEY
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_EXTENSION_KEY
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setWaitSafeMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|nn
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|DelegationTokenSecretManager
name|sm
init|=
name|NameNodeAdapter
operator|.
name|getDtSecretManager
argument_list|(
name|nn
operator|.
name|getNamesystem
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Secret manager should not run in safe mode"
argument_list|,
name|sm
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|leaveSafeMode
argument_list|(
name|nn
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Secret manager should start when safe mode is exited"
argument_list|,
name|sm
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"========= entering safemode again"
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|nn
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Secret manager should stop again when safe mode "
operator|+
literal|"is manually entered"
argument_list|,
name|sm
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set the cluster to leave safemode quickly on its own.
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_EXTENSION_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setWaitSafeMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|nn
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
expr_stmt|;
name|sm
operator|=
name|NameNodeAdapter
operator|.
name|getDtSecretManager
argument_list|(
name|nn
operator|.
name|getNamesystem
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nn
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sm
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|checkTokenIdentifier (UserGroupInformation ugi, final Token<?> token)
specifier|private
name|void
name|checkTokenIdentifier
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
comment|// should be able to use token.decodeIdentifier() but webhdfs isn't
comment|// registered with the service loader for token decoding
name|DelegationTokenIdentifier
name|identifier
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenId
init|=
name|token
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|tokenId
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|identifier
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"A valid token should have non-null password, and should be renewed successfully"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|null
operator|!=
name|dtSecretManager
operator|.
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
argument_list|)
expr_stmt|;
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
operator|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
operator|)
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|token
operator|.
name|renew
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|token
operator|.
name|cancel
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

