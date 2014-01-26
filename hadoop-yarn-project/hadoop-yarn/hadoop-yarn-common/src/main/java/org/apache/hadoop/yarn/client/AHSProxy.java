begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
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
name|security
operator|.
name|PrivilegedAction
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
name|security
operator|.
name|UserGroupInformation
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
name|yarn
operator|.
name|ipc
operator|.
name|YarnRPC
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|AHSProxy
specifier|public
class|class
name|AHSProxy
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AHSProxy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|createAHSProxy (final Configuration conf, final Class<T> protocol, InetSocketAddress ahsAddress)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|createAHSProxy
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
parameter_list|,
name|InetSocketAddress
name|ahsAddress
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting to Application History server at "
operator|+
name|ahsAddress
argument_list|)
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|getProxy
argument_list|(
name|conf
argument_list|,
name|protocol
argument_list|,
name|ahsAddress
argument_list|)
return|;
block|}
DECL|method|getProxy (final Configuration conf, final Class<T> protocol, final InetSocketAddress rmAddress)
specifier|protected
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|getProxy
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
parameter_list|,
specifier|final
name|InetSocketAddress
name|rmAddress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|run
parameter_list|()
block|{
return|return
operator|(
name|T
operator|)
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
operator|.
name|getProxy
argument_list|(
name|protocol
argument_list|,
name|rmAddress
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

