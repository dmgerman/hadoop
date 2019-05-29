begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_comment
comment|/**  * Wrapper class for Radix tree node representing Ozone prefix path segment  * separated by "/".  */
end_comment

begin_class
DECL|class|RadixNode
specifier|public
class|class
name|RadixNode
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|RadixNode (String name)
specifier|public
name|RadixNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|children
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|hasChildren ()
specifier|public
name|boolean
name|hasChildren
parameter_list|()
block|{
return|return
name|children
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|getChildren ()
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|RadixNode
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|children
return|;
block|}
DECL|method|setValue (T v)
specifier|public
name|void
name|setValue
parameter_list|(
name|T
name|v
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|v
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|T
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|field|children
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|RadixNode
argument_list|>
name|children
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|// TODO: k/v pairs for more metadata as needed
DECL|field|value
specifier|private
name|T
name|value
decl_stmt|;
block|}
end_class

end_unit

