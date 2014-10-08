begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.impl
package|package
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
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|api
operator|.
name|CuratorEvent
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
name|service
operator|.
name|ServiceOperations
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
name|AbstractZKRegistryTest
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
name|impl
operator|.
name|zk
operator|.
name|CuratorService
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
name|impl
operator|.
name|zk
operator|.
name|RegistrySecurity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
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
name|IOException
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
comment|/**  * Test the curator service  */
end_comment

begin_class
DECL|class|TestCuratorService
specifier|public
class|class
name|TestCuratorService
extends|extends
name|AbstractZKRegistryTest
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
name|TestCuratorService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|curatorService
specifier|protected
name|CuratorService
name|curatorService
decl_stmt|;
DECL|field|MISSING
specifier|public
specifier|static
specifier|final
name|String
name|MISSING
init|=
literal|"/missing"
decl_stmt|;
DECL|field|rootACL
specifier|private
name|List
argument_list|<
name|ACL
argument_list|>
name|rootACL
decl_stmt|;
annotation|@
name|Before
DECL|method|startCurator ()
specifier|public
name|void
name|startCurator
parameter_list|()
throws|throws
name|IOException
block|{
name|createCuratorService
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|stopCurator ()
specifier|public
name|void
name|stopCurator
parameter_list|()
block|{
name|ServiceOperations
operator|.
name|stop
argument_list|(
name|curatorService
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an instance    */
DECL|method|createCuratorService ()
specifier|protected
name|void
name|createCuratorService
parameter_list|()
throws|throws
name|IOException
block|{
name|curatorService
operator|=
operator|new
name|CuratorService
argument_list|(
literal|"curatorService"
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|init
argument_list|(
name|createRegistryConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|start
argument_list|()
expr_stmt|;
name|rootACL
operator|=
name|RegistrySecurity
operator|.
name|WorldReadWriteACL
expr_stmt|;
name|curatorService
operator|.
name|maybeCreate
argument_list|(
literal|""
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|rootACL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLs ()
specifier|public
name|void
name|testLs
parameter_list|()
throws|throws
name|Throwable
block|{
name|curatorService
operator|.
name|zkList
argument_list|(
literal|"/"
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
DECL|method|testLsNotFound ()
specifier|public
name|void
name|testLsNotFound
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ls
init|=
name|curatorService
operator|.
name|zkList
argument_list|(
name|MISSING
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExists ()
specifier|public
name|void
name|testExists
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertTrue
argument_list|(
name|curatorService
operator|.
name|zkPathExists
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExistsMissing ()
specifier|public
name|void
name|testExistsMissing
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertFalse
argument_list|(
name|curatorService
operator|.
name|zkPathExists
argument_list|(
name|MISSING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVerifyExists ()
specifier|public
name|void
name|testVerifyExists
parameter_list|()
throws|throws
name|Throwable
block|{
name|pathMustExist
argument_list|(
literal|"/"
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
DECL|method|testVerifyExistsMissing ()
specifier|public
name|void
name|testVerifyExistsMissing
parameter_list|()
throws|throws
name|Throwable
block|{
name|pathMustExist
argument_list|(
literal|"/file-not-found"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMkdirs ()
specifier|public
name|void
name|testMkdirs
parameter_list|()
throws|throws
name|Throwable
block|{
name|mkPath
argument_list|(
literal|"/p1"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|pathMustExist
argument_list|(
literal|"/p1"
argument_list|)
expr_stmt|;
name|mkPath
argument_list|(
literal|"/p1/p2"
argument_list|,
name|CreateMode
operator|.
name|EPHEMERAL
argument_list|)
expr_stmt|;
name|pathMustExist
argument_list|(
literal|"/p1/p2"
argument_list|)
expr_stmt|;
block|}
DECL|method|mkPath (String path, CreateMode mode)
specifier|private
name|void
name|mkPath
parameter_list|(
name|String
name|path
parameter_list|,
name|CreateMode
name|mode
parameter_list|)
throws|throws
name|IOException
block|{
name|curatorService
operator|.
name|zkMkPath
argument_list|(
name|path
argument_list|,
name|mode
argument_list|,
literal|false
argument_list|,
name|RegistrySecurity
operator|.
name|WorldReadWriteACL
argument_list|)
expr_stmt|;
block|}
DECL|method|pathMustExist (String path)
specifier|public
name|void
name|pathMustExist
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|curatorService
operator|.
name|zkPathMustExist
argument_list|(
name|path
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
DECL|method|testMkdirChild ()
specifier|public
name|void
name|testMkdirChild
parameter_list|()
throws|throws
name|Throwable
block|{
name|mkPath
argument_list|(
literal|"/testMkdirChild/child"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaybeCreate ()
specifier|public
name|void
name|testMaybeCreate
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertTrue
argument_list|(
name|curatorService
operator|.
name|maybeCreate
argument_list|(
literal|"/p3"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|RegistrySecurity
operator|.
name|WorldReadWriteACL
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|curatorService
operator|.
name|maybeCreate
argument_list|(
literal|"/p3"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|RegistrySecurity
operator|.
name|WorldReadWriteACL
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRM ()
specifier|public
name|void
name|testRM
parameter_list|()
throws|throws
name|Throwable
block|{
name|mkPath
argument_list|(
literal|"/rm"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|zkDelete
argument_list|(
literal|"/rm"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|verifyNotExists
argument_list|(
literal|"/rm"
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|zkDelete
argument_list|(
literal|"/rm"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRMNonRf ()
specifier|public
name|void
name|testRMNonRf
parameter_list|()
throws|throws
name|Throwable
block|{
name|mkPath
argument_list|(
literal|"/rm"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|mkPath
argument_list|(
literal|"/rm/child"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
try|try
block|{
name|curatorService
operator|.
name|zkDelete
argument_list|(
literal|"/rm"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected a failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathIsNotEmptyDirectoryException
name|expected
parameter_list|)
block|{      }
block|}
annotation|@
name|Test
DECL|method|testRMRf ()
specifier|public
name|void
name|testRMRf
parameter_list|()
throws|throws
name|Throwable
block|{
name|mkPath
argument_list|(
literal|"/rm"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|mkPath
argument_list|(
literal|"/rm/child"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|zkDelete
argument_list|(
literal|"/rm"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|verifyNotExists
argument_list|(
literal|"/rm"
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|zkDelete
argument_list|(
literal|"/rm"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBackgroundDelete ()
specifier|public
name|void
name|testBackgroundDelete
parameter_list|()
throws|throws
name|Throwable
block|{
name|mkPath
argument_list|(
literal|"/rm"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|mkPath
argument_list|(
literal|"/rm/child"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|CuratorEventCatcher
name|events
init|=
operator|new
name|CuratorEventCatcher
argument_list|()
decl_stmt|;
name|curatorService
operator|.
name|zkDelete
argument_list|(
literal|"/rm"
argument_list|,
literal|true
argument_list|,
name|events
argument_list|)
expr_stmt|;
name|CuratorEvent
name|taken
init|=
name|events
operator|.
name|take
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"took {}"
argument_list|,
name|taken
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|events
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreate ()
specifier|public
name|void
name|testCreate
parameter_list|()
throws|throws
name|Throwable
block|{
name|curatorService
operator|.
name|zkCreate
argument_list|(
literal|"/testcreate"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|getTestBuffer
argument_list|()
argument_list|,
name|rootACL
argument_list|)
expr_stmt|;
name|pathMustExist
argument_list|(
literal|"/testcreate"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateTwice ()
specifier|public
name|void
name|testCreateTwice
parameter_list|()
throws|throws
name|Throwable
block|{
name|byte
index|[]
name|buffer
init|=
name|getTestBuffer
argument_list|()
decl_stmt|;
name|curatorService
operator|.
name|zkCreate
argument_list|(
literal|"/testcreatetwice"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|buffer
argument_list|,
name|rootACL
argument_list|)
expr_stmt|;
try|try
block|{
name|curatorService
operator|.
name|zkCreate
argument_list|(
literal|"/testcreatetwice"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|buffer
argument_list|,
name|rootACL
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileAlreadyExistsException
name|e
parameter_list|)
block|{      }
block|}
annotation|@
name|Test
DECL|method|testCreateUpdate ()
specifier|public
name|void
name|testCreateUpdate
parameter_list|()
throws|throws
name|Throwable
block|{
name|byte
index|[]
name|buffer
init|=
name|getTestBuffer
argument_list|()
decl_stmt|;
name|curatorService
operator|.
name|zkCreate
argument_list|(
literal|"/testcreateupdate"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|buffer
argument_list|,
name|rootACL
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|zkUpdate
argument_list|(
literal|"/testcreateupdate"
argument_list|,
name|buffer
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
DECL|method|testUpdateMissing ()
specifier|public
name|void
name|testUpdateMissing
parameter_list|()
throws|throws
name|Throwable
block|{
name|curatorService
operator|.
name|zkUpdate
argument_list|(
literal|"/testupdatemissing"
argument_list|,
name|getTestBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateDirectory ()
specifier|public
name|void
name|testUpdateDirectory
parameter_list|()
throws|throws
name|Throwable
block|{
name|mkPath
argument_list|(
literal|"/testupdatedirectory"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|zkUpdate
argument_list|(
literal|"/testupdatedirectory"
argument_list|,
name|getTestBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateDirectorywithChild ()
specifier|public
name|void
name|testUpdateDirectorywithChild
parameter_list|()
throws|throws
name|Throwable
block|{
name|mkPath
argument_list|(
literal|"/testupdatedirectorywithchild"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|mkPath
argument_list|(
literal|"/testupdatedirectorywithchild/child"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|zkUpdate
argument_list|(
literal|"/testupdatedirectorywithchild"
argument_list|,
name|getTestBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUseZKServiceForBinding ()
specifier|public
name|void
name|testUseZKServiceForBinding
parameter_list|()
throws|throws
name|Throwable
block|{
name|CuratorService
name|cs2
init|=
operator|new
name|CuratorService
argument_list|(
literal|"curator"
argument_list|,
name|zookeeper
argument_list|)
decl_stmt|;
name|cs2
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|cs2
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|getTestBuffer ()
specifier|protected
name|byte
index|[]
name|getTestBuffer
parameter_list|()
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|buffer
index|[
literal|0
index|]
operator|=
literal|'0'
expr_stmt|;
return|return
name|buffer
return|;
block|}
DECL|method|verifyNotExists (String path)
specifier|public
name|void
name|verifyNotExists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|curatorService
operator|.
name|zkPathExists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Path should not exist: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

