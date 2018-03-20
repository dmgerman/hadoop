begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.resolver
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
name|resolver
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
import|;
end_import

begin_comment
comment|/**  * Namenode state in the federation. The order of this enum is used to evaluate  * NN priority for RPC calls.  */
end_comment

begin_enum
DECL|enum|FederationNamenodeServiceState
specifier|public
enum|enum
name|FederationNamenodeServiceState
block|{
DECL|enumConstant|ACTIVE
name|ACTIVE
block|,
comment|// HAServiceState.ACTIVE or operational.
DECL|enumConstant|STANDBY
name|STANDBY
block|,
comment|// HAServiceState.STANDBY.
DECL|enumConstant|UNAVAILABLE
name|UNAVAILABLE
block|,
comment|// When the namenode cannot be reached.
DECL|enumConstant|EXPIRED
name|EXPIRED
block|;
comment|// When the last update is too old.
DECL|method|getState (HAServiceState state)
specifier|public
specifier|static
name|FederationNamenodeServiceState
name|getState
parameter_list|(
name|HAServiceState
name|state
parameter_list|)
block|{
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|ACTIVE
case|:
return|return
name|FederationNamenodeServiceState
operator|.
name|ACTIVE
return|;
case|case
name|STANDBY
case|:
return|return
name|FederationNamenodeServiceState
operator|.
name|STANDBY
return|;
case|case
name|INITIALIZING
case|:
return|return
name|FederationNamenodeServiceState
operator|.
name|UNAVAILABLE
return|;
case|case
name|STOPPING
case|:
return|return
name|FederationNamenodeServiceState
operator|.
name|UNAVAILABLE
return|;
default|default:
return|return
name|FederationNamenodeServiceState
operator|.
name|UNAVAILABLE
return|;
block|}
block|}
block|}
end_enum

end_unit

