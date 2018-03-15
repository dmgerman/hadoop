begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.proto
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|proto
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
name|cblock
operator|.
name|meta
operator|.
name|VolumeInfo
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * The protocol that CBlock client side uses to talk to server side. CBlock  * client is the point where a volume is mounted. All the actual volume IO  * operations will go through CBlock client after the volume is mounted.  *  * When users mount a volume on CBlock client, CBlock client side uses this  * protocol to send mount request to CBlock server.  */
end_comment

begin_interface
DECL|interface|CBlockClientProtocol
specifier|public
interface|interface
name|CBlockClientProtocol
block|{
DECL|method|mountVolume (String userName, String volumeName)
name|MountVolumeResponse
name|mountVolume
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|listVolumes ()
name|List
argument_list|<
name|VolumeInfo
argument_list|>
name|listVolumes
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

