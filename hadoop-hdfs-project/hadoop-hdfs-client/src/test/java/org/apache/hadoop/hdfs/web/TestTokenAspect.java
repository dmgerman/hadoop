begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|assertEquals
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
name|assertNotSame
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
name|assertNull
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
name|assertSame
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
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|never
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
name|spy
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
name|times
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
name|verify
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
name|DelegationTokenRenewer
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
name|DelegationTokenRenewer
operator|.
name|RenewAction
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|FileStatus
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|Whitebox
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
name|Progressable
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestTokenAspect
specifier|public
class|class
name|TestTokenAspect
block|{
DECL|class|DummyFs
specifier|private
specifier|static
class|class
name|DummyFs
extends|extends
name|FileSystem
implements|implements
name|DelegationTokenRenewer
operator|.
name|Renewable
implements|,
name|TokenAspect
operator|.
name|TokenManagementDelegator
block|{
DECL|field|TOKEN_KIND
specifier|private
specifier|static
specifier|final
name|Text
name|TOKEN_KIND
init|=
operator|new
name|Text
argument_list|(
literal|"DummyFS Token"
argument_list|)
decl_stmt|;
DECL|field|emulateSecurityEnabled
specifier|private
name|boolean
name|emulateSecurityEnabled
decl_stmt|;
DECL|field|tokenAspect
specifier|private
name|TokenAspect
argument_list|<
name|DummyFs
argument_list|>
name|tokenAspect
decl_stmt|;
DECL|field|ugi
specifier|private
specifier|final
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"bar"
block|}
argument_list|)
decl_stmt|;
DECL|field|uri
specifier|private
name|URI
name|uri
decl_stmt|;
annotation|@
name|Override
DECL|method|append (Path f, int bufferSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
name|Path
name|f
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|cancelDelegationToken (Token<?> token)
specifier|public
name|void
name|cancelDelegationToken
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|create (Path f, FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|boolean
name|overwrite
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|delete (Path f, boolean recursive)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|f
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getCanonicalUri ()
specifier|public
name|URI
name|getCanonicalUri
parameter_list|()
block|{
return|return
name|super
operator|.
name|getCanonicalUri
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStatus (Path f)
specifier|public
name|FileStatus
name|getFileStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getRenewToken ()
specifier|public
name|Token
argument_list|<
name|?
argument_list|>
name|getRenewToken
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
annotation|@
name|Override
DECL|method|getWorkingDirectory ()
specifier|public
name|Path
name|getWorkingDirectory
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|initialize (URI name, Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|URI
name|name
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|initialize
argument_list|(
name|name
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|URI
operator|.
name|create
argument_list|(
name|name
operator|.
name|getScheme
argument_list|()
operator|+
literal|"://"
operator|+
name|name
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
name|tokenAspect
operator|=
operator|new
name|TokenAspect
argument_list|<
name|DummyFs
argument_list|>
argument_list|(
name|this
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|uri
argument_list|)
argument_list|,
name|TOKEN_KIND
argument_list|)
expr_stmt|;
if|if
condition|(
name|emulateSecurityEnabled
operator|||
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|tokenAspect
operator|.
name|initDelegationToken
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|listStatus (Path f)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileStatus
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|mkdirs (Path f, FsPermission permission)
specifier|public
name|boolean
name|mkdirs
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|open (Path f, int bufferSize)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|Path
name|f
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|rename (Path src, Path dst)
specifier|public
name|boolean
name|rename
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|renewDelegationToken (Token<?> token)
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|setDelegationToken (Token<T> token)
specifier|public
parameter_list|<
name|T
extends|extends
name|TokenIdentifier
parameter_list|>
name|void
name|setDelegationToken
parameter_list|(
name|Token
argument_list|<
name|T
argument_list|>
name|token
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|setWorkingDirectory (Path new_dir)
specifier|public
name|void
name|setWorkingDirectory
parameter_list|(
name|Path
name|new_dir
parameter_list|)
block|{     }
block|}
DECL|method|getActionFromTokenAspect ( TokenAspect<DummyFs> tokenAspect)
specifier|private
specifier|static
name|RenewAction
argument_list|<
name|?
argument_list|>
name|getActionFromTokenAspect
parameter_list|(
name|TokenAspect
argument_list|<
name|DummyFs
argument_list|>
name|tokenAspect
parameter_list|)
block|{
return|return
operator|(
name|RenewAction
argument_list|<
name|?
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|tokenAspect
argument_list|,
literal|"action"
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testCachedInitialization ()
specifier|public
name|void
name|testCachedInitialization
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|DummyFs
name|fs
init|=
name|spy
argument_list|(
operator|new
name|DummyFs
argument_list|()
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|DummyFs
operator|.
name|TOKEN_KIND
argument_list|,
operator|new
name|Text
argument_list|(
literal|"127.0.0.1:1234"
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|token
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|token
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
name|fs
operator|.
name|emulateSecurityEnabled
operator|=
literal|true
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
operator|new
name|URI
argument_list|(
literal|"dummyfs://127.0.0.1:1234"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|fs
operator|.
name|tokenAspect
operator|.
name|ensureTokenInitialized
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|setDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
comment|// For the second iteration, the token should be cached.
name|fs
operator|.
name|tokenAspect
operator|.
name|ensureTokenInitialized
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|setDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetRemoteToken ()
specifier|public
name|void
name|testGetRemoteToken
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|DummyFs
name|fs
init|=
name|spy
argument_list|(
operator|new
name|DummyFs
argument_list|()
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|DummyFs
operator|.
name|TOKEN_KIND
argument_list|,
operator|new
name|Text
argument_list|(
literal|"127.0.0.1:1234"
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|token
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|token
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
operator|new
name|URI
argument_list|(
literal|"dummyfs://127.0.0.1:1234"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|fs
operator|.
name|tokenAspect
operator|.
name|ensureTokenInitialized
argument_list|()
expr_stmt|;
comment|// Select a token, store and renew it
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|setDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|fs
operator|.
name|tokenAspect
argument_list|,
literal|"dtRenewer"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|fs
operator|.
name|tokenAspect
argument_list|,
literal|"action"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetRemoteTokenFailure ()
specifier|public
name|void
name|testGetRemoteTokenFailure
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|DummyFs
name|fs
init|=
name|spy
argument_list|(
operator|new
name|DummyFs
argument_list|()
argument_list|)
decl_stmt|;
name|IOException
name|e
init|=
operator|new
name|IOException
argument_list|()
decl_stmt|;
name|doThrow
argument_list|(
name|e
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
name|fs
operator|.
name|emulateSecurityEnabled
operator|=
literal|true
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
operator|new
name|URI
argument_list|(
literal|"dummyfs://127.0.0.1:1234"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|tokenAspect
operator|.
name|ensureTokenInitialized
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|exc
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
argument_list|,
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInitWithNoTokens ()
specifier|public
name|void
name|testInitWithNoTokens
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|DummyFs
name|fs
init|=
name|spy
argument_list|(
operator|new
name|DummyFs
argument_list|()
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
name|fs
operator|.
name|initialize
argument_list|(
operator|new
name|URI
argument_list|(
literal|"dummyfs://127.0.0.1:1234"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|fs
operator|.
name|tokenAspect
operator|.
name|ensureTokenInitialized
argument_list|()
expr_stmt|;
comment|// No token will be selected.
name|verify
argument_list|(
name|fs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|setDelegationToken
argument_list|(
name|Mockito
operator|.
expr|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitWithUGIToken ()
specifier|public
name|void
name|testInitWithUGIToken
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|DummyFs
name|fs
init|=
name|spy
argument_list|(
operator|new
name|DummyFs
argument_list|()
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
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|DummyFs
operator|.
name|TOKEN_KIND
argument_list|,
operator|new
name|Text
argument_list|(
literal|"127.0.0.1:1234"
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|ugi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|fs
operator|.
name|ugi
operator|.
name|addToken
argument_list|(
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
operator|new
name|Text
argument_list|(
literal|"Other token"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"127.0.0.1:8021"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong tokens in user"
argument_list|,
literal|2
argument_list|,
name|fs
operator|.
name|ugi
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|emulateSecurityEnabled
operator|=
literal|true
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
operator|new
name|URI
argument_list|(
literal|"dummyfs://127.0.0.1:1234"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|fs
operator|.
name|tokenAspect
operator|.
name|ensureTokenInitialized
argument_list|()
expr_stmt|;
comment|// Select a token from ugi (not from the remote host), store it but don't
comment|// renew it
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|setDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|fs
operator|.
name|tokenAspect
argument_list|,
literal|"dtRenewer"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|fs
operator|.
name|tokenAspect
argument_list|,
literal|"action"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenewal ()
specifier|public
name|void
name|testRenewal
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token1
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token2
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|long
name|renewCycle
init|=
literal|100
decl_stmt|;
name|DelegationTokenRenewer
operator|.
name|renewCycle
operator|=
name|renewCycle
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"bar"
block|}
argument_list|)
decl_stmt|;
name|DummyFs
name|fs
init|=
name|spy
argument_list|(
operator|new
name|DummyFs
argument_list|()
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|token1
argument_list|)
operator|.
name|doReturn
argument_list|(
name|token2
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|token1
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
comment|// cause token renewer to abandon the token
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"renew failed"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|token1
argument_list|)
operator|.
name|renew
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"get failed"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|addDelegationTokens
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"dummyfs://127.0.0.1:1234"
argument_list|)
decl_stmt|;
name|TokenAspect
argument_list|<
name|DummyFs
argument_list|>
name|tokenAspect
init|=
operator|new
name|TokenAspect
argument_list|<
name|DummyFs
argument_list|>
argument_list|(
name|fs
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|uri
argument_list|)
argument_list|,
name|DummyFs
operator|.
name|TOKEN_KIND
argument_list|)
decl_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|tokenAspect
operator|.
name|initDelegationToken
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
comment|// trigger token acquisition
name|tokenAspect
operator|.
name|ensureTokenInitialized
argument_list|()
expr_stmt|;
name|DelegationTokenRenewer
operator|.
name|RenewAction
argument_list|<
name|?
argument_list|>
name|action
init|=
name|getActionFromTokenAspect
argument_list|(
name|tokenAspect
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|setDelegationToken
argument_list|(
name|token1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|action
operator|.
name|isValid
argument_list|()
argument_list|)
expr_stmt|;
comment|// upon renewal, token will go bad based on above stubbing
name|Thread
operator|.
name|sleep
argument_list|(
name|renewCycle
operator|*
literal|2
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|action
argument_list|,
name|getActionFromTokenAspect
argument_list|(
name|tokenAspect
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|action
operator|.
name|isValid
argument_list|()
argument_list|)
expr_stmt|;
comment|// now that token is invalid, should get a new one
name|tokenAspect
operator|.
name|ensureTokenInitialized
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|setDelegationToken
argument_list|(
name|token2
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|action
argument_list|,
name|getActionFromTokenAspect
argument_list|(
name|tokenAspect
argument_list|)
argument_list|)
expr_stmt|;
name|action
operator|=
name|getActionFromTokenAspect
argument_list|(
name|tokenAspect
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|action
operator|.
name|isValid
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

