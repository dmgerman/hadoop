begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.transport.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** A server endpoint that acts as the communication layer for Ozone  * containers. */
end_comment

begin_interface
DECL|interface|XceiverServerSpi
specifier|public
interface|interface
name|XceiverServerSpi
block|{
comment|/** Starts the server. */
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Stops a running server. */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
function_decl|;
comment|/** Get server IPC port. */
DECL|method|getIPCPort ()
name|int
name|getIPCPort
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

