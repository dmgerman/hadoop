begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RpcConstants
specifier|public
class|class
name|RpcConstants
block|{
DECL|method|RpcConstants ()
specifier|private
name|RpcConstants
parameter_list|()
block|{
comment|// Hidden Constructor
block|}
DECL|field|AUTHORIZATION_FAILED_CALL_ID
specifier|public
specifier|static
specifier|final
name|int
name|AUTHORIZATION_FAILED_CALL_ID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|INVALID_CALL_ID
specifier|public
specifier|static
specifier|final
name|int
name|INVALID_CALL_ID
init|=
operator|-
literal|2
decl_stmt|;
DECL|field|CONNECTION_CONTEXT_CALL_ID
specifier|public
specifier|static
specifier|final
name|int
name|CONNECTION_CONTEXT_CALL_ID
init|=
operator|-
literal|3
decl_stmt|;
DECL|field|PING_CALL_ID
specifier|public
specifier|static
specifier|final
name|int
name|PING_CALL_ID
init|=
operator|-
literal|4
decl_stmt|;
DECL|field|DUMMY_CLIENT_ID
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|DUMMY_CLIENT_ID
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
DECL|field|INVALID_RETRY_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|INVALID_RETRY_COUNT
init|=
operator|-
literal|1
decl_stmt|;
comment|/**   * The Rpc-connection header is as follows    * +----------------------------------+   * |  "hrpc" 4 bytes                  |         * +----------------------------------+   * |  Version (1 byte)                |   * +----------------------------------+   * |  Service Class (1 byte)          |   * +----------------------------------+   * |  AuthProtocol (1 byte)           |         * +----------------------------------+   */
comment|/**    * The first four bytes of Hadoop RPC connections    */
DECL|field|HEADER
specifier|public
specifier|static
specifier|final
name|ByteBuffer
name|HEADER
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
literal|"hrpc"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|HEADER_LEN_AFTER_HRPC_PART
specifier|public
specifier|static
specifier|final
name|int
name|HEADER_LEN_AFTER_HRPC_PART
init|=
literal|3
decl_stmt|;
comment|// 3 bytes that follow
comment|// 1 : Introduce ping and server does not throw away RPCs
comment|// 3 : Introduce the protocol into the RPC connection header
comment|// 4 : Introduced SASL security layer
comment|// 5 : Introduced use of {@link ArrayPrimitiveWritable$Internal}
comment|//     in ObjectWritable to efficiently transmit arrays of primitives
comment|// 6 : Made RPC Request header explicit
comment|// 7 : Changed Ipc Connection Header to use Protocol buffers
comment|// 8 : SASL server always sends a final response
comment|// 9 : Changes to protocol for HADOOP-8990
DECL|field|CURRENT_VERSION
specifier|public
specifier|static
specifier|final
name|byte
name|CURRENT_VERSION
init|=
literal|9
decl_stmt|;
block|}
end_class

end_unit

