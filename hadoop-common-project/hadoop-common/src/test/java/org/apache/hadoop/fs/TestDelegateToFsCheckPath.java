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
name|permission
operator|.
name|FsPermission
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
name|Progressable
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
comment|/**  * The default port of DelegateToFileSystem is set from child file system.  */
end_comment

begin_class
DECL|class|TestDelegateToFsCheckPath
specifier|public
class|class
name|TestDelegateToFsCheckPath
block|{
annotation|@
name|Test
DECL|method|testCheckPathWithoutDefaultPort ()
specifier|public
name|void
name|testCheckPathWithoutDefaultPort
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"dummy://dummy-host"
argument_list|)
decl_stmt|;
name|AbstractFileSystem
name|afs
init|=
operator|new
name|DummyDelegateToFileSystem
argument_list|(
name|uri
argument_list|,
operator|new
name|UnOverrideDefaultPortFileSystem
argument_list|()
argument_list|)
decl_stmt|;
name|afs
operator|.
name|checkPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"dummy://dummy-host"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCheckPathWithDefaultPort ()
specifier|public
name|void
name|testCheckPathWithDefaultPort
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"dummy://dummy-host:%d"
argument_list|,
name|OverrideDefaultPortFileSystem
operator|.
name|DEFAULT_PORT
argument_list|)
argument_list|)
decl_stmt|;
name|AbstractFileSystem
name|afs
init|=
operator|new
name|DummyDelegateToFileSystem
argument_list|(
name|uri
argument_list|,
operator|new
name|OverrideDefaultPortFileSystem
argument_list|()
argument_list|)
decl_stmt|;
name|afs
operator|.
name|checkPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"dummy://dummy-host/user/john/test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|DummyDelegateToFileSystem
specifier|private
specifier|static
class|class
name|DummyDelegateToFileSystem
extends|extends
name|DelegateToFileSystem
block|{
DECL|method|DummyDelegateToFileSystem (URI uri, FileSystem fs)
specifier|public
name|DummyDelegateToFileSystem
parameter_list|(
name|URI
name|uri
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|uri
argument_list|,
name|fs
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|,
literal|"dummy"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * UnOverrideDefaultPortFileSystem does not define default port.    * The default port defined by AbstractFilesystem is used in this case.    * (default 0).    */
DECL|class|UnOverrideDefaultPortFileSystem
specifier|private
specifier|static
class|class
name|UnOverrideDefaultPortFileSystem
extends|extends
name|FileSystem
block|{
annotation|@
name|Override
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
comment|// deliberately empty
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|open (Path f, int bufferSize)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|Path
name|f
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
comment|// deliberately empty
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|create (Path f, FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|boolean
name|overwrite
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
comment|// deliberately empty
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|append (Path f, int bufferSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
name|Path
name|f
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
comment|// deliberately empty
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|rename (Path src, Path dst)
specifier|public
name|boolean
name|rename
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
comment|// deliberately empty
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|delete (Path f, boolean recursive)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|f
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
comment|// deliberately empty
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|listStatus (Path f)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
comment|// deliberately empty
return|return
operator|new
name|FileStatus
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|setWorkingDirectory (Path newDir)
specifier|public
name|void
name|setWorkingDirectory
parameter_list|(
name|Path
name|newDir
parameter_list|)
block|{
comment|// deliberately empty
block|}
annotation|@
name|Override
DECL|method|getWorkingDirectory ()
specifier|public
name|Path
name|getWorkingDirectory
parameter_list|()
block|{
comment|// deliberately empty
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|mkdirs (Path f, FsPermission permission)
specifier|public
name|boolean
name|mkdirs
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
comment|// deliberately empty
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStatus (Path f)
specifier|public
name|FileStatus
name|getFileStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
comment|// deliberately empty
return|return
literal|null
return|;
block|}
block|}
comment|/**    * OverrideDefaultPortFileSystem defines default port.    */
DECL|class|OverrideDefaultPortFileSystem
specifier|private
specifier|static
class|class
name|OverrideDefaultPortFileSystem
extends|extends
name|UnOverrideDefaultPortFileSystem
block|{
DECL|field|DEFAULT_PORT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_PORT
init|=
literal|1234
decl_stmt|;
annotation|@
name|Override
DECL|method|getDefaultPort ()
specifier|public
name|int
name|getDefaultPort
parameter_list|()
block|{
return|return
name|DEFAULT_PORT
return|;
block|}
block|}
block|}
end_class

end_unit

