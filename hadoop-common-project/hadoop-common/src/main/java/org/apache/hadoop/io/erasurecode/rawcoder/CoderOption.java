begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.rawcoder
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|rawcoder
package|;
end_package

begin_comment
comment|/**  * Supported erasure coder options.  */
end_comment

begin_enum
DECL|enum|CoderOption
specifier|public
enum|enum
name|CoderOption
block|{
comment|/* If direct buffer is preferred, for perf consideration */
DECL|enumConstant|PREFER_DIRECT_BUFFER
name|PREFER_DIRECT_BUFFER
argument_list|(
literal|true
argument_list|)
block|,
comment|// READ-ONLY
comment|/**    * Allow changing input buffer content (not positions).    * Maybe better perf if allowed    */
DECL|enumConstant|ALLOW_CHANGE_INPUTS
name|ALLOW_CHANGE_INPUTS
argument_list|(
literal|false
argument_list|)
block|,
comment|// READ-WRITE
comment|/* Allow dump verbose debug info or not */
DECL|enumConstant|ALLOW_VERBOSE_DUMP
name|ALLOW_VERBOSE_DUMP
argument_list|(
literal|false
argument_list|)
block|;
comment|// READ-WRITE
DECL|field|isReadOnly
specifier|private
name|boolean
name|isReadOnly
init|=
literal|false
decl_stmt|;
DECL|method|CoderOption (boolean isReadOnly)
name|CoderOption
parameter_list|(
name|boolean
name|isReadOnly
parameter_list|)
block|{
name|this
operator|.
name|isReadOnly
operator|=
name|isReadOnly
expr_stmt|;
block|}
DECL|method|isReadOnly ()
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
name|isReadOnly
return|;
block|}
block|}
end_enum

begin_empty_stmt
empty_stmt|;
end_empty_stmt

end_unit

