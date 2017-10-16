begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
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
name|collect
operator|.
name|ImmutableMap
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|ResourceTypes
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
name|yarn
operator|.
name|util
operator|.
name|UnitsConversionUtil
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Class to encapsulate information about a Resource - the name of the resource,  * the units(milli, micro, etc), the type(countable), and the value.  */
end_comment

begin_class
DECL|class|ResourceInformation
specifier|public
class|class
name|ResourceInformation
implements|implements
name|Comparable
argument_list|<
name|ResourceInformation
argument_list|>
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|units
specifier|private
name|String
name|units
decl_stmt|;
DECL|field|resourceType
specifier|private
name|ResourceTypes
name|resourceType
decl_stmt|;
DECL|field|value
specifier|private
name|long
name|value
decl_stmt|;
DECL|field|minimumAllocation
specifier|private
name|long
name|minimumAllocation
decl_stmt|;
DECL|field|maximumAllocation
specifier|private
name|long
name|maximumAllocation
decl_stmt|;
comment|// Known resource types
DECL|field|MEMORY_URI
specifier|public
specifier|static
specifier|final
name|String
name|MEMORY_URI
init|=
literal|"memory-mb"
decl_stmt|;
DECL|field|VCORES_URI
specifier|public
specifier|static
specifier|final
name|String
name|VCORES_URI
init|=
literal|"vcores"
decl_stmt|;
DECL|field|GPU_URI
specifier|public
specifier|static
specifier|final
name|String
name|GPU_URI
init|=
literal|"yarn.io/gpu"
decl_stmt|;
DECL|field|FPGA_URI
specifier|public
specifier|static
specifier|final
name|String
name|FPGA_URI
init|=
literal|"yarn.io/fpga"
decl_stmt|;
DECL|field|MEMORY_MB
specifier|public
specifier|static
specifier|final
name|ResourceInformation
name|MEMORY_MB
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|MEMORY_URI
argument_list|,
literal|"Mi"
argument_list|)
decl_stmt|;
DECL|field|VCORES
specifier|public
specifier|static
specifier|final
name|ResourceInformation
name|VCORES
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|VCORES_URI
argument_list|)
decl_stmt|;
DECL|field|GPUS
specifier|public
specifier|static
specifier|final
name|ResourceInformation
name|GPUS
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|GPU_URI
argument_list|)
decl_stmt|;
DECL|field|FPGAS
specifier|public
specifier|static
specifier|final
name|ResourceInformation
name|FPGAS
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|FPGA_URI
argument_list|)
decl_stmt|;
DECL|field|MANDATORY_RESOURCES
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceInformation
argument_list|>
name|MANDATORY_RESOURCES
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|MEMORY_URI
argument_list|,
name|MEMORY_MB
argument_list|,
name|VCORES_URI
argument_list|,
name|VCORES
argument_list|,
name|GPU_URI
argument_list|,
name|GPUS
argument_list|,
name|FPGA_URI
argument_list|,
name|FPGAS
argument_list|)
decl_stmt|;
comment|/**    * Get the name for the resource.    *    * @return resource name    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Set the name for the resource.    *    * A valid resource name must begin with a letter and contain only letters,    * numbers, and any of: '.', '_', or '-'. A valid resource name may also be    * optionally preceded by a name space followed by a slash. A valid name space    * consists of period-separated groups of letters, numbers, and dashes."    *    * @param rName name for the resource    */
DECL|method|setName (String rName)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|rName
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|rName
expr_stmt|;
block|}
comment|/**    * Get units for the resource.    *    * @return units for the resource    */
DECL|method|getUnits ()
specifier|public
name|String
name|getUnits
parameter_list|()
block|{
return|return
name|units
return|;
block|}
comment|/**    * Set the units for the resource.    *    * @param rUnits units for the resource    */
DECL|method|setUnits (String rUnits)
specifier|public
name|void
name|setUnits
parameter_list|(
name|String
name|rUnits
parameter_list|)
block|{
if|if
condition|(
operator|!
name|UnitsConversionUtil
operator|.
name|KNOWN_UNITS
operator|.
name|contains
argument_list|(
name|rUnits
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown unit '"
operator|+
name|rUnits
operator|+
literal|"'. Known units are "
operator|+
name|UnitsConversionUtil
operator|.
name|KNOWN_UNITS
argument_list|)
throw|;
block|}
name|this
operator|.
name|units
operator|=
name|rUnits
expr_stmt|;
block|}
comment|/**    * Checking if a unit included by KNOWN_UNITS is an expensive operation. This    * can be avoided in critical path in RM.    * @param rUnits units for the resource    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|setUnitsWithoutValidation (String rUnits)
specifier|public
name|void
name|setUnitsWithoutValidation
parameter_list|(
name|String
name|rUnits
parameter_list|)
block|{
name|this
operator|.
name|units
operator|=
name|rUnits
expr_stmt|;
block|}
comment|/**    * Get the resource type.    *    * @return the resource type    */
DECL|method|getResourceType ()
specifier|public
name|ResourceTypes
name|getResourceType
parameter_list|()
block|{
return|return
name|resourceType
return|;
block|}
comment|/**    * Set the resource type.    *    * @param type the resource type    */
DECL|method|setResourceType (ResourceTypes type)
specifier|public
name|void
name|setResourceType
parameter_list|(
name|ResourceTypes
name|type
parameter_list|)
block|{
name|this
operator|.
name|resourceType
operator|=
name|type
expr_stmt|;
block|}
comment|/**    * Get the value for the resource.    *    * @return the resource value    */
DECL|method|getValue ()
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**    * Set the value for the resource.    *    * @param rValue the resource value    */
DECL|method|setValue (long rValue)
specifier|public
name|void
name|setValue
parameter_list|(
name|long
name|rValue
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|rValue
expr_stmt|;
block|}
comment|/**    * Get the minimum allocation for the resource.    *    * @return the minimum allocation for the resource    */
DECL|method|getMinimumAllocation ()
specifier|public
name|long
name|getMinimumAllocation
parameter_list|()
block|{
return|return
name|minimumAllocation
return|;
block|}
comment|/**    * Set the minimum allocation for the resource.    *    * @param minimumAllocation the minimum allocation for the resource    */
DECL|method|setMinimumAllocation (long minimumAllocation)
specifier|public
name|void
name|setMinimumAllocation
parameter_list|(
name|long
name|minimumAllocation
parameter_list|)
block|{
name|this
operator|.
name|minimumAllocation
operator|=
name|minimumAllocation
expr_stmt|;
block|}
comment|/**    * Get the maximum allocation for the resource.    *    * @return the maximum allocation for the resource    */
DECL|method|getMaximumAllocation ()
specifier|public
name|long
name|getMaximumAllocation
parameter_list|()
block|{
return|return
name|maximumAllocation
return|;
block|}
comment|/**    * Set the maximum allocation for the resource.    *    * @param maximumAllocation the maximum allocation for the resource    */
DECL|method|setMaximumAllocation (long maximumAllocation)
specifier|public
name|void
name|setMaximumAllocation
parameter_list|(
name|long
name|maximumAllocation
parameter_list|)
block|{
name|this
operator|.
name|maximumAllocation
operator|=
name|maximumAllocation
expr_stmt|;
block|}
comment|/**    * Create a new instance of ResourceInformation from another object.    *    * @param other the object from which the new object should be created    * @return the new ResourceInformation object    */
DECL|method|newInstance (ResourceInformation other)
specifier|public
specifier|static
name|ResourceInformation
name|newInstance
parameter_list|(
name|ResourceInformation
name|other
parameter_list|)
block|{
name|ResourceInformation
name|ret
init|=
operator|new
name|ResourceInformation
argument_list|()
decl_stmt|;
name|copy
argument_list|(
name|other
argument_list|,
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|newInstance (String name, String units, long value, ResourceTypes type, long minimumAllocation, long maximumAllocation)
specifier|public
specifier|static
name|ResourceInformation
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|units
parameter_list|,
name|long
name|value
parameter_list|,
name|ResourceTypes
name|type
parameter_list|,
name|long
name|minimumAllocation
parameter_list|,
name|long
name|maximumAllocation
parameter_list|)
block|{
name|ResourceInformation
name|ret
init|=
operator|new
name|ResourceInformation
argument_list|()
decl_stmt|;
name|ret
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setResourceType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setUnits
argument_list|(
name|units
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setMinimumAllocation
argument_list|(
name|minimumAllocation
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setMaximumAllocation
argument_list|(
name|maximumAllocation
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|newInstance (String name, String units, long value)
specifier|public
specifier|static
name|ResourceInformation
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|units
parameter_list|,
name|long
name|value
parameter_list|)
block|{
return|return
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|units
argument_list|,
name|value
argument_list|,
name|ResourceTypes
operator|.
name|COUNTABLE
argument_list|,
literal|0L
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
DECL|method|newInstance (String name, String units)
specifier|public
specifier|static
name|ResourceInformation
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|units
parameter_list|)
block|{
return|return
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|units
argument_list|,
literal|0L
argument_list|,
name|ResourceTypes
operator|.
name|COUNTABLE
argument_list|,
literal|0L
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
DECL|method|newInstance (String name, String units, ResourceTypes resourceType)
specifier|public
specifier|static
name|ResourceInformation
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|units
parameter_list|,
name|ResourceTypes
name|resourceType
parameter_list|)
block|{
return|return
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|units
argument_list|,
literal|0L
argument_list|,
name|resourceType
argument_list|,
literal|0L
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
DECL|method|newInstance (String name, String units, long minRes, long maxRes)
specifier|public
specifier|static
name|ResourceInformation
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|units
parameter_list|,
name|long
name|minRes
parameter_list|,
name|long
name|maxRes
parameter_list|)
block|{
return|return
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|units
argument_list|,
literal|0L
argument_list|,
name|ResourceTypes
operator|.
name|COUNTABLE
argument_list|,
name|minRes
argument_list|,
name|maxRes
argument_list|)
return|;
block|}
DECL|method|newInstance (String name, long value)
specifier|public
specifier|static
name|ResourceInformation
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
return|return
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|value
argument_list|,
name|ResourceTypes
operator|.
name|COUNTABLE
argument_list|,
literal|0L
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
DECL|method|newInstance (String name)
specifier|public
specifier|static
name|ResourceInformation
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/**    * Copies the content of the source ResourceInformation object to the    * destination object, overwriting all properties of the destination object.    * @param src Source ResourceInformation object    * @param dst Destination ResourceInformation object    */
DECL|method|copy (ResourceInformation src, ResourceInformation dst)
specifier|public
specifier|static
name|void
name|copy
parameter_list|(
name|ResourceInformation
name|src
parameter_list|,
name|ResourceInformation
name|dst
parameter_list|)
block|{
name|dst
operator|.
name|setName
argument_list|(
name|src
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setResourceType
argument_list|(
name|src
operator|.
name|getResourceType
argument_list|()
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setUnits
argument_list|(
name|src
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setValue
argument_list|(
name|src
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setMinimumAllocation
argument_list|(
name|src
operator|.
name|getMinimumAllocation
argument_list|()
argument_list|)
expr_stmt|;
name|dst
operator|.
name|setMaximumAllocation
argument_list|(
name|src
operator|.
name|getMaximumAllocation
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"name: "
operator|+
name|this
operator|.
name|name
operator|+
literal|", units: "
operator|+
name|this
operator|.
name|units
operator|+
literal|", type: "
operator|+
name|resourceType
operator|+
literal|", value: "
operator|+
name|value
operator|+
literal|", minimum allocation: "
operator|+
name|minimumAllocation
operator|+
literal|", maximum allocation: "
operator|+
name|maximumAllocation
return|;
block|}
DECL|method|getShorthandRepresentation ()
specifier|public
name|String
name|getShorthandRepresentation
parameter_list|()
block|{
return|return
literal|""
operator|+
name|this
operator|.
name|value
operator|+
name|this
operator|.
name|units
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|ResourceInformation
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ResourceInformation
name|r
init|=
operator|(
name|ResourceInformation
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|name
operator|.
name|equals
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
operator|!
name|this
operator|.
name|resourceType
operator|.
name|equals
argument_list|(
name|r
operator|.
name|getResourceType
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|units
operator|.
name|equals
argument_list|(
name|r
operator|.
name|units
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|value
operator|==
name|r
operator|.
name|value
return|;
block|}
return|return
operator|(
name|UnitsConversionUtil
operator|.
name|compare
argument_list|(
name|this
operator|.
name|units
argument_list|,
name|this
operator|.
name|value
argument_list|,
name|r
operator|.
name|units
argument_list|,
name|r
operator|.
name|value
argument_list|)
operator|==
literal|0
operator|)
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
specifier|final
name|int
name|prime
init|=
literal|263167
decl_stmt|;
name|int
name|result
init|=
literal|939769357
operator|+
name|name
operator|.
name|hashCode
argument_list|()
decl_stmt|;
comment|// prime * result = 939769357 initially
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|resourceType
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|units
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Long
operator|.
name|hashCode
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (ResourceInformation other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ResourceInformation
name|other
parameter_list|)
block|{
name|int
name|diff
init|=
name|this
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|diff
operator|==
literal|0
condition|)
block|{
name|diff
operator|=
name|UnitsConversionUtil
operator|.
name|compare
argument_list|(
name|this
operator|.
name|units
argument_list|,
name|this
operator|.
name|value
argument_list|,
name|other
operator|.
name|units
argument_list|,
name|other
operator|.
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|diff
operator|==
literal|0
condition|)
block|{
name|diff
operator|=
name|this
operator|.
name|resourceType
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|resourceType
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|diff
return|;
block|}
block|}
end_class

end_unit

