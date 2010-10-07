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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|impl
operator|.
name|Log4JLogger
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
name|log4j
operator|.
name|Level
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
name|Test
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

begin_comment
comment|/**  * This class tests the FileStatus API.  */
end_comment

begin_class
DECL|class|TestListFiles
specifier|public
class|class
name|TestListFiles
block|{
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|FileSystem
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|conf
specifier|final
specifier|protected
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|protected
specifier|static
name|FileSystem
name|fs
decl_stmt|;
DECL|field|TEST_DIR
specifier|final
specifier|protected
specifier|static
name|Path
name|TEST_DIR
init|=
name|getTestDir
argument_list|()
decl_stmt|;
DECL|field|FILE_LEN
specifier|final
specifier|private
specifier|static
name|int
name|FILE_LEN
init|=
literal|10
decl_stmt|;
DECL|field|FILE1
specifier|final
specifier|private
specifier|static
name|Path
name|FILE1
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
DECL|field|DIR1
specifier|final
specifier|private
specifier|static
name|Path
name|DIR1
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR
argument_list|,
literal|"dir1"
argument_list|)
decl_stmt|;
DECL|field|FILE2
specifier|final
specifier|private
specifier|static
name|Path
name|FILE2
init|=
operator|new
name|Path
argument_list|(
name|DIR1
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
DECL|field|FILE3
specifier|final
specifier|private
specifier|static
name|Path
name|FILE3
init|=
operator|new
name|Path
argument_list|(
name|DIR1
argument_list|,
literal|"file3"
argument_list|)
decl_stmt|;
DECL|method|getTestDir ()
specifier|protected
specifier|static
name|Path
name|getTestDir
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data/work-dir/localfs"
argument_list|)
argument_list|,
literal|"main_"
argument_list|)
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|testSetUp ()
specifier|public
specifier|static
name|void
name|testSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|TEST_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|writeFile (FileSystem fileSys, Path name, int fileSize)
specifier|private
specifier|static
name|void
name|writeFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|fileSize
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Create and write a file that contains three blocks of data
name|FSDataOutputStream
name|stm
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test when input path is a file */
annotation|@
name|Test
DECL|method|testFile ()
specifier|public
name|void
name|testFile
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|TEST_DIR
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fs
argument_list|,
name|FILE1
argument_list|,
name|FILE_LEN
argument_list|)
expr_stmt|;
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|itor
init|=
name|fs
operator|.
name|listFiles
argument_list|(
name|FILE1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LocatedFileStatus
name|stat
init|=
name|itor
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FILE_LEN
argument_list|,
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|FILE1
argument_list|)
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stat
operator|.
name|getBlockLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listFiles
argument_list|(
name|FILE1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|stat
operator|=
name|itor
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FILE_LEN
argument_list|,
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|FILE1
argument_list|)
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stat
operator|.
name|getBlockLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|FILE1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Test when input path is a directory */
annotation|@
name|Test
DECL|method|testDirectory ()
specifier|public
name|void
name|testDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|DIR1
argument_list|)
expr_stmt|;
comment|// test empty directory
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|itor
init|=
name|fs
operator|.
name|listFiles
argument_list|(
name|DIR1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listFiles
argument_list|(
name|DIR1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// testing directory with 1 file
name|writeFile
argument_list|(
name|fs
argument_list|,
name|FILE2
argument_list|,
name|FILE_LEN
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listFiles
argument_list|(
name|DIR1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LocatedFileStatus
name|stat
init|=
name|itor
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FILE_LEN
argument_list|,
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|FILE2
argument_list|)
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stat
operator|.
name|getBlockLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listFiles
argument_list|(
name|DIR1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|stat
operator|=
name|itor
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FILE_LEN
argument_list|,
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|FILE2
argument_list|)
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stat
operator|.
name|getBlockLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test more complicated directory
name|writeFile
argument_list|(
name|fs
argument_list|,
name|FILE1
argument_list|,
name|FILE_LEN
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fs
argument_list|,
name|FILE3
argument_list|,
name|FILE_LEN
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Path
argument_list|>
name|filesToFind
init|=
operator|new
name|HashSet
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|filesToFind
operator|.
name|add
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|FILE1
argument_list|)
argument_list|)
expr_stmt|;
name|filesToFind
operator|.
name|add
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|FILE2
argument_list|)
argument_list|)
expr_stmt|;
name|filesToFind
operator|.
name|add
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|FILE3
argument_list|)
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listFiles
argument_list|(
name|TEST_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|stat
operator|=
name|itor
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Path "
operator|+
name|stat
operator|.
name|getPath
argument_list|()
operator|+
literal|" unexpected"
argument_list|,
name|filesToFind
operator|.
name|remove
argument_list|(
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|=
name|itor
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Path "
operator|+
name|stat
operator|.
name|getPath
argument_list|()
operator|+
literal|" unexpected"
argument_list|,
name|filesToFind
operator|.
name|remove
argument_list|(
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|=
name|itor
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Path "
operator|+
name|stat
operator|.
name|getPath
argument_list|()
operator|+
literal|" unexpected"
argument_list|,
name|filesToFind
operator|.
name|remove
argument_list|(
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filesToFind
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listFiles
argument_list|(
name|TEST_DIR
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|stat
operator|=
name|itor
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|stat
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|FILE1
argument_list|)
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|TEST_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

