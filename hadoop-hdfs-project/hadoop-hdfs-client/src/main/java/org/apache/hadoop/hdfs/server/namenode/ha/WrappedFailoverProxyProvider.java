begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
operator|.
name|ha
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
operator|.
name|FailoverProxyProvider
import|;
end_import

begin_comment
comment|/**  * A NNFailoverProxyProvider implementation which wrapps old implementations  * directly implementing the {@link FailoverProxyProvider} interface.  *  * It is assumed that the old impelmentation is using logical URI.  */
end_comment

begin_class
DECL|class|WrappedFailoverProxyProvider
specifier|public
class|class
name|WrappedFailoverProxyProvider
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractNNFailoverProxyProvider
argument_list|<
name|T
argument_list|>
block|{
DECL|field|proxyProvider
specifier|private
specifier|final
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
name|proxyProvider
decl_stmt|;
comment|/**    * Wrap the given instance of an old FailoverProxyProvider.    */
DECL|method|WrappedFailoverProxyProvider (FailoverProxyProvider<T> provider)
specifier|public
name|WrappedFailoverProxyProvider
parameter_list|(
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
name|provider
parameter_list|)
block|{
name|proxyProvider
operator|=
name|provider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
argument_list|<
name|T
argument_list|>
name|getInterface
parameter_list|()
block|{
return|return
name|proxyProvider
operator|.
name|getInterface
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getProxy ()
specifier|public
specifier|synchronized
name|ProxyInfo
argument_list|<
name|T
argument_list|>
name|getProxy
parameter_list|()
block|{
return|return
name|proxyProvider
operator|.
name|getProxy
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|performFailover (T currentProxy)
specifier|public
name|void
name|performFailover
parameter_list|(
name|T
name|currentProxy
parameter_list|)
block|{
name|proxyProvider
operator|.
name|performFailover
argument_list|(
name|currentProxy
argument_list|)
expr_stmt|;
block|}
comment|/**    * Close the proxy,    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|proxyProvider
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Assume logical URI is used for old proxy provider implementations.    */
annotation|@
name|Override
DECL|method|useLogicalURI ()
specifier|public
name|boolean
name|useLogicalURI
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

