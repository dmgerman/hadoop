begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.volume.csi.lifecycle
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
name|volume
operator|.
name|csi
operator|.
name|lifecycle
package|;
end_package

begin_comment
comment|/**  * Volume states  * Volume states are defined in the CSI spec, see more in volume lifecycle.  */
end_comment

begin_enum
DECL|enum|VolumeState
specifier|public
enum|enum
name|VolumeState
block|{
comment|// initial state
DECL|enumConstant|NEW
name|NEW
block|,
comment|// volume capacity validated
DECL|enumConstant|VALIDATED
name|VALIDATED
block|,
comment|// volume created by the controller
DECL|enumConstant|CREATED
name|CREATED
block|,
comment|// controller published the volume
DECL|enumConstant|NODE_READY
name|NODE_READY
block|,
comment|// unavailable
DECL|enumConstant|UNAVAILABLE
name|UNAVAILABLE
block|}
end_enum

end_unit

