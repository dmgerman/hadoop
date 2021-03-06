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
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|ResponseInfo
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
name|junit
operator|.
name|Before
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
DECL|class|TestInfoBlock
specifier|public
class|class
name|TestInfoBlock
block|{
DECL|field|sw
specifier|public
specifier|static
name|StringWriter
name|sw
decl_stmt|;
DECL|field|pw
specifier|public
specifier|static
name|PrintWriter
name|pw
decl_stmt|;
DECL|field|JAVASCRIPT
specifier|static
specifier|final
name|String
name|JAVASCRIPT
init|=
literal|"<script>alert('text')</script>"
decl_stmt|;
DECL|field|JAVASCRIPT_ESCAPED
specifier|static
specifier|final
name|String
name|JAVASCRIPT_ESCAPED
init|=
literal|"&lt;script&gt;alert('text')&lt;/script&gt;"
decl_stmt|;
DECL|class|JavaScriptInfoBlock
specifier|public
specifier|static
class|class
name|JavaScriptInfoBlock
extends|extends
name|InfoBlock
block|{
DECL|field|resInfo
specifier|static
name|ResponseInfo
name|resInfo
decl_stmt|;
static|static
block|{
name|resInfo
operator|=
operator|new
name|ResponseInfo
argument_list|()
expr_stmt|;
name|resInfo
operator|.
name|__
argument_list|(
literal|"User_Name"
argument_list|,
name|JAVASCRIPT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writer ()
specifier|public
name|PrintWriter
name|writer
parameter_list|()
block|{
return|return
name|TestInfoBlock
operator|.
name|pw
return|;
block|}
DECL|method|JavaScriptInfoBlock (ResponseInfo info)
name|JavaScriptInfoBlock
parameter_list|(
name|ResponseInfo
name|info
parameter_list|)
block|{
name|super
argument_list|(
name|resInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|JavaScriptInfoBlock ()
specifier|public
name|JavaScriptInfoBlock
parameter_list|()
block|{
name|super
argument_list|(
name|resInfo
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MultilineInfoBlock
specifier|public
specifier|static
class|class
name|MultilineInfoBlock
extends|extends
name|InfoBlock
block|{
DECL|field|resInfo
specifier|static
name|ResponseInfo
name|resInfo
decl_stmt|;
static|static
block|{
name|resInfo
operator|=
operator|new
name|ResponseInfo
argument_list|()
expr_stmt|;
name|resInfo
operator|.
name|__
argument_list|(
literal|"Multiple_line_value"
argument_list|,
literal|"This is one line."
argument_list|)
expr_stmt|;
name|resInfo
operator|.
name|__
argument_list|(
literal|"Multiple_line_value"
argument_list|,
literal|"This is first line.\nThis is second line."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writer ()
specifier|public
name|PrintWriter
name|writer
parameter_list|()
block|{
return|return
name|TestInfoBlock
operator|.
name|pw
return|;
block|}
DECL|method|MultilineInfoBlock (ResponseInfo info)
name|MultilineInfoBlock
parameter_list|(
name|ResponseInfo
name|info
parameter_list|)
block|{
name|super
argument_list|(
name|resInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|MultilineInfoBlock ()
specifier|public
name|MultilineInfoBlock
parameter_list|()
block|{
name|super
argument_list|(
name|resInfo
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|sw
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|pw
operator|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000L
argument_list|)
DECL|method|testMultilineInfoBlock ()
specifier|public
name|void
name|testMultilineInfoBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|WebAppTests
operator|.
name|testBlock
argument_list|(
name|MultilineInfoBlock
operator|.
name|class
argument_list|)
expr_stmt|;
name|TestInfoBlock
operator|.
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|output
init|=
name|TestInfoBlock
operator|.
name|sw
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|" +"
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
name|String
name|expectedMultilineData1
init|=
name|String
operator|.
name|format
argument_list|(
literal|"<tr class=\"odd\">%n"
operator|+
literal|"<th>%n Multiple_line_value%n</th>%n"
operator|+
literal|"<td>%n This is one line.%n</td>%n"
argument_list|)
decl_stmt|;
name|String
name|expectedMultilineData2
init|=
name|String
operator|.
name|format
argument_list|(
literal|"<tr class=\"even\">%n"
operator|+
literal|"<th>%n Multiple_line_value%n</th>%n<td>%n<div>%n"
operator|+
literal|" This is first line.%n</div>%n<div>%n"
operator|+
literal|" This is second line.%n</div>%n"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
name|expectedMultilineData1
argument_list|)
operator|&&
name|output
operator|.
name|contains
argument_list|(
name|expectedMultilineData2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000L
argument_list|)
DECL|method|testJavaScriptInfoBlock ()
specifier|public
name|void
name|testJavaScriptInfoBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|WebAppTests
operator|.
name|testBlock
argument_list|(
name|JavaScriptInfoBlock
operator|.
name|class
argument_list|)
expr_stmt|;
name|TestInfoBlock
operator|.
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|output
init|=
name|TestInfoBlock
operator|.
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"<script>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
name|JAVASCRIPT_ESCAPED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

