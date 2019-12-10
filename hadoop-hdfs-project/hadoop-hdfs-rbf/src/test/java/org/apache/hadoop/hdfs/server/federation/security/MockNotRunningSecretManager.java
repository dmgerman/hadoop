begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.security
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|token
operator|.
name|delegation
operator|.
name|AbstractDelegationTokenSecretManager
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

begin_comment
comment|/**  * Mock functionality of AbstractDelegationTokenSecretManager.  * Test case that SecretManager is not running.  */
end_comment

begin_class
DECL|class|MockNotRunningSecretManager
specifier|public
class|class
name|MockNotRunningSecretManager
extends|extends
name|AbstractDelegationTokenSecretManager
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
block|{
DECL|method|MockNotRunningSecretManager (Configuration conf)
specifier|public
name|MockNotRunningSecretManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|100000
argument_list|,
literal|100000
argument_list|,
literal|100000
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
comment|// It doesn't execute startThreads() to keep the running status false.
block|}
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|DelegationTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|DelegationTokenIdentifier
argument_list|()
return|;
block|}
block|}
end_class

end_unit

