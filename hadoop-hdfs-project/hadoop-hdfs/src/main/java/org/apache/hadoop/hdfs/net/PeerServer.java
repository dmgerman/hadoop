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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|PeerServer
specifier|public
interface|interface
name|PeerServer
extends|extends
name|Closeable
block|{
comment|/**    * Set the receive buffer size of the PeerServer.    *     * @param size     The receive buffer size.    */
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
function_decl|;
comment|/**    * Get the receive buffer size of the PeerServer.    *    * @return     The receive buffer size.    */
DECL|method|getReceiveBufferSize ()
name|int
name|getReceiveBufferSize
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Listens for a connection to be made to this server and accepts    * it. The method blocks until a connection is made.    *    * @exception IOException  if an I/O error occurs when waiting for a    *               connection.    * @exception SecurityException  if a security manager exists and its      *<code>checkAccept</code> method doesn't allow the operation.    * @exception SocketTimeoutException if a timeout was previously set and    *             the timeout has been reached.    */
DECL|method|accept ()
specifier|public
name|Peer
name|accept
parameter_list|()
throws|throws
name|IOException
throws|,
name|SocketTimeoutException
function_decl|;
comment|/**    * @return                 A string representation of the address we're    *                         listening on.    */
DECL|method|getListeningString ()
specifier|public
name|String
name|getListeningString
parameter_list|()
function_decl|;
comment|/**    * Free the resources associated with this peer server.    * This normally includes sockets, etc.    *    * @throws IOException     If there is an error closing the PeerServer    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

