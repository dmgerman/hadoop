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
name|Set
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
comment|/**  * Tests resolution of AbstractFileSystems for a given path with symlinks.  */
end_comment

begin_class
DECL|class|TestFileContextResolveAfs
specifier|public
class|class
name|TestFileContextResolveAfs
block|{
static|static
block|{
name|FileSystem
operator|.
name|enableSymlinks
argument_list|()
expr_stmt|;
block|}
DECL|field|TEST_ROOT_DIR_LOCAL
specifier|private
specifier|static
name|String
name|TEST_ROOT_DIR_LOCAL
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|fc
specifier|private
name|FileContext
name|fc
decl_stmt|;
DECL|field|localFs
specifier|private
name|FileSystem
name|localFs
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
name|getFileContext
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testFileContextResolveAfs ()
specifier|public
name|void
name|testFileContextResolveAfs
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|localFs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Path
name|localPath
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR_LOCAL
operator|+
literal|"/TestFileContextResolveAfs1"
argument_list|)
decl_stmt|;
name|Path
name|linkPath
init|=
name|localFs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR_LOCAL
argument_list|,
literal|"TestFileContextResolveAfs2"
argument_list|)
argument_list|)
decl_stmt|;
name|localFs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR_LOCAL
argument_list|)
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|create
argument_list|(
name|localPath
argument_list|)
expr_stmt|;
name|fc
operator|.
name|createSymlink
argument_list|(
name|localPath
argument_list|,
name|linkPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|AbstractFileSystem
argument_list|>
name|afsList
init|=
name|fc
operator|.
name|resolveAbstractFileSystems
argument_list|(
name|linkPath
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|afsList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|deleteOnExit
argument_list|(
name|localPath
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|deleteOnExit
argument_list|(
name|linkPath
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

