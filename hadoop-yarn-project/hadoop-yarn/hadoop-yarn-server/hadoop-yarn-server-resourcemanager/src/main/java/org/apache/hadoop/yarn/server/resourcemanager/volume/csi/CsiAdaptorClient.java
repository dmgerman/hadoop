begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.volume.csi
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
name|volume
operator|.
name|csi
operator|.
name|exception
operator|.
name|VolumeException
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
name|yarn
operator|.
name|server
operator|.
name|volume
operator|.
name|csi
operator|.
name|CsiAdaptorClientProtocol
import|;
end_import

begin_comment
comment|/**  * Client talks to CSI adaptor.  */
end_comment

begin_class
DECL|class|CsiAdaptorClient
specifier|public
class|class
name|CsiAdaptorClient
implements|implements
name|CsiAdaptorClientProtocol
block|{
annotation|@
name|Override
DECL|method|validateVolume ()
specifier|public
name|void
name|validateVolume
parameter_list|()
throws|throws
name|VolumeException
block|{
comment|// TODO
block|}
DECL|method|controllerPublishVolume ()
annotation|@
name|Override
specifier|public
name|void
name|controllerPublishVolume
parameter_list|()
throws|throws
name|VolumeException
block|{
comment|// TODO
block|}
block|}
end_class

end_unit

