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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|StringHelper
operator|.
name|join
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|yarn
operator|.
name|webapp
operator|.
name|SubView
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * A simpler two column layout implementation with a header, a navigation bar  * on the left, content on the right, and a footer. Works with resizable themes.  * @see TwoColumnCssLayout  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|,
literal|"MapReduce"
block|}
argument_list|)
DECL|class|TwoColumnLayout
specifier|public
class|class
name|TwoColumnLayout
extends|extends
name|HtmlPage
block|{
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.yarn.webapp.view.HtmlPage#render(org.apache.hadoop.yarn.webapp.hamlet.Hamlet.HTML)    */
DECL|method|render (Page.HTML<_> html)
annotation|@
name|Override
specifier|protected
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
name|preHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|html
operator|.
name|title
argument_list|(
name|$
argument_list|(
name|TITLE
argument_list|)
argument_list|)
operator|.
name|link
argument_list|(
name|root_url
argument_list|(
literal|"static"
argument_list|,
literal|"yarn.css"
argument_list|)
argument_list|)
operator|.
name|style
argument_list|(
literal|"#layout { height: 100%; }"
argument_list|,
literal|"#layout thead td { height: 3em; }"
argument_list|,
literal|"#layout #navcell { width: 11em; padding: 0 1em; }"
argument_list|,
literal|"#layout td.content { padding-top: 0 }"
argument_list|,
literal|"#layout tbody { vertical-align: top; }"
argument_list|,
literal|"#layout tfoot td { height: 4em; }"
argument_list|)
operator|.
name|_
argument_list|(
name|JQueryUI
operator|.
name|class
argument_list|)
expr_stmt|;
name|postHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|JQueryUI
operator|.
name|jsnotice
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|html
operator|.
name|table
argument_list|(
literal|"#layout.ui-widget-content"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|$colspan
argument_list|(
literal|2
argument_list|)
operator|.
name|_
argument_list|(
name|header
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tfoot
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|$colspan
argument_list|(
literal|2
argument_list|)
operator|.
name|_
argument_list|(
name|footer
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tbody
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|$id
argument_list|(
literal|"navcell"
argument_list|)
operator|.
name|_
argument_list|(
name|nav
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|$class
argument_list|(
literal|"content"
argument_list|)
operator|.
name|_
argument_list|(
name|content
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
comment|/**    * Do what needs to be done before the header is rendered.  This usually    * involves setting page variables for Javascript and CSS rendering.    * @param html the html to use to render.     */
DECL|method|preHead (Page.HTML<_> html)
specifier|protected
name|void
name|preHead
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|_
argument_list|>
name|html
parameter_list|)
block|{   }
comment|/**    * Do what needs to be done after the header is rendered.    * @param html the html to use to render.     */
DECL|method|postHead (Page.HTML<_> html)
specifier|protected
name|void
name|postHead
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|_
argument_list|>
name|html
parameter_list|)
block|{   }
comment|/**    * @return the class that will render the header of the page.    */
DECL|method|header ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|header
parameter_list|()
block|{
return|return
name|HeaderBlock
operator|.
name|class
return|;
block|}
comment|/**    * @return the class that will render the content of the page.    */
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
name|LipsumBlock
operator|.
name|class
return|;
block|}
comment|/**    * @return the class that will render the navigation bar.    */
DECL|method|nav ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|nav
parameter_list|()
block|{
return|return
name|NavBlock
operator|.
name|class
return|;
block|}
comment|/**    * @return the class that will render the footer.    */
DECL|method|footer ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|footer
parameter_list|()
block|{
return|return
name|FooterBlock
operator|.
name|class
return|;
block|}
comment|/**    * Sets up a table to be a consistent style.    * @param html the HTML to use to render.    * @param tableId the ID of the table to set styles on.    * @param innerStyles any other styles to add to the table.    */
DECL|method|setTableStyles (Page.HTML<_> html, String tableId, String... innerStyles)
specifier|protected
name|void
name|setTableStyles
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|_
argument_list|>
name|html
parameter_list|,
name|String
name|tableId
parameter_list|,
name|String
modifier|...
name|innerStyles
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|styles
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|styles
operator|.
name|add
argument_list|(
name|join
argument_list|(
literal|'#'
argument_list|,
name|tableId
argument_list|,
literal|"_paginate span {font-weight:normal}"
argument_list|)
argument_list|)
expr_stmt|;
name|styles
operator|.
name|add
argument_list|(
name|join
argument_list|(
literal|'#'
argument_list|,
name|tableId
argument_list|,
literal|" .progress {width:8em}"
argument_list|)
argument_list|)
expr_stmt|;
name|styles
operator|.
name|add
argument_list|(
name|join
argument_list|(
literal|'#'
argument_list|,
name|tableId
argument_list|,
literal|"_processing {top:-1.5em; font-size:1em;"
argument_list|)
argument_list|)
expr_stmt|;
name|styles
operator|.
name|add
argument_list|(
literal|"  color:#000; background:#fefefe}"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|style
range|:
name|innerStyles
control|)
block|{
name|styles
operator|.
name|add
argument_list|(
name|join
argument_list|(
literal|'#'
argument_list|,
name|tableId
argument_list|,
literal|" "
argument_list|,
name|style
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|html
operator|.
name|style
argument_list|(
name|styles
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

