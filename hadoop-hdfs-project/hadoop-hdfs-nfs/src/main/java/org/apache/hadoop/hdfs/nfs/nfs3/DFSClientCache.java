begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
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
name|ExecutionException
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
name|TimeUnit
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
name|FSDataInputStream
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
name|DFSClient
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
name|DFSInputStream
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
name|namenode
operator|.
name|NameNode
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheLoader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|LoadingCache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalNotification
import|;
end_import

begin_comment
comment|/**  * A cache saves DFSClient objects for different users  */
end_comment

begin_class
DECL|class|DFSClientCache
class|class
name|DFSClientCache
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DFSClientCache
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Cache that maps User id to the corresponding DFSClient.    */
annotation|@
name|VisibleForTesting
DECL|field|clientCache
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|DFSClient
argument_list|>
name|clientCache
decl_stmt|;
DECL|field|DEFAULT_DFS_CLIENT_CACHE_SIZE
specifier|final
specifier|static
name|int
name|DEFAULT_DFS_CLIENT_CACHE_SIZE
init|=
literal|256
decl_stmt|;
comment|/**    * Cache that maps<DFSClient, inode path> to the corresponding    * FSDataInputStream.    */
DECL|field|inputstreamCache
specifier|final
name|LoadingCache
argument_list|<
name|DFSInputStreamCaheKey
argument_list|,
name|FSDataInputStream
argument_list|>
name|inputstreamCache
decl_stmt|;
comment|/**    * Time to live for a DFSClient (in seconds)    */
DECL|field|DEFAULT_DFS_INPUTSTREAM_CACHE_SIZE
specifier|final
specifier|static
name|int
name|DEFAULT_DFS_INPUTSTREAM_CACHE_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|DEFAULT_DFS_INPUTSTREAM_CACHE_TTL
specifier|final
specifier|static
name|int
name|DEFAULT_DFS_INPUTSTREAM_CACHE_TTL
init|=
literal|10
operator|*
literal|60
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Configuration
name|config
decl_stmt|;
DECL|class|DFSInputStreamCaheKey
specifier|private
specifier|static
class|class
name|DFSInputStreamCaheKey
block|{
DECL|field|userId
specifier|final
name|String
name|userId
decl_stmt|;
DECL|field|inodePath
specifier|final
name|String
name|inodePath
decl_stmt|;
DECL|method|DFSInputStreamCaheKey (String userId, String inodePath)
specifier|private
name|DFSInputStreamCaheKey
parameter_list|(
name|String
name|userId
parameter_list|,
name|String
name|inodePath
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
name|this
operator|.
name|inodePath
operator|=
name|inodePath
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|DFSInputStreamCaheKey
condition|)
block|{
name|DFSInputStreamCaheKey
name|k
init|=
operator|(
name|DFSInputStreamCaheKey
operator|)
name|obj
decl_stmt|;
return|return
name|userId
operator|.
name|equals
argument_list|(
name|k
operator|.
name|userId
argument_list|)
operator|&&
name|inodePath
operator|.
name|equals
argument_list|(
name|k
operator|.
name|inodePath
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|userId
argument_list|,
name|inodePath
argument_list|)
return|;
block|}
block|}
DECL|method|DFSClientCache (Configuration config)
name|DFSClientCache
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|this
argument_list|(
name|config
argument_list|,
name|DEFAULT_DFS_CLIENT_CACHE_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|DFSClientCache (Configuration config, int clientCache)
name|DFSClientCache
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|int
name|clientCache
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|clientCache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumSize
argument_list|(
name|clientCache
argument_list|)
operator|.
name|removalListener
argument_list|(
name|clientRemovealListener
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|clientLoader
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|inputstreamCache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumSize
argument_list|(
name|DEFAULT_DFS_INPUTSTREAM_CACHE_SIZE
argument_list|)
operator|.
name|expireAfterAccess
argument_list|(
name|DEFAULT_DFS_INPUTSTREAM_CACHE_TTL
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|removalListener
argument_list|(
name|inputStreamRemovalListener
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|inputStreamLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|clientLoader ()
specifier|private
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|DFSClient
argument_list|>
name|clientLoader
parameter_list|()
block|{
return|return
operator|new
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|DFSClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DFSClient
name|load
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|userName
argument_list|)
decl_stmt|;
comment|// Guava requires CacheLoader never returns null.
return|return
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|DFSClient
argument_list|>
argument_list|()
block|{
specifier|public
name|DFSClient
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DFSClient
argument_list|(
name|NameNode
operator|.
name|getAddress
argument_list|(
name|config
argument_list|)
argument_list|,
name|config
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|clientRemovealListener ()
specifier|private
name|RemovalListener
argument_list|<
name|String
argument_list|,
name|DFSClient
argument_list|>
name|clientRemovealListener
parameter_list|()
block|{
return|return
operator|new
name|RemovalListener
argument_list|<
name|String
argument_list|,
name|DFSClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|RemovalNotification
argument_list|<
name|String
argument_list|,
name|DFSClient
argument_list|>
name|notification
parameter_list|)
block|{
name|DFSClient
name|client
init|=
name|notification
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"IOException when closing the DFSClient(%s), cause: %s"
argument_list|,
name|client
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|inputStreamRemovalListener ()
specifier|private
name|RemovalListener
argument_list|<
name|DFSInputStreamCaheKey
argument_list|,
name|FSDataInputStream
argument_list|>
name|inputStreamRemovalListener
parameter_list|()
block|{
return|return
operator|new
name|RemovalListener
argument_list|<
name|DFSClientCache
operator|.
name|DFSInputStreamCaheKey
argument_list|,
name|FSDataInputStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|RemovalNotification
argument_list|<
name|DFSInputStreamCaheKey
argument_list|,
name|FSDataInputStream
argument_list|>
name|notification
parameter_list|)
block|{
try|try
block|{
name|notification
operator|.
name|getValue
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
block|}
block|}
return|;
block|}
DECL|method|inputStreamLoader ()
specifier|private
name|CacheLoader
argument_list|<
name|DFSInputStreamCaheKey
argument_list|,
name|FSDataInputStream
argument_list|>
name|inputStreamLoader
parameter_list|()
block|{
return|return
operator|new
name|CacheLoader
argument_list|<
name|DFSInputStreamCaheKey
argument_list|,
name|FSDataInputStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FSDataInputStream
name|load
parameter_list|(
name|DFSInputStreamCaheKey
name|key
parameter_list|)
throws|throws
name|Exception
block|{
name|DFSClient
name|client
init|=
name|getDfsClient
argument_list|(
name|key
operator|.
name|userId
argument_list|)
decl_stmt|;
name|DFSInputStream
name|dis
init|=
name|client
operator|.
name|open
argument_list|(
name|key
operator|.
name|inodePath
argument_list|)
decl_stmt|;
return|return
operator|new
name|FSDataInputStream
argument_list|(
name|dis
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|getDfsClient (String userName)
name|DFSClient
name|getDfsClient
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|DFSClient
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|client
operator|=
name|clientCache
operator|.
name|get
argument_list|(
name|userName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create DFSClient for user:"
operator|+
name|userName
operator|+
literal|" Cause:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
DECL|method|getDfsInputStream (String userName, String inodePath)
name|FSDataInputStream
name|getDfsInputStream
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|inodePath
parameter_list|)
block|{
name|DFSInputStreamCaheKey
name|k
init|=
operator|new
name|DFSInputStreamCaheKey
argument_list|(
name|userName
argument_list|,
name|inodePath
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s
operator|=
name|inputstreamCache
operator|.
name|get
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to create DFSInputStream for user:"
operator|+
name|userName
operator|+
literal|" Cause:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
DECL|method|invalidateDfsInputStream (String userName, String inodePath)
specifier|public
name|void
name|invalidateDfsInputStream
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|inodePath
parameter_list|)
block|{
name|DFSInputStreamCaheKey
name|k
init|=
operator|new
name|DFSInputStreamCaheKey
argument_list|(
name|userName
argument_list|,
name|inodePath
argument_list|)
decl_stmt|;
name|inputstreamCache
operator|.
name|invalidate
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

