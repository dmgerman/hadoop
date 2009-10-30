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
name|EnumSet
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|Options
operator|.
name|CreateOpts
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
comment|/**  * Tests {@link FileContext.#deleteOnExit(Path)} functionality.  */
end_comment

begin_class
DECL|class|TestFileContextDeleteOnExit
specifier|public
class|class
name|TestFileContextDeleteOnExit
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|String
name|TEST_ROOT_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
operator|+
literal|"/test"
decl_stmt|;
DECL|field|data
specifier|private
specifier|static
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|2
index|]
decl_stmt|;
comment|// two blocks of data
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|%
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|fc
specifier|private
name|FileContext
name|fc
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|fc
operator|=
name|FileContext
operator|.
name|getLocalFSFileContext
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
name|fc
operator|.
name|delete
argument_list|(
name|getTestRootPath
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getTestRootPath ()
specifier|private
name|Path
name|getTestRootPath
parameter_list|()
block|{
return|return
name|fc
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getTestPath (String pathString)
specifier|private
name|Path
name|getTestPath
parameter_list|(
name|String
name|pathString
parameter_list|)
block|{
return|return
name|fc
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
name|pathString
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createFile (FileContext fc, Path path)
specifier|private
name|void
name|createFile
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|out
init|=
name|fc
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|,
name|CreateOpts
operator|.
name|createParent
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|checkDeleteOnExitData (int size, FileContext fc, Path... paths)
specifier|private
name|void
name|checkDeleteOnExitData
parameter_list|(
name|int
name|size
parameter_list|,
name|FileContext
name|fc
parameter_list|,
name|Path
modifier|...
name|paths
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|size
argument_list|,
name|FileContext
operator|.
name|DELETE_ON_EXIT
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Path
argument_list|>
name|set
init|=
name|FileContext
operator|.
name|DELETE_ON_EXIT
operator|.
name|get
argument_list|(
name|fc
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|paths
operator|.
name|length
argument_list|,
operator|(
name|set
operator|==
literal|null
condition|?
literal|0
else|:
name|set
operator|.
name|size
argument_list|()
operator|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDeleteOnExit ()
specifier|public
name|void
name|testDeleteOnExit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create deleteOnExit entries
name|Path
name|file1
init|=
name|getTestPath
argument_list|(
literal|"file1"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|fc
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|fc
operator|.
name|deleteOnExit
argument_list|(
name|file1
argument_list|)
expr_stmt|;
name|checkDeleteOnExitData
argument_list|(
literal|1
argument_list|,
name|fc
argument_list|,
name|file1
argument_list|)
expr_stmt|;
comment|// Ensure shutdown hook is added
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|removeShutdownHook
argument_list|(
name|FileContext
operator|.
name|FINALIZER
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|file2
init|=
name|getTestPath
argument_list|(
literal|"dir1/file2"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|fc
argument_list|,
name|file2
argument_list|)
expr_stmt|;
name|fc
operator|.
name|deleteOnExit
argument_list|(
name|file2
argument_list|)
expr_stmt|;
name|checkDeleteOnExitData
argument_list|(
literal|1
argument_list|,
name|fc
argument_list|,
name|file1
argument_list|,
name|file2
argument_list|)
expr_stmt|;
name|Path
name|dir
init|=
name|getTestPath
argument_list|(
literal|"dir3/dir4/dir5/dir6"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|fc
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|fc
operator|.
name|deleteOnExit
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|checkDeleteOnExitData
argument_list|(
literal|1
argument_list|,
name|fc
argument_list|,
name|file1
argument_list|,
name|file2
argument_list|,
name|dir
argument_list|)
expr_stmt|;
comment|// trigger deleteOnExit and ensure the registered
comment|// paths are cleaned up
name|FileContext
operator|.
name|FINALIZER
operator|.
name|start
argument_list|()
expr_stmt|;
name|FileContext
operator|.
name|FINALIZER
operator|.
name|join
argument_list|()
expr_stmt|;
name|checkDeleteOnExitData
argument_list|(
literal|0
argument_list|,
name|fc
argument_list|,
operator|new
name|Path
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|fc
operator|.
name|exists
argument_list|(
name|file1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|fc
operator|.
name|exists
argument_list|(
name|file2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|fc
operator|.
name|exists
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

