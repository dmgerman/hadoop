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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|text
operator|.
name|StringEscapeUtils
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
name|View
import|;
end_import

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
DECL|class|TextView
specifier|public
specifier|abstract
class|class
name|TextView
extends|extends
name|View
block|{
DECL|field|contentType
specifier|private
specifier|final
name|String
name|contentType
decl_stmt|;
DECL|method|TextView (ViewContext ctx, String contentType)
specifier|protected
name|TextView
parameter_list|(
name|ViewContext
name|ctx
parameter_list|,
name|String
name|contentType
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentType
operator|=
name|contentType
expr_stmt|;
block|}
DECL|method|writer ()
annotation|@
name|Override
specifier|public
name|PrintWriter
name|writer
parameter_list|()
block|{
name|response
argument_list|()
operator|.
name|setContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|writer
argument_list|()
return|;
block|}
comment|/**    * Print strings escaping html.    * @param args the strings to print    */
DECL|method|echo (Object... args)
specifier|public
name|void
name|echo
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|PrintWriter
name|out
init|=
name|writer
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|s
range|:
name|args
control|)
block|{
name|String
name|escapedString
init|=
name|StringEscapeUtils
operator|.
name|escapeEcmaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml4
argument_list|(
name|s
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
name|escapedString
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Print strings as is (no newline, a la php echo).    * @param args the strings to print    */
DECL|method|echoWithoutEscapeHtml (Object... args)
specifier|public
name|void
name|echoWithoutEscapeHtml
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|PrintWriter
name|out
init|=
name|writer
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|s
range|:
name|args
control|)
block|{
name|out
operator|.
name|print
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Print strings as a line (new line appended at the end, a la C/Tcl puts).    * @param args the strings to print    */
DECL|method|puts (Object... args)
specifier|public
name|void
name|puts
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|echo
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|writer
argument_list|()
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
comment|/**    * Print string as a line. This does not escapes the string for html    * @param args the strings to print    */
DECL|method|putWithoutEscapeHtml (Object args)
specifier|public
name|void
name|putWithoutEscapeHtml
parameter_list|(
name|Object
name|args
parameter_list|)
block|{
name|echoWithoutEscapeHtml
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|writer
argument_list|()
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

