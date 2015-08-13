begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

begin_comment
comment|/**  * Information about the EC Zone at the specified path.  */
end_comment

begin_class
DECL|class|ErasureCodingZone
specifier|public
class|class
name|ErasureCodingZone
block|{
DECL|field|dir
specifier|private
name|String
name|dir
decl_stmt|;
DECL|field|ecPolicy
specifier|private
name|ErasureCodingPolicy
name|ecPolicy
decl_stmt|;
DECL|method|ErasureCodingZone (String dir, ErasureCodingPolicy ecPolicy)
specifier|public
name|ErasureCodingZone
parameter_list|(
name|String
name|dir
parameter_list|,
name|ErasureCodingPolicy
name|ecPolicy
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|ecPolicy
operator|=
name|ecPolicy
expr_stmt|;
block|}
comment|/**    * Get directory of the EC zone.    *     * @return    */
DECL|method|getDir ()
specifier|public
name|String
name|getDir
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
comment|/**    * Get the erasure coding policy for the EC Zone    *     * @return    */
DECL|method|getErasureCodingPolicy ()
specifier|public
name|ErasureCodingPolicy
name|getErasureCodingPolicy
parameter_list|()
block|{
return|return
name|ecPolicy
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Dir: "
operator|+
name|getDir
argument_list|()
operator|+
literal|", Policy: "
operator|+
name|ecPolicy
return|;
block|}
block|}
end_class

end_unit

