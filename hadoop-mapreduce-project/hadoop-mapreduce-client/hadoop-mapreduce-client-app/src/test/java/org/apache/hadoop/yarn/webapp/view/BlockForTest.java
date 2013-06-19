begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  *   BlockForTest publishes constructor for test  */
end_comment

begin_class
DECL|class|BlockForTest
specifier|public
class|class
name|BlockForTest
extends|extends
name|HtmlBlock
operator|.
name|Block
block|{
DECL|method|BlockForTest (HtmlBlock htmlBlock, PrintWriter out, int level, boolean wasInline)
specifier|public
name|BlockForTest
parameter_list|(
name|HtmlBlock
name|htmlBlock
parameter_list|,
name|PrintWriter
name|out
parameter_list|,
name|int
name|level
parameter_list|,
name|boolean
name|wasInline
parameter_list|)
block|{
name|htmlBlock
operator|.
name|super
argument_list|(
name|out
argument_list|,
name|level
argument_list|,
name|wasInline
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

