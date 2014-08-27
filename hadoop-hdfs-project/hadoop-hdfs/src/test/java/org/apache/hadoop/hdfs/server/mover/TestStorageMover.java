begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.mover
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
name|mover
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|*
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
name|protocol
operator|.
name|HdfsLocatedFileStatus
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
name|LocatedBlock
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
name|balancer
operator|.
name|Dispatcher
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
name|balancer
operator|.
name|ExitStatus
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
name|snapshot
operator|.
name|SnapshotTestHelper
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
name|Test
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test the data migration tool (for Archival Storage)  */
end_comment

begin_class
DECL|class|TestStorageMover
specifier|public
class|class
name|TestStorageMover
block|{
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|long
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|REPL
specifier|private
specifier|static
specifier|final
name|short
name|REPL
init|=
literal|3
decl_stmt|;
DECL|field|NUM_DATANODES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DATANODES
init|=
literal|6
decl_stmt|;
DECL|field|DEFAULT_CONF
specifier|private
specifier|static
specifier|final
name|Configuration
name|DEFAULT_CONF
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_POLICIES
specifier|private
specifier|static
specifier|final
name|BlockStoragePolicy
operator|.
name|Suite
name|DEFAULT_POLICIES
decl_stmt|;
DECL|field|HOT
specifier|private
specifier|static
specifier|final
name|BlockStoragePolicy
name|HOT
decl_stmt|;
DECL|field|WARM
specifier|private
specifier|static
specifier|final
name|BlockStoragePolicy
name|WARM
decl_stmt|;
DECL|field|COLD
specifier|private
specifier|static
specifier|final
name|BlockStoragePolicy
name|COLD
decl_stmt|;
static|static
block|{
name|DEFAULT_POLICIES
operator|=
name|BlockStoragePolicy
operator|.
name|readBlockStorageSuite
argument_list|(
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|HOT
operator|=
name|DEFAULT_POLICIES
operator|.
name|getPolicy
argument_list|(
literal|"HOT"
argument_list|)
expr_stmt|;
name|WARM
operator|=
name|DEFAULT_POLICIES
operator|.
name|getPolicy
argument_list|(
literal|"WARM"
argument_list|)
expr_stmt|;
name|COLD
operator|=
name|DEFAULT_POLICIES
operator|.
name|getPolicy
argument_list|(
literal|"COLD"
argument_list|)
expr_stmt|;
name|Dispatcher
operator|.
name|setBlockMoveWaitTime
argument_list|(
literal|10
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|/**    * This scheme defines files/directories and their block storage policies. It    * also defines snapshots.    */
DECL|class|NamespaceScheme
specifier|static
class|class
name|NamespaceScheme
block|{
DECL|field|files
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|files
decl_stmt|;
DECL|field|snapshotMap
specifier|final
name|Map
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|snapshotMap
decl_stmt|;
DECL|field|policyMap
specifier|final
name|Map
argument_list|<
name|Path
argument_list|,
name|BlockStoragePolicy
argument_list|>
name|policyMap
decl_stmt|;
DECL|method|NamespaceScheme (List<Path> files, Map<Path,List<String>> snapshotMap, Map<Path, BlockStoragePolicy> policyMap)
name|NamespaceScheme
parameter_list|(
name|List
argument_list|<
name|Path
argument_list|>
name|files
parameter_list|,
name|Map
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|snapshotMap
parameter_list|,
name|Map
argument_list|<
name|Path
argument_list|,
name|BlockStoragePolicy
argument_list|>
name|policyMap
parameter_list|)
block|{
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
name|this
operator|.
name|snapshotMap
operator|=
name|snapshotMap
operator|==
literal|null
condition|?
operator|new
name|HashMap
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
else|:
name|snapshotMap
expr_stmt|;
name|this
operator|.
name|policyMap
operator|=
name|policyMap
expr_stmt|;
block|}
block|}
comment|/**    * This scheme defines DataNodes and their storage, including storage types    * and remaining capacities.    */
DECL|class|ClusterScheme
specifier|static
class|class
name|ClusterScheme
block|{
DECL|field|conf
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|numDataNodes
specifier|final
name|int
name|numDataNodes
decl_stmt|;
DECL|field|repl
specifier|final
name|short
name|repl
decl_stmt|;
DECL|field|storageTypes
specifier|final
name|StorageType
index|[]
index|[]
name|storageTypes
decl_stmt|;
DECL|field|storageCapacities
specifier|final
name|long
index|[]
index|[]
name|storageCapacities
decl_stmt|;
DECL|method|ClusterScheme (Configuration conf, int numDataNodes, short repl, StorageType[][] types, long[][] capacities)
name|ClusterScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|numDataNodes
parameter_list|,
name|short
name|repl
parameter_list|,
name|StorageType
index|[]
index|[]
name|types
parameter_list|,
name|long
index|[]
index|[]
name|capacities
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|types
operator|==
literal|null
operator|||
name|types
operator|.
name|length
operator|==
name|numDataNodes
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|capacities
operator|==
literal|null
operator|||
name|capacities
operator|.
name|length
operator|==
name|numDataNodes
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|numDataNodes
operator|=
name|numDataNodes
expr_stmt|;
name|this
operator|.
name|repl
operator|=
name|repl
expr_stmt|;
name|this
operator|.
name|storageTypes
operator|=
name|types
expr_stmt|;
name|this
operator|.
name|storageCapacities
operator|=
name|capacities
expr_stmt|;
block|}
block|}
DECL|class|MigrationTest
class|class
name|MigrationTest
block|{
DECL|field|clusterScheme
specifier|private
specifier|final
name|ClusterScheme
name|clusterScheme
decl_stmt|;
DECL|field|nsScheme
specifier|private
specifier|final
name|NamespaceScheme
name|nsScheme
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|policies
specifier|private
specifier|final
name|BlockStoragePolicy
operator|.
name|Suite
name|policies
decl_stmt|;
DECL|method|MigrationTest (ClusterScheme cScheme, NamespaceScheme nsScheme)
name|MigrationTest
parameter_list|(
name|ClusterScheme
name|cScheme
parameter_list|,
name|NamespaceScheme
name|nsScheme
parameter_list|)
block|{
name|this
operator|.
name|clusterScheme
operator|=
name|cScheme
expr_stmt|;
name|this
operator|.
name|nsScheme
operator|=
name|nsScheme
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|clusterScheme
operator|.
name|conf
expr_stmt|;
name|this
operator|.
name|policies
operator|=
name|BlockStoragePolicy
operator|.
name|readBlockStorageSuite
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set up the cluster and start NameNode and DataNodes according to the      * corresponding scheme.      */
DECL|method|setupCluster ()
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|clusterScheme
operator|.
name|numDataNodes
argument_list|)
operator|.
name|storageTypes
argument_list|(
name|clusterScheme
operator|.
name|storageTypes
argument_list|)
operator|.
name|storageCapacities
argument_list|(
name|clusterScheme
operator|.
name|storageCapacities
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
DECL|method|shutdownCluster ()
name|void
name|shutdownCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|dfs
argument_list|)
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Create files/directories and set their storage policies according to the      * corresponding scheme.      */
DECL|method|prepareNamespace ()
name|void
name|prepareNamespace
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Path
name|file
range|:
name|nsScheme
operator|.
name|files
control|)
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|file
argument_list|,
name|BLOCK_SIZE
operator|*
literal|2
argument_list|,
name|clusterScheme
operator|.
name|repl
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|nsScheme
operator|.
name|snapshotMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|snapshot
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|SnapshotTestHelper
operator|.
name|createSnapshot
argument_list|(
name|dfs
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|BlockStoragePolicy
argument_list|>
name|entry
range|:
name|nsScheme
operator|.
name|policyMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|dfs
operator|.
name|setStoragePolicy
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Run the migration tool.      */
DECL|method|migrate (String... args)
name|void
name|migrate
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|runMover
argument_list|()
expr_stmt|;
block|}
comment|/**      * Verify block locations after running the migration tool.      */
DECL|method|verify (boolean verifyAll)
name|void
name|verify
parameter_list|(
name|boolean
name|verifyAll
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|verifyAll
condition|)
block|{
name|verifyNamespace
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// TODO verify according to the given path list
block|}
block|}
DECL|method|runMover ()
specifier|private
name|void
name|runMover
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
argument_list|<
name|URI
argument_list|>
name|namenodes
init|=
name|DFSUtil
operator|.
name|getNsServiceRpcUris
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|int
name|result
init|=
name|Mover
operator|.
name|run
argument_list|(
name|namenodes
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ExitStatus
operator|.
name|SUCCESS
operator|.
name|getExitCode
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyNamespace ()
specifier|private
name|void
name|verifyNamespace
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsFileStatus
name|status
init|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getFileInfo
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|verifyRecursively
argument_list|(
literal|null
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyRecursively (final Path parent, final HdfsFileStatus status)
specifier|private
name|void
name|verifyRecursively
parameter_list|(
specifier|final
name|Path
name|parent
parameter_list|,
specifier|final
name|HdfsFileStatus
name|status
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|status
operator|.
name|isDir
argument_list|()
condition|)
block|{
name|Path
name|fullPath
init|=
name|parent
operator|==
literal|null
condition|?
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
else|:
name|status
operator|.
name|getFullPath
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|DirectoryListing
name|children
init|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|listPaths
argument_list|(
name|fullPath
operator|.
name|toString
argument_list|()
argument_list|,
name|HdfsFileStatus
operator|.
name|EMPTY_NAME
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|HdfsFileStatus
name|child
range|:
name|children
operator|.
name|getPartialListing
argument_list|()
control|)
block|{
name|verifyRecursively
argument_list|(
name|fullPath
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|status
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
comment|// is file
name|HdfsLocatedFileStatus
name|fileStatus
init|=
operator|(
name|HdfsLocatedFileStatus
operator|)
name|status
decl_stmt|;
name|byte
name|policyId
init|=
name|fileStatus
operator|.
name|getStoragePolicy
argument_list|()
decl_stmt|;
name|BlockStoragePolicy
name|policy
init|=
name|policies
operator|.
name|getPolicy
argument_list|(
name|policyId
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|StorageType
argument_list|>
name|types
init|=
name|policy
operator|.
name|chooseStorageTypes
argument_list|(
name|status
operator|.
name|getReplication
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|lb
range|:
name|fileStatus
operator|.
name|getBlockLocations
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
specifier|final
name|Mover
operator|.
name|StorageTypeDiff
name|diff
init|=
operator|new
name|Mover
operator|.
name|StorageTypeDiff
argument_list|(
name|types
argument_list|,
name|lb
operator|.
name|getStorageTypes
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|diff
operator|.
name|removeOverlap
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|genStorageTypes (int numDataNodes)
specifier|private
specifier|static
name|StorageType
index|[]
index|[]
name|genStorageTypes
parameter_list|(
name|int
name|numDataNodes
parameter_list|)
block|{
name|StorageType
index|[]
index|[]
name|types
init|=
operator|new
name|StorageType
index|[
name|numDataNodes
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
expr_stmt|;
block|}
return|return
name|types
return|;
block|}
DECL|method|runTest (MigrationTest test)
specifier|private
name|void
name|runTest
parameter_list|(
name|MigrationTest
name|test
parameter_list|)
throws|throws
name|Exception
block|{
name|test
operator|.
name|setupCluster
argument_list|()
expr_stmt|;
try|try
block|{
name|test
operator|.
name|prepareNamespace
argument_list|()
expr_stmt|;
name|test
operator|.
name|migrate
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// let the NN finish deletion
name|test
operator|.
name|verify
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|test
operator|.
name|shutdownCluster
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * A normal case for Mover: move a file into archival storage    */
annotation|@
name|Test
DECL|method|testMigrateFileToArchival ()
specifier|public
name|void
name|testMigrateFileToArchival
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Path
argument_list|,
name|BlockStoragePolicy
argument_list|>
name|policyMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|put
argument_list|(
name|foo
argument_list|,
name|COLD
argument_list|)
expr_stmt|;
name|NamespaceScheme
name|nsScheme
init|=
operator|new
name|NamespaceScheme
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|foo
argument_list|)
argument_list|,
literal|null
argument_list|,
name|policyMap
argument_list|)
decl_stmt|;
name|ClusterScheme
name|clusterScheme
init|=
operator|new
name|ClusterScheme
argument_list|(
name|DEFAULT_CONF
argument_list|,
name|NUM_DATANODES
argument_list|,
name|REPL
argument_list|,
name|genStorageTypes
argument_list|(
name|NUM_DATANODES
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|MigrationTest
name|test
init|=
operator|new
name|MigrationTest
argument_list|(
name|clusterScheme
argument_list|,
name|nsScheme
argument_list|)
decl_stmt|;
name|runTest
argument_list|(
name|test
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

