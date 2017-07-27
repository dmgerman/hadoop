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
name|classification
operator|.
name|InterfaceAudience
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
DECL|class|LipsumBlock
specifier|public
class|class
name|LipsumBlock
extends|extends
name|HtmlBlock
block|{
annotation|@
name|Override
DECL|method|render (Block html)
specifier|public
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|html
operator|.
name|p
argument_list|()
operator|.
name|__
argument_list|(
literal|"Lorem ipsum dolor sit amet, consectetur adipiscing elit."
argument_list|,
literal|"Vivamus eu dui in ipsum tincidunt egestas ac sed nibh."
argument_list|,
literal|"Praesent quis nisl lorem, nec interdum urna."
argument_list|,
literal|"Duis sagittis dignissim purus sed sollicitudin."
argument_list|,
literal|"Morbi quis diam eu enim semper suscipit."
argument_list|,
literal|"Nullam pretium faucibus sapien placerat tincidunt."
argument_list|,
literal|"Donec eget lorem at quam fermentum vulputate a ac purus."
argument_list|,
literal|"Cras ac dui felis, in pulvinar est."
argument_list|,
literal|"Praesent tempor est sed neque pulvinar dictum."
argument_list|,
literal|"Nullam magna augue, egestas luctus sollicitudin sed,"
argument_list|,
literal|"venenatis nec turpis."
argument_list|,
literal|"Ut ante enim, congue sed laoreet et, accumsan id metus."
argument_list|,
literal|"Mauris tincidunt imperdiet est, sed porta arcu vehicula et."
argument_list|,
literal|"Etiam in nisi nunc."
argument_list|,
literal|"Phasellus vehicula scelerisque quam, ac dignissim felis euismod a."
argument_list|,
literal|"Proin eu ante nisl, vel porttitor eros."
argument_list|,
literal|"Aliquam gravida luctus augue, at scelerisque enim consectetur vel."
argument_list|,
literal|"Donec interdum tempor nisl, quis laoreet enim venenatis eu."
argument_list|,
literal|"Quisque elit elit, vulputate eget porta vel, laoreet ac lacus."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

