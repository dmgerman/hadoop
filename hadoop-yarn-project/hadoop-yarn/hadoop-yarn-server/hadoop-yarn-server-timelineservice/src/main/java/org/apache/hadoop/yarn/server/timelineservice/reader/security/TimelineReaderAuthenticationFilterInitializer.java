begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader.security
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
name|timelineservice
operator|.
name|reader
operator|.
name|security
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
name|conf
operator|.
name|Configuration
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
name|http
operator|.
name|FilterContainer
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
name|AuthenticationWithProxyUserFilter
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
name|timeline
operator|.
name|security
operator|.
name|TimelineAuthenticationFilterInitializer
import|;
end_import

begin_comment
comment|/**  * Filter initializer to initialize {@link AuthenticationWithProxyUserFilter}  * for ATSv2 timeline reader server with timeline service specific  * configurations.  */
end_comment

begin_class
DECL|class|TimelineReaderAuthenticationFilterInitializer
specifier|public
class|class
name|TimelineReaderAuthenticationFilterInitializer
extends|extends
name|TimelineAuthenticationFilterInitializer
block|{
comment|/**    * Initializes {@link AuthenticationWithProxyUserFilter}    *<p>    * Propagates to {@link AuthenticationWithProxyUserFilter} configuration all    * YARN configuration properties prefixed with    * {@value TimelineAuthenticationFilterInitializer#PREFIX}.    *    * @param container    *          The filter container    * @param conf    *          Configuration for run-time parameters    */
annotation|@
name|Override
DECL|method|initFilter (FilterContainer container, Configuration conf)
specifier|public
name|void
name|initFilter
parameter_list|(
name|FilterContainer
name|container
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|setAuthFilterConfig
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|container
operator|.
name|addGlobalFilter
argument_list|(
literal|"Timeline Reader Authentication Filter"
argument_list|,
name|AuthenticationWithProxyUserFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|getFilterConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

