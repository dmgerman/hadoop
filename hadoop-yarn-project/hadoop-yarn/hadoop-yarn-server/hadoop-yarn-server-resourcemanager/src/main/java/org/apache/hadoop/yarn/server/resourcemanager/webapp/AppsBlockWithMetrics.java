begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
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
name|resourcemanager
operator|.
name|webapp
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
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|HtmlBlock
import|;
end_import

begin_comment
comment|/**  * Renders a block for the applications with metrics information.  */
end_comment

begin_class
DECL|class|AppsBlockWithMetrics
class|class
name|AppsBlockWithMetrics
extends|extends
name|HtmlBlock
block|{
DECL|method|render (Block html)
annotation|@
name|Override
specifier|public
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|html
operator|.
name|__
argument_list|(
name|MetricsOverviewTable
operator|.
name|class
argument_list|)
expr_stmt|;
name|html
operator|.
name|__
argument_list|(
name|RMAppsBlock
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

