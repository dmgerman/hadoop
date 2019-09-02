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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|FSDataInputStream
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|ExtendedBlock
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
name|RandomAccessFile
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

begin_comment
comment|/**  * Tests to ensure that a block is not read successfully from a datanode  * when it has a corrupt metadata file.  */
end_comment

begin_class
DECL|class|TestCorruptMetadataFile
specifier|public
class|class
name|TestCorruptMetadataFile
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|clusterBuilder
specifier|private
name|MiniDFSCluster
operator|.
name|Builder
name|clusterBuilder
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
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
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
comment|// Reduce block acquire retries as we only have 1 DN and it allows the
comment|// test to run faster
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_MAX_BLOCK_ACQUIRE_FAILURES_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|clusterBuilder
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
literal|1
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testReadBlockFailsWhenMetaIsCorrupt ()
specifier|public
name|void
name|testReadBlockFailsWhenMetaIsCorrupt
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|=
name|clusterBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DataNode
name|dn0
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"test.dat"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
decl_stmt|;
name|File
name|metadataFile
init|=
name|cluster
operator|.
name|getBlockMetadataFile
argument_list|(
literal|0
argument_list|,
name|block
argument_list|)
decl_stmt|;
comment|// First ensure we can read the file OK
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now truncate the meta file, and ensure the data is not read OK
name|RandomAccessFile
name|raFile
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|metadataFile
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|raFile
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|intrunc
init|=
name|fs
operator|.
name|open
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|BlockMissingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|intrunc
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|intrunc
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Write 11 bytes to the file, but an invalid header
name|raFile
operator|.
name|write
argument_list|(
literal|"12345678901"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|raFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|ininvalid
init|=
name|fs
operator|.
name|open
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|BlockMissingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|ininvalid
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|ininvalid
operator|.
name|close
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getCorruptBlocks
argument_list|()
operator|==
literal|1
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|raFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * This test create a sample block meta file and then attempts to load it    * using BlockMetadataHeader to ensure it can load a valid file and that it    * throws a CorruptMetaHeaderException when the header is invalid.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testBlockMetaDataHeaderPReadHandlesCorruptMetaFile ()
specifier|public
name|void
name|testBlockMetaDataHeaderPReadHandlesCorruptMetaFile
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|testDir
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|()
decl_stmt|;
name|RandomAccessFile
name|raFile
init|=
operator|new
name|RandomAccessFile
argument_list|(
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"metafile"
argument_list|)
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
comment|// Write a valid header into the file
comment|// Version
name|raFile
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|// Checksum type
name|raFile
operator|.
name|writeByte
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Bytes per checksum
name|raFile
operator|.
name|writeInt
argument_list|(
literal|512
argument_list|)
expr_stmt|;
comment|// We should be able to get the header with no exceptions
name|BlockMetadataHeader
name|header
init|=
name|BlockMetadataHeader
operator|.
name|preadHeader
argument_list|(
name|raFile
operator|.
name|getChannel
argument_list|()
argument_list|)
decl_stmt|;
comment|// Now truncate the meta file to zero and ensure an exception is raised
name|raFile
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|CorruptMetaHeaderException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|BlockMetadataHeader
operator|.
name|preadHeader
argument_list|(
name|raFile
operator|.
name|getChannel
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now write a partial valid header to sure an exception is thrown
comment|// if the header cannot be fully read
comment|// Version
name|raFile
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|// Checksum type
name|raFile
operator|.
name|writeByte
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|CorruptMetaHeaderException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|BlockMetadataHeader
operator|.
name|preadHeader
argument_list|(
name|raFile
operator|.
name|getChannel
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Finally write the expected 7 bytes, but invalid data
name|raFile
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|raFile
operator|.
name|write
argument_list|(
literal|"1234567"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|CorruptMetaHeaderException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|BlockMetadataHeader
operator|.
name|preadHeader
argument_list|(
name|raFile
operator|.
name|getChannel
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|raFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

