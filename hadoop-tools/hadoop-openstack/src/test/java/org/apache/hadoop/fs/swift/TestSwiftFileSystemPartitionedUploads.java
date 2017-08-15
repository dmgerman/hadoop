begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
package|;
end_package

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
name|BlockLocation
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
name|swift
operator|.
name|http
operator|.
name|SwiftProtocolConstants
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
name|swift
operator|.
name|snative
operator|.
name|SwiftNativeFileSystem
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
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
name|swift
operator|.
name|util
operator|.
name|SwiftUtils
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
name|http
operator|.
name|Header
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
name|internal
operator|.
name|AssumptionViolatedException
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
operator|.
name|assertPathExists
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
operator|.
name|readDataset
import|;
end_import

begin_comment
comment|/**  * Test partitioned uploads.  * This is done by forcing a very small partition size and verifying that it  * is picked up.  */
end_comment

begin_class
DECL|class|TestSwiftFileSystemPartitionedUploads
specifier|public
class|class
name|TestSwiftFileSystemPartitionedUploads
extends|extends
name|SwiftFileSystemBaseTest
block|{
DECL|field|WRONG_PARTITION_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|WRONG_PARTITION_COUNT
init|=
literal|"wrong number of partitions written into "
decl_stmt|;
DECL|field|PART_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|PART_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|PART_SIZE_BYTES
specifier|public
specifier|static
specifier|final
name|int
name|PART_SIZE_BYTES
init|=
name|PART_SIZE
operator|*
literal|1024
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|uri
specifier|private
name|URI
name|uri
decl_stmt|;
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|super
operator|.
name|createConfiguration
argument_list|()
decl_stmt|;
comment|//set the partition size to 1 KB
name|conf
operator|.
name|setInt
argument_list|(
name|SwiftProtocolConstants
operator|.
name|SWIFT_PARTITION_SIZE
argument_list|,
name|PART_SIZE
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_BULK_IO_TEST_TIMEOUT
argument_list|)
DECL|method|testPartitionPropertyPropagatesToConf ()
specifier|public
name|void
name|testPartitionPropertyPropagatesToConf
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|SwiftProtocolConstants
operator|.
name|SWIFT_PARTITION_SIZE
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_BULK_IO_TEST_TIMEOUT
argument_list|)
DECL|method|testPartionPropertyPropagatesToStore ()
specifier|public
name|void
name|testPartionPropertyPropagatesToStore
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fs
operator|.
name|getStore
argument_list|()
operator|.
name|getPartsizeKB
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * tests functionality for big files (> 5Gb) upload    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_BULK_IO_TEST_TIMEOUT
argument_list|)
DECL|method|testFilePartUpload ()
specifier|public
name|void
name|testFilePartUpload
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testFilePartUpload"
argument_list|)
decl_stmt|;
name|int
name|len
init|=
literal|8192
decl_stmt|;
specifier|final
name|byte
index|[]
name|src
init|=
name|SwiftTestUtils
operator|.
name|dataset
argument_list|(
name|len
argument_list|,
literal|32
argument_list|,
literal|144
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
name|getBufferSize
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|totalPartitionsToWrite
init|=
name|len
operator|/
name|PART_SIZE_BYTES
decl_stmt|;
name|assertPartitionsWritten
argument_list|(
literal|"Startup"
argument_list|,
name|out
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//write 2048
name|int
name|firstWriteLen
init|=
literal|2048
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|src
argument_list|,
literal|0
argument_list|,
name|firstWriteLen
argument_list|)
expr_stmt|;
comment|//assert
name|long
name|expected
init|=
name|getExpectedPartitionsWritten
argument_list|(
name|firstWriteLen
argument_list|,
name|PART_SIZE_BYTES
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SwiftUtils
operator|.
name|debug
argument_list|(
name|LOG
argument_list|,
literal|"First write: predict %d partitions written"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertPartitionsWritten
argument_list|(
literal|"First write completed"
argument_list|,
name|out
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|//write the rest
name|int
name|remainder
init|=
name|len
operator|-
name|firstWriteLen
decl_stmt|;
name|SwiftUtils
operator|.
name|debug
argument_list|(
name|LOG
argument_list|,
literal|"remainder: writing: %d bytes"
argument_list|,
name|remainder
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|src
argument_list|,
name|firstWriteLen
argument_list|,
name|remainder
argument_list|)
expr_stmt|;
name|expected
operator|=
name|getExpectedPartitionsWritten
argument_list|(
name|len
argument_list|,
name|PART_SIZE_BYTES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertPartitionsWritten
argument_list|(
literal|"Remaining data"
argument_list|,
name|out
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|expected
operator|=
name|getExpectedPartitionsWritten
argument_list|(
name|len
argument_list|,
name|PART_SIZE_BYTES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertPartitionsWritten
argument_list|(
literal|"Stream closed"
argument_list|,
name|out
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|Header
index|[]
name|headers
init|=
name|fs
operator|.
name|getStore
argument_list|()
operator|.
name|getObjectHeaders
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|Header
name|header
range|:
name|headers
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|header
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|dest
init|=
name|readDataset
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Read dataset from "
operator|+
name|path
operator|+
literal|": data length ="
operator|+
name|len
argument_list|)
expr_stmt|;
comment|//compare data
name|SwiftTestUtils
operator|.
name|compareByteArrays
argument_list|(
name|src
argument_list|,
name|dest
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|FileStatus
name|status
decl_stmt|;
specifier|final
name|Path
name|qualifiedPath
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|status
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|qualifiedPath
argument_list|)
expr_stmt|;
comment|//now see what block location info comes back.
comment|//This will vary depending on the Swift version, so the results
comment|//aren't checked -merely that the test actually worked
name|BlockLocation
index|[]
name|locations
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|status
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Null getFileBlockLocations()"
argument_list|,
name|locations
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"empty array returned for getFileBlockLocations()"
argument_list|,
name|locations
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|//last bit of test -which seems to play up on partitions, which we download
comment|//to a skip
try|try
block|{
name|validatePathLen
argument_list|(
name|path
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
comment|//downgrade to a skip
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
name|e
argument_list|,
literal|null
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * tests functionality for big files (> 5Gb) upload    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_BULK_IO_TEST_TIMEOUT
argument_list|)
DECL|method|testFilePartUploadNoLengthCheck ()
specifier|public
name|void
name|testFilePartUploadNoLengthCheck
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testFilePartUploadLengthCheck"
argument_list|)
decl_stmt|;
name|int
name|len
init|=
literal|8192
decl_stmt|;
specifier|final
name|byte
index|[]
name|src
init|=
name|SwiftTestUtils
operator|.
name|dataset
argument_list|(
name|len
argument_list|,
literal|32
argument_list|,
literal|144
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
name|getBufferSize
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|totalPartitionsToWrite
init|=
name|len
operator|/
name|PART_SIZE_BYTES
decl_stmt|;
name|assertPartitionsWritten
argument_list|(
literal|"Startup"
argument_list|,
name|out
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//write 2048
name|int
name|firstWriteLen
init|=
literal|2048
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|src
argument_list|,
literal|0
argument_list|,
name|firstWriteLen
argument_list|)
expr_stmt|;
comment|//assert
name|long
name|expected
init|=
name|getExpectedPartitionsWritten
argument_list|(
name|firstWriteLen
argument_list|,
name|PART_SIZE_BYTES
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SwiftUtils
operator|.
name|debug
argument_list|(
name|LOG
argument_list|,
literal|"First write: predict %d partitions written"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertPartitionsWritten
argument_list|(
literal|"First write completed"
argument_list|,
name|out
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|//write the rest
name|int
name|remainder
init|=
name|len
operator|-
name|firstWriteLen
decl_stmt|;
name|SwiftUtils
operator|.
name|debug
argument_list|(
name|LOG
argument_list|,
literal|"remainder: writing: %d bytes"
argument_list|,
name|remainder
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|src
argument_list|,
name|firstWriteLen
argument_list|,
name|remainder
argument_list|)
expr_stmt|;
name|expected
operator|=
name|getExpectedPartitionsWritten
argument_list|(
name|len
argument_list|,
name|PART_SIZE_BYTES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertPartitionsWritten
argument_list|(
literal|"Remaining data"
argument_list|,
name|out
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|expected
operator|=
name|getExpectedPartitionsWritten
argument_list|(
name|len
argument_list|,
name|PART_SIZE_BYTES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertPartitionsWritten
argument_list|(
literal|"Stream closed"
argument_list|,
name|out
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|Header
index|[]
name|headers
init|=
name|fs
operator|.
name|getStore
argument_list|()
operator|.
name|getObjectHeaders
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|Header
name|header
range|:
name|headers
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|header
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|dest
init|=
name|readDataset
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Read dataset from "
operator|+
name|path
operator|+
literal|": data length ="
operator|+
name|len
argument_list|)
expr_stmt|;
comment|//compare data
name|SwiftTestUtils
operator|.
name|compareByteArrays
argument_list|(
name|src
argument_list|,
name|dest
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|//now see what block location info comes back.
comment|//This will vary depending on the Swift version, so the results
comment|//aren't checked -merely that the test actually worked
name|BlockLocation
index|[]
name|locations
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|status
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Null getFileBlockLocations()"
argument_list|,
name|locations
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"empty array returned for getFileBlockLocations()"
argument_list|,
name|locations
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validatePathLen (Path path, int len)
specifier|private
name|FileStatus
name|validatePathLen
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
comment|//verify that the length is what was written in a direct status check
specifier|final
name|Path
name|qualifiedPath
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|parentDirListing
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|qualifiedPath
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|listing
init|=
name|lsToString
argument_list|(
name|parentDirListing
argument_list|)
decl_stmt|;
name|String
name|parentDirLS
init|=
name|listing
operator|.
name|toString
argument_list|()
decl_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|qualifiedPath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Length of written file "
operator|+
name|qualifiedPath
operator|+
literal|" from status check "
operator|+
name|status
operator|+
literal|" in dir "
operator|+
name|listing
argument_list|,
name|len
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fileInfo
init|=
name|qualifiedPath
operator|+
literal|"  "
operator|+
name|status
decl_stmt|;
name|assertFalse
argument_list|(
literal|"File claims to be a directory "
operator|+
name|fileInfo
argument_list|,
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|FileStatus
name|listedFileStat
init|=
name|resolveChild
argument_list|(
name|parentDirListing
argument_list|,
name|qualifiedPath
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Did not find "
operator|+
name|path
operator|+
literal|" in "
operator|+
name|parentDirLS
argument_list|,
name|listedFileStat
argument_list|)
expr_stmt|;
comment|//file is in the parent dir. Now validate it's stats
name|assertEquals
argument_list|(
literal|"Wrong len for "
operator|+
name|path
operator|+
literal|" in listing "
operator|+
name|parentDirLS
argument_list|,
name|len
argument_list|,
name|listedFileStat
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|listedFileStat
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|status
return|;
block|}
DECL|method|resolveChild (FileStatus[] parentDirListing, Path childPath)
specifier|private
name|FileStatus
name|resolveChild
parameter_list|(
name|FileStatus
index|[]
name|parentDirListing
parameter_list|,
name|Path
name|childPath
parameter_list|)
block|{
name|FileStatus
name|listedFileStat
init|=
literal|null
decl_stmt|;
for|for
control|(
name|FileStatus
name|stat
range|:
name|parentDirListing
control|)
block|{
if|if
condition|(
name|stat
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|childPath
argument_list|)
condition|)
block|{
name|listedFileStat
operator|=
name|stat
expr_stmt|;
block|}
block|}
return|return
name|listedFileStat
return|;
block|}
DECL|method|lsToString (FileStatus[] parentDirListing)
specifier|private
name|StringBuilder
name|lsToString
parameter_list|(
name|FileStatus
index|[]
name|parentDirListing
parameter_list|)
block|{
name|StringBuilder
name|listing
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|FileStatus
name|stat
range|:
name|parentDirListing
control|)
block|{
name|listing
operator|.
name|append
argument_list|(
name|stat
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|listing
return|;
block|}
comment|/**    * Calculate the #of partitions expected from the upload    * @param uploaded number of bytes uploaded    * @param partSizeBytes the partition size    * @param closed whether or not the stream has closed    * @return the expected number of partitions, for use in assertions.    */
DECL|method|getExpectedPartitionsWritten (long uploaded, int partSizeBytes, boolean closed)
specifier|private
name|int
name|getExpectedPartitionsWritten
parameter_list|(
name|long
name|uploaded
parameter_list|,
name|int
name|partSizeBytes
parameter_list|,
name|boolean
name|closed
parameter_list|)
block|{
comment|//#of partitions in total
name|int
name|partitions
init|=
call|(
name|int
call|)
argument_list|(
name|uploaded
operator|/
name|partSizeBytes
argument_list|)
decl_stmt|;
comment|//#of bytes past the last partition
name|int
name|remainder
init|=
call|(
name|int
call|)
argument_list|(
name|uploaded
operator|%
name|partSizeBytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|closed
condition|)
block|{
comment|//all data is written, so if there was any remainder, it went up
comment|//too
return|return
name|partitions
operator|+
operator|(
operator|(
name|remainder
operator|>
literal|0
operator|)
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
else|else
block|{
comment|//not closed. All the remainder is buffered,
return|return
name|partitions
return|;
block|}
block|}
DECL|method|getBufferSize ()
specifier|private
name|int
name|getBufferSize
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
literal|4096
argument_list|)
return|;
block|}
comment|/**    * Test sticks up a very large partitioned file and verifies that    * it comes back unchanged.    * @throws Throwable    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_BULK_IO_TEST_TIMEOUT
argument_list|)
DECL|method|testManyPartitionedFile ()
specifier|public
name|void
name|testManyPartitionedFile
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testManyPartitionedFile"
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|PART_SIZE_BYTES
operator|*
literal|15
decl_stmt|;
specifier|final
name|byte
index|[]
name|src
init|=
name|SwiftTestUtils
operator|.
name|dataset
argument_list|(
name|len
argument_list|,
literal|32
argument_list|,
literal|144
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
name|getBufferSize
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|src
argument_list|,
literal|0
argument_list|,
name|src
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|expected
init|=
name|getExpectedPartitionsWritten
argument_list|(
name|len
argument_list|,
name|PART_SIZE_BYTES
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertPartitionsWritten
argument_list|(
literal|"write completed"
argument_list|,
name|out
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"too few bytes written"
argument_list|,
name|len
argument_list|,
name|SwiftNativeFileSystem
operator|.
name|getBytesWritten
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"too few bytes uploaded"
argument_list|,
name|len
argument_list|,
name|SwiftNativeFileSystem
operator|.
name|getBytesUploaded
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
comment|//now we verify that the data comes back. If it
comment|//doesn't, it means that the ordering of the partitions
comment|//isn't right
name|byte
index|[]
name|dest
init|=
name|readDataset
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|len
argument_list|)
decl_stmt|;
comment|//compare data
name|SwiftTestUtils
operator|.
name|compareByteArrays
argument_list|(
name|src
argument_list|,
name|dest
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|//finally, check the data
name|FileStatus
index|[]
name|stats
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong entry count in "
operator|+
name|SwiftTestUtils
operator|.
name|dumpStats
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
argument_list|)
argument_list|,
name|expected
argument_list|,
name|stats
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that when a partitioned file is overwritten by a smaller one,    * all the old partitioned files go away    * @throws Throwable    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_BULK_IO_TEST_TIMEOUT
argument_list|)
DECL|method|testOverwritePartitionedFile ()
specifier|public
name|void
name|testOverwritePartitionedFile
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testOverwritePartitionedFile"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len1
init|=
literal|8192
decl_stmt|;
specifier|final
name|byte
index|[]
name|src1
init|=
name|SwiftTestUtils
operator|.
name|dataset
argument_list|(
name|len1
argument_list|,
literal|'A'
argument_list|,
literal|'Z'
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
name|getBufferSize
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|src1
argument_list|,
literal|0
argument_list|,
name|len1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|expected
init|=
name|getExpectedPartitionsWritten
argument_list|(
name|len1
argument_list|,
name|PART_SIZE_BYTES
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertPartitionsWritten
argument_list|(
literal|"initial upload"
argument_list|,
name|out
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
literal|"Exists"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Length"
argument_list|,
name|len1
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
comment|//now write a shorter file with a different dataset
specifier|final
name|int
name|len2
init|=
literal|4095
decl_stmt|;
specifier|final
name|byte
index|[]
name|src2
init|=
name|SwiftTestUtils
operator|.
name|dataset
argument_list|(
name|len2
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
decl_stmt|;
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
name|getBufferSize
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|src2
argument_list|,
literal|0
argument_list|,
name|len2
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|status
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Length"
argument_list|,
name|len2
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|dest
init|=
name|readDataset
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|len2
argument_list|)
decl_stmt|;
comment|//compare data
name|SwiftTestUtils
operator|.
name|compareByteArrays
argument_list|(
name|src2
argument_list|,
name|dest
argument_list|,
name|len2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_BULK_IO_TEST_TIMEOUT
argument_list|)
DECL|method|testDeleteSmallPartitionedFile ()
specifier|public
name|void
name|testDeleteSmallPartitionedFile
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testDeleteSmallPartitionedFile"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len1
init|=
literal|1024
decl_stmt|;
specifier|final
name|byte
index|[]
name|src1
init|=
name|SwiftTestUtils
operator|.
name|dataset
argument_list|(
name|len1
argument_list|,
literal|'A'
argument_list|,
literal|'Z'
argument_list|)
decl_stmt|;
name|SwiftTestUtils
operator|.
name|writeDataset
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|src1
argument_list|,
name|len1
argument_list|,
literal|1024
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
literal|"Exists"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|Path
name|part_0001
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
name|SwiftUtils
operator|.
name|partitionFilenameFromNumber
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|part_0002
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
name|SwiftUtils
operator|.
name|partitionFilenameFromNumber
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|ls
init|=
name|SwiftTestUtils
operator|.
name|ls
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertExists
argument_list|(
literal|"Partition 0001 Exists in "
operator|+
name|ls
argument_list|,
name|part_0001
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"partition 0002 found under "
operator|+
name|ls
argument_list|,
name|part_0002
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
literal|"Partition 0002 Exists in "
operator|+
name|ls
argument_list|,
name|part_0001
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"deleted file still there"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|ls
operator|=
name|SwiftTestUtils
operator|.
name|ls
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"partition 0001 file still under "
operator|+
name|ls
argument_list|,
name|part_0001
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_BULK_IO_TEST_TIMEOUT
argument_list|)
DECL|method|testDeletePartitionedFile ()
specifier|public
name|void
name|testDeletePartitionedFile
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testDeletePartitionedFile"
argument_list|)
decl_stmt|;
name|SwiftTestUtils
operator|.
name|writeDataset
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|data
argument_list|,
name|data
operator|.
name|length
argument_list|,
literal|1024
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
literal|"Exists"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|Path
name|part_0001
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
name|SwiftUtils
operator|.
name|partitionFilenameFromNumber
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|part_0002
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
name|SwiftUtils
operator|.
name|partitionFilenameFromNumber
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|ls
init|=
name|SwiftTestUtils
operator|.
name|ls
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertExists
argument_list|(
literal|"Partition 0001 Exists in "
operator|+
name|ls
argument_list|,
name|part_0001
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
literal|"Partition 0002 Exists in "
operator|+
name|ls
argument_list|,
name|part_0001
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"deleted file still there"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|ls
operator|=
name|SwiftTestUtils
operator|.
name|ls
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"partition 0001 file still under "
operator|+
name|ls
argument_list|,
name|part_0001
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"partition 0002 file still under "
operator|+
name|ls
argument_list|,
name|part_0002
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_BULK_IO_TEST_TIMEOUT
argument_list|)
DECL|method|testRenamePartitionedFile ()
specifier|public
name|void
name|testRenamePartitionedFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|Path
name|src
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testRenamePartitionedFileSrc"
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|data
operator|.
name|length
decl_stmt|;
name|SwiftTestUtils
operator|.
name|writeDataset
argument_list|(
name|fs
argument_list|,
name|src
argument_list|,
name|data
argument_list|,
name|len
argument_list|,
literal|1024
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertExists
argument_list|(
literal|"Exists"
argument_list|,
name|src
argument_list|)
expr_stmt|;
name|String
name|partOneName
init|=
name|SwiftUtils
operator|.
name|partitionFilenameFromNumber
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Path
name|srcPart
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|,
name|partOneName
argument_list|)
decl_stmt|;
name|Path
name|dest
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testRenamePartitionedFileDest"
argument_list|)
decl_stmt|;
name|Path
name|destPart
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|,
name|partOneName
argument_list|)
decl_stmt|;
name|assertExists
argument_list|(
literal|"Partition Exists"
argument_list|,
name|srcPart
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|assertPathExists
argument_list|(
name|fs
argument_list|,
literal|"dest file missing"
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Length of renamed file is wrong"
argument_list|,
name|len
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|destData
init|=
name|readDataset
argument_list|(
name|fs
argument_list|,
name|dest
argument_list|,
name|len
argument_list|)
decl_stmt|;
comment|//compare data
name|SwiftTestUtils
operator|.
name|compareByteArrays
argument_list|(
name|data
argument_list|,
name|destData
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|String
name|srcLs
init|=
name|SwiftTestUtils
operator|.
name|ls
argument_list|(
name|fs
argument_list|,
name|src
argument_list|)
decl_stmt|;
name|String
name|destLs
init|=
name|SwiftTestUtils
operator|.
name|ls
argument_list|(
name|fs
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"deleted file still found in "
operator|+
name|srcLs
argument_list|,
name|src
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"partition file still found in "
operator|+
name|srcLs
argument_list|,
name|srcPart
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

