begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
package|;
end_package

begin_comment
comment|/**  * Different authorization operations supported  * in WASB.  */
end_comment

begin_enum
DECL|enum|WasbAuthorizationOperations
specifier|public
enum|enum
name|WasbAuthorizationOperations
block|{
DECL|enumConstant|READ
DECL|enumConstant|WRITE
DECL|enumConstant|EXECUTE
name|READ
block|,
name|WRITE
block|,
name|EXECUTE
block|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|READ
case|:
return|return
literal|"read"
return|;
case|case
name|WRITE
case|:
return|return
literal|"write"
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid Authorization Operation"
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

end_unit

