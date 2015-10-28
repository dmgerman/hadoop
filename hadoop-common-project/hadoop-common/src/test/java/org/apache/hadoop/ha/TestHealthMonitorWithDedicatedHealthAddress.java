begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
package|;
end_package

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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
import|;
end_import

begin_comment
comment|/**  * Repeats all tests of {@link TestHealthMonitor}, but using a separate  * dedicated health check RPC address.  */
end_comment

begin_class
DECL|class|TestHealthMonitorWithDedicatedHealthAddress
specifier|public
class|class
name|TestHealthMonitorWithDedicatedHealthAddress
extends|extends
name|TestHealthMonitor
block|{
annotation|@
name|Override
DECL|method|createDummyHAService ()
specifier|protected
name|DummyHAService
name|createDummyHAService
parameter_list|()
block|{
return|return
operator|new
name|DummyHAService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

