begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_comment
comment|/**  * Simple enum to express {true, false, don't know}.  */
end_comment

begin_enum
DECL|enum|Tristate
specifier|public
enum|enum
name|Tristate
block|{
comment|// Do not add additional values here.  Logic will assume there are exactly
comment|// three possibilities.
DECL|enumConstant|TRUE
DECL|enumConstant|FALSE
DECL|enumConstant|UNKNOWN
name|TRUE
block|,
name|FALSE
block|,
name|UNKNOWN
block|;
DECL|method|fromBool (boolean v)
specifier|public
specifier|static
name|Tristate
name|fromBool
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
return|return
name|v
condition|?
name|TRUE
else|:
name|FALSE
return|;
block|}
block|}
end_enum

end_unit

