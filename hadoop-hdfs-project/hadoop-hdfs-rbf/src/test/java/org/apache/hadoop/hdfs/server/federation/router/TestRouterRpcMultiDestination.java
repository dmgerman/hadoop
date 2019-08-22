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
name|Matchers
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
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|Whitebox
operator|.
name|getInternalState
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
name|test
operator|.
name|Whitebox
operator|.
name|setInternalState
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
name|MiniRouterDFSCluster
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
name|MiniRouterDFSCluster
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
name|MiniRouterDFSCluster
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
name|namenode
operator|.
name|FSNamesystem
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
name|namenode
operator|.
name|NameNode
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
name|namenode
operator|.
name|ha
operator|.
name|HAContext
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
name|IOUtils
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
name|ipc
operator|.
name|RemoteException
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
name|ipc
operator|.
name|StandbyException
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
name|junit
operator|.
name|Test
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
name|MiniRouterDFSCluster
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
name|List
argument_list|<
name|String
argument_list|>
name|mountList
init|=
name|subclusterResolver
operator|.
name|getMountPoints
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|mountList
operator|!=
literal|null
condition|)
block|{
name|requiredPaths
operator|.
name|addAll
argument_list|(
name|mountList
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
comment|/**    * Test recoverLease when the result is false.    */
annotation|@
name|Test
DECL|method|testRecoverLease ()
specifier|public
name|void
name|testRecoverLease
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
literal|"/recovery/test_recovery_lease"
argument_list|)
decl_stmt|;
name|DistributedFileSystem
name|routerFs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|getRouterFileSystem
argument_list|()
decl_stmt|;
name|FSDataOutputStream
name|fsDataOutputStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fsDataOutputStream
operator|=
name|routerFs
operator|.
name|create
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
name|fsDataOutputStream
operator|.
name|write
argument_list|(
literal|"hello world"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fsDataOutputStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|boolean
name|result
init|=
name|routerFs
operator|.
name|recoverLease
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fsDataOutputStream
argument_list|)
expr_stmt|;
name|routerFs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetContentSummaryEc ()
specifier|public
name|void
name|testGetContentSummaryEc
parameter_list|()
throws|throws
name|Exception
block|{
name|DistributedFileSystem
name|routerDFS
init|=
operator|(
name|DistributedFileSystem
operator|)
name|getRouterFileSystem
argument_list|()
decl_stmt|;
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|expectedECPolicy
init|=
literal|"RS-6-3-1024k"
decl_stmt|;
try|try
block|{
name|routerDFS
operator|.
name|setErasureCodingPolicy
argument_list|(
name|dir
argument_list|,
name|expectedECPolicy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedECPolicy
argument_list|,
name|routerDFS
operator|.
name|getContentSummary
argument_list|(
name|dir
argument_list|)
operator|.
name|getErasureCodingPolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|routerDFS
operator|.
name|unsetErasureCodingPolicy
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSubclusterDown ()
specifier|public
name|void
name|testSubclusterDown
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|totalFiles
init|=
literal|6
decl_stmt|;
name|List
argument_list|<
name|RouterContext
argument_list|>
name|routers
init|=
name|getCluster
argument_list|()
operator|.
name|getRouters
argument_list|()
decl_stmt|;
comment|// Test the behavior when everything is fine
name|FileSystem
name|fs
init|=
name|getRouterFileSystem
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
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|totalFiles
argument_list|,
name|files
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Simulate one of the subclusters is in standby
name|NameNode
name|nn0
init|=
name|getCluster
argument_list|()
operator|.
name|getNamenode
argument_list|(
literal|"ns0"
argument_list|,
literal|null
argument_list|)
operator|.
name|getNamenode
argument_list|()
decl_stmt|;
name|FSNamesystem
name|ns0
init|=
name|nn0
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|HAContext
name|nn0haCtx
init|=
operator|(
name|HAContext
operator|)
name|getInternalState
argument_list|(
name|ns0
argument_list|,
literal|"haContext"
argument_list|)
decl_stmt|;
name|HAContext
name|mockCtx
init|=
name|mock
argument_list|(
name|HAContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
operator|new
name|StandbyException
argument_list|(
literal|"Mock"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockCtx
argument_list|)
operator|.
name|checkOperation
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|setInternalState
argument_list|(
name|ns0
argument_list|,
literal|"haContext"
argument_list|,
name|mockCtx
argument_list|)
expr_stmt|;
comment|// router0 should throw an exception
name|RouterContext
name|router0
init|=
name|routers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RouterRpcServer
name|router0RPCServer
init|=
name|router0
operator|.
name|getRouter
argument_list|()
operator|.
name|getRpcServer
argument_list|()
decl_stmt|;
name|RouterClientProtocol
name|router0ClientProtocol
init|=
name|router0RPCServer
operator|.
name|getClientProtocolModule
argument_list|()
decl_stmt|;
name|setInternalState
argument_list|(
name|router0ClientProtocol
argument_list|,
literal|"allowPartialList"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|router0
operator|.
name|getFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"I should throw an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"No namenode available to invoke getListing"
argument_list|,
name|re
argument_list|)
expr_stmt|;
block|}
comment|// router1 should report partial results
name|RouterContext
name|router1
init|=
name|routers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|files
operator|=
name|router1
operator|.
name|getFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Found "
operator|+
name|files
operator|.
name|length
operator|+
literal|" items, we should have less"
argument_list|,
name|files
operator|.
name|length
operator|<
name|totalFiles
argument_list|)
expr_stmt|;
comment|// Restore the HA context and the Router
name|setInternalState
argument_list|(
name|ns0
argument_list|,
literal|"haContext"
argument_list|,
name|nn0haCtx
argument_list|)
expr_stmt|;
name|setInternalState
argument_list|(
name|router0ClientProtocol
argument_list|,
literal|"allowPartialList"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

