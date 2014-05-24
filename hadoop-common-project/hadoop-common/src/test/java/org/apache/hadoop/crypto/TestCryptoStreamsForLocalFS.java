begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|fs
operator|.
name|LocalFileSystem
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
name|After
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
name|Before
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
name|Ignore
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

begin_class
DECL|class|TestCryptoStreamsForLocalFS
specifier|public
class|class
name|TestCryptoStreamsForLocalFS
extends|extends
name|CryptoStreamsTestBase
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ROOT_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
operator|+
literal|"/work-dir/localfs"
decl_stmt|;
DECL|field|base
specifier|private
specifier|final
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
decl_stmt|;
DECL|field|file
specifier|private
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"test-file"
argument_list|)
decl_stmt|;
DECL|field|fileSys
specifier|private
specifier|static
name|LocalFileSystem
name|fileSys
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
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
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.file.impl"
argument_list|,
name|LocalFileSystem
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fileSys
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|codec
operator|=
name|CryptoCodec
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|fileSys
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtil
operator|.
name|setWritable
argument_list|(
name|base
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|base
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOutputStream (int bufferSize, byte[] key, byte[] iv)
specifier|protected
name|OutputStream
name|getOutputStream
parameter_list|(
name|int
name|bufferSize
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CryptoOutputStream
argument_list|(
name|fileSys
operator|.
name|create
argument_list|(
name|file
argument_list|)
argument_list|,
name|codec
argument_list|,
name|bufferSize
argument_list|,
name|key
argument_list|,
name|iv
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getInputStream (int bufferSize, byte[] key, byte[] iv)
specifier|protected
name|InputStream
name|getInputStream
parameter_list|(
name|int
name|bufferSize
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CryptoInputStream
argument_list|(
name|fileSys
operator|.
name|open
argument_list|(
name|file
argument_list|)
argument_list|,
name|codec
argument_list|,
name|bufferSize
argument_list|,
name|key
argument_list|,
name|iv
argument_list|)
return|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"ChecksumFSInputChecker doesn't support ByteBuffer read"
argument_list|)
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testByteBufferRead ()
specifier|public
name|void
name|testByteBufferRead
parameter_list|()
throws|throws
name|Exception
block|{}
annotation|@
name|Ignore
argument_list|(
literal|"ChecksumFSOutputSummer doesn't support Syncable"
argument_list|)
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testSyncable ()
specifier|public
name|void
name|testSyncable
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Ignore
argument_list|(
literal|"ChecksumFSInputChecker doesn't support ByteBuffer read"
argument_list|)
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testCombinedOp ()
specifier|public
name|void
name|testCombinedOp
parameter_list|()
throws|throws
name|Exception
block|{}
annotation|@
name|Ignore
argument_list|(
literal|"ChecksumFSInputChecker doesn't support enhanced ByteBuffer access"
argument_list|)
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testHasEnhancedByteBufferAccess ()
specifier|public
name|void
name|testHasEnhancedByteBufferAccess
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Ignore
argument_list|(
literal|"ChecksumFSInputChecker doesn't support seekToNewSource"
argument_list|)
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testSeekToNewSource ()
specifier|public
name|void
name|testSeekToNewSource
parameter_list|()
throws|throws
name|Exception
block|{   }
block|}
end_class

end_unit

