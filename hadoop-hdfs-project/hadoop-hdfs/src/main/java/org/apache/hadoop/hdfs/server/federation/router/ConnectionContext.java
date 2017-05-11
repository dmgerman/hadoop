begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
package|;
end_package

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
name|NameNodeProxiesClient
operator|.
name|ProxyAndInfo
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
name|ClientProtocol
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
name|ipc
operator|.
name|RPC
import|;
end_import

begin_comment
comment|/**  * Context to track a connection in a {@link ConnectionPool}. When a client uses  * a connection, it increments a counter to mark it as active. Once the client  * is done with the connection, it decreases the counter. It also takes care of  * closing the connection once is not active.  */
end_comment

begin_class
DECL|class|ConnectionContext
specifier|public
class|class
name|ConnectionContext
block|{
comment|/** Client for the connection. */
DECL|field|client
specifier|private
specifier|final
name|ProxyAndInfo
argument_list|<
name|ClientProtocol
argument_list|>
name|client
decl_stmt|;
comment|/** How many threads are using this connection. */
DECL|field|numThreads
specifier|private
name|int
name|numThreads
init|=
literal|0
decl_stmt|;
comment|/** If the connection is closed. */
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|ConnectionContext (ProxyAndInfo<ClientProtocol> connection)
specifier|public
name|ConnectionContext
parameter_list|(
name|ProxyAndInfo
argument_list|<
name|ClientProtocol
argument_list|>
name|connection
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|connection
expr_stmt|;
block|}
comment|/**    * Check if the connection is active.    *    * @return True if the connection is active.    */
DECL|method|isActive ()
specifier|public
specifier|synchronized
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|this
operator|.
name|numThreads
operator|>
literal|0
return|;
block|}
comment|/**    * Check if the connection is closed.    *    * @return If the connection is closed.    */
DECL|method|isClosed ()
specifier|public
specifier|synchronized
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|this
operator|.
name|closed
return|;
block|}
comment|/**    * Check if the connection can be used. It checks if the connection is used by    * another thread or already closed.    *    * @return True if the connection can be used.    */
DECL|method|isUsable ()
specifier|public
specifier|synchronized
name|boolean
name|isUsable
parameter_list|()
block|{
return|return
operator|!
name|isActive
argument_list|()
operator|&&
operator|!
name|isClosed
argument_list|()
return|;
block|}
comment|/**    * Get the connection client.    *    * @return Connection client.    */
DECL|method|getClient ()
specifier|public
specifier|synchronized
name|ProxyAndInfo
argument_list|<
name|ClientProtocol
argument_list|>
name|getClient
parameter_list|()
block|{
name|this
operator|.
name|numThreads
operator|++
expr_stmt|;
return|return
name|this
operator|.
name|client
return|;
block|}
comment|/**    * Release this connection. If the connection was closed, close the proxy.    * Otherwise, mark the connection as not used by us anymore.    */
DECL|method|release ()
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|()
block|{
if|if
condition|(
operator|--
name|this
operator|.
name|numThreads
operator|==
literal|0
operator|&&
name|this
operator|.
name|closed
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * We will not use this connection anymore. If it's not being used, we close    * it. Otherwise, we let release() do it once we are done with it.    */
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|this
operator|.
name|closed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|numThreads
operator|==
literal|0
condition|)
block|{
name|ClientProtocol
name|proxy
init|=
name|this
operator|.
name|client
operator|.
name|getProxy
argument_list|()
decl_stmt|;
comment|// Nobody should be using this anymore so it should close right away
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

