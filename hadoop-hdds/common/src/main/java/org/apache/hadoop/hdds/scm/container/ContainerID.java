begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|container
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|math3
operator|.
name|util
operator|.
name|MathUtils
import|;
end_import

begin_comment
comment|/**  * Container ID is an integer that is a value between 1..MAX_CONTAINER ID.  *<p>  * We are creating a specific type for this to avoid mixing this with  * normal integers in code.  */
end_comment

begin_class
DECL|class|ContainerID
specifier|public
class|class
name|ContainerID
implements|implements
name|Comparable
block|{
DECL|field|id
specifier|private
specifier|final
name|long
name|id
decl_stmt|;
comment|/**    * Constructs ContainerID.    *    * @param id int    */
DECL|method|ContainerID (long id)
specifier|public
name|ContainerID
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|id
operator|>
literal|0
argument_list|,
literal|"Container ID should be a positive long. "
operator|+
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**    * Factory method for creation of ContainerID.    * @param containerID  long    * @return ContainerID.    */
DECL|method|valueof (long containerID)
specifier|public
specifier|static
name|ContainerID
name|valueof
parameter_list|(
name|long
name|containerID
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|containerID
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|ContainerID
argument_list|(
name|containerID
argument_list|)
return|;
block|}
comment|/**    * Returns int representation of ID.    *    * @return int    */
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
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
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ContainerID
name|that
init|=
operator|(
name|ContainerID
operator|)
name|o
decl_stmt|;
return|return
name|id
operator|==
name|that
operator|.
name|id
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
name|MathUtils
operator|.
name|hash
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|o
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|ContainerID
condition|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
operator|(
operator|(
name|ContainerID
operator|)
name|o
operator|)
operator|.
name|getId
argument_list|()
argument_list|,
name|this
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Object O, should be an instance "
operator|+
literal|"of ContainerID"
argument_list|)
throw|;
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
literal|"id="
operator|+
name|id
return|;
block|}
block|}
end_class

end_unit

