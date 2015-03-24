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
name|Method
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
name|atomic
operator|.
name|AtomicReference
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
import|import
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|util
operator|.
name|reflection
operator|.
name|Whitebox
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|AtomicReference
argument_list|<
name|IOException
argument_list|>
name|ex
init|=
operator|(
name|AtomicReference
argument_list|<
name|IOException
argument_list|>
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|ex
operator|.
name|get
argument_list|()
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
name|Assert
operator|.
name|assertEquals
argument_list|(
name|e
argument_list|,
name|dummy
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|ex
operator|.
name|get
argument_list|()
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
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

