begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contracts.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|services
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
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|AzureBlobFileSystemException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|core
operator|.
name|SpanId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|core
operator|.
name|TraceScope
import|;
end_import

begin_comment
comment|/**  * Azure Blob File System tracing service.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|TracingService
specifier|public
interface|interface
name|TracingService
extends|extends
name|InjectableService
block|{
comment|/**    * Creates a {@link TraceScope} object with the provided description.    * @param description the trace description.    * @return created traceScope.    */
DECL|method|traceBegin (String description)
name|TraceScope
name|traceBegin
parameter_list|(
name|String
name|description
parameter_list|)
function_decl|;
comment|/**    * Creates a {@link TraceScope} object with the provided description.    * @param description the trace description.    * @param parentSpanId the span id of the parent trace scope.    * @return create traceScope    */
DECL|method|traceBegin (String description, SpanId parentSpanId)
name|TraceScope
name|traceBegin
parameter_list|(
name|String
name|description
parameter_list|,
name|SpanId
name|parentSpanId
parameter_list|)
function_decl|;
comment|/**    * Gets current thread latest generated traceScope id.    * @return current thread latest generated traceScope id.    */
DECL|method|getCurrentTraceScopeSpanId ()
name|SpanId
name|getCurrentTraceScopeSpanId
parameter_list|()
function_decl|;
comment|/**    * Appends the provided exception to the trace scope.    * @param traceScope the scope which exception needs to be attached to.    * @param azureBlobFileSystemException the exception to be attached to the scope.    */
DECL|method|traceException (TraceScope traceScope, AzureBlobFileSystemException azureBlobFileSystemException)
name|void
name|traceException
parameter_list|(
name|TraceScope
name|traceScope
parameter_list|,
name|AzureBlobFileSystemException
name|azureBlobFileSystemException
parameter_list|)
function_decl|;
comment|/**    * Ends the provided traceScope.    * @param traceScope the scope that needs to be ended.    */
DECL|method|traceEnd (TraceScope traceScope)
name|void
name|traceEnd
parameter_list|(
name|TraceScope
name|traceScope
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

