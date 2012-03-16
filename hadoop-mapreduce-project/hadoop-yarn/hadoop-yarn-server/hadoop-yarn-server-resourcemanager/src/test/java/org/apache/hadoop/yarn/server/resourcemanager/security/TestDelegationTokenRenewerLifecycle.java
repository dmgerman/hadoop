begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|security
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * This test replicates the condition os MAPREDUCE-3431 -a failure  * during startup triggered an NPE during shutdown  *  */
end_comment

begin_class
DECL|class|TestDelegationTokenRenewerLifecycle
specifier|public
class|class
name|TestDelegationTokenRenewerLifecycle
block|{
annotation|@
name|Test
DECL|method|testStartupFailure ()
specifier|public
name|void
name|testStartupFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|DelegationTokenRenewer
name|delegationTokenRenewer
init|=
operator|new
name|DelegationTokenRenewer
argument_list|()
decl_stmt|;
name|delegationTokenRenewer
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|delegationTokenRenewer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

