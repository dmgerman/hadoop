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
name|assumeTrue
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|fs
operator|.
name|FileUtil
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

begin_comment
comment|/**  * Test cases for helper Windows winutils.exe utility.  */
end_comment

begin_class
DECL|class|TestWinUtils
specifier|public
class|class
name|TestWinUtils
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
name|TestWinUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_DIR
specifier|private
specifier|static
name|File
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
name|TestWinUtils
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
comment|// Not supported on non-Windows platforms
name|assumeTrue
argument_list|(
name|Shell
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|TEST_DIR
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_DIR
argument_list|)
expr_stmt|;
block|}
comment|// Helper routine that writes the given content to the file.
DECL|method|writeFile (File file, String content)
specifier|private
name|void
name|writeFile
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|content
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Helper routine that reads the first 100 bytes from the file.
DECL|method|readFile (File file)
specifier|private
name|String
name|readFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|fos
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|100
index|]
decl_stmt|;
name|fos
operator|.
name|read
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testLs ()
specifier|public
name|void
name|testLs
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|content
init|=
literal|"6bytes"
decl_stmt|;
specifier|final
name|int
name|contentSize
init|=
name|content
operator|.
name|length
argument_list|()
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|testFile
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Verify permissions and file name return tokens
name|String
name|output
init|=
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"ls"
argument_list|,
name|testFile
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|outputArgs
init|=
name|output
operator|.
name|split
argument_list|(
literal|"[ \r\n]"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|outputArgs
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"-rwx------"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outputArgs
index|[
name|outputArgs
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|equals
argument_list|(
name|testFile
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify most tokens when using a formatted output (other tokens
comment|// will be verified with chmod/chown)
name|output
operator|=
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"ls"
argument_list|,
literal|"-F"
argument_list|,
name|testFile
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|outputArgs
operator|=
name|output
operator|.
name|split
argument_list|(
literal|"[|\r\n]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|outputArgs
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outputArgs
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"-rwx------"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|contentSize
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|outputArgs
index|[
literal|4
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outputArgs
index|[
literal|8
index|]
operator|.
name|equals
argument_list|(
name|testFile
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|testFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|testFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testGroups ()
specifier|public
name|void
name|testGroups
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|currentUser
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
comment|// Verify that groups command returns information about the current user
comment|// groups when invoked with no args
name|String
name|outputNoArgs
init|=
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"groups"
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|output
init|=
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"groups"
argument_list|,
name|currentUser
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|output
argument_list|,
name|outputNoArgs
argument_list|)
expr_stmt|;
comment|// Verify that groups command with the -F flag returns the same information
name|String
name|outputFormat
init|=
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"groups"
argument_list|,
literal|"-F"
argument_list|,
name|currentUser
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|outputFormat
operator|=
name|outputFormat
operator|.
name|replace
argument_list|(
literal|"|"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|output
argument_list|,
name|outputFormat
argument_list|)
expr_stmt|;
block|}
DECL|method|chmod (String mask, File file)
specifier|private
name|void
name|chmod
parameter_list|(
name|String
name|mask
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"chmod"
argument_list|,
name|mask
argument_list|,
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|chmodR (String mask, File file)
specifier|private
name|void
name|chmodR
parameter_list|(
name|String
name|mask
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"chmod"
argument_list|,
literal|"-R"
argument_list|,
name|mask
argument_list|,
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ls (File file)
specifier|private
name|String
name|ls
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"ls"
argument_list|,
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|lsF (File file)
specifier|private
name|String
name|lsF
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"ls"
argument_list|,
literal|"-F"
argument_list|,
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|assertPermissions (File file, String expected)
specifier|private
name|void
name|assertPermissions
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|output
init|=
name|ls
argument_list|(
name|file
argument_list|)
operator|.
name|split
argument_list|(
literal|"[ \r\n]"
argument_list|)
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
DECL|method|testChmodInternal (String mode, String expectedPerm)
specifier|private
name|void
name|testChmodInternal
parameter_list|(
name|String
name|mode
parameter_list|,
name|String
name|expectedPerm
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|a
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
comment|// Reset permissions on the file to default
name|chmod
argument_list|(
literal|"700"
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Apply the mode mask
name|chmod
argument_list|(
name|mode
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Compare the output
name|assertPermissions
argument_list|(
name|a
argument_list|,
name|expectedPerm
argument_list|)
expr_stmt|;
name|a
operator|.
name|delete
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNewFileChmodInternal (String expectedPerm)
specifier|private
name|void
name|testNewFileChmodInternal
parameter_list|(
name|String
name|expectedPerm
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Create a new directory
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"dir1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|.
name|mkdir
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set permission use chmod
name|chmod
argument_list|(
literal|"755"
argument_list|,
name|dir
argument_list|)
expr_stmt|;
comment|// Create a child file in the directory
name|File
name|child
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|child
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify the child file has correct permissions
name|assertPermissions
argument_list|(
name|child
argument_list|,
name|expectedPerm
argument_list|)
expr_stmt|;
name|child
operator|.
name|delete
argument_list|()
expr_stmt|;
name|dir
operator|.
name|delete
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|dir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testChmodInternalR (String mode, String expectedPerm, String expectedPermx)
specifier|private
name|void
name|testChmodInternalR
parameter_list|(
name|String
name|mode
parameter_list|,
name|String
name|expectedPerm
parameter_list|,
name|String
name|expectedPermx
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Setup test folder hierarchy
name|File
name|a
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|mkdir
argument_list|()
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|"700"
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|File
name|aa
init|=
operator|new
name|File
argument_list|(
name|a
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|aa
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|"600"
argument_list|,
name|aa
argument_list|)
expr_stmt|;
name|File
name|ab
init|=
operator|new
name|File
argument_list|(
name|a
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ab
operator|.
name|mkdir
argument_list|()
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|"700"
argument_list|,
name|ab
argument_list|)
expr_stmt|;
name|File
name|aba
init|=
operator|new
name|File
argument_list|(
name|ab
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|aba
operator|.
name|mkdir
argument_list|()
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|"700"
argument_list|,
name|aba
argument_list|)
expr_stmt|;
name|File
name|abb
init|=
operator|new
name|File
argument_list|(
name|ab
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|abb
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|"600"
argument_list|,
name|abb
argument_list|)
expr_stmt|;
name|File
name|abx
init|=
operator|new
name|File
argument_list|(
name|ab
argument_list|,
literal|"x"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|abx
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|"u+x"
argument_list|,
name|abx
argument_list|)
expr_stmt|;
comment|// Run chmod recursive
name|chmodR
argument_list|(
name|mode
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Verify outcome
name|assertPermissions
argument_list|(
name|a
argument_list|,
literal|"d"
operator|+
name|expectedPermx
argument_list|)
expr_stmt|;
name|assertPermissions
argument_list|(
name|aa
argument_list|,
literal|"-"
operator|+
name|expectedPerm
argument_list|)
expr_stmt|;
name|assertPermissions
argument_list|(
name|ab
argument_list|,
literal|"d"
operator|+
name|expectedPermx
argument_list|)
expr_stmt|;
name|assertPermissions
argument_list|(
name|aba
argument_list|,
literal|"d"
operator|+
name|expectedPermx
argument_list|)
expr_stmt|;
name|assertPermissions
argument_list|(
name|abb
argument_list|,
literal|"-"
operator|+
name|expectedPerm
argument_list|)
expr_stmt|;
name|assertPermissions
argument_list|(
name|abx
argument_list|,
literal|"-"
operator|+
name|expectedPermx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testBasicChmod ()
specifier|public
name|void
name|testBasicChmod
parameter_list|()
throws|throws
name|IOException
block|{
comment|// - Create a file.
comment|// - Change mode to 377 so owner does not have read permission.
comment|// - Verify the owner truly does not have the permissions to read.
name|File
name|a
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|a
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|chmod
argument_list|(
literal|"377"
argument_list|,
name|a
argument_list|)
expr_stmt|;
try|try
block|{
name|readFile
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"readFile should have failed!"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected: Failed read from a file with permissions 377"
argument_list|)
expr_stmt|;
block|}
comment|// restore permissions
name|chmod
argument_list|(
literal|"700"
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// - Create a file.
comment|// - Change mode to 577 so owner does not have write permission.
comment|// - Verify the owner truly does not have the permissions to write.
name|chmod
argument_list|(
literal|"577"
argument_list|,
name|a
argument_list|)
expr_stmt|;
try|try
block|{
name|writeFile
argument_list|(
name|a
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"writeFile should have failed!"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected: Failed write to a file with permissions 577"
argument_list|)
expr_stmt|;
block|}
comment|// restore permissions
name|chmod
argument_list|(
literal|"700"
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
comment|// - Copy WINUTILS to a new executable file, a.exe.
comment|// - Change mode to 677 so owner does not have execute permission.
comment|// - Verify the owner truly does not have the permissions to execute the file.
name|File
name|winutilsFile
init|=
operator|new
name|File
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|)
decl_stmt|;
name|File
name|aExe
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"a.exe"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|winutilsFile
argument_list|,
name|aExe
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
literal|"677"
argument_list|,
name|aExe
argument_list|)
expr_stmt|;
try|try
block|{
name|Shell
operator|.
name|execCommand
argument_list|(
name|aExe
operator|.
name|getCanonicalPath
argument_list|()
argument_list|,
literal|"ls"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"executing "
operator|+
name|aExe
operator|+
literal|" should have failed!"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected: Failed to execute a file with permissions 677"
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|aExe
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testChmod ()
specifier|public
name|void
name|testChmod
parameter_list|()
throws|throws
name|IOException
block|{
name|testChmodInternal
argument_list|(
literal|"7"
argument_list|,
literal|"-------rwx"
argument_list|)
expr_stmt|;
name|testChmodInternal
argument_list|(
literal|"70"
argument_list|,
literal|"----rwx---"
argument_list|)
expr_stmt|;
name|testChmodInternal
argument_list|(
literal|"u-x,g+r,o=g"
argument_list|,
literal|"-rw-r--r--"
argument_list|)
expr_stmt|;
name|testChmodInternal
argument_list|(
literal|"u-x,g+rw"
argument_list|,
literal|"-rw-rw----"
argument_list|)
expr_stmt|;
name|testChmodInternal
argument_list|(
literal|"u-x,g+rwx-x,o=u"
argument_list|,
literal|"-rw-rw-rw-"
argument_list|)
expr_stmt|;
name|testChmodInternal
argument_list|(
literal|"+"
argument_list|,
literal|"-rwx------"
argument_list|)
expr_stmt|;
comment|// Recursive chmod tests
name|testChmodInternalR
argument_list|(
literal|"755"
argument_list|,
literal|"rwxr-xr-x"
argument_list|,
literal|"rwxr-xr-x"
argument_list|)
expr_stmt|;
name|testChmodInternalR
argument_list|(
literal|"u-x,g+r,o=g"
argument_list|,
literal|"rw-r--r--"
argument_list|,
literal|"rw-r--r--"
argument_list|)
expr_stmt|;
name|testChmodInternalR
argument_list|(
literal|"u-x,g+rw"
argument_list|,
literal|"rw-rw----"
argument_list|,
literal|"rw-rw----"
argument_list|)
expr_stmt|;
name|testChmodInternalR
argument_list|(
literal|"u-x,g+rwx-x,o=u"
argument_list|,
literal|"rw-rw-rw-"
argument_list|,
literal|"rw-rw-rw-"
argument_list|)
expr_stmt|;
name|testChmodInternalR
argument_list|(
literal|"a+rX"
argument_list|,
literal|"rw-r--r--"
argument_list|,
literal|"rwxr-xr-x"
argument_list|)
expr_stmt|;
comment|// Test a new file created in a chmod'ed directory has expected permission
name|testNewFileChmodInternal
argument_list|(
literal|"-rwx------"
argument_list|)
expr_stmt|;
block|}
DECL|method|chown (String userGroup, File file)
specifier|private
name|void
name|chown
parameter_list|(
name|String
name|userGroup
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"chown"
argument_list|,
name|userGroup
argument_list|,
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertOwners (File file, String expectedUser, String expectedGroup)
specifier|private
name|void
name|assertOwners
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|expectedUser
parameter_list|,
name|String
name|expectedGroup
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|args
init|=
name|lsF
argument_list|(
name|file
argument_list|)
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"[\\|]"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedUser
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|args
index|[
literal|2
index|]
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedGroup
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|args
index|[
literal|3
index|]
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testChown ()
specifier|public
name|void
name|testChown
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|a
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|username
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
comment|// username including the domain aka DOMAIN\\user
name|String
name|qualifiedUsername
init|=
name|Shell
operator|.
name|execCommand
argument_list|(
literal|"whoami"
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|admins
init|=
literal|"Administrators"
decl_stmt|;
name|String
name|qualifiedAdmins
init|=
literal|"BUILTIN\\Administrators"
decl_stmt|;
name|chown
argument_list|(
name|username
operator|+
literal|":"
operator|+
name|admins
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|assertOwners
argument_list|(
name|a
argument_list|,
name|qualifiedUsername
argument_list|,
name|qualifiedAdmins
argument_list|)
expr_stmt|;
name|chown
argument_list|(
name|username
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|chown
argument_list|(
literal|":"
operator|+
name|admins
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|assertOwners
argument_list|(
name|a
argument_list|,
name|qualifiedUsername
argument_list|,
name|qualifiedAdmins
argument_list|)
expr_stmt|;
name|chown
argument_list|(
literal|":"
operator|+
name|admins
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|chown
argument_list|(
name|username
operator|+
literal|":"
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|assertOwners
argument_list|(
name|a
argument_list|,
name|qualifiedUsername
argument_list|,
name|qualifiedAdmins
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testSymlinkRejectsForwardSlashesInLink ()
specifier|public
name|void
name|testSymlinkRejectsForwardSlashesInLink
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|newFile
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newFile
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|target
init|=
name|newFile
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|link
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"link"
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
try|try
block|{
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"symlink"
argument_list|,
name|link
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"did not receive expected failure creating symlink "
operator|+
literal|"with forward slashes in link: link = %s, target = %s"
argument_list|,
name|link
argument_list|,
name|target
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected: Failed to create symlink with forward slashes in target"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testSymlinkRejectsForwardSlashesInTarget ()
specifier|public
name|void
name|testSymlinkRejectsForwardSlashesInTarget
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|newFile
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newFile
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|target
init|=
name|newFile
operator|.
name|getPath
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|link
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"link"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
try|try
block|{
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|WINUTILS
argument_list|,
literal|"symlink"
argument_list|,
name|link
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"did not receive expected failure creating symlink "
operator|+
literal|"with forward slashes in target: link = %s, target = %s"
argument_list|,
name|link
argument_list|,
name|target
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected: Failed to create symlink with forward slashes in target"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

