begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.aliasmap
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
name|aliasmap
package|;
end_package

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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|Optional
import|;
end_import

begin_comment
comment|/**  * ITestInMemoryAliasMap is an integration test that writes and reads to  * an AliasMap. This is an integration test because it can't be run in parallel  * like normal unit tests since there is conflict over the port being in use.  */
end_comment

begin_class
DECL|class|ITestInMemoryAliasMap
specifier|public
class|class
name|ITestInMemoryAliasMap
block|{
DECL|field|aliasMap
specifier|private
name|InMemoryAliasMap
name|aliasMap
decl_stmt|;
DECL|field|tempDirectory
specifier|private
name|File
name|tempDirectory
decl_stmt|;
DECL|field|bpid
specifier|private
specifier|static
name|String
name|bpid
init|=
literal|"bpid-0"
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|File
name|temp
init|=
name|Files
operator|.
name|createTempDirectory
argument_list|(
literal|"seagull"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|tempDirectory
operator|=
operator|new
name|File
argument_list|(
name|temp
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|tempDirectory
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
name|temp
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|aliasMap
operator|=
name|InMemoryAliasMap
operator|.
name|init
argument_list|(
name|conf
argument_list|,
name|bpid
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
name|Exception
block|{
name|aliasMap
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|tempDirectory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|readNotFoundReturnsNothing ()
specifier|public
name|void
name|readNotFoundReturnsNothing
parameter_list|()
throws|throws
name|IOException
block|{
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
name|Optional
argument_list|<
name|ProvidedStorageLocation
argument_list|>
name|actualProvidedStorageLocationOpt
init|=
name|aliasMap
operator|.
name|read
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|actualProvidedStorageLocationOpt
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|readWrite ()
specifier|public
name|void
name|readWrite
parameter_list|()
throws|throws
name|Exception
block|{
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
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"eagle"
argument_list|,
literal|"mouse"
argument_list|)
decl_stmt|;
name|long
name|offset
init|=
literal|47
decl_stmt|;
name|long
name|length
init|=
literal|48
decl_stmt|;
name|int
name|nonceSize
init|=
literal|4
decl_stmt|;
name|byte
index|[]
name|nonce
init|=
operator|new
name|byte
index|[
name|nonceSize
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|nonce
argument_list|,
literal|0
argument_list|,
operator|(
name|nonceSize
operator|-
literal|1
operator|)
argument_list|,
name|Byte
operator|.
name|parseByte
argument_list|(
literal|"0011"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|ProvidedStorageLocation
name|expectedProvidedStorageLocation
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
name|path
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|nonce
argument_list|)
decl_stmt|;
name|aliasMap
operator|.
name|write
argument_list|(
name|block
argument_list|,
name|expectedProvidedStorageLocation
argument_list|)
expr_stmt|;
name|Optional
argument_list|<
name|ProvidedStorageLocation
argument_list|>
name|actualProvidedStorageLocationOpt
init|=
name|aliasMap
operator|.
name|read
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|actualProvidedStorageLocationOpt
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedProvidedStorageLocation
argument_list|,
name|actualProvidedStorageLocationOpt
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|list ()
specifier|public
name|void
name|list
parameter_list|()
throws|throws
name|IOException
block|{
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
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"eagle"
argument_list|,
literal|"mouse"
argument_list|)
decl_stmt|;
name|int
name|nonceSize
init|=
literal|4
decl_stmt|;
name|byte
index|[]
name|nonce
init|=
operator|new
name|byte
index|[
name|nonceSize
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|nonce
argument_list|,
literal|0
argument_list|,
operator|(
name|nonceSize
operator|-
literal|1
operator|)
argument_list|,
name|Byte
operator|.
name|parseByte
argument_list|(
literal|"0011"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|ProvidedStorageLocation
name|expectedProvidedStorageLocation1
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
name|path
argument_list|,
literal|47
argument_list|,
literal|48
argument_list|,
name|nonce
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|expectedProvidedStorageLocation2
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
name|path
argument_list|,
literal|48
argument_list|,
literal|49
argument_list|,
name|nonce
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|expectedProvidedStorageLocation3
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
name|path
argument_list|,
literal|49
argument_list|,
literal|50
argument_list|,
name|nonce
argument_list|)
decl_stmt|;
name|aliasMap
operator|.
name|write
argument_list|(
name|block1
argument_list|,
name|expectedProvidedStorageLocation1
argument_list|)
expr_stmt|;
name|aliasMap
operator|.
name|write
argument_list|(
name|block2
argument_list|,
name|expectedProvidedStorageLocation2
argument_list|)
expr_stmt|;
name|aliasMap
operator|.
name|write
argument_list|(
name|block3
argument_list|,
name|expectedProvidedStorageLocation3
argument_list|)
expr_stmt|;
name|InMemoryAliasMap
operator|.
name|IterationResult
name|list
init|=
name|aliasMap
operator|.
name|list
argument_list|(
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
decl_stmt|;
comment|// we should have 3 results
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|list
operator|.
name|getFileRegions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// no more results expected
name|assertFalse
argument_list|(
name|list
operator|.
name|getNextBlock
argument_list|()
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSnapshot ()
specifier|public
name|void
name|testSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|Block
name|block1
init|=
operator|new
name|Block
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|Block
name|block2
init|=
operator|new
name|Block
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"users"
argument_list|,
literal|"alice"
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|remoteLocation
init|=
operator|new
name|ProvidedStorageLocation
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// write the first block
name|aliasMap
operator|.
name|write
argument_list|(
name|block1
argument_list|,
name|remoteLocation
argument_list|)
expr_stmt|;
comment|// create snapshot
name|File
name|snapshotFile
init|=
name|InMemoryAliasMap
operator|.
name|createSnapshot
argument_list|(
name|aliasMap
argument_list|)
decl_stmt|;
comment|// write the 2nd block after the snapshot
name|aliasMap
operator|.
name|write
argument_list|(
name|block2
argument_list|,
name|remoteLocation
argument_list|)
expr_stmt|;
comment|// creata a new aliasmap object from the snapshot
name|InMemoryAliasMap
name|snapshotAliasMap
init|=
literal|null
decl_stmt|;
name|Configuration
name|newConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|newConf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR
argument_list|,
name|snapshotFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|snapshotAliasMap
operator|=
name|InMemoryAliasMap
operator|.
name|init
argument_list|(
name|newConf
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
comment|// now the snapshot should have the first block but not the second one.
name|assertTrue
argument_list|(
name|snapshotAliasMap
operator|.
name|read
argument_list|(
name|block1
argument_list|)
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|snapshotAliasMap
operator|.
name|read
argument_list|(
name|block2
argument_list|)
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|snapshotAliasMap
operator|!=
literal|null
condition|)
block|{
name|snapshotAliasMap
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

