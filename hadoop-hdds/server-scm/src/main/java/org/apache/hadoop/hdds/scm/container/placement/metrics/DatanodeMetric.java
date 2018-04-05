begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.placement.metrics
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
name|placement
operator|.
name|metrics
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
name|hdds
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
import|;
end_import

begin_comment
comment|/**  * DatanodeMetric acts as the basis for all the metric that is used in  * comparing 2 datanodes.  */
end_comment

begin_interface
DECL|interface|DatanodeMetric
specifier|public
interface|interface
name|DatanodeMetric
parameter_list|<
name|T
parameter_list|,
name|S
parameter_list|>
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
block|{
comment|/**    * Some syntactic sugar over Comparable interface. This makes code easier to    * read.    *    * @param o - Other Object    * @return - True if *this* object is greater than argument.    */
DECL|method|isGreater (T o)
name|boolean
name|isGreater
parameter_list|(
name|T
name|o
parameter_list|)
function_decl|;
comment|/**    * Inverse of isGreater.    *    * @param o - other object.    * @return True if *this* object is Lesser than argument.    */
DECL|method|isLess (T o)
name|boolean
name|isLess
parameter_list|(
name|T
name|o
parameter_list|)
function_decl|;
comment|/**    * Returns true if the object has same values. Because of issues with    * equals, and loss of type information this interface supports isEqual.    *    * @param o object to compare.    * @return True, if the values match.    */
DECL|method|isEqual (T o)
name|boolean
name|isEqual
parameter_list|(
name|T
name|o
parameter_list|)
function_decl|;
comment|/**    * A resourceCheck, defined by resourceNeeded.    * For example, S could be bytes required    * and DatanodeMetric can reply by saying it can be met or not.    *    * @param resourceNeeded -  ResourceNeeded in its own metric.    * @return boolean, True if this resource requirement can be met.    */
DECL|method|hasResources (S resourceNeeded)
name|boolean
name|hasResources
parameter_list|(
name|S
name|resourceNeeded
parameter_list|)
throws|throws
name|SCMException
function_decl|;
comment|/**    * Returns the metric.    *    * @return T, the object that represents this metric.    */
DECL|method|get ()
name|T
name|get
parameter_list|()
function_decl|;
comment|/**    * Sets the value of this metric.    *    * @param value - value of the metric.    */
DECL|method|set (T value)
name|void
name|set
parameter_list|(
name|T
name|value
parameter_list|)
function_decl|;
comment|/**    * Adds a value of to the base.    * @param value - value    */
DECL|method|add (T value)
name|void
name|add
parameter_list|(
name|T
name|value
parameter_list|)
function_decl|;
comment|/**    * subtract a value.    * @param value value    */
DECL|method|subtract (T value)
name|void
name|subtract
parameter_list|(
name|T
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

