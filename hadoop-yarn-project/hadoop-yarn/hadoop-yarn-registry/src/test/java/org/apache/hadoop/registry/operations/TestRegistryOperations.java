begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.operations
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|operations
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
name|FileAlreadyExistsException
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
name|PathIsNotEmptyDirectoryException
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
name|PathNotFoundException
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
name|registry
operator|.
name|AbstractRegistryTest
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|BindFlags
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
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryUtils
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
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryPathUtils
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
name|registry
operator|.
name|client
operator|.
name|exceptions
operator|.
name|NoRecordException
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|yarn
operator|.
name|PersistencePolicies
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|RegistryPathStatus
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|yarn
operator|.
name|YarnRegistryAttributes
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
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|TestRegistryOperations
specifier|public
class|class
name|TestRegistryOperations
extends|extends
name|AbstractRegistryTest
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestRegistryOperations
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testPutGetServiceEntry ()
specifier|public
name|void
name|testPutGetServiceEntry
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceRecord
name|written
init|=
name|putExampleServiceEntry
argument_list|(
name|ENTRY_PATH
argument_list|,
literal|0
argument_list|,
name|PersistencePolicies
operator|.
name|APPLICATION
argument_list|)
decl_stmt|;
name|ServiceRecord
name|resolved
init|=
name|operations
operator|.
name|resolve
argument_list|(
name|ENTRY_PATH
argument_list|)
decl_stmt|;
name|validateEntry
argument_list|(
name|resolved
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|written
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteServiceEntry ()
specifier|public
name|void
name|testDeleteServiceEntry
parameter_list|()
throws|throws
name|Throwable
block|{
name|putExampleServiceEntry
argument_list|(
name|ENTRY_PATH
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|operations
operator|.
name|delete
argument_list|(
name|ENTRY_PATH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteNonexistentEntry ()
specifier|public
name|void
name|testDeleteNonexistentEntry
parameter_list|()
throws|throws
name|Throwable
block|{
name|operations
operator|.
name|delete
argument_list|(
name|ENTRY_PATH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|operations
operator|.
name|delete
argument_list|(
name|ENTRY_PATH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStat ()
specifier|public
name|void
name|testStat
parameter_list|()
throws|throws
name|Throwable
block|{
name|putExampleServiceEntry
argument_list|(
name|ENTRY_PATH
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|RegistryPathStatus
name|stat
init|=
name|operations
operator|.
name|stat
argument_list|(
name|ENTRY_PATH
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|size
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|time
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NAME
argument_list|,
name|stat
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLsParent ()
specifier|public
name|void
name|testLsParent
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceRecord
name|written
init|=
name|putExampleServiceEntry
argument_list|(
name|ENTRY_PATH
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|RegistryPathStatus
name|stat
init|=
name|operations
operator|.
name|stat
argument_list|(
name|ENTRY_PATH
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|operations
operator|.
name|list
argument_list|(
name|PARENT_PATH
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|children
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NAME
argument_list|,
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|RegistryPathStatus
argument_list|>
name|childStats
init|=
name|RegistryUtils
operator|.
name|statChildren
argument_list|(
name|operations
argument_list|,
name|PARENT_PATH
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childStats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stat
argument_list|,
name|childStats
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ServiceRecord
argument_list|>
name|records
init|=
name|RegistryUtils
operator|.
name|extractServiceRecords
argument_list|(
name|operations
argument_list|,
name|PARENT_PATH
argument_list|,
name|childStats
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ServiceRecord
name|record
init|=
name|records
operator|.
name|get
argument_list|(
name|ENTRY_PATH
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|record
argument_list|)
expr_stmt|;
name|record
operator|.
name|validate
argument_list|()
expr_stmt|;
name|assertMatches
argument_list|(
name|written
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteNonEmpty ()
specifier|public
name|void
name|testDeleteNonEmpty
parameter_list|()
throws|throws
name|Throwable
block|{
name|putExampleServiceEntry
argument_list|(
name|ENTRY_PATH
argument_list|,
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|operations
operator|.
name|delete
argument_list|(
name|PARENT_PATH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected a failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathIsNotEmptyDirectoryException
name|expected
parameter_list|)
block|{
comment|// expected; ignore
block|}
name|operations
operator|.
name|delete
argument_list|(
name|PARENT_PATH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PathNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testStatEmptyPath ()
specifier|public
name|void
name|testStatEmptyPath
parameter_list|()
throws|throws
name|Throwable
block|{
name|operations
operator|.
name|stat
argument_list|(
name|ENTRY_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PathNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testLsEmptyPath ()
specifier|public
name|void
name|testLsEmptyPath
parameter_list|()
throws|throws
name|Throwable
block|{
name|operations
operator|.
name|list
argument_list|(
name|PARENT_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PathNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testResolveEmptyPath ()
specifier|public
name|void
name|testResolveEmptyPath
parameter_list|()
throws|throws
name|Throwable
block|{
name|operations
operator|.
name|resolve
argument_list|(
name|ENTRY_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirNoParent ()
specifier|public
name|void
name|testMkdirNoParent
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|path
init|=
name|ENTRY_PATH
operator|+
literal|"/missing"
decl_stmt|;
try|try
block|{
name|operations
operator|.
name|mknode
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RegistryPathStatus
name|stat
init|=
name|operations
operator|.
name|stat
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Got a status "
operator|+
name|stat
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testDoubleMkdir ()
specifier|public
name|void
name|testDoubleMkdir
parameter_list|()
throws|throws
name|Throwable
block|{
name|operations
operator|.
name|mknode
argument_list|(
name|USERPATH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|USERPATH
operator|+
literal|"newentry"
decl_stmt|;
name|assertTrue
argument_list|(
name|operations
operator|.
name|mknode
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|operations
operator|.
name|stat
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|operations
operator|.
name|mknode
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutNoParent ()
specifier|public
name|void
name|testPutNoParent
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceRecord
name|record
init|=
operator|new
name|ServiceRecord
argument_list|()
decl_stmt|;
name|record
operator|.
name|set
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_ID
argument_list|,
literal|"testPutNoParent"
argument_list|)
expr_stmt|;
name|String
name|path
init|=
literal|"/path/without/parent"
decl_stmt|;
try|try
block|{
name|operations
operator|.
name|bind
argument_list|(
name|path
argument_list|,
name|record
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// didn't get a failure
comment|// trouble
name|RegistryPathStatus
name|stat
init|=
name|operations
operator|.
name|stat
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Got a status "
operator|+
name|stat
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testPutMinimalRecord ()
specifier|public
name|void
name|testPutMinimalRecord
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|path
init|=
literal|"/path/with/minimal"
decl_stmt|;
name|operations
operator|.
name|mknode
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ServiceRecord
name|record
init|=
operator|new
name|ServiceRecord
argument_list|()
decl_stmt|;
name|operations
operator|.
name|bind
argument_list|(
name|path
argument_list|,
name|record
argument_list|,
name|BindFlags
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
name|ServiceRecord
name|resolve
init|=
name|operations
operator|.
name|resolve
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|record
argument_list|,
name|resolve
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PathNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testPutNoParent2 ()
specifier|public
name|void
name|testPutNoParent2
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceRecord
name|record
init|=
operator|new
name|ServiceRecord
argument_list|()
decl_stmt|;
name|record
operator|.
name|set
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_ID
argument_list|,
literal|"testPutNoParent"
argument_list|)
expr_stmt|;
name|String
name|path
init|=
literal|"/path/without/parent"
decl_stmt|;
name|operations
operator|.
name|bind
argument_list|(
name|path
argument_list|,
name|record
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatDirectory ()
specifier|public
name|void
name|testStatDirectory
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|empty
init|=
literal|"/empty"
decl_stmt|;
name|operations
operator|.
name|mknode
argument_list|(
name|empty
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|operations
operator|.
name|stat
argument_list|(
name|empty
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatRootPath ()
specifier|public
name|void
name|testStatRootPath
parameter_list|()
throws|throws
name|Throwable
block|{
name|operations
operator|.
name|mknode
argument_list|(
literal|"/"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|operations
operator|.
name|stat
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|operations
operator|.
name|list
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|operations
operator|.
name|list
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatOneLevelDown ()
specifier|public
name|void
name|testStatOneLevelDown
parameter_list|()
throws|throws
name|Throwable
block|{
name|operations
operator|.
name|mknode
argument_list|(
literal|"/subdir"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|operations
operator|.
name|stat
argument_list|(
literal|"/subdir"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLsRootPath ()
specifier|public
name|void
name|testLsRootPath
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|empty
init|=
literal|"/"
decl_stmt|;
name|operations
operator|.
name|mknode
argument_list|(
name|empty
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|operations
operator|.
name|stat
argument_list|(
name|empty
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testResolvePathThatHasNoEntry ()
specifier|public
name|void
name|testResolvePathThatHasNoEntry
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|empty
init|=
literal|"/empty2"
decl_stmt|;
name|operations
operator|.
name|mknode
argument_list|(
name|empty
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|ServiceRecord
name|record
init|=
name|operations
operator|.
name|resolve
argument_list|(
name|empty
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected an exception, got "
operator|+
name|record
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoRecordException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testOverwrite ()
specifier|public
name|void
name|testOverwrite
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceRecord
name|written
init|=
name|putExampleServiceEntry
argument_list|(
name|ENTRY_PATH
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ServiceRecord
name|resolved1
init|=
name|operations
operator|.
name|resolve
argument_list|(
name|ENTRY_PATH
argument_list|)
decl_stmt|;
name|resolved1
operator|.
name|description
operator|=
literal|"resolved1"
expr_stmt|;
try|try
block|{
name|operations
operator|.
name|bind
argument_list|(
name|ENTRY_PATH
argument_list|,
name|resolved1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"overwrite succeeded when it should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileAlreadyExistsException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
comment|// verify there's no changed
name|ServiceRecord
name|resolved2
init|=
name|operations
operator|.
name|resolve
argument_list|(
name|ENTRY_PATH
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|written
argument_list|,
name|resolved2
argument_list|)
expr_stmt|;
name|operations
operator|.
name|bind
argument_list|(
name|ENTRY_PATH
argument_list|,
name|resolved1
argument_list|,
name|BindFlags
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
name|ServiceRecord
name|resolved3
init|=
name|operations
operator|.
name|resolve
argument_list|(
name|ENTRY_PATH
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|resolved1
argument_list|,
name|resolved3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutGetContainerPersistenceServiceEntry ()
specifier|public
name|void
name|testPutGetContainerPersistenceServiceEntry
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|path
init|=
name|ENTRY_PATH
decl_stmt|;
name|ServiceRecord
name|written
init|=
name|buildExampleServiceEntry
argument_list|(
name|PersistencePolicies
operator|.
name|CONTAINER
argument_list|)
decl_stmt|;
name|operations
operator|.
name|mknode
argument_list|(
name|RegistryPathUtils
operator|.
name|parentOf
argument_list|(
name|path
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|operations
operator|.
name|bind
argument_list|(
name|path
argument_list|,
name|written
argument_list|,
name|BindFlags
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|ServiceRecord
name|resolved
init|=
name|operations
operator|.
name|resolve
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|validateEntry
argument_list|(
name|resolved
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|written
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddingWriteAccessIsNoOpEntry ()
specifier|public
name|void
name|testAddingWriteAccessIsNoOpEntry
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertFalse
argument_list|(
name|operations
operator|.
name|addWriteAccessor
argument_list|(
literal|"id"
argument_list|,
literal|"pass"
argument_list|)
argument_list|)
expr_stmt|;
name|operations
operator|.
name|clearWriteAccessors
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListListFully ()
specifier|public
name|void
name|testListListFully
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceRecord
name|r1
init|=
operator|new
name|ServiceRecord
argument_list|()
decl_stmt|;
name|ServiceRecord
name|r2
init|=
name|createRecord
argument_list|(
literal|"i"
argument_list|,
name|PersistencePolicies
operator|.
name|PERMANENT
argument_list|,
literal|"r2"
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|USERPATH
operator|+
name|SC_HADOOP
operator|+
literal|"/listing"
decl_stmt|;
name|operations
operator|.
name|mknode
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|r1path
init|=
name|path
operator|+
literal|"/r1"
decl_stmt|;
name|operations
operator|.
name|bind
argument_list|(
name|r1path
argument_list|,
name|r1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|String
name|r2path
init|=
name|path
operator|+
literal|"/r2"
decl_stmt|;
name|operations
operator|.
name|bind
argument_list|(
name|r2path
argument_list|,
name|r2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|RegistryPathStatus
name|r1stat
init|=
name|operations
operator|.
name|stat
argument_list|(
name|r1path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"r1"
argument_list|,
name|r1stat
operator|.
name|path
argument_list|)
expr_stmt|;
name|RegistryPathStatus
name|r2stat
init|=
name|operations
operator|.
name|stat
argument_list|(
name|r2path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"r2"
argument_list|,
name|r2stat
operator|.
name|path
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|r1stat
argument_list|,
name|r2stat
argument_list|)
expr_stmt|;
comment|// listings now
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|operations
operator|.
name|list
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong no. of children"
argument_list|,
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// there's no order here, so create one
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|names
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|entries
init|=
literal|""
decl_stmt|;
for|for
control|(
name|String
name|child
range|:
name|list
control|)
block|{
name|names
operator|.
name|put
argument_list|(
name|child
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|entries
operator|+=
name|child
operator|+
literal|" "
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"No 'r1' in "
operator|+
name|entries
argument_list|,
name|names
operator|.
name|containsKey
argument_list|(
literal|"r1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No 'r2' in "
operator|+
name|entries
argument_list|,
name|names
operator|.
name|containsKey
argument_list|(
literal|"r2"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|RegistryPathStatus
argument_list|>
name|stats
init|=
name|RegistryUtils
operator|.
name|statChildren
argument_list|(
name|operations
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong no. of children"
argument_list|,
literal|2
argument_list|,
name|stats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r1stat
argument_list|,
name|stats
operator|.
name|get
argument_list|(
literal|"r1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r2stat
argument_list|,
name|stats
operator|.
name|get
argument_list|(
literal|"r2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

