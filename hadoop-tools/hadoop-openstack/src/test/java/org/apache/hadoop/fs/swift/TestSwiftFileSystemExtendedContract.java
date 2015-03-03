begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|RestClientBindings
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
name|util
operator|.
name|StringUtils
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

begin_class
DECL|class|TestSwiftFileSystemExtendedContract
specifier|public
class|class
name|TestSwiftFileSystemExtendedContract
extends|extends
name|SwiftFileSystemBaseTest
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testOpenNonExistingFile ()
specifier|public
name|void
name|testOpenNonExistingFile
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testOpenNonExistingFile"
argument_list|)
decl_stmt|;
comment|//open it as a file, should get FileNotFoundException
try|try
block|{
specifier|final
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"didn't expect to get here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Expected: "
operator|+
name|fnfe
argument_list|,
name|fnfe
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testFilesystemHasURI ()
specifier|public
name|void
name|testFilesystemHasURI
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertNotNull
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testCreateFile ()
specifier|public
name|void
name|testCreateFile
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
literal|"/test/testCreateFile"
argument_list|)
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|fsDataOutputStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|fsDataOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExists
argument_list|(
literal|"created file"
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testWriteReadFile ()
specifier|public
name|void
name|testWriteReadFile
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
literal|"/test/test"
argument_list|)
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|fsDataOutputStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|f
argument_list|)
decl_stmt|;
specifier|final
name|String
name|message
init|=
literal|"Test string"
decl_stmt|;
name|fsDataOutputStream
operator|.
name|write
argument_list|(
name|message
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fsDataOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExists
argument_list|(
literal|"created file"
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|open
init|=
literal|null
decl_stmt|;
try|try
block|{
name|open
operator|=
name|fs
operator|.
name|open
argument_list|(
name|f
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
specifier|final
name|int
name|read
init|=
name|open
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|read
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|delete
argument_list|(
name|f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|open
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testConfDefinesFilesystem ()
specifier|public
name|void
name|testConfDefinesFilesystem
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|SwiftTestUtils
operator|.
name|getServiceURI
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testConfIsValid ()
specifier|public
name|void
name|testConfIsValid
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|URI
name|fsURI
init|=
name|SwiftTestUtils
operator|.
name|getServiceURI
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RestClientBindings
operator|.
name|bind
argument_list|(
name|fsURI
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testGetSchemeImplemented ()
specifier|public
name|void
name|testGetSchemeImplemented
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|scheme
init|=
name|fs
operator|.
name|getScheme
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|SwiftNativeFileSystem
operator|.
name|SWIFT
argument_list|,
name|scheme
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a filesystem is case sensitive.    * This is done by creating a mixed-case filename and asserting that    * its lower case version is not there.    *    * @throws Exception failures    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testFilesystemIsCaseSensitive ()
specifier|public
name|void
name|testFilesystemIsCaseSensitive
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mixedCaseFilename
init|=
literal|"/test/UPPER.TXT"
decl_stmt|;
name|Path
name|upper
init|=
name|path
argument_list|(
name|mixedCaseFilename
argument_list|)
decl_stmt|;
name|Path
name|lower
init|=
name|path
argument_list|(
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|mixedCaseFilename
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"File exists"
operator|+
name|upper
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|upper
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"File exists"
operator|+
name|lower
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|lower
argument_list|)
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|upper
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
literal|"UPPER"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileStatus
name|upperStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|upper
argument_list|)
decl_stmt|;
name|assertExists
argument_list|(
literal|"Original upper case file"
operator|+
name|upper
argument_list|,
name|upper
argument_list|)
expr_stmt|;
comment|//verify the lower-case version of the filename doesn't exist
name|assertPathDoesNotExist
argument_list|(
literal|"lower case file"
argument_list|,
name|lower
argument_list|)
expr_stmt|;
comment|//now overwrite the lower case version of the filename with a
comment|//new version.
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|lower
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
literal|"l"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExists
argument_list|(
literal|"lower case file"
argument_list|,
name|lower
argument_list|)
expr_stmt|;
comment|//verifEy the length of the upper file hasn't changed
name|assertExists
argument_list|(
literal|"Original upper case file "
operator|+
name|upper
argument_list|,
name|upper
argument_list|)
expr_stmt|;
name|FileStatus
name|newStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|upper
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected status:"
operator|+
name|upperStatus
operator|+
literal|" actual status "
operator|+
name|newStatus
argument_list|,
name|upperStatus
operator|.
name|getLen
argument_list|()
argument_list|,
name|newStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

