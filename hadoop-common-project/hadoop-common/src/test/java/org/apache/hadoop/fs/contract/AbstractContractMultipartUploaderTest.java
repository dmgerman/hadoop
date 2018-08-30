begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
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
name|FileNotFoundException
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|IOUtils
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
name|tuple
operator|.
name|Pair
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
name|BBUploadHandle
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
name|FileStatus
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
name|MultipartUploader
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
name|MultipartUploaderFactory
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
name|PartHandle
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
name|PathHandle
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
name|UploadHandle
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
name|fs
operator|.
name|contract
operator|.
name|ContractTestUtils
operator|.
name|verifyPathExists
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
import|;
end_import

begin_class
DECL|class|AbstractContractMultipartUploaderTest
specifier|public
specifier|abstract
class|class
name|AbstractContractMultipartUploaderTest
extends|extends
name|AbstractFSContractTestBase
block|{
comment|/**    * The payload is the part number repeated for the length of the part.    * This makes checking the correctness of the upload straightforward.    * @param partNumber part number    * @return the bytes to upload.    */
DECL|method|generatePayload (int partNumber)
specifier|private
name|byte
index|[]
name|generatePayload
parameter_list|(
name|int
name|partNumber
parameter_list|)
block|{
name|int
name|sizeInBytes
init|=
name|partSizeInBytes
argument_list|()
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|sizeInBytes
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
name|sizeInBytes
operator|/
operator|(
name|Integer
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
operator|)
condition|;
operator|++
name|i
control|)
block|{
name|buffer
operator|.
name|putInt
argument_list|(
name|partNumber
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|array
argument_list|()
return|;
block|}
comment|/**    * Load a path, make an MD5 digest.    * @param path path to load    * @return the digest array    * @throws IOException failure to read or digest the file.    */
DECL|method|digest (Path path)
specifier|protected
name|byte
index|[]
name|digest
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
init|)
block|{
name|byte
index|[]
name|fdData
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|MessageDigest
name|newDigest
init|=
name|DigestUtils
operator|.
name|getMd5Digest
argument_list|()
decl_stmt|;
return|return
name|newDigest
operator|.
name|digest
argument_list|(
name|fdData
argument_list|)
return|;
block|}
block|}
comment|/**    * Get the partition size in bytes to use for each upload.    * @return a number> 0    */
DECL|method|partSizeInBytes ()
specifier|protected
specifier|abstract
name|int
name|partSizeInBytes
parameter_list|()
function_decl|;
comment|/**    * Get the number of test payloads to upload.    * @return a number> 1    */
DECL|method|getTestPayloadCount ()
specifier|protected
name|int
name|getTestPayloadCount
parameter_list|()
block|{
return|return
literal|10
return|;
block|}
comment|/**    * Assert that a multipart upload is successful.    * @throws Exception failure    */
annotation|@
name|Test
DECL|method|testSingleUpload ()
specifier|public
name|void
name|testSingleUpload
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
name|path
argument_list|(
literal|"testSingleUpload"
argument_list|)
decl_stmt|;
name|MultipartUploader
name|mpu
init|=
name|MultipartUploaderFactory
operator|.
name|get
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|UploadHandle
name|uploadHandle
init|=
name|mpu
operator|.
name|initialize
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pair
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
argument_list|>
name|partHandles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|MessageDigest
name|origDigest
init|=
name|DigestUtils
operator|.
name|getMd5Digest
argument_list|()
decl_stmt|;
name|byte
index|[]
name|payload
init|=
name|generatePayload
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|origDigest
operator|.
name|update
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|PartHandle
name|partHandle
init|=
name|mpu
operator|.
name|putPart
argument_list|(
name|file
argument_list|,
name|is
argument_list|,
literal|1
argument_list|,
name|uploadHandle
argument_list|,
name|payload
operator|.
name|length
argument_list|)
decl_stmt|;
name|partHandles
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
literal|1
argument_list|,
name|partHandle
argument_list|)
argument_list|)
expr_stmt|;
name|PathHandle
name|fd
init|=
name|completeUpload
argument_list|(
name|file
argument_list|,
name|mpu
argument_list|,
name|uploadHandle
argument_list|,
name|partHandles
argument_list|,
name|origDigest
argument_list|,
name|payload
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// Complete is idempotent
name|PathHandle
name|fd2
init|=
name|mpu
operator|.
name|complete
argument_list|(
name|file
argument_list|,
name|partHandles
argument_list|,
name|uploadHandle
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
literal|"Path handles differ"
argument_list|,
name|fd
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|fd2
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|completeUpload (final Path file, final MultipartUploader mpu, final UploadHandle uploadHandle, final List<Pair<Integer, PartHandle>> partHandles, final MessageDigest origDigest, final int expectedLength)
specifier|private
name|PathHandle
name|completeUpload
parameter_list|(
specifier|final
name|Path
name|file
parameter_list|,
specifier|final
name|MultipartUploader
name|mpu
parameter_list|,
specifier|final
name|UploadHandle
name|uploadHandle
parameter_list|,
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
argument_list|>
name|partHandles
parameter_list|,
specifier|final
name|MessageDigest
name|origDigest
parameter_list|,
specifier|final
name|int
name|expectedLength
parameter_list|)
throws|throws
name|IOException
block|{
name|PathHandle
name|fd
init|=
name|mpu
operator|.
name|complete
argument_list|(
name|file
argument_list|,
name|partHandles
argument_list|,
name|uploadHandle
argument_list|)
decl_stmt|;
name|FileStatus
name|status
init|=
name|verifyPathExists
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
literal|"Completed file"
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"length of "
operator|+
name|status
argument_list|,
name|expectedLength
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
literal|"digest of source and "
operator|+
name|file
operator|+
literal|" differ"
argument_list|,
name|origDigest
operator|.
name|digest
argument_list|()
argument_list|,
name|digest
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|fd
return|;
block|}
comment|/**    * Assert that a multipart upload is successful.    * @throws Exception failure    */
annotation|@
name|Test
DECL|method|testMultipartUpload ()
specifier|public
name|void
name|testMultipartUpload
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
name|path
argument_list|(
literal|"testMultipartUpload"
argument_list|)
decl_stmt|;
name|MultipartUploader
name|mpu
init|=
name|MultipartUploaderFactory
operator|.
name|get
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|UploadHandle
name|uploadHandle
init|=
name|mpu
operator|.
name|initialize
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pair
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
argument_list|>
name|partHandles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|MessageDigest
name|origDigest
init|=
name|DigestUtils
operator|.
name|getMd5Digest
argument_list|()
decl_stmt|;
specifier|final
name|int
name|payloadCount
init|=
name|getTestPayloadCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|payloadCount
condition|;
operator|++
name|i
control|)
block|{
name|byte
index|[]
name|payload
init|=
name|generatePayload
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|origDigest
operator|.
name|update
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|PartHandle
name|partHandle
init|=
name|mpu
operator|.
name|putPart
argument_list|(
name|file
argument_list|,
name|is
argument_list|,
name|i
argument_list|,
name|uploadHandle
argument_list|,
name|payload
operator|.
name|length
argument_list|)
decl_stmt|;
name|partHandles
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|i
argument_list|,
name|partHandle
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|completeUpload
argument_list|(
name|file
argument_list|,
name|mpu
argument_list|,
name|uploadHandle
argument_list|,
name|partHandles
argument_list|,
name|origDigest
argument_list|,
name|payloadCount
operator|*
name|partSizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a multipart upload is successful even when the parts are    * given in the reverse order.    */
annotation|@
name|Test
DECL|method|testMultipartUploadReverseOrder ()
specifier|public
name|void
name|testMultipartUploadReverseOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
name|path
argument_list|(
literal|"testMultipartUploadReverseOrder"
argument_list|)
decl_stmt|;
name|MultipartUploader
name|mpu
init|=
name|MultipartUploaderFactory
operator|.
name|get
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|UploadHandle
name|uploadHandle
init|=
name|mpu
operator|.
name|initialize
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pair
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
argument_list|>
name|partHandles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|MessageDigest
name|origDigest
init|=
name|DigestUtils
operator|.
name|getMd5Digest
argument_list|()
decl_stmt|;
specifier|final
name|int
name|payloadCount
init|=
name|getTestPayloadCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|payloadCount
condition|;
operator|++
name|i
control|)
block|{
name|byte
index|[]
name|payload
init|=
name|generatePayload
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|origDigest
operator|.
name|update
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|payloadCount
init|;
name|i
operator|>
literal|0
condition|;
operator|--
name|i
control|)
block|{
name|byte
index|[]
name|payload
init|=
name|generatePayload
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|PartHandle
name|partHandle
init|=
name|mpu
operator|.
name|putPart
argument_list|(
name|file
argument_list|,
name|is
argument_list|,
name|i
argument_list|,
name|uploadHandle
argument_list|,
name|payload
operator|.
name|length
argument_list|)
decl_stmt|;
name|partHandles
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|i
argument_list|,
name|partHandle
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|completeUpload
argument_list|(
name|file
argument_list|,
name|mpu
argument_list|,
name|uploadHandle
argument_list|,
name|partHandles
argument_list|,
name|origDigest
argument_list|,
name|payloadCount
operator|*
name|partSizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a multipart upload is successful even when the parts are    * given in reverse order and the part numbers are not contiguous.    */
annotation|@
name|Test
DECL|method|testMultipartUploadReverseOrderNonContiguousPartNumbers ()
specifier|public
name|void
name|testMultipartUploadReverseOrderNonContiguousPartNumbers
parameter_list|()
throws|throws
name|Exception
block|{
name|describe
argument_list|(
literal|"Upload in reverse order and the part numbers are not contiguous"
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
name|path
argument_list|(
literal|"testMultipartUploadReverseOrderNonContiguousPartNumbers"
argument_list|)
decl_stmt|;
name|MultipartUploader
name|mpu
init|=
name|MultipartUploaderFactory
operator|.
name|get
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|UploadHandle
name|uploadHandle
init|=
name|mpu
operator|.
name|initialize
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pair
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
argument_list|>
name|partHandles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|MessageDigest
name|origDigest
init|=
name|DigestUtils
operator|.
name|getMd5Digest
argument_list|()
decl_stmt|;
name|int
name|payloadCount
init|=
literal|2
operator|*
name|getTestPayloadCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|i
operator|<=
name|payloadCount
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|byte
index|[]
name|payload
init|=
name|generatePayload
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|origDigest
operator|.
name|update
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|payloadCount
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|-=
literal|2
control|)
block|{
name|byte
index|[]
name|payload
init|=
name|generatePayload
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|PartHandle
name|partHandle
init|=
name|mpu
operator|.
name|putPart
argument_list|(
name|file
argument_list|,
name|is
argument_list|,
name|i
argument_list|,
name|uploadHandle
argument_list|,
name|payload
operator|.
name|length
argument_list|)
decl_stmt|;
name|partHandles
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|i
argument_list|,
name|partHandle
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|completeUpload
argument_list|(
name|file
argument_list|,
name|mpu
argument_list|,
name|uploadHandle
argument_list|,
name|partHandles
argument_list|,
name|origDigest
argument_list|,
name|getTestPayloadCount
argument_list|()
operator|*
name|partSizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that when we abort a multipart upload, the resulting file does    * not show up.    */
annotation|@
name|Test
DECL|method|testMultipartUploadAbort ()
specifier|public
name|void
name|testMultipartUploadAbort
parameter_list|()
throws|throws
name|Exception
block|{
name|describe
argument_list|(
literal|"Upload and then abort it before completing"
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
name|path
argument_list|(
literal|"testMultipartUploadAbort"
argument_list|)
decl_stmt|;
name|MultipartUploader
name|mpu
init|=
name|MultipartUploaderFactory
operator|.
name|get
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|UploadHandle
name|uploadHandle
init|=
name|mpu
operator|.
name|initialize
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pair
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
argument_list|>
name|partHandles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|20
init|;
name|i
operator|>=
literal|10
condition|;
operator|--
name|i
control|)
block|{
name|byte
index|[]
name|payload
init|=
name|generatePayload
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|PartHandle
name|partHandle
init|=
name|mpu
operator|.
name|putPart
argument_list|(
name|file
argument_list|,
name|is
argument_list|,
name|i
argument_list|,
name|uploadHandle
argument_list|,
name|payload
operator|.
name|length
argument_list|)
decl_stmt|;
name|partHandles
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|i
argument_list|,
name|partHandle
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|mpu
operator|.
name|abort
argument_list|(
name|file
argument_list|,
name|uploadHandle
argument_list|)
expr_stmt|;
name|String
name|contents
init|=
literal|"ThisIsPart49\n"
decl_stmt|;
name|int
name|len
init|=
name|contents
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|length
decl_stmt|;
name|InputStream
name|is
init|=
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|contents
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|mpu
operator|.
name|putPart
argument_list|(
name|file
argument_list|,
name|is
argument_list|,
literal|49
argument_list|,
name|uploadHandle
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|mpu
operator|.
name|complete
argument_list|(
name|file
argument_list|,
name|partHandles
argument_list|,
name|uploadHandle
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"Uploaded file should not exist"
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
comment|/**    * Trying to abort from an invalid handle must fail.    */
annotation|@
name|Test
DECL|method|testAbortUnknownUpload ()
specifier|public
name|void
name|testAbortUnknownUpload
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
name|path
argument_list|(
literal|"testAbortUnknownUpload"
argument_list|)
decl_stmt|;
name|MultipartUploader
name|mpu
init|=
name|MultipartUploaderFactory
operator|.
name|get
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ByteBuffer
name|byteBuffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
literal|"invalid-handle"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|UploadHandle
name|uploadHandle
init|=
name|BBUploadHandle
operator|.
name|from
argument_list|(
name|byteBuffer
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|mpu
operator|.
name|abort
argument_list|(
name|file
argument_list|,
name|uploadHandle
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Trying to abort with a handle of size 0 must fail.    */
annotation|@
name|Test
DECL|method|testAbortEmptyUploadHandle ()
specifier|public
name|void
name|testAbortEmptyUploadHandle
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
name|path
argument_list|(
literal|"testAbortEmptyUpload"
argument_list|)
decl_stmt|;
name|MultipartUploader
name|mpu
init|=
name|MultipartUploaderFactory
operator|.
name|get
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ByteBuffer
name|byteBuffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|UploadHandle
name|uploadHandle
init|=
name|BBUploadHandle
operator|.
name|from
argument_list|(
name|byteBuffer
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|mpu
operator|.
name|abort
argument_list|(
name|file
argument_list|,
name|uploadHandle
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * When we complete with no parts provided, it must fail.    */
annotation|@
name|Test
DECL|method|testCompleteEmptyUpload ()
specifier|public
name|void
name|testCompleteEmptyUpload
parameter_list|()
throws|throws
name|Exception
block|{
name|describe
argument_list|(
literal|"Expect an empty MPU to fail, but still be abortable"
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|dest
init|=
name|path
argument_list|(
literal|"testCompleteEmptyUpload"
argument_list|)
decl_stmt|;
name|MultipartUploader
name|mpu
init|=
name|MultipartUploaderFactory
operator|.
name|get
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|UploadHandle
name|handle
init|=
name|mpu
operator|.
name|initialize
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|mpu
operator|.
name|complete
argument_list|(
name|dest
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
name|handle
argument_list|)
argument_list|)
expr_stmt|;
name|mpu
operator|.
name|abort
argument_list|(
name|dest
argument_list|,
name|handle
argument_list|)
expr_stmt|;
block|}
comment|/**    * When we pass empty uploadID, putPart throws IllegalArgumentException.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testPutPartEmptyUploadID ()
specifier|public
name|void
name|testPutPartEmptyUploadID
parameter_list|()
throws|throws
name|Exception
block|{
name|describe
argument_list|(
literal|"Expect IllegalArgumentException when putPart uploadID is empty"
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|dest
init|=
name|path
argument_list|(
literal|"testCompleteEmptyUpload"
argument_list|)
decl_stmt|;
name|MultipartUploader
name|mpu
init|=
name|MultipartUploaderFactory
operator|.
name|get
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|mpu
operator|.
name|initialize
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|UploadHandle
name|emptyHandle
init|=
name|BBUploadHandle
operator|.
name|from
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|payload
init|=
name|generatePayload
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|mpu
operator|.
name|putPart
argument_list|(
name|dest
argument_list|,
name|is
argument_list|,
literal|1
argument_list|,
name|emptyHandle
argument_list|,
name|payload
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * When we pass empty uploadID, complete throws IllegalArgumentException.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testCompleteEmptyUploadID ()
specifier|public
name|void
name|testCompleteEmptyUploadID
parameter_list|()
throws|throws
name|Exception
block|{
name|describe
argument_list|(
literal|"Expect IllegalArgumentException when complete uploadID is empty"
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|dest
init|=
name|path
argument_list|(
literal|"testCompleteEmptyUpload"
argument_list|)
decl_stmt|;
name|MultipartUploader
name|mpu
init|=
name|MultipartUploaderFactory
operator|.
name|get
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|UploadHandle
name|realHandle
init|=
name|mpu
operator|.
name|initialize
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|UploadHandle
name|emptyHandle
init|=
name|BBUploadHandle
operator|.
name|from
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pair
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
argument_list|>
name|partHandles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|byte
index|[]
name|payload
init|=
name|generatePayload
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|PartHandle
name|partHandle
init|=
name|mpu
operator|.
name|putPart
argument_list|(
name|dest
argument_list|,
name|is
argument_list|,
literal|1
argument_list|,
name|realHandle
argument_list|,
name|payload
operator|.
name|length
argument_list|)
decl_stmt|;
name|partHandles
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
literal|1
argument_list|,
name|partHandle
argument_list|)
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|mpu
operator|.
name|complete
argument_list|(
name|dest
argument_list|,
name|partHandles
argument_list|,
name|emptyHandle
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

