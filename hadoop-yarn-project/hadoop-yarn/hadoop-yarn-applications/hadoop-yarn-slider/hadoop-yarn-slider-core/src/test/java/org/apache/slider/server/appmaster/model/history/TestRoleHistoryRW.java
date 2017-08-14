begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.model.history
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|history
package|;
end_package

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
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ResourceKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|PlacementPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|ProviderRole
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
operator|.
name|BaseMockAppStateTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
operator|.
name|MockFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
operator|.
name|MockRoleHistory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|NodeEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|NodeInstance
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|RoleHistory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|RoleStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|avro
operator|.
name|LoadedRoleHistory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|avro
operator|.
name|RoleHistoryWriter
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Test fole history reading and writing.  */
end_comment

begin_class
DECL|class|TestRoleHistoryRW
specifier|public
class|class
name|TestRoleHistoryRW
extends|extends
name|BaseMockAppStateTest
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
name|TestRoleHistoryRW
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|time
specifier|private
specifier|static
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|HISTORY_V1_6_ROLE
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_V1_6_ROLE
init|=
literal|"org/apache/slider/server/avro/history-v01-6-role.json"
decl_stmt|;
DECL|field|HISTORY_V1_3_ROLE
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_V1_3_ROLE
init|=
literal|"org/apache/slider/server/avro/history-v01-3-role.json"
decl_stmt|;
DECL|field|HISTORY_V1B_1_ROLE
specifier|public
specifier|static
specifier|final
name|String
name|HISTORY_V1B_1_ROLE
init|=
literal|"org/apache/slider/server/avro/history_v01b_1_role.json"
decl_stmt|;
DECL|field|role0Status
specifier|private
name|RoleStatus
name|role0Status
decl_stmt|;
DECL|field|role1Status
specifier|private
name|RoleStatus
name|role1Status
decl_stmt|;
DECL|field|PROVIDER_ROLE3
specifier|static
specifier|final
name|ProviderRole
name|PROVIDER_ROLE3
init|=
operator|new
name|ProviderRole
argument_list|(
literal|"role3"
argument_list|,
literal|3
argument_list|,
name|PlacementPolicy
operator|.
name|STRICT
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
name|ResourceKeys
operator|.
name|DEF_YARN_LABEL_EXPRESSION
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getTestName ()
specifier|public
name|String
name|getTestName
parameter_list|()
block|{
return|return
literal|"TestHistoryRW"
return|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|role0Status
operator|=
name|getRole0Status
argument_list|()
expr_stmt|;
name|role1Status
operator|=
name|getRole1Status
argument_list|()
expr_stmt|;
block|}
comment|//@Test
DECL|method|testWriteReadEmpty ()
specifier|public
name|void
name|testWriteReadEmpty
parameter_list|()
throws|throws
name|Throwable
block|{
name|RoleHistory
name|roleHistory
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|roleHistory
operator|.
name|onStart
argument_list|(
name|fs
argument_list|,
name|historyPath
argument_list|)
expr_stmt|;
name|Path
name|history
init|=
name|roleHistory
operator|.
name|saveHistory
argument_list|(
name|time
operator|++
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|history
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|RoleHistoryWriter
name|historyWriter
init|=
operator|new
name|RoleHistoryWriter
argument_list|()
decl_stmt|;
name|historyWriter
operator|.
name|read
argument_list|(
name|fs
argument_list|,
name|history
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testWriteReadData ()
specifier|public
name|void
name|testWriteReadData
parameter_list|()
throws|throws
name|Throwable
block|{
name|RoleHistory
name|roleHistory
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|roleHistory
operator|.
name|onStart
argument_list|(
name|fs
argument_list|,
name|historyPath
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|addr
init|=
literal|"localhost"
decl_stmt|;
name|NodeInstance
name|instance
init|=
name|roleHistory
operator|.
name|getOrCreateNodeInstance
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|NodeEntry
name|ne1
init|=
name|instance
operator|.
name|getOrCreate
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ne1
operator|.
name|setLastUsed
argument_list|(
literal|0xf00d
argument_list|)
expr_stmt|;
name|Path
name|history
init|=
name|roleHistory
operator|.
name|saveHistory
argument_list|(
name|time
operator|++
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|history
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|RoleHistoryWriter
name|historyWriter
init|=
operator|new
name|RoleHistoryWriter
argument_list|()
decl_stmt|;
name|RoleHistory
name|rh2
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|LoadedRoleHistory
name|loadedRoleHistory
init|=
name|historyWriter
operator|.
name|read
argument_list|(
name|fs
argument_list|,
name|history
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|loadedRoleHistory
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rh2
operator|.
name|rebuild
argument_list|(
name|loadedRoleHistory
argument_list|)
expr_stmt|;
name|NodeInstance
name|ni2
init|=
name|rh2
operator|.
name|getExistingNodeInstance
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ni2
argument_list|)
expr_stmt|;
name|NodeEntry
name|ne2
init|=
name|ni2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ne2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ne2
operator|.
name|getLastUsed
argument_list|()
argument_list|,
name|ne1
operator|.
name|getLastUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testWriteReadActiveData ()
specifier|public
name|void
name|testWriteReadActiveData
parameter_list|()
throws|throws
name|Throwable
block|{
name|RoleHistory
name|roleHistory
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|roleHistory
operator|.
name|onStart
argument_list|(
name|fs
argument_list|,
name|historyPath
argument_list|)
expr_stmt|;
name|String
name|addr
init|=
literal|"localhost"
decl_stmt|;
name|String
name|addr2
init|=
literal|"rack1server5"
decl_stmt|;
name|NodeInstance
name|localhost
init|=
name|roleHistory
operator|.
name|getOrCreateNodeInstance
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|NodeEntry
name|orig1
init|=
name|localhost
operator|.
name|getOrCreate
argument_list|(
name|role0Status
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|orig1
operator|.
name|setLastUsed
argument_list|(
literal|0x10
argument_list|)
expr_stmt|;
name|NodeInstance
name|rack1server5
init|=
name|roleHistory
operator|.
name|getOrCreateNodeInstance
argument_list|(
name|addr2
argument_list|)
decl_stmt|;
name|NodeEntry
name|orig2
init|=
name|rack1server5
operator|.
name|getOrCreate
argument_list|(
name|role1Status
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|orig2
operator|.
name|setLive
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|orig2
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|NodeEntry
name|orig3
init|=
name|localhost
operator|.
name|getOrCreate
argument_list|(
name|role1Status
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|orig3
operator|.
name|setLastUsed
argument_list|(
literal|0x20
argument_list|)
expr_stmt|;
name|orig3
operator|.
name|setLive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|orig3
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|orig3
operator|.
name|release
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|orig3
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|roleHistory
operator|.
name|dump
argument_list|()
expr_stmt|;
name|long
name|savetime
init|=
literal|0x0001000
decl_stmt|;
name|Path
name|history
init|=
name|roleHistory
operator|.
name|saveHistory
argument_list|(
name|savetime
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|history
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|describe
argument_list|(
literal|"Loaded"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"testWriteReadActiveData in {}"
argument_list|,
name|history
argument_list|)
expr_stmt|;
name|RoleHistoryWriter
name|historyWriter
init|=
operator|new
name|RoleHistoryWriter
argument_list|()
decl_stmt|;
name|RoleHistory
name|rh2
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|LoadedRoleHistory
name|loadedRoleHistory
init|=
name|historyWriter
operator|.
name|read
argument_list|(
name|fs
argument_list|,
name|history
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|loadedRoleHistory
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rh2
operator|.
name|rebuild
argument_list|(
name|loadedRoleHistory
argument_list|)
expr_stmt|;
name|rh2
operator|.
name|dump
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rh2
operator|.
name|getClusterSize
argument_list|()
argument_list|)
expr_stmt|;
name|NodeInstance
name|ni2
init|=
name|rh2
operator|.
name|getExistingNodeInstance
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ni2
argument_list|)
expr_stmt|;
name|NodeEntry
name|loadedNE
init|=
name|ni2
operator|.
name|get
argument_list|(
name|role0Status
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|loadedNE
operator|.
name|getLastUsed
argument_list|()
argument_list|,
name|orig1
operator|.
name|getLastUsed
argument_list|()
argument_list|)
expr_stmt|;
name|NodeInstance
name|ni2b
init|=
name|rh2
operator|.
name|getExistingNodeInstance
argument_list|(
name|addr2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ni2b
argument_list|)
expr_stmt|;
name|NodeEntry
name|loadedNE2
init|=
name|ni2b
operator|.
name|get
argument_list|(
name|role1Status
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|loadedNE2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|loadedNE2
operator|.
name|getLastUsed
argument_list|()
argument_list|,
name|savetime
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rh2
operator|.
name|getThawedDataTime
argument_list|()
argument_list|,
name|savetime
argument_list|)
expr_stmt|;
comment|// now start it
name|rh2
operator|.
name|buildRecentNodeLists
argument_list|()
expr_stmt|;
name|describe
argument_list|(
literal|"starting"
argument_list|)
expr_stmt|;
name|rh2
operator|.
name|dump
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|NodeInstance
argument_list|>
name|available0
init|=
name|rh2
operator|.
name|cloneRecentNodeList
argument_list|(
name|role0Status
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|available0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|NodeInstance
name|entry
init|=
name|available0
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|entry
operator|.
name|hostname
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entry
argument_list|,
name|localhost
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NodeInstance
argument_list|>
name|available1
init|=
name|rh2
operator|.
name|cloneRecentNodeList
argument_list|(
name|role1Status
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|available1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//and verify that even if last used was set, the save time is picked up
name|assertEquals
argument_list|(
name|entry
operator|.
name|get
argument_list|(
name|role1Status
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getLastUsed
argument_list|()
argument_list|,
name|roleHistory
operator|.
name|getSaveTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testWriteThaw ()
specifier|public
name|void
name|testWriteThaw
parameter_list|()
throws|throws
name|Throwable
block|{
name|RoleHistory
name|roleHistory
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|roleHistory
operator|.
name|onStart
argument_list|(
name|fs
argument_list|,
name|historyPath
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|addr
init|=
literal|"localhost"
decl_stmt|;
name|NodeInstance
name|instance
init|=
name|roleHistory
operator|.
name|getOrCreateNodeInstance
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|NodeEntry
name|ne1
init|=
name|instance
operator|.
name|getOrCreate
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ne1
operator|.
name|setLastUsed
argument_list|(
literal|0xf00d
argument_list|)
expr_stmt|;
name|Path
name|history
init|=
name|roleHistory
operator|.
name|saveHistory
argument_list|(
name|time
operator|++
argument_list|)
decl_stmt|;
name|long
name|savetime
init|=
name|roleHistory
operator|.
name|getSaveTime
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|history
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|RoleHistory
name|rh2
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rh2
operator|.
name|onStart
argument_list|(
name|fs
argument_list|,
name|historyPath
argument_list|)
argument_list|)
expr_stmt|;
name|NodeInstance
name|ni2
init|=
name|rh2
operator|.
name|getExistingNodeInstance
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ni2
argument_list|)
expr_stmt|;
name|NodeEntry
name|ne2
init|=
name|ni2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ne2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ne2
operator|.
name|getLastUsed
argument_list|()
argument_list|,
name|ne1
operator|.
name|getLastUsed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rh2
operator|.
name|getThawedDataTime
argument_list|()
argument_list|,
name|savetime
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testPurgeOlderEntries ()
specifier|public
name|void
name|testPurgeOlderEntries
parameter_list|()
throws|throws
name|Throwable
block|{
name|RoleHistoryWriter
name|historyWriter
init|=
operator|new
name|RoleHistoryWriter
argument_list|()
decl_stmt|;
name|time
operator|=
literal|1
expr_stmt|;
name|Path
name|file1
init|=
name|touch
argument_list|(
name|historyWriter
argument_list|,
name|time
operator|++
argument_list|)
decl_stmt|;
name|Path
name|file2
init|=
name|touch
argument_list|(
name|historyWriter
argument_list|,
name|time
operator|++
argument_list|)
decl_stmt|;
name|Path
name|file3
init|=
name|touch
argument_list|(
name|historyWriter
argument_list|,
name|time
operator|++
argument_list|)
decl_stmt|;
name|Path
name|file4
init|=
name|touch
argument_list|(
name|historyWriter
argument_list|,
name|time
operator|++
argument_list|)
decl_stmt|;
name|Path
name|file5
init|=
name|touch
argument_list|(
name|historyWriter
argument_list|,
name|time
operator|++
argument_list|)
decl_stmt|;
name|Path
name|file6
init|=
name|touch
argument_list|(
name|historyWriter
argument_list|,
name|time
operator|++
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|historyWriter
operator|.
name|purgeOlderHistoryEntries
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|historyWriter
operator|.
name|purgeOlderHistoryEntries
argument_list|(
name|fs
argument_list|,
name|file2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|historyWriter
operator|.
name|purgeOlderHistoryEntries
argument_list|(
name|fs
argument_list|,
name|file2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|historyWriter
operator|.
name|purgeOlderHistoryEntries
argument_list|(
name|fs
argument_list|,
name|file5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|historyWriter
operator|.
name|purgeOlderHistoryEntries
argument_list|(
name|fs
argument_list|,
name|file6
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
comment|// make an impossible assertion that will fail if the method
comment|// actually completes
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|historyWriter
operator|.
name|purgeOlderHistoryEntries
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ignored
parameter_list|)
block|{
comment|//  expected
block|}
block|}
DECL|method|touch (RoleHistoryWriter historyWriter, long timeMs)
specifier|public
name|Path
name|touch
parameter_list|(
name|RoleHistoryWriter
name|historyWriter
parameter_list|,
name|long
name|timeMs
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|historyWriter
operator|.
name|createHistoryFilename
argument_list|(
name|historyPath
argument_list|,
name|timeMs
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|path
return|;
block|}
comment|//@Test
DECL|method|testSkipEmptyFileOnRead ()
specifier|public
name|void
name|testSkipEmptyFileOnRead
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"verify that empty histories are skipped on read; old histories "
operator|+
literal|"purged"
argument_list|)
expr_stmt|;
name|RoleHistory
name|roleHistory
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|roleHistory
operator|.
name|onStart
argument_list|(
name|fs
argument_list|,
name|historyPath
argument_list|)
expr_stmt|;
name|time
operator|=
literal|0
expr_stmt|;
name|Path
name|oldhistory
init|=
name|roleHistory
operator|.
name|saveHistory
argument_list|(
name|time
operator|++
argument_list|)
decl_stmt|;
name|String
name|addr
init|=
literal|"localhost"
decl_stmt|;
name|NodeInstance
name|instance
init|=
name|roleHistory
operator|.
name|getOrCreateNodeInstance
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|NodeEntry
name|ne1
init|=
name|instance
operator|.
name|getOrCreate
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ne1
operator|.
name|setLastUsed
argument_list|(
literal|0xf00d
argument_list|)
expr_stmt|;
name|Path
name|goodhistory
init|=
name|roleHistory
operator|.
name|saveHistory
argument_list|(
name|time
operator|++
argument_list|)
decl_stmt|;
name|RoleHistoryWriter
name|historyWriter
init|=
operator|new
name|RoleHistoryWriter
argument_list|()
decl_stmt|;
name|Path
name|touched
init|=
name|touch
argument_list|(
name|historyWriter
argument_list|,
name|time
operator|++
argument_list|)
decl_stmt|;
name|RoleHistory
name|rh2
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rh2
operator|.
name|onStart
argument_list|(
name|fs
argument_list|,
name|historyPath
argument_list|)
argument_list|)
expr_stmt|;
name|NodeInstance
name|ni2
init|=
name|rh2
operator|.
name|getExistingNodeInstance
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ni2
argument_list|)
expr_stmt|;
comment|//and assert the older file got purged
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|oldhistory
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|goodhistory
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|touched
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testSkipBrokenFileOnRead ()
specifier|public
name|void
name|testSkipBrokenFileOnRead
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"verify that empty histories are skipped on read; old histories "
operator|+
literal|"purged"
argument_list|)
expr_stmt|;
name|RoleHistory
name|roleHistory
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|roleHistory
operator|.
name|onStart
argument_list|(
name|fs
argument_list|,
name|historyPath
argument_list|)
expr_stmt|;
name|time
operator|=
literal|0
expr_stmt|;
name|Path
name|oldhistory
init|=
name|roleHistory
operator|.
name|saveHistory
argument_list|(
name|time
operator|++
argument_list|)
decl_stmt|;
name|String
name|addr
init|=
literal|"localhost"
decl_stmt|;
name|NodeInstance
name|instance
init|=
name|roleHistory
operator|.
name|getOrCreateNodeInstance
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|NodeEntry
name|ne1
init|=
name|instance
operator|.
name|getOrCreate
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ne1
operator|.
name|setLastUsed
argument_list|(
literal|0xf00d
argument_list|)
expr_stmt|;
name|Path
name|goodhistory
init|=
name|roleHistory
operator|.
name|saveHistory
argument_list|(
name|time
operator|++
argument_list|)
decl_stmt|;
name|RoleHistoryWriter
name|historyWriter
init|=
operator|new
name|RoleHistoryWriter
argument_list|()
decl_stmt|;
name|Path
name|badfile
init|=
name|historyWriter
operator|.
name|createHistoryFilename
argument_list|(
name|historyPath
argument_list|,
name|time
operator|++
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|badfile
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"{broken:true}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|RoleHistory
name|rh2
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|describe
argument_list|(
literal|"IGNORE STACK TRACE BELOW"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rh2
operator|.
name|onStart
argument_list|(
name|fs
argument_list|,
name|historyPath
argument_list|)
argument_list|)
expr_stmt|;
name|describe
argument_list|(
literal|"IGNORE STACK TRACE ABOVE"
argument_list|)
expr_stmt|;
name|NodeInstance
name|ni2
init|=
name|rh2
operator|.
name|getExistingNodeInstance
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ni2
argument_list|)
expr_stmt|;
comment|//and assert the older file got purged
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|oldhistory
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|goodhistory
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|badfile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that a v1 JSON file can be read. Here the number of roles    * matches the current state.    * @throws Throwable    */
comment|//@Test
DECL|method|testReloadDataV13Role ()
specifier|public
name|void
name|testReloadDataV13Role
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|source
init|=
name|HISTORY_V1_3_ROLE
decl_stmt|;
name|RoleHistoryWriter
name|writer
init|=
operator|new
name|RoleHistoryWriter
argument_list|()
decl_stmt|;
name|LoadedRoleHistory
name|loadedRoleHistory
init|=
name|writer
operator|.
name|read
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|loadedRoleHistory
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|RoleHistory
name|roleHistory
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|roleHistory
operator|.
name|rebuild
argument_list|(
name|loadedRoleHistory
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that a v1 JSON file can be read. Here more roles than expected    * @throws Throwable    */
comment|//@Test
DECL|method|testReloadDataV16Role ()
specifier|public
name|void
name|testReloadDataV16Role
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|source
init|=
name|HISTORY_V1_6_ROLE
decl_stmt|;
name|RoleHistoryWriter
name|writer
init|=
operator|new
name|RoleHistoryWriter
argument_list|()
decl_stmt|;
name|LoadedRoleHistory
name|loadedRoleHistory
init|=
name|writer
operator|.
name|read
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|loadedRoleHistory
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|RoleHistory
name|roleHistory
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|roleHistory
operator|.
name|rebuild
argument_list|(
name|loadedRoleHistory
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that a v1 JSON file can be read. Here the number of roles    * is less than the current state.    * @throws Throwable    */
comment|//@Test
DECL|method|testReloadLessRoles ()
specifier|public
name|void
name|testReloadLessRoles
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|source
init|=
name|HISTORY_V1_3_ROLE
decl_stmt|;
name|RoleHistoryWriter
name|writer
init|=
operator|new
name|RoleHistoryWriter
argument_list|()
decl_stmt|;
name|LoadedRoleHistory
name|loadedRoleHistory
init|=
name|writer
operator|.
name|read
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|loadedRoleHistory
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ProviderRole
argument_list|>
name|expandedRoles
init|=
operator|new
name|ArrayList
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|expandedRoles
operator|.
name|add
argument_list|(
name|PROVIDER_ROLE3
argument_list|)
expr_stmt|;
name|RoleHistory
name|roleHistory
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|expandedRoles
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|roleHistory
operator|.
name|rebuild
argument_list|(
name|loadedRoleHistory
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that a v1b JSON file can be read. Here more roles than expected    * @throws Throwable    */
comment|//@Test
DECL|method|testReloadDataV1B1Role ()
specifier|public
name|void
name|testReloadDataV1B1Role
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|source
init|=
name|HISTORY_V1B_1_ROLE
decl_stmt|;
name|RoleHistoryWriter
name|writer
init|=
operator|new
name|RoleHistoryWriter
argument_list|()
decl_stmt|;
name|LoadedRoleHistory
name|loadedRoleHistory
init|=
name|writer
operator|.
name|read
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|loadedRoleHistory
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|loadedRoleHistory
operator|.
name|roleMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|RoleHistory
name|roleHistory
init|=
operator|new
name|MockRoleHistory
argument_list|(
name|MockFactory
operator|.
name|ROLES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|roleHistory
operator|.
name|rebuild
argument_list|(
name|loadedRoleHistory
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

