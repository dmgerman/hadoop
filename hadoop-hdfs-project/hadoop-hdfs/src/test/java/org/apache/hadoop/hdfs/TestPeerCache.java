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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertSame
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
name|assertTrue
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ReadableByteChannel
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|net
operator|.
name|Peer
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
name|net
operator|.
name|unix
operator|.
name|DomainSocket
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
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
name|HashMultiset
import|;
end_import

begin_class
DECL|class|TestPeerCache
specifier|public
class|class
name|TestPeerCache
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
name|TestPeerCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|FakePeer
specifier|private
specifier|static
class|class
name|FakePeer
implements|implements
name|Peer
block|{
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|hasDomain
specifier|private
specifier|final
name|boolean
name|hasDomain
decl_stmt|;
DECL|field|dnId
specifier|private
name|DatanodeID
name|dnId
decl_stmt|;
DECL|method|FakePeer (DatanodeID dnId, boolean hasDomain)
specifier|public
name|FakePeer
parameter_list|(
name|DatanodeID
name|dnId
parameter_list|,
name|boolean
name|hasDomain
parameter_list|)
block|{
name|this
operator|.
name|dnId
operator|=
name|dnId
expr_stmt|;
name|this
operator|.
name|hasDomain
operator|=
name|hasDomain
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInputStreamChannel ()
specifier|public
name|ReadableByteChannel
name|getInputStreamChannel
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setReadTimeout (int timeoutMs)
specifier|public
name|void
name|setReadTimeout
parameter_list|(
name|int
name|timeoutMs
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getReceiveBufferSize ()
specifier|public
name|int
name|getReceiveBufferSize
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getTcpNoDelay ()
specifier|public
name|boolean
name|getTcpNoDelay
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setWriteTimeout (int timeoutMs)
specifier|public
name|void
name|setWriteTimeout
parameter_list|(
name|int
name|timeoutMs
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|isClosed ()
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRemoteAddressString ()
specifier|public
name|String
name|getRemoteAddressString
parameter_list|()
block|{
return|return
name|dnId
operator|.
name|getInfoAddr
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalAddressString ()
specifier|public
name|String
name|getLocalAddressString
parameter_list|()
block|{
return|return
literal|"127.0.0.1:123"
return|;
block|}
annotation|@
name|Override
DECL|method|getInputStream ()
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getOutputStream ()
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|isLocal ()
specifier|public
name|boolean
name|isLocal
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FakePeer(dnId="
operator|+
name|dnId
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getDomainSocket ()
specifier|public
name|DomainSocket
name|getDomainSocket
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasDomain
condition|)
return|return
literal|null
return|;
comment|// Return a mock which throws an exception whenever any function is
comment|// called.
return|return
name|Mockito
operator|.
name|mock
argument_list|(
name|DomainSocket
operator|.
name|class
argument_list|,
operator|new
name|Answer
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"injected fault."
argument_list|)
throw|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|FakePeer
operator|)
condition|)
return|return
literal|false
return|;
name|FakePeer
name|other
init|=
operator|(
name|FakePeer
operator|)
name|o
decl_stmt|;
return|return
name|hasDomain
operator|==
name|other
operator|.
name|hasDomain
operator|&&
name|dnId
operator|.
name|equals
argument_list|(
name|other
operator|.
name|dnId
argument_list|)
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
name|dnId
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|hasDomain
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAddAndRetrieve ()
specifier|public
name|void
name|testAddAndRetrieve
parameter_list|()
throws|throws
name|Exception
block|{
name|PeerCache
name|cache
init|=
operator|new
name|PeerCache
argument_list|(
literal|3
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|DatanodeID
name|dnId
init|=
operator|new
name|DatanodeID
argument_list|(
literal|"192.168.0.1"
argument_list|,
literal|"fakehostname"
argument_list|,
literal|"fake_storage_id"
argument_list|,
literal|100
argument_list|,
literal|101
argument_list|,
literal|102
argument_list|)
decl_stmt|;
name|FakePeer
name|peer
init|=
operator|new
name|FakePeer
argument_list|(
name|dnId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|dnId
argument_list|,
name|peer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|peer
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|peer
argument_list|,
name|cache
operator|.
name|get
argument_list|(
name|dnId
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExpiry ()
specifier|public
name|void
name|testExpiry
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|CAPACITY
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|EXPIRY_PERIOD
init|=
literal|10
decl_stmt|;
name|PeerCache
name|cache
init|=
operator|new
name|PeerCache
argument_list|(
name|CAPACITY
argument_list|,
name|EXPIRY_PERIOD
argument_list|)
decl_stmt|;
name|DatanodeID
name|dnIds
index|[]
init|=
operator|new
name|DatanodeID
index|[
name|CAPACITY
index|]
decl_stmt|;
name|FakePeer
name|peers
index|[]
init|=
operator|new
name|FakePeer
index|[
name|CAPACITY
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
name|CAPACITY
condition|;
operator|++
name|i
control|)
block|{
name|dnIds
index|[
name|i
index|]
operator|=
operator|new
name|DatanodeID
argument_list|(
literal|"192.168.0.1"
argument_list|,
literal|"fakehostname_"
operator|+
name|i
argument_list|,
literal|"fake_storage_id"
argument_list|,
literal|100
argument_list|,
literal|101
argument_list|,
literal|102
argument_list|)
expr_stmt|;
name|peers
index|[
name|i
index|]
operator|=
operator|new
name|FakePeer
argument_list|(
name|dnIds
index|[
name|i
index|]
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|CAPACITY
condition|;
operator|++
name|i
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|dnIds
index|[
name|i
index|]
argument_list|,
name|peers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Wait for the peers to expire
name|Thread
operator|.
name|sleep
argument_list|(
name|EXPIRY_PERIOD
operator|*
literal|50
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure that the peers were closed when they were expired
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|CAPACITY
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|peers
index|[
name|i
index|]
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// sleep for another second and see if
comment|// the daemon thread runs fine on empty cache
name|Thread
operator|.
name|sleep
argument_list|(
name|EXPIRY_PERIOD
operator|*
literal|50
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEviction ()
specifier|public
name|void
name|testEviction
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|CAPACITY
init|=
literal|3
decl_stmt|;
name|PeerCache
name|cache
init|=
operator|new
name|PeerCache
argument_list|(
name|CAPACITY
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|DatanodeID
name|dnIds
index|[]
init|=
operator|new
name|DatanodeID
index|[
name|CAPACITY
operator|+
literal|1
index|]
decl_stmt|;
name|FakePeer
name|peers
index|[]
init|=
operator|new
name|FakePeer
index|[
name|CAPACITY
operator|+
literal|1
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
name|dnIds
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|dnIds
index|[
name|i
index|]
operator|=
operator|new
name|DatanodeID
argument_list|(
literal|"192.168.0.1"
argument_list|,
literal|"fakehostname_"
operator|+
name|i
argument_list|,
literal|"fake_storage_id_"
operator|+
name|i
argument_list|,
literal|100
argument_list|,
literal|101
argument_list|,
literal|102
argument_list|)
expr_stmt|;
name|peers
index|[
name|i
index|]
operator|=
operator|new
name|FakePeer
argument_list|(
name|dnIds
index|[
name|i
index|]
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|CAPACITY
condition|;
operator|++
name|i
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|dnIds
index|[
name|i
index|]
argument_list|,
name|peers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Check that the peers are cached
name|assertEquals
argument_list|(
name|CAPACITY
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add another entry and check that the first entry was evicted
name|cache
operator|.
name|put
argument_list|(
name|dnIds
index|[
name|CAPACITY
index|]
argument_list|,
name|peers
index|[
name|CAPACITY
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CAPACITY
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|null
argument_list|,
name|cache
operator|.
name|get
argument_list|(
name|dnIds
index|[
literal|0
index|]
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure that the other entries are still there
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|CAPACITY
condition|;
operator|++
name|i
control|)
block|{
name|Peer
name|peer
init|=
name|cache
operator|.
name|get
argument_list|(
name|dnIds
index|[
name|i
index|]
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|peers
index|[
name|i
index|]
argument_list|,
name|peer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|peer
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|peer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiplePeersWithSameKey ()
specifier|public
name|void
name|testMultiplePeersWithSameKey
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|CAPACITY
init|=
literal|3
decl_stmt|;
name|PeerCache
name|cache
init|=
operator|new
name|PeerCache
argument_list|(
name|CAPACITY
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|DatanodeID
name|dnId
init|=
operator|new
name|DatanodeID
argument_list|(
literal|"192.168.0.1"
argument_list|,
literal|"fakehostname"
argument_list|,
literal|"fake_storage_id"
argument_list|,
literal|100
argument_list|,
literal|101
argument_list|,
literal|102
argument_list|)
decl_stmt|;
name|HashMultiset
argument_list|<
name|FakePeer
argument_list|>
name|peers
init|=
name|HashMultiset
operator|.
name|create
argument_list|(
name|CAPACITY
argument_list|)
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
name|CAPACITY
condition|;
operator|++
name|i
control|)
block|{
name|FakePeer
name|peer
init|=
operator|new
name|FakePeer
argument_list|(
name|dnId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|peers
operator|.
name|add
argument_list|(
name|peer
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|dnId
argument_list|,
name|peer
argument_list|)
expr_stmt|;
block|}
comment|// Check that all of the peers ended up in the cache
name|assertEquals
argument_list|(
name|CAPACITY
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|peers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Peer
name|peer
init|=
name|cache
operator|.
name|get
argument_list|(
name|dnId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|peer
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|peer
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|peers
operator|.
name|remove
argument_list|(
name|peer
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDomainSocketPeers ()
specifier|public
name|void
name|testDomainSocketPeers
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|CAPACITY
init|=
literal|3
decl_stmt|;
name|PeerCache
name|cache
init|=
operator|new
name|PeerCache
argument_list|(
name|CAPACITY
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|DatanodeID
name|dnId
init|=
operator|new
name|DatanodeID
argument_list|(
literal|"192.168.0.1"
argument_list|,
literal|"fakehostname"
argument_list|,
literal|"fake_storage_id"
argument_list|,
literal|100
argument_list|,
literal|101
argument_list|,
literal|102
argument_list|)
decl_stmt|;
name|HashMultiset
argument_list|<
name|FakePeer
argument_list|>
name|peers
init|=
name|HashMultiset
operator|.
name|create
argument_list|(
name|CAPACITY
argument_list|)
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
name|CAPACITY
condition|;
operator|++
name|i
control|)
block|{
name|FakePeer
name|peer
init|=
operator|new
name|FakePeer
argument_list|(
name|dnId
argument_list|,
name|i
operator|==
name|CAPACITY
operator|-
literal|1
argument_list|)
decl_stmt|;
name|peers
operator|.
name|add
argument_list|(
name|peer
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|dnId
argument_list|,
name|peer
argument_list|)
expr_stmt|;
block|}
comment|// Check that all of the peers ended up in the cache
name|assertEquals
argument_list|(
name|CAPACITY
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test that get(requireDomainPeer=true) finds the peer with the
comment|// domain socket.
name|Peer
name|peer
init|=
name|cache
operator|.
name|get
argument_list|(
name|dnId
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|peer
operator|.
name|getDomainSocket
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|peers
operator|.
name|remove
argument_list|(
name|peer
argument_list|)
expr_stmt|;
comment|// Test that get(requireDomainPeer=true) returns null when there are
comment|// no more peers with domain sockets.
name|peer
operator|=
name|cache
operator|.
name|get
argument_list|(
name|dnId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|peer
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|// Check that all of the other peers ended up in the cache.
while|while
condition|(
operator|!
name|peers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|peer
operator|=
name|cache
operator|.
name|get
argument_list|(
name|dnId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|peer
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|peer
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|peers
operator|.
name|remove
argument_list|(
name|peer
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

