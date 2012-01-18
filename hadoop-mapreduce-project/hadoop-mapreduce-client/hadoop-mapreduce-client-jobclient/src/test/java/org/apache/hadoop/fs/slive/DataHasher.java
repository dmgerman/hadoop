begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * Class which is used to create the data to write for a given path and offset  * into that file for writing and later verification that the expected value is  * read at that file bytes offset  */
end_comment

begin_class
DECL|class|DataHasher
class|class
name|DataHasher
block|{
DECL|field|rnd
specifier|private
name|Random
name|rnd
decl_stmt|;
DECL|method|DataHasher (long mixIn)
name|DataHasher
parameter_list|(
name|long
name|mixIn
parameter_list|)
block|{
name|this
operator|.
name|rnd
operator|=
operator|new
name|Random
argument_list|(
name|mixIn
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param offSet    *          the byte offset into the file    *     * @return the data to be expected at that offset    */
DECL|method|generate (long offSet)
name|long
name|generate
parameter_list|(
name|long
name|offSet
parameter_list|)
block|{
return|return
operator|(
operator|(
name|offSet
operator|*
literal|47
operator|)
operator|^
operator|(
name|rnd
operator|.
name|nextLong
argument_list|()
operator|*
literal|97
operator|)
operator|)
operator|*
literal|37
return|;
block|}
block|}
end_class

end_unit

