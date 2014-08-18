begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
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
name|Mockito
operator|.
name|argThat
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
name|isA
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|MRDelegationTokenIdentifier
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|HistoryServerStateStoreService
operator|.
name|HistoryServerState
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
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|delegation
operator|.
name|DelegationKey
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatcher
import|;
end_import

begin_class
DECL|class|TestHistoryServerFileSystemStateStoreService
specifier|public
class|class
name|TestHistoryServerFileSystemStateStoreService
block|{
DECL|field|testDir
specifier|private
specifier|static
specifier|final
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
argument_list|,
literal|"TestHistoryServerFileSystemStateStoreService"
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|testDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_RECOVERY_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_STATE_STORE
argument_list|,
name|HistoryServerFileSystemStateStoreService
operator|.
name|class
argument_list|,
name|HistoryServerStateStoreService
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_FS_STATE_STORE_URI
argument_list|,
name|testDir
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
DECL|method|createAndStartStore ()
specifier|private
name|HistoryServerStateStoreService
name|createAndStartStore
parameter_list|()
throws|throws
name|IOException
block|{
name|HistoryServerStateStoreService
name|store
init|=
name|HistoryServerStateStoreServiceFactory
operator|.
name|getStore
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Factory did not create a filesystem store"
argument_list|,
name|store
operator|instanceof
name|HistoryServerFileSystemStateStoreService
argument_list|)
expr_stmt|;
name|store
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|store
return|;
block|}
DECL|method|testTokenStore (String stateStoreUri)
specifier|private
name|void
name|testTokenStore
parameter_list|(
name|String
name|stateStoreUri
parameter_list|)
throws|throws
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_FS_STATE_STORE_URI
argument_list|,
name|stateStoreUri
argument_list|)
expr_stmt|;
name|HistoryServerStateStoreService
name|store
init|=
name|createAndStartStore
argument_list|()
decl_stmt|;
name|HistoryServerState
name|state
init|=
name|store
operator|.
name|loadState
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"token state not empty"
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"key state not empty"
argument_list|,
name|state
operator|.
name|tokenMasterKeyState
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|DelegationKey
name|key1
init|=
operator|new
name|DelegationKey
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|"keyData1"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|MRDelegationTokenIdentifier
name|token1
init|=
operator|new
name|MRDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"tokenOwner1"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tokenRenewer1"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tokenUser1"
argument_list|)
argument_list|)
decl_stmt|;
name|token1
operator|.
name|setSequenceNumber
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|tokenDate1
init|=
literal|1L
decl_stmt|;
specifier|final
name|MRDelegationTokenIdentifier
name|token2
init|=
operator|new
name|MRDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"tokenOwner2"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tokenRenewer2"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tokenUser2"
argument_list|)
argument_list|)
decl_stmt|;
name|token2
operator|.
name|setSequenceNumber
argument_list|(
literal|12345678
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|tokenDate2
init|=
literal|87654321L
decl_stmt|;
name|store
operator|.
name|storeTokenMasterKey
argument_list|(
name|key1
argument_list|)
expr_stmt|;
try|try
block|{
name|store
operator|.
name|storeTokenMasterKey
argument_list|(
name|key1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"redundant store of key undetected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|store
operator|.
name|storeToken
argument_list|(
name|token1
argument_list|,
name|tokenDate1
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeToken
argument_list|(
name|token2
argument_list|,
name|tokenDate2
argument_list|)
expr_stmt|;
try|try
block|{
name|store
operator|.
name|storeToken
argument_list|(
name|token1
argument_list|,
name|tokenDate1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"redundant store of token undetected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|createAndStartStore
argument_list|()
expr_stmt|;
name|state
operator|=
name|store
operator|.
name|loadState
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect loaded token count"
argument_list|,
literal|2
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"missing token 1"
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|containsKey
argument_list|(
name|token1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect token 1 date"
argument_list|,
name|tokenDate1
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|get
argument_list|(
name|token1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"missing token 2"
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|containsKey
argument_list|(
name|token2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect token 2 date"
argument_list|,
name|tokenDate2
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|get
argument_list|(
name|token2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect master key count"
argument_list|,
literal|1
argument_list|,
name|state
operator|.
name|tokenMasterKeyState
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"missing master key 1"
argument_list|,
name|state
operator|.
name|tokenMasterKeyState
operator|.
name|contains
argument_list|(
name|key1
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DelegationKey
name|key2
init|=
operator|new
name|DelegationKey
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|"keyData2"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DelegationKey
name|key3
init|=
operator|new
name|DelegationKey
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|,
literal|"keyData3"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|MRDelegationTokenIdentifier
name|token3
init|=
operator|new
name|MRDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"tokenOwner3"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tokenRenewer3"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tokenUser3"
argument_list|)
argument_list|)
decl_stmt|;
name|token3
operator|.
name|setSequenceNumber
argument_list|(
literal|12345679
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|tokenDate3
init|=
literal|87654321L
decl_stmt|;
name|store
operator|.
name|removeToken
argument_list|(
name|token1
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeTokenMasterKey
argument_list|(
name|key2
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|newTokenDate2
init|=
literal|975318642L
decl_stmt|;
name|store
operator|.
name|updateToken
argument_list|(
name|token2
argument_list|,
name|newTokenDate2
argument_list|)
expr_stmt|;
name|store
operator|.
name|removeTokenMasterKey
argument_list|(
name|key1
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeTokenMasterKey
argument_list|(
name|key3
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeToken
argument_list|(
name|token3
argument_list|,
name|tokenDate3
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|createAndStartStore
argument_list|()
expr_stmt|;
name|state
operator|=
name|store
operator|.
name|loadState
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect loaded token count"
argument_list|,
literal|2
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"token 1 not removed"
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|containsKey
argument_list|(
name|token1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"missing token 2"
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|containsKey
argument_list|(
name|token2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect token 2 date"
argument_list|,
name|newTokenDate2
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|get
argument_list|(
name|token2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"missing token 3"
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|containsKey
argument_list|(
name|token3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect token 3 date"
argument_list|,
name|tokenDate3
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|get
argument_list|(
name|token3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect master key count"
argument_list|,
literal|2
argument_list|,
name|state
operator|.
name|tokenMasterKeyState
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"master key 1 not removed"
argument_list|,
name|state
operator|.
name|tokenMasterKeyState
operator|.
name|contains
argument_list|(
name|key1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"missing master key 2"
argument_list|,
name|state
operator|.
name|tokenMasterKeyState
operator|.
name|contains
argument_list|(
name|key2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"missing master key 3"
argument_list|,
name|state
operator|.
name|tokenMasterKeyState
operator|.
name|contains
argument_list|(
name|key3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTokenStore ()
specifier|public
name|void
name|testTokenStore
parameter_list|()
throws|throws
name|IOException
block|{
name|testTokenStore
argument_list|(
name|testDir
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTokenStoreHdfs ()
specifier|public
name|void
name|testTokenStoreHdfs
parameter_list|()
throws|throws
name|IOException
block|{
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
name|build
argument_list|()
decl_stmt|;
name|conf
operator|=
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|testTokenStore
argument_list|(
literal|"/tmp/historystore"
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
DECL|method|testUpdatedTokenRecovery ()
specifier|public
name|void
name|testUpdatedTokenRecovery
parameter_list|()
throws|throws
name|IOException
block|{
name|IOException
name|intentionalErr
init|=
operator|new
name|IOException
argument_list|(
literal|"intentional error"
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|FileSystem
name|spyfs
init|=
name|spy
argument_list|(
name|fs
argument_list|)
decl_stmt|;
comment|// make the update token process fail halfway through where we're left
comment|// with just the temporary update file and no token file
name|ArgumentMatcher
argument_list|<
name|Path
argument_list|>
name|updateTmpMatcher
init|=
operator|new
name|ArgumentMatcher
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|argument
parameter_list|)
block|{
if|if
condition|(
name|argument
operator|instanceof
name|Path
condition|)
block|{
return|return
operator|(
operator|(
name|Path
operator|)
name|argument
operator|)
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"update"
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
name|doThrow
argument_list|(
name|intentionalErr
argument_list|)
operator|.
name|when
argument_list|(
name|spyfs
argument_list|)
operator|.
name|rename
argument_list|(
name|argThat
argument_list|(
name|updateTmpMatcher
argument_list|)
argument_list|,
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_FS_STATE_STORE_URI
argument_list|,
name|testDir
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|HistoryServerStateStoreService
name|store
init|=
operator|new
name|HistoryServerFileSystemStateStoreService
argument_list|()
block|{
annotation|@
name|Override
name|FileSystem
name|createFileSystem
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|spyfs
return|;
block|}
block|}
decl_stmt|;
name|store
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MRDelegationTokenIdentifier
name|token1
init|=
operator|new
name|MRDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"tokenOwner1"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tokenRenewer1"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"tokenUser1"
argument_list|)
argument_list|)
decl_stmt|;
name|token1
operator|.
name|setSequenceNumber
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|tokenDate1
init|=
literal|1L
decl_stmt|;
name|store
operator|.
name|storeToken
argument_list|(
name|token1
argument_list|,
name|tokenDate1
argument_list|)
expr_stmt|;
specifier|final
name|Long
name|newTokenDate1
init|=
literal|975318642L
decl_stmt|;
try|try
block|{
name|store
operator|.
name|updateToken
argument_list|(
name|token1
argument_list|,
name|newTokenDate1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"intentional error not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|intentionalErr
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify the update file is seen and parsed upon recovery when
comment|// original token file is missing
name|store
operator|=
name|createAndStartStore
argument_list|()
expr_stmt|;
name|HistoryServerState
name|state
init|=
name|store
operator|.
name|loadState
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect loaded token count"
argument_list|,
literal|1
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"missing token 1"
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|containsKey
argument_list|(
name|token1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect token 1 date"
argument_list|,
name|newTokenDate1
argument_list|,
name|state
operator|.
name|tokenState
operator|.
name|get
argument_list|(
name|token1
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

