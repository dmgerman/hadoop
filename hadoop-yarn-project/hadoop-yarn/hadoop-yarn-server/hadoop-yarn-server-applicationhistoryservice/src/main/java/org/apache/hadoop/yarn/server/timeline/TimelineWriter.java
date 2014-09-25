begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline
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
name|timeline
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelineEntities
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
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelineDomain
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
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelinePutResponse
import|;
end_import

begin_comment
comment|/**  * This interface is for storing timeline information.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|TimelineWriter
specifier|public
interface|interface
name|TimelineWriter
block|{
comment|/**    * Stores entity information to the timeline store. Any errors occurring for    * individual put request objects will be reported in the response.    *     * @param data    *          a {@link TimelineEntities} object.    * @return a {@link TimelinePutResponse} object.    * @throws IOException    */
DECL|method|put (TimelineEntities data)
name|TimelinePutResponse
name|put
parameter_list|(
name|TimelineEntities
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Store domain information to the timeline store. If A domain of the    * same ID already exists in the timeline store, it will be COMPLETELY updated    * with the given domain.    *     * @param domain    *          a {@link TimelineDomain} object    * @throws IOException    */
DECL|method|put (TimelineDomain domain)
name|void
name|put
parameter_list|(
name|TimelineDomain
name|domain
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

