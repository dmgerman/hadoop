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
name|FederationTestUtils
operator|.
name|createFile
import|;
end_import

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
name|server
operator|.
name|federation
operator|.
name|FederationTestUtils
operator|.
name|verifyFileExists
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
name|assertEquals
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|protocol
operator|.
name|ClientProtocol
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
name|protocol
operator|.
name|DirectoryListing
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
name|protocol
operator|.
name|HdfsFileStatus
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
name|MockResolver
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
name|resolver
operator|.
name|FileSubclusterResolver
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
name|PathLocation
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
name|RemoteLocation
import|;
end_import

begin_comment
comment|/**  * The the RPC interface of the {@link getRouter()} implemented by  * {@link RouterRpcServer}.  */
end_comment

begin_class
DECL|class|TestRouterRpcMultiDestination
specifier|public
class|class
name|TestRouterRpcMultiDestination
extends|extends
name|TestRouterRpc
block|{
annotation|@
name|Override
DECL|method|testSetup ()
specifier|public
name|void
name|testSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|RouterDFSCluster
name|cluster
init|=
name|getCluster
argument_list|()
decl_stmt|;
comment|// Create mock locations
name|getCluster
argument_list|()
operator|.
name|installMockLocations
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|RouterContext
argument_list|>
name|routers
init|=
name|cluster
operator|.
name|getRouters
argument_list|()
decl_stmt|;
comment|// Add extra location to the root mount / such that the root mount points:
comment|// /
comment|//   ns0 -> /
comment|//   ns1 -> /
for|for
control|(
name|RouterContext
name|rc
range|:
name|routers
control|)
block|{
name|Router
name|router
init|=
name|rc
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|MockResolver
name|resolver
init|=
operator|(
name|MockResolver
operator|)
name|router
operator|.
name|getSubclusterResolver
argument_list|()
decl_stmt|;
name|resolver
operator|.
name|addLocation
argument_list|(
literal|"/"
argument_list|,
name|cluster
operator|.
name|getNameservices
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
block|}
comment|// Create a mount that points to 2 dirs in the same ns:
comment|// /same
comment|//   ns0 -> /
comment|//   ns0 -> /target-ns0
for|for
control|(
name|RouterContext
name|rc
range|:
name|routers
control|)
block|{
name|Router
name|router
init|=
name|rc
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|MockResolver
name|resolver
init|=
operator|(
name|MockResolver
operator|)
name|router
operator|.
name|getSubclusterResolver
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nss
init|=
name|cluster
operator|.
name|getNameservices
argument_list|()
decl_stmt|;
name|String
name|ns0
init|=
name|nss
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|resolver
operator|.
name|addLocation
argument_list|(
literal|"/same"
argument_list|,
name|ns0
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|addLocation
argument_list|(
literal|"/same"
argument_list|,
name|ns0
argument_list|,
name|cluster
operator|.
name|getNamenodePathForNS
argument_list|(
name|ns0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Delete all files via the NNs and verify
name|cluster
operator|.
name|deleteAllFiles
argument_list|()
expr_stmt|;
comment|// Create test fixtures on NN
name|cluster
operator|.
name|createTestDirectoriesNamenode
argument_list|()
expr_stmt|;
comment|// Wait to ensure NN has fully created its test directories
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Pick a NS, namenode and getRouter() for this test
name|RouterContext
name|router
init|=
name|cluster
operator|.
name|getRandomRouter
argument_list|()
decl_stmt|;
name|this
operator|.
name|setRouter
argument_list|(
name|router
argument_list|)
expr_stmt|;
name|String
name|ns
init|=
name|cluster
operator|.
name|getRandomNameservice
argument_list|()
decl_stmt|;
name|this
operator|.
name|setNs
argument_list|(
name|ns
argument_list|)
expr_stmt|;
name|this
operator|.
name|setNamenode
argument_list|(
name|cluster
operator|.
name|getNamenode
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create a test file on a single NN that is accessed via a getRouter() path
comment|// with 2 destinations. All tests should failover to the alternate
comment|// destination if the wrong NN is attempted first.
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|String
name|randomString
init|=
literal|"testfile-"
operator|+
name|r
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|setNamenodeFile
argument_list|(
literal|"/"
operator|+
name|randomString
argument_list|)
expr_stmt|;
name|setRouterFile
argument_list|(
literal|"/"
operator|+
name|randomString
argument_list|)
expr_stmt|;
name|FileSystem
name|nnFs
init|=
name|getNamenodeFileSystem
argument_list|()
decl_stmt|;
name|FileSystem
name|routerFs
init|=
name|getRouterFileSystem
argument_list|()
decl_stmt|;
name|createFile
argument_list|(
name|nnFs
argument_list|,
name|getNamenodeFile
argument_list|()
argument_list|,
literal|32
argument_list|)
expr_stmt|;
name|verifyFileExists
argument_list|(
name|nnFs
argument_list|,
name|getNamenodeFile
argument_list|()
argument_list|)
expr_stmt|;
name|verifyFileExists
argument_list|(
name|routerFs
argument_list|,
name|getRouterFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testListing (String path)
specifier|private
name|void
name|testListing
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Collect the mount table entries for this path
name|Set
argument_list|<
name|String
argument_list|>
name|requiredPaths
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|RouterContext
name|rc
init|=
name|getRouterContext
argument_list|()
decl_stmt|;
name|Router
name|router
init|=
name|rc
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|FileSubclusterResolver
name|subclusterResolver
init|=
name|router
operator|.
name|getSubclusterResolver
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|mount
range|:
name|subclusterResolver
operator|.
name|getMountPoints
argument_list|(
name|path
argument_list|)
control|)
block|{
name|requiredPaths
operator|.
name|add
argument_list|(
name|mount
argument_list|)
expr_stmt|;
block|}
comment|// Get files/dirs from the Namenodes
name|PathLocation
name|location
init|=
name|subclusterResolver
operator|.
name|getDestinationForPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|RemoteLocation
name|loc
range|:
name|location
operator|.
name|getDestinations
argument_list|()
control|)
block|{
name|String
name|nsId
init|=
name|loc
operator|.
name|getNameserviceId
argument_list|()
decl_stmt|;
name|String
name|dest
init|=
name|loc
operator|.
name|getDest
argument_list|()
decl_stmt|;
name|NamenodeContext
name|nn
init|=
name|getCluster
argument_list|()
operator|.
name|getNamenode
argument_list|(
name|nsId
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|nn
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FileStatus
index|[]
name|files
init|=
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|dest
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|file
range|:
name|files
control|)
block|{
name|String
name|pathName
init|=
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|requiredPaths
operator|.
name|add
argument_list|(
name|pathName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Get files/dirs from the Router
name|DirectoryListing
name|listing
init|=
name|getRouterProtocol
argument_list|()
operator|.
name|getListing
argument_list|(
name|path
argument_list|,
name|HdfsFileStatus
operator|.
name|EMPTY_NAME
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|requiredPathsIterator
init|=
name|requiredPaths
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// Match each path returned and verify order returned
name|HdfsFileStatus
index|[]
name|partialListing
init|=
name|listing
operator|.
name|getPartialListing
argument_list|()
decl_stmt|;
for|for
control|(
name|HdfsFileStatus
name|fileStatus
range|:
name|listing
operator|.
name|getPartialListing
argument_list|()
control|)
block|{
name|String
name|fileName
init|=
name|requiredPathsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|currentFile
init|=
name|fileStatus
operator|.
name|getFullPath
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|currentFile
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
comment|// Verify the total number of results found/matched
name|assertEquals
argument_list|(
name|requiredPaths
operator|+
literal|" doesn't match "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|partialListing
argument_list|)
argument_list|,
name|requiredPaths
operator|.
name|size
argument_list|()
argument_list|,
name|partialListing
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testProxyListFiles ()
specifier|public
name|void
name|testProxyListFiles
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|URISyntaxException
throws|,
name|NoSuchMethodException
throws|,
name|SecurityException
block|{
comment|// Verify that the root listing is a union of the mount table destinations
comment|// and the files stored at all nameservices mounted at the root (ns0 + ns1)
comment|// / -->
comment|// /ns0 (from mount table)
comment|// /ns1 (from mount table)
comment|// /same (from the mount table)
comment|// all items in / of ns0 from mapping of / -> ns0:::/)
comment|// all items in / of ns1 from mapping of / -> ns1:::/)
name|testListing
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
comment|// Verify that the "/same" mount point lists the contents of both dirs in
comment|// the same ns
comment|// /same -->
comment|// /target-ns0 (from root of ns0)
comment|// /testdir (from contents of /target-ns0)
name|testListing
argument_list|(
literal|"/same"
argument_list|)
expr_stmt|;
comment|// List a non-existing path and validate error response with NN behavior
name|ClientProtocol
name|namenodeProtocol
init|=
name|getCluster
argument_list|()
operator|.
name|getRandomNamenode
argument_list|()
operator|.
name|getClient
argument_list|()
operator|.
name|getNamenode
argument_list|()
decl_stmt|;
name|Method
name|m
init|=
name|ClientProtocol
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"getListing"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|byte
index|[]
operator|.
expr|class
argument_list|,
name|boolean
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|badPath
init|=
literal|"/unknownlocation/unknowndir"
decl_stmt|;
name|compareResponses
argument_list|(
name|getRouterProtocol
argument_list|()
argument_list|,
name|namenodeProtocol
argument_list|,
name|m
argument_list|,
operator|new
name|Object
index|[]
block|{
name|badPath
block|,
name|HdfsFileStatus
operator|.
name|EMPTY_NAME
block|,
literal|false
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testProxyRenameFiles ()
specifier|public
name|void
name|testProxyRenameFiles
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|super
operator|.
name|testProxyRenameFiles
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nss
init|=
name|getCluster
argument_list|()
operator|.
name|getNameservices
argument_list|()
decl_stmt|;
name|String
name|ns0
init|=
name|nss
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|ns1
init|=
name|nss
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Rename a file from ns0 into the root (mapped to both ns0 and ns1)
name|String
name|testDir0
init|=
name|getCluster
argument_list|()
operator|.
name|getFederatedTestDirectoryForNS
argument_list|(
name|ns0
argument_list|)
decl_stmt|;
name|String
name|filename0
init|=
name|testDir0
operator|+
literal|"/testrename"
decl_stmt|;
name|String
name|renamedFile
init|=
literal|"/testrename"
decl_stmt|;
name|testRename
argument_list|(
name|getRouterContext
argument_list|()
argument_list|,
name|filename0
argument_list|,
name|renamedFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testRename2
argument_list|(
name|getRouterContext
argument_list|()
argument_list|,
name|filename0
argument_list|,
name|renamedFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Rename a file from ns1 into the root (mapped to both ns0 and ns1)
name|String
name|testDir1
init|=
name|getCluster
argument_list|()
operator|.
name|getFederatedTestDirectoryForNS
argument_list|(
name|ns1
argument_list|)
decl_stmt|;
name|String
name|filename1
init|=
name|testDir1
operator|+
literal|"/testrename"
decl_stmt|;
name|testRename
argument_list|(
name|getRouterContext
argument_list|()
argument_list|,
name|filename1
argument_list|,
name|renamedFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testRename2
argument_list|(
name|getRouterContext
argument_list|()
argument_list|,
name|filename1
argument_list|,
name|renamedFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

