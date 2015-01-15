begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

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
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|Arrays
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
name|Test
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
name|HardLink
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This testing is fairly lightweight.  Assumes HardLink routines will  * only be called when permissions etc are okay; no negative testing is  * provided.  *   * These tests all use   * "src" as the source directory,   * "tgt_one" as the target directory for single-file hardlinking, and  * "tgt_mult" as the target directory for multi-file hardlinking.  *   * Contents of them are/will be:  * dir:src:   *   files: x1, x2, x3  * dir:tgt_one:  *   files: x1 (linked to src/x1), y (linked to src/x2),   *          x3 (linked to src/x3), x11 (also linked to src/x1)  * dir:tgt_mult:  *   files: x1, x2, x3 (all linked to same name in src/)  *     * NOTICE: This test class only tests the functionality of the OS  * upon which the test is run! (although you're pretty safe with the  * unix-like OS's, unless a typo sneaks in.)  */
end_comment

begin_class
DECL|class|TestHardLink
specifier|public
class|class
name|TestHardLink
block|{
DECL|field|TEST_ROOT_DIR
specifier|public
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
literal|"/test"
decl_stmt|;
DECL|field|TEST_DIR
specifier|final
specifier|static
specifier|private
name|File
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"hl"
argument_list|)
decl_stmt|;
DECL|field|DIR
specifier|private
specifier|static
name|String
name|DIR
init|=
literal|"dir_"
decl_stmt|;
comment|//define source and target directories
DECL|field|src
specifier|private
specifier|static
name|File
name|src
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
name|DIR
operator|+
literal|"src"
argument_list|)
decl_stmt|;
DECL|field|tgt_mult
specifier|private
specifier|static
name|File
name|tgt_mult
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
name|DIR
operator|+
literal|"tgt_mult"
argument_list|)
decl_stmt|;
DECL|field|tgt_one
specifier|private
specifier|static
name|File
name|tgt_one
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
name|DIR
operator|+
literal|"tgt_one"
argument_list|)
decl_stmt|;
comment|//define source files
DECL|field|x1
specifier|private
specifier|static
name|File
name|x1
init|=
operator|new
name|File
argument_list|(
name|src
argument_list|,
literal|"x1"
argument_list|)
decl_stmt|;
DECL|field|x2
specifier|private
specifier|static
name|File
name|x2
init|=
operator|new
name|File
argument_list|(
name|src
argument_list|,
literal|"x2"
argument_list|)
decl_stmt|;
DECL|field|x3
specifier|private
specifier|static
name|File
name|x3
init|=
operator|new
name|File
argument_list|(
name|src
argument_list|,
literal|"x3"
argument_list|)
decl_stmt|;
comment|//define File objects for the target hardlinks
DECL|field|x1_one
specifier|private
specifier|static
name|File
name|x1_one
init|=
operator|new
name|File
argument_list|(
name|tgt_one
argument_list|,
literal|"x1"
argument_list|)
decl_stmt|;
DECL|field|y_one
specifier|private
specifier|static
name|File
name|y_one
init|=
operator|new
name|File
argument_list|(
name|tgt_one
argument_list|,
literal|"y"
argument_list|)
decl_stmt|;
DECL|field|x3_one
specifier|private
specifier|static
name|File
name|x3_one
init|=
operator|new
name|File
argument_list|(
name|tgt_one
argument_list|,
literal|"x3"
argument_list|)
decl_stmt|;
DECL|field|x11_one
specifier|private
specifier|static
name|File
name|x11_one
init|=
operator|new
name|File
argument_list|(
name|tgt_one
argument_list|,
literal|"x11"
argument_list|)
decl_stmt|;
DECL|field|x1_mult
specifier|private
specifier|static
name|File
name|x1_mult
init|=
operator|new
name|File
argument_list|(
name|tgt_mult
argument_list|,
literal|"x1"
argument_list|)
decl_stmt|;
DECL|field|x2_mult
specifier|private
specifier|static
name|File
name|x2_mult
init|=
operator|new
name|File
argument_list|(
name|tgt_mult
argument_list|,
literal|"x2"
argument_list|)
decl_stmt|;
DECL|field|x3_mult
specifier|private
specifier|static
name|File
name|x3_mult
init|=
operator|new
name|File
argument_list|(
name|tgt_mult
argument_list|,
literal|"x3"
argument_list|)
decl_stmt|;
comment|//content strings for file content testing
DECL|field|str1
specifier|private
specifier|static
name|String
name|str1
init|=
literal|"11111"
decl_stmt|;
DECL|field|str2
specifier|private
specifier|static
name|String
name|str2
init|=
literal|"22222"
decl_stmt|;
DECL|field|str3
specifier|private
specifier|static
name|String
name|str3
init|=
literal|"33333"
decl_stmt|;
comment|/**    * Assure clean environment for start of testing    * @throws IOException    */
annotation|@
name|BeforeClass
DECL|method|setupClean ()
specifier|public
specifier|static
name|void
name|setupClean
parameter_list|()
block|{
comment|//delete source and target directories if they exist
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|tgt_one
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|tgt_mult
argument_list|)
expr_stmt|;
comment|//check that they are gone
name|assertFalse
argument_list|(
name|src
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tgt_one
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tgt_mult
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize clean environment for start of each test    */
annotation|@
name|Before
DECL|method|setupDirs ()
specifier|public
name|void
name|setupDirs
parameter_list|()
throws|throws
name|IOException
block|{
comment|//check that we start out with empty top-level test data directory
name|assertFalse
argument_list|(
name|src
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tgt_one
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tgt_mult
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|//make the source and target directories
name|src
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|tgt_one
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|tgt_mult
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|//create the source files in src, with unique contents per file
name|makeNonEmptyFile
argument_list|(
name|x1
argument_list|,
name|str1
argument_list|)
expr_stmt|;
name|makeNonEmptyFile
argument_list|(
name|x2
argument_list|,
name|str2
argument_list|)
expr_stmt|;
name|makeNonEmptyFile
argument_list|(
name|x3
argument_list|,
name|str3
argument_list|)
expr_stmt|;
comment|//validate
name|validateSetup
argument_list|()
expr_stmt|;
block|}
comment|/**    * validate that {@link setupDirs()} produced the expected result    */
DECL|method|validateSetup ()
specifier|private
name|void
name|validateSetup
parameter_list|()
throws|throws
name|IOException
block|{
comment|//check existence of source directory and files
name|assertTrue
argument_list|(
name|src
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|src
operator|.
name|list
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x1
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x2
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x3
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|//check contents of source files
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x1
argument_list|)
operator|.
name|equals
argument_list|(
name|str1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x2
argument_list|)
operator|.
name|equals
argument_list|(
name|str2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x3
argument_list|)
operator|.
name|equals
argument_list|(
name|str3
argument_list|)
argument_list|)
expr_stmt|;
comment|//check target directories exist and are empty
name|assertTrue
argument_list|(
name|tgt_one
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tgt_mult
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tgt_one
operator|.
name|list
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tgt_mult
operator|.
name|list
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * validate that single-file link operations produced the expected results    */
DECL|method|validateTgtOne ()
specifier|private
name|void
name|validateTgtOne
parameter_list|()
throws|throws
name|IOException
block|{
comment|//check that target directory tgt_one ended up with expected four files
name|assertTrue
argument_list|(
name|tgt_one
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tgt_one
operator|.
name|list
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x1_one
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x11_one
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y_one
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x3_one
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|//confirm the contents of those four files reflects the known contents
comment|//of the files they were hardlinked from.
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x1_one
argument_list|)
operator|.
name|equals
argument_list|(
name|str1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x11_one
argument_list|)
operator|.
name|equals
argument_list|(
name|str1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|y_one
argument_list|)
operator|.
name|equals
argument_list|(
name|str2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x3_one
argument_list|)
operator|.
name|equals
argument_list|(
name|str3
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * validate that multi-file link operations produced the expected results    */
DECL|method|validateTgtMult ()
specifier|private
name|void
name|validateTgtMult
parameter_list|()
throws|throws
name|IOException
block|{
comment|//check that target directory tgt_mult ended up with expected three files
name|assertTrue
argument_list|(
name|tgt_mult
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tgt_mult
operator|.
name|list
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x1_mult
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x2_mult
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x3_mult
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|//confirm the contents of those three files reflects the known contents
comment|//of the files they were hardlinked from.
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x1_mult
argument_list|)
operator|.
name|equals
argument_list|(
name|str1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x2_mult
argument_list|)
operator|.
name|equals
argument_list|(
name|str2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x3_mult
argument_list|)
operator|.
name|equals
argument_list|(
name|str3
argument_list|)
argument_list|)
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
name|setupClean
argument_list|()
expr_stmt|;
block|}
DECL|method|makeNonEmptyFile (File file, String contents)
specifier|private
name|void
name|makeNonEmptyFile
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
name|FileWriter
name|fw
init|=
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|fw
operator|.
name|write
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|fw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|appendToFile (File file, String contents)
specifier|private
name|void
name|appendToFile
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
name|FileWriter
name|fw
init|=
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|fw
operator|.
name|write
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|fw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|fetchFileContents (File file)
specifier|private
name|String
name|fetchFileContents
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|20
index|]
decl_stmt|;
name|FileReader
name|fr
init|=
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|int
name|cnt
init|=
name|fr
operator|.
name|read
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|fr
operator|.
name|close
argument_list|()
expr_stmt|;
name|char
index|[]
name|result
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|buf
argument_list|,
name|cnt
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/**    * Sanity check the simplest case of HardLink.getLinkCount()    * to make sure we get back "1" for ordinary single-linked files.    * Tests with multiply-linked files are in later test cases.    */
annotation|@
name|Test
DECL|method|testGetLinkCount ()
specifier|public
name|void
name|testGetLinkCount
parameter_list|()
throws|throws
name|IOException
block|{
comment|//at beginning of world, check that source files have link count "1"
comment|//since they haven't been hardlinked yet
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getLinkCount
argument_list|(
name|x1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getLinkCount
argument_list|(
name|x2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getLinkCount
argument_list|(
name|x3
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the single-file method HardLink.createHardLink().    * Also tests getLinkCount() with values greater than one.    */
annotation|@
name|Test
DECL|method|testCreateHardLink ()
specifier|public
name|void
name|testCreateHardLink
parameter_list|()
throws|throws
name|IOException
block|{
comment|//hardlink a single file and confirm expected result
name|createHardLink
argument_list|(
name|x1
argument_list|,
name|x1_one
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x1_one
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getLinkCount
argument_list|(
name|x1
argument_list|)
argument_list|)
expr_stmt|;
comment|//x1 and x1_one are linked now
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getLinkCount
argument_list|(
name|x1_one
argument_list|)
argument_list|)
expr_stmt|;
comment|//so they both have count "2"
comment|//confirm that x2, which we didn't change, still shows count "1"
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getLinkCount
argument_list|(
name|x2
argument_list|)
argument_list|)
expr_stmt|;
comment|//now do a few more
name|createHardLink
argument_list|(
name|x2
argument_list|,
name|y_one
argument_list|)
expr_stmt|;
name|createHardLink
argument_list|(
name|x3
argument_list|,
name|x3_one
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getLinkCount
argument_list|(
name|x2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getLinkCount
argument_list|(
name|x3
argument_list|)
argument_list|)
expr_stmt|;
comment|//create another link to a file that already has count 2
name|createHardLink
argument_list|(
name|x1
argument_list|,
name|x11_one
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getLinkCount
argument_list|(
name|x1
argument_list|)
argument_list|)
expr_stmt|;
comment|//x1, x1_one, and x11_one
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getLinkCount
argument_list|(
name|x1_one
argument_list|)
argument_list|)
expr_stmt|;
comment|//are all linked, so they
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getLinkCount
argument_list|(
name|x11_one
argument_list|)
argument_list|)
expr_stmt|;
comment|//should all have count "3"
comment|//validate by contents
name|validateTgtOne
argument_list|()
expr_stmt|;
comment|//validate that change of content is reflected in the other linked files
name|appendToFile
argument_list|(
name|x1_one
argument_list|,
name|str3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x1_one
argument_list|)
operator|.
name|equals
argument_list|(
name|str1
operator|+
name|str3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x11_one
argument_list|)
operator|.
name|equals
argument_list|(
name|str1
operator|+
name|str3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x1
argument_list|)
operator|.
name|equals
argument_list|(
name|str1
operator|+
name|str3
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test the multi-file method HardLink.createHardLinkMult(),    * multiple files within a directory into one target directory    */
annotation|@
name|Test
DECL|method|testCreateHardLinkMult ()
specifier|public
name|void
name|testCreateHardLinkMult
parameter_list|()
throws|throws
name|IOException
block|{
comment|//hardlink a whole list of three files at once
name|String
index|[]
name|fileNames
init|=
name|src
operator|.
name|list
argument_list|()
decl_stmt|;
name|createHardLinkMult
argument_list|(
name|src
argument_list|,
name|fileNames
argument_list|,
name|tgt_mult
argument_list|)
expr_stmt|;
comment|//validate by link count - each file has been linked once,
comment|//so each count is "2"
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getLinkCount
argument_list|(
name|x1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getLinkCount
argument_list|(
name|x2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getLinkCount
argument_list|(
name|x3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getLinkCount
argument_list|(
name|x1_mult
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getLinkCount
argument_list|(
name|x2_mult
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getLinkCount
argument_list|(
name|x3_mult
argument_list|)
argument_list|)
expr_stmt|;
comment|//validate by contents
name|validateTgtMult
argument_list|()
expr_stmt|;
comment|//validate that change of content is reflected in the other linked files
name|appendToFile
argument_list|(
name|x1_mult
argument_list|,
name|str3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x1_mult
argument_list|)
operator|.
name|equals
argument_list|(
name|str1
operator|+
name|str3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fetchFileContents
argument_list|(
name|x1
argument_list|)
operator|.
name|equals
argument_list|(
name|str1
operator|+
name|str3
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test createHardLinkMult() with empty list of files.    * We use an extended version of the method call, that    * returns the number of System exec calls made, which should    * be zero in this case.    */
annotation|@
name|Test
DECL|method|testCreateHardLinkMultEmptyList ()
specifier|public
name|void
name|testCreateHardLinkMultEmptyList
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|emptyList
init|=
block|{}
decl_stmt|;
comment|//test the case of empty file list
name|createHardLinkMult
argument_list|(
name|src
argument_list|,
name|emptyList
argument_list|,
name|tgt_mult
argument_list|)
expr_stmt|;
comment|//check nothing changed in the directory tree
name|validateSetup
argument_list|()
expr_stmt|;
block|}
comment|/*    * Assume that this test won't usually be run on a Windows box.    * This test case allows testing of the correct syntax of the Windows    * commands, even though they don't actually get executed on a non-Win box.    * The basic idea is to have enough here that substantive changes will    * fail and the author will fix and add to this test as appropriate.    *     * Depends on the HardLinkCGWin class and member fields being accessible    * from this test method.    */
annotation|@
name|Test
DECL|method|testWindowsSyntax ()
specifier|public
name|void
name|testWindowsSyntax
parameter_list|()
block|{
class|class
name|win
extends|extends
name|HardLinkCGWin
block|{}
comment|//basic checks on array lengths
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|win
operator|.
name|getLinkCountCommand
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//make sure "%f" was not munged
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
literal|"%f"
operator|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|//make sure "\\%f" was munged correctly
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
literal|"\\%f"
operator|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|win
operator|.
name|getLinkCountCommand
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"hardlink"
argument_list|)
argument_list|)
expr_stmt|;
comment|//make sure "-c%h" was not munged
name|assertEquals
argument_list|(
literal|4
argument_list|,
operator|(
literal|"-c%h"
operator|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

