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
name|ReportNewAggregatorsInfoRequest
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
name|ReportNewAggregatorsInfoResponse
import|;
end_import

begin_comment
comment|/**  *<p>The protocol between an<code>TimelineAggregatorsCollection</code> and a   *<code>NodeManager</code> to report a new application aggregator get launched.  *</p>  *   */
end_comment

begin_interface
annotation|@
name|Private
DECL|interface|AggregatorNodemanagerProtocol
specifier|public
interface|interface
name|AggregatorNodemanagerProtocol
block|{
comment|/**    *     *<p>    * The<code>TimelineAggregatorsCollection</code> provides a list of mapping    * between application and aggregator's address in     * {@link ReportNewAggregatorsInfoRequest} to a<code>NodeManager</code> to    *<em>register</em> aggregator's info, include: applicationId and REST URI to     * access aggregator. NodeManager will add them into registered aggregators     * and register them into<code>ResourceManager</code> afterwards.    *</p>    *     * @param request the request of registering a new aggregator or a list of aggregators    * @return     * @throws YarnException    * @throws IOException    */
DECL|method|reportNewAggregatorInfo ( ReportNewAggregatorsInfoRequest request)
name|ReportNewAggregatorsInfoResponse
name|reportNewAggregatorInfo
parameter_list|(
name|ReportNewAggregatorsInfoRequest
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

