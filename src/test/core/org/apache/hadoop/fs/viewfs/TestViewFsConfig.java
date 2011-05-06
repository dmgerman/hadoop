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
name|FileAlreadyExistsException
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
name|UnsupportedFileSystemException
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
name|ConfigUtil
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
name|InodeTree
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

begin_class
DECL|class|TestViewFsConfig
specifier|public
class|class
name|TestViewFsConfig
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileAlreadyExistsException
operator|.
name|class
argument_list|)
DECL|method|testInvalidConfig ()
specifier|public
name|void
name|testInvalidConfig
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ConfigUtil
operator|.
name|addLink
argument_list|(
name|conf
argument_list|,
literal|"/internalDir/linkToDir2"
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///dir2"
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|ConfigUtil
operator|.
name|addLink
argument_list|(
name|conf
argument_list|,
literal|"/internalDir/linkToDir2/linkToDir3"
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///dir3"
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
class|class
name|Foo
block|{ }
empty_stmt|;
operator|new
name|InodeTree
argument_list|<
name|Foo
argument_list|>
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Foo
name|getTargetFileSystem
parameter_list|(
specifier|final
name|URI
name|uri
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|UnsupportedFileSystemException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Foo
name|getTargetFileSystem
parameter_list|(
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
name|InodeTree
operator|.
name|INodeDir
argument_list|<
name|Foo
argument_list|>
name|dir
parameter_list|)
throws|throws
name|URISyntaxException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Foo
name|getTargetFileSystem
parameter_list|(
name|URI
index|[]
name|mergeFsURIList
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|UnsupportedFileSystemException
block|{
return|return
literal|null
return|;
block|}
block|}
expr_stmt|;
block|}
block|}
end_class

end_unit

