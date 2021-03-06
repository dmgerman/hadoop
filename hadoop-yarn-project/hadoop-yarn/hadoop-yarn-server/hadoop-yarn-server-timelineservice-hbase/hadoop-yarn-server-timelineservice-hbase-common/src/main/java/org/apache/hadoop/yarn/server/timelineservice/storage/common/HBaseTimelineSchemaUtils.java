begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|flow
operator|.
name|AggregationOperation
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
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|flow
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_comment
comment|/**  * A bunch of utility functions used in HBase TimelineService common module.  */
end_comment

begin_class
DECL|class|HBaseTimelineSchemaUtils
specifier|public
specifier|final
class|class
name|HBaseTimelineSchemaUtils
block|{
comment|/** milliseconds in one day. */
DECL|field|MILLIS_ONE_DAY
specifier|public
specifier|static
specifier|final
name|long
name|MILLIS_ONE_DAY
init|=
literal|86400000L
decl_stmt|;
DECL|field|APP_ID_FORMAT
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|NumberFormat
argument_list|>
name|APP_ID_FORMAT
init|=
operator|new
name|ThreadLocal
argument_list|<
name|NumberFormat
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NumberFormat
name|initialValue
parameter_list|()
block|{
name|NumberFormat
name|fmt
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|4
argument_list|)
expr_stmt|;
return|return
name|fmt
return|;
block|}
block|}
decl_stmt|;
DECL|method|HBaseTimelineSchemaUtils ()
specifier|private
name|HBaseTimelineSchemaUtils
parameter_list|()
block|{   }
comment|/**    * Combines the input array of attributes and the input aggregation operation    * into a new array of attributes.    *    * @param attributes Attributes to be combined.    * @param aggOp Aggregation operation.    * @return array of combined attributes.    */
DECL|method|combineAttributes (Attribute[] attributes, AggregationOperation aggOp)
specifier|public
specifier|static
name|Attribute
index|[]
name|combineAttributes
parameter_list|(
name|Attribute
index|[]
name|attributes
parameter_list|,
name|AggregationOperation
name|aggOp
parameter_list|)
block|{
name|int
name|newLength
init|=
name|getNewLengthCombinedAttributes
argument_list|(
name|attributes
argument_list|,
name|aggOp
argument_list|)
decl_stmt|;
name|Attribute
index|[]
name|combinedAttributes
init|=
operator|new
name|Attribute
index|[
name|newLength
index|]
decl_stmt|;
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|attributes
argument_list|,
literal|0
argument_list|,
name|combinedAttributes
argument_list|,
literal|0
argument_list|,
name|attributes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aggOp
operator|!=
literal|null
condition|)
block|{
name|Attribute
name|a2
init|=
name|aggOp
operator|.
name|getAttribute
argument_list|()
decl_stmt|;
name|combinedAttributes
index|[
name|newLength
operator|-
literal|1
index|]
operator|=
name|a2
expr_stmt|;
block|}
return|return
name|combinedAttributes
return|;
block|}
comment|/**    * Returns a number for the new array size. The new array is the combination    * of input array of attributes and the input aggregation operation.    *    * @param attributes Attributes.    * @param aggOp Aggregation operation.    * @return the size for the new array    */
DECL|method|getNewLengthCombinedAttributes (Attribute[] attributes, AggregationOperation aggOp)
specifier|private
specifier|static
name|int
name|getNewLengthCombinedAttributes
parameter_list|(
name|Attribute
index|[]
name|attributes
parameter_list|,
name|AggregationOperation
name|aggOp
parameter_list|)
block|{
name|int
name|oldLength
init|=
name|getAttributesLength
argument_list|(
name|attributes
argument_list|)
decl_stmt|;
name|int
name|aggLength
init|=
name|getAppOpLength
argument_list|(
name|aggOp
argument_list|)
decl_stmt|;
return|return
name|oldLength
operator|+
name|aggLength
return|;
block|}
DECL|method|getAppOpLength (AggregationOperation aggOp)
specifier|private
specifier|static
name|int
name|getAppOpLength
parameter_list|(
name|AggregationOperation
name|aggOp
parameter_list|)
block|{
if|if
condition|(
name|aggOp
operator|!=
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|getAttributesLength (Attribute[] attributes)
specifier|private
specifier|static
name|int
name|getAttributesLength
parameter_list|(
name|Attribute
index|[]
name|attributes
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
return|return
name|attributes
operator|.
name|length
return|;
block|}
return|return
literal|0
return|;
block|}
comment|/**    * Converts an int into it's inverse int to be used in (row) keys    * where we want to have the largest int value in the top of the table    * (scans start at the largest int first).    *    * @param key value to be inverted so that the latest version will be first in    *          a scan.    * @return inverted int    */
DECL|method|invertInt (int key)
specifier|public
specifier|static
name|int
name|invertInt
parameter_list|(
name|int
name|key
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|key
return|;
block|}
comment|/**    * returns the timestamp of that day's start (which is midnight 00:00:00 AM)    * for a given input timestamp.    *    * @param ts Timestamp.    * @return timestamp of that day's beginning (midnight)    */
DECL|method|getTopOfTheDayTimestamp (long ts)
specifier|public
specifier|static
name|long
name|getTopOfTheDayTimestamp
parameter_list|(
name|long
name|ts
parameter_list|)
block|{
name|long
name|dayTimestamp
init|=
name|ts
operator|-
operator|(
name|ts
operator|%
name|MILLIS_ONE_DAY
operator|)
decl_stmt|;
return|return
name|dayTimestamp
return|;
block|}
comment|/**    * Checks if passed object is of integral type(Short/Integer/Long).    *    * @param obj Object to be checked.    * @return true if object passed is of type Short or Integer or Long, false    * otherwise.    */
DECL|method|isIntegralValue (Object obj)
specifier|public
specifier|static
name|boolean
name|isIntegralValue
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
name|obj
operator|instanceof
name|Short
operator|)
operator|||
operator|(
name|obj
operator|instanceof
name|Integer
operator|)
operator|||
operator|(
name|obj
operator|instanceof
name|Long
operator|)
return|;
block|}
comment|/**    * A utility method that converts ApplicationId to string without using    * FastNumberFormat in order to avoid the incompatibility issue caused    * by mixing hadoop-common 2.5.1 and hadoop-yarn-api 3.0 in this module.    * This is a work-around implementation as discussed in YARN-6905.    *    * @param appId application id    * @return the string representation of the given application id    *    */
DECL|method|convertApplicationIdToString (ApplicationId appId)
specifier|public
specifier|static
name|String
name|convertApplicationIdToString
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|64
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ApplicationId
operator|.
name|appIdStrPrefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
operator|.
name|append
argument_list|(
name|appId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
operator|.
name|append
argument_list|(
name|APP_ID_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|appId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

