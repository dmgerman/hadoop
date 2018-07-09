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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_PROVIDED_ALIASMAP_INMEMORY_RPC_ADDRESS
import|;
end_import

begin_comment
comment|/**  * A {@link ConfiguredFailoverProxyProvider} implementation used to connect  * to an InMemoryAliasMap.  */
end_comment

begin_class
DECL|class|InMemoryAliasMapFailoverProxyProvider
specifier|public
class|class
name|InMemoryAliasMapFailoverProxyProvider
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ConfiguredFailoverProxyProvider
argument_list|<
name|T
argument_list|>
block|{
DECL|method|InMemoryAliasMapFailoverProxyProvider ( Configuration conf, URI uri, Class<T> xface, HAProxyFactory<T> factory)
specifier|public
name|InMemoryAliasMapFailoverProxyProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|HAProxyFactory
argument_list|<
name|T
argument_list|>
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|uri
argument_list|,
name|xface
argument_list|,
name|factory
argument_list|,
name|DFS_PROVIDED_ALIASMAP_INMEMORY_RPC_ADDRESS
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

