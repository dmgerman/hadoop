begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen.datatypes.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
operator|.
name|datatypes
operator|.
name|util
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
name|tools
operator|.
name|rumen
operator|.
name|datatypes
operator|.
name|DataType
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
name|tools
operator|.
name|rumen
operator|.
name|datatypes
operator|.
name|DefaultDataType
import|;
end_import

begin_comment
comment|/**  * A simple job property parser that acts like a pass-through filter.  */
end_comment

begin_class
DECL|class|DefaultJobPropertiesParser
specifier|public
class|class
name|DefaultJobPropertiesParser
implements|implements
name|JobPropertyParser
block|{
annotation|@
name|Override
DECL|method|parseJobProperty (String key, String value)
specifier|public
name|DataType
argument_list|<
name|?
argument_list|>
name|parseJobProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|DefaultDataType
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

