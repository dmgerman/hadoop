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
name|webapp
operator|.
name|Params
operator|.
name|*
import|;
end_import

begin_class
DECL|class|HeaderBlock
specifier|public
class|class
name|HeaderBlock
extends|extends
name|HtmlBlock
block|{
DECL|method|render (Block html)
annotation|@
name|Override
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|html
operator|.
name|div
argument_list|(
literal|"#header.ui-widget"
argument_list|)
operator|.
name|div
argument_list|(
literal|"#user"
argument_list|)
operator|.
name|_
argument_list|(
literal|"Logged in as: "
operator|+
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|div
argument_list|(
literal|"#logo"
argument_list|)
operator|.
name|img
argument_list|(
literal|"/static/hadoop-st.png"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|h1
argument_list|(
name|$
argument_list|(
name|TITLE
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

