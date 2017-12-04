begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
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
name|security
operator|.
name|authorize
operator|.
name|PolicyProvider
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
name|authorize
operator|.
name|Service
import|;
end_import

begin_comment
comment|/**  * PolicyProvider for Client to Service AM protocol.  */
end_comment

begin_class
DECL|class|ClientAMPolicyProvider
specifier|public
class|class
name|ClientAMPolicyProvider
extends|extends
name|PolicyProvider
block|{
DECL|field|CLIENT_AM_SERVICE
specifier|private
specifier|static
specifier|final
name|Service
index|[]
name|CLIENT_AM_SERVICE
init|=
operator|new
name|Service
index|[]
block|{
operator|new
name|Service
argument_list|(
literal|"security.yarn-service.client-am-protocol.acl"
argument_list|,
name|ClientAMProtocol
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|getServices ()
specifier|public
name|Service
index|[]
name|getServices
parameter_list|()
block|{
return|return
name|CLIENT_AM_SERVICE
return|;
block|}
empty_stmt|;
block|}
end_class

end_unit

