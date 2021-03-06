begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.webapp
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
name|applicationhistoryservice
operator|.
name|webapp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|YarnWebParams
operator|.
name|CONTAINER_ID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|YarnWebParams
operator|.
name|ENTITY_STRING
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
name|webapp
operator|.
name|SubView
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
name|webapp
operator|.
name|log
operator|.
name|AggregatedLogsBlock
import|;
end_import

begin_class
DECL|class|AHSLogsPage
specifier|public
class|class
name|AHSLogsPage
extends|extends
name|AHSView
block|{
comment|/*    * (non-Javadoc)    *     * @see    * org.apache.hadoop.yarn.server.applicationhistoryservice.webapp.AHSView#    * preHead(org.apache.hadoop .yarn.webapp.hamlet.Hamlet.HTML)    */
annotation|@
name|Override
DECL|method|preHead (Page.HTML<__> html)
specifier|protected
name|void
name|preHead
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|__
argument_list|>
name|html
parameter_list|)
block|{
name|String
name|logEntity
init|=
name|$
argument_list|(
name|ENTITY_STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|logEntity
operator|==
literal|null
operator|||
name|logEntity
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|logEntity
operator|=
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|logEntity
operator|==
literal|null
operator|||
name|logEntity
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|logEntity
operator|=
literal|"UNKNOWN"
expr_stmt|;
block|}
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
block|}
comment|/**    * The content of this page is the AggregatedLogsBlock    *     * @return AggregatedLogsBlock.class    */
annotation|@
name|Override
DECL|method|content ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|content
parameter_list|()
block|{
return|return
name|AggregatedLogsBlock
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

