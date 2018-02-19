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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Semaphore
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
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

begin_class
DECL|class|TestFileSystemCaching
specifier|public
class|class
name|TestFileSystemCaching
block|{
annotation|@
name|Test
DECL|method|testCacheEnabled ()
specifier|public
name|void
name|testCacheEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|newConf
argument_list|()
decl_stmt|;
name|FileSystem
name|fs1
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"cachedfile://a"
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|fs2
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"cachedfile://a"
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|fs1
argument_list|,
name|fs2
argument_list|)
expr_stmt|;
block|}
DECL|class|DefaultFs
specifier|private
specifier|static
class|class
name|DefaultFs
extends|extends
name|LocalFileSystem
block|{
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
block|{
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
block|}
annotation|@
name|Test
DECL|method|testDefaultFsUris ()
specifier|public
name|void
name|testDefaultFsUris
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.defaultfs.impl"
argument_list|,
name|DefaultFs
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|URI
name|defaultUri
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"defaultfs://host"
argument_list|)
decl_stmt|;
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
name|defaultUri
argument_list|)
expr_stmt|;
comment|// sanity check default fs
specifier|final
name|FileSystem
name|defaultFs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|defaultUri
argument_list|,
name|defaultFs
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
comment|// has scheme, no auth
name|assertSame
argument_list|(
name|defaultFs
argument_list|,
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"defaultfs:/"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|defaultFs
argument_list|,
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"defaultfs:///"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// has scheme, same auth
name|assertSame
argument_list|(
name|defaultFs
argument_list|,
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"defaultfs://host"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// has scheme, different auth
name|assertNotSame
argument_list|(
name|defaultFs
argument_list|,
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"defaultfs://host2"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// no scheme, no auth
name|assertSame
argument_list|(
name|defaultFs
argument_list|,
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// no scheme, same auth
name|intercept
argument_list|(
name|UnsupportedFileSystemException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"//host"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|UnsupportedFileSystemException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"//host2"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|InitializeForeverFileSystem
specifier|public
specifier|static
class|class
name|InitializeForeverFileSystem
extends|extends
name|LocalFileSystem
block|{
DECL|field|sem
specifier|final
specifier|static
name|Semaphore
name|sem
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
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
comment|// notify that InitializeForeverFileSystem started initialization
name|sem
operator|.
name|release
argument_list|()
expr_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testCacheEnabledWithInitializeForeverFS ()
specifier|public
name|void
name|testCacheEnabledWithInitializeForeverFS
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|conf
operator|.
name|set
argument_list|(
literal|"fs.localfs1.impl"
argument_list|,
literal|"org.apache.hadoop.fs."
operator|+
literal|"TestFileSystemCaching$InitializeForeverFileSystem"
argument_list|)
expr_stmt|;
try|try
block|{
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"localfs1://a"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|URISyntaxException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// wait for InitializeForeverFileSystem to start initialization
name|InitializeForeverFileSystem
operator|.
name|sem
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.cachedfile.impl"
argument_list|,
name|FileSystem
operator|.
name|getFileSystemClass
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"cachedfile://a"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCacheDisabled ()
specifier|public
name|void
name|testCacheDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.uncachedfile.impl"
argument_list|,
name|FileSystem
operator|.
name|getFileSystemClass
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"fs.uncachedfile.impl.disable.cache"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileSystem
name|fs1
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"uncachedfile://a"
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|fs2
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"uncachedfile://a"
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|fs1
argument_list|,
name|fs2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testCacheForUgi ()
specifier|public
parameter_list|<
name|T
extends|extends
name|TokenIdentifier
parameter_list|>
name|void
name|testCacheForUgi
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
name|newConf
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|ugiA
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugiB
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"bar"
argument_list|)
decl_stmt|;
name|FileSystem
name|fsA
init|=
name|getCachedFS
argument_list|(
name|ugiA
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|fsA1
init|=
name|getCachedFS
argument_list|(
name|ugiA
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|//Since the UGIs are the same, we should have the same filesystem for both
name|assertSame
argument_list|(
name|fsA
argument_list|,
name|fsA1
argument_list|)
expr_stmt|;
name|FileSystem
name|fsB
init|=
name|getCachedFS
argument_list|(
name|ugiB
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|//Since the UGIs are different, we should end up with different filesystems
comment|//corresponding to the two UGIs
name|assertNotSame
argument_list|(
name|fsA
argument_list|,
name|fsB
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|T
argument_list|>
name|t1
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugiA2
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|fsA
operator|=
name|getCachedFS
argument_list|(
name|ugiA2
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Although the users in the UGI are same, they have different subjects
comment|// and so are different.
name|assertNotSame
argument_list|(
name|fsA
argument_list|,
name|fsA1
argument_list|)
expr_stmt|;
name|ugiA
operator|.
name|addToken
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|fsA
operator|=
name|getCachedFS
argument_list|(
name|ugiA
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Make sure that different UGI's with the same subject lead to the same
comment|// file system.
name|assertSame
argument_list|(
name|fsA
argument_list|,
name|fsA1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the cached filesystem for "cachedfile://a" for the supplied user    * @param ugi user    * @param conf configuration    * @return the filesystem    * @throws IOException failure to get/init    * @throws InterruptedException part of the signature of UGI.doAs()    */
DECL|method|getCachedFS (UserGroupInformation ugi, Configuration conf)
specifier|private
name|FileSystem
name|getCachedFS
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|ugi
operator|.
name|doAs
argument_list|(
call|(
name|PrivilegedExceptionAction
argument_list|<
name|FileSystem
argument_list|>
call|)
argument_list|()
operator|->
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"cachedfile://a"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testUserFS ()
specifier|public
name|void
name|testUserFS
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
name|newConf
argument_list|()
decl_stmt|;
name|FileSystem
name|fsU1
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"cachedfile://a"
argument_list|)
argument_list|,
name|conf
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|FileSystem
name|fsU2
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"cachedfile://a"
argument_list|)
argument_list|,
name|conf
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|fsU1
argument_list|,
name|fsU2
argument_list|)
expr_stmt|;
block|}
DECL|method|newConf ()
specifier|private
name|Configuration
name|newConf
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.cachedfile.impl"
argument_list|,
name|FileSystem
operator|.
name|getFileSystemClass
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|testFsUniqueness ()
specifier|public
name|void
name|testFsUniqueness
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
name|newConf
argument_list|()
decl_stmt|;
comment|// multiple invocations of FileSystem.get return the same object.
name|FileSystem
name|fs1
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|fs2
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|fs1
argument_list|,
name|fs2
argument_list|)
expr_stmt|;
comment|// multiple invocations of FileSystem.newInstance return different objects
name|fs1
operator|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
operator|new
name|URI
argument_list|(
literal|"cachedfile://a"
argument_list|)
argument_list|,
name|conf
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|fs2
operator|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
operator|new
name|URI
argument_list|(
literal|"cachedfile://a"
argument_list|)
argument_list|,
name|conf
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs1
operator|!=
name|fs2
operator|&&
operator|!
name|fs1
operator|.
name|equals
argument_list|(
name|fs2
argument_list|)
argument_list|)
expr_stmt|;
name|fs1
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseAllForUGI ()
specifier|public
name|void
name|testCloseAllForUGI
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
name|newConf
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|ugiA
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|FileSystem
name|fsA
init|=
name|getCachedFS
argument_list|(
name|ugiA
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|//Now we should get the cached filesystem
name|FileSystem
name|fsA1
init|=
name|getCachedFS
argument_list|(
name|ugiA
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|fsA
argument_list|,
name|fsA1
argument_list|)
expr_stmt|;
name|FileSystem
operator|.
name|closeAllForUGI
argument_list|(
name|ugiA
argument_list|)
expr_stmt|;
comment|//Now we should get a different (newly created) filesystem
name|fsA1
operator|=
name|getCachedFS
argument_list|(
name|ugiA
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|fsA
argument_list|,
name|fsA1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelete ()
specifier|public
name|void
name|testDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|FilterFileSystem
argument_list|(
name|mockFs
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|delete
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|reset
argument_list|(
name|mockFs
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|delete
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteOnExit ()
specifier|public
name|void
name|testDeleteOnExit
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
try|try
init|(
name|FileSystem
name|fs
init|=
operator|new
name|FilterFileSystem
argument_list|(
name|mockFs
argument_list|)
init|)
block|{
comment|// delete on close if path does exist
name|when
argument_list|(
name|mockFs
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|FileStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|deleteOnExit
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|reset
argument_list|(
name|mockFs
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockFs
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|FileStatus
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|delete
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteOnExitFNF ()
specifier|public
name|void
name|testDeleteOnExitFNF
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|Path
name|path
decl_stmt|;
try|try
init|(
name|FileSystem
name|fs
init|=
operator|new
name|FilterFileSystem
argument_list|(
name|mockFs
argument_list|)
init|)
block|{
name|path
operator|=
operator|new
name|Path
argument_list|(
literal|"/a"
argument_list|)
expr_stmt|;
comment|// don't delete on close if path doesn't exist
name|assertFalse
argument_list|(
name|fs
operator|.
name|deleteOnExit
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|reset
argument_list|(
name|mockFs
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|verify
argument_list|(
name|mockFs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|delete
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteOnExitRemoved ()
specifier|public
name|void
name|testDeleteOnExitRemoved
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|Path
name|path
decl_stmt|;
try|try
init|(
name|FileSystem
name|fs
init|=
operator|new
name|FilterFileSystem
argument_list|(
name|mockFs
argument_list|)
init|)
block|{
name|path
operator|=
operator|new
name|Path
argument_list|(
literal|"/a"
argument_list|)
expr_stmt|;
comment|// don't delete on close if path existed, but later removed
name|when
argument_list|(
name|mockFs
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|FileStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|deleteOnExit
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|reset
argument_list|(
name|mockFs
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|delete
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCancelDeleteOnExit ()
specifier|public
name|void
name|testCancelDeleteOnExit
parameter_list|()
throws|throws
name|IOException
block|{
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
init|(
name|FileSystem
name|fs
init|=
operator|new
name|FilterFileSystem
argument_list|(
name|mockFs
argument_list|)
init|)
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
comment|// don't delete on close if path existed, but later cancelled
name|when
argument_list|(
name|mockFs
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|FileStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|deleteOnExit
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|getFileStatus
argument_list|(
name|eq
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|cancelDeleteOnExit
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fs
operator|.
name|cancelDeleteOnExit
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
comment|// false because not registered
name|reset
argument_list|(
name|mockFs
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|verify
argument_list|(
name|mockFs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getFileStatus
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|delete
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCacheIncludesURIUserInfo ()
specifier|public
name|void
name|testCacheIncludesURIUserInfo
parameter_list|()
throws|throws
name|Throwable
block|{
name|URI
name|containerA
init|=
operator|new
name|URI
argument_list|(
literal|"wasb://a@account.blob.core.windows.net"
argument_list|)
decl_stmt|;
name|URI
name|containerB
init|=
operator|new
name|URI
argument_list|(
literal|"wasb://b@account.blob.core.windows.net"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|FileSystem
operator|.
name|Cache
operator|.
name|Key
name|keyA
init|=
operator|new
name|FileSystem
operator|.
name|Cache
operator|.
name|Key
argument_list|(
name|containerA
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
operator|.
name|Cache
operator|.
name|Key
name|keyB
init|=
operator|new
name|FileSystem
operator|.
name|Cache
operator|.
name|Key
argument_list|(
name|containerB
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|keyA
argument_list|,
name|keyB
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|keyA
argument_list|,
operator|new
name|FileSystem
operator|.
name|Cache
operator|.
name|Key
argument_list|(
operator|new
name|URI
argument_list|(
literal|"wasb://account.blob.core.windows.net"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|keyA
argument_list|,
operator|new
name|FileSystem
operator|.
name|Cache
operator|.
name|Key
argument_list|(
operator|new
name|URI
argument_list|(
literal|"wasb://A@account.blob.core.windows.net"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|keyA
argument_list|,
operator|new
name|FileSystem
operator|.
name|Cache
operator|.
name|Key
argument_list|(
operator|new
name|URI
argument_list|(
literal|"wasb://a:password@account.blob.core.windows.net"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

