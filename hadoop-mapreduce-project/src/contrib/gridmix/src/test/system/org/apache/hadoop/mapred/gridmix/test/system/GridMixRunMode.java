begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix.test.system
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
operator|.
name|test
operator|.
name|system
package|;
end_package

begin_comment
comment|/**  * Gridmix run modes.   *  */
end_comment

begin_enum
DECL|enum|GridMixRunMode
specifier|public
enum|enum
name|GridMixRunMode
block|{
DECL|enumConstant|DATA_GENERATION
DECL|enumConstant|RUN_GRIDMIX
DECL|enumConstant|DATA_GENERATION_AND_RUN_GRIDMIX
name|DATA_GENERATION
argument_list|(
literal|1
argument_list|)
block|,
name|RUN_GRIDMIX
argument_list|(
literal|2
argument_list|)
block|,
name|DATA_GENERATION_AND_RUN_GRIDMIX
argument_list|(
literal|3
argument_list|)
block|;
DECL|field|mode
specifier|private
name|int
name|mode
decl_stmt|;
DECL|method|GridMixRunMode (int mode)
name|GridMixRunMode
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
block|}
end_enum

end_unit

