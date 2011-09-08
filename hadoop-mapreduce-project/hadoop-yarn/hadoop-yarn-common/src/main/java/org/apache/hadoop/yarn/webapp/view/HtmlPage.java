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
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|MimeType
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
name|WebAppException
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
name|hamlet
operator|.
name|Hamlet
import|;
end_import

begin_comment
comment|/**  * The parent class of all HTML pages.  Override   * {@link #render(org.apache.hadoop.yarn.webapp.hamlet.Hamlet.HTML)}  * to actually render the page.  */
end_comment

begin_class
DECL|class|HtmlPage
specifier|public
specifier|abstract
class|class
name|HtmlPage
extends|extends
name|TextView
block|{
DECL|class|_
specifier|public
specifier|static
class|class
name|_
implements|implements
name|Hamlet
operator|.
name|_
block|{   }
DECL|class|Page
specifier|public
class|class
name|Page
extends|extends
name|Hamlet
block|{
DECL|method|Page (PrintWriter out)
name|Page
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|subView (Class<? extends SubView> cls)
specifier|protected
name|void
name|subView
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|cls
parameter_list|)
block|{
name|context
argument_list|()
operator|.
name|set
argument_list|(
name|nestLevel
argument_list|()
argument_list|,
name|wasInline
argument_list|()
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|cls
argument_list|)
expr_stmt|;
name|setWasInline
argument_list|(
name|context
argument_list|()
operator|.
name|wasInline
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|html ()
specifier|public
name|HTML
argument_list|<
name|HtmlPage
operator|.
name|_
argument_list|>
name|html
parameter_list|()
block|{
return|return
operator|new
name|HTML
argument_list|<
name|HtmlPage
operator|.
name|_
argument_list|>
argument_list|(
literal|"html"
argument_list|,
literal|null
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|EOpt
operator|.
name|ENDTAG
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|field|DOCTYPE
specifier|public
specifier|static
specifier|final
name|String
name|DOCTYPE
init|=
literal|"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\""
operator|+
literal|" \"http://www.w3.org/TR/html4/strict.dtd\">"
decl_stmt|;
DECL|field|page
specifier|private
name|Page
name|page
decl_stmt|;
DECL|method|page ()
specifier|private
name|Page
name|page
parameter_list|()
block|{
if|if
condition|(
name|page
operator|==
literal|null
condition|)
block|{
name|page
operator|=
operator|new
name|Page
argument_list|(
name|writer
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|page
return|;
block|}
DECL|method|HtmlPage ()
specifier|protected
name|HtmlPage
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|HtmlPage (ViewContext ctx)
specifier|protected
name|HtmlPage
parameter_list|(
name|ViewContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|,
name|MimeType
operator|.
name|HTML
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render ()
specifier|public
name|void
name|render
parameter_list|()
block|{
name|puts
argument_list|(
name|DOCTYPE
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|page
argument_list|()
operator|.
name|html
argument_list|()
operator|.
name|meta_http
argument_list|(
literal|"Content-type"
argument_list|,
name|MimeType
operator|.
name|HTML
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|page
argument_list|()
operator|.
name|nestLevel
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|WebAppException
argument_list|(
literal|"Error rendering page: nestLevel="
operator|+
name|page
argument_list|()
operator|.
name|nestLevel
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Render the the HTML page.    * @param html the page to render data to.    */
DECL|method|render (Page.HTML<_> html)
specifier|protected
specifier|abstract
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
function_decl|;
block|}
end_class

end_unit

