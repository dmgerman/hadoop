begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp.view
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
name|view
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
name|MockApps
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
name|test
operator|.
name|WebAppTests
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
name|TwoColumnCssLayout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestTwoColumnCssPage
specifier|public
class|class
name|TestTwoColumnCssPage
block|{
DECL|class|TestController
specifier|public
specifier|static
class|class
name|TestController
extends|extends
name|Controller
block|{
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
literal|"title"
argument_list|,
literal|"Testing a Two Column Layout"
argument_list|)
expr_stmt|;
name|set
argument_list|(
literal|"ui.accordion.id"
argument_list|,
literal|"nav"
argument_list|)
expr_stmt|;
name|set
argument_list|(
literal|"ui.themeswitcher.id"
argument_list|,
literal|"themeswitcher"
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|TwoColumnCssLayout
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|names ()
specifier|public
name|void
name|names
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|8
condition|;
operator|++
name|i
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|MockApps
operator|.
name|newAppName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|setTitle
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|textnames ()
specifier|public
name|void
name|textnames
parameter_list|()
block|{
name|names
argument_list|()
expr_stmt|;
name|renderText
argument_list|(
name|$
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestView
specifier|public
specifier|static
class|class
name|TestView
extends|extends
name|HtmlPage
block|{
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
name|$
argument_list|(
literal|"title"
argument_list|)
argument_list|)
operator|.
name|h1
argument_list|(
name|$
argument_list|(
literal|"title"
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|shouldNotThrow ()
annotation|@
name|Test
specifier|public
name|void
name|shouldNotThrow
parameter_list|()
block|{
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|TwoColumnCssLayout
operator|.
name|class
argument_list|)
expr_stmt|;
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
block|{
name|WebApps
operator|.
name|$for
argument_list|(
literal|"test"
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

