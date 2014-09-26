begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tracing
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tracing
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|classification
operator|.
name|InterfaceStability
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|io
operator|.
name|retry
operator|.
name|AtMostOnce
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
name|io
operator|.
name|retry
operator|.
name|Idempotent
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
name|security
operator|.
name|KerberosInfo
import|;
end_import

begin_comment
comment|/**  * Protocol interface that provides tracing.  */
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_USER_NAME_KEY
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|TraceAdminProtocol
specifier|public
interface|interface
name|TraceAdminProtocol
block|{
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**    * List the currently active trace span receivers.    *     * @throws IOException        On error.    */
annotation|@
name|Idempotent
DECL|method|listSpanReceivers ()
specifier|public
name|SpanReceiverInfo
index|[]
name|listSpanReceivers
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Add a new trace span receiver.    *     * @param desc                The span receiver description.    * @return                    The ID of the new trace span receiver.    *    * @throws IOException        On error.    */
annotation|@
name|AtMostOnce
DECL|method|addSpanReceiver (SpanReceiverInfo desc)
specifier|public
name|long
name|addSpanReceiver
parameter_list|(
name|SpanReceiverInfo
name|desc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove a trace span receiver.    *    * @param spanReceiverId      The id of the span receiver to remove.    * @throws IOException        On error.    */
annotation|@
name|AtMostOnce
DECL|method|removeSpanReceiver (long spanReceiverId)
specifier|public
name|void
name|removeSpanReceiver
parameter_list|(
name|long
name|spanReceiverId
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

