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
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|io
operator|.
name|InputStream
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
name|EnumSet
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
name|fs
operator|.
name|StreamCapabilities
operator|.
name|StreamCapability
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
name|HdfsDataOutputStream
operator|.
name|SyncFlag
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
name|DatanodeInfo
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
name|ErasureCodingPolicy
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
name|io
operator|.
name|erasurecode
operator|.
name|CodecUtil
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
name|erasurecode
operator|.
name|ErasureCodeNative
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|NativeRSRawErasureCoderFactory
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
name|log4j
operator|.
name|Level
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
name|Timeout
import|;
end_import

begin_class
DECL|class|TestDFSStripedOutputStream
specifier|public
class|class
name|TestDFSStripedOutputStream
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDFSStripedOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DFSOutputStream
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DataStreamer
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|ecPolicy
specifier|private
name|ErasureCodingPolicy
name|ecPolicy
decl_stmt|;
DECL|field|dataBlocks
specifier|private
name|int
name|dataBlocks
decl_stmt|;
DECL|field|parityBlocks
specifier|private
name|int
name|parityBlocks
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cellSize
specifier|private
name|int
name|cellSize
decl_stmt|;
DECL|field|stripesPerBlock
specifier|private
specifier|final
name|int
name|stripesPerBlock
init|=
literal|4
decl_stmt|;
DECL|field|blockSize
specifier|private
name|int
name|blockSize
decl_stmt|;
annotation|@
name|Rule
DECL|field|globalTimeout
specifier|public
name|Timeout
name|globalTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|method|getEcPolicy ()
specifier|public
name|ErasureCodingPolicy
name|getEcPolicy
parameter_list|()
block|{
return|return
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
return|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
comment|/*      * Initialize erasure coding policy.      */
name|ecPolicy
operator|=
name|getEcPolicy
argument_list|()
expr_stmt|;
name|dataBlocks
operator|=
operator|(
name|short
operator|)
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
expr_stmt|;
name|parityBlocks
operator|=
operator|(
name|short
operator|)
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
expr_stmt|;
name|cellSize
operator|=
name|ecPolicy
operator|.
name|getCellSize
argument_list|()
expr_stmt|;
name|blockSize
operator|=
name|stripesPerBlock
operator|*
name|cellSize
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"EC policy = "
operator|+
name|ecPolicy
argument_list|)
expr_stmt|;
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
decl_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|ErasureCodeNative
operator|.
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|CodecUtil
operator|.
name|IO_ERASURECODE_CODEC_RS_RAWCODERS_KEY
argument_list|,
name|NativeRSRawErasureCoderFactory
operator|.
name|CODER_NAME
argument_list|)
expr_stmt|;
block|}
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
name|numDNs
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|enableAllECPolicies
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|setErasureCodingPolicy
argument_list|(
literal|"/"
argument_list|,
name|ecPolicy
operator|.
name|getName
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
DECL|method|testFileEmpty ()
specifier|public
name|void
name|testFileEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/EmptyFile"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneCell1 ()
specifier|public
name|void
name|testFileSmallerThanOneCell1
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneCell"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneCell2 ()
specifier|public
name|void
name|testFileSmallerThanOneCell2
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneCell"
argument_list|,
name|cellSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileEqualsWithOneCell ()
specifier|public
name|void
name|testFileEqualsWithOneCell
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/EqualsWithOneCell"
argument_list|,
name|cellSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneStripe1 ()
specifier|public
name|void
name|testFileSmallerThanOneStripe1
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneStripe"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneStripe2 ()
specifier|public
name|void
name|testFileSmallerThanOneStripe2
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneStripe"
argument_list|,
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileEqualsWithOneStripe ()
specifier|public
name|void
name|testFileEqualsWithOneStripe
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/EqualsWithOneStripe"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanOneStripe1 ()
specifier|public
name|void
name|testFileMoreThanOneStripe1
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanOneStripe1"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanOneStripe2 ()
specifier|public
name|void
name|testFileMoreThanOneStripe2
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanOneStripe2"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileLessThanFullBlockGroup ()
specifier|public
name|void
name|testFileLessThanFullBlockGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/LessThanFullBlockGroup"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|*
operator|(
name|stripesPerBlock
operator|-
literal|1
operator|)
operator|+
name|cellSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileFullBlockGroup ()
specifier|public
name|void
name|testFileFullBlockGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/FullBlockGroup"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanABlockGroup1 ()
specifier|public
name|void
name|testFileMoreThanABlockGroup1
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanABlockGroup1"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanABlockGroup2 ()
specifier|public
name|void
name|testFileMoreThanABlockGroup2
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanABlockGroup2"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanABlockGroup3 ()
specifier|public
name|void
name|testFileMoreThanABlockGroup3
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanABlockGroup3"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|*
literal|3
operator|+
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@link DFSStripedOutputStream} doesn't support hflush() or hsync() yet.    * This test is to make sure that DFSStripedOutputStream doesn't throw any    * {@link UnsupportedOperationException} on hflush() or hsync() so as to    * comply with output stream spec.    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testStreamFlush ()
specifier|public
name|void
name|testStreamFlush
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|byte
index|[]
name|bytes
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|blockSize
operator|*
name|dataBlocks
operator|*
literal|3
operator|+
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/ec-file-1"
argument_list|)
argument_list|)
init|)
block|{
name|assertFalse
argument_list|(
literal|"DFSStripedOutputStream should not have hflush() capability yet!"
argument_list|,
name|os
operator|.
name|hasCapability
argument_list|(
name|StreamCapability
operator|.
name|HFLUSH
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"DFSStripedOutputStream should not have hsync() capability yet!"
argument_list|,
name|os
operator|.
name|hasCapability
argument_list|(
name|StreamCapability
operator|.
name|HSYNC
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|os
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|os
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|os
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|os
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|os
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"stream is not a DFSStripedOutputStream"
argument_list|,
name|os
operator|.
name|getWrappedStream
argument_list|()
operator|instanceof
name|DFSStripedOutputStream
argument_list|)
expr_stmt|;
specifier|final
name|DFSStripedOutputStream
name|dfssos
init|=
operator|(
name|DFSStripedOutputStream
operator|)
name|os
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
name|dfssos
operator|.
name|hsync
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|SyncFlag
operator|.
name|UPDATE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testOneFile (String src, int writeBytes)
specifier|private
name|void
name|testOneFile
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|writeBytes
parameter_list|)
throws|throws
name|Exception
block|{
name|src
operator|+=
literal|"_"
operator|+
name|writeBytes
expr_stmt|;
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|writeBytes
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
operator|new
name|String
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|waitBlockGroupsReported
argument_list|(
name|fs
argument_list|,
name|src
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|checkData
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
name|writeBytes
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|DatanodeInfo
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
name|blockSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileBlockSizeSmallerThanCellSize ()
specifier|public
name|void
name|testFileBlockSizeSmallerThanCellSize
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"testFileBlockSizeSmallerThanCellSize"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|cellSize
operator|*
literal|2
argument_list|)
decl_stmt|;
try|try
block|{
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|bytes
argument_list|,
name|cellSize
operator|/
literal|2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Creating a file with block size smaller than "
operator|+
literal|"ec policy's cell size should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught expected exception"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"less than the cell size"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

