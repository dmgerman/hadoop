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
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|ServiceStateException
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
name|server
operator|.
name|records
operator|.
name|Version
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

begin_class
DECL|class|TestHistoryServerLeveldbStateStoreService
specifier|public
class|class
name|TestHistoryServerLeveldbStateStoreService
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
literal|"TestHistoryServerLeveldbSystemStateStoreService"
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
name|HistoryServerLeveldbStateStoreService
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
name|MR_HS_LEVELDB_STATE_STORE_PATH
argument_list|,
name|testDir
operator|.
name|getAbsoluteFile
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
literal|"Factory did not create a leveldb store"
argument_list|,
name|store
operator|instanceof
name|HistoryServerLeveldbStateStoreService
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
annotation|@
name|Test
DECL|method|testCheckVersion ()
specifier|public
name|void
name|testCheckVersion
parameter_list|()
throws|throws
name|IOException
block|{
name|HistoryServerLeveldbStateStoreService
name|store
init|=
operator|new
name|HistoryServerLeveldbStateStoreService
argument_list|()
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
comment|// default version
name|Version
name|defaultVersion
init|=
name|store
operator|.
name|getCurrentVersion
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|defaultVersion
argument_list|,
name|store
operator|.
name|loadVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// compatible version
name|Version
name|compatibleVersion
init|=
name|Version
operator|.
name|newInstance
argument_list|(
name|defaultVersion
operator|.
name|getMajorVersion
argument_list|()
argument_list|,
name|defaultVersion
operator|.
name|getMinorVersion
argument_list|()
operator|+
literal|2
argument_list|)
decl_stmt|;
name|store
operator|.
name|dbStoreVersion
argument_list|(
name|compatibleVersion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|compatibleVersion
argument_list|,
name|store
operator|.
name|loadVersion
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
operator|new
name|HistoryServerLeveldbStateStoreService
argument_list|()
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
comment|// overwrite the compatible version
name|assertEquals
argument_list|(
name|defaultVersion
argument_list|,
name|store
operator|.
name|loadVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// incompatible version
name|Version
name|incompatibleVersion
init|=
name|Version
operator|.
name|newInstance
argument_list|(
name|defaultVersion
operator|.
name|getMajorVersion
argument_list|()
operator|+
literal|1
argument_list|,
name|defaultVersion
operator|.
name|getMinorVersion
argument_list|()
argument_list|)
decl_stmt|;
name|store
operator|.
name|dbStoreVersion
argument_list|(
name|incompatibleVersion
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
operator|new
name|HistoryServerLeveldbStateStoreService
argument_list|()
expr_stmt|;
try|try
block|{
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
name|fail
argument_list|(
literal|"Incompatible version, should have thrown before here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceStateException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Exception message mismatch"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Incompatible version for state:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|close
argument_list|()
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
name|HistoryServerStateStoreService
name|store
init|=
name|createAndStartStore
argument_list|()
decl_stmt|;
comment|// verify initially the store is empty
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
comment|// store a key and some tokens
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
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify the key and tokens can be recovered
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
comment|// store some more keys and tokens, remove the previous key and one
comment|// of the tokens, and renew a previous token
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
comment|// verify the new keys and tokens are recovered, the removed key and
comment|// token are no longer present, and the renewed token has the updated
comment|// expiration date
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
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

