begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|net
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|SocketTimeoutException
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
name|PeerServer
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

begin_class
DECL|class|DomainPeerServer
class|class
name|DomainPeerServer
implements|implements
name|PeerServer
block|{
DECL|field|LOG
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DomainPeerServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sock
specifier|private
specifier|final
name|DomainSocket
name|sock
decl_stmt|;
DECL|method|DomainPeerServer (DomainSocket sock)
name|DomainPeerServer
parameter_list|(
name|DomainSocket
name|sock
parameter_list|)
block|{
name|this
operator|.
name|sock
operator|=
name|sock
expr_stmt|;
block|}
DECL|method|DomainPeerServer (String path, int port)
specifier|public
name|DomainPeerServer
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|DomainSocket
operator|.
name|bindAndListen
argument_list|(
name|DomainSocket
operator|.
name|getEffectivePath
argument_list|(
name|path
argument_list|,
name|port
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getBindPath ()
specifier|public
name|String
name|getBindPath
parameter_list|()
block|{
return|return
name|sock
operator|.
name|getPath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setReceiveBufferSize (int size)
specifier|public
name|void
name|setReceiveBufferSize
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|sock
operator|.
name|setAttribute
argument_list|(
name|DomainSocket
operator|.
name|RCV_BUF_SIZE
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept ()
specifier|public
name|Peer
name|accept
parameter_list|()
throws|throws
name|IOException
throws|,
name|SocketTimeoutException
block|{
name|DomainSocket
name|connSock
init|=
name|sock
operator|.
name|accept
argument_list|()
decl_stmt|;
name|Peer
name|peer
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|peer
operator|=
operator|new
name|DomainPeer
argument_list|(
name|connSock
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|peer
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
if|if
condition|(
name|peer
operator|!=
literal|null
condition|)
name|peer
operator|.
name|close
argument_list|()
expr_stmt|;
name|connSock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getListeningString ()
specifier|public
name|String
name|getListeningString
parameter_list|()
block|{
return|return
literal|"unix:"
operator|+
name|sock
operator|.
name|getPath
argument_list|()
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
try|try
block|{
name|sock
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
name|error
argument_list|(
literal|"error closing DomainPeerServer: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
literal|"DomainPeerServer("
operator|+
name|getListeningString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

