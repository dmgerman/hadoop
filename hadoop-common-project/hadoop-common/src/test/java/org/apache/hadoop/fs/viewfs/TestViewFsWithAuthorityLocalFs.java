begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileContext
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
name|FsConstants
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
comment|/**  *   * Test the ViewFsBaseTest using a viewfs with authority:   *    viewfs://mountTableName/  *    ie the authority is used to load a mount table.  *    The authority name used is "default"  *  */
end_comment

begin_class
DECL|class|TestViewFsWithAuthorityLocalFs
specifier|public
class|class
name|TestViewFsWithAuthorityLocalFs
extends|extends
name|ViewFsBaseTest
block|{
DECL|field|schemeWithAuthority
name|URI
name|schemeWithAuthority
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create the test root on local_fs
name|fcTarget
operator|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// this sets up conf (and fcView which we replace)
comment|// Now create a viewfs using a mount table called "default"
comment|// hence viewfs://default/
name|schemeWithAuthority
operator|=
operator|new
name|URI
argument_list|(
name|FsConstants
operator|.
name|VIEWFS_SCHEME
argument_list|,
literal|"default"
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fcView
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|schemeWithAuthority
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testBasicPaths ()
specifier|public
name|void
name|testBasicPaths
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|schemeWithAuthority
argument_list|,
name|fcView
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fcView
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|fcView
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fcView
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|fcView
operator|.
name|getHomeDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/foo/bar"
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|schemeWithAuthority
argument_list|,
literal|null
argument_list|)
argument_list|,
name|fcView
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

