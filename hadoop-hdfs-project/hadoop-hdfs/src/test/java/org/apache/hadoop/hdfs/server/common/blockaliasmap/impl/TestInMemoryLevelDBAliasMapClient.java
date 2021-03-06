begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common.blockaliasmap.impl
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
name|common
operator|.
name|blockaliasmap
operator|.
name|impl
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
name|collect
operator|.
name|Lists
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
name|io
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|DFSConfigKeys
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
name|Block
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
name|ProvidedStorageLocation
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
name|aliasmap
operator|.
name|InMemoryAliasMap
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
name|aliasmap
operator|.
name|InMemoryLevelDBAliasMapServer
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
name|blockaliasmap
operator|.
name|BlockAliasMap
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
name|FileRegion
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
name|LambdaTestUtils
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SERVICE_RPC_BIND_HOST_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
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
name|assertArrayEquals
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
name|Optional
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
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Tests the {@link InMemoryLevelDBAliasMapClient}.  */
end_comment

begin_class
DECL|class|TestInMemoryLevelDBAliasMapClient
specifier|public
class|class
name|TestInMemoryLevelDBAliasMapClient
block|{
DECL|field|levelDBAliasMapServer
specifier|private
name|InMemoryLevelDBAliasMapServer
name|levelDBAliasMapServer
decl_stmt|;
DECL|field|inMemoryLevelDBAliasMapClient
specifier|private
name|InMemoryLevelDBAliasMapClient
name|inMemoryLevelDBAliasMapClient
decl_stmt|;
DECL|field|tempDir
specifier|private
name|File
name|tempDir
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|BPID
specifier|private
specifier|final
specifier|static
name|String
name|BPID
init|=
literal|"BPID-0"
decl_stmt|;
annotation|@
name|Rule
DECL|field|exception
specifier|public
specifier|final
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
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
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|int
name|port
init|=
literal|9876
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ALIASMAP_INMEMORY_RPC_ADDRESS
argument_list|,
literal|"localhost:"
operator|+
name|port
argument_list|)
expr_stmt|;
name|tempDir
operator|=
name|Files
operator|.
name|createTempDir
argument_list|()
expr_stmt|;
name|File
name|levelDBDir
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
name|levelDBDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR
argument_list|,
name|tempDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|levelDBAliasMapServer
operator|=
operator|new
name|InMemoryLevelDBAliasMapServer
argument_list|(
name|InMemoryAliasMap
operator|::
name|init
argument_list|,
name|BPID
argument_list|)
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|=
operator|new
name|InMemoryLevelDBAliasMapClient
argument_list|()
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
name|levelDBAliasMapServer
operator|.
name|close
argument_list|()
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|writeRead ()
specifier|public
name|void
name|writeRead
parameter_list|()
throws|throws
name|Exception
block|{
name|levelDBAliasMapServer
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|levelDBAliasMapServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
literal|42
argument_list|,
literal|43
argument_list|,
literal|44
argument_list|)
decl_stmt|;
name|byte
index|[]
name|nonce
init|=
literal|"blackbird"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
operator|new
name|Path
argument_list|(
literal|"cuckoo"
argument_list|)
argument_list|,
literal|45
argument_list|,
literal|46
argument_list|,
name|nonce
argument_list|)
decl_stmt|;
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|writer
init|=
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
name|writer
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block
argument_list|,
name|providedStorageLocation
argument_list|)
argument_list|)
expr_stmt|;
name|BlockAliasMap
operator|.
name|Reader
argument_list|<
name|FileRegion
argument_list|>
name|reader
init|=
name|inMemoryLevelDBAliasMapClient
operator|.
name|getReader
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|FileRegion
argument_list|>
name|fileRegion
init|=
name|reader
operator|.
name|resolve
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block
argument_list|,
name|providedStorageLocation
argument_list|)
argument_list|,
name|fileRegion
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|iterateSingleBatch ()
specifier|public
name|void
name|iterateSingleBatch
parameter_list|()
throws|throws
name|Exception
block|{
name|levelDBAliasMapServer
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|levelDBAliasMapServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Block
name|block1
init|=
operator|new
name|Block
argument_list|(
literal|42
argument_list|,
literal|43
argument_list|,
literal|44
argument_list|)
decl_stmt|;
name|Block
name|block2
init|=
operator|new
name|Block
argument_list|(
literal|43
argument_list|,
literal|44
argument_list|,
literal|45
argument_list|)
decl_stmt|;
name|byte
index|[]
name|nonce1
init|=
literal|"blackbird"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|nonce2
init|=
literal|"cuckoo"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation1
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
operator|new
name|Path
argument_list|(
literal|"eagle"
argument_list|)
argument_list|,
literal|46
argument_list|,
literal|47
argument_list|,
name|nonce1
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation2
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
operator|new
name|Path
argument_list|(
literal|"falcon"
argument_list|)
argument_list|,
literal|46
argument_list|,
literal|47
argument_list|,
name|nonce2
argument_list|)
decl_stmt|;
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|writer1
init|=
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
name|writer1
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block1
argument_list|,
name|providedStorageLocation1
argument_list|)
argument_list|)
expr_stmt|;
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|writer2
init|=
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
name|writer2
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block2
argument_list|,
name|providedStorageLocation2
argument_list|)
argument_list|)
expr_stmt|;
name|BlockAliasMap
operator|.
name|Reader
argument_list|<
name|FileRegion
argument_list|>
name|reader
init|=
name|inMemoryLevelDBAliasMapClient
operator|.
name|getReader
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FileRegion
argument_list|>
name|actualFileRegions
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|FileRegion
name|fileRegion
range|:
name|reader
control|)
block|{
name|actualFileRegions
operator|.
name|add
argument_list|(
name|fileRegion
argument_list|)
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
operator|new
name|FileRegion
index|[]
block|{
operator|new
name|FileRegion
argument_list|(
name|block1
argument_list|,
name|providedStorageLocation1
argument_list|)
block|,
operator|new
name|FileRegion
argument_list|(
name|block2
argument_list|,
name|providedStorageLocation2
argument_list|)
block|}
argument_list|,
name|actualFileRegions
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|iterateThreeBatches ()
specifier|public
name|void
name|iterateThreeBatches
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ALIASMAP_INMEMORY_BATCH_SIZE
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|levelDBAliasMapServer
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|levelDBAliasMapServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Block
name|block1
init|=
operator|new
name|Block
argument_list|(
literal|42
argument_list|,
literal|43
argument_list|,
literal|44
argument_list|)
decl_stmt|;
name|Block
name|block2
init|=
operator|new
name|Block
argument_list|(
literal|43
argument_list|,
literal|44
argument_list|,
literal|45
argument_list|)
decl_stmt|;
name|Block
name|block3
init|=
operator|new
name|Block
argument_list|(
literal|44
argument_list|,
literal|45
argument_list|,
literal|46
argument_list|)
decl_stmt|;
name|Block
name|block4
init|=
operator|new
name|Block
argument_list|(
literal|47
argument_list|,
literal|48
argument_list|,
literal|49
argument_list|)
decl_stmt|;
name|Block
name|block5
init|=
operator|new
name|Block
argument_list|(
literal|50
argument_list|,
literal|51
argument_list|,
literal|52
argument_list|)
decl_stmt|;
name|Block
name|block6
init|=
operator|new
name|Block
argument_list|(
literal|53
argument_list|,
literal|54
argument_list|,
literal|55
argument_list|)
decl_stmt|;
name|byte
index|[]
name|nonce1
init|=
literal|"blackbird"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|nonce2
init|=
literal|"cuckoo"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|nonce3
init|=
literal|"sparrow"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|nonce4
init|=
literal|"magpie"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|nonce5
init|=
literal|"seagull"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|nonce6
init|=
literal|"finch"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation1
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
operator|new
name|Path
argument_list|(
literal|"eagle"
argument_list|)
argument_list|,
literal|46
argument_list|,
literal|47
argument_list|,
name|nonce1
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation2
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
operator|new
name|Path
argument_list|(
literal|"falcon"
argument_list|)
argument_list|,
literal|48
argument_list|,
literal|49
argument_list|,
name|nonce2
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation3
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
operator|new
name|Path
argument_list|(
literal|"robin"
argument_list|)
argument_list|,
literal|50
argument_list|,
literal|51
argument_list|,
name|nonce3
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation4
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
operator|new
name|Path
argument_list|(
literal|"parakeet"
argument_list|)
argument_list|,
literal|52
argument_list|,
literal|53
argument_list|,
name|nonce4
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation5
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
operator|new
name|Path
argument_list|(
literal|"heron"
argument_list|)
argument_list|,
literal|54
argument_list|,
literal|55
argument_list|,
name|nonce5
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation6
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
operator|new
name|Path
argument_list|(
literal|"duck"
argument_list|)
argument_list|,
literal|56
argument_list|,
literal|57
argument_list|,
name|nonce6
argument_list|)
decl_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block1
argument_list|,
name|providedStorageLocation1
argument_list|)
argument_list|)
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block2
argument_list|,
name|providedStorageLocation2
argument_list|)
argument_list|)
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block3
argument_list|,
name|providedStorageLocation3
argument_list|)
argument_list|)
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block4
argument_list|,
name|providedStorageLocation4
argument_list|)
argument_list|)
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block5
argument_list|,
name|providedStorageLocation5
argument_list|)
argument_list|)
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block6
argument_list|,
name|providedStorageLocation6
argument_list|)
argument_list|)
expr_stmt|;
name|BlockAliasMap
operator|.
name|Reader
argument_list|<
name|FileRegion
argument_list|>
name|reader
init|=
name|inMemoryLevelDBAliasMapClient
operator|.
name|getReader
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FileRegion
argument_list|>
name|actualFileRegions
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|6
argument_list|)
decl_stmt|;
for|for
control|(
name|FileRegion
name|fileRegion
range|:
name|reader
control|)
block|{
name|actualFileRegions
operator|.
name|add
argument_list|(
name|fileRegion
argument_list|)
expr_stmt|;
block|}
name|FileRegion
index|[]
name|expectedFileRegions
init|=
operator|new
name|FileRegion
index|[]
block|{
operator|new
name|FileRegion
argument_list|(
name|block1
argument_list|,
name|providedStorageLocation1
argument_list|)
block|,
operator|new
name|FileRegion
argument_list|(
name|block2
argument_list|,
name|providedStorageLocation2
argument_list|)
block|,
operator|new
name|FileRegion
argument_list|(
name|block3
argument_list|,
name|providedStorageLocation3
argument_list|)
block|,
operator|new
name|FileRegion
argument_list|(
name|block4
argument_list|,
name|providedStorageLocation4
argument_list|)
block|,
operator|new
name|FileRegion
argument_list|(
name|block5
argument_list|,
name|providedStorageLocation5
argument_list|)
block|,
operator|new
name|FileRegion
argument_list|(
name|block6
argument_list|,
name|providedStorageLocation6
argument_list|)
block|}
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedFileRegions
argument_list|,
name|actualFileRegions
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|ReadThread
class|class
name|ReadThread
implements|implements
name|Runnable
block|{
DECL|field|block
specifier|private
specifier|final
name|Block
name|block
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|BlockAliasMap
operator|.
name|Reader
argument_list|<
name|FileRegion
argument_list|>
name|reader
decl_stmt|;
DECL|field|delay
specifier|private
name|int
name|delay
decl_stmt|;
DECL|field|fileRegionOpt
specifier|private
name|Optional
argument_list|<
name|FileRegion
argument_list|>
name|fileRegionOpt
decl_stmt|;
DECL|method|ReadThread (Block block, BlockAliasMap.Reader<FileRegion> reader, int delay)
name|ReadThread
parameter_list|(
name|Block
name|block
parameter_list|,
name|BlockAliasMap
operator|.
name|Reader
argument_list|<
name|FileRegion
argument_list|>
name|reader
parameter_list|,
name|int
name|delay
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|delay
operator|=
name|delay
expr_stmt|;
block|}
DECL|method|getFileRegion ()
specifier|public
name|Optional
argument_list|<
name|FileRegion
argument_list|>
name|getFileRegion
parameter_list|()
block|{
return|return
name|fileRegionOpt
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|delay
argument_list|)
expr_stmt|;
name|fileRegionOpt
operator|=
name|reader
operator|.
name|resolve
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|WriteThread
class|class
name|WriteThread
implements|implements
name|Runnable
block|{
DECL|field|block
specifier|private
specifier|final
name|Block
name|block
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|writer
decl_stmt|;
DECL|field|providedStorageLocation
specifier|private
specifier|final
name|ProvidedStorageLocation
name|providedStorageLocation
decl_stmt|;
DECL|field|delay
specifier|private
name|int
name|delay
decl_stmt|;
DECL|method|WriteThread (Block block, ProvidedStorageLocation providedStorageLocation, BlockAliasMap.Writer<FileRegion> writer, int delay)
name|WriteThread
parameter_list|(
name|Block
name|block
parameter_list|,
name|ProvidedStorageLocation
name|providedStorageLocation
parameter_list|,
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|writer
parameter_list|,
name|int
name|delay
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|providedStorageLocation
operator|=
name|providedStorageLocation
expr_stmt|;
name|this
operator|.
name|delay
operator|=
name|delay
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|delay
argument_list|)
expr_stmt|;
name|writer
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block
argument_list|,
name|providedStorageLocation
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|generateRandomFileRegion (int seed)
specifier|public
name|FileRegion
name|generateRandomFileRegion
parameter_list|(
name|int
name|seed
parameter_list|)
block|{
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
name|seed
argument_list|,
name|seed
operator|+
literal|1
argument_list|,
name|seed
operator|+
literal|2
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"koekoek"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|nonce
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
name|path
argument_list|,
name|seed
operator|+
literal|3
argument_list|,
name|seed
operator|+
literal|4
argument_list|,
name|nonce
argument_list|)
decl_stmt|;
return|return
operator|new
name|FileRegion
argument_list|(
name|block
argument_list|,
name|providedStorageLocation
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|multipleReads ()
specifier|public
name|void
name|multipleReads
parameter_list|()
throws|throws
name|IOException
block|{
name|levelDBAliasMapServer
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|levelDBAliasMapServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|inMemoryLevelDBAliasMapClient
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FileRegion
argument_list|>
name|expectedFileRegions
init|=
name|r
operator|.
name|ints
argument_list|(
literal|0
argument_list|,
literal|200
argument_list|)
operator|.
name|limit
argument_list|(
literal|50
argument_list|)
operator|.
name|boxed
argument_list|()
operator|.
name|map
argument_list|(
name|i
lambda|->
name|generateRandomFileRegion
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|BlockAliasMap
operator|.
name|Reader
argument_list|<
name|FileRegion
argument_list|>
name|reader
init|=
name|inMemoryLevelDBAliasMapClient
operator|.
name|getReader
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|writer
init|=
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ReadThread
argument_list|>
name|readThreads
init|=
name|expectedFileRegions
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|fileRegion
lambda|->
operator|new
name|ReadThread
argument_list|(
name|fileRegion
operator|.
name|getBlock
argument_list|()
argument_list|,
name|reader
argument_list|,
literal|4000
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|Future
argument_list|<
name|?
argument_list|>
argument_list|>
name|readFutures
init|=
name|readThreads
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|readThread
lambda|->
name|executor
operator|.
name|submit
argument_list|(
name|readThread
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|Future
argument_list|<
name|?
argument_list|>
argument_list|>
name|writeFutures
init|=
name|expectedFileRegions
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|fileRegion
lambda|->
operator|new
name|WriteThread
argument_list|(
name|fileRegion
operator|.
name|getBlock
argument_list|()
argument_list|,
name|fileRegion
operator|.
name|getProvidedStorageLocation
argument_list|()
argument_list|,
name|writer
argument_list|,
literal|1000
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|writeThread
lambda|->
name|executor
operator|.
name|submit
argument_list|(
name|writeThread
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|readFutures
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|readFuture
lambda|->
block|{
try|try
block|{
return|return
name|readFuture
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FileRegion
argument_list|>
name|actualFileRegions
init|=
name|readThreads
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|readThread
lambda|->
name|readThread
operator|.
name|getFileRegion
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actualFileRegions
argument_list|)
operator|.
name|containsExactlyInAnyOrder
argument_list|(
name|expectedFileRegions
operator|.
name|toArray
argument_list|(
operator|new
name|FileRegion
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testServerBindHost ()
specifier|public
name|void
name|testServerBindHost
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_SERVICE_RPC_BIND_HOST_KEY
argument_list|,
literal|"0.0.0.0"
argument_list|)
expr_stmt|;
name|writeRead
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNonExistentBlock ()
specifier|public
name|void
name|testNonExistentBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|inMemoryLevelDBAliasMapClient
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|levelDBAliasMapServer
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|levelDBAliasMapServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|Block
name|block1
init|=
operator|new
name|Block
argument_list|(
literal|100
argument_list|,
literal|43
argument_list|,
literal|44
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation1
init|=
literal|null
decl_stmt|;
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|writer1
init|=
name|inMemoryLevelDBAliasMapClient
operator|.
name|getWriter
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
try|try
block|{
name|writer1
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block1
argument_list|,
name|providedStorageLocation1
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail on writing a region with null ProvidedLocation"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BlockAliasMap
operator|.
name|Reader
argument_list|<
name|FileRegion
argument_list|>
name|reader
init|=
name|inMemoryLevelDBAliasMapClient
operator|.
name|getReader
argument_list|(
literal|null
argument_list|,
name|BPID
argument_list|)
decl_stmt|;
name|LambdaTestUtils
operator|.
name|assertOptionalUnset
argument_list|(
literal|"Expected empty BlockAlias"
argument_list|,
name|reader
operator|.
name|resolve
argument_list|(
name|block1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

