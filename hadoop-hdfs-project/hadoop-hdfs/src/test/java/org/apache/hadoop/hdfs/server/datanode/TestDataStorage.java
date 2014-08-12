begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|common
operator|.
name|Storage
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
name|protocol
operator|.
name|NamespaceInfo
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
name|Mockito
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

begin_class
DECL|class|TestDataStorage
specifier|public
class|class
name|TestDataStorage
block|{
DECL|field|DEFAULT_BPID
specifier|private
specifier|final
specifier|static
name|String
name|DEFAULT_BPID
init|=
literal|"bp-0"
decl_stmt|;
DECL|field|CLUSTER_ID
specifier|private
specifier|final
specifier|static
name|String
name|CLUSTER_ID
init|=
literal|"cluster0"
decl_stmt|;
DECL|field|BUILD_VERSION
specifier|private
specifier|final
specifier|static
name|String
name|BUILD_VERSION
init|=
literal|"2.0"
decl_stmt|;
DECL|field|SOFTWARE_VERSION
specifier|private
specifier|final
specifier|static
name|String
name|SOFTWARE_VERSION
init|=
literal|"2.0"
decl_stmt|;
DECL|field|CTIME
specifier|private
specifier|final
specifier|static
name|long
name|CTIME
init|=
literal|1
decl_stmt|;
DECL|field|TEST_DIR
specifier|private
specifier|final
specifier|static
name|File
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
operator|+
literal|"/dstest"
argument_list|)
decl_stmt|;
DECL|field|START_OPT
specifier|private
specifier|final
specifier|static
name|StartupOption
name|START_OPT
init|=
name|StartupOption
operator|.
name|REGULAR
decl_stmt|;
DECL|field|mockDN
specifier|private
name|DataNode
name|mockDN
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DataNode
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nsInfo
specifier|private
name|NamespaceInfo
name|nsInfo
decl_stmt|;
DECL|field|storage
specifier|private
name|DataStorage
name|storage
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|storage
operator|=
operator|new
name|DataStorage
argument_list|()
expr_stmt|;
name|nsInfo
operator|=
operator|new
name|NamespaceInfo
argument_list|(
literal|0
argument_list|,
name|CLUSTER_ID
argument_list|,
name|DEFAULT_BPID
argument_list|,
name|CTIME
argument_list|,
name|BUILD_VERSION
argument_list|,
name|SOFTWARE_VERSION
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_DIR
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to make test dir."
argument_list|,
name|TEST_DIR
operator|.
name|mkdirs
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
name|IOException
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_DIR
argument_list|)
expr_stmt|;
block|}
DECL|method|createStorageLocations (int numLocs)
specifier|private
specifier|static
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|createStorageLocations
parameter_list|(
name|int
name|numLocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createStorageLocations
argument_list|(
name|numLocs
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Create a list of StorageLocations.    * If asFile sets to true, create StorageLocation as regular files, otherwise    * create directories for each location.    * @param numLocs the total number of StorageLocations to be created.    * @param asFile set to true to create as file.    * @return a list of StorageLocations.    */
DECL|method|createStorageLocations ( int numLocs, boolean asFile)
specifier|private
specifier|static
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|createStorageLocations
parameter_list|(
name|int
name|numLocs
parameter_list|,
name|boolean
name|asFile
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
operator|new
name|ArrayList
argument_list|<
name|StorageLocation
argument_list|>
argument_list|()
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
name|numLocs
condition|;
name|i
operator|++
control|)
block|{
name|String
name|uri
init|=
name|TEST_DIR
operator|+
literal|"/data"
operator|+
name|i
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|asFile
condition|)
block|{
name|file
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|file
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|StorageLocation
name|loc
init|=
name|StorageLocation
operator|.
name|parse
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|locations
operator|.
name|add
argument_list|(
name|loc
argument_list|)
expr_stmt|;
block|}
return|return
name|locations
return|;
block|}
DECL|method|createNamespaceInfos (int num)
specifier|private
specifier|static
name|List
argument_list|<
name|NamespaceInfo
argument_list|>
name|createNamespaceInfos
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|List
argument_list|<
name|NamespaceInfo
argument_list|>
name|nsInfos
init|=
operator|new
name|ArrayList
argument_list|<
name|NamespaceInfo
argument_list|>
argument_list|()
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|bpid
init|=
literal|"bp-"
operator|+
name|i
decl_stmt|;
name|nsInfos
operator|.
name|add
argument_list|(
operator|new
name|NamespaceInfo
argument_list|(
literal|0
argument_list|,
name|CLUSTER_ID
argument_list|,
name|bpid
argument_list|,
name|CTIME
argument_list|,
name|BUILD_VERSION
argument_list|,
name|SOFTWARE_VERSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nsInfos
return|;
block|}
comment|/** Check whether the path is a valid DataNode data directory. */
DECL|method|checkDir (File dataDir)
specifier|private
specifier|static
name|void
name|checkDir
parameter_list|(
name|File
name|dataDir
parameter_list|)
block|{
name|Storage
operator|.
name|StorageDirectory
name|sd
init|=
operator|new
name|Storage
operator|.
name|StorageDirectory
argument_list|(
name|dataDir
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|sd
operator|.
name|getRoot
argument_list|()
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sd
operator|.
name|getCurrentDir
argument_list|()
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sd
operator|.
name|getVersionFile
argument_list|()
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Check whether the root is a valid BlockPoolSlice storage. */
DECL|method|checkDir (File root, String bpid)
specifier|private
specifier|static
name|void
name|checkDir
parameter_list|(
name|File
name|root
parameter_list|,
name|String
name|bpid
parameter_list|)
block|{
name|Storage
operator|.
name|StorageDirectory
name|sd
init|=
operator|new
name|Storage
operator|.
name|StorageDirectory
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|File
name|bpRoot
init|=
operator|new
name|File
argument_list|(
name|sd
operator|.
name|getCurrentDir
argument_list|()
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|Storage
operator|.
name|StorageDirectory
name|bpSd
init|=
operator|new
name|Storage
operator|.
name|StorageDirectory
argument_list|(
name|bpRoot
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bpSd
operator|.
name|getRoot
argument_list|()
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bpSd
operator|.
name|getCurrentDir
argument_list|()
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bpSd
operator|.
name|getVersionFile
argument_list|()
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddStorageDirectories ()
specifier|public
name|void
name|testAddStorageDirectories
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
specifier|final
name|int
name|numLocations
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|numNamespace
init|=
literal|3
decl_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
name|createStorageLocations
argument_list|(
name|numLocations
argument_list|)
decl_stmt|;
comment|// Add volumes for multiple namespaces.
name|List
argument_list|<
name|NamespaceInfo
argument_list|>
name|namespaceInfos
init|=
name|createNamespaceInfos
argument_list|(
name|numNamespace
argument_list|)
decl_stmt|;
for|for
control|(
name|NamespaceInfo
name|ni
range|:
name|namespaceInfos
control|)
block|{
name|storage
operator|.
name|addStorageLocations
argument_list|(
name|mockDN
argument_list|,
name|ni
argument_list|,
name|locations
argument_list|,
name|START_OPT
argument_list|)
expr_stmt|;
for|for
control|(
name|StorageLocation
name|sl
range|:
name|locations
control|)
block|{
name|checkDir
argument_list|(
name|sl
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
name|checkDir
argument_list|(
name|sl
operator|.
name|getFile
argument_list|()
argument_list|,
name|ni
operator|.
name|getBlockPoolID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|numLocations
argument_list|,
name|storage
operator|.
name|getNumStorageDirs
argument_list|()
argument_list|)
expr_stmt|;
name|locations
operator|=
name|createStorageLocations
argument_list|(
name|numLocations
argument_list|)
expr_stmt|;
try|try
block|{
name|storage
operator|.
name|addStorageLocations
argument_list|(
name|mockDN
argument_list|,
name|namespaceInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|locations
argument_list|,
name|START_OPT
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected to throw IOException: adding active directories."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"All specified directories are not accessible or do not exist."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// The number of active storage dirs has not changed, since it tries to
comment|// add the storage dirs that are under service.
name|assertEquals
argument_list|(
name|numLocations
argument_list|,
name|storage
operator|.
name|getNumStorageDirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add more directories.
name|locations
operator|=
name|createStorageLocations
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|storage
operator|.
name|addStorageLocations
argument_list|(
name|mockDN
argument_list|,
name|nsInfo
argument_list|,
name|locations
argument_list|,
name|START_OPT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|storage
operator|.
name|getNumStorageDirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRecoverTransitionReadFailure ()
specifier|public
name|void
name|testRecoverTransitionReadFailure
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numLocations
init|=
literal|3
decl_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
name|createStorageLocations
argument_list|(
name|numLocations
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|storage
operator|.
name|recoverTransitionRead
argument_list|(
name|mockDN
argument_list|,
name|nsInfo
argument_list|,
name|locations
argument_list|,
name|START_OPT
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"An IOException should throw: all StorageLocations are NON_EXISTENT"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"All specified directories are not accessible or do not exist."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|storage
operator|.
name|getNumStorageDirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test enforces the behavior that if there is an exception from    * doTransition() during DN starts up, the storage directories that have    * already been processed are still visible, i.e., in    * DataStorage.storageDirs().    */
annotation|@
name|Test
DECL|method|testRecoverTransitionReadDoTransitionFailure ()
specifier|public
name|void
name|testRecoverTransitionReadDoTransitionFailure
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numLocations
init|=
literal|3
decl_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
name|createStorageLocations
argument_list|(
name|numLocations
argument_list|)
decl_stmt|;
name|String
name|bpid
init|=
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
decl_stmt|;
comment|// Prepare volumes
name|storage
operator|.
name|recoverTransitionRead
argument_list|(
name|mockDN
argument_list|,
name|bpid
argument_list|,
name|nsInfo
argument_list|,
name|locations
argument_list|,
name|START_OPT
argument_list|)
expr_stmt|;
comment|// Reset DataStorage
name|storage
operator|.
name|unlockAll
argument_list|()
expr_stmt|;
name|storage
operator|=
operator|new
name|DataStorage
argument_list|()
expr_stmt|;
comment|// Trigger an exception from doTransition().
name|nsInfo
operator|.
name|clusterID
operator|=
literal|"cluster1"
expr_stmt|;
try|try
block|{
name|storage
operator|.
name|recoverTransitionRead
argument_list|(
name|mockDN
argument_list|,
name|bpid
argument_list|,
name|nsInfo
argument_list|,
name|locations
argument_list|,
name|START_OPT
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expect to throw an exception from doTransition()"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Incompatible clusterIDs"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|numLocations
argument_list|,
name|storage
operator|.
name|getNumStorageDirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

