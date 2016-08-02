begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_import
import|import
name|com
operator|.
name|beust
operator|.
name|jcommander
operator|.
name|converters
operator|.
name|BaseConverter
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
name|fs
operator|.
name|Path
import|;
end_import

begin_class
DECL|class|PathArgumentConverter
specifier|public
class|class
name|PathArgumentConverter
extends|extends
name|BaseConverter
argument_list|<
name|Path
argument_list|>
block|{
DECL|method|PathArgumentConverter (String optionName)
specifier|public
name|PathArgumentConverter
parameter_list|(
name|String
name|optionName
parameter_list|)
block|{
name|super
argument_list|(
name|optionName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|convert (String value)
specifier|public
name|Path
name|convert
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

