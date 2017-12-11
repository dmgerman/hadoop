begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
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
name|federation
operator|.
name|router
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|RouterConfigBuilder
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
name|federation
operator|.
name|RouterDFSCluster
operator|.
name|NamenodeContext
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
name|federation
operator|.
name|RouterDFSCluster
operator|.
name|RouterContext
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
name|federation
operator|.
name|StateStoreDFSCluster
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
name|federation
operator|.
name|resolver
operator|.
name|MountTableManager
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
name|federation
operator|.
name|resolver
operator|.
name|MountTableResolver
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|AddMountTableEntryRequest
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|AddMountTableEntryResponse
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|MountTable
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
name|BeforeClass
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

begin_comment
comment|/**  * Test a router end-to-end including the MountTable.  */
end_comment

begin_class
DECL|class|TestRouterMountTable
specifier|public
class|class
name|TestRouterMountTable
block|{
DECL|field|cluster
specifier|private
specifier|static
name|StateStoreDFSCluster
name|cluster
decl_stmt|;
DECL|field|nnContext
specifier|private
specifier|static
name|NamenodeContext
name|nnContext
decl_stmt|;
DECL|field|routerContext
specifier|private
specifier|static
name|RouterContext
name|routerContext
decl_stmt|;
DECL|field|mountTable
specifier|private
specifier|static
name|MountTableResolver
name|mountTable
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|globalSetUp ()
specifier|public
specifier|static
name|void
name|globalSetUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Build and start a federated cluster
name|cluster
operator|=
operator|new
name|StateStoreDFSCluster
argument_list|(
literal|false
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|RouterConfigBuilder
argument_list|()
operator|.
name|stateStore
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|rpc
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|addRouterOverrides
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startCluster
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|startRouters
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
comment|// Get the end points
name|nnContext
operator|=
name|cluster
operator|.
name|getRandomNamenode
argument_list|()
expr_stmt|;
name|routerContext
operator|=
name|cluster
operator|.
name|getRandomRouter
argument_list|()
expr_stmt|;
name|Router
name|router
init|=
name|routerContext
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|mountTable
operator|=
operator|(
name|MountTableResolver
operator|)
name|router
operator|.
name|getSubclusterResolver
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
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
name|stopRouter
argument_list|(
name|routerContext
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
DECL|method|testReadOnly ()
specifier|public
name|void
name|testReadOnly
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Add a read only entry
name|MountTable
name|readOnlyEntry
init|=
name|MountTable
operator|.
name|newInstance
argument_list|(
literal|"/readonly"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"ns0"
argument_list|,
literal|"/testdir"
argument_list|)
argument_list|)
decl_stmt|;
name|readOnlyEntry
operator|.
name|setReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|addMountTable
argument_list|(
name|readOnlyEntry
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add a regular entry
name|MountTable
name|regularEntry
init|=
name|MountTable
operator|.
name|newInstance
argument_list|(
literal|"/regular"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"ns0"
argument_list|,
literal|"/testdir"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|addMountTable
argument_list|(
name|regularEntry
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create a folder which should show in all locations
specifier|final
name|FileSystem
name|nnFs
init|=
name|nnContext
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|routerFs
init|=
name|routerContext
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|routerFs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/regular/newdir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|dirStatusNn
init|=
name|nnFs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testdir/newdir"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dirStatusNn
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|FileStatus
name|dirStatusRegular
init|=
name|routerFs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/regular/newdir"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dirStatusRegular
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|FileStatus
name|dirStatusReadOnly
init|=
name|routerFs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/readonly/newdir"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dirStatusReadOnly
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// It should fail writing into a read only path
try|try
block|{
name|routerFs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/readonly/newdirfail"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We should not be able to write into a read only mount point"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|String
name|msg
init|=
name|ioe
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|startsWith
argument_list|(
literal|"/readonly/newdirfail is in a read only mount point"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add a mount table entry to the mount table through the admin API.    * @param entry Mount table entry to add.    * @return If it was succesfully added.    * @throws IOException Problems adding entries.    */
DECL|method|addMountTable (final MountTable entry)
specifier|private
name|boolean
name|addMountTable
parameter_list|(
specifier|final
name|MountTable
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
name|RouterClient
name|client
init|=
name|routerContext
operator|.
name|getAdminClient
argument_list|()
decl_stmt|;
name|MountTableManager
name|mountTableManager
init|=
name|client
operator|.
name|getMountTableManager
argument_list|()
decl_stmt|;
name|AddMountTableEntryRequest
name|addRequest
init|=
name|AddMountTableEntryRequest
operator|.
name|newInstance
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|AddMountTableEntryResponse
name|addResponse
init|=
name|mountTableManager
operator|.
name|addMountTableEntry
argument_list|(
name|addRequest
argument_list|)
decl_stmt|;
comment|// Reload the Router cache
name|mountTable
operator|.
name|loadCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|addResponse
operator|.
name|getStatus
argument_list|()
return|;
block|}
block|}
end_class

end_unit

