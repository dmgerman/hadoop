begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api
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
name|api
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
operator|.
name|Private
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
name|exceptions
operator|.
name|YarnException
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetTimelineCollectorContextRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetTimelineCollectorContextResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|ReportNewCollectorInfoRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|ReportNewCollectorInfoResponse
import|;
end_import

begin_comment
comment|/**  *<p>The protocol between an<code>TimelineCollectorManager</code> and a  *<code>NodeManager</code> to report a new application collector get launched.  *</p>  *  */
end_comment

begin_interface
annotation|@
name|Private
DECL|interface|CollectorNodemanagerProtocol
specifier|public
interface|interface
name|CollectorNodemanagerProtocol
block|{
comment|/**    *    *<p>    * The<code>TimelineCollectorManager</code> provides a list of mapping    * between application and collector's address in    * {@link ReportNewCollectorInfoRequest} to a<code>NodeManager</code> to    *<em>register</em> collector's info, include: applicationId and REST URI to    * access collector. NodeManager will add them into registered collectors    * and register them into<code>ResourceManager</code> afterwards.    *</p>    *    * @param request the request of registering a new collector or a list of    *                collectors    * @return    * @throws YarnException    * @throws IOException    */
DECL|method|reportNewCollectorInfo ( ReportNewCollectorInfoRequest request)
name|ReportNewCollectorInfoResponse
name|reportNewCollectorInfo
parameter_list|(
name|ReportNewCollectorInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * The collector needs to get the context information including user, flow    * and flow run ID to associate with every incoming put-entity requests.    *</p>    * @param request the request of getting the aggregator context information of    *                the given application    * @return    * @throws YarnException    * @throws IOException    */
DECL|method|getTimelineCollectorContext ( GetTimelineCollectorContextRequest request)
name|GetTimelineCollectorContextResponse
name|getTimelineCollectorContext
parameter_list|(
name|GetTimelineCollectorContextRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

