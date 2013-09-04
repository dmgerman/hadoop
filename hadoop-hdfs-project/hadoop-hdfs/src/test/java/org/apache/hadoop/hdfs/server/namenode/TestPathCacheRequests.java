begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|assertEquals
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
name|Arrays
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
name|RemoteIterator
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|hdfs
operator|.
name|protocol
operator|.
name|AddPathCacheDirectiveException
operator|.
name|EmptyPathError
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
name|hdfs
operator|.
name|protocol
operator|.
name|AddPathCacheDirectiveException
operator|.
name|InvalidPathNameError
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
name|hdfs
operator|.
name|protocol
operator|.
name|AddPathCacheDirectiveException
operator|.
name|InvalidPoolError
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
name|hdfs
operator|.
name|protocol
operator|.
name|AddPathCacheDirectiveException
operator|.
name|PoolWritePermissionDeniedError
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
name|hdfs
operator|.
name|protocol
operator|.
name|CachePoolInfo
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
name|hdfs
operator|.
name|protocol
operator|.
name|PathCacheDirective
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
name|hdfs
operator|.
name|protocol
operator|.
name|PathCacheEntry
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
name|hdfs
operator|.
name|protocol
operator|.
name|RemovePathCacheEntryException
operator|.
name|InvalidIdException
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
name|hdfs
operator|.
name|protocol
operator|.
name|RemovePathCacheEntryException
operator|.
name|NoSuchIdException
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|NamenodeProtocols
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
name|util
operator|.
name|Fallible
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

