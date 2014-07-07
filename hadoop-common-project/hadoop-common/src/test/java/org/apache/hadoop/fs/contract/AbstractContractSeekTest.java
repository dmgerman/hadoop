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
name|CommonConfigurationKeysPublic
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
name|io
operator|.
name|IOUtils
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|util
operator|.
name|Random
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
name|cleanup
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
name|dataset
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
name|verifyRead
import|;
end_import

begin_comment
comment|/**  * Test Seek operations  */
end_comment

begin_class
DECL|class|AbstractContractSeekTest
specifier|public
specifier|abstract
class|class
name|AbstractContractSeekTest
extends|extends
name|AbstractFSContractTestBase
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
name|AbstractContractSeekTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_RANDOM_SEEK_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RANDOM_SEEK_COUNT
init|=
literal|100
decl_stmt|;
DECL|field|testPath
specifier|private
name|Path
name|testPath
decl_stmt|;
DECL|field|smallSeekFile
specifier|private
name|Path
name|smallSeekFile
decl_stmt|;
DECL|field|zeroByteFile
specifier|private
name|Path
name|zeroByteFile
decl_stmt|;
DECL|field|instream
specifier|private
name|FSDataInputStream
name|instream
decl_stmt|;
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|skipIfUnsupported
argument_list|(
name|SUPPORTS_SEEK
argument_list|)
expr_stmt|;
comment|//delete the test directory
name|testPath
operator|=
name|getContract
argument_list|()
operator|.
name|getTestPath
argument_list|()
expr_stmt|;
name|smallSeekFile
operator|=
name|path
argument_list|(
literal|"seekfile.txt"
argument_list|)
expr_stmt|;
name|zeroByteFile
operator|=
name|path
argument_list|(
literal|"zero.txt"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|block
init|=
name|dataset
argument_list|(
name|TEST_FILE_LEN
argument_list|,
literal|0
argument_list|,
literal|255
argument_list|)
decl_stmt|;
comment|//this file now has a simple rule: offset => value
name|createFile
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|smallSeekFile
argument_list|,
literal|false
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|touch
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|zeroByteFile
argument_list|)
expr_stmt|;
block|}
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
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|instream
argument_list|)
expr_stmt|;
name|instream
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSeekZeroByteFile ()
specifier|public
name|void
name|testSeekZeroByteFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"seek and read a 0 byte file"
argument_list|)
expr_stmt|;
name|instream
operator|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|zeroByteFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
comment|//expect initial read to fai;
name|int
name|result
init|=
name|instream
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertMinusOne
argument_list|(
literal|"initial byte read"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
comment|//expect that seek to 0 works
name|instream
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|//reread, expect same exception
name|result
operator|=
name|instream
operator|.
name|read
argument_list|()
expr_stmt|;
name|assertMinusOne
argument_list|(
literal|"post-seek byte read"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
name|instream
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMinusOne
argument_list|(
literal|"post-seek buffer read"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockReadZeroByteFile ()
specifier|public
name|void
name|testBlockReadZeroByteFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"do a block read on a 0 byte file"
argument_list|)
expr_stmt|;
name|instream
operator|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|zeroByteFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
comment|//expect that seek to 0 works
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|int
name|result
init|=
name|instream
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertMinusOne
argument_list|(
literal|"block read zero byte file"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/**    * Seek and read on a closed file.    * Some filesystems let callers seek on a closed file -these must    * still fail on the subsequent reads.    * @throws Throwable    */
annotation|@
name|Test
DECL|method|testSeekReadClosedFile ()
specifier|public
name|void
name|testSeekReadClosedFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|boolean
name|supportsSeekOnClosedFiles
init|=
name|isSupported
argument_list|(
name|SUPPORTS_SEEK_ON_CLOSED_FILE
argument_list|)
decl_stmt|;
name|instream
operator|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|smallSeekFile
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"Stream is of type "
operator|+
name|instream
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|instream
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|instream
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|supportsSeekOnClosedFiles
condition|)
block|{
name|fail
argument_list|(
literal|"seek succeeded on a closed stream"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//expected a closed file
block|}
try|try
block|{
name|int
name|data
init|=
name|instream
operator|.
name|available
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"read() succeeded on a closed stream, got "
operator|+
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//expected a closed file
block|}
try|try
block|{
name|int
name|data
init|=
name|instream
operator|.
name|read
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"read() succeeded on a closed stream, got "
operator|+
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//expected a closed file
block|}
try|try
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|int
name|result
init|=
name|instream
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"read(buffer, 0, 1) succeeded on a closed stream, got "
operator|+
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//expected a closed file
block|}
comment|//what position does a closed file have?
try|try
block|{
name|long
name|offset
init|=
name|instream
operator|.
name|getPos
argument_list|()
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// its valid to raise error here; but the test is applied to make
comment|// sure there's no other exception like an NPE.
block|}
comment|//and close again
name|instream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNegativeSeek ()
specifier|public
name|void
name|testNegativeSeek
parameter_list|()
throws|throws
name|Throwable
block|{
name|instream
operator|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|smallSeekFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|instream
operator|.
name|seek
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|long
name|p
init|=
name|instream
operator|.
name|getPos
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Seek to -1 returned a position of "
operator|+
name|p
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|instream
operator|.
name|read
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"expected an exception, got data "
operator|+
name|result
operator|+
literal|" at a position of "
operator|+
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|//bad seek -expected
name|handleExpectedException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//bad seek -expected, but not as preferred as an EOFException
name|handleRelaxedException
argument_list|(
literal|"a negative seek"
argument_list|,
literal|"EOFException"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSeekFile ()
specifier|public
name|void
name|testSeekFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"basic seek operations"
argument_list|)
expr_stmt|;
name|instream
operator|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|smallSeekFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
comment|//expect that seek to 0 works
name|instream
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|instream
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
name|instream
operator|.
name|seek
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|instream
operator|.
name|seek
argument_list|(
literal|63
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|63
argument_list|,
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSeekAndReadPastEndOfFile ()
specifier|public
name|void
name|testSeekAndReadPastEndOfFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"verify that reading past the last bytes in the file returns -1"
argument_list|)
expr_stmt|;
name|instream
operator|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|smallSeekFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
comment|//expect that seek to 0 works
comment|//go just before the end
name|instream
operator|.
name|seek
argument_list|(
name|TEST_FILE_LEN
operator|-
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Premature EOF"
argument_list|,
name|instream
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Premature EOF"
argument_list|,
name|instream
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertMinusOne
argument_list|(
literal|"read past end of file"
argument_list|,
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSeekPastEndOfFileThenReseekAndRead ()
specifier|public
name|void
name|testSeekPastEndOfFileThenReseekAndRead
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"do a seek past the EOF, then verify the stream recovers"
argument_list|)
expr_stmt|;
name|instream
operator|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|smallSeekFile
argument_list|)
expr_stmt|;
comment|//go just before the end. This may or may not fail; it may be delayed until the
comment|//read
name|boolean
name|canSeekPastEOF
init|=
operator|!
name|getContract
argument_list|()
operator|.
name|isSupported
argument_list|(
name|ContractOptions
operator|.
name|REJECTS_SEEK_PAST_EOF
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|instream
operator|.
name|seek
argument_list|(
name|TEST_FILE_LEN
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|//if this doesn't trigger, then read() is expected to fail
name|assertMinusOne
argument_list|(
literal|"read after seeking past EOF"
argument_list|,
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|//This is an error iff the FS claims to be able to seek past the EOF
if|if
condition|(
name|canSeekPastEOF
condition|)
block|{
comment|//a failure wasn't expected
throw|throw
name|e
throw|;
block|}
name|handleExpectedException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//This is an error iff the FS claims to be able to seek past the EOF
if|if
condition|(
name|canSeekPastEOF
condition|)
block|{
comment|//a failure wasn't expected
throw|throw
name|e
throw|;
block|}
name|handleRelaxedException
argument_list|(
literal|"a seek past the end of the file"
argument_list|,
literal|"EOFException"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|//now go back and try to read from a valid point in the file
name|instream
operator|.
name|seek
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Premature EOF"
argument_list|,
name|instream
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Seek round a file bigger than IO buffers    * @throws Throwable    */
annotation|@
name|Test
DECL|method|testSeekBigFile ()
specifier|public
name|void
name|testSeekBigFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Seek round a large file and verify the bytes are what is expected"
argument_list|)
expr_stmt|;
name|Path
name|testSeekFile
init|=
name|path
argument_list|(
literal|"bigseekfile.txt"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|dataset
argument_list|(
literal|65536
argument_list|,
literal|0
argument_list|,
literal|255
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|testSeekFile
argument_list|,
literal|false
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|instream
operator|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|testSeekFile
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
comment|//expect that seek to 0 works
name|instream
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|instream
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
comment|//do seek 32KB ahead
name|instream
operator|.
name|seek
argument_list|(
literal|32768
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"@32768"
argument_list|,
name|block
index|[
literal|32768
index|]
argument_list|,
operator|(
name|byte
operator|)
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|instream
operator|.
name|seek
argument_list|(
literal|40000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"@40000"
argument_list|,
name|block
index|[
literal|40000
index|]
argument_list|,
operator|(
name|byte
operator|)
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|instream
operator|.
name|seek
argument_list|(
literal|8191
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"@8191"
argument_list|,
name|block
index|[
literal|8191
index|]
argument_list|,
operator|(
name|byte
operator|)
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|instream
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"@0"
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPositionedBulkReadDoesntChangePosition ()
specifier|public
name|void
name|testPositionedBulkReadDoesntChangePosition
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"verify that a positioned read does not change the getPos() value"
argument_list|)
expr_stmt|;
name|Path
name|testSeekFile
init|=
name|path
argument_list|(
literal|"bigseekfile.txt"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|dataset
argument_list|(
literal|65536
argument_list|,
literal|0
argument_list|,
literal|255
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|testSeekFile
argument_list|,
literal|false
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|instream
operator|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|testSeekFile
argument_list|)
expr_stmt|;
name|instream
operator|.
name|seek
argument_list|(
literal|39999
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|-
literal|1
operator|!=
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40000
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|readBuffer
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
name|instream
operator|.
name|read
argument_list|(
literal|128
argument_list|,
name|readBuffer
argument_list|,
literal|0
argument_list|,
name|readBuffer
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//have gone back
name|assertEquals
argument_list|(
literal|40000
argument_list|,
name|instream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
comment|//content is the same too
name|assertEquals
argument_list|(
literal|"@40000"
argument_list|,
name|block
index|[
literal|40000
index|]
argument_list|,
operator|(
name|byte
operator|)
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
comment|//now verify the picked up data
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"@"
operator|+
name|i
argument_list|,
name|block
index|[
name|i
operator|+
literal|128
index|]
argument_list|,
name|readBuffer
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Lifted from TestLocalFileSystem:    * Regression test for HADOOP-9307: BufferedFSInputStream returning    * wrong results after certain sequences of seeks and reads.    */
annotation|@
name|Test
DECL|method|testRandomSeeks ()
specifier|public
name|void
name|testRandomSeeks
parameter_list|()
throws|throws
name|Throwable
block|{
name|int
name|limit
init|=
name|getContract
argument_list|()
operator|.
name|getLimit
argument_list|(
name|TEST_RANDOM_SEEK_COUNT
argument_list|,
name|DEFAULT_RANDOM_SEEK_COUNT
argument_list|)
decl_stmt|;
name|describe
argument_list|(
literal|"Testing "
operator|+
name|limit
operator|+
literal|" random seeks"
argument_list|)
expr_stmt|;
name|int
name|filesize
init|=
literal|10
operator|*
literal|1024
decl_stmt|;
name|byte
index|[]
name|buf
init|=
name|dataset
argument_list|(
name|filesize
argument_list|,
literal|0
argument_list|,
literal|255
argument_list|)
decl_stmt|;
name|Path
name|randomSeekFile
init|=
name|path
argument_list|(
literal|"testrandomseeks.bin"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|randomSeekFile
argument_list|,
literal|false
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|FSDataInputStream
name|stm
init|=
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|randomSeekFile
argument_list|)
decl_stmt|;
comment|// Record the sequence of seeks and reads which trigger a failure.
name|int
index|[]
name|seeks
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
name|int
index|[]
name|reads
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
try|try
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
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|int
name|seekOff
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|toRead
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|buf
operator|.
name|length
operator|-
name|seekOff
argument_list|,
literal|32000
argument_list|)
argument_list|)
decl_stmt|;
name|seeks
index|[
name|i
operator|%
name|seeks
operator|.
name|length
index|]
operator|=
name|seekOff
expr_stmt|;
name|reads
index|[
name|i
operator|%
name|reads
operator|.
name|length
index|]
operator|=
name|toRead
expr_stmt|;
name|verifyRead
argument_list|(
name|stm
argument_list|,
name|buf
argument_list|,
name|seekOff
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AssertionError
name|afe
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Sequence of actions:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|seeks
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"seek @ "
argument_list|)
operator|.
name|append
argument_list|(
name|seeks
index|[
name|j
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
literal|"read "
argument_list|)
operator|.
name|append
argument_list|(
name|reads
index|[
name|j
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|error
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|afe
throw|;
block|}
finally|finally
block|{
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

