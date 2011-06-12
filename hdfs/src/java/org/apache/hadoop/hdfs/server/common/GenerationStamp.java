begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
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

begin_comment
comment|/****************************************************************  * A GenerationStamp is a Hadoop FS primitive, identified by a long.  ****************************************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|GenerationStamp
specifier|public
class|class
name|GenerationStamp
implements|implements
name|Comparable
argument_list|<
name|GenerationStamp
argument_list|>
block|{
comment|/**    * The first valid generation stamp.    */
DECL|field|FIRST_VALID_STAMP
specifier|public
specifier|static
specifier|final
name|long
name|FIRST_VALID_STAMP
init|=
literal|1000L
decl_stmt|;
comment|/**    * Generation stamp of blocks that pre-date the introduction    * of a generation stamp.    */
DECL|field|GRANDFATHER_GENERATION_STAMP
specifier|public
specifier|static
specifier|final
name|long
name|GRANDFATHER_GENERATION_STAMP
init|=
literal|0
decl_stmt|;
DECL|field|genstamp
specifier|private
specifier|volatile
name|long
name|genstamp
decl_stmt|;
comment|/**    * Create a new instance, initialized to FIRST_VALID_STAMP.    */
DECL|method|GenerationStamp ()
specifier|public
name|GenerationStamp
parameter_list|()
block|{
name|this
argument_list|(
name|GenerationStamp
operator|.
name|FIRST_VALID_STAMP
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new instance, initialized to the specified value.    */
DECL|method|GenerationStamp (long stamp)
name|GenerationStamp
parameter_list|(
name|long
name|stamp
parameter_list|)
block|{
name|this
operator|.
name|genstamp
operator|=
name|stamp
expr_stmt|;
block|}
comment|/**    * Returns the current generation stamp    */
DECL|method|getStamp ()
specifier|public
name|long
name|getStamp
parameter_list|()
block|{
return|return
name|this
operator|.
name|genstamp
return|;
block|}
comment|/**    * Sets the current generation stamp    */
DECL|method|setStamp (long stamp)
specifier|public
name|void
name|setStamp
parameter_list|(
name|long
name|stamp
parameter_list|)
block|{
name|this
operator|.
name|genstamp
operator|=
name|stamp
expr_stmt|;
block|}
comment|/**    * First increments the counter and then returns the stamp     */
DECL|method|nextStamp ()
specifier|public
specifier|synchronized
name|long
name|nextStamp
parameter_list|()
block|{
name|this
operator|.
name|genstamp
operator|++
expr_stmt|;
return|return
name|this
operator|.
name|genstamp
return|;
block|}
annotation|@
name|Override
comment|// Comparable
DECL|method|compareTo (GenerationStamp that)
specifier|public
name|int
name|compareTo
parameter_list|(
name|GenerationStamp
name|that
parameter_list|)
block|{
return|return
name|this
operator|.
name|genstamp
operator|<
name|that
operator|.
name|genstamp
condition|?
operator|-
literal|1
else|:
name|this
operator|.
name|genstamp
operator|>
name|that
operator|.
name|genstamp
condition|?
literal|1
else|:
literal|0
return|;
block|}
annotation|@
name|Override
comment|// Object
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|GenerationStamp
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|compareTo
argument_list|(
operator|(
name|GenerationStamp
operator|)
name|o
argument_list|)
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
comment|// Object
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|genstamp
operator|^
operator|(
name|genstamp
operator|>>>
literal|32
operator|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

