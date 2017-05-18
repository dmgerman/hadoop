begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ksm.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ksm
operator|.
name|protocolPB
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
name|classification
operator|.
name|InterfaceAudience
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
name|ipc
operator|.
name|ProtocolInfo
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|KeySpaceManagerProtocolProtos
operator|.
name|KeySpaceManagerService
import|;
end_import

begin_comment
comment|/**  * Protocol used to communicate with KSM.  */
end_comment

begin_interface
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"org.apache.hadoop.ozone.protocol.KeySpaceManagerProtocol"
argument_list|,
name|protocolVersion
operator|=
literal|1
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|KeySpaceManagerProtocolPB
specifier|public
interface|interface
name|KeySpaceManagerProtocolPB
extends|extends
name|KeySpaceManagerService
operator|.
name|BlockingInterface
block|{ }
end_interface

end_unit

