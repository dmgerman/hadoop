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
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * This class holds utility functions for HTML  */
end_comment

begin_class
DECL|class|Html
specifier|public
class|class
name|Html
block|{
DECL|field|validIdRe
specifier|static
specifier|final
name|Pattern
name|validIdRe
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[a-zA-Z_.0-9]+$"
argument_list|)
decl_stmt|;
DECL|method|isValidId (String id)
specifier|public
specifier|static
name|boolean
name|isValidId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|validIdRe
operator|.
name|matcher
argument_list|(
name|id
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
block|}
end_class

end_unit

