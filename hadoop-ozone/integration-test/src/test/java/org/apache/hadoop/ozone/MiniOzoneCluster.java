begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|hdds
operator|.
name|scm
operator|.
name|StorageContainerManager
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
name|client
operator|.
name|rest
operator|.
name|OzoneException
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
name|ksm
operator|.
name|KeySpaceManager
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
name|web
operator|.
name|client
operator|.
name|OzoneRestClient
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
name|hdds
operator|.
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_comment
comment|/**  * Interface used for MiniOzoneClusters.  */
end_comment

begin_interface
DECL|interface|MiniOzoneCluster
specifier|public
interface|interface
name|MiniOzoneCluster
extends|extends
name|AutoCloseable
extends|,
name|Closeable
block|{
DECL|method|close ()
name|void
name|close
parameter_list|()
function_decl|;
DECL|method|restartDataNode (int i)
name|boolean
name|restartDataNode
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|restartDataNode (int i, boolean keepPort)
name|boolean
name|restartDataNode
parameter_list|(
name|int
name|i
parameter_list|,
name|boolean
name|keepPort
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
function_decl|;
DECL|method|getStorageContainerManager ()
name|StorageContainerManager
name|getStorageContainerManager
parameter_list|()
function_decl|;
DECL|method|getKeySpaceManager ()
name|KeySpaceManager
name|getKeySpaceManager
parameter_list|()
function_decl|;
DECL|method|createOzoneRestClient ()
name|OzoneRestClient
name|createOzoneRestClient
parameter_list|()
throws|throws
name|OzoneException
function_decl|;
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|method|createStorageContainerLocationClient ()
name|createStorageContainerLocationClient
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|waitOzoneReady ()
name|void
name|waitOzoneReady
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
function_decl|;
DECL|method|waitDatanodeOzoneReady (int dnIndex)
name|void
name|waitDatanodeOzoneReady
parameter_list|(
name|int
name|dnIndex
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
function_decl|;
DECL|method|waitTobeOutOfChillMode ()
name|void
name|waitTobeOutOfChillMode
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
function_decl|;
DECL|method|waitForHeartbeatProcessed ()
name|void
name|waitForHeartbeatProcessed
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
function_decl|;
block|}
end_interface

end_unit

