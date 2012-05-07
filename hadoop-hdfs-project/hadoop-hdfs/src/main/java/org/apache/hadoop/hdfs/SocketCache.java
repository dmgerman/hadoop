begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|Preconditions
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
name|collect
operator|.
name|LinkedListMultimap
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * A cache of sockets.  */
end_comment

begin_class
DECL|class|SocketCache
class|class
name|SocketCache
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
name|SocketCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|multimap
specifier|private
specifier|final
name|LinkedListMultimap
argument_list|<
name|SocketAddress
argument_list|,
name|Socket
argument_list|>
name|multimap
decl_stmt|;
DECL|field|capacity
specifier|private
specifier|final
name|int
name|capacity
decl_stmt|;
comment|/**    * Create a SocketCache with the given capacity.    * @param capacity  Max cache size.    */
DECL|method|SocketCache (int capacity)
specifier|public
name|SocketCache
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|multimap
operator|=
name|LinkedListMultimap
operator|.
name|create
argument_list|()
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
if|if
condition|(
name|capacity
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SocketCache disabled in configuration."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get a cached socket to the given address.    * @param remote  Remote address the socket is connected to.    * @return  A socket with unknown state, possibly closed underneath. Or null.    */
DECL|method|get (SocketAddress remote)
specifier|public
specifier|synchronized
name|Socket
name|get
parameter_list|(
name|SocketAddress
name|remote
parameter_list|)
block|{
if|if
condition|(
name|capacity
operator|<=
literal|0
condition|)
block|{
comment|// disabled
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|Socket
argument_list|>
name|socklist
init|=
name|multimap
operator|.
name|get
argument_list|(
name|remote
argument_list|)
decl_stmt|;
if|if
condition|(
name|socklist
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Iterator
argument_list|<
name|Socket
argument_list|>
name|iter
init|=
name|socklist
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Socket
name|candidate
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|candidate
operator|.
name|isClosed
argument_list|()
condition|)
block|{
return|return
name|candidate
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Give an unused socket to the cache.    * @param sock socket not used by anyone.    */
DECL|method|put (Socket sock)
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
name|Socket
name|sock
parameter_list|)
block|{
if|if
condition|(
name|capacity
operator|<=
literal|0
condition|)
block|{
comment|// Cache disabled.
name|IOUtils
operator|.
name|closeSocket
argument_list|(
name|sock
argument_list|)
expr_stmt|;
return|return;
block|}
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|sock
argument_list|)
expr_stmt|;
name|SocketAddress
name|remoteAddr
init|=
name|sock
operator|.
name|getRemoteSocketAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|remoteAddr
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot cache (unconnected) socket with no remote address: "
operator|+
name|sock
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeSocket
argument_list|(
name|sock
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|capacity
operator|==
name|multimap
operator|.
name|size
argument_list|()
condition|)
block|{
name|evictOldest
argument_list|()
expr_stmt|;
block|}
name|multimap
operator|.
name|put
argument_list|(
name|remoteAddr
argument_list|,
name|sock
argument_list|)
expr_stmt|;
block|}
DECL|method|size ()
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
return|return
name|multimap
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Evict the oldest entry in the cache.    */
DECL|method|evictOldest ()
specifier|private
specifier|synchronized
name|void
name|evictOldest
parameter_list|()
block|{
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|SocketAddress
argument_list|,
name|Socket
argument_list|>
argument_list|>
name|iter
init|=
name|multimap
operator|.
name|entries
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot evict from empty cache!"
argument_list|)
throw|;
block|}
name|Entry
argument_list|<
name|SocketAddress
argument_list|,
name|Socket
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|Socket
name|sock
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|closeSocket
argument_list|(
name|sock
argument_list|)
expr_stmt|;
block|}
comment|/**    * Empty the cache, and close all sockets.    */
DECL|method|clear ()
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|Socket
name|sock
range|:
name|multimap
operator|.
name|values
argument_list|()
control|)
block|{
name|IOUtils
operator|.
name|closeSocket
argument_list|(
name|sock
argument_list|)
expr_stmt|;
block|}
name|multimap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|finalize ()
specifier|protected
name|void
name|finalize
parameter_list|()
block|{
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

