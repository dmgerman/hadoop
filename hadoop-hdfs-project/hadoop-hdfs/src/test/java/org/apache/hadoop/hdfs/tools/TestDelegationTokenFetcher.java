begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
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
name|DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY
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
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doThrow
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|Iterator
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
name|LocalFileSystem
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
name|tools
operator|.
name|FakeRenewer
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

begin_class
DECL|class|TestDelegationTokenFetcher
specifier|public
class|class
name|TestDelegationTokenFetcher
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
name|TestDelegationTokenFetcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
annotation|@
name|Rule
DECL|field|f
specifier|public
name|TemporaryFolder
name|f
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
DECL|field|tokenFile
specifier|private
specifier|static
specifier|final
name|String
name|tokenFile
init|=
literal|"token"
decl_stmt|;
comment|/**    * try to fetch token without http server with IOException    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testTokenFetchFail ()
specifier|public
name|void
name|testTokenFetchFail
parameter_list|()
throws|throws
name|Exception
block|{
name|WebHdfsFileSystem
name|fs
init|=
name|mock
argument_list|(
name|WebHdfsFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|tokenFile
argument_list|)
decl_stmt|;
name|DelegationTokenFetcher
operator|.
name|saveDelegationToken
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|,
literal|null
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
comment|/**    * Call fetch token using http server    */
annotation|@
name|Test
DECL|method|expectedTokenIsRetrievedFromHttp ()
specifier|public
name|void
name|expectedTokenIsRetrievedFromHttp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|testToken
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|(
literal|"id"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"pwd"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|FakeRenewer
operator|.
name|KIND
argument_list|,
operator|new
name|Text
argument_list|(
literal|"127.0.0.1:1234"
argument_list|)
argument_list|)
decl_stmt|;
name|WebHdfsFileSystem
name|fs
init|=
name|mock
argument_list|(
name|WebHdfsFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|testToken
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|tokenFile
argument_list|)
decl_stmt|;
name|DelegationTokenFetcher
operator|.
name|saveDelegationToken
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|,
literal|null
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|Credentials
name|creds
init|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|p
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|itr
init|=
name|creds
operator|.
name|getAllTokens
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"token not exist error"
argument_list|,
name|itr
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|fetchedToken
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
literal|"token wrong identifier error"
argument_list|,
name|testToken
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|fetchedToken
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
literal|"token wrong password error"
argument_list|,
name|testToken
operator|.
name|getPassword
argument_list|()
argument_list|,
name|fetchedToken
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|DelegationTokenFetcher
operator|.
name|renewTokens
argument_list|(
name|conf
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|testToken
argument_list|,
name|FakeRenewer
operator|.
name|getLastRenewed
argument_list|()
argument_list|)
expr_stmt|;
name|DelegationTokenFetcher
operator|.
name|cancelTokens
argument_list|(
name|conf
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|testToken
argument_list|,
name|FakeRenewer
operator|.
name|getLastCanceled
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * If token returned is null, saveDelegationToken should not    * throw nullPointerException    */
annotation|@
name|Test
DECL|method|testReturnedTokenIsNull ()
specifier|public
name|void
name|testReturnedTokenIsNull
parameter_list|()
throws|throws
name|Exception
block|{
name|WebHdfsFileSystem
name|fs
init|=
name|mock
argument_list|(
name|WebHdfsFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|tokenFile
argument_list|)
decl_stmt|;
name|DelegationTokenFetcher
operator|.
name|saveDelegationToken
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|,
literal|null
argument_list|,
name|p
argument_list|)
expr_stmt|;
comment|// When Token returned is null, TokenFile should not exist
name|Assert
operator|.
name|assertFalse
argument_list|(
name|p
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
operator|.
name|exists
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenWithoutRenewerViaRPC ()
specifier|public
name|void
name|testDelegationTokenWithoutRenewerViaRPC
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Should be able to fetch token without renewer.
name|LocalFileSystem
name|localFileSystem
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|tokenFile
argument_list|)
decl_stmt|;
name|p
operator|=
name|localFileSystem
operator|.
name|makeQualified
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|DelegationTokenFetcher
operator|.
name|saveDelegationToken
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|,
literal|null
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|Credentials
name|creds
init|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|p
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|itr
init|=
name|creds
operator|.
name|getAllTokens
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"token not exist error"
argument_list|,
name|itr
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Token
name|token
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Token should be there without renewer"
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// Test compatibility of DelegationTokenFetcher.printTokensToString
name|String
name|expectedNonVerbose
init|=
literal|"Token (HDFS_DELEGATION_TOKEN token 1 for "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
operator|+
literal|" with renewer ) for"
decl_stmt|;
name|String
name|resNonVerbose
init|=
name|DelegationTokenFetcher
operator|.
name|printTokensToString
argument_list|(
name|conf
argument_list|,
name|p
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The non verbose output is expected to start with \""
operator|+
name|expectedNonVerbose
operator|+
literal|"\""
argument_list|,
name|resNonVerbose
operator|.
name|startsWith
argument_list|(
name|expectedNonVerbose
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|resNonVerbose
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|DelegationTokenFetcher
operator|.
name|printTokensToString
argument_list|(
name|conf
argument_list|,
name|p
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Without renewer renewal of token should fail.
name|DelegationTokenFetcher
operator|.
name|renewTokens
argument_list|(
name|conf
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed to renew"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"tried to renew a token ("
operator|+
name|token
operator|.
name|decodeIdentifier
argument_list|()
operator|+
literal|") without a renewer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

