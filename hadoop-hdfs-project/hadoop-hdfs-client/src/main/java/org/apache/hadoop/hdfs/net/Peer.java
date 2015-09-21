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
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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

begin_comment
comment|/**  * Represents a connection to a peer.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|Peer
specifier|public
interface|interface
name|Peer
extends|extends
name|Closeable
block|{
comment|/**    * @return                The input stream channel associated with this    *                        peer, or null if it has none.    */
DECL|method|getInputStreamChannel ()
specifier|public
name|ReadableByteChannel
name|getInputStreamChannel
parameter_list|()
function_decl|;
comment|/**    * Set the read timeout on this peer.    *    * @param timeoutMs       The timeout in milliseconds.    */
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
function_decl|;
comment|/**    * @return                The receive buffer size.    */
DECL|method|getReceiveBufferSize ()
specifier|public
name|int
name|getReceiveBufferSize
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * @return                True if TCP_NODELAY is turned on.    */
DECL|method|getTcpNoDelay ()
specifier|public
name|boolean
name|getTcpNoDelay
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Set the write timeout on this peer.    *    * Note: this is not honored for BasicInetPeer.    * See {@link BasicInetPeer#setWriteTimeout} for details.    *    * @param timeoutMs       The timeout in milliseconds.    */
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
function_decl|;
comment|/**    * @return                true only if the peer is closed.    */
DECL|method|isClosed ()
specifier|public
name|boolean
name|isClosed
parameter_list|()
function_decl|;
comment|/**    * Close the peer.    *    * It's safe to re-close a Peer that is already closed.    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * @return               A string representing the remote end of our    *                       connection to the peer.    */
DECL|method|getRemoteAddressString ()
specifier|public
name|String
name|getRemoteAddressString
parameter_list|()
function_decl|;
comment|/**    * @return               A string representing the local end of our    *                       connection to the peer.    */
DECL|method|getLocalAddressString ()
specifier|public
name|String
name|getLocalAddressString
parameter_list|()
function_decl|;
comment|/**    * @return               An InputStream associated with the Peer.    *                       This InputStream will be valid until you close    *                       this peer with Peer#close.    */
DECL|method|getInputStream ()
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * @return               An OutputStream associated with the Peer.    *                       This OutputStream will be valid until you close    *                       this peer with Peer#close.    */
DECL|method|getOutputStream ()
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * @return               True if the peer resides on the same    *                       computer as we.    */
DECL|method|isLocal ()
specifier|public
name|boolean
name|isLocal
parameter_list|()
function_decl|;
comment|/**    * @return               The DomainSocket associated with the current    *                       peer, or null if there is none.    */
DECL|method|getDomainSocket ()
specifier|public
name|DomainSocket
name|getDomainSocket
parameter_list|()
function_decl|;
comment|/**    * Return true if the channel is secure.    *    * @return               True if our channel to this peer is not    *                       susceptible to man-in-the-middle attacks.    */
DECL|method|hasSecureChannel ()
specifier|public
name|boolean
name|hasSecureChannel
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

