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
name|DataOutputStream
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
name|io
operator|.
name|InputStream
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
name|Field
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
name|InvocationTargetException
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
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|reflect
operator|.
name|FieldUtils
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
name|CreateFlag
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
name|FsTracer
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
name|hdfs
operator|.
name|DataStreamer
operator|.
name|LastExceptionInStreamer
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
name|client
operator|.
name|impl
operator|.
name|DfsClientConf
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
name|BlockListAsLongs
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
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
name|datatransfer
operator|.
name|BlockConstructionStage
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
name|datatransfer
operator|.
name|PacketReceiver
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
name|blockmanagement
operator|.
name|BlockManager
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
name|blockmanagement
operator|.
name|DatanodeDescriptor
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
name|blockmanagement
operator|.
name|DatanodeManager
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
name|DatanodeStorage
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
name|PathUtils
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
name|Whitebox
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|core
operator|.
name|SpanId
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
name|Assert
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
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyLong
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
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
name|verify
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
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
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
name|spy
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
name|when
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
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT
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
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
import|;
end_import

begin_class
DECL|class|TestDFSOutputStream
specifier|public
class|class
name|TestDFSOutputStream
block|{
DECL|field|cluster
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * The close() method of DFSOutputStream should never throw the same exception    * twice. See HDFS-5335 for details.    */
annotation|@
name|Test
DECL|method|testCloseTwice ()
specifier|public
name|void
name|testCloseTwice
parameter_list|()
throws|throws
name|IOException
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
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
literal|"/test"
argument_list|)
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|dos
init|=
operator|(
name|DFSOutputStream
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|os
argument_list|,
literal|"wrappedStream"
argument_list|)
decl_stmt|;
name|DataStreamer
name|streamer
init|=
operator|(
name|DataStreamer
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|dos
argument_list|,
literal|"streamer"
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|LastExceptionInStreamer
name|ex
init|=
operator|(
name|LastExceptionInStreamer
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|streamer
argument_list|,
literal|"lastException"
argument_list|)
decl_stmt|;
name|Throwable
name|thrown
init|=
operator|(
name|Throwable
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|ex
argument_list|,
literal|"thrown"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|thrown
argument_list|)
expr_stmt|;
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOException
name|dummy
init|=
operator|new
name|IOException
argument_list|(
literal|"dummy"
argument_list|)
decl_stmt|;
name|ex
operator|.
name|set
argument_list|(
name|dummy
argument_list|)
expr_stmt|;
try|try
block|{
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
argument_list|,
name|dummy
argument_list|)
expr_stmt|;
block|}
name|thrown
operator|=
operator|(
name|Throwable
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|ex
argument_list|,
literal|"thrown"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|thrown
argument_list|)
expr_stmt|;
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * The computePacketChunkSize() method of DFSOutputStream should set the actual    * packet size< 64kB. See HDFS-7308 for details.    */
annotation|@
name|Test
DECL|method|testComputePacketChunkSize ()
specifier|public
name|void
name|testComputePacketChunkSize
parameter_list|()
throws|throws
name|Exception
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
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
literal|"/test"
argument_list|)
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|dos
init|=
operator|(
name|DFSOutputStream
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|os
argument_list|,
literal|"wrappedStream"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|packetSize
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
specifier|final
name|int
name|bytesPerChecksum
init|=
literal|512
decl_stmt|;
name|Method
name|method
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"computePacketChunkSize"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|dos
argument_list|,
name|packetSize
argument_list|,
name|bytesPerChecksum
argument_list|)
expr_stmt|;
name|Field
name|field
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"packetSize"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
name|Integer
operator|)
name|field
operator|.
name|get
argument_list|(
name|dos
argument_list|)
operator|+
literal|33
operator|<
name|packetSize
argument_list|)
expr_stmt|;
comment|// If PKT_MAX_HEADER_LEN is 257, actual packet size come to over 64KB
comment|// without a fix on HDFS-7308.
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
name|Integer
operator|)
name|field
operator|.
name|get
argument_list|(
name|dos
argument_list|)
operator|+
literal|257
operator|<
name|packetSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * This tests preventing overflows of package size and bodySize.    *<p>    * See also https://issues.apache.org/jira/browse/HDFS-11608.    *</p>    * @throws IOException    * @throws SecurityException    * @throws NoSuchFieldException    * @throws InvocationTargetException    * @throws IllegalArgumentException    * @throws IllegalAccessException    * @throws NoSuchMethodException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testPreventOverflow ()
specifier|public
name|void
name|testPreventOverflow
parameter_list|()
throws|throws
name|IOException
throws|,
name|NoSuchFieldException
throws|,
name|SecurityException
throws|,
name|IllegalAccessException
throws|,
name|IllegalArgumentException
throws|,
name|InvocationTargetException
throws|,
name|NoSuchMethodException
block|{
specifier|final
name|int
name|defaultWritePacketSize
init|=
name|DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT
decl_stmt|;
name|int
name|configuredWritePacketSize
init|=
name|defaultWritePacketSize
decl_stmt|;
name|int
name|finalWritePacketSize
init|=
name|defaultWritePacketSize
decl_stmt|;
comment|/* test default WritePacketSize, e.g. 64*1024 */
name|runAdjustChunkBoundary
argument_list|(
name|configuredWritePacketSize
argument_list|,
name|finalWritePacketSize
argument_list|)
expr_stmt|;
comment|/* test large WritePacketSize, e.g. 1G */
name|configuredWritePacketSize
operator|=
literal|1000
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
name|finalWritePacketSize
operator|=
name|PacketReceiver
operator|.
name|MAX_PACKET_SIZE
expr_stmt|;
name|runAdjustChunkBoundary
argument_list|(
name|configuredWritePacketSize
argument_list|,
name|finalWritePacketSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * @configuredWritePacketSize the configured WritePacketSize.    * @finalWritePacketSize the final WritePacketSize picked by    *                       {@link DFSOutputStream#adjustChunkBoundary}    */
DECL|method|runAdjustChunkBoundary ( final int configuredWritePacketSize, final int finalWritePacketSize)
specifier|private
name|void
name|runAdjustChunkBoundary
parameter_list|(
specifier|final
name|int
name|configuredWritePacketSize
parameter_list|,
specifier|final
name|int
name|finalWritePacketSize
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchFieldException
throws|,
name|SecurityException
throws|,
name|IllegalAccessException
throws|,
name|IllegalArgumentException
throws|,
name|InvocationTargetException
throws|,
name|NoSuchMethodException
block|{
specifier|final
name|boolean
name|appendChunk
init|=
literal|false
decl_stmt|;
specifier|final
name|long
name|blockSize
init|=
literal|3221225500L
decl_stmt|;
specifier|final
name|long
name|bytesCurBlock
init|=
literal|1073741824L
decl_stmt|;
specifier|final
name|int
name|bytesPerChecksum
init|=
literal|512
decl_stmt|;
specifier|final
name|int
name|checksumSize
init|=
literal|4
decl_stmt|;
specifier|final
name|int
name|chunkSize
init|=
name|bytesPerChecksum
operator|+
name|checksumSize
decl_stmt|;
specifier|final
name|int
name|packateMaxHeaderLength
init|=
literal|33
decl_stmt|;
name|MiniDFSCluster
name|dfsCluster
init|=
literal|null
decl_stmt|;
specifier|final
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|,
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Configuration
name|dfsConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|dfsConf
operator|.
name|set
argument_list|(
name|MiniDFSCluster
operator|.
name|HDFS_MINIDFS_BASEDIR
argument_list|,
name|baseDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|dfsConf
operator|.
name|setInt
argument_list|(
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
argument_list|,
name|configuredWritePacketSize
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|dfsConf
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
name|dfsCluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|FSDataOutputStream
name|os
init|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|baseDir
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"testPreventOverflow"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DFSOutputStream
name|dos
init|=
operator|(
name|DFSOutputStream
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|os
argument_list|,
literal|"wrappedStream"
argument_list|)
decl_stmt|;
comment|/* set appendChunk */
specifier|final
name|Method
name|setAppendChunkMethod
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"setAppendChunk"
argument_list|,
name|boolean
operator|.
name|class
argument_list|)
decl_stmt|;
name|setAppendChunkMethod
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setAppendChunkMethod
operator|.
name|invoke
argument_list|(
name|dos
argument_list|,
name|appendChunk
argument_list|)
expr_stmt|;
comment|/* set bytesCurBlock */
specifier|final
name|Method
name|setBytesCurBlockMethod
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"setBytesCurBlock"
argument_list|,
name|long
operator|.
name|class
argument_list|)
decl_stmt|;
name|setBytesCurBlockMethod
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setBytesCurBlockMethod
operator|.
name|invoke
argument_list|(
name|dos
argument_list|,
name|bytesCurBlock
argument_list|)
expr_stmt|;
comment|/* set blockSize */
specifier|final
name|Field
name|blockSizeField
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"blockSize"
argument_list|)
decl_stmt|;
name|blockSizeField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|blockSizeField
operator|.
name|setLong
argument_list|(
name|dos
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
comment|/* call adjustChunkBoundary */
specifier|final
name|Method
name|method
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"adjustChunkBoundary"
argument_list|)
decl_stmt|;
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|dos
argument_list|)
expr_stmt|;
comment|/* get and verify writePacketSize */
specifier|final
name|Field
name|writePacketSizeField
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"writePacketSize"
argument_list|)
decl_stmt|;
name|writePacketSizeField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|writePacketSizeField
operator|.
name|getInt
argument_list|(
name|dos
argument_list|)
argument_list|,
name|finalWritePacketSize
argument_list|)
expr_stmt|;
comment|/* get and verify chunksPerPacket */
specifier|final
name|Field
name|chunksPerPacketField
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"chunksPerPacket"
argument_list|)
decl_stmt|;
name|chunksPerPacketField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|chunksPerPacketField
operator|.
name|getInt
argument_list|(
name|dos
argument_list|)
argument_list|,
operator|(
name|finalWritePacketSize
operator|-
name|packateMaxHeaderLength
operator|)
operator|/
name|chunkSize
argument_list|)
expr_stmt|;
comment|/* get and verify packetSize */
specifier|final
name|Field
name|packetSizeField
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"packetSize"
argument_list|)
decl_stmt|;
name|packetSizeField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|packetSizeField
operator|.
name|getInt
argument_list|(
name|dos
argument_list|)
argument_list|,
name|chunksPerPacketField
operator|.
name|getInt
argument_list|(
name|dos
argument_list|)
operator|*
name|chunkSize
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
block|{
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testCongestionBackoff ()
specifier|public
name|void
name|testCongestionBackoff
parameter_list|()
throws|throws
name|IOException
block|{
name|DfsClientConf
name|dfsClientConf
init|=
name|mock
argument_list|(
name|DfsClientConf
operator|.
name|class
argument_list|)
decl_stmt|;
name|DFSClient
name|client
init|=
name|mock
argument_list|(
name|DFSClient
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|client
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|dfsClientConf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|getTracer
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FsTracer
operator|.
name|get
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|clientRunning
operator|=
literal|true
expr_stmt|;
name|DataStreamer
name|stream
init|=
operator|new
name|DataStreamer
argument_list|(
name|mock
argument_list|(
name|HdfsFileStatus
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|ExtendedBlock
operator|.
name|class
argument_list|)
argument_list|,
name|client
argument_list|,
literal|"foo"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|DataOutputStream
name|blockStream
init|=
name|mock
argument_list|(
name|DataOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|blockStream
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|stream
argument_list|,
literal|"blockStream"
argument_list|,
name|blockStream
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|stream
argument_list|,
literal|"stage"
argument_list|,
name|BlockConstructionStage
operator|.
name|PIPELINE_CLOSE
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|LinkedList
argument_list|<
name|DFSPacket
argument_list|>
name|dataQueue
init|=
operator|(
name|LinkedList
argument_list|<
name|DFSPacket
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|stream
argument_list|,
literal|"dataQueue"
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|ArrayList
argument_list|<
name|DatanodeInfo
argument_list|>
name|congestedNodes
init|=
operator|(
name|ArrayList
argument_list|<
name|DatanodeInfo
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|stream
argument_list|,
literal|"congestedNodes"
argument_list|)
decl_stmt|;
name|congestedNodes
operator|.
name|add
argument_list|(
name|mock
argument_list|(
name|DatanodeInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|DFSPacket
name|packet
init|=
name|mock
argument_list|(
name|DFSPacket
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|packet
operator|.
name|getTraceParents
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|SpanId
index|[]
block|{}
argument_list|)
expr_stmt|;
name|dataQueue
operator|.
name|add
argument_list|(
name|packet
argument_list|)
expr_stmt|;
name|stream
operator|.
name|run
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|congestedNodes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoLocalWriteFlag ()
specifier|public
name|void
name|testNoLocalWriteFlag
parameter_list|()
throws|throws
name|IOException
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flags
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|NO_LOCAL_WRITE
argument_list|,
name|CreateFlag
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|BlockManager
name|bm
init|=
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
decl_stmt|;
name|DatanodeManager
name|dm
init|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
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
literal|"/test-no-local"
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|,
name|flags
argument_list|,
literal|512
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|512
argument_list|,
literal|null
argument_list|)
init|)
block|{
comment|// Inject a DatanodeManager that returns one DataNode as local node for
comment|// the client.
name|DatanodeManager
name|spyDm
init|=
name|spy
argument_list|(
name|dm
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|dn1
init|=
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|dn1
argument_list|)
operator|.
name|when
argument_list|(
name|spyDm
argument_list|)
operator|.
name|getDatanodeByHost
argument_list|(
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|bm
argument_list|,
literal|"datanodeManager"
argument_list|,
name|spyDm
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|512
operator|*
literal|16
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|bm
argument_list|,
literal|"datanodeManager"
argument_list|,
name|dm
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
specifier|final
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
comment|// Total number of DataNodes is 3.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cluster
operator|.
name|getAllBlockReports
argument_list|(
name|bpid
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numDataNodesWithData
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|DatanodeStorage
argument_list|,
name|BlockListAsLongs
argument_list|>
name|dnBlocks
range|:
name|cluster
operator|.
name|getAllBlockReports
argument_list|(
name|bpid
argument_list|)
control|)
block|{
for|for
control|(
name|BlockListAsLongs
name|blocks
range|:
name|dnBlocks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|blocks
operator|.
name|getNumberOfBlocks
argument_list|()
operator|>
literal|0
condition|)
block|{
name|numDataNodesWithData
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|// Verify that only one DN has no data.
name|assertEquals
argument_list|(
literal|1
argument_list|,
literal|3
operator|-
name|numDataNodesWithData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEndLeaseCall ()
specifier|public
name|void
name|testEndLeaseCall
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
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DFSClient
name|spyClient
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|dfsOutputStream
init|=
name|spyClient
operator|.
name|create
argument_list|(
literal|"/file2"
argument_list|,
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|spyDFSOutputStream
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|dfsOutputStream
argument_list|)
decl_stmt|;
name|spyDFSOutputStream
operator|.
name|closeThreads
argument_list|(
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|spyClient
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|endFileLease
argument_list|(
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
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
literal|"/normal-file"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Verify output stream supports hsync() and hflush().
name|assertTrue
argument_list|(
literal|"DFSOutputStream should support hflush()!"
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
name|assertTrue
argument_list|(
literal|"DFSOutputStream should support hsync()!"
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
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
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
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * If dfs.client.recover-on-close-exception.enable is set and exception    * happens in close, the local lease should be closed and lease in namenode    * should be recovered.    */
annotation|@
name|Test
DECL|method|testExceptionInClose ()
specifier|public
name|void
name|testExceptionInClose
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testStr
init|=
literal|"Test exception in close"
decl_stmt|;
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/closeexception"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Write
operator|.
name|RECOVER_ON_CLOSE_EXCEPTION_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|dos
init|=
operator|(
name|DFSOutputStream
operator|)
name|FieldUtils
operator|.
name|readField
argument_list|(
name|os
argument_list|,
literal|"wrappedStream"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|dos
operator|.
name|setExceptionInClose
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|testStr
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// There should be exception
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
name|boolean
name|closed
decl_stmt|;
try|try
block|{
name|closed
operator|=
name|fs
operator|.
name|isFileClosed
argument_list|(
name|testFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|closed
return|;
block|}
argument_list|,
literal|1000
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|isFileClosed
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

