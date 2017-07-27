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
name|YarnWebParams
import|;
end_import

begin_comment
comment|/**  * This class is used to display a message that the proxy request failed  * because of a redirection issue.  */
end_comment

begin_class
DECL|class|RedirectionErrorPage
specifier|public
class|class
name|RedirectionErrorPage
extends|extends
name|RmView
block|{
DECL|method|preHead (Page.HTML<__> html)
annotation|@
name|Override
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
name|aid
init|=
name|$
argument_list|(
name|YarnWebParams
operator|.
name|APPLICATION_ID
argument_list|)
decl_stmt|;
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|YarnWebParams
operator|.
name|ERROR_MESSAGE
argument_list|,
literal|"The application master for "
operator|+
name|aid
operator|+
literal|" redirected the "
operator|+
literal|"resource manager's web proxy's request back to the web proxy, "
operator|+
literal|"which means your request to view the application master's web UI "
operator|+
literal|"cannot be fulfilled. The typical cause for this error is a "
operator|+
literal|"network misconfiguration that causes the resource manager's web "
operator|+
literal|"proxy host to resolve to an unexpected IP address on the "
operator|+
literal|"application master host. Please contact your cluster "
operator|+
literal|"administrator to resolve the issue."
argument_list|)
expr_stmt|;
block|}
DECL|method|content ()
annotation|@
name|Override
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
name|ErrorBlock
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

