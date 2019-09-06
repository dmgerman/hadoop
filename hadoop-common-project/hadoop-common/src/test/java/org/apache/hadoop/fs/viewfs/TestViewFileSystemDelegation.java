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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|FileSystemTestHelper
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
name|LocalFileSystem
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
name|permission
operator|.
name|AclEntry
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
name|viewfs
operator|.
name|TestChRootedFileSystem
operator|.
name|MockFileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
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
name|viewfs
operator|.
name|TestChRootedFileSystem
operator|.
name|getChildFileSystem
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Verify that viewfs propagates certain methods to the underlying fs   */
end_comment

begin_class
DECL|class|TestViewFileSystemDelegation
specifier|public
class|class
name|TestViewFileSystemDelegation
block|{
comment|//extends ViewFileSystemTestSetup {
DECL|field|conf
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|viewFs
specifier|static
name|FileSystem
name|viewFs
decl_stmt|;
DECL|field|fs1
specifier|static
name|FakeFileSystem
name|fs1
decl_stmt|;
DECL|field|fs2
specifier|static
name|FakeFileSystem
name|fs2
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
name|ViewFileSystemTestSetup
operator|.
name|createConfig
argument_list|()
expr_stmt|;
name|setupFileSystem
argument_list|(
operator|new
name|URI
argument_list|(
literal|"fs1:/"
argument_list|)
argument_list|,
name|FakeFileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|setupFileSystem
argument_list|(
operator|new
name|URI
argument_list|(
literal|"fs2:/"
argument_list|)
argument_list|,
name|FakeFileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|viewFs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|FsConstants
operator|.
name|VIEWFS_URI
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|fs1
operator|=
operator|(
name|FakeFileSystem
operator|)
name|getChildFileSystem
argument_list|(
operator|(
name|ViewFileSystem
operator|)
name|viewFs
argument_list|,
operator|new
name|URI
argument_list|(
literal|"fs1:/"
argument_list|)
argument_list|)
expr_stmt|;
name|fs2
operator|=
operator|(
name|FakeFileSystem
operator|)
name|getChildFileSystem
argument_list|(
operator|(
name|ViewFileSystem
operator|)
name|viewFs
argument_list|,
operator|new
name|URI
argument_list|(
literal|"fs2:/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setupFileSystem (URI uri, Class clazz)
specifier|static
name|void
name|setupFileSystem
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Class
name|clazz
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|scheme
init|=
name|uri
operator|.
name|getScheme
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs."
operator|+
name|scheme
operator|+
literal|".impl"
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|FakeFileSystem
name|fs
init|=
operator|(
name|FakeFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|uri
argument_list|,
name|fs
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|targetPath
init|=
operator|new
name|FileSystemTestHelper
argument_list|()
operator|.
name|getAbsoluteTestRootPath
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|ConfigUtil
operator|.
name|addLink
argument_list|(
name|conf
argument_list|,
literal|"/mounts/"
operator|+
name|scheme
argument_list|,
name|targetPath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setupMockFileSystem (Configuration config, URI uri)
specifier|private
specifier|static
name|void
name|setupMockFileSystem
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|URI
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|scheme
init|=
name|uri
operator|.
name|getScheme
argument_list|()
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
literal|"fs."
operator|+
name|scheme
operator|+
literal|".impl"
argument_list|,
name|MockFileSystem
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ConfigUtil
operator|.
name|addLink
argument_list|(
name|config
argument_list|,
literal|"/mounts/"
operator|+
name|scheme
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSanity ()
specifier|public
name|void
name|testSanity
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
literal|"fs1:/"
argument_list|)
operator|.
name|getScheme
argument_list|()
argument_list|,
name|fs1
operator|.
name|getUri
argument_list|()
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
literal|"fs1:/"
argument_list|)
operator|.
name|getAuthority
argument_list|()
argument_list|,
name|fs1
operator|.
name|getUri
argument_list|()
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
literal|"fs2:/"
argument_list|)
operator|.
name|getScheme
argument_list|()
argument_list|,
name|fs2
operator|.
name|getUri
argument_list|()
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
literal|"fs2:/"
argument_list|)
operator|.
name|getAuthority
argument_list|()
argument_list|,
name|fs2
operator|.
name|getUri
argument_list|()
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVerifyChecksum ()
specifier|public
name|void
name|testVerifyChecksum
parameter_list|()
throws|throws
name|Exception
block|{
name|checkVerifyChecksum
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|checkVerifyChecksum
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that ViewFileSystem dispatches calls for every ACL method through the    * mount table to the correct underlying FileSystem with all Path arguments    * translated as required.    */
annotation|@
name|Test
DECL|method|testAclMethods ()
specifier|public
name|void
name|testAclMethods
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|ViewFileSystemTestSetup
operator|.
name|createConfig
argument_list|()
decl_stmt|;
name|setupMockFileSystem
argument_list|(
name|conf
argument_list|,
operator|new
name|URI
argument_list|(
literal|"mockfs1:/"
argument_list|)
argument_list|)
expr_stmt|;
name|setupMockFileSystem
argument_list|(
name|conf
argument_list|,
operator|new
name|URI
argument_list|(
literal|"mockfs2:/"
argument_list|)
argument_list|)
expr_stmt|;
name|FileSystem
name|viewFs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|FsConstants
operator|.
name|VIEWFS_URI
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|mockFs1
init|=
operator|(
operator|(
name|MockFileSystem
operator|)
name|getChildFileSystem
argument_list|(
operator|(
name|ViewFileSystem
operator|)
name|viewFs
argument_list|,
operator|new
name|URI
argument_list|(
literal|"mockfs1:/"
argument_list|)
argument_list|)
operator|)
operator|.
name|getRawFileSystem
argument_list|()
decl_stmt|;
name|FileSystem
name|mockFs2
init|=
operator|(
operator|(
name|MockFileSystem
operator|)
name|getChildFileSystem
argument_list|(
operator|(
name|ViewFileSystem
operator|)
name|viewFs
argument_list|,
operator|new
name|URI
argument_list|(
literal|"mockfs2:/"
argument_list|)
argument_list|)
operator|)
operator|.
name|getRawFileSystem
argument_list|()
decl_stmt|;
name|Path
name|viewFsPath1
init|=
operator|new
name|Path
argument_list|(
literal|"/mounts/mockfs1/a/b/c"
argument_list|)
decl_stmt|;
name|Path
name|mockFsPath1
init|=
operator|new
name|Path
argument_list|(
literal|"/a/b/c"
argument_list|)
decl_stmt|;
name|Path
name|viewFsPath2
init|=
operator|new
name|Path
argument_list|(
literal|"/mounts/mockfs2/d/e/f"
argument_list|)
decl_stmt|;
name|Path
name|mockFsPath2
init|=
operator|new
name|Path
argument_list|(
literal|"/d/e/f"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entries
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|viewFs
operator|.
name|modifyAclEntries
argument_list|(
name|viewFsPath1
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs1
argument_list|)
operator|.
name|modifyAclEntries
argument_list|(
name|mockFsPath1
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|modifyAclEntries
argument_list|(
name|viewFsPath2
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs2
argument_list|)
operator|.
name|modifyAclEntries
argument_list|(
name|mockFsPath2
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|removeAclEntries
argument_list|(
name|viewFsPath1
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs1
argument_list|)
operator|.
name|removeAclEntries
argument_list|(
name|mockFsPath1
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|removeAclEntries
argument_list|(
name|viewFsPath2
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs2
argument_list|)
operator|.
name|removeAclEntries
argument_list|(
name|mockFsPath2
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|removeDefaultAcl
argument_list|(
name|viewFsPath1
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs1
argument_list|)
operator|.
name|removeDefaultAcl
argument_list|(
name|mockFsPath1
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|removeDefaultAcl
argument_list|(
name|viewFsPath2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs2
argument_list|)
operator|.
name|removeDefaultAcl
argument_list|(
name|mockFsPath2
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|removeAcl
argument_list|(
name|viewFsPath1
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs1
argument_list|)
operator|.
name|removeAcl
argument_list|(
name|mockFsPath1
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|removeAcl
argument_list|(
name|viewFsPath2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs2
argument_list|)
operator|.
name|removeAcl
argument_list|(
name|mockFsPath2
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|setAcl
argument_list|(
name|viewFsPath1
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs1
argument_list|)
operator|.
name|setAcl
argument_list|(
name|mockFsPath1
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|setAcl
argument_list|(
name|viewFsPath2
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs2
argument_list|)
operator|.
name|setAcl
argument_list|(
name|mockFsPath2
argument_list|,
name|entries
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|getAclStatus
argument_list|(
name|viewFsPath1
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs1
argument_list|)
operator|.
name|getAclStatus
argument_list|(
name|mockFsPath1
argument_list|)
expr_stmt|;
name|viewFs
operator|.
name|getAclStatus
argument_list|(
name|viewFsPath2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs2
argument_list|)
operator|.
name|getAclStatus
argument_list|(
name|mockFsPath2
argument_list|)
expr_stmt|;
block|}
DECL|method|checkVerifyChecksum (boolean flag)
name|void
name|checkVerifyChecksum
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|viewFs
operator|.
name|setVerifyChecksum
argument_list|(
name|flag
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flag
argument_list|,
name|fs1
operator|.
name|getVerifyChecksum
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flag
argument_list|,
name|fs2
operator|.
name|getVerifyChecksum
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|FakeFileSystem
specifier|static
class|class
name|FakeFileSystem
extends|extends
name|LocalFileSystem
block|{
DECL|field|verifyChecksum
name|boolean
name|verifyChecksum
init|=
literal|true
decl_stmt|;
DECL|field|uri
name|URI
name|uri
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (URI uri, Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
annotation|@
name|Override
DECL|method|setVerifyChecksum (boolean verifyChecksum)
specifier|public
name|void
name|setVerifyChecksum
parameter_list|(
name|boolean
name|verifyChecksum
parameter_list|)
block|{
name|this
operator|.
name|verifyChecksum
operator|=
name|verifyChecksum
expr_stmt|;
block|}
DECL|method|getVerifyChecksum ()
specifier|public
name|boolean
name|getVerifyChecksum
parameter_list|()
block|{
return|return
name|verifyChecksum
return|;
block|}
block|}
block|}
end_class

end_unit

