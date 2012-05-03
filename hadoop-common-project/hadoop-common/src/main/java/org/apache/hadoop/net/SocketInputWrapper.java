begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
name|FilterInputStream
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
name|SocketException
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
name|classification
operator|.
name|InterfaceStability
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

begin_comment
comment|/**  * A wrapper stream around a socket which allows setting of its timeout. If the  * socket has a channel, this uses non-blocking IO via the package-private  * {@link SocketInputStream} implementation. Otherwise, timeouts are managed by  * setting the underlying socket timeout itself.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"HDFS"
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|SocketInputWrapper
specifier|public
class|class
name|SocketInputWrapper
extends|extends
name|FilterInputStream
block|{
DECL|field|socket
specifier|private
specifier|final
name|Socket
name|socket
decl_stmt|;
DECL|field|hasChannel
specifier|private
specifier|final
name|boolean
name|hasChannel
decl_stmt|;
DECL|method|SocketInputWrapper (Socket s, InputStream is)
name|SocketInputWrapper
parameter_list|(
name|Socket
name|s
parameter_list|,
name|InputStream
name|is
parameter_list|)
block|{
name|super
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|this
operator|.
name|socket
operator|=
name|s
expr_stmt|;
name|this
operator|.
name|hasChannel
operator|=
name|s
operator|.
name|getChannel
argument_list|()
operator|!=
literal|null
expr_stmt|;
if|if
condition|(
name|hasChannel
condition|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|is
operator|instanceof
name|SocketInputStream
argument_list|,
literal|"Expected a SocketInputStream when there is a channel. "
operator|+
literal|"Got: %s"
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the timeout for reads from this stream.    *     * Note: the behavior here can differ subtly depending on whether the    * underlying socket has an associated Channel. In particular, if there is no    * channel, then this call will affect the socket timeout for<em>all</em>    * readers of this socket. If there is a channel, then this call will affect    * the timeout only for<em>this</em> stream. As such, it is recommended to    * only create one {@link SocketInputWrapper} instance per socket.    *     * @param timeoutMs    *          the new timeout, 0 for no timeout    * @throws SocketException    *           if the timeout cannot be set    */
DECL|method|setTimeout (long timeoutMs)
specifier|public
name|void
name|setTimeout
parameter_list|(
name|long
name|timeoutMs
parameter_list|)
throws|throws
name|SocketException
block|{
if|if
condition|(
name|hasChannel
condition|)
block|{
operator|(
operator|(
name|SocketInputStream
operator|)
name|in
operator|)
operator|.
name|setTimeout
argument_list|(
name|timeoutMs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|socket
operator|.
name|setSoTimeout
argument_list|(
operator|(
name|int
operator|)
name|timeoutMs
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return an underlying ReadableByteChannel implementation.    * @throws IllegalStateException if this socket does not have a channel    */
DECL|method|getReadableByteChannel ()
specifier|public
name|ReadableByteChannel
name|getReadableByteChannel
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|hasChannel
argument_list|,
literal|"Socket %s does not have a channel"
argument_list|,
name|this
operator|.
name|socket
argument_list|)
expr_stmt|;
return|return
operator|(
name|SocketInputStream
operator|)
name|in
return|;
block|}
block|}
end_class

end_unit

