begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|*
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
name|Collection
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
name|ChecksumException
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
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestNativeCrc32
specifier|public
class|class
name|TestNativeCrc32
block|{
DECL|field|BASE_POSITION
specifier|private
specifier|static
specifier|final
name|long
name|BASE_POSITION
init|=
literal|0
decl_stmt|;
DECL|field|IO_BYTES_PER_CHECKSUM_DEFAULT
specifier|private
specifier|static
specifier|final
name|int
name|IO_BYTES_PER_CHECKSUM_DEFAULT
init|=
literal|512
decl_stmt|;
DECL|field|IO_BYTES_PER_CHECKSUM_KEY
specifier|private
specifier|static
specifier|final
name|String
name|IO_BYTES_PER_CHECKSUM_KEY
init|=
literal|"io.bytes.per.checksum"
decl_stmt|;
DECL|field|NUM_CHUNKS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_CHUNKS
init|=
literal|3
decl_stmt|;
DECL|field|checksumType
specifier|private
specifier|final
name|DataChecksum
operator|.
name|Type
name|checksumType
decl_stmt|;
DECL|field|bytesPerChecksum
specifier|private
name|int
name|bytesPerChecksum
decl_stmt|;
DECL|field|fileName
specifier|private
name|String
name|fileName
decl_stmt|;
DECL|field|data
DECL|field|checksums
specifier|private
name|ByteBuffer
name|data
decl_stmt|,
name|checksums
decl_stmt|;
DECL|field|checksum
specifier|private
name|DataChecksum
name|checksum
decl_stmt|;
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Parameters
DECL|method|data ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32C
block|}
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
DECL|method|TestNativeCrc32 (DataChecksum.Type checksumType)
specifier|public
name|TestNativeCrc32
parameter_list|(
name|DataChecksum
operator|.
name|Type
name|checksumType
parameter_list|)
block|{
name|this
operator|.
name|checksumType
operator|=
name|checksumType
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|NativeCrc32
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"These tests assume they can write a checksum value as a 4-byte int."
argument_list|,
literal|4
argument_list|,
name|checksumType
operator|.
name|size
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|bytesPerChecksum
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|IO_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|IO_BYTES_PER_CHECKSUM_DEFAULT
argument_list|)
expr_stmt|;
name|fileName
operator|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
expr_stmt|;
name|checksum
operator|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|checksumType
argument_list|,
name|bytesPerChecksum
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVerifyChunkedSumsSuccess ()
specifier|public
name|void
name|testVerifyChunkedSumsSuccess
parameter_list|()
throws|throws
name|ChecksumException
block|{
name|allocateDirectByteBuffers
argument_list|()
expr_stmt|;
name|fillDataAndValidChecksums
argument_list|()
expr_stmt|;
name|NativeCrc32
operator|.
name|verifyChunkedSums
argument_list|(
name|bytesPerChecksum
argument_list|,
name|checksumType
operator|.
name|id
argument_list|,
name|checksums
argument_list|,
name|data
argument_list|,
name|fileName
argument_list|,
name|BASE_POSITION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVerifyChunkedSumsFail ()
specifier|public
name|void
name|testVerifyChunkedSumsFail
parameter_list|()
throws|throws
name|ChecksumException
block|{
name|allocateDirectByteBuffers
argument_list|()
expr_stmt|;
name|fillDataAndInvalidChecksums
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|ChecksumException
operator|.
name|class
argument_list|)
expr_stmt|;
name|NativeCrc32
operator|.
name|verifyChunkedSums
argument_list|(
name|bytesPerChecksum
argument_list|,
name|checksumType
operator|.
name|id
argument_list|,
name|checksums
argument_list|,
name|data
argument_list|,
name|fileName
argument_list|,
name|BASE_POSITION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVerifyChunkedSumsByteArraySuccess ()
specifier|public
name|void
name|testVerifyChunkedSumsByteArraySuccess
parameter_list|()
throws|throws
name|ChecksumException
block|{
name|allocateArrayByteBuffers
argument_list|()
expr_stmt|;
name|fillDataAndValidChecksums
argument_list|()
expr_stmt|;
name|NativeCrc32
operator|.
name|verifyChunkedSumsByteArray
argument_list|(
name|bytesPerChecksum
argument_list|,
name|checksumType
operator|.
name|id
argument_list|,
name|checksums
operator|.
name|array
argument_list|()
argument_list|,
name|checksums
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|array
argument_list|()
argument_list|,
name|data
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|remaining
argument_list|()
argument_list|,
name|fileName
argument_list|,
name|BASE_POSITION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVerifyChunkedSumsByteArrayFail ()
specifier|public
name|void
name|testVerifyChunkedSumsByteArrayFail
parameter_list|()
throws|throws
name|ChecksumException
block|{
name|allocateArrayByteBuffers
argument_list|()
expr_stmt|;
name|fillDataAndInvalidChecksums
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|ChecksumException
operator|.
name|class
argument_list|)
expr_stmt|;
name|NativeCrc32
operator|.
name|verifyChunkedSumsByteArray
argument_list|(
name|bytesPerChecksum
argument_list|,
name|checksumType
operator|.
name|id
argument_list|,
name|checksums
operator|.
name|array
argument_list|()
argument_list|,
name|checksums
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|array
argument_list|()
argument_list|,
name|data
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|remaining
argument_list|()
argument_list|,
name|fileName
argument_list|,
name|BASE_POSITION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCalculateChunkedSumsSuccess ()
specifier|public
name|void
name|testCalculateChunkedSumsSuccess
parameter_list|()
throws|throws
name|ChecksumException
block|{
name|allocateDirectByteBuffers
argument_list|()
expr_stmt|;
name|fillDataAndValidChecksums
argument_list|()
expr_stmt|;
name|NativeCrc32
operator|.
name|calculateChunkedSums
argument_list|(
name|bytesPerChecksum
argument_list|,
name|checksumType
operator|.
name|id
argument_list|,
name|checksums
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCalculateChunkedSumsFail ()
specifier|public
name|void
name|testCalculateChunkedSumsFail
parameter_list|()
throws|throws
name|ChecksumException
block|{
name|allocateDirectByteBuffers
argument_list|()
expr_stmt|;
name|fillDataAndInvalidChecksums
argument_list|()
expr_stmt|;
name|NativeCrc32
operator|.
name|calculateChunkedSums
argument_list|(
name|bytesPerChecksum
argument_list|,
name|checksumType
operator|.
name|id
argument_list|,
name|checksums
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCalculateChunkedSumsByteArraySuccess ()
specifier|public
name|void
name|testCalculateChunkedSumsByteArraySuccess
parameter_list|()
throws|throws
name|ChecksumException
block|{
name|allocateArrayByteBuffers
argument_list|()
expr_stmt|;
name|fillDataAndValidChecksums
argument_list|()
expr_stmt|;
name|NativeCrc32
operator|.
name|calculateChunkedSumsByteArray
argument_list|(
name|bytesPerChecksum
argument_list|,
name|checksumType
operator|.
name|id
argument_list|,
name|checksums
operator|.
name|array
argument_list|()
argument_list|,
name|checksums
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|array
argument_list|()
argument_list|,
name|data
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCalculateChunkedSumsByteArrayFail ()
specifier|public
name|void
name|testCalculateChunkedSumsByteArrayFail
parameter_list|()
throws|throws
name|ChecksumException
block|{
name|allocateArrayByteBuffers
argument_list|()
expr_stmt|;
name|fillDataAndInvalidChecksums
argument_list|()
expr_stmt|;
name|NativeCrc32
operator|.
name|calculateChunkedSumsByteArray
argument_list|(
name|bytesPerChecksum
argument_list|,
name|checksumType
operator|.
name|id
argument_list|,
name|checksums
operator|.
name|array
argument_list|()
argument_list|,
name|checksums
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|array
argument_list|()
argument_list|,
name|data
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testNativeVerifyChunkedSumsSuccess ()
specifier|public
name|void
name|testNativeVerifyChunkedSumsSuccess
parameter_list|()
throws|throws
name|ChecksumException
block|{
name|allocateDirectByteBuffers
argument_list|()
expr_stmt|;
name|fillDataAndValidChecksums
argument_list|()
expr_stmt|;
name|NativeCrc32
operator|.
name|nativeVerifyChunkedSums
argument_list|(
name|bytesPerChecksum
argument_list|,
name|checksumType
operator|.
name|id
argument_list|,
name|checksums
argument_list|,
name|checksums
operator|.
name|position
argument_list|()
argument_list|,
name|data
argument_list|,
name|data
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|remaining
argument_list|()
argument_list|,
name|fileName
argument_list|,
name|BASE_POSITION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testNativeVerifyChunkedSumsFail ()
specifier|public
name|void
name|testNativeVerifyChunkedSumsFail
parameter_list|()
throws|throws
name|ChecksumException
block|{
name|allocateDirectByteBuffers
argument_list|()
expr_stmt|;
name|fillDataAndInvalidChecksums
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|ChecksumException
operator|.
name|class
argument_list|)
expr_stmt|;
name|NativeCrc32
operator|.
name|nativeVerifyChunkedSums
argument_list|(
name|bytesPerChecksum
argument_list|,
name|checksumType
operator|.
name|id
argument_list|,
name|checksums
argument_list|,
name|checksums
operator|.
name|position
argument_list|()
argument_list|,
name|data
argument_list|,
name|data
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|remaining
argument_list|()
argument_list|,
name|fileName
argument_list|,
name|BASE_POSITION
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allocates data buffer and checksums buffer as arrays on the heap.    */
DECL|method|allocateArrayByteBuffers ()
specifier|private
name|void
name|allocateArrayByteBuffers
parameter_list|()
block|{
name|data
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
name|bytesPerChecksum
operator|*
name|NUM_CHUNKS
index|]
argument_list|)
expr_stmt|;
name|checksums
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
name|NUM_CHUNKS
operator|*
name|checksumType
operator|.
name|size
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allocates data buffer and checksums buffer as direct byte buffers.    */
DECL|method|allocateDirectByteBuffers ()
specifier|private
name|void
name|allocateDirectByteBuffers
parameter_list|()
block|{
name|data
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|bytesPerChecksum
operator|*
name|NUM_CHUNKS
argument_list|)
expr_stmt|;
name|checksums
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|NUM_CHUNKS
operator|*
name|checksumType
operator|.
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Fill data buffer with monotonically increasing byte values.  Overflow is    * fine, because it's just test data.  Update the checksum with the same byte    * values.  After every chunk, write the checksum to the checksums buffer.    * After finished writing, flip the buffers to prepare them for reading.    */
DECL|method|fillDataAndValidChecksums ()
specifier|private
name|void
name|fillDataAndValidChecksums
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_CHUNKS
condition|;
operator|++
name|i
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|bytesPerChecksum
condition|;
operator|++
name|j
control|)
block|{
name|byte
name|b
init|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|*
name|bytesPerChecksum
operator|+
name|j
operator|)
operator|&
literal|0xFF
argument_list|)
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|checksum
operator|.
name|update
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|checksums
operator|.
name|putInt
argument_list|(
operator|(
name|int
operator|)
name|checksum
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|checksum
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|data
operator|.
name|flip
argument_list|()
expr_stmt|;
name|checksums
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
comment|/**    * Fill data buffer with monotonically increasing byte values.  Overflow is    * fine, because it's just test data.  Update the checksum with different byte    * byte values, so that the checksums are incorrect intentionally.  After every    * chunk, write the checksum to the checksums buffer.  After finished writing,    * flip the buffers to prepare them for reading.    */
DECL|method|fillDataAndInvalidChecksums ()
specifier|private
name|void
name|fillDataAndInvalidChecksums
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_CHUNKS
condition|;
operator|++
name|i
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|bytesPerChecksum
condition|;
operator|++
name|j
control|)
block|{
name|byte
name|b
init|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|*
name|bytesPerChecksum
operator|+
name|j
operator|)
operator|&
literal|0xFF
argument_list|)
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|checksum
operator|.
name|update
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|b
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|checksums
operator|.
name|putInt
argument_list|(
operator|(
name|int
operator|)
name|checksum
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|checksum
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|data
operator|.
name|flip
argument_list|()
expr_stmt|;
name|checksums
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

