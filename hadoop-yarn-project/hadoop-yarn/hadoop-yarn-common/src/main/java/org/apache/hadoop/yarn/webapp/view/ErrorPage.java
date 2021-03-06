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
name|CharArrayWriter
import|;
end_import

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

begin_comment
comment|/**  * A jquery-ui themeable error page  */
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
DECL|class|ErrorPage
specifier|public
class|class
name|ErrorPage
extends|extends
name|HtmlPage
block|{
annotation|@
name|Override
DECL|method|render (Page.HTML<__> html)
specifier|protected
name|void
name|render
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
name|set
argument_list|(
name|JQueryUI
operator|.
name|ACCORDION_ID
argument_list|,
literal|"msg"
argument_list|)
expr_stmt|;
name|String
name|title
init|=
literal|"Sorry, got error "
operator|+
name|status
argument_list|()
decl_stmt|;
name|html
operator|.
name|title
argument_list|(
name|title
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
name|__
argument_list|(
name|JQueryUI
operator|.
name|class
argument_list|)
operator|.
comment|// an embedded sub-view
name|style
argument_list|(
literal|"#msg { margin: 1em auto; width: 88%; }"
argument_list|,
literal|"#msg h1 { padding: 0.2em 1.5em; font: bold 1.3em serif; }"
argument_list|)
operator|.
name|div
argument_list|(
literal|"#msg"
argument_list|)
operator|.
name|h1
argument_list|(
name|title
argument_list|)
operator|.
name|div
argument_list|()
operator|.
name|__
argument_list|(
literal|"Please consult"
argument_list|)
operator|.
name|a
argument_list|(
literal|"http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html"
argument_list|,
literal|"RFC 2616"
argument_list|)
operator|.
name|__
argument_list|(
literal|" for meanings of the error code."
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|h1
argument_list|(
literal|"Error Details"
argument_list|)
operator|.
name|pre
argument_list|()
operator|.
name|__
argument_list|(
name|errorDetails
argument_list|()
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
DECL|method|errorDetails ()
specifier|protected
name|String
name|errorDetails
parameter_list|()
block|{
if|if
condition|(
operator|!
name|$
argument_list|(
name|ERROR_DETAILS
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|$
argument_list|(
name|ERROR_DETAILS
argument_list|)
return|;
block|}
if|if
condition|(
name|error
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|toStackTrace
argument_list|(
name|error
argument_list|()
argument_list|,
literal|1024
operator|*
literal|64
argument_list|)
return|;
block|}
return|return
literal|"No exception was thrown."
return|;
block|}
DECL|method|toStackTrace (Throwable error, int cutoff)
specifier|public
specifier|static
name|String
name|toStackTrace
parameter_list|(
name|Throwable
name|error
parameter_list|,
name|int
name|cutoff
parameter_list|)
block|{
comment|// default initial size is 32 chars
name|CharArrayWriter
name|buffer
init|=
operator|new
name|CharArrayWriter
argument_list|(
literal|8
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|error
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|size
argument_list|()
operator|<
name|cutoff
condition|?
name|buffer
operator|.
name|toString
argument_list|()
else|:
name|buffer
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|cutoff
argument_list|)
return|;
block|}
block|}
end_class

end_unit

