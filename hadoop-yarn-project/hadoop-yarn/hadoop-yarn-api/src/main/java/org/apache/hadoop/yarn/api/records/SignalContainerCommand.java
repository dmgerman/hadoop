begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
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
operator|.
name|Public
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
operator|.
name|Evolving
import|;
end_import

begin_comment
comment|/**  * Enumeration of various signal container commands.  */
end_comment

begin_enum
annotation|@
name|Public
annotation|@
name|Evolving
DECL|enum|SignalContainerCommand
specifier|public
enum|enum
name|SignalContainerCommand
block|{
comment|/**    * Used to capture thread dump.    * On Linux, it is equivalent to SIGQUIT.    */
DECL|enumConstant|OUTPUT_THREAD_DUMP
name|OUTPUT_THREAD_DUMP
block|,
comment|/** Gracefully shutdown a container.    * On Linux, it is equivalent to SIGTERM.    */
DECL|enumConstant|GRACEFUL_SHUTDOWN
name|GRACEFUL_SHUTDOWN
block|,
comment|/** Forcefully shutdown a container.    * On Linux, it is equivalent to SIGKILL.    */
DECL|enumConstant|FORCEFUL_SHUTDOWN
name|FORCEFUL_SHUTDOWN
block|, }
end_enum

end_unit

