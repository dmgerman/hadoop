begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.util
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
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|exceptions
operator|.
name|SwiftConfigurationException
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
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Utilities used across test cases  */
end_comment

begin_class
DECL|class|SwiftTestUtils
specifier|public
class|class
name|SwiftTestUtils
extends|extends
name|org
operator|.
name|junit
operator|.
name|Assert
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SwiftTestUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_FS_SWIFT
specifier|public
specifier|static
specifier|final
name|String
name|TEST_FS_SWIFT
init|=
literal|"test.fs.swift.name"
decl_stmt|;
DECL|field|IO_FILE_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|IO_FILE_BUFFER_SIZE
init|=
literal|"io.file.buffer.size"
decl_stmt|;
comment|/**    * Get the test URI    * @param conf configuration    * @throws SwiftConfigurationException missing parameter or bad URI    */
DECL|method|getServiceURI (Configuration conf)
specifier|public
specifier|static
name|URI
name|getServiceURI
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|SwiftConfigurationException
block|{
name|String
name|instance
init|=
name|conf
operator|.
name|get
argument_list|(
name|TEST_FS_SWIFT
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SwiftConfigurationException
argument_list|(
literal|"Missing configuration entry "
operator|+
name|TEST_FS_SWIFT
argument_list|)
throw|;
block|}
try|try
block|{
return|return
operator|new
name|URI
argument_list|(
name|instance
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SwiftConfigurationException
argument_list|(
literal|"Bad URI: "
operator|+
name|instance
argument_list|)
throw|;
block|}
block|}
DECL|method|hasServiceURI (Configuration conf)
specifier|public
specifier|static
name|boolean
name|hasServiceURI
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|instance
init|=
name|conf
operator|.
name|get
argument_list|(
name|TEST_FS_SWIFT
argument_list|)
decl_stmt|;
return|return
name|instance
operator|!=
literal|null
return|;
block|}
comment|/**    * Assert that a property in the property set matches the expected value    * @param props property set    * @param key property name    * @param expected expected value. If null, the property must not be in the set    */
DECL|method|assertPropertyEquals (Properties props, String key, String expected)
specifier|public
specifier|static
name|void
name|assertPropertyEquals
parameter_list|(
name|Properties
name|props
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|String
name|val
init|=
name|props
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
literal|"Non null property "
operator|+
name|key
operator|+
literal|" = "
operator|+
name|val
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"property "
operator|+
name|key
operator|+
literal|" = "
operator|+
name|val
argument_list|,
name|expected
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *    * Write a file and read it in, validating the result. Optional flags control    * whether file overwrite operations should be enabled, and whether the    * file should be deleted afterwards.    *    * If there is a mismatch between what was written and what was expected,    * a small range of bytes either side of the first error are logged to aid    * diagnosing what problem occurred -whether it was a previous file    * or a corrupting of the current file. This assumes that two    * sequential runs to the same path use datasets with different character    * moduli.    *    * @param fs filesystem    * @param path path to write to    * @param len length of data    * @param overwrite should the create option allow overwrites?    * @param delete should the file be deleted afterwards? -with a verification    * that it worked. Deletion is not attempted if an assertion has failed    * earlier -it is not in a<code>finally{}</code> block.    * @throws IOException IO problems    */
DECL|method|writeAndRead (FileSystem fs, Path path, byte[] src, int len, int blocksize, boolean overwrite, boolean delete)
specifier|public
specifier|static
name|void
name|writeAndRead
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|byte
index|[]
name|src
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|blocksize
parameter_list|,
name|boolean
name|overwrite
parameter_list|,
name|boolean
name|delete
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|writeDataset
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|src
argument_list|,
name|len
argument_list|,
name|blocksize
argument_list|,
name|overwrite
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
name|len
argument_list|)
decl_stmt|;
name|compareByteArrays
argument_list|(
name|src
argument_list|,
name|dest
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|delete
condition|)
block|{
name|boolean
name|deleted
init|=
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Deleted"
argument_list|,
name|deleted
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"Cleanup failed"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Write a file.    * Optional flags control    * whether file overwrite operations should be enabled    * @param fs filesystem    * @param path path to write to    * @param len length of data    * @param overwrite should the create option allow overwrites?    * @throws IOException IO problems    */
DECL|method|writeDataset (FileSystem fs, Path path, byte[] src, int len, int blocksize, boolean overwrite)
specifier|public
specifier|static
name|void
name|writeDataset
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|byte
index|[]
name|src
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|blocksize
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
literal|"Not enough data in source array to write "
operator|+
name|len
operator|+
literal|" bytes"
argument_list|,
name|src
operator|.
name|length
operator|>=
name|len
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|overwrite
argument_list|,
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|IO_FILE_BUFFER_SIZE
argument_list|,
literal|4096
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|blocksize
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
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFileHasLength
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read the file and convert to a byte dataaset    * @param fs filesystem    * @param path path to read from    * @param len length of data to read    * @return the bytes    * @throws IOException IO problems    */
DECL|method|readDataset (FileSystem fs, Path path, int len)
specifier|public
specifier|static
name|byte
index|[]
name|readDataset
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|byte
index|[]
name|dest
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
try|try
block|{
name|in
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|dest
return|;
block|}
comment|/**    * Assert that tthe array src[0..len] and dest[] are equal    * @param src source data    * @param dest actual    * @param len length of bytes to compare    */
DECL|method|compareByteArrays (byte[] src, byte[] dest, int len)
specifier|public
specifier|static
name|void
name|compareByteArrays
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|byte
index|[]
name|dest
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Number of bytes read != number written"
argument_list|,
name|len
argument_list|,
name|dest
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|errors
init|=
literal|0
decl_stmt|;
name|int
name|first_error_byte
init|=
operator|-
literal|1
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
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|src
index|[
name|i
index|]
operator|!=
name|dest
index|[
name|i
index|]
condition|)
block|{
if|if
condition|(
name|errors
operator|==
literal|0
condition|)
block|{
name|first_error_byte
operator|=
name|i
expr_stmt|;
block|}
name|errors
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|errors
operator|>
literal|0
condition|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|" %d errors in file of length %d"
argument_list|,
name|errors
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// the range either side of the first error to print
comment|// this is a purely arbitrary number, to aid user debugging
specifier|final
name|int
name|overlap
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|first_error_byte
operator|-
name|overlap
argument_list|)
init|;
name|i
operator|<
name|Math
operator|.
name|min
argument_list|(
name|first_error_byte
operator|+
name|overlap
argument_list|,
name|len
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|actual
init|=
name|dest
index|[
name|i
index|]
decl_stmt|;
name|byte
name|expected
init|=
name|src
index|[
name|i
index|]
decl_stmt|;
name|String
name|letter
init|=
name|toChar
argument_list|(
name|actual
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|String
operator|.
name|format
argument_list|(
literal|"[%04d] %2x %s%n"
argument_list|,
name|i
argument_list|,
name|actual
argument_list|,
name|letter
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|!=
name|actual
condition|)
block|{
name|line
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"[%04d] %2x %s -expected %2x %s%n"
argument_list|,
name|i
argument_list|,
name|actual
argument_list|,
name|letter
argument_list|,
name|expected
argument_list|,
name|toChar
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Convert a byte to a character for printing. If the    * byte value is< 32 -and hence unprintable- the byte is    * returned as a two digit hex value    * @param b byte    * @return the printable character string    */
DECL|method|toChar (byte b)
specifier|public
specifier|static
name|String
name|toChar
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|>=
literal|0x20
condition|)
block|{
return|return
name|Character
operator|.
name|toString
argument_list|(
operator|(
name|char
operator|)
name|b
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%02x"
argument_list|,
name|b
argument_list|)
return|;
block|}
block|}
DECL|method|toChar (byte[] buffer)
specifier|public
specifier|static
name|String
name|toChar
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|byte
name|b
range|:
name|buffer
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|toChar
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toAsciiByteArray (String s)
specifier|public
specifier|static
name|byte
index|[]
name|toAsciiByteArray
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|char
index|[]
name|chars
init|=
name|s
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|chars
operator|.
name|length
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|len
index|]
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|chars
index|[
name|i
index|]
operator|&
literal|0xff
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
return|;
block|}
DECL|method|cleanupInTeardown (FileSystem fileSystem, String cleanupPath)
specifier|public
specifier|static
name|void
name|cleanupInTeardown
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|String
name|cleanupPath
parameter_list|)
block|{
name|cleanup
argument_list|(
literal|"TEARDOWN"
argument_list|,
name|fileSystem
argument_list|,
name|cleanupPath
argument_list|)
expr_stmt|;
block|}
DECL|method|cleanup (String action, FileSystem fileSystem, String cleanupPath)
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|(
name|String
name|action
parameter_list|,
name|FileSystem
name|fileSystem
parameter_list|,
name|String
name|cleanupPath
parameter_list|)
block|{
name|noteAction
argument_list|(
name|action
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|fileSystem
operator|!=
literal|null
condition|)
block|{
name|fileSystem
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|cleanupPath
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|fileSystem
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error deleting in "
operator|+
name|action
operator|+
literal|" - "
operator|+
name|cleanupPath
operator|+
literal|": "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|noteAction (String action)
specifier|public
specifier|static
name|void
name|noteAction
parameter_list|(
name|String
name|action
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"==============  "
operator|+
name|action
operator|+
literal|" ============="
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * downgrade a failure to a message and a warning, then an    * exception for the Junit test runner to mark as failed    * @param message text message    * @param failure what failed    * @throws AssumptionViolatedException always    */
DECL|method|downgrade (String message, Throwable failure)
specifier|public
specifier|static
name|void
name|downgrade
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|failure
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Downgrading test "
operator|+
name|message
argument_list|,
name|failure
argument_list|)
expr_stmt|;
name|AssumptionViolatedException
name|ave
init|=
operator|new
name|AssumptionViolatedException
argument_list|(
name|failure
argument_list|,
literal|null
argument_list|)
decl_stmt|;
throw|throw
name|ave
throw|;
block|}
comment|/**    * report an overridden test as unsupported    * @param message message to use in the text    * @throws AssumptionViolatedException always    */
DECL|method|unsupported (String message)
specifier|public
specifier|static
name|void
name|unsupported
parameter_list|(
name|String
name|message
parameter_list|)
block|{
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|/**    * report a test has been skipped for some reason    * @param message message to use in the text    * @throws AssumptionViolatedException always    */
DECL|method|skip (String message)
specifier|public
specifier|static
name|void
name|skip
parameter_list|(
name|String
name|message
parameter_list|)
block|{
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|/**    * Make an assertion about the length of a file    * @param fs filesystem    * @param path path of the file    * @param expected expected length    * @throws IOException on File IO problems    */
DECL|method|assertFileHasLength (FileSystem fs, Path path, int expected)
specifier|public
specifier|static
name|void
name|assertFileHasLength
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|"Wrong file length of file "
operator|+
name|path
operator|+
literal|" status: "
operator|+
name|status
argument_list|,
name|expected
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a path refers to a directory    * @param fs filesystem    * @param path path of the directory    * @throws IOException on File IO problems    */
DECL|method|assertIsDirectory (FileSystem fs, Path path)
specifier|public
specifier|static
name|void
name|assertIsDirectory
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertIsDirectory
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a path refers to a directory    * @param fileStatus stats to check    */
DECL|method|assertIsDirectory (FileStatus fileStatus)
specifier|public
specifier|static
name|void
name|assertIsDirectory
parameter_list|(
name|FileStatus
name|fileStatus
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Should be a dir -but isn't: "
operator|+
name|fileStatus
argument_list|,
name|fileStatus
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write the text to a file, returning the converted byte array    * for use in validating the round trip    * @param fs filesystem    * @param path path of file    * @param text text to write    * @param overwrite should the operation overwrite any existing file?    * @return the read bytes    * @throws IOException on IO problems    */
DECL|method|writeTextFile (FileSystem fs, Path path, String text, boolean overwrite)
specifier|public
specifier|static
name|byte
index|[]
name|writeTextFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|String
name|text
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|overwrite
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|bytes
operator|=
name|toAsciiByteArray
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|bytes
return|;
block|}
comment|/**    * Touch a file: fails if it is already there    * @param fs filesystem    * @param path path    * @throws IOException IO problems    */
DECL|method|touch (FileSystem fs, Path path)
specifier|public
specifier|static
name|void
name|touch
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writeTextFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|assertDeleted (FileSystem fs, Path file, boolean recursive)
specifier|public
specifier|static
name|void
name|assertDeleted
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|file
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
name|assertPathExists
argument_list|(
name|fs
argument_list|,
literal|"about to be deleted file"
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|boolean
name|deleted
init|=
name|fs
operator|.
name|delete
argument_list|(
name|file
argument_list|,
name|recursive
argument_list|)
decl_stmt|;
name|String
name|dir
init|=
name|ls
argument_list|(
name|fs
argument_list|,
name|file
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Delete failed on "
operator|+
name|file
operator|+
literal|": "
operator|+
name|dir
argument_list|,
name|deleted
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"Deleted file"
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read in "length" bytes, convert to an ascii string    * @param fs filesystem    * @param path path to read    * @param length #of bytes to read.    * @return the bytes read and converted to a string    * @throws IOException    */
DECL|method|readBytesToString (FileSystem fs, Path path, int length)
specifier|public
specifier|static
name|String
name|readBytesToString
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|buf
argument_list|)
expr_stmt|;
return|return
name|toChar
argument_list|(
name|buf
argument_list|)
return|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getDefaultWorkingDirectory ()
specifier|public
specifier|static
name|String
name|getDefaultWorkingDirectory
parameter_list|()
block|{
return|return
literal|"/user/"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
return|;
block|}
DECL|method|ls (FileSystem fileSystem, Path path)
specifier|public
specifier|static
name|String
name|ls
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SwiftUtils
operator|.
name|ls
argument_list|(
name|fileSystem
argument_list|,
name|path
argument_list|)
return|;
block|}
DECL|method|dumpStats (String pathname, FileStatus[] stats)
specifier|public
specifier|static
name|String
name|dumpStats
parameter_list|(
name|String
name|pathname
parameter_list|,
name|FileStatus
index|[]
name|stats
parameter_list|)
block|{
return|return
name|pathname
operator|+
name|SwiftUtils
operator|.
name|fileStatsToString
argument_list|(
name|stats
argument_list|,
literal|"\n"
argument_list|)
return|;
block|}
comment|/**    /**    * Assert that a file exists and whose {@link FileStatus} entry    * declares that this is a file and not a symlink or directory.    * @param fileSystem filesystem to resolve path against    * @param filename name of the file    * @throws IOException IO problems during file operations    */
DECL|method|assertIsFile (FileSystem fileSystem, Path filename)
specifier|public
specifier|static
name|void
name|assertIsFile
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|Path
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|assertPathExists
argument_list|(
name|fileSystem
argument_list|,
literal|"Expected file"
argument_list|,
name|filename
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fileSystem
operator|.
name|getFileStatus
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|String
name|fileInfo
init|=
name|filename
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
comment|/* disabled for Hadoop v1 compatibility     assertFalse("File claims to be a symlink " + fileInfo,                        status.isSymlink()); */
block|}
comment|/**    * Create a dataset for use in the tests; all data is in the range    * base to (base+modulo-1) inclusive    * @param len length of data    * @param base base of the data    * @param modulo the modulo    * @return the newly generated dataset    */
DECL|method|dataset (int len, int base, int modulo)
specifier|public
specifier|static
name|byte
index|[]
name|dataset
parameter_list|(
name|int
name|len
parameter_list|,
name|int
name|base
parameter_list|,
name|int
name|modulo
parameter_list|)
block|{
name|byte
index|[]
name|dataset
init|=
operator|new
name|byte
index|[
name|len
index|]
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|dataset
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|base
operator|+
operator|(
name|i
operator|%
name|modulo
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|dataset
return|;
block|}
comment|/**    * Assert that a path exists -but make no assertions as to the    * type of that entry    *    * @param fileSystem filesystem to examine    * @param message message to include in the assertion failure message    * @param path path in the filesystem    * @throws IOException IO problems    */
DECL|method|assertPathExists (FileSystem fileSystem, String message, Path path)
specifier|public
specifier|static
name|void
name|assertPathExists
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|String
name|message
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|fileSystem
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|//failure, report it
name|fail
argument_list|(
name|message
operator|+
literal|": not found "
operator|+
name|path
operator|+
literal|" in "
operator|+
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|ls
argument_list|(
name|fileSystem
argument_list|,
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Assert that a path does not exist    *    * @param fileSystem filesystem to examine    * @param message message to include in the assertion failure message    * @param path path in the filesystem    * @throws IOException IO problems    */
DECL|method|assertPathDoesNotExist (FileSystem fileSystem, String message, Path path)
specifier|public
specifier|static
name|void
name|assertPathDoesNotExist
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|String
name|message
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|FileStatus
name|status
init|=
name|fileSystem
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|fail
argument_list|(
name|message
operator|+
literal|": unexpectedly found "
operator|+
name|path
operator|+
literal|" as  "
operator|+
name|status
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|expected
parameter_list|)
block|{
comment|//this is expected
block|}
block|}
comment|/**    * Assert that a FileSystem.listStatus on a dir finds the subdir/child entry    * @param fs filesystem    * @param dir directory to scan    * @param subdir full path to look for    * @throws IOException IO probles    */
DECL|method|assertListStatusFinds (FileSystem fs, Path dir, Path subdir)
specifier|public
specifier|static
name|void
name|assertListStatusFinds
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|dir
parameter_list|,
name|Path
name|subdir
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
index|[]
name|stats
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|StringBuilder
name|builder
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
name|stats
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|stat
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
if|if
condition|(
name|stat
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|subdir
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Path "
operator|+
name|subdir
operator|+
literal|" not found in directory "
operator|+
name|dir
operator|+
literal|":"
operator|+
name|builder
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

