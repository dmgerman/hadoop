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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppState
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
operator|.
name|DIV
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
operator|.
name|LI
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
operator|.
name|UL
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
name|HtmlBlock
import|;
end_import

begin_class
DECL|class|NavBlock
specifier|public
class|class
name|NavBlock
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
name|UL
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|mainList
init|=
name|html
operator|.
name|div
argument_list|(
literal|"#nav"
argument_list|)
operator|.
name|h3
argument_list|(
literal|"Cluster"
argument_list|)
operator|.
name|ul
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"cluster"
argument_list|)
argument_list|,
literal|"About"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"nodes"
argument_list|)
argument_list|,
literal|"Nodes"
argument_list|)
operator|.
name|_
argument_list|()
decl_stmt|;
name|UL
argument_list|<
name|LI
argument_list|<
name|UL
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|subAppsList
init|=
name|mainList
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"apps"
argument_list|)
argument_list|,
literal|"Applications"
argument_list|)
operator|.
name|ul
argument_list|()
decl_stmt|;
name|subAppsList
operator|.
name|li
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
for|for
control|(
name|RMAppState
name|state
range|:
name|RMAppState
operator|.
name|values
argument_list|()
control|)
block|{
name|subAppsList
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"apps"
argument_list|,
name|state
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|state
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|subAppsList
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
name|mainList
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"scheduler"
argument_list|)
argument_list|,
literal|"Scheduler"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|h3
argument_list|(
literal|"Tools"
argument_list|)
operator|.
name|ul
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
literal|"/conf"
argument_list|,
literal|"Configuration"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
literal|"/logs"
argument_list|,
literal|"Local logs"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
literal|"/stacks"
argument_list|,
literal|"Server stacks"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|li
argument_list|()
operator|.
name|a
argument_list|(
literal|"/metrics"
argument_list|,
literal|"Server metrics"
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
expr_stmt|;
block|}
block|}
end_class

end_unit

