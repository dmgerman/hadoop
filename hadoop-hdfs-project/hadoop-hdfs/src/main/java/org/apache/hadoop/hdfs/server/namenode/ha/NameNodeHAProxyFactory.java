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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|NameNodeProxies
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
name|AlignmentContext
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
name|InetSocketAddress
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
DECL|class|NameNodeHAProxyFactory
specifier|public
class|class
name|NameNodeHAProxyFactory
parameter_list|<
name|T
parameter_list|>
implements|implements
name|HAProxyFactory
argument_list|<
name|T
argument_list|>
block|{
DECL|field|alignmentContext
specifier|private
name|AlignmentContext
name|alignmentContext
decl_stmt|;
annotation|@
name|Override
DECL|method|createProxy (Configuration conf, InetSocketAddress nnAddr, Class<T> xface, UserGroupInformation ugi, boolean withRetries, AtomicBoolean fallbackToSimpleAuth)
specifier|public
name|T
name|createProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InetSocketAddress
name|nnAddr
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|boolean
name|withRetries
parameter_list|,
name|AtomicBoolean
name|fallbackToSimpleAuth
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|NameNodeProxies
operator|.
name|createNonHAProxy
argument_list|(
name|conf
argument_list|,
name|nnAddr
argument_list|,
name|xface
argument_list|,
name|ugi
argument_list|,
name|withRetries
argument_list|,
name|fallbackToSimpleAuth
argument_list|,
name|alignmentContext
argument_list|)
operator|.
name|getProxy
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createProxy (Configuration conf, InetSocketAddress nnAddr, Class<T> xface, UserGroupInformation ugi, boolean withRetries)
specifier|public
name|T
name|createProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InetSocketAddress
name|nnAddr
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|boolean
name|withRetries
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|NameNodeProxies
operator|.
name|createNonHAProxy
argument_list|(
name|conf
argument_list|,
name|nnAddr
argument_list|,
name|xface
argument_list|,
name|ugi
argument_list|,
name|withRetries
argument_list|)
operator|.
name|getProxy
argument_list|()
return|;
block|}
DECL|method|setAlignmentContext (AlignmentContext alignmentContext)
specifier|public
name|void
name|setAlignmentContext
parameter_list|(
name|AlignmentContext
name|alignmentContext
parameter_list|)
block|{
name|this
operator|.
name|alignmentContext
operator|=
name|alignmentContext
expr_stmt|;
block|}
block|}
end_class

end_unit

