begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp.example
package|package
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
name|example
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
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
name|Controller
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
name|WebApps
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
name|view
operator|.
name|HtmlPage
import|;
end_import

begin_comment
comment|/**  * The embedded UI serves two pages at:  *<br>http://localhost:8888/my and  *<br>http://localhost:8888/my/anythingYouWant  */
end_comment

begin_class
DECL|class|MyApp
specifier|public
class|class
name|MyApp
block|{
comment|// This is an app API
DECL|method|anyAPI ()
specifier|public
name|String
name|anyAPI
parameter_list|()
block|{
return|return
literal|"anything, really!"
return|;
block|}
comment|// Note this is static so it can be in any files.
DECL|class|MyController
specifier|public
specifier|static
class|class
name|MyController
extends|extends
name|Controller
block|{
DECL|field|app
specifier|final
name|MyApp
name|app
decl_stmt|;
comment|// The app injection is optional
DECL|method|MyController (MyApp app, RequestContext ctx)
annotation|@
name|Inject
name|MyController
parameter_list|(
name|MyApp
name|app
parameter_list|,
name|RequestContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|app
operator|=
name|app
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|index ()
specifier|public
name|void
name|index
parameter_list|()
block|{
name|set
argument_list|(
literal|"anything"
argument_list|,
literal|"something"
argument_list|)
expr_stmt|;
block|}
DECL|method|anythingYouWant ()
specifier|public
name|void
name|anythingYouWant
parameter_list|()
block|{
name|set
argument_list|(
literal|"anything"
argument_list|,
name|app
operator|.
name|anyAPI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Ditto
DECL|class|MyView
specifier|public
specifier|static
class|class
name|MyView
extends|extends
name|HtmlPage
block|{
comment|// You can inject the app in views if needed.
annotation|@
name|Override
DECL|method|render (Page.HTML<_> html)
specifier|public
name|void
name|render
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|_
argument_list|>
name|html
parameter_list|)
block|{
name|html
operator|.
name|title
argument_list|(
literal|"My App"
argument_list|)
operator|.
name|p
argument_list|(
literal|"#content_id_for_css_styling"
argument_list|)
operator|.
name|_
argument_list|(
literal|"You can have"
argument_list|,
name|$
argument_list|(
literal|"anything"
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
comment|// Note, there is no _(); (to parent element) method at root level.
comment|// and IDE provides instant feedback on what level you're on in
comment|// the auto-completion drop-downs.
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|WebApps
operator|.
name|$for
argument_list|(
operator|new
name|MyApp
argument_list|()
argument_list|)
operator|.
name|at
argument_list|(
literal|8888
argument_list|)
operator|.
name|inDevMode
argument_list|()
operator|.
name|start
argument_list|()
operator|.
name|joinThread
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

