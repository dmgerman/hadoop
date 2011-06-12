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

begin_comment
comment|/**  * Simple timer class that abstracts time access  */
end_comment

begin_class
DECL|class|Timer
class|class
name|Timer
block|{
comment|// no construction allowed
DECL|method|Timer ()
specifier|private
name|Timer
parameter_list|()
block|{    }
comment|/**    * The current time in milliseconds    *     * @return long (milliseconds)    */
DECL|method|now ()
specifier|static
name|long
name|now
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
comment|/**    * Calculates how much time in milliseconds elapsed from given start time to    * the current time in milliseconds    *     * @param startTime    * @return elapsed time (milliseconds)    */
DECL|method|elapsed (long startTime)
specifier|static
name|long
name|elapsed
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|long
name|elapsedTime
init|=
name|now
argument_list|()
operator|-
name|startTime
decl_stmt|;
if|if
condition|(
name|elapsedTime
operator|<
literal|0
condition|)
block|{
name|elapsedTime
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|elapsedTime
return|;
block|}
block|}
end_class

end_unit

