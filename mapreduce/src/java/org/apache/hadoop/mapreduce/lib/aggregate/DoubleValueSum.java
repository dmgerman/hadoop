begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.aggregate
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|aggregate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * This class implements a value aggregator that sums up a sequence of double  * values.  *   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|DoubleValueSum
specifier|public
class|class
name|DoubleValueSum
implements|implements
name|ValueAggregator
argument_list|<
name|String
argument_list|>
block|{
DECL|field|sum
name|double
name|sum
init|=
literal|0
decl_stmt|;
comment|/**    * The default constructor    *     */
DECL|method|DoubleValueSum ()
specifier|public
name|DoubleValueSum
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**    * add a value to the aggregator    *     * @param val    *          an object whose string representation represents a double value.    *     */
DECL|method|addNextValue (Object val)
specifier|public
name|void
name|addNextValue
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
name|this
operator|.
name|sum
operator|+=
name|Double
operator|.
name|parseDouble
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * add a value to the aggregator    *     * @param val    *          a double value.    *     */
DECL|method|addNextValue (double val)
specifier|public
name|void
name|addNextValue
parameter_list|(
name|double
name|val
parameter_list|)
block|{
name|this
operator|.
name|sum
operator|+=
name|val
expr_stmt|;
block|}
comment|/**    * @return the string representation of the aggregated value    */
DECL|method|getReport ()
specifier|public
name|String
name|getReport
parameter_list|()
block|{
return|return
literal|""
operator|+
name|sum
return|;
block|}
comment|/**    * @return the aggregated value    */
DECL|method|getSum ()
specifier|public
name|double
name|getSum
parameter_list|()
block|{
return|return
name|this
operator|.
name|sum
return|;
block|}
comment|/**    * reset the aggregator    */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|sum
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * @return return an array of one element. The element is a string    *         representation of the aggregated value. The return value is    *         expected to be used by the a combiner.    */
DECL|method|getCombinerOutput ()
specifier|public
name|ArrayList
argument_list|<
name|String
argument_list|>
name|getCombinerOutput
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|retv
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|retv
operator|.
name|add
argument_list|(
literal|""
operator|+
name|sum
argument_list|)
expr_stmt|;
return|return
name|retv
return|;
block|}
block|}
end_class

end_unit