begin_class
DECL|class|TestPathCacheRequests
specifier|public
class|class
name|TestPathCacheRequests
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestPathCacheRequests
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|proto
specifier|private
specifier|static
name|NamenodeProtocols
name|proto
init|=
literal|null
decl_stmt|;
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
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|proto
operator|=
name|cluster
operator|.
name|getNameNodeRpc
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
name|Exception
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCreateAndRemovePools ()
specifier|public
name|void
name|testCreateAndRemovePools
parameter_list|()
throws|throws
name|Exception
block|{
name|CachePoolInfo
name|req
init|=
name|CachePoolInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPoolName
argument_list|(
literal|"pool1"
argument_list|)
operator|.
name|setOwnerName
argument_list|(
literal|"bob"
argument_list|)
operator|.
name|setGroupName
argument_list|(
literal|"bobgroup"
argument_list|)
operator|.
name|setMode
argument_list|(
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
argument_list|)
operator|.
name|setWeight
argument_list|(
literal|150
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CachePool
name|pool
init|=
name|proto
operator|.
name|addCachePool
argument_list|(
name|req
argument_list|)
decl_stmt|;
try|try
block|{
name|proto
operator|.
name|removeCachePool
argument_list|(
literal|909
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected to get an exception when "
operator|+
literal|"removing a non-existent pool."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{     }
name|proto
operator|.
name|removeCachePool
argument_list|(
name|pool
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|proto
operator|.
name|removeCachePool
argument_list|(
name|pool
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected to get an exception when "
operator|+
literal|"removing a non-existent pool."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{     }
name|req
operator|=
operator|new
name|CachePoolInfo
argument_list|(
literal|"pool2"
argument_list|)
expr_stmt|;
name|proto
operator|.
name|addCachePool
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateAndModifyPools ()
specifier|public
name|void
name|testCreateAndModifyPools
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a new pool
name|CachePoolInfo
name|info
init|=
name|CachePoolInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPoolName
argument_list|(
literal|"pool1"
argument_list|)
operator|.
name|setOwnerName
argument_list|(
literal|"abc"
argument_list|)
operator|.
name|setGroupName
argument_list|(
literal|"123"
argument_list|)
operator|.
name|setMode
argument_list|(
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
argument_list|)
operator|.
name|setWeight
argument_list|(
literal|150
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CachePool
name|pool
init|=
name|proto
operator|.
name|addCachePool
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|CachePoolInfo
name|actualInfo
init|=
name|pool
operator|.
name|getInfo
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected info to match create time settings"
argument_list|,
name|info
argument_list|,
name|actualInfo
argument_list|)
expr_stmt|;
comment|// Modify the pool
name|info
operator|=
name|CachePoolInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPoolName
argument_list|(
literal|"pool2"
argument_list|)
operator|.
name|setOwnerName
argument_list|(
literal|"def"
argument_list|)
operator|.
name|setGroupName
argument_list|(
literal|"456"
argument_list|)
operator|.
name|setMode
argument_list|(
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0644
argument_list|)
argument_list|)
operator|.
name|setWeight
argument_list|(
literal|200
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|proto
operator|.
name|modifyCachePool
argument_list|(
name|pool
operator|.
name|getId
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// Check via listing this time
name|RemoteIterator
argument_list|<
name|CachePool
argument_list|>
name|iter
init|=
name|proto
operator|.
name|listCachePools
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|CachePool
name|listedPool
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|actualInfo
operator|=
name|listedPool
operator|.
name|getInfo
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected info to match modified settings"
argument_list|,
name|info
argument_list|,
name|actualInfo
argument_list|)
expr_stmt|;
try|try
block|{
name|proto
operator|.
name|removeCachePool
argument_list|(
literal|808
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected to get an exception when "
operator|+
literal|"removing a non-existent pool."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{     }
name|proto
operator|.
name|removeCachePool
argument_list|(
name|pool
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|proto
operator|.
name|removeCachePool
argument_list|(
name|pool
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected to get an exception when "
operator|+
literal|"removing a non-existent pool."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{     }
block|}
DECL|method|validateListAll ( RemoteIterator<PathCacheEntry> iter, long id0, long id1, long id2)
specifier|private
specifier|static
name|void
name|validateListAll
parameter_list|(
name|RemoteIterator
argument_list|<
name|PathCacheEntry
argument_list|>
name|iter
parameter_list|,
name|long
name|id0
parameter_list|,
name|long
name|id1
parameter_list|,
name|long
name|id2
parameter_list|)
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|PathCacheEntry
argument_list|(
name|id0
argument_list|,
operator|new
name|PathCacheDirective
argument_list|(
literal|"/alpha"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|PathCacheEntry
argument_list|(
name|id1
argument_list|,
operator|new
name|PathCacheDirective
argument_list|(
literal|"/beta"
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|PathCacheEntry
argument_list|(
name|id2
argument_list|,
operator|new
name|PathCacheDirective
argument_list|(
literal|"/gamma"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetAndGet ()
specifier|public
name|void
name|testSetAndGet
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|CachePool
name|pool1
init|=
name|proto
operator|.
name|addCachePool
argument_list|(
operator|new
name|CachePoolInfo
argument_list|(
literal|"pool1"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|CachePool
name|pool2
init|=
name|proto
operator|.
name|addCachePool
argument_list|(
operator|new
name|CachePoolInfo
argument_list|(
literal|"pool2"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|CachePool
name|pool3
init|=
name|proto
operator|.
name|addCachePool
argument_list|(
operator|new
name|CachePoolInfo
argument_list|(
literal|"pool3"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|CachePool
name|pool4
init|=
name|proto
operator|.
name|addCachePool
argument_list|(
name|CachePoolInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPoolName
argument_list|(
literal|"pool4"
argument_list|)
operator|.
name|setMode
argument_list|(
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|testUgi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"myuser"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mygroup"
block|}
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Fallible
argument_list|<
name|PathCacheEntry
argument_list|>
argument_list|>
name|addResults1
init|=
name|testUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|List
argument_list|<
name|Fallible
argument_list|<
name|PathCacheEntry
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Fallible
argument_list|<
name|PathCacheEntry
argument_list|>
argument_list|>
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Fallible
argument_list|<
name|PathCacheEntry
argument_list|>
argument_list|>
name|entries
decl_stmt|;
name|entries
operator|=
name|proto
operator|.
name|addPathCacheDirectives
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|PathCacheDirective
index|[]
block|{
operator|new
name|PathCacheDirective
argument_list|(
literal|"/alpha"
argument_list|,
name|pool1
operator|.
name|getId
argument_list|()
argument_list|)
block|,
operator|new
name|PathCacheDirective
argument_list|(
literal|"/beta"
argument_list|,
name|pool2
operator|.
name|getId
argument_list|()
argument_list|)
block|,
operator|new
name|PathCacheDirective
argument_list|(
literal|""
argument_list|,
name|pool3
operator|.
name|getId
argument_list|()
argument_list|)
block|,
operator|new
name|PathCacheDirective
argument_list|(
literal|"/zeta"
argument_list|,
literal|404
argument_list|)
block|,
operator|new
name|PathCacheDirective
argument_list|(
literal|"/zeta"
argument_list|,
name|pool4
operator|.
name|getId
argument_list|()
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|entries
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Save the successful additions
name|long
name|ids1
index|[]
init|=
operator|new
name|long
index|[
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|ids1
index|[
name|i
index|]
operator|=
name|addResults1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getEntryId
argument_list|()
expr_stmt|;
block|}
comment|// Verify that the unsuccessful additions failed properly
try|try
block|{
name|addResults1
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected an error when adding an empty path"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|EmptyPathError
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|addResults1
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected an error when adding to a nonexistent pool."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InvalidPoolError
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|addResults1
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected an error when adding to a pool with "
operator|+
literal|"mode 0 (no permissions for anyone)."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|PoolWritePermissionDeniedError
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Fallible
argument_list|<
name|PathCacheEntry
argument_list|>
argument_list|>
name|addResults2
init|=
name|proto
operator|.
name|addPathCacheDirectives
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|PathCacheDirective
index|[]
block|{
operator|new
name|PathCacheDirective
argument_list|(
literal|"/alpha"
argument_list|,
name|pool1
operator|.
name|getId
argument_list|()
argument_list|)
block|,
operator|new
name|PathCacheDirective
argument_list|(
literal|"/theta"
argument_list|,
literal|404
argument_list|)
block|,
operator|new
name|PathCacheDirective
argument_list|(
literal|"bogus"
argument_list|,
name|pool1
operator|.
name|getId
argument_list|()
argument_list|)
block|,
operator|new
name|PathCacheDirective
argument_list|(
literal|"/gamma"
argument_list|,
name|pool1
operator|.
name|getId
argument_list|()
argument_list|)
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|id
init|=
name|addResults2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getEntryId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"expected to get back the same ID as last time "
operator|+
literal|"when re-adding an existing path cache directive."
argument_list|,
name|ids1
index|[
literal|0
index|]
argument_list|,
name|id
argument_list|)
expr_stmt|;
try|try
block|{
name|addResults2
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected an error when adding a path cache "
operator|+
literal|"directive with an empty pool name."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InvalidPoolError
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|addResults2
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected an error when adding a path cache "
operator|+
literal|"directive with a non-absolute path name."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InvalidPathNameError
argument_list|)
expr_stmt|;
block|}
name|long
name|ids2
index|[]
init|=
operator|new
name|long
index|[
literal|1
index|]
decl_stmt|;
name|ids2
index|[
literal|0
index|]
operator|=
name|addResults2
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getEntryId
argument_list|()
expr_stmt|;
comment|// Validate listing all entries
name|RemoteIterator
argument_list|<
name|PathCacheEntry
argument_list|>
name|iter
init|=
name|proto
operator|.
name|listPathCacheEntries
argument_list|(
operator|-
literal|1l
argument_list|,
operator|-
literal|1l
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|validateListAll
argument_list|(
name|iter
argument_list|,
name|ids1
index|[
literal|0
index|]
argument_list|,
name|ids1
index|[
literal|1
index|]
argument_list|,
name|ids2
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|iter
operator|=
name|proto
operator|.
name|listPathCacheEntries
argument_list|(
operator|-
literal|1l
argument_list|,
operator|-
literal|1l
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|validateListAll
argument_list|(
name|iter
argument_list|,
name|ids1
index|[
literal|0
index|]
argument_list|,
name|ids1
index|[
literal|1
index|]
argument_list|,
name|ids2
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// Validate listing certain pools
name|iter
operator|=
name|proto
operator|.
name|listPathCacheEntries
argument_list|(
literal|0
argument_list|,
name|pool3
operator|.
name|getId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|iter
operator|=
name|proto
operator|.
name|listPathCacheEntries
argument_list|(
literal|0
argument_list|,
name|pool2
operator|.
name|getId
argument_list|()
argument_list|,
literal|4444
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|addResults1
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Fallible
argument_list|<
name|Long
argument_list|>
argument_list|>
name|removeResults1
init|=
name|proto
operator|.
name|removePathCacheEntries
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|ids1
index|[
literal|1
index|]
block|,
operator|-
literal|42L
block|,
literal|999999L
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|ids1
index|[
literal|1
index|]
argument_list|)
argument_list|,
name|removeResults1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|removeResults1
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected an error when removing a negative ID"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InvalidIdException
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|removeResults1
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected an error when removing a nonexistent ID"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NoSuchIdException
argument_list|)
expr_stmt|;
block|}
name|iter
operator|=
name|proto
operator|.
name|listPathCacheEntries
argument_list|(
literal|0
argument_list|,
name|pool2
operator|.
name|getId
argument_list|()
argument_list|,
literal|4444
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

