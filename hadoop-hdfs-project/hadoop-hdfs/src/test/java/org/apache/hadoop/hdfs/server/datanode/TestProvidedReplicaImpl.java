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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|Channels
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ReadableByteChannel
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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|BoundedInputStream
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
name|FileSystemTestHelper
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

begin_comment
comment|/**  * Tests the implementation of {@link ProvidedReplica}.  */
end_comment

begin_class
DECL|class|TestProvidedReplicaImpl
specifier|public
class|class
name|TestProvidedReplicaImpl
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestProvidedReplicaImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BASE_DIR
specifier|private
specifier|static
specifier|final
name|String
name|BASE_DIR
init|=
operator|new
name|FileSystemTestHelper
argument_list|()
operator|.
name|getTestRootDir
argument_list|()
decl_stmt|;
DECL|field|FILE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"provided-test"
decl_stmt|;
comment|// length of the file that is associated with the provided blocks.
DECL|field|FILE_LEN
specifier|private
specifier|static
specifier|final
name|long
name|FILE_LEN
init|=
literal|128
operator|*
literal|1024
operator|*
literal|10L
operator|+
literal|64
operator|*
literal|1024
decl_stmt|;
comment|// length of each provided block.
DECL|field|BLK_LEN
specifier|private
specifier|static
specifier|final
name|long
name|BLK_LEN
init|=
literal|128
operator|*
literal|1024L
decl_stmt|;
DECL|field|replicas
specifier|private
specifier|static
name|List
argument_list|<
name|ProvidedReplica
argument_list|>
name|replicas
decl_stmt|;
DECL|method|createFileIfNotExists (String baseDir)
specifier|private
specifier|static
name|void
name|createFileIfNotExists
parameter_list|(
name|String
name|baseDir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|newFile
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|FILE_NAME
argument_list|)
decl_stmt|;
name|newFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|newFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|newFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|OutputStream
name|writer
init|=
operator|new
name|FileOutputStream
argument_list|(
name|newFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|bytes
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|FILE_LEN
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created provided file "
operator|+
name|newFile
operator|+
literal|" of length "
operator|+
name|newFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createProvidedReplicas (Configuration conf)
specifier|private
specifier|static
name|void
name|createProvidedReplicas
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|long
name|numReplicas
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|FILE_LEN
operator|/
name|BLK_LEN
argument_list|)
decl_stmt|;
name|File
name|providedFile
init|=
operator|new
name|File
argument_list|(
name|BASE_DIR
argument_list|,
name|FILE_NAME
argument_list|)
decl_stmt|;
name|replicas
operator|=
operator|new
name|ArrayList
argument_list|<
name|ProvidedReplica
argument_list|>
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating "
operator|+
name|numReplicas
operator|+
literal|" provided replicas"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numReplicas
condition|;
name|i
operator|++
control|)
block|{
name|long
name|currentReplicaLength
init|=
name|FILE_LEN
operator|>=
operator|(
name|i
operator|+
literal|1
operator|)
operator|*
name|BLK_LEN
condition|?
name|BLK_LEN
else|:
name|FILE_LEN
operator|-
name|i
operator|*
name|BLK_LEN
decl_stmt|;
name|replicas
operator|.
name|add
argument_list|(
operator|new
name|FinalizedProvidedReplica
argument_list|(
name|i
argument_list|,
name|providedFile
operator|.
name|toURI
argument_list|()
argument_list|,
name|i
operator|*
name|BLK_LEN
argument_list|,
name|currentReplicaLength
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|createFileIfNotExists
argument_list|(
operator|new
name|File
argument_list|(
name|BASE_DIR
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|createProvidedReplicas
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks if {@code ins} matches the provided file from offset    * {@code fileOffset} for length {@ dataLength}.    * @param file the local file    * @param ins input stream to compare against    * @param fileOffset offset    * @param dataLength length    * @throws IOException    */
DECL|method|verifyReplicaContents (File file, InputStream ins, long fileOffset, long dataLength)
specifier|public
specifier|static
name|void
name|verifyReplicaContents
parameter_list|(
name|File
name|file
parameter_list|,
name|InputStream
name|ins
parameter_list|,
name|long
name|fileOffset
parameter_list|,
name|long
name|dataLength
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|fileIns
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|fileIns
operator|.
name|skip
argument_list|(
name|fileOffset
argument_list|)
expr_stmt|;
try|try
init|(
name|ReadableByteChannel
name|i
init|=
name|Channels
operator|.
name|newChannel
argument_list|(
operator|new
name|BoundedInputStream
argument_list|(
name|fileIns
argument_list|,
name|dataLength
argument_list|)
argument_list|)
init|)
block|{
try|try
init|(
name|ReadableByteChannel
name|j
init|=
name|Channels
operator|.
name|newChannel
argument_list|(
name|ins
argument_list|)
init|)
block|{
name|ByteBuffer
name|ib
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|4096
argument_list|)
decl_stmt|;
name|ByteBuffer
name|jb
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|4096
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|il
init|=
name|i
operator|.
name|read
argument_list|(
name|ib
argument_list|)
decl_stmt|;
name|int
name|jl
init|=
name|j
operator|.
name|read
argument_list|(
name|jb
argument_list|)
decl_stmt|;
if|if
condition|(
name|il
operator|<
literal|0
operator|||
name|jl
operator|<
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|il
argument_list|,
name|jl
argument_list|)
expr_stmt|;
break|break;
block|}
name|ib
operator|.
name|flip
argument_list|()
expr_stmt|;
name|jb
operator|.
name|flip
argument_list|()
expr_stmt|;
name|int
name|cmp
init|=
name|Math
operator|.
name|min
argument_list|(
name|ib
operator|.
name|remaining
argument_list|()
argument_list|,
name|jb
operator|.
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|cmp
condition|;
operator|++
name|k
control|)
block|{
name|assertEquals
argument_list|(
name|ib
operator|.
name|get
argument_list|()
argument_list|,
name|jb
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ib
operator|.
name|compact
argument_list|()
expr_stmt|;
name|jb
operator|.
name|compact
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testProvidedReplicaRead ()
specifier|public
name|void
name|testProvidedReplicaRead
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|providedFile
init|=
operator|new
name|File
argument_list|(
name|BASE_DIR
argument_list|,
name|FILE_NAME
argument_list|)
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
name|replicas
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ProvidedReplica
name|replica
init|=
name|replicas
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// block data should exist!
name|assertTrue
argument_list|(
name|replica
operator|.
name|blockDataExists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|providedFile
operator|.
name|toURI
argument_list|()
argument_list|,
name|replica
operator|.
name|getBlockURI
argument_list|()
argument_list|)
expr_stmt|;
name|verifyReplicaContents
argument_list|(
name|providedFile
argument_list|,
name|replica
operator|.
name|getDataInputStream
argument_list|(
literal|0
argument_list|)
argument_list|,
name|BLK_LEN
operator|*
name|i
argument_list|,
name|replica
operator|.
name|getBlockDataLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"All replica contents verified"
argument_list|)
expr_stmt|;
name|providedFile
operator|.
name|delete
argument_list|()
expr_stmt|;
comment|// the block data should no longer be found!
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|replicas
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ProvidedReplica
name|replica
init|=
name|replicas
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|replica
operator|.
name|blockDataExists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

