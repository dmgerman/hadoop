begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.common.helpers
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
operator|.
name|common
operator|.
name|helpers
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|lang3
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
name|lang3
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|ContainerInfo
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
operator|.
name|Pipeline
import|;
end_import

begin_comment
comment|/**  * Class wraps ozone container info.  */
end_comment

begin_class
DECL|class|ContainerWithPipeline
specifier|public
class|class
name|ContainerWithPipeline
implements|implements
name|Comparator
argument_list|<
name|ContainerWithPipeline
argument_list|>
implements|,
name|Comparable
argument_list|<
name|ContainerWithPipeline
argument_list|>
block|{
DECL|field|containerInfo
specifier|private
specifier|final
name|ContainerInfo
name|containerInfo
decl_stmt|;
DECL|field|pipeline
specifier|private
specifier|final
name|Pipeline
name|pipeline
decl_stmt|;
DECL|method|ContainerWithPipeline (ContainerInfo containerInfo, Pipeline pipeline)
specifier|public
name|ContainerWithPipeline
parameter_list|(
name|ContainerInfo
name|containerInfo
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|)
block|{
name|this
operator|.
name|containerInfo
operator|=
name|containerInfo
expr_stmt|;
name|this
operator|.
name|pipeline
operator|=
name|pipeline
expr_stmt|;
block|}
DECL|method|getContainerInfo ()
specifier|public
name|ContainerInfo
name|getContainerInfo
parameter_list|()
block|{
return|return
name|containerInfo
return|;
block|}
DECL|method|getPipeline ()
specifier|public
name|Pipeline
name|getPipeline
parameter_list|()
block|{
return|return
name|pipeline
return|;
block|}
DECL|method|fromProtobuf ( HddsProtos.ContainerWithPipeline allocatedContainer)
specifier|public
specifier|static
name|ContainerWithPipeline
name|fromProtobuf
parameter_list|(
name|HddsProtos
operator|.
name|ContainerWithPipeline
name|allocatedContainer
parameter_list|)
block|{
return|return
operator|new
name|ContainerWithPipeline
argument_list|(
name|ContainerInfo
operator|.
name|fromProtobuf
argument_list|(
name|allocatedContainer
operator|.
name|getContainerInfo
argument_list|()
argument_list|)
argument_list|,
name|Pipeline
operator|.
name|getFromProtobuf
argument_list|(
name|allocatedContainer
operator|.
name|getPipeline
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getProtobuf ()
specifier|public
name|HddsProtos
operator|.
name|ContainerWithPipeline
name|getProtobuf
parameter_list|()
block|{
name|HddsProtos
operator|.
name|ContainerWithPipeline
operator|.
name|Builder
name|builder
init|=
name|HddsProtos
operator|.
name|ContainerWithPipeline
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setContainerInfo
argument_list|(
name|getContainerInfo
argument_list|()
operator|.
name|getProtobuf
argument_list|()
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|getPipeline
argument_list|()
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
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
name|containerInfo
operator|.
name|toString
argument_list|()
operator|+
literal|" | "
operator|+
name|pipeline
operator|.
name|toString
argument_list|()
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
name|ContainerWithPipeline
name|that
init|=
operator|(
name|ContainerWithPipeline
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|getContainerInfo
argument_list|()
argument_list|,
name|that
operator|.
name|getContainerInfo
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getPipeline
argument_list|()
argument_list|,
name|that
operator|.
name|getPipeline
argument_list|()
argument_list|)
operator|.
name|isEquals
argument_list|()
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
argument_list|(
literal|11
argument_list|,
literal|811
argument_list|)
operator|.
name|append
argument_list|(
name|getContainerInfo
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getPipeline
argument_list|()
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
comment|/**    * Compares its two arguments for order.  Returns a negative integer, zero, or    * a positive integer as the first argument is less than, equal to, or greater    * than the second.<p>    *    * @param o1 the first object to be compared.    * @param o2 the second object to be compared.    * @return a negative integer, zero, or a positive integer as the first    * argument is less than, equal to, or greater than the second.    * @throws NullPointerException if an argument is null and this comparator    *                              does not permit null arguments    * @throws ClassCastException   if the arguments' types prevent them from    *                              being compared by this comparator.    */
annotation|@
name|Override
DECL|method|compare (ContainerWithPipeline o1, ContainerWithPipeline o2)
specifier|public
name|int
name|compare
parameter_list|(
name|ContainerWithPipeline
name|o1
parameter_list|,
name|ContainerWithPipeline
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getContainerInfo
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Compares this object with the specified object for order.  Returns a    * negative integer, zero, or a positive integer as this object is less than,    * equal to, or greater than the specified object.    *    * @param o the object to be compared.    * @return a negative integer, zero, or a positive integer as this object is    * less than, equal to, or greater than the specified object.    * @throws NullPointerException if the specified object is null    * @throws ClassCastException   if the specified object's type prevents it    *                              from being compared to this object.    */
annotation|@
name|Override
DECL|method|compareTo (ContainerWithPipeline o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ContainerWithPipeline
name|o
parameter_list|)
block|{
return|return
name|this
operator|.
name|compare
argument_list|(
name|this
argument_list|,
name|o
argument_list|)
return|;
block|}
block|}
end_class

end_unit

