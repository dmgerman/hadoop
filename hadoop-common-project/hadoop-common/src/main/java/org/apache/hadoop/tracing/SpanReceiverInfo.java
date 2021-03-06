begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tracing
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tracing
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|SpanReceiverInfo
specifier|public
class|class
name|SpanReceiverInfo
block|{
DECL|field|id
specifier|private
specifier|final
name|long
name|id
decl_stmt|;
DECL|field|className
specifier|private
specifier|final
name|String
name|className
decl_stmt|;
DECL|field|configPairs
specifier|final
name|List
argument_list|<
name|ConfigurationPair
argument_list|>
name|configPairs
init|=
operator|new
name|ArrayList
argument_list|<
name|ConfigurationPair
argument_list|>
argument_list|()
decl_stmt|;
DECL|class|ConfigurationPair
specifier|static
class|class
name|ConfigurationPair
block|{
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|ConfigurationPair (String key, String value)
name|ConfigurationPair
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getKey ()
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getValue ()
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|method|SpanReceiverInfo (long id, String className)
name|SpanReceiverInfo
parameter_list|(
name|long
name|id
parameter_list|,
name|String
name|className
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getClassName ()
specifier|public
name|String
name|getClassName
parameter_list|()
block|{
return|return
name|className
return|;
block|}
block|}
end_class

end_unit

