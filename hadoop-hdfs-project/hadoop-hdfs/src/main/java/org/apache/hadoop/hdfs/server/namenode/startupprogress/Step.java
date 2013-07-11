begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.startupprogress
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
name|namenode
operator|.
name|startupprogress
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|CompareToBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|EqualsBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * A step performed by the namenode during a {@link Phase} of startup.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Step
specifier|public
class|class
name|Step
implements|implements
name|Comparable
argument_list|<
name|Step
argument_list|>
block|{
DECL|field|SEQUENCE
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|SEQUENCE
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|file
specifier|private
specifier|final
name|String
name|file
decl_stmt|;
DECL|field|sequenceNumber
specifier|private
specifier|final
name|int
name|sequenceNumber
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|long
name|size
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|StepType
name|type
decl_stmt|;
comment|/**    * Creates a new Step.    *     * @param type StepType type of step    */
DECL|method|Step (StepType type)
specifier|public
name|Step
parameter_list|(
name|StepType
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
literal|null
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new Step.    *     * @param file String file    */
DECL|method|Step (String file)
specifier|public
name|Step
parameter_list|(
name|String
name|file
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|file
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new Step.    *     * @param file String file    * @param size long size in bytes    */
DECL|method|Step (String file, long size)
specifier|public
name|Step
parameter_list|(
name|String
name|file
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|file
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new Step.    *     * @param type StepType type of step    * @param file String file    */
DECL|method|Step (StepType type, String file)
specifier|public
name|Step
parameter_list|(
name|StepType
name|type
parameter_list|,
name|String
name|file
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|file
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new Step.    *     * @param type StepType type of step    * @param file String file    * @param size long size in bytes    */
DECL|method|Step (StepType type, String file, long size)
specifier|public
name|Step
parameter_list|(
name|StepType
name|type
parameter_list|,
name|String
name|file
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|sequenceNumber
operator|=
name|SEQUENCE
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Step other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Step
name|other
parameter_list|)
block|{
comment|// Sort steps by file and then sequentially within the file to achieve the
comment|// desired order.  There is no concurrent map structure in the JDK that
comment|// maintains insertion order, so instead we attach a sequence number to each
comment|// step and sort on read.
return|return
operator|new
name|CompareToBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|file
argument_list|,
name|other
operator|.
name|file
argument_list|)
operator|.
name|append
argument_list|(
name|sequenceNumber
argument_list|,
name|other
operator|.
name|sequenceNumber
argument_list|)
operator|.
name|toComparison
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object otherObj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|otherObj
parameter_list|)
block|{
if|if
condition|(
name|otherObj
operator|==
literal|null
operator|||
name|otherObj
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Step
name|other
init|=
operator|(
name|Step
operator|)
name|otherObj
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|this
operator|.
name|file
argument_list|,
name|other
operator|.
name|file
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|size
argument_list|,
name|other
operator|.
name|size
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|type
argument_list|,
name|other
operator|.
name|type
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
comment|/**    * Returns the optional file name, possibly null.    *     * @return String optional file name, possibly null    */
DECL|method|getFile ()
specifier|public
name|String
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
comment|/**    * Returns the optional size in bytes, possibly Long.MIN_VALUE if undefined.    *     * @return long optional size in bytes, possibly Long.MIN_VALUE    */
DECL|method|getSize ()
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**    * Returns the optional step type, possibly null.    *     * @return StepType optional step type, possibly null    */
DECL|method|getType ()
specifier|public
name|StepType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|file
argument_list|)
operator|.
name|append
argument_list|(
name|size
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

