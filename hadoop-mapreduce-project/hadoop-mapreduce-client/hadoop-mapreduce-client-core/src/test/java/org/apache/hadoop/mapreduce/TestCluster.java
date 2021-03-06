begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceConfigurationError
import|;
end_import

begin_comment
comment|/**  * Testing the Cluster initialization.  */
end_comment

begin_class
DECL|class|TestCluster
specifier|public
class|class
name|TestCluster
block|{
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testProtocolProviderCreation ()
specifier|public
name|void
name|testProtocolProviderCreation
parameter_list|()
throws|throws
name|Exception
block|{
name|Iterator
name|iterator
init|=
name|mock
argument_list|(
name|Iterator
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getClientProtocolProvider
argument_list|()
argument_list|)
operator|.
name|thenThrow
argument_list|(
operator|new
name|ServiceConfigurationError
argument_list|(
literal|"Test error"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getClientProtocolProvider
argument_list|()
argument_list|)
expr_stmt|;
name|Iterable
name|frameworkLoader
init|=
name|mock
argument_list|(
name|Iterable
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|frameworkLoader
operator|.
name|iterator
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
name|Cluster
operator|.
name|frameworkLoader
operator|=
name|frameworkLoader
expr_stmt|;
name|Cluster
name|testCluster
init|=
operator|new
name|Cluster
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
comment|// Check that we get the acceptable client, even after
comment|// failure in instantiation.
name|assertNotNull
argument_list|(
literal|"ClientProtocol is expected"
argument_list|,
name|testCluster
operator|.
name|getClient
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check if we do not try to load the providers after a failure.
name|verify
argument_list|(
name|iterator
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
DECL|method|getClientProtocolProvider ()
specifier|public
name|ClientProtocolProvider
name|getClientProtocolProvider
parameter_list|()
block|{
return|return
operator|new
name|ClientProtocolProvider
argument_list|()
block|{
annotation|@
name|Override
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
return|return
name|mock
argument_list|(
name|ClientProtocol
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
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
throws|throws
name|IOException
block|{
return|return
name|mock
argument_list|(
name|ClientProtocol
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|(
name|ClientProtocol
name|clientProtocol
parameter_list|)
throws|throws
name|IOException
block|{       }
block|}
return|;
block|}
block|}
end_class

end_unit

