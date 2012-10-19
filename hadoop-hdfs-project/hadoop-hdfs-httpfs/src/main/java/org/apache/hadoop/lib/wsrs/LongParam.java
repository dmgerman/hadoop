begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.wsrs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|wsrs
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
name|Private
DECL|class|LongParam
specifier|public
specifier|abstract
class|class
name|LongParam
extends|extends
name|Param
argument_list|<
name|Long
argument_list|>
block|{
DECL|method|LongParam (String name, Long defaultValue)
specifier|public
name|LongParam
parameter_list|(
name|String
name|name
parameter_list|,
name|Long
name|defaultValue
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|defaultValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse (String str)
specifier|protected
name|Long
name|parse
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|str
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDomain ()
specifier|protected
name|String
name|getDomain
parameter_list|()
block|{
return|return
literal|"a long"
return|;
block|}
block|}
end_class

end_unit

