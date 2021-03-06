begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline.recovery
package|package
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
name|timeline
operator|.
name|recovery
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
name|FileContext
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
name|conf
operator|.
name|YarnConfiguration
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
name|security
operator|.
name|client
operator|.
name|TimelineDelegationTokenIdentifier
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timeline
operator|.
name|recovery
operator|.
name|TimelineStateStore
operator|.
name|TimelineServiceState
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
DECL|class|TestLeveldbTimelineStateStore
specifier|public
class|class
name|TestLeveldbTimelineStateStore
block|{
DECL|field|fsContext
specifier|private
name|FileContext
name|fsContext
decl_stmt|;
DECL|field|fsPath
specifier|private
name|File
name|fsPath
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|store
specifier|private
name|TimelineStateStore
name|store
decl_stmt|;
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
name|fsPath
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-tmpDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|fsContext
operator|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
expr_stmt|;
name|fsContext
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|fsPath
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_RECOVERY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_STATE_STORE_CLASS
argument_list|,
name|LeveldbTimelineStateStore
operator|.
name|class
argument_list|,
name|TimelineStateStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_LEVELDB_STATE_STORE_PATH
argument_list|,
name|fsPath
operator|.
name|getAbsolutePath
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
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|fsContext
operator|!=
literal|null
condition|)
block|{
name|fsContext
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|fsPath
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initAndStartTimelineServiceStateStoreService ()
specifier|private
name|LeveldbTimelineStateStore
name|initAndStartTimelineServiceStateStoreService
parameter_list|()
block|{
name|store
operator|=
operator|new
name|LeveldbTimelineStateStore
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
return|return
operator|(
name|LeveldbTimelineStateStore
operator|)
name|store
return|;
block|}
annotation|@
name|Test
DECL|method|testTokenStore ()
specifier|public
name|void
name|testTokenStore
parameter_list|()
throws|throws
name|Exception
block|{
name|initAndStartTimelineServiceStateStoreService
argument_list|()
expr_stmt|;
name|TimelineServiceState
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
name|TimelineDelegationTokenIdentifier
name|token1
init|=
operator|new
name|TimelineDelegationTokenIdentifier
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
name|token1
operator|.
name|getBytes
argument_list|()
expr_stmt|;
specifier|final
name|Long
name|tokenDate1
init|=
literal|1L
decl_stmt|;
specifier|final
name|TimelineDelegationTokenIdentifier
name|token2
init|=
operator|new
name|TimelineDelegationTokenIdentifier
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
name|token2
operator|.
name|getBytes
argument_list|()
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
name|initAndStartTimelineServiceStateStoreService
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
name|assertEquals
argument_list|(
literal|"incorrect latest sequence number"
argument_list|,
literal|12345678
argument_list|,
name|state
operator|.
name|getLatestSequenceNumber
argument_list|()
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
name|TimelineDelegationTokenIdentifier
name|token3
init|=
operator|new
name|TimelineDelegationTokenIdentifier
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
name|token3
operator|.
name|getBytes
argument_list|()
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
name|initAndStartTimelineServiceStateStoreService
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
name|assertEquals
argument_list|(
literal|"incorrect latest sequence number"
argument_list|,
literal|12345679
argument_list|,
name|state
operator|.
name|getLatestSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|LeveldbTimelineStateStore
name|store
init|=
name|initAndStartTimelineServiceStateStoreService
argument_list|()
decl_stmt|;
comment|// default version
name|Version
name|defaultVersion
init|=
name|store
operator|.
name|getCurrentVersion
argument_list|()
decl_stmt|;
name|Assert
operator|.
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
name|storeVersion
argument_list|(
name|compatibleVersion
argument_list|)
expr_stmt|;
name|Assert
operator|.
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
name|stop
argument_list|()
expr_stmt|;
comment|// overwrite the compatible version
name|store
operator|=
name|initAndStartTimelineServiceStateStoreService
argument_list|()
expr_stmt|;
name|Assert
operator|.
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
name|storeVersion
argument_list|(
name|incompatibleVersion
argument_list|)
expr_stmt|;
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
try|try
block|{
name|initAndStartTimelineServiceStateStoreService
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Incompatible version, should expect fail here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceStateException
name|e
parameter_list|)
block|{
name|Assert
operator|.
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
literal|"Incompatible version for timeline state store"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

