begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|mapreduce
operator|.
name|MRConfig
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
name|mapreduce
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
name|mapreduce
operator|.
name|protocol
operator|.
name|ClientProtocolProvider
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|LocalClientProtocolProvider
specifier|public
class|class
name|LocalClientProtocolProvider
extends|extends
name|ClientProtocolProvider
block|{
annotation|@
name|Override
DECL|method|create (Configuration conf)
specifier|public
name|ClientProtocol
name|create
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|framework
init|=
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|FRAMEWORK_NAME
argument_list|,
name|MRConfig
operator|.
name|LOCAL_FRAMEWORK_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|MRConfig
operator|.
name|LOCAL_FRAMEWORK_NAME
operator|.
name|equals
argument_list|(
name|framework
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
literal|"mapreduce.job.maps"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|conf
operator|.
name|setInt
argument_list|(
literal|"mapreduce.job.maps"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LocalJobRunner
argument_list|(
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create (InetSocketAddress addr, Configuration conf)
specifier|public
name|ClientProtocol
name|create
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// LocalJobRunner doesn't use a socket
block|}
annotation|@
name|Override
DECL|method|close (ClientProtocol clientProtocol)
specifier|public
name|void
name|close
parameter_list|(
name|ClientProtocol
name|clientProtocol
parameter_list|)
block|{
comment|// no clean up required
block|}
block|}
end_class

end_unit

