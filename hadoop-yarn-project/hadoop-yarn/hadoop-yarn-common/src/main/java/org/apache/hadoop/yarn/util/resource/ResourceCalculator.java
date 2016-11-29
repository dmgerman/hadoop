begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util.resource
package|package
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
name|resource
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
operator|.
name|Private
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
name|InterfaceStability
operator|.
name|Unstable
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
name|records
operator|.
name|Resource
import|;
end_import

begin_comment
comment|/**  * A set of {@link Resource} comparison and manipulation interfaces.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ResourceCalculator
specifier|public
specifier|abstract
class|class
name|ResourceCalculator
block|{
specifier|public
specifier|abstract
name|int
DECL|method|compare (Resource clusterResource, Resource lhs, Resource rhs)
name|compare
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|lhs
parameter_list|,
name|Resource
name|rhs
parameter_list|)
function_decl|;
DECL|method|divideAndCeil (int a, int b)
specifier|public
specifier|static
name|int
name|divideAndCeil
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|(
name|a
operator|+
operator|(
name|b
operator|-
literal|1
operator|)
operator|)
operator|/
name|b
return|;
block|}
DECL|method|divideAndCeil (long a, long b)
specifier|public
specifier|static
name|long
name|divideAndCeil
parameter_list|(
name|long
name|a
parameter_list|,
name|long
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|(
name|a
operator|+
operator|(
name|b
operator|-
literal|1
operator|)
operator|)
operator|/
name|b
return|;
block|}
DECL|method|roundUp (int a, int b)
specifier|public
specifier|static
name|int
name|roundUp
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|)
block|{
return|return
name|divideAndCeil
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
operator|*
name|b
return|;
block|}
DECL|method|roundUp (long a, long b)
specifier|public
specifier|static
name|long
name|roundUp
parameter_list|(
name|long
name|a
parameter_list|,
name|long
name|b
parameter_list|)
block|{
return|return
name|divideAndCeil
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
operator|*
name|b
return|;
block|}
DECL|method|roundDown (long a, long b)
specifier|public
specifier|static
name|long
name|roundDown
parameter_list|(
name|long
name|a
parameter_list|,
name|long
name|b
parameter_list|)
block|{
return|return
operator|(
name|a
operator|/
name|b
operator|)
operator|*
name|b
return|;
block|}
DECL|method|roundDown (int a, int b)
specifier|public
specifier|static
name|int
name|roundDown
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|)
block|{
return|return
operator|(
name|a
operator|/
name|b
operator|)
operator|*
name|b
return|;
block|}
comment|/**    * Compute the number of containers which can be allocated given    *<code>available</code> and<code>required</code> resources.    *     * @param available available resources    * @param required required resources    * @return number of containers which can be allocated    */
DECL|method|computeAvailableContainers ( Resource available, Resource required)
specifier|public
specifier|abstract
name|long
name|computeAvailableContainers
parameter_list|(
name|Resource
name|available
parameter_list|,
name|Resource
name|required
parameter_list|)
function_decl|;
comment|/**    * Multiply resource<code>r</code> by factor<code>by</code>     * and normalize up using step-factor<code>stepFactor</code>.    *     * @param r resource to be multiplied    * @param by multiplier    * @param stepFactor factor by which to normalize up     * @return resulting normalized resource    */
DECL|method|multiplyAndNormalizeUp ( Resource r, double by, Resource stepFactor)
specifier|public
specifier|abstract
name|Resource
name|multiplyAndNormalizeUp
parameter_list|(
name|Resource
name|r
parameter_list|,
name|double
name|by
parameter_list|,
name|Resource
name|stepFactor
parameter_list|)
function_decl|;
comment|/**    * Multiply resource<code>r</code> by factor<code>by</code>     * and normalize down using step-factor<code>stepFactor</code>.    *     * @param r resource to be multiplied    * @param by multiplier    * @param stepFactor factor by which to normalize down     * @return resulting normalized resource    */
DECL|method|multiplyAndNormalizeDown ( Resource r, double by, Resource stepFactor)
specifier|public
specifier|abstract
name|Resource
name|multiplyAndNormalizeDown
parameter_list|(
name|Resource
name|r
parameter_list|,
name|double
name|by
parameter_list|,
name|Resource
name|stepFactor
parameter_list|)
function_decl|;
comment|/**    * Normalize resource<code>r</code> given the base     *<code>minimumResource</code> and verify against max allowed    *<code>maximumResource</code> using a step factor for the normalization.    *    * @param r resource    * @param minimumResource minimum value    * @param maximumResource the upper bound of the resource to be allocated    * @param stepFactor the increment for resources to be allocated    * @return normalized resource    */
DECL|method|normalize (Resource r, Resource minimumResource, Resource maximumResource, Resource stepFactor)
specifier|public
specifier|abstract
name|Resource
name|normalize
parameter_list|(
name|Resource
name|r
parameter_list|,
name|Resource
name|minimumResource
parameter_list|,
name|Resource
name|maximumResource
parameter_list|,
name|Resource
name|stepFactor
parameter_list|)
function_decl|;
comment|/**    * Round-up resource<code>r</code> given factor<code>stepFactor</code>.    *     * @param r resource    * @param stepFactor step-factor    * @return rounded resource    */
DECL|method|roundUp (Resource r, Resource stepFactor)
specifier|public
specifier|abstract
name|Resource
name|roundUp
parameter_list|(
name|Resource
name|r
parameter_list|,
name|Resource
name|stepFactor
parameter_list|)
function_decl|;
comment|/**    * Round-down resource<code>r</code> given factor<code>stepFactor</code>.    *     * @param r resource    * @param stepFactor step-factor    * @return rounded resource    */
DECL|method|roundDown (Resource r, Resource stepFactor)
specifier|public
specifier|abstract
name|Resource
name|roundDown
parameter_list|(
name|Resource
name|r
parameter_list|,
name|Resource
name|stepFactor
parameter_list|)
function_decl|;
comment|/**    * Divide resource<code>numerator</code> by resource<code>denominator</code>    * using specified policy (domination, average, fairness etc.); hence overall    *<code>clusterResource</code> is provided for context.    *      * @param clusterResource cluster resources    * @param numerator numerator    * @param denominator denominator    * @return<code>numerator</code>/<code>denominator</code>     *         using specific policy    */
DECL|method|divide ( Resource clusterResource, Resource numerator, Resource denominator)
specifier|public
specifier|abstract
name|float
name|divide
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|numerator
parameter_list|,
name|Resource
name|denominator
parameter_list|)
function_decl|;
comment|/**    * Determine if a resource is not suitable for use as a divisor    * (will result in divide by 0, etc)    *    * @param r resource    * @return true if divisor is invalid (should not be used), false else    */
DECL|method|isInvalidDivisor (Resource r)
specifier|public
specifier|abstract
name|boolean
name|isInvalidDivisor
parameter_list|(
name|Resource
name|r
parameter_list|)
function_decl|;
comment|/**    * Ratio of resource<code>a</code> to resource<code>b</code>.    *     * @param a resource     * @param b resource    * @return ratio of resource<code>a</code> to resource<code>b</code>    */
DECL|method|ratio (Resource a, Resource b)
specifier|public
specifier|abstract
name|float
name|ratio
parameter_list|(
name|Resource
name|a
parameter_list|,
name|Resource
name|b
parameter_list|)
function_decl|;
comment|/**    * Divide-and-ceil<code>numerator</code> by<code>denominator</code>.    *     * @param numerator numerator resource    * @param denominator denominator    * @return resultant resource    */
DECL|method|divideAndCeil (Resource numerator, int denominator)
specifier|public
specifier|abstract
name|Resource
name|divideAndCeil
parameter_list|(
name|Resource
name|numerator
parameter_list|,
name|int
name|denominator
parameter_list|)
function_decl|;
comment|/**    * Check if a smaller resource can be contained by bigger resource.    */
DECL|method|fitsIn (Resource cluster, Resource smaller, Resource bigger)
specifier|public
specifier|abstract
name|boolean
name|fitsIn
parameter_list|(
name|Resource
name|cluster
parameter_list|,
name|Resource
name|smaller
parameter_list|,
name|Resource
name|bigger
parameter_list|)
function_decl|;
block|}
end_class

end_unit

