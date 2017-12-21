begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ObjectMetadata
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|PutObjectRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|FileAlreadyExistsException
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
name|store
operator|.
name|EtagChecksum
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
name|createFile
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
name|touch
import|;
end_import

begin_comment
comment|/**  * Tests of the S3A FileSystem which don't have a specific home and can share  * a filesystem instance with others..  */
end_comment

begin_class
DECL|class|ITestS3AMiscOperations
specifier|public
class|class
name|ITestS3AMiscOperations
extends|extends
name|AbstractS3ATestBase
block|{
DECL|field|HELLO
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|HELLO
init|=
literal|"hello"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testCreateNonRecursiveSuccess ()
specifier|public
name|void
name|testCreateNonRecursiveSuccess
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|shouldWork
init|=
name|path
argument_list|(
literal|"nonrecursivenode"
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|out
init|=
name|createNonRecursive
argument_list|(
name|shouldWork
argument_list|)
init|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertIsFile
argument_list|(
name|shouldWork
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testCreateNonRecursiveNoParent ()
specifier|public
name|void
name|testCreateNonRecursiveNoParent
parameter_list|()
throws|throws
name|IOException
block|{
name|createNonRecursive
argument_list|(
name|path
argument_list|(
literal|"/recursive/node"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileAlreadyExistsException
operator|.
name|class
argument_list|)
DECL|method|testCreateNonRecursiveParentIsFile ()
specifier|public
name|void
name|testCreateNonRecursiveParentIsFile
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|parent
init|=
name|path
argument_list|(
literal|"/file.txt"
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|parent
argument_list|)
expr_stmt|;
name|createNonRecursive
argument_list|(
operator|new
name|Path
argument_list|(
name|parent
argument_list|,
literal|"fail"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutObjectDirect ()
specifier|public
name|void
name|testPutObjectDirect
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|ObjectMetadata
name|metadata
init|=
name|fs
operator|.
name|newObjectMetadata
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|setContentLength
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|path
argument_list|(
literal|"putDirect"
argument_list|)
decl_stmt|;
specifier|final
name|PutObjectRequest
name|put
init|=
operator|new
name|PutObjectRequest
argument_list|(
name|fs
operator|.
name|getBucket
argument_list|()
argument_list|,
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"PUT"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|fs
operator|.
name|putObjectDirect
argument_list|(
name|put
argument_list|)
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"put object was created"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|createNonRecursive (Path path)
specifier|private
name|FSDataOutputStream
name|createNonRecursive
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFileSystem
argument_list|()
operator|.
name|createNonRecursive
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
operator|(
name|short
operator|)
literal|4096
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Touch a path, return the full path.    * @param name relative name    * @return the path    * @throws IOException IO failure    */
DECL|method|touchFile (String name)
name|Path
name|touchFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|path
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
comment|/**    * Create a file with the data, return the path.    * @param name relative name    * @param data data to write    * @return the path    * @throws IOException IO failure    */
DECL|method|mkFile (String name, byte[] data)
name|Path
name|mkFile
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|f
init|=
name|path
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|f
argument_list|,
literal|true
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/**    * The assumption here is that 0-byte files uploaded in a single PUT    * always have the same checksum, including stores with encryption.    * @throws Throwable on a failure    */
annotation|@
name|Test
DECL|method|testEmptyFileChecksums ()
specifier|public
name|void
name|testEmptyFileChecksums
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|file1
init|=
name|touchFile
argument_list|(
literal|"file1"
argument_list|)
decl_stmt|;
name|EtagChecksum
name|checksum1
init|=
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|file1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Checksum for {}: {}"
argument_list|,
name|file1
argument_list|,
name|checksum1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"file 1 checksum"
argument_list|,
name|checksum1
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
literal|"file 1 checksum"
argument_list|,
literal|0
argument_list|,
name|checksum1
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"checksums"
argument_list|,
name|checksum1
argument_list|,
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|touchFile
argument_list|(
literal|"file2"
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that different file contents have different    * checksums, and that that they aren't the same as the empty file.    * @throws Throwable failure    */
annotation|@
name|Test
DECL|method|testNonEmptyFileChecksums ()
specifier|public
name|void
name|testNonEmptyFileChecksums
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|file3
init|=
name|mkFile
argument_list|(
literal|"file3"
argument_list|,
name|HELLO
argument_list|)
decl_stmt|;
specifier|final
name|EtagChecksum
name|checksum1
init|=
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|file3
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"file 3 checksum"
argument_list|,
name|checksum1
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|file4
init|=
name|touchFile
argument_list|(
literal|"file4"
argument_list|)
decl_stmt|;
specifier|final
name|EtagChecksum
name|checksum2
init|=
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|file4
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
literal|"checksums"
argument_list|,
name|checksum1
argument_list|,
name|checksum2
argument_list|)
expr_stmt|;
comment|// overwrite
name|createFile
argument_list|(
name|fs
argument_list|,
name|file4
argument_list|,
literal|true
argument_list|,
literal|"hello, world"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|checksum2
argument_list|,
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|file4
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that on an unencrypted store, the checksum of two non-empty    * (single PUT) files is the same if the data is the same.    * This will fail if the bucket has S3 default encryption enabled.    * @throws Throwable failure    */
annotation|@
name|Test
DECL|method|testNonEmptyFileChecksumsUnencrypted ()
specifier|public
name|void
name|testNonEmptyFileChecksumsUnencrypted
parameter_list|()
throws|throws
name|Throwable
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|encryptionAlgorithm
argument_list|()
operator|.
name|equals
argument_list|(
name|S3AEncryptionMethods
operator|.
name|NONE
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|EtagChecksum
name|checksum1
init|=
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|mkFile
argument_list|(
literal|"file5"
argument_list|,
name|HELLO
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"file 3 checksum"
argument_list|,
name|checksum1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"checksums"
argument_list|,
name|checksum1
argument_list|,
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|mkFile
argument_list|(
literal|"file6"
argument_list|,
name|HELLO
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|encryptionAlgorithm ()
specifier|private
name|S3AEncryptionMethods
name|encryptionAlgorithm
parameter_list|()
block|{
return|return
name|getFileSystem
argument_list|()
operator|.
name|getServerSideEncryptionAlgorithm
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testNegativeLength ()
specifier|public
name|void
name|testNegativeLength
parameter_list|()
throws|throws
name|Throwable
block|{
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|getFileSystem
argument_list|()
operator|.
name|getFileChecksum
argument_list|(
name|mkFile
argument_list|(
literal|"negative"
argument_list|,
name|HELLO
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLengthPastEOF ()
specifier|public
name|void
name|testLengthPastEOF
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|f
init|=
name|mkFile
argument_list|(
literal|"file5"
argument_list|,
name|HELLO
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|f
argument_list|,
name|HELLO
operator|.
name|length
argument_list|)
argument_list|,
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|f
argument_list|,
name|HELLO
operator|.
name|length
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

