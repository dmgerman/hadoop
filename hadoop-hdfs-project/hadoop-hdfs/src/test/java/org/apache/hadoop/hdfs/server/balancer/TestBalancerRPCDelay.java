begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.balancer
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
name|balancer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * The Balancer ensures that it disperses RPCs to the NameNode  * in order to avoid NN's RPC queue saturation.  */
end_comment

begin_class
DECL|class|TestBalancerRPCDelay
specifier|public
class|class
name|TestBalancerRPCDelay
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|100000
argument_list|)
DECL|method|testBalancerRPCDelay ()
specifier|public
name|void
name|testBalancerRPCDelay
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|TestBalancer
argument_list|()
operator|.
name|testBalancerRPCDelay
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

