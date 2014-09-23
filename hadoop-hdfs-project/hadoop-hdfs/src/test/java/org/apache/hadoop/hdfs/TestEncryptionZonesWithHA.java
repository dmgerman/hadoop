begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|crypto
operator|.
name|key
operator|.
name|JavaKeyStoreProvider
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderCryptoExtension
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
name|permission
operator|.
name|FsPermission
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
name|FileSystemTestHelper
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
name|namenode
operator|.
name|ha
operator|.
name|HATestUtil
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
name|client
operator|.
name|HdfsAdmin
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

begin_comment
comment|/**  * Tests interaction of encryption zones with HA failover.  */
end_comment

begin_class
DECL|class|TestEncryptionZonesWithHA
specifier|public
class|class
name|TestEncryptionZonesWithHA
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|nn0
specifier|private
name|NameNode
name|nn0
decl_stmt|;
DECL|field|nn1
specifier|private
name|NameNode
name|nn1
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|dfsAdmin0
specifier|private
name|HdfsAdmin
name|dfsAdmin0
decl_stmt|;
DECL|field|dfsAdmin1
specifier|private
name|HdfsAdmin
name|dfsAdmin1
decl_stmt|;
DECL|field|fsHelper
specifier|private
name|FileSystemTestHelper
name|fsHelper
decl_stmt|;
DECL|field|testRootDir
specifier|private
name|File
name|testRootDir
decl_stmt|;
DECL|field|TEST_KEY
specifier|private
specifier|final
name|String
name|TEST_KEY
init|=
literal|"testKey"
decl_stmt|;
annotation|@
name|Before
DECL|method|setupCluster ()
specifier|public
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_PERIOD_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|HAUtil
operator|.
name|setAllowStandbyReads
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fsHelper
operator|=
operator|new
name|FileSystemTestHelper
argument_list|()
expr_stmt|;
name|String
name|testRoot
init|=
name|fsHelper
operator|.
name|getTestRootDir
argument_list|()
decl_stmt|;
name|testRootDir
operator|=
operator|new
name|File
argument_list|(
name|testRoot
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_ENCRYPTION_KEY_PROVIDER_URI
argument_list|,
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://file"
operator|+
operator|new
name|Path
argument_list|(
name|testRootDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test.jks"
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
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
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
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
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createKey
argument_list|(
name|TEST_KEY
argument_list|,
name|cluster
argument_list|,
literal|0
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createKey
argument_list|(
name|TEST_KEY
argument_list|,
name|cluster
argument_list|,
literal|1
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|nn0
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|nn1
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|dfsAdmin0
operator|=
operator|new
name|HdfsAdmin
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|(
literal|0
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|dfsAdmin1
operator|=
operator|new
name|HdfsAdmin
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|(
literal|1
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|KeyProviderCryptoExtension
name|nn0Provider
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getProvider
argument_list|()
decl_stmt|;
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|provider
operator|=
name|nn0Provider
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdownCluster ()
specifier|public
name|void
name|shutdownCluster
parameter_list|()
throws|throws
name|IOException
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
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that encryption zones are properly tracked by the standby.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testEncryptionZonesTrackedOnStandby ()
specifier|public
name|void
name|testEncryptionZonesTrackedOnStandby
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|len
init|=
literal|8196
decl_stmt|;
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/enc"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|dirChild
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"child"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|dirFile
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|dir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
name|dfsAdmin0
operator|.
name|createEncryptionZone
argument_list|(
name|dir
argument_list|,
name|TEST_KEY
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|dirChild
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|dirFile
argument_list|,
name|len
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xFEED
argument_list|)
expr_stmt|;
name|String
name|contents
init|=
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|dirFile
argument_list|)
decl_stmt|;
comment|// Failover the current standby to active.
name|HATestUtil
operator|.
name|waitForStandbyToCatchUp
argument_list|(
name|nn0
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Got unexpected ez path"
argument_list|,
name|dir
operator|.
name|toString
argument_list|()
argument_list|,
name|dfsAdmin1
operator|.
name|getEncryptionZoneForPath
argument_list|(
name|dir
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Got unexpected ez path"
argument_list|,
name|dir
operator|.
name|toString
argument_list|()
argument_list|,
name|dfsAdmin1
operator|.
name|getEncryptionZoneForPath
argument_list|(
name|dirChild
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"File contents after failover were changed"
argument_list|,
name|contents
argument_list|,
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|dirFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

