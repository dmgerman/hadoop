begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin
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
name|nodemanager
operator|.
name|api
operator|.
name|deviceplugin
package|;
end_package

begin_comment
comment|/**  * YarnRuntime parameter enum for {@link DevicePlugin}.  * It's passed into {@code onDevicesAllocated}.  * Device plugin could populate {@link DeviceRuntimeSpec}  * based on which YARN container runtime will use.  * */
end_comment

begin_enum
DECL|enum|YarnRuntimeType
specifier|public
enum|enum
name|YarnRuntimeType
block|{
DECL|enumConstant|RUNTIME_DEFAULT
name|RUNTIME_DEFAULT
argument_list|(
literal|"default"
argument_list|)
block|,
DECL|enumConstant|RUNTIME_DOCKER
name|RUNTIME_DOCKER
argument_list|(
literal|"docker"
argument_list|)
block|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|YarnRuntimeType (String n)
name|YarnRuntimeType
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|n
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_enum

end_unit

