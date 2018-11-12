begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.volume.csi.event
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
name|event
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
operator|.
name|Volume
import|;
end_import

begin_comment
comment|/**  * Trigger controller publish.  */
end_comment

begin_class
DECL|class|ControllerPublishVolumeEvent
specifier|public
class|class
name|ControllerPublishVolumeEvent
extends|extends
name|VolumeEvent
block|{
DECL|method|ControllerPublishVolumeEvent (Volume volume)
specifier|public
name|ControllerPublishVolumeEvent
parameter_list|(
name|Volume
name|volume
parameter_list|)
block|{
name|super
argument_list|(
name|volume
argument_list|,
name|VolumeEventType
operator|.
name|CONTROLLER_PUBLISH_VOLUME_EVENT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

